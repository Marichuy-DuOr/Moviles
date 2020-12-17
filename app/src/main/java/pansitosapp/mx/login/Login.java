package pansitosapp.mx.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pansitosapp.mx.R;
import pansitosapp.mx.http.Client;
import pansitosapp.mx.http.Node;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends Fragment implements View.OnClickListener{

    private NavController navController; // para moverse entre fragmentos

    EditText campoEmail, campoPassword;
    Button login, register;

    ProgressDialog progress;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.login, container, false); // Cambiar según el layout del fragmento
        super.onCreate(savedInstanceState);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view); //Inicializa el navcontroler

        cargarPreferencias(); // revisa si un usuario ya esta loggeado

        //En el caso de que no, continua para que el usuario inicie sesión o se registre
        login = view.findViewById(R.id.btnLogin);
        login.setOnClickListener(this);

        register = view.findViewById(R.id.btnRegister);
        register.setOnClickListener(this);

        campoEmail = view.findViewById(R.id.editTextEmail);
        campoPassword = view.findViewById(R.id.editTextPassword);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnRegister:
                // Ejemplo rapido para mandar parametros entre fragmentos
                Bundle bundle = new Bundle();
                bundle.putString("parametro", "mando una cadena de texto");
                navController.navigate(R.id.nav_to_register, bundle);
                break;
        }
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String message = preferences.getString("message","No existe la informacion");
        // revisa lo que esta guardado actualmente en el sistema
        // si no hay un usuario, se brinca el if y continua con el login
        if (message.equals("OK")) { // en el caso de que si, revisa el rol del usuario
            String rol = preferences.getString("rol","No existe la informacion");
            if(rol.equals("client")){
                navController.navigate(R.id.nav_to_client_menu); // Si es cliente lo manda al menu del cliente
            } else {
                navController.navigate(R.id.nav_to_admin_menu); // Si es admin lo manda al menu del usuario
            }
        }
    }

    private void login () {
        Client userRest = Node.getClient().create(Client.class); // declara el clase para hacer la peticion al servidor

        JsonObject data = new JsonObject(); // Objeto Json para mandar el body
        data.addProperty("email", campoEmail.getText().toString()); //Agregar el body
        data.addProperty("password", campoPassword.getText().toString());

        //Inicia el pregress dialog para que el usuario sepa que su peticion esta siendo procesada
        progress = ProgressDialog.show(getContext(), "Verificando","Espera", true);

        final Call<JsonObject> call = userRest.onLogin(data); // hace la peticion a la respectiva ruta del servidor

        call.enqueue(new Callback<JsonObject>() { // verifica la respuesta del servidor
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progress.dismiss(); // cierro el progress dialog
                if (response.code() != 200) { // el el caso de que la respuesta sea negativa
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        Log.i("mainActivity", jObjError.toString());
                        System.out.println(jObjError.toString());

                        //pongo un toast rapido para que el usuario sepa que los datos que puso son incorrectos
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Usuario o contraseña incorrectos" , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                        toast.show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // cuado logra iniciar sesion, guardo las variables del json en variables normales de java
                JsonObject json = response.body();
                String message = json.getAsJsonPrimitive("message").getAsString();
                String token = json.getAsJsonPrimitive("token").getAsString();
                Integer idUsuario = json.getAsJsonPrimitive("idUsuario").getAsInt();
                String rol = json.getAsJsonPrimitive("rol").getAsString();
                String nombre = json.getAsJsonPrimitive("nombre").getAsString();

                // Declaro shared preferences para guardar las variables del usuario en el sistema
                SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("message", message);
                editor.putString("token", token);
                editor.putInt("idUsuario", idUsuario);
                editor.putString("rol", rol);
                editor.putString("nombre", nombre);

                editor.commit();

                // cambio de fragmento según el rol del usuario
                if(rol.equals("client")){
                    navController.navigate(R.id.nav_to_client_menu);
                } else {
                    navController.navigate(R.id.nav_to_admin_menu);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss(); // el progres dialog debe de cerrarse en todos los lugares que sea necesario
                call.cancel();
            }
        });
    }

}

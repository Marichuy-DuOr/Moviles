package pansitosapp.mx.register;

import android.app.ProgressDialog;
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

public class Register extends Fragment implements View.OnClickListener{

    private NavController navController; //para navegar entre fragmentos
    EditText campoNombre, campoApepat, campoApemat, campoEmail, campoPassword, campoPasswordVerf;
    Button regresar, register;

    ProgressDialog progress;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.register, container, false);
        super.onCreate(savedInstanceState);

        String argumento = getArguments().getString("parametro"); // ejemplo de como recibir parametros entre fragamentos
        //System.out.println(argumento);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        regresar = view.findViewById(R.id.btnRegresar);
        regresar.setOnClickListener(this);

        register = view.findViewById(R.id.btnRegister);
        register.setOnClickListener(this);

        campoNombre = view.findViewById(R.id.editTextNombre);
        campoApepat = view.findViewById(R.id.editTextApepat);
        campoApemat = view.findViewById(R.id.editTextApemat);
        campoEmail = view.findViewById(R.id.editTextEmail);
        campoPassword = view.findViewById(R.id.editTextPassword);
        campoPasswordVerf = view.findViewById(R.id.editTextPasswordVerf);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegresar:
                navController.navigate(R.id.nav_to_login); // lo navega al login
                break;
            case R.id.btnRegister:
                register(); // registra al usuario
                break;
        }
    }

    private void register () {
        // recibe las variables que puso el usuario
        String nombre = campoNombre.getText().toString();
        String apepat = campoApepat.getText().toString();
        String apemat = campoApemat.getText().toString();
        String email = campoEmail.getText().toString();
        String pass = campoPassword.getText().toString();
        String passVerf = campoPasswordVerf.getText().toString();

        // if para verificar que las variables no esten vacias
        if(!nombre.equals("") && !apepat.equals("") && !apemat.equals("") && !email.equals("") && !pass.equals("")){
            // revisa que las contraseñas sean iguales
            if(pass.equals(passVerf)){
                Client userRest = Node.getClient().create(Client.class); // Clase para las peticiones al servidor

                JsonObject data = new JsonObject(); // Objeto Json para el body

                data.addProperty("nombre", nombre);
                data.addProperty("apepat", apepat);
                data.addProperty("apemat", apemat);
                data.addProperty("email", email);
                data.addProperty("password", pass);

                //progress dialog para esperar a que se cree el usuario
                progress = ProgressDialog.show(getContext(), "Verificando","Espera", true);

                // llama a la petición con el body
                final Call<JsonObject> call = userRest.onRegister(data);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() != 200) {
                            JSONObject jObjError = null;
                            progress.dismiss(); // cerrar donde sea necesario
                            try {
                                jObjError = new JSONObject(response.errorBody().string());
                                Log.i("mainActivity", jObjError.toString());
                                System.out.println(jObjError.toString());

                                // toast para avisar que el usuario no pudo ser creado por cualquier razon
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Algo salio mal, intentalo otra vez" , Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                                toast.show();
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        progress.dismiss(); // cierra el progress dialog cuando termino de crear el usuario
                        JsonObject json = response.body(); // respuesta del servidor
                        navController.navigate(R.id.nav_login); // lo navega al login para que ahora si inicie sesión
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        progress.dismiss(); // cerrar donde sea necesario
                        call.cancel();
                    }
                });
            } else {
                //toast para avisar que las contraseñas estan mal puestas
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Las contraseñas no coinciden" , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }
        } else {
            // toast para avisar que aun hay campos en blanco
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Termina de llenar los campos" , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }

    }
}

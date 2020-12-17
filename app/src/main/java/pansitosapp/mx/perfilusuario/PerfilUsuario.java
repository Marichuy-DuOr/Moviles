package pansitosapp.mx.perfilusuario;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pansitosapp.mx.R;
import pansitosapp.mx.http.Client;
import pansitosapp.mx.http.Node;
import pansitosapp.mx.productos.Pan;
import pansitosapp.mx.productos.PanAdapter;
import pansitosapp.mx.productos.Productos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilUsuario extends Fragment {

    private NavController navController; //para navegar entre fragmentos

    ProgressDialog progress;

    TextView textNombre, textEmail;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.perfil_usuario, container, false); // Cambiar el layout que corresponda
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // flecha para regresar al menu
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        textNombre = (TextView) view.findViewById(R.id.txtNombre);
        textEmail = (TextView) view.findViewById(R.id.txtEmail);

        getUser();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { //agregar cuando pongas opciones en el menu
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // para que el boton de la flecha funcione
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ((AppCompatActivity)getActivity()).onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUser () {
        Client userRest = Node.getClient().create(Client.class);
        progress = ProgressDialog.show(getContext(), "Cargando","Espera", true);
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String token = preferences.getString("token","No existe la informacion");

        final Call<JsonObject> call = userRest.getelUsuario(token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) { // cuando recibe la respuesta del servidor

                progress.dismiss(); // cerrar el progress dialog
                if (response.code() != 200) {
                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        Log.i("mainActivity", jObjError.toString());
                        System.out.println(jObjError.toString());
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                JsonObject json = response.body();
                json.toString();
                JsonArray jsonArray = json.getAsJsonArray("array");

                JsonObject object = jsonArray.get(0).getAsJsonObject();

                String nombre = object.getAsJsonPrimitive("nombre").getAsString();
                String email = object.getAsJsonPrimitive("email").getAsString();

                textNombre.setText(nombre);
                textEmail.setText(email);

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                call.cancel();
            }
        });
    }
}

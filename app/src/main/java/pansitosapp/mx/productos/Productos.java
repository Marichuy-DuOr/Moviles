package pansitosapp.mx.productos;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import pansitosapp.mx.R;
import pansitosapp.mx.http.Client;
import pansitosapp.mx.http.Node;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Productos extends Fragment implements PanInterface, View.OnClickListener {

    private NavController navController; // control para navegar entre componentes
    ProgressDialog progress;

    private RecyclerView recyclerPanes;
    PanAdapter panAdapter;

    ArrayList<Pan> listaPanes; // lista con todos los panes

    FloatingActionButton addPan; // boton para crear un producto nuevo

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.productos, container, false); // Cambiar el layout que corresponda
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // flecha para regresar al menu
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        addPan = view.findViewById(R.id.btnNuevo);
        addPan.setOnClickListener(this);

        getAllProductos(); // llena el array de panes

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNuevo:
                navController.navigate(R.id.nav_to_edit_productos); // lo lleva a crear un producto nuevo
                break;
        }
    }

    private void getAllProductos () {
        Client userRest = Node.getClient().create(Client.class); // declara la clase para las peticiones al servidor

        // inicia el progress dialog en lo que la aplicacion recibe los panes
        progress = ProgressDialog.show(getContext(), "Cargando","Espera", true);

        // cargar el token en una variable para poderlo mandar en una peticion como header
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String token = preferences.getString("token","No existe la informacion");

        final Call<JsonObject> call = userRest.getAllProductos(token); // hace la peticion al servidor mandando el token que corresponde
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

                // despues de recibir los panes
                listaPanes = new ArrayList<>(); // inicializa la lista

                JsonObject json = response.body(); // recibe el body
                // el servidior manda la respuesta en una variable llamada "array" en el body que tambien es un array
                JsonArray jsonArray = json.getAsJsonArray("array"); // recibe el arra en un tipo de variable especial para esto
                for (int i=0; i < jsonArray.size(); i++){ //recorre el array para sacar el contenido

                    JsonObject object = jsonArray.get(i).getAsJsonObject(); // saca el objeto del array en otro objeto json
                    // saca cada variable del objeto en variables normales de java
                    // si la variable es int o float utiliza Integer o Float para que no haya errores
                    Integer id = object.getAsJsonPrimitive("id").getAsInt();
                    String nombre = object.getAsJsonPrimitive("nombre").getAsString();
                    Float precio = object.getAsJsonPrimitive("precio").getAsFloat();
                    String imagen = object.getAsJsonPrimitive("imagen").getAsString();
                    String descripcion = object.getAsJsonPrimitive("descripcion").getAsString();

                    Pan pan = new Pan(id, nombre, precio, imagen, descripcion); // crea un objeto Pan
                    listaPanes.add(pan); // lo agrega a la lista
                }

                // Cuando ya estan todos los panes en el array list manejamos el recycledView para mostrarlos todos
                recyclerPanes = getView().findViewById(R.id.recyclerid); //obtiene el id de xml
                recyclerPanes.setLayoutManager(new LinearLayoutManager(getContext()));
                // declara el adapter que es el que maneja el otro xml que se va a reciclar y le manda el array de panes
                panAdapter =  new PanAdapter(listaPanes);

                recyclerPanes.setAdapter(panAdapter); // le asignas el adapter al recyclerView
                panAdapter.setOnClick(Productos.this); // Agrega click listener

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss(); // cerrar donde sea necesario porque no hay forma de cerrarlo desde la aplicacion
                call.cancel();
            }
        });
    }

    @Override
    public void onPanClick(int pos) { // lo que vaya a hacer el producto cuando hagan click
        // lo dirijo al fragmento para editar panes con el pan como parametro para modificarlo
        Bundle bundle = new Bundle();
        bundle.putSerializable("elPan", listaPanes.get(pos) );
        navController.navigate(R.id.nav_to_edit_productos, bundle);
    }

}

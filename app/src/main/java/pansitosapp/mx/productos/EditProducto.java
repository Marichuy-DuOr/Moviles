package pansitosapp.mx.productos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.JsonArray;
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

public class EditProducto extends Fragment implements View.OnClickListener {
    private NavController navController;
    ProgressDialog progress;

    EditText nombre, precio, descripcion, imagen;
    Button guardar, eliminar;

    Pan pan;
    String token; // uso el token muchas veces, mejor lo declaro desde el inicio

    Boolean band;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.edit_productos, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        // revisa si se recivieron argumentos
        if (getArguments() != null){ // en el caso de que si, se pone la opcion para editar el pan
            pan = (Pan) getArguments().getSerializable("elPan");
            band = false;
        } else { // en el caso de que no, se va a crear un pan nuevo
            band = true;
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // guardar el token
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        token = preferences.getString("token","No existe la informacion");

        guardar = view.findViewById(R.id.btnGuardar);
        guardar.setOnClickListener(this);

        eliminar = view.findViewById(R.id.btnEliminar);
        eliminar.setOnClickListener(this);

        nombre = view.findViewById(R.id.editTextNombre);
        precio = view.findViewById(R.id.editTextPrecio);
        descripcion = view.findViewById(R.id.editTextDescripcion);
        imagen = view.findViewById(R.id.editTextImagen);

        if(!band){ // solo cuando se edita un pan
            nombre.setText(pan.getNombre());
            precio.setText(pan.getPrecio().toString());
            descripcion.setText(pan.getDescripcion());
            imagen.setText(pan.getImagen());
        } else { // quita el boton de eliminar para que no este cuando se crea un pan
            eliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGuardar:
                if (band){ // lo manda a la respectiva opcion
                    createProducto();
                } else {
                    modifyProducto ();
                }
                break;
            case R.id.btnEliminar:
                deleteProducto();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { // poner para que el menu de la flecha para regresar funcione
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // para la flecha de regresar
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ((AppCompatActivity)getActivity()).onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createProducto () {
        // recibe los datos que puso el usuario
        String nom = nombre.getText().toString();
        String prec = precio.getText().toString();
        String desc = descripcion.getText().toString();
        String img = imagen.getText().toString();

        // revisa que no esten vacios
        if(!nom.equals("") && !prec.equals("") && !desc.equals("") && !img.equals("")){
            Client userRest = Node.getClient().create(Client.class); // declara la clase de las peticiones al servidor

            JsonObject data = new JsonObject(); // declara un objeto json para el body
            data.addProperty("nombre", nom);
            data.addProperty("precio", prec);
            data.addProperty("imagen", img);
            data.addProperty("descripcion", desc);

            // inicia el progress dialog en lo que se guarda el objeto
            progress = ProgressDialog.show(getContext(), "Agregando","Espera", true);

            // hace la peticion al servidor con el token y el body
            final Call<JsonObject> call = userRest.createProducto(token, data);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progress.dismiss(); // cierra el progress dialog
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

                    // termina y redirige a productos
                    JsonObject json = response.body();
                    navController.navigate(R.id.nav_to_productos);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progress.dismiss(); // aqui tambien se cierra
                    call.cancel();
                }
            });
        } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Termina de llenar los campos" , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }
    }

    private void modifyProducto () {
        // obtine las variables que puso el usuario
        String nom = nombre.getText().toString();
        String prec = precio.getText().toString();
        String desc = descripcion.getText().toString();
        String img = imagen.getText().toString();

        // comprueba que no esten vacias
        if(!nom.equals("") && !prec.equals("") && !desc.equals("") && !img.equals("")){
            Client userRest = Node.getClient().create(Client.class); // declara la clase de las peticiones al servidor

            JsonObject data = new JsonObject(); // declara objeto json para el body
            data.addProperty("id", pan.getId()); // pone el id del pan que ya tenemos previamente guardado
            data.addProperty("nombre", nom);
            data.addProperty("precio", prec);
            data.addProperty("imagen", img);
            data.addProperty("descripcion", desc);

            // progress dialog en lo que se realizan los cambios
            progress = ProgressDialog.show(getContext(), "Actualizando","Espera", true);

            // peticion al servidor, manda el token y el body
            final Call<JsonObject> call = userRest.modifyProducto(token, data);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progress.dismiss(); // cierra el progress dialog
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
                    // recibe la respuesta del servidor y redirige a Productos
                    JsonObject json = response.body();
                    navController.navigate(R.id.nav_to_productos);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progress.dismiss(); // cierra esta cosa donde sea necesario
                    call.cancel();
                }
            });
        } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Termina de llenar los campos" , Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }
    }

    private void deleteProducto () {
        Client userRest = Node.getClient().create(Client.class); // declara la clase de peticiones al servidor

        // progress dialog para cuando se elimina algo
        progress = ProgressDialog.show(getContext(), "Eliminando","Espera", true);

        // manda la peticion al servidor, le manda el token y el id del pan
        final Call<JsonObject> call = userRest.deleteProducto(token, pan.getId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progress.dismiss(); // cierra el progress dialog
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

                // cuando se borra el producto lo regresa al fragmento de todos los productos
                JsonObject json = response.body();
                navController.navigate(R.id.nav_to_productos);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss(); // cierralo aqui tambien
                call.cancel();
            }
        });
    }

    // no lo uso, pero se queda como ejemplo para el futuro
    private void getProducto () { // ejemplo para consulta de un solo objeto mandando id
        Client userRest = Node.getClient().create(Client.class);

        progress = ProgressDialog.show(getContext(), "Cargando","Espera", true);
        final Call<JsonObject> call = userRest.getProducto(token,1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progress.dismiss();
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
                JsonArray jsonArray = json.getAsJsonArray("array");
                JsonObject object = jsonArray.get(0).getAsJsonObject();

                String nombre = object.getAsJsonPrimitive("nombre").getAsString();
                Float precio = object.getAsJsonPrimitive("precio").getAsFloat();
                String imagen = object.getAsJsonPrimitive("imagen").getAsString();
                String descripcion = object.getAsJsonPrimitive("descripcion").getAsString();
                System.out.println("get: nombre-> " + nombre + " precio-> " + precio + " desc-> " + descripcion + " imagen-> "+ imagen);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                call.cancel();
            }
        });
    }

}

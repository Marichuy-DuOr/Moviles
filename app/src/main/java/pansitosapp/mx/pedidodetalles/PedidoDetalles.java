package pansitosapp.mx.pedidodetalles;


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
import android.widget.Toast;

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
import pansitosapp.mx.pedidospendientes.Pedido;
import pansitosapp.mx.productos.Pan;
import pansitosapp.mx.productos.PanAdapter;
import pansitosapp.mx.productos.Productos;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PedidoDetalles extends Fragment implements View.OnClickListener {
    private NavController navController;
    ProgressDialog progress;

    private RecyclerView recyclerDetalles;
    DetalleAdapter detalleAdapter;

    ArrayList<Detalle> listaDetalles;

    Pedido pedido;
    String token;

    Button finalizarPedido, mapa;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pedido_detalles, container, false); // Cambiar el layout que corresponda
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // flecha para regresar al menu
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        pedido = (Pedido) getArguments().getSerializable("elPedido");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        finalizarPedido = view.findViewById(R.id.btnCompletada);
        finalizarPedido.setOnClickListener(this);
        mapa = view.findViewById(R.id.btnMapa);
        mapa.setOnClickListener(this);

        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        token = preferences.getString("token","No existe la informacion");

        if (pedido.getEstado() == 1) {
            finalizarPedido.setVisibility(View.GONE);
        }

        getAllDetalles(); // llena el array de panes

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
            case R.id.btnCompletada:
                completarPedido();
                break;
            case R.id.btnMapa:
                Bundle bundle = new Bundle();
                bundle.putSerializable("elPedido", pedido );
                navController.navigate(R.id.nav_to_mapa_entrega, bundle);
                break;
        }
    }

    private void getAllDetalles () {
        Client userRest = Node.getClient().create(Client.class);

        progress = ProgressDialog.show(getContext(), "Cargando","Espera", true);

        final Call<JsonObject> call = userRest.getDetalles(token, pedido.getId());
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

                listaDetalles = new ArrayList<>();

                JsonObject json = response.body();
                JsonArray jsonArray = json.getAsJsonArray("array");
                for (int i=0; i < jsonArray.size(); i++){

                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    Integer cantidad = object.getAsJsonPrimitive("cantidad").getAsInt();
                    String nombre = object.getAsJsonPrimitive("nombre").getAsString();
                    Float precio = object.getAsJsonPrimitive("precio").getAsFloat();
                    String imagen = object.getAsJsonPrimitive("imagen").getAsString();
                    String descripcion = object.getAsJsonPrimitive("descripcion").getAsString();

                    Detalle detalle = new Detalle( nombre, imagen, precio, cantidad, descripcion);
                    listaDetalles.add(detalle);
                }

                recyclerDetalles = getView().findViewById(R.id.recyclerid);
                recyclerDetalles.setLayoutManager(new LinearLayoutManager(getContext()));
                detalleAdapter =  new DetalleAdapter(listaDetalles);

                recyclerDetalles.setAdapter(detalleAdapter);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss(); // cerrar donde sea necesario porque no hay forma de cerrarlo desde la aplicacion
                call.cancel();
            }
        });
    }

    private void completarPedido () {
        Client userRest = Node.getClient().create(Client.class); // declara la clase de las peticiones al servidor

        JsonObject data = new JsonObject();
        data.addProperty("id", pedido.getId());

        // progress dialog en lo que se realizan los cambios
        progress = ProgressDialog.show(getContext(), "Actualizando","Espera", true);

        // peticion al servidor, manda el token y el body
        final Call<JsonObject> call = userRest.completarPedido(token, data);
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

                JsonObject json = response.body();
                Bundle bundle = new Bundle();
                bundle.putInt("elEstado", 0);
                navController.navigate(R.id.nav_to_pedidos_pendientes, bundle);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss(); // cierra esta cosa donde sea necesario
                call.cancel();
            }
        });
    }


}

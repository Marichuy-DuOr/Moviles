package pansitosapp.mx.pedidospendientes;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import pansitosapp.mx.R;
import pansitosapp.mx.http.Client;
import pansitosapp.mx.http.Node;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosPendientes extends Fragment implements PedidoInterface {

    private NavController navController;
    ProgressDialog progress;

    private RecyclerView recyclerPedidos;
    PedidoAdapter pedidoAdapter;

    ArrayList<Pedido> listaPedidos;

    Integer argumento;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pedidos_pendientes, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        argumento = getArguments().getInt("elEstado");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        getAllPedidos();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            ((AppCompatActivity)getActivity()).onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllPedidos () {
        Client userRest = Node.getClient().create(Client.class);

        progress = ProgressDialog.show(getContext(), "Cargando","Espera", true);

        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String token = preferences.getString("token","No existe la informacion");

        final Call<JsonObject> call = userRest.getAllPedidosActivos(token, argumento);
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
                listaPedidos = new ArrayList<>();

                JsonObject json = response.body();
                JsonArray jsonArray = json.getAsJsonArray("array");
                for (int i=0; i < jsonArray.size(); i++){

                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    Integer id = object.getAsJsonPrimitive("id").getAsInt();
                    Integer id_usuario = object.getAsJsonPrimitive("id_usuario").getAsInt();
                    String nombre = object.getAsJsonPrimitive("nombre").getAsString();
                    String fecha = object.getAsJsonPrimitive("fecha").getAsString();
                    Float total = object.getAsJsonPrimitive("total").getAsFloat();
                    Double lat = object.getAsJsonPrimitive("lat").getAsDouble();
                    Double lon = object.getAsJsonPrimitive("lon").getAsDouble();
                    Integer estado = object.getAsJsonPrimitive("estado").getAsInt();

                    fecha = fecha.replace("T", " ");
                    fecha = fecha.substring(0, 16);


                    Pedido pedido = new Pedido(id, id_usuario, nombre, fecha, total, lat, lon, estado);
                    listaPedidos.add(pedido);
                }

                recyclerPedidos = getView().findViewById(R.id.recyclerid);
                recyclerPedidos.setLayoutManager(new LinearLayoutManager(getContext()));

                pedidoAdapter =  new PedidoAdapter(listaPedidos);

                recyclerPedidos.setAdapter(pedidoAdapter);
                pedidoAdapter.setOnClick(PedidosPendientes.this);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progress.dismiss();
                call.cancel();
            }
        });
    }

    @Override
    public void onPedidoClick(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("elPedido", listaPedidos.get(pos) );
        navController.navigate(R.id.nav_to_pedido_detalles, bundle);
    }

}

package pansitosapp.mx.clientmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pansitosapp.mx.R;

public class ClientMenu  extends Fragment implements View.OnClickListener {

    // Hace basicamente lo mismo que el AdminMenu pero con las opciones del Cliente
    private NavController navController;
    String usuarioNombre;

    CardView opc1;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.client_menu, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        // getActivity().setTitle(R.string.title_nurse);

        cargarPreferencias();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        opc1 = view.findViewById(R.id.opcion1);
        opc1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.opcion1:
                navController.navigate(R.id.nav_to_mapa);
                break;
        }
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        /*String message = preferences.getString("message","No existe la informacion");
        String token = preferences.getString("token","No existe la informacion");
        Integer idUsuario = preferences.getInt("idUsuario",0);
        String rol = preferences.getString("rol","No existe la informacion");*/
        String nombre = preferences.getString("nombre","No existe la informacion");

        // System.out.println(nombre);
        usuarioNombre = nombre;
    }

    private void logout(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.commit();
        navController.navigate(R.id.nav_to_login);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_principal, menu);
        MenuItem userName = menu.findItem(R.id.btnUser);
        userName.setTitle(usuarioNombre);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnUser) {
            navController.navigate(R.id.nav_to_perfil_usuario);
        } else if (id == R.id.btnLogout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }
}
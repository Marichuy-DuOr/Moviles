package pansitosapp.mx.adminmenu;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import pansitosapp.mx.R;

public class AdminMenu extends Fragment implements View.OnClickListener {

    private NavController navController;
    String usuarioNombre;

    CardView opc1, opc2, opc3;
    Bundle bundle;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.admin_menu, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); //flecha de regresar oculta
        setHasOptionsMenu(true); // muesta el menu que tiene el nombre del usuario y el boton de logout
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
        opc2 = view.findViewById(R.id.opcion2);
        opc2.setOnClickListener(this);
        opc3 = view.findViewById(R.id.opcion3);
        opc3.setOnClickListener(this);

        bundle = new Bundle();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.opcion1:
                navController.navigate(R.id.nav_to_productos); // opciones de los productos registrados
                break;
            case R.id.opcion2:
                bundle.putInt("elEstado", 0);
                navController.navigate(R.id.nav_to_pedidos_pendientes, bundle);
                break;
            case R.id.opcion3:
                bundle.putInt("elEstado", 1);
                navController.navigate(R.id.nav_to_pedidos_pendientes, bundle);
                break;
        }
    }

    private void cargarPreferencias(){ // carga el nombre del usuario para mostrarlo en el menu
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        String nombre = preferences.getString("nombre","No existe la informacion");

        System.out.println(nombre);
        usuarioNombre = nombre;
    }

    private void logout(){ // borra las variables del usuario del sistema para hacer logout y lo leva al fragmento del login
        SharedPreferences preferences = getActivity().getSharedPreferences("Usuario",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.commit();
        navController.navigate(R.id.nav_to_login);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { //poner esta funcion si pones el menu
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_principal, menu);
        //cambian el nombre del usuario segun los datos del sistema
        MenuItem userName = menu.findItem(R.id.btnUser);
        userName.setTitle(usuarioNombre);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // tambien poner esta funcion siemple que pongas menu
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnUser) { // el nombre del usuario te lleva al perfil del usuario
            navController.navigate(R.id.nav_to_perfil_usuario);
        } else if (id == R.id.btnLogout) { // lo manda a la funcion para cerrar sesión
            logout();
        }

        return super.onOptionsItemSelected(item);
    }
}

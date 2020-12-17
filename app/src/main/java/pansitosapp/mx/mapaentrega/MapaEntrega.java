package pansitosapp.mx.mapaentrega;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import pansitosapp.mx.R;
import pansitosapp.mx.pedidospendientes.Pedido;

public class MapaEntrega extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private NavController navController;

    SupportMapFragment mapFragment;
    private GoogleMap mapa;

    Pedido pedido;

    Button btn;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mapa_entrega, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        pedido = (Pedido) getArguments().getSerializable("elPedido");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        mapFragment =  (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        btn = view.findViewById(R.id.button);
        btn.setOnClickListener(this);

    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        LatLng UPV = new LatLng(pedido.getLat(), pedido.getLon());
        mapa.addMarker(new MarkerOptions().position(UPV).title("Marker UPV"));
        // mapa.moveCamera(CameraUpdateFactory.newLatLng(UPV));
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(UPV, 17));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", pedido.getLat(), pedido.getLon());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                break;
        }
    }
}
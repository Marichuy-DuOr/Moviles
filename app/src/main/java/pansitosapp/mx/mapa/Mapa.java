package pansitosapp.mx.mapa;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pansitosapp.mx.R;

public class Mapa extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener{

    private NavController navController; //para navegar entre fragmentos

    ProgressDialog progress;

    SupportMapFragment mapFragment;
    private LatLng UPV;
    private GoogleMap mapa;

    Button btn1, btn2, btn3;

    double lat = 21.8835868;
    double lon = -102.2913539;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mapa, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        btn1 = view.findViewById(R.id.button1);
        btn1.setOnClickListener(this);
        btn2 = view.findViewById(R.id.button2);
        btn2.setOnClickListener(this);
        btn3 = view.findViewById(R.id.button3);
        btn3.setOnClickListener(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        UPV = new LatLng(lat, lon);
        mapa = googleMap;
        mapa.addMarker(new MarkerOptions().position(UPV).title("Marker UPV"));
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(UPV, 17));

        mapa.setOnMapClickListener(this);

        if (ActivityCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);

        }

    }

    @Override
    public void onMapClick(LatLng puntoPulsado) {
        mapa.clear();
        mapa.addMarker(new MarkerOptions().position(puntoPulsado)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        lat = puntoPulsado.latitude;
        lon = puntoPulsado.longitude;
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
            case R.id.button1:
                mapa.clear();
                mapa.addMarker(new MarkerOptions().position(mapa.getCameraPosition().target));
                lat = mapa.getCameraPosition().target.latitude;
                lon = mapa.getCameraPosition().target.longitude;
                break;
            case R.id.button2:
                mapa.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
                //mapa.moveCamera(CameraUpdateFactory.newLatLng(UPV)); // lo mismo que la de arriba pero sin animación
                break;
            case R.id.button3:
                System.out.println("Lat-> " + lat + " Lon-> " + lon);
                break;
        }
    }
}

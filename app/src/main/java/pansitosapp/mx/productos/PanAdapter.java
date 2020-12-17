package pansitosapp.mx.productos;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pansitosapp.mx.R;

public class PanAdapter extends RecyclerView.Adapter<PanAdapter.PanViewHolder>{

    ArrayList<Pan> listaPanes; // los panes
    PanInterface onClick; // la interfaz de panes

    public PanAdapter(ArrayList<Pan> listaPanes) {
        this.listaPanes = listaPanes;
    }

    @Override
    public PanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // poner el view que se va a reciclar
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false);
        return new PanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PanViewHolder holder, final int position) {
        // modifica por objeto del array
        holder.txtNombre.setText(listaPanes.get(position).getNombre());
        holder.txtDescripcion.setText(listaPanes.get(position).getDescripcion());
        holder.txtPrecio.setText("$" + listaPanes.get(position).getPrecio().toString());
        // para cargar la imagen con la url
        Picasso.with(holder.itemView.getContext()).load(listaPanes.get(position).getImagen()).into(holder.imagen);
        // agrega el click listener a cada uno de los objetos
        holder.opcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // aqui adentro irian cambios en el view como cuando sombreas notificaciones ya vistas
                onClick.onPanClick(position); // lo manda al onClick del fragmento padre que es el de Productos
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPanes.size();
    }

    public class PanViewHolder extends RecyclerView.ViewHolder {
        // para modificarlos segun las variables del pan
        TextView txtNombre,txtDescripcion, txtPrecio;
        ImageView imagen;

        CardView opcion; // tiene el id del card para el onClick

        public PanViewHolder(View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.name);
            txtDescripcion = (TextView) itemView.findViewById(R.id.desc);
            txtPrecio = (TextView) itemView.findViewById(R.id.precio);
            imagen = (ImageView) itemView.findViewById(R.id.img);
            opcion = (CardView) itemView.findViewById(R.id.opcion);

        }
    }

    public void setOnClick(PanInterface onClick) {this.onClick = onClick; } // agregar para el onClick
}

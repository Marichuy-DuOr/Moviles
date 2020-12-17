package pansitosapp.mx.pedidodetalles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import pansitosapp.mx.R;

public class DetalleAdapter extends RecyclerView.Adapter<DetalleAdapter.DetalleViewHolder>{

    ArrayList<Detalle> listaDetalles;

    public DetalleAdapter(ArrayList<Detalle> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    @Override
    public DetalleAdapter.DetalleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_detalle,parent,false);
        return new DetalleAdapter.DetalleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetalleAdapter.DetalleViewHolder holder, final int position) {
        holder.txtNombre.setText(listaDetalles.get(position).getNombre());
        holder.txtDescripcion.setText(listaDetalles.get(position).getDescripcion());
        holder.txtPrecio.setText("$" + listaDetalles.get(position).getPrecio().toString());
        Picasso.with(holder.itemView.getContext()).load(listaDetalles.get(position).getImagen()).into(holder.imagen);
        holder.txtCantidad.setText(" " + listaDetalles.get(position).getCantidad().toString() + " ");
    }

    @Override
    public int getItemCount() {
        return listaDetalles.size();
    }

    public class DetalleViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio, txtCantidad, txtDescripcion;
        ImageView imagen;

        public DetalleViewHolder(View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.name);
            txtDescripcion = (TextView) itemView.findViewById(R.id.desc);
            txtPrecio = (TextView) itemView.findViewById(R.id.precio);
            txtCantidad = (TextView) itemView.findViewById(R.id.cantidad);
            imagen = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}

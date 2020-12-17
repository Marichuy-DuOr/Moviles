package pansitosapp.mx.pedidospendientes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pansitosapp.mx.R;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>{

    ArrayList<Pedido> listaPedidos;
    PedidoInterface onClick;

    public PedidoAdapter(ArrayList<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @Override
    public PedidoAdapter.PedidoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_pedido,parent,false);
        return new PedidoAdapter.PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PedidoAdapter.PedidoViewHolder holder, final int position) {
        holder.txtId.setText("Pedido " + listaPedidos.get(position).getId().toString());
        holder.txtNombre.setText(listaPedidos.get(position).getNombre());
        holder.txtFecha.setText(listaPedidos.get(position).getFecha());
        holder.txtTotal.setText("Total $" + listaPedidos.get(position).getTotal().toString());

        holder.opcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onPedidoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtNombre, txtFecha, txtTotal;
        CardView opcion;

        public PedidoViewHolder(View itemView) {
            super(itemView);
            txtId = (TextView) itemView.findViewById(R.id.pedido);
            txtNombre = (TextView) itemView.findViewById(R.id.nombre);
            txtFecha = (TextView) itemView.findViewById(R.id.fecha);
            txtTotal = (TextView) itemView.findViewById(R.id.total);
            opcion = (CardView) itemView.findViewById(R.id.opcion);
        }
    }

    public void setOnClick(PedidoInterface onClick) { this.onClick = onClick; }
}

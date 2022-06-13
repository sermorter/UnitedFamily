package com.example.unitedfamily;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
// Sergio Moreno Terán

public class AdapterPremios extends RecyclerView.Adapter<AdapterPremios.ViewHolder> implements  View.OnClickListener{
    LayoutInflater inflater;
    ArrayList<Premios> model;

    //Listener
    private View.OnClickListener listener;

    public AdapterPremios(Context context, ArrayList<Premios> model) {
        this.inflater = LayoutInflater.from(context);
        this.model=model;
    }
    @NonNull
    @Override
    public AdapterPremios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lista_premios,parent,false);
        view.setOnClickListener(this);
        return new AdapterPremios.ViewHolder(view);
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull AdapterPremios.ViewHolder holder, int position) {
        String nombre = model.get(position).getNombre();
        String descripcion = model.get(position).getDescripcion();
        String precio = model.get(position).getPrecio();

        holder.nombres.setText(nombre);
        holder.descripcion.setText(descripcion);
        holder.precio.setText(precio);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    @Override
    public void onClick(View view) {
        if (listener!=null){
            listener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nombres,descripcion,precio;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombres=itemView.findViewById(R.id.nombre_hijo);
            descripcion = itemView.findViewById(R.id.descripcion_premio);
            precio = itemView.findViewById(R.id.precio_premio);
        }
    }
}

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

public class AdapterTareas extends RecyclerView.Adapter<AdapterTareas.ViewHolder> implements  View.OnClickListener{

    LayoutInflater inflater;
    ArrayList<Tareas> model;

    //Listener
    private View.OnClickListener listener;

    public AdapterTareas(Context context, ArrayList<Tareas> model){
        this.inflater = LayoutInflater.from(context);
        this.model=model;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lista_tareas,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre = model.get(position).getNombre();
        String descripcion = model.get(position).getDescripcion();
        String premios = model.get(position).getPremio();

        holder.nombres.setText(nombre);
        holder.descripcion.setText(descripcion);
        holder.premio.setText(premios);
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

        TextView nombres,descripcion,premio;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombres=itemView.findViewById(R.id.nombre_hijo);
            descripcion = itemView.findViewById(R.id.descripcion_premio);
            premio = itemView.findViewById(R.id.precio_premio);
        }
    }
}


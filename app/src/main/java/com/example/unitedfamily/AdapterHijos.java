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

public class AdapterHijos extends RecyclerView.Adapter<AdapterHijos.ViewHolder> implements  View.OnClickListener{
    LayoutInflater inflater;
    ArrayList<Hijos> model;

    //Listener
    private View.OnClickListener listener;

    public AdapterHijos(Context context, ArrayList<Hijos> model) {
        this.inflater = LayoutInflater.from(context);
        this.model=model;
    }
    @NonNull
    @Override
    public AdapterHijos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lista_hijos,parent,false);
        view.setOnClickListener(this);
        return new AdapterHijos.ViewHolder(view);
    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(@NonNull AdapterHijos.ViewHolder holder, int position) {
        String nombre = model.get(position).getNombre();


        holder.nombres.setText(nombre);

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

        TextView nombres;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombres=itemView.findViewById(R.id.nombre_hijo);

        }
    }
}

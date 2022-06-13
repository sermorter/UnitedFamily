package com.example.unitedfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
// Sergio Moreno Terán

public class activityListaHijosPadre extends AppCompatActivity {
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    private ConstraintLayout gif;
    DatabaseReference database;
    DatabaseReference databaseRF;
    AdapterHijos adapterHijos;
    RecyclerView recyclerViewHijos;
    ArrayList<Hijos> list;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_hijos_padre);
        //Inicializo el gif
        gif = findViewById(R.id.gif);
        gif.setVisibility(View.INVISIBLE);

        //inicializamos firebase custom
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Inicio la instancia de mi base de datos
        database = FirebaseDatabase.getInstance().getReference();
        databaseRF = FirebaseDatabase.getInstance().getReference("historial");

        list = new ArrayList<>();
        recyclerViewHijos = (RecyclerView) findViewById(R.id.recyclerViewHijos);

        //Relleno lista con la BD
        cargarListaPadre();



        // Metodo para actualizar el recyclerview
        swipe = findViewById(R.id.swipeRefreshLayout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Hago el gif visible , limpio la lista, y la vuelvo a rellenar llamando a la BD
                gif.setVisibility(View.VISIBLE);
                list.clear();
                cargarListaPadre();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mostrarData();
                        adapterHijos.notifyDataSetChanged();
                        swipe.setRefreshing(false);
                    }
                }, 2000);


            }
        });

        gif.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mostrarData();
                //Metodo para mostrar un mensaje si el recyclerView esta vacio
                if(recyclerViewHijos.getAdapter() == null || recyclerViewHijos.getAdapter().getItemCount()==0 ){
                    Toast.makeText(activityListaHijosPadre.this, R.string.tu_hijos_canjeado, Toast.LENGTH_SHORT).show();

                }
            }
        }, 2000);



    }



    public void cargarListaPadre() {
        // añadir a recyclerview

        databaseRF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Variable que voy incrementando para comporbar con la bd todos los premios
                    int aux = 1;
                    // Bandera para salir del while
                    boolean salir = false;
                    String descripción ="";
                    // Compruebo segun el usuario logueado los hijos que tienen de padre el user_id que esta logueado ahora mismo
                    if (currentUser.getEmail().split("@")[0].equals(dataSnapshot.child("user_id").getValue())) {
                        System.out.println(dataSnapshot.child("user_id").getValue());
                        Hijos hijo = new Hijos();
                        hijo.setNombre("Nombre : "+dataSnapshot.getKey());
                        // Cuento el numero de hijos que tiene y aumento el contador
                        while(!salir){
                            if (dataSnapshot.child(String.valueOf(aux)).exists()){
                                descripción +="\n"+dataSnapshot.child(String.valueOf(aux)).getValue().toString();
                                aux++;
                            }else{
                                salir=true;

                            }
                        }

                        hijo.setDescripcion(descripción);
                        list.add(hijo);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //Metodo para mostrar los datos obtenidos de los metodos anteriores y pasarlo a una recycler view
    public void mostrarData() {
        recyclerViewHijos.setLayoutManager(new LinearLayoutManager(this));
        adapterHijos = new AdapterHijos(this, list);
        recyclerViewHijos.setAdapter(adapterHijos);


        adapterHijos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String nombre = list.get(recyclerViewHijos.getChildAdapterPosition(view)).getNombre();
                String todo = list.get(recyclerViewHijos.getChildAdapterPosition(view)).getDescripcion();

                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle(getString(R.string.nombre)+nombre);
                dialog.setMessage(todo);
                dialog.setIcon(R.drawable.user);
                dialog.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });



                dialog.show();

            }
        });
        gif.setVisibility(View.INVISIBLE);


    }
}

package com.example.unitedfamily;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmenttareashijo#newInstance} factory method to
 * create an instance of this fragment.
 */
// Sergio Moreno Terán

public class fragmenttareashijo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View vista;

    private ConstraintLayout gif;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    DatabaseReference database;
    DatabaseReference databaseRF;

    ArrayList<Tareas> list;

    SwipeRefreshLayout swipe;

    AdapterTareas adapterTareas;
    RecyclerView recyclerViewTareas;

    public fragmenttareashijo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmenttareashijo.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmenttareashijo newInstance(String param1, String param2) {
        fragmenttareashijo fragment = new fragmenttareashijo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_fragmenttareashijo, container, false);
        //Inicializo el gif
        gif = vista.findViewById(R.id.gif);
        gif.setVisibility(View.INVISIBLE);
        // Inicio la instancia de mi base de datos
        database = FirebaseDatabase.getInstance().getReference();
        databaseRF = FirebaseDatabase.getInstance().getReference("tareas");
        //inicializamos firebase custom
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        list = new ArrayList<>();
        recyclerViewTareas = (RecyclerView) vista.findViewById(R.id.recyclerViewPremios);

        recuperarPadre();


        // Metodo para actualizar el recyclerview
        swipe = vista.findViewById(R.id.swipeRefreshLayout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Hago el gif visible , limpio la lista, y la vuelvo a rellenar llamando a la BD
                        gif.setVisibility(View.VISIBLE);
                        list.clear();
                        recuperarPadre();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mostrarData();
                                adapterTareas.notifyDataSetChanged();
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
                adapterTareas.notifyDataSetChanged();
                swipe.setRefreshing(false);
                //Metodo para mostrar un mensaje si el recyclerView esta vacio
                if(recyclerViewTareas.getAdapter() == null || recyclerViewTareas.getAdapter().getItemCount()==0 ){
                    Toast.makeText(getActivity().getBaseContext(), R.string.ninguna_tarea_realizar, Toast.LENGTH_SHORT).show();

                }
            }
        }, 2000);

        return vista;
    }
    // Metodo para que me devuelva el familiar asociado al current user y llamar al metodo para cargar a la lista la tarea gracias al nombre del padre
    public void recuperarPadre(){
        System.out.println("has entrado en recuperar padre");
        final String[] padre = {""};
        database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("familiaresasociados").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {



                    cargarListaPadre(task.getResult().getValue().toString());

                }
                else {
                    //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
    public void cargarListaPadre(String padre){
    // añadir a recyclerview
       databaseRF.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tareas tarea = new Tareas();

                   if (padre.equals(dataSnapshot.child("user_id").getValue())){
                       tarea.setDescripcion("Descripcion : "+dataSnapshot.child("descripcion_tarea").getValue().toString());
                       tarea.setNombre(dataSnapshot.child("nombre_tarea").getValue().toString());
                       tarea.setPremio(dataSnapshot.child("premio_tarea").getValue().toString());
                       list.add(tarea);

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
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTareas = new AdapterTareas(getContext(), list);
        recyclerViewTareas.setAdapter(adapterTareas);


        adapterTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String premio = list.get(recyclerViewTareas.getChildAdapterPosition(view)).getPremio();
                String nombre = list.get(recyclerViewTareas.getChildAdapterPosition(view)).getNombre();

                gif.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        final int[] aux = {0};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(R.string.estas_seguro);
                        dialog.setPositiveButton(R.string.realizar_tarea, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("puntos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            aux[0] = Integer.parseInt(task.getResult().getValue().toString());
                                            // Actualizo datos de los puntos
                                            database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("puntos").setValue(aux[0]+=Integer.parseInt(premio));
                                            //Elimino la tarea de la BD
                                            databaseRF.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        // Compruebo el nombre de la tarea y la elimino
                                                        if (nombre.equals(dataSnapshot.child("nombre_tarea").getValue())){
                                                            databaseRF.child(nombre).removeValue();
                                                            Toast.makeText(getActivity().getBaseContext(), getString(R.string.tarea_realizada) +
                                                                    getString(R.string.refresca_la_pantalla) , Toast.LENGTH_SHORT).show();
                                                            // Hago visible el gif
                                                            gif.setVisibility(View.VISIBLE);
                                                            // IMPORTANTE-- Dos handler con 2 segundos de espera por metodos asincronos
                                                            // La bD al eliminar un objeto hace unas cosas extrañas , debido a eso
                                                            // he añadido 2 handler , 1 para que limpie la lista y la recupere
                                                            // El ultimo para rellenar el adapter con la nueva lista correctamente actualizada
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                            // Refresco la pantalla para que se muestre la lista de premios actrualizada
                                                            // Limpio la lista
                                                            list.clear();
                                                            // Vuelvo a rellenar la lista segun la BD
                                                            recuperarPadre();
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                    // Actualizar Lista de Premios
                                                                    for (Tareas e: list) {
                                                                        System.out.println("lista final"+e.toString());
                                                                    }
                                                                     mostrarData();
                                                                    // Hago invisible el gif
                                                                    gif.setVisibility(View.INVISIBLE);
                                                                    adapterTareas.notifyDataSetChanged();
                                                                        }
                                                                    }, 2000);
                                                                }
                                                            }, 2000);

                                                        }
                                                    }

                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });

                                        }
                                        else {
                                            //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                        }
                                    }
                                });



                            }
                        });

                        dialog.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        dialog.show();

                        gif.setVisibility(View.INVISIBLE);

                    }
                }, 1000);


            }
        });
        gif.setVisibility(View.INVISIBLE);


    }



}
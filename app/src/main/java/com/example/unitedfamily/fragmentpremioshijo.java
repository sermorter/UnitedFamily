package com.example.unitedfamily;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentpremioshijo#newInstance} factory method to
 * create an instance of this fragment.
 */
// Sergio Moreno Terán

public class fragmentpremioshijo extends Fragment {

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
    DatabaseReference databaseHistorial;

    ArrayList<Premios> list;
    SwipeRefreshLayout swipe;


    AdapterPremios adapterPremios;
    RecyclerView recyclerViewPremios;
    public fragmentpremioshijo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentpremioshijo.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentpremioshijo newInstance(String param1, String param2) {
        fragmentpremioshijo fragment = new fragmentpremioshijo();
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
        vista = inflater.inflate(R.layout.fragment_fragmentpremioshijo, container, false);
        //Inicializo el gif
        gif = vista.findViewById(R.id.gif);
        gif.setVisibility(View.INVISIBLE);
        // Inicio la instancia de mi base de datos
        database = FirebaseDatabase.getInstance().getReference();
        databaseRF = FirebaseDatabase.getInstance().getReference("premios");
        //inicializamos firebase custom
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseHistorial = FirebaseDatabase.getInstance().getReference("historial");


        list = new ArrayList<>();
        recyclerViewPremios = (RecyclerView) vista.findViewById(R.id.recyclerViewPremios);


        //Relleno lista con la BD
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
                        adapterPremios.notifyDataSetChanged();
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
                if(recyclerViewPremios.getAdapter() == null || recyclerViewPremios.getAdapter().getItemCount()==0 ){
                    Toast.makeText(getActivity().getBaseContext(), R.string.premio_canjear_ninguno, Toast.LENGTH_SHORT).show();

                }
            }
        }, 2000);
        return vista;
    }
    // Metodo para que me devuelva el familiar asociado al current user y llamar al metodo para cargar a la lista premios gracias al nombre del padre
    public void recuperarPadre(){
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
                    Premios premio = new Premios();

                    if (padre.equals(dataSnapshot.child("user_id").getValue())){
                        premio.setDescripcion("Descripcion : "+dataSnapshot.child("descripcion_premio").getValue().toString());
                        premio.setNombre(dataSnapshot.child("nombre_premio").getValue().toString());
                        premio.setPrecio(dataSnapshot.child("precio_premio").getValue().toString());
                        list.add(premio);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // Metodo que devuelve la fecha actual con un formato especifico
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String fechaActual(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(LocalDateTime.now()).toString();
    }
    //Metodo para al canjkear un premio guardar los datos en una tabla nueva llamada historial
    public void obtenerPadreYAñadirBDHistorial(String nombre,String precio){
        final String[] padre = {""};
        final int[] aux = {1};
        database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("familiaresasociados").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Obtengo el numero de premios en el historial
                    padre[0] = task.getResult().getValue().toString();
                    databaseHistorial.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(currentUser.getEmail().toString().split("@")[0])){
                                    Boolean bandera = false;
                                    while (!bandera){
                                        if (dataSnapshot.child(String.valueOf(aux[0])).exists()) {
                                            aux[0]++;
                                        } else bandera = true;
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //Añado los datos a la BD Historial
                    // Metodo Handler con 1 segundo de retraso porque al obtener el indice es un metodo asincrono
                    new Handler().postDelayed(new Runnable(){
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            databaseHistorial.child(currentUser.getEmail().toString().split("@")[0]).child("user_id").setValue(padre[0]);
                            databaseHistorial.child(currentUser.getEmail().toString().split("@")[0]).child(String.valueOf(aux[0]).toString()).setValue("" +
                                    "Premio Canjeado: "+
                                    nombre+" "+
                                    precio+" "+
                                   fechaActual());

                        }

                    }, 1000);

                }
                else {
                    //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
    //Metodo para mostrar los datos obtenidos de los metodos anteriores y pasarlo a una recycler view
    public void mostrarData() {
        recyclerViewPremios.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterPremios = new AdapterPremios(getContext(), list);
        recyclerViewPremios.setAdapter(adapterPremios);


        adapterPremios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String premio = list.get(recyclerViewPremios.getChildAdapterPosition(view)).getPrecio();
                String nombre = list.get(recyclerViewPremios.getChildAdapterPosition(view)).getNombre();

                gif.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        final int[] aux = {0};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle(R.string.estas_seguro);
                        dialog.setPositiveButton(R.string.canjear_premio, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("puntos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (Integer.parseInt(task.getResult().getValue().toString()) >= Integer.parseInt(premio)) {
                                                aux[0] = Integer.parseInt(task.getResult().getValue().toString());
                                                // Actualizo datos de los puntos
                                                database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("puntos").setValue(aux[0] -= Integer.parseInt(premio));
                                                //Elimino el premio de la BD
                                                databaseRF.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            // Compruebo el nombre del premio y la elimino
                                                            if (nombre.equals(dataSnapshot.child("nombre_premio").getValue())) {
                                                                databaseRF.child(nombre).removeValue();
                                                                Toast.makeText(getActivity().getBaseContext(), getString(R.string.premio_puntos_actualizados) +
                                                                        getString(R.string.refresca_la_pantalla), Toast.LENGTH_SHORT).show();
                                                                //Añado el la BD Historial los campos
                                                                obtenerPadreYAñadirBDHistorial(nombre,premio);
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
                                                                                for (Premios e: list) {
                                                                                    System.out.println("lista final"+e.toString());
                                                                                }
                                                                                mostrarData();
                                                                                // Hago invisible el gif
                                                                                gif.setVisibility(View.INVISIBLE);
                                                                                adapterPremios.notifyDataSetChanged();
                                                                            }
                                                                        }, 2000);
                                                                    }
                                                                }, 2000);
                                                            }
                                                        }

                                                    }


                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(getActivity().getBaseContext(), R.string.error_basededatos  , Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }else {
                                                Toast.makeText(getActivity().getBaseContext(), R.string.puntos_insuficientes , Toast.LENGTH_SHORT).show();
                                            }
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
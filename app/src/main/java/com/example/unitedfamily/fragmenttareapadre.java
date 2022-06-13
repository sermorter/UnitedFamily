package com.example.unitedfamily;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmenttareapadre#newInstance} factory method to
 * create an instance of this fragment.
 */
// Sergio Moreno Terán

public class fragmenttareapadre extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference database;

    Button añadir;
    TextInputEditText inputNombre;
    TextInputEditText inputDescripcion;
    TextInputEditText inputPremio;

    Spinner spinner;
    View view;
    ImageView imagenTareas;
    public fragmenttareapadre() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_fragmenttareapadre.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmenttareapadre newInstance(String param1, String param2) {
        fragmenttareapadre fragment = new fragmenttareapadre();
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
        view = inflater.inflate(R.layout.fragment_fragmenttareapadre, container, false);

        // Inicio la instancia de mi base de datos
        database = FirebaseDatabase.getInstance().getReference();

        //inicializamos firebase custom
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // inicializo los componentes con sus respectivas Ids

        imagenTareas = view.findViewById(R.id.imageViewTareas);
        spinner = view.findViewById(R.id.spinner);
        añadir = view.findViewById(R.id.botonconfirmar2);
        inputNombre = view.findViewById(R.id.edittextnombre);
        inputDescripcion = view.findViewById(R.id.edittextddesc);
        inputPremio = view.findViewById(R.id.edittextpremio);
        //LLamo al metodo para que me rellene el spinner
        rellenarSpinner();
       /* int id  = imagenTareas.getId();
        String idname = getResources().getResourceName(id);
        Toast.makeText(getActivity().getBaseContext(), idname, Toast.LENGTH_SHORT).show();*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //llamo al metodo para cambiar la imagen segun el usuario selecciona
                cambiarImagen();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Onlick del boton añadir
        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llamo al metodo para que añada a la base de datos las tareas que ha añadido el padre
                if (validarCampos()){
                    añadirDatosBD();
                }
            }
        });
        return view;
    }
    public void rellenarSpinner(){
        List<String> lista = new ArrayList<>();
        lista.add("Imagen 1");
        lista.add("Imagen 2");
        lista.add("Imagen 3");
        spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,lista));

    }
    public void añadirDatosBD(){
        // En un futuro el premio e imagen

        // ENVIAR TAREA COMPRADO A FIREBASE
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre_tarea", inputNombre.getText().toString());
        datos.put("descripcion_tarea", inputDescripcion.getText().toString());
        datos.put("premio_tarea", inputPremio.getText().toString());
        datos.put("user_id", currentUser.getEmail().toString().split("@")[0]);
        database.child("tareas").child(inputNombre.getText().toString()).setValue(datos);
        Toast.makeText(getActivity().getBaseContext(), R.string.tarea_añadida , Toast.LENGTH_SHORT).show();
        // Borro los campos
        borrarCampos();
    }


// Metodo que cambia de imagen // futura implementacion
    public void cambiarImagen(){
        if (spinner.getSelectedItem().toString().equals("Imagen 1")){
            imagenTareas.setImageResource(R.drawable.tarea1);
        }
        if (spinner.getSelectedItem().toString().equals("Imagen 2")){
            imagenTareas.setImageResource(R.drawable.tarea2);

        }
        if (spinner.getSelectedItem().toString().equals("Imagen 3")){
            imagenTareas.setImageResource(R.drawable.tarea3);

        }

    }
    // Metodo que borra campos
    public void borrarCampos(){
        inputNombre.setText("");
        inputDescripcion.setText("");
        inputPremio.setText("");
    }
    // Metodo que valida que los campos no esten vacios
    public boolean validarCampos(){
        if (inputNombre.getText().toString().equals("")){
            Toast.makeText(getActivity().getBaseContext(),R.string.rellene_nombre, Toast.LENGTH_SHORT).show();
            return false;
        }else if(inputDescripcion.getText().toString().equals("")){
            Toast.makeText(getActivity().getBaseContext(),R.string.rellene_descripcion , Toast.LENGTH_SHORT).show();
            return false;

        }else if(inputPremio.getText().toString().equals("")|| !esNumero(inputPremio.getText().toString())){
            Toast.makeText(getActivity().getBaseContext(),R.string.rellene_precio_numerico, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    // Metodo para comprobar si el parametro es un Integer
    public boolean esNumero(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }



}


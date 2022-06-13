package com.example.unitedfamily;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentpremiospadre#newInstance} factory method to
 * create an instance of this fragment.
 */
// Sergio Moreno Terán

public class fragmentpremiospadre extends Fragment {

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
    TextInputEditText inputPrcio;

    public fragmentpremiospadre() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentpremiospadre.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentpremiospadre newInstance(String param1, String param2) {
        fragmentpremiospadre fragment = new fragmentpremiospadre();
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
        View vista =inflater.inflate(R.layout.fragment_fragmentpremiospadre, container, false);
                // Inicio la instancia de mi base de datos
                database = FirebaseDatabase.getInstance().getReference();

        //inicializamos firebase custom
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // inicializo los componentes con sus respectivas Ids


        añadir = vista.findViewById(R.id.botonconfirmar3);
        inputNombre = vista.findViewById(R.id.edittextnombre);
        inputDescripcion = vista.findViewById(R.id.edittextddesc);
        inputPrcio = vista.findViewById(R.id.edittextpremio);

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llamo al metodo para que añada a la base de datos las tareas que ha añadido el padre
                if (validarCampos()){
                    añadirDatosBD();
                }
            }
        });

        return vista;
    }
    // Metodo para añadir a la bd los datos

    public void añadirDatosBD(){

        // ENVIAR TAREA COMPRADO A FIREBASE
        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre_premio", inputNombre.getText().toString());
        datos.put("descripcion_premio", inputDescripcion.getText().toString());
        datos.put("precio_premio", inputPrcio.getText().toString());
        datos.put("user_id", currentUser.getEmail().toString().split("@")[0]);
        database.child("premios").child(inputNombre.getText().toString()).setValue(datos);
        Toast.makeText(getActivity().getBaseContext(), R.string.premio_añadido , Toast.LENGTH_SHORT).show();
        // Borro los campos
        borrarCampos();
    }
    // Metodo para borrar los campos

    private void borrarCampos() {
        inputPrcio.setText("");
        inputNombre.setText("");
        inputDescripcion.setText("");
    }

    // Metodo para validar que los campos no estén vacios
    public boolean validarCampos(){
        if (inputNombre.getText().toString().equals("")){
            Toast.makeText(getActivity().getBaseContext(), R.string.rellene_nombre , Toast.LENGTH_SHORT).show();
            return false;
        }else if(inputDescripcion.getText().toString().equals("")){
            Toast.makeText(getActivity().getBaseContext(), R.string.rellene_descripcion , Toast.LENGTH_SHORT).show();
            return false;

        }else if(inputPrcio.getText().toString().equals("")|| !esNumero(inputPrcio.getText().toString())){
            Toast.makeText(getActivity().getBaseContext(), R.string.rellene_precio_numerico , Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    // Metodo para comporbar validar que es un numero Integer
    public boolean esNumero(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }
}
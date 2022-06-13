package com.example.unitedfamily;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentiniciohijo#newInstance} factory method to
 * create an instance of this fragment.
 */
// Sergio Moreno Terán

public class fragmentiniciohijo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnconfirmar;
    private ConstraintLayout gif;
    View vista;
    // Conectar firebase
    DatabaseReference database;

    ImageView padrehijo;
    TextView textoBienvenido;
    TextInputEditText inputFamiliar;
    TextInputLayout editTextFamiliar;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    public fragmentiniciohijo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment asociarPadreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentiniciohijo newInstance(String param1, String param2) {
        fragmentiniciohijo fragment = new fragmentiniciohijo();
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
        vista = inflater.inflate(R.layout.fragment_fragmentiniciohijo, container, false);
        // Inicio la instancia de mi base de datos
        database = FirebaseDatabase.getInstance().getReference();

        //inicializamos firebase custom
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        gif = vista.findViewById(R.id.gif);

        textoBienvenido = vista.findViewById(R.id.textobienvenido);
        editTextFamiliar = vista.findViewById(R.id.labelnombre);
        btnconfirmar = vista.findViewById(R.id.botonconfirmar);
        inputFamiliar =  vista.findViewById(R.id.edittextnombre);
        padrehijo = vista.findViewById(R.id.padrehijo);
        /*  Hago invisible todos los componentes nada mas inflar la vista y
            dependiendo del resultado del comprobarFamiliar();
            muestro unos componentes u otros */
        padrehijo.setVisibility(View.INVISIBLE);
        btnconfirmar.setVisibility(View.INVISIBLE);
        inputFamiliar.setVisibility(View.INVISIBLE);
        editTextFamiliar.setVisibility(View.INVISIBLE);


        gif.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Para ocultar el boton de confirmar y el editText si ya tiene un familiar asociado
                comprobarFamiliar();
            }
        }, 1000);
        btnconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inputFamiliar.getEditableText().toString().equals("")){
                    Toast.makeText(getActivity().getBaseContext(),  R.string.introduce_nombre, Toast.LENGTH_SHORT).show();
                }else {
                   Toast.makeText(getActivity().getBaseContext(),  R.string.familiarasociado, Toast.LENGTH_SHORT).show();
                    // Para ocultar el boton de confirmar y el editText al haber asociado a un familiar
                    gif.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            // Para ocultar el boton de confirmar y el editText si ya tiene un familiar asociado
                            comprobarFamiliar();
                        }
                    }, 1000);
                }

                database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("familiaresasociados").setValue(inputFamiliar.getEditableText().toString());

            }
        });


        return vista;
    }
    //Metodo para comporbar si tiene un familiar asociado y hacer invisible los campos para rellenar

    public void comprobarFamiliar(){

                database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("familiaresasociados").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.getResult().getValue().equals("")) {
                            Toast.makeText(getActivity().getBaseContext(), R.string.yatienesunfamiliarasociado , Toast.LENGTH_SHORT).show();
                            btnconfirmar.setVisibility(View.INVISIBLE);
                            inputFamiliar.setVisibility(View.INVISIBLE);
                            editTextFamiliar.setVisibility(View.INVISIBLE);
                            textoBienvenido.setText(R.string.yatienesunfamiliarasociado);
                            padrehijo.setVisibility(View.VISIBLE);
                            gif.setVisibility(View.INVISIBLE);

                        }
                        else {
                            gif.setVisibility(View.INVISIBLE);
                            btnconfirmar.setVisibility(View.VISIBLE);
                            inputFamiliar.setVisibility(View.VISIBLE);
                            editTextFamiliar.setVisibility(View.VISIBLE);
                            padrehijo.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }
}
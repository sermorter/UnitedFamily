package com.example.unitedfamily;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentiniciopadre#newInstance} factory method to
 * create an instance of this fragment.
 */
// Sergio Moreno Terán

public class fragmentiniciopadre extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View vista;
    Button btcompartir;
    public fragmentiniciopadre() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentiniciopadre.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentiniciopadre newInstance(String param1, String param2) {
        fragmentiniciopadre fragment = new fragmentiniciopadre();
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
        vista = inflater.inflate(R.layout.fragment_fragmentiniciopadre, container, false);
        //Inicializo el boton
        btcompartir = vista.findViewById(R.id.botoncomparte);

        //LLamo al onclick del boton de compartir la apk
        btcompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LLamo al metodo que comparte la aplicación
                compartirApk();
            }
        });
        return vista;
    }
    // Método que crea un intent con un texto copiado , el link de descargar la apk si estuviera subida en la play store
    // EL link es de una aplicacion llamada United Family
    public void compartirApk(){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.app_name));
            String aux = getString(R.string.descarga_apk)+"\n";
            aux = aux + "https://play.google.com/store/apps/details?id=com.sharefaith.sfchurchapp_47b5fb92d1d85891&gl=ES";
            // Esto seria si mi apk estuviera publicada en la apk store
            //aux = aux+"https://play.google.com/store/apps/details?id"+ getActivity().getBaseContext().getPackageName();
            i.putExtra(Intent.EXTRA_TEXT,aux);
            startActivity(i);
        }catch(Exception e){

        }
    }

}
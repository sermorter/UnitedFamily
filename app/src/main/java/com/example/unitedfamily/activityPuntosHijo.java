package com.example.unitedfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activityPuntosHijo extends AppCompatActivity {

    TextView tvpuntos;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    ScratchView scratchView;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntos_hijo);

        tvpuntos = findViewById(R.id.tvpuntos);
        database = FirebaseDatabase.getInstance().getReference();

        // Obtener usuario logueado
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        // Obtengo los puntos actuales de current user
       database.child("users").child(currentUser.getEmail().toString().split("@")[0]).child("puntos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
           final int[] aux = {0};
           @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    aux[0] = Integer.parseInt(task.getResult().getValue().toString());
                    // Actualizo el textView de los puntos
                    tvpuntos.setText(getString(R.string.saldo)+" "+aux[0]+" "+getString(R.string.puntos));
                }
                else {
                    //Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

        // Metodo que cuando se borra un 40% de la scratch image , sale un toast(mensaje)

        scratchView = findViewById(R.id.scratchView);
        scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                Toast.makeText(activityPuntosHijo.this, getString(R.string.puntos_actuales)+ tvpuntos.getText().toString().split(":")[1], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {

            }
        });
            }
        }, 1000);
    }


}

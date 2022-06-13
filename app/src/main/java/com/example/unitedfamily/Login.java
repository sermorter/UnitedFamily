package com.example.unitedfamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Sergio Moreno Terán
public class Login extends AppCompatActivity {
    private ImageView imageView;
    private Button login,registerbt;
    String urlInstagram="https://www.instagram.com/unitedfamilysmt/";
    String urlTwitter="https://twitter.com/UnitedFamilySMT";
    String urlYoutube="https://www.youtube.com/channel/UCBJsEnd7kODIjxdekOeSUXg";

    TextInputLayout inputUser;
    TextInputLayout inputPassword;

    AwesomeValidation awesomeValidation;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase database;
    DatabaseReference mDatabase;

    ImageView imagenfondo;

    ImageButton yt,tw,ig;
    DatabaseReference ref;
    private ConstraintLayout gif;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.loginbt);
        registerbt = (Button) findViewById(R.id.registerbt);

        inputUser = (TextInputLayout) findViewById(R.id.textInputUser);
        inputPassword =(TextInputLayout) findViewById(R.id.textInputPassword);

        gif = findViewById(R.id.gif);
        gif.setVisibility(View.INVISIBLE);


        yt = (ImageButton) findViewById(R.id.youtubebt);
        yt.setVisibility(View.INVISIBLE);
        tw = (ImageButton) findViewById(R.id.twitterbt);
        tw.setVisibility(View.INVISIBLE);
        ig = (ImageButton) findViewById(R.id.instabt);
        ig.setVisibility(View.INVISIBLE);

        imagenfondo = findViewById(R.id.fondoInicio);
        imagenfondo.setVisibility(View.VISIBLE);

        database = FirebaseDatabase.getInstance();

        login.setVisibility(View.INVISIBLE);
        registerbt.setVisibility(View.INVISIBLE);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                imagenfondo.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
                registerbt.setVisibility(View.VISIBLE);
                tw.setVisibility(View.VISIBLE);
                yt.setVisibility(View.VISIBLE);
                ig.setVisibility(View.VISIBLE);
            }
        }, 3000);

        //inicializo la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        // Para comprobar si hay un usuario logueado y pasarlo dentro directmente
        // añadir mas adelante
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Compruebo que si al entrar en la APK ya hay un usuario logueado para pasarlo a la pantalla Home Directamente
          if (user != null){
            entrar();
        }

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        // Validando el formato del email
        awesomeValidation.addValidation(this,R.id.textInputUser, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        // Validando el formato de la contraseña
        awesomeValidation.addValidation(this,R.id.textInputPassword,".{6,}",R.string.invalid_password);



    }

    public void clickLogin(View v) {
        // Validaciones
        gif.setVisibility(View.VISIBLE);
        // Gif para esperar 1,3 segundos
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {



                if(awesomeValidation.validate()){

                    String mail = inputUser.getEditText().getText().toString();
                    String pass = inputPassword.getEditText().getText().toString();

                    firebaseAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //
                            if (task.isSuccessful()){
                                entrar();
                            }else{
                                Toast.makeText(Login.this, R.string.erroricreden , Toast.LENGTH_SHORT).show();
                                gif.setVisibility(View.INVISIBLE);
                            }
                            //
                        }

                    });
                }else {
                    gif.setVisibility(View.INVISIBLE);

                }

            }
        }, 1300);

    }
    public void clickRegister(View v){

        Intent it = new Intent(this, Registro.class);
        startActivity(it);
        Toast.makeText(Login.this, R.string.cargandoregisttro , Toast.LENGTH_SHORT).show();
    }

    public void clickIconoInstagram(View v){
        Uri link = Uri.parse(urlInstagram);
        Intent i = new Intent(Intent.ACTION_VIEW,link);
        startActivity(i);
    }
    public void clickIconoTwitter(View v){
        Uri link = Uri.parse(urlTwitter);
        Intent i = new Intent(Intent.ACTION_VIEW,link);
        startActivity(i);
    }
    public void clickIconoYoutube(View v){
        Uri link = Uri.parse(urlYoutube);
        Intent i = new Intent(Intent.ACTION_VIEW,link);
        startActivity(i);
    }
    // Metodo para entrar en el menu padre o menu hijo dependiendo de el resultado de la consulta segun el usuario logueado,
    // si es 1 entra en el menú de hijo
    // si es 2 entra en el menu de padre
    public void entrar() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Intent hijo = new Intent(this,HomeHijo.class);
        Intent padre = new Intent(this,HomePadre.class);
                mDatabase.child("users").child(user.getEmail().toString().split("@")[0]).child("tipo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, R.string.erroricreden, Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getResult().getValue().toString().equals("1")) {
                                Toast.makeText(Login.this, R.string.loginexitoso , Toast.LENGTH_SHORT).show();
                                startActivity(hijo);
                                finish();
                            } else if (task.getResult().getValue().toString().equals("2")) {
                                Toast.makeText(Login.this, R.string.loginexitoso , Toast.LENGTH_SHORT).show();
                                startActivity(padre);
                                finish();
                            }
                        }
                    }
                });
        gif.setVisibility(View.INVISIBLE);
    }

}
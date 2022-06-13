package com.example.unitedfamily;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
// Sergio Moreno Terán
public class Registro extends AppCompatActivity {
    TextInputLayout inputUser;
    TextInputLayout inputPassword1;
    TextInputLayout inputPassword2;

    Button registrar;

    public static int tipo;

    RadioButton bthijo;
    RadioButton btpadre;
    CheckBox terms;

    // Conectar firebase
    DatabaseReference database;
    DatabaseReference ref;

    FirebaseAuth firebaseAuth;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        firebaseAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        // Validando el formato del email
        awesomeValidation.addValidation(this,R.id.textInputUser, Patterns.EMAIL_ADDRESS,R.string.invalid_mail);
        // Validando el formato de la contraseña
        awesomeValidation.addValidation(this,R.id.textInputPassword1,".{6,}",R.string.invalid_password);
        awesomeValidation.addValidation(this,R.id.textInputPassword2,".{6,}",R.string.invalid_password);
        // inicializo los inputTextLayout
        inputUser = (TextInputLayout) findViewById(R.id.textInputUser);
        inputPassword1 =(TextInputLayout) findViewById(R.id.textInputPassword1);
        inputPassword2 =(TextInputLayout) findViewById(R.id.textInputPassword2);
        // Inicializo el boton de registrar
        registrar = (Button) findViewById(R.id.button);
        // Inicializo el checkbox de terminos y condiciones
        terms = (CheckBox) findViewById(R.id.termsAndContitions);
        //Inicializo los radioButtons
        bthijo = (RadioButton) findViewById(R.id.hijos);
        btpadre = (RadioButton) findViewById(R.id.padres);
        //Inicializo el tipo

        // Valido que todos los campos esten correctamente rellenos
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = inputUser.getEditText().getText().toString();
                String pass = inputPassword1.getEditText().getText().toString();
                String pass2 = inputPassword2.getEditText().getText().toString();
                if (terms.isChecked()){
                    if (bthijo.isChecked() || btpadre.isChecked()) {

                        if (awesomeValidation.validate()) {
                            if (pass.equals(pass2)) {
                                firebaseAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Registro.this, R.string.usuariocreado, Toast.LENGTH_SHORT).show();
                                            añadirDatosUsuarioFirebase();
                                            finish();
                                        } else {
                                            Toast.makeText(Registro.this, task.getException().toString().split(":")[1], Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                });
                            } else {
                                Toast.makeText(Registro.this, R.string.nocoincide, Toast.LENGTH_SHORT).show();

                            }

                        }
                    }else {
                        Toast.makeText(Registro.this, R.string.introducetipo , Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(Registro.this, R.string.acepta , Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
    // Metodo que añade a firebase database un objeto con los datos del usuario que se acaba de registrar
    public void añadirDatosUsuarioFirebase(){
        String email = inputUser.getEditText().getText().toString();
        String id = email.split("@")[0].replace(".","_");
        // Inicializamos la bd
        database = FirebaseDatabase.getInstance().getReference();
        //ref = database.getReference("https://united-family-104b7-default-rtdb.firebaseio.com/");

        // Envio datos con el correo UNICO de la persona registrada
        Map<String, Object> datosPersona = new HashMap<>();
        datosPersona.put("email_persona", email);
        datosPersona.put("tipo", tipo);
        datosPersona.put("familiaresasociados","");
        datosPersona.put("puntos","0");
        database.child("users").child(id).setValue(datosPersona);

    }
        // Metodo para comprobar cual boton esta seleccionado
        public void onRadioButtonClicked(View view) {
            boolean checked = ((RadioButton) view).isChecked();

            switch(view.getId()) {
                case R.id.hijos:
                    if (checked)
                    tipo = 1;
                    break;
                case R.id.padres:
                    if (checked)
                    tipo = 2;
                    break;
            }
        }
    }

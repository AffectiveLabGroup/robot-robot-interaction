package com.example.sanbotapp.activities.personalizacionUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.MenuConfiguracionActivity;
import com.example.sanbotapp.activities.personalizacionRobot.PersonalizacionRobotActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class NombreEdadUsuarioActivity extends TopBaseActivity {

    private EditText nombreUsuario;
    private String nombre;

    private EditText edadUsuario;
    private int edad;

    private Button botonContinuar;
    private Button botonAtras;

    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_nombre_edad);

        gestionSharedPreferences = new GestionSharedPreferences(this);

        nombre = gestionSharedPreferences.getStringSharedPreferences("nombreUsuario", null);
        edad = gestionSharedPreferences.getIntSharedPreferences("edadUsuario", 0);


        try {
            nombreUsuario = findViewById(R.id.nombreUsuario);
            edadUsuario = findViewById(R.id.edadUsuario);
            botonContinuar = findViewById(R.id.botonContinuar);
            botonAtras = findViewById(R.id.botonAtras);

            if(nombre!=null){
                nombreUsuario.setText(nombre);
            }
            if(edad!=0){
                String edadString = Integer.toString(edad);
                edadUsuario.setText(edadString);
            }
            botonContinuar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    gestionSharedPreferences.putStringSharedPreferences("nombreUsuario", "nombreUsuario", String.valueOf(nombreUsuario.getText()));
                    String edadTexto = edadUsuario.getText().toString().trim();
                    if (!edadTexto.isEmpty()) {
                        try {
                            int edad = Integer.parseInt(edadTexto);
                            gestionSharedPreferences.putIntSharedPreferences("edadUsuario", "edadUsuario", edad);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Log.d(" edadUsuario", "Edad Usuario vacia");
                        }
                    }
                    // Pasamos a la actividad de personalización
                    Intent personalizacionActivity = new Intent(NombreEdadUsuarioActivity.this, PersonalizacionRobotActivity.class);
                    startActivity(personalizacionActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent menuConfiguracionActivity = new Intent(NombreEdadUsuarioActivity.this, MenuConfiguracionActivity.class);
                    startActivity(menuConfiguracionActivity);
                    finish();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onMainServiceConnected() {

    }

}

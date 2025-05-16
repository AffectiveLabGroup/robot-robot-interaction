package com.example.sanbotapp.activities.personalizacionUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.ModuloConversacionalActivity;
import com.example.sanbotapp.activities.ModuloRobotRobotActivity;
import com.example.sanbotapp.activities.personalizacionRobot.PersonalizacionRobotActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class PersonalityActivity extends TopBaseActivity {
    @Override
    protected void onMainServiceConnected() {

    }
    private Button botonAtras;
    private Button botonModeLola;
    private Button botonModeLolo;
    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality);

        gestionSharedPreferences = new GestionSharedPreferences(this);

        try {
            botonModeLola = findViewById(R.id.botonModeLola);
            botonModeLolo = findViewById(R.id.botonModeLolo);
            botonAtras = findViewById(R.id.botonAtras);

            botonModeLola.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    String text = "soy Lola una muchacha conversadora y entusiasta";
                    // Guardamos en el almacenamiento local la edad del usuario
                    gestionSharedPreferences.putStringSharedPreferences("genre", "genre", "Femenino");
                    gestionSharedPreferences.putStringSharedPreferences("personality", "personality", text);
                    gestionSharedPreferences.putStringSharedPreferences("nameRobot", "nameRobot", "Lola");
                    // Pasamos a la actividad de personalización
                    Intent personalizacionActivity = new Intent(PersonalityActivity.this, ModuloRobotRobotActivity.class);
                    startActivity(personalizacionActivity);
                    finish();
                }
            });

            botonModeLolo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    String text = "soy Lolo un muchacho introvertido y timido";
                    // Guardamos en el almacenamiento local la edad del usuario
                    gestionSharedPreferences.putStringSharedPreferences("genre", "genre", "Masculino");
                    gestionSharedPreferences.putStringSharedPreferences("personality", "personality", text);
                    gestionSharedPreferences.putStringSharedPreferences("nameRobot", "nameRobot", "Lolo");
                    // Pasamos a la actividad de personalización
                    Intent personalizacionActivity = new Intent(PersonalityActivity.this, ModuloRobotRobotActivity.class);
                    startActivity(personalizacionActivity);
                    finish();
                }
            });

            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // Pasamos a la actividad de cuestionario nombre
                    Intent cuestionarioNombreActivity = new Intent(PersonalityActivity.this, NombreUsuarioActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

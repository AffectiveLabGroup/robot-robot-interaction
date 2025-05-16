package com.example.sanbotapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.personalizacionRobot.PersonalizacionRobotActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.NombreUsuarioActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;

public class ExplicacionConversacionActivity extends TopBaseActivity {

    // Componentes de la pantalla de cuestionario edad
    private Button botonContinuar;
    private Button botonAtras;
    private TextView textoExplicacion;

    private SpeechControl speechControl;


    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explicacion_conversa);

        SpeechManager speechManager;
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);

        gestionSharedPreferences = new GestionSharedPreferences(this);


        botonContinuar = findViewById(R.id.botonContinuar);
        botonAtras = findViewById(R.id.botonAtras);
        textoExplicacion = findViewById(R.id.textoExplicacion);

        speechControl = new SpeechControl(speechManager);


        botonContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                // Pasamos a la actividad de personalización
                Intent personalizacionActivity = new Intent(ExplicacionConversacionActivity.this, ModuloConversacionalActivity.class);
                startActivity(personalizacionActivity);
                finish();
            }
        });
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                // Pasamos a la actividad de cuestionario nombre
                Intent cuestionarioNombreActivity = new Intent(ExplicacionConversacionActivity.this, PersonalizacionRobotActivity.class);
                startActivity(cuestionarioNombreActivity);
                finish();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 1000);

    }

    private void update(){
        speechControl.hablar(textoExplicacion.getText().toString());
    }


    @Override
    protected void onMainServiceConnected() {

    }

}

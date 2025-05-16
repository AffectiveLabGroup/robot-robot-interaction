package com.example.sanbotapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sanbotapp.activities.personalizacionUsuario.NombreEdadUsuarioActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.NombreUsuarioActivity;
import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.personalizacionUsuario.PersonalityActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class MenuPruebasActivity extends TopBaseActivity {
    private Button botonPruebaSanbot;
    private Button botonPruebaOpenAI;
    private Button botonInteracRobot;
    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestionSharedPreferences = new GestionSharedPreferences(this);

        try {

            botonPruebaSanbot = findViewById(R.id.botonPruebaSanbot);
            botonPruebaOpenAI = findViewById(R.id.botonPruebaOpenAI);
            botonInteracRobot = findViewById(R.id.botonInteracRobot);

            SharedPreferences sharedVozSeleccionada = this.getSharedPreferences("vozSeleccionada", MODE_PRIVATE);
            SharedPreferences.Editor editorVozSeleccionada = sharedVozSeleccionada.edit();

            SharedPreferences sharedInterpretacionEmocionalActivada = this.getSharedPreferences("interpretacionEmocionalActivada", MODE_PRIVATE);
            SharedPreferences.Editor editorInterpretacionEmocionalActivada = sharedInterpretacionEmocionalActivada.edit();

            botonPruebaSanbot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    gestionSharedPreferences.clearSharedPreferences("vozSeleccionada");
                    gestionSharedPreferences.clearSharedPreferences("nombreUsuario");
                    gestionSharedPreferences.clearSharedPreferences("edadUsuario");
                    gestionSharedPreferences.clearSharedPreferences("generoRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("grupoEdadRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("contextoPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("conversacionAutomatica");
                    gestionSharedPreferences.clearSharedPreferences("modoTeclado");
                    gestionSharedPreferences.clearSharedPreferences("personalizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextualizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("interpretacionEmocionalActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextoVacio");

                    editorVozSeleccionada.putString("vozSeleccionada", "sanbot");
                    //Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebas.this, TutorialModuloConversacional.class);
                    Intent tutorialModuloConversacionalActivity = new Intent(MenuPruebasActivity.this, TutorialModuloConversacionalActivity.class);
                    startActivity(tutorialModuloConversacionalActivity);
                    finish();
                }
            });
            botonPruebaOpenAI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    gestionSharedPreferences.clearSharedPreferences("vozSeleccionada");
                    gestionSharedPreferences.clearSharedPreferences("nombreUsuario");
                    gestionSharedPreferences.clearSharedPreferences("edadUsuario");
                    gestionSharedPreferences.clearSharedPreferences("generoRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("grupoEdadRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("contextoPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("conversacionAutomatica");
                    gestionSharedPreferences.clearSharedPreferences("modoTeclado");
                    gestionSharedPreferences.clearSharedPreferences("personalizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextualizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("interpretacionEmocionalActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextoVacio");
                    editorInterpretacionEmocionalActivada.putBoolean("interpretacionEmocionalActivada", true);
                    editorInterpretacionEmocionalActivada.apply();
                    Intent cuestionarioNombreActivity = new Intent(MenuPruebasActivity.this, MenuConfiguracionAutomaticaActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
            botonInteracRobot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    gestionSharedPreferences.clearSharedPreferences("vozSeleccionada");
                    gestionSharedPreferences.clearSharedPreferences("nombreUsuario");
                    gestionSharedPreferences.clearSharedPreferences("edadUsuario");
                    gestionSharedPreferences.clearSharedPreferences("generoRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("grupoEdadRobotPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("contextoPersonalizacion");
                    gestionSharedPreferences.clearSharedPreferences("conversacionAutomatica");
                    gestionSharedPreferences.clearSharedPreferences("modoTeclado");
                    gestionSharedPreferences.clearSharedPreferences("personalizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextualizacionActivada");
                    gestionSharedPreferences.clearSharedPreferences("interpretacionEmocionalActivada");
                    gestionSharedPreferences.clearSharedPreferences("contextoVacio");
                    gestionSharedPreferences.clearSharedPreferences("genre");
                    gestionSharedPreferences.clearSharedPreferences("personality");
                    gestionSharedPreferences.clearSharedPreferences("nameRobot");

                    editorInterpretacionEmocionalActivada.putBoolean("interpretacionEmocionalActivada", true);
                    editorInterpretacionEmocionalActivada.apply();
                    Intent interacRobotActivity = new Intent(MenuPruebasActivity.this, PersonalityActivity.class);
                    startActivity(interacRobotActivity);
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

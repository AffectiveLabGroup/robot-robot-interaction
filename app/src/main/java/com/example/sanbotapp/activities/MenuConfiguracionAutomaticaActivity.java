package com.example.sanbotapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.personalizacionRobot.PersonalizacionCompletaRobotActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.NombreEdadUsuarioActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.NombreUsuarioActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class MenuConfiguracionAutomaticaActivity extends TopBaseActivity {
    private Button botonAjustesAut;
    private Button botonAjustesManual;
    private Boolean isAutomatic;
    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_automatica);

        gestionSharedPreferences = new GestionSharedPreferences(this);

        isAutomatic = gestionSharedPreferences.getBooleanSharedPreferences("isAutomatic", false);


        try {
            botonAjustesAut = findViewById(R.id.botonAjusteAut);
            botonAjustesManual = findViewById(R.id.botonAjusteManual);

            botonAjustesAut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    setAutomatic(true);
                    gestionSharedPreferences.putBooleanSharedPreferences("isAutomatic", "isAutomatic", true);
                    Intent settingsActivity = new Intent(MenuConfiguracionAutomaticaActivity.this, ModuloConversacionalActivity.class);
                    startActivity(settingsActivity);
                    finish();
                }
            });
            botonAjustesManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    setAutomatic(false);
                    gestionSharedPreferences.putBooleanSharedPreferences("isAutomatic", "isAutomatic", false);
                    Intent contextualizacionActivity = new Intent(MenuConfiguracionAutomaticaActivity.this, NombreEdadUsuarioActivity.class);
                    startActivity(contextualizacionActivity);
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

    public Boolean getAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(Boolean automatic) {
        isAutomatic = automatic;
    }
}

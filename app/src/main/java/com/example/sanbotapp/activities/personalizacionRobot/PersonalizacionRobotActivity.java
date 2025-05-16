package com.example.sanbotapp.activities.personalizacionRobot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sanbotapp.activities.ExplicacionConversacionActivity;
import com.example.sanbotapp.activities.TutorialModuloConversacionalActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.EdadUsuarioActivity;
import com.example.sanbotapp.activities.ModuloConversacionalActivity;
import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.personalizacionUsuario.NombreEdadUsuarioActivity;
import com.example.sanbotapp.gestion.GestionMediaPlayer;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.example.sanbotapp.modulos.moduloOpenAI.ModuloOpenAIAudioSpeech;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.io.IOException;


public class PersonalizacionRobotActivity extends TopBaseActivity {

    private SpeechManager speechManager;
    private static String vozSeleccionada;
    private Button botonAceptar;
    private Button botonAtras;
    private Button botonOmitir;
    private Button botonGrabar;
    private EditText contextoPersonalizacion;
    private Spinner dropdownVoz;
    private int dropdownIndexVoz;
    private int dropdownIndexGeneroRobot;
    private int dropdownIndexGrupoEdadRobot;
    private String generoSeleccionado;
    private String grupoEdadSeleccionado;
    private String respuestaGPT; // Respuesta dada a la consulta realizada por el usuario
    private static byte[] respuestaGPTVoz;

    private SpeechControl speechControl;
    private GestionSharedPreferences gestionSharedPreferences;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ModuloOpenAIAudioSpeech moduloOpenAIAudioSpeech;
    private ModuloOpenAIAudioSpeech moduloOpenAISpeechVoice;
    private GestionMediaPlayer gestionMediaPlayer;



    /*
    public void onResume(){
        super.onResume();
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuConfiguracionActivity = new Intent(PersonalizacionRobotActivity.this, MenuConfiguracionActivity.class);
                startActivity(menuConfiguracionActivity);
                finish();
            }
        });
    }

     */

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_personalizacion_robot);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        speechControl = new SpeechControl(speechManager);

        gestionSharedPreferences = new GestionSharedPreferences(this);
        moduloOpenAIAudioSpeech = new ModuloOpenAIAudioSpeech();
        moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();
        gestionMediaPlayer = new GestionMediaPlayer();


        /*
        // Creo una sección de almacenamiento local donde se guardará la voz seleccionada del robot
        vozSeleccionada = gestionSharedPreferences.getStringSharedPreferences("vozSeleccionada", null);
        SharedPreferences.Editor editorVoz = sharedPrefVoz.edit();

        // Creo una sección de almacenamiento local donde se guardará el género del robot
        SharedPreferences sharedPrefGenero = this.getSharedPreferences("generoRobotPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorGenero = sharedPrefGenero.edit();

        // Creo una sección de almacenamiento local donde se guardará el grupo de edad del robot
        SharedPreferences sharedPrefGrupoEdad = this.getSharedPreferences("grupoEdadRobotPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorGrupoEdad = sharedPrefGrupoEdad.edit();

        // Creo una sección de almacenamiento local donde se guardará el contexto
        SharedPreferences sharedPrefContexto = this.getSharedPreferences("contextoPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorContexto = sharedPrefContexto.edit();

         */

        dropdownIndexVoz = gestionSharedPreferences.getIntSharedPreferences("dropdownIndexVoz", 0);
        dropdownIndexGeneroRobot = gestionSharedPreferences.getIntSharedPreferences("dropdownIndexGeneroRobot", 0);
        dropdownIndexGrupoEdadRobot = gestionSharedPreferences.getIntSharedPreferences("dropdownIndexGrupoEdadRobot", 0);

        dropdownVoz = findViewById(R.id.spinnerVoces);
        contextoPersonalizacion = findViewById(R.id.textoContexto);
        botonAceptar = findViewById(R.id.botonAceptar);
        botonAtras = findViewById(R.id.botonAtras);
        botonOmitir = findViewById(R.id.botonOmitir);
        botonGrabar = findViewById(R.id.botonGrabarRespuesta);

        // Creo el seleccionador de voces
        //String[] items = new String[]{"Sanbot","Alloy", "Echo", "Fable", "Onyx", "Nova", "Shimmer"};
        String[] items = new String[]{"Sanbot", "Alloy", "Ash", "Coral", "Echo", "Fable", "Onyx", "Nova", "Sage" , "Shimmer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PersonalizacionRobotActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownVoz.setAdapter(adapter);

        dropdownVoz.setSelection(dropdownIndexVoz);

        dropdownVoz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                vozSeleccionada = items[position];
                dropdownIndexVoz = position;

                System.out.println("voz seleccionada: " + vozSeleccionada);
                // Pasar a mnúscula la voz seleccionada
                vozSeleccionada = vozSeleccionada.toLowerCase();
                // Reproducir la voz seleccionada
                gestionVoz();
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                // ...
            }

        });


        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("voz", "voy a meter " + vozSeleccionada);
                gestionSharedPreferences.putStringSharedPreferences("vozSeleccionada", "vozSeleccionada", vozSeleccionada);
                gestionSharedPreferences.putIntSharedPreferences("vozSeleccionada", "dropdownIndexVoz", dropdownIndexVoz);

                if (contextoPersonalizacion.getText().equals(null)) {
                    gestionSharedPreferences.putStringSharedPreferences("contextoPersonalizacion", "contextoPersonalizacion", null);
                } else {
                    String contexto = String.valueOf(contextoPersonalizacion.getText());
                    gestionSharedPreferences.putStringSharedPreferences("contextoPersonalizacion", "contextoPersonalizacion", contexto);
                }
                // Pasamos a la actividad de modulo conversacional
                Intent moduloConversacionalActivity = new Intent(PersonalizacionRobotActivity.this, ModuloConversacionalActivity.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });

        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run(){
                        String respuesta = speechControl.modoEscucha();
                        while (respuesta.isEmpty()) {
                        }

                        Log.d("respuesta", "el valor de respuesta es " + respuesta);

                        // Una vez que la variable tiene valor, ejecuta la acción en el hilo principal
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.removeCallbacksAndMessages(null);
                                contextoPersonalizacion.setText(respuesta);
                            }
                        });
                    }
                }).start();
            }
        });
        botonOmitir.setOnClickListener(new View.OnClickListener() {
            // Pasamos a la actividad de modulo conversacional
            @Override
            public void onClick(View v) {
                Intent moduloConversacionalActivity = new Intent(PersonalizacionRobotActivity.this, ExplicacionConversacionActivity.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });
        botonAtras.setOnClickListener(new View.OnClickListener() {
            // Pasamos a la actividad de cuestionario edad
            @Override
            public void onClick(View v) {
                Intent cuestionarioEdadActivity = new Intent(PersonalizacionRobotActivity.this, NombreEdadUsuarioActivity.class);
                startActivity(cuestionarioEdadActivity);
                finish();
            }
        });
    }


    private void gestionVoz(){
        if(vozSeleccionada.equals("sanbot")){
            speechControl.hablar("Has seleccionado la voz " + vozSeleccionada);

        }
        else{

            // Llamo al módulo de OpenAI para obtener la respuesta
            //moduloOpenAIAudioSpeech.reproducirVoz("Has seleccionado la voz " + vozSeleccionada, vozSeleccionada);
            moduloOpenAISpeechVoice.peticionVozOpenAI("Has seleccionado la voz " + vozSeleccionada, vozSeleccionada);
            handler.post(new Runnable() {
                public void run() {
                    handler.removeCallbacksAndMessages(null);
                    respuestaGPTVoz = moduloOpenAISpeechVoice.getGPTVoz();
                    gestionMediaPlayer.reproducirMediaPlayer(respuestaGPTVoz);
                    Log.d("parada mp", "error");
                }
            });
        }
    }

    @Override
    protected void onMainServiceConnected() {

    }
}

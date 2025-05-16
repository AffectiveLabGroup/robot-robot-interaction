package com.example.sanbotapp.activities;

import android.content.Intent;
import android.database.DataSetObserver;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.personalizacionUsuario.PersonalityActivity;
import com.example.sanbotapp.conversacion.ChatArrayAdapter;
import com.example.sanbotapp.conversacion.MensajeChat;
import com.example.sanbotapp.gestion.GestionMediaPlayer;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.example.sanbotapp.modulos.ModuloEmocional;
import com.example.sanbotapp.modulos.ModuloWebSocket;
import com.example.sanbotapp.modulos.moduloOpenAI.ModuloOpenAIAudioSpeech;
import com.example.sanbotapp.modulos.moduloOpenAI.ModuloOpenAIChatCompletions;
import com.example.sanbotapp.robotControl.HandsControl;
import com.example.sanbotapp.robotControl.HardwareControl;
import com.example.sanbotapp.robotControl.HeadControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.SystemControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.SpeakOption;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuloRobotRobotActivity extends TopBaseActivity {
    private Button botonConfiguracion;
    private Button botonNuevaConversacion;
    private Button botonDetener;
    private Button botonTutorial;
    private Button botonRepetir;
    private Button botonHablarTeclado;
    private Button botonEnviarTeclado;
    private ListView dialogo;
    private Button botonHablar;
    private EditText textoConsulta;
    private RelativeLayout barraInferior;

    // Modulos del robot
    private SpeechManager speechManager;
    private HeadMotionManager headMotionManager;
    private HandMotionManager handMotionManager;
    private SystemManager systemManager;
    private HardWareManager hardWareManager;

    // Modulos del programa
    private SpeechControl speechControl;
    private ModuloOpenAIChatCompletions moduloOpenAI;
    private ModuloEmocional moduloEmocional;
    private ModuloOpenAIAudioSpeech moduloOpenAISpeechVoice;
    private GestionMediaPlayer gestionMediaPlayer;
    private HandsControl handsControl;
    private HeadControl headControl;
    private SystemControl systemControl;
    private HardwareControl hardwareControl;
    private GestionSharedPreferences gestionSharedPreferences;

    // Gestión MediaPlayer
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean finHabla = false;
    private boolean finReproduccion = false;
    private boolean forzarParada = false;

    // Variables usadas en el modulo

    private String respuesta;
    private String consultaChatGPT; // Consulta realizada por el usuario
    private String respuestaGPT; // Respuesta dada a la consulta realizada por el usuario
    private static byte[] respuestaGPTVoz;
    private String vozSeleccionada;
    private static SpeakOption speakOption = new SpeakOption();
    private String contexto;
    private String genre;
    private String personality;
    private String nameRobot;
    private String emisor;
    private ChatArrayAdapter chatArrayAdapter;
    private List<MensajeChat> conversacion;

    private ModuloWebSocket socketModule;

    public enum robotsNames {
        Lolo,
        Lola
    };


    // chat setting
    private void chatSetting () {
        dialogo.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        if(chatArrayAdapter.isEmpty()){
            recuperarConversacion();
        }
        actualizarVistaConversacion();
        dialogo.setAdapter(chatArrayAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacksAndMessages(null);
        chatSetting();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Configuración de la aplicación
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Establecer pantalla
        setContentView(R.layout.activity_modulo_conversacional);

        // Instanciación de componentes
        botonConfiguracion = findViewById(R.id.botonConfiguracion);
        botonTutorial = findViewById(R.id.botonTutorial);
        botonDetener = findViewById(R.id.botonDetener);
        botonRepetir = findViewById(R.id.botonRepetir);
        botonNuevaConversacion = findViewById(R.id.botonNuevaConversacion);
        dialogo = findViewById(R.id.burbujaDialogo);
        barraInferior = findViewById(R.id.barra_inferior);
        botonConfiguracion.setText("Atrás");

        disableButton();

        conversacion = new ArrayList<>();
        chatSetting();
        gestionSharedPreferences = new GestionSharedPreferences(this);

        // Inicialización de las unidades del robot
        vozSeleccionada = "sanbot";
        genre = gestionSharedPreferences.getStringSharedPreferences("genre", "Femenino");
        personality = gestionSharedPreferences.getStringSharedPreferences("personality", "");
        nameRobot = gestionSharedPreferences.getStringSharedPreferences("nameRobot", "");

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        headMotionManager = (HeadMotionManager) getUnitManager(FuncConstant.HEADMOTION_MANAGER);
        handMotionManager = (HandMotionManager) getUnitManager(FuncConstant.HANDMOTION_MANAGER);
        hardWareManager = (HardWareManager) getUnitManager(FuncConstant.HARDWARE_MANAGER);
        systemManager = (SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER);

        speakOption.setSpeed(50);
        speakOption.setIntonation(50);
        if(nameRobot.equals(robotsNames.Lolo.name())) {
            speakOption.setSpeed(52);
            speakOption.setIntonation(70);
        }

        speechControl = new SpeechControl(speechManager);
        moduloOpenAI = new ModuloOpenAIChatCompletions();
        headControl = new HeadControl(headMotionManager);
        handsControl = new HandsControl(handMotionManager);
        systemControl = new SystemControl(systemManager);
        hardwareControl = new HardwareControl(hardWareManager);


        moduloEmocional = new ModuloEmocional(handsControl, headControl, hardwareControl, systemControl);
        moduloOpenAISpeechVoice = new ModuloOpenAIAudioSpeech();
        gestionMediaPlayer = new GestionMediaPlayer();

        ///********Conversation Prompt*************
        String prompt = "Primero que todo quiero que mantengas una conversación coloquial, mas juvenil (cero formalidad), como si fuera un amigo de toda la vida, que las respuestas no sean tan largas, máximo 200 caractéres" +
                ", quiero que a veces lo llames por su nombre y en dependencia de mi personalidad actues como tal sin exagerar." +
                " En cada respuesta que te envíe quiero que me envíes al principio de tu respuesta entre corchetes " +
                "un número o varios separados por un guion, entre paréntesis en función de la emoción que transmiten mis respuestas: 1 éxtasis, 2 alegría, 3 serenidad, 4 admiración, 5 confianza, " +
                "6 aceptación, 7 terror, 8 miedo, 9 temor, 10 asombro, 11 sorpresa, 12 distracción, 13 aflicción, 14 tristeza, 15 melancolía, 16 aversión, 17 asco, 18 aburrimiento," +
                "19 furia, 20 ira, 21 enfado, 22 vigilancia, 23 anticipación, 24 interés, 25 optimismo, 26 amor, 27 sumisión, 28 susto, 29 decepción, 30 remordimiento, 31 desprecio, 32 agresividad," +
                "33 esperanza, 34 culpa, 35 curiosidad, 36 desesperación, 37 incredulidad, 38 envidia, 39 cinismo, 40 orgullo, 41 ansiedad, 42 deleite, 43 sentimentalismo, 44 vergüenza, 45 indignación, " +
                "46 pesimismo, 47 morbosidad y 48 dominancia, añadas un slash y un número en función de la emoción que quieres intentar transmitir con tu respuesta " +
                "siguiendo el mismo código numérico. Es decir seguirá el siguiente patrón: [(<número o números de emoción o emociones separados por guiones de mi respuesta>)" +
                "/ (<número o números de emoción o emociones de la respuesta que quieres transmitir>)] + tu respuesta a la conversación." + " Quiero que reconduzcas la conversación en función de la emoción que interpretes y " +
                "que trates de empatizar lo máximo posible con mis respuestas. Aquí te dejo algunos ejemplos: Si te digo algo triste, tú puedes tratar de animarme siendo optimista y mostrarás curiosidad por saber lo que me pasa, " +
                "así que [(14)/(25-35)], si mi respuesta es de enfado, tú tratarás de calmarme y mostrarás curiosidad por saber qué me ocurre, asi que [(21)/(3-35)], si te digo que me gusta alguien " +
                "mi respuesta será de amor y vergüenza, y tú puedes sentir sorpresa, así que [(26-44)/(11)]. Ten en cuenta que mi nombre es "+ nameRobot +
                "  y que soy del genero: " + genre + " y " + personality + " . Actua en base a mi genero y mi personalidad. Las respuestas no deben ser tan rebuscadas ni repetitivas. La conversacion debe estar orientada" +
                "Si te preguntan algo sobre ti inventa algo que sea interesante sobre ti.";

        if(nameRobot.equals(robotsNames.Lola.name()))
            prompt = prompt + "Termina la conversacion a las 3 respuestas tuyas, pero termina educadamente, " +
                    "          por ejemplo diciendo: Bueno... me tengo que ir que me estan esperando, fue un gusto verte de nuevo. Chao. Siempre termina con un emoticono de Saludo o Bye cuando finalices la conversacion. " +
                    "          No debes ser recursivo, cuando te despides es que ya terminastes y paras de hablar, no le digas mas nada aunque el otro te hable.";
        else
            prompt = prompt + "Termina la conversacion cuando el otro conversador te indique que ya quiere terminar de conversar, pero termina educadamente," +
                    "          por ejemplo diciendo: Fue un gusto volverte a ver o Espero verte de nuevo. Chao o Adios. Siempre termina con un emoticono de Saludo o Bye" +
                    "          cuando finalices la conversacion. No debes ser recursivo, cuando te despides es que ya terminastes y paras de hablar, no le digas mas nada aunque el otro te hable.";

        clasificarRoleSystem(prompt);

        socketModule = new ModuloWebSocket(nameRobot, new ModuloWebSocket.MessageListener() {
            @Override
            public void onMessageReceived(String robotId, String message) {
                runOnUiThread(() -> {
                    writeDialogue(message);
                    if(!robotId.equalsIgnoreCase(nameRobot)) return;

                    try {
                        Log.i("registrar Consulta", message + "---" + robotId);
                        if (nameRobot.equalsIgnoreCase(robotsNames.Lolo.name())){
                            if (message.toLowerCase().contains("chao".toLowerCase()) || message.contains("adios".toLowerCase()) ) {
                                return;
                            }
                        }
                        registrarConsulta(message);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        socketModule.connect();

        emisor = nameRobot.toLowerCase();
        String msg = "¡Hola Lolo! Qué alegría verte. ¿Podemos hablar? ";

        if(nameRobot.toLowerCase().equals(ModuloWebSocket.robotsId.lola.toString())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //writeDialogue(msg);
                    speechControl.hablar(msg);
                }
            }, 1000);

            socketModule.sendMsg(ModuloWebSocket.robotsId.lola.toString(), msg);

            if(nameRobot.toLowerCase().equals(ModuloWebSocket.robotsId.lolo.toString())){
                socketModule.requestMsg();
            }
        }

        try {
            // Gestión de la pulsación del botón de silenciar
            botonDetener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Si el robot está hablando (voz Sanbot) se pausa
                    // sino, se reanuda
                    if (speechControl.isRobotHablando()) {
                        try {
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.DETENER);
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            botonConfiguracion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // Pasamos a la actividad de cuestionario nombre
                    Intent cuestionarioNombreActivity = new Intent(ModuloRobotRobotActivity.this, PersonalityActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void clasificarRoleSystem(String prompt) {
        moduloOpenAI.anadirRoleSystem(prompt);
    }


    private void registrarConsulta(String texto) throws IOException, InterruptedException {
        if(!texto.isEmpty()) {
            respuesta = texto;
            try {
               // writeDialogue(respuesta);
                consultaChatCompletions(respuesta);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeDialogue(String consulta) {
        boolean pos = nameRobot.equalsIgnoreCase(robotsNames.Lola.name());
        chatArrayAdapter.add(new MensajeChat(pos, consulta));
        conversacion.add(new MensajeChat(pos, consulta));
    }

    private void consultaChatCompletions(String pregunta) throws IOException, InterruptedException {
        new Thread(new Runnable() {
            public void run(){
                moduloOpenAI.consultaOpenAI(pregunta);
                respuestaGPT = moduloOpenAI.getRespuestaGPT();
                try {
                        moduloEmocional.gestionEmocional(respuestaGPT);
                        respuestaGPT = moduloEmocional.separarRespuestaGPT(respuestaGPT);
                } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                }

                handler.post(new Runnable() {
                    public void run() {
                        try {
                            handler.removeCallbacksAndMessages(null);
                            gestionVoz(vozSeleccionada, AccionReproduccionVoz.HABLAR);
                            //writeDialogue(consultaChatGPT);
                            speechControl.finHablaRunnable(() -> {
                                Log.i("Probando espera", respuestaGPT);
                                socketModule.sendMsg(emisor, respuestaGPT);
                            });

                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();
    }


    private void disableButton(){
        botonTutorial.setVisibility(View.INVISIBLE);
        botonRepetir.setVisibility(View.INVISIBLE);
        botonNuevaConversacion.setVisibility(View.INVISIBLE);
        barraInferior.setVisibility(View.INVISIBLE);
    }
    private void recuperarConversacion(){
        for (MensajeChat cm : conversacion) {
            Log.d("chatmessage", cm.toString());
            chatArrayAdapter.add(cm);
        }
        Log.d("chatarray", String.valueOf(chatArrayAdapter.getCount() - 1));
        dialogo.setSelection(chatArrayAdapter.getCount() - 1);
    }

    private void actualizarVistaConversacion(){
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("ca", String.valueOf(chatArrayAdapter.getCount() - 1));
                dialogo.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }
    private void gestionVoz(String voz, AccionReproduccionVoz accionVoz) throws IOException, InterruptedException {
        if(voz.equals("sanbot")){
            switch (accionVoz) {
                case HABLAR:
                    speechControl.hablar(respuestaGPT);
                    break;
                case DETENER:
                    Log.d("Le estoy dando", "robot hablando, intentando callar");
                    // Se silencia
                    forzarParada=true;
                    speechControl.pararHabla();
                    break;
            }
        }
    }

    private void gestionarFinHablaSanbot(){
        Log.d("hola", "entrando....");
        new Thread(new Runnable() {
            public void run(){
                Log.d("hola", "entrando mas....");
                finHabla = false;
                finHabla = speechControl.heAcabado2();
                while (!finHabla) {
                    Log.d("waiting", "waiting..." + finHabla);
                }
                Log.d("hola", "finHabla es " + finHabla);
                // Una vez que la variable tiene valor, ejecuta la acción en el hilo principal
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handler.removeCallbacksAndMessages(null);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }).start();
    }

    private enum AccionReproduccionVoz {
        HABLAR,
        DETENER,
        REPETIR,
    }

        @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketModule.disconnect(); // importante liberar recursos
    }
}

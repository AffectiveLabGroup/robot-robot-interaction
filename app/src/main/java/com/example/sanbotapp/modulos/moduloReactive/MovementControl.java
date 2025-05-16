package com.example.sanbotapp.modulos.moduloReactive;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.sanbotapp.robotControl.SpeechControl;
import com.example.sanbotapp.robotControl.WheelControl;
import com.qihancloud.opensdk.function.unit.ModularMotionManager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MovementControl {

    static {
        System.loadLibrary("MapHelper");
    }

    private SpeechControl speechControl;
    private WheelControl wheelControl;
    private ModularMotionManager modularMotionManager;
//    private MapClient mMapClient;
//    private MapClient.Listener mapListener;
    private Context context;
    Runnable runnable;
    Handler handler = new Handler();


    public MovementControl(ModularMotionManager modularMotionManager, Context context, WheelControl wheelControl){
        this.modularMotionManager = modularMotionManager;
        this.context = context;
        this.wheelControl = wheelControl;
    }


    /**
     * Activar movimiento aleoatorio / comienza a caminar
     */
    public void activarMovimientoAleatorio(){
        modularMotionManager.switchWander(true);
    }

    /**
     * Desactivar movimiento aleoatorio / comienza a caminar
     */
    public void desactivarMovimientoAleatorio(){
        modularMotionManager.switchWander(false);
    }

    /**
     * Activar movimiento aleatorio con wheels
     */
    public void activarMovimientoAleatorioWheels(){
        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.DERECHA, 90);
        wheelControl.avanzar(200);
        wheelControl.controlBasicoRuedas(WheelControl.AccionesRuedas.IZQUIERDA, 180);
        wheelControl.avanzar(200);

    }

    /**
     * Activa el seguimiento del robot
     */
    public void activarSeguimiento(){
        modularMotionManager.switchFollow(true);

        System.out.println("FOLLOW STATUS" + modularMotionManager.getFollowStatus().getDescription());
        System.out.println("FOLLOW result" + modularMotionManager.getFollowStatus().getResult());
        System.out.println("FOLLOW errr code" + modularMotionManager.getFollowStatus().getErrorCode());

    }

    public void desactivarSeguimiento(){
        modularMotionManager.switchFollow(false);
    }


    /**
     *  Conectar un mapa y navegacion
     */
//    public void conectarMapa() {
//        Log.d("MovementControl", "🔄 Intentando conectar con el servicio de mapas...");
//
//        // Llamamos a MapClient.Connect() con un listener
//        MapClient.Connect(context.getApplicationContext().getPackageName(), new MapClient.Listener() {
//            @Override
//            public void initialize(MapClient manager) {
//                mMapClient = manager;
//                Log.d("MovementControl", "✅ MapClient inicializado correctamente.");
//            }
//
//            @Override
//            public void connState(int state, Object data) {
//                if (state == Msg.Result.FREE) {
//                    Log.d("MovementControl", "📌 Estado: Libre (no conectado aún).");
//                } else if (state == Msg.Result.CONNECTING) {
//                    Log.d("MovementControl", "🔄 Conectando... Intento #" + data);
//                } else if (state == Msg.Result.CONNECTED) {
//                    Log.d("MovementControl", "✅ Conectado correctamente.");
//                } else {
//                    Log.e("MovementControl", "❌ Error desconocido en la conexión: " + state);
//                }
//            }
//
//            @Override
//            public void mapState(int i, int i1, int i2, int i3) {
//
//            }
//
//            @Override
//            public void position(double v, double v1, double v2) {
//
//            }
//
//            @Override
//            public void mapChanged() {
//
//            }
//
//            @Override
//            public void moveError(int i, int i1) {
//
//            }
//        });
//    }
//
//    /**
//     * Verificar si el mapa se ha importado correctamente
//     */
//    public void verificarMapa() {
//        if (mMapClient == null) {
//            Log.e("MovementControl", "❌ Error: MapClient aún no está inicializado.");
//            return;
//        }
//
//        List<String> maps = mMapClient.getAllMaps();
//        if (maps != null && !maps.isEmpty()) {
//            for (String mapName : maps) {
//                Log.d("MovementControl", "🗺️ Mapa disponible: " + mapName);
//            }
//        } else {
//            Log.e("MovementControl", "❌ No hay mapas importados.");
//        }
//    }

    private void copyFileFromAssets(String filename, String destinationPath) {
        try (InputStream in = context.getAssets().open(filename);
             OutputStream out = new FileOutputStream(destinationPath)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            Log.d("MapClient", "Archivo copiado: " + filename);
        } catch (IOException e) {
            Log.e("MapClient", "Error copiando archivo: " + filename, e);
        }
    }

//    public void buscarMapas(){
//        List<String> maps = MapClient.getAllMaps();
//        if (maps != null && !maps.isEmpty()) {
//            for (String mapName : maps) {
//                Log.d("MapClient", "Mapa disponible: " + mapName);
//                aplicarMapa(mapName);
//                habilitarNavegacion();
//                calibrarPosicionInicial();
//                //moverRobotPuntosEspecificos();
//                break;
//            }
//        } else {
//            Log.e("MapClient", "No hay mapas importados o falta permiso de lectura.");
//            importarMapa();
//        }
//
//    }
//
//
//    public void importarMapa(){
//        String basePath = context.getExternalFilesDir(null) + "/mymap/";
//        new File(basePath).mkdirs(); // Crear carpeta si no existe
//
//        copyFileFromAssets("Mymap.pgm", basePath + "Mymap.pgm");
//        copyFileFromAssets("Mymap.yaml", basePath + "Mymap.yaml");
//        copyFileFromAssets("Mymap.pose", basePath + "Mymap.pose");
//
//        int result = MapClient.insertMap(basePath);
//        if (result == Msg.Result.RESULT_OK) {
//            Log.d("MapClient", "Mapa importado con éxito.");
//        } else {
//            Log.e("MapClient", "Error importando mapa: " + result);
//        }
//
//    }
//
//    public void aplicarMapa2(String nombremapa){
//        int result = mMapClient.applyMap(nombremapa); // Reemplaza con el nombre real del mapa
//        if (result == Msg.Result.RESULT_OK) {
//            Log.d("MapClient", "Mapa aplicado correctamente");
//        } else {
//            Log.e("MapClient", "Error al aplicar mapa: " + result);
//        }
//    }
//
//    public void aplicarMapa(String nombreMapa) {
//        if (mMapClient == null) {
//            Log.e("MovementControl", "❌ Error: MapClient aún no está conectado.");
//            return;
//        }
//
//        int result = mMapClient.applyMap(nombreMapa);
//        if (result == Msg.Result.RESULT_OK) {
//            Log.d("MovementControl", "✅ Mapa '" + nombreMapa + "' aplicado correctamente.");
//        } else {
//            Log.e("MovementControl", "❌ Error al aplicar el mapa: " + result);
//        }
//    }
//
//    public void habilitarNavegacion(){
//        int navResult = mMapClient.openNavigation();
//        if (navResult == Msg.Result.RESULT_OK) {
//            Log.d("MapClient", "Navegación activada");
//        } else {
//            Log.e("MapClient", "Error al abrir navegación: " + navResult);
//        }
//
//    }
//
//    public void calibrarPosicionInicial(){
//        int posResult = mMapClient.adjustPosition(0.0, 0.0, 0.0); // Coordenadas iniciales (x, y, ángulo)
//        if (posResult == Msg.Result.RESULT_OK) {
//            Log.d("MapClient", "Posición calibrada correctamente");
//        } else {
//            Log.e("MapClient", "Error al calibrar posición: " + posResult);
//        }
//
//    }
//
//    public void moverRobotPuntosEspecificos(){
//        int moveResult = mMapClient.navigationTo(2.0, 3.0, Math.toRadians(90)); // Mueve a (2,3) con 90°
//        if (moveResult == Msg.Result.RESULT_OK) {
//            Log.d("MapClient", "Navegando al punto (2,3)");
//        } else {
//            Log.e("MapClient", "Error en la navegación: " + moveResult);
//        }
//
//    }





}





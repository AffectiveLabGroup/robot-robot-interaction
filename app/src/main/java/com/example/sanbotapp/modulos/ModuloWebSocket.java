package com.example.sanbotapp.modulos;
import android.util.Log;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ModuloWebSocket {
    private String message;
    private String robotId;
    private final String URISocket = "http://robot-server-flask.onrender.com";
    private Socket mSocket;
    private MessageListener listener;

    public enum robotsId {
        lolo,
        lola
    };

    // Interface para comunicación con la actividad externa
    public interface MessageListener {
        void onMessageReceived(String robotId, String message);
    }

    public ModuloWebSocket(String robotId, MessageListener listener) {
        this.robotId = robotId;
        this.listener = listener;
        initSocket();
    }

    private void initSocket() {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{"websocket"};
            mSocket = IO.socket(URISocket, opts);

            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("Socket", "Conectado al servidor");
                }
            });

            mSocket.on("receive_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    robotId = data.optString("robot");
                    String messageReceived = data.optString("message");
                    Log.i("Socket", "Mensaje de " + robotId + ": " + messageReceived);
                    Log.i("Socket", "Mensaje recibido para " + robotId + ": " + messageReceived);
                    message = messageReceived;

                    if (listener != null) {
                        listener.onMessageReceived(robotId, messageReceived);
                    }
                }
            });

            mSocket.on(Socket.EVENT_DISCONNECT, args -> Log.i("Socket", "Desconectado del servidor"));

        } catch (URISyntaxException e) {
            Log.e("Socket", "Error al crear el socket", e);
        }
    }

    public void connect() {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
        }
    }

    public void disconnect() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.disconnect();
        }
    }

    public void sendMsg(String emisor, String messageSended) {
        JSONObject enviar = new JSONObject();
        try {
            enviar.put("robot", emisor);
            enviar.put("message", messageSended);
            mSocket.emit("send_message", enviar);
            Log.i("Socket", "Mensaje enviado");
        } catch (Exception e) {
            Log.e("Socket", "Error al enviar mensaje", e);
        }
    }
    public void requestMsg() {
        JSONObject pedir = new JSONObject();
        try {
            pedir.put("robot", robotId);
            mSocket.emit("request_message", pedir);
            Log.i("request_message", pedir.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public String getRobotId() {
        return robotId;
    }
}

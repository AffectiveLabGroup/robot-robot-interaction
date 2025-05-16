package com.example.sanbotapp.modulos.moduloReactive;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class NoiseDetector {
    private static final int SAMPLE_RATE = 8000;
    private AudioRecord audioRecord;
    private Handler mHandler;
    private OnNoiseDetectedListener listener;
    private boolean isListening = false;
    private double mEMA = 0.0;  // Almacenamiento de nivel de ruido

    public interface OnNoiseDetectedListener {
        void onNoiseDetected(double decibels);
    }

    public NoiseDetector(OnNoiseDetectedListener listener) {
        this.listener = listener;
        this.mHandler = new Handler();
    }

    /**
     * Inicia la detección de ruido en segundo plano
     */
    public void startListening() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        audioRecord.startRecording();
        Log.d("NoiseDetector", "✅ Micrófono iniciado con AudioRecord.");

        new Thread(() -> {
            short[] buffer = new short[bufferSize];
            while (true) {
                int readSize = audioRecord.read(buffer, 0, buffer.length);
                if (readSize > 0) {
                    double amplitude = calculateAmplitude(buffer, readSize);
                    double decibels = 20 * Math.log10(amplitude);
                    Log.d("NoiseDetector", "🔊 Volumen detectado: " + decibels + " dB");
                }
            }
        }).start();
    }


    /**
     *  Detiene la detección de ruido
     */
    public void stopListening() {
        if (!isListening) return;


    }

    private double calculateAmplitude(short[] buffer, int readSize) {
        double sum = 0;
        for (int i = 0; i < readSize; i++) {
            sum += Math.abs(buffer[i]);
        }
        return sum / readSize;
    }
}


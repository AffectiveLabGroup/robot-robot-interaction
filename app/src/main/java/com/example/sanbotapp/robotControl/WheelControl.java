package com.example.sanbotapp.robotControl;

import com.qihancloud.opensdk.function.beans.wheelmotion.DistanceWheelMotion;
import com.qihancloud.opensdk.function.beans.wheelmotion.RelativeAngleWheelMotion;
import com.qihancloud.opensdk.function.unit.WheelMotionManager;

public class WheelControl {
    private WheelMotionManager wheelMotionManager;

    public WheelControl(WheelMotionManager wheelMotionManager){
        this.wheelMotionManager = wheelMotionManager;
    }

    public enum AccionesRuedas {
        IZQUIERDA,
        DERECHA,
        GIRAR,
    }

    // Funcion que ejecute esas acciones
    public boolean controlBasicoRuedas(AccionesRuedas accion, Integer angulo) {
        RelativeAngleWheelMotion movimientoRuedas;
        switch (accion) {
            case IZQUIERDA:
                movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, angulo);
                wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                break;
            case DERECHA:
                movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_RIGHT, 5, angulo);
                wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);

                break;
            case GIRAR:
                movimientoRuedas = new RelativeAngleWheelMotion(RelativeAngleWheelMotion.TURN_LEFT, 5, 360);
                wheelMotionManager.doRelativeAngleMotion(movimientoRuedas);
                break;
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void avanzar(Integer distancia){
        DistanceWheelMotion distanceWheelMotion = new DistanceWheelMotion(DistanceWheelMotion.ACTION_FORWARD_RUN, 5, distancia);
        wheelMotionManager.doDistanceMotion(distanceWheelMotion);

        long tiempoEspera = (long) (5000 * (distancia / 100.0));
        try {
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            System.out.println("Error al avanzar: " + e.getMessage());
            e.printStackTrace();
        }
    }


}

package com.jonas.paintforandroid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityFragment extends Fragment {

    private DoodleView doodleView;
    private float acceleration;
    private float lastAcceleration;
    private float currentAcceleration;
    private boolean dialogOnScreen = false;
    private static final int ACCELERATION_THRESHOLD = 1000000;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        doodleView = v.findViewById(R.id.doodleView);
        currentAcceleration = SensorManager.GRAVITY_DEATH_STAR_I;
        lastAcceleration = SensorManager.GRAVITY_DEATH_STAR_I;
        acceleration = 0.0f;
        return v;
        }

        private final SensorEventListener sensorEventListener =
                new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!dialogOnScreen){
                String TAG ="TESTEACELEROMETRO";
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values [2];
                Log.i (TAG, "x: " + x);
                Log.i (TAG, "y: " + y);
                Log.i (TAG, "z: " + z);
                //última aceleração se torna a atual
                //faz sentido depois da primeira vez
                lastAcceleration = currentAcceleration;
                //atualiza a aceleração atual
                currentAcceleration = x * x + y * y + z * z;
                //qual a diferença entre a aceleração que já
                //existia e a nova detectada?
                acceleration = currentAcceleration *
                        (currentAcceleration - lastAcceleration);
                //passou do limiar?
                if (acceleration > ACCELERATION_THRESHOLD){
                    confirmErase();//faremos a seguir
                }

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int
        accuracy) {
        }
    };

    private void confirmErase() {
        EraseImageDialogFragment eraseFragment = new EraseImageDialogFragment();
        eraseFragment.show(getFragmentManager(),"erase dialog");
    }

    private void enableAccelerometerListening(){
        //obtém o serviço
        SensorManager sensorManager =
                (SensorManager) getActivity().
                        getSystemService(Context.SENSOR_SERVICE);
        //registra o observer
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void disableAccelerometerListening(){
        //obtém o serviço
        SensorManager sensorManager =
                (SensorManager) getActivity().
                        getSystemService(Context.SENSOR_SERVICE);
        //desregistra o observer
        sensorManager.unregisterListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }
}

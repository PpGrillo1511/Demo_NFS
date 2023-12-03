package com.example.demonfs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static Handler handler=new Handler();
    private Client client;
    public static Communication communication;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private ImageView ivLeft;
    private ImageView ivRight;
    private ImageView ivUp;
    private ImageView ivDown;
    private ImageView ivStop;
    private BluetoothAdapter bluetoothAdapter;
    public static ListView lvDevices;
    private final int REQUEST_ENABLE_BT = 1;
    private Intent enableIntent;
    private ArrayList<Device> devices=new ArrayList<>();
    private ListAdapter listAdapter;
    private byte direction;
    private String mensaje;
    private byte previousDirection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvDevices=findViewById(R.id.lv_devices);
        ivLeft=findViewById(R.id.iv_left);
        ivRight=findViewById(R.id.iv_right);
        ivUp=findViewById(R.id.iv_up);
        ivDown=findViewById(R.id.iv_down);
        ivStop = findViewById(R.id.iv_stop);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscopeSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Device device=(Device) lvDevices.getItemAtPosition(position);
               client=new Client(bluetoothAdapter.getRemoteDevice(device.getAddress()),MainActivity.this,bluetoothAdapter);
               client.start();
               sensorManager.registerListener(gyroscopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        gyroscopeEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                long x, y, z;
                x = Math.round(Math.toDegrees(sensorEvent.values[0]));
                y = Math.round(Math.toDegrees(sensorEvent.values[1]));
                z = Math.round(Math.toDegrees(sensorEvent.values[2]));
                if(x<-200){
                   ivUp.setVisibility(ImageView.VISIBLE);
                   ivDown.setVisibility(ImageView.INVISIBLE);
                   ivLeft.setVisibility(ImageView.INVISIBLE);
                   ivRight.setVisibility(ImageView.INVISIBLE);
                   mensaje="1";
                   direction=1;
                }else if (x>240){
                    ivUp.setVisibility(ImageView.INVISIBLE);
                    ivDown.setVisibility(ImageView.VISIBLE);
                    ivLeft.setVisibility(ImageView.INVISIBLE);
                    ivRight.setVisibility(ImageView.INVISIBLE);
                    mensaje="2";
                    direction=2;
                }else if(y>220){
                    ivUp.setVisibility(ImageView.INVISIBLE);
                    ivDown.setVisibility(ImageView.INVISIBLE);
                    ivLeft.setVisibility(ImageView.INVISIBLE);
                    ivRight.setVisibility(ImageView.VISIBLE);
                    mensaje="3";
                    direction=3;
                }else if(y<-220){
                    ivUp.setVisibility(ImageView.INVISIBLE);
                    ivDown.setVisibility(ImageView.INVISIBLE);
                    ivLeft.setVisibility(ImageView.VISIBLE);
                    ivRight.setVisibility(ImageView.INVISIBLE);
                    mensaje="4";
                    direction=4;
                }else {
                    ivUp.setVisibility(ImageView.INVISIBLE);
                    ivDown.setVisibility(ImageView.INVISIBLE);
                    ivLeft.setVisibility(ImageView.INVISIBLE);
                    ivRight.setVisibility(ImageView.INVISIBLE);
                    mensaje="0";
                    direction=0;
                }
                if (direction!=previousDirection){
                    communication.write(mensaje.getBytes());
                    mensaje="";
                    previousDirection=direction;
                }
                if (x < -200 || x > 240 || y > 220 || y < -220) {
                    ivStop.setVisibility(ImageView.INVISIBLE); // Si se mueve, ocultar el punto de parada
                } else {
                    ivStop.setVisibility(ImageView.VISIBLE); // El dispositivo está quieto, mostrar el punto de parada
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "No tiene bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled() == false) {
               enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                }else{
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
            }else{
                getDevices();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_OK)
            getDevices();
    }

    protected void getDevices(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> pairedDevices=bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0) {
            for (BluetoothDevice device : pairedDevices) {
                Device dev=new Device();
                dev.setImage(R.drawable.bluetooth);
                dev.setAddress(device.getAddress());
                dev.setName(device.getName());
                devices.add(dev);
            }
            listAdapter=new ListAdapter(this,devices);
          //  adapter=new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,devices);
            lvDevices.setAdapter(listAdapter);
        }else
            Toast.makeText(this, "No hay dispositivos emparejados", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    if(bluetoothAdapter.isEnabled()==false)
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (communication!=null)
            communication.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        lvDevices.setVisibility(View.VISIBLE);
    }
}
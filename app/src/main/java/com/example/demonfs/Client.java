package com.example.demonfs;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Handler;

public class Client extends Thread {
    private Context context;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String TAG = "NFS_UTXJ";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;

    public Client(BluetoothDevice device, Context context, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.device = device;
        this.bluetoothAdapter = bluetoothAdapter;
        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Si entró");
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Falló la creación del socket", e);
            }
        }

    }

    @Override
    public void run() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            try {
                socket.connect();
                MainActivity.communication=new Communication(socket);
                MainActivity.communication.start();
                MainActivity.lvDevices.setVisibility(View.INVISIBLE);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Log.e(TAG, "No se puede cerrar el client socket", ex);
                }
            }
        }

    }
}

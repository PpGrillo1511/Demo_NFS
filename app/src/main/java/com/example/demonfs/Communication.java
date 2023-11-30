package com.example.demonfs;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Communication extends Thread {
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] messageBuffer;

    public Communication(BluetoothSocket socket) {
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(Client.TAG, "Ocurrio un error al crear los streams",e);
        }
    }

    @Override
    public void run() {
        messageBuffer=new byte[1024];
        int numBytes;
        while(true){
            try {
                numBytes=inputStream.read(messageBuffer);
                //0.- MESSAGE_READ
                Message readMessage=MainActivity.handler.obtainMessage(0,numBytes,-1);
                readMessage.sendToTarget();
            } catch (IOException e) {
                Log.e(Client.TAG, "Stream desconectado",e);
                break;
            }
        }
    }
    public void write(byte[] bytes){
        try {
            outputStream.write(bytes);
            //1.- MESSAGE_WRITE
            Message writeMessage=MainActivity.handler.obtainMessage(1,-1,-1,messageBuffer);
            writeMessage.sendToTarget();
        } catch (IOException e) {
            Log.e(Client.TAG, "Error al enviar los datos",e);
            //2.- MESSAGE_TOAST
            Message writeErrorMessage=MainActivity.handler.obtainMessage(2);
            Bundle bundle=new Bundle();
            bundle.putString("toast","No se puede enviar datos a el HC-05");
            writeErrorMessage.setData(bundle);
            MainActivity.handler.sendMessage(writeErrorMessage);
        }
    }
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(Client.TAG, "Could not close the connect socket", e);
        }
    }
}

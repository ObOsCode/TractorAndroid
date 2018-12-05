package com.example.tractor.services.tractor.connections;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.tractor.services.tractor.TractorDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by user on 07.07.17.
 */

public class TractorBluetoothConnection extends TractorConnection
{

    private OutputStream _outStream;

    private InputStream _inputStream;

//    BufferedReader _receivedDataReader;

    private BluetoothSocket _socket;

    private BluetoothAdapter _adapter;



    public  TractorBluetoothConnection()
    {

        _adapter = BluetoothAdapter.getDefaultAdapter();

        if(_adapter==null)
        {
            _isSuppored = false;

            return;
        }

        if(_adapter.isEnabled())
        {
            addBoundedDevices();
        }
    }


    public void addBoundedDevices()
    {
        Set<BluetoothDevice> pairedDevices = _adapter.getBondedDevices();

        if(pairedDevices.size()>0)
        {
            for(BluetoothDevice device: pairedDevices)
            {

                addDevice(new TractorDevice<BluetoothDevice>(device.getAddress(), device.getName(), device));
            }
        }
    }



    @Override
    public void connectToDevice(TractorDevice device) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException
    {
        //Инициируем соединение с устройством

        BluetoothDevice btDevice = (BluetoothDevice)device.getDevice();

        Method m = btDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});

        _socket = (BluetoothSocket) m.invoke(btDevice, 1);

        _socket.connect();

        _outStream = _socket.getOutputStream();

        _inputStream = _socket.getInputStream();

        _connectedDevice = device;

        _isConnecting = true;
    }


    public byte[] read() throws IOException
    {
        byte[] buffer = new byte[_inputStream.available()];
        _inputStream.read(buffer, 0, buffer.length);
        return buffer;
    }


    public void close() throws IOException
    {
        if(_isConnecting)
        {
            _socket.close();
            _isConnecting = false;
            _connectedDevice = null;
        }
    }


    @Override
    public void sendData(byte[] data) throws IOException
    {
        _outStream.write(data);
    }


    public void startDiscovery()
    {
        _adapter.startDiscovery();
    }


    public void cancelDiscovery()
    {
        _adapter.cancelDiscovery();
    }


    public boolean isDiscovering()
    {
        return _adapter.isDiscovering();
    }


    public boolean isEnabled()
    {
        return _adapter.isEnabled();
    }

}

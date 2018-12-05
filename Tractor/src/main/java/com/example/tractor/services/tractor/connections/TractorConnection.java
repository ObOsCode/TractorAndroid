package com.example.tractor.services.tractor.connections;

import android.util.Log;

import com.example.tractor.services.tractor.TractorDevice;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import ru.roboticsUMK.commandProtocol.ProtocolCommand;


/**
 * Created by user on 11.07.17.
 */

public abstract class TractorConnection
{

    public static final String CONNECT_PASSWORD = "iddqdidkfa";

    protected boolean _isConnecting = false;

    protected boolean _isSuppored = true;

    protected ArrayList<TractorDevice> _deviceList = new ArrayList<TractorDevice>();

    protected TractorDevice _connectedDevice;

    private ProtocolCommand _lastReceivedCommand;


    public TractorConnection()
    {

    }


    public boolean isSuppored()
    {
        return _isSuppored;
    }


    public boolean isConnecting()
    {
        return _isConnecting;
    };


    public TractorDevice getConnectedDevice()
    {
        return _connectedDevice;
    }


    public ArrayList<TractorDevice> getDeviceList()
    {
        return _deviceList;
    }


    public void addDevice(TractorDevice newDevice)
    {

        if(_deviceList.contains(newDevice))
        {
            return;
        }

        String newDeviceId = newDevice.getId();

        for(TractorDevice device: _deviceList)
        {
            if(device.getId().equals(newDeviceId))
            {
                return;
            }
        }

        _deviceList.add(newDevice);
    }


    public ProtocolCommand getLastReceivedCommand()
    {
        return _lastReceivedCommand;
    }
//
//
//    public void setLastReceivedCommand(ProtocolCommand command)
//    {
//        this._lastReceivedCommand = command;
//    }


    abstract public byte[] read() throws IOException;


    abstract public boolean isDiscovering();


    abstract public boolean isEnabled();


    abstract public void startDiscovery();


    abstract public void cancelDiscovery();


    abstract public void connectToDevice(TractorDevice device) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    abstract public void close() throws IOException;


    abstract public void sendData(byte[] data) throws IOException;
}

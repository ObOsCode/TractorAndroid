package com.example.tractor.services.tractor.connections;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.tractor.services.tractor.TractorDevice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import ru.roboticsUMK.commandProtocol.connections.SocketProtocolConnection;

/**
 * Created by user on 11.07.17.
 */

public class TractorWiFiConnection extends TractorConnection
{

    private WifiManager _adapter;

//    private final String _WI_FI_PASSWORD = "50914647";
    private final String _WI_FI_PASSWORD = "Sa8quahni!";

    private Socket _socket;
    private DataInputStream _inputStream;
    private DataOutputStream _outputStream;

    private Context _context;


    public TractorWiFiConnection(Context context)
    {
        super();

        _context = context;

        _adapter = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        if(!_adapter.isWifiEnabled())
        {
            _adapter.setWifiEnabled(true);
        }else
        {
            addSearchDevices();

            _adapter.disconnect();
        }
    }


    public void addSearchDevices()
    {
        List<ScanResult> wifiScanList = _adapter.getScanResults();

        for(int i = 0; i < wifiScanList.size(); i++)
        {
            ScanResult result = wifiScanList.get(i);

            addDevice(new TractorDevice(result.BSSID, result.SSID));
        }
    }


    public void setEnabled(boolean value)
    {
        _adapter.setWifiEnabled(value);
    }


    @Override
    public boolean isEnabled()
    {
        return _adapter.isWifiEnabled();
    }


    @Override
    public void startDiscovery()
    {
        _adapter.startScan();
    }


    @Override
    public void cancelDiscovery()
    {

    }


    @Override
    public boolean isDiscovering()
    {
        return false;
    }


    public void createSocket() throws IOException
    {


        String host = "192.168.40.206";
        int port = 9999;

        Log.d("myTag", "Try connect to socket. Host: " + host + " Port: " + port);

//            _socket = new Socket(SocketProtocolConnection.HOST, SocketProtocolConnection.PORT);
        _socket = new Socket(host, port);

        _inputStream = new DataInputStream(_socket.getInputStream());

        _outputStream = new DataOutputStream(_socket.getOutputStream());

        _isConnecting = true;

        Log.d("myTag", "Connected to socket");
    }


    @Override
    public void connectToDevice(TractorDevice device) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        Log.d("myTag", "connect to - " + device.getName());

        WifiConfiguration config = new WifiConfiguration();
        config.SSID ="\""+ device.getName() +"\"";
        config.preSharedKey = "\""+ _WI_FI_PASSWORD +"\"";

        int netID = _adapter.addNetwork(config);
        _adapter.disconnect();
        _adapter.enableNetwork(netID, true);
        _adapter.reconnect();

        Log.d("myTag", "netID - " + netID);

        _connectedDevice = device;
    }


    @Override
    public void close() throws IOException
    {
        if(_isConnecting)
        {
            _socket.close();
            _adapter.disconnect();
            _isConnecting = false;
            _connectedDevice = null;
        }
    }


    @Override
    public void sendData(byte[] data) throws IOException
    {
        _outputStream.write(data);
    }


    @Override
    public byte[] read() throws IOException
    {
        byte[] buffer = new byte[_inputStream.available()];
        _inputStream.read(buffer, 0, buffer.length);
        return buffer;
    }

}

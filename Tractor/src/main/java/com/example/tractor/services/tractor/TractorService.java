package com.example.tractor.services.tractor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tractor.services.tractor.connections.TractorBluetoothConnection;
import com.example.tractor.services.tractor.connections.TractorConnection;
import com.example.tractor.services.tractor.connections.TractorWiFiConnection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import ru.roboticsUMK.commandProtocol.ProtocolCommand;
import ru.roboticsUMK.commandProtocol.commands.ControlDeviceCommand;
import ru.roboticsUMK.commandProtocol.parser.CommandProtocolParser;
import ru.roboticsUMK.commandProtocol.parser.IProtocolParserListener;


public class TractorService extends Service implements IProtocolParserListener
{

    public final static int CONNECTION_TYPE_BT = 0;
    public final static int CONNECTION_TYPE_WF = 1;

    public final static String ACTION_CONNECT = "com.example.tractor.connect";
    public final static String ACTION_DISCONNECT = "com.example.tractor.disconnect";
    public final static String ACTION_CONNECT_ERROR = "com.example.tractor.connectError";
    public final static String ACTION_DISCOVERY_STARTED = "com.example.tractor.discoveryStarted";
    public final static String ACTION_DISCOVERY_FINISHED = "com.example.tractor.discoveryFinished";
    public final static String ACTION_DEVICE_FOUND = "com.example.tractor.deviceFound";
    public final static String ACTION_DATA_RECEIVED = "com.example.tractor.dataReceived";

//    public final static String EXTRA_RECEIVED_DATA = "receivedData";

    private TractorConnection _connection;

    private BroadcastReceiver _connectionReceiver;

    private boolean _isEngineRunning = false;


    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("myTag", "Tractor service create");
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.d("myTag", "Tractor service destroy");
    }


    @Override
    public void onCommand(ProtocolCommand command)
    {

        Log.d("myTag", "Server command " + command.getData());

        switch (command.getType())
        {
            case ControlDeviceCommand.TYPE_DEVICE_CONNECT_RESULT:

                if(command.getData()[0] == 1)
                {
                    Intent conIntent = new Intent(TractorService.ACTION_CONNECT);
                    sendBroadcast(conIntent);
                }
                else
                {
                    Intent intent = new Intent(TractorService.ACTION_CONNECT_ERROR);
                    intent.putExtra("message", "Incorrect password");
                    sendBroadcast(intent);
                }

                break;
        }

    }


    public void createConnection(int type)
    {
        if (_connectionReceiver != null)
        {
            unregisterReceiver(_connectionReceiver);
            _connectionReceiver = null;
        }

        switch (type)
        {
            case CONNECTION_TYPE_BT:
                createBTConnection();

                break;
            case CONNECTION_TYPE_WF:
                createWiFiConnection();
                break;
        }
    }


//    @Override
//    public void onCommand(ProtocolCommand command)
//    {
//        Log.d("myTag", "Command data - " + command.getStringData());
//
//        switch (command.getType())
//        {
//            //Connect result received
//            case ControlDeviceCommand.TYPE_DEVICE_CONNECT_RESULT:
//
////                if(command.getParam("engine").equals("1"))
////                {
////                    _isEngineRunning = true;
////                }
//
//                Log.d("myTag", "CONNECT!!!!!!!!!!!!");
//
//                Intent conIntent = new Intent(TractorService.ACTION_CONNECT);
//                sendBroadcast(conIntent);
//                break;
//
//            case ControlDeviceCommand.TYPE_TRACTOR_INFO:
////                TractorService.this._currentSpeed = Integer.parseInt(command.getParam("speed"));
////                TractorService.this._currentTurn = Integer.parseInt(command.getParam("turn"));
//                break;
//        }
//
//        _connection.setLastReceivedCommand(command);
//
//        Intent intent = new Intent(TractorService.ACTION_DATA_RECEIVED);
////        intent.putExtra(EXTRA_RECEIVED_DATA, data);
//        sendBroadcast(intent);
//    }


    public void connectToServer(final TractorDevice device)
    {

        closeConnection();

        try
        {
            _connection.connectToDevice(device);

        } catch (IOException e)
        {
            e.printStackTrace();
            Intent intent = new Intent(TractorService.ACTION_CONNECT_ERROR);
            intent.putExtra("message", "IOException");
            sendBroadcast(intent);

        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            Intent intent = new Intent(TractorService.ACTION_CONNECT_ERROR);
            intent.putExtra("message", "NoSuchMethodException");
            sendBroadcast(intent);

        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
            Intent intent = new Intent(TractorService.ACTION_CONNECT_ERROR);
            intent.putExtra("message", "IllegalAccessException");
            sendBroadcast(intent);

        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
            Intent intent = new Intent(TractorService.ACTION_CONNECT_ERROR);
            intent.putExtra("message", "InvocationTargetException");
            sendBroadcast(intent);
        }
    }


    public void closeConnection()
    {
        try
        {
            if(_connection != null && _connection.isConnecting())
            {
                sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_DEVICE_DISCONNECT, 0));
                _connection.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public TractorConnection getConnection()
    {
        return _connection;
    }


    public void setPower(int value)
    {
        sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_SET_POWER, value));
    }


    public void setTurn(int value)
    {
        sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_SET_TURN, value));
    }


    public void startEngine()
    {
        if(!_isEngineRunning)
        {
            sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_START_ENGINE, 1));
            _isEngineRunning = true;
        }
    }


    public void stopEngine()
    {
        if(_isEngineRunning)
        {
            sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_START_ENGINE, 0));
            _isEngineRunning = false;
        }
    }


    public boolean isEngineRunning()
    {
        return _isEngineRunning;
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return  new TractorServiceBinder();
    }


    public class TractorServiceBinder extends Binder
    {
        public TractorService getService()
        {
            return TractorService.this;
        }
    }


    ////////////
    //Private///
    ////////////


    private void sendCommand(ProtocolCommand command)
    {
        try
        {
            _connection.sendData(command.getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private void createBTConnection()
    {
//        BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED
    }

//    private void createBTConnection()
//    {
//        if(_connectionReceiver != null)
//        {
//            unregisterReceiver(_connectionReceiver);
//            _connectionReceiver = null;
//        }
//
//        _connection = new TractorBluetoothConnection();
//
//        _connectionReceiver = new BroadcastReceiver()
//        {
//            public  void onReceive(Context context, Intent intent)
//            {
//
//                String action = intent.getAction();
//
//                switch (action)
//                {
//                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                        sendBroadcast(new Intent(ACTION_DISCOVERY_STARTED));
//                        break;
//
//                    case BluetoothDevice.ACTION_FOUND:
//
//                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                        _connection.addDevice(new TractorDevice<BluetoothDevice>(device.getAddress(), device.getName(), device));
//
//                        sendBroadcast(new Intent(ACTION_DEVICE_FOUND));
//                        break;
//
//                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                        sendBroadcast(new Intent(ACTION_DISCOVERY_FINISHED));
//                        break;
//
//                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                        closeConnection();
//                        sendBroadcast(new Intent(ACTION_DISCONNECT));
//                        break;
//                }
//            }
//        };
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//
//        registerReceiver(_connectionReceiver, filter);
//    }


    private void createWiFiConnection()
    {
        _connection = new TractorWiFiConnection(getBaseContext());

        _connectionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                final String action = intent.getAction();

                switch (action)
                {

                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                        ((TractorWiFiConnection) _connection).addSearchDevices();
                        sendBroadcast(new Intent(ACTION_DEVICE_FOUND));
                        break;

                    case ConnectivityManager.CONNECTIVITY_ACTION:

                        Log.d("myTag", "CONNECTIVITY_ACTION");

                        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

                        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
                        {
                            Log.d("myTag", "Have Wifi Connection");

                            Thread thread = new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {


                                    try
                                    {
                                        ((TractorWiFiConnection) _connection).createSocket();

                                    } catch (IOException e)
                                    {
//                                        e.printStackTrace();

                                        Log.d("myTag", e.getMessage());

                                        Intent intent = new Intent(TractorService.ACTION_CONNECT_ERROR);
                                        intent.putExtra("message", "Error connect to socket server");
                                        sendBroadcast(intent);
                                    }

                                    if(!_connection.isConnecting())
                                    {
                                        return;
                                    }

                                    final CommandProtocolParser parser = new CommandProtocolParser();

                                    parser.setDetectCommandListener(TractorService.this);

                                    sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_DEVICE_CONNECT, TractorConnection.CONNECT_PASSWORD));

                                    while (_connection.isConnecting())
                                    {
                                        byte[] buffer = new byte[0];

                                        try
                                        {
                                            buffer = _connection.read();
                                        } catch (IOException e)
                                        {
                                            e.printStackTrace();
                                        }

                                        if(buffer.length > 0)
                                        {
                                            parser.parse(buffer);
                                        }
                                    }
                                }
                            });

                            thread.start();
                        }
                        else
                        {
                            Log.d("myTag", "Don't have Wifi Connection");
                        }

                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(_connectionReceiver, filter);
    }


//    private void createWiFiConnection()
//    {
//        if(_connectionReceiver != null)
//        {
//            unregisterReceiver(_connectionReceiver);
//            _connectionReceiver = null;
//        }
//
//        _connection = new TractorWiFiConnection(getBaseContext());
//
//        _connectionReceiver = new BroadcastReceiver()
//        {
//            @Override
//            public void onReceive(Context context, Intent intent)
//            {
//
//                final String action = intent.getAction();
//
//                switch (action)
//                {
//                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
//                        ((TractorWiFiConnection) _connection).addSearchDevices();
//                        sendBroadcast(new Intent(ACTION_DEVICE_FOUND));
//                        break;
//
//                    case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
//
//                        SupplicantState supplicantState = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
//
//                        if(supplicantState == SupplicantState.COMPLETED)
//                        {
//                            Log.d("myTag", "New Wi-Fi device connected!");
//
//                            final CommandProtocolParser parser = new CommandProtocolParser();
//
//                            parser.setDetectCommandListener(TractorService.this);
//
//        //                    sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_DEVICE_CONNECT, 1));
//                            sendCommand(new ProtocolCommand(ControlDeviceCommand.TYPE_DEVICE_CONNECT, TractorConnection.CONNECT_PASSWORD));
//
//                            Thread thread = new Thread(new Runnable()
//                            {
//                                @Override
//                                public void run()
//                                {
//                                    while (_connection.isConnecting())
//                                    {
//                                        byte[] buffer = new byte[0];
//
//                                        try
//                                        {
//                                            buffer = _connection.read();
//                                        } catch (IOException e)
//                                        {
//                                            e.printStackTrace();
//                                        }
//
//                                        if(buffer.length > 0)
//                                        {
//                                            parser.parse(buffer);
//                                        }
//                                    }
//                                }
//                            });
//
//                            thread.start();
//                        }
//
//                        break;
//
////                    case WifiManager.NETWORK_STATE_CHANGED_ACTION:
////                        Log.d("myTag", "NETWORK_STATE_CHANGED_ACTION");
////                        break;
//
////                    case WifiManager.EXTRA_SUPPLICANT_CONNECTED:
////                        Log.d("myTag", "EXTRA_SUPPLICANT_CONNECTED - ");
////                        break;
////
////                    case WifiManager.EXTRA_SUPPLICANT_ERROR:
////                        Log.d("myTag", "EXTRA_SUPPLICANT_ERROR - ");
////                        break;
////
////                    case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
////                        Log.d("myTag", "SUPPLICANT_CONNECTION_CHANGE_ACTION - ");
////                        break;
//                }
////                if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
////                    if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
////                        //do stuff
////                    } else {
////                        // wifi connection was lost
////                    }
////                }
//            }
//        };
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//
////        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
////        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
////        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
////        filter.addAction(WifiManager.EXTRA_SUPPLICANT_CONNECTED);
////        filter.addAction(WifiManager.EXTRA_SUPPLICANT_ERROR);
//
//        registerReceiver(_connectionReceiver, filter);
//    }


}

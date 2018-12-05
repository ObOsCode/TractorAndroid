package com.example.tractor.activitys.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tractor.R;
import com.example.tractor.activitys.ConnectDeviceActivity;
import com.example.tractor.services.tractor.TractorService;
import com.example.tractor.services.tractor.connections.Command;

import ru.roboticsUMK.commandProtocol.ProtocolCommand;


public class ConnectTractorActivityBase extends AppCompatActivity
{

    protected TractorService tractorService;

    private BroadcastReceiver _tractorConnectionReceiver;

    private ServiceConnection _tractorServiceConnection;

    IntentFilter _receiverFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        _tractorServiceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder)
            {
                tractorService = ((TractorService.TractorServiceBinder) iBinder).getService();

                ConnectTractorActivityBase.this.onServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName)
            {
                ConnectTractorActivityBase.this.onServiceDisconnected();
            }
        };

        _tractorConnectionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                String action = intent.getAction();

                switch (action)
                {
                    case TractorService.ACTION_CONNECT:
                        onConnectDevice();
                        break;
                    case TractorService.ACTION_DISCONNECT:
                        onDisconnectDevice();
                        break;
                    case TractorService.ACTION_CONNECT_ERROR:
                        onConnectDeviceError(intent.getStringExtra("message"));
                        break;
                    case TractorService.ACTION_DISCOVERY_STARTED:
                        onDiscoveryStarted();
                        break;
                    case TractorService.ACTION_DISCOVERY_FINISHED:
                        onDiscoveryFinished();
                        break;
                    case TractorService.ACTION_DEVICE_FOUND:
                        onDeviceFound();
                        break;
                    case TractorService.ACTION_DATA_RECEIVED:
                        onDataReceived(tractorService.getConnection().getLastReceivedCommand());
                        break;
                }

            }
        };

        _receiverFilter = new IntentFilter();
        _receiverFilter.addAction(TractorService.ACTION_CONNECT);
        _receiverFilter.addAction(TractorService.ACTION_DISCONNECT);
        _receiverFilter.addAction(TractorService.ACTION_CONNECT_ERROR);
        _receiverFilter.addAction(TractorService.ACTION_DISCOVERY_STARTED);
        _receiverFilter.addAction(TractorService.ACTION_DISCOVERY_FINISHED);
        _receiverFilter.addAction(TractorService.ACTION_DEVICE_FOUND);
        _receiverFilter.addAction(TractorService.ACTION_DATA_RECEIVED);

        startService(new Intent(this, TractorService.class));
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        bindService(new Intent(this, TractorService.class), _tractorServiceConnection, 0);

        registerReceiver(_tractorConnectionReceiver, _receiverFilter);
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        unbindService(_tractorServiceConnection);
        unregisterReceiver(_tractorConnectionReceiver);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }



    public void stopTractorService()
    {
        tractorService.closeConnection();
        super.stopService(new Intent(this, TractorService.class));
    }



    ////////////
    //Protected
    ///////////

    //For override in superclass

    protected void onServiceConnected()
    {

    }


    protected void onServiceDisconnected()
    {

    }


    protected void onConnectDevice()
    {

    }

    protected void onConnectDeviceError(String message)
    {

    }

    protected void onDisconnectDevice()
    {
//        String message = "";

        Toast.makeText(this, getString(R.string.bt_on_disconnect_message), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), ConnectDeviceActivity.class));
    }

    protected void onDiscoveryStarted()
    {

    }

    protected void onDiscoveryFinished()
    {

    }

    protected void onDeviceFound()
    {

    }

    protected void onDataReceived(ProtocolCommand command)
    {

    }

}

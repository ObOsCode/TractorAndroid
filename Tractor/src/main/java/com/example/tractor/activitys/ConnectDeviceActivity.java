package com.example.tractor.activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tractor.R;
import com.example.tractor.activitys.base.MenuActivityBase;
import com.example.tractor.services.tractor.TractorDevice;
import com.example.tractor.services.tractor.TractorService;
import com.example.tractor.services.tractor.connections.TractorConnection;


public class ConnectDeviceActivity extends MenuActivityBase implements View.OnClickListener, AdapterView.OnItemClickListener, TabHost.OnTabChangeListener
{

    private TextView _searchInfoTextView;
    private Button _searchButton;

    private AlertDialog _connectDialog;

    private ArrayAdapter<TractorDevice> _deviceListAdapter;

    ListView _devicesListView;

    private final String _BT_TAB_TAG = "BT";
    private final String _WIFI_TAB_TAG = "WIFI";

    public static final int ENABLE_BT_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.device_list_activity_label));

        setContentView(R.layout.activity_connect_device);

        TabHost tabHost = (TabHost) findViewById(R.id.tab_connection_type);
        tabHost.setup();
        TabHost.TabSpec tabSpec;

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(_WIFI_TAB_TAG);
        tabSpec.setIndicator(getString(R.string.wifi_button_label));
        tabSpec.setContent(R.id.layout_wifi_devices_list);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(_BT_TAB_TAG);
        tabSpec.setIndicator(getString(R.string.bt_button_label));
        tabSpec.setContent(R.id.layout_bt_devices_list);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag(_WIFI_TAB_TAG);
        tabHost.setOnTabChangedListener(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.bt_connect));
        dialogBuilder.setCancelable(false);

        _connectDialog = dialogBuilder.create();

        _searchButton = (Button)findViewById(R.id.but_search_device);
        _searchButton.setOnClickListener(this);

        _searchInfoTextView = (TextView) findViewById(R.id.text_search_device_info);

        //Hide home button
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    protected void onServiceConnected()
    {
        super.onServiceConnected();

        tractorService.closeConnection();

        setWFConnection();
    }


    @Override
    public void onClick(View view)
    {

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        TractorConnection connection = tractorService.getConnection();

        if(connection.isDiscovering())
        {
            connection.cancelDiscovery();
        }

        TractorDevice device = connection.getDeviceList().get(i);

        showConnectDialog(device.getName());

        tractorService.connectToServer(device);
    }


    @Override
    public void onTabChanged(String s)
    {
        Log.d("myTag", "onTabChanged");
    }


    @Override
    protected void onDestroy()
    {
        Log.d("myTag", "onDestroy");
        super.onDestroy();
        stopTractorService();
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode)
//        {
//            case ENABLE_BT_REQUEST:
//
//                if(resultCode == RESULT_OK)
//                {
//                    createDeviceListAdapter();
//                }
//        }
//    }
//
//
//    @Override
//    protected void onDiscoveryStarted()
//    {
//        super.onDiscoveryStarted();
//
//        _searchButton.setText(R.string.search_device_but_label_cancel);
//        _searchInfoTextView.setText(R.string.bt_search_process);
//    }
//
//
    @Override
    protected void onDeviceFound()
    {
        super.onDeviceFound();

        _deviceListAdapter.notifyDataSetChanged();
    }
//
//
//    @Override
//    protected void onDiscoveryFinished()
//    {
//        super.onDiscoveryFinished();
//        _searchButton.setText(R.string.search_device_but_label);
//        _searchInfoTextView.setText(R.string.bt_search_finished);
//    }
//
//
    @Override
    protected void onConnectDevice()
    {
        super.onConnectDevice();

        hideConnectDialog();

        String deviceName = tractorService.getConnection().getConnectedDevice().getName();

        Toast.makeText(this, getString(R.string.bt_connection_to_device_success) + " " + deviceName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onConnectDeviceError(String message)
    {
        super.onConnectDeviceError(message);

        hideConnectDialog();

        Log.d("myTag", message);

        Toast.makeText(this, getString(R.string.bt_connection_error) + " " + message, Toast.LENGTH_SHORT).show();
    }


//    @Override
//    public void onTabChanged(String s)
//    {
//        switch (s)
//        {
//            case _BT_TAB_TAG:
//                setBTConnection();
//                break;
//            case _WIFI_TAB_TAG:
//                setWFConnection();
//                break;
//        }
//
//        _searchInfoTextView.setText("");
//    }
//
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//    {
//        TractorConnection connection = tractorService.getConnection();
//
//        if(connection.isDiscovering())
//        {
//            connection.cancelDiscovery();
//        }
//
//        TractorDevice device = connection.getDeviceList().get(i);
//
//        showConnectDialog(device.getName());
//
//        tractorService.connectToServer(device);
//    }
//
//
//    @Override
//    public void onClick(View view)
//    {
//        switch (view.getId())
//        {
//            case R.id.but_search_device:
//
//                TractorConnection connection = tractorService.getConnection();
//
//                if(!connection.isDiscovering())
//                {
//                    connection.startDiscovery();
//                }else
//                {
//                    connection.cancelDiscovery();
//                }
//                break;
//        }
//    }


    //////////
    //Private
    //////////


    private void createDeviceListAdapter()
    {
        _deviceListAdapter = new ArrayAdapter<TractorDevice>(this, android.R.layout.simple_list_item_1, tractorService.getConnection().getDeviceList())
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                final TractorDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };

        _devicesListView.setAdapter(_deviceListAdapter);

        _deviceListAdapter.notifyDataSetChanged();
    }


//    private void setBTConnection()
//    {
//        super.tractorService.createConnection(TractorService.CONNECTION_TYPE_BT);
//
//        _devicesListView = (ListView) findViewById(R.id.list_bt_devices);
//        _devicesListView.setOnItemClickListener(this);
//
//        if(!tractorService.getConnection().isSuppored())
//        {
//            Toast.makeText(getApplicationContext(), getString(R.string.connection_not_suppored), Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        if(!tractorService.getConnection().isEnabled())
//        {
//            // Bluetooth выключен. Предложим пользователю включить его.
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST);
//        }else
//        {
//            createDeviceListAdapter();
//        }
//    }


    private void setWFConnection()
    {
        super.tractorService.createConnection(TractorService.CONNECTION_TYPE_WF);

        _devicesListView = (ListView) findViewById(R.id.list_wifi_devices);
        _devicesListView.setOnItemClickListener(this);

        createDeviceListAdapter();
    }


    private void showConnectDialog(CharSequence deviceName)
    {
        _connectDialog.setMessage(getString(R.string.bt_connect_with) + " " + deviceName + "...");
        _connectDialog.show();
    }


    private void hideConnectDialog()
    {
        _connectDialog.hide();
    }


}//class

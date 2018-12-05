package com.example.tractor.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tractor.R;
import com.example.tractor.activitys.base.MenuActivityBase;

public class MainMenuActivity extends MenuActivityBase implements View.OnClickListener
{

    private Button _autopilotBut;
    private Button _manualControlBut;
    private Button _backToGarageBut;
    private Button _diagnosticsBut;
    private Button _stopBut;
    private Button _followToPersonBut;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        _autopilotBut = (Button) findViewById(R.id.but_main_menu_autopilot);
        _manualControlBut = (Button) findViewById(R.id.but_main_menu_manual_control);
        _backToGarageBut = (Button) findViewById(R.id.but_main_menu_back_garage);
        _diagnosticsBut = (Button) findViewById(R.id.but_main_menu_diagnostics);
        _stopBut = (Button) findViewById(R.id.but_main_menu_stop);
        _followToPersonBut = (Button) findViewById(R.id.but_main_menu_folow_man);

        _autopilotBut.setOnClickListener(this);
        _manualControlBut.setOnClickListener(this);
        _backToGarageBut.setOnClickListener(this);
        _diagnosticsBut.setOnClickListener(this);
        _stopBut.setOnClickListener(this);
        _followToPersonBut.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        Intent intent = null;

        switch (view.getId())
        {
            case R.id.but_main_menu_autopilot:
                intent = new Intent(this, AutopilotMenuActivity.class);
                break;
            case R.id.but_main_menu_manual_control:
//                intent = new Intent(this, ManualControlRoundJoysticActivity.class);
                intent = new Intent(this, ManualControlSlideJoystickActivity.class);
                break;
        }

        if(intent!=null)
        {
            startActivity(intent);
        }
    }
}

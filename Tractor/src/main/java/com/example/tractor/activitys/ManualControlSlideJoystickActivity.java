package com.example.tractor.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tractor.R;
import com.example.tractor.activitys.base.MenuActivityBase;
import com.example.tractor.views.joysticks.slideJoystick.SlideJoysticChangeListener;
import com.example.tractor.views.joysticks.slideJoystick.SlideJoystick;

import ru.roboticsUMK.commandProtocol.ProtocolCommand;
import ru.roboticsUMK.commandProtocol.commands.ControlDeviceCommand;

public class ManualControlSlideJoystickActivity extends MenuActivityBase implements SlideJoysticChangeListener, View.OnClickListener
{

    private SlideJoystick _joysticHorizontal;
    private SlideJoystick _joysticVertical;

    private Button _startButton;

    private TextView _tractorInfoTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control_slide_joystick);

        _joysticHorizontal = (SlideJoystick) findViewById(R.id.joystick_horizontal);
        _joysticVertical = (SlideJoystick) findViewById(R.id.joystick_vertical);

        _joysticHorizontal.setChangeListener(this);
        _joysticVertical.setChangeListener(this);

        _startButton = (Button) findViewById(R.id.but_start_engine);
        _startButton.setOnClickListener(this);

        _tractorInfoTextView = (TextView) findViewById(R.id.text_tractor_info);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        _joysticHorizontal.removeChangeListener();
        _joysticVertical.removeChangeListener();
    }


    @Override
    protected void onServiceConnected()
    {
        super.onServiceConnected();
        if (tractorService.isEngineRunning())
        {
            _startButton.setText(R.string.stop_engine_but_label);
        }else
        {
            _startButton.setText(R.string.start_engine_but_label);
        }
    }


    @Override
    public void onJoystickPositionChange(int value, SlideJoystick joystick)
    {
        switch (joystick.getType())
        {
            case SlideJoystick.TYPE_HORIZONTAL:
                tractorService.setTurn(value);
                break;
            case SlideJoystick.TYPE_VERTICAL:
                tractorService.setPower(value);
                break;
        }
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.but_start_engine:

                if(tractorService.isEngineRunning())
                {
                    tractorService.stopEngine();
                    _startButton.setText(R.string.start_engine_but_label);
                }else
                {
                    tractorService.startEngine();
                    _startButton.setText(R.string.stop_engine_but_label);
                }

                break;
        }
    }


    @Override
    protected void onDataReceived(ProtocolCommand command)
    {
        super.onDataReceived(command);

        switch (command.getType())
        {
            case ControlDeviceCommand.TYPE_TRACTOR_INFO:
//                String text = "Скорость: " + command.getParam("speed") + "\n" +
//                        "Поворот: " + command.getParam("turn");
//                _tractorInfoTextView.setText(text);
                break;
        }
    }


}

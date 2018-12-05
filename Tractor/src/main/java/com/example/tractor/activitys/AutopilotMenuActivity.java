package com.example.tractor.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tractor.R;
import com.example.tractor.activitys.base.MenuActivityBase;

public class AutopilotMenuActivity extends MenuActivityBase implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autopilot_menu);

        Button addTrackButton = (Button) findViewById(R.id.but_autopilot_add_track);
        addTrackButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.but_autopilot_add_track:

                Intent intent = new Intent(this, AddTrackActivity.class);
                startActivity(intent);
                break;
        }
    }
}

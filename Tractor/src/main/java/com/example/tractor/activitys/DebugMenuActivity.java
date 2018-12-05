package com.example.tractor.activitys;

import android.os.Bundle;

import com.example.tractor.R;
import com.example.tractor.activitys.base.MenuActivityBase;

public class DebugMenuActivity extends MenuActivityBase
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_menu);
    }
}

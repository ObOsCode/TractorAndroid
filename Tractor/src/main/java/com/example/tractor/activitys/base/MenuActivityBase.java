package com.example.tractor.activitys.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tractor.R;
import com.example.tractor.activitys.ConnectDeviceActivity;
import com.example.tractor.activitys.DebugMenuActivity;

public class MenuActivityBase extends ConnectTractorActivityBase
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.actionbar_menu_debug:
                showInputPasswordDialog();
                return true;

            case R.id.actionbar_menu_disable_connection:
                startActivity(new Intent(this.getApplicationContext(), ConnectDeviceActivity.class));
                return true;

            case R.id.actionbar_menu_settings:
                return true;

            case R.id.actionbar_menu_close_app:
                ActivityCompat.finishAffinity(this);
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showInputPasswordDialog()
    {
        //Получаем вид с файла prompt.xml, который применим для диалогового окна:
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.input_password_dialog, null);

        //Создаем AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setView(dialogView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText userInput = (EditText) dialogView.findViewById(R.id.text_input_password);

        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton(R.string.ok_button_label, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(userInput.getText().toString().equals(getString(R.string.debug_menu_password)))
                {
                    Intent debugIntent = new Intent(MenuActivityBase.this, DebugMenuActivity.class);
                    startActivity(debugIntent);
                }
                else
                {
                    Toast toast = Toast.makeText(MenuActivityBase.this, getString(R.string.password_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        dialogBuilder.setNegativeButton(R.string.search_device_but_label_cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = dialogBuilder.create();

        dialog.show();
    }

}//class

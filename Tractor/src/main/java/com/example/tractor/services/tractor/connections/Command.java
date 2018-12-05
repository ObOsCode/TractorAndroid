package com.example.tractor.services.tractor.connections;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 12.07.17.
 */

public class Command
{

    public static final int START_ENGINE= 1;
    public static final int SPEED = 2;
    public static final int TURN = 3;
    public static final int CONNECT = 4;

    public static final int INPUT_CONNECT_INFO = 5;
    public static final int INPUT_TRACTOR_INFO = 6;

    private int _type;

    private String _data;

    private Map<String, String> _params = new HashMap<String, String>();


    public static Command parse(String data)
    {
        int commandType = Integer.parseInt(data.substring(data.indexOf("TS") + 2, data.indexOf("TE")));
        String commandData = data.substring(data.indexOf("DS") + 2, data.indexOf("DE"));

        Command command = new Command(commandType, commandData);

        return command;
    }


    public Command(int type, String data)
    {
        super();
        _type = type;
        _data = data;

        if(_data.indexOf("%") < 0)
        {
            if(_data.indexOf(":") >= 0)
            {
                int index = _data.indexOf(":");

                String paramName = _data.substring(0, index);
                String paramValue = _data.substring(index + 1, _data.length());

//                _params.put(paramName, paramValue);
                addParam(paramName, paramValue);
            }

        }else
        {
            String[] paramsStr = _data.split("%");

            for(String paramStr: paramsStr)
            {
                int index = paramStr.indexOf(":");

                if(index>0)
                {
                    String paramName = paramStr.substring(0, index);
                    String paramValue = paramStr.substring(index + 1, paramStr.length());
//                    _params.put(paramName, paramValue);
                    addParam(paramName, paramValue);
                }
            }
        }
    }


    public Command(int type, int data)
    {
        this(type, Integer.toString(data));
    }


    public void addParam(String name, String value)
    {
        _params.put(name, value);
    }

    public String getParam(String name)
    {
        return _params.get(name);

    }


    public int getType()
    {
        return _type;
    }


    public String getData()
    {
        return _data;
    }


    public String getString()
    {
        return "TS" + Integer.toString(_type) + "TEDS" + _data + "DE";
    }
}

package com.example.tractor.services.tractor;

/**
 * Created by user on 07.07.17.
 */

public class TractorDevice<DeviceType>
{
    private String _id;
    private String _name;
    DeviceType _device;


    public TractorDevice(String id, String name)
    {
        super();

        _id = id;
        _name = name;
    }


    public TractorDevice(String id, String name, DeviceType device)
    {
        this(id, name);

        _device = device;
    }


    public String getName()
    {
        return _name;
    }

    public String getId()
    {
        return _id;
    }

    public DeviceType getDevice()
    {
        return _device;
    }

}

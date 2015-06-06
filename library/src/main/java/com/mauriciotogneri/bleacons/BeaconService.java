package com.mauriciotogneri.bleacons;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mauriciotogneri.bleacons.modes.ReadingMode;

import java.util.Arrays;

@TargetApi(18)
public class BeaconService extends Service implements LeScanCallback
{
    private ReadingMode readingMode;
    private final Object readingModeLock = new Object();
    private BluetoothAdapter bluetoothAdapter;
    private volatile boolean isScanning = false;

    public void startListening(ReadingMode mode)
    {
        synchronized (readingModeLock)
        {
            readingMode = mode;
        }

        resume();
    }

    public void pause()
    {
        if ((isScanning) && (bluetoothAdapter != null))
        {
            bluetoothAdapter.stopLeScan(this);
        }

        isScanning = false;
    }

    public void resume()
    {
        if ((!isScanning) && (bluetoothAdapter != null))
        {
            bluetoothAdapter.startLeScan(this);
        }

        isScanning = true;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] data)
    {
        String macAddress = device.getAddress();

        synchronized (readingModeLock)
        {
            if (readingMode != null)
            {
                readingMode.process(macAddress, rssi, data);
            }
        }

        Log.d("BEACON SCANNED", "MAC: " + macAddress + ", RSSI: " + rssi + ", DATA LENGTH: " + Arrays.toString(data));
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return new BeaconBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public void onDestroy()
    {
        pause();

        super.onDestroy();
    }

    public class BeaconBinder extends Binder
    {
        public BeaconService getService()
        {
            return BeaconService.this;
        }
    }
}
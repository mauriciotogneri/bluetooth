package com.mauriciotogneri.bluetooth.test.beacons;

import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.mauriciotogneri.bluetooth.beacons.Beacon;
import com.mauriciotogneri.bluetooth.beacons.BeaconListener;
import com.mauriciotogneri.bluetooth.beacons.BeaconManager;
import com.mauriciotogneri.bluetooth.beacons.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bluetooth.beacons.custom.IBeacon;
import com.mauriciotogneri.bluetooth.test.R;

public class TestActivity extends Activity implements BeaconListener
{
	private BeaconManager beaconManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);
		
		try
		{
			this.beaconManager = new BeaconManager(this, 1000);
			this.beaconManager.addFilter(new IBeacon.Filter());
			this.beaconManager.addListener(this);
			this.beaconManager.start();
		}
		catch (UnsupportedBluetoothLeException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onReceive(List<Beacon> beacons)
	{
		Log.e("TEST", "<<< RECEIVED " + beacons.size());
	}
	
	@Override
	public void onDestroy()
	{
		this.beaconManager.stop();
		
		super.onDestroy();
	}
}

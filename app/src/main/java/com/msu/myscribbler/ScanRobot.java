package com.msu.myscribbler;

import java.util.Set;

import com.msu.myscribbler.R;
import com.msu.myscribbler.utils.Log;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ScanRobot extends ActionBarActivity{

	private static final String TAG= "ScanRobot";
	
	public static String DEVICE_NAME="scan_device_name";
	public static String DEVICE_ADDRESS = "scan_device";
	
	private static final int REQUEST_ENABLE_BT = 1;

	private Toolbar toolbar;
	private Button startScanButton;
	private TextView infoView;
	private BluetoothAdapter myBluetoothAdapter;
	private Set<BluetoothDevice> pairedDevice;
	private ArrayAdapter<String> myAdapter;
	private ListView myListView;
	
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.scan_robot);
		
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
	    setSupportActionBar(toolbar);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		myListView= (ListView) findViewById(R.id.listview_devices);
		myAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 );
		myListView.setAdapter(myAdapter);
		
		infoView= (TextView) findViewById(R.id.touch_search_view);
		
		// Register for broadcasts when a device is discovered
	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    this.registerReceiver(myReceiver, filter);

	    // Register for broadcasts when discovery has finished
	    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    this.registerReceiver(myReceiver, filter);
	    
	    startScanButton= (Button)findViewById(R.id.scan_start);
	    
		myBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
		
		if(myBluetoothAdapter.isEnabled()){
			}else{
				Log.d(TAG, "bluetoothadapter disabled");
				startScanButton.setEnabled(false);
				Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
			}
	
		if(myBluetoothAdapter==null){
			Log.d(TAG, "Bluetooth null");
			startScanButton.setEnabled(false);
			
			Toast.makeText(getApplicationContext(),"Bluetooth not supported. Please check your settings!",
					     Toast.LENGTH_LONG).show();
		}else{
			Log.d(TAG, "bluetoothadapter not null");
			Log.d(TAG, "bluetoothadapter enabled");
				
				startScanButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {					
					findDevices(v);
					listDevices(v);
					//myAdapter.clear();
					//myBluetoothAdapter.startDiscovery();			
					}
				});
			
			
		}
		
		myListView.setOnItemClickListener(deviceClickListener);
		
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private OnItemClickListener deviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, "Adapter item on click listener");
			myBluetoothAdapter.cancelDiscovery();
			// Get the device MAC address, which is the last 17 chars in the
		    // View
		    String deviceName = ((TextView) view).getText().toString();
		    Log.d(TAG, "Device name: "+deviceName);
		    String address = deviceName.substring(deviceName.length() - 17);
		    Log.d(TAG, "device address: "+address);
		    // Create the result Intent and include the MAC address
		      Intent intent = new Intent();
		      intent.putExtra(DEVICE_ADDRESS, address);
		      intent.putExtra(DEVICE_NAME, deviceName);
		      // Set result and finish this Activity
		      setResult(Activity.RESULT_OK, intent);
		      finish();
		}
		
		 
	 };
	
	/**
	 * Method to find bluetooth devices
	 */
	public void findDevices(View view){
		
		
		if(myBluetoothAdapter.isDiscovering()){
			myBluetoothAdapter.cancelDiscovery();
			Log.d(TAG, "Cancelling discovery");
		}else{
			Log.d(TAG, "Starting discovery");
			myAdapter.clear();
			myBluetoothAdapter.startDiscovery();
			//progressBar.setVisibility(View.VISIBLE);
			registerReceiver(myReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
			ScanRobot.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = ProgressDialog.show(ScanRobot.this, "",
								"Scanning devices", false, false);					
				}
			});
		}
	}
	
	
	/**
	 * Method to list bluetooth devices
	 * @param view
	 */
	public void listDevices(View view){
		infoView.setVisibility(View.VISIBLE);
		pairedDevice= myBluetoothAdapter.getBondedDevices();
		
		for(BluetoothDevice device: pairedDevice)
			myAdapter.add(device.getName()+"\n"+device.getAddress());
		
		Toast.makeText(getApplicationContext(), "Listing Paired Devices", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Activity result if prompted to turn Bluetooth on
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if(requestCode == REQUEST_ENABLE_BT){
			 if(myBluetoothAdapter.isEnabled()){
				 startScanButton.setEnabled(true);
				 Toast.makeText(getApplicationContext(),"Bluetooth Enabled..." ,	
					      Toast.LENGTH_SHORT).show();
			 }
		 }

	}

	
	
	final BroadcastReceiver myReceiver= new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action= intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() != BluetoothDevice.BOND_BONDED){
					Log.d(TAG, "device not bonded, adding");
					myAdapter.add(device.getName()+"\n"+device.getAddress());
					myAdapter.notifyDataSetChanged();
				}
			}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				
				Log.d(TAG, "discovery finished");
				//progressBar.setVisibility(View.INVISIBLE);
				ScanRobot.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//if(!(ScanRobot.this).isFinishing()){
							progressDialog.dismiss();
						//}
						
					}
				});
			}
			
			}
		};
	
}

package com.msu.myscribbler;

import java.io.File;

import com.msu.myscribbler.adapters.TabsPagerAdapter;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.commands.Scribbler;
import com.msu.myscribbler.utils.Log;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


@SuppressLint("NewApi") 
public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

	private static final String TAG="MainActivity";
	
	private AppModel appModel;
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	public final static String imageFilePath = Environment.getExternalStorageDirectory() + "/MyScribbler/Images/";
	public final static String commandsFilePath=Environment.getExternalStorageDirectory() + "/MyScribbler/CommandsFile/";
	
	private static final String[] tabs={"HOME", "INFO", "GALLERY"};
		
	private static final int SCAN_DEVICE = 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		// create folders in internal storage of the phone
		File ImgDir = new File(imageFilePath);
		if(!ImgDir.exists() || !ImgDir.isDirectory()){
			ImgDir.mkdirs();
		}
		File commandsDir= new File(commandsFilePath);
		if(!commandsDir.exists() || !commandsDir.isDirectory()){
			commandsDir.mkdirs();
		}
		
		// instantiating a Scribbler model on an application context level
		appModel=(AppModel)getApplicationContext();
		appModel.setScribbler(new Scribbler());
		
		//Initialize all the fields
		viewPager= (ViewPager) findViewById(R.id.pager);
		actionBar=getActionBar();
		mAdapter= new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
        
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
        // add swiping listener
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// On Tab Selected
		viewPager.setCurrentItem(tab.getPosition());
		}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    MenuItem settingItem=menu.findItem(R.id.action_settings);
	    settingItem.setVisible(false);
	    MenuItem openItem= menu.findItem(R.id.open);
	    openItem.setVisible(false);
	    MenuItem saveItem= menu.findItem(R.id.save);
	    saveItem.setVisible(false);
	    
	    return true;
	  }
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.scan:
			Intent scanIntent= new Intent(this, ScanRobot.class);
			startActivityForResult(scanIntent, SCAN_DEVICE);
			return true;
		case R.id.disconnect:
			Log.d(TAG, "Disconnect clicked");
			/*Scribbler currentScribbler= appModel.getScribbler();
			String address=currentScribbler.getDeviceAddress();
			Log.d(TAG, "Device Address: "+address);
			currentScribbler.disconnectFromDevice(address);*/
			return true;
		case R.id.dashboard:
			Log.d(TAG, "Command GUI clicked");
			Intent guiIntent= new Intent(this, CommandsGUI.class);
			startActivity(guiIntent);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
		
		
	}

	/**
	 * Result from the ScanRobot activity
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
	    case SCAN_DEVICE:
	    	 if (resultCode == Activity.RESULT_OK) {
	    		// Get the selected device's MAC address
	    		String name= data.getExtras().getString(ScanRobot.DEVICE_NAME);
	    	    String address = data.getExtras().getString(ScanRobot.DEVICE_ADDRESS);
	    	    Log.d(TAG, "Connected Device Address " + address);
	    		Scribbler myScribbler= new Scribbler(address);
	    		// try connecting to the device
	    		try{
	    			if(myScribbler.connect()){
	    				Log.d(TAG, "Connection successfull");
	    				//myScribbler.setDeviceAddress(address);
	    				//myScribbler.setDeviceName(name);
	    				appModel.setScribbler(myScribbler);
	    				// set the appModel status as connected
	    				appModel.setConnected(true);
	    				
	    				Scribbler check= appModel.getScribbler();
	    				//Log.d(TAG, "appmodel scribbler name: "+check.getDeviceName());
	    				check.beep(784, .03f);
	    				
	    				Toast.makeText(getApplicationContext(),
	    		                "Successful Connection to " + name, Toast.LENGTH_SHORT).show();
	    			}else{
	    				Toast.makeText(getApplicationContext(),
	    		                "Error connecting to " + name, Toast.LENGTH_SHORT).show();
	    			}
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
	    		
	    		 
	    	 }
	    	 break;
	    	 
		}	
		
	}
	
    
}

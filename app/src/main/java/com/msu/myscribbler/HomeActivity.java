package com.msu.myscribbler;

import java.io.File;

import com.msu.myscribbler.commands.Scribbler;
import com.msu.myscribbler.commands.SetCommands.LED;
import com.msu.myscribbler.fragments.GalleryFragment;
import com.msu.myscribbler.fragments.MainFragment;
import com.msu.myscribbler.interfaces.GalleryFragmentCallback;
import com.msu.myscribbler.interfaces.NavigationDrawerCallbacks;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.navigationdrawer.NavigationDrawerFragment;
import com.msu.myscribbler.utils.Constants;
import com.msu.myscribbler.utils.Log;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;


public class HomeActivity extends ActionBarActivity implements  NavigationDrawerCallbacks, GalleryFragmentCallback  {
	
	private static final String TAG="HomeActivity";
	
	private Toolbar toolbar;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private AppModel appModel;
		
	public final static String imageFilePath = Environment.getExternalStorageDirectory() + "/MyScribbler/Images/";
	public final static String commandsFilePath=Environment.getExternalStorageDirectory() + "/MyScribbler/CommandsFile/";
	
	public static final int SCAN_DEVICE = 1;
	public static final int TAKE_PHOTO=2; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
	
	    // call navigation drawer init method
	 	initializeDrawer();
	 		
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
		
		if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            MainFragment fragment = new MainFragment();
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }
	}
	
		
	/**
	 * Method to initialize the navigation drawer
	 */
	protected void initializeDrawer(){
		// initialize navigation drawer
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
	    setSupportActionBar(toolbar);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
		mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);
						
	}
	
	@Override
	public void onBackPressed() {
	     if (mNavigationDrawerFragment.isDrawerOpen())
	         mNavigationDrawerFragment.closeDrawer();
	     else{
	    	 super.onBackPressed();	    	 
	     }	         
	 }
		
	@Override
	public void onNavigationDrawerItemSelected(int position, final String title) {
		 if(!title.isEmpty()){
        	 new Handler().postDelayed(new Runnable(){
     			@Override
     			public void run() {
     				Log.d(TAG, "Item Title: "+title);
     		        Intent intent = null;     		      
     		        if(Constants.SCAN.equalsIgnoreCase(title)){     		        	
     		        	intent= new Intent(HomeActivity.this, ScanRobot.class);
     		        	startActivityForResult(intent, SCAN_DEVICE);
     		        }else if(Constants.GUI.equalsIgnoreCase(title)){     		        
     		        	intent= new Intent(HomeActivity.this, CommandsGUI.class);
     		        	startActivity(intent);
     		        	//startActivityForResult(intent, TAKE_PHOTO);     		            	
     		        } else if(Constants.DISCONNECT.equalsIgnoreCase(title)){
     		        	//intent= new Intent(getActivity(), AddNewFriends.class);
     		        	if(appModel.getScribbler().isConnected()){
     		        		AlertDialog.Builder confirm= new AlertDialog.Builder(HomeActivity.this);
							confirm.setTitle("Confirm Disconnect?");
							confirm.setCancelable(true);
							confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									boolean result=appModel.getScribbler().disconnect();
		     		        		if(result)
		     		        			Toast.makeText(getApplicationContext(), "Connection disabled...", Toast.LENGTH_SHORT).show();
		     		        		else
		     		        			Toast.makeText(getApplicationContext(), "Error disconnecting...", Toast.LENGTH_SHORT).show();
								}
							});
							confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							 AlertDialog confirmAlert= confirm.create();
							 confirm.show();
     		        		
     		        	}else{
     		        		Toast.makeText(getApplicationContext(), "Not connected to any device...", Toast.LENGTH_SHORT).show();
     		        	}
     		        }    		          				
     			}             	
             }, 300);
        }     
	}
	
	@Override
	public void updateData() {
			
	}
	
	
	/**
	 * Result from the ScanRobot activity
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		
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
	    
	    case HomeActivity.TAKE_PHOTO:
	    	if (resultCode == Activity.RESULT_OK) {
	    		Log.d(TAG, "TAKE PHOTO");
	    		/*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    		Fragment galleryFragment= getSupportFragmentManager().findFragmentByTag(GalleryFragment.TAG);
	    		transaction.detach(galleryFragment);
	    		transaction.attach(galleryFragment);
	    		transaction.commit();*/
	    	}
	    	break;	
	   
		}	
		
	}

	

}

package com.msu.myscribbler.fragments;

import java.math.BigDecimal;

import com.msu.myscribbler.CommandsGUI;
import com.msu.myscribbler.HomeActivity;
import com.msu.myscribbler.R;
import com.msu.myscribbler.TakePicture;
import com.msu.myscribbler.VoiceCommand;
import com.msu.myscribbler.R.id;
import com.msu.myscribbler.R.layout;
import com.msu.myscribbler.commands.SetCommands.LED;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.utils.Constants;
import com.msu.myscribbler.utils.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnItemSelectedListener, OnTouchListener, SensorEventListener{
	
	private static final String TAG="HomeFragment";
	
	private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";
    
	private AppModel appModel;
	
	private Spinner mySpinner;
	private String[] entries={"Buttons", "Phone Tilt", "Voice"};
	
	private Switch leftSwitch, centerSwitch, rightSwitch;
	
	private ImageView leftButton, rightButton, forwardButton, backwardButton, stopButton;
	private SensorManager sensorManager;
	private Sensor acceleroMeter;

	
	private boolean tiltControl=false;

	private double threshold = 2.0;
	private double negThreshold = -2.0;	
	private float x, y, z;
	private boolean allEnabled = false;
	private boolean upEnabled = false;
	private boolean downEnabled = false; 
	private boolean leftEnabled = false; 
	private boolean rightEnabled = false;
	
	private MyCompassView mCustomDrawableView;

	/**
     * @return a new instance of {@link ContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static HomeFragment newInstance(CharSequence title, int indicatorColor,
                                              int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);     

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Log.d(TAG, "oncreate called");
		
		setHasOptionsMenu(true);
		mCustomDrawableView= new MyCompassView(getActivity());
		
		appModel=(AppModel)getActivity().getApplicationContext();
		// initialize sensor manager
		sensorManager=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		acceleroMeter=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);		
	}

	@Override
	public void onResume() {
		
		super.onResume();
		Log.d(TAG, "on Resume");
		sensorManager.registerListener(this, acceleroMeter, SensorManager.SENSOR_DELAY_NORMAL);
		//sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	}
		
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "on Pause");
		sensorManager.unregisterListener(this);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.home_fragment_menu, menu);
		
	}

	
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.switch_ctrl:
			Log.d(TAG, "SWITCH CONTROL MENU ITEM CLICKED");
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
			 //String options[] = {Constants.BUTTONS,Constants.PHONE_TILT, Constants.VOICE};
			/** Removed Phone Tilt **/
			String options[] = {Constants.BUTTONS, Constants.VOICE};
			 builder1.setTitle("Choose Control Type");
			 builder1.setCancelable(true)
			 		.setItems(options, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Stop the Scribbler if it is moving
							if(appModel.getScribbler()!=null && appModel.isConnected()){
								try {
									appModel.getScribbler().stop();
								} catch (Exception e) {
									e.printStackTrace();
								}			
							}
							if(which==0){
								unregisterSensor();
							}else if(which==1){							
								unregisterSensor();
								Intent i= new Intent(getActivity(), VoiceCommand.class);
								startActivity(i);
							}else if(which==2){
								Log.d(TAG, "Phone tilt selected");
								registerSensor();
							}
						}			 			
			 		});		
			 AlertDialog alert1= builder1.create();
			 alert1.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);			
		}
		
	}
	 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 Log.d(TAG, "onCreateView called");
	     return inflater.inflate(R.layout.home_fragment, container, false);
	}
	 
	 @Override
	 public void onViewCreated(View rootView, Bundle savedInstanceState) {
		 
		 super.onViewCreated(rootView, savedInstanceState);
		 Bundle args= getArguments();
		 Log.d(TAG, "onViewCreated called");
		 Log.d(TAG, "Device Name: "+appModel.getScribbler().getName());
		 leftButton=(ImageView)rootView.findViewById(R.id.left);
		 leftButton.setOnTouchListener(this);			
		 rightButton=(ImageView) rootView.findViewById(R.id.right);
		 rightButton.setOnTouchListener(this);
		 forwardButton=(ImageView) rootView.findViewById(R.id.forward);		 
		 forwardButton.setOnTouchListener(this);
		 backwardButton=(ImageView) rootView.findViewById(R.id.backward);		 
		 backwardButton.setOnTouchListener(this);
		 stopButton=(ImageView) rootView.findViewById(R.id.stop);		 
		 stopButton.setOnTouchListener(this);
		 mySpinner= (Spinner) rootView.findViewById(R.id.controller);
		 ArrayAdapter<String>adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,entries);
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 mySpinner.setAdapter(adapter);
		 mySpinner.setOnItemSelectedListener(this);
			
		 leftSwitch= (Switch) rootView.findViewById(R.id.left_switch);
		 leftSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					appModel.getScribbler().setLED(LED.LEFT, true);
				else
					appModel.getScribbler().setLED(LED.LEFT, false);
				
			}
		 });

		 centerSwitch= (Switch) rootView.findViewById(R.id.center_switch);
		 centerSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
						appModel.getScribbler().setLED(LED.CENTER, true);
					else
						appModel.getScribbler().setLED(LED.CENTER, false);					
				}
			 });
		 
		 rightSwitch= (Switch) rootView.findViewById(R.id.right_switch);
		 rightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
						appModel.getScribbler().setLED(LED.RIGHT, true);
					else
						appModel.getScribbler().setLED(LED.RIGHT, false);					
				}
			 });
		 
		 
		 Button takePicture= (Button)rootView.findViewById(R.id.take_picture);
		 takePicture.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				Intent pictureIntent= new Intent(getActivity(), TakePicture.class);
				//startActivity(pictureIntent);
				getActivity().startActivityForResult(pictureIntent, HomeActivity.TAKE_PHOTO);
			}
		});			
	 }
	 
		
	/**
	 * OnTouch listeners for the buttons
	 * Continuously moves the robot according to the button held
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "onTouch listener called");
		// Null pointer check for Scribbler and connected status
		if(appModel.getScribbler()!=null && appModel.isConnected()){
			switch(v.getId()){
			case R.id.left:
				if(event.getAction() == MotionEvent.ACTION_SCROLL){
					return false;
				}else if(event.getAction() == MotionEvent.ACTION_DOWN){	
						Log.d(TAG, "left button pressed");
						try {
							leftButton.setBackgroundResource(R.drawable.arrow_left_touch);
							appModel.getScribbler().turnLeft(.5);
						} catch (Exception e) {
							e.printStackTrace();
						}
					return true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
						Log.d(TAG, "left button released");
						try {
							leftButton.setBackgroundResource(R.drawable.arrow_left);
							appModel.getScribbler().stop();
						} catch (Exception e) {
							e.printStackTrace();
						}
					return true;
				}
				break;
			case R.id.right:
				if(event.getAction() == MotionEvent.ACTION_SCROLL){
					return false;
				}else if(event.getAction() == MotionEvent.ACTION_DOWN){	
					Log.d(TAG, "right button pressed");
					try {
						rightButton.setBackgroundResource(R.drawable.arrow_right_touch);
						appModel.getScribbler().turnRight(.5);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
					Log.d(TAG, "right button released");
					try {
						rightButton.setBackgroundResource(R.drawable.arrow_right);
						appModel.getScribbler().stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
				break;
			case R.id.forward:
				if(event.getAction() == MotionEvent.ACTION_SCROLL){
					return false;
				}else if(event.getAction() == MotionEvent.ACTION_DOWN){	
					Log.d(TAG, "forward button pressed");
					try {
						forwardButton.setBackgroundResource(R.drawable.arrow_up_touch);
						appModel.getScribbler().forward(.5);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
					Log.d(TAG, "forward button released");
					try {
						forwardButton.setBackgroundResource(R.drawable.arrow_up);
						appModel.getScribbler().stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
				break;
			case R.id.backward:
				if(event.getAction() == MotionEvent.ACTION_SCROLL){
					return false;
				}else if(event.getAction() == MotionEvent.ACTION_DOWN){	
					Log.d(TAG, "backward button pressed");
					try {
						backwardButton.setBackgroundResource(R.drawable.arrow_down_touch);
						appModel.getScribbler().backward(.5);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
					Log.d(TAG, "backward button released");
					try {
						backwardButton.setBackgroundResource(R.drawable.arrow_down);
						appModel.getScribbler().stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
				break;
			case R.id.stop:
				Log.d(TAG, "stop button clicked");
				if(event.getAction() == MotionEvent.ACTION_SCROLL){
					return false;
				}else if(event.getAction() == MotionEvent.ACTION_DOWN){	
					try{
						stopButton.setBackgroundResource(R.drawable.stop_touch);
						appModel.getScribbler().stop();
						allEnabled=false;
					}catch(Exception e){
						e.printStackTrace();
					}
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
					Log.d(TAG, "backward button released");
					try {
						stopButton.setBackgroundResource(R.drawable.stop);
						//appModel.getScribbler().stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				}
				
				break;
			}

		}else{
			Toast.makeText(getActivity(),"Not connected to any device...",
				     Toast.LENGTH_SHORT).show();
		}
		
		//v.performClick();
		return false;
	}
	

	/**
	 * Register the Sensor
	 */
	private void registerSensor(){
		sensorManager.registerListener(this, acceleroMeter, SensorManager.SENSOR_DELAY_NORMAL);
		tiltControl=true;
		allEnabled=true;
	}
	
	/**
	 * Unregister the Sensor
	 */
	private void unregisterSensor(){
		sensorManager.unregisterListener(this);
		tiltControl=false;
	}
	
	/**
	 * methods for the SensorEventListener Interface
	 * @param event
	 */
	float[] mGravity;
	float[] mGeomagnetic;
	
	private float round(float d, int decimalPlace){
		  BigDecimal bd = new BigDecimal(Float.toString(d));
	      bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	      return bd.floatValue();
	}
	
	@Override
	public void onSensorChanged(final SensorEvent event) {	
		// check sensor type
		if(allEnabled == true && event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){					
			// assign directions
			x = event.values[0];
			y = event.values[1];
			Log.d(TAG, "X: "+String.valueOf(x));
			Log.d(TAG, "Y: "+String.valueOf(y));
			// STOP: within 2 or -2 of any direction while its moving
			if(appModel.getScribbler().isMoving() &&
					appModel.getScribbler().isConnected() && 
					x > negThreshold &&
					x < threshold){
				
					appModel.getScribbler().move(0, 0);
					appModel.getScribbler().setMoving(false);
					
					if(upEnabled == true){
						upEnabled = false;
					}
					else if(downEnabled == true){
						downEnabled = false;
					}
					else if(rightEnabled == true){
						rightEnabled = false;
					}
					else if(leftEnabled == true){
						leftEnabled = false;
					}				
			}
			
			// Move the Scribbler BACKWARDS
	        if (appModel.getScribbler().isConnected() && 
	        		x < negThreshold &&
	        		 !(x > negThreshold)) {
	          Log.d(TAG, "Backwards Loop");
	          // describing the speed based on what x is 
	          if(x < -2.0 && x > -2.5){
	        	  new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().backward(0.2);
	      				return null;
	      				}      			
	      			}.execute();	      		
	        	  
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -2.6 && x > -3.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.3);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -3.1 && x > -3.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.4);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -3.6 && x > -4.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.5);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -4.1 && x > -4.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.6);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -4.6 && x > -5.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.7);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -5.1 && x > -5.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.8);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x < -5.6 && x > -6.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(0.9);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	          else if(x > -6.1){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().backward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().backward(1.0);
	        	  appModel.getScribbler().setMoving(true);
	        	  downEnabled = true;
	          }
	         }
		          // Move the Scribbler FORWARDS
	         else if (appModel.getScribbler().isConnected() &&
	        		 x > threshold &&
	        		 !(x < negThreshold)) {
	       	  
	       	  // describing the speed based on what x is 
	        	 Log.d(TAG, "Forward Loop");
	       	  
	       	  if(x > 2.0 && x < 2.5){
	       		 new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().forward(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	       		  
	       		  appModel.getScribbler().setMoving(true);
	        	  upEnabled = true;
	       	  }
	       	  else if(x > 2.6 && x < 3.0){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(0.3);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > 3.1 && x < 3.5){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(0.4);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > 3.6 && x < 4.0){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(0.5);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > 4.1 && x < 4.5){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(0.6);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > 4.6 && x < 5.0){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(0.7);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > 5.1 && x < 5.5){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(0.8);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > 5.6 && x < 6.0){	       		  
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	      			//appModel.getScribbler().forward(0.9);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	       	  else if(x > -6.1){
	       		new AsyncTask<Void, Void, Void>(){
	      			@Override
	      			protected Void doInBackground(Void... params) {	 
	      				appModel.getScribbler().forward(0.2);
	      				return null;
	      				}      			
	      			}.execute();
	       		  //appModel.getScribbler().forward(1.0);
	       		  appModel.getScribbler().setMoving(true);
	       		  upEnabled = true;
	       	  }
	         }
	        
	        // Move the Scribbler RIGHT
	        if (appModel.getScribbler().isConnected() && 
	        		y < negThreshold &&
	        		 !(y > negThreshold)) {
	        	Log.d(TAG, "Right Loop");
	          
	          // describing the speed based on what x is 
	          if(y > -2.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -2.6 && y > -3.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.3);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -3.1 && y > -3.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.4);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -3.6 && y > -4.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.5);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -4.1 && y > -4.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.6);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -4.6 && x > -5.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.7);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -5.1 && y > -5.5){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.8);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y < -5.6 && y > -6.0){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(0.9);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	          else if(y > -6.1){
	        	  new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnRight(0.2);
		      				return null;
		      				}      			
		      			}.execute();
	        	  //appModel.getScribbler().turnRight(1.0);
	        	  appModel.getScribbler().setMoving(true);
	        	  rightEnabled = true;
	          }
	         }
		          // Move the Scribbler LEFT
	         else if (appModel.getScribbler().isConnected() &&
	        		 y > threshold &&
	        		 !(y < negThreshold)) {
	       	  Log.d(TAG, "Left Loop");
	       	  // describing the speed based on what x is 
	       	  
	       	  if(y > 2.0 && y < 2.5){
	       		 new AsyncTask<Void, Void, Void>(){
		      			@Override
		      			protected Void doInBackground(Void... params) {	 
		      				appModel.getScribbler().turnLeft( 0.2);
		      				return null;
		      				}      			
		      			}.execute();
	       		  
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 2.6 && y < 3.0){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft( 0.3);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 3.1 && y < 3.5){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft( 0.4);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 3.6 && y < 4.0){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft( 0.5);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 4.1 && y < 4.5){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft(0.6);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 4.6 && y < 5.0){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft(0.7);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 5.1 && y < 5.5){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft(0.8);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 5.6 && y < 6.0){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft(0.9);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	       	  else if(y > 6.1){
		       		 new AsyncTask<Void, Void, Void>(){
			      			@Override
			      			protected Void doInBackground(Void... params) {	 
			      				appModel.getScribbler().turnLeft( 0.2);
			      				return null;
			      				}      			
			      			}.execute();
	       		  //appModel.getScribbler().turnLeft(1.0);
	       		  appModel.getScribbler().setMoving(true);
	        	  leftEnabled = true;
	       	  }
	         }
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(TAG, "onAccuracyChanged called");
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d(TAG, "Spinner item selected");
		// unregister sensor listener
		//sensorManager.unregisterListener(this);
		
		if(appModel.getScribbler()!=null && appModel.isConnected()){
			try {
				appModel.getScribbler().stop();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		mySpinner.setSelection(position);
		String selected=(String)mySpinner.getSelectedItem();
		if(selected.equals("Phone Tilt")){
			Log.d(TAG, "Phone tilt selected");
			sensorManager.registerListener(this, acceleroMeter, SensorManager.SENSOR_DELAY_NORMAL);
			tiltControl=true;
			allEnabled=true;
		}else if(selected.equals("Buttons")){
			Log.d(TAG, "Buttons selected");
			sensorManager.unregisterListener(this);
			tiltControl=false;
		}else if(selected.equals("Voice")){
			Log.d(TAG, "Voice command selected");
			sensorManager.unregisterListener(this);
			Intent i= new Intent(getActivity(), VoiceCommand.class);
			startActivity(i);
		}
		
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "Nothing selected");
		
	}

	public boolean isTiltControl() {
		return tiltControl;
	}

	public void setTiltControl(boolean tiltControl) {
		this.tiltControl = tiltControl;
	}

	
}

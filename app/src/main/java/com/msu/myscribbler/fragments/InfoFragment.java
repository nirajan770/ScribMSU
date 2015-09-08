package com.msu.myscribbler.fragments;

import java.util.HashMap;

import com.msu.myscribbler.R;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.utils.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class InfoFragment extends Fragment {

	private static final String TAG="InfoFragment";
	
	private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";
	
    private static final boolean D = true;

	private AppModel appModel;

	private TextView name, battery;
	private TextView obstacleLeft, obstacleCenter, obstacleRight;
	private TextView lightLeft, lightCenter, lightRight;
	private TextView irLeft, irRight;
	private TextView lineLeft, lineRight;
	private float batteryValue;
	private int[] obstacleValues, lightValues, irValues, lineValues;
	private String nameValue;

	private SharedPreferences settings;
	private Resources res;
	private Handler handler;

	private ToggleButton toggleButton;
	private Button manualButton;
	private Runnable r;

	private ImageView battery_level;
	// Must be instance variable to avoid garbage collection!
	private OnSharedPreferenceChangeListener preferenceListener;  
	
	/**
	* @return a new instance of {@link ContentFragment}, adding the parameters into a bundle and
	* setting them as arguments.
	*/
	public static InfoFragment newInstance(CharSequence title, int indicatorColor,
	                                              int dividerColor) {
		Bundle bundle = new Bundle();
	    bundle.putCharSequence(KEY_TITLE, title);
	    bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
	    bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);     

	    InfoFragment fragment = new InfoFragment();
	    fragment.setArguments(bundle);

	    return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Log.d(TAG, "on Create");
		appModel=(AppModel)getActivity().getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    res = getResources();
	    handler = new Handler();
	}


	public void retrieveAllValues(){
		Log.d(TAG, "retrieve values");
		HashMap<String, int[]> hm = appModel.getScribbler().getAll();
		lineValues = hm.get("LINE");
	    irValues = hm.get("IR");
	    lightValues = hm.get("LIGHT");
	    //nameValue = appModel.getScribbler().getDeviceName();
	    //batteryValue = appModel.getScribbler().getBattery();
	    obstacleValues = appModel.getScribbler().getObstacle("all");
	}



	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView= inflater.inflate(R.layout.info_fragment, container,false);
				
		// Initialize the textviews that will be updated when requested
	    name = (TextView) rootView.findViewById(R.id.name_value);
	    battery = (TextView) rootView.findViewById(R.id.battery_value);
	    obstacleLeft = (TextView) rootView.findViewById(R.id.obstacle_left_value);
	    obstacleCenter = (TextView) rootView.findViewById(R.id.obstacle_center_value);
	    obstacleRight = (TextView) rootView.findViewById(R.id.obstacle_right_value);
	    lightLeft = (TextView) rootView.findViewById(R.id.light_left_value);
	    lightCenter = (TextView) rootView.findViewById(R.id.light_center_value);
	    lightRight = (TextView) rootView.findViewById(R.id.light_right_value);
	    irRight = (TextView) rootView.findViewById(R.id.ir_right_value);
	    irLeft = (TextView) rootView.findViewById(R.id.ir_left_value);
	    lineLeft = (TextView) rootView.findViewById(R.id.line_left_value);
	    lineRight = (TextView) rootView.findViewById(R.id.line_right_value);
	    toggleButton = (ToggleButton) rootView.findViewById(R.id.toggleTimer);
	    manualButton = (Button) rootView.findViewById(R.id.button_manual_refresh);
	    battery_level = (ImageView) rootView.findViewById(R.id.battery_level_image);
	    
	    // set the correct control button based on previous preference
	    if (settings.getBoolean(res.getString(R.string.autoRefresh_pref), false)) {
	      toggleButton.setChecked(false);
	      toggleButton.setVisibility(View.VISIBLE);
	      manualButton.setVisibility(View.GONE);
	    } else {
	      toggleButton.setVisibility(View.GONE);
	      manualButton.setVisibility(View.VISIBLE);
	    }
	    // Listener that will change the control button as needed
	    preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
	      @Override
	      public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

	        if (key.equals(res.getString(R.string.autoRefresh_pref))) {
	          boolean auto = settings.getBoolean(res.getString(R.string.autoRefresh_pref), false);
	          
	          if (auto) {
	            toggleButton.setVisibility(View.VISIBLE);
	            manualButton.setVisibility(View.GONE);
	          } else {
	            toggleButton.setChecked(false);
	            toggleButton.setVisibility(View.GONE);
	            manualButton.setVisibility(View.VISIBLE);
	          }
	        }
	      }
	    };
	    settings.registerOnSharedPreferenceChangeListener(preferenceListener);

	    
	    r = new Runnable() {
	      @Override
	      public void run() {
	        // Update only of scribbler is connected
	        if (appModel.getScribbler().isConnected()) {
	          Log.i(TAG, "Populating Values");
	          updateValues();
	          populate();
	        } else {
	          Log.e(TAG, "Disconnected unexpectedly...");
	          toggleButton.setChecked(false);
	          Toast.makeText(getActivity(),"Not connected to any device...",
					     Toast.LENGTH_SHORT).show();
	        }
	        // Keep polling until user decides to stop or leaves activity
	        if (toggleButton.isChecked()) {
	          handler.postDelayed(
	              this,
	              Integer.parseInt(settings.getString(
	                  res.getString(R.string.refresh_rate_pref),
	                  res.getString(R.string.default_refresh_rate))));
	        }
	      }
	    };

	    toggleButton.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        if (appModel.getScribbler().isConnected()) {
	          if (toggleButton.isChecked())
	            handler.postDelayed(
	                r,
	                Integer.parseInt(settings.getString(
	                    res.getString(R.string.refresh_rate_pref),
	                    res.getString(R.string.default_refresh_rate))));
	        } else {
	          toggleButton.setChecked(false);
	        }
	            
	      }
	    });

	    // update information once
	    manualButton.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        if (appModel.getScribbler().isConnected()) 
	          r.run(); 
	        else
	        	 Toast.makeText(getActivity(),"Not connected to any device...",
					     Toast.LENGTH_SHORT).show();	        	
	      }
	    });
		
		return rootView;
	
		}

	/**
	* Function that properly updates variables containing information for the
	* sensors.
	*/
	private void updateValues() {
	    HashMap<String, int[]> hm = appModel.getScribbler().getAll();
	    lineValues = hm.get("LINE");
	    irValues = hm.get("IR");
	    lightValues = hm.get("LIGHT");
	    nameValue = appModel.getScribbler().getName();
	    batteryValue = appModel.getScribbler().getBattery();
	    obstacleValues = appModel.getScribbler().getObstacle("all");
	    //obstacleValues = appModel.getScribbler().getIR("all");
	  }
	  
	
	/**
	* Function that properly populates the UI with the current sensors values.
	*/
	private void populate() {
	    // Update Name
	    name.setText(nameValue);

	    // Update Battery
	    battery.setText(Float.toString(batteryValue));
	    if (batteryValue < 6.2) {
	    	battery.setTextColor(Color.RED);
	      	battery_level.setImageResource(R.drawable.battery_low);
	    
	    } else if (batteryValue >= 6.3 && batteryValue <= 6.8) {
	    	battery.setTextColor(Color.YELLOW);
	      	battery_level.setImageResource(R.drawable.battery_half);
	    
	    } else if(batteryValue >= 6.9 && batteryValue <= 7.4 ){
	    	battery.setTextColor(Color.YELLOW);
	    	battery_level.setImageResource(R.drawable.battery_3_4);
	    }
	   
	    else{
	    	battery.setTextColor(Color.GREEN);
	    	battery_level.setImageResource(R.drawable.battery_full);
	    }

	    // Update Obstacle Values
	    obstacleLeft.setText(Integer.toString(obstacleValues[0]));
	    Log.d(TAG, "obstacle left: "+String.valueOf(obstacleValues[0]));
	    obstacleCenter.setText(Integer.toString(obstacleValues[1]));
	    Log.d(TAG, "obstacle center: "+String.valueOf(obstacleValues[1]));
	    obstacleRight.setText(Integer.toString(obstacleValues[2]));
	    Log.d(TAG, "obstacle right: "+String.valueOf(obstacleValues[2]));

	    // Update Light Values
	    lightLeft.setText(Integer.toString(lightValues[0]));
	    lightCenter.setText(Integer.toString(lightValues[1]));
	    lightRight.setText(Integer.toString(lightValues[2]));

	    // Update IR Values
	    irLeft.setText(Integer.toString(irValues[0]));
	    irRight.setText(Integer.toString(irValues[1]));

	    // Update Line Values
	    lineLeft.setText(Integer.toString(lineValues[0]));
	    lineRight.setText(Integer.toString(lineValues[1]));
	  }  
	  
	
	
}

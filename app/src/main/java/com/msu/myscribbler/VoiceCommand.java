package com.msu.myscribbler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.utils.Constants;
import com.msu.myscribbler.utils.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceCommand extends ActionBarActivity{
	private static final int REQUEST_CODE = 1234;
	
	private TextView cmdsList;
	private ListView resultList;
	Button speakButton;
	
	private Toolbar toolbar;
	private AppModel appState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voice_command);
		
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
	    setSupportActionBar(toolbar);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		appState = (AppModel) getApplicationContext();

		// list of voice recognized commands
		String[] values= new String[]{Constants.VOICE_LEFT, Constants.VOICE_RIGHT,
				Constants.VOICE_FORWARD, Constants.VOICE_BACKWARD, Constants.BEEP};
		
		cmdsList= (TextView) findViewById(R.id.voice_cmds);
		cmdsList.setText(Arrays.toString(values).replaceAll("\\[|\\]", ""));
		
				
		speakButton = (Button) findViewById(R.id.speakButton);
		resultList = (ListView) findViewById(R.id.list);

		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
  
		if (activities.size() == 0) {
			
			speakButton.setEnabled(false);
			Toast.makeText(getApplicationContext(), "Recognizer Not Found", Toast.LENGTH_LONG).show();
		}
  
		speakButton.setOnClickListener(new View.OnClickListener() {
   
			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivity();
			}
		});
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
	

	/**
	 * Starts the intent which communicates with the Google servers
	 * to decipher the audio file just recorded. 
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
		startActivityForResult(intent, REQUEST_CODE);	
	}

	/**
	 * Once the startVoiceRecognitionActivity() has finished, this function
	 * takes all of the Strings (presented in an ArrayList) and then 
	 * figures out if the user said forward, backward, turn left, turn right,
	 * or beep. If one of those conditions are met, then the Scribbler performs 
	 * the action. If not, there is a Toast print with an error telling the user
	 * what was just said was unknown.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			resultList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, matches));
			
			for(int i = 0; i < matches.size(); i++){
				
				// FORWARD
				if(matches.get(i).trim().equalsIgnoreCase("Forward")){
					
					appState.getScribbler().forward(0.5);
					final Handler handler= new Handler();
					handler.postDelayed(new Runnable(){
						@Override
						public void run() {
							appState.getScribbler().stop();
						}			
					},500);
					
				
				}
				else if(matches.get(i).trim().equalsIgnoreCase("Backward")){
					
					appState.getScribbler().backward(0.5);
					final Handler handler= new Handler();
					handler.postDelayed(new Runnable(){
						@Override
						public void run() {
							appState.getScribbler().stop();
						}			
					},500);
					
				}
				else if(matches.get(i).trim().equalsIgnoreCase("Left")){
					
					appState.getScribbler().turnLeft(0.5);
					final Handler handler= new Handler();
					handler.postDelayed(new Runnable(){
						@Override
						public void run() {
							appState.getScribbler().stop();
						}			
					},500);
					
				}
				else if(matches.get(i).trim().equalsIgnoreCase("Right")){
					
					appState.getScribbler().turnRight(0.5);
					final Handler handler= new Handler();
					handler.postDelayed(new Runnable(){
						@Override
						public void run() {
							appState.getScribbler().stop();
						}			
					},500);
				}
				else if(matches.get(i).trim().equalsIgnoreCase("Beep")){
					
					appState.getScribbler().beep(800, 2);
				}				
			}
		}
		
	super.onActivityResult(requestCode, resultCode, data);
	
	}
	
	/**
	 * Used to help maintain clean activity across the app. If someone is connected 
	 * to the Scribbler, they should disconnect before leaving the app. This function
	 * ensures that happens.
	 */
	@Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
		  if (event.getAction() == KeyEvent.ACTION_DOWN) {
			  switch (event.getKeyCode()) {
			  case KeyEvent.KEYCODE_HOME: 
				  if(appState.getScribbler().isConnected()){
					  appState.getScribbler().disconnect();
					  Toast.makeText(getApplicationContext(), "Disconnected from Scribbler", Toast.LENGTH_LONG).show();
				  }
      	  return true;
			  }
		  }
		  return super.dispatchKeyEvent(event);  // let the default handling take care of it
    }
	
	
}

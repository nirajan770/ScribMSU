package com.msu.myscribbler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.msu.myscribbler.adapters.CommandItemAdapter;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.models.CommandItem;
import com.msu.myscribbler.utils.Constants;
import com.msu.myscribbler.utils.FileDialog;
import com.msu.myscribbler.utils.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class CommandsGUI extends ActionBarActivity{

	private static final String TAG="CommandsGUI";
	
	private Toolbar toolbar;
	private AppModel appModel;
	
	private ProgressDialog progressDialog;
	
	FileOutputStream outputStream;
	FileDialog fileDialog;
	private ListView commandsListView;
	private CommandItemAdapter myAdapter;
	private ArrayList<CommandItem> listItems;
	
	private Spinner commandSpinner, timeSpinner;
	private Button addButton;
	private Button executeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.commands_gui);	
		
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
	    setSupportActionBar(toolbar);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		appModel=(AppModel)getApplicationContext();
		timeSpinner=(Spinner)findViewById(R.id.time_spinner);
		addButton=(Button)findViewById(R.id.add_button);
		
		commandSpinner=(Spinner)findViewById(R.id.commands_spinner);
		//commandSpinner.setSelection(0, false);
		// Set up listener for commands spinner
		commandSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String itemSelected= parent.getItemAtPosition(position).toString();
				Log.d(TAG, "ITEM SELECTED: "+itemSelected);
				// Check if to display time spinner or not
				boolean flag= displayTimeSpinner(itemSelected);
				if(flag){
					timeSpinner.setVisibility(View.VISIBLE);
				}else
					timeSpinner.setVisibility(View.GONE);
				addButton.setVisibility(View.VISIBLE);
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {				
			}
		});
		
		
		
		commandsListView= (ListView)findViewById(R.id.commands_listview);
		listItems= new ArrayList<CommandItem>();
		myAdapter= new CommandItemAdapter(this, listItems);
		commandsListView.setAdapter(myAdapter);		
		commandsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommandItem clickedItem= (CommandItem) myAdapter.getItem(position);
				displayCommandDialog(clickedItem, position);
				return true;
			}		
		});
		
		
		
		addButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String selectedCommand=commandSpinner.getItemAtPosition(commandSpinner.getSelectedItemPosition()).toString();
				String selectedTime=timeSpinner.getItemAtPosition(timeSpinner.getSelectedItemPosition()).toString();
				Log.d(TAG, "SELECTED COMMAND: "+selectedCommand);
				Log.d(TAG, "SELECTED TIME: "+selectedTime);
				//boolean flag= displayTimeSpinner(selectedCommand);
				CommandItem cmd=null;
				cmd= new CommandItem(selectedCommand, selectedTime);
				/*if(flag){
					Log.d(TAG, "Creating item with time");
					cmd= new CommandItem(selectedCommand, selectedTime);
				}else{
					Log.d(TAG, "Creating item WITHOUT time");
					cmd= new CommandItem(selectedCommand);
				}*/
					
				//myAdapter.addItem(new CommandItem(selectedCommand, selectedTime));
				myAdapter.addItem(cmd);
				myAdapter.notifyDataSetChanged();
			}
		});
		
		executeButton=(Button)findViewById(R.id.execute_button);
		executeButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(appModel.getScribbler()!=null && appModel.isConnected()){	
					new AsyncTask<Void, Void, Void>(){
						@Override
						protected Void doInBackground(Void... params) {
							executeAllCommands();	
							return null;
						}					
					}.execute();
				}else{
					Toast.makeText(CommandsGUI.this,"Not connected to any device...",
							   Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
	}

	@Override
	protected void onResume() {		
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    MenuItem settingItem=menu.findItem(R.id.action_settings);
	    settingItem.setVisible(false);
	    MenuItem scanItem=menu.findItem(R.id.scan);
	    scanItem.setVisible(false);
	    MenuItem disconnectItem=menu.findItem(R.id.disconnect);
	    disconnectItem.setVisible(false);
	    MenuItem gui= menu.findItem(R.id.dashboard);
	    gui.setVisible(false);
	    MenuItem openItem= menu.findItem(R.id.open);
	    openItem.setVisible(true);
	    MenuItem saveItem= menu.findItem(R.id.save);
	    saveItem.setVisible(true);
	    
	    return true;
	  }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				return true;
	    
		
			case R.id.save:
				displayFilenameDialogBox();
				return true;
		
			case R.id.open:
				File mPath = new File(Environment.getExternalStorageDirectory() + "/MyScribbler/CommandsFile/");
	            fileDialog = new FileDialog(this, mPath);
	            fileDialog.setFileEndsWith(".txt");
	            fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
	                public void fileSelected(File file) {
	                    Log.d(getClass().getName(), "selected file " + file.toString());
	                    try {
							readFile(file.toString());
						} catch (IOException e) {
							
							e.printStackTrace();
						}
	                }
	            });
	            
	            fileDialog.showDialog();
	            return true;
		
			default:
				return super.onOptionsItemSelected(item);
		}		
	}
	
	
	private void readFile(String filename) throws IOException{
		InputStream instream = null; 
		listItems.clear();
		try {
			// open the file for reading
			instream= new FileInputStream(filename);

			// if file the available for reading
			if (instream != null) {
			  // prepare the file for reading
			  InputStreamReader inputreader = new InputStreamReader(instream);
			  BufferedReader buffreader = new BufferedReader(inputreader);

			  String line;

			  // read every line of the file into the line-variable, on line at the time
			  while ((line = buffreader.readLine()) != null){
				  Log.d(TAG, "READ FILE LINE: "+line);
				  String[] splited= line.split("\\s+");
				  Log.d(TAG, "FIRST "+splited[0]);
				  Log.d(TAG, "SECOND "+splited[1]);
				  Log.d(TAG, "THIRD "+splited[2]);
				  
				  //CommandItem item= new CommandItem(splited[0]+" "+splited[1], splited[2]);
				  // Each line consists of just command name, time, and "seconds"
				  CommandItem item= new CommandItem(splited[0], splited[1] +" "+splited[2]);
				  
				  listItems.add(item);
			  }
			}
				myAdapter.notifyDataSetChanged();
			} catch (Exception ex) {
			    // print stack trace.
			} finally {
			// close the file.
			instream.close();
			
			}
	}
	
	private void displayFilenameDialogBox(){
		 // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a filename");
		//builder.setMessage(title);
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		builder.setView(input);		
		builder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						Log.d(TAG, "File Name Entered");
						try{
							String filename=MainActivity.commandsFilePath+input.getText().toString()+".txt";
							//File file= new File(filename);
							outputStream = new FileOutputStream(new File(filename));
							//OutputStreamWriter outputWriter= new OutputStreamWriter(outputStream);
							ArrayList<CommandItem> allCommands= myAdapter.getAllItems();
							String separator = System.getProperty("line.separator");
							
							//FileWriter writer=new FileWriter(new File(filename),true);
							//BufferedWriter buf= new BufferedWriter(writer);
							BufferedWriter buf= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
							for(CommandItem c: allCommands){
								//outputWriter.write(c.getmCommand()+" "+c.getmTime());
								String data=c.getmCommand()+" "+c.getmTime();
								//outputStream.write(data.getBytes());
								buf.write(data);
								buf.newLine();
							}
							buf.close();
							//outputStream.flush();
							//outputStream.close();
							//outputWriter.close();
						}catch(Exception e){
							e.printStackTrace();
						}
						dialog.dismiss();						
					}

				});
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {                       
                	   dialog.dismiss();
                   }
               });
        // Create the AlertDialog object and return it
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(
        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		alert.show();
	}
	
	public void displayCommandDialog(CommandItem item, final int position){
		 AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		 String options[] = { "Remove Command", "Clear All"};
		 builder1.setTitle("Edit");
		 builder1.setCancelable(true)
		 		.setItems(options, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							//listItems.remove(position);
							myAdapter.removeItem(position);
							myAdapter.notifyDataSetChanged();
						}else if(which==1){
							// Ask for confirmation
							AlertDialog.Builder confirm= new AlertDialog.Builder(CommandsGUI.this);
							confirm.setTitle("Delete All Commands?");
							confirm.setCancelable(true);
							confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//listItems.clear();
									myAdapter.removeAll();
									myAdapter.notifyDataSetChanged();
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
							
						} // end of if clear conversation						
					}			 			
		 		});		
		 AlertDialog alert1= builder1.create();
		 alert1.show();
		 
	 }

	/**
	 * Method that executes all the commands selected
	 */
	private void executeAllCommands(){
		ArrayList<CommandItem>allCommands=myAdapter.getAllItems();
			//for(CommandItem item: allCommands){
			for(int i=0; i<allCommands.size(); i++){
				CommandItem item= allCommands.get(i);				
				//final double time=0.5;
				//final long setTime= Long.parseLong(item.getmTime());
				String[] time= item.getmTime().split(" ");
				String setTime= time[0];
				Log.d(TAG, "COMMAND "+item.getmCommand());
				//Log.d(TAG, "TIME MULTIPLY: "+String.valueOf(time));
				Log.d(TAG, "TIME "+String.valueOf(setTime));
				String command= item.getmCommand();
				try{					
					if(command.equals(Constants.GO_FORWARD)){	
						appModel.getScribbler().forward(0.5);						
					}else if(command.equals(Constants.GO_BACKWARD)){						
						appModel.getScribbler().backward(0.5);						  
					}else if(command.equals(Constants.TURN_LEFT)){
						appModel.getScribbler().turnLeft(0.5);  
					}else if(command.equals(Constants.TURN_RIGHT)){
						appModel.getScribbler().turnRight(0.5); 
					}else if(command.equals(Constants.TAKE_PHOTO)){
						Intent pictureIntent= new Intent(this, TakePicture.class);
						startActivity(pictureIntent);
					}else if(command.equals(Constants.STOP)){						
						appModel.getScribbler().stop();
					}else if(command.equals(Constants.BEEP))
						appModel.getScribbler().beep(784, Float.parseFloat(setTime));	
					else if(command.equals(Constants.DANCE))
						appModel.getScribbler().dance(setTime);
				
				}catch(Exception e){
					e.printStackTrace();
				}
				try {
					Log.d(TAG, "SLEEPING");
					if(setTime!=null && !setTime.isEmpty())
						if(!command.equals(Constants.DANCE))
							Thread.sleep(Integer.parseInt(setTime)*1000);
						else
							Thread.sleep(1000);
					else
						Thread.sleep(2000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}				
			}			
			// Always stop after executing
			try{
				appModel.getScribbler().stop();
			}catch(Exception e){
				e.printStackTrace();
			}		
	}
	
	/**
	 * Method to check whether or not to display Time spinner after command is selected
	 */
	private boolean displayTimeSpinner(String command){
		boolean result=false;
		if(command.equals(Constants.GO_FORWARD))
			result=true;
		else if(command.equals(Constants.GO_BACKWARD))
			result=true;
		else if(command.equals(Constants.TURN_LEFT))
			result=true;
		else if(command.equals(Constants.TURN_RIGHT))
			result=true;
		else if(command.equals(Constants.BEEP))
			result=true;
		else if(command.equals(Constants.DANCE))
			result=true;
		
		return result;			
	}
	
}

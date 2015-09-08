package com.msu.myscribbler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.msu.myscribbler.interfaces.AsyncResponse;
import com.msu.myscribbler.models.AppModel;
import com.msu.myscribbler.utils.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class TakePicture extends ActionBarActivity implements AsyncResponse{
	 private static final String TAG = "TakePicture";
	 

	 private Toolbar toolbar;
	 
	  private ImageView iv;

	  // taking picture resources
	  private ProgressBar pb;
	  private TextView loadingMessage;
	  private TextView errorMessage;

	  // save/cancel resources
	  private Button buttonSave;
	  private Button buttonCancel;

	  // Edit name resources
	  private TextView textViewPName;
	  private EditText editTextName;

	  TakePictureTask takePicture= new TakePictureTask();
	  private Bitmap image;

	  private AppModel appModel;

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.take_picture);
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
	    setSupportActionBar(toolbar);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	    appModel=(AppModel)this.getApplicationContext();
	    
	    
	    iv = (ImageView) findViewById(R.id.imageView_picture);
	    pb = (ProgressBar) findViewById(R.id.progressBar_picture);
	    loadingMessage = (TextView) findViewById(R.id.textView_takingPicture);
	    errorMessage= (TextView) findViewById(R.id.takingPicture_error);
	    buttonSave = (Button) findViewById(R.id.button_savePicture);
	    buttonCancel = (Button) findViewById(R.id.button_cancelPicture);
	    textViewPName = (TextView) findViewById(R.id.textView_pictureName);
	    editTextName = (EditText) findViewById(R.id.editText_pictureName);

	    // image is null until taken
	    image = null;

	    // implement cancel onClick
	    buttonCancel.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        finish();
	      }
	    });

	    // implement save onClick
	    buttonSave.setOnClickListener(new OnClickListener() {
	      @Override
	      public void onClick(View v) {
	        if (image != null) {
	          // save image to internal storage
	          try {
	            String fileName = editTextName.getText().toString() + ".jpg";
	            if(editTextName.getText().toString()!=null && !editTextName.getText().toString().isEmpty()){
	            	FileOutputStream fos= new FileOutputStream(MainActivity.imageFilePath+fileName);
		            BufferedOutputStream bos = new BufferedOutputStream(fos);

		            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
		            bos.flush();
		            fos.close();

		            // Let the user know picture saved successfully and
		            // finish
		            Toast.makeText(getBaseContext(), "Picture saved successfully", Toast.LENGTH_LONG)
		                .show();
		            Log.i(TAG, "Saved Picture");
		            Intent intent= new Intent();
		            setResult(Activity.RESULT_OK, intent);
		            finish();

	            }else{
	            	 Toast.makeText(getBaseContext(), "Please enter a file name", Toast.LENGTH_LONG)
		                .show();
	            }
	            
	          } catch (Exception e) {
	            Toast.makeText(getBaseContext(),"Picture could not be saved", Toast.LENGTH_LONG)
	                .show();
	           Log.e(TAG, e.getMessage());
	          }
	        } else {
	          Toast.makeText(getBaseContext(),"Picture could not be saved", Toast.LENGTH_LONG)
	              .show();
	          Log.e(TAG, "There does not seem to be an image to save");
	        }
	      }
	    });

	    takePicture.delegate=this;
	    if (appModel.getScribbler().isConnected()){
	    	pb.setVisibility(View.VISIBLE);
	    	loadingMessage.setVisibility(View.VISIBLE);
	    	takePicture.execute();
	    }else{
	    	errorMessage.setVisibility(View.VISIBLE);
	    	Toast.makeText(TakePicture.this,"Not connected to any device...",
				     Toast.LENGTH_SHORT).show();	
	    }
	    	
	    
	  }

	  public void onResume() {
	    super.onResume();	    
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
	  
	  
	  @Override
	  public void asyncReturn(Bitmap bitmap) {
		  image=bitmap;			
		}
	  
	  
	  /**
	   * AsyncTask to take picture.
	   */
	  private class TakePictureTask extends AsyncTask<Void, Void, Bitmap> {

		  public AsyncResponse delegate=null;
		  
	    protected Bitmap doInBackground(Void... params) {
	    	Log.d(TAG, "doInBackgroudn");
	    	if(appModel.getScribbler()!=null)
	    		Log.d(TAG, "scribbler get not null");
	    	else
	    		Log.d(TAG, "scribbler get null");
	    	Bitmap picture=appModel.getScribbler().takePicture(1280,800);
	    	//Bitmap picture= appModel.getScribbler().takePicture(256, 192);
	    	
	      return picture;
	    }

	    @Override
	    protected void onPreExecute() {
	      super.onPreExecute();

	      // Show the ProgressBar and associated text
	      pb.setVisibility(View.VISIBLE);
	      loadingMessage.setVisibility(View.VISIBLE);
	    }

	    protected void onPostExecute(Bitmap bm) {
	    	// Hide ProgressBar and associated text
	        pb.setVisibility(View.INVISIBLE);
	        loadingMessage.setVisibility(View.INVISIBLE);
	        
	    	if (bm != null) {
	    		Log.i(TAG, "Picture Success");
	 	        // Show image and picture options
	 	        iv.setVisibility(View.VISIBLE);
	 	        // set the image in the imageview
	 	        iv.setImageBitmap(bm);
	 	        
	 	        delegate.asyncReturn(bm);
	 	        // assign the newly created bitmap so we can save
	 	        image = bm;	        
	 	        buttonSave.setVisibility(View.VISIBLE);
	 	        buttonCancel.setVisibility(View.VISIBLE);
	 	        textViewPName.setVisibility(View.VISIBLE);
	 	        editTextName.setVisibility(View.VISIBLE);
	    	}else{
	    		Log.e(TAG, "Error taking picture...");
	    	}
	    }

	  }

	
	
	
	
	
}

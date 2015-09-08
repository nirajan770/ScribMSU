package com.msu.myscribbler;

import com.msu.myscribbler.utils.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class FullScreenActivity extends ActionBarActivity {
    private static final String TAG="FullScreenActivity";

    private Toolbar toolbar;

    private ImageView fullView;
    private ProgressBar spinner;
    
    DisplayImageOptions options;
    private int loadingImage=R.drawable.ic_launcher;
    
    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
	    setSupportActionBar(toolbar);		
		getSupportActionBar().setDisplayShowHomeEnabled(true);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
        fullView= (ImageView) findViewById(R.id.full_screen);
        spinner = (ProgressBar) findViewById(R.id.loading);
        
    	options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.img_default)
		.showImageForEmptyUri(R.drawable.img_default)
		.showImageOnFail(R.drawable.img_default)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

        Bundle extras= getIntent().getExtras();
       
        if(extras!=null){
            url= extras.getString("url");
            Log.d(TAG, "FullScreen URL " + url);
        }
        //imageLoader.DisplayImage(url, loadingImage, fullView);
        ImageLoader.getInstance()
		.displayImage("file://"+url, fullView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {	
				spinner.setVisibility(View.VISIBLE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				spinner.setVisibility(View.GONE);
			}
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				spinner.setVisibility(View.GONE);
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view,
					int current, int total) {				
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
     * Save the path to the image in case of retaining the state of Activity
     */
    @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {		
		super.onRestoreInstanceState(savedInstanceState);
		url=savedInstanceState.getString("image_path");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);		
		outState.putString("image_path", url);
	}

    
    
}

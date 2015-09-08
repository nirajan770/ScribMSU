package com.msu.myscribbler.fragments;

import java.io.File;
import java.util.ArrayList;

import com.msu.myscribbler.FullScreenActivity;
import com.msu.myscribbler.MainActivity;
import com.msu.myscribbler.R;
import com.msu.myscribbler.R.id;
import com.msu.myscribbler.R.layout;
import com.msu.myscribbler.interfaces.GalleryFragmentCallback;
import com.msu.myscribbler.utils.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryFragment extends Fragment implements GalleryFragmentCallback{

	public static final String TAG="GalleryFragment";
	
	private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";
    
    DisplayImageOptions options;
    
	File[] listFile;
	ArrayList<String> f = new ArrayList<String>();// list of file paths
	private ImageAdapter imageAdapter;
	
	/**
	* @return a new instance of {@link ContentFragment}, adding the parameters into a bundle and
	* setting them as arguments.
	*/
	public static GalleryFragment newInstance(CharSequence title, int indicatorColor,
	                                              int dividerColor) {
		Bundle bundle = new Bundle();
	    bundle.putCharSequence(KEY_TITLE, title);
	    bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
	    bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);     

	    GalleryFragment fragment = new GalleryFragment();
	    fragment.setArguments(bundle);

	    return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.img_default)
		.showImageForEmptyUri(R.drawable.img_default)
		.showImageOnFail(R.drawable.img_default)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}	
		
	@Override
	public void onResume() {	
		super.onResume();
		Log.d(TAG, "Resume");
		getFromSdcard();
		imageAdapter.notifyDataSetChanged();
	}

	public void getFromSdcard(){
	    File file= new File(MainActivity.imageFilePath);
	    f.clear();
	    if (file.isDirectory()){
	    	listFile = file.listFiles();
	        for (int i = 0; i < listFile.length; i++){
	        	f.add(listFile[i].getAbsolutePath());
	        }
	    }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView called");
		View rootView= inflater.inflate(R.layout.gallery_fragment, container,false);
		rootView.setTag(TAG);
		getFromSdcard();
		GridView imagegrid= (GridView) rootView.findViewById(R.id.grid);
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);
		
		imagegrid.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				return false;
			}
		});
		return rootView;
	
		}
	
	
	public class ImageAdapter extends BaseAdapter {
	    private LayoutInflater mInflater;

	    public ImageAdapter() {
	        //mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	mInflater= LayoutInflater.from(getActivity());
	    }

	    public int getCount() {
	        return f.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(final int position, View convertView, ViewGroup parent) {
	        ViewHolder holder;
	        if (convertView == null) {
	            holder = new ViewHolder();
	            convertView = mInflater.inflate(R.layout.galleryitem, null);
	            holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);

	            convertView.setTag(holder);
	        }
	        else {
	            holder = (ViewHolder) convertView.getTag();
	        }


	        //Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
	        //holder.imageview.setImageBitmap(myBitmap);
	        ImageLoader.getInstance()
			.displayImage("file://"+f.get(position), holder.imageview, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {	
					
				}
				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				
				}
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					
				}
			}, new ImageLoadingProgressListener() {
				@Override
				public void onProgressUpdate(String imageUri, View view,
						int current, int total) {				
				}
			});	
	        holder.imageview.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Image Clicked");
                    Intent fullScreenIntent = new Intent(getActivity(), FullScreenActivity.class);
                    fullScreenIntent.putExtra("url", f.get(position));
                    getActivity().startActivity(fullScreenIntent);					
				}
			});
	        
	        return convertView;
	    }
	}
	class ViewHolder {
	    ImageView imageview;
	}
	@Override
	public void updateData() {
		Log.d(TAG, "UPDATE DATA METHOD");
		getFromSdcard();
		imageAdapter.notifyDataSetChanged();
		
	}
	
	
}

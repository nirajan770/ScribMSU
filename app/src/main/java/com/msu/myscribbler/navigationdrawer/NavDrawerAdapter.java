package com.msu.myscribbler.navigationdrawer;

import java.util.List;
import com.msu.myscribbler.R;
import com.msu.myscribbler.interfaces.NavigationDrawerCallbacks;
import com.msu.myscribbler.utils.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

 

public class NavDrawerAdapter extends BaseAdapter {

	 private List<NavigationItem> mData;
	 private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
	 private int mSelectedPosition;
	 private int mTouchedPosition = -1;
	 
	 private Context mContext;	 
	 private static final String TAG= "NavAdapter";
	    
	    
	 public NavDrawerAdapter(Context context, List<NavigationItem> data) {
	    mData = data;
	    mContext=context;	   
	 }

	 public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
	    return mNavigationDrawerCallbacks;
	 }

	 public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
	    mNavigationDrawerCallbacks = navigationDrawerCallbacks;
	 }    

	@Override
	public int getCount() {
		return mData != null ? mData.size() : 0;
	}


	@Override
	public Object getItem(int position) {		
		return mData.get(position);
	}


	@Override
	public long getItemId(int position) {		 
		return mData.indexOf(getItem(position));
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View itemView = convertView;
		final NavigationItem rowItem= this.mData.get(position);
		
		if(itemView==null){
			DrawerItemHolder drawerHolder = new DrawerItemHolder();
			itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false);
			drawerHolder.title= (TextView) itemView.findViewById(R.id.title);
			itemView.setTag(drawerHolder);
		}
		
		DrawerItemHolder holder= (DrawerItemHolder) itemView.getTag();
		holder.title.setText(rowItem.getTitle());
	    	
    	/*holder.title.setOnClickListener(new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			Log.d(TAG, "CLICKED "+String.valueOf(position));
   			 	if(rowItem.isItem()){
   			 		if (mNavigationDrawerCallbacks != null)
   			 			mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(position, rowItem.getText());	 
   			 	}    			  			 
   		 	}
   	 	});*/
		
		return itemView;
	}

	private static class DrawerItemHolder{		
        public TextView title;
       
	}
	 
}

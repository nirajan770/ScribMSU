package com.msu.myscribbler.adapters;

import java.util.ArrayList;

import com.msu.myscribbler.R;
import com.msu.myscribbler.models.CommandItem;
import com.msu.myscribbler.utils.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommandItemAdapter extends BaseAdapter{
	private static final String TAG="CommandItemAdapter";
	
	ArrayList<CommandItem> myList=new ArrayList<CommandItem>();
	LayoutInflater inflater;
	Context context;
	
	public CommandItemAdapter(Context context, ArrayList<CommandItem> myList){
		this.myList=myList;
		this.context=context;
		inflater=LayoutInflater.from(this.context);
	}
	
	public void addItem(CommandItem item){
		myList.add(item);
	}

	public void removeItem(int position){
		myList.remove(position);
	}
	
	public void removeAll(){
		myList.clear();
	}
	
	public ArrayList<CommandItem> getAllItems(){
		return myList;
	}
	
	@Override
	public int getCount() {
		
		return myList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return myList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyViewHolder mViewHolder;
		if(convertView==null){
			convertView= inflater.inflate(R.layout.command_list_items, null);
			mViewHolder=new MyViewHolder();
			mViewHolder.commandView= (TextView)convertView.findViewById(R.id.commands_view);
			mViewHolder.timeView=(TextView)convertView.findViewById(R.id.time_view);
			convertView.setTag(mViewHolder);			
		}else{
			mViewHolder=(MyViewHolder)convertView.getTag();
			
		}
		CommandItem rowItem=  myList.get(position);
		if(rowItem!=null){
			mViewHolder.commandView.setText(rowItem.getmCommand());
			// check if the command item is associated with time
			if(rowItem.isHasTime()){
				Log.d(TAG, "ITEM HAS TIME");
				mViewHolder.timeView.setVisibility(View.VISIBLE);
				mViewHolder.timeView.setText(rowItem.getmTime());				
			}else{
				Log.d(TAG, "ITEM DOESN'T HAVE TIME");
				mViewHolder.timeView.setVisibility(View.GONE);
			}
				
		}
		return convertView;
	}

	
	private class MyViewHolder {
		TextView commandView;
		TextView timeView;
	}
	
	
}

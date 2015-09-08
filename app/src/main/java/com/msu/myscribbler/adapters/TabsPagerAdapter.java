package com.msu.myscribbler.adapters;

import com.msu.myscribbler.fragments.GalleryFragment;
import com.msu.myscribbler.fragments.HomeFragment;
import com.msu.myscribbler.fragments.InfoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	private static final int NUMBER_OF_TABS=3;
	
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		
	}

	@Override
	public Fragment getItem(int index) {
		switch(index){
		case 0:
			return new HomeFragment();
		
		case 1:
			return new InfoFragment();
			
		case 2:
			return new GalleryFragment();
			
			
		}
	
		return null;
	}

	@Override
	public int getCount() {
		
		return NUMBER_OF_TABS;
	}

	
	
	
}

package com.msu.myscribbler.navigationdrawer;

import android.graphics.drawable.Drawable;


public class NavigationItem {
    private String mText;
    private Drawable mDrawable;
    private boolean isProfile;
    private String title;
    private boolean isItem;
    private boolean isTitle;

    public boolean isTitle() {
		return isTitle;
	}

	public void setTitle(boolean isTitle) {
		this.isTitle = isTitle;
	}

	public NavigationItem(String text, Drawable drawable) {
        mText = text;
        mDrawable = drawable;
        isItem=true;
        isProfile=false;
        isTitle=false;
    }

    public NavigationItem(boolean isProfile) {
		this(null, null);
		this.isProfile = isProfile;
		isItem=false;
		isTitle=false;
	}    
    
	public NavigationItem(String title) {
		this(null, null);
		this.title = title;
		this.isProfile=false;
		this.isItem=false;
		this.isTitle=true;
	}   

	public boolean isItem() {
		return isItem;
	}

	public void setItem(boolean isItem) {
		this.isItem = isItem;
	}

	public boolean isProfile() {
		return isProfile;
	}

	public void setProfile(boolean isProfile) {
		this.isProfile = isProfile;
	}

	
    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}


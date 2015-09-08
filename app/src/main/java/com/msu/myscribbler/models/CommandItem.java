package com.msu.myscribbler.models;

public class CommandItem {

	private String mCommand;
	private String mTime;
	
	// flag to check if command has time associated
	private boolean hasTime=false;
	
	public CommandItem(){
		
	}
	
	public CommandItem(String command, String time){
		this.mCommand=command;
		this.mTime=time;
		this.hasTime=true;
	}
	
	public CommandItem(String command){
		this.mCommand=command;
		this.hasTime=false;
	}
	
	public String getmCommand() {
		return mCommand;
	}
	public void setmCommand(String mCommand) {
		this.mCommand = mCommand;
	}
	public String getmTime() {
		return mTime;
	}
	public void setmTime(String mTime) {
		this.mTime = mTime;
	}
	public boolean isHasTime() {
		return hasTime;
	}
	public void setHasTime(boolean hasTime) {
		this.hasTime = hasTime;
	}
	
	
}

/*package com.msu.myscribbler.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.msu.myscribbler.models.AppModel;

*//**
 * This class lists all the byte codes for the Scribbler robot
 * The byte code values are from the BrynMawr's Myro source code
 * http://web1.cs.brynmawr.edu/Scribbler_Wire_Protocol
 * 
 * @author Nirajan
 *//*

public class CommandsManager {

	public enum LED{
		ALL, 
		LEFT, 
		CENTER,
		RIGHT
	}
	
	
	private static final int SOFT_RESET 			= 33;
	private static final int GET_ALL				= 65;
	private static final int GET_ALL_BINARY		= 66;
	private static final int GET_LIGHT_LEFT		= 67;
	private static final int GET_LIGHT_CENTER	= 68;
	private static final int GET_LIGHT_RIGHT		= 69;
	private static final int GET_LIGHT_ALL		= 70;
	private static final int GET_IR_LEFT			= 71;
	private static final int GET_IR_RIGHT		= 72;
	private static final int GET_IR_ALL			= 73;
	private static final int GET_LINE_LEFT		= 74;
	private static final int GET_LINE_RIGHT		= 75;
	private static final int GET_LINE_ALL		= 76;
	private static final int GET_STATE			= 77;
	private static final int GET_NAME1			= 78;
	private static final int GET_NAME2			= 79;
	private static final int GET_STALL			= 79;
	private static final int GET_INFO			= 80;
	private static final int GET_DATA			= 81;
	private static final int GET_PASS1			= 50;
	private static final int GET_PASS2			= 51;
	
	private static final int GET_RLE				= 82;	// A segmented and run-length encoded image
	private static final int GET_IMAGE			= 83;	// The entire 256x192 image in YUYV format
	private static final int GET_WINDOW			= 84;	// The windowed image (followed by which window)
	private static final int GET_DONGLE_L_IR		= 85;	// Number of returned pulses when left emitter is turned on
	private static final int GET_DONGLE_C_IR		= 86;	// Number of returned pulses when center emitter is turned on
	private static final int GET_DONGLE_R_IR		= 87;	// Number of returned pulses when right emitter is turned on
	private static final int GET_WINDOW_LIGHT	= 88;	// Average intensity in the user defined region 
	private static final int GET_BATTERY			= 89;	// Battery Voltage
	private static final int GET_SERIAL_MEM		= 90;	// With the address returns the value in serial memory
	private static final int GET_SCRIB_PROGRAM 	= 91;	// With offset, returns the scribbler program buffer
	private static final int GET_CAM_PARAM		= 92;	// With address returns the camera parameter at that address
	
	private static final int GET_BLOB			= 95;
	
	private static final int SET_PASS1			= 55;
	private static final int SET_PASS2			= 56;
	
	private static final int SET_SINGLE_DATA		= 96;
	private static final int SET_DATA			= 97;
	private static final int SET_ECHO_MODE		= 98;
	
	private static final int SET_LED_LEFT_ON		= 99;
	private static final int SET_LED_LEFT_OFF	= 100;
	private static final int SET_LED_CENTER_ON	= 101;
	private static final int SET_LED_CENTER_OFF	= 102;
	private static final int SET_LED_RIGHT_ON	= 103;
	private static final int SET_LED_RIGHT_OFF	= 104;
	private static final int SET_LED_ALL_ON		= 105;
	private static final int SET_LED_ALL_OFF		= 106;
	private static final int SET_LED_ALL			= 107;
	private static final int SET_MOTORS_OFF		= 108;
	private static final int SET_MOTORS			= 109;
	private static final int SET_NAME1			= 110;
	private static final int SET_NAME2			= 119;
	private static final int SET_LOUD			= 111;
	private static final int SET_QUIET			= 112;
	private static final int SET_SPEAKER			= 113;
	private static final int SET_SPEAKER_2		= 114;
	
	private static final int SET_DONGLE_LED_ON	= 116;	// turn binary dongle off
	private static final int SET_DONGLE_LED_OFF	= 117;	// turn binary dongle led off
	private static final int SET_RLE				= 118;	// Set rlf parameters 
	private static final int SET_DONGLE_IR		= 120;	// Set Dongle IR Power
	private static final int SET_SERIAL_MEM		= 121;
	private static final int SET_SCRIB_PROGRAM	= 122;
	private static final int SET_START_PROGRAM	= 123;
	private static final int SET_RESET_SCRIBBLER = 124;
	private static final int SET_SERIAL_ERASE	= 125;
	private static final int SET_DIMMER_LED		= 126;
	private static final int SET_WINDOW			= 127;
	private static final int SET_FORWARDNESS		= 128;
	private static final int SET_WHITE_BALANCE	= 129;
	private static final int SET_NO_WHITE_BALANCE= 130;
	private static final int SET_CAM_PARAM		= 131;
	
	private static final int GET_JPEG_GRAY_HEADER= 135;
	private static final int GET_JPEG_COLOR_HEADER=137;
	private static final int GET_JPEG_COLOR_SCAN	= 138;
	private static final int SET_PASS_N_BYTES	= 139;
	
	private static final int MIN_FORWARD_VALUE 	= 100;
	private static final int MIN_REVERSE_VALUE 	= 99;
	private static final int STOP_VALUE 			= 100;
	
	private static final int PACKET_LENGTH = 9;
	private static final int SENSOR_PACKET_SIZE = 11;

	private Scribbler scribbler;
	private BluetoothSocket mSocket;
	private static final String TAG="CommandsManager";
	
	
	
	
	public CommandsManager(Scribbler scribbler){
		Log.d(TAG, "constructor");
		this.scribbler=scribbler;
		//mSocket=this.scribbler.getBluetoothSocket();
		//Log.d(TAG, "bluetooth socket "+mSocket.toString());
	}
	
	*//********************************
	 * SET COMMANDS FOR THE SCRIBBLER
	 * *******************************
	 *//*
	public void setCommand(byte[] values){
		byte[] resultBytes = null;

		//CommandThread workThread = null;
		try {
			//workThread = new CommandThread(scribbler.getBluetoothSocket());
			//workThread = new CommandThread(this.scribbler.getBluetoothSocket());
			//workThread.writeCommand(this.scribbler.getBluetoothSocket(), values);
			writeCommand(this.scribbler.getBluetoothSocket(), this.scribbler.isConnected(), values);
			Thread.sleep(200);
		}catch (InterruptedException e) {
	    	      e.printStackTrace();
	    }
	    resultBytes= readCommand(scribbler.getBluetoothSocket(),this.scribbler.isConnected(), PACKET_LENGTH);
	    resultBytes=readCommand(scribbler.getBluetoothSocket(),this.scribbler.isConnected(), SENSOR_PACKET_SIZE);
	    
	    scribbler.setLastSensors(resultBytes);
	    
	}
	
	*//**
	 * Method to set commands for movement of the robot
	 * @param translate
	 * @param direction
	 *//*
	public void move(double translate, double direction){
		// if the  movement is towards left
		double left = Math.min(Math.max(translate - direction, -1), 1);
		// if the movement is towards right
	    double right = Math.min(Math.max(translate + direction, -1), 1);
	    
	    byte[] values = { ((byte) SET_MOTORS), (byte) ((left + 1) * 100), (byte) ((right + 1) * 100)};
	    // set the command
	    setCommand(values);
	    
	}
	
	*//**
	 * Method to set command to stop the robot
	 *//*
	public void halt(){
		setCommand(new byte[] { (byte) SET_MOTORS_OFF });
	}
	
	*//**
	 * Methods to set speaker command
	 * @param frequency
	 * @param duration
	 *//*
	public void setFirstSpeaker(int frequency, int duration) {
	    byte[] values = { ((byte) SET_SPEAKER), (byte) (duration >> 8),
	        (byte) (duration % 256), (byte) (frequency >> 8), (byte) (frequency % 256) };

	    setCommand(values);
	  }
	public void setSecondSpeaker(int frequency1, int frequency2, int duration) {
	    byte[] values = { ((byte) SET_SPEAKER_2), (byte) (duration >> 8),
	        (byte) (duration % 256), (byte) (frequency1 >> 8), (byte) (frequency1 % 256),
	        (byte) (frequency2 >> 8), (byte) (frequency2 % 256) };

	    setCommand(values);
	  }

	*//**Method to set commands for the LED lights
	 * Enum representing all the LED lights on Scribbler
	 *//*
	public void setLED(LED position, boolean state){
		byte[] resultBytes;
	    switch (position) {
	    case ALL:
	    	resultBytes = state ? new byte[] { (byte) SET_LED_ALL_ON }
	          : new byte[] { (byte) SET_LED_ALL_OFF };
	      break;
	    case LEFT:
	    	resultBytes = state ? new byte[] { (byte) SET_LED_LEFT_ON }
	          : new byte[] { (byte) SET_LED_LEFT_OFF };
	      break;
	    case CENTER:
	    	resultBytes = state ? new byte[] { (byte) SET_LED_CENTER_ON }
	          : new byte[] { (byte) SET_LED_CENTER_OFF };
	      break;
	    case RIGHT:
	    	resultBytes = state ? new byte[] { (byte) SET_LED_RIGHT_ON }
	          : new byte[] { (byte) SET_LED_RIGHT_OFF };
	      break;
	    default:
	    	resultBytes = new byte[] { (byte) SET_LED_ALL_OFF };
	      break;
	    }
	    setCommand(resultBytes);
		
		
	}
	
	
	*//********************************
	 * GET COMMANDS FOR THE SCRIBBLER
	 * *******************************
	 *//*
	public int[] getCommand(byte[] inputBytes, int numBytes, String type){
		int[] ret;
	    byte[] tmp;
	    //CommandThread workThread = null;
	    try{
	    	//workThread= new CommandThread(this.scribbler.getBluetoothSocket());
	    	//workThread.writeCommand(this.scribbler.getBluetoothSocket(),inputBytes);
	    	writeCommand(this.scribbler.getBluetoothSocket(),this.scribbler.isConnected(),inputBytes);
	    	Thread.sleep(200);
	    }catch (InterruptedException e) {
  	      e.printStackTrace();
	    }
	    	    
	    // Read the Message Echo
	    
	    //tmp= workThread.readCommand(scribbler.getBluetoothSocket(), PACKET_LENGTH);
	    tmp=readCommand(scribbler.getBluetoothSocket(),this.scribbler.isConnected(), PACKET_LENGTH);
	    if (tmp == null){ 
	    	Log.d(TAG,"TEMP IS NULL");
	    Log.d(TAG, "Error Getting Command");
	    }

	    // Read contents of what's desired
	    //tmp= workThread.readCommand(scribbler.getBluetoothSocket(), numBytes);
	    tmp=readCommand(scribbler.getBluetoothSocket(),this.scribbler.isConnected(), numBytes);
	    // Convert the received bytes if needed
	    if (type.toLowerCase().equals("byte")) {
	      ret = new int[tmp.length];
	      for (int i = 0; i < ret.length; i++) {
	        ret[i] = tmp[i];
	      }
	    } else if (type.toLowerCase().equals("word")) {
	      int c = 0;
	      ret = new int[numBytes / 2];

	     Log.d(TAG, "GET[before word modify]: " + byteToString(tmp));

	      for (int i = 0; i < numBytes; i = i + 2) {
	        ret[c] = (tmp[i] & 0xFF) << 8 | tmp[i + 1] & 0xFF;
	        c++;
	      }
	    } else {
	    	Log.e(TAG, "Cannot _get type: " + type);
	      ret = new int[] {};
	    }

	    Log.d(TAG, "GET: " + integerToString(ret));
	    return ret;
		
	}
	*//**
	 * Method to get all the sensor values
	 *//*
	public int[] getAll() {
	    int[] temp;
	    int numBytes = 11;
	    int[] values = new int[8];

	    // Get the Raw Bytes
	    temp = getCommand(new byte[] { (byte) GET_ALL }, numBytes, "byte");
	    if (temp == null) return null;
	    
	    // IR Values
	    values[0] = temp[0];
	    values[1] = temp[1];

	    // Light Values
	    values[2] = (temp[2] & 0xFF) << 8 | temp[3] & 0xFF;
	    values[3] = (temp[4] & 0xFF) << 8 | temp[5] & 0xFF;
	    values[4] = (temp[6] & 0xFF) << 8 | temp[7] & 0xFF;

	    // Line Values
	    values[5] = temp[8];
	    values[6] = temp[9];

	    // Stall Value
	    values[7] = temp[10];

	    return values;
	  }
	
	public byte[] getBattery() {
	    byte[] batteryByte = null;
	    int retSize = 2;
	   // CommandThread workThread = null;
	    try{
	    	//workThread= new CommandThread(this.scribbler.getBluetoothSocket());
	    	//workThread.writeCommand(this.scribbler.getBluetoothSocket(), new byte[] { (byte) GET_BATTERY });
	    	writeCommand(this.scribbler.getBluetoothSocket(),this.scribbler.isConnected(), new byte[] { (byte) GET_BATTERY });
	    	Thread.sleep(200);
	    	//batteryByte=workThread.readCommand(this.scribbler.getBluetoothSocket(), retSize);
	    	readCommand(this.scribbler.getBluetoothSocket(),this.scribbler.isConnected(), retSize);
	    }catch (InterruptedException e) {
  	      e.printStackTrace();
	    }
	    
	    Log.i(TAG, "Battery Done");
	    return batteryByte;

	  }

	@SuppressWarnings("null")
	public int[] getObstacle() {
		Log.d(TAG, "GET OBSTACLE");
		// to store the obstacle sensor values returned
		
	    int[] obstacleValues = new int[3];
	    int[] obstacleCodes = { GET_DONGLE_L_IR, GET_DONGLE_C_IR, GET_DONGLE_R_IR };
	    int retSize = 2;
	    byte[] temp = null;
	    //CommandThread workThread = null;
	    try{
	    	for (int i = 0; i < obstacleCodes.length; i++) {
	    		
	    		writeFluke(this.scribbler.getBluetoothSocket(),this.scribbler.isConnected(), new byte[] { (byte) obstacleCodes[i] });
	    		
	    		temp=readCommand(this.scribbler.getBluetoothSocket(), this.scribbler.isConnected(), retSize);
	    		obstacleValues[i] = temp[0] << 8 & 0xFF | temp[1] & 0xFF;
	    		Log.d(TAG, "Obstacle Values: "+String.valueOf(obstacleValues[i]));
	    	}
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return obstacleValues;
	  }
	
	public byte[] getPictureArray() {
		    byte[] imageByte = null;

		    int width = 256;
		    int height = 192;
		    int size = width * height;
		    //CommandThread workThread = null;
		    try{
		    	//workThread=new CommandThread(this.scribbler.getBluetoothSocket());
		    	//workThread.writeFluke(this.scribbler.getBluetoothSocket(), new byte[] { (byte) GET_IMAGE });
		    	writeFluke(this.scribbler.getBluetoothSocket(),this.scribbler.isConnected(), new byte[] {(byte)(83&0xff)});
		    	Thread.sleep(200);
		    	//imageByte=workThread.readCommand(this.scribbler.getBluetoothSocket(), size);
		    	imageByte=readCommand(this.scribbler.getBluetoothSocket(),this.scribbler.isConnected(), size);
		    }catch(InterruptedException e){
		    	e.printStackTrace();
		    }
		    Log.d(TAG, "Image Received");
		    
		    return imageByte;
		  }

	public int[] getIR() {
	    int[] irByte = null;
	    int numBytes = 2;

	    irByte = getCommand(new byte[] { (byte) GET_IR_ALL }, numBytes, "byte");

	    Log.i(TAG, "IR Done");
	    return irByte;
	  }
	
	public int[] getLight() {
		int[] lightByte = null;
		int numBytes = 6;

		lightByte = getCommand(new byte[] { (byte) GET_LIGHT_ALL }, numBytes, "word");

		Log.i(TAG, "LIGHT Done");
		return lightByte;
	 }
	
	
		
		*//**
		 * Method to write commands to Scribbler
		 * returns 9 byte echo and 11 bytes of sensor data
		 * @param values
		 *//*
		public static void writeCommand(BluetoothSocket sock, boolean connected, byte[] values){
			// Ensure correctly-sized packet
		    if(connected){  
		      OutputStream out=null;
		      try{
		    	  out=sock.getOutputStream();
		    	  
		      }catch(IOException e){
		    	  e.printStackTrace();
		    	  Log.d(TAG, "Error Write Command getting output stream");
		      }
		      ByteBuffer b = ByteBuffer.allocate(PACKET_LENGTH).put(values);
		      while (b.position() < b.limit()) {
		        b.put((byte) 0);
		      }
		      
		      try {
		    	 // Thread.sleep(100);		    	  
		          out.write(b.array());
		         
		        } catch (IOException e) {
		        Log.e(TAG, "Error Writing Output command ");
		        Log.e(TAG, "Error Writing "+byteToString(b.array()));
		        } 
		    }    
		}
		
		public static void writeFluke(BluetoothSocket sock, boolean connected, byte[] values) {
		    if(connected){
		      
		      OutputStream out=null;
		      try {
		    	out=sock.getOutputStream();		       
		      } catch (IOException e) {
		       Log.e(TAG, "Error Writing Fluke Command ");
		      }
		      // Only write what the code-- no message size specified
		      ByteBuffer b = ByteBuffer.allocate(values.length).put(values);
		      try{
		    	  //out.write(b.array());
		    	  out.write(values);
		      }catch(IOException e){
		    	  e.printStackTrace();
		    	  Log.d(TAG, "Error writing fluke command 2");
		      }
		      Log.d(TAG, "ByteBuffer length: "+String.valueOf(b.capacity()));
		     		    	
		    	
		    } 
		 }
		
		public static byte[] readCommand(BluetoothSocket sock,boolean connected, int numBytes) {
			ByteBuffer buf = ByteBuffer.allocate(numBytes);
			byte[] byteResult=null;
			if(connected){
			
		    InputStream in=null;
		    try {
		        in = sock.getInputStream();
		      } catch (IOException e1) {
		        e1.printStackTrace();
		      }
		    
		      byte[] fake = new byte[numBytes];
		      for (int i = 0; i < numBytes; i++)
		        fake[i] = 0;

		      byte[] buffer = new byte[numBytes];
		      int read = 0;
		      
		      try {
		        while (buf.hasRemaining()) {
		        	
		          read = in.read(buffer);
		          Log.d(TAG, "INT READ: "+String.valueOf(read));
		          for (int i = 0; i < read; i++) {
		            int b = buffer[i] & 0xff;
		            try {
		            	buf.put((byte) b);
		            	} catch (BufferOverflowException be) {
		            		// Fix bug where robot sends too much information back
		            		//if (D) Log.e(TAG, be.getMessage().toString());
		            		be.printStackTrace();
		            		Log.e(TAG, "Trying to gracefully disconnect rather than crash");
		            		return null;
		            		}
		          	}
		        	}
		        	Log.d(TAG, "Read " + buf.position() + " bytes: ");
		      } catch (IOException e) {
		        Log.e(TAG, "Error Reading" + e.getMessage());
		      }
			
			int available=0;
			int bytesRead=0;
			InputStream in=null;
			//int [] output=new int[numBytes];
			int [] output=null;
			ByteBuffer byteBuffer=null;
			
		    try {
		        in = sock.getInputStream();
		      
		        available=in.available();
		        output=new int[available];
		        Log.d(TAG, "AVAILABLE INT"+String.valueOf(available));
		        while(available>0 && bytesRead<=numBytes ){
		        	for(int i=0; i<available; i++){
		        		output[bytesRead]=in.read();
		        		bytesRead++;
		        	}
		        	Thread.sleep(5);
		        	available=in.available();
		        }
		        		        
		        byteBuffer=ByteBuffer.allocate(output.length*4);	
		        IntBuffer intBuffer=byteBuffer.asIntBuffer();
		        intBuffer.put(output);
		        byteResult=byteBuffer.array();
		    } catch (IOException e1) {
		        e1.printStackTrace();
		    } catch (InterruptedException e) {
				e.printStackTrace();
			}
				
		    }
			Log.d(TAG, "ByteToString "+ byteToString(buf.array()));
			return buf.array();
			
			//return byteResult;
		  
		}	
	
	
	*//**
	 * Method to convert byte to String for logging purposes
	 * @param ba
	 * @return
	 *//*
	 private static String byteToString(byte[] bytes) {
		    StringBuilder sb = new StringBuilder("[ ");
		    for (byte b : bytes) {
		      sb.append(Integer.toHexString(b & 0xff)).append(" ");
		    }
		    sb.append("]");
		    return sb.toString();
		  }
	 private static String integerToString(int[] ba) {
		    StringBuilder sb = new StringBuilder("[ ");
		    for (int b : ba) {
		      sb.append(Integer.toHexString(b)).append(" ");
		    }
		    sb.append("]");
		    return sb.toString();
		  }
}
*/
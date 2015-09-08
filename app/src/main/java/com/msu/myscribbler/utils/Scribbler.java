/*package com.msu.myscribbler.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import com.msu.myscribbler.models.CommandsInterface;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;


*//**
 * This class represents a Bluetooth enabled/connected device
 * For the context of this app, it is the Scribbler robot
 * @author Nirajan
 *
 *//*
public class Scribbler implements CommandsInterface{

	private static final String TAG="Scribbler";
	
	private String deviceName;
	private String deviceAddress;
	private boolean connected;
	private byte[] lastSensors;
	private BluetoothSocket bluetoothSocket;
	
	private CommandsManager commandsManager;
	
	*//** ImageProcessing **//*
	private int[] originalImageData;
	private int[] outputImageData;
	
	*//** default constructor **//*
	public Scribbler(){
		this(null);
	}
	
	public Scribbler(String deviceAddress){
		this.deviceAddress=deviceAddress;
		this.deviceName="";
		// set the connection state to false when instantiating the object
		setConnected(false);
		bluetoothSocket=null;
		commandsManager=null;
	}

	*//**
	 * getters and setters
	 * @return
	 *//*
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}
	
	public BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
		this.bluetoothSocket = bluetoothSocket;
	}
	

	public byte[] getLastSensors() {
		return lastSensors;
	}

	public void setLastSensors(byte[] lastSensors) {
		this.lastSensors = lastSensors;
	}
	
	

	*//**
	 * Method to connect to the device
	 *//*
	public boolean connectToDevice(){
		boolean result=false;
		
		try {
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.deviceAddress);
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			bluetoothSocket=(BluetoothSocket) m.invoke(device, Integer.valueOf(1));
			// try connecting
			bluetoothSocket.connect();
			setConnected(true);
			commandsManager= new CommandsManager(this);
			result=true;
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			setConnected(false);
			result=false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			setConnected(false);
			result=false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			setConnected(false);
			result=false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			setConnected(false);
			result=false;
		} catch (IOException e) {
			e.printStackTrace();
			setConnected(false);
			result=false;			
		}
			
		return result;
		
	}
	
	*//**
	 * Method to disconnect from the device
	 * @param deviceAddress
	 * @return
	 *//*
	public void disconnectFromDevice(String deviceAddress){
		try{
			if(bluetoothSocket!=null){
				Log.d(TAG, "Bluetooth socket not null");
				bluetoothSocket.close();
				setConnected(false);
			}
		}catch(IOException e){
			e.printStackTrace();
			Log.d(TAG, "Error closing bluetooth connection");
		}
		
	}

	
	*//**
	 * Methods to call set/get commands to move the robot
	 *//*
	@Override
	public void forward(double amount) throws Exception {
		Log.d(TAG, "Forward");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(amount, 0);	// direction value 0 to move forward
		}
		// to stop the scribbler after a second
		final Handler handler= new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				Log.d(TAG, "forward halt called");
				commandsManager.halt();				
			}			
		},500);
		
	}
	
	@Override
	public void backward(double amount) throws Exception {
		Log.d(TAG, "Backward");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(-amount, 0);	// direction value 0 to move forward, negative amount to move backwards
		}
		// to stop the scribbler after a second
				final Handler handler= new Handler();
				handler.postDelayed(new Runnable(){
					@Override
					public void run() {
						Log.d(TAG, "forward halt called");
						commandsManager.halt();				
					}			
				},500);
	}

	@Override
	public void halt() throws Exception {
		Log.d(TAG, "Halt");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.halt();
		}
		
		
	}

	@Override
	public void turnLeft(double amount) throws Exception {
		Log.d(TAG, "Turn Left");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(0, -amount);	
		}
		// to stop the scribbler after a second
				final Handler handler= new Handler();
				handler.postDelayed(new Runnable(){
					@Override
					public void run() {
						Log.d(TAG, "forward halt called");
						commandsManager.halt();				
					}			
				},500);
		
	}

	@Override
	public void turnRight(double amount) throws Exception {
		Log.d(TAG, "Turn Right");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(0, amount);	
		}
		// to stop the scribbler after a second
				final Handler handler= new Handler();
				handler.postDelayed(new Runnable(){
					@Override
					public void run() {
						Log.d(TAG, "forward halt called");
						commandsManager.halt();				
					}			
				},500);
		
	}

	@Override
	public void turnAround() throws Exception {
		
		
	}

	@Override
	public void setBeep(float frequency, float duration) {
		Log.d(TAG, "Beep called");
		if (commandsManager != null){
		    Log.d(TAG, "commands manager not null");
		    commandsManager.setFirstSpeaker((int)frequency,(int)(duration*1000));	      
		}
		
	}

	*//** Method to call set/get methods to keep robot moving continuously **//*
	@Override
	public void keepForward(double amount) throws Exception {
		Log.d(TAG, "Keep Forward");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(amount, 0);	// direction value 0 to move forward
		}
		
	}

	@Override
	public void keepBackward(double amount) throws Exception {
		Log.d(TAG, "Keep Backward");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(-amount, 0);	// direction value 0 to move forward, negative amount to move backwards
		}
		
	}

	@Override
	public void keepLeft(double amount) throws Exception {
		Log.d(TAG, "Keep Turning Left");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(0, -amount);	
		}
		
	}

	@Override
	public void keepRight(double amount) throws Exception {
		Log.d(TAG, "Keep Turning Right");
		if(commandsManager!=null){
			 Log.d(TAG, "commands manager not null");
			commandsManager.move(0, amount);	
		}
		
	}
	
	
	public float getBattery() {
	    byte[] ba = null;
	    int unmodified=0;
	    float value = 0;
	    if (commandsManager != null) {
	      ba = commandsManager.getBattery();
	        unmodified = (ba[0] & 0xFF) << 8 | ba[1] & 0xFF;
	        value = unmodified / 20.9813f;

	      
	    }
	    return value;
	  }
	
	public int[] getObstacle(String type) {
	    int[] ba, ret = null;

	    type = type.toLowerCase();
	    if (commandsManager != null) {
	      ba = commandsManager.getObstacle();
	      
	      if (type.equals("left")) {
	        ret = new int[1];
	        ret[0] = ba[0];
	      } else if (type.equals("center")) {
	        ret = new int[1];
	        ret[0] = ba[1];
	      } else if (type.equals("right")) {
	        ret = new int[1];
	        ret[0] = ba[2];
	      } else {
	        ret = ba;
	      }
	    }
	    return ret;
	  }	
	
	*//*** Method to get all the sensor values **//*
	public HashMap<String, int[]> getAll() {
		 HashMap<String, int[]> hashMap = new HashMap<String, int[]>();
		 int[] values;

		 if (commandsManager != null) {
		      values = commandsManager.getAll();
		      hashMap.put("IR", new int[] { values[0], values[1] });
		      hashMap.put("LIGHT", new int[] { values[2], values[3], values[4] });
		      hashMap.put("LINE", new int[] { values[5], values[6] });
		      hashMap.put("STALL", new int[] { values[7] });
		    }
		    return hashMap;
		 
		 
	 }
	
	
	*//** TAKE PICTURE **//*
	public Bitmap takePicture() {
	    Bitmap bm = null;
	    byte[] ba;

	    if (commandsManager != null) {
	      ba = commandsManager.getPictureArray();
	      IntBuffer intBuf=ByteBuffer.wrap(ba).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
	      originalImageData=new int[intBuf.remaining()];
	      intBuf.get(originalImageData);
	      outputImageData=new int[256*192*3];
	      YUVToRGB();
	      
	      bm = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888);
		  Bitmap r = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888);
		  int color, currentLocation;
			for(int i = 0; i < 192; i++)
			{
				for(int j = 0; j < 256; j++)
				{					
					currentLocation = (i * 256 + j) * 3;
					
					// set bitmap image data to our image data
					color = Color.argb(	255, 									// A
										outputImageData[currentLocation], 		// R
										outputImageData[currentLocation + 1], 	// G
										outputImageData[currentLocation + 2]); 	// B
					bm.setPixel(j, i, color);
				}
			}
		  
	     bm = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888);
	     //bm=BitmapFactory.decodeByteArray(ba, 0, ba.length);
	      
	     int w = 256;
	      int h = 192;
	      int vy, vu, y1v, y1u, uy, uv, y2u, y2v;
	      int V = 0, Y = 0, U = 0;

	      for (int i = 0; i < h; i++) {
	        for (int j = 0; j < w; j++) {
	          if (j >= 3) {
	            vy = -1;
	            vu = 2;
	            y1v = -1;
	            y1u = -3;
	            uy = -1;
	            uv = -2;
	            y2u = -1;
	            y2v = -3;
	          } else {
	            vy = 1;
	            vu = 2;
	            y1v = 3;
	            y1u = 1;
	            uy = 1;
	            uv = 2;
	            y2u = 3;
	            y2v = 1;
	          }
	          if ((j % 4) == 0) {
	            V = ba[i * w + j] & 0xff;
	            Y = ba[i * w + j + vy] & 0xff;
	            U = ba[i * w + j + vu] & 0xff;
	          } else if ((j % 4) == 1) {
	            Y = ba[i * w + j] & 0xff;
	            V = ba[i * w + j + y1v] & 0xff;
	            U = ba[i * w + j + y1u] & 0xff;
	          } else if ((j % 4) == 2) {
	            U = ba[i * w + j] & 0xff;
	            Y = ba[i * w + j + uy] & 0xff;
	            V = ba[i * w + j + uv] & 0xff;
	          } else if ((j % 4) == 3) {
	            Y = ba[i * w + j] & 0xff;
	            U = ba[i * w + j + y2u] & 0xff;
	            V = ba[i * w + j + y2v] & 0xff;
	          }
	          U = U - 128;
	          V = V - 128;
	          // Y = Y;

	          bm.setPixel(j, i, Color.rgb((int) Math.max(Math.min(Y + 1.13983 * V, 255), 0),
	              (int) Math.max(Math.min(Y - 0.39466 * U - 0.58060 * V, 255), 0),
	              (int) Math.max(Math.min(Y + 2.03211 * U, 255), 0)));
	        }
	      }
	    
	    
	    }

	    return bm;
	  }

	private void YUVToRGB()
	{
		int y = 0, u = 0, v = 0, R, G, B, current;
		int vy, vu, y1v, y1u, uy, uv, y2u, y2v;
		int mask = 0xFF;
		 
		for(int i = 0; i < 192; i++)
		{
			for(int j = 0; j < 256; j++)
			{
				if(j >= 3)
				{
					vy = -1; vu = -2; y1v = -1; y1u = -3; uy = -1; uv = -2; y2u = -1; y2v = -3;
				}
				else
				{
					vy = 1; vu = 2; y1v = 3; y1u = 1; uy = 1; uv = 2; y2u = 3; y2v = 1;
				}
				
				if((j % 4) == 0)
				{
					v = originalImageData[i * 256 + j]      & mask;
					y = originalImageData[i * 256 + j + vy] & mask;
					u = originalImageData[i * 256 + j + vu] & mask;
				}
				else if((j % 4) == 1)
				{
					y = originalImageData[i * 256 + j]       & mask;
					v = originalImageData[i * 256 + j + y1v] & mask;
					u = originalImageData[i * 256 + j + y1u] & mask;
				}
				else if((j % 4) == 2)
				{
					u = originalImageData[i * 256 + j]      & mask;
					y = originalImageData[i * 256 + j + uy] & mask;
					v = originalImageData[i * 256 + j + uv] & mask;
				}
				else if((j % 4) == 3)
				{
					y = originalImageData[i * 256 + j]       & mask;
					u = originalImageData[i * 256 + j + y2u] & mask;
					v = originalImageData[i * 256 + j + y2v] & mask;
				}
				
				// 0x80 = 128
				u = u - 0x80; // masking these makes the colors go all pink
				v = v - 0x80;
				
				R = Math.max( Math.min( (int)(y + 1.13983 * v),               255 ), 0 ) & mask;
				G = Math.max( Math.min( (int)(y - 0.39466 * u - 0.58060 * v), 255 ), 0 ) & mask;
				B = Math.max( Math.min( (int)(y + 2.03211 * u),               255 ), 0 ) & mask;
				
				current = (i * 256 + j) * 3;
				
				outputImageData[current]     = R;
				outputImageData[current + 1] = G;
				outputImageData[current + 2] = B;
			}
		}
	}





}
*/
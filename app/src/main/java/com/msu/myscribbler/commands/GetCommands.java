/*
 * Code taken from project: https://github.com/sahhhm/Scribdroid
 */

package com.msu.myscribbler.commands;

import com.msu.myscribbler.utils.Log;



public class GetCommands {

  private static final String TAG = "GetCommands";
  private static final boolean D = true;

  private Scribbler s;

  private static final int PACKET_LENGTH = 9;

  private static final int GET_ALL = 65;
  private static final int GET_LIGHT_ALL = 70;
  private static final int GET_IR_ALL = 73;
  private static final int GET_NAME1 = 78;
  private static final int GET_NAME2 = 64;
  private static final int GET_IMAGE = 83;
  private static final int GET_DONGLE_L_IR = 85;
  private static final int GET_DONGLE_C_IR = 86;
  private static final int GET_DONGLE_R_IR = 87;
  private static final int GET_BATTERY = 89;

  public GetCommands(Scribbler aScrib) {
    s = aScrib;
  }

  public byte[] getPictureArray() {
    byte[] line;

    int width = 1280;
    int height = 800;
    int size = width * height;

    ReadWrite
        ._writeFluke(s.getSocket(), s.isConnected(), new byte[] { (byte) GET_IMAGE });

    /** READS THE INPUTSTREAM TO GET IMAGE BYTES **/
    line = ReadWrite._read(s.getSocket(), s.isConnected(), size);
    //line = ReadWrite._readImage(s.getSocket(), s.isConnected(), size);
    if (D) Log.d(TAG, "Finished Reading--getArray");

    return line;
  }

  /**
   * Function which returns all the scribbler sensors (ir, light, line, stall)
   * 
   * @return an int[] of size 8. idx[0,1]: ir left, ir right... idx[2,3,4]:
   *         light left, light center, light right... idx[5,6]: line left, line
   *         right... idx[7]: stall
   */
  public int[] getAll() {
    int[] temp;
    int numBytes = 11;
    int[] values = new int[8];

    // Get the Raw Bytes
    temp = _get(new byte[] { (byte) GET_ALL }, numBytes, "byte");
    if (temp == null) return null;
    Log.d(TAG, "All Values: "+ int2str(temp));
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

  /**
   * 
   * @return - byte[] consisting of the 16 bytes that make up the robot name
   */
  public int[] getName() {
    int[] ba, ba1, ba2;
    int retSize = 8;

    // Get both halves of the name
    ba1 = _get(new byte[] { (byte) GET_NAME1 }, retSize, "byte");
    ba2 = _get(new byte[] { (byte) GET_NAME2 }, retSize, "byte");

    // Combine both halves of the name
    ba = new int[ba1.length + ba2.length];
    System.arraycopy(ba1, 0, ba, 0, ba1.length);
    System.arraycopy(ba2, 0, ba, ba1.length, ba2.length);
    return ba;
  }

  public byte[] getBattery() {
    byte[] battval;
    int retSize = 2;

    ReadWrite._writeFluke(s.getSocket(), s.isConnected(),
        new byte[] { (byte) GET_BATTERY });
    battval = ReadWrite._read(s.getSocket(), s.isConnected(), retSize);

    if (D) Log.i(TAG, "Battery Done");
    return battval;

  }

  /**
   * Returns the left, center, right obstacle values of the robot. Note: this is
   * a FLUKE command.
   * 
   * @return An integer array of size 3 containing left, center, right obstacle
   *         values
   */
  public int[] getObstacle() {
    int[] obstacleValues = new int[3];
    int[] obstacleCodes = { GET_DONGLE_L_IR, GET_DONGLE_C_IR, GET_DONGLE_R_IR };
    int retSize = 2;
    byte[] temp;

    // Populate the left, center, right obstacle values
    for (int i = 0; i < obstacleCodes.length; i++) {
      ReadWrite._writeFluke(s.getSocket(), s.isConnected(),
          new byte[] { (byte) obstacleCodes[i] });
      temp = ReadWrite._read(s.getSocket(), s.isConnected(), retSize);
      obstacleValues[i] = temp[0] << 8 & 0xFF | temp[1] & 0xFF;
    }
 
    
    Log.d(TAG, "Obstacle Return "+int2str(obstacleValues));
    return obstacleValues;
  }

  public int[] getIR() {
    int[] ba = null;
    int numBytes = 2;

    ba = _get(new byte[] { (byte) GET_IR_ALL }, numBytes, "byte");

    if (D) Log.i(TAG, "IR Done");
    return ba;
  }

  public int[] getLight() {
    int[] ba = null;
    int numBytes = 6;

    ba = _get(new byte[] { (byte) GET_LIGHT_ALL }, numBytes, "word");

    if (D) Log.i(TAG, "LIGHT Done");
    return ba;
  }

  /**
   * Function that gets contents of a request to the robot
   * 
   * @param ba
   *          Byte array containing the message to be sent to the robot
   * @param numBytes
   *          the number of bytes in the robots message
   * @param getType
   *          the way the read bytes need to be formatted (byte or word)
   * @return an int[] containing the requested message from the robot
   */
  private int[] _get(byte[] ba, int numBytes, String getType) {
    int[] ret;
    byte[] temp;

    // Write Message
    ReadWrite._write(s.getSocket(), s.isConnected(), ba);
    
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // Read the Message Echo
    temp = ReadWrite._read(s.getSocket(), s.isConnected(), PACKET_LENGTH);
    if (temp == null) Log.d(TAG,"TEMP IS NULL");
    if (D) Log.d(TAG, "ECHO READ: " + temp.length + " -> " + ba2s(temp));

    // Read contents of what's desired
    temp = ReadWrite._read(s.getSocket(), s.isConnected(), numBytes);
    
    // Convert the received bytes if needed
    if (getType.toLowerCase().equals("byte")) {
      ret = new int[temp.length];
      for (int i = 0; i < ret.length; i++) {
        ret[i] = temp[i];
      }
    } else if (getType.toLowerCase().equals("word")) {
      int c = 0;
      ret = new int[numBytes / 2];

      if (D) Log.d(TAG, "GET[before word modify]: " + ba2s(temp));

      for (int i = 0; i < numBytes; i = i + 2) {
        ret[c] = (temp[i] & 0xFF) << 8 | temp[i + 1] & 0xFF;
        c++;
      }
    } else {
      if (D) Log.e(TAG, "Cannot _get type: " + getType);
      ret = new int[] {};
    }

    if (D) Log.d(TAG, "GET: " + int2str(ret));
    return ret;
  }

  private static String ba2s(byte[] ba) {
    StringBuilder sb = new StringBuilder("[ ");
    for (byte b : ba) {
      sb.append(Integer.toHexString(b & 0xff)).append(" ");
    }
    sb.append("]");
    return sb.toString();
  }

  private static String int2str(int[] ba) {
    StringBuilder sb = new StringBuilder("[ ");
    for (int b : ba) {
      sb.append(Integer.toHexString(b)).append(" ");
    }
    sb.append("]");
    return sb.toString();
  }
}
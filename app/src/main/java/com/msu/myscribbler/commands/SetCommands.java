/*
 * Code taken from project: https://github.com/sahhhm/Scribdroid
 */

package com.msu.myscribbler.commands;

public class SetCommands {
  private Scribbler s;

  private static final int PACKET_LENGTH = 9;
  private static final int SENSOR_PACKET_SIZE = 11;

  private static final int SET_SPEAKER = 113;
  private static final int SET_SPEAKER_2 = 114;
  private static final int SET_LED_LEFT_ON = 99;
  private static final int SET_LED_LEFT_OFF = 100;
  private static final int SET_LED_CENTER_ON = 101;
  private static final int SET_LED_CENTER_OFF = 102;
  private static final int SET_LED_RIGHT_ON = 103;
  private static final int SET_LED_RIGHT_OFF = 104;
  private static final int SET_LED_ALL_ON = 105;
  private static final int SET_LED_ALL_OFF = 106;
  private static final int SET_MOTORS_OFF = 108;
  private static final int SET_MOTORS = 109;

  public SetCommands(Scribbler aScrib) {
    s = aScrib;
  }

  public void _set(byte[] values) {
    byte[] ba = null;

    ReadWrite._write(s.getSocket(), s.isConnected(), values);

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    ba = ReadWrite._read(s.getSocket(), s.isConnected(), PACKET_LENGTH);

    ba = ReadWrite._read(s.getSocket(), s.isConnected(), SENSOR_PACKET_SIZE);
    s.setLastSensors(ba);

  }

  public void _setSpeaker(int frequency, int duration) {
    byte[] values = { ((byte) SET_SPEAKER), (byte) (duration >> 8),
        (byte) (duration % 256), (byte) (frequency >> 8), (byte) (frequency % 256) };

    _set(values);
  }

  public void _setSpeaker2(int frequency1, int frequency2, int duration) {
    byte[] values = { ((byte) SET_SPEAKER_2), (byte) (duration >> 8),
        (byte) (duration % 256), (byte) (frequency1 >> 8), (byte) (frequency1 % 256),
        (byte) (frequency2 >> 8), (byte) (frequency2 % 256) };

    _set(values);
  }

  public void _move(double translate, double rotate) {
    double left = Math.min(Math.max(translate - rotate, -1), 1);
    double right = Math.min(Math.max(translate + rotate, -1), 1);

    byte[] values = { ((byte) SET_MOTORS), (byte) ((left + 1) * 100), // Need
                                                                      // this
                                                                      // math
                                                                      // to
        (byte) ((right + 1) * 100) // get 0, 100, or 200
    };
    _set(values);
  }

  public void _stop() {
    _set(new byte[] { (byte) SET_MOTORS_OFF });
  }

  public void _setLED(LED position, boolean on) {
    byte[] ba;
    switch (position) {
    case ALL:
      ba = on ? new byte[] { (byte) SET_LED_ALL_ON }
          : new byte[] { (byte) SET_LED_ALL_OFF };
      break;
    case LEFT:
      ba = on ? new byte[] { (byte) SET_LED_LEFT_ON }
          : new byte[] { (byte) SET_LED_LEFT_OFF };
      break;
    case CENTER:
      ba = on ? new byte[] { (byte) SET_LED_CENTER_ON }
          : new byte[] { (byte) SET_LED_CENTER_OFF };
      break;
    case RIGHT:
      ba = on ? new byte[] { (byte) SET_LED_RIGHT_ON }
          : new byte[] { (byte) SET_LED_RIGHT_OFF };
      break;
    default:
      ba = new byte[] { (byte) SET_LED_ALL_OFF };
      break;
    }
    _set(ba);
  }

  public enum LED {
    ALL, LEFT, CENTER, RIGHT
  }
}
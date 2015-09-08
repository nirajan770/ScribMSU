package com.msu.myscribbler.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyCompassView extends View {

  private Paint paint;
  private Float azimut;

  public Float getAzimut() {
	return azimut;
}

public void setAzimut(Float azimut) {
	this.azimut = azimut;
}

public MyCompassView(Context context) {
    super(context);
    init();
  }

  private void init() {
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStrokeWidth(2);
    paint.setTextSize(25);
    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(Color.WHITE);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    /*int xPoint = getMeasuredWidth() / 2;
    int yPoint = getMeasuredHeight() / 2;

    float radius = (float) (Math.max(xPoint, yPoint) * 0.6);
    canvas.drawCircle(xPoint, yPoint, radius, paint);
    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

    // 3.143 is a good approximation for the circle
    canvas.drawLine(xPoint,
        yPoint,
        (float) (xPoint + radius
            * Math.sin((double) (-position) / 180 * 3.143)),
        (float) (yPoint - radius
            * Math.cos((double) (-position) / 180 * 3.143)), paint);

    canvas.drawText(String.valueOf(position), xPoint, yPoint, paint);*/
	  int width = getWidth();
      int height = getHeight();
      int centerx = width/2;
      int centery = height/2;
      canvas.drawLine(centerx, 0, centerx, height, paint);
      canvas.drawLine(0, centery, width, centery, paint);
      // Rotate the canvas with the azimut      
      if (azimut!=null)
        canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
      paint.setColor(0xff0000ff);
      canvas.drawLine(centerx, -1000, centerx, +1000, paint);
      canvas.drawLine(-1000, centery, 1000, centery, paint);
      canvas.drawText("N", centerx+5, centery-10, paint);
      canvas.drawText("S", centerx-10, centery+15, paint);
      paint.setColor(0xff00ff00);
  }

  public void updateData(float position) {
    this.azimut = position;
    invalidate();
  }

} 
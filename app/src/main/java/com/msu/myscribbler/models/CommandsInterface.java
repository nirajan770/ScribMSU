package com.msu.myscribbler.models;

/**
 * An interface that lists all different types of movement of the robot
 * @author Nirajan
 *
 */
public interface CommandsInterface {
	void forward(double amount) throws Exception;
	void backward(double amount) throws Exception;
	void halt() throws Exception;
	void turnLeft(double amount) throws Exception;
	void turnRight(double amount) throws Exception;
	void turnAround() throws Exception;
	void setBeep(float frequency, float duration);
	
	/**
	 * continous movement
	 */
	void keepForward(double amount) throws Exception;
	void keepBackward(double amount) throws Exception;
	void keepLeft(double amount) throws Exception;
	void keepRight(double amount) throws Exception;
}

package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	public static RobotDrive merlin; //Create New Robot Drive
	
    //Controllers
    public static Joystick stick; //Create a new stick for our 3D Pro
    public static Joystick controller; //Creates a new stick for our XBox Controller
	
	//Solenoids
	public static DoubleSolenoid ballPlungerSol = new DoubleSolenoid(2, 3); //This solenoid runs the the ball pusher on the dust pan
	public static DoubleSolenoid dustPanSol = new DoubleSolenoid(4, 5); //This solenoid runs the moving of the dust pan.
	public static DoubleSolenoid stops = new DoubleSolenoid(1, 6); //This solenoid runs the stop on the side of the bot.

	//Speed Controllers
	public static CANTalon flyWheel = new CANTalon(0); //Old speed controller, need to remove

	//Gyro
	public static ADXRS450_Gyro gyro; //SPI gyro from FIRST Choice

	//Compressor
	public static Compressor c = new Compressor(0); //Create compressor 'c' on 0

	//Booleans
	public boolean plungerButton; //Create a bool for our plunger button
	public boolean upButton; //Create a bool for our dust pan up button
	public boolean locksButtonValue; //Create a bool for our locks open button
	public boolean locksButtonCloseValue; //Create a bool for our lock close button
	public boolean invertButton; //Create a bool for our inversion button
	public boolean downButton; //Create a bool for our dust pan down button
	public boolean spinShooterwheelForward; //Create a bool for our fly wheel out button
	public boolean spinShooterwheelBackward; //Create a bool for our fly wheel in button
	public boolean slowModeButton; //Create a bool for our slow mode button
	public boolean gyroSetButton; //Create a bool for our gyro set button
	public boolean locksEngaded = false; //Create a bool for locking the dust pan
	public boolean dustpanUpStatus; //Create a bool for dust pan status
	public boolean autoStop = false; //Create a bool for stopping auto mode
	public boolean triggerLocks = false; //Create a bool to trigger locks
	public boolean triggerClose = false; //Create a bool to shut locks
	
	//Doubles
	public static double Kp = 0.03; //Constant used to drive forward in a line
	public double driveSchedulerX; //Used to hold X drive value so it can be modified multiple times
	public double driveSchedulerY; //Used to hold Y drive value so it can be modified multiple times

	//Ints
	public int atonLoopCounter; //Loop used to order auto mode
}


package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;

public class Robot extends IterativeRobot {
	
    //Drivetrain
    RobotDrive merlin; //Create New Robot Drive
	
    //Controllers
    Joystick stick; //Create a new stick for our 3D Pro
	Joystick controller; //Creates a new stick for our XBox Controller
	
	//Solenoids
	DoubleSolenoid ballPlungerSol = new DoubleSolenoid(2, 3); //This solenoid runs the the ball pusher on the dust pan
	DoubleSolenoid dustPanSol = new DoubleSolenoid(4, 5); //This solenoid runs the moving of the dust pan.
	DoubleSolenoid stops = new DoubleSolenoid(1, 6); //This solenoid runs the stop on the side of the bot.
	
	//Speed Controllers
	CANTalon flyWheel = new CANTalon(0); //Old speed controller, need to remove

	//Gyro
	ADXRS450_Gyro gyro; //SPI gyro from FIRST Choice

	//Compressor
	Compressor c = new Compressor(0); //Create compressor 'c' on 0

	//Booleans
	boolean plungerButton; //Create a bool for our plunger button
	boolean upButton; //Create a bool for our dust pan up button
	boolean locksButtonValue; //Create a bool for our locks open button
	boolean locksButtonCloseValue; //Create a bool for our lock close button
	boolean invertButton; //Create a bool for our inversion button
	boolean downButton; //Create a bool for our dust pan down button
	boolean spinShooterwheelForward; //Create a bool for our fly wheel out button
	boolean spinShooterwheelBackward; //Create a bool for our fly wheel in button
	boolean slowModeButton; //Create a bool for our slow mode button
	boolean gyroSetButton; //Create a bool for our gyro set button
	boolean locksEngaded = false; //Create a bool for locking the dust pan
	boolean dustpanUpStatus; //Create a bool for dust pan status
	boolean autoStop = false; //Create a bool for stopping auto mode
	boolean triggerLocks = false; //Create a bool to trigger locks
	boolean triggerClose = false; //Create a bool to shut locks
	
	//Doubles
	double Kp = 0.03; //Constant used to drive forward in a line
	double driveSchedulerX; //Used to hold X drive value so it can be modified multiple times
	double driveSchedulerY; //Used to hold Y drive value so it can be modified multiple times

	//Ints
	int atonLoopCounter; //Loop used to order auto mode
	
    public void robotInit() {
        //Drivetrain
    	merlin = new RobotDrive(8, 9, 0, 1); //Assign robotdrive on the 4 pwm pins
    	
    	//Controllers
    	stick = new Joystick(1); //Assign to a joystick on port 1
    	controller = new Joystick(0); //Assign to a controller on port 0

    	//Gyro
    	gyro = new ADXRS450_Gyro(); //Create a new object for our SPI gyro
    	gyro.calibrate(); //Calibrate our gyro
    	
    	//Grip-More info here: https://github.com/WPIRoboticsProjects/GRIP/wiki/Tutorial:-Run-GRIP-from-a-CPP,-Java,-or-LabVIEW-FRC-program
    	try {
            new ProcessBuilder("/home/lvuser/grip").inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void autonomousInit() {
    	//No init code
    }

    public void autonomousPeriodic() {
    	//Compressor
    	c.setClosedLoopControl(true); //Run the compressor on a closed loop
   
    	//Gyro
    	double angle = gyro.getAngle(); //Set angle equal to the gyro's angle
		SmartDashboard.putNumber("Angle: ", angle); //Send the angle to the dashboard
		
    	/* Auto Loop */
    	if(autoStop == false){ //Check if we are done
    		if(atonLoopCounter < 50){ //Check if we have done 50 loops(About 1 seconds)
        		stops.set(DoubleSolenoid.Value.kReverse); //Put out stop
        		dustPanSol.set(DoubleSolenoid.Value.kReverse); //Drop dust pan
        		gyro.calibrate(); //Calibrate the gyro
        		SmartDashboard.putString("Auto Status: ", "Down and Calibrate"); //Send status to dashboard
        		SmartDashboard.putNumber("Loop Number: ", atonLoopCounter);	//Send loop to dashboard
        		atonLoopCounter++; //Add 1 to our counter
        	}
        	if(atonLoopCounter > 49 && atonLoopCounter < 350){ //After 1 second, drive for 6 seconds
                SmartDashboard.putNumber("Error", (angle*Kp)); //Send X error to dashboard
        		driveSchedulerX = angle*Kp; //Drive the the oppiste of our x error to correct
        		driveSchedulerY = -0.9; //Drive backwards  at 90%
        		SmartDashboard.putString("Auto Status: ", "Driving Backward"); //Send status to dashboard
        		SmartDashboard.putNumber("Loop Number: ", atonLoopCounter);	//Send loop to dashboard
        		atonLoopCounter++; //Add 1 to our counter
        	}
        	if(atonLoopCounter >349 && atonLoopCounter < 355){
        		driveSchedulerX = 0.0; //Turn off motors
        		driveSchedulerY = 0.0; //Turn off motors
        		autoStop = true; //Tell auto to stop
        		atonLoopCounter++; //Add 1 to our counter
        		SmartDashboard.putString("Auto Status: ", "Stopped"); //Send Status to dashboard
        		SmartDashboard.putNumber("Loop Number: ", atonLoopCounter);	//Send loop to dashboard
        	}
        	merlin.arcadeDrive(driveSchedulerX, driveSchedulerY); //Drive the robot
    	}
    }

    public void teleopPeriodic() {
    	//Compressor
    	c.setClosedLoopControl(true); //Run the compressor on a closed loop
    	
    	//Gyro
    	double angle = gyro.getAngle(); //Set angle equal to the gyro's angle
    	SmartDashboard.putNumber("Angle: ", angle);
    	gyroSetButton = stick.getRawButton(5);
    	if(gyroSetButton == true){
    		gyro.calibrate();
    	}
    	
    	//Drivetrain
    	invertButton = controller.getRawButton(5);
    	if(invertButton == false){
        	double controllerLY = controller.getRawAxis(4) * -1;
        	double controllerRX = controller.getRawAxis(1) * -1;
        	driveSchedulerY = controllerLY;
        	driveSchedulerX = controllerRX;
        	SmartDashboard.putString("Inverted Drive: ", "Off");
    	}
    	else{
        	double controllerLY = controller.getRawAxis(4) * 1;
        	double controllerRX = controller.getRawAxis(1) * 1;
        	driveSchedulerY = controllerLY;
        	driveSchedulerX = controllerRX;
        	SmartDashboard.putString("Inverted Drive: ", "On");
    	}
    	slowModeButton = controller.getRawButton(6);
    	if(slowModeButton == true){
        	double controllerLY = controller.getRawAxis(4) * -0.6;
        	double controllerRX = controller.getRawAxis(1) * -0.7;
        	driveSchedulerY = controllerLY;
        	driveSchedulerX = controllerRX;
        	SmartDashboard.putString("Slow Mode: ", "On");
    	}
    	else{
    		SmartDashboard.putString("Slow Mode: ", "Off");
    	}
    	
    	//Shooter Wheel
    	spinShooterwheelForward = stick.getRawButton(4);
    	spinShooterwheelBackward = stick.getRawButton(3);
        if(spinShooterwheelForward == true && spinShooterwheelBackward == false){
        	flyWheel.set(-1);
        	SmartDashboard.putString("Shooter Wheel: ", "Shooting");
        }
        if(spinShooterwheelBackward == true && spinShooterwheelForward == false){
        	flyWheel.set(1);
        	SmartDashboard.putString("Shooter Wheel: ", "Picking Up");
        }
        if(spinShooterwheelBackward == false && spinShooterwheelForward == false){
        	flyWheel.set(0);
        	SmartDashboard.putString("Shooter Wheel: ", "Off");
        }
    	
        //Dust Pan Moving Code
    	upButton = stick.getRawButton(9);
    	downButton = stick.getRawButton(10);
    	if(upButton == true && downButton == false){
    		dustPanSol.set(DoubleSolenoid.Value.kForward);
    		SmartDashboard.putString("Dustpan Status: ", "Up");
    		dustpanUpStatus = true;
    		triggerClose = true;
    	}
    	if(downButton == true && upButton == false){
    		dustPanSol.set(DoubleSolenoid.Value.kReverse);
    		SmartDashboard.putString("Dustpan Status: ", "Down");
    		dustpanUpStatus = false;
    		triggerLocks = true;
    	}
    	
        //Dust Pan Locks
    	locksButtonValue = stick.getRawButton(11);
    	if(locksButtonValue == true || locksEngaded == true || triggerLocks == true){
    		stops.set(DoubleSolenoid.Value.kForward);
    		locksEngaded = true;
    		triggerLocks = false;
    	}
    	else{
    		stops.set(DoubleSolenoid.Value.kReverse);
    	}
    	
    	locksButtonCloseValue = stick.getRawButton(12);
    	if(locksButtonCloseValue == true || triggerClose == true){
    		stops.set(DoubleSolenoid.Value.kReverse);
    		locksEngaded = false;
    		triggerClose = false;
    	}
    	if(locksEngaded == false){
    		SmartDashboard.putString("Locks Status: ", "Out");
    	}
    	if(locksEngaded == true){
    		SmartDashboard.putString("Locks Status: ", "In");
    	}
    	
    	//Ball Plunger
    	plungerButton = stick.getRawButton(1);
    	if(plungerButton == true){
    		ballPlungerSol.set(DoubleSolenoid.Value.kForward);
    		SmartDashboard.putString("Plunger Status: ", "Out");
    	}
    	else{
    		ballPlungerSol.set(DoubleSolenoid.Value.kReverse);
    		SmartDashboard.putString("Plunger Status: ", "In");
    	}
    	
    	//Drivetrain
    	SmartDashboard.putNumber("X Drive Value: ", driveSchedulerX);
    	SmartDashboard.putNumber("Y Drive Value: ", driveSchedulerY);
    	merlin.arcadeDrive(driveSchedulerX, driveSchedulerY);
    }
    
    public void testPeriodic() {
    	c.setClosedLoopControl(true);
    }
    
}


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
	
    //Create new objects
    RobotDrive merlin; //Create New Robot Drive
	Joystick stick; //Create a new stick for our 3D Pro
	Joystick controller; //Creates a new stick for our XBox Controller
	DoubleSolenoid ballPlungerSol = new DoubleSolenoid(2, 3);
	DoubleSolenoid dustPanSol = new DoubleSolenoid(4, 5);
	DoubleSolenoid stops = new DoubleSolenoid(1, 6);
	//CANTalon flyWheel = new CANTalon(0);
	CANTalon frontLeft = new CANTalon(0);
	CANTalon frontRight = new CANTalon(1);
	VictorSP backLeft = new VictorSP(0);
	VictorSP backRight = new VictorSP(1);
	Jaguar flyWheel = new Jaguar(2);
	Compressor c = new Compressor(0);
	ADXRS450_Gyro gyro;
	int atonLoopCounter;
	boolean buttonValue;
	boolean inverted;
	boolean aimAssist;
	boolean upButton;
	boolean locksButtonValue;
	boolean locksButtonCloseValue;
	boolean invertButton;
	boolean downButton;
	boolean spinShooterwheelForward;
	boolean spinShooterwheelBackward;
	boolean slowModeButton;
	boolean gyroSetButton;
	boolean turnDone = false;
	boolean locksEngaded = false;
	boolean dustpanUpStatus;
	boolean autoStop = false;
	boolean triggerLocks = false;
	boolean triggerClose = false;
	double adjTilt;
	double Kp = 0.03;
	double xCord;
	double driveSchedulerX;
	double driveSchedulerY;
	
    public void robotInit() {
        
        //Assign objects
    	merlin = new RobotDrive(frontLeft, backLeft,frontRight, backRight); //Assign to robodrive on PWM 0 and 1
    	stick = new Joystick(1); //Assign to a joystick on port 0
    	controller = new Joystick(0);
    	gyro = new ADXRS450_Gyro();
        //Gyro
        gyro.calibrate();
    	//Grip Test Code
    	try {
            new ProcessBuilder("/home/lvuser/grip").inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void autonomousInit() {

    }

    public void autonomousPeriodic() {
		double angle = gyro.getAngle(); // get current heading
    	//Compressor
    	c.setClosedLoopControl(true);
   
		SmartDashboard.putNumber("Angle: ", angle);
    	if(autoStop == false){
    		if(atonLoopCounter < 50){ //About 50 loops per second
        		stops.set(DoubleSolenoid.Value.kReverse);
        		dustPanSol.set(DoubleSolenoid.Value.kReverse);
        		gyro.calibrate();
        		SmartDashboard.putString("Auto Status: ", "Down and Calibrate");
        		SmartDashboard.putNumber("Loop Number: ", atonLoopCounter);	
        		atonLoopCounter++;
        	}
        	if(atonLoopCounter > 49 && atonLoopCounter < 350){
                SmartDashboard.putNumber("Error", (angle*Kp));
        		driveSchedulerX = angle*Kp;
        		driveSchedulerY = -0.9;
        		SmartDashboard.putString("Auto Status: ", "Driving Forward");
        		atonLoopCounter++;
        	}
        	if(atonLoopCounter >349 && atonLoopCounter < 355){
        		driveSchedulerX = 0.0;
        		driveSchedulerY = 0.0;
        		autoStop = true;
        		atonLoopCounter++;
        		SmartDashboard.putString("Auto Status: ", "Stopped");
        	}
        	merlin.arcadeDrive(driveSchedulerX, driveSchedulerY);
    	}
    }

    public void teleopPeriodic() {
    	//Compressor
    	c.setClosedLoopControl(true);
    	
    	//Gyro
    	double angle = gyro.getAngle(); // get current heading
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
    	buttonValue = stick.getRawButton(1);
    	if(buttonValue == true){
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

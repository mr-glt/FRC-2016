
package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;

public class Robot extends IterativeRobot {
    //Smart Dashboard Crap
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
	
    //Create new objects
    RobotDrive merlin; //Create New Robot Drive
	Joystick stick; //Create a new stick
	Joystick controller; //Creates 
	DoubleSolenoid ballPlungerSol = new DoubleSolenoid(2, 3);
	DoubleSolenoid dustPanSol = new DoubleSolenoid(4, 5);
	DoubleSolenoid stops = new DoubleSolenoid(1, 6);
	CANTalon flyWheel = new CANTalon(0);
	Compressor c = new Compressor(0);
	ADXRS450_Gyro gyro;
	CameraServer server;
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
	double adjTilt;
	double Kp = 0.03;
	double xCord;
	double driveSchedulerX;
	double driveSchedulerY;
	
    public void robotInit() {
    	//Smart Dashboard Crap
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        //Assign objects
    	merlin = new RobotDrive(0,1); //Assign to robodrive on PWM 0 and 1
    	stick = new Joystick(1); //Assign to a joystick on port 0
    	controller = new Joystick(0);
    	gyro = new ADXRS450_Gyro();
        server = CameraServer.getInstance();
        server.setQuality(50);
        server.startAutomaticCapture("cam0");
        //Gyro
        gyro.calibrate();
    }
    
    public void autonomousInit() {
    	//Smart Dashboard Crap
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }

    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        	//Compressor
        	c.setClosedLoopControl(true);
        
    		double angle = gyro.getAngle(); // get current heading
    		SmartDashboard.putNumber("Angle: ", angle);
        	if(atonLoopCounter < 50){ //About 50 loops per second
        		dustPanSol.set(DoubleSolenoid.Value.kReverse);
        		stops.set(DoubleSolenoid.Value.kForward);
        		gyro.calibrate();
        		
        	}
        	if(atonLoopCounter > 49 && atonLoopCounter < 250 && autoStop == false){
                SmartDashboard.putNumber("Error", (angle*Kp));
        		driveSchedulerX = -angle*Kp;
        		driveSchedulerY = 0.45;
        	}
        	if(atonLoopCounter > 249 && atonLoopCounter < 450 && autoStop == false){
        		if(turnDone == false){
	        		if(angle < 30){
	            		driveSchedulerX = -0.5;
	            		driveSchedulerY = 0.0;
	        		}
	        		if(angle > 30){
	        			driveSchedulerX = 0.5;
	        			driveSchedulerY = 0.0;
	        		}
	        		if(angle > 29 && angle < 31){
	                		driveSchedulerX = 0.0;
	                		driveSchedulerY = 0.0;
	                		turnDone = true;
	        		}
        		}
        	}
        	if(atonLoopCounter > 449 && atonLoopCounter < 500 && autoStop == false){
        		dustPanSol.set(DoubleSolenoid.Value.kForward);
        	}
        	if(atonLoopCounter > 499 && atonLoopCounter < 650 && autoStop == false){
        		flyWheel.set(1);
        	}
        	if(atonLoopCounter > 649 && atonLoopCounter < 750 && autoStop == false){
        		flyWheel.set(1);
        		ballPlungerSol.set(DoubleSolenoid.Value.kForward);	
        	}
        	if(atonLoopCounter > 749 && atonLoopCounter < 800 && autoStop == false){
        		flyWheel.set(0);
        		ballPlungerSol.set(DoubleSolenoid.Value.kReverse);
        		dustPanSol.set(DoubleSolenoid.Value.kReverse);
        		autoStop = true;
        	}
        	break;
    	case defaultAuto:
    	default:
    		//Compressor
        	c.setClosedLoopControl(true);
    		/*
    		double angle = gyro.getAngle(); // get current heading
    		SmartDashboard.putNumber("Angle: ", angle);
    		

    		
    		if(atonLoopCounter < 200) //About 50 loops per second
    		{
        		SmartDashboard.putNumber("Error", (angle*Kp));
        		SmartDashboard.putString("Aton Status", "Driving Forward");
        		driveSchedulerX = -angle*Kp;
        		driveSchedulerY = 0.45;
    			atonLoopCounter++;
    		} 
            if(atonLoopCounter > 199 && turnDone == false){
        		if(angle < 180){
            		driveSchedulerX = -0.5;
            		driveSchedulerY = 0.0;
        		}
        		if(angle > 180){
        			driveSchedulerX = 0.5;
        			driveSchedulerY = 0.0;
        		}
        		if(angle > 179 && angle < 181){
                		driveSchedulerX = 0.0;
                		driveSchedulerY = 0.0;
                		turnDone = true;
                		
        		}
        	
            }
    		if(atonLoopCounter < 300 && turnDone == true){
            	SmartDashboard.putString("Aton Status", "Programed Rotate");
            	driveSchedulerY = 0.45;
            	driveSchedulerX = 0.0;
            	atonLoopCounter++;
            }

            if(atonLoopCounter > 399 && atonLoopCounter < 410){
            	SmartDashboard.putString("Aton Status", "Auto Aim");
             	   if(xCord < 75){
             		   merlin.drive(0.0, 0.2);
             		   SmartDashboard.putString("Aim Assist: ", "Too Far Left, Moving Right");
             	   }
             	   if(xCord > 85){
             		   merlin.drive(0.0, -0.2);
             		   SmartDashboard.putString("Aim Assist: ", "Too Far Right, Moving Left");
             	   }
             	   if(!(xCord <70) && !(xCord >90)){
             		   merlin.drive(0.0, 0.0);
             		   SmartDashboard.putString("Aim Assist: ", "On Target");
             	   }
            	atonLoopCounter++;
            }

            if(atonLoopCounter > 399){
            	driveSchedulerX = 0.0;
            	SmartDashboard.putString("Aton Status", "Done");
            	atonLoopCounter = 0;
            	break;
            }
          
            merlin.arcadeDrive(driveSchedulerY, driveSchedulerX);

            SmartDashboard.putNumber("Error", (angle*Kp));
            merlin.drive(-0.2, -angle*Kp); // drive towards heading 0
         	
            */
            break;
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
        	double controllerRX = controller.getRawAxis(1) * -0.9;
        	driveSchedulerY = controllerLY;
        	driveSchedulerX = controllerRX;
        	SmartDashboard.putString("Inverted Drive: ", "Off");
    	}
    	else{
        	double controllerLY = controller.getRawAxis(4) * 1;
        	double controllerRX = controller.getRawAxis(1) * 0.9;
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
        	SmartDashboard.putString("Shooter Wheel: ", "Pusing Out");
        }
        if(spinShooterwheelBackward == true && spinShooterwheelForward == false){
        	flyWheel.set(1);
        	SmartDashboard.putString("Shooter Wheel: ", "Sucking In");
        }
        if(spinShooterwheelBackward == false && spinShooterwheelForward == false){
        	flyWheel.set(0);
        }
    	//Dust Pan Moving Code
    	upButton = stick.getRawButton(9);
    	downButton = stick.getRawButton(10);
    	if(upButton == true && downButton == false){
    		dustPanSol.set(DoubleSolenoid.Value.kForward);
    		SmartDashboard.putString("Dustpan Status: ", "Up");
    		dustpanUpStatus = true;
    	}
    	if(downButton == true && upButton == false){
    		dustPanSol.set(DoubleSolenoid.Value.kReverse);
    		SmartDashboard.putString("Dustpan Status: ", "Down");
    		dustpanUpStatus = false;
    		stops.set(DoubleSolenoid.Value.kForward);
    		locksEngaded = true;
    	}
    	
        //Dust Pan Locks
    	locksButtonValue = stick.getRawButton(11);
    	if(locksButtonValue == true || locksEngaded == true){
    		stops.set(DoubleSolenoid.Value.kForward);
    		locksEngaded = true;
    	}
    	else{
    		stops.set(DoubleSolenoid.Value.kReverse);
    	}
    	
    	locksButtonCloseValue = stick.getRawButton(12);
    	if(locksButtonCloseValue == true){
    		stops.set(DoubleSolenoid.Value.kReverse);
    		locksEngaded = false;
    	}
    	SmartDashboard.putBoolean("Locks Status: ", locksEngaded);
    	
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
    	merlin.arcadeDrive(driveSchedulerX, driveSchedulerY);
    }
    
    public void testPeriodic() {
    	c.setClosedLoopControl(true);
    }
    
}

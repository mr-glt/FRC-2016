
package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.IOException;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	ADXRS450_Gyro gyro;
	NetworkTable table;
	int atonLoopCounter;
	boolean buttonValue;
	boolean inverted;
	boolean aimAssist;
	boolean upButton;
	boolean locksButtonValue;
	boolean locksButtonCloseValue;
	boolean downButton;
	boolean spinShooterwheelForward;
	boolean spinShooterwheelBackward;
	boolean turnDone = false;
	boolean locksEngaded = false;
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
    	//Grip Test Code
    	try {
            new ProcessBuilder("/home/lvuser/grip").inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        table = NetworkTable.getTable("GRIP/myContoursReport");
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
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
        	//Compressor
        	//Compressor c = new Compressor(0);
        	//c.setClosedLoopControl(true);
    		
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
/*
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
        */
            if(atonLoopCounter > 399){
            	driveSchedulerX = 0.0;
            	SmartDashboard.putString("Aton Status", "Done");
            	atonLoopCounter = 0;
            	break;
            }
          
            merlin.arcadeDrive(driveSchedulerY, driveSchedulerX);
            /*
            SmartDashboard.putNumber("Error", (angle*Kp));
            merlin.drive(-0.2, -angle*Kp); // drive towards heading 0
         	*/
            break;
    	}
    }

    public void teleopPeriodic() {
    	//Compressor
    	Compressor c = new Compressor(0);
    	c.setClosedLoopControl(true);
    	double angle = gyro.getAngle(); // get current heading
    	SmartDashboard.putNumber("Angle: ", angle);
    	//Drivetrain
    	double controllerLY = controller.getRawAxis(4) * -0.96;
    	double controllerRX = controller.getRawAxis(1) * -0.76;
    	driveSchedulerY = controllerLY;
    	driveSchedulerX = controllerRX;
    	
    	//Shooter Wheel
    	spinShooterwheelForward = stick.getRawButton(3);
    	spinShooterwheelBackward = stick.getRawButton(4);
        if(spinShooterwheelForward == true && spinShooterwheelBackward == false){
        	flyWheel.set(1);
        	SmartDashboard.putString("Shooter Wheel: ", "Pusing Out");
        }
        if(spinShooterwheelBackward == true && spinShooterwheelForward == false){
        	flyWheel.set(-1);
        	SmartDashboard.putString("Shooter Wheel: ", "Sucking In");
        }
        if(spinShooterwheelBackward == false && spinShooterwheelForward == false){
        	flyWheel.set(0);
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
    	SmartDashboard.putBoolean("Locks Status", locksEngaded);
    	
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
    	//Dust Pan Moving Code
    	upButton = stick.getRawButton(9);
    	downButton = stick.getRawButton(10);
    	if(upButton == true && downButton == false){
    		dustPanSol.set(DoubleSolenoid.Value.kForward);
    		SmartDashboard.putString("Dustpan Status: ", "Up");
    	}
    	if(downButton == true && upButton == false){
    		dustPanSol.set(DoubleSolenoid.Value.kReverse);
    		SmartDashboard.putString("Dustpan Status: ", "Down");
    	}
    	
	    //Grip Test Code
	    double defaultValue[] = new double[0];
	    double[] visionX = table.getNumberArray("centerX", defaultValue);
	    double[] visionY = table.getNumberArray("centerY", defaultValue);
	    for(Double visionXCord : visionX){
	    	SmartDashboard.putNumber("X Value of Box: ", visionXCord);
	    	xCord = visionXCord;
        }
        for(Double visionYCord : visionY){
		   SmartDashboard.putNumber("Y Value of Box: ", visionYCord);
        } 
       
       //Aim Assist
        aimAssist = stick.getRawButton(2);
        if(aimAssist == true){
    	   if(xCord < 75){
    		   driveSchedulerY = 0.5;
    		   SmartDashboard.putString("Aim Assist: ", "Too Far Left, Moving Right");
    	   }
    	   if(xCord > 85){
    		   driveSchedulerY = -0.5;
    		   SmartDashboard.putString("Aim Assist: ", "Too Far Right, Moving Left");
    	   }
    	   
    	   if(!(xCord <70) && !(xCord >90)){
    		   driveSchedulerY = 0.0;
    		   SmartDashboard.putString("Aim Assist: ", "On Target");
    	   }
       }
       else{
    	   SmartDashboard.putString("Aim Assist: ", "Off");
       }
       merlin.arcadeDrive(driveSchedulerX, driveSchedulerY);
    }
    
    public void testPeriodic() {
    	//Compressor
    	Compressor c = new Compressor(0);
    	c.setClosedLoopControl(true);
    }
    
}


package org.usfirst.frc.team6027.robot;

import java.io.IOException;
import java.util.Arrays;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    //Smart Dashboard Crap
	final String lowbarAuto = "Lowbar";
	final String roughAuto = "Rough Tor";
	final String testAuto = "Test";
    String autoSelected;
    SendableChooser chooser;
	
    //Create new objects
    RobotDrive merlin; //Create New Robot Drive
	Joystick stick; //Create a new stick
	//Joystick controller; //Creates 
	DoubleSolenoid ballPlungerSol = new DoubleSolenoid(1, 2);
	DoubleSolenoid dustPanSol = new DoubleSolenoid(3, 4);
	CANTalon flyWheel = new CANTalon(0);
	CANTalon dustPanTilt = new CANTalon(1);
	Potentiometer dustPanAngle;
	ADXRS450_Gyro gyro;
	DigitalInput upperLimit;
	DigitalInput bottomLimit;
	//CameraServer server;
	NetworkTable table;
	int atonLoopCounter;
	private final NetworkTable grip = NetworkTable.getTable("grip");
	boolean buttonValue;
	boolean locksButtonValue;
	boolean locksButtonCloseValue;
	boolean inverted;
	boolean locksEngaded = false;
	boolean aimAssist;
	double adjTilt;
	double Kp = 0.03;
	double xCord;


	//For Talon PID
	StringBuilder _sb = new StringBuilder();
	int _loops = 0;
	
    public void robotInit() {
        
    	//Smart Dashboard Crap
    	chooser = new SendableChooser();
        chooser.addDefault("Test", testAuto);
        chooser.addObject("Lowbar", lowbarAuto);
        chooser.addObject("Rough", roughAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        //Assign objects
    	merlin = new RobotDrive(0,1); //Assign to robodrive on PWM 0 and 1
    	stick = new Joystick(0); //Assign to a joystick on port 0
    	//controller = new Joystick(1);
    	upperLimit = new DigitalInput(9);
    	bottomLimit = new DigitalInput(2);
    	gyro = new ADXRS450_Gyro();
    	dustPanAngle = new AnalogPotentiometer(0, 210, -10);
    	//Camera
        /*
    	server = CameraServer.getInstance();
        server.setQuality(50);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
    	*/
    	//Talaon PID Controler
       /*
    	flyWheel.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        flyWheel.reverseSensor(false);
        flyWheel.configNominalOutputVoltage(+0.0f, -0.0f);
        flyWheel.configPeakOutputVoltage(+12.0f, 0.0f);
        flyWheel.setProfile(0);
        flyWheel.setF(0.1097);
        flyWheel.setP(0.22);
        flyWheel.setI(0); 
        flyWheel.setD(0);
  */
    //Grip Test Code
        
    	try {
            new ProcessBuilder("/home/lvuser/grip").inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        table = NetworkTable.getTable("GRIP/myContoursReport");
    }
    
    
    public void autonomousInit() {
    	//Smart Dashboard Crap
    	autoSelected = (String) chooser.getSelected();
		autoSelected = SmartDashboard.getString("Auto Selector", testAuto);
		autoSelected = SmartDashboard.getString("Auto Selector", lowbarAuto);
		System.out.println("Auto selected: " + autoSelected);
		
    }

    public void autonomousPeriodic() {
    	/*
    	switch(autoSelected) {
    	case lowbarAuto:
    		//Put Code Here
            
    		break;
    	case testAuto:
    	default:
            */
            double angle = gyro.getAngle(); // get current heading
        	if(atonLoopCounter < 200) //About 50 loops per second
    		{
        		SmartDashboard.putNumber("Error", (angle*Kp));
        		SmartDashboard.putString("Aton Status", "Driving Forward");
        		merlin.drive(-0.2, -angle*Kp);
    			atonLoopCounter++;
    		} 
        	else {
    			merlin.drive(0.0, 0.0);
    		}
            if(atonLoopCounter > 199 && atonLoopCounter < 230){
            	SmartDashboard.putString("Aton Status", "Programed Rotate");
            	merlin.drive(0.0, 0.3);
            	atonLoopCounter++;
            }
            if(atonLoopCounter > 229 && atonLoopCounter < 410){
            	SmartDashboard.putString("Aton Status", "Auto Aim");
            	atonLoopCounter++;
            }
            if(atonLoopCounter > 409){
            	atonLoopCounter = 0;
            }
            /*
            SmartDashboard.putNumber("Error", (angle*Kp));
            merlin.drive(-0.2, -angle*Kp); // drive towards heading 0
            */
            //break;
   
    	//case roughAuto:
    		//Put Code Here
    		
    	//	break;
    	//}
    }


    public void teleopPeriodic() {
    	//Vars
    	
    	double controllerLY = stick.getRawAxis(1) * -0.56;
    	double controllerRX = stick.getRawAxis(0) * -0.48;

    	//Drivetrain
    	
    	double angle = gyro.getAngle();
    	//if(controllerRX < -0.1 && controllerRX > 0.1){
    		merlin.arcadeDrive(controllerLY, controllerRX);
    		SmartDashboard.putString("Drive Mode", "Unaided");
    	//}
    	//else{
    		//merlin.drive(controllerLY, -angle*Kp); // drive towards heading 0
    		//SmartDashboard.putString("Drive Mode", "Aided");
    	//}
    	
    	//Temp Shooter Wheel
        flyWheel.set(stick.getY());
    
    	//Compressor
    	//Compressor c = new Compressor(0);
    	//c.setClosedLoopControl(true);
        
        //Dust Pan Angle
        double degrees = dustPanAngle.get();
        SmartDashboard.putNumber("Dust Pan Angle", degrees);
        
    	//Ball Plunger
    	buttonValue = stick.getRawButton(1);
    	if(buttonValue == true){
    		ballPlungerSol.set(DoubleSolenoid.Value.kForward);
    		SmartDashboard.putString("Plunger Status", "Out");
    	}
    	else{
    		ballPlungerSol.set(DoubleSolenoid.Value.kReverse);
    		SmartDashboard.putString("Plunger Status", "In");
    	}
    	
    	//Dust Pan Locks
    	locksButtonValue = stick.getRawButton(3);
    	if(locksButtonValue == true || locksEngaded == true){
    		dustPanSol.set(DoubleSolenoid.Value.kForward);
    		locksEngaded = true;
    	}
    	else{
    		dustPanSol.set(DoubleSolenoid.Value.kReverse);
    	}
    	
    	locksButtonCloseValue = stick.getRawButton(4);
    	if(locksButtonCloseValue == true){
    		dustPanSol.set(DoubleSolenoid.Value.kReverse);
    		locksEngaded = false;
    	}
    	SmartDashboard.putBoolean("Locks Status", locksEngaded);
    	
    	//Dust Pan Tilt
    	adjTilt = stick.getY() * 0.25;
	    if(locksEngaded == false){	
    		if(upperLimit.get() == false){
	    		if(stick.getY() < -0.1){
	    			dustPanTilt.set(adjTilt); //Need to Change to Stick
	    			SmartDashboard.putString("Upper Limit Switch", "it");
	    			SmartDashboard.putString("Down Override", "True");
	    			SmartDashboard.putString("Dust Pan Status", "Movable Down");
	    		}
	    		else{
	    			SmartDashboard.putString("Upper Limit Switch", "Hit");
	    			SmartDashboard.putString("Down Override", "False");
	    			SmartDashboard.putString("Dust Pan Status", "Locked");
	    		}	
	    	}
	    	else{
	    		if(bottomLimit.get() == false){
	        		if(stick.getY() > 0.1){
	        			dustPanTilt.set(adjTilt); //Need to Change to Stick
	        			SmartDashboard.putString("Bottom Limit Switch", "Hit");
	        			SmartDashboard.putString("Up Override", "True");
	        			SmartDashboard.putString("Dust Pan Status", "Movable Up");
	        		}
	        		else{
	        			SmartDashboard.putString("Bottom Limit Switch", "Hit");
	        			SmartDashboard.putString("Up Override", "False");
	        			SmartDashboard.putString("Dust Pan Status", "Locked");
	        		}
	    		}
	    		else{
	        		dustPanTilt.set(adjTilt); //Need to Change to Stick
	        		SmartDashboard.putString("Upper Limit Switch", "Free");
	        		SmartDashboard.putString("Bottom Limit Switch", "Free");
	        		SmartDashboard.putString("Down Override", "NA");
	        		SmartDashboard.putString("Up Override", "NA");
	        		SmartDashboard.putString("Dust Pan Status", "Movable");
	    		}
	    		

	    	}
	    }
	    else{
	    	
	    }

    	//Grip Test Code
        
    	double defaultValue[] = new double[0];
        double[] visionX = table.getNumberArray("centerX", defaultValue);
        double[] visionY = table.getNumberArray("centerY", defaultValue);
       for(Double visionXCord : visionX){
    		   SmartDashboard.putNumber("X Value of Box", visionXCord);
    		   xCord = visionXCord;
       }
       for(Double visionYCord : visionY){
		   SmartDashboard.putNumber("Y Value of Box", visionYCord);
       } 
       aimAssist = stick.getRawButton(7);
       //Aim Assist
       if(aimAssist == true){
    	   if(xCord < 75){
    		   merlin.drive(0, 0.2);
    		   SmartDashboard.putString("Aim Assist", "Too Far Left, Moving Right");
    	   }
    	   if(xCord > 85){
    		   merlin.drive(0, -0.2);
    		   SmartDashboard.putString("Aim Assist", "Too Far Right, Moving Left");
    	   }
    	   if(!(xCord <70) && !(xCord >90)){
    		   SmartDashboard.putString("Aim Assist", "On Target");
    	   }
       }
       else{
    	   SmartDashboard.putString("Aim Assist", "Off");
       }
    }	
    

    public void testPeriodic() {
    	//Vars
    	//double controllerLY = controller.getRawAxis(1) * 0.56;
    	//double controllerRX = controller.getRawAxis(4) * 0.48;

    	
    	//Compressor
    	//Compressor c = new Compressor(0);
    	//c.setClosedLoopControl(true);
    	
    	//Drivetrain
    	//merlin.arcadeDrive(controllerLY, controllerRX);
    	
        //Shooter RPM
        double leftYstick = stick.getAxis(AxisType.kY);
    	double motorOutput = flyWheel.getOutputVoltage() / flyWheel.getBusVoltage();
    	/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(motorOutput);
        _sb.append("\tspd:");
        _sb.append(flyWheel.getSpeed() );
        
        	/* Speed mode */
        	double targetSpeed = 1 * 1500.0; /* 1500 RPM in either direction */
        	flyWheel.changeControlMode(TalonControlMode.Speed);
        	flyWheel.set(targetSpeed); /* 1500 RPM in either direction */

        	/* append more signals to print when in speed mode. */
            _sb.append("\terr:");
            _sb.append(flyWheel.getClosedLoopError());
            _sb.append("\ttrg:");
            _sb.append(targetSpeed);

        if(++_loops >= 10) {
        	_loops = 0;
        	System.out.println(_sb.toString());
        }
        _sb.setLength(0);
    }
    
}

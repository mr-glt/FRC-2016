
package org.usfirst.frc.team6027.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
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
	DoubleSolenoid ballPlungerSol = new DoubleSolenoid(1, 2);
	DoubleSolenoid dustPanSol = new DoubleSolenoid(3, 4);
	CANTalon flyWheel = new CANTalon(0);
	CANTalon dustPanTilt = new CANTalon(1);
	Potentiometer dustPanAngle;
	ADXRS450_Gyro gyro;
	DigitalInput upperLimit;
	DigitalInput bottomLimit;
	boolean buttonValue;
	boolean locksButtonValue;
	boolean locksButtonCloseValue;
	boolean inverted;
	boolean locksEngaded = false;
	double adjTilt;
	double Kp = 0.03;


	//For Talon PID
	StringBuilder _sb = new StringBuilder();
	int _loops = 0;
	
    public void robotInit() {
        
    	//Smart Dashboard Crap
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        //SmartDashboard.putData("Auto choices", chooser);
        
        //Assign objects
    	merlin = new RobotDrive(0,1); //Assign to robodrive on PWM 0 and 1
    	stick = new Joystick(0); //Assign to a joystick on port 0
    	upperLimit = new DigitalInput(1);
    	bottomLimit = new DigitalInput(2);
    	gyro = new ADXRS450_Gyro();
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
    }
    
    public void autonomousInit() {
    	//Smart Dashboard Crap
//    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
//		System.out.println("Auto selected: " + autoSelected);
		
    }

    public void autonomousPeriodic() {
/*    	switch(autoSelected) {
    	case customAuto:

            break;
    	case defaultAuto:
    	default:

            break;
    	}
  */
    	
        double angle = gyro.getAngle(); // get current heading

        SmartDashboard.putNumber("Error", (angle*Kp));
        merlin.drive(-0.2, -angle*Kp); // drive towards heading 0
    }


    public void teleopPeriodic() {
    	//Vars
    	//double controllerLY = controller.getRawAxis(1) * 0.56;
    	//double controllerRX = controller.getRawAxis(4) * 0.48;

    	//Drivetrain
    	//merlin.arcadeDrive(controllerLY, controllerRX);
    	
    	//Shooter Wheel
        flyWheel.set(stick.getY());
    
    
        //Dust Pan Angle
        //dustPanAngle = new AnalogPotentiometer(0, 360, 30);
        //double degrees = dustPanAngle.get();
        //SmartDashboard.putNumber("Dust Pan Angle", degrees);
        

    }
    

    public void testPeriodic() {
    	//Vars
    	double controllerLY = controller.getRawAxis(1) * 0.56;
    	double controllerRX = controller.getRawAxis(4) * 0.48;

    	
    	//Compressor
    	Compressor c = new Compressor(0);
    	c.setClosedLoopControl(true);
    	
    	//Drivetrain
    	merlin.arcadeDrive(controllerLY, controllerRX);
    	
    	//Dust Pan Tilt
    	
    	adjTilt = stick.getY() * 0.25;
    	if(upperLimit.get() == true){
    		if(stick.getAxis(AxisType.kY) < 0){
    			dustPanTilt.set(0); //Need to Change to Stick
    		}
    		else{
    			SmartDashboard.putString("Upper Limit Switch", "Upper Limit Hit");
    		}	
    	}
    	else{
    		dustPanTilt.set(0); //Need to Change to Stick
    	}
    	
        //Dust Pan Angle
        dustPanAngle = new AnalogPotentiometer(0, 360, 30);
        double degrees = dustPanAngle.get();
        SmartDashboard.putNumber("Dust Pan Angle", degrees);
    	
        if(locksEngaded == false){
        	dustPanTilt.set(stick.getY());
        }
        else{
        	SmartDashboard.putString("Movable Status", "LOCKES ENGADED CAN NOT MOVE!!!");
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
    	
    	//Ball Plunger
    	buttonValue = stick.getRawButton(1);
    	if(buttonValue == true){
    		ballPlungerSol.set(DoubleSolenoid.Value.kForward);
    		SmartDashboard.putString("Plunger Status", "Out");
    	}
    	else{
    		ballPlungerSol.set(DoubleSolenoid.Value.kReverse);
    		SmartDashboard.putString("Plunger Status", "Out");
    	}
    	
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
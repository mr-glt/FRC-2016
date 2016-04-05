package org.usfirst.frc.team6027.robot.subsystems;

import org.usfirst.frc.team6027.robot.RobotMap;
import org.usfirst.frc.team6027.robot.commands.DrivetrainDoNothing;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Drivetrain extends Subsystem {
    
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	double driveSchedulerX; //Used to hold X drive value so it can be modified multiple times
	double driveSchedulerY; //Used to hold Y drive value so it can be modified multiple times
    
	public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DrivetrainDoNothing());
    	//RobotMap.merlin.arcadeDrive(RobotMap.stick);
    }
    public void normalDrive(){
    	//Drivetrain
        	double controllerLY = RobotMap.controller.getRawAxis(4) * -1; //Invert the axis
        	double controllerRX = RobotMap.controller.getRawAxis(1) * -1; //Invert the axis
    
        	driveSchedulerX = controllerLY; //Drive to controller
        	driveSchedulerX = controllerRX; //Drive to controller
        	SmartDashboard.putString("Inverted Drive: ", "Off"); //Send status to SmartDashboard
        	SmartDashboard.putNumber("X Drive Value: ", driveSchedulerX); //Send status
        	SmartDashboard.putNumber("Y Drive Value: ", driveSchedulerY); //Send status
        	RobotMap.merlin.arcadeDrive(driveSchedulerX, driveSchedulerY); //Drive the robot
    }
    public void invertedDrive(){
    	//Drivetrain
        	double controllerLY = RobotMap.controller.getRawAxis(4) * 1; //Invert the axis
        	double controllerRX = RobotMap.controller.getRawAxis(1) * 1; //Invert the axis
    
        	driveSchedulerX = controllerLY; //Drive to controller
        	driveSchedulerX = controllerRX; //Drive to controller
        	SmartDashboard.putString("Inverted Drive: ", "On"); //Send status to SmartDashboard
        	SmartDashboard.putNumber("X Drive Value: ", driveSchedulerX); //Send status
        	SmartDashboard.putNumber("Y Drive Value: ", driveSchedulerY); //Send status
        	RobotMap.merlin.arcadeDrive(driveSchedulerX, driveSchedulerY); //Drive the robot
    }
    public void slowDrive(){
    	//Drivetrain
        	double controllerLY = RobotMap.controller.getRawAxis(4) * -0.7; //Invert the axis
        	double controllerRX = RobotMap.controller.getRawAxis(1) * 0.7; //Invert the axis
    
        	driveSchedulerX = controllerLY; //Drive to controller
        	driveSchedulerX = controllerRX; //Drive to controller
        	SmartDashboard.putString("Inverted Drive: ", "Off"); //Send status to SmartDashboard
        	SmartDashboard.putNumber("X Drive Value: ", driveSchedulerX); //Send status
        	SmartDashboard.putNumber("Y Drive Value: ", driveSchedulerY); //Send status
        	RobotMap.merlin.arcadeDrive(driveSchedulerX, driveSchedulerY); //Drive the robot
    }
    public void noDrive(){
    	//Drivetrain
        	SmartDashboard.putString("Inverted Drive: ", "Off"); //Send status to SmartDashboard
        	SmartDashboard.putNumber("X Drive Value: ", 0); //Send status
        	SmartDashboard.putNumber("Y Drive Value: ", 0); //Send status
        	RobotMap.merlin.arcadeDrive(0, 0); //Drive the robot
    }
}

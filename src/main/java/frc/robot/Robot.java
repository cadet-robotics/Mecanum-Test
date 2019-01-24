/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
public class Robot extends TimedRobot {
  //Port (PWM and USB) constants
  private static final int FRONT_LEFT_PORT = 4,
                           FRONT_RIGHT_PORT = 1,
                           BACK_LEFT_PORT = 3,
                           BACK_RIGHT_PORT = 2,
                           JOYSTICK_PORT = 1;
  
  //Modifier for motor speeds
  private static final double SPEED_MODIFIER = 0.75;
  
  //Mecanum driver and controllers
  private MecanumDrive mecDrive;
  private Joystick joystick;
  
  //Axis
  private double xAxis,
                 yAxis,
                 zAxis;
  
  @Override
  public void robotInit() {
    //Create motor objects
    PWMVictorSPX fl = new PWMVictorSPX(FRONT_LEFT_PORT),
                 fr = new PWMVictorSPX(FRONT_RIGHT_PORT),
                 bl = new PWMVictorSPX(BACK_LEFT_PORT),
                 br = new PWMVictorSPX(BACK_RIGHT_PORT);
    
    //Put motor objects in the mecanum driver and init controllers
    mecDrive = new MecanumDrive(fl, bl, fr, br);
    joystick = new Joystick(JOYSTICK_PORT);
    
    CameraServer camServer = CameraServer.getInstance();
    UsbCamera cam = camServer.startAutomaticCapture();
    cam.setResolution(1280, 720);
  }

  @Override
  public void teleopPeriodic() {
    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.
    
    //Use joystick
    double throttle = mapDouble(joystick.getRawAxis(3), -1, 1, 1, 0);
    
    xAxis = joystick.getRawAxis(0) * throttle;
    yAxis = -1 * joystick.getRawAxis(1) * throttle;
    zAxis = joystick.getRawAxis(2) * throttle;
    
    //Run the mecanum driver
    mecDrive.driveCartesian(xAxis * SPEED_MODIFIER, yAxis * SPEED_MODIFIER, zAxis * SPEED_MODIFIER);
  }
  
  double mapDouble(double val, double oldMin, double oldMax, double newMin, double newMax){
    return (((val - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
  }
}

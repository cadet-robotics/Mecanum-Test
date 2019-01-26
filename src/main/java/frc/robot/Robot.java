/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.IOException;
import java.util.Map;

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
  private static int FRONT_LEFT_PORT = 4,
                     FRONT_RIGHT_PORT = 1,
                     BACK_LEFT_PORT = 3,
                     BACK_RIGHT_PORT = 2,
                     JOYSTICK_PORT = 0;
  
  private static int X_AXIS_CONTROL = 0,
                     Y_AXIS_CONTROL = 1,
                     Z_AXIS_CONTROL = 2;
  
  //Modifier for motor speeds
  private static final double SPEED_MODIFIER = 0.75;
  
  //Mecanum driver and controllers
  private MecanumDrive mecDrive;
  private Joystick joystick;
  
  //Thing to load things
  ControlsLoader controlsLoader = new ControlsLoader();
  
  //Camera
  UsbCamera cam,
            cam2;
  
  //Axis
  private double xAxis,
                 yAxis,
                 zAxis;
  
  @Override
  public void robotInit() {
    //Load from config
    loadControls();
    loadMotorPorts();
    
    //Create motor objects
    PWMVictorSPX fl = new PWMVictorSPX(FRONT_LEFT_PORT),
                 fr = new PWMVictorSPX(FRONT_RIGHT_PORT),
                 bl = new PWMVictorSPX(BACK_LEFT_PORT),
                 br = new PWMVictorSPX(BACK_RIGHT_PORT);
    
    //Put motor objects in the mecanum driver and init controllers
    mecDrive = new MecanumDrive(fl, bl, fr, br);
    joystick = new Joystick(JOYSTICK_PORT);
    
    CameraServer camServer = CameraServer.getInstance();
    cam = camServer.startAutomaticCapture(0);
    
    //Fairly optimal resolution and fps settings
    //Gets <2 Mb/s
    cam.setFPS(15);
    cam.setResolution(320, 240);
  }

  @Override
  public void teleopPeriodic() {
    // Use the joystick X axis for lateral movement, Y axis for forward
    // movement, and Z axis for rotation.
    
    //Use joystick
    double throttle = mapDouble(joystick.getRawAxis(3), -1, 1, 1, 0);
    
    xAxis = joystick.getRawAxis(X_AXIS_CONTROL) * throttle;
    yAxis = -1 * joystick.getRawAxis(Y_AXIS_CONTROL) * throttle;
    zAxis = joystick.getRawAxis(Z_AXIS_CONTROL) * throttle;
    
    
    
    //Run the mecanum driver
    mecDrive.driveCartesian(xAxis * SPEED_MODIFIER, yAxis * SPEED_MODIFIER, zAxis * SPEED_MODIFIER);
  }
  
  /**
   * Loads the motor ports from the config file
   */
  void loadMotorPorts(){
    try{
      Map<String, Integer> motorMap = controlsLoader.load("MOTOR_PORTS");
      
      for(String s : motorMap.keySet()){
        System.out.println(s + ": " + motorMap.get(s));
        
        switch(s){
          case "front_left":
            FRONT_LEFT_PORT = motorMap.get(s);
            break;
          
          case "front_right":
            FRONT_RIGHT_PORT = motorMap.get(s);
            break;
          
          case "back_left":
            BACK_LEFT_PORT = motorMap.get(s);
            break;
          
          case "back_right":
            BACK_RIGHT_PORT = motorMap.get(s);
            break;
          
          default:
            System.err.println("Unrecognized motor id: " + s);
        }
      }
    } catch(IOException e){
      System.err.println("Could not load motor ports. Using preprogrammed defaults");
      e.printStackTrace();
    }
  }
  
  /**
   * Loads the controls from the config file
   */
  void loadControls(){
    //Load controls settings
    try{
      Map<String, Integer> controlsMap = controlsLoader.load("CONTROLS");
      
      for(String s : controlsMap.keySet()){
        System.out.println(s + ": " + controlsMap.get(s));
        
        switch(s){
          case "joystick":
            JOYSTICK_PORT = controlsMap.get(s);
            break;
          
          case "x_axis":
            X_AXIS_CONTROL = controlsMap.get(s);
            break;
          
          case "y_axis":
            Y_AXIS_CONTROL = controlsMap.get(s);
            break;
          
          case "z_axis":
            Z_AXIS_CONTROL = controlsMap.get(s);
            break;
          
          default:
            System.err.println("Unrecognized control id: " + s);
        }
      }
    } catch(IOException e){
      System.err.println("Could not load controls. Using programmed constants");
      e.printStackTrace();
    }
  }
  
  /**
   * Maps a value from range to range
   * 
   * @param val The value to map
   * @param oldMin The old range's minimum
   * @param oldMax The old range's maximum
   * @param newMin The new range's minimum
   * @param newMax The new range's maximum
   * @return The new mapped value
   */
  double mapDouble(double val, double oldMin, double oldMax, double newMin, double newMax){
    return (((val - oldMin) * (newMax - newMin)) / (oldMax - oldMin)) + newMin;
  }
}

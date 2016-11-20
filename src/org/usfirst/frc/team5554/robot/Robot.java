
package org.usfirst.frc.team5554.robot;


//import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
//import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Rect;
//import com.ni.vision.VisionException;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
	/*****************Setting The Program Ports***********************/
	//Setting Motor Constants
	 final int THROWER_PORT_UP = 6;
	 final int THROWER_PORT_DOWN = 4;
	 final int LeftPort = 1;
	 final int RightPort = 0;
	 final int PULLER_PORT = 5; //Feeder push/pulling
	 final int GRABBER_PORT = 3; //Feeder Angle
	 final int ANGLE_PORT = 2; //Shooter Angle
	 //final int ENCODER_SHOOTER_PORT = 1; //To Remove
	 //final int ENCODER_FEEDER_PORT = 0; //To Remove
	 
	 //Setting DIO
	 final int FEEDER_SAFETYBREAK_PORT = 1;
	 final int SHOOTER_SAFETYBREAK_PORT = 0;
	 
	 /************************************End**************************/
	 

	 
	 //Setting Up The JoySticks 
	 Joystick driveJoy;
	 Joystick operatorJoy;
	 
	 /******************Defining The Programs Flags**********************************/
	 private boolean IsReversed;
	 
	 private boolean _autoShooter;
	 private boolean _autoDriver;
	 
	 
	 /********************************Joystick Vars*********************/
	 private double speedAxis;
	 private double turnAxis;
	 private double scalerAxis;
	 private double graberAxis;
	 private double angle;
	 private boolean pullerButtonPull;
	 private boolean shoot;
	 private boolean flagSetTrue;
	 private boolean flagSetFalse;
	 private boolean pullerButtonPush;	
	 @SuppressWarnings("unused")
	private boolean press;
	 private boolean press2;//ignore the switches (like what was done in the camera section in teleop periodic)
	 private boolean ignoreSwitch=false;
	 private boolean ignoreSwitchButton;
	 @SuppressWarnings("unused")
	private boolean cameraSwitch;
	 /************************************End**************************/
	 
	 
	 
	 /************************Serial Vars******************************/
	 private byte[] readBuf;
	 static int i;
	 static int j;
	 int tankCounter;
	 int shootCounter;
	 MyThread myThread;
	 double shootAngle;
	 double feederAngle;
	 /************************************End**************************/
	 
	 
	 /*****************Auto Vars***************************************/
	 @SuppressWarnings("unused")
	private int stage;
	 /**********************End****************************************/
	 
	 //The Hand Made Classes
	 Feeder feeder;
	 Thrower thrower;
	 Tank tank;
	 //Setting The Camera
	 CameraServer server;
 	 Image frame;
	 USBCamera camera1;
	 //USBCamera camera2;
	 Rect y;
	 @SuppressWarnings("unused")
	private int cameraNumber=1;
	 /***************Nested Class For The Serial COMS***********************************/
	 private class MyThread extends Thread 
		{

			@Override
			public void run() {
				    SerialPort serial;
			    	serial = new SerialPort(9600,Port.kMXP);
			    	serial.setReadBufferSize(2);
			    	serial.disableTermination();
				while(true)
		    	{
		        	try{
		            readBuf = serial.read(2);
		        	}
		        	catch(RuntimeException o){}
		        	if(readBuf!=null && readBuf.length > 0)
		        	{
		        		switch(readBuf[0]){
		        			case '1':{
		        				SmartDashboard.putString("state 0","LookUp - 1");
		        				break;
		        				}
		        			case '2':{
		        				SmartDashboard.putString("state 0","LookDown - 2");
		        				break;
		        				}
		        			case '3':{
		        				SmartDashboard.putString("state 0","fastup - 3");
		        				break;
		        				}
		        			case '4':{
		        				SmartDashboard.putString("state 0","shoot - 4");
		        				break;
		        				}
		        		}
		        		switch(readBuf[1]){
		        			case '1':{
		        				SmartDashboard.putString("state 1","Left - 1");
		        				break;
		        				}
		        			case '2':{
		        				SmartDashboard.putString("state 1","Right - 2");
		        				break;
		        				}
		        			case'3':{
		        				SmartDashboard.putString("state 1","Fast Left - 3");
		        				break;
		        				}
		        			case '4':{
		        				SmartDashboard.putString("state 1","fast Right - 4");
		        				break;
		        				}
		        			case '5':{
		        				SmartDashboard.putString("state 1","Shoot - 5");
		        				break;
		        				}
		        			default:{ 
		        				SmartDashboard.putBoolean("Is false", true);
		        				SmartDashboard.putNumber("serial_1", i);
		        				i++;
		        				break;
		        				}
		        			}
		        	}  
		        	else
		        	{
		        		SmartDashboard.putBoolean("Is false", false);
		        		SmartDashboard.putNumber("DEBUG", j);
		        		j++;
		        	}
		    	}
		    }
		}
    public void robotInit() {
    	//Serial Initializing Variables
    	i=0;
    	j=0;
    	readBuf = new byte[256];//Buffer
    	myThread = new MyThread();//Thread
    	myThread.start();//starting the Thread
    	tankCounter = 0;
    	shootCounter = 0;
    	
    	
    	//Joystick initialization
    	driveJoy = new Joystick(0); //The Tank Drive Joystick
    	operatorJoy = new Joystick(1); //The Shooting Mechanism Joystick
    	
    	shootAngle = 0;
    	
    	feederAngle = 0;
    	
    	//camera initialization
    	//try{
    	//camera1 = new USBCamera("cam1");
    	//camera2 = new USBCamera("cam1");
    	//}
    	//catch(VisionException o){
    	//	System.out.println("Error: "+o.getLocalizedMessage());
    	//}
    	//camera1.openCamera();
    	server = CameraServer.getInstance();
    	server.setQuality(50);
    	server.startAutomaticCapture("cam1");
    	//frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
//    	rect = new Rect(340,330,30,30);//280
    	
        stage = 1;//autonomous Variables
        
        
        _autoShooter = false;
        _autoDriver = false;
        
        
        feeder = new Feeder(GRABBER_PORT,PULLER_PORT,FEEDER_SAFETYBREAK_PORT);//,ENCODER_FEEDER_PORT);//Setting Up The Feeder NEED TO ADD HALLEFECTPORT LAST PARAMETER
        thrower = new Thrower(THROWER_PORT_UP,THROWER_PORT_DOWN,ANGLE_PORT,SHOOTER_SAFETYBREAK_PORT);//,ENCODER_SHOOTER_PORT);//Setting Up The Thrower //Setting Up The Feeder NEED TO ADD SAFETYBREAK_PORT LAST PARAMETER
        tank = new Tank(LeftPort, RightPort);//Setting Up The Tank Drive
    }
    
    public void autonomousInit() {
    }

    public void disabledTeleop(){
    	
    }
    /**
     * This function is called periodically during autonomous
     * ***********************Autonomous for gate*************************
     */
    public void autonomousPeriodic()
    {/*
    	switch (stage)
    	{
    		case 1: 
    		feeder.autonomusGrabber();
    			break;
    		case 2:
    			tank.moveForward();
    			Timer.delay(0.5);
    			tank.stop();
    			break;	
    		case 3:
    			feeder.setGraber(-0.7, ignoreSwitch);
    			Timer.delay(0.5);
    			feeder.setGraber(0, ignoreSwitch);
    			break;
    		case 4:
    			tank.moveForward();
    			Timer.delay(1.5);
    			tank.stop();
    			break;
    		case 5:
    			tank.autonomousTurnLeft();
    			Timer.delay(0.7);
    			tank.stop();
    			break;
    		case 6:
    			thrower.angleChanger(-0.6,false);
    			Timer.delay(1.8);//changed from 1.35
    			thrower.angleChanger(-0.035,false);
    			break;
    		case 7:
    			autoShoot();
    			stage--;
    			break;
    		default:
    			stage=50;
    			break;
    	}
    	if (stage < 100)
    		stage++;
*/    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//camera1.startCapture();
    	System.out.println("The feeder state is: " + feeder.getMicroswitchState());
    	System.out.println("The shooter state is: " + thrower.getMicroswitchState());
    	//Setting The Variebles 
    	speedAxis = driveJoy.getRawAxis(1);
    	turnAxis = driveJoy.getRawAxis(2);
    	scalerAxis = driveJoy.getRawAxis(3);
    	graberAxis = operatorJoy.getRawAxis(1);
    	pullerButtonPull = operatorJoy.getRawButton(1);
    	pullerButtonPush = operatorJoy.getRawButton(2);
    	angle = -operatorJoy.getRawAxis(5);
    	shoot = operatorJoy.getRawButton(6);
    	flagSetTrue = driveJoy.getRawButton(7);
    	flagSetFalse = driveJoy.getRawButton(8);
    	
    	//auto Shoot Vars
    	_autoShooter = operatorJoy.getRawButton(3);
    	_autoDriver = driveJoy.getRawButton(12);
    	
    	ignoreSwitchButton=operatorJoy.getRawButton(8);
    	
    	/**camera code section**/
    	
    	
//    	cameraSwitch=operatorJoy.getRawButton(5);
//    	if(!cameraSwitch){
//    		press = false;
//    	}
//    	if(cameraSwitch && !press){
//    		press =true;
//    		if(cameraNumber==1){
//    			camera1.stopCapture();
//    			camera1.closeCamera();
//    			camera2.openCamera();
//    			camera2.startCapture();
//    			cameraNumber=2;
//    		}
//    		else{
//    			camera2.stopCapture();
//    			camera2.closeCamera();
//    		    camera1.openCamera();
//    			camera1.startCapture();
//    			cameraNumber=1;
//    		}
//    	}
//    	try{
//    	if(cameraNumber==1){
//    		camera1.getImage(frame);
//  //  		NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE,ShapeMode.SHAPE_RECT, 0f);
//    		server.setImage(frame);
//    	}
//    	else{
//    		camera2.getImage(frame);
//    		server.setImage(frame);
//    		cameraNumber=0;
//    	}
//    	}
//    	catch(VisionException v){}
    	
    	
    	if(!ignoreSwitchButton)
    		press2=false;
    	if(ignoreSwitchButton && !press2){
    		press2=true;
    		if(!ignoreSwitch)
    			ignoreSwitch=true;
    		else
    			ignoreSwitch=false;
    	}
    		
    		
    		
    	if(driveJoy.getRawButton(1) && (speedAxis>0.1 || speedAxis<0.1))
    	{
    		tank.switchFastGear();
    	}
    	else if (driveJoy.getRawButton(2) && (speedAxis>0.1 || speedAxis<0.1))
    	{
    		tank.switchPowerGear();
    	}
    	
    	if (!_autoDriver)
    	{
    		//Setting The Reverse Mod
    		if ( flagSetTrue )        
				IsReversed = true;
      		if ( flagSetFalse )        
      			IsReversed = false;
      		
      		tank.CombinedDrive( speedAxis, turnAxis ,scalerAxis , IsReversed);
    	}
    	else {
        	try{
        	//autoShoot();
        		autoDrive();
        	}
    	catch(ArrayIndexOutOfBoundsException o){}
    	}
    	if(!_autoShooter)
    	{
      		feeder.setGraber(graberAxis, ignoreSwitch);
      		if(pullerButtonPull){
    			feeder.pull();
    		}
    		else if(pullerButtonPush){
    			feeder.push();
    		}
    		else
    			feeder.stop();
    		thrower.angleChanger(angle, ignoreSwitch);
    		thrower.shoot(shoot);
    	}
    	else 
        	try{
        	//autoShoot();
        		autoShooter();
        	}
    	catch(ArrayIndexOutOfBoundsException o){}
    	
    	/*feederAngle = feeder.getShaftAngle();
    	try{
    	if(readBuf[0] == '4')
    		shootAngle = thrower.getShaftAngle();
    	}
    	catch(ArrayIndexOutOfBoundsException o)
    	{
    		
    	}
    	
    	SmartDashboard.putNumber("shooter angle", shootAngle);
    	SmartDashboard.putNumber("feeder angle", feederAngle);*/
    	
    	
    }
    
    public void autoDrive()
    {
    	switch(readBuf[1]){
		case '0':
    	{
    		break;
    	}
    	case '1'://turn left
		{
			tank.TurnLeft();
			tankCounter=0;
			break;
		}
    	case '2'://turn right
		{
			tank.TurnRight();
			tankCounter=0;
			break;
		}
    	case '3'://turn left fast
    	{
    		tank.TurnLeftFast();
    		tankCounter=0;
    		break;
    	}
    	case '4'://turn right fast
		{
			tank.TurnRightFast();
			tankCounter=0;
			break;
		}
    	case '5'://all good TURNING
		{
			tank.stop();
			Timer.delay(0.01);
			tankCounter++;
			
			if(tankCounter>=20 && shootCounter>=20)
			{	
	        	operatorJoy.setRumble(Joystick.RumbleType.kLeftRumble,1.0f);
	        	operatorJoy.setRumble(Joystick.RumbleType.kRightRumble,1.0f);
				tankCounter=0;
				shootCounter=0;
				tank.stop();
				thrower.shoot(true);
				Timer.delay(1);
				//feeder.setGraber(-0.6);
				//Timer.delay(0.35);
				feeder.pull();
				Timer.delay(1);
				thrower.shoot(false);
				Timer.delay(2);

	    		operatorJoy.setRumble(Joystick.RumbleType.kLeftRumble,0f);
	        	operatorJoy.setRumble(Joystick.RumbleType.kRightRumble,0f);
	    	
			}	
			break;
		}
		default:
		{
			tank.stop();
			break;
		}
    	}
    }
    public void autoShooter()
    {
    	switch(readBuf[0]){
    	case '0': //look up
		{
			break;
		}
    	case '1': //look up
		{
			thrower.lookUp();
			shootCounter=0;
			break;
		}
    	case '2'://look down
		{
			thrower.lookDown();
			shootCounter=0;
			break;
		}
    	case '3'://fast look up
		{
			thrower.FastLookSUp();
			shootCounter=0;
			break;
		}
    	case '4'://all good SHOOTING
		{
			thrower.angleChanger(0,ignoreSwitch);
			Timer.delay(0.01);
			shootCounter++;
			break;
		}
    	default:
    	{
			thrower.angleChanger(0,false);
		}
    	}
    }
    
    public void autoShoot()
    {
    	
    	/*Setting The Thrower******************************************/
    	switch(readBuf[0]){
    	case '0': //look up
		{
			break;
		}
    	case '1': //look up
		{
			thrower.lookUp();
			shootCounter=0;
			break;
		}
    	case '2'://look down
		{
			thrower.lookDown();
			shootCounter=0;
			break;
		}
    	case '3'://fast look up
		{
			thrower.FastLookSUp();
			shootCounter=0;
			break;
		}
    	case '4'://all good SHOOTING
		{
			thrower.angleChanger(0,ignoreSwitch);
			Timer.delay(0.01);
			shootCounter++;
			break;
		}
    	default:
    	{
			thrower.angleChanger(0,false);
		}
    	}
		/**Setting the Tank***********************************************************/
		switch(readBuf[1]){
		case '0':
    	{
    		break;
    	}
    	case '1'://turn left
		{
			tank.TurnLeft();
			tankCounter=0;
			break;
		}
    	case '2'://turn right
		{
			tank.TurnRight();
			tankCounter=0;
			break;
		}
    	case '3'://turn left fast
    	{
    		tank.TurnLeftFast();
    		tankCounter=0;
    		break;
    	}
    	case '4'://turn right fast
		{
			tank.TurnRightFast();
			tankCounter=0;
			break;
		}
    	case '5'://all good TURNING
		{
			tank.stop();
			Timer.delay(0.01);
			tankCounter++;
			
			if(tankCounter>=20 && shootCounter>=20)
			{	
	        	operatorJoy.setRumble(Joystick.RumbleType.kLeftRumble,1.0f);
	        	operatorJoy.setRumble(Joystick.RumbleType.kRightRumble,1.0f);
				tankCounter=0;
				shootCounter=0;
				tank.stop();
				thrower.shoot(true);
				Timer.delay(1);
				//feeder.setGraber(-0.6);
				//Timer.delay(0.35);
				feeder.pull();
				Timer.delay(1);
				thrower.shoot(false);
				Timer.delay(2);

	    		operatorJoy.setRumble(Joystick.RumbleType.kLeftRumble,0f);
	        	operatorJoy.setRumble(Joystick.RumbleType.kRightRumble,0f);
	    	
			}	
			break;
		}
		default:
		{
			tank.stop();
			break;
		}
		}
    	readBuf[0] = '0';
    	readBuf[1] = '0';
    	Timer.delay(0.03);
    	thrower.angleChanger(0,ignoreSwitch);
    	//tank.stop();
    	Timer.delay(0.01);
    }
    
    
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}

package org.usfirst.frc.team5554.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

public class Thrower {
	Victor Throwheelup;  
	Victor Throwheeldown;
	Victor angle;
	boolean Shoot; 
	DigitalInput SafetyBreak;
	AnalogInput encoder;
	
	public Thrower(int upperWheel ,int lowerWheel,int anglePort,int safetyBreakPort){//,int encoderPort){
		Throwheelup = new Victor(upperWheel);
		Throwheeldown = new Victor(lowerWheel);
		angle = new Victor(anglePort);
		SafetyBreak = new DigitalInput(safetyBreakPort);
		//encoder = new AnalogInput(encoderPort);
	}
	
	public void angleChanger(double speed,boolean ignoreSwitch){
		if(!ignoreSwitch)
		{
			if (SafetyBreak.get() || speed > 0)
				angle.set(speed);
			else if(speed < 0){
				angle.set(speed);
			}
			else{
				angle.set(0);
			}
		}
		else
			angle.set(-speed);
	}
	
	
	
	public void autonomusangleChange(double speed)
	{
		
	}
/*	public double getShaftAngle()
	{
		return (72 * encoder.getVoltage());
	}
	*/
	public void shoot(boolean shoot){
		if (shoot){
		Throwheelup.set(1);
		Throwheeldown.set(1);
	}
		else {
			Throwheelup.set(0);
			Throwheeldown.set(0);
		}
	}
	
	public void autonomousDown()
	{
			angle.set(0.5);
	}
	
	public void lookDown()
	{
		this.angleChanger(0.36,false);//338
	}
	public void lookUp()
	{
		this.angleChanger(-0.25,false);//15
	}
	public void FastLookSUp()
	{
		this.angleChanger(-0.3,false);//0.25
	}

    public boolean getMicroswitchState(){
    	return SafetyBreak.get();
    }
}

package org.usfirst.frc.team5554.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

public class Tank {

	private Victor Left;
	private Victor Right;
	private DoubleSolenoid gears;
	
    /**
     * 
     * @return Sets the Victor motors in the appropriate given ports. 
     * @param frontLeftPort is the PWM port for the front left Victor motor wheel.
     * @param rearLeftPort is the PWM port for the rear left Victor motor wheel.
     * @param frontRightPort is the PWM port for the front right Victor motor wheel.
     * @param rearRightPort is the PWM port for the rear right Victor motor wheel.
     * 
     **/
	public Tank(int LeftPort,int RightPort)
	{
		Left = new Victor(LeftPort);
		Right = new Victor(RightPort);
		gears = new DoubleSolenoid(0,1);
	}

	
	
	
	/**
	 * 
	 * @param Y the Y axis to be used for the driving system.
	 * @param Z the Z axis to be used for the driving system.
	 * @param S the scaler on the joystick. may be used as any axis, but axis 3 is best for this.
	 * @param flag determines if the driving system is inverted or not. true for inverted.
	 * 
	 */
	public void CombinedDrive(double Y, double Z, double S, boolean flag)
	{
		//y=0.5 z= 1
		double left;
		double right;
		double s=(S+1)/2;
		if (flag){
		left=(-Y+Z)*s;
		right=(Y+Z)*s;
		}
		else
		{
			left=(-Y-Z)*-s;
			right=(Y-Z)*-s;
		}
	
		if (left > 1)left=1;
		if (left < -1)left=-1;
		if (right > 1)right=1;
		if (right < -1)right=-1;
		if (flag)
		{
	  		Left.set(left);
	  		Right.set(right);
		}
		else
		{
			Left.set(left);
		    Right.set(right);
		}
	}
	public void moveForward()
	{
		CombinedDrive(0.5,0,1,true);
	}
	
	public void TurnLeft()
	{
		this.CombinedDrive(0, -0.15, 1, true);  //0.12
	}
	public void TurnRight()
	{
		this.CombinedDrive(0, 0.15, 1, true);  //0.12
	}

	public void stop() {
		  Left.set(0);
		  Right.set(0);
	}
	public void TurnRightFast() {
		CombinedDrive(0, 0.15, 1, true);//0.18
	}
	public void TurnLeftFast() {
		CombinedDrive(0, -0.15, 1, true);//0.18
	}
	
	public void autonomousTurnLeft()
	{
		this.CombinedDrive(0, -1, 1, true);
	}
	public void switchPowerGear()
	{
		gears.set(DoubleSolenoid.Value.kForward);
		Timer.delay(0.01);
		gears.set(DoubleSolenoid.Value.kOff);
	}
	public void switchFastGear()
	{
		gears.set(DoubleSolenoid.Value.kReverse);
		Timer.delay(0.01);
		gears.set(DoubleSolenoid.Value.kOff);
	}
}

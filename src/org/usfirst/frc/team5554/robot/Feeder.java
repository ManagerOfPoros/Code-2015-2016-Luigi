package org.usfirst.frc.team5554.robot;

//import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

public class Feeder 
{
	private Victor graber; //Victor For The Graber angle
    private Victor puller; //Victor For Pulling System
    private DigitalInput mictoSwitch;
    //private AnalogInput encoder;
    
    public Feeder(int graberPort,int pullerPort, int switchPort)//,int encoderPort)
    {
    	graber = new Victor(graberPort);
    	puller = new Victor(pullerPort);
    	mictoSwitch = new DigitalInput(switchPort);
    	//encoder = new AnalogInput(encoderPort);
    }
    
    /**
     * setGrabber
     * set the angel for the grabber
     * @param graberAxis   set The angel of the feeder
     * @param ignoreSwitch If true: Ignore the micro switches
     */
    public void setGraber(double graberAxis,boolean ignoreSwitch)
    {
    	if(!ignoreSwitch)
    	{
			if (mictoSwitch.get() || graberAxis < 0)
				graber.set(graberAxis);
			else if(graberAxis > 0){
				graber.set(graberAxis);
			}else{
				graber.set(0);
			}
        }
    	else
    		graber.set(-graberAxis);
    }
    
    public void pull(){
    	puller.set(-1);
    }
    public void stop() {
    	puller.set(0);
    }
    public void push() {
    	puller.set(1);
    }
    
    
/*	public double getShaftAngle()
	{
		return (72 * encoder.getVoltage());
	}*/
    
    public void autonomusGrabber()
    {
    	while ( !mictoSwitch.get() )
    	{
    		setGraber(1, false);
    	}
    	setGraber(0, false);
    }
    
    public boolean getMicroswitchState(){
    	return mictoSwitch.get();
    }
}
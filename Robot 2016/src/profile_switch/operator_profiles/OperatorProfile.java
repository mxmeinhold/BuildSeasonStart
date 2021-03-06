package profile_switch.operator_profiles;

import Variables.Joysticks;
import Variables.Motors;
import Variables.Sensors;
import edu.wpi.first.wpilibj.CANTalon;

/**
 *@deprecated
 */
public class OperatorProfile {
	
	//Control Variables (Buttons)
	public int shootButton = Joysticks.X;
	public int intakeButton = Joysticks.Y;
	public int outtakeButton = Joysticks.A;
	/** @deprecated*/
	public int armUpButton;
	/** @deprecated*/
	public int armDownButton;
	public int tapeArmExtend = Joysticks.RIGHT_BUMPER;
	public int tapeArmRetract = Joysticks.LEFT_BUMPER;
	public int tapeArmRotUp = Joysticks.RIGHT_TRIGGER;
	public int tapeArmRotDown = Joysticks.LEFT_TRIGGER;
	
	public int overrideKillModifier = Joysticks.START;
	
	public OperatorProfile()
	{
		
	}
	
	
	//TODO Fill in then code and add checks for limit switches.
	public void buttonCheck() {
		if (Joysticks.joyOp.getRawButton(shootButton)) {
			//shoot
		}
		if (Joysticks.joyOp.getRawButton(intakeButton)) {
			//run intake motor to pull ball in
		}
		else if (Joysticks.joyOp.getRawButton(outtakeButton)) {
			//run intake motor to push ball out
		}
		if (Joysticks.joyOp.getPOV() == 0) {
			//rotate intake arm up
		}
		else if (Joysticks.joyOp.getPOV() == 4) {
			//rotate intake arm down
		}
		if (Joysticks.joyOp.getRawButton(tapeArmExtend)) {
			//extend tape measure arm
		}
		else if (Joysticks.joyOp.getRawButton(tapeArmRetract)) {
			//retract tape measure arm
		}
		if (Joysticks.joyOp.getRawButton(tapeArmRotUp)) {
			//rotate tape arm up
		}
		else if (Joysticks.joyOp.getRawButton(tapeArmRotDown)) {
			//rotate tape arm down
		}
		if (Joysticks.joyOp.getRawButton(overrideKillModifier)) {
			if (Joysticks.joyOp.getRawButton(shootButton)) {
				//stops the shoot motor
			}
			if (Joysticks.joyOp.getRawButton(intakeButton)) {
				//stops intake
			}
			if (Joysticks.joyOp.getRawButton(outtakeButton)) {
				//stops the outtake
			}
		}
	}
	
	public void intake()
	{
		if(!Sensors.limitSwitch.get())
		{
			
		}
	}
	
	//Act Methods TODO Rewrite these to fit the new system and robot.
//	public void shoot()
//	{
//		//Normal shoot (buttons x and b)
//		if(Joysticks.joyOp.getRawButton(Joysticks.X) || Joysticks.joyOp.getRawButton(Joysticks.B))
//				System.out.print("");//code to shoot
//	}
//	
//	public void arm()
//	{
//		//Up arm (button y)
//		if(Joysticks.joyOp.getRawButton(Joysticks.Y))
//				System.out.print("");//code to arm
//		//Down arm (button a)
//		if(Joysticks.joyOp.getRawButton(Joysticks.A))
//				System.out.print("");//code to reverse arm
//	}
//	
//	public void intake()
//	{
//		//intake (button lb)
//		//Starts intake on left bumper
//		if(Joysticks.joyOp.getRawButton(Joysticks.LEFT_BUMPER)) {
//			startMotor(Motors.motorArmIntake, Motors.FULL_POWER);
//			startMotor(Motors.motorChassisIntake, Motors.FULL_POWER);
//		}
//		
//		//Stops intake on left/right bumper
//		if (Joysticks.joyOp.getRawButton(Joysticks.LEFT_BUMPER) && Joysticks.joyOp.getRawButton(Joysticks.RIGHT_BUMPER)) {
//			stopMotor(Motors.motorArmIntake);
//			stopMotor(Motors.motorChassisIntake);
//		}
//		
//		//Stops motor if intake switch is it
//		else if (Motors.limitSwitch.get()) {
//			stopMotor(Motors.motorArmIntake);
//			stopMotor(Motors.motorChassisIntake);
//		}
//				
//		//outtake (button rb)
//		if(Joysticks.joyOp.getRawButton(Joysticks.RIGHT_BUMPER))
//				System.out.print("");//code to reverse intake
//	}
//	
//	public void intakeArm()
//	{
//		//Forward intakeArm (button lt)
//		if(Joysticks.joyOp.getRawButton(Joysticks.LEFT_TRIGGER))
//				System.out.print("");//code to intakeArm
//		//Reverse intakeArm (button rt)
//		if(Joysticks.joyOp.getRawButton(Joysticks.RIGHT_TRIGGER))
//				System.out.print("");//code to reverse intakeArm
//	}
//	
//	private void startMotor(CANTalon m, double speed) {
//		m.set(speed);
//	}
//	
//	private void stopMotor(CANTalon m) {
//		m.set(Motors.NO_POWER);
//	}
}
package auto;

import variables.Motors;
import variables.Sensors;
import auto.util.Movement;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Umbrella class for all defense classes that allow FRC 1111's robot, Griffon, to cross over defenses in autonomous mode
 */
public class Autonomous {
	
	/**
	 * Distances must be in inches.
	 * TODO must calibrate all
	 */
	public class Distances {
		public static final double distFromDefense = 1;
		public static final double distFromEdge = 1;
		public static final double distFromTowerEdge = 1;
		public static final double distFromCastle = 1;
		public static final double distFromTowerCenter = 1;
	}
	
	/**
	 * TODO Must be calibrated if equal to 1 and not 1.0
	 */
	public class Timings {
		public static final double timeAcrossLowBar = 1, timeAcrossCheval = 1, timeAcrossMoat = 1.75, timeAcrossPortcullis = 1, timeAcrossRamparts = 1.75, 
				timeAcrossRockWall = 1.75, timeAcrossRoughTerrain = 1.75;
		public static final double timeToShootPosFromCenterLine = 1, timeToShootPosFromEdge = 1;
		public static final double timeToCenterLineFromPos3 = 1, timeToCenterLineFromPos5 = 1;
		public static final double shooterRevUpTime = 1; 
		public static final double timeToLowerArm = 2;
		public static final double timeToDefense = 4; 
		public static final double timeToRaiseArm = 1; 
		public static final double timeToPortcullis = 1;
		public static final double timeToEjectBall = 1;
		public static final double timeToDropPos = 1;
	}
	
	

	/** The position the robot is in when Autonomous starts */
	protected int pos;

	//Identifiers
	/** The name of the defense that the robot is going to cross */
	String defenseType;
	public String castle = "castle";
	public String towerEdge = "edge firing position";
	public String towerCenter = "center firing position";
	public String edge = "edge of field";
	/** Centerline of the field*/
	public String centerline = "centerline";
	public String portcullis = "portcullis";
	public String dropPos = "drop position";
	public String neutralZone = "neutral zone";
	
	//PID arm control variables TODO calibrate
	protected double armDownPos = 1;
	protected double armUpPos = 1;
	double currentArmPos = 0;
	
	//Pathing indicators
	/** If the robot is to go the edge of the field */
	protected boolean goToEdge = false;
	/** If the robot is to go the centerline of the field */
	protected boolean goToCenterline = false;
	/** If the robot is to go straight to the tower */
	protected boolean goStraight = false;
		
	//General indicators
	protected boolean isPidEnabled;
	protected boolean isArmLowered = false;
	protected boolean isArmRaised = false;
	protected boolean isAtDefense = false;
	protected boolean hasCrossedDefense = false;
	protected boolean isAtCenterFiringPos = false;
	protected boolean shooting = false;
	protected boolean isBallDropped = false;
	protected boolean isInDropPos = false;
	protected boolean hasStartedMoving = false;
	protected boolean isInNeutralZone = false;

	//Edge indicators
	protected boolean isAtEdgeOfField = false;
	protected boolean isAtEnemyCastle = false;
	/** If the robot is in the edge firing position */
	protected boolean isInEdgeFiringPos = false;

	//Centerline indicators
	protected boolean isAtCenterline = false; //The centreline is the tape holding the 2 carpets together.
	
	private double timeToGetAcrossDefense;
	
	
	//*****START CONSTRUCTORS*****
	
	
	/**
	 * @param p the position of the defense that will be crossed by the robot
	 * @param d the type of defense that the robot will cross
	 */
	public Autonomous(int p, String d) {
		pos = p; defenseType = d; 
	}
	
	/**
	 * @param p the position of the defense that will be crossed by the robot
	 * @param d the type of defense that the robot will cross
	 * @param e if PID control on the arm is enabled
	 */
	public Autonomous(int p, String d, boolean e) {
		pos = p; defenseType = d; isPidEnabled = e;
	}
	
	/**
	 * @param d the type of defense that the robot will cross
	 * @param e if PID control on the arm is enabled
	 */
	public Autonomous(String d, boolean e) {
		defenseType = d; isPidEnabled = e;
	}
	
	/**
	 * @param p the position the robot is starting in
	 * @param e if PID control on the arm is enabled
	 */
	public Autonomous(int p, boolean e) {
		pos = p; isPidEnabled = e;
	}
	
	
	//*****END CONSTRUCTORS*****
	
	
	
	//*****START BASIC AUTONOMOUS METHODS*****
	
	
	/**
	 * Method that shoots the ball at the high goal
	 */
	public void shoot() {
		moveToFiringPosition(timeToGetAcrossDefense);
		fire();
	}
	
	/**
	 * Method that crosses the defense in and then moves into firing position
	 * @param time the time the robot is to move for in order to get across the defense
	 */
	public void moveToFiringPosition(double time) {
		determinePathing();
		timeToGetAcrossDefense = time;
		
		//Crossing defense
		if (!hasCrossedDefense) { //Tests if the robot hasn't crossed the defense yet
			crossDefense(time);
		}
		
		moveToFiringPosition();
	}
	
	/**
	 * Method that moves the robot into firing position the normal way (no special commands to be executed)
	 */
	public void moveToFiringPosition() {
		if (!hasStartedMoving) {
			determinePathing();
		}
		
		//NOTE: Only activates if robot is in position 2
		if (goToEdge) { //Tests if robot needs to go to the edge of the field
			//Moving to the edge of the field
			if (!isAtEdgeOfField) { //Tests if the robot has not been to the edge of the field
				moveTo(edge);
			}
			
			//Moving to the castle
			else if (isAtEdgeOfField && !isAtEnemyCastle) { //Tests if the robot has been to the edge of the field, but hasn't been to the castle
				moveTo(castle);
			}
			
			//Moving to the edge firing position
			else if (isAtEnemyCastle && !isInEdgeFiringPos) { //Tests if the robot has been to the castle but isn't in firing position
				moveTo(towerEdge);
			}
		}
		
		//NOTE: Only activates if robot is in position 3, 4, or 5
		else if (goToCenterline) { //Tests if the robot needs to go to the centerline of the field
			//Moving to the centerline of the field
			if (!isAtCenterline && !goStraight) { //Tests if the robot has been to the centerline yet
				moveTo(centerline);
			}
			
			//Moving to the center firing position
			else if ((isAtCenterline || goStraight) && !isAtCenterFiringPos) { //Tests if the robot is at the centerline/needs to go straight and isn't in firing position
				moveTo(towerCenter);
			}
		}
		
		updateDashboard();
	}
	
	/**
	 * Method that crosses a defense a normal way (no special commands to be executed)
	 * @param time the time that the robot is to move for in order to get across the defense
	 * @param reverse if the robot will drive in reverse
	 * @note if any special things must be done to cross the defense, this method must be overridden
	 */
	public void crossDefense(double time) {
		//Lowering arm
		if (!isArmLowered) { //Tests if the arm has been lowered yet
			lowerArm();
		}
		
		//Getting to the defense
		else if (isArmLowered && !isAtDefense) { //Tests if the arm has been lowered and the robot hasn't been to the defense
			reach();
		}
				
		//Crossing the defense
		else if (isAtDefense && !hasCrossedDefense) { //Tests if the robot has been to the defense but hasn't crossed it yet
			hasCrossedDefense = moveFor(time, false);
		}
	}
	
	/**
	 * Method that fires a ball into the high goal
	 */
	public void fire() { //TODO there needs to be a shutdown at some point for this.
		shooting = true;
		Motors.motorShooter.set(Motors.SHOOTER_POWER * Motors.SHOOTER_OPTIMAL_MAXIMUM_VOLTAGE);
		Timer.delay(Timings.shooterRevUpTime);
		Motors.motorInnerIntake.set(Motors.INNER_INTAKE_POWER);
	}
	
	/**
	 * Method that just reaches the defense, does not cross it.
	 */
	public void reach() {
		if (!isAtDefense) 
			isAtDefense = moveFor(Timings.timeToDefense, false);
	}
	
	/**
	 * Method that crosses the LOW BAR defense and drops a ball
	 */
	public void crossDefenseDropBall() {
		//Crossing defense
		if (!hasCrossedDefense) { //Tests if the defense has been crossed
			crossDefense(Timings.timeAcrossLowBar);
		}
		
		//Raising arm	
		else if (hasCrossedDefense && !isArmRaised) { //Tests if the robot has crossed the defense and arm isn't raised
			raiseArm();
		}
		
		//Moving to drop position
		else if (isArmRaised && !isInDropPos) {
			isInDropPos = moveFor(Timings.timeToDropPos, true);
		}
		
		//Dropping ball
		else if (isArmRaised && !isBallDropped) { //Tests if the arm is raised but ball isn't dropped yet
			if (Timer.getMatchTime() >= 15 - Timings.timeToEjectBall) { //Tests if time passed is less than that it takes to eject the ball form the robot
				Motors.motorInnerIntake.set(-Motors.INNER_INTAKE_AUTO_POWER);
			}
			
			else {
				isBallDropped = true;
				isAtDefense = false;
				Motors.motorInnerIntake.set(Motors.NO_POWER);
			}
		}
		
		//Moving back to the defense
		else if (isBallDropped && !isInNeutralZone) {
			moveTo(neutralZone);
		}
	}
	
	
	//*****END BASIC AUTONOMOUS METHODS*****
	
	
	//*****START MOVE METHODS*****
	
	
	/**
	 * Method that moves the robot to a given object
	 * @param obj the object the robot is to move to
	 * @note method automatically handles turning to the object
	 */
	public void moveTo(String obj) { //TODO timing issue fix!!!!!!!!!!!!!!!!!!!!!!!!
		double dir = checkDirections(obj); //Direction back of robot needs to be oriented
		double dist = checkDistances(obj); //Distance from the object the robot needs to be
		boolean isInPos = false; //If the robot is in position
		
		//Orienting back of robot to the given object
		if (!isInRangeOf(dir)) { //Tests if the robot is in range of centerline to the tower
			Movement.orient(dir, Motors.AUTO_DEFAULT_TURN_SPEED);
		}
		
		//Moving to centerline
		else if (obj.equals(centerline) && !isInPos) { //Tests if the robot is to go to the centerline and isn't in position yet
			//Moving to centerline from position 3
			if (pos == 3) { //Tests if the robot is in position 3
				isInPos = moveFor(Timings.timeToCenterLineFromPos3, true);
			}
			
			//Moving to centerline from position 5
			else if (pos == 5) { //Tests if the robot is in position 5
				isInPos = moveFor(Timings.timeToCenterLineFromPos5, true);
			}
		}
		
		//Moving to edge of portcullis in order to raise arm and get robot under
		else if (obj.equals(portcullis) && !isInPos) { //Tests if the robot is to go to the centerline and isn't in position yet
			isInPos = moveFor(Timings.timeToPortcullis, true);
		}
		
		//Moving to drop position from the low bar
		else if (obj.equals(dropPos) && !isInPos) { //Tests if the robot is to go to the drop position but isn't in position yet
			isInPos = moveFor(Timings.timeToDropPos, false);
		}
		
		//Updating statuses and stopping drive motors
		else if (isCloseTo(dist)) { //Tests if robot is near tower and isn't in firing position
			isInPos = true;
			updateStatus(obj, isInPos);
			Movement.stopDriveMotors();
		}
		
		//Driving backwards into position
		else if (!isInPos) { //Tests if robot is far from the tower and isn't in firing position
			Movement.driveBackwards(Motors.AUTO_DEFAULT_DRIVE_SPEED);
		}
	}
	
	/**
	 * Method that checks the direction the robot needs to move in in order to orient itself properly
	 * @param obj the object that the robot is to orient to
	 * @return the angle (in degrees) the robot needs to be oriented to
	 * @note orients the rear of the robot to the appropriate orientation
	 */
	double checkDirections(String obj) {
		if (obj.equals(castle)) { //Tests if the robot's back needs to be oriented to the castle
			return 180;
		}
		
		else if (obj.equals(towerCenter)) { //Tests if the robot's back needs to be oriented to the center firing position
			return 180;
		}
		
		else if (obj.equals(towerEdge)) { //Tests if the robot's back needs to be oriented to the edge firing position
			return -90;
		}
		
		else if (obj.equals(edge)) { //Tests if the robot's back needs to be oriented to the edge of the field
			return -90;
		}
		
		else if (pos == 3) { //Tests if the robot's back needs to be oriented to the centerline from position 3
			return -90;
		}
		
		else if (pos == 5) { //Tests if the robot's back needs to be oriented to the centerline from position 5
			return 90;
		}
		
		else if (obj.equals(dropPos)) { //Tests if the robot needs to be oriented to the drop position
			return 45;
		}
		
		else if  (obj.equals(neutralZone)) {
			return 180;
		}
		
		else {
			return -1;
		}
	}
	
	/**
	 * Method that checks the distance the robot needs to be from the given object
	 * @param obj the object that the robot is to orient to
	 * @return the distance the robot needs to be from the given object
	 */
	double checkDistances(String obj) {
		if (obj.equals(castle)) { //Tests if the robot needs to be moved near the castle
			return Distances.distFromCastle;
		}
		
		else if (obj.equals(towerCenter)) { //Tests if the robot needs to be moved near the center firing position
			return Distances.distFromTowerCenter;
		}
		
		else if (obj.equals(towerEdge)) { //Tests if the robot needs to be moved near the edge firing position
			return Distances.distFromTowerEdge;
		}
		
		else if (obj.equals(edge)) { //Tests if the robot needs to be moved near the edge of the field
			return Distances.distFromEdge;
		}
		
		else {
			return -1;
		}
	}
	
	/**
	 * Method that moves the robot for a given time
	 * @param time the time the robot is to move for
	 * @param reverse if the robot is driving in reverse
	 * @return if the robot has traveled for the allotted time
	 * @precondition robot must be facing backwards
	 * @note this method handles stopping the drive motors
	 */
	public boolean moveFor(double time, boolean reverse) {		
		if (Timer.getMatchTime() >= 15 - time) 
		{ //Tests if the time passed is less than the time it takes to cross the defense and if the robot is turned around
			if (reverse) 
				Movement.driveBackwards(Motors.AUTO_DEFAULT_DRIVE_SPEED);
			else 
				Movement.driveForwards(Motors.AUTO_DEFAULT_DRIVE_SPEED);
		}else 
		{
			Movement.stopDriveMotors();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method that tests if the robot is at or under the given distance from an object
	 * @param dist the distance from the object that robot is supposed to be from
	 * @return if the robot is at or under the given distance from an object
	 */
	protected boolean isCloseTo(double dist) {
		if (Sensors.getUltraAverage() <= dist)  //Tests if close to given distance
			return true;
		else 
			return false;
	}
	
	
	//*****END MOVE METHODS*****
	
	
	//*****START TURN METHODS*****
	
	
	/**
	 * Method that tests if the robot is orientated at the given angle within a certain range
	 * @param dir the angle the robot is supposed to be oriented to
	 * @return if the robot is oriented to the give direction
	 */
	protected boolean isInRangeOf(double dir) {
		double currentDir = Sensors.navX.getYaw();
	
		if (currentDir <= dir + Sensors.navXRange && currentDir >= dir - Sensors.navXRange) { //Test if the current direction of the robot is in the +/- range of the desired direction
			return true;
		}
		
		else {
			return false;
		}
	}
	
	//*****END TURN METHODS*****
	
	
	//*****START ARM METHODS*****
	
	
	/**
	 * Method that tests if the arm needs to be lowered with PID or timing
	 */
	public void lowerArm() {
		if (isPidEnabled) { //Tests if at the edge of the defense and PID control is enabled for the arm
			moveArmPID(armDownPos);
			checkArmPos(armDownPos);
		}
		
		else {
			moveArmTiming(Timings.timeToLowerArm);
		}
	}
	
	/**
	 * Method that lowers the arm in order to cross the defense using PID control
	 */
	protected void moveArmPID(double armPos) {
		if (currentArmPos < armPos) { //Tests if the current position of the arm is less than the target position
			currentArmPos += Motors.pidChangeRate; //Increase the current arm position by the change rate
			Motors.motorArm.setSetpoint(currentArmPos);
		}
			
		else if (currentArmPos > armPos) { //Tests if the current position of the arm is greater than the target position
			currentArmPos -= Motors.pidChangeRate; //Decrease the current arm position by the change rate
			Motors.motorArm.setSetpoint(currentArmPos);
		}
	}
	
	/**
	 * Method that checks the position of the arm position
	 * @param armPos the position the arm is supposed to be in
	 */
	protected void checkArmPos(double armPos) {
		if (currentArmPos >= armPos - Motors.i && currentArmPos <= armPos + Motors.pidChangeRate) { //Tests if the arm is lowered to the correct position
			if (armPos == armDownPos) { //Tests if the arm is supposed to be lowered
				isArmLowered = true;
			}
			
			else if (armPos == armUpPos) { //Tests if the arm is supposed to be raised
				isArmRaised = true;
			}
		}
	}
	
	/**
	 * Method that lowers the arm in order to cross the arm using timing control
	 */
	protected void moveArmTiming(double performTime) {		
		if (Timer.getMatchTime() >= 15.0 - performTime) { //Tests if the time passed is less than the time it takes to lower the arm
			Motors.motorArm.set(Motors.ARM_DOWN_POWER);
		}
			
		else {
			Motors.motorArm.set(Motors.NO_POWER);
		}
	}
	
	/**
	 * Method that raises the arm of the robot with timing or PID control
	 */
	protected void raiseArm() {
		if (isPidEnabled) { //Tests if PID control on the arm is enabled
			moveArmPID(armUpPos);
			checkArmPos(armUpPos);
		}
		
		else {
			moveArmTiming(Timings.timeToRaiseArm);
		}
	}
	
	
	//*****END ARM METHODS*****
	
	
	//*****START DISPLAY/STATUS UPDATE METHODS****
	
	
		/**
		 * Method that updates the dashboard with status signals of the robot's progress TODO Only for testing
		 */
		public void updateDashboard() {
			SmartDashboard.putBoolean("PID Enabled:", isPidEnabled);
			SmartDashboard.putBoolean("Arm Lowered:", isArmLowered);
			SmartDashboard.putBoolean("At Defense:", isAtDefense);
			SmartDashboard.putBoolean("Crossed Defense:", hasCrossedDefense);
			
			if (goToEdge) { //Tests if the edge status need to be displayed
				SmartDashboard.putBoolean("At Edge:", isAtEdgeOfField);
				SmartDashboard.putBoolean("At Enemy Castle:", isAtEnemyCastle);
				SmartDashboard.putBoolean("At Edge Firing Position:", isInEdgeFiringPos);
			}
			
			else if (goToCenterline || goStraight) { //Tests if the center or straight statuses need to be displayed 
				SmartDashboard.putBoolean("At Centerline:", isAtCenterline);
				SmartDashboard.putBoolean("At Center Firing Position:", isAtCenterFiringPos);
				
			}
			
			SmartDashboard.putBoolean("Shooting:", shooting);
			
			if (isPidEnabled) { //Tests if PID statuses need to be displayed
				SmartDashboard.putNumber("Current Arm Position:", currentArmPos);
				SmartDashboard.putNumber("Set Arm Position:", armDownPos);
			}
			
			if (defenseType.equalsIgnoreCase("portcullis")) { //Tests if portcullis status need to be displayed
				SmartDashboard.putBoolean("Arm Is Raised:", isArmRaised);
			}
		}
		
		/**
		 * Method that updates the status of field variables
		 * @param obj the object the robot is moving to
		 * @param status the status of the variable associated with the object
		 */
		public void updateStatus(String obj, boolean status) {
			if (obj.equals(castle)) { //Tests if the castle status needs to be updated
				isAtEnemyCastle = status;
			}
			
			else if (obj.equals(centerline)) { //Tests if the centerline status needs to be updated
				isAtCenterline = status;
			}
			
			else if (obj.equals(edge)) { //Tests if the edge status needs to be updated
				isAtEdgeOfField = status;
			}
			
			else if (obj.equals(towerCenter)) { //Tests if the center firing position status needs to be updated
				isAtCenterFiringPos = status;
			}
			
			else if (obj.equals(towerEdge)) { //Tests if the edge firing position status needs to be updated
				isInEdgeFiringPos = status;
			}
			
			else if (obj.equals(neutralZone)) {
				isAtDefense = status;
			}
		}
		//*****END DISPLAY/STATUS UPDATE METHODS*****
		
		
		//*****START DETERMINATION METHODS*****
		
		
		/**
		 * Method that determines the path the robot will take based on its position
		 */
		void determinePathing() {
			if (pos == 2) //Tests if the robot is in position 2
				goToEdge = true;
			else if (pos == 3) //Tests if the robot is in position 3
				goToCenterline = true;

			else if (pos == 4) { //Tests if the robot is in position 4
				goStraight = true;
				isAtCenterline = true;
			}else if (pos == 5)  //Tests if the robot is in position 5
				goToCenterline = true;
		}
		
		
		//*****END DETERMINATION METHODS*****
}
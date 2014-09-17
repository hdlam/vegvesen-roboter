/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

import java.awt.Point;
import java.util.ArrayList;

/**
 * This class should work as the brains of the Robots.
 *
 * @author Magnus Hu
 */
public class Robot {

	Terrain map;
	int mapTarget;
	double Xpos;
	double Ypos;
	double currentAngle; //in radians
	double targetAngle;
	Point target;
	double targetDistance;
	double targetX;
	double targetY;
	double speed = 0;
	double mxspd = 50;
	double accel = 150000; 
	double deacc = 50;
	double turn = 0;
	double mxturn = 1;
	double lookAhead = mxspd * 4;
	Robot robotAhead;
	int col, driveAheadCounter;
	/*
	 * 	double speed = 0;
	double mxspd = 50;
	double accel = 1000000000; 
	double deacc = 15;
	double turn = 0;
	double mxturn = 1;
	double lookAhead = mxspd * 4;
	Robot robotAhead;
	 */
	
	
	//for catching orbit behavoirs
	double spinCounter = 0;
	boolean goingLeft = true;

	public Robot(double initX, double initY, double initAngle, Terrain terrain) {
		Xpos = initX;
		Ypos = initY;
		currentAngle = initAngle;
		map = terrain;
		findClosestPoint();
		col = driveAheadCounter = 0;
	}
	
	public int getCol(){
		return col;
	}
	
	public void moveCalc() {
		targetDistance = Math.sqrt((targetY - Ypos) * (targetY - Ypos) + (targetX - Xpos) * (targetX - Xpos));

		targetAngle = Math.atan2((targetY - Ypos), (targetX - Xpos));
		
		turn = mxturn * Math.sqrt(speed / mxspd);

		refractor();
	}

	void move(long delta) {
		//start by moving
		double step = ((double) delta) / 1000d;
		Xpos += Math.cos(currentAngle) * speed * step;
		Ypos += Math.sin(currentAngle) * speed * step;
		moveCalc();


		//Using Brooks subsumption
		if (redLightAhead() && mapTarget == 6) {
			
			turnToTarget(delta);
			deaccelerate(delta);
		} else if (driverAhead()) {
			if(driveAheadCounter > 500){
				reverseSpin();
				driveAheadCounter=0;
				
			}
			turnToTarget(delta);
			driveAheadCounter++;
			deaccelerate(delta);
		} else if (nodeReached()) {
			driveAheadCounter=0;
			nextTarget(delta);
		} else if (goingInCircles()) {
			driveAheadCounter=0;
			nextTarget(delta);
		} else {
			driveAheadCounter=0;
			turnToTarget(delta);
			accelerate(delta);
		}
	}
	private void reverseSpin() {
		spinLeft(1.5);
		
	}

	boolean redLightAhead() {
		//use timer?
		return TrafficSim.redlight;
	}

	boolean driverAhead() {
//		System.out.println(currentAngle);
//		System.out.println("\n\n" + this + ":\n");
		ArrayList<Robot> candidates = new ArrayList<>();
//		System.out.println(this);
		candidates = map.nearbyBots(this.Ypos, this.Xpos);
//		if (map.listBots(mapTarget) != null) {
//			candidates.addAll(map.listBots(mapTarget));
//		}
//		
//		double coveredDistance = targetDistance;
//		int node = mapTarget;
//		while (coveredDistance < lookAhead) {
//			node++;
//			if (node == map.getLength()) {
//				node = 0;
//			}
//			if (map.listBots(node) != null) {
//				candidates.addAll(map.listBots(node));
//			}
//			coveredDistance += map.distanceTo(node);
//		}
//		System.out.println("size: " + candidates.size());
//		System.out.println("list: " + candidates);
		if (candidates.size() <= 1) {
			//No drivers nearby at all.
			return false;
		} else {
			//find the closest robot
			Robot next = null;
			Double distance = 200.0;
			for (int i = 0; i < candidates.size(); i++) {
				Robot robot = candidates.get(i);
				if(robot == this) //ignore itself
					continue;
				double robotY = robot.Ypos;
				double robotX = robot.Xpos;
				double robotDistance = Math.sqrt((robotY - Ypos) * (robotY - Ypos) + (robotX - Xpos) * (robotX - Xpos));
				double robotAngle = Math.atan2((robotY - Ypos), (robotX - Xpos));
//				System.out.println(robotAngle + "\n");
				if (Math.abs(robotAngle-currentAngle) < Math.PI/5) {
					if (robotDistance < 100){
						col = 1;
						robot.col = 2;
						next = robot;
						return true;
					}
				}
			}
			return false;
			/*
			if (next == null){
//				System.out.println("All behind");
				//they are all behind us. Carry on.
				col = 0;
				return false;
			} else {
				col = 1;
				robotAhead = next;
				return true;
			}*/
		}
	}

	boolean nodeReached() {
		return targetDistance < 10;
	}

	boolean goingInCircles() {
		return spinCounter > Math.PI;
	}

	void nextTarget(long delta) {
		mapTarget++;
		if (mapTarget >= map.getLength()) {
			mapTarget = 0;
		}
		spinCounter = 0;
		Point p = map.get(mapTarget);
		targetX = p.getX();
		targetY = p.getY();
		map.relocate(this, mapTarget);

		move(delta);
	}

	void turnToTarget(long delta) {
		double step = ((double) delta) / 1000d;
		double rotate = turn * step;
		if (Math.abs(currentAngle - targetAngle) < rotate) {
			rotate = Math.abs(currentAngle - targetAngle);
		}

		if (targetAngle + Math.PI == currentAngle || targetAngle - Math.PI == currentAngle) {
			//Directly behind us, turn!
			spinRight(rotate);
		} else if (targetAngle > 0) {
			if (currentAngle > targetAngle || currentAngle < targetAngle - Math.PI) {
				spinRight(rotate);
			} else {
				spinLeft(rotate);
			}
		} else {
			if (currentAngle < targetAngle || currentAngle > targetAngle + Math.PI) {
				spinLeft(rotate);
			} else {
				spinRight(rotate);
			}
		}
	}

	void accelerate(long delta) {
		double step = ((double) delta) / 1000d;
		speed += accel * step;
		if (speed > mxspd) {
			speed = mxspd;
		}
		
	}
	
	void deaccelerate(long delta){
		double step = ((double) delta) / 1000d;
		speed -= deacc 	 * step;
		if (speed < 0) {
			speed = 0;
		}
	}

	void tempo(long delta) {
		double step = ((double) delta) / 1000d;
		Xpos += Math.cos(currentAngle) * speed * step;
		Ypos += Math.sin(currentAngle) * speed * step;

		moveCalc();
		double rotate = turn * step;
		if (Math.abs(currentAngle - targetAngle) < rotate) {
			rotate = Math.abs(currentAngle - targetAngle);
		}

		//Adjust angle

		if (targetDistance < 10) {
			mapTarget++;
			if (mapTarget == map.getLength()) {
				mapTarget = 0;
			}
			//moveTo(map.get(mapTarget));
			move(delta);
			return;
		}
		if (targetAngle + Math.PI == currentAngle || targetAngle - Math.PI == currentAngle) {
			//Directly behind us, turn!
			spinRight(rotate);
		} else if (targetAngle > 0) {
			if (currentAngle > targetAngle || currentAngle < targetAngle - Math.PI) {
				spinRight(rotate);
			} else {
				spinLeft(rotate);
			}
		} else {
			if (currentAngle < targetAngle || currentAngle > targetAngle + Math.PI) {
				spinLeft(rotate);
			} else {
				spinRight(rotate);
			}
		}
		if (spinCounter > Math.PI) {
			mapTarget++;
			if (mapTarget == map.getLength()) {
				mapTarget = 0;
			}
			spinCounter = 0;
			//moveTo(map.get(mapTarget));
		}
		refractor();
	}

	void spinRight(double rotate) {
		currentAngle -= rotate;
		if (!goingLeft) {
			spinCounter += rotate;
		} else {
			goingLeft = false;
			spinCounter = rotate;
		}
	}

	void spinLeft(double rotate) {
		currentAngle += rotate;
		if (goingLeft) {
			spinCounter += rotate;
		} else {
			goingLeft = true;
			spinCounter = rotate;
		}
	}

	private void refractor() {
		if (currentAngle > Math.PI) {
			currentAngle -= 2 * Math.PI;
		} else if (currentAngle < -Math.PI) {
			currentAngle += 2 * Math.PI;
		}
	}

	private void findClosestPoint() {
		double shortestDistance = Double.MAX_VALUE;
		int targetID = 0;
		for (int i = 0; i < map.getLength(); i++) {
			Point p = map.get(i);
			int j = i + 1;
			if (j == map.getLength()) {
				j = 0;
			}
			Point q = map.get(j);

			double robPAngle = Math.atan2((p.getY() - Ypos), (p.getX() - Xpos));
			double PQAngle = Math.atan2((q.getY() - p.getY()), (q.getX() - p.getX()));
			double angle = Math.abs(Math.atan2(Math.sin(PQAngle - robPAngle), Math.cos(PQAngle - robPAngle)));
			//System.out.println(i + ": " + angle);
			if (angle < shortestDistance) {
				shortestDistance = angle;
				targetID = i;
			}
		}
		mapTarget = targetID - 1;
		nextTarget(0);
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

import java.util.ArrayList;

/**
 * This class should work as the brains of the Robots.
 *
 * @author Magnus Hu
 */
public class Robot {

	private Terrain map;
	private int mapTarget;
	private double Xpos;
	private double Ypos;
	private double currentAngle; // in radians
	private double targetAngle;
	private Point target;
	private double targetDistance;
	private double targetX;
	private double targetY;
	private double speed = 0;
	private double mxspd = 50;
	private double accel = 15;
	private double deacc = 25;
	private double turn = 0;
	private double mxturn = 1;
	private double speedOfCarInFront = mxspd;
	private double lookAhead = mxspd * 4;
	private Robot robotAhead;
	private int col, driveAheadCounter;
	private String ID;
	private double deltaAngle;
	private Polygon minDist;
	private Polygon maxDist;
	private TrafficSim2 ts;
	/*
	 * double speed = 0; double mxspd = 50; double accel = 1000000000; double
	 * deacc = 15; double turn = 0; double mxturn = 1; double lookAhead = mxspd
	 * * 4; Robot robotAhead;
	 */

	// for catching orbit behavoirs
	double spinCounter = 0;
	boolean goingLeft = true;

	public Robot(double initX, double initY, double initAngle, Terrain terrain,
			String id, TrafficSim2 ts) {
		this.ts = ts;
		Xpos = initX;
		Ypos = initY;
		currentAngle = initAngle;
		map = terrain;
		findClosestPoint();
		col = driveAheadCounter = 0;
		ID = id;
		deltaAngle = 0;
		minDist = new Polygon(new float[]{
				(float) (Xpos-ts.size/2-2),(float) (Ypos-ts.size/2-2),
				(float) (Xpos-ts.size/2-2),(float) (Ypos+ts.size/2+2),
				(float) (Xpos+ts.size/2+2),(float) (Ypos+ts.size/2+2),
				(float) (Xpos+ts.size/2+2),(float) (Ypos-ts.size/2-2),
				});
		maxDist = new Polygon(new float[]{
				(float) (Xpos),(float) (Ypos),
				(float) (Xpos),(float) (Ypos),
				(float) (Xpos+ts.size/2),(float) (Ypos+ts.size/2),
				(float) (Xpos+ts.size/2),(float) (Ypos-ts.size/2),
		});
	}
	
	public void setPosition(double x, double y){
		Xpos = x;
		Ypos = y;
	}
	
	Polygon getMinDist(){
		return minDist;
	}
	Polygon getMaxDist(){
		return maxDist;
	}
	
	public void draw(Graphics g) {
		if(getCol() ==1)
			g.setColor(Color.red);
		else if(speed <= 1)
			g.setColor(Color.blue);
		else if(speed <= 25)
			g.setColor(Color.cyan);
		else if(getCol() == 0)
			g.setColor(Color.white);
		else
			g.setColor(Color.yellow);
		g.drawOval((float)getX()-ts.size/2, (float)getY()-ts.size/2, ts.size, ts.size);
		g.drawString(speed+"", (float)getX(), (float)getY());
		if(ts.render)
		{
		g.draw(minDist);
		g.draw(maxDist);
		}
		
		
	}

	public int getCol() {
		return col;
	}

	private void moveCalc() {
		targetDistance = Math.sqrt((targetY - Ypos) * (targetY - Ypos)
				+ (targetX - Xpos) * (targetX - Xpos));

		targetAngle = Math.atan2((targetY - Ypos), (targetX - Xpos));

		turn = mxturn * Math.sqrt(speed / mxspd);

		refractor();
	}
	
	
	private void calculateHitBoxes(double step){
		float px = (float) Xpos;
		float py = (float) Ypos;
		double l = (step*speed*3)*ts.size/2+3*ts.size/2;
		double a = Math.toRadians(Math.toDegrees(currentAngle)+90);
		double sinA = Math.sin(a)*(ts.size*2*step*speed+ts.size/2+1);//=cos(b) & -cos(c)
		double sinC = Math.cos(a)*(ts.size*2*step*speed+ts.size/2+1);//=sin(c) & -sin(b)
		float px2 = (float)(px+sinA*l/(ts.size/2));
		float py2 = (float)(py-sinC*l/(ts.size/2));
		
		maxDist =  new Polygon(new float[]{
				(float) (Xpos),(float) (Ypos),
				(float) (px2+sinC), (float) (py2+sinA),
				(float) (px2-sinC), (float) (py2-sinA),
				(float) (Xpos),(float) (Ypos),
				
		});
		
		
		
		
		
		sinA = Math.sin(a)*(ts.size/2);//=cos(b) & -cos(c)
		sinC = Math.cos(a)*(ts.size/2);//=sin(c) & -sin(b)
		l = ts.size/2;
		px2 = (float)(sinA*l/(ts.size/2));
		py2 = (float)(sinC*l/(ts.size/2));
		minDist = new Polygon(new float[]{
				(float) (px-px2+sinC), (float) (py+py2+sinA),
				(float) (px+px2+sinC), (float) (py-py2+sinA),
				(float) (px+px2-sinC), (float) (py-py2-sinA),
				(float) (px-px2-sinC), (float) (py+py2-sinA)
		});
		
	}
	
	
	private void checkCrash(){
		boolean crash = false;
		ArrayList<Robot> candidates = ts.prototype;
		for (Robot robot : candidates) {
			if(robot == this)
				continue;
			if(robot.getMinDist().intersects(this.getMinDist()))
				crash = true;
		}
		if(crash)
			col = 1;
		else
			col = 0;
		
	}


	public void move(long delta) {
		// start by moving
		double step = ((double) delta) / 1000d;
		// System.out.println("x:" + (Math.cos(currentAngle) * speed * step));
		Xpos += Math.cos(currentAngle) * speed * step;
		Ypos += Math.sin(currentAngle) * speed * step;
		moveCalc();
		//reposition the hitboxes
		calculateHitBoxes(step);
		
		//setting the color to red if crash
		checkCrash();

		// Using Brooks subsumption
		if (redLightAhead() && mapTarget == 6&& targetDistance < 120) {
			col = 2;

			turnToTarget(delta);
			deaccelerate(delta,0);
		} else if (!redLightAhead() && mapTarget == 18 && targetDistance < 120) {
			col = 2;
			turnToTarget(delta);
			deaccelerate(delta,0);
		}

		else if (nodeReached()) {
			driveAheadCounter = 0;
			nextTarget(delta);
		} else if (driverAhead()) {
			if (TrafficSim2.smartCarSim) {
				Robot robotInFront = nearestDriverAhead();
				if (robotInFront != null) {
					if(Math.abs(Math.abs(currentAngle) - Math.abs(robotInFront.getAngle())) < Math.PI/2)
						drive(delta, robotInFront);
					else{
						deaccelerate(delta,0);
					}
				} else
					accelerate(delta, mxspd);

			} else
				deaccelerate(delta, 0);
			turnToTarget(delta);
			
			
		} else if (goingInCircles()) {
			driveAheadCounter = 0;
			nextTarget(delta);
		} else {
			driveAheadCounter = 0;
			turnToTarget(delta);
			accelerate(delta, mxspd);
		}
	}


	private boolean redLightAhead() {
		return TrafficSim2.redlight;
	}

	private boolean driverAhead() {
		Robot near = nearestDriverAhead();
		if (near == null)
			return false;
		else
			return true;
	}

	private Robot nearestDriverAhead() {
//		ArrayList<Robot> candidates = ts.prototype;
//		for (Robot robot : candidates) {
//			if(robot == this)
//				continue;
//			if(robot.getMinDist().intersects(this.getMaxDist()))	
//					return robot;
//		}
//		return null;
		ArrayList<Robot> candidates = ts.prototype;
		Robot rob = null;
		for (Robot robot : candidates) {
			if(robot == this)
				continue;
			if(robot.getMinDist().intersects(this.getMaxDist())){
				if(rob == null || distanceTo(rob)> distanceTo(robot))
					rob = robot;
				
			}
		}
		return rob;
		
//		if (candidates.size() <= 1) {
//			// No drivers nearby at all.
//			return null;
//		} else {
//			// find the closest robot
//			Robot next = null;
//			for (int i = 0; i < candidates.size(); i++) {
//				Robot robot = candidates.get(i);
//				if (robot == this) // ignore itself
//					continue;
//				double robotY = robot.Ypos;
//				double robotX = robot.Xpos;
//				double robotDistance = Math.sqrt((robotY - Ypos)
//						* (robotY - Ypos) + (robotX - Xpos) * (robotX - Xpos));
//				double robotAngle = Math
//						.atan2((robotY - Ypos), (robotX - Xpos));
//				if (Math.abs(robotAngle - currentAngle) < TrafficSim2.angle) {
//					if (robotDistance < TrafficSim2.nearestDist)
//						return robot;
//					if (robotDistance < TrafficSim2.distance * speed * 0.1 + 6
//							&& (next == null || distanceTo(next) > robotDistance)) {
//						col = 1;
//						robot.col = 2;
//						next = robot;
//					}
//				}
//			}
//			return next;
		
		
		
		
	}

	private boolean nodeReached() {
		return targetDistance < 10;
	}

	private boolean goingInCircles() {
		return spinCounter > Math.PI;
	}

	private void nextTarget(long delta) {
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

	private void turnToTarget(long delta) {
		double step = ((double) delta) / 1000d;
		double rotate = turn * step;
		if (Math.abs(currentAngle - targetAngle) < rotate) {
			rotate = Math.abs(currentAngle - targetAngle);
		}

		if (targetAngle + Math.PI == currentAngle
				|| targetAngle - Math.PI == currentAngle) {
			// Directly behind us, turn!
			spinRight(rotate);
		} else if (targetAngle > 0) {
			if (currentAngle > targetAngle
					|| currentAngle < targetAngle - Math.PI) {
				spinRight(rotate);
			} else {
				spinLeft(rotate);
			}
		} else {
			if (currentAngle < targetAngle
					|| currentAngle > targetAngle + Math.PI) {
				spinLeft(rotate);
			} else {
				spinRight(rotate);
			}
		}
	}

	private boolean isDriving() {
		return (speed > 0);
	}

	private double getSpeed() {
		return speed;
	}

	private double distanceTo(Robot robot) {
		return Math.sqrt((robot.Ypos - Ypos) * (robot.Ypos - Ypos)
				+ (robot.Xpos - Xpos) * (robot.Xpos - Xpos));
	}

	private void drive(long delta, Robot robot) {
		if (robot == null)
			accelerate(delta, mxspd);
		else if (speed > robot.getSpeed())
			deaccelerate(delta,robot.getSpeed());
		else if(speed < robot.getSpeed())
			accelerate(delta, robot.getSpeed());

	}

	private void accelerate(long delta, double maxspeed) {
		double step = ((double) delta) / 1000d;
		speed += accel * step;
		if (speed > maxspeed) {
			speed = maxspeed;
		}

	}

	private void deaccelerate(long delta, double minspeed) {
		double step = ((double) delta) / 1000d;
		speed -= deacc * step;
		if (speed < minspeed) {
			speed = minspeed;
		}
	}


	private void spinRight(double rotate) {
		// number of right rotation is here, named rotate
		deltaAngle = rotate;
		currentAngle -= rotate;
		if (!goingLeft) {
			spinCounter += rotate;
		} else {
			goingLeft = false;
			spinCounter = rotate;
		}
	}

	private void spinLeft(double rotate) {
		// number of left rotation is here, named rotate
		deltaAngle = -rotate;
		currentAngle += rotate;
		if (goingLeft) {
			spinCounter += rotate;
		} else {
			goingLeft = true;
			spinCounter = rotate;
		}
	}

	private void refractor() { // makes sure that the angle is always inside
								// [-PI, PI]
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
			double PQAngle = Math.atan2((q.getY() - p.getY()),
					(q.getX() - p.getX()));
			double angle = Math.abs(Math.atan2(Math.sin(PQAngle - robPAngle),
					Math.cos(PQAngle - robPAngle)));
			// System.out.println(i + ": " + angle);
			if (angle < shortestDistance) {
				shortestDistance = angle;
				targetID = i;
			}
		}
		mapTarget = targetID - 1;
		nextTarget(0);
	}

	/**
	 * GETTERS
	 * 
	 * 
	 */
	public double getX() {
		return Xpos;
	}

	public double getY() {
		return Ypos;
	}

	public double getAngle() {
			return currentAngle;
		
	}

	public int[] getMotor() {
		int[] a = new int[2];
		if (Math.abs(deltaAngle) < 0.01) {
			return new int[] { 450, 450 };

		}
		if (deltaAngle > 0) {
			a[0] = (int) ((speed * Math.sin(deltaAngle) / Math
					.sin(2 * deltaAngle)) - 4) * 10;
			a[1] = (int) ((speed * Math.sin(deltaAngle) / Math
					.sin(2 * deltaAngle)) + 4) * 10;
		} else {
			a[0] = (int) ((speed * Math.sin(deltaAngle) / Math
					.sin(2 * deltaAngle)) + 4) * 10;
			a[1] = (int) ((speed * Math.sin(deltaAngle) / Math
					.sin(2 * deltaAngle)) - 4) * 10;
		}
		return a;
	}
}

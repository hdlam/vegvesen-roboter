///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package trafficsim;
//
//import java.awt.Point;
//import java.awt.RenderingHints.Key;
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.lwjgl.LWJGLException;
//import org.lwjgl.Sys;
//import org.lwjgl.input.Keyboard;
//import org.lwjgl.opengl.Display;
//import org.lwjgl.opengl.DisplayMode;
//import org.lwjgl.opengl.GL11;
//import org.newdawn.slick.BasicGame;
//import org.newdawn.slick.GameContainer;
//import org.newdawn.slick.Graphics;
//import org.newdawn.slick.SlickException;
//
///**
// * This is the main class for the project
// *
// * @author Magnus Hu
// */
//public class TrafficSim{
//
//
//	Terrain map = new Terrain();
//	Robot[] prototype = new Robot[8];
//	//Robot prototype = new Robot(400, 400, 0, map);
//	static boolean redlight = false, smartCarSim = false;
//	int size = 6;
//	boolean zKeyIsDown = false, xKeyIsDown = false;;
//	int w = 800,h = 800; //152 * 124
//	//800 * 652
//	//Threshold values
//	static double distance = 60;
//	static double angle = Math.PI/4;
//	static double nearestDist = 17;
//	
//	public void start() throws InterruptedException {
//		try {
//			Display.setDisplayMode(new DisplayMode(w, h));
//			Display.create();
//		} catch (LWJGLException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
//
//		// init OpenGL here
//		ArrayList<Point> track = map.getTrack();
//		GL11.glMatrixMode(GL11.GL_PROJECTION);
//		GL11.glLoadIdentity();
//		GL11.glOrtho(0, w, 0, h, 1, -1);
//		GL11.glMatrixMode(GL11.GL_MODELVIEW);
//		long lastTime = getTime();
//		int prototypeX;
//		int prototypeY;
//		for (int i = 0; i < prototype.length; i++) {
//			prototype[i] = new Robot(w * Math.random(), h * Math.random(), 2 * Math.PI * Math.random() - Math.PI, map, "test");
//
//		}
//
//		while (!Display.isCloseRequested()) {
//			//keyboard inputs
//			if(Keyboard.isKeyDown(Keyboard.KEY_Q))
//				System.exit(0);
//			if(Keyboard.isKeyDown(Keyboard.KEY_X) && !xKeyIsDown){
//				redlight = !redlight;
//				xKeyIsDown = true;
//			}
//			if (Keyboard.isKeyDown(Keyboard.KEY_Z) && !zKeyIsDown) {
//				zKeyIsDown = true;
//				smartCarSim = !smartCarSim;
//			}
//			if(!Keyboard.isKeyDown(Keyboard.KEY_Z))
//				zKeyIsDown = false;
//			if(!Keyboard.isKeyDown(Keyboard.KEY_X))
//				xKeyIsDown = false;
//			
//			
//			
//			// render OpenGL here
//			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//			if(smartCarSim){
//				GL11.glColor3f(1f, 1.0f, 0.1f);
//				GL11.glBegin(GL11.GL_QUADS);
//		        GL11.glVertex2f(50,600);
//		        GL11.glVertex2f(50+50,600);
//		        GL11.glVertex2f(50+50,600+50);
//		        GL11.glVertex2f(50,600+50);
//		        GL11.glEnd();
//			}
//	        			
//			for (int i = 0; i < track.size(); i++) {
//				if(i == 5 && redlight){
//					GL11.glColor3f(1f, 0.1f, 0.1f);
//				}
//				else if(i == 17 && !redlight)
//					GL11.glColor3f(1f, 0.1f, 0.1f);
//				else
//					GL11.glColor3f(0.1f, 1f, 0.1f);
//					
//				GL11.glBegin(GL11.GL_LINES);
//				GL11.glVertex2d(track.get(i).getX(), track.get(i).getY());
//				GL11.glVertex2d(track.get((i + 1) % track.size()).getX(), track.get((i + 1) % track.size()).getY());
//				GL11.glEnd();
//
//			}
//			long delta = getTime() - lastTime;
//			//Iterate over robots
////            for (int i = 0; i < track.size(); i++) {
////                Point point = track.get(i);
////                
////            }
//			for (int i = 0; i < prototype.length; i++) {
//				prototype[i].move(delta);
//				prototypeX = (int) prototype[i].getX();
//				prototypeY = (int) prototype[i].getY();
////				if(prototype[i].getMotor()[0] != prototype[i].getMotor()[1])
////				{	
////				System.out.print(prototype[i].getMotor()[0] + "               ");
////				System.out.println(prototype[i].getMotor()[1]);
////				}
//				double a = prototype[i].getAngle();
//				if(prototype[i].getCol() == 0)
//					GL11.glColor3f(1f, 1f, 1f);
//				else if(prototype[i].getCol() == 1)
//					GL11.glColor3f(1f, 0.5f, 0.5f);
//				else
//					GL11.glColor3f(0.3f, 0.5f, 1f);
//				
//				GL11.glBegin(GL11.GL_QUADS);
//				GL11.glVertex2d(prototypeX + size * Math.cos(a) - 1 * Math.sin(a),
//						prototypeY + size * Math.sin(a) + 1 * Math.cos(a));
//				GL11.glVertex2d(prototypeX + size * Math.cos(a) + 1 * Math.sin(a),
//						prototypeY + size * Math.sin(a) - 1 * Math.cos(a));
//				GL11.glVertex2d(prototypeX - size * Math.cos(a) + 3 * Math.sin(a),
//						prototypeY - size * Math.sin(a) - size * Math.cos(a));
//				GL11.glVertex2d(prototypeX - size * Math.cos(a) - size * Math.sin(a),
//						prototypeY - size * Math.sin(a) + size * Math.cos(a));
//				GL11.glEnd();
//				GL11.glColor3f(1, 1, 0);
////				GL11.glLoadIdentity();
////				GL11.glPushMatrix();
////				GL11.glRotated(a, 0, 0, 1);
//				
////				GL11.glBegin(GL11.GL_TRIANGLE_FAN);
////				GL11.glVertex2d(prototypeX + size * Math.cos(a) - 1 * Math.sin(a),
////						prototypeY + size * Math.sin(a) + 1 * Math.cos(a));
////				GL11.glVertex2d(prototypeX + distance * Math.cos(a),
////						prototypeY + size * Math.sin(a)  -10);
////				GL11.glVertex2d(prototypeX + size * Math.cos(a) -10,
////						prototypeY + size * Math.sin(a) +10);
////				
////				GL11.glEnd();
//				
////				GL11.glPopMatrix();
//				
//			}
//			lastTime = getTime();
//			Thread.sleep(16);
//			Display.update();
//		}
//		
//		Display.destroy();
//	}
//
//	public long getTime() {
//		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
//	}
//
//	public static void main(String[] args) {
//		// TODO code application logic here
//
//		TrafficSim sim = new TrafficSim();
//		try {
//			sim.start();
//		} catch (InterruptedException ex) {
//			Logger.getLogger(TrafficSim.class.getName()).log(Level.SEVERE, null, ex);
//		}
//	}
//
//}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

import java.awt.Point;
import java.awt.RenderingHints.Key;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

/**
 * This is the main class for the project
 *
 * @author HD
 */
public class TrafficSim2 extends BasicGame{
	Terrain map;
	ArrayList<Robot> prototype;
	static boolean redlight, smartCarSim; 
	final int size = 20;
	int w,h;
	boolean zKeyIsDown, xKeyIsDown, render;
	Input input;
	double angle;
	//800 * 652
	//Threshold values
	static double distance;
	static double nearestDist;
	private int numOfBots = 8;
	
	
	public TrafficSim2(String title) {
		super(title);
	}
	
	@Override
	public void init(GameContainer gc) throws SlickException {
	distance = 80;
	angle = Math.PI/3;
	nearestDist = 23;
	render = false;
	
	map = new Terrain();
	prototype = new ArrayList<Robot>();
	redlight = true; smartCarSim = false;
	
	
	zKeyIsDown = false; xKeyIsDown = false;;
	w = 800; h = 800;
	
	
	for (int i = 0; i < numOfBots; i++) {
		prototype.add(new Robot(w * Math.random(), h * Math.random(), 2 * Math.PI * Math.random() - Math.PI, map, "test", this));
	}
	input = gc.getInput();
}


	@Override
	public void render(GameContainer gc, Graphics graphics) throws SlickException {
		
		for (int i = 0; i < map.getLength(); i++) {
			Point  p = map.get(i);
			Point p2 = map.get(i+1);
			if(i == 5 && redlight){
				graphics.setColor(Color.red);
			}
			else if(i == 17 && !redlight)
				graphics.setColor(Color.red);
			else
				graphics.setColor(Color.green);
			
			graphics.drawLine(p.x, p.y, p2.x, p2.y);
		}
		graphics.setColor(Color.white);
		graphics.drawLine(map.get(map.getLength()).x, map.get(map.getLength()).y, map.get(0).x, map.get(0).y);
		graphics.drawString("platooning is:" + (smartCarSim?"on":"off"), 50, 50);
		for (int i = 0; i < prototype.size(); i++) {
			
			prototype.get(i).draw(graphics);
			
//			graphics.drawOval((float)Math.cos(prototype[i].getAngle())*size/2+(float)prototype[i].getX()+size/2, (float)Math.sin(prototype[i].getAngle())*size/2+(float)prototype[i].getY()+size/2, 3, 3);
			
//			graphics.drawLine(
//					(float)(prototype[i].getX() + size/2 * Math.cos(prototype[i].getAngle()) -  Math.sin(prototype[i].getAngle())),
//					(float)(prototype[i].getY() + size/2 * Math.sin(prototype[i].getAngle()) +  Math.cos(prototype[i].getAngle())),
//					(float)(prototype[i].getX() + (distance+size/2) * Math.cos(prototype[i].getAngle()-angle/2) - 1 * Math.sin(prototype[i].getAngle()-angle/2)),
//					(float)(prototype[i].getY() + (distance+size/2) * Math.sin(prototype[i].getAngle()-angle/2) + 1 * Math.cos(prototype[i].getAngle()-angle/2))
//					);
//			graphics.drawLine(
//					(float)(prototype[i].getX() + size/2 * Math.cos(prototype[i].getAngle()) -  Math.sin(prototype[i].getAngle())),
//					(float)(prototype[i].getY() + size/2 * Math.sin(prototype[i].getAngle()) +  Math.cos(prototype[i].getAngle())),
//					(float)(prototype[i].getX() + (distance+size) * Math.cos(prototype[i].getAngle()+angle/2) - 1 * Math.sin(prototype[i].getAngle()+angle/2)),
//					(float)(prototype[i].getY() + (distance+size) * Math.sin(prototype[i].getAngle()+angle/2) + 1 * Math.cos(prototype[i].getAngle()+angle/2))
//					);
		}
		
	}
	
	
	
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		for (int j = 0; j < prototype.size(); j++) {
			prototype.get(j).move(delta);
		}
		if(input.isKeyPressed(Input.KEY_X)){
			redlight = !redlight;
		}
		if(input.isKeyPressed(Input.KEY_Q))
			System.exit(0);
		if(input.isKeyPressed(Input.KEY_Z))
			smartCarSim =!smartCarSim;
		
		if(input.isKeyPressed(Input.KEY_R)){
			render = !render;
		}
		
		if(input.isKeyPressed(Input.KEY_P)){
			prototype = new ArrayList<Robot>();
			for (int i = 0; i < numOfBots; i++) {
				prototype.add(new Robot(w * Math.random(), h * Math.random(), 2 * Math.PI * Math.random() - Math.PI, map, "test", this));
				
			}
		}
		
		if(input.isKeyDown((Input.KEY_1))){
			prototype.get(0).setPosition(300, 300);
		}
	}
	
	
	
	
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public static void main(String[] args) {
		// TODO code application logic here

		TrafficSim2 sim = new TrafficSim2("simulator");
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(sim);
			appgc.setDisplayMode(800, 800, false);
			appgc.start();
			
		}
		catch (SlickException ex)
		{
			Logger.getLogger(TrafficSim2.class.getName()).log(Level.SEVERE, null, ex);
		}

		
		
		
	}

}

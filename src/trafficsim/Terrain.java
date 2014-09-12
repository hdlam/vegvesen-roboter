/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficsim;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Magnus Hu
 */
public class Terrain {
    public ArrayList<Point> track;
	ArrayList<ArrayList<Robot>> robotFinder;
    
    public Terrain(){
        track = new ArrayList<>();
        int xOffset = 260;
        int yOffset = 60;
        track.add(new Point(140+xOffset,  0+yOffset));
        track.add(new Point(220+xOffset, 20+yOffset));
        track.add(new Point(260+xOffset, 60+yOffset));
        track.add(new Point(280+xOffset,140+yOffset));
        track.add(new Point(260+xOffset,220+yOffset));
        track.add(new Point(200+xOffset,280+yOffset));
        track.add(new Point(140+xOffset,340+yOffset));
        track.add(new Point( 80+xOffset,400+yOffset));
        track.add(new Point( 20+xOffset,460+yOffset));
        track.add(new Point(  0+xOffset,540+yOffset));
        track.add(new Point( 20+xOffset,620+yOffset));
        track.add(new Point( 60+xOffset,660+yOffset));
        track.add(new Point(140+xOffset,680+yOffset));
        track.add(new Point(220+xOffset,660+yOffset));
        track.add(new Point(260+xOffset,620+yOffset));
        track.add(new Point(280+xOffset,540+yOffset));
        track.add(new Point(260+xOffset,460+yOffset));
        track.add(new Point(200+xOffset,400+yOffset));
        track.add(new Point(140+xOffset,340+yOffset));
        track.add(new Point( 80+xOffset,280+yOffset));
        track.add(new Point( 20+xOffset,220+yOffset));
        track.add(new Point(  0+xOffset,140+yOffset));
        track.add(new Point( 20+xOffset, 60+yOffset));
        track.add(new Point( 60+xOffset, 20+yOffset));
		
		
		robotFinder = new ArrayList<>();
		for (int i = 0; i < track.size(); i++) {
			robotFinder.add(new ArrayList<Robot>());
			
		}
    }
    /**
     * Use this method to get a target point from the map
     *
     * TODO: Current implementation will fail in about 11 laps, Unless Robot has
     * access to track length and can do the modelo itself.
     * @return the next Point to aim for
     */
    public Point get(int targetPoint){
//		while(targetPoint >= track.size()){
//			targetPoint -= track.size();
//		}
    	//return getTrack().get(targetPoint);
        return getTrack().get(targetPoint%track.size());
    }

    /**
     * @return the track
     */
    public ArrayList<Point> getTrack() {
        return track;
    }

	int getLength() {
		return track.size();
	}
	
	public void relocate(Robot rob, int loc){
		int preLoc = loc -1;
		if (loc == 0){
			preLoc = robotFinder.size()-1;
		}
		robotFinder.get(preLoc).remove(rob);
		robotFinder.get(loc).add(rob);
	}
	public ArrayList<Robot> listBots(int loc){
		return robotFinder.get(loc);
	}
	public double distanceTo(int id){
		int last = id -1;
		if (last == -1) {
			last = getLength() - 1;
		}
		Point p1 = track.get(last);
		Point p2 = track.get(id);
		return p1.distance(p2);
	}
}

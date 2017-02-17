package application.model;

import java.util.ArrayList;

import com.lynden.gmapsfx.javascript.object.LatLong;

/*
 * Class with all the info of the robot
 */
public class RobotData {

    private ArrayList<Double> temp;
    private ArrayList<Double> hum;
    private ArrayList<Double> met;
    private ArrayList<Double> but;
    private ArrayList<Double> co2;
    
    private ArrayList<LatLong> robotPos;
    private LatLong newPos;
        
    private final static RobotData instance = new RobotData();

    public static RobotData getInstance() {
        return instance;
    }
    
    public RobotData() {
	temp = new ArrayList<Double>();
	hum = new ArrayList<Double>();
	met = new ArrayList<Double>();
	but = new ArrayList<Double>();
	co2 = new ArrayList<Double>();
	robotPos = new ArrayList<LatLong>();
    }
    
    public double getTemp() {
        return temp.get(temp.size()-1);
    }
    public double getHum() {
        return hum.get(hum.size()-1);
    }
    public double getMet() {
        return met.get(met.size()-1);
    }
    public double getBut() {
        return but.get(but.size()-1);
    }
    public double getCo2() {
        return co2.get(co2.size()-1);
    }
    public LatLong getRobotPos() {
        return robotPos.get(robotPos.size()-1);
    }
    public LatLong getNewPos() {
        return newPos;
    }
    public void setTemp(double temp) {
        this.temp.add(temp);
    }
    public void setHum(double hum) {
        this.hum.add(hum);
    }
    public void setMet(double met) {
        this.met.add(met);
    }
    public void setBut(double but) {
        this.but.add(but);
    }
    public void setCo2(double co2) {
        this.co2.add(co2);
    }
    public void setRobotPos(LatLong robotPos) {
        this.robotPos.add(robotPos);
    }
    public void setNewPos(LatLong newPos) {
        this.newPos = newPos;
    }
    
    
}

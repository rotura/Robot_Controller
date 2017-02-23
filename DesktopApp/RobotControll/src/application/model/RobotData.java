package application.model;

import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.lynden.gmapsfx.javascript.object.LatLong;

import javafx.collections.FXCollections;

/*
 * Class with all the info of the robot
 */
public class RobotData {

	private ObservableList<Double> temp;
	private ObservableList<Double> hum;
	private ObservableList<Double> met;
	private ObservableList<Double> but;
	private ObservableList<Double> co2;
	private ObservableList<Date> date;

	private ObservableList<double[]> robotPos;
	private double[] newPos = new double[2];
	
	private double tempMax = Integer.MIN_VALUE;
	private double tempMin = Integer.MAX_VALUE;

	private final static RobotData instance = new RobotData();

	public static RobotData getInstance() {
		return instance;
	}

	public RobotData() {
		temp = FXCollections.observableArrayList();
		temp.add(0.0);

		hum = FXCollections.observableArrayList();
		hum.add(0.0);

		met = FXCollections.observableArrayList();
		met.add(0.0);

		but = FXCollections.observableArrayList();
		but.add(0.0);

		co2 = FXCollections.observableArrayList();
		co2.add(0.0);

		date = FXCollections.observableArrayList();
		date.add(new Date());
		
		robotPos = FXCollections.observableArrayList();
		newPos[0] = 43.538762;
		newPos[1] = -5.698957;
		robotPos.add(newPos);
	}

	public double getTemp() {
		return temp.get(temp.size() - 1);
	}

	public double getTempMax() {
		return tempMax;
	}

	public double getTempMin() {
		return tempMin;
	}

	public double getHum() {
		return hum.get(hum.size() - 1);
	}

	public ObservableList<Double> getDataHum() {
		return hum;
	}

	public double getMet() {
		return met.get(met.size() - 1);
	}

	public double getBut() {
		return but.get(but.size() - 1);
	}

	public double getCo2() {
		return co2.get(co2.size() - 1);
	}

	public double getLat() {
		return robotPos.get(robotPos.size() - 1)[0];
	}

	public double getLon() {
		return robotPos.get(robotPos.size() - 1)[1];
	}
	
	public double[] getNewPos() {
		return newPos;
	}

	public Date getDate() {
		return date.get(date.size() - 1);
	}

	public void setDate(Date date) {
		this.date.add(date);
	}
	
	public void setTemp(double temp) {
		this.temp.add(temp);
		if (temp > tempMax)
			tempMax = temp;
		if (temp < tempMin)
			tempMin = temp;
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
		newPos[0] = robotPos.getLatitude();
		newPos[1] = robotPos.getLongitude();
		this.robotPos.add(newPos);
	}
	
	public void setRobotPos(double lat, double lon) {
		newPos[0] = lat;
		newPos[1] = lon;
		this.robotPos.add(newPos);
	}

	public void setNewPos(LatLong newPos) {
		this.newPos[0] = newPos.getLatitude();
		this.newPos[1] = newPos.getLongitude();
	}
	
	public void exportData(File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(getContent());
            fileWriter.close();
        } catch (IOException ex) {
        	System.out.println("Fichero no guardado");
        }
         
    }

	@SuppressWarnings("deprecation")
	private char[] getContent() {
		String data = "Date,Temp,Hum,Met,But,Co2,Lat,Lon";
		for(int i = 0; i < hum.size(); i++){
			data += "\n" + date.get(i).toGMTString() + ","
					+ temp.get(i).toString() + ","
					+ hum.get(i).toString() + ","
					+ met.get(i).toString() + ","
					+ but.get(i).toString() + ","
					+ co2.get(i).toString() + ","
					+ robotPos.get(i)[0] + ","
					+ robotPos.get(i)[1] + ","
					;
		}
		return data.toCharArray();
	}

}

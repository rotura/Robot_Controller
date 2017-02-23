package application.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.lynden.gmapsfx.javascript.object.LatLong;

import application.model.RobotData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;



public class mapPopUpController implements Initializable{

    @FXML
    private Button exit;
    
    @FXML
    private Button goHere;
    
    @FXML
    private Label latlon;
    
    private LatLong ll;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    exit.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		Stage stage = (Stage) exit.getScene().getWindow();
	        stage.close();
	    }
	});
	
	goHere.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		RobotData.getInstance().setNewPos(ll);
		Stage stage = (Stage) exit.getScene().getWindow();
	        stage.close();
	    }
	});
    }
    
    public void setLatLon(LatLong latLong){
        ll = latLong;
    }

	public void setResource(ResourceBundle resources) {
		exit.setText(resources.getString("exit"));
		goHere.setText(resources.getString("goHere"));
		latlon.setText(resources.getString("position")+ ":\t" + ll.toString());

	}

}
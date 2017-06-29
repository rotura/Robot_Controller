package application.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class helpController implements Initializable{

    @FXML
    private Button exit;
    
    @FXML
    private TextArea helpText;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    setResource(resources);	
    	
	exit.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		Stage stage = (Stage) exit.getScene().getWindow();
	        stage.close();
	    }
	});
	
    }

    public void setResource(ResourceBundle resources) {
		exit.setText(resources.getString("exit"));
		helpText.setText(resources.getString("helpText1") + "\n" 
				+ resources.getString("helpText2") + "\n"
				+ resources.getString("helpText3"));
	}
    
}

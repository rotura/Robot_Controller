package application.ui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class helpController implements Initializable{

    @FXML
    private Button exit;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	exit.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		Stage stage = (Stage) exit.getScene().getWindow();
	        stage.close();
	    }
	});
    }

}

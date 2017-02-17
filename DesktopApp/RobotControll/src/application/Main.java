package application;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import application.model.RobotData;
import application.ui.mainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane MainView;
    private Timer t; 
    @Override
    public void start(Stage primaryStage) {
	try {
	    this.primaryStage = primaryStage;
	    primaryStage.setTitle("Tank Aplication");
	    initAplicationView();
	    webDaemon();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    @Override
    public void stop(){
	t.cancel();
    }
    
    private void webDaemon() throws IOException {
	t= new Timer();
	t.scheduleAtFixedRate(new TimerTask() {
	    public void run() {
		InputStream response = null;
		try {
		    response = new URL("http://192.168.4.5/datos").openStream();
		} catch (IOException e) {
		    System.out.println("Error al conectarse a la WeMos");;
		}
		try (Scanner scanner = new Scanner(response)) {
		    String responseBody = scanner.useDelimiter("\\A").next();
		    System.out.println(responseBody);
		    RobotData.getInstance().setTemp((Double.parseDouble(responseBody.split(";")[1].split(":")[1])));
		    RobotData.getInstance().setHum((Double.parseDouble(responseBody.split(";")[0].split(":")[1])));
		}
	    }
	}, 0, // run first occurrence immediately
		2500);
    }

    private void initAplicationView() {
	try {
	    FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(Main.class.getResource("ui/main.fxml"));
	    loader.setController(new mainController());
	    MainView = (BorderPane) loader.load();
	    Scene scene = new Scene(MainView);
	    scene.getStylesheets().add(getClass()
		    .getResource("ui/application.css").toExternalForm());
	    primaryStage.setScene(scene);
	    primaryStage.show();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	launch(args);
    }
}
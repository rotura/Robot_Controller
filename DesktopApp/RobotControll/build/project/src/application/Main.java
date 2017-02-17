package application;
	
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import application.ui.mainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
 

public class Main extends Application {
	
    private Stage primaryStage;
    private BorderPane MainView;
    
    @Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			primaryStage.setTitle("Tank Aplication");
			initAplicationView();
			webDaemon();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void webDaemon() throws IOException {
	    InputStream response = new URL("http://google.es").openStream();
	    try (Scanner scanner = new Scanner(response)) {
		    String responseBody = scanner.useDelimiter("\\A").next();
		    System.out.println(responseBody);
		}	    
}

	private void initAplicationView() {
	    try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("ui/main.fxml"));
		loader.setController(new mainController());
		MainView = (BorderPane) loader.load();
		Scene scene = new Scene(MainView);
		scene.getStylesheets().add(getClass().getResource("ui/application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	} catch(Exception e) {
		e.printStackTrace();
	}
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}

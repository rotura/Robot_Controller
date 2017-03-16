package application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import application.model.RobotData;
import application.ui.controllers.mainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane MainView;
	private Timer t;
	FXMLLoader loader;
	Process child;
	static String path;

	@Override
	public void start(Stage primaryStage) {

		// Create a canonical file to get the actual path of the aplication
		File miDir = new File(".");
		try {
			path = miDir.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Get the path to run the Tomcat Server
		// This is fot the package app
		// String command = path + "/apache-tomcat-9.0.0.M17/bin/startup.bat";
		String command = path + "/server/wildflyext.bat";

		// This is to test in Eclipse
		//String command = path + "/../apache-tomcat-9.0.0.M17/bin/startup.bat";
		//String command = path + "\\..\\server\\wildflyext.bat";
		try {
			child = Runtime.getRuntime().exec(command);
			this.primaryStage = primaryStage;
			primaryStage.setTitle("Robot Controller");
			initAplicationView();
			webDaemon();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		// Get the path to stop the Tomcat Server
		try {
			// This is for the package app
			// Runtime.getRuntime().exec(path +
			// "/apache-tomcat-9.0.0.M17/bin/shutdown.bat");

			// This is to test in Eclipse
			//Runtime.getRuntime().exec(path + "/../apache-tomcat-9.0.0.M17/bin/shutdown.bat");
			Runtime.getRuntime().exec(path + "\\..\\server\\wildflyext\\bin\\jboss-cli.bat --connect command=:shutdown");
		} catch (IOException e) {
			e.printStackTrace();
		}
		child.destroy();
		t.cancel();
		((mainController) loader.getController()).stopFunctions();
	}

	/**
	 * Method to run a background task that get Data of the Arduino robot
	 * 
	 * @throws IOException
	 */
	private void webDaemon() throws IOException {
		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				InputStream response = null;
				try {
					response = new URL("http://192.168.4.5/datos").openStream();
					try (Scanner scanner = new Scanner(response)) {
						String responseBody = scanner.useDelimiter("\\A").next();
						System.out.println(responseBody);
						String[] data = responseBody.split(";");
						RobotData.getInstance().setTemp((Double.parseDouble(data[1].split(":")[1])));
						RobotData.getInstance().setHum((Double.parseDouble(data[0].split(":")[1])));
					}
				} catch (IOException e) {
					System.out.println("Fail to connect with the robot");
				}

			}
		}, 0, // run first occurrence immediately
				500);
	}

	private void initAplicationView() {
		try {
			loader = new FXMLLoader();
			loader.setResources(ResourceBundle.getBundle("bundles.MyBundle", new Locale("es", "ES")));
			loader.setLocation(Main.class.getResource("ui/main.fxml"));
			loader.setController(new mainController());
			MainView = (BorderPane) loader.load();
			Scene scene = new Scene(MainView);
			scene.getStylesheets().add(getClass().getResource("ui/application.css").toExternalForm());
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

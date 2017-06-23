package application;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
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
	static Timer t;
	FXMLLoader loader;
	Process child;
	public Boolean pet = false;

	private final static Main instance = new Main();

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void start(Stage primaryStage) {

		try {
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
				@SuppressWarnings("unused")
				InputStream response = null;
				try {
					/*
					 * if (pet) { System.out.println("Esperando"); } else { pet
					 * = true;
					 */
					RobotData.getInstance().sem.acquire();
					String uri = "http://192.168.4.1/";
					ArrayList<String> tareas = RobotData.getInstance().getTareas();
					for(String t: tareas)
						uri += t +";" ;
					uri = uri.substring(0, uri.length()-1);
					URL url = new URL(uri);
					System.out.println(uri);
					url.openStream();
					/*response = url.openStream();
					// pet = false;

					try (Scanner scanner = new Scanner(response)) {
						String responseBody = scanner.useDelimiter("\\A").next();
						System.out.println(responseBody);
						String[] data = responseBody.split(";");
						if (start) {
							RobotData.getInstance().setTemp((Double.parseDouble(data[1].split(":")[1])));
							RobotData.getInstance().setHum((Double.parseDouble(data[0].split(":")[1])));
						}
						start = true;
					}*/
					// }
				} catch (IOException e) {
					System.out.println("Fail to connect with the robot");
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println("Semaforo ROJO");
				}
				RobotData.getInstance().sem.release();
				// pet = false;
			}
		}, 0, // run first occurrence immediately
				3500);
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

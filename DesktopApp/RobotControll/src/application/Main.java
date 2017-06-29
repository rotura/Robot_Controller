package application;

import java.util.Locale;
import java.util.ResourceBundle;

import application.ui.controllers.mainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	private Stage primaryStage;
	private BorderPane MainView;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		((mainController) loader.getController()).stopFunctions();
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

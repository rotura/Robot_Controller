package application.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.text.html.HTML;
import javax.xml.stream.Location;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.Animation;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import application.Main;
import application.model.PeticionHttp;
import application.model.RobotData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import netscape.javascript.JSObject;

@SuppressWarnings("unused")
public class mainController implements Initializable, MapComponentInitializedListener {

	@FXML
	private LineChart<?, ?> temperature;

	// Tank's control buttons
	@FXML
	private Button rightButton, leftButton, upButton, downButton, rightRotationButton, leftRotationButton;

	// Camera's control buttons
	@FXML
	private Button camLeft, camRight, camCenter, camUp, camDown;

	@FXML
	private BarChart<?, ?> humidity;

	@FXML
	private StackedBarChart<?, ?> gas;

	@FXML
	private GridPane sensorPane;

	private Main mainApp;
	private GoogleMap map;
	private Marker robotPos;

	@FXML
	private Label humL;

	@FXML
	private Label tempL;

	@FXML
	private TextArea gasL;

	@FXML
	private TextArea gpsL;

	@FXML
	private NumberAxis humAxis, tmpAxis, gasAxis;

	@FXML
	private GoogleMapView mapView;

	private Timer t;
	private int i = 0;

	@FXML
	private Label humText, tempText, gasText, gpsText, sensorText, controllerText, camText;

	@FXML
	private Menu helpText, languajeText, controll;

	@FXML
	private MenuItem esLanguaje, enLanguaje, help, moreInfo, exportData;

	private ResourceBundle resources;
	private Browser browser;
	private BrowserView view;
	private Timeline timeline;
	private URL peticion;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.resources = resources;
		setLocale(new Locale("es", "ES"));

		esLanguaje.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setLocale(new Locale("es", "ES"));
			}
		});

		enLanguaje.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setLocale(new Locale("en", "EN"));
			}
		});

		exportData.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();

				// Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
				fileChooser.getExtensionFilters().add(extFilter);

				// Show save file dialog
				File file = fileChooser.showSaveDialog(null);

				if (file != null) {
					RobotData.getInstance().exportData(file);
				}
			}
		});
		// Datos de prueba para rellenar gr�ficas
		chargeData();

		// A�adir el mapa
		mapView.addMapInializedListener(this);

		// A�adir im�genes
		setImages();

		// A�adir C�mara
		setCamera();
		try {
			dataDaemon();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Charge of data to testing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void chargeData() {
		// Sensor de humedad
		XYChart.Series hum = new XYChart.Series();
		hum.setName("Percentage of humidity");
		hum.getData().add(new XYChart.Data("10:00", 23));

		humidity.getData().addAll(hum);

		// Set ranges
		humAxis.setAutoRanging(false);
		humAxis.setLowerBound(0);
		humAxis.setUpperBound(60);
		humAxis.setTickUnit(10);
		humAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(gasAxis) {
			@Override
			public String toString(Number object) {
				return (object.intValue()) + "%";
			}
		});

		// Sensor de Temeperatura
		XYChart.Series series1 = new XYChart.Series<>();
		series1.setName("Temp Max");
		series1.getData().add(new XYChart.Data<>("10:01", 28));
		series1.getData().add(new XYChart.Data<>("10:02", 30));
		series1.getData().add(new XYChart.Data<>("10:03", 30));
		series1.getData().add(new XYChart.Data<>("10:04", 32));
		series1.getData().add(new XYChart.Data<>("10:05", 32));
		series1.getData().add(new XYChart.Data<>("10:06", 32));

		XYChart.Series series2 = new XYChart.Series<>();
		series2.setName("Temp Act");
		series2.getData().add(new XYChart.Data<>("10:01", 28));
		series2.getData().add(new XYChart.Data<>("10:02", 30));
		series2.getData().add(new XYChart.Data<>("10:03", 29));
		series2.getData().add(new XYChart.Data<>("10:04", 32));
		series2.getData().add(new XYChart.Data<>("10:05", 25));
		series2.getData().add(new XYChart.Data<>("10:06", 27));

		XYChart.Series series3 = new XYChart.Series<>();
		series3.setName("Temp Min");
		series3.getData().add(new XYChart.Data<>("10:01", 28));
		series3.getData().add(new XYChart.Data<>("10:02", 28));
		series3.getData().add(new XYChart.Data<>("10:03", 28));
		series3.getData().add(new XYChart.Data<>("10:04", 28));
		series3.getData().add(new XYChart.Data<>("10:05", 25));
		series3.getData().add(new XYChart.Data<>("10:06", 25));

		// Set ranges
		tmpAxis.setAutoRanging(false);
		tmpAxis.setLowerBound(-10);
		tmpAxis.setUpperBound(40);
		tmpAxis.setTickUnit(2);
		tmpAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(gasAxis) {
			@Override
			public String toString(Number object) {
				return (object.intValue()) + "�C";
			}
		});

		temperature.getData().addAll(series1, series2, series3);

		// Sensor de Gas
		XYChart.Series met = new XYChart.Series<>();
		XYChart.Series but = new XYChart.Series<>();
		XYChart.Series co2 = new XYChart.Series<>();
		met.getData().add(new XYChart.Data("Metano", 5));
		but.getData().add(new XYChart.Data("Butano", 3));
		co2.getData().add(new XYChart.Data("CO2", 1));

		gas.setLegendVisible(false);
		gas.getData().addAll(met, but, co2);

		// Set ranges
		gasAxis.setAutoRanging(false);
		gasAxis.setLowerBound(0);
		gasAxis.setUpperBound(10);
		gasAxis.setTickUnit(1);
		gasAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(gasAxis) {
			@Override
			public String toString(Number object) {
				return (object.intValue()) + "%";
			}
		});
	}

	/*
	 * Set all images of the buttons
	 * 
	 */
	private void setImages() {
		// Boton derecho
		Image image = new Image(getClass().getResourceAsStream("/images/rightArrow.png")); // Cargamos
		// la
		// imagen
		ImageView imageViewRight = new ImageView(image); // Creamos el imageView
		// con la imagen
		// anterior
		// Ajustamos el tama�o de la imagen al del boton
		imageViewRight.setFitHeight(50);
		imageViewRight.setFitWidth(50);
		// Le ponemos la imagen al boton
		rightButton.setGraphic(imageViewRight);
		rightButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addTarea("d");
			}
		});
		// Boton izquierdo
		image = new Image(getClass().getResourceAsStream("/images/leftArrow.png"));
		ImageView imageViewLeft = new ImageView(image);
		imageViewLeft.setFitHeight(50);
		imageViewLeft.setFitWidth(50);
		leftButton.setGraphic(imageViewLeft);
		leftButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addTarea("a");
			}
		});

		// Boton arriba
		image = new Image(getClass().getResourceAsStream("/images/upArrow.png"));
		ImageView imageViewUp = new ImageView(image);
		imageViewUp.setFitHeight(50);
		imageViewUp.setFitWidth(50);
		upButton.setGraphic(imageViewUp);
		upButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addTarea("w");
			}
		});

		// Boton abajo
		image = new Image(getClass().getResourceAsStream("/images/downArrow.png"));
		ImageView imageViewDown = new ImageView(image);
		imageViewDown.setFitHeight(50);
		imageViewDown.setFitWidth(50);
		downButton.setGraphic(imageViewDown);
		downButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addTarea("s");
			}
		});

		// Boton rotar derecha
		image = new Image(getClass().getResourceAsStream("/images/rightRotationArrow.png"));
		ImageView imageViewRightR = new ImageView(image);
		imageViewRightR.setFitHeight(50);
		imageViewRightR.setFitWidth(50);
		rightRotationButton.setGraphic(imageViewRightR);

		// Boton rotar izquierda
		image = new Image(getClass().getResourceAsStream("/images/leftRotationArrow.png"));
		ImageView imageViewLeftR = new ImageView(image);
		imageViewLeftR.setFitHeight(50);
		imageViewLeftR.setFitWidth(50);
		leftRotationButton.setGraphic(imageViewLeftR);

		// Boton camara derecho
		image = new Image(getClass().getResourceAsStream("/images/rightArrow.png"));
		ImageView imageViewRightC = new ImageView(image);
		imageViewRightC.setFitHeight(50);
		imageViewRightC.setFitWidth(50);
		camRight.setGraphic(imageViewRightC);
		camRight.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//peticionHttp("http://192.168.4.1/-camx");
				addTarea("-camx");
			}
		});
		// Boton camara izquierdo
		image = new Image(getClass().getResourceAsStream("/images/leftArrow.png"));
		ImageView imageViewLeftC = new ImageView(image);
		imageViewLeftC.setFitHeight(50);
		imageViewLeftC.setFitWidth(50);
		camLeft.setGraphic(imageViewLeftC);
		camLeft.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//peticionHttp("http://192.168.4.1/+camx");
				addTarea("+camx");
			}
		});

		// Boton camara arriba
		image = new Image(getClass().getResourceAsStream("/images/upArrow.png"));
		ImageView imageViewUpC = new ImageView(image);
		imageViewUpC.setFitHeight(50);
		imageViewUpC.setFitWidth(50);
		camUp.setGraphic(imageViewUpC);
		camUp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//peticionHttp("http://192.168.4.1/+camy");
				addTarea("+camy");
			}
		});

		// Boton camara abajo
		image = new Image(getClass().getResourceAsStream("/images/downArrow.png"));
		ImageView imageViewDownC = new ImageView(image);
		imageViewDownC.setFitHeight(50);
		imageViewDownC.setFitWidth(50);
		camDown.setGraphic(imageViewDownC);
		camDown.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
					//peticionHttp("http://192.168.4.1/-camy");
				addTarea("-camy");
			}
		});

		camCenter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				/*peticionHttp("http://192.168.4.1/camx=90");
				long t = System.currentTimeMillis();
				while(System.currentTimeMillis() - t < 1500){}
				peticionHttp("http://192.168.4.1/camy=120");*/
				addTarea("camx=90");
				addTarea("camy=120");
			}
		});
	}

	/*
	 * Conect the main app with the FXML controller
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	private void peticionHttp(String pet) {
		new PeticionHttp(pet).start();
	}

	@SuppressWarnings("unchecked")
	private void dataDaemon() throws IOException {

		timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), ev -> {

			// Datos para prueba
			RobotData.getInstance().setBut(new Random().nextInt(10));
			RobotData.getInstance().setMet(new Random().nextInt(10));
			RobotData.getInstance().setCo2(new Random().nextInt(10));
			//RobotData.getInstance().setTemp(new Random().nextInt(50) - 25);
			//RobotData.getInstance().setHum(new Random().nextInt(50));
			RobotData.getInstance().setDate(new Date());
			RobotData.getInstance().setRobotPos(RobotData.getInstance().getNewPos()[0],
					RobotData.getInstance().getNewPos()[1]);

			// Actualizamos el texto plano
			humL.setText(RobotData.getInstance().getHum() + "%");
			tempL.setText(RobotData.getInstance().getTemp() + "�C");
			gpsL.setText("Lat: \t\t" + RobotData.getInstance().getLat() + "�\n" + "Lon: \t\t"
					+ RobotData.getInstance().getLon() + "�");
			gasL.setText("Metano: \t" + RobotData.getInstance().getMet() + "%\n" + "Butano: \t"
					+ RobotData.getInstance().getBut() + "%\n" + "CO2: \t" + RobotData.getInstance().getCo2() + "%");

			// Actualizamos las gr�ficas
			// Humedad
			((XYChart.Series<String, Number>) humidity.getData().get(0)).getData().get(0)
					.setYValue(RobotData.getInstance().getHum());

			for (Node n : humidity.lookupAll(".default-color0.chart-bar")) {
				n.setStyle("-fx-bar-fill: blue;");
			}

			// Temp Max
			((XYChart.Series<String, Number>) temperature.getData().get(0)).getData().get(0)
					.setYValue((Number) temperature.getData().get(0).getData().get(1).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(0)).getData().get(1)
					.setYValue((Number) temperature.getData().get(0).getData().get(2).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(0)).getData().get(2)
					.setYValue((Number) temperature.getData().get(0).getData().get(3).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(0)).getData().get(3)
					.setYValue((Number) temperature.getData().get(0).getData().get(4).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(0)).getData().get(4)
					.setYValue((Number) temperature.getData().get(0).getData().get(5).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(0)).getData().get(5)
					.setYValue(RobotData.getInstance().getTempMax());
			
			tmpAxis.setLowerBound(RobotData.getInstance().getTempMin()-1);
			tmpAxis.setUpperBound(RobotData.getInstance().getTempMax()+1);

			// Temp Actual
			((XYChart.Series<String, Number>) temperature.getData().get(1)).getData().get(0)
					.setYValue((Number) temperature.getData().get(1).getData().get(1).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(1)).getData().get(1)
					.setYValue((Number) temperature.getData().get(1).getData().get(2).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(1)).getData().get(2)
					.setYValue((Number) temperature.getData().get(1).getData().get(3).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(1)).getData().get(3)
					.setYValue((Number) temperature.getData().get(1).getData().get(4).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(1)).getData().get(4)
					.setYValue((Number) temperature.getData().get(1).getData().get(5).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(1)).getData().get(5)
					.setYValue(RobotData.getInstance().getTemp());

			// Temp Min
			((XYChart.Series<String, Number>) temperature.getData().get(2)).getData().get(0)
					.setYValue((Number) temperature.getData().get(2).getData().get(1).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(2)).getData().get(1)
					.setYValue((Number) temperature.getData().get(2).getData().get(2).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(2)).getData().get(2)
					.setYValue((Number) temperature.getData().get(2).getData().get(3).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(2)).getData().get(3)
					.setYValue((Number) temperature.getData().get(2).getData().get(4).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(2)).getData().get(4)
					.setYValue((Number) temperature.getData().get(2).getData().get(5).getYValue());
			((XYChart.Series<String, Number>) temperature.getData().get(2)).getData().get(5)
					.setYValue(RobotData.getInstance().getTempMin());

			// Butano
			for (Node n : gas.lookupAll(".default-color1.chart-bar")) {
				n.setStyle("-fx-bar-fill: #ff6b00;");
			}
			((XYChart.Series<String, Number>) gas.getData().get(1)).getData().get(0)
					.setYValue(RobotData.getInstance().getBut());

			// Co2
			for (Node n : gas.lookupAll(".default-color2.chart-bar")) {
				n.setStyle("-fx-bar-fill: #a75acb;");
			}
			((XYChart.Series<String, Number>) gas.getData().get(2)).getData().get(0)
					.setYValue(RobotData.getInstance().getCo2());

			// Met
			for (Node n : gas.lookupAll(".default-color0.chart-bar")) {
				n.setStyle("-fx-bar-fill: #ccd100;");
			}
			((XYChart.Series<String, Number>) gas.getData().get(0)).getData().get(0)
					.setYValue(RobotData.getInstance().getMet());

		}));
		timeline.setCycleCount(Integer.MAX_VALUE);
		timeline.play();
	}

	/*
	 * Inicialization of the map, with the position of the robot
	 * 
	 * @see com.lynden.gmapsfx.MapComponentInitializedListener#mapInitialized()
	 */
	@Override
	public void mapInitialized() {
		// Set the position of the map.
		LatLong robotPosition = new LatLong(43.538762, -5.698957);

		// Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();

		mapOptions.center(robotPosition).overviewMapControl(false).panControl(false).rotateControl(false)
				.scaleControl(false).streetViewControl(false).zoomControl(false).zoom(20);

		// Opciones del marcador del robot
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(robotPosition).title("Tank position").animation(Animation.DROP).visible(true);

		map = mapView.createMap(mapOptions, false);

		// A�adimos el marcador en la posici�n actual del tanque
		robotPos = new Marker(markerOptions);
		map.addMarker(robotPos);

		// A�adimos un evento al pulsar en el mapa
		map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));

			Parent root;
			try {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("ui/MapPopUp.fxml"));
				loader.setController(new mapPopUpController());
				root = loader.load();
				Stage stage = new Stage();
				stage.setTitle(resources.getString("locationWindow"));
				stage.setScene(new Scene(root));
				stage.initModality(Modality.APPLICATION_MODAL);
				((mapPopUpController) loader.getController()).setLatLon(ll);
				((mapPopUpController) loader.getController()).setResource(resources);
				;
				stage.show();

			} catch (IOException e) {
				e.printStackTrace();
			}

		});

	}

	@FXML
	private void openHelp() {
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("ui/help.fxml"));
			loader.setController(new helpController());
			root = loader.load();
			Stage stage = new Stage();
			stage.setTitle(resources.getString("Help"));
			stage.setScene(new Scene(root));
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setLocale(Locale locale) {
		resources = ResourceBundle.getBundle("bundles.MyBundle", locale);

		humText.setText(resources.getString("Hum"));
		tempText.setText(resources.getString("Temp"));
		gasText.setText(resources.getString("Gas"));
		gpsText.setText(resources.getString("GPS"));
		sensorText.setText(resources.getString("sensor"));
		controllerText.setText(resources.getString("tankControll"));
		camText.setText(resources.getString("camControl"));
		helpText.setText(resources.getString("Help"));
		languajeText.setText(resources.getString("languajeText"));
		esLanguaje.setText(resources.getString("esLanguaje"));
		enLanguaje.setText(resources.getString("enLanguaje"));

		exportData.setText(resources.getString("exportData"));
		controll.setText(resources.getString("controll"));
		help.setText(resources.getString("Help"));
		moreInfo.setText(resources.getString("moreInfo"));

		gas.setTitle(resources.getString("Gas"));
		temperature.setTitle(resources.getString("Temp"));
		humidity.setTitle(resources.getString("Hum"));
	}

	private void setCamera() {
		browser = new Browser();
		view = new BrowserView(browser);
		view.setPadding(new Insets(2, 2, 2, 2));
		sensorPane.add(view, 2, 1);
		browser.loadURL("http://localhost:8180/cam.html");
		/*
		 * WebView b = new WebView(); WebEngine webEngine = b.getEngine();
		 * webEngine.load("http://localhost:8180/cam.html"); sensorPane.add(b,
		 * 2, 1);
		 */
	}

	public void stopFunctions() {
		timeline.stop();
		browser.dispose();
	}

	public void addTarea(String t){
		RobotData.getInstance().addTarea(t);
	}
	
}

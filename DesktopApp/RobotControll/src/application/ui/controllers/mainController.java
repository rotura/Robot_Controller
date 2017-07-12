package application.ui.controllers;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import com.sun.jndi.toolkit.url.Uri;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import application.Main;
import application.model.RobotData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
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
	private LineChart<?, ?> temperature, digital, analog;

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
	private TilePane sensorPane, dataPane;

	@FXML
	private BorderPane tempPane, humPane, gasPane, gpsPane, dataBPane, cameraPane, dPane, aPane;

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
	private NumberAxis humAxis, tmpAxis, gasAxis, analogAxis, digitalAxis;
	@FXML
	private GoogleMapView mapView;

	private Timer t;
	private int i = 0;

	@FXML
	private Label humText, tempText, gasText, gpsText, sensorText, controllerText, camText;

	@FXML
	private Label humPaneLabel, tempPaneLabel, gasPaneLabel, gpsPaneLabel, dataPaneLabel, cameraPaneLabel, dLabel,
			aLabel;

	@FXML
	private Menu helpText, languajeText, controll;

	@FXML
	private MenuItem esLanguaje, enLanguaje, help, moreInfo, exportData, cancell;

	@FXML
	private CheckMenuItem tempView, humView, gasView, gpsView, cameraView, dataView, digitAnag;

	private ResourceBundle resources;
	private Browser browser;
	private BrowserView view;
	private Timeline timeline;
	private URL peticion;
	private boolean state = false;
	private boolean buttonPressed = false;
	private Date date;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		sensorPane.setHgap(10);
		sensorPane.setVgap(10);
		gpsPane.setPrefSize(50, 50);
		sensorPane.setPrefTileHeight(400);
		sensorPane.setPrefTileWidth(400);

		this.resources = resources;
		setLocale(new Locale("es", "ES"));

		esLanguaje.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				setLocale(new Locale("es", "ES"));
			}
		});

		tempView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (tempView.isSelected()) {
					sensorPane.getChildren().add(tempPane);
				} else {
					sensorPane.getChildren().remove(tempPane);
				}
			}
		});

		gasView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (gasView.isSelected()) {
					sensorPane.getChildren().add(gasPane);
				} else {
					sensorPane.getChildren().remove(gasPane);
				}
			}
		});

		humView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (humView.isSelected()) {
					sensorPane.getChildren().add(humPane);
				} else {
					sensorPane.getChildren().remove(humPane);
				}
			}
		});

		cameraView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (cameraView.isSelected()) {
					sensorPane.getChildren().add(cameraPane);
				} else {
					sensorPane.getChildren().remove(cameraPane);
				}
			}
		});

		gpsView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (gpsView.isSelected()) {
					sensorPane.getChildren().add(gpsPane);
				} else {
					sensorPane.getChildren().remove(gpsPane);
				}
			}
		});

		dataView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (dataView.isSelected()) {
					sensorPane.getChildren().add(dataBPane);
				} else {
					sensorPane.getChildren().remove(dataBPane);
				}
			}
		});

		digitAnag.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (digitAnag.isSelected()) {
					sensorPane.getChildren().add(dPane);
					sensorPane.getChildren().add(aPane);
				} else {
					sensorPane.getChildren().remove(dPane);
					sensorPane.getChildren().remove(aPane);
				}
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
		// Datos de inicio para rellenar gráficas
		chargeData();

		// Añadir el mapa
		mapView.addMapInializedListener(this);

		// Añadir imágenes
		setImages();

		// Añadir Cámara
		setCamera();

	}

	/*
	 * Charge of data to testing
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void chargeData() {
		// Sensor de humedad
		XYChart.Series hum = new XYChart.Series();
		hum.setName("Percentage of humidity");
		hum.getData().add(new XYChart.Data("%", 0));

		humidity.getData().addAll(hum);

		// Set ranges
		humAxis.setAutoRanging(false);
		humAxis.setLowerBound(0);
		humAxis.setUpperBound(70);
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
		series1.getData().add(new XYChart.Data<>("1", 0));
		series1.getData().add(new XYChart.Data<>("2", 0));
		series1.getData().add(new XYChart.Data<>("3", 0));
		series1.getData().add(new XYChart.Data<>("4", 0));
		series1.getData().add(new XYChart.Data<>("5", 0));
		series1.getData().add(new XYChart.Data<>("6", 0));

		XYChart.Series series2 = new XYChart.Series<>();
		series2.setName("Temp Act");
		series2.getData().add(new XYChart.Data<>("1", 0));
		series2.getData().add(new XYChart.Data<>("2", 0));
		series2.getData().add(new XYChart.Data<>("3", 0));
		series2.getData().add(new XYChart.Data<>("4", 0));
		series2.getData().add(new XYChart.Data<>("5", 0));
		series2.getData().add(new XYChart.Data<>("6", 0));

		XYChart.Series series3 = new XYChart.Series<>();
		series3.setName("Temp Min");
		series3.getData().add(new XYChart.Data<>("1", 0));
		series3.getData().add(new XYChart.Data<>("2", 0));
		series3.getData().add(new XYChart.Data<>("3", 0));
		series3.getData().add(new XYChart.Data<>("4", 0));
		series3.getData().add(new XYChart.Data<>("5", 0));
		series3.getData().add(new XYChart.Data<>("6", 0));

		// Set ranges
		tmpAxis.setAutoRanging(false);
		tmpAxis.setLowerBound(-10);
		tmpAxis.setUpperBound(40);
		tmpAxis.setTickUnit(2);
		tmpAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(gasAxis) {
			@Override
			public String toString(Number object) {
				return (object.intValue()) + "ºC";
			}
		});

		temperature.getData().addAll(series1, series2, series3);

		// Sensor de Gas
		XYChart.Series met = new XYChart.Series<>();
		XYChart.Series but = new XYChart.Series<>();
		XYChart.Series co2 = new XYChart.Series<>();
		met.getData().add(new XYChart.Data("Metano", 0));
		but.getData().add(new XYChart.Data("Butano", 0));
		co2.getData().add(new XYChart.Data("CO2", 0));

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

		// Sensor de Temeperatura
		XYChart.Series analog = new XYChart.Series<>();
		analog.setName("Analog");
		analog.getData().add(new XYChart.Data<>("1", 0));
		analog.getData().add(new XYChart.Data<>("2", 0));
		analog.getData().add(new XYChart.Data<>("3", 0));
		analog.getData().add(new XYChart.Data<>("4", 0));
		analog.getData().add(new XYChart.Data<>("5", 0));
		analog.getData().add(new XYChart.Data<>("6", 0));

		// Set ranges
		analogAxis.setAutoRanging(false);
		analogAxis.setLowerBound(-1);
		analogAxis.setUpperBound(1025);
		analogAxis.setTickUnit(2);
		analogAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(gasAxis) {
			@Override
			public String toString(Number object) {
				return (object.intValue() + "");
			}
		});

		this.analog.getData().addAll(analog);

		// Sensor digital
		XYChart.Series digit = new XYChart.Series<>();
		digit.setName("Digital");
		digit.getData().add(new XYChart.Data<>("1", 0));
		digit.getData().add(new XYChart.Data<>("2", 0));
		digit.getData().add(new XYChart.Data<>("3", 0));
		digit.getData().add(new XYChart.Data<>("4", 0));
		digit.getData().add(new XYChart.Data<>("5", 0));
		digit.getData().add(new XYChart.Data<>("6", 0));

		// Set ranges
		digitalAxis.setAutoRanging(false);
		digitalAxis.setLowerBound(-1);
		digitalAxis.setUpperBound(2);
		digitalAxis.setTickUnit(1);
		digitalAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(gasAxis) {
			@Override
			public String toString(Number object) {
				return (object.intValue() + "");
			}
		});

		digital.getData().addAll(digit);

	}

	/*
	 * Set all images of the buttons
	 * 
	 */
	private void setImages() {

		// Boton derecho
		Image image = new Image(getClass().getResourceAsStream("/images/rightArrow.png")); // Cargamos
		// la imagen
		ImageView imageViewRight = new ImageView(image); // Creamos el imageView
		// con la imagen
		// anterior
		// Ajustamos el tamaño de la imagen al del boton
		imageViewRight.setFitHeight(50);
		imageViewRight.setFitWidth(50);
		// Le ponemos la imagen al boton
		rightButton.setGraphic(imageViewRight);
		rightButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!buttonPressed) {
					addTarea("d");
					DropShadow shadow = new DropShadow();
					rightButton.setEffect(shadow);
					buttonPressed = true;
				}
			}
		});
		rightButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				addTarea("x");
				rightButton.setEffect(null);
				buttonPressed = false;
			}
		});

		// Boton izquierdo
		image = new Image(getClass().getResourceAsStream("/images/leftArrow.png"));
		ImageView imageViewLeft = new ImageView(image);
		imageViewLeft.setFitHeight(50);
		imageViewLeft.setFitWidth(50);
		leftButton.setGraphic(imageViewLeft);
		leftButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!buttonPressed) {
					addTarea("a");
					DropShadow shadow = new DropShadow();
					leftButton.setEffect(shadow);
				}
			}
		});
		leftButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				addTarea("x");
				leftButton.setEffect(null);
				buttonPressed = false;
			}
		});

		// Boton arriba
		image = new Image(getClass().getResourceAsStream("/images/upArrow.png"));
		ImageView imageViewUp = new ImageView(image);
		imageViewUp.setFitHeight(50);
		imageViewUp.setFitWidth(50);
		upButton.setGraphic(imageViewUp);
		upButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!buttonPressed) {
					addTarea("w");
					DropShadow shadow = new DropShadow();
					upButton.setEffect(shadow);
				}
			}
		});
		upButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				addTarea("x");
				upButton.setEffect(null);
				buttonPressed = false;
			}
		});

		// Boton abajo
		image = new Image(getClass().getResourceAsStream("/images/downArrow.png"));
		ImageView imageViewDown = new ImageView(image);
		imageViewDown.setFitHeight(50);
		imageViewDown.setFitWidth(50);
		downButton.setGraphic(imageViewDown);
		downButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!buttonPressed) {
					addTarea("s");
					DropShadow shadow = new DropShadow();
					downButton.setEffect(shadow);
				}
			}
		});
		downButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				addTarea("x");
				downButton.setEffect(null);
				buttonPressed = false;
			}
		});

		// Boton rotar derecha
		image = new Image(getClass().getResourceAsStream("/images/rightRotationArrow.png"));
		ImageView imageViewRightR = new ImageView(image);
		imageViewRightR.setFitHeight(50);
		imageViewRightR.setFitWidth(50);
		rightRotationButton.setGraphic(imageViewRightR);
		rightRotationButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!buttonPressed) {
					addTarea("e");
					DropShadow shadow = new DropShadow();
					rightRotationButton.setEffect(shadow);
				}
			}
		});
		rightRotationButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				addTarea("x");
				rightRotationButton.setEffect(null);
				buttonPressed = false;
			}
		});

		// Boton rotar izquierda
		image = new Image(getClass().getResourceAsStream("/images/leftRotationArrow.png"));
		ImageView imageViewLeftR = new ImageView(image);
		imageViewLeftR.setFitHeight(50);
		imageViewLeftR.setFitWidth(50);
		leftRotationButton.setGraphic(imageViewLeftR);
		leftRotationButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!buttonPressed) {
					addTarea("q");
					DropShadow shadow = new DropShadow();
					leftRotationButton.setEffect(shadow);
				}
			}
		});
		leftRotationButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				addTarea("x");
				leftRotationButton.setEffect(null);
				buttonPressed = false;
			}
		});

		// Boton camara derecho
		image = new Image(getClass().getResourceAsStream("/images/rightArrow.png"));
		ImageView imageViewRightC = new ImageView(image);
		imageViewRightC.setFitHeight(50);
		imageViewRightC.setFitWidth(50);
		camRight.setGraphic(imageViewRightC);
		camRight.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addTarea("-camx");
				DropShadow shadow = new DropShadow();
				camRight.setEffect(shadow);
			}
		});
		camRight.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				camRight.setEffect(null);
			}
		});
		// Boton camara izquierdo
		image = new Image(getClass().getResourceAsStream("/images/leftArrow.png"));
		ImageView imageViewLeftC = new ImageView(image);
		imageViewLeftC.setFitHeight(50);
		imageViewLeftC.setFitWidth(50);
		camLeft.setGraphic(imageViewLeftC);
		camLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addTarea("+camx");
				DropShadow shadow = new DropShadow();
				camLeft.setEffect(shadow);
			}
		});
		camLeft.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				camLeft.setEffect(null);
			}
		});

		// Boton camara arriba
		image = new Image(getClass().getResourceAsStream("/images/upArrow.png"));
		ImageView imageViewUpC = new ImageView(image);
		imageViewUpC.setFitHeight(50);
		imageViewUpC.setFitWidth(50);
		camUp.setGraphic(imageViewUpC);
		camUp.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addTarea("+camy");
				DropShadow shadow = new DropShadow();
				camUp.setEffect(shadow);
			}
		});
		camUp.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				camUp.setEffect(null);
			}
		});

		// Boton camara abajo
		image = new Image(getClass().getResourceAsStream("/images/downArrow.png"));
		ImageView imageViewDownC = new ImageView(image);
		imageViewDownC.setFitHeight(50);
		imageViewDownC.setFitWidth(50);
		camDown.setGraphic(imageViewDownC);
		camDown.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addTarea("-camy");
				DropShadow shadow = new DropShadow();
				camDown.setEffect(shadow);
			}
		});
		camDown.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				camDown.setEffect(null);
			}
		});

		camCenter.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				addTarea("camx=90");
				addTarea("camy=120");
				DropShadow shadow = new DropShadow();
				camCenter.setEffect(shadow);
			}
		});

		camCenter.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				camCenter.setEffect(null);
			}
		});
	}

	/*
	 * Conect the main app with the FXML controller
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * This method start a daemon to update all graphics
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void dataDaemon() throws IOException {
		date = new Date();
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {

			// Actualizamos el texto plano

			gasL.setText(gasL.getText() + RobotData.getInstance().getData());
			gasL.setScrollTop(Double.MAX_VALUE);

			// Opciones del marcador del robot

			MarkerOptions markerOptions = new MarkerOptions();
			LatLong x = new LatLong(RobotData.getInstance().getLat(), RobotData.getInstance().getLon());
			markerOptions.position(x).title("Tank position").animation(Animation.DROP).visible(true);

			// Añadimos el marcador en la posición actual del tanque
			map.removeMarker(robotPos);
			robotPos = new Marker(markerOptions);
			map.addMarker(robotPos);

			// Actualizamos las gráficas
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

			tmpAxis.setLowerBound(RobotData.getInstance().getTempMin() - 1);
			tmpAxis.setUpperBound(RobotData.getInstance().getTempMax() + 1);

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
			;
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

			// Analogico
			((XYChart.Series<String, Number>) analog.getData().get(0)).getData().get(0)
					.setYValue((Number) analog.getData().get(0).getData().get(1).getYValue());
			((XYChart.Series<String, Number>) analog.getData().get(0)).getData().get(1)
					.setYValue((Number) analog.getData().get(0).getData().get(2).getYValue());
			((XYChart.Series<String, Number>) analog.getData().get(0)).getData().get(2)
					.setYValue((Number) analog.getData().get(0).getData().get(3).getYValue());
			((XYChart.Series<String, Number>) analog.getData().get(0)).getData().get(3)
					.setYValue((Number) analog.getData().get(0).getData().get(4).getYValue());
			((XYChart.Series<String, Number>) analog.getData().get(0)).getData().get(4)
					.setYValue((Number) analog.getData().get(0).getData().get(5).getYValue());
			((XYChart.Series<String, Number>) analog.getData().get(0)).getData().get(5)
					.setYValue(RobotData.getInstance().getAnalog());

			// Digital
			((XYChart.Series<String, Number>) digital.getData().get(0)).getData().get(0)
					.setYValue((Number) digital.getData().get(0).getData().get(1).getYValue());
			((XYChart.Series<String, Number>) digital.getData().get(0)).getData().get(1)
					.setYValue((Number) digital.getData().get(0).getData().get(2).getYValue());
			((XYChart.Series<String, Number>) digital.getData().get(0)).getData().get(2)
					.setYValue((Number) digital.getData().get(0).getData().get(3).getYValue());
			((XYChart.Series<String, Number>) digital.getData().get(0)).getData().get(3)
					.setYValue((Number) digital.getData().get(0).getData().get(4).getYValue());
			((XYChart.Series<String, Number>) digital.getData().get(0)).getData().get(4)
					.setYValue((Number) digital.getData().get(0).getData().get(5).getYValue());
			((XYChart.Series<String, Number>) digital.getData().get(0)).getData().get(5)
					.setYValue(RobotData.getInstance().getDigital());

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
		LatLong robotPosition = new LatLong(0.0, 0.0);

		// Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();
		mapOptions.center(robotPosition).overviewMapControl(false).panControl(false).rotateControl(false)
				.scaleControl(false).streetViewControl(false).zoomControl(false).zoom(20);

		// Opciones del marcador del robot
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(robotPosition).title("Tank position").animation(Animation.DROP).visible(true);

		map = mapView.createMap(mapOptions, false);
		// Añadimos el marcador en la posición actual del tanque
		robotPos = new Marker(markerOptions);
		map.addMarker(robotPos);

		// Añadimos un evento al pulsar en el mapa
		map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
			if (state) {
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
			loader.setResources(resources);
			root = loader.load();
			Stage stage = new Stage();
			stage.setTitle(resources.getString("Help"));
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void startConexion() {
		t = new Timer();
		if (!state) {

			state = true;

			// Start webDaemon
			t.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					InputStream response = null;
					try {
						RobotData.getInstance().sem.acquire();
						String uri = "http://192.168.4.1/";
						String tarea = RobotData.getInstance().getTarea();
						if (tarea != null) {
							uri += tarea + ";";
							uri = uri.substring(0, uri.length() - 1);
						}
						URL url = new URL(uri);
						response = url.openStream();

						try (Scanner scanner = new Scanner(response)) {
							String responseBody = scanner.useDelimiter("\\A").next();
							String[] data = responseBody.split(";");

							if (data != null && data.length > 0) {
								RobotData.getInstance().setDate(new Date());
								for (String sensor : data) { // Read all sensors
																// in
																// the response
									switch (sensor.split(":")[0]) {
									case "temp":
										RobotData.getInstance().setTemp(Double.parseDouble(sensor.split(":")[1]));
										break;

									case "hum":
										RobotData.getInstance().setHum(Double.parseDouble(sensor.split(":")[1]));
										break;

									case "but":
										RobotData.getInstance().setBut(Double.parseDouble(sensor.split(":")[1]));
										break;

									case "met":
										RobotData.getInstance().setMet(Double.parseDouble(sensor.split(":")[1]));
										break;

									case "gps":
										RobotData.getInstance().setRobotPos((Double.parseDouble(sensor.split(":")[1])),
												Double.parseDouble(sensor.split(":")[2]));
										break;

									case "co2":
										RobotData.getInstance().setMet(Double.parseDouble(sensor.split(":")[1]));
										break;

									case "digit":
										RobotData.getInstance().setDigital(Integer.parseInt(sensor.split(":")[1]));
										break;

									case "analog":
										RobotData.getInstance().setAnalog(Integer.parseInt(sensor.split(":")[1]));
										break;

									}
								}
							}
						}
					} catch (IOException e) {
						System.out.println("Fail to connect with the robot");
					} catch (Exception e) {
						System.out.println("Fail to connect with the robot");
					}
					RobotData.getInstance().sem.release();
				}
			}, 0, // run first occurrence immediately
					3500);

			// Start graphic updater
			try {
				dataDaemon();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setLocale(Locale locale) {
		resources = ResourceBundle.getBundle("bundles.MyBundle", locale);

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
		cancell.setText(resources.getString("lessInfo"));

		gasPaneLabel.setText(resources.getString("Gas"));
		tempPaneLabel.setText(resources.getString("Temp"));
		humPaneLabel.setText(resources.getString("Hum"));
		dataPaneLabel.setText(resources.getString("sensor"));
		gpsPaneLabel.setText(resources.getString("GPS"));
		cameraPaneLabel.setText(resources.getString("camera"));
		dLabel.setText(resources.getString("digit"));
		aLabel.setText(resources.getString("Anag"));

		tempView.setText(resources.getString("Temp"));
		humView.setText(resources.getString("Hum"));
		gasView.setText(resources.getString("Gas"));
		gpsView.setText(resources.getString("GPS"));
		dataView.setText(resources.getString("sensor"));
		cameraView.setText(resources.getString("camera"));
		digitAnag.setText(resources.getString("digitAnag"));

	}

	/**
	 * Start the browser with the camera script
	 */
	private void setCamera() {
		browser = new Browser();
		view = new BrowserView(browser);
		view.setPadding(new Insets(2, 2, 2, 2));
		cameraPane.setCenter(view);
		;
		String location = null;
		try {
			location = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "resources"
					+ File.separator + "cam.html").toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			System.out.println("Cant find URL");
		}

		browser.loadURL(location);
	}

	/**
	 * Stop all threads
	 */
	public void stopFunctions() {
		if (state) {
			timeline.stop();
			t.cancel();
			state = false;
		}
	}

	
	public void exit(){
		if(state){
			timeline.stop();
			t.cancel();
		}
		browser.dispose();
	}
	
	public void addTarea(String t) {
		RobotData.getInstance().addTarea(t);
	}

}

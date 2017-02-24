# Robot_Controller
Finish project of the University. A javaFX application to control the robot and view in real time sensors and camera, and an Arduino Code to make the robot.

## User Manual
### Export Data
- In the menu **"Controll"**, is the option **"Export Data"**.
- The data will be saved in a **CSV**.
- The column separator to Excel is '**;**'.
- The csv opened with Excel format the data automatically.

### Camera controll usage
- The camera controller is runing in a Tomcat server.
- This server is Open and Close automatically with the App.
- To make it run, you need a **JAVA_HOME** or **JRE_HOME** variable in the System.
- If dont work, you need to set **CATALINA_HOME** system's variable pointing to **/DesktopApp/apache-tomcat-9.0.0.M17**

## Requirements:
 - Show robot's camera in real time
 - Show all sensor as raw input
  -  MQ2
  -  MQ3
  -  MQ7
  -  Temprature Sensor
  -  Humidity Sensor
  -  Light Sensor
  -  GPS (NMEA)
 - Show all sensor in charts (GPS in a map)
 - Controlls for the robot
  -  Move Forward, Backward
  -  Rotate Right, Left
  -  Move in circle Right, left
 - Controlls for the camera
  -  Move Tilt, Pan
 - Mark a destiny point in th map
  -  The robot must go to the marked position alone 
  
## Languages
 - Java
  -  Logic and Model of the desktop application
 - JavaFX
  -  Desktop application's UI
 - CSS
  -  Style for the UI
 - HTML
  -  Special objects to inject in the Desktop Application
 - JavaScritp
  -  Use in group with HTML in special objects
 - Arduino
  -  To programm the propper robot

#include <TinyGPS.h>

#include <Wire.h>
#include <Servo.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>

const char password[] = "uniovi";

WiFiServer server(80);
String adelante = "15";
String atras = "0";
char charBuf[3];
long temp, hum, co2, met, but = 0;
float lat, lon = 0.0;
int pinTemp = D3;
int pinHum = D4;
int pinCo2 = D7;
int pinMet = D8;
//But in A0

TinyGPS gps;
SoftwareSerial ss(D5, D6);

void setup() 
{
  Serial.begin(115200);
  ss.begin(9600);
  Serial.println("Inicio Setup");
  Wire.begin(D1,D2); // join i2c bus (address optional for master)

  //WIFI
  setupWiFi();
  server.begin();
  Serial.println("Fin Setup");
  Serial.print("Servidor Web en: http://");
  Serial.print(WiFi.softAPIP());

  //SENSORES
  pinMode(pinTemp, INPUT);      // set pin as input
  pinMode(pinHum, INPUT);      // set pin as input
  pinMode(pinCo2, INPUT);      // set pin as input
  pinMode(pinMet, INPUT);      // set pin as input
  
}

void loop() 
{
  /*Wire.beginTransmission(8); // transmit to device #8
  atras.toCharArray(charBuf, 3);
  Wire.write(charBuf);
  adelante.toCharArray(charBuf, 3);
  Wire.write(",");
  Wire.write(charBuf);
  Wire.write(",");*/
  leerSensores();
  llamarGps();
  String result = "temp:" + String(temp) + ";hum:" + String(hum) + ";" + "lat:" + String(lat, 6) + ";" + "lon:" + String(lon, 6) + ";" + "met:" + String(met) + ";" + "but:" + String(but) + ";" + "co2:" + String(co2) + ";";
  char res[result.length()+1]; 
  result.toCharArray(res, result.length()+1);
  Serial.println(res);




  
  // Comprueba clientes conectados
  WiFiClient cliente = server.available();
  if (!cliente) {
    // Mientras no haya cliente repito el bucle
    delay(100);
    //Wire.endTransmission();
    return;
  }

 while (Serial.available())
    {
      char c = Serial.read();
      cliente.print(c); // uncomment this line if you want to see the GPS data flowing
    }
    // Hay cliente!
  //Serial.println("Nuevo cliente, esperar mientras no está listo");
  while(!cliente.available()){
    delay(100);
  }
 
  // Leo la petición , hasta el salto de linea \n
  String peticion = cliente.readStringUntil('\r');
  //Serial.println(peticion);
  cliente.flush();

// Enviamos al cliente una respuesta HTTP
  cliente.println("HTTP/1.1 200 OK");
  cliente.println("Content-Type: text/html");
  cliente.println();
 
  // Miro si contiene alguna de las dos cadenas 
  if (peticion.indexOf("abrir") != -1) {
    cliente.write("ABIERTO");
    //Wire.write("Abierto");        // sends five bytes
  } 
  if (peticion.indexOf("cerrar") != -1){
    cliente.write("CERRADO");
    //Wire.write("Cerrado");        // sends five bytes
  }
    if (peticion.indexOf("datos") != -1){
    cliente.write(res);
  }
 
  //Wire.endTransmission();
  // Pequeña pausa para asegurar el envio de datos
  delay(2000);

}






void leerSensores(){

  temp = digitalRead(pinTemp);     // read the input pin
  hum = digitalRead(pinHum);     // read the input pin
  co2 = digitalRead(pinCo2);     // read the input pin
  met = digitalRead(pinMet);     // read the input pin 
  but = analogRead(A0);     // read the input pin

}




IPAddress subnet(255, 255, 255, 0);
IPAddress ip(192, 168, 4, 5);

void setupWiFi()
{
  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(ip, ip, subnet);
  // Do a little work to get a unique-ish name. Append the
  // last two bytes of the MAC (HEX'd) to "Thing-":
  uint8_t mac[WL_MAC_ADDR_LENGTH];
  WiFi.softAPmacAddress(mac);
  String macID = String(mac[WL_MAC_ADDR_LENGTH - 2], HEX) +
                 String(mac[WL_MAC_ADDR_LENGTH - 1], HEX);
  macID.toUpperCase();
  String AP_NameString = "Puertaaa " + macID;

  char AP_NameChar[AP_NameString.length() + 1];
  memset(AP_NameChar, 0, AP_NameString.length() + 1);

  for (int i=0; i<AP_NameString.length(); i++)
    AP_NameChar[i] = AP_NameString.charAt(i);

  //WiFi.softAP(AP_NameChar);
  WiFi.softAP(AP_NameChar,password);
  // IP: http://192.168.4.5/
}









void llamarGps(){
  bool newData = false;
  unsigned long chars;
  unsigned short sentences, failed;

  // For one second we parse GPS data and report some key values
  for (unsigned long start = millis(); millis() - start < 1000;)
  {
    while (ss.available())
    {
      char c = ss.read();
      //Serial.write(c); // uncomment this line if you want to see the GPS data flowing
      if (gps.encode(c)) // Did a new valid sentence come in?
        newData = true;
    }
  }

  if (newData)
  {
    float flat, flon;
    unsigned long age;
    gps.f_get_position(&flat, &flon, &age);
    lat = flat;
    lon = flon;
  }
  else
  {
  gps.stats(&chars, &sentences, &failed);
  Serial.print(" CHARS=");
  Serial.print(chars);
  Serial.print(" SENTENCES=");
  Serial.print(sentences);
  Serial.print(" CSUM ERR=");
  Serial.println(failed);
  }
  if (chars == 0)
    Serial.println("** No characters received from GPS: check wiring **");
}


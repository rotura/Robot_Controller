#include <ESP8266.h>

const char password[] = "uniovi";

WiFiServer server(80);
long temp, hum, co2, met, but = 0;
float lat, lon = 0.0;
int velocidad = 0;

int IN3 = 5;    // Input3 conectada al pin 5
int IN4 = 4;    // Input4 conectada al pin 4 
int ENB = 3;    // ENB conectada al pin 3 de Arduino

int IN1 = 8;    // Input3 conectada al pin 5
int IN2 = 7;    // Input4 conectada al pin 4 
int ENA = 6;    // ENB conectada al pin 3 de Arduino

void setup() 
{
  Serial.begin(9600);
  Serial.println("Inicio Setup");

  //WIFI
  setupWiFi();
  server.begin();
  Serial.println("Fin Setup");
  Serial.print("Servidor Web en: http://");
  Serial.print(WiFi.softAPIP());


 pinMode (ENB, OUTPUT); 
 pinMode (IN3, OUTPUT);
 pinMode (IN4, OUTPUT);

 pinMode (ENA, OUTPUT); 
 pinMode (IN1, OUTPUT);
 pinMode (IN2, OUTPUT);
}

void loop() 
{
  Serial.println(velocidad);
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
  } 
  if (peticion.indexOf("cerrar") != -1){
    cliente.write("CERRADO");
  }
    if (peticion.indexOf("datos") != -1){
    //cliente.write(res);
  }
  if (peticion.indexOf("w") != -1){
    cliente.println("Adelante");
    adelante();
  }
  if (peticion.indexOf("a") != -1){
    cliente.println("Izquierda");
    izquierda();
  }
  if (peticion.indexOf("s") != -1){
    cliente.println("Atras");
    atras();
  }
  if (peticion.indexOf("d") != -1){
    cliente.println("Derecha");
    derecha();
  }
  if (peticion.indexOf("v=") != -1){
    cliente.println("Velocidad cambiada");
    setVelocidad(getValue(peticion, '=', 1));
    cliente.println(velocidad);
  }
 
  // Pequeña pausa para asegurar el envio de datos
  //delay(2000);

}






void leerSensores(){

  /*temp = digitalRead(pinTemp);     // read the input pin
  hum = digitalRead(pinHum);     // read the input pin
  co2 = digitalRead(pinCo2);     // read the input pin
  met = digitalRead(pinMet);     // read the input pin 
  but = analogRead(A0);     // read the input pin*/

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


void adelante(){
  Serial.print("adelante");
  digitalWrite (IN3, HIGH);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, HIGH);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(1000);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void atras(){
  Serial.print("atras");
  digitalWrite (IN3, LOW);
  digitalWrite (IN4, HIGH);

  digitalWrite (IN1, LOW);
  digitalWrite (IN2, HIGH);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(1000);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void derecha(){
  Serial.print("derecha");
  digitalWrite (IN3, LOW);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, HIGH);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(1000);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void izquierda(){
  Serial.print("izquierda");
  digitalWrite (IN3, HIGH);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, LOW);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(1000);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void setVelocidad(String s){
  Serial.print("setVelocidad");
  velocidad = s.toInt();
}

String getValue(String data, char separator, int index)
{
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

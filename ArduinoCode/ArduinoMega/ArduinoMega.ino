#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <Servo.h>
#include <TinyGPS.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>//Recuerda descargar la libreria en electrocrea.com  
#define DHTPIN 52
#define DHTTYPE DHT11  
#define RADTODEG(R)((180.0 * R) / PI)//Converts Radians to Degrees 

LiquidCrystal_I2C lcd(0x3F, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE);//Direccion de LCD

int IN3 = 10;    // Input3 conectada al pin 5
int IN4 = 9;    // Input4 conectada al pin 4 
int ENB = 3;    // ENB conectada al pin 3 de Arduino

int IN1 = 8;    // Input1 conectada al pin 5
int IN2 = 7;    // Input2 conectada al pin 4 
int ENA = 6;    // ENA conectada al pin 3 de Arduino

int counter = 0;
TinyGPS gps;

DHT dht(DHTPIN, DHTTYPE);

int pos = 0;
Servo camX, camY;
int acamX = 90;
int acamY = 90;
int velocidad = 255;

double temp, alc, but, hum = 0.0;
float lat, lon, newLat, newLon = 0.0;
String peticion = "";
boolean autopilot = false;

void setup()   { 
  camX.attach(5);
  camY.attach(4);
  Serial.begin(9600);
  //WiFi
  Serial1.begin(9600);
  delay (1000);
  Serial1.println("AT");
  delay(100);
  Serial1.println("AT+CIPMUX=1");
  delay(100);
  Serial1.println("AT+CIPSERVER=1,80");
  delay(100); 
  //GPS
  Serial2.begin(9600);
/*  lcd.begin(20,4);// Indicamos medidas de LCD     
  lcd.clear();//Elimina todos los simbolos del LCD
  lcd.setCursor(0,0);//Posiciona la primera letra despues del segmento 5 en linea 1             
  lcd.print("Robot:");
  delay (1000);//Dura 2 segundos   */
  pinMode (ENB, OUTPUT); 
  pinMode (IN3, OUTPUT);
  pinMode (IN4, OUTPUT);

  pinMode (ENA, OUTPUT); 
  pinMode (IN1, OUTPUT);
  pinMode (IN2, OUTPUT);

  dht.begin();
}

void loop() {
  readSensor();  
  //Comprobamos peticiones
    while (Serial1.available() >0 )
    {  char c = Serial1.read();
       if (c == 71) 
          {   Serial.println("Enviada Web Request");
              String pet = getValue(getValue(Serial1.readStringUntil('\r'), '/', 1), ' ',0);
              peticion = pet; 
              savePet();
              webserver();
          }
   }
  /*if(counter < 1){
    initLCD();
  }
  else{
    printLCD();  
  }*/
}


void readSensor(){
  temp = Thermistor(analogRead(0));

  int adc_MQ = analogRead(1); //Lemos la salida analógica  del MQ
  float voltaje = adc_MQ * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
  float Rs=2000*((5-voltaje)/voltaje);  //Calculamos Rs con un RL de 1k
  alc=0.4091*pow(Rs/5463, -1.497); // calculamos la concentración  de alcohol con la ecuación obtenida.

  hum = dht.readHumidity();
  delay(100);

  adc_MQ = analogRead(2); //Lemos la salida analógica  del MQ
  voltaje = adc_MQ * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
  Rs=2000*((5-voltaje)/voltaje);  //Calculamos Rs con un RL de 1k
  but=0.4091*pow(Rs/5463, -1.497); // calculamos la concentración  de alcohol con la ecuación obtenida.

  delay(100);
  llamarGps();
}


double Thermistor(int RawADC) 
{
double Temp;
Temp = log(((10240000/RawADC) - 10000));
Temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * Temp * Temp ))* Temp );
Temp = Temp - 273.15;           
 return Temp;
}

void llamarGps(){
  bool newData = false;
  unsigned long chars;
  unsigned short sentences, failed;

  // For one second we parse GPS data and report some key values
  for (unsigned long start = millis(); millis() - start < 1000;)
  {
    while (Serial2.available())
    {
      char c = Serial2.read();
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
    if(calc_dist(newLat, newLon, lat, lon) < 10 && autopilot)
      parar();
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

void webserver(void) 
    {   
       String resp = "hum:" + String(hum) + ";temp:" + String(temp) + ";but:" + String(but) + ";met:" + String(alc)
                      + ";gps:" + String(lat) + ":" + String(lon);
       String httpResponse;
       String httpHeader;
       httpHeader = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n"; 
       httpHeader += "Content-Length: ";
       httpHeader += resp.length();
       httpHeader += "\r\n";
       httpHeader +="Connection: close\r\n\r\n";
       httpResponse = httpHeader + resp + " ";
       http(httpResponse);
       delay(1);
       Serial1.println("AT+CIPCLOSE=0");    
    }

void http(String output)
{
    Serial1.print("AT+CIPSEND=0,");              // AT+CIPSEND=0, num
    Serial1.println(output.length());
    if (Serial1.find('>'))                       // Si recibimos la peticion del mensaje
       {    Serial.println(output);
            Serial1.println(output);            //Aqui va el string hacia el WIFI 
            delay(10);
            while ( Serial1.available() > 0 ) 
              { if (  Serial1.find("SEND OK") )    // Busca el OK en la respuesta del wifi
                      break; 
              }
       }
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

void moveCamX(int i, int pos){
  if(pos >=0 && pos <= 180){
    acamX = pos;
    camX.write(acamX); 
  }
  else{
    Serial.println(i);
    if(i>0){
     if(acamX < 180){
       acamX += 10;
       camX.write(acamX);  
      }
    }
   else
       if(acamX > 0){
       acamX += -10;
       camX.write(acamX);  
      }  
    }
  }

void moveCamY(int i, int pos){
 if(pos >=0 && pos <= 180){
    acamY = pos;
    camY.write(acamY); 
  }
  else{
    Serial.println(i);
    if(i<0){
      if(acamY < 180){
        acamY += 10;
        camY.write(acamY);  
      }
    }
    else
      if(acamY > 0){
        acamY += -10;
        camY.write(acamY);  
      }  
    }
  }


void adelante(){
  digitalWrite (IN3, HIGH);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, HIGH);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(500);
  autopilot = false;
}

void atras(){
  digitalWrite (IN3, LOW);
  digitalWrite (IN4, HIGH);

  digitalWrite (IN1, LOW);
  digitalWrite (IN2, HIGH);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  autopilot = false;
}

void derecha(){
  digitalWrite (IN3, LOW);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, HIGH);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  autopilot = false;
}

void izquierda(){
  digitalWrite (IN3, HIGH);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, LOW);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  autopilot = false;
}

void parar(){
  analogWrite(ENA,0);
  analogWrite(ENB,0);
  autopilot = false;
}

void setVelocidad(String s){
  if(s.toInt() >= 140)
    velocidad = s.toInt();
}

void printLCD(){
  lcd.setCursor(0,0);             
    lcd.print("Temp:");
    lcd.print(temp);
    lcd.print("C");
    
    lcd.setCursor(12,0);             
    lcd.print("Hum:");
    lcd.print(hum,0);
    lcd.print("%");

    lcd.setCursor(0,1);            
    lcd.print("Alcohol: ");
    lcd.print(alc);
    lcd.setCursor(13,1);            
    lcd.print(" mg/L");

    lcd.setCursor(0,2);            
    lcd.print("Lat:");
    lcd.print(lat, 6);
    
    lcd.setCursor(0,3);            
    lcd.print("Lon:");
    lcd.print(lon, 6);
    
    delay (1000);//Dura 1 segundo 
  }

  void initLCD(){
    lcd.clear();
    lcd.setCursor(0,0);//Posiciona la primera letra despues del segmento 5 en linea 1             
    lcd.print("Robot:");
    lcd.setCursor(0,1);//Posiciona la primera letra despues del segmento 6 en linea 2            
    lcd.print("Inicializando");
    delay (1000);//Dura 1 segundo  
    lcd.setCursor(0,1);//Posiciona la primera letra despues del segmento 6 en linea 2            
    lcd.print("Inicializando.");
    delay (1000);//Dura 1 segundo  
    lcd.setCursor(0,1);//Posiciona la primera letra despues del segmento 6 en linea 2            
    lcd.print("Inicializando..");
    delay (1000);//Dura 1 segundo  
    lcd.setCursor(0,1);//Posiciona la primera letra despues del segmento 6 en linea 2            
    lcd.print("Inicializando...");
    delay(1000);
    lcd.clear();
    counter++;
  }

  void savePet(){
      String aux = getValue(peticion, ';', 0) ;
      ejecutar(aux);
    }

 void ejecutar(String p){
    if(p == "+camx")
       moveCamX(1, -1);
    if(p == "-camx")
       moveCamX(-1, -1);
    if(p == "+camy")
       moveCamY(1, -1);
    if(p == "-camy")
       moveCamY(-1, -1);
    if(getValue(p,'=',0) == "camx")
       moveCamX(0, getValue(peticion,'=',1).toInt());
    if(getValue(p,'=',0) == "camy")
       moveCamY(0, getValue(peticion,'=',1).toInt());
    if(p == "data"){}
    if (p == "w")
      adelante();
    if (p == "a")
      izquierda();
    if (p == "s")
      atras();
    if (p == "d")
      derecha();
    if (p == "x")
      parar();
    if (p.indexOf("v=") != -1)
      setVelocidad(getValue(peticion, '=', 1));
  
  }

// Function to calculate the distance between two waypoints
float calc_dist(float flat1, float flon1, float flat2, float flon2)
{
  float dist_calc=0;
  float dist_calc2=0;
  float diflat=0;
  float diflon=0;

  diflat=radians(flat2-flat1);
  flat1=radians(flat1);
  flat2=radians(flat2);
  diflon=radians((flon2)-(flon1));

  dist_calc = (sin(diflat/2.0)*sin(diflat/2.0));
  dist_calc2= cos(flat1);
  dist_calc2*=cos(flat2);
  dist_calc2*=sin(diflon/2.0);
  dist_calc2*=sin(diflon/2.0);
  dist_calc +=dist_calc2;

  dist_calc=(2*atan2(sqrt(dist_calc),sqrt(1.0-dist_calc)));

  dist_calc*=6371000.0; //Converting to meters
  return dist_calc;
}

float cal_angle(float X, float Z){
  float y = X / Z;
  float x = asin(y);
  float z = RADTODEG (x);
  return z;
  
}

void movePos(){
  float angle = 0; //SE NECESITA UN COMPAS Y LEER AQUI EL VALOR.
  float X = calc_dist(newLat, newLon, 0.0, 0.0);
  float Z = calc_dist(newLat, newLon, lat, lon);

  float newAngle = cal_angle(X, Z);
  while(angle != newAngle){
    derecha();
    delay(300);
    parar();
    angle= angle+1; //LEER DE NUEVO EL ANGULO DEL COMPAS
  }

  adelante();
  autopilot = true;
}


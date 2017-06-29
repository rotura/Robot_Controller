
#include <Servo.h>
#include <TinyGPS.h>
#include <Wire.h>

/*Ejemplo para controlar un LCD con un modulo Serial
Instrucciones:
VCC del modulo: 5v 
GND del modulo: GND
SCL del modulo (varia dependiendo de la placa Arduino) 
SDA del modulo (varia dependiendo de la placa Arduino) 
*/
#include <LiquidCrystal_I2C.h>//Recuerda descargar la libreria en electrocrea.com    
LiquidCrystal_I2C lcd(0x3F, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE);//Direccion de LCD

int IN3 = 10;    // Input3 conectada al pin 5
int IN4 = 9;    // Input4 conectada al pin 4 
int ENB = 3;    // ENB conectada al pin 3 de Arduino

int IN1 = 8;    // Input1 conectada al pin 5
int IN2 = 7;    // Input2 conectada al pin 4 
int ENA = 6;    // ENA conectada al pin 3 de Arduino

int counter = 0;
TinyGPS gps;
int pos = 0;
Servo camX, camY;
int acamX = 90;
int acamY = 90;
int velocidad = 255;

double temp, alc, hum = 0.0;
float lat, lon = 0.0;
String peticion = "";

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
  //alc = map(analogRead(1),0,1023,10,1000);

  int adc_MQ = analogRead(1); //Lemos la salida analógica  del MQ
  float voltaje = adc_MQ * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
  float Rs=2000*((5-voltaje)/voltaje);  //Calculamos Rs con un RL de 1k
  alc=0.4091*pow(Rs/5463, -1.497); // calculamos la concentración  de alcohol con la ecuación obtenida.

  /*Serial.print("adc:");
  Serial.print(adc_MQ);
  Serial.print("    voltaje:");
  Serial.print(voltaje);
  Serial.print("    Rs:");
  Serial.print(Rs);
  Serial.print("    alcohol:");
  Serial.print(alc);
  Serial.println("mg/L");*/
  //Serial.println("lat: " + String(lat));
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

  /*if (newData)
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
    Serial.println("** No characters received from GPS: check wiring **");*/
}

String GetLineWIFI()
   {   String S = "" ;
       if (Serial1.available())
          {    char c = Serial1.read();
                while ( c != '\n' )            //Hasta que el caracter sea intro
                  {     S = S + c ;
                        delay(25) ;
                        c = Serial1.read();
                   }
                 return( S ) ;
          }
   }

void webserver(void) 
    {   
       String resp = "hum:" + String(hum) + ";temp:" + String(temp);
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
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void atras(){
  digitalWrite (IN3, LOW);
  digitalWrite (IN4, HIGH);

  digitalWrite (IN1, LOW);
  digitalWrite (IN2, HIGH);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(500);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void derecha(){
  digitalWrite (IN3, LOW);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, HIGH);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(500);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
}

void izquierda(){
  digitalWrite (IN3, HIGH);
  digitalWrite (IN4, LOW);

  digitalWrite (IN1, LOW);
  digitalWrite (IN2, LOW);

  analogWrite(ENA,velocidad);
  analogWrite(ENB,velocidad - 40); //Ajuste para igualar motores
  delay(500);
  
  analogWrite(ENA,0);
  analogWrite(ENB,0);
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

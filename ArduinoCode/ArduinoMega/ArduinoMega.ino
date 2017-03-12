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
int counter = 0;
TinyGPS gps;

double temp, alc, hum = 0.0;
float lat, lon = 0.0;

void setup()   { 
  Serial.begin(115200);
  Serial2.begin(9600);
  lcd.begin(20,4);// Indicamos medidas de LCD     
  lcd.clear();//Elimina todos los simbolos del LCD
  lcd.setCursor(0,0);//Posiciona la primera letra despues del segmento 5 en linea 1             
  lcd.print("Robot:");
  delay (1000);//Dura 2 segundos   
}

void loop() {
  readSensor();  
  if(counter < 1){
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
  else{
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
}


void readSensor(){
  temp = Thermistor(analogRead(0));
  //alc = map(analogRead(1),0,1023,10,1000);

  int adc_MQ = analogRead(1); //Lemos la salida analógica  del MQ
  float voltaje = adc_MQ * (5.0 / 1023.0); //Convertimos la lectura en un valor de voltaje
  float Rs=2000*((5-voltaje)/voltaje);  //Calculamos Rs con un RL de 1k
  alc=0.4091*pow(Rs/5463, -1.497); // calculamos la concentración  de alcohol con la ecuación obtenida.

Serial.print("adc:");
  Serial.print(adc_MQ);
  Serial.print("    voltaje:");
  Serial.print(voltaje);
  Serial.print("    Rs:");
  Serial.print(Rs);
  Serial.print("    alcohol:");
  Serial.print(alc);
  Serial.println("mg/L");
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

#include <Servo.h>

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
Servo myservo;
double temp, alc = 0.0;

void setup()   { 
  Serial.begin(9600);
  myservo.attach(3);
  lcd.begin(20,4);// Indicamos medidas de LCD     
  lcd.clear();//Elimina todos los simbolos del LCD
  lcd.setCursor(0,0);//Posiciona la primera letra despues del segmento 5 en linea 1             
  lcd.print("Robot:");
  delay (2500);//Dura 2 segundos   
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
    counter++;
  }
  else{
    lcd.clear();
    lcd.setCursor(0,0);//Posiciona la primera letra despues del segmento 5 en linea 1             
    lcd.print("Temperatura: ");
    lcd.print(temp);
    lcd.print("C");
    lcd.setCursor(0,1);//Posiciona la primera letra despues del segmento 6 en linea 2            
    lcd.print("Alcohol: ");
    lcd.print(alc);
    delay (1000);//Dura 1 segundo   
  }
}


void readSensor(){
  Serial.println(analogRead(0));
  temp = Thermistor(analogRead(0));
  Serial.println(temp);
  alc = map(analogRead(1),0,1023,10,1000);
  
  }


double Thermistor(int RawADC) 
{
double Temp;
Temp = log(((10240000/RawADC) - 10000));
Temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * Temp * Temp ))* Temp );
Temp = Temp - 273.15;           
 return Temp;
}

/*
  Author: github.com/vasll
  Description: Air project Firmware for an Arduino. It fetches data from an MQ135 sensor, a DHT22 sensor, 
  and displays that data on an LCD 16x2 display. Data is both displayed and sent to the serial port 
  at the same time, this is achieved via protothreads.
*/
#include <pt.h>                 // Protothreading library: https://roboticsbackend.com/arduino-protothreads-tutorial/
#include <SimpleDHT.h>          // DHT sensor library
#include <MQ135.h>              // MQ135 sensor library
#include <LiquidCrystal.h>      // LCD screen library
#include "iconsLCD.h"           // LCD screen icons library

// Global variables
MQ135 gasSensor = MQ135(7);           // mq135 on analog pin 7
SimpleDHT22 dht22(2);                 // dht22 on digital pin 2
LiquidCrystal lcd(3, 4, 5, 6, 7, 8);  // LCD 16x2 display on pins 3, 4, 5, 6, 7, 8
static struct pt ptPrintData, ptDisplayData;
static float temperature, humidity, ppm, rzero;

// Protothread that prints sensor data to Serial.print()
static int printSensorData(struct pt *pt){
  static unsigned long lastTime = 0;
  PT_BEGIN(pt); // START OF PROTOTHREAD
  
  while(true){
    lastTime = millis();
    //READ DATA FROM DHT22 SENSOR
    dht22.read2(&temperature, &humidity, NULL);
    //READ DATA FROM MQ125
    ppm = gasSensor.getPPM();
    rzero = gasSensor.getRZero();
    
    //PRINT DATA -- FORMAT: "TMP,HUM,RZR,CO2"
    Serial.print("[ARDUINO],");
    Serial.print((String)temperature + "," + (String)humidity + "," + (String)rzero + "," + (String)ppm + "\n");
    PT_WAIT_UNTIL(pt, millis() - lastTime > 1000);
  }
  
  PT_END(pt); // END
}

// Protothread that displays sensor data to the LCD display
static int displaySensorData(struct pt *pt){
  static unsigned long lastTime = 0;
  static int switchCheck = 0; //incremented by 1 every cicle
  int switchFactor = 10;      //every 10 seconds the display changes what's displayed
  PT_BEGIN(pt); // START OF PROTOTHREAD
  
  while(true){
    lastTime = millis();
    
    if(switchCheck <= switchFactor){
      printTemperatureIcon(lcd);
      lcd.print(" Temperature ");
      lcd.setCursor(4, 1);
      lcd.print((String)temperature+"C      ");
    }else if(switchCheck > switchFactor && switchCheck <= switchFactor*2){
      printHumidityIcon(lcd);
      lcd.print(" Humidity    ");
      lcd.setCursor(4, 1);
      lcd.print((String)humidity+"%      ");
    }else if(switchCheck > switchFactor && switchCheck <= switchFactor*3){
      printWindIcon(lcd);
      lcd.print(" CO2 Level   ");
      lcd.setCursor(4, 1);
      lcd.print((String)ppm+"ppm   ");
    }else{
      switchCheck = 0;
    }
    switchCheck += 1;
    
    PT_WAIT_UNTIL(pt, millis() - lastTime > 1000);
  }

  PT_END(pt); // END
}

void scrollDisplayRightBy(int squares, int square_delay=45){
  for(int i=0; i<squares; i++){
    lcd.scrollDisplayRight();
    delay(square_delay);
  }
}

void setup() {
  lcd.begin(16, 2);       // LCD with 16 columns and 2 rows
  Serial.begin(9600);
  PT_INIT(&ptPrintData);    // Start protothread
  PT_INIT(&ptDisplayData);  // Start protothread
  
  // "Fancy" screen booting sequence
  lcd.setCursor(0, 0);
  lcd.print("  AIR Firmware  ");
  lcd.setCursor(0, 1);
  lcd.print("    Ver. 1.0    ");
  delay(2500);
  scrollDisplayRightBy(16);

  Serial.println("[ARDUINO],0,0,0,0");

  lcd.setCursor(0, 0);
  lcd.print("Warming sensors ");  // The mq135 sensor needs to warm up before giving accurate readings
  lcd.setCursor(0, 1);
  lcd.print("Time: 30s       ");
  scrollDisplayRightBy(24);

  for(int i=30; i>=0; i--){
    lcd.setCursor(0, 1);
    lcd.print("Time: "+(String)i+"s       "); // 30s warmup time
    delay(1000);
  }
  scrollDisplayRightBy(24);
  lcd.clear();
}


void loop() {
  printSensorData(&ptPrintData);
  displaySensorData(&ptDisplayData);
}

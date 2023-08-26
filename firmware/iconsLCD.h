#include <LiquidCrystal.h>
// Contains some icons to be displayed to the arduino 16x2 LCD
// Reference: https://create.arduino.cc/projecthub/jacoslabbert99/arduino-lcd-icons-custom-characters-548f38

// TEMPERATURE ICON
byte tempchar1[8]={B00000, B00001, B00010, B00100, B00100, B00100, B00100, B00111,};
byte tempchar2[8]={B00111, B00111, B00111, B01111, B11111, B11111, B01111, B00011,};
byte tempchar3[8]={B00000, B10000, B01011, B00100, B00111, B00100, B00111, B11100,};
byte tempchar4[8]={B11111, B11100, B11100, B11110, B11111, B11111, B11110, B11000,};
                  
// HUMIDITY ICON
byte humchar1[8]={B00000, B00001, B00011, B00011, B00111, B01111, B01111, B11111,};
byte humchar2[8]={B11111, B11111, B11111, B01111, B00011, B00000, B00000, B00000,};
byte humchar3[8]={B00000, B10000, B11000, B11000, B11100, B11110, B11110, B11111,};
byte humchar4[8]={B11111, B11111, B11111, B11110, B11100, B00000, B00000, B00000,};

// WIND THING ICON                      
byte wind1[] = {B00000,B00000,B00000,B00011,B00111,B00111,B00111,B00011};
byte wind2[] = {B11111,B11111,B01110,B00000,B00000,B00000,B00000,B00000};
byte wind3[] = {B00000,B00000,B00000,B00000,B00000,B01110,B11111,B11111};
byte wind4[] = {B11000,B11100,B11100,B11100,B11000,B00000,B00000,B00000,};
                  
void printTemperatureIcon(LiquidCrystal lcd){
	lcd.createChar(1,tempchar1);
	lcd.createChar(2,tempchar2);
	lcd.createChar(3,tempchar3);
	lcd.createChar(4,tempchar4);
	lcd.setCursor(0,0);
	lcd.write(1);
	lcd.setCursor(0,1);
	lcd.write(2);
	lcd.setCursor(1,0);
	lcd.write(3);
	lcd.setCursor(1,1);
	lcd.write(4);
	lcd.setCursor(3,0);
}

void printHumidityIcon(LiquidCrystal lcd){
	lcd.createChar(1,humchar1);
	lcd.createChar(2,humchar2);
	lcd.createChar(3,humchar3);
	lcd.createChar(4,humchar4);
	lcd.setCursor(0,0);
	lcd.write(1);
	lcd.setCursor(0,1);
	lcd.write(2);
	lcd.setCursor(1,0);
	lcd.write(3);
	lcd.setCursor(1,1);
	lcd.write(4);
	lcd.setCursor(3,0);
}

void printWindIcon(LiquidCrystal lcd){
	lcd.createChar(1 , wind1);
	lcd.createChar(2 , wind2);
	lcd.createChar(3 , wind3);
	lcd.createChar(4 , wind4);
	lcd.clear();
	lcd.setCursor(0,0);
	lcd.write(1);
	lcd.setCursor(0,1);
	lcd.write(2);
	lcd.setCursor(1,0);
	lcd.write(3);
	lcd.setCursor(1,1);
	lcd.write(4);
	lcd.setCursor(3,0);
}

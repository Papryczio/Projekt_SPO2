  #include "BluetoothSerial.h"
  #include <Wire.h>
  #include <SPI.h>
  #include "MAX30105.h"
  #include "spo2_algorithm.h"
  #include "heartRate.h"
  #include <Adafruit_GFX.h>
  #include <Adafruit_SSD1306.h>
  
  #if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
  #error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
  #endif

  #define SCREEN_WIDTH 128 // OLED display width, in pixels
  #define SCREEN_HEIGHT 64 // OLED display height, in pixels

  // Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
  #define OLED_RESET     -1 // Reset pin # (or -1 if sharing Arduino reset pin)
  Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);
  
  BluetoothSerial SerialBT;
  MAX30105 pikpik;
  uint32_t beepbeepIR[100];
  uint32_t beepbeepRed[100];

  int32_t bufferLength; //data length
  int32_t spo2; //SPO2 value
  int8_t validSPO2; //indicator to show if the SPO2 calculation is valid
  int32_t heartRate; //heart rate value
  int8_t validHeartRate;
  int i;
  int lastValidHR;
  int lastValidSPO2;


  //20x17
  const unsigned char heartBig [] PROGMEM = {
  0x1f, 0x0f, 0x80, 0x31, 0x98, 0xc0, 0x40, 0xf0, 0x20, 0xc0, 0x60, 0x30, 0x80, 0x00, 0x10, 0x80, 
  0x00, 0x10, 0xc0, 0x00, 0x30, 0x40, 0x00, 0x20, 0x60, 0x00, 0x60, 0x30, 0x00, 0xc0, 0x18, 0x01, 
  0x80, 0x0c, 0x03, 0x00, 0x06, 0x06, 0x00, 0x03, 0x0c, 0x00, 0x01, 0x98, 0x00, 0x00, 0xf0, 0x00, 
  0x00, 0x60, 0x00
  };

  //15x13
  const unsigned char heartSmall [] PROGMEM = {
  0x3c, 0x78, 0x42, 0x84, 0x81, 0x02, 0x80, 0x02, 0x80, 0x02, 0x80, 0x02, 0x40, 0x04, 0x20, 0x08, 
  0x10, 0x10, 0x08, 0x20, 0x04, 0x40, 0x02, 0x80, 0x01, 0x00
  };
  
  void setup() {
    Serial.begin(115200);
    SerialBT.begin("Pulsoksymetr"); //Bluetooth device name
    Serial.println("The device started, now you can pair it with bluetooth!");

    if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
      Serial.println(F("SSD1306 allocation failed"));
      for(;;);
    }

    display.clearDisplay();

    display.setTextSize(1.5);
    display.setTextColor(WHITE);
    display.cp437(true);

    display.setCursor(0,0);
    display.print("Pulsoksymetr");
    display.setCursor(0,32);
    display.print("Waiting for data");
    display.drawBitmap(100,40, heartSmall, 15, 13, WHITE);
    display.display();

 
  pikpik.begin(Wire, I2C_SPEED_FAST);
  byte LED_Current = 0x1F; // prąd diody LED (intensywność świecenia)
  byte sample_averaging = 8; // "To reduce the amount of data throughput, adjacent samples (in each individual channel) 
  //                            can be averaged and decimated on the chip by setting this register."
  byte modeControl = 2; // 
  pikpik.setup(LED_Current, sample_averaging, modeControl, 100, 411, 4096);
  // 0x1f - LED current
  // 8    - sample averaging -- To reduce the amount of data throughput, adjacent samples (in each individual channel) 
  //                            can be averaged and decimated on the chip by setting this register.
  // 3    - Mode Control
  i = 0;
  lastValidHR = 0;
  lastValidSPO2 = 0;
  }
  
  void loop() 
  {
    bufferLength = 100;
    for(int i = 0; i < 100; i++){
      while (pikpik.available() == false)
      pikpik.check();
      do{
      beepbeepIR[i] = pikpik.getIR();
      beepbeepRed[i] = pikpik.getRed();
      pikpik.nextSample();
      } while(beepbeepIR[i] < 10000);
    }
    
    while(1){
      //przesuwanie
      for(int i = 20; i < 100; i++){
        beepbeepIR[i - 20] = beepbeepIR[i];
        beepbeepRed[i - 20] = beepbeepRed[i];
      }
      //dopisywanie
      for(int i = 80; i < 100; i++){
        while (pikpik.available() == false)
        pikpik.check();
        do{
        beepbeepIR[i] = pikpik.getIR();
        beepbeepRed[i] = pikpik.getRed();
        pikpik.nextSample();
        } while (beepbeepIR[i] < 10000);
        
        Serial.print(F("red="));
        Serial.print(beepbeepRed[i], DEC);
        Serial.print(F(", ir="));
        Serial.print(beepbeepIR[i], DEC);
  
        Serial.print(F(", HR="));
        Serial.print(heartRate, DEC);
  
        Serial.print(F(", HRvalid="));
        Serial.print(validHeartRate, DEC);
  
        Serial.print(F(", SPO2="));
        Serial.print(spo2, DEC);
  
        Serial.print(F(", SPO2Valid="));
        Serial.println(validSPO2, DEC);
          //SPO2
          SerialBT.println(spo2, DEC);
          Serial.println(spo2, DEC);
          //SPO2 VALID
          Serial.println(validSPO2, DEC);
          SerialBT.println(validSPO2, DEC);
          //BPM
          SerialBT.println(heartRate, DEC);
          Serial.println(heartRate, DEC);
          //BPM VALID
          Serial.println(validHeartRate, DEC);
          SerialBT.println(validHeartRate, DEC); 
        delay(100);
        if (SerialBT.available()) Serial.write(SerialBT.read());
      }
      maxim_heart_rate_and_oxygen_saturation(beepbeepIR, bufferLength, beepbeepRed, &spo2, &validSPO2, &heartRate, &validHeartRate); 
        display.clearDisplay();
        
        display.setTextSize(2);
        display.setTextColor(WHITE);
        display.cp437(true);

        display.setCursor(3,0);
        display.print("BPM");
        display.setCursor(3,24);
        if(validHeartRate == 1){
          display.print(heartRate);
          lastValidHR = heartRate;
        } else {
          display.print(lastValidHR);
        }
        display.setCursor(72,0);
        display.print("SPO2");
        display.setCursor(72,24);
        if(validSPO2 == 1){
          display.print(spo2);
          lastValidSPO2 = spo2;
        } else {
          display.print(lastValidSPO2);
        }
        if(heartRate < 100){
          display.drawBitmap(32, 24, heartSmall, 15, 13, WHITE);
        } else {
          display.drawBitmap(42, 24, heartSmall, 15, 13, WHITE);
        }

        display.display();
      
    }
  }

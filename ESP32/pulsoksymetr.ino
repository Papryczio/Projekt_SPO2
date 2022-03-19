  #include "BluetoothSerial.h"
  #include <Wire.h>
  #include "MAX30105.h"
  #include "spo2_algorithm.h"
  #include "heartRate.h"
  
  #if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
  #error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
  #endif

  BluetoothSerial SerialBT;
  MAX30105 pikpik;
  uint32_t beepbeepIR[100];
  uint32_t beepbeepRed[100];

  int32_t bufferLength; //data length
  int32_t spo2; //SPO2 value
  int8_t validSPO2; //indicator to show if the SPO2 calculation is valid
  int32_t heartRate; //heart rate value
  int8_t validHeartRate;
  
  void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP_UV"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");

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
        //delay(100);
        if (SerialBT.available()) Serial.write(SerialBT.read());
      }
      maxim_heart_rate_and_oxygen_saturation(beepbeepIR, bufferLength, beepbeepRed, &spo2, &validSPO2, &heartRate, &validHeartRate); 
    }
  }

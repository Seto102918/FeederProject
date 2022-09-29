#include <Arduino.h> 

#if defined(ESP32) 

  #include <WiFi.h> 

#elif defined(ESP8266) 

  #include <ESP8266WiFi.h> 

#endif 

#include <Firebase_ESP_Client.h> 

 

//Provide the token generation process info. 

#include "addons/TokenHelper.h" 

//Provide the RTDB payload printing info and other helper functions. 

#include "addons/RTDBHelper.h" 

 

// Insert your network credentials 

#define WIFI_SSID "sweet home" 

#define WIFI_PASSWORD "adepat1314"

 

// Insert Firebase project API Key 

#define API_KEY "AIzaSyBUQRxBHsI0lupZtQ6Yu_K8hY6K_IMFV7A" 

 

// Insert RTDB URLefine the RTDB URL */ 

#define DATABASE_URL "https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app/"  

 

//Define Firebase Data object 

FirebaseData fbdo; 

 

FirebaseAuth auth; 

FirebaseConfig config; 

 

unsigned long sendDataPrevMillis = 0; 

int count = 0; 

bool signupOK = false; 


int freq = 2000;
int channel = 0;
int resolution = 8;
#define relay 12

int tes = 0;
 
void setup() {

 Serial.begin(115200); 

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD); 

  Serial.print("Connecting to Wi-Fi"); 

  while (WiFi.status() != WL_CONNECTED){ 

    Serial.print("."); 

    delay(300); 

  } 

  Serial.println(); 

  Serial.print("Connected with IP: "); 

  Serial.println(WiFi.localIP()); 

  Serial.println(); 

 

  /* Assign the api key (required) */ 

  config.api_key = API_KEY; 

 

  /* Assign the RTDB URL (required) */ 

  config.database_url = DATABASE_URL; 

 

  /* Sign up */ 

  if (Firebase.signUp(&config, &auth, "", "")){ 
    Serial.println("ok"); 
    signupOK = true; 
  } else{ 
    Serial.printf("%s\n", config.signer.signupError.message.c_str()); 
  } 

 

  /* Assign the callback function for the long running token generation task */ 

  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h 

   

  Firebase.begin(&config, &auth); 

  Firebase.reconnectWiFi(true); 

  
  pinMode(relay, OUTPUT);
  Serial.begin(115200);
  ledcSetup(channel, freq, resolution);
  ledcAttachPin(14, channel);
}
 
void loop() {
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)){ 
    sendDataPrevMillis = millis(); 

     if (Firebase.RTDB.getInt(&fbdo, "Buzzer")) {
        if (fbdo.dataType() == "int") {
          tes = fbdo.intData();
          Serial.println(tes);
        }
     }else {Serial.println(fbdo.errorReason());}


     if (tes == 1){digitalWrite(relay, HIGH);}
       if (relay, HIGH){ 
        Serial.println("HIGH");
       
        ledcWriteTone(channel, 2000);
 
  for (int dutyCycle = 0; dutyCycle <= 255; dutyCycle=dutyCycle+10){
 
    Serial.println(dutyCycle);
 
    ledcWrite(channel, dutyCycle);
    delay(1000);}
    }
  
     else{ 
      digitalWrite(relay, LOW);
      (Serial.println("LOW"));
        
     }

  }

 
  delay(5000);
}

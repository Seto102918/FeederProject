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


#define TRIG_PIN 23 // ESP32 pin GIOP23 connected to Ultrasonic Sensor's TRIG pin
#define ECHO_PIN 22 // ESP32 pin GIOP22 connected to Ultrasonic Sensor's ECHO pin

float duration_us, distance_cm;
int value;

void setup(){ 

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

  } 

  else{ 

    Serial.printf("%s\n", config.signer.signupError.message.c_str()); 

  } 

 

  /* Assign the callback function for the long running token generation task */ 

  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h 

   

  Firebase.begin(&config, &auth); 

  Firebase.reconnectWiFi(true); 

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

} 

 

void loop(){ 

  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  // measure duration of pulse from ECHO pin
  duration_us = pulseIn(ECHO_PIN, HIGH);

  // calculate the distance
  distance_cm = 0.017 * duration_us;

  // print the value to Serial Monitor
  Serial.print("distance: ");
  Serial.print(distance_cm);
  Serial.println(" cm");

  delay(1000);

  value = (((10.97 - distance_cm)/10.97)*100);

  Serial.print("Water Level: ");
  Serial.print(value);
  Serial.println(" %");

  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0)){ 

    sendDataPrevMillis = millis(); 

    // Write an Int number on the database path test/int 

    if (Firebase.RTDB.setInt(&fbdo, "waterlvl", value)){ 

      Serial.print("The Water Level Sensor Value: "); 
      Serial.println(value); 

    } 

    else { 

      Serial.println("FAILED"); 

      Serial.println("REASON: " + fbdo.errorReason()); 

    } 

  } 

} 

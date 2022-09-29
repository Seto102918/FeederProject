#include <Arduino.h>
#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif

#include <Servo_ESP32.h>
#include "time.h"
#include "HX711.h"

#include <Firebase_ESP_Client.h>

#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#define WIFI_SSID "sweet home"
#define WIFI_PASSWORD "adepat1314"

#define API_KEY "AIzaSyBUQRxBHsI0lupZtQ6Yu_K8hY6K_IMFV7A"
#define DATABASE_URL "https://feederproject-8a71b-default-rtdb.asia-southeast1.firebasedatabase.app/" 
#define USER_EMAIL "USER_EMAIL"
#define USER_PASSWORD "USER_PASSWORD"
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
int intValue, intJam1, intJam2, intJam3;
int intMenit1, intMenit2, intMenit3;
bool signupOK = false;

const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = 25200;
const int   daylightOffset_sec = 0;

static const int servoPin = 23;
Servo_ESP32 servo1;

HX711 scale;
uint8_t dataPin = 22;
uint8_t clockPin = 19;

float w1, w2, previous = 0;
float w_selesai, w_awal;
float bataswadah = 400;
float isi;

char timeHour[3];
char timeMinutes [3];
int timeH, timeM;

int jumlahPemutaran = 0;

void setup() {
  /*
   * Servo
   */

  servo1.attach(servoPin);

  
  /*
   * WIFI
   */
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());

  /*
   * FIREBASE
   */
   
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  }
  else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  /*
   * NTP SERVER
   */
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

   /*
    * SCALE
    */
  scale.begin(dataPin, clockPin);
  
  Serial.print("UNITS: ");
  Serial.println(scale.get_units(10));
  
  scale.set_scale(855);       
  scale.tare();
  
  Serial.print("UNITS: ");
  Serial.println(scale.get_units(1));
}

void loop() {
  /*
   * Waktu NTP
   */
  timeH = getHour();
  timeM = getMin();


  /*
   * SCALE
   */

  w1 = scale.get_units(10);
  delay(100);
  w2 = scale.get_units();
  while (abs(w1 - w2) > 10)
  {
     w1 = w2;
     w2 = scale.get_units();
     delay(100);
  }

  Serial.println("UNITS: ");
  Serial.println(w1);
  Serial.println(w2);
  /*
   * Firebase
   */
  
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    
    intJam1 = getFirebaseInt("/Jam/1");
    intMenit1 = getFirebaseInt("/Menit/1");
    state1 = getFirebaseInt("/State/2")
    printJadwal("Jadwal1 => ", intJam1, intMenit1, state1);
    
    intJam2 = getFirebaseInt("/Jam/2");
    intMenit2 = getFirebaseInt("/Menit/2");
    state2 = getFirebaseInt("/State/2");
    printJadwal("Jadwal2 => ", intJam2, intMenit2, state2);
    
    intJam3 = getFirebaseInt("/Jam/3");
    intMenit3 = getFirebaseInt("/Menit/3");
    state3 = getFirebaseInt("/State/3");
    printJadwal("Jadwal3 => ", intJam3, intMenit3, state3);

    isi = getFirebaseInt("Refill");
    Serial.print("Refill isi:");
    Serial.println(isi);

    if (Firebase.RTDB.setInt(&fbdo, "Berat", w1)){}
    else {
      Serial.println("Firebase Failed: " + fbdo.errorReason());
    }
  

  /*
   * Servo
   */
   
    if (w1 >= bataswadah){                                                                                                                     
         Serial.println("WADAH PENUH"); 
    }else{
      if ((timeH == intJam1 && timeM == intMenit1 && state1 == 1)|| (timeH == intJam2 && timeM == intMenit2 && state2 == 1) || (timeH == intJam3 && timeM == intMenit3 && state3 == 1)){
        Serial.print("Run, w_selesai =>");
        
        w_awal = w1;
        w_selesai = w1 + isi;
        Serial.println(w_selesai);
        
        while (w_awal < w_selesai){
          Serial.println("JALAN NIH COOOK");
          
          servo1.write(180);
          delay(5000);
          servo1.write(0);
          delay(5000);
          
          w_awal = scale.get_units();                     
          Serial.print("UNITS Sekarang: ");                      
          Serial.println(w_awal);
          
          jumlahPemutaran++;
  
          Serial.print(w_awal);
          Serial.print(":");
          Serial.println(w_selesai);
          
          if(jumlahPemutaran>20){                                                                                            
              Serial.println("Makanan Mungkin Habis atau Makanan ada yang terhambat"); 
              jumlahPemutaran = 0;
              
              if (Firebase.RTDB.setInt(&fbdo, "RefillAlert", 1)){
              } else {
                Serial.println("Firebase Failed: " + fbdo.errorReason());
              }
              
              return;
              
          }else{
              if (Firebase.RTDB.setInt(&fbdo, "RefillAlert", 0)){
              } else {
                Serial.println("Firebase Failed: " + fbdo.errorReason());
              }
          }
        }
        Serial.println("DAH");
         
      }else { Serial.println("No Run"); }
    }
  }
  delay(900000);
}


/*
 * 
 *
 *FUNCTIONS 
 * 
 * 
 */

 
int getFirebaseInt(String path){
  if (Firebase.RTDB.getInt(&fbdo, path)) {
      if (fbdo.dataType() == "int") {
        int Rvalue = round(fbdo.intData());
        return Rvalue;
      }else {Serial.println("Firebase Data Type Mismatch");}
  }else {Serial.println("Firebase Failed: " + fbdo.errorReason());}
}

int getHour(){
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)){
    Serial.println("Failed to obtain time");
    return 0;
  }
  Serial.print("Hour: ");
  Serial.println(&timeinfo, "%H");
  strftime(timeHour,3, "%H", &timeinfo);
  return atoi(timeHour);
}

int getMin(){
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)){
    Serial.println("Failed to obtain time");
    return 0;
  }
  Serial.print("Minute: ");
  Serial.println(&timeinfo, "%M");
  strftime(timeMinutes,3, "%M", &timeinfo);
  return atoi(timeMinutes);
}

void printJadwal(String Jadwalmana, int Jamnya, int Menitnya, int state){
    Serial.print(Jadwalmana);
    Serial.print(Jamnya);
    Serial.print(":");
    Serial.println(Menitnya);
    Serial.print("State: ");
    Serial.println(state);
}

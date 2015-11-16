//GSM
#include <GSM.h>

#define GSM_PIN        "3512"
#define GPRS_APN       "pinternet.interkom.de"
#define GPRS_LOGIN     ""
#define GPRS_PASSWORD  ""

//Sensors
#define TEMP_PIN -1
#define HUMIDITY_PIN -1
#define AIR-QUALITY_PIN -1
#define AIR-PRESSURE_PIN -1
#define RAIN_PIN -1
#define WIND-DIR_PIN -1
#define WIND-POWER_PIN -1

GSMClient client;
GPRS gprs;
GSM gsmAccess; 

char server[] = "wetterstation-duerer.rhcloud.com";
int port = 80;

int getTemp() {
  return 0;
}

int getHumidity() {
  return 0;
}

int getAirQuality() {
  return 0;
}

int getAirPressure() {
  return 0;
}

int getRain() {
  return 0;
}

String getWindDir() {
  return "-1";
}

int getWindPower() {
  return 0;
} 

String getData() {
  String result = "";
  result += "temp="     + String(getTemp())         + "&";
  result += "humidity=" + String(getHumidity())     + "&";
  result += "airQ="     + String(getAirQuality())   + "&";
  result += "airP="     + String(getAirPressure())  + "&";
  result += "rain="     + String(getRain())         + "&";
  result += "windDir="  + getWindDir()              + "&";
  result += "wind="     + String(getWindPower())         ;
  return result;
}

void setup()
{
  Serial.begin(9600);
  Serial.println("Starting Arduino web client.");
  boolean connected = false;

  // After starting the modem with GSM.begin()
  // attach the shield to the GPRS network with the APN, login and password
  while(!connected) {
    if(gsmAccess.begin(GSM_PIN) == GSM_READY && gprs.attachGPRS(GPRS_APN, GPRS_LOGIN, GPRS_PASSWORD) == GPRS_READY)
      connected = true;
    else {
      Serial.println("Not connected");
      delay(1000);
    }
  }

  Serial.println("connecting...");

  String postData = getData();
  
  if (client.connect(server, port)) {
    Serial.println("connected");
    // Make a HTTP-Post request:
    client.println("POST /postWeather HTTP/1.1");
    client.println("Host: wetterstation-duerer.rhcloud.com");
    client.println("Connection: close");
    client.print("Content-Length: ");
    client.println(postData.length());
    client.println();
    client.println(postData);
  } else {
    Serial.println("connection failed");
  }
}

void loop()
{
  // if there are incoming bytes available 
  // from the server, read them and print them:
  if (client.available())
  {
    char c = client.read();
    Serial.print(c);
  }

  // if the server's disconnected, stop the client:
  if (!client.available() && !client.connected())
  {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();

    // do nothing forevermore:
    for(;;)
      ;
  }
}

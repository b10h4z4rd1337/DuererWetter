#include <GSM.h>

#define GSM_PIN ""

#define GPRS_APN       "GPRS_APN"
#define GPRS_LOGIN     "login"
#define GPRS_PASSWORD  "password"

GSMClient client;
GPRS gprs;
GSM gsmAccess; 

char server[] = "wetterstation-duerer.rhcloud.com";
int port = 80;

String getData() {
  //TO-DO: Implement Sensors!
  return "Hello World";
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
    client.println("POST /uploadData HTTP/1.1");
    client.println("Host: wetterstation-duerer.rhcloud.com");
    client.println("User-Agent: Arduino/1.0");
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

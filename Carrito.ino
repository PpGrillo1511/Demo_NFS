#include <SoftwareSerial.h>

SoftwareSerial bluetooth(0, 1); // RX, TX
const int LED_FORWARD=3;
const int LED_BACKWARD=4;
const int LED_LEFT=5;
const int LED_RIGHT=6;

void setup() {
  Serial.begin(9600);      // Inicializa la comunicación serie con el monitor serial
  bluetooth.begin(9600);   // Inicializa la comunicación serie con el módulo Bluetooth
  pinMode(LED_FORWARD,OUTPUT);
  pinMode(LED_BACKWARD,OUTPUT);
  pinMode(LED_LEFT,OUTPUT);
  pinMode(LED_RIGHT,OUTPUT);
  Serial.println("Esperando conexión Bluetooth...");
}

void loop() {
  // Espera hasta que haya datos disponibles para leer desde el módulo Bluetooth
  if (bluetooth.available()) {
    char data = bluetooth.read(); // Lee el dato recibido

    Serial.print("Dato recibido: ");
    Serial.println(data); // Muestra el dato recibido en el monitor serial
    switch(data){
       case '1':
           digitalWrite(LED_FORWARD,HIGH);
           digitalWrite(LED_BACKWARD,LOW);
           digitalWrite(LED_LEFT,LOW);
           digitalWrite(LED_RIGHT,LOW);
       break;
       case '2':
           digitalWrite(LED_FORWARD,LOW);
           digitalWrite(LED_BACKWARD,HIGH);
           digitalWrite(LED_LEFT,LOW);
           digitalWrite(LED_RIGHT,LOW);
       break;
       case '3':
           digitalWrite(LED_FORWARD,LOW);
           digitalWrite(LED_BACKWARD,LOW);
           digitalWrite(LED_LEFT,LOW);
           digitalWrite(LED_RIGHT,HIGH);
       break;
       case '4':
           digitalWrite(LED_FORWARD,LOW);
           digitalWrite(LED_BACKWARD,LOW);
           digitalWrite(LED_LEFT,HIGH);
           digitalWrite(LED_RIGHT,LOW);
       break;
       case '0':
           digitalWrite(LED_FORWARD,LOW);
           digitalWrite(LED_BACKWARD,LOW);
           digitalWrite(LED_LEFT,LOW);
           digitalWrite(LED_RIGHT,LOW);
       break;
    }
    delay(100);
  }
}

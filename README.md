# TemiVR AndroidStudio
## About this project
This project was created with students of the HCI Lab internship program to study programming to remotely control robots and is in development for further Virtual Reality technologies. Within this project is the code of the Unity program used to transmit data received from KATVR to MQTT to control the Temi robot.

## Requirement
1. Android Studio (Chipmunk 2021.2.1 or newer)
2. Temi SDK
3. Eclipse Paho MQTT Android SDK
4. Android minimum API: 23

## Basic
### Temi Control

About how to control temi. This project need 2 temi command: Walk and Turn.
 
To control temi movement on its axis, use command
```Java
Robot.getInstance().skidJoy(float xAxis, float yAxis);
```
To control temi turn by a certain degree, use command
```Java
Robot.getInstance().turnBy(int degrees, float speed);
```
### MQTT
For Paho MQTT Tutorial, Click [here](https://people.utm.my/shaharil/mqtt-android-studio/)

## Reference
[Temi Robot SDK](https://github.com/robotemi/sdk)
 
[Eclipse Paho](https://github.com/eclipse/paho.mqtt.android)

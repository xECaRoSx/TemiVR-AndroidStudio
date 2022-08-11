package net.hcilab.temivr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.robotemi.sdk.Robot;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
    int First_angle = 0;
    int Yaw = 0;
    int Now_angle = 0;
    Boolean Calibrated = true;
    public static int counter = 0;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//=========================================== VARIABLES ===========================================
        //TEMI MOVE BUTTON
        TextView moveStatus = findViewById(R.id.txtMoveStatus);
        TextView CalDebug = findViewById(R.id.Callibad);
        ImageButton arrowUp = findViewById(R.id.btnUp);
        ImageButton arrowDown = findViewById(R.id.btnDown);
        ImageButton arrowLeft = findViewById(R.id.btnLeft);
        ImageButton arrowRight = findViewById(R.id.btnRight);

        //MQTT
        TextView messageMQTT1 = findViewById(R.id.msgMQTT_1);
        TextView messageMQTT2 = findViewById(R.id.msgMQTT_2);
        String walkStatus = "2";


        int walkstate = 0;
//=================================================================================================
        //FORWARD BUTTON
        arrowUp.setOnTouchListener((view, motionEvent) ->
        {
            Robot.getInstance().skidJoy(1,0);
            moveStatus.setText("Forward Button");
            return false;
        });

        //BACKWARD BUTTON
        arrowDown.setOnTouchListener((view, motionEvent) ->
        {
            Robot.getInstance().skidJoy(-1,0);
            moveStatus.setText("Backward Button");
            return false;
        });

        //TURN LEFT BUTTON
        arrowLeft.setOnTouchListener((view, motionEvent) ->
        {
            //Robot.getInstance().turnBy(10,1);
            Robot.getInstance().skidJoy(0,-1);
            moveStatus.setText("Left Button");
            return false;

        });

        //TURN RIGHT BUTTON
        arrowRight.setOnTouchListener((view, motionEvent) ->
        {
            //Robot.getInstance().turnBy(-10,1);
            Robot.getInstance().skidJoy(0,1);
            moveStatus.setText("Right Button");
            return false;
        });

        //MQTT
        String clientId = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.getApplicationContext(),"tcp://broker.mqttdashboard.com:1883",clientId);

        try {

            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });

        } catch (MqttException e)
        {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                if (topic.equals("Moving")) {
                    messageMQTT1.setText(new String(message.getPayload()));
                    int walkstate = Integer.valueOf(messageMQTT1.getText().toString());
                    String walkStatus = Integer.toString(walkstate);
                    //messageMQTT2.setText(walkStatus);
                    WalkCheck(walkStatus,moveStatus);
                }
                else if (topic.equals("BodyYaw")) {
                    messageMQTT2.setText(new String(message.getPayload()));
                    if(Calibrated)
                    {
                        First_angle = Integer.valueOf(messageMQTT2.getText().toString());
                        Calibrated = false;

                    }
                    Now_angle = Integer.valueOf(messageMQTT2.getText().toString());
                    if(counter <= 20)
                    {
                        counter+=1;
                    }
                    else
                    {


                        if(messageMQTT1.getText().toString().equals("0")) {

                            Yaw = (First_angle - Now_angle);

                            CalDebug.setText(First_angle + " - " + Now_angle + " = " + Yaw);
                            if (Yaw != 0) {
                                if(Math.abs(Yaw)>240)
                                {
                                    if(Yaw<0)
                                    {
                                        Yaw = 360+Yaw;
                                    }

                                    else{
                                        Yaw = (360-Yaw)*(-1);
                                    }

                                }

                                TurnAround(Yaw);
                            }


                        //CalDebug.setText(Yaw + Yaw / 4);
                            counter = 0;
                        }
                    }

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });




    }

    public void published(View v){

        String topic = "event";
        String message = "the payload";
        try {
            client.publish(topic, message.getBytes(),0,false);
            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }
    private void setSubscription(){

        try{
            client.subscribe("Moving",0);
            client.subscribe("BodyYaw",0);
        }
        catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconn(View v){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Disconnected!!",Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void WalkCheck(String walkStatus, TextView moveStatus)
    {
        if (walkStatus == "1") {
            Robot.getInstance().skidJoy(0.7F,0);
            moveStatus.setText("Walking True");
        }
        else if (walkStatus == "0"){
            moveStatus.setText("Walking False");
        }
        else {
            moveStatus.setText("No Input");
        }
    }

    public void TurnAround(int i)
    {
        if(i < 0) {
            //left
            Robot.getInstance().turnBy(i);
            First_angle = Now_angle;
        }

        else {
            //right
            Robot.getInstance().turnBy(i);
            First_angle = Now_angle;
        }
    }


}
package lebreton.fred.testeurfeuxsignalisation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.util.Set;




public class MainActivity extends AppCompatActivity {

    private Button clignoG;
    private Button clignoD;
    private Button positionD;
    private Button positionG;
    private Button feuxStop;
    private TextView textClignoG;
    private TextView textClignoD;
    private TextView textPositionG;
    private TextView textPositionD;
    private TextView textFeuxStop;
    private TextView connection;
    private BluetoothDevice boitier;


    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg){

            byte[]writeBuf=(byte[])msg.obj;
            int begin=(int)msg.arg1;
            int end=(int)msg.arg2;
            switch(msg.what){
                case 1:
                    String writeMessage=new String(writeBuf);
                    writeMessage=writeMessage.substring(begin,end);
                    break;
            }
        }
    };

    Handler sHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            byte[] writeBuf = (byte[]) msg.obj;
            if (msg.arg1 == 1) {

                connection.setText("");
                connection.setText("Connecté");
            }
            if (msg.arg1 == 2){

                connection.setText("");
                connection.setText("Déconnecté");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        clignoG = (Button)findViewById(R.id.clignoG);
        clignoD = (Button)findViewById(R.id.clignoD);
        positionD = (Button)findViewById(R.id.positionD);
        positionG = (Button)findViewById(R.id.positionG);
        feuxStop= (Button)findViewById(R.id.feuxStop);

        textClignoG = (TextView) findViewById(R.id.textClignoG);
        textClignoD = (TextView) findViewById(R.id.textClignoD);
        textPositionD = (TextView) findViewById(R.id.textPositionD);
        textPositionG = (TextView) findViewById(R.id.textPositionG);
        textFeuxStop = (TextView) findViewById(R.id.textFeuxStop);
        connection = (TextView) findViewById(R.id.connection);

        // Activation du bluetooth sur telephone
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
// Recherche de la pedale dans liste des devices déjà apparaillés
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice devicePaired : pairedDevices) {
                if (devicePaired.getName().contains("HC-06")) {
                    boitier = devicePaired;
                } else {
                    Log.d("pas de boitier decteté", "");
                }
            }

        }
       ConnectThread connectThread = new ConnectThread(bluetoothAdapter,sHandler,boitier);
       connectThread.start();

      /*  while (connection.getText().toString()!= "Connecté"){

            try {
                Thread.sleep(2000,0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this,"Connection en cours ...",Toast.LENGTH_SHORT).show();
        }
*/
      final ConnectedThread connectedThread = new ConnectedThread(connectThread.getSocket(),mHandler);
      connectedThread.start();




       clignoG.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

                if(connection.getText() == "Connecté"){
                   byte[] commande = {'a'};
                   connectedThread.write(commande);
                }
           }
       });
        clignoD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connection.getText() == "Connecté"){
                    byte[] commande = {'e'};
                    connectedThread.write(commande);
                }
            }
        });
        positionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connection.getText() == "Connecté"){
                    byte[] commande = {'r'};
                    connectedThread.write(commande);
                }
            }
        });
        feuxStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connection.getText() == "Connecté"){
                    byte[] commande = {'t'};
                    connectedThread.write(commande);
                }
            }
        });
        positionG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connection.getText() == "Connecté"){
                    byte[] commande = {'y'};
                    connectedThread.write(commande);
                }
            }
        });
    }

}
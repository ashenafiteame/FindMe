package com.example.ashenafi.findme;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

import static com.example.ashenafi.findme.MainActivity.ACTION_DATA_AVAILABLE;
import static com.example.ashenafi.findme.MainActivity.ACTION_GATT_CONNECTED;
import static com.example.ashenafi.findme.MainActivity.ACTION_GATT_SERVICES_DISCOVERED;
import static com.example.ashenafi.findme.MainActivity.EXTRA_DATA;

public class FirstActivity extends AppCompatActivity {
    BluetoothGatt bluetoothGatt;
    TextView statuss,realvaluetv,sendData,RSSItv,distancetv;
    BluetoothDevice bluetoothDevice;
    Boolean StopReading = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statuss = findViewById(R.id.textView13);
        realvaluetv = findViewById(R.id.textView23);
        sendData = findViewById(R.id.textView33);
        RSSItv = findViewById(R.id.textView43);
        distancetv = findViewById(R.id.textView53);



        Button startMonitoring = findViewById(R.id.ringon);
        startMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               StopReading=false;
                BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"));
                readFromServer(bluetoothGatt, characteristic);//
            }
        });

        Button stopMonitoring=findViewById(R.id.ringoff);
        stopMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopReading =true;
            }
        });
        Button ConnectToDevice = findViewById(R.id.connect);
        ConnectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BluetoothProfile.STATE_CONNECTED!=2){// 2 means connecteed
                    Context context=view.getContext();
                    bluetoothGatt = bluetoothDevice.connectGatt(context, false, btleGattCallback);
            }}
        });
        Button disconnect=findViewById(R.id.disconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   bluetoothGatt.disconnect();
                  }
        });

        // 1getting the device from main activity
         bluetoothDevice = getIntent().getExtras().getParcelable("deviceSelected");

        // 2connecting to the device and calling the btleGattCallback callback as a parameter
        bluetoothGatt = bluetoothDevice.connectGatt(this, false, btleGattCallback);


    }
    //3 btleGattCallback
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
        @Override
        // this will get called when a device connects or disconnects
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_DISCONNECTED:
                    FirstActivity.this.runOnUiThread(new Runnable() {
                        public void run() {

                           statuss.setText("\"device disconnected");
                            statuss.setTextColor(Color.parseColor("#990000"));
                        }
                    });
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    FirstActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            statuss.append("device connected\n");
                            statuss.setTextColor(Color.parseColor("#009900"));
                            String intentAction = ACTION_GATT_CONNECTED;
                            broadcastUpdate(intentAction);
                        }

                    });
                    gatt.discoverServices(); // discover services and characteristics for this device
                    break;
                default:
                    FirstActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            statuss.append("encountered unknown state!\n");
                        }
                    });
                    break;
            }
        }

        @Override
        // this will get called after the client initiates a BluetoothGatt.discoverServices() call
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            FirstActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"));
                        readFromServer(gatt, characteristic);//
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        statuss.append("\nservice not discovered " + status);
                    }
                }
            });


        }


        @Override
        public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
            FirstActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (status == BluetoothGatt.GATT_SUCCESS && rssi != 0) {
                        int prssi=-59;
                        double txPower = -59; //hard coded power value. Usually ranges between -59 to -65
                        double ratio = rssi * 1.0 / txPower;
                        double distance = 0;
                       if (ratio < 1.0) {
                           distance = Math.pow(ratio, 10);
                       } else {
                            distance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;

                    }
                      //  double distance =Math.pow(10,(txPower-rssi)/20);

                        try {
                            BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"));
                            if ( distance < 10.0) {
                                //String A = "A   ";
                                sendData.setText(" A ( with in normal range)");
                                //writeToServer(gatt, characteristic,A);
                            }
                            else if (distance > 10.0 && distance < 20.0) {
                                String B = "B";
                                sendData.setText(" B ( send warning to the server)");
                                writeToServer(gatt, characteristic, B);
                            } else {
                               // String A = "C";
                                sendData.setText(" C ( SMS to family members and location also)");
                                sendSMS("+393511952421","you got lost");
                               // writeToServer(gatt, characteristic, A);
                            }
                        }
                        catch (Exception e){
                            sendData.setText("\nexception"+e);
                        }
                            RSSItv.setText( rssi + "dB");
                            distancetv.setText(distance + " Meters");


                    }
                }
            });
        }


        @Override
        // this will get called anytime you perform a read or write characteristic operation
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            FirstActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                        bluetoothGatt.readRemoteRssi();//to monitor rssi value
                        byte[] data = characteristic.getValue();
                        realvaluetv.setText(new String(data) );
                        String x=new String(data);

                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                     if (ringtone.isPlaying()){
                                         ringtone.stop();
                                     }
                                   else if (x.equals("1.00") && !ringtone.isPlaying() && !StopReading) {
                                                 ringtone.play();
                                    }


                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                }
            });
        }


    };

  //calles oncharacterstic read
    private void readFromServer(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
           for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                gatt.setCharacteristicNotification(characteristic, true);
        }
        gatt.readCharacteristic(characteristic);


    }
//calles oncharacterstic write
    private boolean writeToServer(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, String data) {
        //peripheralTextView.append("\n write to ESP32= " + data);
        characteristic.setValue(data); // call this BEFORE(!) you 'write' any stuff to the server
        return gatt.writeCharacteristic(characteristic);

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            statuss.setText("message sent");

        } catch (Exception ex) {
            statuss.setText(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
        }

        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
                case R.id.connect:
                    bluetoothGatt.disconnect();
                    bluetoothGatt = bluetoothDevice.connectGatt(this, false, btleGattCallback);
                break;
                case R.id.disconnect:
                    bluetoothGatt.disconnect();
                break;
                case R.id.ring_on:
                    StopReading=false;
                    BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"));
                    readFromServer(bluetoothGatt, characteristic);//
                break;
            case R.id.ring_off:
                StopReading =true;
                break;
        }


        return super.onOptionsItemSelected(item);
    }

}
////package com.example.ashenafi.findme;
////
////import android.Manifest;
////import android.app.AlertDialog;
////import android.bluetooth.BluetoothAdapter;
////import android.bluetooth.BluetoothDevice;
////import android.bluetooth.BluetoothGatt;
////import android.bluetooth.BluetoothGattCallback;
////import android.bluetooth.BluetoothGattCharacteristic;
////import android.bluetooth.BluetoothGattDescriptor;
////import android.bluetooth.BluetoothGattService;
////import android.bluetooth.BluetoothManager;
////import android.bluetooth.BluetoothProfile;
////import android.bluetooth.le.BluetoothLeScanner;
////import android.bluetooth.le.ScanCallback;
////import android.bluetooth.le.ScanResult;
////import android.content.BroadcastReceiver;
////import android.content.Context;
////import android.content.DialogInterface;
////import android.content.Intent;
////import android.content.pm.PackageManager;
////import android.os.AsyncTask;
////import android.os.Build;
////import android.os.Bundle;
////import android.os.Handler;
////import android.support.annotation.RequiresApi;
////import android.support.design.widget.FloatingActionButton;
////import android.support.design.widget.NavigationView;
////import android.support.design.widget.Snackbar;
////import android.support.v4.view.GravityCompat;
////import android.support.v4.widget.DrawerLayout;
////import android.support.v7.app.ActionBarDrawerToggle;
////import android.support.v7.app.AppCompatActivity;
////import android.support.v7.widget.Toolbar;
////import android.view.Menu;
////import android.view.MenuItem;
////import android.view.View;
////import android.widget.AdapterView;
////import android.widget.ArrayAdapter;
////import android.widget.Button;
////import android.widget.EditText;
////import android.widget.ListView;
////import android.widget.TextView;
////
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.UUID;
////
////
////
////
////
////
////public class MainActivity extends AppCompatActivity
////        implements NavigationView.OnNavigationItemSelectedListener {
////    Menu tmenu;
////    ListView listDevicesFound;
////    ArrayAdapter<String> btArrayAdapter;
////    TextView peripheralTextView;
////    EditText deviceIndexInput;
////    EditText DataToSend;
////    Button connectToDevice;
////    Button disconnectDevice;
////    BluetoothManager btManager;
////    BluetoothAdapter btAdapter;
////    BluetoothLeScanner btScanner;
////    Boolean btScanning = false;
////    int deviceIndex = 0;
////    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<>();
////    BluetoothGatt bluetoothGatt;
////
////
////
////    public final static String ACTION_GATT_CONNECTED =
////            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
////    public final static String ACTION_GATT_DISCONNECTED =
////            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
////    public final static String ACTION_GATT_SERVICES_DISCOVERED =
////            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
////    public final static String ACTION_DATA_AVAILABLE =
////            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
////    public final static String EXTRA_DATA =
////            "com.example.bluetooth.le.EXTRA_DATA";
////    private final static int REQUEST_ENABLE_BT = 1;
////    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
////    private static final long SCAN_PERIOD = 5000;
////
////
////
////
////
////    private final BroadcastReceiver receiver = new BroadcastReceiver() {
////
////        @Override
////        public void onReceive(Context context, Intent intent) {
////
////            String action = intent.getAction();
////            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
////                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
////                //peripheralTextView.append("  RSSI: " + rssi + "dBm");
////            }
////        }
////    };
////
//////    Button startScanningButton;
//////    Button stopScanningButton;
////
////    // 4Device connect call back
////    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
////        @Override
////        // this will get called when a device connects or disconnects
////        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
////            switch (newState) {
////                case BluetoothProfile.STATE_DISCONNECTED:
////                    MainActivity.this.runOnUiThread(new Runnable() {
////                        public void run() {
////                            peripheralTextView.append("device disconnected\n");
////                            connectToDevice.setVisibility(View.VISIBLE);
////                            disconnectDevice.setVisibility(View.INVISIBLE);
////                        }
////                    });
////                    break;
////                case BluetoothProfile.STATE_CONNECTED:
////                    MainActivity.this.runOnUiThread(new Runnable() {
////                        public void run() {
////                            peripheralTextView.append("device connected\n");
////                            connectToDevice.setVisibility(View.INVISIBLE);
////                            disconnectDevice.setVisibility(View.VISIBLE);
////
////                            //
////
////
////                            String intentAction;
////
////                            intentAction = ACTION_GATT_CONNECTED;
////
////                            boolean rssiStatus = gatt.readRemoteRssi();
////                            broadcastUpdate(intentAction);
////                            // Attempts to discover services after successful connection.
////
////                        }
////                    });
////                    // discover services and characteristics for this device
////                    gatt.discoverServices();
////                    break;
////                default:
////                    MainActivity.this.runOnUiThread(new Runnable() {
////                        public void run() {
////                            peripheralTextView.append("encountered unknown state!\n");
////                        }
////                    });
////                    break;
////            }
////        }
////
////        @Override
////        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
////            super.onDescriptorWrite(gatt, descriptor, status);
////            peripheralTextView.append("onDescriptor:Writedescriptor: " + descriptor.getUuid() + ". characteristic: " + descriptor.getCharacteristic().getUuid() + ". status: " + status);
////        }
////
////
////        @Override
////        // this will get called after the client initiates a BluetoothGatt.discoverServices() call
////        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
////            MainActivity.this.runOnUiThread(new Runnable() {
////                public void run() {
////                    // peripheralTextView.append("\n1device services have been discovered\n");
////                    if (status == BluetoothGatt.GATT_SUCCESS) {
////                        for (BluetoothGattService gattService : gatt.getServices()) {
////                            // peripheralTextView.append("\nonServicesDiscovered: service=" + gattService.getUuid());
////                            for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()) {
////                                // peripheralTextView.append("\nonServicesDiscovered: characteristic=" + characteristic.getUuid());
////                                if (characteristic.getUuid().toString().equals("6e400002-b5a3-f393-e0a9-e50e24dcca9e")) {
////                                    // peripheralTextView.append( "\n device to be written found\n");
////                                    // peripheralTextView.setText("");
////                                    String originalString = DataToSend.getText().toString();
////                                    characteristic.setValue(originalString); // call this BEFORE(!) you 'write' any stuff to the server
////                                    gatt.writeCharacteristic(characteristic);
////                                    peripheralTextView.append("\n write to ESP32= " + originalString);
////                                }
////                                if (characteristic.getUuid().toString().equals("6e400003-b5a3-f393-e0a9-e50e24dcca9e")) {
////                                    // peripheralTextView.append( "\n8888888Congratulations Data read from device: \n");
////
////
////                                    gatt.readCharacteristic(characteristic);
////                                    byte[] data = characteristic.getValue();
////                                    peripheralTextView.append("\nchanged=: " + data);
//////                                    {
////                                    //  peripheralTextView.append( characteristic.getValue().toString());
////                                    // peripheralTextView.append("\n999999Congratulations Data read from device: \n");
////
//////                                    int flag = characteristic.getProperties();
//////                                    int format = -1;
//////                                    if ((flag & 0x01) != 0) {
//////                                        format = BluetoothGattCharacteristic.FORMAT_UINT16;
//////                                        peripheralTextView.append( "\nHeart rate format UINT16.");
//////                                    } else {
//////                                        format = BluetoothGattCharacteristic.FORMAT_UINT8;
//////                                        peripheralTextView.append( "\nHeart rate format UINT8.");
//////                                    }
//////                                    final int heartRate = characteristic.getIntValue(format, 1);
//////                                    peripheralTextView.append( String.format("Received heart rate: %d", heartRate));
//////
//////                                    }
////                                }
////
////                            }
////                        }
////
////                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
////                    } else {
////                        peripheralTextView.append("\nnot successful: " + status);
////                    }
////                }
////            });
////            displayGattServices(bluetoothGatt.getServices());
////        }
////
////        @Override
////        // this will get called anytime you perform a read or write characteristic operation
////        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
////            MainActivity.this.runOnUiThread(new Runnable() {
////                public void run() {
////
////                    gatt.writeCharacteristic(characteristic);
////                    peripheralTextView.append("device read or wrote to\n");
////
////                }
////            });
////        }
////
////        @Override
////        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
////            if (status == BluetoothGatt.GATT_SUCCESS) {
////                peripheralTextView.append("\nRSSI=: " + rssi);
////            }
////        }
////
////
////        @Override
////        // Result of a characteristic read operation
////        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
////
////            peripheralTextView.append("\nreading data=: ");
////
////            characteristic = gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")).getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"));
////
////            boolean isReading = gatt.readCharacteristic(characteristic);
////            if (status == BluetoothGatt.GATT_SUCCESS) {
////
////                byte[] value = characteristic.getValue();
////                StringBuilder sb = new StringBuilder();
////                for (byte b : value) {
////                    sb.append(String.format("%02X", b));
////                }
//////                synchronized (readLock) {
//////                    isReading = false;
//////                    readLock.notifyAll();
//////                }
////                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
////            }
////        }
////    };
////    // Stops scanning after 5 seconds.
////    private Handler mHandler = new Handler();
////    // 2 Device scan callback. for showing scanned device result
////    private ScanCallback leScanCallback = new ScanCallback() {
////        @Override
////        public void onScanResult(int callbackType, ScanResult result) {
////            if (result.getDevice().getName() != null) {
////                //    peripheralTextView.append("Index: " + deviceIndex + ", \tDevice Name: " + result.getDevice().getName() + " \t\t\t\trssi: " + result.getRssi() + "\n");
////                devicesDiscovered.add(result.getDevice());
////                btArrayAdapter.add("Name: " + result.getDevice().getName() + "\n" + "Address: " + result.getDevice().getAddress() + "\n" + "rssi: " + result.getRssi() + " dBm");
////                btArrayAdapter.notifyDataSetChanged();
////                deviceIndex++;
////
////                // auto scroll for text view
////                // final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
////                // if there is no need to scroll, scrollAmount will be <=0
////                // if (scrollAmount > 0) {
////                //    peripheralTextView.scrollTo(0, scrollAmount);
////                //}
////            }
////        }
////    };
////
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////        Toolbar toolbar = findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
////
////
////        FloatingActionButton fab = findViewById(R.id.fab);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////            }
////        });
////        DrawerLayout drawer = findViewById(R.id.drawer_layout);
////        NavigationView navigationView = findViewById(R.id.nav_view);
////        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
////                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
////        drawer.addDrawerListener(toggle);
////        toggle.syncState();
////        navigationView.setNavigationItemSelectedListener(this);
////
////
////        listDevicesFound = findViewById(R.id.devicesfound);
////        btArrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
////        listDevicesFound.setAdapter(btArrayAdapter);
////
////        listDevicesFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//////                // calling the gatt callback auto connect is true for saving connection history
//////               ScannedDevice item = mDeviceAdapter.getItem(position);
//////                if (item != null) {
////
////
////                //Intent intent = new Intent(view.getContext(), FirstActivity.class);
////
////                //  int deviceSelected = Integer.parseInt(deviceIndexInput.getText().toString());
////
////                // calling the gatt callback auto connect is true for saving connection history
////                // bluetoothGatt = (BluetoothGatt) devicesDiscovered.get(i).connectGatt(this, false, btleGattCallback);
////
////
////                // BluetoothDevice selectedDevice = item.getDevice();
////                //   intent.putExtra("deviceonce",devicesDiscovered.get(i));
////                //  startActivity(intent);
////
////
////                BluetoothDevice selectedDevice1 = devicesDiscovered.get(i);
////                Intent intent1 = new Intent(view.getContext(), FirstActivity.class);
////                intent1.putExtra("btdevice", selectedDevice1);
////                startActivity(intent1);
//////
//////                    // stop before change Activity
////                // stopScan();
//////                }
////            }
////        });
////
////
//////        sendData = findViewById(R.id.sendto);
//////        DataToSend = findViewById(R.id.Inputdata);
//////        DataToSend.setText("A");
//////        sendData.setOnClickListener(new View.OnClickListener() {
//////            public void onClick(View v) {
//////                peripheralTextView.setText("");
//////                bluetoothGatt.discoverServices();
//////            }
//////        });
//////        //text view
//////        peripheralTextView = (TextView) findViewById(R.id.PeripheralTextView);
//////        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());
//////        peripheralTextView.setText("wellcome\n");
////
////        //input text to select wich device
////        //deviceIndexInput = (EditText) findViewById(R.id.InputIndex);
////        //deviceIndexInput.setText("0");
////
////        // start scan button
////
//////        startScanningButton = (Button) findViewById(R.id.StartScanButton);
//////        startScanningButton.setOnClickListener(new View.OnClickListener() {
//////            public void onClick(View v) {
//////                startScanning();
//////            }
//////        });
////
////
//////        //stop scan button
//////        stopScanningButton = (Button) findViewById(R.id.StopScanButton);
//////        stopScanningButton.setOnClickListener(new View.OnClickListener() {
//////            public void onClick(View v) {
//////                stopScanning();
//////            }
//////        });
//////        stopScanningButton.setVisibility(View.INVISIBLE);
////
////
////        //connect button to device at index
//////        connectToDevice = (Button) findViewById(R.id.ConnectButton);
//////        connectToDevice.setOnClickListener(new View.OnClickListener() {
//////            public void onClick(View v) {
//////                connectToDeviceSelected();
//////            }
//////        });
//////        // disconect button already connected
//////        disconnectDevice = (Button) findViewById(R.id.DisconnectButton);
//////        disconnectDevice.setVisibility(View.INVISIBLE);
//////        disconnectDevice.setOnClickListener(new View.OnClickListener() {
//////            public void onClick(View v) {
//////                disconnectDeviceSelected();
//////            }
//////        });
////
////
////        //getting bluetooth service
////        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
////        btAdapter = btManager.getAdapter();
////        btScanner = btAdapter.getBluetoothLeScanner();
////        //turning on bloototh if not on
////        if (btAdapter != null && !btAdapter.isEnabled()) {
////            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
////        }
////
////
////        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
////                builder.setTitle("This app needs location access");
////                builder.setMessage("Please grant location access so this app can detect peripherals.");
////                builder.setPositiveButton(android.R.string.ok, null);
////                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
////                    @RequiresApi(api = Build.VERSION_CODES.M)
////                    @Override
////                    public void onDismiss(DialogInterface dialog) {
////                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
////                    }
////                });
////                builder.show();
////            }
////        }
////
////    }
////
////    // 1scanning bt server
////    public void startScanning() {
////        btScanning = true;
////        deviceIndex = 0;// initializing index
////        devicesDiscovered.clear();//
////        btArrayAdapter.clear();
////        // peripheralTextView.setText("");//
////        // peripheralTextView.append("Started Scanning\n");
////        //startScanningButton.setVisibility(View.INVISIBLE);
////        // stopScanningButton.setVisibility(View.VISIBLE);
////        AsyncTask.execute(new Runnable() {
////            @Override
////            public void run() {
////                btScanner.startScan(leScanCallback);//calling bt service
////            }
////        });
////
////        mHandler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                // peripheralTextView.append("stopped Scanning\n");
////                stopScanning();//stop scanning when time is over
////            }
////        }, SCAN_PERIOD);
////    }
////
////    public void stopScanning() {
//////        peripheralTextView.append("Stopped Scanning\n");
////        btScanning = false;
//////        startScanningButton.setVisibility(View.VISIBLE);
////        //    stopScanningButton.setVisibility(View.INVISIBLE);
////        AsyncTask.execute(new Runnable() {
////            @Override
////            public void run() {
////                btScanner.stopScan(leScanCallback);//stop bt scanning
////            }
////        });
////    }
////
////    // 3creating connection using the value of index at the edit text
////    public void connectToDeviceSelected() {
////        //peripheralTextView.setText("");
////        // peripheralTextView.append("Trying to connect to device at index: " + deviceIndexInput.getText() + "\n");
////        int deviceSelected = Integer.parseInt(deviceIndexInput.getText().toString());
////
////        // calling the gatt callback auto connect is true for saving connection history
////        bluetoothGatt = devicesDiscovered.get(deviceSelected).connectGatt(this, false, btleGattCallback);
////    }
////
////    private void broadcastUpdate(final String action,
////                                 final BluetoothGattCharacteristic characteristic) {
////        final Intent intent = new Intent(action);
////
////        if (UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e").equals(characteristic.getUuid())) {
////
////            final byte[] data = characteristic.getValue();
////            if (data != null && data.length > 0) {
////                final StringBuilder stringBuilder = new StringBuilder(data.length);
////                for (byte byteChar : data)
////                    stringBuilder.append(String.format("%02X ", byteChar));
////                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
////
////
////                peripheralTextView.append("ashu" + characteristic.getUuid().toString() + "\n" + stringBuilder.toString());
////            }
////        }
////        sendBroadcast(intent);
////    }
////
////    public void disconnectDeviceSelected() {
////        peripheralTextView.setText("");
////        peripheralTextView.append("Disconnecting from device\n");
////        bluetoothGatt.disconnect();
////
////    }
////
////    private void broadcastUpdate(final String action) {
////        final Intent intent = new Intent(action);
////        sendBroadcast(intent);
////    }
////
////    private void displayGattServices(List<BluetoothGattService> gattServices) {
////        if (gattServices == null) return;
////
////        // Loops through available GATT Services.
////        for (BluetoothGattService gattService : gattServices) {
////
////            final String uuid = gattService.getUuid().toString();
////            MainActivity.this.runOnUiThread(new Runnable() {
////                public void run() {
////                    // peripheralTextView.append("Sservice discovered: \n"+uuid+"\n");
////                }
////            });
////            new ArrayList<HashMap<String, String>>();
////            List<BluetoothGattCharacteristic> gattCharacteristics =
////                    gattService.getCharacteristics();
////
////            // Loops through available Characteristics.
////            for (BluetoothGattCharacteristic gattCharacteristic :
////                    gattCharacteristics) {
////
////                final String charUuid = gattCharacteristic.getUuid().toString();
////                MainActivity.this.runOnUiThread(new Runnable() {
////                    public void run() {
////                        // peripheralTextView.append("Ccharacteristic discovered for service:\n "+charUuid+"\n");
////                    }
////                });
////
////            }
////        }
////    }
////
////    @Override
////    public void onBackPressed() {
////        DrawerLayout drawer = findViewById(R.id.drawer_layout);
////        if (drawer.isDrawerOpen(GravityCompat.START)) {
////            drawer.closeDrawer(GravityCompat.START);
////        } else {
////            super.onBackPressed();
////        }
////    }
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.main, menu);
////        tmenu = menu;
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        // Handle action bar item clicks here. The action bar will
////        // automatically handle clicks on the Home/Up button, so long
////        // as you specify a parent activity in AndroidManifest.xml.
////        switch (item.getItemId()) {
////            case R.id.action_scan:
////                MenuItem item1 = tmenu.findItem(R.id.action_stop);
////                item1.setVisible(true);
////                this.invalidateOptionsMenu();
////                startScanning();
////                break;
////            case R.id.action_stop:
////                stopScanning();
////                break;
////        }
////
////
////        return super.onOptionsItemSelected(item);
////    }
////
////    @SuppressWarnings("StatementWithEmptyBody")
////    @Override
////    public boolean onNavigationItemSelected(MenuItem item) {
////        // Handle navigation view item clicks here.
////        int id = item.getItemId();
////
////        if (id == R.id.nav_home) {
////            // Handle the camera action
////        } else if (id == R.id.nav_gallery) {
////
////        } else if (id == R.id.nav_slideshow) {
////
////        } else if (id == R.id.nav_tools) {
////
////        } else if (id == R.id.nav_share) {
////
////        } else if (id == R.id.nav_send) {
////
////        }
////
////        DrawerLayout drawer = findViewById(R.id.drawer_layout);
////        drawer.closeDrawer(GravityCompat.START);
////        return true;
////    }
////
////
//////    private final BroadcastReceiver receiver = new BroadcastReceiver(){
//////        @Override
//////        public void onReceive(Context context, Intent intent) {
//////
//////            String action = intent.getAction();
//////            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
//////                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
//////               // Toast.makeText(BtControl.this,"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
//////            }
//////        }
//////    };
//////
//////    registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
////}
////
////
//////ser    00001801-0000-1000-8000-00805f9b34fb  generic attribute
//////ch     00002a05-0000-1000-8000-00805f9b34fb   service changed
////
//////ser    00001800-0000-1000-8000-00805f9b34fb   generic accesss
//////ch     00002a00-0000-1000-8000-00805f9b34fb   device name
//////ch     00002a01-0000-1000-8000-00805f9b34fb    appearance
//////ch     00002aa6-0000-1000-8000-00805f9b34fb     address resolution
////
//////ser    6e400001-b5a3-f393-e0a9-e50e24dcca9e  custome service
//////ch     6e400003-b5a3-f393-e0a9-e50e24dcca9e  read
//////ch     6e400002-b5a3-f393-e0a9-e50e24dcca9e  write
////
/////**
//// * broadcast receiver which receives the response from IDeviceCommand.readDescriptor()
//// *
//// * @author matt2
//// * <p>
//// * //
//// */
//////public abstract class RSSIReceiver extends BroadcastReceiver {
//////
//////    @Override
//////    public void onReceive(Context context, Intent intent) {
//////        String deviceAddress = intent.getStringExtra(DeviceService.EXTRA_DEVICE_ADDRESS);
//////        int rssi = intent.getIntExtra(DeviceService.EXTRA_RSSI, 0);
//////        int status = intent.getIntExtra(DeviceService.EXTRA_STATUS, 0);
//////        onRSSI(deviceAddress, rssi, status);
//////    }
//////
//////    /**
//////     * implement this to receive the rssi from the device
//////     * @param deviceAdress String of the device MAC address
//////     * @param status status code 0: good, otherwise bad things happened
//////     */
//////
//////    public abstract void onRSSI(String   deviceAdress,
//////                                int    rssi,
//////                                int    status);
//////}
////
////
//////    private void displayGattServices(List<BluetoothGattService> gattServices) {
//////        if (gattServices == null) return;
//////        // Loops through available GATT Services.
//////        for (BluetoothGattService gattService : gattServices) {
//////            final String uuid = gattService.getUuid().toString();
//////            FirstActivity.this.runOnUiThread(new Runnable() {
//////                public void run() {
//////                     peripheralTextView.append("Sservice discovered: \n"+uuid+"\n");
//////                }
//////            });
//////            new ArrayList<HashMap<String, String>>();
//////            List<BluetoothGattCharacteristic> gattCharacteristics =
//////                    gattService.getCharacteristics();
//////
//////            // Loops through available Characteristics.
//////            for (BluetoothGattCharacteristic gattCharacteristic :
//////                    gattCharacteristics) {
//////
//////                final String charUuid = gattCharacteristic.getUuid().toString();
//////                FirstActivity.this.runOnUiThread(new Runnable() {
//////                    public void run() {
//////                         peripheralTextView.append("Ccharacteristic discovered for service:\n "+charUuid+"\n");
//////                    }
//////                });
//////
//////            }
//////        }
////// }
////
////
////
//////
//////    /*
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////
//////    private static final int REQUEST_ENABLE_BT = 1;
//////
//////
//////    Button btnScanDevice;
//////    TextView stateBluetooth;
//////    BluetoothAdapter bluetoothAdapter;
//////    ListView listDevicesFound;
//////    ArrayAdapter<String> btArrayAdapter;
//////
//////
////////    @Override
////////    protected void onCreate(Bundle savedInstanceState) {
////////        super.onCreate(savedInstanceState);
////////        setContentView(R.layout.first_activity);
////////
////////        scan = (Button) findViewById(R.id.button1);
////////        scan.setOnClickListener(new View.OnClickListener() {
////////            @Override
////////            public void onClick(View v) {
////////                Intent myIntent = new Intent(FirstActivity.this, MainActivity.class);
////////                FirstActivity.this.startActivity(myIntent);
////////            }
////////        });
////////
////////
////////    }
//////
//////
//////
//////
//////
///////*
//////    /** Called when the activity is first created. */
//////    @Override
//////    public void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.first_activity);
//////
//////        btnScanDevice = (Button)findViewById(R.id.scandevice);
//////
//////        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
//////        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//////
//////        listDevicesFound = (ListView)findViewById(R.id.devicesfound);
//////        btArrayAdapter = new ArrayAdapter<String>(FirstActivity.this, android.R.layout.simple_list_item_1);
//////        listDevicesFound.setAdapter(btArrayAdapter);
//////
//////        CheckBlueToothState();
//////
//////        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
//////
//////        registerReceiver(ActionFoundReceiver,
//////                new IntentFilter(BluetoothDevice.ACTION_FOUND));
//////    }
//////
//////    @Override
//////    protected void onDestroy() {
//////        // TODO Auto-generated method stub
//////        super.onDestroy();
//////        unregisterReceiver(ActionFoundReceiver);
//////    }
//////
//////    private void CheckBlueToothState(){
//////        if (bluetoothAdapter == null){
//////            stateBluetooth.setText("Bluetooth NOT support");
//////        }else{
//////            if (bluetoothAdapter.isEnabled()){
//////                if(bluetoothAdapter.isDiscovering()){
//////                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
//////                }else{
//////                    stateBluetooth.setText("Bluetooth is Enabled.");
//////                    btnScanDevice.setEnabled(true);
//////                }
//////            }else{
//////                stateBluetooth.setText("Bluetooth is NOT Enabled!");
//////                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//////                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//////            }
//////        }
//////    }
//////
//////    private Button.OnClickListener btnScanDeviceOnClickListener
//////            = new Button.OnClickListener(){
//////
//////        @Override
//////        public void onClick(View arg0) {
//////            // TODO Auto-generated method stub
//////
//////            btArrayAdapter.clear();
//////            bluetoothAdapter.startDiscovery();
//////        }};
//////
//////    @Override
//////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//////        // TODO Auto-generated method stub
//////        if(requestCode == REQUEST_ENABLE_BT){
//////            CheckBlueToothState();
//////        }
//////    }
//////
//////    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
//////
//////        @Override
//////        public void onReceive(Context context, Intent intent) {
//////            // TODO Auto-generated method stub
//////            String action = intent.getAction();
//////            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
//////
//////                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//////                short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
//////                btArrayAdapter.add("Name: "+device.getName() + "\n" +"Address: "+ device.getAddress() + "\n" +"rssi: "+ rssi +" dBm");
//////                btArrayAdapter.notifyDataSetChanged();
//////            }
//////
//////            TimerTask task = new TimerTask(){
//////                public void run(){
//////                    //execute the task
//////                    btArrayAdapter.clear();
//////                    bluetoothAdapter.startDiscovery();
//////                }
//////            };
//////            Timer timer = new Timer();
//////            timer.schedule(task, 1000);
//////        }};
//////
//////
//////
//////}
//////
//@Override
//// Result of a characteristic read operation
//public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//
//        String characteristicValue = characteristic.getValue().toString();
//        peripheralTextView.append("CHARACTERISTIC VALUE: " + characteristicValue);
//        gatt.disconnect();
//
//
//        peripheralTextView.append("\nreading data=: ");
//        if (status == BluetoothGatt.GATT_SUCCESS) {
//        peripheralTextView.append("onCharacteristicRead status = " + status);
//        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//        }
//
//final byte[] data = characteristic.getValue();
//        if (data != null && data.length > 0) {
//final StringBuilder stringBuilder = new StringBuilder(data.length);
//        for (byte byteChar : data) {
//        stringBuilder.append(String.format("%02X ", byteChar));
//        }
//
//
//final String strReceived = stringBuilder.toString();
//        peripheralTextView.append("received string status = " + strReceived);
//
//        }
//        }
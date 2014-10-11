/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uni.leipzig.bm2.ble;

import java.util.List;
import java.util.UUID;

import uni.leipzig.bm2.config.BottleMailConfig;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {

	private static final boolean DEBUG = BottleMailConfig.BLE_DEBUG;	
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    
    private String receivedMessage = "";
    private String receivedHex = "";
    
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    // Service UUIDs
    private final static UUID SERV_UUID_BM = 
    		UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private final static UUID SERV_UUID_GACCESS = 
    		UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    private final static UUID SERV_UUID_GATTRIBUTE = 
    		UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    
    // Characteristic UUIDs
    private final static UUID CHAR_UUID_BOTTLEMAIL = 
    		UUID.fromString(SampleGattAttributes.BOTTLEMAIL);
    private final static UUID CHAR_UUID_DEVICE_NAME = 
    		UUID.fromString(SampleGattAttributes.DEVICE_NAME);
    private final static UUID CHAR_UUID_SERVICE_CHANGED = 
    		UUID.fromString(SampleGattAttributes.SERVICE_CHANGED);
//    private final static UUID CHAR_UUID_HEART_RATE_MEASUREMENT =
//            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    		if(DEBUG) Log.e(TAG, "+++ onConnectionStateChange +++");
    		
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
    		if(DEBUG) Log.e(TAG, "+++ onServicesDiscovered +++");
    		
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
    		if(DEBUG) Log.e(TAG, "+++ onCharacteristicRead +++");
    		
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
    		if(DEBUG) Log.e(TAG, "+++ onCharacteristicChanged +++");
    		
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
        
//        @Override
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
////        	characteristic = gatt.getService(UUID_DEVICE_NAME).getCharacteristic(UUID_DEVICE_NAME);
//        	String deviceName = gatt.getDevice().getName();
//        	String serviceName = characteristic.getService().getUuid().toString();
//        	String charName = characteristic.getUuid().toString();
//        	String value = characteristic.getStringValue(0);
//        	
//        	int props = characteristic.getProperties();
//        	String propertiesString = String.format("0x%04X ", props);
//        	if((props & BluetoothGattCharacteristic.PROPERTY_READ) != 0) propertiesString += "read ";
//        	if((props & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) propertiesString += "write ";
//        	if((props & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) propertiesString += "notify ";
//        	if((props & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) propertiesString += "indicate ";
//        	
//        	Log.e(TAG, deviceName + " | " + serviceName + " | " + charName + " | " + value + " | " + propertiesString);
//        }
//
//        @Override
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor desc, int status) {
////        	characteristic = gatt.getService(UUID_DEVICE_NAME).getCharacteristic(UUID_DEVICE_NAME);
//        	String deviceName = gatt.getDevice().getName();
//        	String serviceName = desc.getCharacteristic().getService().getUuid().toString();
//        	String charName = desc.getCharacteristic().getUuid().toString();
//        	String descName = desc.getUuid().toString();
//        	String value = desc.getValue().toString();
//        	
//        	int props = desc.getCharacteristic().getProperties();
//        	String propertiesString = String.format("0x%04X ", props);
//        	if((props & BluetoothGattCharacteristic.PROPERTY_READ) != 0) propertiesString += "read ";
//        	if((props & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) propertiesString += "write ";
//        	if((props & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) propertiesString += "notify ";
//        	if((props & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) propertiesString += "indicate ";
//        	
//        	Log.e(TAG, deviceName + " | " + serviceName + " | " + charName + " | " + descName + " | " + value + " | " + propertiesString);
//        }
    };

    private void broadcastUpdate(final String action) {
		if(DEBUG) Log.e(TAG, "+++ broadcastUpdate(action) +++");
		
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
		if(DEBUG) Log.e(TAG, "+++ broadcastUpdate(action, characteristic) +++");
		
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?
        // u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (CHAR_UUID_BOTTLEMAIL.equals(characteristic.getUuid())) {
            // For BootlEmail-Hello-World profile
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                receivedHex += stringBuilder.toString();
                receivedMessage += new String(data);
                
                // TODO: Change test on '.' to real message end notice.
                if (receivedMessage.charAt(receivedMessage.length()-1) == '.') {
                	intent.putExtra(EXTRA_DATA, receivedMessage + "\n" + receivedHex);
                	receivedHex = "";
                	receivedMessage = "";
                }
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
//        } else if (CHAR_UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
//            }
//            final int heartRate = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
//            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        // HAVE to be public to put this class into ble-package
    	public BluetoothLeService getService() {
    		if(DEBUG) Log.e(TAG, "+++ getService +++");
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
		if(DEBUG) Log.e(TAG, "+++ onBind +++");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
		if(DEBUG) Log.e(TAG, "+++ onUnbind +++");
        
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
		if(DEBUG) Log.e(TAG, "+++ initialize +++");
        
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
		if(DEBUG) Log.e(TAG, "+++ connect +++");
        
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
		if(DEBUG) Log.e(TAG, "+++ disconnect +++");
        
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
		if(DEBUG) Log.e(TAG, "+++ close +++");
        
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if(DEBUG) Log.e(TAG, "+++ readCharacteristic +++");
        
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
		if(DEBUG) Log.e(TAG, "+++ setCharacteristicNotification +++");
        
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (CHAR_UUID_BOTTLEMAIL.equals(characteristic.getUuid())) {
        	Log.e(TAG, "Would set the notification for the descriptors of BottlEmail characteristic, " +
        			"but that seems to be unneccesary.");
//        	BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//        			UUID.fromString(SampleGattAttributes.CHARACTERISTIC_USER_DESCRIPTION));
//        	descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//        	mBluetoothGatt.writeDescriptor(descriptor);
        // This is specific to Heart Rate Measurement.
//      }else if (CHAR_UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
        } else if (CHAR_UUID_DEVICE_NAME.equals(characteristic.getUuid())){
        	Log.e(TAG, "Would set the notification for the descriptors of Device name characteristic, " +
        			"if there are some.");
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
		if(DEBUG) Log.e(TAG, "+++ getSupportedGattServices +++");
        
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }
    
    public void writeDataToBottlEmailCharacteristic(String data) {
		if(DEBUG) Log.e(TAG, "+++ testWriteDataToBottlEmailCharacteristic +++");
		
    	BluetoothGattCharacteristic chara = 
    			mBluetoothGatt.getService(SERV_UUID_BM).
    			getCharacteristic(CHAR_UUID_BOTTLEMAIL);
    	mBluetoothGatt.setCharacteristicNotification(chara, true);
    	chara.setValue(data.getBytes());
    	mBluetoothGatt.writeCharacteristic(chara);
    }
    
    public void testWriteDataToAllCharacteristics () {
		if(DEBUG) Log.e(TAG, "+++ testWriteDataToAllCharacteristics +++");
        		
		// read / notify
		BluetoothGattCharacteristic chara = 
				mBluetoothGatt.getService(SERV_UUID_BM).
				getCharacteristic(CHAR_UUID_BOTTLEMAIL);
		mBluetoothGatt.setCharacteristicNotification(chara, true);
		
		String blubb = new String("Blubb");
		chara.setValue(blubb.getBytes());
		mBluetoothGatt.writeCharacteristic(chara);

		// does not write back
		chara = mBluetoothGatt.getService(SERV_UUID_GATTRIBUTE).
				getCharacteristic(CHAR_UUID_SERVICE_CHANGED);
		mBluetoothGatt.setCharacteristicNotification(chara, true);
		chara.setValue(blubb.getBytes());
		mBluetoothGatt.writeCharacteristic(chara);
		
		// only 2a02 and 2a03 are responsing with read/write properties
    	List<BluetoothGattCharacteristic> charas = 
    			mBluetoothGatt.getService(SERV_UUID_GACCESS).getCharacteristics();
    	for (BluetoothGattCharacteristic ch : charas) {
    		mBluetoothGatt.setCharacteristicNotification(chara, true);
    		ch.setValue(blubb.getBytes());
    		mBluetoothGatt.writeCharacteristic(ch);
    	}
    }

//    public void testWriteDataToAllDescriptors () {
//		if(DEBUG) Log.e(TAG, "+++ testWriteDataToAllDescriptors +++");
//        
//    	BluetoothGattCharacteristic chara = mBluetoothGatt.getService(SERV_UUID_BM).
//				getCharacteristic(CHAR_UUID_BOTTLEMAIL);
//    	List<BluetoothGattDescriptor> descris = chara.getDescriptors();
//		mBluetoothGatt.setCharacteristicNotification(chara, true);
//		
//		String blubb = new String("Blubb");
//		for (BluetoothGattDescriptor desc : descris) {
//			desc.setValue(blubb.getBytes());
//	    	mBluetoothGatt.writeDescriptor(desc);
//		}
//    }
}

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

import java.util.HashMap;

import android.util.Log;

import uni.leipzig.bm2.config.BottleMailConfig;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
	
	private static final boolean DEBUG = BottleMailConfig.BLE_DEBUG;	
    private final static String TAG = SampleGattAttributes.class.getSimpleName();
    
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    public static String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static String PERIPH_PRIVACY_FLAG = "00002a02-0000-1000-8000-00805f9b34fb";
    public static String RECONNECTION_ADDRESS = "00002a03-0000-1000-8000-00805f9b34fb";
    public static String PERIPH_PREF_CONN_PARAM = "00002a04-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb";
    public static String TEMPERATURE = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    
    static {
        // Sample Services.
    	attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Temperature Service");
        
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(DEVICE_NAME, "Device name");
        attributes.put(APPEARANCE, "Appearance");
        attributes.put(PERIPH_PRIVACY_FLAG, "Peripheral privacy flag");
        attributes.put(RECONNECTION_ADDRESS, "Reconnection address");
        attributes.put(PERIPH_PREF_CONN_PARAM, "Peripheral preferred connection parameters");
        attributes.put(SERVICE_CHANGED, "Service Changed");
        attributes.put(TEMPERATURE, "Temperatur in Celsius");
        
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
		if(DEBUG) Log.e(TAG, "+++ lookup +++");
		
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

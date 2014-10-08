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
    public static String BMAIL_HELLO = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String BMAIL_CONFIG_DESCRIPTOR = /*"00002a01-0000-1000-8000-00805f9b34fb";*/"00002902-0000-1000-8000-00805f9b34fb";
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
    	attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Bottlemail Service");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(BMAIL_HELLO, "Bottlemail Hello");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
		if(DEBUG) Log.e(TAG, "+++ lookup +++");
		
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

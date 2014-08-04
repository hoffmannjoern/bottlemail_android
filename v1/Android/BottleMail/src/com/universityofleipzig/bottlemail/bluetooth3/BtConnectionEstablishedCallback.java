package com.universityofleipzig.bottlemail.bluetooth3;

import android.bluetooth.BluetoothSocket;

public interface BtConnectionEstablishedCallback {

	void connectionEstablished(BluetoothSocket socket);

}

package com.universityofleipzig.bottlemail.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Formatter;

import com.universityofleipzig.bottlemail.data.BMail;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluethoothConectedThread extends Thread {

	// debugging
	private static final String TAG = "BluethoothConectedThread";

	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;

	public BluethoothConectedThread(BluetoothSocket socket) {
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {

		byte[] buffer = new byte[1]; // buffer store for the stream
		int bytes = -1; // bytes returned from read()
		boolean isNew = true;
		// Keep listening to the InputStream until an exception occurs
		while (true) {

			try {

				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI activity
				// mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
				// buffer).sendToTarget();
				Log.e(TAG, "Buffer: " + new String(buffer) + " , "
						+ byteToHex(buffer)+"|bytes"+bytes);
				//MainFragmentController.getInstance().receiveMSGTEST(new BMail(0, new String(buffer) + "," +byteToHex(buffer), Calendar.getInstance()));

			} catch (IOException e) {
				Log.e(TAG, "CT-run: IOException: " + e.getMessage());
				break;
			}
		}
		try {
			mmInStream.close();
		} catch (IOException e) {
			Log.e(TAG, "CT-closeIn: IOException: " + e.getMessage());
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(String hex) {
		try {
			mmOutStream.write(hexStringToByteArray(hex));
			mmOutStream.flush();
		} catch (IOException e) {
			Log.e(TAG, "CT-write: IOException" + e.getMessage());
			// cancel();
		}
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			if (mmInStream != null)
				this.mmInStream.close();
			if (mmOutStream != null)
				this.mmOutStream.close();
			if (mmSocket != null)
				mmSocket.close();
			Log.d(TAG, "canceled");
		} catch (IOException e) {
			Log.e(TAG, "cancel: IOException" + e.getMessage());
		}
	}

	private String byteToHex(byte[] bytes) {

		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			new Formatter(sb).format("%02x", b);
		}

		return sb.toString();
	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}

	public void testwrite() {
		// byte[] bArray = new byte[1];
		// bArray[0] = (byte) ;
		// this.write(hexStringToByteArray("24000121cdd7"));
		/*
		 * Header: Startzeichen 24 ($) Timestamp (8Byte) AuthorLength (1Byte)
		 * Titlelength (1Byte) Textlength (8Byte) Piclength (8Byte) Author
		 * (AuthorlengthByte) Title (TitlelengthByte) ende 21 (!) CRC (2Byte)
		 */
		// send header, warten auf id
		// danach send nachricht
		/*
		 * Nachricht: Startzeichen 24 ($) id: Text 0202/ Bild 0203 Nachricht
		 * (TextlengthByte aus Header bzw max 300Byte) end 21 (!) crc (2Byte)
		 */
		// nach jeder nachricht antowrt ID (Text/Bild) oder fehler, dann erneut
		// senden
		// wenn nachricht gößer als 300B, dann aufsplitten in entsprechende
		// Teile
		this.write("24000221a5fd");
	}

}
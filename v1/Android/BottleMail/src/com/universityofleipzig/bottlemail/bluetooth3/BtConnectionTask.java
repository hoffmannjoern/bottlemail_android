package com.universityofleipzig.bottlemail.bluetooth3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import com.universityofleipzig.bottlemail.data.DataTransformer;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

public class BtConnectionTask extends AsyncTask<Void, Byte[], Void> {
	
	private String TAG = "BtConnectionTask";
	private final BluetoothSocket mBtSocket;
    private ConnectionReceiver mCallback;
    private final InputStream mBtInStream;
    private final OutputStream mBtOutStream;
    
    private boolean mAdd = false;
    private ArrayList<Byte> mReceivedBytes = new ArrayList<Byte>();

    public BtConnectionTask(BluetoothSocket socket) {
        mBtSocket = socket;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mBtInStream = tmpIn;
        mBtOutStream = tmpOut;
    }
    
    /** Task start: initiates the receive loop. */
    @Override
    protected Void doInBackground(Void... params) {
		Log.d(TAG, "+++++ doInBackground +++++");
        loopReceiver();
        return null;
    }

    /** Starts receiver loop that will remain active as long as the socket exists. */
    private void loopReceiver() {
    	
    	Log.d(TAG, "+++++ loopReceiver +++++");
    	// buffer store for the stream
		byte[] buffer = new byte[1];
		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				
				// Read from the InputStream
				mBtInStream.read(buffer);
				
				//Log.d(TAG, "Buffer: (" + mReceivedBytes.size() + ") " + buffer[0]+ " --> "+DataTransformer.byteToHex(buffer)+" -- "+new String(buffer, "UTF-8"));
				
				if(buffer[0] == 36){
					//mReceivedBytes.clear();
					mAdd = true;
				}
				
				if(mAdd){
					mReceivedBytes.add(buffer[0]);
					//Log.d(TAG, "Buffer: (" + mReceivedBytes.size() + ") " + buffer[0]+ " --> "+DataTransformer.byteToHex(buffer)+" -- "+new String(buffer, "UTF-8"));
					publishProgress(getReceivedBytes());
				}
			} catch (IOException e) {
				Log.e(TAG, "CT-run: IOException: " + e.getMessage());
				break;
			}
		}

		try {
			mBtInStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * Called on the GUI thread when we get new incoming data to trigger
     * the incoming data callback.
     */
    @Override
    protected void onProgressUpdate(Byte[]... als) {
    	//Log.d(TAG, "+++++ onProgressUpdate +++++");
        Byte[] msg = als[0];
        mCallback.incomingData(msg);
    }
       
    /** Sends data to the remote device. */
    public void send(String hex, ConnectionReceiver callback) {
    	//Log.d(TAG, "+++++ send +++++");
    	//Log.d(TAG, "Hex-String: "+ hex);
    	mCallback = callback;
    	
        try {
        	mAdd = false;
        	mReceivedBytes.clear();
            mBtOutStream.write(DataTransformer.hexStringToByteArray(hex));
            mBtOutStream.flush();
        } catch (IOException e) {
			Log.e(TAG, "CT-write: IOException" + e.getMessage());
		}
    }
    
    /** Shuts the socket when the task is cancelled. */
    @Override
    protected void onCancelled() {
    	Log.d(TAG, "onCancelled");
        /*if(mBtSocket.isConnected()){
        	Log.d(TAG, "socket connected!");
        }
    	try {
    		mBtSocket.close();
            Log.d(TAG, "socket now closed.");
        } catch (IOException e) { }*/
    }
    
    Byte[] getReceivedBytes(){
    	return mReceivedBytes.toArray(new Byte[mReceivedBytes.size()]);
    }
}

package com.universityofleipzig.bottlemail.bluetooth3;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import com.universityofleipzig.bottlemail.data.DataTransformer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

public class BtClientTask extends AsyncTask<Void, Void, BluetoothSocket>{
	
	private static final String TAG = "BtClientTask";
	
	private final static UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothSocket mBtSocket;
    private final BluetoothDevice mBtDevice;
    private final BluetoothAdapter mBtAdapter;

    private BluetoothSocket mCurentBtSocket;
    private Queue<QueueEntry> mCommands = new LinkedList<QueueEntry>();
    private BtConnectionTask btCurentConnection;
    private ConnectionFinishedListener mListener;
    
    public BtClientTask(BluetoothDevice device) {
    	
    	Log.d(TAG, "+++++ construct BtClientTask +++++");
		
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
       
        // Use a temporary object that is later assigned to the socket,
        // because the socket is final
        BluetoothSocket tmpSocket = null;
        mBtDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // Use the Serial Port Profile
            tmpSocket = mBtDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        }
        catch (IOException e) { 
        	Log.e(TAG, "Socket-Error: " + e.getMessage());
        }
        mBtSocket = tmpSocket;
        
        mListener = new ConnectionFinishedListener(){
			@Override
			public void notify(Void e){
				sendNextCommand();
			}
		};
    }

    public void addCommand(String hex, ConnectionReceiver callback){
    	mCommands.offer(new QueueEntry(hex, callback));
    }
    
    private void sendNextCommand(){
    	
    	if(mCommands.isEmpty()) {
    		Log.d(TAG, "Queue is empty");
    		
    	}
    	else {
    		
    		QueueEntry command = mCommands.poll();
    		
    		command.getCallback().subscribe(mListener);
    		
    		if(btCurentConnection == null){
    		
	    		btCurentConnection = new BtConnectionTask(mCurentBtSocket);
	    		btCurentConnection.execute();
    		}
    		Log.d(TAG, "sendNextCommand: " + command.getCommand());
    		
    		btCurentConnection.send(command.getCommand(), command.getCallback());
    	}
    }
    
    @Override
    protected BluetoothSocket doInBackground(Void... params) {
            
		Log.d(TAG, "+++++ do in background +++++");

        // Cancel discovery because it will slow down the connection
        mBtAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mBtSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mBtSocket.close();
            } catch (IOException closeException) { }
            
            return null;
        }

        return mBtSocket;
    }
    
    /** Triggered on task cancellation: close the socket. */
    @Override
    protected void onCancelled() {
    	disconnect();
    }

    void disconnect(){
    	Log.d(TAG, "+++++ onCancelled +++++");
        try {
        	
        	btCurentConnection.cancel(true);
        	
        	mBtSocket.close();
                    
            Log.d(TAG, "+++++ socket closed +++++");
        } catch (IOException e) { }
    }
    
    /** Called when the connection has been set up. */
    @Override
    protected void onPostExecute(BluetoothSocket socket) {
    	Log.d(TAG, "+++++ onPostExecute +++++"); 
    	
    	mCurentBtSocket = socket;
    	
    	sendNextCommand();
    }

}

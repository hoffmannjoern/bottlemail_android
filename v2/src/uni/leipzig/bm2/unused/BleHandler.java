package uni.leipzig.bm2.unused;


public class BleHandler {

	//TODO just copied from Version1, added only needed stuff to compile
	
	// debugging
	private static final String TAG = "BleHandler";

//	private BtConnectionTask mBtCurentConnection;
//
//	private static BleHandler mBleHandler = new BleHandler();
//	private BluetoothAdapter mBleAdapter;	
//	private BleHandler(){
//	
//		Log.e( TAG, "+++++ construct BtHandler +++++");
//		mBleAdapter = BluetoothAdapter.getDefaultAdapter();	
//	}
//	
//	
//	public static BleHandler getInstance() {
//		
//		Log.d( TAG, "+++ getInstance +++");
//		return mBleHandler;
//	}
//
//	//scanning for devices
//	public void startDiscovery() throws Exception {
//		
//		if (mBleAdapter.isEnabled()) {
//			mBleAdapter.startDiscovery();
//		}
//		else{
//			throw new Exception();
//		}
//	}
//	
//	//stop scanning for devices
//	public void cancelDiscovery(){
//		
//		if(mBleAdapter != null){
//			if(mBleAdapter.isDiscovering())
//				mBleAdapter.cancelDiscovery();
//		}
//	}
	
	
//	//holt ID der Flasche
//	public void getBottleID(Bottle bottle, final MessageCallback btlDetailsCB){
//			
//		/*Log.d(TAG, "+++++ getBottleID +++++");
//		
//		BtClientTask clientTask = startBtClientTask(bottle);
//		
//		final ConnectionCallback conCB = createConnectionCallback(btlDetailsCB);
//       
//		//Befehl erstellen
//		
//		clientTask.execute(new BtConnectionEstablishedCallback() {
//               @Override
//               public void connectionEstablished(BluetoothSocket socket) {
//               	mBtCurentConnection = new BtConnectionTask(socket, conCB); 
//               	mBtCurentConnection.execute();
//               	//getBottleID
//               	mBtCurentConnection.send("24000221a5fd");
//               	
//               }       
//       });*/
//	}
	
//	//holt angegebene Anzahl an Nachrichten von Flasche 
//	public void getMessages(final Bottle bottle, int numberOfMessages, final MessageCallback messageCallback){
//			
//		Log.d(TAG, "+++++ getMessages +++++");
//		
//		NoMFinishedCallback noMFinishedCallback = new NoMFinishedCallback(){
//			@Override
//			public void incomingData(int numberOfMessages){
//				Log.d(TAG, "numberOfMessages: " + numberOfMessages);
//				
//				for(int i = 1; i<= 3; i++){
//					getMessage(bottle, i, messageCallback);
//				}
//			}
//		};
		
//		NoMConnectionReceiver receiver = new NoMConnectionReceiver(noMFinishedCallback);
//		
//		getNumberOfMessages(bottle, receiver);
//		
//		/*clientTask.execute(new BtConnectionEstablishedCallback() {
//           @Override
//           public void connectionEstablished(BluetoothSocket socket) {
//           	mBtCurentConnection = new BtConnectionTask(socket, conCB); 
//           	mBtCurentConnection.execute();
//           	//getMessages
//           	//mBtCurentConnection.send("240001000021D527", 10);
//           }
//		});*/
//	}
//	
//	private void getNumberOfMessages(Bottle bottle, ConnectionReceiver receiver){
//		
//		BtClientTask clientTask = startBtClientTask(bottle);
//		
//		clientTask.addCommand("2401010000216E3B", receiver);
//	}
//	
//	private void getMessage(Bottle bottle, int id, final MessageCallback messageCallback){
//		
//		BtClientTask clientTask = startBtClientTask(bottle);
//		
//		BmailConnectionReceiver receiver = new BmailConnectionReceiver(id, messageCallback);
//		
//		if(id == 1){
//			clientTask.addCommand("24010200040000000121590C", receiver);
//			clientTask.addCommand("2401030008000000010000000021689C", receiver);
//		}
//		else if(id == 2){
//			clientTask.addCommand("24010200040000000221C23E", receiver);
//			clientTask.addCommand("2401030008000000020000000021A481", receiver);
//		}
//		else if(id == 3){
//			clientTask.addCommand("240102000400000003214B2F", receiver);
//			clientTask.addCommand("2401030008000000030000000021E08A", receiver);
//		}
//		else if(id == 4){
//			clientTask.addCommand("24010200040000000421F45B", receiver);
//			clientTask.addCommand("2401030008000000040000000021E08A", receiver);
//		}
//	}
//	
//	public void sendMessage(Bottle bottle, BMail bMail, MessageCallback composeMsgCB){
//		
//		Log.d(TAG, "+++++ send messages +++++");
//	}
//	
//	private BtClientTask mBtClientTask;
//	
//	private BtClientTask startBtClientTask(Bottle bottle){
//		
//		if(mBtClientTask == null){
//			
//			Log.d(TAG, "+++++ startBtClientTask +++++");
//			
//			BluetoothDevice btDevice = null;
//			
//			if(bottle.getMac() != null)
//				btDevice = mBtAdapter.getRemoteDevice(bottle.getMac());
//			if( btDevice == null){
//				Log.e("getConnectThread", "Kein BluetoothDevice!!");
//			}
//			
//			mBtClientTask = new BtClientTask(btDevice);
//			mBtClientTask.execute();
//		}	
//		
//		return mBtClientTask;
//	}
//	
//	public void disconnect(){
//		if(mBtClientTask != null){
//			mBtClientTask.disconnect();
//			
//			mBtClientTask.cancel(true);
//			mBtClientTask = null;
//		}
//		
//		cancelDiscovery();
//	}
}

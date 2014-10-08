package uni.leipzig.bm2.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;

import android.util.Log;

public class DataTransformer {
	
	final String statusBitHeader = "T";
	final String TAG = "DataTransformer";
	
	byte[] makeHeader(BMail bMail){
		//2 byte
		char authorLength = (char)bMail.getAuthor().length();
		//2 byte
		int titleLength = bMail.getTitle().length();
		//8 byte
		long textLength = bMail.getText().length();
		//8 byte
		long picLength = 0;
		// 1 (statusByte) + 8 (datum) + 2 + 2 +8 + 8 = 29
		int length = 29 + authorLength + titleLength;
		
		
		//Calendar timestamp = Calendar.getInstance();
		
		
		long datum = getDateAsLong(bMail.getTimestamp());
		
		ArrayList<byte[]> headerTeile = new ArrayList<byte[]>();
		
		//1
		headerTeile.add(statusBitHeader.getBytes());
		//8
		headerTeile.add(longToByteArray(datum));
		//2
		headerTeile.add(charToByteArray(authorLength));
		//2
		headerTeile.add(longToByteArray(titleLength));
		//8
		headerTeile.add(longToByteArray(textLength));
		//8
		headerTeile.add(longToByteArray(picLength));
		
		headerTeile.add(bMail.getAuthor().getBytes());
		headerTeile.add(bMail.getTitle().getBytes());
		
		byte[] header = new byte[length];
		int bLength = 0;
		int headerPos = 0;
		for(byte[] b : headerTeile){
			
			
			bLength = b.length;
			
			System.arraycopy(b, 0, header, bLength, bLength);
			headerPos += bLength;
		}
		Log.d("########################### header-length: ",header.length+"");
		Log.d(" ####################### header[0] = ", header[0]+"");
		return header;
	}

	/**
	 * Transformiert ein Array mit Byte-Objekten in einen String.
	 * @param bytes - Array mit Objekten von Typ Byte.
	 * @return - String-Wert des uebergebenen Byte-Array.
	 */
	public static String byteToHex(Byte[] bytes) {
    	//Log.d(TAG, "+++++ byteToHex +++++");
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			new Formatter(sb).format("%02x", b);
		}

		return sb.toString();
	}

	
	/**
	 * Transformiert ein Array mit byte-Weten in einen String.
	 * @param bytes - Array mit Weten von Typ byte.
	 * @return - String-Wert des uebergebenen byte-Array.
	 */
	public static String byteToHex(byte[] bytes) {
    	//Log.d(TAG, "+++++ byteToHex +++++");
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			new Formatter(sb).format("%02x", b);
		}

		return sb.toString();
	}
	
	/**
	 * Transformiert einen Hex-String in ein Array mit Werten vom Typ byte.
	 * @param s - Zeichenkette mit Hex-Werten.
	 * @return - Array mit byte-Repraesentation der uebergegeben Hex-Zeichenkette.
	 */
	public static byte[] hexStringToByteArray(String s) {
		//Log.d(TAG, "+++++ hexStringToByteArray +++++");
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
	
	/**
	 * Transformiert einen Hex-String in einen binaer-String.
	 * @param hex
	 * @return
	 */
	public static String HexToBinaryString(String hex) {
	    int i = Integer.parseInt(hex, 16);
	    String bin = Integer.toBinaryString(i);
	    return bin;
	}
	
	/**
	 * Transformiert einen Hex-String in einen 16bit-Integerwert.
	 * @param hex
	 * @return
	 */
	public static int HexToBinary(String hex) {
	    int bin = Integer.parseInt(hex, 16);	    
	    return bin;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static String getString(Byte[] data){
		byte[] arr = new byte[data.length];
		
		for(int i=0;i<arr.length;i++)
			arr[i] = data[i];
		
		try {
			return new String(arr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static String getString(Byte[] data, int offset, int length){
		
		return getString(Arrays.copyOfRange(data, offset, offset + length));
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static String getHexString(Byte[] data){
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (byte b : data) {
			new Formatter(sb).format("%02x", b);
		}

		return sb.toString();
	}
	
	/**
	 * 
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int getInt(Byte[] data, int offset, int length){
		return getInt(Arrays.copyOfRange(data, offset, offset + length));
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static int getInt(Byte[] data){
		int result = 0;
	    int temp = 0;

	    for( int i = 0; i < data.length; i++ ){
	      temp = data[data.length - 1 - i ];
	      if ( temp < 0 ){        // Das Vorzeichen belegt ja auch ein Bit
	        temp = -temp;
	        temp += 1 << 7;   // Belegt das 1. Bit
	      }
	      temp <<= i * 8;   // gleichbedeutend mit    temp = temp * 2^(i * 8)
	      result += temp;
	    }
	    return result;
	}
	
	static long byteArrayToLong(byte[] data){
		
		return ByteBuffer.wrap(data).getLong();
	}
	
	
	static byte[] longToByteArray(long data){
		
		ByteBuffer b = ByteBuffer.allocate(8);
		b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putLong(data);
			
		return b.array();
			
	}
	
	static byte[] charToByteArray(char data){
		
		ByteBuffer b = ByteBuffer.allocate(2);
		b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putChar(data);
			
		return b.array();
			
	}
	
	static byte[] shortToByteArray(short i){
		
		ByteBuffer b = ByteBuffer.allocate(2);
		b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putShort(i);
			
		return b.array();
			
	}
	
	static byte[] intToByteArray(int data){
		
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putInt(data);
			
		return b.array();
			
	}
	
	
	private static long getDateAsLong(Calendar timeStamp){
		
		String month;
		String day;
		if(timeStamp.get(timeStamp.MONTH)<10){
			month = "0"+timeStamp.get(timeStamp.MONTH);
		}
		else{
			month = ""+timeStamp.get(timeStamp.MONTH);
		}
		if(timeStamp.get(timeStamp.DAY_OF_MONTH)<10){
			day = "0"+timeStamp.get(timeStamp.DAY_OF_MONTH);
		}
		else{
			day = ""+timeStamp.get(timeStamp.DAY_OF_MONTH);
		}
		String date = timeStamp.get(timeStamp.YEAR) + month + day;
		
		long datum = Long.parseLong(date);
		
		return datum;
	}
}
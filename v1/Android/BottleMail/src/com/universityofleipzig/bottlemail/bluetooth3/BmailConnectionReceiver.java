package com.universityofleipzig.bottlemail.bluetooth3;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.universityofleipzig.bottlemail.data.DataTransformer;

public class BmailConnectionReceiver extends ConnectionReceiver {
	
	private static final String TAG = "BmailConnectionReceiver";
	
	private int mId = -1;
	private String mAuthor;
	private String mTitle;
	private int mTextLength = -1;
	private int mPictureLength = -1;
	private String mText;
	
	private MessageCallback mMessageCallback;
	
	public BmailConnectionReceiver(int id, MessageCallback messageCallback){
		mId = id;
		mMessageCallback = messageCallback;
	}

	@Override
	public void incomingData(Byte[] data){
		
		//Log.d(TAG, "incomingData: " + DataTransformer.byteToHex(data));
		
		if(data.length >= 3){
			if(data[1] ==  1 && data[2] == 2){
				getMessageHeader(data);
			}
			else if(data[1] ==  1 && data[2] == 3){
				getMessageBody(data);
			}
			else if(data[1] == -1 && data[2] == -1){
				getError(data);
			}
		}
	}
	
	private void getMessageHeader(Byte[] data){

		//Log.d(TAG, "getMessageHeader: " + DataTransformer.byteToHex(data));
		
		//Log.d(TAG, "getMessageHeader");
		if(data.length >= 16){
			int authorLength = data[14];
			int titleLength = data[15];
			
			if(data.length == (33 + authorLength + titleLength)){
				if(data[32 + authorLength + titleLength] == 33){
					
					Log.d(TAG, "imcomingData: " + DataTransformer.byteToHex(data));
					
					//Log.d(TAG, "incomingString: " + DataTransformer.getString(data));
					
					mTextLength = DataTransformer.getInt(data, 16, 8);
					
					mPictureLength = DataTransformer.getInt(data, 24, 8);
					
					Log.d(TAG, "authorLength: " + authorLength);
					Log.d(TAG, "titleLength: " + titleLength);
					Log.d(TAG, "mTextLength: " + mTextLength);
					Log.d(TAG, "mPictureLength: " + mPictureLength);
					
					//Log.d(TAG, DataTransformer.byteToHex(Arrays.copyOfRange(data, 16, 24)));
					//Log.d(TAG, "mTextLength: " + DataTransformer.byteToHex(Arrays.copyOfRange(data, 16, 24)));
					//Log.d(TAG, "mTextLength: " + mTextLength);
					
					mAuthor = DataTransformer.getString(data, 32, authorLength);
					
					mTitle = DataTransformer.getString(data, 32 + authorLength, titleLength);
					
					//Author: " + mAuthor);
					//Log.d(TAG, "Title: " + mTitle);
					
					notifyListener();
				}
			}
		}
	}
	
	private void getMessageBody(Byte[] data){
		if(data.length >= 5){
			
			//Log.d(TAG, "textLength: " + DataTransformer.byteToHex(data));
			
			//Log.d(TAG, "textLength: " + DataTransformer.byteToHex(Arrays.copyOfRange(data, 3, 5)));
			int textLength = mTextLength;
			//int textLength = DataTransformer.getInt(Arrays.copyOfRange(data, 3, 5));
			
			//Log.d(TAG, "textLength: " + textLength);
			
			//Log.d(TAG, "imcomingData: " + DataTransformer.byteToHex(data));
			
			if(data.length == 6 + textLength){
				if(data[5 + textLength] == 33){
					
					mText = DataTransformer.getString(data, 5, textLength);
					
					Log.d(TAG, "Author: " + mAuthor + " Title: " + mTitle + " Text: " + mText);
					
					notifyListener();
					
					mMessageCallback.incomingData(mId, mAuthor, mTitle, mText);
				}
			}
		}
	}
	
	private void getError(Byte[] data){
		
		if(data.length == 10){
			
			if(data[7] == 33){
				Log.e(TAG, "ERROR: " + DataTransformer.byteToHex(data));
			}
		}
	}
}

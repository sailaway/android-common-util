package com.adf.util;

import android.util.Log;

public class LogUtil {

	public static String TAG = "android-dev-framework";
	
	public static final void err(String msg){
		Log.e(TAG, msg);
	}
	public static final void log(String msg){
		Log.v(TAG, msg);
	}
	
}

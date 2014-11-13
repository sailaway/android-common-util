/**
 * Copyright 2014 sailaway(https://github.com/sailaway)
 *
 * Licensed under theGNU GENERAL PUBLIC LICENSE Version 3 (the "License");
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 * 
 */
package com.adf.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SystemUtil {

	/**
	 * this function will call system default call operation
	 * if you use this function to call phone,you must add permission:
	 * <uses-permission android:name="android.permission.CALL_PHONE"></uses-permission>
	 * in your manifest file
	 * */
	public static void callPhoneDirect(Context context,String phoneNumber) {
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_CALL);
		String str = "tel:"+phoneNumber;
		intent.setData(Uri.parse(str));
		context.startActivity(intent);
	}
	
	/**
	 * will call display the call panel,and the user can edit the number before really call 
	 * this function use no permission.
	 * */
	public static void callDialPanel(Context context,String phoneNumber) {
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		String str = "tel:"+phoneNumber;
		intent.setData(Uri.parse(str));
		context.startActivity(intent);
	}
}

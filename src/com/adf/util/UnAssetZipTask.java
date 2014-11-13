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

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;

/**
 * extra asset zip file to assign path
 * */
public class UnAssetZipTask extends AsyncTask<String, String, Boolean>{
	private Context context;
	private String extraPath ;
	
	public UnAssetZipTask(Context context,String path){
		this.context = context;
		if(!path.endsWith(File.separator)){
			this.extraPath = path + File.separator;
		} else {
			this.extraPath = path;
		}
	}

	@Override
	protected Boolean doInBackground(String... params) {
		for(String is : params){
			String path = AssertUtil.offlinePanoResource(context,extraPath,is);
			publishProgress(path);
		}
		return true;
	}
	
}

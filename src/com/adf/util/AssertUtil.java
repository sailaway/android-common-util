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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class AssertUtil {

	public static String offlinePanoResource(Context context,String extraPath,String asset){
		AssetManager am = context.getAssets();
		InputStream is = null;
		try {
			is = am.open(asset);
			boolean ret = ZipUtil.unzipEnhance(is, extraPath, false);
			if(ret){
				return extraPath + asset;
			}
		} catch (IOException e) {
			return null;
		} finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}

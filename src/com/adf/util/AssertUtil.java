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

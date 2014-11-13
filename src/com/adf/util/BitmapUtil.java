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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

public class BitmapUtil {
	
	public static boolean saveBitmap(Bitmap bitmap,String filepath){
		File f = new File(filepath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(f);
			
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (FileNotFoundException e) {
			return false;
		} finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
		
		return true;
	}

	public static Bitmap converttoBitmap565(Bitmap b){
		if(b == null){
			return null;
		}
		if(b.getConfig() == Bitmap.Config.RGB_565){
			return b;
		}
		Bitmap b565 = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.RGB_565);
		Canvas c = new Canvas(b565);
		c.drawBitmap(b, 0, 0, null);
		return b565;
	}
	
	public static Point getBitmapSize(InputStream is){
		BitmapFactory.Options opt = new BitmapFactory.Options();  
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        int x = opt.outWidth;  
        int y = opt.outHeight;
        Point p = new Point(x,y);
        return p;
	}
	
	public static Point getBitmapSize(String filepath){
		File f = new File(filepath);
		Point p = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			p = getBitmapSize(fs);
		} catch (FileNotFoundException e) {
		}finally{
			if(fs != null){
				try {
					fs.close();
				} catch (IOException e) {
				}
			}
		}
		return p;
	}
	
	public static Bitmap getResizedImageBitmap(InputStream input,int downscale){
        BitmapFactory.Options options = new BitmapFactory.Options();
        //returning a smaller image to save memory.
        options.inSampleSize = downscale;
        Bitmap b = BitmapFactory.decodeStream(input, null, options);//注意看options的用法
        return b;
	}
	
	public static Bitmap getResizedImageBitmap(String filepath,int downscale){
		File f = new File(filepath);
		FileInputStream fs = null;
		Bitmap b = null;
		try {
			fs = new FileInputStream(f);
			b = getResizedImageBitmap(fs,downscale);
		} catch (FileNotFoundException e) {
		}finally{
			if(fs != null){
				try {
					fs.close();
				} catch (IOException e) {
				}
			}
		}
		return b;
	}
	
	public static int downsampleLimitSize(Point size,int limit_size){
		final int width = size.x;
		final int height = size.y;
		final int byteperpixel = 4;
		
		int downsample = 1;
		
		int org_size = width * height * byteperpixel;
		int downsize = org_size;
		while(downsize > limit_size){
			downsize = org_size / (++downsample);
		}
		return downsample;
	}
	
	public static Bitmap getResizedImageBitmap(InputStream input,int widthLimit, int heightLimit){
		Point size = getBitmapSize(input);
		if(size == null || size.x <=0 || size.y <=0){
			return null;
		}
		
        int outWidth = size.x;
        int outHeight = size.y;
        int s = 1;
        while ((outWidth / s > widthLimit) || (outHeight / s > heightLimit)) {
        	++s;
        }
        return getResizedImageBitmap(input, s);
	}
	
	public static Bitmap getResizedImageBitmap(String filepath,int widthLimit, int heightLimit){
		File f = new File(filepath);
		FileInputStream fs = null;
		Bitmap b = null;
		try {
			fs = new FileInputStream(f);
			b = getResizedImageBitmap(fs,widthLimit,heightLimit);
		} catch (FileNotFoundException e) {
		}finally{
			if(fs != null){
				try {
					fs.close();
				} catch (IOException e) {
				}
			}
		}
		return b;
	}
	
}

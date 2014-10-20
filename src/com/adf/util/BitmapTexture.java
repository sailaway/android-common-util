package com.adf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Point;

public class BitmapTexture {
	
	public static final boolean xis2n(int i){
    	if(i > 0 && (i & (i-1)) == 0){
    		return true;
    	}
    	return false;
    }
	
	/**
	 * for example ; base = 2,i = 456,will return 512
	 * 				 base = 2,i = 1024, will return 1024
	 * 				 base = 3,i = 334, will return 729
	 * */
	public static int ceilPower(int base,int i){
		double log =  Math.log(i)/Math.log(base);
		int ceil = (int) (log + 0.5);
		
		int ceil_value = (int) Math.pow(base, ceil);
		return ceil_value;
	}
	
	/**
	 * for example ; base = 2,i = 456,will return 256
	 * 				 base = 2,i = 1024, will return 1024
	 * 				 base = 3,i = 334, will return 243
	 * 
	 * */
	public static int floorPower(int base,int i){
		double log =  Math.log(i)/Math.log(base);
		int floor = (int)log;
		
		int floor_value = (int) Math.pow(base, floor);
		return floor_value;
	}
	
	public static int filter_img_min_edge = 128;
	public static int filter_img_min_size = 256*256;
	public static int max_read_image_size_kb = 8 * 1024;//default read a bitmap that pixel smaller than max_read_image_size_kb,if picture is too big,then downsample
	
	public static final int clip_height_from_top = 0;
	public static final int clip_height_from_center = 1;
	public static final int clip_height_from_bottom = 2;
	
	
	/**
	 * @param maxTexturesize ,in kb;
	 * */
	public static Bitmap decodeBitmapLimitTextureSize(String filepath,int maxTexturesize){
		Point size_point = BitmapUtil.getBitmapSize(filepath);
		Bitmap b = null;
		int downscale = downSampleLimitTextureSize(size_point,maxTexturesize);
    	b = BitmapUtil.getResizedImageBitmap(filepath, downscale);
    	return b;
	}
	
	/**
	 * @param maxTexturesize ,in kb;
	 * */
	public static Bitmap decodeBitmapLimitTextureSize(InputStream is,int maxTexturesize){
		Point size_point = BitmapUtil.getBitmapSize(is);
		Bitmap b = null;
		int downscale = downSampleLimitTextureSize(size_point,maxTexturesize);
    	b = BitmapUtil.getResizedImageBitmap(is, downscale);
    	return b;
	}
	
	/**
	 * @param maxTexturesize ,in kb;
	 * */
	public static int downSampleLimitTextureSize(Point size_point,int maxTexturesize){
		final int byteperpixel = 4;
		
		int downscale = 1;
		int size_kb = ((size_point.x * size_point.y * byteperpixel) >> 10);
    	if(size_kb > maxTexturesize){
    		do{
    			downscale *= 2;
    			size_kb = (size_kb /(downscale * downscale));
    		}while(size_kb > maxTexturesize);
    	}
    	return downscale;
	}
	
	public interface IStream{
		public InputStream refreshInputStream();
	}
	
	/**
	 * 1,keep the all horizontal content no loss;
	 * 2,bitmap width and height must be the power of 2
	 * 3,can scale and resize,but keep principle 1;
	 * 
	 *  @param maxTexturesize ,in kb;
	 * */
	@SuppressWarnings("resource")
	public static Bitmap transBitmapFitTexture(IStream stream,boolean filterlittle,int height_clip_type,int max_texture_size){
		if(stream == null){
			return null;
		}
		InputStream input ;
		
		Point orig_size;
		
		input = stream.refreshInputStream();
		if(input == null){
			return null;
		}
		orig_size = BitmapUtil.getBitmapSize(input);
		
		try {
			input.close();
		} catch (IOException e) {
			//ignore
		}
		
		int img_w = orig_size.x;
		int img_h = orig_size.y;
		
		if(xis2n(img_w) && xis2n(img_h)){
			input = stream.refreshInputStream();
			if(input == null){
				return null;
			}
			Bitmap b = decodeBitmapLimitTextureSize(input,max_texture_size);
			try {
				input.close();
			} catch (IOException e) {
				//ignore
			}
			return b;
		}
		//if filter ,then if a pic is too small ,just return null
		if(filterlittle){
			if(img_w < filter_img_min_edge || img_h < filter_img_min_edge 
					|| img_w * img_h < filter_img_min_size){
				return null;
			}
		}
		
		int downscale = BitmapUtil.downsampleLimitSize(new Point(img_w,img_h),max_read_image_size_kb * 1024);
//		int downscale = downSampleLimitTextureSize(new Point(img_w,img_h),max_read_image_size_kb);
		
		img_w /= downscale;
		img_h /= downscale;
		
		int ceil_width = ceilPower(2, img_w);//destination width
		double scale = (double)ceil_width / img_w;
		int scale_height = (int) (scale * img_h);
		
		int clip_height = floorPower(2, scale_height);
		
		input = stream.refreshInputStream();
		if(input == null){
			return null;
		}
		Bitmap orig = BitmapUtil.getResizedImageBitmap(input, downscale);
		
		try {
			input.close();
		} catch (IOException e) {
			//ignore
		}
		
		if(orig == null){
			return null;
		}
		
		orig = BitmapUtil.converttoBitmap565(orig);
		if(orig == null){
			return null;
		}
		
		orig = Bitmap.createScaledBitmap(orig, ceil_width, scale_height, true);
		if(orig == null){
			return null;
		}
		
		int py = 0;
		switch(height_clip_type){
		case clip_height_from_top:
			py = 0;
			break;
		case clip_height_from_center:
			py = ((scale_height - clip_height) >>1);
			if(py < 0 ){
				py = 0;
			}
			break;
		case clip_height_from_bottom:
			py = scale_height - clip_height;
			if(py < 0 ){
				py = 0;
			}
			break;
		}
		
		Bitmap dst = Bitmap.createBitmap(orig, 0, py, ceil_width, clip_height);
		return dst;
	}

	/**
	 * 1,keep the all horizontal content no loss;
	 * 2,bitmap width and height must be the power of 2
	 * 3,can scale and resize,but keep principle 1;
	 * 
	 *  @param maxTexturesize ,in kb;
	 * */
	public static Bitmap transBitmapFitTexture(String filepath,boolean filterlittle,int height_clip_type,int max_texture_size){
		File f = new File(filepath);
		if(!f.exists() || !f.canRead()){
			return null;
		}
		Point orig_size;
		orig_size = BitmapUtil.getBitmapSize(filepath);
		
		final int img_w = orig_size.x;
		final int img_h = orig_size.y;
		
		if(xis2n(img_w) && xis2n(img_h)){
			return decodeBitmapLimitTextureSize(filepath,max_texture_size);
		}
		//if filter ,then if a pic is too small ,just return null
		if(filterlittle){
			if(img_w < filter_img_min_edge || img_h < filter_img_min_edge 
					|| img_w * img_h < filter_img_min_size){
				return null;
			}
		}
		
		int ceil_width = ceilPower(2, img_w);//destination width
		double scale = (double)ceil_width / img_w;
		int scale_height = (int) (scale * img_h);
		
		int downscale = downSampleLimitTextureSize(new Point(ceil_width,scale_height),max_texture_size);
		ceil_width /= downscale;
		scale_height /= downscale;
		int clip_height = floorPower(2, scale_height);
		
		Bitmap orig = BitmapUtil.getResizedImageBitmap(filepath, downscale);
		orig = Bitmap.createScaledBitmap(orig, ceil_width, scale_height, true);
//		BitmapUtil.saveBitmap(orig, "/sdcard/Pano.360/debug.jpg");
		if(orig == null){
			return null;
		}
		orig = BitmapUtil.converttoBitmap565(orig);
		if(orig == null){
			return null;
		}
		
		int py = 0;
		switch(height_clip_type){
		case clip_height_from_top:
			py = 0;
			break;
		case clip_height_from_center:
			py = ((scale_height - clip_height) >>1);
			if(py < 0 ){
				py = 0;
			}
			break;
		case clip_height_from_bottom:
			py = scale_height - clip_height;
			if(py < 0 ){
				py = 0;
			}
			break;
		}
		
		Bitmap dst = Bitmap.createBitmap(orig, 0, py, ceil_width, clip_height);
		return dst;
	}
}

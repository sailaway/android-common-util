package com.adf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.util.Log;

public class FileUtil {
	private final static String TAG = "FileUtil";

	/**
	 * actual file copy
	 * */
	private static boolean _copyFile(File src_f,File dst_f){
		int buffersize = 4*1024;
		FileInputStream fi = null;
		FileOutputStream fo = null ;
		FileChannel cfi = null;
		FileChannel cfo = null;
		ByteBuffer buffer = ByteBuffer.allocateDirect(buffersize);;
		int count = buffersize;
		long left = 0;
		try {
			fi = new FileInputStream(src_f);
			fo = new FileOutputStream(dst_f);
			cfi = fi.getChannel();
			cfo = fo.getChannel();
			
			while((left = cfi.size() - cfi.position())>0){
				if(left < buffersize){
					count = (int) left;
					buffer = ByteBuffer.allocateDirect(count);
				}
				cfi.read(buffer);
				buffer.flip();
				cfo.write(buffer);
				cfo.force(false);
				buffer.clear();
			}
			
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}finally{
			if(cfi != null){
				try {
					cfi.close();
				} catch (IOException e) {
					return false;
				}
			}
			if(cfo != null){
				try {
					cfo.close();
				} catch (IOException e) {
					return false;
				}
			}
			if(fi != null){
				try {
					fi.close();
				} catch (IOException e) {
					return false;
				}
			}
			if(fo != null){
				try {
					fo.flush();
					fo.close();
				} catch (IOException e) {
					return false;
				}
			}
		}
		Log.v(TAG, "copy file "+ dst_f.getAbsolutePath());
		return true;
	}

	/**
	 * before you call this function ,please ensure that the src file is not a dir <br>
	 * 
	 * @param cover; indicate what to do if dst file exist.
	 * 			 if set to true ,will overwrite the exist file
	 * 			 if set to false ,while the dst file exist,will return false
	 * @param createdir; indicate what to do if dst file parent dir is not exist
	 * 			if set to true ,will create dirs
	 * 			else ,will return false
	 * 
	 * @return if src file is not exist,or copy failed return false;
	 * 		   if copy success,return true
	 * */
	public static boolean copyFile(String src,String dst,boolean overwrite,boolean createdirs){
		if(src == null || dst == null){
			return false;
		}
		if(src.length() <=0 || dst.length() <=0){
			return false;
		}
		if(src.equals(dst)){
			return true;
		}
		File src_f = new File(src);
		if(!src_f.exists()){
			return false;
		}
		if(!src_f.isFile()){
			return false;
		}
		File dst_f = new File(dst);
		if(!overwrite && dst_f.exists()){
			return false;
		}
		File dst_pf = dst_f.getParentFile();
		if(!dst_pf.exists()){
			if(createdirs){
				boolean success = dst_pf.mkdirs();
				if(!success){
					return false;
				}
			} else {
				return false;
			}
		}
		
		return _copyFile(src_f,dst_f);
	}
	
	private static boolean _copyFile(InputStream fi,OutputStream fo){
		int buffersize = 4*1024;
		byte[] buffer = new byte[buffersize];
		int read = 0;
		try {
			while((read = fi.read(buffer))>0){
				fo.write(buffer, 0, read);
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}finally{
			if(fi != null){
				try {
					fi.close();
				} catch (IOException e) {
					return false;
				}
			}
			if(fo != null){
				try {
					fo.flush();
					fo.close();
				} catch (IOException e) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean copyFile(InputStream is,String dst,boolean overwrite,boolean createdirs){
		if(is == null){
			return false;
		}
		File dst_f = new File(dst);
		if(!overwrite && dst_f.exists()){
			return false;
		}
		File dst_pf = dst_f.getParentFile();
		if(!dst_pf.exists()){
			if(createdirs){
				boolean success = dst_pf.mkdirs();
				if(!success){
					return false;
				}
			} else {
				return false;
			}
		}
		try {
			FileOutputStream os = new FileOutputStream(dst_f);
			return _copyFile(is,os);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * actual copy 
	 * */
	private static boolean _copy(File src_f,File dst_f,boolean overwrite){
		if(src_f.isDirectory()){
			boolean ret ; 
			if(!dst_f.exists()){
				ret = dst_f.mkdir();
				if(!ret){
					return false;
				} else {
					Log.v(TAG, "mkdir "+ dst_f.getAbsolutePath());
				}
			}
			
			File[] children = src_f.listFiles();
			for(File child : children){
				File child_dst = new File(dst_f,child.getName());
				ret = _copy(child,child_dst,overwrite);
				if(!ret){
					return false;
				}
			}
		} else {
			if(!overwrite){
				if(dst_f.exists()){
					return true;
				}
			}
			return _copyFile(src_f,dst_f);
		}
		
		return true;
	}
	
	/**
	 *  copy file or directory <br>
	 * if src is a file see {@link copyFile}
	 * 
	 * @param merge ;just for directory,if set false,and the dst directory is exist,return false;
	 * 			if set true,then will merge the directory
	 * 
	 * @param overwrite ;if copying file ,see {@link copyFile} <br>
	 * 				if copying directory,set true will overwrite the exist directory same path file,else otherwise
	 * 			if copy directory,then will merge the directory
	 * @param createdirs ;whether create dst parent directory
	 * 
	 * 
	 * */
	public static boolean copy(String src,String dst,boolean merge,boolean overwrite,boolean createdirs){
		File src_f = new File(src);
		if(src_f.isFile()){
			return copyFile(src, dst, overwrite, createdirs);
		}
		
		if(src == null || dst == null){
			return false;
		}
		if(src.length() <=0 || dst.length() <=0){
			return false;
		}
		if(src.equals(dst)){
			return true;
		}
		if(!src_f.exists()){
			return false;
		}
		File dst_f = new File(dst);
		
		if(!merge && dst_f.exists()){
			return false;
		}
		File dst_pf = dst_f.getParentFile();
		if(!dst_pf.exists()){
			if(createdirs){
				boolean success = dst_pf.mkdirs();
				if(!success){
					return false;
				}
			} else {
				return false;
			}
		}
		return _copy(src_f,dst_f,overwrite);
	}
	
	/**
	 * if src is a file will call {@link copyFile}(src,dst,true,true);
	 * just call {@link copy}(src,dst,true,true,true)
	 *  
	 * */
	public boolean copyDirectoryOrFile(String src,String dst){
		File src_f = new File(src);
		if(src_f.isFile()){
			return copyFile(src, dst, true, true);
		} else {
			return copy(src, dst, true, true, true);
		}
	}
}

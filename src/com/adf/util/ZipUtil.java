package com.adf.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
//	private final static String TAG = "ZipUtil";

	/**
	 * @deprecated
	 * */
	public static boolean unzip(InputStream zipFileName, String outputDirectory) {
        try {
            ZipInputStream in = new ZipInputStream(zipFileName);
            /**
             * 获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry＄1�7
             * 当getNextEntry方法的返回�1�7�为null，则代表ZipInputStream中没有下丄1�7个ZipEntry＄1�7
             *  输入流读取完成；
             * */
            ZipEntry entry = in.getNextEntry();
            while (entry != null) {

                /**
                 * 创建以zip包文件名为目录名的根目录
                 * */
                File file = new File(outputDirectory);
                file.mkdir();
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);//this code is to remove the directory's '/' character?
              
                    file = new File(outputDirectory + File.separator + name);
                    file.mkdir();
               
                } else {
                    file = new File(outputDirectory + File.separator + entry.getName());
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    out.close();
                }
                /**
                 * 读取下一个ZipEntry
                 * */
                entry = in.getNextEntry();
            }
            in.close();
        } catch (FileNotFoundException e) {
        	return false;
        } catch (IOException e) {
        	return false;
        }
        return true;
    }
	
	public static boolean unzipEnhance(InputStream zipFileName, String outputDirectory,boolean overwrite) {
        try {
            ZipInputStream in = new ZipInputStream(zipFileName);
            /**
             * 获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry＄1�7
             * 当getNextEntry方法的返回�1�7�为null，则代表ZipInputStream中没有下丄1�7个ZipEntry＄1�7
             *  输入流读取完成；
             * */
            ZipEntry entry = in.getNextEntry();
            
            int buffersize = 4*1024;
            byte[] buffer = new byte[buffersize];
            int read = 0;
            /**
             * 创建以zip包文件名为目录名的根目录
             * */
            File file = new File(outputDirectory);
            file.mkdir();
            while (entry != null) {
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    //name = name.substring(0, name.length() - 1);//this code is to remove the directory's '/' character?
              
                    file = new File(outputDirectory + File.separator + name);
                    if(!file.exists()){
//                    	Log.v(TAG, "mkdir "+file.getAbsolutePath());
                    	file.mkdir();
                    }
               
                } else {
                    file = new File(outputDirectory + File.separator + entry.getName());
                    
                    if(file.exists()){
                		if(!overwrite){
                			entry = in.getNextEntry();
                			continue;
                		}
                	} else {
                		/*zip file 是随机遍历，有可能子文件读取了，但是其所在目录还没有创建*/
                        File pf = file.getParentFile();
                        if(pf != null && !pf.exists()){
                        	pf.mkdirs();
                        }
                	}
                    
                    FileOutputStream out = new FileOutputStream(file);
                    while ((read = in.read(buffer)) >0) {
                        out.write(buffer, 0, read);
                    }
                    out.close();
//                    Log.v(TAG, "write file: "+file.getAbsolutePath());
                }
                /**
                 * 读取下一个ZipEntry
                 * */
                entry = in.getNextEntry();
            }
            in.close();
        } catch (FileNotFoundException e) {
        	return false;
        } catch (IOException e) {
        	return false;
        }
        return true;
    }
	
}
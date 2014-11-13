/**
 * Copyright 2014 sailaway(https://github.com/sailaway)
 *
 * Licensed under theGNU GENERAL PUBLIC LICENSE Version 3 (the "License");
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 * 
 */
package com.adf.bean;

import java.io.File;

import android.content.Context;

import com.litl.leveldb.DB;

/**
 * */
public class DBInstance {
	public static String LevelDBName = "level.db";
	private static DB db;
	private DBInstance(){}
	public static final DB getInstance(File file){
		if(db == null){
			db = new DB(file);
			db.open();
		}
		return db;
	}
	public static final DB getInstance(Context context){
		File file = context.getDatabasePath(LevelDBName);
		file.getParentFile().mkdirs();
		return getInstance(file);
	}
	
	public static final DB reopenInstance(Context context){
		File file = context.getDatabasePath(LevelDBName);
		file.getParentFile().mkdirs();
		return reopenInstance(file);
	}
	public static final DB reopenInstance(File file){
		destroyInstance();
		return getInstance(file);
	}
	
	public static final void destroyInstance(){
		if(db != null){
			db.close();
			db = null;
		}
	}
}

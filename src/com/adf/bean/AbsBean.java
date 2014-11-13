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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.adf.bean.annotation.AdfDbColumn;
import com.adf.bean.annotation.AdfJsonColumn;
import com.adf.util.LogUtil;
import com.litl.leveldb.DB;

/**
 * columns Must be int,float,double,short this basic type or String type 
 * 
 * columns that want to save db, please add AdfDbColumn annotation
 * 
 * columns that want to convert to json object,please add AdfJsonColumn annotation
 * 
 * member variable now full SUPPORT String,char,boolean,byte,int,long,float,double,short type.
 * 
 * member variable can be Array but NOT support List
 * array column NOT support multiple dimension,all array column MUST be single dimension
 * 
 * self define type column MUST REWRITE toString function AND define a constructor with a String parameter
 * 
 * array column is deal apart from type column
 * */
public abstract class AbsBean {
	
	protected String identify;
	
	public AbsBean() {
		identify = UUID.randomUUID().toString();
	}
	
	public AbsBean(String id){
		identify = id;
	}
	
	public String tableName(){
		String name = getClass().getSimpleName();
		return name;
	}
	public String getIdentify(){
		return identify;
	}
	
	public String joinKeyWithColumnAndIndentify(String column){
		return joinKeyWithColumnAndIndentify(column, identify);
	}
	public String joinKeyWithColumnAndIndentify(String column,String id){
		return tableName() + column + id;
	}
	
	public String joinKeyWithColumn(String column){
		return tableName() + column;
	}
	
	public String getAssignColumnAssignIdValue(DB db,String column,String id){
		String key = joinKeyWithColumnAndIndentify(column,id);
		return db.get(key);
	}
	
	public AbsBean loadFromDbById(DB db,String id) {
		this.identify = id;
		List<String> columns = dbColumns();
		for(int i = 0;i<columns.size();i++){
			String column = columns.get(i);
			String dbKey = joinKeyWithColumnAndIndentify(column);
			String value = db.get(dbKey);
			setValueByColumn(column,value);
		}
		return this;
	}
	
	public List<String> dbColumns(){
		Field[] fields = getClass().getDeclaredFields();
		
		List<String> cols = new ArrayList<String>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(AdfDbColumn.class)) {
				cols.add(field.getName());
			}
		}
		return cols;
	}
	
	/**
	 * IMPORT
	 * */
	public void setValueByOject(String col,Object obj) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
		Field f;
		f = getClass().getDeclaredField(col);
		f.setAccessible(true);
		f.set(this, obj);
	}
	
	private Class<?> getArrayObjectClass(Class<?> arrayCls){
		String arrClsName = arrayCls.getName();
		String clsName = arrClsName.replace("[L", "");
		clsName = clsName.replace("[", "");
		clsName = clsName.replace(";", "");
		if(clsName.equals("Z")){
			return boolean.class;
		} else if(clsName.equals("B")){
			return byte.class;
		} else if(clsName.equals("C")){
			return char.class;
		} else if(clsName.equals("S")){
			return short.class;
		} else if(clsName.equals("I")){
			return int.class;
		} else if(clsName.equals("J")){
			return long.class;
		} else if(clsName.equals("F")){
			return float.class;
		} else if(clsName.equals("D")){
			return double.class;
		}
		Class<?> cls;
		try {
			cls = Class.forName(clsName);
			return cls;
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
	
	/**
	 * IMPORT
	 * */
	private void setArrayColumn(String col,JSONArray arr) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
		Field f;
		f = getClass().getDeclaredField(col);
		f.setAccessible(true);
		Class<?> arrCls = f.getType();
		Class<?> objCls = getArrayObjectClass(arrCls);
		if(objCls != null){
			Object array = Array.newInstance(objCls, arr.length());
			for (int i = 0; i < arr.length(); i++) {
				Object oi = arr.opt(i);
				if(oi.getClass() == objCls){					
					Array.set(array, i, arr.opt(i));
				} else {
					Constructor<?> cons;
					try {
						cons = objCls.getDeclaredConstructor(String.class);
						cons.setAccessible(true);
						Object obj = cons.newInstance(oi.toString());
						Array.set(array, i, obj);
					} catch (NoSuchMethodException e) {
						LogUtil.err("setArrayColumn NoSuchMethodException, col " + col + " :"+e.getMessage());
					} catch (InstantiationException e) {
						LogUtil.err("setArrayColumn InstantiationException, col " + col + " :"+e.getMessage());
					} catch (InvocationTargetException e) {
						LogUtil.err("setArrayColumn InvocationTargetException, col " + col + " :"+e.getMessage());
					}
				}
			}
			f.set(this, array);
		} else {
			throw new IllegalArgumentException("Can not get Array Column Object class");
		}
	}
	
	/**
	 * IMPORT
	 * */
	public void setValueByColumn(String col,String val) throws IllegalArgumentException{
		try {
			Field f = getClass().getDeclaredField(col);
			Class<?> cls = f.getType();
			f.setAccessible(true);
			if(cls.isArray()){
				JSONArray arr;
				try {
					arr = new JSONArray(val);
					setArrayColumn(col, arr);
				} catch (JSONException e) {
				}
				return;
			}
			if ((int.class == cls) || (Integer.class == cls)) {
				int ival = Integer.parseInt(val);
				f.set(this, ival);
			} else if ((long.class == cls) || (Long.class == cls)) {
				long lval = Long.parseLong(val);
				f.set(this, lval);
			} else if ((float.class == cls) || (Float.class == cls)) {
				float fval = Float.parseFloat(val);
				f.set(this, fval);
			} else if ((short.class == cls) || (Short.class == cls)) {
				short sval = Short.parseShort(val);
				f.set(this, sval);
			} else if ((double.class == cls) || (Double.class == cls)) {
				double dval = Double.parseDouble(val);
				f.set(this, dval);
			} else if ((byte.class == cls) || (Byte.class == cls)) {
				byte bval = Byte.parseByte(val);
				f.set(this, bval);
			} else if ((boolean.class == cls) || (Boolean.class == cls)) {
				boolean bval = Boolean.parseBoolean(val);
				f.set(this, bval);
			}  else if (char.class == cls) {
				char cval = val.charAt(0);
				f.set(this, cval);
			} else {
				Constructor<?> cons = cls.getDeclaredConstructor(String.class);
				cons.setAccessible(true);
				Object obj = cons.newInstance(val);
				f.set(this, obj);
			}
		} catch (NoSuchFieldException e) {
			LogUtil.err("setValueByColumn NoSuchFieldException, col " + col + " not exist!!!");
		} catch (IllegalAccessException e) {
			LogUtil.err("setValueByColumn IllegalAccessException, col " + col + " :"+e.getMessage());
			//throw e;
		} catch (IllegalArgumentException e) {
			LogUtil.err("setValueByColumn IllegalArgumentException, col " + col + " :"+e.getMessage());
			//throw e;
		} catch (NoSuchMethodException e) {
			LogUtil.err("setValueByColumn NoSuchMethodException, col " + col + " :"+e.getMessage());
			//throw e;
		} catch (InstantiationException e) {
			LogUtil.err("setValueByColumn InstantiationException, col " + col + " :"+e.getMessage());
			//throw e;
		} catch (InvocationTargetException e) {
			LogUtil.err("setValueByColumn InvocationTargetException, col " + col + " :"+e.getMessage());
			//throw e;
		}
	}
	
	private JSONArray arrayObjectToJson(Object val){
		int count = Array.getLength(val);
		JSONArray arr = new JSONArray();
		for (int i = 0; i < count; i++) {
			Object obj = Array.get(val, i);
			arr.put(obj);
		}
		return arr;
	}
	
	public String columnValueToString(Object obj){
		if(obj.getClass().isArray()){
			JSONArray arr = arrayObjectToJson(obj);
			return arr.toString();
		}
		return obj.toString();
	}
	
	public Object getValueByColumn(String col){
		Field f;
		try {
			f = getClass().getDeclaredField(col);
			f.setAccessible(true);
			Object obj = f.get(this);
			if(obj != null){
				return obj;
			}
		} catch (NoSuchFieldException e) {
			LogUtil.err("getValueByColumn NoSuchFieldException, col " + col + " not exist!!!");
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
			LogUtil.err("getValueByColumn IllegalArgumentException, col " + col + " :"+e.getMessage());
			throw e;
		}
		return null;
	}
	
	public boolean save2Db(DB db){
		List<String> columns = dbColumns();
		for(int i = 0;i<columns.size();i++){
			String column = columns.get(i);
			String dbKey = joinKeyWithColumnAndIndentify(column);
			Object value = getValueByColumn(column);
			if(value != null){
				String str = columnValueToString(value);
				db.put(dbKey, str);
			}
		}
		return true;
	}
	
	/**
	 * save one single column value to database
	 * */
	public boolean saveColumnValue2Db(DB db,String column,String value){
		String dbKey = joinKeyWithColumnAndIndentify(column);
		if(value != null){
			db.put(dbKey, value);
			return true;
		}
		return false;
	}
	
	public boolean deleteBeanFromDb(DB db){
		List<String> columns = dbColumns();
		for(int i = 0;i < columns.size();i++){
			String column = columns.get(i);
			String dbKey = joinKeyWithColumnAndIndentify(column);
			db.delete(dbKey);
		}
		return true;
	}
	public boolean deleteColumnValueFromDb(DB db,String column){
		String dbKey = joinKeyWithColumnAndIndentify(column);
		db.delete(dbKey);
		return true;
	}
	
	public String splitIdentifyFromDbKey(String column,String dbKey){
		if(dbKey == null || column == null){
			return null;
		}
		String joinKey = joinKeyWithColumn(column);
		if(dbKey.startsWith(joinKey)){
			String id = dbKey.substring(joinKey.length());
			return id;
		} else {
			return null;
//			throw new RuntimeException("splitIdentifyFromDbKey dbKey not start with tablename and column");
		}
	}
	
	List<? extends AbsBean> getBeanListByIds(DB db,List<String> ids){
		List<AbsBean> list = new ArrayList<AbsBean>();
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			list.add(loadFromDbById(db, id));
		}
		return list;
	}
	
	
	//////////////////////////////JSON//////////////////////////////
	public List<String> jsonColumns(){
		Field[] fields = getClass().getDeclaredFields();
		
		List<String> cols = new ArrayList<String>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(AdfJsonColumn.class)) {
				cols.add(field.getName());
			}
		}
		return cols;
	}
	
	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		List<String> cols = jsonColumns();
		for (int i = 0; i < cols.size(); i++) {
			String col = cols.get(i);
			Object val = getValueByColumn(col);
			if(val != null){
				if(val.getClass().isArray()){
					JSONArray arr = arrayObjectToJson(val);
					try {
						obj.put(col, arr);
					} catch (JSONException e) {
					}
				} else {
					try {
						obj.put(col, val);
					} catch (JSONException e) {
					}
				}
			}
		}
		return obj;
	}
	public boolean fromJson(JSONObject json){
		List<String> cols = jsonColumns();
		for (int i = 0; i < cols.size(); i++) {
			String col = cols.get(i);
			Object val = json.opt(col);
			if(val != null){
				if(val instanceof JSONArray){
					JSONArray arr = (JSONArray)val;
					try {
						setArrayColumn(col, arr);
					} catch (NoSuchFieldException e) {
					} catch (IllegalAccessException e) {
					} catch (IllegalArgumentException e) {
					}
				} else {					
					try {
						setValueByOject(col, val);
					} catch (NoSuchFieldException e) {
					} catch (IllegalAccessException e) {
					} catch (IllegalArgumentException e) {
					}
				}
			}
		}
		return true;
	}
}

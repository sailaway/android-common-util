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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.litl.leveldb.DB;
import com.litl.leveldb.Iterator;

public class BeanSearch {
	
	public interface IColumnCondition{
		public boolean fitCondition(String col,String val,String id);
	}
	
	/**
	 * return a map that key is the identify and value is the assign column value
	 * */
	public static Map<String,String> searchAssignColumn(DB db,AbsBean bean,String column){
		if (db == null || column == null) {
			return null;
		}
		HashMap<String,String> map = new HashMap<String, String>();
		String key = bean.joinKeyWithColumn(column);
		Iterator iter = db.iterator();
		for (iter.seek(key); iter.isValid(); iter.next()) {
            String dbkeyStr = iter.getKeyString();
        	String id = bean.splitIdentifyFromDbKey(column,dbkeyStr);
        	if(id != null){
        		String value = iter.getValueString();
                map.put(id, value);
        	} else {
        		break;
        	}
        }
		iter.close();
		return map;
	}
	
	/**
	 * return a list contains that assign column that value is as given
	 * */
	public static List<String> searchByColumnNameAndValue(DB db,AbsBean bean,String column,String value){
		if(db == null || column == null || value == null){
			return null;
		}
		List<String> ids = new ArrayList<String>();
		Map<String,String> map = searchAssignColumn(db,bean, column);
		if(map == null){
			return null;
		}
		Set<String> keys = map.keySet();
		java.util.Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String k = it.next();
			String v = map.get(k);
			if(v.equals(value)){
				ids.add(k);
			}
		}
		return ids;
	}
	
	/**
	 * return a list contains that assign column that value is as given
	 * */
	public static List<String> searchByColumnsNameAndValues(DB db,AbsBean bean,String[] columns,String[] values){
		if(db == null || columns == null || values == null || 
				columns.length <= 0 || values.length <= 0 || columns.length != values.length){
			return null;
		}
		String column = columns[0];
		String value  = values[0];
		List<String> ids = new ArrayList<String>();
		Map<String,String> map = searchAssignColumn(db,bean, column);
		if(map == null){
			return null;
		}
		Set<String> keys = map.keySet();
		java.util.Iterator<String> it = keys.iterator();
		boolean checkFailed = false;
		while(it.hasNext()){
			String k = it.next();
			String v = map.get(k);
			if(v.equals(value)){
				checkFailed = false;
				for(int i = 1; i < columns.length;i++){
					v = bean.getAssignColumnAssignIdValue(db, columns[i], k);
					if(!v.equals(values[i])){
						checkFailed = true;
						break;
					}
				}
				if(checkFailed){
					continue;
				}
				ids.add(k);
			}
		}
		return ids;
	}
	
	/**
	 * return a list contains all ids that assign column value in range [min,max]
	 * param can not be null
	 * */
	public static List<String> searchByColumnValueRange(DB db,AbsBean bean,String column,String min,String max){
		if(db == null || column == null || min == null || max == null){
			return null;
		}
		List<String> ids = new ArrayList<String>();
		Map<String,String> map = searchAssignColumn(db,bean, column);
		if(map == null){
			return null;
		}
		Set<String> keys = map.keySet();
		java.util.Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String k = it.next();
			String v = map.get(k);
			if(min.compareTo(v) <= 0 && max.compareTo(v) >= 0){
				ids.add(k);
			}
		}
		return ids;
	}
	
	/**
	 * return a List of ids that the assign columns fit condition
	 * 	*/
	public static List<String> searchByColumnCondition(DB db,AbsBean bean,List<String> cols,IColumnCondition condition){
		if(db == null || condition == null || cols == null || cols.size() <= 0){
			return null;
		}
		List<String> ids = searchByColumnCondition(db,bean, cols.get(0), condition);
		for(int i = 1;i < cols.size();i++){
			String col = cols.get(i);
			for (int j = 0; j < ids.size();) {
				String key = bean.joinKeyWithColumnAndIndentify(col, ids.get(j));
				String val = db.get(key);
				if(!condition.fitCondition(col, val,ids.get(j))){
					ids.remove(j);
				} else {
					j++;
				}
			}
		}
		return ids;
	}
	
	/**
	 * return a List of ids that fit the assign condition
	 * 	*/
	public static List<String> searchByColumnCondition(DB db,AbsBean bean,IColumnCondition condition){
		List<String> cols = bean.dbColumns();
		return searchByColumnCondition(db, bean, cols, condition);
	}
	
	/**
	 * return a List of ids that fit the assign column meat the condition
	 * 	*/
	public static List<String> searchByColumnCondition(DB db,AbsBean bean,String col,IColumnCondition condition){
		if(db == null || condition == null || col == null){
			return null;
		}
		List<String> ids = new ArrayList<String>();
		String key = bean.joinKeyWithColumn(col);
		Iterator iter = db.iterator();
		for (iter.seek(key); iter.isValid(); iter.next()) {
            String dbkeyStr = iter.getKeyString();
        	String id = bean.splitIdentifyFromDbKey(col,dbkeyStr);
        	if(id != null){
        		String value = iter.getValueString();
                if(condition.fitCondition(col, value,id)){
                	ids.add(id);
                }
        	} else {
        		break;
        	}
        }
		iter.close();
		return ids;
	}
}

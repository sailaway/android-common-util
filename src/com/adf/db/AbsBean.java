package com.adf.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.adf.db.annotation.AdfColumn;
import com.adf.util.LogUtil;
import com.litl.leveldb.DB;

/**
 * columns Must be int,float,double,short this basic type or String type 
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
		List<String> columns = columns();
		for(int i = 0;i<columns.size();i++){
			String column = columns.get(i);
			String dbKey = joinKeyWithColumnAndIndentify(column);
			String value = db.get(dbKey);
			setValueByColumn(column,value);
		}
		return this;
	}
	
	public List<String> columns(){
		Field[] fields = getClass().getDeclaredFields();
		
		List<String> cols = new ArrayList<String>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(AdfColumn.class)) {
				cols.add(field.getName());
			}
		}
		return cols;
	}
	
	public void setValueByColumn(String col,String val) throws IllegalArgumentException{
		try {
			Field f = getClass().getDeclaredField(col);
			Class<?> cls = f.getType();
			f.setAccessible(true);
			if (String.class == cls) {
				f.set(this, val);
			} else if ((Integer.TYPE == cls) || (Integer.class == cls)) {
				int ival = Integer.parseInt(val);
				f.set(this, ival);
			} else if ((Long.TYPE == cls) || (Long.class == cls)) {
				long lval = Long.parseLong(val);
				f.set(this, lval);
			} else if ((Float.TYPE == cls) || (Float.class == cls)) {
				float fval = Float.parseFloat(val);
				f.set(this, fval);
			} else if ((Short.TYPE == cls) || (Short.class == cls)) {
				short sval = Short.parseShort(val);
				f.set(this, sval);
			} else if ((Double.TYPE == cls) || (Double.class == cls)) {
				double dval = Double.parseDouble(val);
				f.set(this, dval);
			} else {// if (Blob.class == cls)
				LogUtil.err("setValueByColumn Not supported field type:" + cls.toString() + " !!!");
				throw new IllegalArgumentException("Not support Column Type");
			}
		} catch (NoSuchFieldException e) {
			LogUtil.err("setValueByColumn NoSuchFieldException, col " + col + " not exist!!!");
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
			LogUtil.err("setValueByColumn IllegalArgumentException, col " + col + " :"+e.getMessage());
			throw e;
		}
	}
	
	public String getValueByColumn(String col){
		Field f;
		try {
			f = getClass().getDeclaredField(col);
			f.setAccessible(true);
			Object obj = f.get(this);
			if(obj != null){				
				return obj.toString();
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
		List<String> columns = columns();
		for(int i = 0;i<columns.size();i++){
			String column = columns.get(i);
			String dbKey = joinKeyWithColumnAndIndentify(column);
			String value = getValueByColumn(column);
			if(value != null){
				db.put(dbKey, value);
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
		List<String> columns = columns();
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
}

package com.litl.leveldb;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.adf.util.LogUtil;

public class DB extends NativeObject {
    public abstract static class Snapshot extends NativeObject {
        Snapshot(long ptr) {
            super(ptr);
        }
    }

    private final File mPath;
    private boolean mDestroyOnClose = false;

    public DB(File path) {
        super();

        if (path == null) {
            throw new NullPointerException();
        }
        mPath = path;
    }

    public void open() {
        mPtr = nativeOpen(mPath.getAbsolutePath());
    }

    @Override
    protected void closeNativeObject(long ptr) {
        nativeClose(ptr);

        if (mDestroyOnClose) {
            destroy(mPath);
        }
    }
    
    //added by dean
    public static byte[] bytes(String str) {
    	if(str == null){
    		return null;
    	}
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
	
  //added by dean
	public static String bytes2String(byte[] bytes){
		if(bytes == null){
			return null;
		}
		try {
			String str = new String(bytes, "UTF-8");
			return str;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	//added by dean
	public void put(String key,String value){
		if(key == null){
//			throw new NullPointerException("key");
			LogUtil.err("DB put key is null");
			return;
		}
		if(value == null){
//			throw new NullPointerException("value");
			LogUtil.err("DB put key "+key+" with null value");
			return;
		}
		byte[] keyBytes = bytes(key);
		byte[] valueBytes = bytes(value);
		put(keyBytes,valueBytes);
	}
	
	//added by dean
	public String get(String key){
		if(key == null){
//			throw new NullPointerException("key");
			LogUtil.err("DB get key is null");
			return null;
		}
		byte[] keyBytes = bytes(key);
		byte[] valueBytes = get(keyBytes);
		String value = bytes2String(valueBytes);
		return value;
	}
    
    public void put(byte[] key, byte[] value) {
        assertOpen("Database is closed");
        if (key == null) {
//            throw new NullPointerException("key");
        	LogUtil.err("DB get byte key is null");
        	return;
        }
        if (value == null) {
//            throw new NullPointerException("value");
        	LogUtil.err("DB get byte key is "+key+" with null value");
        	return;
        }

        nativePut(mPtr, key, value);
    }

    public byte[] get(byte[] key) {
        return get(null, key);
    }

    public byte[] get(Snapshot snapshot, byte[] key) {
        assertOpen("Database is closed");
        if (key == null) {
        	LogUtil.err("DB get snapshoot key is null");
//            throw new NullPointerException();
        	return null;
        }

        return nativeGet(mPtr, snapshot != null ? snapshot.getPtr() : 0, key);
    }

    public void delete(String key){
    	if(key == null){
    		return;
    	}
    	delete(bytes(key));
    }
    
    public void delete(byte[] key) {
        assertOpen("Database is closed");
        if (key == null) {
//            throw new NullPointerException();
        	LogUtil.err("DB delete key is null");
        	return;
        }

        nativeDelete(mPtr, key);
    }

    public void write(WriteBatch batch) {
        assertOpen("Database is closed");
        if (batch == null) {
//            throw new NullPointerException();
        	LogUtil.err("DB WriteBatch batch == null");
        	return;
        }

        nativeWrite(mPtr, batch.getPtr());
    }

    public Iterator iterator() {
        return iterator(null);
    }

    public Iterator iterator(final Snapshot snapshot) {
        assertOpen("Database is closed");

        ref();

        if (snapshot != null) {
            snapshot.ref();
        }

        return new Iterator(nativeIterator(mPtr, snapshot != null ? snapshot.getPtr() : 0)) {
            @Override
            protected void closeNativeObject(long ptr) {
                super.closeNativeObject(ptr);
                if (snapshot != null) {
                    snapshot.unref();
                }

                DB.this.unref();
            }
        };
    }

    public Snapshot getSnapshot() {
        assertOpen("Database is closed");
        ref();
        return new Snapshot(nativeGetSnapshot(mPtr)) {
            protected void closeNativeObject(long ptr) {
                nativeReleaseSnapshot(DB.this.getPtr(), getPtr());
                DB.this.unref();
            }
        };
    }

    public void destroy() {
        mDestroyOnClose = true;
        if (getPtr() == 0) {
            destroy(mPath);
        }
    }

    public static void destroy(File path) {
        nativeDestroy(path.getAbsolutePath());
    }

    private static native long nativeOpen(String dbpath);

    private static native void nativeClose(long dbPtr);

    private static native void nativePut(long dbPtr, byte[] key, byte[] value);

    private static native byte[] nativeGet(long dbPtr, long snapshotPtr, byte[] key);

    private static native void nativeDelete(long dbPtr, byte[] key);

    private static native void nativeWrite(long dbPtr, long batchPtr);

    private static native void nativeDestroy(String dbpath);

    private static native long nativeIterator(long dbPtr, long snapshotPtr);

    private static native long nativeGetSnapshot(long dbPtr);

    private static native void nativeReleaseSnapshot(long dbPtr, long snapshotPtr);

    public static native String stringFromJNI();

    {
        System.loadLibrary("leveldbjni");
    }
}

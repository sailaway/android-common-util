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

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LBSHelp {
	
	private static Criteria getCriteria(){
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);          /* 设置查询精度  */ 
        c.setSpeedRequired(false);                      /* 设置是否要求速度    */ 
        c.setCostAllowed(false);                        /* 设置是否允许产生费用    */ 
        c.setBearingRequired(false);                    /* 设置是否霄1�7要得到方各1�7    */ 
        c.setAltitudeRequired(false);                   /* 设置是否霄1�7要得到海拔高庄1�7    */ 
        c.setPowerRequirement(Criteria.POWER_LOW);      /* 设置允许的电池消耗级刄1�7    */ 
        return c;   
    }

	/**
	 * just use net work provider to locate current position;
	 * */
	public static Location getCurrentPositionUseNetWorkProvider(Context context){//,LocationListener listener
    	final LocationManager mLocaltionManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	
    	String bestProvider = mLocaltionManager.getBestProvider(getCriteria(), true);   /*设置查询条件  */
    	//FIXME for while use gps ,getLastKnownLocation function always return null.so fuck 
    	bestProvider = LocationManager.NETWORK_PROVIDER;
    	if(bestProvider==null){
    		List<String> provoders = mLocaltionManager.getProviders(false);
        	if(provoders == null || provoders.size()<=0){
        		return null;
        	}
        	bestProvider = provoders.get(0);
    	}
        
    	if(bestProvider==null){
    		return null;
    	}
    	
    	Location l = mLocaltionManager.getLastKnownLocation(bestProvider);
    	
    	if(l == null ){//&& bestProvider.equals(LocationManager.GPS_PROVIDER)
//    		while(l==null){
//    			mLocaltionManager.requestLocationUpdates(bestProvider, 0l, 0f, listener);
//    			l = mLocaltionManager.getLastKnownLocation(bestProvider);
//    		}
    	}
    	
    	return l;
    }
}

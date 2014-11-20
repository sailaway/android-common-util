/**
 * Copyright 2014 sailaway(https://github.com/sailaway)
 *
 * Licensed under theGNU GENERAL PUBLIC LICENSE Version 3 (the "License");
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 * 
 */
package com.adf.app;


import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public abstract class AbsActivity extends Activity implements OnClickListener{
	
	public void initViews(){}
	public void setupViews(){}
	
	@Override
	public void onClick(View v) {}
	
	public void toastUser(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}

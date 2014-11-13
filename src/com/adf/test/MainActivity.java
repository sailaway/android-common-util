/**
 * Copyright 2014 sailaway(https://github.com/sailaway)
 *
 * Licensed under theGNU GENERAL PUBLIC LICENSE Version 3 (the "License");
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 * 
 */
package com.adf.test;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.adf.app.AbsActivity;
import com.adf.framework.R;

public class MainActivity extends AbsActivity implements OnClickListener {

	int[] imgResIds = new int[]{
		android.R.drawable.btn_radio,
		android.R.drawable.star_big_on,
		android.R.drawable.star_big_off,
		android.R.drawable.star_big_on,
		android.R.drawable.star_big_off,
	};
	int[] textIds = new int[]{
		android.R.string.cancel,
		android.R.string.copy,
		android.R.string.paste,
		android.R.string.cut,
		android.R.string.cut,
	};
	
	void testTabLayout(){
//		final TabLayout tl = (TabLayout) findViewById(R.id.tab_ct_bt_layout);
//		tl.setTabImgAndTexts(imgResIds, textIds);
//		tl.setOnTabClickListener(new AdfOnTabClickListener() {
//			@Override
//			public void onTabClick(final int idx, View v) {
//				tl.showBadgetView(idx, "1", true);
//				tl.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						tl.hideBadgetView(idx, true);
//					}
//				}, 2000);
//			}
//		});
//		tl.showBadgetView(0,"9",true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		testTabLayout();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}

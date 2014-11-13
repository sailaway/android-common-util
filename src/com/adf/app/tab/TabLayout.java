/**
 * Copyright 2014 sailaway(https://github.com/sailaway)
 *
 * Licensed under theGNU GENERAL PUBLIC LICENSE Version 3 (the "License");
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 * 
 */
package com.adf.app.tab;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adf.app.badge.BadgeView;
import com.adf.framework.R;


public class TabLayout extends LinearLayout implements OnClickListener{
	
	protected static final int TabBtCellLayoutIdStart = 10001;
	
	protected static final int TabCountDefault = 5;
	
	protected List<ViewGroup> mTabViews;
	int mSplitImgResId;
	int mTabCount;
	float mTabTextSize;
	ColorStateList mTabTextColor;
	
	int mCurSelectIdx;

	public TabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.TabLayout);
		mSplitImgResId = ta.getResourceId(R.styleable.TabLayout_tab_split_drawable, 0);
		float txtSize = getResources().getDimension(R.dimen.tab_ct_tv_size);
		mTabTextSize = ta.getDimension(R.styleable.TabLayout_tab_text_size, txtSize);
		mTabCount = ta.getInteger(R.styleable.TabLayout_tab_count, TabCountDefault);
		mTabTextColor = ta.getColorStateList(R.styleable.TabLayout_tab_text_color);
		if(mTabTextColor == null){
			mTabTextColor = getResources().getColorStateList(R.color.tab_text_color);
		}
		ta.recycle();
		mTabViews = new ArrayList<ViewGroup>();
	}
	public TabLayout(Context context) {
		super(context);
	}

	public interface AdfOnTabClickListener{
		public void onTabClick(int idx,View v);
	}
	
	protected AdfOnTabClickListener mListener;
	
	public void setTabImgAndTexts(int[] imgResIds,int[] textIds){
		String[] texts = new String[textIds.length];
		for (int i = 0; i < texts.length; i++) {
			texts[i] = getContext().getString(textIds[i]);
		}
		setTabImgAndTexts(imgResIds, texts);
	}
	
	public void setTabImgAndTexts(int[] imgResIds,String[] texts){
		if(imgResIds.length < mTabCount || texts.length < mTabCount){
			throw new IllegalArgumentException("TabLayout setTabImgAndTexts image or text array is little mTabCount("+mTabCount+")");
		}
		int count = mTabCount;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 
				LinearLayout.LayoutParams.MATCH_PARENT, 1);
		int splitWidth = (int) getResources().getDimension(R.dimen.tab_split_width);
		LinearLayout.LayoutParams split_lp = new LinearLayout.LayoutParams(splitWidth, 
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER;
		LayoutInflater in = LayoutInflater.from(getContext());
		for (int i = 0; i < count; i++) {
			ViewGroup v = (ViewGroup) in.inflate(R.layout.tab_ct_bt_cell, this,false);
			v.setOnClickListener(this);
			v.setId(TabBtCellLayoutIdStart + i);
			ImageView img = (ImageView)v.findViewById(R.id.tab_ct_bt_cell_iv);
			img.setImageResource(imgResIds[i]);
			TextView tv = (TextView)v.findViewById(R.id.tab_ct_bt_cell_tv);
			tv.setText(texts[i]);
			tv.setTextColor(mTabTextColor);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTabTextSize);
			addView(v,lp);
			mTabViews.add(v);
			if(i < count -1){
				ImageView split = (ImageView) in.inflate(R.layout.tab_split, this, false);
				split.setImageResource(mSplitImgResId);
				addView(split,split_lp);
			}
		}
		selectTab(0);
	}
	
	public void showBadgetView(int idx,String txt,boolean animate){
		showBadgetView(idx, txt, BadgeView.POSITION_TOP_RIGHT,animate,null);
	}
	public void showBadgetView(int idx,String txt,Animation anim){
		showBadgetView(idx, txt, BadgeView.POSITION_TOP_RIGHT,true,anim);
	}
	private void showBadgetView(int idx,String txt,int badgePos,boolean animate,Animation anim){
		ViewGroup parent = mTabViews.get(idx);
		BadgeView badge = getBadgeView(idx);
		if(badge == null){			
			badge = new BadgeView(getContext());
			parent.addView(badge);
		}
		badge.setBadgePosition(badgePos);
		badge.setText(txt);
		if(anim != null){
			badge.show(anim);
		} else {			
			badge.show(animate);
		}
	} 
	public void showBadgetView(int idx,BadgeView badge){
		ViewGroup parent = mTabViews.get(idx);
		BadgeView old = getBadgeView(idx);
		if(old != null){
			parent.removeView(old);
		}
		parent.addView(badge);
		badge.show();
	}
	
	public BadgeView getBadgeView(int idx){
		ViewGroup parent = mTabViews.get(idx);
		int count = parent.getChildCount();
		for (int i = 0; i < count; i++) {
			if(parent.getChildAt(i) instanceof BadgeView){
				return (BadgeView)parent.getChildAt(i);
			}
		}
		return null;
	}
	
	public void hideBadgetView(int idx,boolean animate){
		BadgeView badge = getBadgeView(idx);
		if(badge != null){
			badge.hide(animate);
		}
	}
	public void hideBadgetView(int idx,Animation anim){
		BadgeView badge = getBadgeView(idx);
		if(badge != null){
			badge.hide(anim);
		}
	}
	
	public void setOnTabClickListener(AdfOnTabClickListener mListener) {
		this.mListener = mListener;
	}
	public AdfOnTabClickListener getOnTabClickListener() {
		return mListener;
	}
	
	public void selectTab(int idx){
		int count = mTabCount;
		for (int i = 0; i < count; i++) {
			setCellSelected(i, false);
		}
		setCellSelected(idx, true);
		mCurSelectIdx = idx;
	}
	
	public void recursionSetSubViewSelect(View v,boolean select){
		v.setSelected(select);
		if(v instanceof ViewGroup){
			ViewGroup g = (ViewGroup)v;
			for (int i = 0; i < g.getChildCount(); i++) {
				View child = g.getChildAt(i);
				recursionSetSubViewSelect(child, select);
			}
		}
	}
	
	private void setCellSelected(int idx,boolean selected){
		ViewGroup childGroup = mTabViews.get(idx);
//		for (int j = 0; j < childGroup.getChildCount(); j++) {
//			recursionSetSubViewSelect(childGroup.getChildAt(j),false);
//			//childGroup.getChildAt(j).setSelected(false);
//		}
		recursionSetSubViewSelect(childGroup,selected);
		//childGroup.setSelected(false);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		int idx = id - TabBtCellLayoutIdStart;
		int count = mTabCount;
		if(idx >= 0 &&idx < count){
			if(mCurSelectIdx != idx){				
				selectTab(idx);
			}
			if(mListener != null){
				mListener.onTabClick(idx, v);
			}
		}
	}
}

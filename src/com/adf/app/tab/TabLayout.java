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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adf.app.badge.BadgeView;
import com.adf.framework.R;

/**
 * TODO add BadgeView
 * */
public class TabLayout extends LinearLayout implements OnClickListener{
	
	protected static final int TabBtCellLayoutIdStart = 10001;
	
	protected static final int TabCountDefault = 5;
	
	protected List<ViewGroup> mTabViews;
	int mSplitImgResId;
	int mTabCount;
	float mTabTextSize;
	ColorStateList mTabTextColor;

	public TabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.TabLayout);
		mSplitImgResId = ta.getResourceId(R.styleable.TabLayout_tab_split_drawable, 0);
		float txtSize = getResources().getDimension(R.dimen.tab_ct_tv_size);
		mTabTextSize = ta.getDimension(R.styleable.TabLayout_tab_text_size, txtSize);
		mTabCount = ta.getInteger(R.styleable.TabLayout_tab_count, TabCountDefault);
		mTabTextColor = ta.getColorStateList(R.styleable.TabLayout_tab_text_color);

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
	}
	
	public void showBadgetView(int idx,String txt){
		showBadgetView(idx, txt, BadgeView.POSITION_TOP_RIGHT);
	}
	public void showBadgetView(int idx,String txt,int badgePos){
		ViewGroup parent = mTabViews.get(idx);
		BadgeView badge = new BadgeView(getContext());
		badge.setBadgePosition(badgePos);
		parent.addView(badge);
		badge.setText(txt);
		badge.show();
	} 
	public void showBadgetView(int idx,BadgeView badge){
		ViewGroup parent = mTabViews.get(idx);
		parent.addView(badge);
		badge.show();
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
	}
	
	private void setCellSelected(int idx,boolean selected){
		ViewGroup childGroup = mTabViews.get(idx);
		for (int j = 0; j < childGroup.getChildCount(); j++) {
			childGroup.getChildAt(j).setSelected(false);
		}
		childGroup.setSelected(false);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		int idx = id - TabBtCellLayoutIdStart;
		int count = mTabCount;
		if(idx >= 0 &&idx < count){
			selectTab(idx);
			if(mListener != null){
				mListener.onTabClick(idx, v);
			}
		}
	}
}

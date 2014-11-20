package com.adf.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

/**
 * manager a UI module,and at the same time,
 * can push/pull from PageContainer
 * 
 * All sub class member variable MUST init in function before it first use,
 * or change to static variable,for initViews and setupViews will 
 * */
public abstract class AbsPage implements OnClickListener{

	protected Context mContext;
	/**
	 * 最底层RootView
	 * */
	protected View mRootView;
	
	protected void initViews(){};
	protected void setupViews(){};
	
	public AbsPage(View root,Context context){
		this.mRootView = root;
		this.mContext = context;
		initViews();
		setupViews();
	}
	public AbsPage(int layoutId,Context context){
		LayoutInflater in = LayoutInflater.from(context);
		FrameLayout fl = new FrameLayout(context);
		this.mRootView = in.inflate(layoutId, fl, false);
		this.mContext = context;
		initViews();
		setupViews();
	}
	
	public View getRootView(){
		return mRootView;
	}
	
	protected Activity getActivity(){
		return (Activity)mContext;
	}
	
	protected View findViewById(int id) {
		return mRootView.findViewById(id);
	}
	
	@Override
	public void onClick(View v) {}
	
	public void startActivity(Intent intent){
		mContext.startActivity(intent);
	}
	public void startActivityForResult(Intent intent, int requestCode){
		getActivity().startActivityForResult(intent, 0);		
	}
	
	public LayoutInflater getLayoutInflater(){
		LayoutInflater in = LayoutInflater.from(mContext);
		return in;
	}
	public Resources getResources(){
		return mContext.getResources();
	}
	public int getResourceColor(int resId){
		int color = getResources().getColor(resId);
		return color;
	}
	public Drawable getResourceDrawable(int resId){
		Drawable d = getResources().getDrawable(resId);
		return d;
	}
	public String getString(int resId){
		String s = getResources().getString(resId);
		return s;
	}
}

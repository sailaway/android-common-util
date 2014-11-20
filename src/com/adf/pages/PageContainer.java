package com.adf.pages;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class PageContainer extends FrameLayout {
	
	protected List<AbsLayoutPage> mPages;
	
	public PageContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public PageContainer(Context context) {
		super(context);
		init();
	}
	
	void init(){
		mPages = new ArrayList<AbsLayoutPage>();
	}
	
	public LayoutInflater getLayoutInflater(){
		LayoutInflater in = LayoutInflater.from(getContext());
		return in;
	}
	
	public void pushView(AbsLayoutPage page,boolean anim){
		addView(page.getRootView());
		page.setContainer(this);
		mPages.add(page);
		page.onResume();
	}
	
	public void popToViewIndex(int idx,boolean anim){
		while(getChildCount() > idx + 1){
			popView(false, anim);
		}
		int i = getChildCount() -1;
		AbsLayoutPage page =  mPages.get(i);
		page.onResume();
	}
	
	private void popView(boolean resumeWhilePop,boolean anim){
		int i = getChildCount() -1;
		AbsLayoutPage page =  mPages.get(i);
		page.onPause();
		if(resumeWhilePop){
			if(i > 1){
				AbsLayoutPage newTop =  mPages.get(i - 1);
				newTop.onResume();
			}
		}
		removeViewAt(i);
		mPages.remove(i);
	}
	
	public boolean isShowingRootView(){
		return mPages.size() == 1;
	}
	
	public void popTopView(boolean anim){
		popView(true, anim);
	}
	public void replaceTopView(AbsLayoutPage page,boolean anim){
		popTopView(anim);
		pushView(page,anim);
	}
	public AbsLayoutPage getTopPage(){
		return mPages.get(mPages.size() -1);
	}

}

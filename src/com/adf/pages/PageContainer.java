package com.adf.pages;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class PageContainer extends FrameLayout {
	final int Animation_Duration = 200;
	
	protected List<AbsLayoutPage> mPages;
	LayoutAnimationController mAnimController;
	
	public PageContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public PageContainer(Context context) {
		super(context);
		init();
	}
	
	Animation mInAnim;
	Animation mOutAnim;
	
	void init(){
		mPages = new ArrayList<AbsLayoutPage>();
		int reltePtype = TranslateAnimation.RELATIVE_TO_PARENT;
		mOutAnim = new TranslateAnimation(reltePtype, 0, reltePtype, -1.0f, 
				reltePtype, 0, reltePtype, 0);
		mInAnim = new TranslateAnimation(reltePtype, 1.0f, reltePtype, 0, 
				reltePtype, 0, reltePtype, 0);
		mOutAnim.setDuration(Animation_Duration);
		mInAnim.setDuration(Animation_Duration);
////		Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
		mAnimController = new LayoutAnimationController(mInAnim);
		mAnimController.setOrder(LayoutAnimationController.ORDER_REVERSE);
		//mAnimController.setDelay(Animation_Duration);
	}
	
	public LayoutInflater getLayoutInflater(){
		LayoutInflater in = LayoutInflater.from(getContext());
		return in;
	}
	
	void doPushAnim(final AbsLayoutPage page){
		mInAnim.reset();
		mOutAnim.reset();
		mOutAnim.setDuration(Animation_Duration);
		mInAnim.setDuration(Animation_Duration);
		if(mPages.size() > 0){
			final AbsLayoutPage oldTop =  mPages.get(mPages.size() - 1);
			AnimationListener outListener = new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					Log.e("ADF","outListener onAnimationEnd");
					oldTop.mRootView.setVisibility(View.GONE);
					oldTop.mRootView.setVisibility(View.GONE);
					oldTop.onPause();
				}
			};
			mOutAnim.setAnimationListener(outListener);
			oldTop.mRootView.startAnimation(mOutAnim);
		}
		AnimationListener inListener = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				Log.e("ADF","inListener onAnimationEnd");
				page.mRootView.setAnimation(null);
			}
		};
		Log.e("ADF","doPushAnim");
		mInAnim.setAnimationListener(inListener);
		mInAnim.setStartTime(Animation.START_ON_FIRST_FRAME);
		page.mRootView.setAnimation(mInAnim);//.startAnimation(mInAnim);
		//pushView(page, false);
		addView(page.getRootView());
		page.setContainer(this);
		mPages.add(page);
		page.onResume();
	}
	void doPopAnim(final boolean resumeWhilePop){
		if(resumeWhilePop){
			if(mPages.size() > 1){
				AbsLayoutPage newTop =  mPages.get(mPages.size() - 2);
				newTop.mRootView.setVisibility(View.VISIBLE);
				newTop.onResume();
			}
		}
		mOutAnim.reset();
		mOutAnim.setDuration(Animation_Duration);
		final AbsLayoutPage oldTop =  mPages.get(mPages.size() - 1);
		AnimationListener outAnim = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				oldTop.onPause();
				removeViewAt(mPages.size() - 1);
				mPages.remove(mPages.size() - 1);
			}
		};
		mOutAnim.setAnimationListener(outAnim);
		oldTop.mRootView.startAnimation(mOutAnim);
	}
	
	public void pushView(AbsLayoutPage page,boolean anim){
//		setAnimationEnabled(anim);
		if(anim){
			doPushAnim(page);
		} else {			
			Log.e("ADF","pushView without anim");
			if(mPages.size() > 0){
				AbsLayoutPage oldTop =  mPages.get(mPages.size() - 1);
				oldTop.mRootView.setVisibility(View.GONE);
				oldTop.onPause();
			}
			addView(page.getRootView());
			page.setContainer(this);
			mPages.add(page);
			page.onResume();
		}
	}
	
	public void popToViewIndex(int idx,boolean anim){
		while(getChildCount() > idx + 1){
			popView(false, anim);
		}
		int i = getChildCount() -1;
		if(i >= 0){
			AbsLayoutPage page =  mPages.get(i);
			page.onResume();
		}
	}
	
	void setAnimationEnabled(boolean enable){
		if(enable){
			setLayoutAnimation(mAnimController);
		} else {
			setLayoutAnimation(null);
		}
	}
	
	private void popView(boolean resumeWhilePop,boolean anim){
		if(mPages.size() <= 0){
			return;
		}
		//setAnimationEnabled(anim);
		if(anim){
			doPopAnim(resumeWhilePop);
		} else {			
			int i = getChildCount() -1;
			AbsLayoutPage page =  mPages.get(i);
			page.onPause();
			if(resumeWhilePop){
				if(i >= 0){
					AbsLayoutPage newTop =  mPages.get(i - 1);
					newTop.mRootView.setVisibility(View.VISIBLE);
					newTop.onResume();
				}
			}
			removeViewAt(i);
			mPages.remove(i);
		}
		
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
	public void replaceAllView(AbsLayoutPage page,boolean anim){
		popToViewIndex(-1, anim);
		pushView(page,anim);
	}
	
	public AbsLayoutPage getTopPage(){
		return mPages.get(mPages.size() -1);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
	}

}

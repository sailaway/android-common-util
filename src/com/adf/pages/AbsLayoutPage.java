package com.adf.pages;


import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

public abstract class AbsLayoutPage extends AbsPage {

	protected PageContainer mContainer;
	
	public void onPause(){};
	public void onResume(){};
	public void onDestroy(){};
	
	public AbsLayoutPage(int layoutId, Context context) {
		super(layoutId, context);
	}
	
	public void pushView(AbsLayoutPage page,boolean anim){
		mContainer.pushView(page,anim);
	}
	public void popTopView(boolean anim) {
		mContainer.popTopView(anim);
	}
	
	public void setContainer(PageContainer container){
		this.mContainer = container;
	}
	public PageContainer getContainer(){
		return this.mContainer;
	}
	
	public boolean onContextItemSelected(MenuItem item){
		return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
	}

}

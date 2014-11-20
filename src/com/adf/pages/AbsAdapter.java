package com.adf.pages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbsAdapter extends BaseAdapter {
	
	protected Context mContext;
	int mLayoutId;
	
	public AbsAdapter(Context context,int itemResId){
		this.mLayoutId = itemResId;
		this.mContext = context;
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater in = LayoutInflater.from(mContext);
			convertView = in.inflate(mLayoutId, parent, false);
		}
		setItemView(position, convertView);
		return convertView;
	}
	public abstract void setItemView(int position,View view);

}

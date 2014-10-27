package com.adf.app;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ab.activity.AbActivity;


public abstract class AbsActivity extends AbActivity implements OnClickListener{
	
	@Override
	public void onClick(View v) {}
	
	public void toastUser(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}

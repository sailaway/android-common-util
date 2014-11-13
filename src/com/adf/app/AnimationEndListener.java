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

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public abstract class AnimationEndListener implements AnimationListener{
	@Override
	public void onAnimationRepeat(Animation animation){};
	@Override
	public void onAnimationStart(Animation animation) {}
}

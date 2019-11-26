package com.sleepy2;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

class B implements ResolutionStrategy {
	@Override
	public MeasuredDimension calcMeasures(int widthMeasureSpec, int heightMeasureSpec) {
		return  new MeasuredDimension(1280, 720);
	}
}

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.resolutionStrategy = new B();

		initialize(new SleepyBird(), config);
	}
}

package com.sample.pathrabolic.activities;

import java.util.LinkedHashMap;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.pathrabolic.PathrabolicView;
import com.sample.pathrabolic.R;

public class Simple extends SherlockActivity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple);
		
//		PathrabolicView a = (PathrabolicView) findViewById(R.id.pbv);
		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "first entry");
		map.put(1, "another button");
		map.put(2, "yet another button");
		
		
//		a.init(map);
	}

}

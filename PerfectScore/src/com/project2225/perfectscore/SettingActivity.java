package com.project2225.perfectscore;

import com.project2225.perfectscore.container.Constant;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingActivity extends Activity {
	Switch sServer;
	
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sServer=(Switch)findViewById(R.id.s_server);
		
		pref=getSharedPreferences(Constant.KEY_PREF, MODE_PRIVATE);
		editor=pref.edit();
		
		sServer.setChecked(pref.getBoolean(Constant.KEY_IS_SERVER, false));
		
		sServer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				editor.putBoolean(Constant.KEY_IS_SERVER, isChecked);
				editor.commit();
			}
		});
	}
}

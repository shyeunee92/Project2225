package com.project2225.perfectscore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HomeActivity extends Activity {
	ImageView ivAdd;
	ImageView ivList;
	ImageView ivShare;
	ImageView ivSetting;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ivAdd=(ImageView)findViewById(R.id.iv_add);
        ivList=(ImageView)findViewById(R.id.iv_list);
        ivShare=(ImageView)findViewById(R.id.iv_share);
        ivSetting=(ImageView)findViewById(R.id.iv_setting);
        
        ivAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(HomeActivity.this, AddActivity.class);
				startActivity(i);
			}
		});
        
        ivList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(HomeActivity.this, ListActivity.class);
				startActivity(i);
			}
		});
        
        ivShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(HomeActivity.this, ShareActivity.class);
				startActivity(i);
			}
		});
        
        ivSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(HomeActivity.this, SettingActivity.class);
				startActivity(i);
			}
		});
    }

}

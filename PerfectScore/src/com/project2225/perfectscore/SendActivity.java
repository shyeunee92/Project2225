package com.project2225.perfectscore;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.project2225.perfectscore.adapter.CategoryAdapter;
import com.project2225.perfectscore.adapter.QuestionAdapter;
import com.project2225.perfectscore.container.Constant;
import com.project2225.perfectscore.container.QuestionItem;
import com.project2225.perfectscore.database.NotesDBAdapter;

public class SendActivity extends Activity{
	ActionBar abList;
	DrawerLayout dlList;
	ActionBarDrawerToggle dlToggle;
	Button btAll;
	Button btShare;
	ListView lvCategory;
	TextView tvTitle;
	ListView lvQuestion;
	
	CategoryAdapter categoryAdapter;
	QuestionAdapter questionAdapter;
	
	ArrayList<String> categoryList;
	ArrayList<QuestionItem> questionList;
	
	NotesDBAdapter dbAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		abList=getActionBar();
		dlList=(DrawerLayout)findViewById(R.id.dl_list);
		btAll=(Button)findViewById(R.id.bt_all);
		btShare=(Button)findViewById(R.id.bt_share);
		lvCategory=(ListView)findViewById(R.id.lv_category);
		tvTitle=(TextView)findViewById(R.id.tv_title);
		lvQuestion=(ListView)findViewById(R.id.lv_questions);
		categoryList=new ArrayList<String>();
		questionList=new ArrayList<QuestionItem>();
		
		categoryAdapter=new CategoryAdapter(this, categoryList);
		questionAdapter=new QuestionAdapter(this, false, questionList);
		
		dbAdapter=new NotesDBAdapter(this);
		
		dlToggle=new ActionBarDrawerToggle(this,dlList,R.drawable.ic_drawer,R.string.app_name,R.string.app_name){
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
			
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};
		
		dlList.setDrawerListener(dlToggle);
		abList.setDisplayHomeAsUpEnabled(true);
		abList.setHomeButtonEnabled(true);
		
		lvQuestion.setEmptyView(findViewById(R.id.tv_questions_empty));
		lvQuestion.setAdapter(questionAdapter);
		lvCategory.setEmptyView(findViewById(R.id.tv_category_empty));
		lvCategory.setAdapter(categoryAdapter);
		
		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				questionListUpdate(0, categoryList.get(position));
			}
		});
		
		btAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				questionListUpdate(1, "");
			}
		});
		
		btShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				questionListUpdate(2, "");
			}
		});
		
		lvQuestion.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder dialog=new AlertDialog.Builder(SendActivity.this);
				dialog.setTitle("문제 공유");
				dialog.setMessage("문제를 공유하시겠습니까?");
				dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				dialog.show();
			}
		});
		
		
		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				questionListUpdate(0, categoryList.get(position));				
			}
		});
		
		categoryListUpdate();
		questionListUpdate(1, "");
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		dlToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		dlToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(dlToggle.onOptionsItemSelected(item)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void  categoryListUpdate() {
		dbAdapter.open();
		dbAdapter.getCategory(categoryList);
		dbAdapter.close();
		categoryAdapter.notifyDataSetChanged();
	}
	
	public void questionListUpdate(int type,String category) {
		//type - 0 : 카테고리, 1 : 전체, 2 : 공유
		dbAdapter.open();
		switch (type) {
		case 0:{
			tvTitle.setText(category);
			dbAdapter.getNotes(dbAdapter.getNotes_category(category), questionList);
			break;
		}
		case 1:{
			tvTitle.setText("내 문제 전체 목록");
			dbAdapter.getNotes(dbAdapter.getAllNotes(),questionList);
			break;
		}
		case 2:{
			tvTitle.setText("공유 문제 목록");
			dbAdapter.getNotes(dbAdapter.getNotes_user(2), questionList);
			break;
		}
		}
		dbAdapter.close();
		/**
		 * 여기에 문제 목록 불러오기
		 */
		dlList.closeDrawers();
		questionAdapter.notifyDataSetChanged();
	}
}
package com.project2225.perfectscore;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.project2225.perfectscore.adapter.AnswerAdapter;
import com.project2225.perfectscore.container.Constant;
import com.project2225.perfectscore.container.JsonConstant;
import com.project2225.perfectscore.container.SelectionItem;
import com.project2225.perfectscore.util.ViewUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class QuestionActivity extends Activity{
	TextView tvCategory,tvQuestion;
	LinearLayout llSelection,llText;
	Button btShow;
	
	public String type;//문제타입
	public String category;//카테고리
	public String question;//문제타이틀
	public String selection;//보기
	public String answer;//정답
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		tvCategory=(TextView)findViewById(R.id.tv_category);
		tvQuestion=(TextView)findViewById(R.id.tv_question);
		llSelection=(LinearLayout)findViewById(R.id.ll_answer_selection);
		llText=(LinearLayout)findViewById(R.id.ll_answer_text);
		btShow=(Button)findViewById(R.id.bt_show);
		
		Intent i=getIntent();
		type=i.getStringExtra(Constant.KEY_TYPE);
		category=i.getStringExtra(Constant.KEY_CATEGORY);
		question=i.getStringExtra(Constant.KEY_QUESTION);
		selection=i.getStringExtra(Constant.KEY_SELECTION);
		answer=i.getStringExtra(Constant.KEY_ANSWER);
		
		tvCategory.setText(category);
		tvQuestion.setText(question);
		if(type.equals("객")){
			llSelection.setVisibility(View.VISIBLE);
			llText.setVisibility(View.GONE);
			
			final ListView lvSelection=(ListView)findViewById(R.id.lv_answer);
			final ArrayList<SelectionItem> selectionList=new ArrayList<SelectionItem>();
			try {
				JSONArray arr=new JSONArray(selection);
				for(int j=0;j<arr.length();j++){
					SelectionItem item=new SelectionItem();
					JSONObject obj=arr.getJSONObject(j);
					item.isChecked=obj.getBoolean(JsonConstant.KEY_IS_ANSWER);
					item.item=obj.getString(JsonConstant.KEY_ITEM);
					selectionList.add(item);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			AnswerAdapter adapter=new AnswerAdapter(false, QuestionActivity.this, selectionList);
			lvSelection.setAdapter(adapter);
			ViewUtil.setListViewHeightBasedOnChildren(lvSelection);
			
			btShow.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AnswerAdapter adapter=new AnswerAdapter(true, QuestionActivity.this, selectionList);
					lvSelection.setAdapter(adapter);
					ViewUtil.setListViewHeightBasedOnChildren(lvSelection);
				}
			});
			
		}else{
			llText.setVisibility(View.VISIBLE);
			llSelection.setVisibility(View.GONE);

			final TextView tvAnswer=(TextView)findViewById(R.id.tv_answer);
			
			btShow.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					tvAnswer.setText(answer);
					tvAnswer.setTextColor(Color.BLUE);
				}
			});
		}
		
	}
}

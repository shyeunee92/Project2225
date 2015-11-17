package com.project2225.perfectscore.adapter;

import java.util.ArrayList;

import com.project2225.perfectscore.R;
import com.project2225.perfectscore.container.QuestionItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class QuestionAdapter extends BaseAdapter{
	boolean adapterType;
	LayoutInflater inflater;
	ArrayList<QuestionItem> arrayList;
	
	public QuestionAdapter(Context context,boolean adapterType,ArrayList<QuestionItem> arrayList) {
		inflater=LayoutInflater.from(context);
		this.arrayList=arrayList;
		this.adapterType=adapterType;
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return arrayList.get(position).idx;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=convertView;
		ViewHolder vh;
		if(view==null){
			vh=new ViewHolder();
			view=inflater.inflate(R.layout.item_question, null);
			vh.cbCheck=(CheckBox)view.findViewById(R.id.cb_check);
			if(adapterType){
				vh.cbCheck.setVisibility(View.VISIBLE);
			}else{
				vh.cbCheck.setVisibility(View.GONE);
			}
			vh.tvQuestion=(TextView)view.findViewById(R.id.tv_question);
			vh.tvTime=(TextView)view.findViewById(R.id.tv_time);
			view.setTag(vh);
		}else{
			vh=(ViewHolder)view.getTag();
		}
		
		QuestionItem item=arrayList.get(position);
		
		if(adapterType){
			vh.cbCheck.setChecked(item.isChecked);
		}
		vh.tvQuestion.setText(item.question);
		vh.tvTime.setText(item.time);
		
		return view;
	}
	
	private class ViewHolder{
		CheckBox cbCheck;
		TextView tvQuestion;
		TextView tvTime;
	}
}

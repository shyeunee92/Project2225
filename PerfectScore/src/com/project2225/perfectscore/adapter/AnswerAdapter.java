package com.project2225.perfectscore.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.project2225.perfectscore.R;
import com.project2225.perfectscore.container.SelectionItem;

public class AnswerAdapter extends BaseAdapter{
	LayoutInflater inflater;
	ArrayList<SelectionItem> arrayList;
	boolean isShown;
	
	public AnswerAdapter(boolean isShown,Context context,ArrayList<SelectionItem> arrayList) {
		inflater=LayoutInflater.from(context);
		this.arrayList=arrayList;
		this.isShown=isShown;
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=convertView;
		ViewHolder vh;
		if(view==null){
			view=inflater.inflate(R.layout.item_category, null);
			vh=new ViewHolder();
			vh.tvItem=(TextView)view.findViewById(R.id.tv_category);
			view.setTag(vh);
		}else{
			vh=(ViewHolder)view.getTag();
		}
		SelectionItem item=arrayList.get(position);
		vh.tvItem.setText(item.item);
		vh.tvItem.setTextColor(Color.BLACK);
		if(isShown&&item.isChecked){
			vh.tvItem.setTextColor(Color.BLUE);
		}
		
		return view;
	}
	
	private class ViewHolder{
		public TextView tvItem;
	}
}
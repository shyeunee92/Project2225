package com.project2225.perfectscore.adapter;

import java.util.ArrayList;

import com.project2225.perfectscore.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter{
	LayoutInflater inflater;
	ArrayList<String> arrayList;
	
	public CategoryAdapter(Context context,ArrayList<String> arrayList) {
		inflater=LayoutInflater.from(context);
		this.arrayList=arrayList;
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
			vh.tvCategory=(TextView)view.findViewById(R.id.tv_category);
			view.setTag(vh);
		}else{
			vh=(ViewHolder)view.getTag();
		}
		
		vh.tvCategory.setText(arrayList.get(position));
		
		return view;
	}
	
	private class ViewHolder{
		public TextView tvCategory;
	}
}

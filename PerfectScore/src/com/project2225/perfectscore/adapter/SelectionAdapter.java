package com.project2225.perfectscore.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.project2225.perfectscore.R;
import com.project2225.perfectscore.container.SelectionItem;

public class SelectionAdapter extends BaseAdapter{
	LayoutInflater inflater;
	ArrayList<SelectionItem> arrayList;
	
	public SelectionAdapter(Context context,ArrayList<SelectionItem> arrayList) {
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
			view=inflater.inflate(R.layout.item_selection, null);
			vh=new ViewHolder();
			vh.cbIsAnswer=(CheckBox)view.findViewById(R.id.cb_is_answer);
			vh.tvItem=(TextView)view.findViewById(R.id.tv_item);
			view.setTag(vh);
		}else{
			vh=(ViewHolder)view.getTag();
		}
		final SelectionItem item=arrayList.get(position);
		
		vh.cbIsAnswer.setChecked(item.isChecked);
		vh.tvItem.setText(item.item);
		
		vh.cbIsAnswer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				item.isChecked=isChecked;
			}
		});
		
		return view;
	}
	
	private class ViewHolder{
		public CheckBox cbIsAnswer;
		public TextView tvItem;
	}
}
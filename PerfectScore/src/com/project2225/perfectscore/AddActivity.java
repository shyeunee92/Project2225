package com.project2225.perfectscore;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.project2225.perfectscore.adapter.SelectionAdapter;
import com.project2225.perfectscore.container.Constant;
import com.project2225.perfectscore.container.JsonConstant;
import com.project2225.perfectscore.container.SelectionItem;
import com.project2225.perfectscore.database.NotesDBAdapter;
import com.project2225.perfectscore.util.ViewUtil;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class AddActivity extends Activity {
	EditText etCategory;
	EditText etQuestion;
	RadioGroup rgType;
	Button btAdd;

	AnswerFragment fragment;

	String type = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		etCategory = (EditText) findViewById(R.id.et_category);
		etQuestion = (EditText) findViewById(R.id.et_question);
		rgType = (RadioGroup) findViewById(R.id.rg_type);
		btAdd = (Button) findViewById(R.id.bt_add);

		rgType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				Log.e("checkId", checkedId + "");
				if (checkedId == R.id.rb_text) {// 주
					fragment = new AnswerFragment(Constant.TYPE_TEXT);
					fragmentReplace(fragment);
					type = "주";
				} else {// 객
					fragment = new AnswerFragment(Constant.TYPE_SELECTION);
					fragmentReplace(fragment);
					type = "객";
				}

			}
		});

		btAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etCategory.getText().toString().trim().equals("")) {
					Toast.makeText(AddActivity.this, "카테고리가 없습니다.",
							Toast.LENGTH_SHORT).show();
				} else if (etQuestion.getText().toString().trim().equals("")) {
					Toast.makeText(AddActivity.this, "문제가 없습니다.",
							Toast.LENGTH_SHORT).show();
				} else if (fragment.isAnswer()) {
					NotesDBAdapter dbAdapter = new NotesDBAdapter(
							AddActivity.this);
					dbAdapter.open();
					dbAdapter.insertNote(type, etCategory.getText().toString(),
							etQuestion.getText().toString(),
							fragment.getSelection(), fragment.getAnswer(), 1, 1);
					dbAdapter.close();
					finish();
				} else {
					Toast.makeText(AddActivity.this, "답이 없습니다.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		rgType.check(R.id.rb_text);
	}

	public void fragmentReplace(AnswerFragment fragment) {
		// replace fragment
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.ll_answer, fragment);
		// Commit the transaction
		transaction.commit();

	}

	private class AnswerFragment extends Fragment {
		private int type;
		private boolean isAnswer = false;
		private String answer = "";
		private String selection = "";

		ArrayList<SelectionItem> selectionList;
		EditText etAnswer;

		public AnswerFragment(int type) {
			super();
			this.type = type;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = null;
			switch (type) {
			case Constant.TYPE_SELECTION:
				rootView = inflater.inflate(R.layout.fragment_select,
						container, false);
				final ListView lvSelection = (ListView) rootView
						.findViewById(R.id.lv_answer);
				final EditText etEditor = (EditText) rootView
						.findViewById(R.id.et_editor);
				Button btAddItem = (Button) rootView
						.findViewById(R.id.bt_add_item);

				selectionList = new ArrayList<SelectionItem>();
				final SelectionAdapter adapter = new SelectionAdapter(
						getActivity(), selectionList);

				lvSelection.setEmptyView(rootView.findViewById(R.id.tv_empty));
				lvSelection.setAdapter(adapter);

				btAddItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!etEditor.getText().toString().trim().equals("")) {
							SelectionItem item = new SelectionItem();
							item.item = etEditor.getText().toString();
							selectionList.add(item);
							adapter.notifyDataSetChanged();
							ViewUtil.setListViewHeightBasedOnChildren(lvSelection);
							etEditor.setText("");
						} else {
							Toast.makeText(getActivity(), "입력 내용이 없습니다.",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
				break;
			case Constant.TYPE_TEXT:
				rootView = inflater.inflate(R.layout.fragment_text, container,
						false);
				etAnswer = (EditText) rootView.findViewById(R.id.et_answer);
				break;
			}
			return rootView;
		}

		public boolean isAnswer() {
			switch (type) {
			case Constant.TYPE_SELECTION:
				if (selectionList.size() > 0) {
					JSONArray arr = new JSONArray();
					for (int k = 0; k < selectionList.size(); k++) {
						JSONObject obj = new JSONObject();
						try {
							obj.put(JsonConstant.KEY_IS_ANSWER,
									selectionList.get(k).isChecked);
							obj.put(JsonConstant.KEY_ITEM,
									selectionList.get(k).item);
							arr.put(obj);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					selection = arr.toString();
					isAnswer=true;
				}else{
					Toast.makeText(getActivity(), "항목을 추가해주세요.", Toast.LENGTH_SHORT).show();
					isAnswer=false;
				}
				answer = "";
				break;
			case Constant.TYPE_TEXT:
				if(!etAnswer.getText().toString().trim().equals("")){
					answer=etAnswer.getText().toString();
					isAnswer=true;
				}else{
					isAnswer=false;
				}
				selection = "";
				break;
			}
			return isAnswer;
		}

		public String getAnswer() {
			return answer;
		}

		public String getSelection() {
			return selection;
		}
	}
}

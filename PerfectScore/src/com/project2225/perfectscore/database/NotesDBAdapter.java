package com.project2225.perfectscore.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.project2225.perfectscore.container.QuestionItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class NotesDBAdapter {

	// public static final String KEY_TITLE = "title";
	// public static final String KEY_BODY = "body";
	// public static final String KEY_ROWID = "_id";
	public static final String KEY_IDX = "note_idx";
	public static final String KEY_TYPE = "note_type";
	public static final String KEY_CATEGORY = "note_category";
	public static final String KEY_TITLE = "note_title";
	public static final String KEY_SELECT = "note_select";
	public static final String KEY_ANSWER = "note_answer";
	public static final String KEY_USER = "note_user";
	public static final String KEY_IS_USED = "note_is_used";
	public static final String KEY_TIME = "note_time";

	private static final String TAG = "NotesDbAdapter";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 *
	 * Database creation sql statement
	 */

	// private static final String DATABASE_CREATE =
	// "create table notes (_id integer primary key autoincrement, "
	// + "title text not null, body text not null);";
	private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS notes (note_idx integer primary key autoincrement, "
			+ "note_type integer null, note_category text null, note_title text null, note_select text null, note_answer text null, note_user integer null, note_is_used integer null, note_time text null);";

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "notes";
	private static final int DATABASE_VERSION = 2;
	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log.w(TAG, "Upgrading database from version " + oldVersion +
			// " to " + newVersion
			// + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	public NotesDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public NotesDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	// 오답노트 추가하기
	public long insertNote(String pnote_type, String pnote_category,
			String pnote_title, String pnote_select, String pnote_answer,
			int pnote_user, int pnote_is_used) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TYPE, pnote_type);
		initialValues.put(KEY_CATEGORY, pnote_category);
		initialValues.put(KEY_TITLE, pnote_title);
		initialValues.put(KEY_SELECT, pnote_select);
		initialValues.put(KEY_ANSWER, pnote_answer);
		initialValues.put(KEY_USER, pnote_user);
		initialValues.put(KEY_IS_USED, pnote_is_used);
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		String now = sdfDate.format(new Date());
		initialValues.put(KEY_TIME, now);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	// 데이터 전체 삭제
	public boolean deleteAllNote(long rowId) {
		// Log.i("Delete called", "value__" + rowId);
		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	// 데이터 선택 삭제 (파라메타 = index)
	public boolean deleteNote(long rowId) {
		// Log.i("Delete called", "value__" + rowId);
		return mDb.delete(DATABASE_TABLE, KEY_IDX + "=" + rowId, null) > 0;
	}

	// 모든 오답정보 가져오기
	public Cursor getAllNotes() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_IDX, KEY_TYPE,
				KEY_CATEGORY, KEY_TITLE, KEY_SELECT, KEY_ANSWER, KEY_USER,
				KEY_IS_USED, KEY_TIME }, null, null, null, null, null);
	}

	public void getNotes(Cursor result,ArrayList<QuestionItem> list) {
		list.clear();
		result.moveToFirst();
		while (!result.isAfterLast()) {
			QuestionItem item=new QuestionItem();
			item.idx = result.getInt(result.getColumnIndex(KEY_IDX));
			item.type = result.getString(result.getColumnIndex(KEY_TYPE));
			item.category = result.getString(result.getColumnIndex(KEY_CATEGORY));
			item.question=result.getString(result.getColumnIndex(KEY_TITLE));
			item.selection=result.getString(result.getColumnIndex(KEY_SELECT));
			item.answer=result.getString(result.getColumnIndex(KEY_ANSWER));
			item.user=result.getInt(result.getColumnIndex(KEY_USER));
			item.isUsed=result.getInt(result.getColumnIndex(KEY_IS_USED));
			item.time=result.getString(result.getColumnIndex(KEY_TIME));
			list.add(item);
			result.moveToNext();
		}
		result.close();
	}
	
	public void getCategory(ArrayList<String> list) {
		list.clear();
		
		Cursor c=mDb.query(true, DATABASE_TABLE, new String[]{KEY_CATEGORY}, null, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			list.add(c.getString(c.getColumnIndex(KEY_CATEGORY)));
			c.moveToNext();
		}
		c.close();
		
	}

	// 데이터 가져오기 (조건 : 문제타입)
	public Cursor getNotes_type(String pnote_type) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_IDX, KEY_TYPE, KEY_CATEGORY, KEY_TITLE, KEY_SELECT,
				KEY_ANSWER, KEY_USER, KEY_IS_USED, KEY_TIME }, KEY_TYPE + "='"
				+ pnote_type + "'", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// 데이터 가져오기 (조건 : 카테고리)
	public Cursor getNotes_category(String pnote_category) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_IDX, KEY_TYPE, KEY_CATEGORY, KEY_TITLE, KEY_SELECT,
				KEY_ANSWER, KEY_USER, KEY_IS_USED, KEY_TIME }, KEY_CATEGORY + "='"
				+ pnote_category + "'", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// 데이터 가져오기 (조건 : user)
	public Cursor getNotes_user(int pnote_user) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_IDX, KEY_TYPE, KEY_CATEGORY, KEY_TITLE, KEY_SELECT,
				KEY_ANSWER, KEY_USER, KEY_IS_USED, KEY_TIME }, KEY_USER + "="
				+ pnote_user + "", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// 데이터 가져오기 (조건 : is_used)
	public Cursor getNotes_is_used(int pnote_is_used) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_IDX, KEY_TYPE, KEY_CATEGORY, KEY_TITLE, KEY_SELECT,
				KEY_ANSWER, KEY_USER, KEY_IS_USED }, KEY_IS_USED + "="
				+ pnote_is_used, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// // 원하는 index의 오답정보 데이터 가져오기
	// public Cursor fetchNote(long rowId) throws SQLException {
	//
	// Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] { KEY_IDX,
	// KEY_TYPE, KEY_CATEGORY, KEY_TITLE, KEY_SELECT, KEY_ANSWER, KEY_USER,
	// KEY_IS_USED }, KEY_IDX
	// + "=" + rowId, null, null, null, null, null);
	// if (mCursor != null) {
	// mCursor.moveToFirst();
	// }
	// return mCursor;
	// }

	// 업데이트
	public boolean updateNote(int pnote_idx, String pnote_type,
			String pnote_category, String pnote_title, String pnote_select,
			String pnote_answer, int pnote_user, int pnote_is_used) {
		ContentValues args = new ContentValues();
		args.put(KEY_TYPE, pnote_type);
		args.put(KEY_CATEGORY, pnote_category);
		args.put(KEY_TITLE, pnote_title);
		args.put(KEY_SELECT, pnote_select);
		args.put(KEY_ANSWER, pnote_answer);
		args.put(KEY_USER, pnote_user);
		args.put(KEY_IS_USED, pnote_is_used);
		return mDb
				.update(DATABASE_TABLE, args, KEY_IDX + "=" + pnote_idx, null) > 0;
	}

}

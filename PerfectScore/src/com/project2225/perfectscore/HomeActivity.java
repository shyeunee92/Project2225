// 블루투스 연결되어 있는 다른 장비에게 오답노트 데이터를 전송함
//sendData(1,"객", "사회", "대한민국은 유럽에 포함되어 있다.", "예" + split2+"아니오", "2",1,1);

// ===
// 현재 디비에 저장되어 있는 오답노트 정보 다 읽어오기
//Cursor result = dbAdapter.getAllNotes(); //getNotes_type("객");
//if(result.getCount() == 0){
//	Toast.makeText(getApplicationContext(), "데이터가 없습니다.", Toast.LENGTH_LONG).show();
//}else{
//	result.moveToFirst();
//	while (!result.isAfterLast()) {
//		String note_idx = result.getString(0);
//		String note_type = result.getString(1);
//		String pnote_category = result.getString(2);
//		String pnote_title = result.getString(3);
//		String pnote_select = result.getString(4);
//		String pnote_answer = result.getString(5);
//		int pnote_user = result.getInt(6);
//		int pnote_is_used = result.getInt(7);
//
//		Toast.makeText(getApplicationContext(), note_idx + ", " + note_type+ ", " + pnote_category+ ", " + pnote_title+ ", " + pnote_select+ ", " + pnote_answer+ ", " + pnote_user+ ", " + pnote_is_used, Toast.LENGTH_LONG).show();
//		result.moveToNext();
//	}
//
//}          
//result.close();
// ===

// 데이터 수정 (UPDATE)
//dbAdapter.updateNote(1, "객" , "과학", "우주에서 지구가 가장 크다","예|아니오","아니오",1,1);
// ====
package com.project2225.perfectscore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.project2225.perfectscore.container.Constant;
import com.project2225.perfectscore.database.NotesDBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.textservice.SpellCheckerService.Session;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HomeActivity extends Activity {
	ImageView ivAdd;
	ImageView ivList;
	ImageView ivShare;
	ImageView ivSetting;
	private int mState; // 현재 통신 상태
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device
	public static final int MESSAGE_READ = 5;
	public static final int MESSAGE_WRITE = 6;
	public static final int MESSAGE_TOAST = 7;
	public static final int CONNECT_STATE = 8;
	public static final int MESSAGE_READ_TEST = 10;
	private static final int REQUEST_CONNECT_DEVICE = 11;
	private static final int REQUEST_ENABLE_BT = 22;
	private static final int REQUEST_SELECT_QUESTION = 33;
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");// ("fa87c0d0-afac-11de-8a39-0800200c9a66");
	// private UUID MY_UUID;// ("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final String NAME_SECURE = "BLBT";

	private char STX = '';
	private char ETX = '';
	private static final String split1 = "│";
	private static final String split2 = "|";
	private thOpenServer mthOpenServer; // 서버 스레드
	private thConnect mthConnect; // 클라이언트 스레드
	private BluetoothServerSocket mmServerSocket = null; // 서버용 소켓
	private BluetoothSocket mmClientSocket = null; // 클라이언트용 소켓
	private BluetoothAdapter mBluetoothAdapter = null;

	private NotesDBAdapter dbAdapter;
	private static final String TAG = "NotesDbAdapter";
	SQLiteDatabase mainDB = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		dbAdapter = new NotesDBAdapter(this);
		dbAdapter.open();

		ivAdd = (ImageView) findViewById(R.id.iv_add);
		ivList = (ImageView) findViewById(R.id.iv_list);
		ivShare = (ImageView) findViewById(R.id.iv_share);
		ivSetting = (ImageView) findViewById(R.id.iv_setting);

		ivAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, AddActivity.class);
				startActivity(i);

				// sendData(1,"객", "사회", "대한민국은 유럽에 포함되어 있다.", "예" +
				// split2+"아니오", "2",1,1);

			}
		});

		ivList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, ListActivity.class);
				startActivity(i);
			}
		});

		ivShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences pref = getSharedPreferences(
						Constant.KEY_PREF, MODE_PRIVATE);
				if (pref.getBoolean(Constant.KEY_IS_SERVER, false)) {
					// 서버 ====
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

					// 플루투스가 활성화 되어 있지 않으면 사용자에게 요청.
					if (!mBluetoothAdapter.isEnabled()) {
						Intent enableIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					} else {
						mthOpenServer = new thOpenServer();
						mthOpenServer.start();
					}
					// ======
				} else {
					// 클라이언트
					Intent Shareintent = new Intent(getApplicationContext(),
							ShareActivity.class);
					startActivityForResult(Shareintent, REQUEST_CONNECT_DEVICE);
					// =====
				}

			}
		});

		ivSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, SettingActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		// int mSelectAlarm = 0; // 알람 선택 1, 2, 3 순으로 번호 저장
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// 블루투스 디바이스 리스트에서 디바이스 한개 선택했을 경우 리턴
			if (resultCode == Activity.RESULT_OK) {
				// 장치의 MAX 주소를 얻는다.
				String address = data.getExtras().getString(
						ShareActivity.EXTRA_DEVICE_ADDRESS);
				// BluetoothDevice 객체를 얻는다.
				BluetoothDevice device = BluetoothAdapter.getDefaultAdapter()
						.getRemoteDevice(address);
				mthConnect = new thConnect(device);
				mthConnect.start();
				uiHandler.sendEmptyMessage(0);
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getApplicationContext(),
						"Success Bluetooth Enable", Toast.LENGTH_SHORT).show();

				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				mthOpenServer = new thOpenServer();
				mthOpenServer.start();

			} else {
				Toast.makeText(getApplicationContext(),
						"Fail Bluetooth Enable", Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_SELECT_QUESTION:
			String type=data.getStringExtra(Constant.KEY_TYPE);
			String category=data.getStringExtra(Constant.KEY_CATEGORY);
			String question=data.getStringExtra(Constant.KEY_QUESTION);
			String selection=data.getStringExtra(Constant.KEY_SELECTION);
			String answer=data.getStringExtra(Constant.KEY_ANSWER);
			sendData(1, type, category, question, selection, answer, 2, 1);
			break;
		}
	}

	// what arg1 arg2 obj
	// mHandler.obtainMessage(MESSAGE_READ, bytes, -1, string).sendToTarget();
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case MESSAGE_WRITE:
					break;
				case MESSAGE_READ:
					String SubCodeString = "";
					int nOffsetStart = 0,
					nOffsetCode = 2,
					i = 0;
					String readMessage = msg.obj.toString();
					if (readMessage.isEmpty() == false) {
						if (readMessage.length() > 0) {
							String CodeString = readMessage.substring(
									nOffsetStart, nOffsetCode);

							if (CodeString.equals("P1")) {
								SubCodeString = readMessage.substring(
										nOffsetStart + nOffsetCode,
										readMessage.length());

								//Toast.makeText(getApplicationContext(),"P1 Receive Data: " + SubCodeString,Toast.LENGTH_SHORT).show();

								String[] Stringlist = SubCodeString
										.split(split1);
								dbAdapter.insertNote(Stringlist[0],
										Stringlist[1], Stringlist[2],
										Stringlist[3], Stringlist[4],
										Integer.parseInt(Stringlist[5]),
										Integer.parseInt(Stringlist[6]));

							} else {
								Toast.makeText(getApplicationContext(),
										"Read Else: " + readMessage,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(), msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void sendData(int sendData_Type, String note_type,
			String pnote_category, String pnote_title, String pnote_select,
			String pnote_answer, int pnote_user, int pnote_is_used) {
		/*
		 * if (mState != STATE_CONNECTED) { Toast.makeText(this,
		 * "Not connected", Toast.LENGTH_SHORT).show(); return; }
		 */
		if (mthOpenServer == null && mthConnect == null) {
			return;
		}
		String MsgData = "";
		String sendDataType = "";
		if (sendData_Type == 1) // 문제 보내기
			sendDataType = "P1";
		if (sendData_Type == 2)
			sendDataType = "P2";

		MsgData = sendDataType + note_type + split1 + pnote_category + split1
				+ pnote_title + split1 + pnote_select + split1 + pnote_answer
				+ split1 + String.valueOf(pnote_user) + split1
				+ String.valueOf(pnote_is_used);
		if (MsgData.length() > 0) {
			MsgData = STX + MsgData + ETX;
			byte[] send = MsgData.getBytes();
			if (mthOpenServer != null)
				mthOpenServer.write(send);
			else if (mthConnect != null)
				mthConnect.write(send);
		}
	}
	
	private Handler uiHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				AlertDialog.Builder dialog=new AlertDialog.Builder(HomeActivity.this);
				dialog.setTitle("공유");
				String[] items=new String[]{"문제 받기","문제 보내기"};
				dialog.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which==0){
							
						}else if(which==1){
							Intent intent=new Intent(HomeActivity.this,SendActivity.class);
							startActivityForResult(intent, REQUEST_SELECT_QUESTION);
						}
					}
				});
				dialog.show();
				break;

			default:
				break;
			}
		};
	};

	// ========================================
	// 서버 스레드
	// ========================================
	private class thOpenServer extends Thread {
		private InputStream mmInStream = null;
		private OutputStream mmOutStream = null;

		private byte[] buffer = new byte[1024];
		private String ReceiveData;
		private String cReceiveData = "";
		private int nSTXIndex = -1, nETXIndex = -1;
		boolean runflag = true;

		public thOpenServer() {
			BluetoothServerSocket tmp = null;
			try {
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
						NAME_SECURE, MY_UUID);
			} catch (IOException e) {
				mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
						e.getMessage().toString()).sendToTarget();
			}
			mmServerSocket = tmp;
			Toast.makeText(getApplicationContext(), "Open Server",
					Toast.LENGTH_SHORT).show();
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int bytes;
			BluetoothSocket socket = null;
			mState = STATE_CONNECTED;
			while (runflag) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
							e.getMessage().toString()).sendToTarget();
					break;
				}
				if (socket != null) {
					uiHandler.sendEmptyMessage(0);
					try {
						mHandler.obtainMessage(CONNECT_STATE, 0, 0, "TRUE")
								.sendToTarget();
						mmInStream = socket.getInputStream();
						mmOutStream = socket.getOutputStream();
					} catch (IOException e) {
						mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
								.sendToTarget();
						mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
								e.getMessage().toString()).sendToTarget();
					}
					/*
					 * while (true) { try { // 데이터 받음 bytes =
					 * mmInStream.read(buffer);
					 * mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
					 * buffer).sendToTarget(); } catch (IOException e) {
					 * mHandler.obtainMessage(CONNECT_STATE, 0, 0,
					 * "FALSE").sendToTarget();
					 * mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
					 * e.getMessage().toString()).sendToTarget(); break; } }
					 */

					while (runflag) {
						try {
							bytes = mmInStream.read(buffer);
							ReceiveData = ReceiveData
									+ new String(buffer, 0, bytes);
							nSTXIndex = ReceiveData.indexOf(STX);
							if (nSTXIndex >= 0) {
								nETXIndex = ReceiveData.indexOf(ETX);
								if (nETXIndex >= 0) {
									cReceiveData = ReceiveData.substring(
											nSTXIndex + 1, nETXIndex);
									ReceiveData = "";
									nSTXIndex = -1;
									nETXIndex = -1;
								}
							}
							if (cReceiveData != "") {
								mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
										cReceiveData).sendToTarget();
								cReceiveData = "";
							}
						} catch (IOException e) {
							mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
									.sendToTarget();
							mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
									e.getMessage().toString()).sendToTarget();
							break;
						}
					}
				}
			}
			this.cancel();
		}

		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				mState = STATE_NONE;
				mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
						.sendToTarget();
				mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
						e.getMessage().toString()).sendToTarget();
			}
		}

		public void thstop() {
			// mmOutStream.write(buffer);
			runflag = false;
		}

		private void cancel() {
			try {
				// if(rd_Connect.isChecked() == true){
				// sendData("BF"); // 블루투스 모듈 접속 해제
				// }
				// else {
				// mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
				// "Not Connected").sendToTarget();
				// }
				mState = STATE_NONE;
				if (mmInStream != null)
					mmInStream.close();
				if (mmOutStream != null)
					mmOutStream.close();
				if (mmServerSocket != null)
					mmServerSocket.close();
			} catch (IOException e) {
				mState = STATE_NONE;
				mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
						.sendToTarget();
				mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
						e.getMessage().toString()).sendToTarget();
			}
		}
	}

	// ========================================
	// 클라이언트 스레드
	// ========================================
	private class thConnect extends Thread {

		private InputStream mmInStream = null;
		private OutputStream mmOutStream = null;
		private byte[] buffer = new byte[1024];
		private String ReceiveData;
		private String cReceiveData = "";
		private int nSTXIndex = -1, nETXIndex = -1;
		boolean runflag = true;

		public thConnect(BluetoothDevice device) {
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
						.sendToTarget();
				mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
						e.getMessage().toString()).sendToTarget();
			}
			mmClientSocket = tmp;
		}

		public void run() {

			int bytes;
			ReceiveData = "";
			// 일단 accept()를 호출하고 기다린다.
			while (runflag) {
				try {
					mmClientSocket.connect();
					mState = STATE_CONNECTED;
					mHandler.obtainMessage(CONNECT_STATE, 0, 0, "TRUE")
							.sendToTarget();
					try {
						mmInStream = mmClientSocket.getInputStream();
						mmOutStream = mmClientSocket.getOutputStream();
					} catch (IOException e) {
						mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
								.sendToTarget();
					}
					while (true) {
						try {
							bytes = mmInStream.read(buffer);
							ReceiveData = ReceiveData
									+ new String(buffer, 0, bytes);
							nSTXIndex = ReceiveData.indexOf(STX);
							if (nSTXIndex >= 0) {
								nETXIndex = ReceiveData.indexOf(ETX);
								if (nETXIndex >= 0) {
									cReceiveData = ReceiveData.substring(
											nSTXIndex + 1, nETXIndex);
									ReceiveData = "";
									nSTXIndex = -1;
									nETXIndex = -1;
								}
							}
							if (cReceiveData != "") {
								mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
										cReceiveData).sendToTarget();
								cReceiveData = "";
							}
						} catch (IOException e) {
							mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
									.sendToTarget();
							mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
									e.getMessage().toString()).sendToTarget();
							break;
						}
					}
				} catch (IOException e) {
					try {
						mState = STATE_NONE;
						mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
								e.getMessage().toString()).sendToTarget();
						mmClientSocket.close();
					} catch (IOException closeEx) {
						mState = STATE_NONE;
						mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
								.sendToTarget();
						mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
								e.getMessage().toString()).sendToTarget();
					}
					return;
				}
			}
			cancel();
		}

		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
						.sendToTarget();
				mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
						e.getMessage().toString()).sendToTarget();
			}
		}

		public void thstop() {
			// mmOutStream.write(buffer);
			runflag = false;
		}

		private void cancel() {
			try {
				mState = STATE_NONE;
				if (mmInStream != null)
					mmInStream.close();
				if (mmOutStream != null)
					mmOutStream.close();
				if (mmClientSocket != null)
					mmClientSocket.close();
			} catch (IOException e) {
				mHandler.obtainMessage(CONNECT_STATE, 0, 0, "FALSE")
						.sendToTarget();
				mHandler.obtainMessage(MESSAGE_TOAST, 0, 0,
						e.getMessage().toString()).sendToTarget();
			}
		}
	}
}

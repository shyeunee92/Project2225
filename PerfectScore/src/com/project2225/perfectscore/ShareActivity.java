package com.project2225.perfectscore;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ShareActivity extends Activity{
	
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	//지역 블루투스 어탭터
	private BluetoothAdapter mBtAdapter = null;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private static final int REQUEST_ENABLE_BT = 3;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_share);
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_share);
        setResult(Activity.RESULT_CANCELED);
		Button scanButton = (Button) findViewById(R.id.btn_Scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
            }
        });
        
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1);// R.layout.activity_device_name); //android.R.layout.simple_list_item_1);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1);//R.layout.activity_device_name);// android.R.layout.simple_list_item_1);
		
		// 페어링된 장치를 표시하는 listview
		ListView PairedListView = (ListView)findViewById(R.id.listView_Paired);
		PairedListView.setAdapter(mPairedDevicesArrayAdapter);
		PairedListView.setOnItemClickListener(mDeviceClickListener);
		
		// 새롭게 발견된 장치를 표시하는 Listview
		ListView NewDevicesListView = (ListView)findViewById(R.id.listView_NewDevices);
		NewDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		NewDevicesListView.setOnItemClickListener(mDeviceClickListener);
		
        // 장치가 검색되었을 경우의 방송 수신 등록
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		
		// 장치 검색이 종료되는 경우의 방송 수신 등록
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		
		// 지역 블루투스 어댑터를 얻는다.
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
        // 플루투스가 활성화 되어 있지 않으면 사용자에게 요청.
        if (!mBtAdapter.isEnabled()){
        	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        
        // 페어링된 장치들을 얻는다.
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        		
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "No devices have been paired";
            mPairedDevicesArrayAdapter.add(noDevices);
        }
        doDiscovery();
	}
	
	private void doDiscovery() {
		mPairedDevicesArrayAdapter.clear();
		
		// 페어링된 장치들을 얻는다.
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        
		if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = "No devices have been paired";
            mPairedDevicesArrayAdapter.add(noDevices);
        }
		
		mNewDevicesArrayAdapter.clear();
	    // 검색 중임을 표시한다.
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.String_Scanning);
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }
	
	 @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
        	mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

	// 리스트 뷰에 있는 장치를 클릭하였을 경우 처리
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return;
        }
    };	 
    
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	
	        // 검색이 장치를 발견한 경우
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // 인텐트로부터 BluetoothDevice 객체를 얻는다.
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // 이미 페어링되었으면 건너뛴다.
	            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
	                mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            }
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	            setProgressBarIndeterminateVisibility(false);
	            setTitle("ACTION_DISCOVERY_FINISHED");
	            if (mNewDevicesArrayAdapter.getCount() == 0) {
	                String noDevices = "No devices found";
	                mNewDevicesArrayAdapter.add(noDevices);
	            }
	        }
	    }
	};
	
	// 이벤트 발생
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch(requestCode){
		 case REQUEST_ENABLE_BT:
			 // 블루투스를 활성화하는 액션에 리턴할 경우
			 if(resultCode == Activity.RESULT_OK){
				 Toast.makeText(getApplicationContext(), "블루투스 장치가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
				 doDiscovery();
			 } else {
				 Toast.makeText(getApplicationContext(), "블루투스 장치가 활성화 되지 않았습니다.", Toast.LENGTH_SHORT).show();
				 finish();
				 return;
			 }	 
		 }
	}
}

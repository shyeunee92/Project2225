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
	//���� ������� ������
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
		
		// ���� ��ġ�� ǥ���ϴ� listview
		ListView PairedListView = (ListView)findViewById(R.id.listView_Paired);
		PairedListView.setAdapter(mPairedDevicesArrayAdapter);
		PairedListView.setOnItemClickListener(mDeviceClickListener);
		
		// ���Ӱ� �߰ߵ� ��ġ�� ǥ���ϴ� Listview
		ListView NewDevicesListView = (ListView)findViewById(R.id.listView_NewDevices);
		NewDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		NewDevicesListView.setOnItemClickListener(mDeviceClickListener);
		
        // ��ġ�� �˻��Ǿ��� ����� ��� ���� ���
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		
		// ��ġ �˻��� ����Ǵ� ����� ��� ���� ���
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		
		// ���� ������� ����͸� ��´�.
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
        // �÷������� Ȱ��ȭ �Ǿ� ���� ������ ����ڿ��� ��û.
        if (!mBtAdapter.isEnabled()){
        	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        
        // ���� ��ġ���� ��´�.
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
		
		// ���� ��ġ���� ��´�.
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
	    // �˻� ������ ǥ���Ѵ�.
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

	// ����Ʈ �信 �ִ� ��ġ�� Ŭ���Ͽ��� ��� ó��
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
	
	        // �˻��� ��ġ�� �߰��� ���
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // ����Ʈ�κ��� BluetoothDevice ��ü�� ��´�.
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // �̹� ���Ǿ����� �ǳʶڴ�.
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
	
	// �̺�Ʈ �߻�
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch(requestCode){
		 case REQUEST_ENABLE_BT:
			 // ��������� Ȱ��ȭ�ϴ� �׼ǿ� ������ ���
			 if(resultCode == Activity.RESULT_OK){
				 Toast.makeText(getApplicationContext(), "������� ��ġ�� Ȱ��ȭ �Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
				 doDiscovery();
			 } else {
				 Toast.makeText(getApplicationContext(), "������� ��ġ�� Ȱ��ȭ ���� �ʾҽ��ϴ�.", Toast.LENGTH_SHORT).show();
				 finish();
				 return;
			 }	 
		 }
	}
}

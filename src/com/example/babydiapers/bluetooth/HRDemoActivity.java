package com.example.babydiapers.bluetooth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.babydiapers.R;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;

/* this activity's purpose is to show how to use particular type of devices in easy and fast way */
public class HRDemoActivity extends Activity {
	
	private Handler mHandler = null;
	private BluetoothManager mBTManager = null;
	private BluetoothAdapter mBTAdapter = null;
	private BluetoothDevice  mBTDevice = null;
	private BluetoothGatt    mBTGatt = null;
	private BluetoothGattService        mBTService = null;
	private BluetoothGattCharacteristic mBTValueCharacteristic = null,mBTValueCharacteristic_Bat=null;
	// UUDI od Heart Rate service:
	final static private UUID mHeartRateServiceUuid = BleDefinedUUIDs.Service.HEART_RATE;
	final static private UUID mHeartRateCharacteristicUuid = BleDefinedUUIDs.Characteristic.SIMPLEPROFILE_CHAR6;
	final static private UUID mHeartRateCharacteristicBatUuid = BleDefinedUUIDs.Characteristic.SIMPLEPROFILE_CHAR7;
	private TextView mConsole = null;
	private TextView mTextView  = null;
	private ProgressBar mDiapersProgressBar=null,mBat_Level=null;
	private TextView mTips = null;
	private TextView tvBatLevel = null;
	private TextView tvUpdate = null;
	private BluetoothGatt mBTGatt_Bat=null;
	private int imageIds[];
	private ArrayList<ImageView> images;
	private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem; //当前页面
//	private TextView mTips = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hrdemo);
		mConsole = (TextView) findViewById(R.id.hr_console_item);
		log("Creating activity");
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
//		setTitle("Heart Rate Demo");
		setTitle("BabyDiapers Demo");
		mConsole = (TextView) findViewById(R.id.hr_console_item);
		mTextView = (TextView) findViewById(R.id.hr_text_view);
//		mDiapersProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		mTips = (TextView) findViewById(R.id.tvTips);
		
		tvUpdate = (TextView) findViewById(R.id.tvUpdate);
//		mTips = (TextView) findViewById(R.id.tvTips);
		//图片ID
        imageIds = new int[]{ 
            R.drawable.a,    
            R.drawable.b,    
            R.drawable.c,    
            R.drawable.d,    
            R.drawable.e,
            R.drawable.f
            
        };
      //显示的图片
        images = new ArrayList<ImageView>();
        for(int i =0; i < imageIds.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imageIds[i]);
            
            images.add(imageView);
        }
        mViewPager = (ViewPager) findViewById(R.id.vp);
        
        adapter = new ViewPagerAdapter(); 
        mViewPager.setAdapter(adapter);
        mViewPager.setVisibility(View.GONE);
		mHandler = new Handler();
		log("Activity created");
	}
	
	private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return images.size();
        }

        //是否是同一张图片
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            // TODO Auto-generated method stub
//            super.destroyItem(container, position, object);
//            view.removeViewAt(position);
            view.removeView(images.get(position));
            
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            // TODO Auto-generated method stub
            view.addView(images.get(position));
            
            return images.get(position);
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        
        //每隔2秒钟切换一张图片
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
    }
    
    //切换图片
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            currentItem = (currentItem +1) % imageIds.length;
            //更新界面
//            handler.sendEmptyMessage(0);
            handler.obtainMessage().sendToTarget();
        }
        
    }
    
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //设置当前页面
            mViewPager.setCurrentItem(currentItem);
        }
        
    };

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		log("Resuming activity");
		
		// first check if BT/BLE is available and enabled
		if(initBt() == false) return;
		if(isBleAvailable() == false) return;
		if(isBtEnabled() == false) return;
		
		// then start discovering devices around
		startSearchingForHr();
		
		log("Activity resumed");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		disableNotificationForHr();
		disableNotificationForBatLevel();
		disconnectFromDevice();
		closeGatt();
	};

	private boolean initBt() {
		mBTManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		if(mBTManager != null) mBTAdapter = mBTManager.getAdapter();
		
		return (mBTManager != null) && (mBTAdapter != null);
	}
	
	private boolean isBleAvailable() {
		log("Checking if BLE hardware is available");
		
		boolean hasBle = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		if(hasBle && mBTManager != null && mBTAdapter != null) {
			log("BLE hardware available");
		}
		else {
			log("BLE hardware is missing!");
			return false;
		}
		return true;
	}
	
	private boolean isBtEnabled() {
		log("Checking if BT is enabled");
		if(mBTAdapter.isEnabled()) {
			log("BT is enabled");
		}
		else {
			log("BT is disabled. Use Setting to enable it and then come back to this app");
			return false;
		}
		return true;
	}

	private void startSearchingForHr() {
		// we define what kind of services found device needs to provide. In our case we are interested only in
		// Heart Rate service
		final UUID[] uuids = new UUID[] { mHeartRateServiceUuid };
		mBTAdapter.startLeScan(uuids, mDeviceFoundCallback);
		// results will be returned by callback
		log("Search for devices providing BabyDiapers service started");
		
		// please, remember to add timeout for that scan
		Runnable timeout = new Runnable() {
            @Override
            public void run() {
				if(mBTAdapter.isDiscovering() == false) return;
				stopSearchingForHr();	
            }
        };
        mHandler.postDelayed(timeout, 10000); //10 seconds		
	}

	private void stopSearchingForHr() {
		mBTAdapter.stopLeScan(mDeviceFoundCallback);
		log("Searching for devices with BabyDiapers service stopped");
	}
	
	private void connectToDevice() {
		log("Connecting to the device NAME: " + mBTDevice.getName() + " HWADDR: " + mBTDevice.getAddress());
		mBTGatt = mBTDevice.connectGatt(this, true, mGattCallback);
		mBTGatt_Bat = mBTDevice.connectGatt(this, true, mGattCallback_Bat);
	}
	
	private void disconnectFromDevice() {
		log("Disconnecting from device");
		if(mBTGatt != null) mBTGatt.disconnect();
		if(mBTGatt_Bat != null) mBTGatt_Bat.disconnect();
	}
	
	private void closeGatt() {
		if(mBTGatt != null) mBTGatt.close();
		mBTGatt = null;
		if(mBTGatt_Bat != null) mBTGatt_Bat.close();
		mBTGatt_Bat = null;
	}
	
	private void discoverServices() {
		log("Starting discovering services");
		mBTGatt.discoverServices();
		mBTGatt_Bat.discoverServices();
	}
	
	private void getHrService() {
		log("Getting BabyDiapers Service");
		mBTService = mBTGatt.getService(mHeartRateServiceUuid);
		
		if(mBTService == null) {
			log("Could not get BabyDiapers Service");
		}
		else {
			log("BabyDiapers Service successfully retrieved");
			getHrCharacteristic();
		}
	}
	
	private void getHrCharacteristic() {
		log("Getting BabyDiapers Measurement characteristic");
		mBTValueCharacteristic = mBTService.getCharacteristic(mHeartRateCharacteristicUuid);
		mBTValueCharacteristic_Bat = mBTService.getCharacteristic(mHeartRateCharacteristicBatUuid);
		if(mBTValueCharacteristic == null) {
			log("Could not find BabyDiapers BabyPeeCH Characteristic");
		}
		else {
			log("BabyDiapers BabyPeeCH characteristic retrieved properly");
			enableNotificationForHr();			
		}
		if(mBTValueCharacteristic_Bat == null) {
			log("Could not find BabyDiapers BAT_LEVEL Characteristic");
		}
		else {
			log("BabyDiapers BAT_LEVEL characteristic retrieved properly");			
			enableNotificationForBatLevel();
		}		
		
	}
	
	private void enableNotificationForHr() {
		log("Enabling notification for BabyDiapers");
        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic, true);       
        if(!success) {
        	log("Enabling notification failed!");
        	return;
        }

//        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.CHAR_CLIENT_CONFIG);
        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.BabyPeeCH);
        if(descriptor != null) {
	        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
	        mBTGatt.writeDescriptor(descriptor);	        
	       
	        log("Notification enabled");
        }		
        else {
        	log("Could not get descriptor for characteristic! Notification are not enabled.");
        }
	}
	
	private void enableNotificationForBatLevel() {
		log("Enabling notification for BabyDiapers");
		boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic_Bat, true);
//        boolean success = mBTGatt_Bat.setCharacteristicNotification(mBTValueCharacteristic_Bat, true);       
        if(!success) {
        	log("Enabling notification failed!");
        	return;
        }

//        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.CHAR_CLIENT_CONFIG);
        BluetoothGattDescriptor descriptor = mBTValueCharacteristic_Bat.getDescriptor(BleDefinedUUIDs.Descriptor.BAT_LEVEL);
      
        if(descriptor != null) {
	        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//	        mBTGatt.writeDescriptor(descriptor);        
	        mBTGatt_Bat.writeDescriptor(descriptor); 
	        log("Notification enabled");
        }		
        else {
        	log("Could not get descriptor for characteristic! Notification are not enabled.");
        }
	}
	private void disableNotificationForHr() {
		log("Disabling notification for BabyDiapers");
        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic, false);
        if(!success) {
        	log("Disabling notification failed!");
        	return;
        }

//        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.CHAR_CLIENT_CONFIG);
        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.BabyPeeCH);
        if(descriptor != null) {
	        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	        mBTGatt.writeDescriptor(descriptor);
	        log("Notification disabled");
        }		
        else {
        	log("Could not get descriptor for characteristic! Notification could be still enabled.");
        }
	}
	
	private void disableNotificationForBatLevel() {
		log("Disabling notification for BabyDiapers");
        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic_Bat, false);
//        boolean success = mBTGatt_Bat.setCharacteristicNotification(mBTValueCharacteristic_Bat, false);
        if(!success) {
        	log("Disabling notification failed!");
        	return;
        }

//        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.CHAR_CLIENT_CONFIG);
        BluetoothGattDescriptor descriptor = mBTValueCharacteristic_Bat.getDescriptor(BleDefinedUUIDs.Descriptor.BAT_LEVEL);
        if(descriptor != null) {
	        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	        mBTGatt.writeDescriptor(descriptor);
//	        mBTGatt_Bat.writeDescriptor(descriptor);
	        log("Notification disabled");
        }		
        else {
        	log("Could not get descriptor for characteristic! Notification could be still enabled.");
        }
	}
	
	private void getAndDisplayHrValue() {
    	byte[] raw = mBTValueCharacteristic.getValue();
//    	int index = ((raw[0] & 0x01) == 1) ? 2 : 1;
//    	int format = (index == 1) ? BluetoothGattCharacteristic.FORMAT_UINT8 : BluetoothGattCharacteristic.FORMAT_UINT16;
//    	final int value = mBTValueCharacteristic.getIntValue(format, index);
//    	final String description = value + " bpm";
    	int val1=(raw[0]-0x30),val2=((raw[2]-0x30)),val3=(raw[3]-0x30);
    	final float value=(float) (val1+val2*0.1+val3*0.01);
    	final String description =value+ " V";   	
    	
    	runOnUiThread(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				mTextView.setText(description);
				mDiapersProgressBar.setProgress((int)value*100);
				final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
				tvUpdate.setText(timestamp);
				//Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
				//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				
				if(value<3.30){
					mTips.setText("宝宝的纸尿裤已经尿湿了，可以给宝宝更换新的纸尿裤啦！！！");
					mViewPager.setVisibility(View.VISIBLE);
					r.play();
				}else{
					mTips.setText("");
					r.stop();
					mViewPager.setVisibility(View.GONE);
				}
			}
    	});
	}
	
	private void getAndDisplayBatLevelValue() {
    	byte[] raw = mBTValueCharacteristic_Bat.getValue();
//    	int index = ((raw[0] & 0x01) == 1) ? 2 : 1;
//    	int format = (index == 1) ? BluetoothGattCharacteristic.FORMAT_UINT8 : BluetoothGattCharacteristic.FORMAT_UINT16;
//    	final int value = mBTValueCharacteristic.getIntValue(format, index);
//    	final String description = value + " bpm";
    	int val1=(raw[0]-0x30),val2=((raw[2]-0x30)),val3=(raw[3]-0x30);
    	final float value=(float) (val1+val2*0.1+val3*0.01);
    	final String description =value+ " V";
    	runOnUiThread(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				tvBatLevel.setText(description);
				mBat_Level.setProgress((int)value*100);
				final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
				tvUpdate.setText(timestamp);
				//Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
				//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				if(value<2.0){
					mTips.setText("电池电压低，请更换电池！！！");
					r.play();
				}else{
					mTips.setText("");
					r.stop();
				}
			}
    	});
	}
    private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        	// here we found some device with heart rate service, lets save it:
        	HRDemoActivity.this.mBTDevice = device;
        	log("Device with BabyDiapers service discovered. HW Address: "  + device.getAddress());
        	stopSearchingForHr();
        	
        	connectToDevice();
        }
    };	
	
    /* callbacks called for any action on HR Device */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	log("Device connected");
            	discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            	log("Device disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	if(status == BluetoothGatt.GATT_SUCCESS) {
        		log("Services discovered");
        		getHrService();
        	}
        	else {
        		log("Unable to discover services");
        	}
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
        	if(characteristic.equals(mBTValueCharacteristic)) {
        		getAndDisplayHrValue();
//        		getAndDisplayBatLevelValue();
        	}
        	if(characteristic.equals(mBTValueCharacteristic_Bat)) {
        		getAndDisplayBatLevelValue();
        	}
        }       
        
        /* the rest of callbacks are not interested for us */
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {}


        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {};
        
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {};
    };
     
    /* callbacks called for any action on HR Device */
    private final BluetoothGattCallback mGattCallback_Bat = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	log("Device connected");
            	discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            	log("Device disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	if(status == BluetoothGatt.GATT_SUCCESS) {
        		log("Services discovered");
        		getHrService();
        	}
        	else {
        		log("Unable to discover services");
        	}
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic)
        {
        	if(characteristic.equals(mBTValueCharacteristic)) {
        		getAndDisplayHrValue();
        	}
        	if(characteristic.equals(mBTValueCharacteristic_Bat)) {
        		getAndDisplayBatLevelValue();
        	}
        }       
        
        /* the rest of callbacks are not interested for us */
        
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {}


        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {};
        
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {};
    };
    
    
	// put new logs into the UI console
	private void log(final String txt) {
		if(mConsole == null) return;
		
		final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mConsole.setText(timestamp + " : " + txt + "\n" + mConsole.getText());
			}		
		});
	}
}

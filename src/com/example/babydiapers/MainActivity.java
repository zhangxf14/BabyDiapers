package com.example.babydiapers;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;






import com.example.babydiapers.bluetooth.BleDefinedUUIDs;
import com.example.babydiapers.bluetooth.BleWrapper;
import com.example.babydiapers.bluetooth.BleWrapperUiCallbacks;
import com.example.babydiapers.bluetooth.DeviceListAdapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babydiapers.CircleProgressBar;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class MainActivity extends BaseActivity {	
	//add new
	private static final long SCANNING_TIMEOUT = 1 * 1000; /* 5 seconds */
	private static final int ENABLE_BT_REQUEST_ID = 1;	
	private Handler mHandler = new Handler();
	private DeviceListAdapter mDevicesListAdapter = null;
	private BleWrapper mBleWrapper = null;	
	//
	private Handler mHandler_Baby = null;
	private BluetoothManager mBTManager = null;
	private BluetoothAdapter mBTAdapter = null;
	private BluetoothDevice  mBTDevice = null;
	private BluetoothGatt    mBTGatt = null;
	private BluetoothGatt    mBTGatt_Bat=null;
	private BluetoothGattService        mBTService = null;
	private BluetoothGattCharacteristic mBTValueCharacteristic = null,mBTValueCharacteristic_Bat=null;
	final static private UUID mBabyDiaperServiceUuid = BleDefinedUUIDs.Service.BABY_DIAPER;
	final static private UUID mBabyDiaperCharacteristicUuid = BleDefinedUUIDs.Characteristic.SIMPLEPROFILE_CHAR6;
	final static private UUID mBabyDiaperCharacteristicBatUuid = BleDefinedUUIDs.Characteristic.SIMPLEPROFILE_CHAR7;
	private TextView tvTips = null;
	private TextView tvUpdate = null;
	private TextView tvTitle=null;
	
	private int imageIds[];
	private ArrayList<ImageView> images;
	private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem; //当前页面
	private CircleProgressBar mCircleBar;
	private int iCount=0,iTime = 0;  
	private static final int TIME = 50;  //1s	
	private static final int TIME_SET = 1000;  //1s	
	private TextView tvBirthdate;
	private TextView tvDays;
	private ImageView ivPictrue;

	private ArrayList<UserInfo> userInfos;
	private ArrayList<UserId> userIds;
	private String usercode;
	private byte[]  userPictrue;
	private String username;
	private String sex;
	private String birthdate;
	private Bitmap bitmap=null;	
	//获取通知管理器，用于发送通知	
	final int NOTIFYID_1 = 123;	//第一个通知的ID
	final int NOTIFYID_2 = 124;	//第二个通知的ID
	protected double dist;		//蓝牙设备距离计算值
	protected int BLERSSI;		//BLE的RSSI
	private int distSet;//设定安全距离
	protected BabyInfo data=new BabyInfo();
	
	private float RSSI_SET;
	private float FACTOR_SET;
	AlertDialog dialog; // 定义个全局dialog，用关闭对话框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgentApplication.getInstance().addActivity(this);		
		// create BleWrapper with empty callback object except uiDeficeFound function (we need only that here) 
        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
        	@Override
        	public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
        		handleFoundDevice(device, rssi, record);
        	}
        });        
        // check if we have BT and BLE on board
        if(mBleWrapper.checkBleHardwareAvailable() == false) {
        	bleMissing();
        }
        SoundUtil.initSoundPool(this);
        splash();
        setContentView(R.layout.activity_main);
        loadUserInfo();
		initViews();
		initEvents();
		mHandler_Baby = new Handler();
		distSet=PreferenceUtils.getPrefInt(this, "dist", 5);
		RSSI_SET=PreferenceUtils.getPrefFloat(MainActivity.this, "RSSI", 59.0f);        
		FACTOR_SET=PreferenceUtils.getPrefFloat(MainActivity.this, "FACTOR", 2.0f);
		
	}
	/*
	 * 闪屏
	 */
	private void splash() {
		if(!SplashActivity.isSplash &&(!LoginActivity.isLogin)){
			Intent intent = new Intent(MainActivity.this, SplashActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_up,R.anim.activity_down);  
		}		
	}
	/*
	 * 加载用户信息
	 */
	private void loadUserInfo() {
		userIds=UserIdDao.getInstance(getApplicationContext()).queryAll();
		int count=userIds.size();
		if(count==0){
			Intent intent = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_up,R.anim.activity_down);	
		}else{
			int id=userIds.get(count-1).getId();
			userInfos=UserInfoDao.getInstance(getApplicationContext()).query(String.valueOf(id));
			getUserInfo(userInfos);
		}		
	}
	/*
	 * 获取用户信息
	 */
	private void getUserInfo(ArrayList<UserInfo> userInfo) {
			usercode=userInfo.get(0).getUsercode();
			userPictrue=userInfo.get(0).getUserPictrue();
			username=userInfo.get(0).getUsername();
			sex=userInfo.get(0).getSex();
			birthdate=userInfo.get(0).getBirthdate();
			/** 
			* 根据Bitmap字节数据转换成 Bitmap对象 
			* BitmapFactory.decodeByteArray() 方法对字节数据，从0到字节的长进行解码，生成Bitmap对像。 
			**/  
			bitmap = BitmapFactory.decodeByteArray(userPictrue, 0, userPictrue.length);	
	}
	/*
	 * 定时搜索蓝牙设备
	 */
	Handler handler_timer = new Handler();  
	Runnable runnable = new Runnable() {  
       @Override  
       public void run() {  
           // handler自带方法实现定时器  
	       try {  
	           handler_timer.postDelayed(this, TIME);
	           if(iCount>100){
	        	   iCount=0;
	           }
	           mCircleBar.setProgress(iCount++);
	           mCircleBar.setmTxtHint1("正在搜索");
	           mCircleBar.setmTxtHint2("请等待");
	           iTime++;
//	           SoundUtil.play(R.raw.beep51, 0);
	           if(iTime>101){	        	   
	        	   if(mDevicesListAdapter==null|mDevicesListAdapter.getCount()==0){	        		   
	        		   mCircleBar.setmTxtHint1("未搜索到");  
	        		   mCircleBar.setmTxtHint2("请重试");
	        		   mCircleBar.setProgress(50);
	        	   }else{
	        		   mCircleBar.setProgress(50);
	        		   SoundUtil.play(R.raw.pegconn, 0);
	        		   singleChoiceDialog();
	        		   iCount=0;
	        	   }
	        	   handler_timer.removeCallbacks(runnable);	        	   
	           }   	
	   			mBleWrapper.startScanning(); 
           } catch (Exception e) {
               e.printStackTrace();
           }  
	   }  
	};
	/**
	 * 初始化组件
	 */
	private void initViews() {
		/**
		 * 在Android程序设计中，通常来说在Actionbar中在条目过多时会显示三个竖着的小点的菜单，但在实机测试的时候发现并不显示，
		 * 查找资料并测试之后发现问题所在：如果该机器拥有实体的menu键则不在右侧显示溢出菜单，而改为按menu来生成。这样就不利于统一的界面风格。
		 * 我们可以改变系统探测实体menu键的存在与否来改变这个的显示。
		 */
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}		

		tvTips = (TextView) findViewById(R.id.tvTips);
		tvUpdate = (TextView) findViewById(R.id.tvUpdate);
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
        mCircleBar = (CircleProgressBar) findViewById(R.id.circleProgressbar);
        mCircleBar.setProgress(50);
        mCircleBar.setmTxtHint1("点击搜索");

        ActionBar actionBar = getActionBar();  
        actionBar.setDisplayHomeAsUpEnabled(true); 
          
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(  
                  ActionBar.LayoutParams.MATCH_PARENT,  
                  ActionBar.LayoutParams.MATCH_PARENT,  
                  Gravity.CENTER);  
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View titleView = inflater.inflate(R.layout.action_bar_title, null);  
        
        actionBar.setCustomView(titleView, lp); 
         
        actionBar.setDisplayShowHomeEnabled(true);//去掉导航  
        actionBar.setDisplayShowTitleEnabled(false);//去掉标题  
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  
        actionBar.setDisplayShowCustomEnabled(true);
        
        tvTitle=(TextView)findViewById(R.id.title);
        tvTitle.setText(username);
        
        ivPictrue=(ImageView) actionBar.getCustomView().findViewById(R.id.ivPictrue);
        if(bitmap!=null) ivPictrue.setImageBitmap(bitmap); 
        
        tvBirthdate=(TextView)findViewById(R.id.tvBirthdate);
        tvDays=(TextView)findViewById(R.id.tvDays);
        
        tvBirthdate.setText(birthdate);
        tvDays.setText(TimeUtil.getAge(birthdate));
	}
	/**
	 * 初始化事件
	 */
	private void initEvents() {
		mCircleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCircleBar.getmTxtHint1().equals("已连接")){
					return;
				}else if(mDevicesListAdapter.getCount()!=0 & mCircleBar.getmTxtHint1().equals("点击连接") ){
					singleChoiceDialog();
					return;
				}else{
//					SoundUtil.play(R.raw.ding, 0);
					iCount=0;//搜索，重新计数
					iTime=0;
					handler_timer.postDelayed(runnable, TIME); //每隔1s执行
					
				}
			}
		});
		
		ivPictrue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCircleBar.getmTxtHint1().equals("已连接")){
				   userListDialog();	
				}else{
				   openUserListActivity();	
				   SoundUtil.play(R.raw.pegconn, 0);
				}
				
			}
		});
		
	}
	/*
	 * 打开用户列表界面
	 */
	protected void openUserListActivity() {
		Intent intent = new Intent(MainActivity.this, UserListActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);		
	}
	/*
	 * 用户列表选择对话框
	 */
	protected void userListDialog() {
		AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
		.create();
		alert.setIcon(android.R.drawable.ic_dialog_info); // 设置对话框的图标
		alert.setTitle("选择用户"); // 设置对话框的标题
		alert.setMessage("当前用户已连接，真的要重新选择用户吗？"); // 设置要显示的内容
		// 添加取消按钮
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "不",
				new DialogInterface.OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
					}
				});
		// 添加确定按钮
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "是的",
				new DialogInterface.OnClickListener() {	
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						disconnectDevice();
						openUserListActivity();	
					}
				});
		alert.show(); // 创建对话框并显示			
	}
	/*
	 * 断开连接对话框
	 */
	protected void disconnectDialog() {
		AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
		.create();
		alert.setIcon(android.R.drawable.ic_dialog_info); // 设置对话框的图标
		alert.setTitle("断开连接"); // 设置对话框的标题
		alert.setMessage("真的要断开连接吗？"); // 设置要显示的内容
		// 添加取消按钮
		alert.setButton(DialogInterface.BUTTON_NEGATIVE, "不",
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
					}
				});
		// 添加确定按钮
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "是的",
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						disconnectDevice();						
					}
				});
		alert.show(); // 创建对话框并显示			
	}	
	
	/*
	 * 蓝牙连接对话框
	 */
	private void singleChoiceDialog(){
		//
		BluetoothDevice device = mDevicesListAdapter.getDevice(0);
	    if (device == null) return;
	    String[] deviceName= new String[mDevicesListAdapter.getCount()];
	    for(int i=0;i< mDevicesListAdapter.getCount();i++){
	    	device = mDevicesListAdapter.getDevice(i);	    	
	    	deviceName[i]=device.getName();
	    }
		//创建对话框
		new AlertDialog.Builder(MainActivity.this).setTitle("蓝牙连接").setIcon(
		     android.R.drawable.ic_dialog_info).setSingleChoiceItems(
	//		 new String[] { "Item1", "Item2" }, 0,
		     deviceName,-1,		 
		     new DialogInterface.OnClickListener() {			

				public void onClick(DialogInterface dialog, int which) {
				        dialog.dismiss();
		        		mCircleBar.setmTxtHint1("正在连接");
		        		mCircleBar.setmTxtHint2("请稍后");
		        		mCircleBar.setProgress(50);
				        final BluetoothDevice deviceFinal = mDevicesListAdapter.getDevice(which);  
			    	    MainActivity.this.mBTDevice = deviceFinal;
//			        	log("Device with BabyDiapers service discovered. HW Address: "  + deviceFinal.getAddress());
			        	stopSearchingForBabyDiaper();			        	
			        	connectToDevice();
			        	timerGetRSSI.run();
			        	handler_settings.postDelayed(runnable_settings, TIME_SET);
			        	
			      }
		     }).setNegativeButton("取消",  new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
//			    	   dialog.dismiss(); 
	        		   mCircleBar.setmTxtHint1("点击连接");
	        		   mCircleBar.setProgress(50);
	        		   mCircleBar.setmTxtHint2("");
			      }
			 }).show();		
	}
	/*
	 * 蓝牙RSSI计算距离 
	 * 计算公式：
	    d = 10^((abs(RSSI) - A) / (10 * n))
	             其中：
	    d - 计算所得距离
	    RSSI - 接收信号强度（负值）
	    A - 发射端和接收端相隔1米时的信号强度
	    n - 环境衰减因子
	 * 传入RSSI值，返回距离（单位：米）。其中，A参数赋了59，n赋了2.0。 
		由于所处环境不同，每台发射源（蓝牙设备）对应参数值都不一样。按道理，公式里的每项参数都应该做实验（校准）获得。
		当你不知道周围蓝牙设备准确位置时，只能给A和n赋经验值（如本例）。
	 */
	protected double calcDistByRSSI(int rssi) {
		//A和n的值，需要根据实际环境进行检测得出  
		double A_Value=74;  /**A - 发射端和接收端相隔1米时的信号强度*/  
		double n_Value=2.0; /** n - 环境衰减因子*/ 
		A_Value=RSSI_SET;
		n_Value=FACTOR_SET;
		
		int iRssi = Math.abs(rssi);  
	    double power = (iRssi-A_Value)/(10*n_Value);  
		return Math.pow(10,power);  
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
/* */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			settingsDialog();
			return true;
		}else if(id == R.id.action_disconnect){
			if(mCircleBar.getmTxtHint1().equals("已连接")){
				disconnectDialog();
			}else{
				Toast.makeText(this, "设备未连接！", Toast.LENGTH_LONG).show();
			}
			return true;	
		}else if(id == R.id.action_babylogs){
			openBabyLogActivity();
			return true;	
		}else if (id == R.id.action_calibrate) {
			calibrateDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	 * 超出安全距离启动定时器
	 */
	Handler handler_settings = new Handler();
	protected int iSet=0;
	protected boolean isTime=false;  
	Runnable runnable_settings = new Runnable() {  
       @Override  
       public void run() {  
           // handler自带方法实现定时器  
	       try {  
	    	   handler_settings.postDelayed(this, 60);//1分钟后
	    	   iSet++;
	    	   if(iSet>TIME_SET){
	    		   handler_settings.removeCallbacks(runnable_settings);	
	    		   isTime=true;	    		   
	    	   }
	    	           	   
	          
           } catch (Exception e) {
               e.printStackTrace();
           }  
	   }  
	};
	
	/*
	 * 设置对话框
	 */
	protected void settingsDialog() {
		// 得到编辑框的布局文件input_message
        LinearLayout inputMessage = (LinearLayout) this.getLayoutInflater().inflate( 
                R.layout.activity_settings, null);
        final EditText etDistance= (EditText) inputMessage.findViewById(R.id.etDistance);
        distSet=PreferenceUtils.getPrefInt(this, "dist", 5);
        etDistance.setText(String.valueOf(distSet));
       
        SpannableString s = new SpannableString( etDistance.getHint());  
        AbsoluteSizeSpan textSize = new AbsoluteSizeSpan(13,true);  
        s.setSpan(textSize,0,s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
  
        etDistance.setHint(s);  

        AlertDialog dialog; // 定义个全局dialog，用关闭对话框
        dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("请设置安全距离")
                .setMessage("提示：\n当您的宝宝穿戴了智能纸尿裤后，并保持与您的手机通信正常，一旦宝宝与您的手机的距离超出了安全距离，手机提示音将响起...")
                .setView(inputMessage)
                .setPositiveButton("保存并确定", new DialogInterface.OnClickListener() {                    

					@Override 
                    public void onClick(DialogInterface dialog, int which) {
                      distSet=Integer.parseInt(etDistance.getText().toString()); 
                      if(distSet>100 ||distSet <0){
                    	  Toast.makeText(MainActivity.this, "设置的参数超出了范围！",Toast.LENGTH_SHORT).show();
                    	  return;
                      }
                      PreferenceUtils.setPrefInt(MainActivity.this, "dist", distSet);
                    } 

                }) 
//                .setNeutralButton("保存", new DialogInterface.OnClickListener() {
//                    @Override 
//                    public void onClick(DialogInterface dialog, int which) {
//                    	distSet=Integer.parseInt(etDistance.getText().toString()); 
//                    	PreferenceUtils.setPrefInt(MainActivity.this, "dist", distSet);
//
//                    } 
//                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override 
                    public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();//关闭消息框 
                    } 

                }).show();		
	}
	
	/*
	 * 设置对话框
	 */
	protected void calibrateDialog() {
		// 得到编辑框的布局文件input_message
        LinearLayout inputMessage = (LinearLayout) this.getLayoutInflater().inflate( 
                R.layout.activity_calibrate, null);
        final EditText etRSSI= (EditText) inputMessage.findViewById(R.id.etRSSI);
        final EditText etFactor=(EditText) inputMessage.findViewById(R.id.etFactor);
//        final TextView tvRealDb=(TextView) inputMessage.findViewById(R.id.tvRealDb);
//        mHandler.postDelayed(timerGetRSSI, SCANNING_TIMEOUT);
//        getDisplayRSSI();
//        tvRealDb.setText(String.valueOf(BLERSSI));
//        
        RSSI_SET=PreferenceUtils.getPrefFloat(MainActivity.this, "RSSI", 59.0f);
        etRSSI.setText(String.valueOf(RSSI_SET));
        
        FACTOR_SET=PreferenceUtils.getPrefFloat(MainActivity.this, "FACTOR", 2.0f);
        etFactor.setText(String.valueOf(FACTOR_SET));
       
      
        dialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("重新设定参数")
                .setMessage("在信号强度中，请输入发射端和接收端相隔1米时的当前信号强度：")
                .setView(inputMessage)
                .setPositiveButton("保存并确定", new DialogInterface.OnClickListener() {                    

					@Override 
                    public void onClick(DialogInterface dialog, int which) {
						RSSI_SET=(float) Double.parseDouble(etRSSI.getText().toString()); 
						FACTOR_SET=(float) Double.parseDouble(etFactor.getText().toString());
						PreferenceUtils.setPrefFloat(MainActivity.this, "RSSI", RSSI_SET);
                    	PreferenceUtils.setPrefFloat(MainActivity.this, "FACTOR", FACTOR_SET);
                    } 

                }) 
//                .setNeutralButton("保存", new DialogInterface.OnClickListener() {
//                    @Override 
//                    public void onClick(DialogInterface dialog, int which) {
//                    	RSSI_SET=(float) Double.parseDouble(etRSSI.getText().toString()); 
//                    	FACTOR_SET=(float) Double.parseDouble(etFactor.getText().toString()); 
//						
//                    	PreferenceUtils.setPrefFloat(MainActivity.this, "RSSI", RSSI_SET);
//                    	PreferenceUtils.setPrefFloat(MainActivity.this, "FACTOR", FACTOR_SET);
//
//                    } 
//                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override 
                    public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();//关闭消息框 
                    } 

                }).show();	
        mHandler_dialog.sendEmptyMessage(0);  
	}
	private Handler mHandler_dialog = new Handler(){  
        @Override  
        public void handleMessage(Message msg) {  
            // TODO Auto-generated method stub  
            int what = msg.what;  
            if (what == 0) {    //update  
                TextView tv = (TextView) dialog.findViewById(R.id.tvRealDb);  
//                tv.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", System    
//                        .currentTimeMillis()).toString()); 
                tv.setText(String.valueOf(Math.abs(BLERSSI)));
                if(dialog.isShowing()){  
                	mHandler_dialog.sendEmptyMessageDelayed(0,1000);  
                }  
            }else {  
            	dialog.cancel();  
            }  
        }  
    };  

    /*
     * 打开宝宝日志界面
     */
	private void openBabyLogActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this, BabyLogActivity.class);
		//传递图片，加载较慢。
//		intent.putExtra("userPicture", userPictrue);
//		intent.putExtra("bitmap", bitmap);
		startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);	
	}
	private void disconnectDevice() {
		if(mBTDevice==null) return;
		mBleWrapper.stopScanning();
    	invalidateOptionsMenu();    	
    	mDevicesListAdapter.clearList();
    	//
    	if(mBTGatt==null) return;
    	disableNotificationForBabyDiaper();
		disableNotificationForBatLevel();
		disconnectFromDevice();
		closeGatt();		
		clear();
		mHandler.removeCallbacks(timerGetRSSI);
		SoundUtil.play(R.raw.pegconn, 0);
	}

	private void clear() {
		tvTips.setText("");		
		tvUpdate.setText("");
		mViewPager.setVisibility(View.GONE);
		mCircleBar.setmTxtHint1("点击搜索");
		mCircleBar.setmTxtHint2("");
		mCircleBar.setProgress(50);
		
	}	
	//add new
	 @Override
	    protected void onResume() {
	    	super.onResume();
	    	// on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
	    	if(mBleWrapper.isBtEnabled() == false) {
				// BT is not turned on - ask user to make it enabled
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
			    // see onActivityResult to check what is the status of our request
			}	    	
	    	// initialize BleWrapper object
	        mBleWrapper.initialize();	    	
	    	mDevicesListAdapter = new DeviceListAdapter(this);
	    	// remember to add timeout for scanning to not run it forever and drain the battery
//			addScanningTimeout();    	
//			mBleWrapper.startScanning();			
	        invalidateOptionsMenu();
			
			// first check if BT/BLE is available and enabled
			if(initBt() == false) return;
			if(isBleAvailable() == false) return;
			if(isBtEnabled() == false) return;			
			// then start discovering devices around

			if(mBTDevice!=null) connectToDevice();
	    };
	    
	    @Override
	    protected void onPause() {
	    	super.onPause();
	    	mBleWrapper.stopScanning();
	    	invalidateOptionsMenu();	    	
	    	mDevicesListAdapter.clearList();	    	
	    };	   
	    /* check if user agreed to enable BT */
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        // user didn't want to turn on BT
	        if (requestCode == ENABLE_BT_REQUEST_ID) {
	        	if(resultCode == Activity.RESULT_CANCELED) {
			    	btDisabled();
			        return;
			    }
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }

		/* make sure that potential scanning will take no longer
		 * than <SCANNING_TIMEOUT> seconds from now on */
//		private void addScanningTimeout() {
		Runnable timerGetRSSI = new Runnable() {
            @Override
            public void run() {
            	getDisplayRSSI();            	
            	mHandler.postDelayed(this, SCANNING_TIMEOUT);
            }
        };
		protected double tmp=-1.0;        
 
	    private void getDisplayRSSI() {
	    	runOnUiThread(new Runnable() {		
				@Override
				public void run() {					
					//Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);										
//					if(mBTGatt==null){
//						tvTips.setText("设备已断开!");
//						return;
//					}
//					SoundUtil.play(R.raw.pegconn, 0);
					if(getRssiVal())
						dist=calcDistByRSSI(BLERSSI);
					DecimalFormat  df = new DecimalFormat("######0.00"); 
					String distDF = df.format(dist);
					if(dist>distSet && isTime){
						tvTips.setText("宝宝超出了您设定的安全距离！！！"+"\n实时距离："+distDF+" m");
						r.play();
					}else{						
						tvTips.setText("安全距离："+distSet+" m"+"\n实时距离："+distDF+" m");
						r.stop();
					}
					if(tmp==dist){
						iCount++;	
					}else{
						iCount--;
					}
					if(tmp==dist&&iCount>3){
						tvTips.setText("设备已断开!");
						disconnectDevice();
						mHandler.removeCallbacks(timerGetRSSI);
//						iCount=0;
					}
					tmp=dist;
					
				}
	    	});
		}
		/* add device to the current list of devices */
	    private void handleFoundDevice(final BluetoothDevice device,
	            final int rssi,
	            final byte[] scanRecord)
		{
			// adding to the UI have to happen in UI thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {				
					mDevicesListAdapter.addDevice(device, rssi, scanRecord);
					mDevicesListAdapter.notifyDataSetChanged();
				}
			});
		}	

	    private void btDisabled() {
	    	Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
	        finish();    	
	    }
	    
	    private void bleMissing() {
	    	Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
	        finish();    	
	    }
        /*
         * 视图页面适配器类
         */
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
//	            super.destroyItem(container, position, object);
//	            view.removeViewAt(position);
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
//	        acquire();
	        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();	        
	        //每隔2秒钟切换一张图片
	        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 4, TimeUnit.SECONDS);
	    }	    
	    /*
	     * 切换图片
	     * 
	     */
	    private class ViewPagerTask implements Runnable {

	        @Override
	        public void run() {
	            // TODO Auto-generated method stub
	            currentItem = (currentItem +1) % imageIds.length;
	            //更新界面
//	            handler.sendEmptyMessage(0);
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
	        super.onStop();
	    }
		private boolean initBt() {
			mBTManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
			if(mBTManager != null) mBTAdapter = mBTManager.getAdapter();			
			return (mBTManager != null) && (mBTAdapter != null);
		}		
		private boolean isBleAvailable() {
//			log("Checking if BLE hardware is available");			
			boolean hasBle = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
			if(hasBle && mBTManager != null && mBTAdapter != null) {
//				log("BLE hardware available");
			}
			else {
//				log("BLE hardware is missing!");
				return false;
			}
			return true;
		}
		
		private boolean isBtEnabled() {
//			log("Checking if BT is enabled");
			if(mBTAdapter.isEnabled()) {
//				log("BT is enabled");
			}
			else {
//				log("BT is disabled. Use Setting to enable it and then come back to this app");
				return false;
			}
			return true;
		}

		private void startSearchingForBabyDiaper() {
			// we define what kind of services found device needs to provide. In our case we are interested only in
			// Heart Rate service
			final UUID[] uuids = new UUID[] { mBabyDiaperServiceUuid };
			mBTAdapter.startLeScan(uuids, mDeviceFoundCallback);
			// results will be returned by callback
//			log("Search for devices providing BabyDiapers service started");
			
			// please, remember to add timeout for that scan
			Runnable timeout = new Runnable() {
	            @Override
	            public void run() {
					if(mBTAdapter.isDiscovering() == false) return;
					stopSearchingForBabyDiaper();	
	            }
	        };
	        mHandler_Baby.postDelayed(timeout, 10000); //10 seconds		
		}

		private void stopSearchingForBabyDiaper() {
			mBTAdapter.stopLeScan(mDeviceFoundCallback);
//			log("Searching for devices with BabyDiapers service stopped");
		}
		
		private void connectToDevice() {
//			log("Connecting to the device NAME: " + mBTDevice.getName() + " HWADDR: " + mBTDevice.getAddress());
			mBTGatt = mBTDevice.connectGatt(this, true, mGattCallback);
			mBTGatt_Bat = mBTDevice.connectGatt(this, true, mGattCallback_Bat);
			mCircleBar.setmTxtHint1("已"
					+ ".连接");
		}
		
		private void disconnectFromDevice() {
//			log("Disconnecting from device");
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
//			log("Starting discovering services");
			mBTGatt.discoverServices();
			mBTGatt_Bat.discoverServices();
		}
		
		private void getBabyDiaperService() {
//			log("Getting BabyDiapers Service");
			mBTService = mBTGatt.getService(mBabyDiaperServiceUuid);
			
			if(mBTService == null) {
//				log("Could not get BabyDiapers Service");
			}
			else {
//				log("BabyDiapers Service successfully retrieved");
				getBabyDiaperCharacteristic();
			}
		}
		
		private void getBabyDiaperCharacteristic() {
//			log("Getting BabyDiapers Measurement characteristic");
			mBTValueCharacteristic = mBTService.getCharacteristic(mBabyDiaperCharacteristicUuid);
			mBTValueCharacteristic_Bat = mBTService.getCharacteristic(mBabyDiaperCharacteristicBatUuid);
			if(mBTValueCharacteristic == null) {
//				log("Could not find BabyDiapers BabyPeeCH Characteristic");
			}
			else {
//				log("BabyDiapers BabyPeeCH characteristic retrieved properly");
				enableNotificationForBabyDiaper();			
			}
			if(mBTValueCharacteristic_Bat == null) {
//				log("Could not find BabyDiapers BAT_LEVEL Characteristic");
			}
			else {
//				log("BabyDiapers BAT_LEVEL characteristic retrieved properly");			
				enableNotificationForBatLevel();
			}			
		}		
		private void enableNotificationForBabyDiaper() {
//			log("Enabling notification for BabyDiapers");
	        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic, true);       
	        if(!success) {
//	        	log("Enabling notification failed!");
	        	
	        	return;
	        }
	        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.BabyPeeCH);
	        if(descriptor != null) {
		        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		        
		        mBTGatt.writeDescriptor(descriptor);		       
//		        log("Notification enabled");
	        }		
	        else {
//	        	log("Could not get descriptor for characteristic! Notification are not enabled.");
	        }
		}
		
		private void enableNotificationForBatLevel() {
//			log("Enabling notification for BabyDiapers");
			boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic_Bat, true);
//	        boolean success = mBTGatt_Bat.setCharacteristicNotification(mBTValueCharacteristic_Bat, true);       
	        if(!success) {
//	        	log("Enabling notification failed!");
	        	return;
	        }
	        BluetoothGattDescriptor descriptor = mBTValueCharacteristic_Bat.getDescriptor(BleDefinedUUIDs.Descriptor.BAT_LEVEL);
	      
	        if(descriptor != null) {
//		        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		        descriptor.setValue(new byte[]{BluetoothGattDescriptor.PERMISSION_READ});
//		        mBTGatt.writeDescriptor(descriptor);        
		        mBTGatt_Bat.writeDescriptor(descriptor); 
//		        log("Notification enabled");
	        }		
	        else {
//	        	log("Could not get descriptor for characteristic! Notification are not enabled.");
	        }
		}
		private void disableNotificationForBabyDiaper() {
//			log("Disabling notification for BabyDiapers");
	        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic, false);
	        if(!success) {
//	        	log("Disabling notification failed!");
	        	return;
	        }
	        BluetoothGattDescriptor descriptor = mBTValueCharacteristic.getDescriptor(BleDefinedUUIDs.Descriptor.BabyPeeCH);
	        if(descriptor != null) {
		        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		        mBTGatt.writeDescriptor(descriptor);
//		        log("Notification disabled");
	        }		
	        else {
//	        	log("Could not get descriptor for characteristic! Notification could be still enabled.");
	        }
		}		
		private void disableNotificationForBatLevel() {
//			log("Disabling notification for BabyDiapers");
	        boolean success = mBTGatt.setCharacteristicNotification(mBTValueCharacteristic_Bat, false);
//	        boolean success = mBTGatt_Bat.setCharacteristicNotification(mBTValueCharacteristic_Bat, false);
	        if(!success) {
//	        	log("Disabling notification failed!");
	        	return;
	        }

	        BluetoothGattDescriptor descriptor = mBTValueCharacteristic_Bat.getDescriptor(BleDefinedUUIDs.Descriptor.BAT_LEVEL);
	        if(descriptor != null) {
		        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		        mBTGatt.writeDescriptor(descriptor);
		        mBTGatt_Bat.writeDescriptor(descriptor);
//		        log("Notification disabled");
	        }		
	        else {
//	        	log("Could not get descriptor for characteristic! Notification could be still enabled.");
	        }
		}		
		private void getAndDisplayBabyDiaperValue() {
	    	byte[] raw = mBTValueCharacteristic.getValue();
	    	int val1=(raw[0]-0x30),val2=((raw[2]-0x30)),val3=(raw[3]-0x30);
	    	final float value=(float) (val1+val2*0.1+val3*0.01);
	    	runOnUiThread(new Runnable() {
				@SuppressWarnings("deprecation")
				@SuppressLint("SimpleDateFormat")
				@Override
				public void run() {
					final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
					tvUpdate.setText(timestamp);					
					final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					Notification notify = new Notification(); // 创建一个Notification对象
					notify.icon = R.drawable.ic_launcher;
					notify.tickerText = "通知";
					notify.when = System.currentTimeMillis(); // 设置发送时间
					notify.defaults = Notification.DEFAULT_ALL;	//设置默认声音、默认振动和默认闪光灯					
					notify.flags|=Notification.FLAG_AUTO_CANCEL;	//打开应用程序后图标消失					
					// 设置启动的程序，如果存在则找出，否则新的启动
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setComponent(new ComponentName(MainActivity.this, MainActivity.class));//用ComponentName得到class对象
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况

					PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);//将经过设置了的Intent绑定给PendingIntent
					notify.contentIntent = contentIntent;// 通知绑定 PendingIntent
					notify.flags=Notification.FLAG_AUTO_CANCEL;//设置自动取消
					
					notify.setLatestEventInfo(MainActivity.this, "温馨提醒：", "宝宝尿湿啦！",
							contentIntent);//设置事件信息					
					
					if(value<3.30){
						tvTips.setText("宝宝尿湿啦！");
						mViewPager.setVisibility(View.VISIBLE);
						viewPagerDialog();
						notificationManager.notify(NOTIFYID_1, notify); // 通过通知管理器发送通知
						data.setUsercode(usercode);
//						data.setUserPictrue(userPictrue);
						data.setUsername(username);
						data.setTime(timestamp);
						data.setNumbers("1");
						
						BabyInfoDao.getInstance(getApplicationContext()).insert(data);
					}else{
						mViewPager.setVisibility(View.GONE);
					}
				}
	    	});	    	
		}
		/*
		 * 动画播放对话框
		 */
		protected void viewPagerDialog() {
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
			.create();
			alert.setIcon(android.R.drawable.ic_dialog_info); // 设置对话框的图标
			alert.setTitle("动画播放"); // 设置对话框的标题
			alert.setMessage("需要取消动画播放吗？"); // 设置要显示的内容
			// 添加取消按钮
			alert.setButton(DialogInterface.BUTTON_NEGATIVE, "不",
					new DialogInterface.OnClickListener() {
		
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
						}
					});
			// 添加确定按钮
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "是的",
					new DialogInterface.OnClickListener() {
		
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							mViewPager.setVisibility(View.GONE);						
						}
					});
			alert.show(); // 创建对话框并显示			
		}	
		private void getAndDisplayBatLevelValue() {
	    	byte[] raw = mBTValueCharacteristic_Bat.getValue();
	    	int val1=(raw[0]-0x30),val2=((raw[2]-0x30)),val3=(raw[3]-0x30);
	    	final float value=(float) (val1+val2*0.1+val3*0.01);
	    	final String description =value+ " V";
	    	
	    	runOnUiThread(new Runnable() {
				@SuppressLint("SimpleDateFormat")
				@Override
				public void run() {
					mCircleBar.setmTxtHint2(description);
					mCircleBar.setmTxtHint1("已连接");
					mCircleBar.setProgress((int)(value/3.3*100));
					final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
					tvUpdate.setText(timestamp);
					//Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					//alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					
					if(value<2.0){
						tvTips.setText("电池电压低，请更换电池！！！");
						r.play();
					}else{
//						tvTips.setText("");
						r.stop();
					}
				}
	    	});
		}
	    private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
	        @Override
	        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
	        	// here we found some device with heart rate service, lets save it:
	        	MainActivity.this.mBTDevice = device;
//	        	log("Device with BabyDiapers service discovered. HW Address: "  + device.getAddress());
	        	stopSearchingForBabyDiaper();	        	
	        	connectToDevice();
	        }
	    };
		
		
	    /* callbacks called for any action on HR Device */
	    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
	        @Override
	        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
	            if (newState == BluetoothProfile.STATE_CONNECTED) {
//	            	log("Device connected");
	            	discoverServices();	            	
	            }
	            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//	            	log("Device disconnected");
	            }
	        }

	        @Override
	        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
	        	if(status == BluetoothGatt.GATT_SUCCESS) {
//	        		log("Services discovered");
	        		getBabyDiaperService();
	        	}
	        	else {
//	        		log("Unable to discover services");
	        	}
	        }

	        @Override
	        public void onCharacteristicChanged(BluetoothGatt gatt,
	                                            BluetoothGattCharacteristic characteristic)
	        {
	        	if(characteristic.equals(mBTValueCharacteristic)) {
	        		getAndDisplayBabyDiaperValue();
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
	        public void onCharacteristicWrite(BluetoothGatt gatt, 
	        								 BluetoothGattCharacteristic characteristic, 
	        								 int status) {};
	        
	        @Override
	        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
	        	// TODO Auto-generated method stub
				super.onReadRemoteRssi(gatt, rssi, status);
				//将回调的RSSI值赋值
				BLERSSI=rssi;
	        };
	    };
	     
	    /* callbacks called for any action on HR Device */
	    private final BluetoothGattCallback mGattCallback_Bat = new BluetoothGattCallback() {	    	
	        @Override
	        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
	            if (newState == BluetoothProfile.STATE_CONNECTED) {
//	            	log("Device connected");
	            	discoverServices();
	            }
	            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//	            	log("Device disconnected");
	            }
	        }

	        @Override
	        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
	        	if(status == BluetoothGatt.GATT_SUCCESS) {
//	        		log("Services discovered");
	        		getBabyDiaperService();
	        	}
	        	else {
//	        		log("Unable to discover services");
	        	}
	        }

	        @Override
	        public void onCharacteristicChanged(BluetoothGatt gatt,
	                                            BluetoothGattCharacteristic characteristic)
	        {
	        	if(characteristic.equals(mBTValueCharacteristic)) {
	        		getAndDisplayBabyDiaperValue();
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
	        public void onCharacteristicWrite(BluetoothGatt gatt, 
	        							     BluetoothGattCharacteristic characteristic, 
	        							     int status) {};
	        
	        @Override
	        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
	        	// TODO Auto-generated method stub
				super.onReadRemoteRssi(gatt, rssi, status);
				//将回调的RSSI值赋值
				BLERSSI=rssi;
	        };
	    };    
	    //是否能读取到已连接设备的RSSI值
		//执行该方法一次，获得蓝牙回调onReadRemoteRssi（）一次
		 /** 
	     * Read the RSSI for a connected remote device. 
	     * */
	    public boolean getRssiVal() { 
	        if (mBTGatt == null) 
	            return false; 
	        return mBTGatt.readRemoteRssi(); 
	       
	    } 
		// put new logs into the UI console
//		private void log(final String txt) {
//			if(mConsole == null) return;
//			
//			final String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS").format(new Date());
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					mConsole.setText(timestamp + " : " + txt + "\n" + mConsole.getText());
//				}		
//			});
//		}
		@Override
		public boolean onKeyDown(int keyCode,KeyEvent event){
			if(keyCode==KeyEvent.KEYCODE_BACK){
				quitDialog();
				return true;					//屏蔽后退键
			}
			return super.onKeyDown(keyCode, event);
		}
		/*
		 * 退出提示
		 */
		private void quitDialog() {
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
			.create();
			alert.setIcon(android.R.drawable.ic_dialog_info); // 设置对话框的图标
			alert.setTitle("退出？"); // 设置对话框的标题
			alert.setMessage("真的要退出应用吗？"); // 设置要显示的内容
			// 添加取消按钮
			alert.setButton(DialogInterface.BUTTON_NEGATIVE, "不",
					new DialogInterface.OnClickListener() {
		
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
						}
					});
			// 添加确定按钮
			alert.setButton(DialogInterface.BUTTON_POSITIVE, "是的",
					new DialogInterface.OnClickListener() {
		
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
//							finish(); // 返回系统主界面	
							SoundUtil.play(R.raw.pegconn, 0);
							AgentApplication.getInstance().onTerminate(); 
							

						}
					});
			alert.show(); // 创建对话框并显示			
		}

}

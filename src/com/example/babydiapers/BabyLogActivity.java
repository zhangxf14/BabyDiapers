package com.example.babydiapers;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 成员列表界面
 * @author Sai
 *
 */
public class BabyLogActivity extends BaseActivity {
		
	private ListView lvBabyLog;
	private BabyListAdapter adapter;
	private ArrayList<BabyInfo> mDatas = new ArrayList<BabyInfo>();
	private TextView tvBack;
	public static boolean isUser=false;
	public static int position=0;
    private ArrayList<UserId> userIds;
	private int id;
	private byte[] userPicture;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		AgentApplication.getInstance().addActivity(this);	
		this.setContentView(R.layout.activity_babylog_list);
		initViews();
		init(true, true);
		initEvents();
	}

	protected void initViews() {
		lvBabyLog = (ListView) findViewById(R.id.lvBabyLog);		
		tvBack = (TextView) findViewById(R.id.tvBack);		
	}

	protected void initEvents() {
//		lvBabyLog.setOnItemClickListener(new OnItemClickListener() {
//			@SuppressWarnings("static-access")
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//				isUser=true;
//				BabyLogActivity.this.position=position;
//				userId.setId(position+1);
//				UserIdDao.getInstance(getApplicationContext()).insert(userId);
//				Intent intent = new Intent(BabyLogActivity.this, MainActivity.class);
//				startActivity(intent);
////				finish();
//				overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
////				overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
////				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
//				SoundUtil.play(R.raw.pegconn, 0);
//			}
//		});
		
		tvBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(BabyLogActivity.this, MainActivity.class);
//				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.activity_up,R.anim.activity_down);
//				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				SoundUtil.play(R.raw.pegconn, 0);
			}
		});
	}

	protected void init(boolean hasBackOnActionBar, boolean hasNfc) {
		loadData();
		adapter = new BabyListAdapter(mDatas,id,userPicture);
		lvBabyLog.setAdapter(adapter);
	}

	private void loadData() {
//		Intent intent=	getIntent();		
//		byte[]	userPicture =intent.getByteArrayExtra("userPicture");
//		
		userIds=UserIdDao.getInstance(getApplicationContext()).queryAll();
		int count=userIds.size();
	    id=userIds.get(count-1).getId();
//		mDatas = BabyInfoDao.getInstance(getApplicationContext()).queryAll();//列出当前所有成员
		mDatas=BabyInfoDao.getInstance(getApplicationContext()).query(String.valueOf(id));
		userPicture=UserInfoDao.getInstance(getApplicationContext()).query(String.valueOf(id)).get(0).getUserPictrue();
		for ( BabyInfo mdata : mDatas) {
			mdata.setUserPictrue(userPicture);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}

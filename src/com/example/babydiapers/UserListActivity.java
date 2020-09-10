package com.example.babydiapers;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 成员列表界面
 * @author Sai
 *
 */
public class UserListActivity extends BaseActivity {
		
	private ListView listView;
	private UserListAdapter adapter;
	private ArrayList<UserInfo> mDatas = new ArrayList<UserInfo>();
	private TextView tvAddUser;
	public static boolean isUser=false;
	public static int position=0;
    private UserId userId=new UserId();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		AgentApplication.getInstance().addActivity(this);	
		this.setContentView(R.layout.activity_user_list);
		initViews();
		init(true, true);
		initEvents();
	}

	protected void initViews() {
		listView = (ListView) findViewById(R.id.listView1);		
		tvAddUser = (TextView) findViewById(R.id.tvAddUser);		
	}

	protected void initEvents() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				isUser=true;
				UserListActivity.this.position=position;
				userId.setId(position+1);
				UserIdDao.getInstance(getApplicationContext()).insert(userId);
				Intent intent = new Intent(UserListActivity.this, MainActivity.class);
				startActivity(intent);
//				finish();
				overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//				overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				SoundUtil.play(R.raw.pegconn, 0);
			}
		});
		
		tvAddUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserListActivity.this, LoginActivity.class);
				startActivity(intent);
//				finish();
				overridePendingTransition(R.anim.activity_up,R.anim.activity_down);
//				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				SoundUtil.play(R.raw.pegconn, 0);
			}
		});
	}

	protected void init(boolean hasBackOnActionBar, boolean hasNfc) {
		loadData();
		adapter = new UserListAdapter(mDatas);
		listView.setAdapter(adapter);
	}

	private void loadData() {
		mDatas = UserInfoDao.getInstance(getApplicationContext()).queryAll();//列出当前所有成员
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

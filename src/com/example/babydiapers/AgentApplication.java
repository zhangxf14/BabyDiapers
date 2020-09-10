package com.example.babydiapers;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class AgentApplication extends Application {
	private static AgentApplication instance;

	private List<Activity> activities = new ArrayList<Activity>();
    
	public static AgentApplication getInstance()
	 {
	 if(null == instance)
	 {
	 instance = new AgentApplication();
	 }
	 return instance;

	 }
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		for (Activity activity : activities) {
			activity.finish();
		}
		
//		onDestroy();
		
		System.exit(0);
	}
}

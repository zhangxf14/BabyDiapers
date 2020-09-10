/**
 * 
 */
package com.example.babydiapers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author Administrator
 *
 */
public class SplashActivity extends BaseActivity {
	protected static final int SET_SECOND = 2;
	private static int END = 1;  
    private static int TIME = 1000; //闪屏保持的时间（毫秒计
    public  static boolean isSplash=false;
    private int i=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgentApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_splash);
		endSplashActivity();  
	}
	private void endSplashActivity() {  
//	       endSplashHandler.sendEmptyMessageAtTime(END, TIME);   
		handler_timer.postDelayed(runnable, TIME); //每隔1s执行
    }
	
//	Handler endSplashHandler = new Handler(){  
//	    @Override  
//	    public void handleMessage(Message msg) {  
//	        super.handleMessage(msg); 
//	        isSplash=true;
//	        startActivity(new Intent(SplashActivity.this , MainActivity.class));//MainActivity 改成想要跳转的Activity  
//	    };  
//    }; 

    /*
	 * 定时N s后,切换到主界面
	 */
	Handler handler_timer = new Handler();  
	Runnable runnable = new Runnable() {
  
       @Override  
       public void run() {  
           // handler自带方法实现定时器  
	       try {  
	           handler_timer.postDelayed(this, TIME);
	           i++; 
	           if(i>SET_SECOND){
	        	   isSplash=true;
	        	   handler_timer.removeCallbacks(runnable);	
	   	           startActivity(new Intent(SplashActivity.this , MainActivity.class));  
	   	           finish();
	           }	            
           } catch (Exception e) {
               e.printStackTrace();  
           }  
	   }  
	};
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		//关闭窗体动画显示
//		this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		overridePendingTransition(R.anim.activity_up,R.anim.activity_down);
//		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	};
}

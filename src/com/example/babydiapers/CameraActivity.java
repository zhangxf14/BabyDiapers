/**
 * 
 */
package com.example.babydiapers;
/**
 * @author Administrator
 *
 */
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
  

import android.graphics.ImageFormat;  
import android.hardware.Camera;  
import android.os.AsyncTask;  
import android.os.Bundle;  
import android.app.Activity;  
import android.util.Log;  
import android.view.SurfaceHolder;  
import android.view.SurfaceView;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
  
public class CameraActivity extends Activity {  
    private String tag ="CameraActivity";  
    private SurfaceView surfaceView;   
    //Surface的控制器  
    private SurfaceHolder surfaceHolder;  
    private Camera camera;  
    private Button saveButton;  
    public static boolean isCamera=false;
    //拍照的回调接口  
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {  
        public void onPictureTaken(byte[] data, Camera camera) {  
            new SavePictureTask().execute(data);  
            camera.startPreview();  
        }  
    };  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_camera);  
        initViews();  
    }  
  
    @SuppressWarnings("deprecation")
	private void initViews() {  
        saveButton = (Button) findViewById(R.id.camera_save);  
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);  
        surfaceHolder = surfaceView.getHolder();  
        // 给SurfaceView当前的持有者  SurfaceHolder 一个回调对象。  
        //用户可以实现此接口接收surface变化的消息。当用在一个SurfaceView 中时，   
        //它只在SurfaceHolder.Callback.surfaceCreated()和SurfaceHolder.Callback.surfaceDestroyed()之间有效。  
        //设置Callback的方法是SurfaceHolder.addCallback.  
        //实现过程一般继承SurfaceView并实现SurfaceHolder.Callback接口  
        surfaceHolder.addCallback(surfaceCallback);  
        // 设置surface不需要自己的维护缓存区    
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        saveButton.setOnClickListener(new OnClickListener() {  
              
            @Override  
            public void onClick(View v) {  
                camera.takePicture(null, null, pictureCallback);  
//              Camera.takePicture(shutterCallback,rawCallback,pictureCallback );   
//                private ShutterCallback shutterCallback = new ShutterCallback(){    
//                          public void onShutter(){    
//                            /* 按快门瞬间会执行这里的代码 */  
//                          }    
//                        };  
//                private PictureCallback rawCallback = new PictureCallback(){    
//                          public void onPictureTaken(byte[] _data, Camera _camera){    
//                            /* 如需要处理 raw 则在这里 写代码 */  
//                          }    
//                        };  
//                   //当拍照后 存储JPG文件到 sd卡  
//                  PictureCallback pictureCallback=new PictureCallback(){  
//                      public void onPictureTaken(byte[] data,Camera camera) {  
//                          FileOutputStream outSteam=null;  
//                          try{  
//                              SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");  
//                              String times=format.format((new Date()));  
//                              outSteam=new FileOutputStream("/sdcard/"+times+"mhc.jpg");  
//                              outSteam.write(data);  
//                              outSteam.close();  
//                          }catch(FileNotFoundException e){  
//                              Log.d("Camera", "row");  
//                          } catch (IOException e) {  
//                              e.printStackTrace();  
//                          }  
//                      };  
//                  };  
            }  
        });  
    }  
      
      
    //SurfaceView当前的持有者的回调接口的实现    
    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {  
          
        @Override  
        public void surfaceCreated(SurfaceHolder holder) {  
            camera = Camera.open();  
            Log.e(tag, "摄像头Open完成");  
            try {  
                camera.setPreviewDisplay(holder);  
            } catch (IOException e) {  
                camera.release();  
                camera = null;  
            }  
        }  
          
        @Override  
        public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                int height) {  
            Camera.Parameters parameters = camera.getParameters();  
            parameters.setPictureFormat(ImageFormat.JPEG);  
            camera.setDisplayOrientation(90);  
            camera.setParameters(parameters);  
            camera.startPreview();  
        }  
          
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {  
            camera.stopPreview();  
            camera.release();  
            camera = null;  
        }  
    };  
      
    class SavePictureTask extends AsyncTask<byte[], String, String> {  
        @Override  
        protected String doInBackground(byte[]... params) {  
            File picture = new File("/sdcard/mhc.jpg");  
            try {  
                FileOutputStream fos = new FileOutputStream(picture.getPath());  
                fos.write(params[0]);  
                fos.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            Log.e(tag, "照片保存完成");  
            CameraActivity.this.finish();
            isCamera=true;
            return null;  
        }  
    }  
}  

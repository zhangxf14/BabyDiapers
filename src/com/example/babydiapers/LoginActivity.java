package com.example.babydiapers;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;

@SuppressLint("SdCardPath")
public class LoginActivity extends BaseActivity {
	private CircleImageView ivCamera;
	private EditText etName,etBirthdate;
	private RadioButton rbMale,rbfemale;
	private Button btnAdd;
	public static boolean isLogin=false,isPhoto=false;
	public static String nickName="",birthdate="",sex="男";
	private Paint paint=new Paint(); 
	private static final int CAMERA_REQUEST = 8888;
	protected static final int PHOTO_REQUEST = 1111;
	public static Bitmap bitmap=null;
    
	private UserInfo userInfo=new UserInfo();
	private UserId   userId=new UserId();
	protected ArrayList<UserInfo> userInfos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AgentApplication.getInstance().addActivity(this); 	
		setContentView(R.layout.activity_login); 
		initViews();
		initEvents();
	}
    /*
     * 初始化事件
     */
	private void initEvents() {
		ivCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popPhotoDialog();
				
			}
		});
//		etName.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				btnAdd.setEnabled(true);				
//			}
//		});
		rbMale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rbMale.setChecked(true);
				rbfemale.setChecked(false);
				sex="男";
			}
		});
		rbfemale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rbMale.setChecked(false);
				rbfemale.setChecked(true);
				sex="女";
			}
		});
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isPhoto){
					popPhotoDialog();
					Toast.makeText(LoginActivity.this, "请添加成员头像", Toast.LENGTH_LONG).show();
					SoundUtil.play(R.raw.ding, 0);
			    	return;
				}
			    nickName=etName.getText().toString();	
			    if(nickName.equals("")){
			    	Toast.makeText(LoginActivity.this, "请填写成员名", Toast.LENGTH_LONG).show();
			    	SoundUtil.play(R.raw.ding, 0);
			    	return;
			    }
			    birthdate=etBirthdate.getText().toString();
			    if(birthdate.equals("")){
			    	showDatePickDialog();
			    	Toast.makeText(LoginActivity.this, "请填写出生日期", Toast.LENGTH_LONG).show();
			    	SoundUtil.play(R.raw.ding, 0);
			    	return;
			    }
				isLogin=true;
				userInfo.setUserPictrue(createData(bitmap));
//				userInfo.setUsercode(Usercode);
				userInfo.setUsername(nickName);
				userInfo.setSex(sex);
				userInfo.setBirthdate(birthdate);
				//将用户信息插入数据库
				UserInfoDao.getInstance(getApplicationContext()).insert(userInfo);
				//查询所有用户
				userInfos=UserInfoDao.getInstance(getApplicationContext()).queryAll();
				userId.setId(userInfos.size());
				//将用户序号插入到数据库
				UserIdDao.getInstance(getApplicationContext()).insert(userId);
				/*切换到主界面 */
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.activity_up,R.anim.activity_down);
//				overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				SoundUtil.play(R.raw.pegconn, 0);
			}
		});
		
		etBirthdate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					showDatePickDialog();
					return true;
				}
				return false;
			}
		});
		etBirthdate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showDatePickDialog();
				}
			}
		});
		
	}
	/*
	 * 显示日期选择对话框
	 */
	protected void showDatePickDialog() {
		Calendar calendar = Calendar.getInstance();
		DatePickerDialog datePickerDialog = new DatePickerDialog(LoginActivity.this, new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				LoginActivity.this.etBirthdate.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
				
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.show();

	}

	/*
	 * 弹出对话框
	 */
    protected void popPhotoDialog() {
    	final String[] items=new String[]{"拍照","从相册中选择","取消"};
		Builder builder =new AlertDialog.Builder(LoginActivity.this);
		builder.setItems(items, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
					case 0:
						openCamera();
			            break;
					case 1:
						selectPhoto();
			            break;
					case 2:
			            break;					       
			        default :
			        	break;

				}							
			}

			private void selectPhoto() {
				Intent intent = new Intent();  
                /* 开启Pictures画面Type设定为image */  
                intent.setType("image/*");  
                /* 使用Intent.ACTION_GET_CONTENT这个Action */  
                intent.setAction(Intent.ACTION_GET_CONTENT);   
                /* 取得相片后返回本画面 */  
                startActivityForResult(intent, PHOTO_REQUEST);
			}

			private void openCamera() {
//				startActivity(new Intent(LoginActivity.this, CameraActivity.class));  
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        startActivityForResult(intent, CAMERA_REQUEST);
				
			}					
		});
		builder.create().show();
		
	}
	/*
     * 初始化组件
     */
	private void initViews() {
		ivCamera = (CircleImageView) findViewById(R.id.ivCamera);
		etName = (EditText) findViewById(R.id.etName);
		rbMale = (RadioButton) findViewById(R.id.rbMale);
		rbfemale = (RadioButton) findViewById(R.id.rbfemale);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		etBirthdate=(EditText) findViewById(R.id.etBirthdate);
//		btnAdd.setEnabled(false);
	}
	
	 
    @SuppressLint("SdCardPath")
	@Override
    protected void onResume() {
    	super.onResume();
    	/*
    	 * setBackground，setBackgroundResource，setBackgroundDrawable对应属性background，会根据ImageView组件给定的长宽进行拉伸。
			setImageBitmap，setImageResource对应属性src，存放的是原图的大小，不会进行拉伸。如果想改变src的大小，应该使用属性scaleType。 
			ScaleType： 
			 CENTER   居中，不执行缩放 
			 CENTER_CROP  按原始比例缩放, 裁剪中间部 
			 CENTER_INSIDE 按原始比例缩放, 居中，不裁剪 
			 FIT_CENTER 居中缩放 
			 FIT_START 上对齐缩放 
			 FIT_END  下对齐缩放 
			 FIT_XY  不按比例拉伸缩放 
			 MATRIX  用矩阵来绘制 
    	 */    	        
    };
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();     
    };
    

    /** 
     * 根据文件路径获取所需要文件，并将该文件封装成Bitmap对象返回 
     * @param fileUrl 
     * @return 
     */  
    public static Bitmap getLoacalBitmap(String fileUrl) {  
        try {  
             FileInputStream fis = new FileInputStream(fileUrl);  
             return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片          
          } catch (FileNotFoundException e) {  
             e.printStackTrace();  
             return null;  
        }  
   } 
    /**
     * 获取圆形图片方法
     * @param bitmap
     * @param pixels
     * @return Bitmap      
     */
    private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {  
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),  
                bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
          
        final int color = 0xff424242;
       
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        int x = bitmap.getWidth(); 
        
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
        bitmap.recycle();
        return output;    
        
    } 
    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    private Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        cropWidth /= 2;
        int cropHeight = (int) (cropWidth);
        return Bitmap.createBitmap(bitmap, w / 3, 0, cropWidth, cropHeight, null, false);
    }
    /**                                                                         
     * @param bitmap      原图
     * @param edgeLength  希望得到的正方形部分的边长
     * @return  缩放截取正中部分后的位图。
     */
    private static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength){
	   if(null == bitmap || edgeLength <= 0)
	   {
	    return  null;
	   }
	                                                                                
	   Bitmap result = bitmap;
	   int widthOrg = bitmap.getWidth();
	   int heightOrg = bitmap.getHeight();
	                                                                                
	   if(widthOrg > edgeLength && heightOrg > edgeLength)
	   {
	    //压缩到一个最小长度是edgeLength的bitmap
	    int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
	    int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
	    int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    Bitmap scaledBitmap;
	                                                                                 
	          try{
	           scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	          }
	          catch(Exception e){
	           return null;
	          }
	                                                                                      
	       //从图中截取正中间的正方形部分。
	       int xTopLeft = (scaledWidth - edgeLength) / 2;
	       int yTopLeft = (scaledHeight - edgeLength) / 2;
	                                                                                    
	       try{
	        result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
	        scaledBitmap.recycle();
	       }
	       catch(Exception e){
	        return null;
	       }       
	   }	                                                                                     
	   return result;
	}

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @SuppressWarnings("static-access")
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {  
            Uri uri = data.getData();  
            Log.e("uri", uri.toString());  
            ContentResolver cr = this.getContentResolver();  
            try {  
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                if(bitmap.getWidth()>bitmap.getHeight())
                	bitmap =rotateBitmap(bitmap, 90);
                bitmap=centerSquareScaleBitmap(bitmap,500);
//	            bitmap =cropBitmap(bitmap);
	            
//	            bitmap = getCircleBitmap(bitmap, 14);  
	            this.bitmap=bitmap;
	            ivCamera .setImageBitmap(this.bitmap); //设置Bitmap	
	            isPhoto=true;
            } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e);  
            }  
        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            if(bitmap.getWidth()>bitmap.getHeight())
//            	bitmap =rotateBitmap(bitmap, 90);
            bitmap=centerSquareScaleBitmap(bitmap,130);
//          bitmap =cropBitmap(bitmap);
//          bitmap =rotateBitmap(bitmap, 90);
//          bitmap = getCircleBitmap(bitmap, 14);  
            this.bitmap=bitmap;
            ivCamera .setImageBitmap(this.bitmap); //设置Bitmap	
            isPhoto=true;
        }

        super.onActivityResult(requestCode, resultCode, data);  
    } 
   
    /* 
     * *创建数据 
     */
    public byte[] createData(Bitmap bmp) {
//    ContentValues initValues = new ContentValues();
//    Long id = null;

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    /**
    * Bitmap.CompressFormat.JPEG 和 Bitmap.CompressFormat.PNG
    * JPEG 与 PNG 的是区别在于 JPEG是有损数据图像，PNG使用从LZ77派生的无损数据压缩算法。
    * 这里建议使用PNG格式保存
    * 100 表示的是质量为100%。当然，也可以改变成你所需要的百分比质量。
    * os 是定义的字节输出流
    * 
    * .compress() 方法是将Bitmap压缩成指定格式和质量的输出流
    */
    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);

//    initValues.put("USERPICTRUE", os.toByteArray());//以字节形式保存
//
//    SQLiteDatabase db = getDatabaseWrit();
//    id = db.insert(TABLE_IMAGE, null, initValues);//保存数据
//    db.close();

    Log.i("Image ", "create done.");
    return os.toByteArray();
    }
    
}

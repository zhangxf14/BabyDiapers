package com.example.babydiapers;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 成员列表界面的适配器
 * @author zhangxf
 *
 */
public class BabyListAdapter extends BaseAdapter{
	private ArrayList<BabyInfo> mDatas;
	private int id;
	private byte[] userPicture;
	public BabyListAdapter(ArrayList<BabyInfo> datas,int id, byte[] userPicture){
		this.mDatas=datas;
		this.id=id;	
		this.userPicture=userPicture;
	}
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BabyInfo data=mDatas.get(position);
		Holder holder=null; 
		View view =convertView;
		if(view==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view=inflater.inflate(R.layout.adapter_item_babylog, null);
			holder=creatHolder(view);
			view.setTag(holder);
		}
		else{
			holder=(Holder) view.getTag();
		}
		holder.UpdateUI(data);
		return view;
	}
	public Holder creatHolder(View view){
		Holder holder=new Holder();
		holder.ivPictrue=(ImageView) view.findViewById(R.id.ivPictrue);
		holder.tvName=(TextView) view.findViewById(R.id.tvName);
		holder.tvTime=(TextView) view.findViewById(R.id.tvTime);
		holder.tvNumbers=(TextView) view.findViewById(R.id.tvNumbers);
		return holder;
	}
	class Holder {
		private ImageView ivPictrue;		
		private TextView tvName,tvTime,tvNumbers;
		public void UpdateUI(BabyInfo data){
//			Bitmap bitmap=BitmapFactory.decodeByteArray(data.getUserPictrue(), 0, data.getUserPictrue().length);
			Bitmap bitmap=BitmapFactory.decodeByteArray(userPicture, 0, userPicture.length);
			ivPictrue.setImageBitmap(bitmap);
			tvName.setText(data.getUsername());
			tvTime.setText(data.getTime());
			tvNumbers.setText(data.getNumbers());
			
		}
	}
}
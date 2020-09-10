package com.example.babydiapers;

import java.util.ArrayList;

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
public class UserListAdapter extends BaseAdapter{
	private ArrayList<UserInfo> mDatas;
	public UserListAdapter(ArrayList<UserInfo> datas){
		this.mDatas=datas;
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
		UserInfo data=mDatas.get(position);
		Holder holder=null; 
		View view =convertView;
		if(view==null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view=inflater.inflate(R.layout.adapter_item_user, null);
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
		
		return holder;
	}
	class Holder {
		private ImageView ivPictrue;		
		private TextView tvName;
		public void UpdateUI(UserInfo data){
			Bitmap bitmap=BitmapFactory.decodeByteArray(data.getUserPictrue(), 0, data.getUserPictrue().length);
			ivPictrue.setImageBitmap(bitmap);
			tvName.setText(data.getUsername());
			
		}
	}
}
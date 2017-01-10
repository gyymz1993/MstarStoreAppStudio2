package cn.mstar.store.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.mstar.store.R;
import cn.mstar.store.entity.StoreInfo;
import cn.mstar.store.utils.ImageLoadOptions;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreAdapter extends BaseAdapter {
	private Context context;
	private List<StoreInfo> list;
	private LayoutInflater inflater;
	public StoreAdapter(Context context,List<StoreInfo> list){
		this.context=context;
		this.list=list;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		StoreInfo storeInfo=list.get(position);
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_store_info, null);
			viewHolder.timeTV=(TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.nameTV=(TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.distenceTV=(TextView) convertView.findViewById(R.id.tv_distence);
			viewHolder.PhoneTV=(TextView) convertView.findViewById(R.id.tv_phone);
			viewHolder.storeIV=(ImageView) convertView.findViewById(R.id.iv_store);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		if(storeInfo.getStore_label()!=null && !"".equals(storeInfo.getStore_label()))
				ImageLoader.getInstance().displayImage(storeInfo.getStore_label(), viewHolder.storeIV, ImageLoadOptions.getOptions());
		viewHolder.timeTV.setText(storeInfo.getWtime());
		viewHolder.distenceTV.setText(storeInfo.getDistance());
		viewHolder.PhoneTV.setText(storeInfo.getTel());
		viewHolder.nameTV.setText(storeInfo.getStore_name());
		return convertView;
	}
	public class ViewHolder{
		TextView timeTV,nameTV,PhoneTV,distenceTV;
		ImageView storeIV;
	}
}

package cn.mstar.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.entity.AreaEntity;

public class AreaAdapter extends BaseAdapter{
	private List<AreaEntity> list;
	private LayoutInflater inflater;
	public AreaAdapter(Context context,List<AreaEntity> list){
		
		this.list=list;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_select_area, null);
			viewHolder.nameTV=(TextView) convertView.findViewById(R.id.name_tv);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.nameTV.setText(list.get(position).getName());
		return convertView;
	}
	class ViewHolder{
	TextView nameTV;
}
}

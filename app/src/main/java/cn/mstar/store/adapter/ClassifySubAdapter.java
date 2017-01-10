package cn.mstar.store.adapter;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.mstar.store.R;
import cn.mstar.store.entity.MoreClassifySubData;
import cn.mstar.store.utils.ImageLoadOptions;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/*
 * 分类页 右侧产品的 adapter
 */
public class ClassifySubAdapter extends BaseAdapter {

	private Context context;
	private List<MoreClassifySubData> moreClassifySubList;
	private LayoutInflater inflater;
	public ClassifySubAdapter(Context context,List<MoreClassifySubData> moreClassifySubList){
		this.context=context;
		this.moreClassifySubList=moreClassifySubList;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return moreClassifySubList.size();
	}

	@Override
	public Object getItem(int position) {
		return moreClassifySubList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View  convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_product, null);
			viewHolder.iv_product=(ImageView) convertView.findViewById(R.id.iv_product);
			viewHolder.tv_product=(TextView) convertView.findViewById(R.id.tv_product);
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.tv_product.setText(moreClassifySubList.get(position).getCategoryName());
		ImageLoader.getInstance().displayImage(moreClassifySubList.get(position).getPic(), viewHolder.iv_product,ImageLoadOptions.getOptions());

		return convertView;
	}

	public class ViewHolder{
		public ImageView iv_product;
		public TextView tv_product;
	}
}

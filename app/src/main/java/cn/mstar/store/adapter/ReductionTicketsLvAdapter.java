//package cn.mstar.store.adapter;
//
//import java.util.List;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import cn.mstar.store.R;
//
//public class ReductionTicketsLvAdapter extends BaseAdapter {
//
//
//
//	Context mContext;
//	List<String> data;
//	LayoutInflater inf;
//
//
//	public ReductionTicketsLvAdapter (Context c, List<String> data) {
//
//		mContext = c;
//		this.data = data;
//		inf = LayoutInflater.from(mContext);
//	}
//
//
//	@Override
//	public int getCount() {
//		return 11;
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return null;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	class ViewHolder {
//		RelativeLayout rel_top_color;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		View view;
//		ViewHolder vh;
//		if (convertView == null) {
//			view = inf.inflate(R.layout.reduction_ticket_item, null);
//			vh = initViewHolder (view);
//		} else {
//			view = convertView;
//			vh = (ViewHolder) view.getTag();
//		}
//		boolean isdone = false;
//		if (position % 2 == 0 && !isdone) {
//
//			vh.rel_top_color.setBackgroundResource(R.drawable.coupons_red);
//			isdone = true;
//		}
//		if (position % 3 == 0 && !isdone) {
//
//			vh.rel_top_color.setBackgroundResource(R.drawable.coupons_yellow);
//			isdone = true;
//		}
//		if (position % 5 == 0 && !isdone) {
//
//			vh.rel_top_color.setBackgroundResource(R.drawable.coupons_grey);
//			isdone = true;
//		}
//
//		view.setTag(vh);
//		return view;
//	}
//
//
//	private ViewHolder initViewHolder(View view) {
//		ViewHolder vh = new ViewHolder ();
//		vh.rel_top_color = (RelativeLayout) view.findViewById(R.id.rel_top_part);
//		return vh;
//	}
//
//}

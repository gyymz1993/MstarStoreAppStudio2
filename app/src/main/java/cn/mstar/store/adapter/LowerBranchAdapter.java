package cn.mstar.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.entity.Json_LowBra;
import cn.mstar.store.entity.PopEntity;

/**
 * Created by UlrichAbiguime at Shenzhen.
 */
public class LowerBranchAdapter extends BaseAdapter {

    private Context mContext;
    private List<Json_LowBra.DataEntity.StoreInfoEntity> mData;

    public LowerBranchAdapter(Context mContext, List mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Json_LowBra.DataEntity.StoreInfoEntity getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_low_layout, parent, false);
            vh.idTvStname = (TextView) convertView.findViewById(R.id.id_tv_stname);
            vh.idTvSttime = (TextView) convertView.findViewById(R.id.id_tv_sttime);
            vh.idTvStpeople = (TextView) convertView.findViewById(R.id.id_tv_stpeople);
            vh.idTvNumber = (TextView) convertView.findViewById(R.id.id_tv_number);
            vh.idTvXiaoshoue = (TextView) convertView.findViewById(R.id.id_tv_xiaoshoue);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.idTvStname.setText(getItem(position).getStoreName());
        vh.idTvSttime.setText(getItem(position).getAddtime());
        vh.idTvStpeople.setText(getItem(position).getTrueName());
        vh.idTvXiaoshoue.setText( getItem(position).getAmount()+"");
        vh.idTvNumber.setText( getItem(position).getStoreTel());
        return convertView;
    }


   static  class ViewHolder {
        TextView idTvStname;
        TextView idTvSttime;
        TextView idTvStpeople;
        TextView idTvNumber;
        TextView idTvXiaoshoue;
    }
}

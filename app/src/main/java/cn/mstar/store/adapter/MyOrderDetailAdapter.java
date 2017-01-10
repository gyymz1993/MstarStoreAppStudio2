package cn.mstar.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.entity.OrderDetailEntity;
import cn.mstar.store.utils.ImageLoadOptions;

/**
 * Created by Shinelon on 2016/1/26.
 */
public class MyOrderDetailAdapter extends BaseAdapter {

    private Context ctx;
    private List<OrderDetailEntity.OrderItem> data;

    public MyOrderDetailAdapter(Context ctx, List data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public OrderDetailEntity.OrderItem getItem(int position) {
        return data.get(position);
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_mystore_order_layout, parent, false);
            vh.img = (ImageView) convertView.findViewById(R.id.img);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.spec = (TextView) convertView.findViewById(R.id.spec);
            vh.price = (TextView) convertView.findViewById(R.id.price);
            vh.num = (TextView) convertView.findViewById(R.id.num);
            convertView.findViewById(R.id.btn).setVisibility(View.GONE);
            convertView.findViewById(R.id.footer).setVisibility(View.GONE);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage(getItem(position).pic, vh.img, ImageLoadOptions.getOptions());
        vh.name.setText(getItem(position).title);
        vh.spec.setText("规格: " + getItem(position).specialTitle);
        vh.price.setText("¥" + getItem(position).price);
        vh.num.setText("x" + getItem(position).num);
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView name;
        TextView spec;
        TextView price;
        TextView num;
    }
}

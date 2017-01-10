package cn.mstar.store.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.activity.LogisticsDetialsActivity;
import cn.mstar.store.entity.MyStoreOrdersEntity;
import cn.mstar.store.utils.ImageLoadOptions;

/**
 * Created by Shinelon on 2016/1/11.
 */
public class StoreIndentAdapter extends BaseAdapter {

    private Context ctx;
    private List<MyStoreOrdersEntity> dataList;
    private int state;

    public StoreIndentAdapter(Context ctx, List dataList) {
        this.ctx = ctx;
        this.dataList = dataList;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public MyStoreOrdersEntity getItem(int position) {
        return dataList.get(position);
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
            vh.speci = (TextView) convertView.findViewById(R.id.spec);
            vh.price = (TextView) convertView.findViewById(R.id.price);
            vh.num = (TextView) convertView.findViewById(R.id.num);
            vh.btn = (TextView) convertView.findViewById(R.id.btn);
            vh.goodsNum = (TextView) convertView.findViewById(R.id.goodsNum);
            vh.distribution = (TextView) convertView.findViewById(R.id.distribution);
            vh.total = (TextView) convertView.findViewById(R.id.total);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.btn.setTag(position);
        initBtn(vh.btn);

        ImageLoader.getInstance().displayImage(getItem(position).proInfo.get(0).pic, vh.img, ImageLoadOptions.getOptions());
        vh.name.setText(getItem(position).proInfo.get(0).name);
        vh.speci.setText("规格: " + getItem(position).proInfo.get(0).specialTitle);
        vh.price.setText("¥" + getItem(position).proInfo.get(0).Price);
        vh.num.setText("x" + getItem(position).proInfo.get(0).num);
        vh.goodsNum.setText("共" + getItem(position).orderInfo.count + "件商品");
        vh.distribution.setText("¥" + getItem(position).orderInfo.fxcomm);
        vh.total.setText("¥" + getItem(position).orderInfo.totalPrice);
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView name;
        TextView speci;
        TextView price;
        TextView num;
        TextView btn;
        TextView goodsNum;
        TextView distribution;
        TextView total;
    }

    private void initBtn(final TextView view) {
        switch (state) {
            case 10:
                view.setVisibility(View.GONE);
                break;
            case 20:
                break;
            case 30:
                view.setText("查看物流");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) view.getTag();
                        Intent intent = new Intent(ctx, LogisticsDetialsActivity.class);
                        intent.putExtra("eCode", getItem(position).orderInfo.eCode);
                        intent.putExtra("shippingCode", getItem(position).orderInfo.shippingCode);
                        ctx.startActivity(intent);
                    }
                });
                break;
            case 50:
                view.setText("售前退款");
                break;
            case 40:
                view.setVisibility(View.GONE);
                break;
        }
    }
}

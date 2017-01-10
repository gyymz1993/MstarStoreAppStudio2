package cn.mstar.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.entity.PromotionProduct;
import cn.mstar.store.interfaces.AdapterCallBack;
import cn.mstar.store.utils.ImageLoadOptions;

/**
 * Created by 1 on 2016/1/6.
 */
public class PromotionContentAdapter extends BaseAdapter {
    private Context ctx;
    private List<PromotionProduct> dataList;
    private Map<String, Boolean> checkedState;

    private int type = 0;

    public PromotionContentAdapter(Context ctx, List<PromotionProduct> dataList) {
        this.ctx = ctx;
        this.dataList = dataList;
        checkedState = new HashMap<>();
    }

    public void selectAll() {
        for (PromotionProduct t : dataList) {
            checkedState.put(t.proId, true);
        }
    }

    public void cancelAll() {
        checkedState.clear();
    }

    public String getIds() {
        StringBuilder sb = new StringBuilder("");
        Set<String> set = checkedState.keySet();
        int i = 0;
        for (String s : set) {
            if (i++ == 0) {
                sb.append(s);
            } else {
                sb.append("|").append(s);
            }
        }
        return sb.toString();
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public PromotionProduct getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_manage_goods_layout, parent, false);
            vh.check = (CheckBox) convertView.findViewById(R.id.check);
            vh.img = (ImageView) convertView.findViewById(R.id.img);
            vh.name = (TextView) convertView.findViewById(R.id.name);
            vh.price = (TextView) convertView.findViewById(R.id.price);
            vh.txt1 = (TextView) convertView.findViewById(R.id.item_txt1);
            vh.txt2 = (TextView) convertView.findViewById(R.id.item_txt2);
            vh.buyers_num = (TextView) convertView.findViewById(R.id.buyers_num);
            vh.buyers_num.setVisibility(View.VISIBLE);
            vh.sub_prom = (TextView) convertView.findViewById(R.id.sub_prom);
            vh.sub_upload = (TextView) convertView.findViewById(R.id.sub_upload);
            convertView.findViewById(R.id.item_handle).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.handle_bar).setVisibility(View.GONE);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String prom = null;
        String upload = null;
        switch (type) {
            case 0:
                vh.sub_prom.setVisibility(View.VISIBLE);
                vh.sub_upload.setVisibility(View.VISIBLE);
                if (getItem(position).ifNew == 1) {
                    upload = "取消上新";
                    vh.sub_upload.setTag("取消上新");
                } else {
                    upload = "上新";
                    vh.sub_upload.setTag("上新");
                }
                if (getItem(position).ifPro == 1) {
                    prom = "取消促销";
                    vh.sub_prom.setTag("取消促销");
                } else {
                    prom = "促销";
                    vh.sub_prom.setTag("促销");
                }

                break;
            case 1:
                vh.sub_upload.setVisibility(View.VISIBLE);
                vh.sub_prom.setVisibility(View.GONE);
                upload = "取消";
                vh.sub_upload.setTag("取消上新");
                break;
            case 2:
                vh.sub_upload.setVisibility(View.GONE);
                vh.sub_prom.setVisibility(View.VISIBLE);
                prom = "取消";
                vh.sub_prom.setTag("取消促销");
                break;
        }
        vh.sub_prom.setText(prom);
        vh.sub_upload.setText(upload);
        setClick(vh.sub_prom, position);
        setClick(vh.sub_upload, position);

        ImageLoader.getInstance().displayImage(getItem(position).pic, vh.img, ImageLoadOptions.getOptions());
        if (checkedState.get(getItem(position).proId) != null) {
            vh.check.setChecked(true);
        } else {
            vh.check.setChecked(false);
        }
        vh.check.setTag(position);
        vh.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedState.get(getItem(position).proId) != null) {
                    checkedState.remove(getItem(position).proId);
                } else {
                    checkedState.put(getItem(position).proId, true);
                }
                callBack.changeState(isAllSelected());
            }
        });
        vh.name.setText(getItem(position).name);
        vh.price.setText("售价¥" + getItem(position).price);
        vh.txt1.setText("销售佣金¥" + getItem(position).salecomm);
        vh.txt2.setText("分销佣金¥" + getItem(position).fxcomm);
        vh.buyers_num.setText(getItem(position).sales + "人在买");
        return convertView;
    }

    class ViewHolder {
        CheckBox check;
        ImageView img;
        TextView name;
        TextView price;
        TextView txt1;
        TextView txt2;
        TextView buyers_num;
        TextView sub_prom;
        TextView sub_upload;
    }


    private void setClick(TextView view, final int position) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.subClick((TextView) v, position);
            }
        });
    }

    private AdapterCallBack callBack;

    public void setOnAdapterCallBack(AdapterCallBack callBack) {
        this.callBack = callBack;
    }

    public boolean isAllSelected() {
        return checkedState.size() == dataList.size();
    }
}

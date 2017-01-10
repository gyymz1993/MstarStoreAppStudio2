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

import cn.mstar.store.R;
import cn.mstar.store.entity.MyStoreProductEntity;
import cn.mstar.store.entity.PromotionProduct;
import cn.mstar.store.interfaces.AdapterCallBack;
import cn.mstar.store.utils.ImageLoadOptions;

/**
 * Created by 1 on 2016/1/6.
 */
public class ManageProductAdapter extends BaseAdapter {

    private Context ctx;
    private List<MyStoreProductEntity> dataList;
    private Map<String, MyStoreProductEntity> checkedState;

    private int type = 0;

    public ManageProductAdapter(Context ctx, List<MyStoreProductEntity> dataList) {
        this.ctx = ctx;
        this.dataList = dataList;
        checkedState = new HashMap<>();
    }

    public void selectAll() {
        for (MyStoreProductEntity t : dataList) {
            checkedState.put(t.proId, t);
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

    public String getCommonIds() {
        StringBuilder sb = new StringBuilder("");
        Set<String> set = checkedState.keySet();
        int i = 0;
        for (String key : set) {
            if (i++ == 0) {
                sb.append(checkedState.get(key).goodsCommonid);
            } else {
                sb.append("|").append(checkedState.get(key).goodsCommonid);
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
    public MyStoreProductEntity getItem(int position) {
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
            vh.state = (TextView) convertView.findViewById(R.id.state);
            vh.delete = (TextView) convertView.findViewById(R.id.delete);
            vh.undercarriage = (TextView) convertView.findViewById(R.id.let_down);
            vh.gettingGoods = (TextView) convertView.findViewById(R.id.get_product);
            vh.modification = (TextView) convertView.findViewById(R.id.mend);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.state.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        vh.delete.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        vh.gettingGoods.setVisibility(type == 3 ? View.VISIBLE : View.GONE);
        String goodsType = getItem(position).goodsType;
        vh.state.setText("2".equals(goodsType) ? "本店" : "3".equals(goodsType) ? "平台" : "");
        vh.undercarriage.setText("0".equals(getItem(position).state) ? "上架" : "下架");
        vh.undercarriage.setTag("0".equals(getItem(position).state) ? "上架" : "下架");
        vh.delete.setTag("删除");
        vh.gettingGoods.setTag("拿货");
        vh.modification.setTag("修改");
        setClick(vh.delete, position);
        setClick(vh.undercarriage, position);
        setClick(vh.gettingGoods, position);
        setClick(vh.modification, position);

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
                    checkedState.put(getItem(position).proId, getItem(position));
                }
                if (callBack != null)
                    callBack.changeState(isAllSelected());
            }
        });
        vh.name.setText(getItem(position).name);
        vh.price.setText("售价¥" + getItem(position).price);
        vh.txt1.setText("拿货价¥" + getItem(position).costPrice);
        vh.txt2.setText("建议零售价¥" + getItem(position).marketPrice);
        return convertView;
    }

    class ViewHolder {
        CheckBox check;
        ImageView img;
        TextView name;
        TextView price;
        TextView txt1;
        TextView state;
        TextView txt2;
        TextView delete;
        TextView undercarriage;
        TextView gettingGoods;
        TextView modification;
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
        return checkedState.size() == dataList.size() && dataList.size() != 0;
    }
}

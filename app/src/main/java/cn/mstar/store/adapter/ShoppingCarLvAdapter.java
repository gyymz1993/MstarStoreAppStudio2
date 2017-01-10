package cn.mstar.store.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.activity.MainActivity;
import cn.mstar.store.activity.ProductDetailsActivity;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.customviews.CustomListeningTextview;
import cn.mstar.store.entity.ShoppingCartItem;
import cn.mstar.store.functionutils.RequestUtils;
import cn.mstar.store.interfaces.OnResultStatusListener;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

public class ShoppingCarLvAdapter extends BaseAdapter {

    private final String TAG="ShoppingCartLvAdatpter";
    private static final String POSITION = "position";
    private Context mContext;
    private List<ShoppingCartItem> data;
    private  LayoutInflater inf;
    private int SCREEN_WIDTH;

    public ShoppingCarLvAdapter(Activity  activity,List<ShoppingCartItem> dt, int scw) {
        this.data = dt;
        //this.data = new ArrayList<>(dt);
        this.mContext=activity;
        inf = LayoutInflater.from(mContext);
        SCREEN_WIDTH = scw;
        checkedState = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            checkedState.put(POSITION + i, false);
        }
    }
   private void removeAt(int position) {
        if (data != null) {
            data.remove(position);
           // data = new ArrayList<>(data);
            checkedState.remove(POSITION + position);
            updateBottomTotal();
            if (data.size() == 0){
                if (onNoShop!=null){
                    onNoShop.onNoShop();
                }
            }
            notifyDataSetChanged();
        }
    }

    public void notifyChangeData(){
        data.clear();
        notifyDataSetChanged();
    }

    OnShopCartCountChange  onNoShop;
    public void setOnNoShop(OnShopCartCountChange onNoShop) {
        this.onNoShop = onNoShop;
    }

    public interface  OnShopCartCountChange{
        void  onNoShop();
    }

    // 通过context去改变fragment里的数据  计算总计
    private void updateBottomTotal() {
        if (checkedState != null) {
            List<ShoppingCartItem> checkedGoods = new ArrayList<>();
            for (String key : checkedState.keySet()) {
                if (checkedState.get(key) != null && checkedState.get(key) == true) {
                    int i = Integer.valueOf(key.substring(POSITION.length()));
                    ShoppingCartItem cartItem = data.get(i);
                    checkedGoods.add(cartItem);
                    LogUtils.e("ShoppingCarLvAdapter" +"i=:"+i+"---"+cartItem.toString());
                }
            }
            ((MainActivity) mContext).updateBottomTotal(checkedGoods);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder vh;
        if (convertView == null) {
            view = inf.inflate(R.layout.shopping_car_item, null);
            vh = new ViewHolder(view);
        } else {
            view = convertView;
            vh = (ViewHolder) convertView.getTag();
        }
        boolean isChecked = false;
        if (checkedState != null && checkedState.containsKey(POSITION + position)) {
            isChecked = checkedState.get(POSITION + position);
        }
        final ShoppingCartItem item = (ShoppingCartItem) getItem(position);
        LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) vh.tv_title.getLayoutParams();
        params.width = SCREEN_WIDTH / 2;
        vh.tv_title.setLayoutParams(params);  //将文字设置为两行

        vh.tv_title.setText(item.name);
        vh.tv_title.setText(item.name);
        vh.ck_category_name.setChecked(isChecked);
        vh.tv_product_commodities.setText(item.specialTitle);
        vh.tv_unit_price.setText(Double.toString(item.price));

        if (item.number != 0) {
            vh.tv_item_count_static.setText("X" + item.number);
            vh.tv_item_count_dynamic.setText("" + item.number);
        }
        ImageLoader.getInstance().displayImage(item.pic, vh.iv_item_pic, ImageLoadOptions.getOptions());
        //跳转商品详情页
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityWithId(item.proId);
            }
        });

        //选择
        vh.ck_category_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedState.put(POSITION + position, vh.ck_category_name.isChecked());
                updateBottomTotal();
            }
        });

        //删除
        vh.iv_delete_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });
        //数量-1
        vh.iv_sous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.valueOf(vh.tv_item_count_dynamic.getText().toString());
                if (val > 1) {
                    val--;
                    vh.tv_item_count_dynamic.setText(val + "");
                    updateText(val + "", position, vh.tv_item_count_dynamic, vh.tv_item_count_static);
                    updateBottomTotal();
                }
            }
        });
        //数量加加
        vh.iv_plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.valueOf(vh.tv_item_count_dynamic.getText().toString());
                if (val < item.stock) {
                    val++;
                    vh.tv_item_count_dynamic.setText(val + "");
                    updateText(val + "", position, vh.tv_item_count_dynamic, vh.tv_item_count_static);
                    updateBottomTotal();
                } else {
                    CustomToast.makeToast(mContext, mContext.getString(R.string.outofstock), Toast.LENGTH_SHORT);
                }
            }
        });
        view.setTag(vh);
        return view;
    }


    /*
     * 创建人：Yangshao
     * 创建时间：2016/3/17 9:20
     * @version      商品  详情 页面
     *
     */
    protected void startActivityWithId(int proId) {
        Intent intent = new Intent(mContext, ProductDetailsActivity.class);
        intent.putExtra("proId", proId);
        intent.setAction(MyAction.productListActivityAction);
        mContext.startActivity(intent);
    }

    public void updateText(String string, int position, TextView tv_item_count_dynamic, TextView tv_item_count_static) {
        String s = tv_item_count_dynamic.getText().toString();
        tv_item_count_static.setText("X " + s);
        updateDataSet(position, Integer.valueOf(s));
        updateBottomTotal();
    }

    private void updateDataSet(int position, int newCount) {
        ShoppingCartItem cartItem = data.get(position);
        if (newCount != cartItem.number) {
            cartItem.number = newCount;
            if (position < data.size()) {
                data.set(position, cartItem);
                notifyDataSetChanged();
            }
        }
    }

    Dialog alertDialog = null;
    public void delete(final int position) {
        final ShoppingCartItem item = (ShoppingCartItem) getItem(position);
        final AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).create();
        View view = LayoutInflater.from(mContext).inflate(R.layout.scan_history_dialog, null, false);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText("你确定要删除这个记录吗？");
        mAlertDialog.setView(view);
        mAlertDialog.show();
        view.findViewById(R.id.scan_history_cancel_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        view.findViewById(R.id.scan_history_confirm_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestUtils.checkLogStatus(new VolleyRequest.LogonStatusLinstener() {
                    @Override
                    public void OK(String reason) {
                        // 从网络上删掉
                        RequestUtils.deleteShoppingCartItem(item, new OnResultStatusListener() {
                            public void success(String result) {
                                // 添加成功
                                dismissDialog();
                                CustomToast.makeToast(mContext, mContext.getString(R.string.del_shopping_cart_success), Toast.LENGTH_SHORT);
                                ((MainActivity) (mContext)).inflateDatas();

                                removeAt(position);

                                mAlertDialog.dismiss();
                            }

                            @Override
                            public void failure(String error) {
                                switch (error) {
                                    case "0":
                                        // 添加失败
                                        CustomToast.makeToast(mContext, mContext.getString(R.string.del_shopping_cart_failure), Toast.LENGTH_SHORT);
                                        break;
                                    case "1":
                                        // 网络失败
                                        CustomToast.makeToast(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT);
                                        break;
                                }
                                mAlertDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void NO() {
                        // 本地删除
                        CustomToast.makeToast(mContext, mContext.getString(R.string.delete_error), Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog.cancel();
            alertDialog = null;
        }
    }

    private Map<String, Boolean> checkedState;

    public void checkAll(boolean isChecked) {
        if (checkedState == null) {
            checkedState = new HashMap<>();
        }
        for (int i = 0; i < getCount(); i++) {
            checkedState.put(POSITION + i, isChecked);
        }
        updateBottomTotal();
    }


    public void addAll(List<ShoppingCartItem> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        for (ShoppingCartItem shoppingCartItem : data) {
            data.add(shoppingCartItem);
            notifyDataSetChanged();
        }
    }


    class ViewHolder {

        @Bind(R.id.ck_radiobutton_category_name)
        CheckBox ck_category_name;
        @Bind(R.id.shopping_car_shopname) TextView tv_shopName;
        @Bind(R.id.iv_image)
        public ImageView iv_item_pic;
        @Bind(R.id.iv_icon_del)
        public ImageView iv_delete_icon;
        @Bind(R.id.btn_commodity_plus)
        public ImageView iv_plus;
        @Bind(R.id.btn_commodity_minus)
        public ImageView iv_sous;
        @Bind(R.id.tv_item_name)
        public TextView tv_title;
        @Bind(R.id.tv_item_price)
        public TextView tv_unit_price;
        @Bind(R.id.tv_item_count_static)
        public TextView tv_item_count_static;
        @Bind(R.id.btn_commodity_number_display)
        public CustomListeningTextview tv_item_count_dynamic;
        @Bind(R.id.tv_product_commodities)
        public TextView tv_product_commodities;


        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }

    }

}

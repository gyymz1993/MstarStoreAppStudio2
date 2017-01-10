package cn.mstar.store.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.activity.ConfirmIndentActivity;
import cn.mstar.store.activity.MainActivity;
import cn.mstar.store.adapter.ShoppingCarLvAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.ShoppingCartItem;
import cn.mstar.store.interfaces.OnGoodsCheckedListener;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

public class ShoppingCartFragment extends Fragment implements OnGoodsCheckedListener,ShoppingCarLvAdapter.OnShopCartCountChange {

    private static final String TAG = "ShoppingCartFragment";
    private ShoppingCarLvAdapter mAdapter;
    @Bind(R.id.ck_checkall) CheckBox ck_checkAll;
    @Bind(R.id.tv_totalPrice) TextView tv_totalPrice;
    @Bind(R.id.bt_go_pay) Button bt_go_pay;
    @Bind(R.id.wifi_retry) TextView tv_wifi_retry;
    @Bind(R.id.title_name) TextView tv_title;
    protected ListView lv_list;
    private View rootView;
    private String link;
    private int cartTotalItem = 0;
    List<ShoppingCartItem> outsideCheckedGoods;
    public List<ShoppingCartItem> data = new ArrayList<>();
    private RelativeLayout  rel_contentLayout;   //有内容
    private LinearLayout no_result_layout;    //无内容
    private LinearLayout  lny_network_error_view;  //无网络
    private LinearLayout lny_loading_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shoppingcart, container, false);
        ButterKnife.bind(this, rootView);
        MainActivity.curu_Tab= MainActivity.TAB_SHOPPING_CART;
        link = AppURL.LIST_SHOPPING_CART+"&key="+ Utils.getTokenKey((MyApplication) getActivity().getApplication());
        initViews(rootView);
        return rootView;
    }



    protected void hideAllView () {
        lny_loading_layout.setVisibility(View.GONE);
        lny_network_error_view.setVisibility(View.GONE);
        no_result_layout.setVisibility(View.GONE);
    }


    protected void showRelcontentLayout () {
        rel_contentLayout.setVisibility(View.VISIBLE);
        rel_contentLayout.setEnabled(true);
    }

    protected void hideRelcontentLayout () {
        rel_contentLayout.setVisibility(View.GONE);
        rel_contentLayout.setEnabled(false);
    }


    protected void showLoadingView () {
        hideAllView();
        lny_loading_layout.setVisibility(View.VISIBLE);
    }

    protected void showNetworkErrorView() {
        hideAllView();
        hideRelcontentLayout();
        data.clear();
        lny_network_error_view.setVisibility(View.VISIBLE);

    }


    public  void showNoResultView() {
        hideAllView();
        hideRelcontentLayout();
        no_result_layout.setVisibility(View.VISIBLE);
        data.clear();
        updateBottom(data);
    }


    protected void initViews (View view) {
        lny_loading_layout = (LinearLayout) view.findViewById(R.id.lny_loading_layout);
        lny_network_error_view = (LinearLayout) view.findViewById(R.id.lny_network_error_view);
        rel_contentLayout= (RelativeLayout) view.findViewById(R.id.rel_contentLayout);
        no_result_layout = (LinearLayout) view.findViewById(R.id.lny_no_result);
        lv_list = (ListView) view.findViewById(android.R.id.list);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.myshoppingcar));
        showLoadingView();

        mAdapter = new ShoppingCarLvAdapter(getActivity(),data, getScreenWidth());
        mAdapter.setOnNoShop(this);
        lv_list.setAdapter(mAdapter);
    }




    @Override
    public void onResume() {
        super.onResume();
        inflateDatas();
    }


    @OnClick(R.id.wifi_retry)
    public void inflateDatas() {
        data.clear();
        showProgress();
        if (ck_checkAll!= null)
            ck_checkAll.setChecked(false);
        link+="&size=240&page=10000";
        LogUtils.e(TAG + "购物车URL:" + link);
        VolleyRequest.GetCookieRequest(getActivity(), link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    dismissProgress();
                    Gson gson = new Gson();
                    JsonArray item = gson.fromJson(result, JsonObject.class).getAsJsonObject("data").get("cartInfo").getAsJsonArray();
                    cartTotalItem = gson.fromJson(result, JsonObject.class).getAsJsonObject("data").get("list_count").getAsInt();
                    Type type = new TypeToken<ShoppingCartItem[]>() {
                    }.getType();
                    ShoppingCartItem[] itemz = gson.fromJson(item, type);
                    if (item == null || item.size() == 0) {
                        showNoResultView();
                    } else {
                        for (ShoppingCartItem tmp : itemz) {
                            if (!data.contains(tmp)) {
                                data.add(tmp);
                            }
                        }
                    }
                    hideAllView();
                    showRelcontentLayout();
                    setAdapter();
                } catch (Exception e) {
                    dismissProgress();
                    showNoResultView();
                }
            }

            @Override
            public void onFail(String error) {
                dismissProgress();
                showNetworkErrorView();
            }
        });
    }

    private void setAdapter() {
        mAdapter.notifyDataSetChanged();
        ck_checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter == null) {
                    return;
                }
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    LinearLayout lny = (LinearLayout) lv_list.getChildAt(i);
                    if (lny != null) {
                        CheckBox ck = (CheckBox) lny.findViewById(R.id.ck_radiobutton_category_name);
                        if (ck != null) {
                            ck.setChecked(ck_checkAll.isChecked());
                        }
                        if (mAdapter != null) {
                            mAdapter.checkAll(ck_checkAll.isChecked());
                        }
                    }
                }
                if (mAdapter.getCount() == 0) {
                    if (ck_checkAll.isChecked()) {
                        ck_checkAll.setChecked(false);
                    }
                }
            }
        });

        bt_go_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // outsideCheckedGoods 是一个  List<ShoppingCartItem>
                if (bt_go_pay.isEnabled() && outsideCheckedGoods != null && outsideCheckedGoods.size() > 0) {
                    Intent intent = new Intent(getActivity(), ConfirmIndentActivity.class);
                    // 把所有产品传给下一个activity
                    intent.setAction(MyAction.goPayAction);
                    Object obj = outsideCheckedGoods.get(0);
                    intent.putExtra("outsideCheckedGoods", (Serializable) outsideCheckedGoods);
                    startActivity(intent);
                }
            }
        });
    }




    public void showProgress() {
        showLoadingView();
    }

    public void dismissProgress () {
        showLoadingView();
    }

    public int getScreenWidth () {
        DisplayMetrics metrics = new DisplayMetrics();
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    public static Fragment getInstance(String s) {
        Fragment frg = new ShoppingCartFragment();
        Bundle args = new Bundle();
        args.putString(TAG, s);
        frg.setArguments(args);
        return frg;
    }



    @Override
    public void updateBottom(List<ShoppingCartItem> checkedGoods) {
        try {
            if (checkedGoods == null || checkedGoods.size() == 0)
                ck_checkAll.setChecked(false);
            outsideCheckedGoods = checkedGoods;
            Double total = .0;
            for (ShoppingCartItem item: checkedGoods) {
                total+=item.price*item.number;
            }
            if (checkedGoods.size() != mAdapter.getCount() || mAdapter.getCount() == 0) {
                ck_checkAll.setChecked(false);
                // make them not to influence the others.
            } else {
                ck_checkAll.setChecked(true);
                // make them not to influence the others.
            }
            //价格
            tv_totalPrice.setText(getString(R.string.yuan_char)+ Utils.formatedPrice(total));
            if (checkedGoods.size() == 0) {
                bt_go_pay.setEnabled(false);
            } else {
                bt_go_pay.setEnabled(true);
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onNoShop() {
        showNoResultView();
    }
}

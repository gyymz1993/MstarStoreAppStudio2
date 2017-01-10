package cn.mstar.store.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.mvp.model.JsonShowRingDB;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.VerticalViewPager;
import cn.mstar.store.utils.ImageLoadOptions;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;


public class ChooseRingPreview extends Activity{

    final  String TAG="ChooseRingPreview";
    private LinearLayout ly_content1;
    private LinearLayout ly_content;
    private LinearLayout lyShowData;
    private ImageView id_ig_view1;
    private ImageView id_ig_view;
    private TextView pay_total;
    private int proid,ringid;
    private TextView id_tv_prepay,id_tv_total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_ring_preview);
        proid = getIntent().getIntExtra("proid",0);
        ringid = getIntent().getIntExtra("ringid", 0);
        LogUtils.e(TAG+"\n"+proid);
        intiTitle();
        initView();
        initTypeLayout();
        showLoading();
        loadNetData();
    }

    private ImageView titleBack;
    private void intiTitle() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("定制详情");
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        ly_content1= (LinearLayout) findViewById(R.id.id_ly_content1);
        ly_content= (LinearLayout) findViewById(R.id.id_ly_content);
        id_ig_view1= (ImageView) findViewById(R.id.id_ig_view1);
        id_ig_view= (ImageView) findViewById(R.id.id_ig_view);
        pay_total= (TextView) findViewById(R.id.id_pay_total);
        id_tv_prepay= (TextView) findViewById(R.id.id_tv_prepay);
        id_tv_total= (TextView) findViewById(R.id.id_tv_total);
        lyShowData=(LinearLayout)findViewById(R.id.id_ly_show_data);
        pay_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChooseRingPreview.this, ConfirmIndentActivity.class);
                intent.setAction(MyAction.ringPreviewAction);
                LogUtils.e(TAG + "url" + getPayUrl());
                intent.putExtra("url", getPayUrl());
                startActivity(intent);
            }
        });
    }


    LinearLayout ly_loading;
    LinearLayout ly_noData;
    LinearLayout ly_netError;
    TextView tv_reset;
    private void initTypeLayout(){
        ly_loading = (LinearLayout) findViewById(R.id.lny_loading_layout);
        ly_noData = (LinearLayout) findViewById(R.id.lny_no_result);
        ly_netError = (LinearLayout) findViewById(R.id.lny_network_error_view);
        tv_reset = (TextView) findViewById(R.id.wifi_retry);
    }

    private void showLoading() {
        dismissAllView();
        lyShowData.setVisibility(View.GONE);
        ly_loading.setVisibility(View.VISIBLE);
    }

    private void showDataing() {
        dismissAllView();
        lyShowData.setVisibility(View.VISIBLE);
    }

    private void showNoResult() {
        dismissAllView();
        ly_noData.setVisibility(View.VISIBLE);
    }

    private void dismissAllView() {
        ly_loading.setVisibility(View.GONE);
        ly_noData.setVisibility(View.GONE);
        ly_netError.setVisibility(View.GONE);
    }

    private void showNetError() {
        dismissAllView();
        ly_netError.setVisibility(View.VISIBLE);
    }


    private String getPayUrl(){
        return AppURL.RING_PAY+"&proId="+proid+"|"+ringid+"&proId_number="+1+"|"+1;
    }

    private void loadNetData() {
        LogUtils.e(TAG + "URL" + getUrl());
        VolleyRequest.GetCookieRequest(this, getUrl(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                showDataing();
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if (error.equals("0")) {
                    JsonShowRingDB jsonShowRingDB = gson.fromJson(result, JsonShowRingDB.class);
                    JsonShowRingDB.DataEntity.DiamondInfoEntity diamond_info = jsonShowRingDB.getData().getDiamond_info();
                    JsonShowRingDB.DataEntity.RingInfoEntity ring_info = jsonShowRingDB.getData().getRing_info();
                    JsonShowRingDB.DataEntity.TotalInfoEntity total_info = jsonShowRingDB.getData().getTotal_info();
                    ImageLoader.getInstance().displayImage(diamond_info.getPic(), id_ig_view, ImageLoadOptions.getOptions());
                    ImageLoader.getInstance().displayImage(ring_info.getPic(), id_ig_view1, ImageLoadOptions.getOptions());
                    addTextVie(diamond_info);
                    addTextVie1(ring_info);
                    showPrice(total_info);
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                }
            }
            @Override
            public void onFail(String error) {
                showNetError();
            }
        });
    }

    private void showPrice(JsonShowRingDB.DataEntity.TotalInfoEntity total_info) {
        id_tv_prepay.setText(""+total_info.getYfprice()+"");
        id_tv_total.setText("总价："+getResources().getString(R.string.yuan_char)+total_info.getTotalprice());
    }

    public void addTextVie1(JsonShowRingDB.DataEntity.RingInfoEntity ring_info)  {
        List<String> titleNames=new ArrayList<>();
        titleNames.clear();
        titleNames.add("名称:" + ring_info.getName());
        titleNames.add("价格:" + ring_info.getPrice());
        for (int i=0,n=ring_info.getAttr().size();i<n;i++){
            titleNames.add(ring_info.getAttr().get(i));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams linelayoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, 1);
        for (int i = 0,n=titleNames.size(); i < n; i++) {
            TextView textView = new TextView(this);
            textView.setText(titleNames.get(i));
            textView.setTextSize(12);
            textView.setTag(i + 1);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            if (i==0){
                textView.setTextSize(15);
                textView.setTextColor(Color.BLACK);
            }
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.between_line));
            layoutParams.setMargins(1, 1, 1, 1);
            textView.setPadding(1, 1, 1, 1);
            ly_content1.addView(textView, layoutParams);
            ly_content1.addView(view, linelayoutParams);

            TextView button=null;
            if (i==(n-1)){
                button=new TextView(this);
                //  button.setBackgroundColor(getResources().getColor(R.color.color_btn));
                button.setBackgroundResource(R.drawable.shape_buttnon);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(20, 10, 1, 1);
                button.setText("重选戒托 ▶");
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setTextSize(13);
                button.setTextColor(Color.WHITE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChooseRingPreview.this, ChooseRingFrameActivity.class);
                        intent.putExtra("proid", proid);
                        startActivity(intent);
                    }
                });
                ly_content1.addView(button, layoutParams2);
            }
        }
    }



    public void addTextVie(JsonShowRingDB.DataEntity.DiamondInfoEntity diamond_info)  {
        List<String> titleNames1=new ArrayList<>();
        titleNames1.clear();
        titleNames1.add("名称:" + diamond_info.getName());
        titleNames1.add("价格:" + diamond_info.getPrice());
        for (int i=0,n=diamond_info.getAttr().size();i<n;i++){
            titleNames1.add(diamond_info.getAttr().get(i));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams linelayoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, 1);
        for (int i = 0,n=titleNames1.size();i < titleNames1.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(titleNames1.get(i));
            textView.setTextSize(12);
            textView.setTag(i + 1);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            if (i==0){
                textView.setTextSize(15);
                textView.setTextColor(Color.BLACK);
            }
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.between_line));
            layoutParams.setMargins(1, 1, 1, 1);
            textView.setPadding(1, 1, 1, 1);
            ly_content.addView(textView, layoutParams);
            ly_content.addView(view, linelayoutParams);
            if (i==(n-1)){
                TextView button=new TextView(this);
                //  button.setBackgroundColor(getResources().getColor(R.color.color_btn));
                button.setBackgroundResource(R.drawable.shape_buttnon);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(20, 10, 1, 1);
                button.setText("重选裸戒 ▶");
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setTextSize(13);
                button.setTextColor(Color.WHITE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChooseRingPreview.this, ChooseRingListActivity.class);
                        intent.putExtra("proid", proid);
                        startActivity(intent);
                    }
                });
                ly_content.addView(button, layoutParams2);
            }
        }
    }

    private String getUrl(){
       String url = AppURL.RING_SHOW+"&diamondId="+proid+"&ringId="+ringid+"&tokenKey="+ MyApplication.getInstance().tokenKey;
        return url;
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }
}

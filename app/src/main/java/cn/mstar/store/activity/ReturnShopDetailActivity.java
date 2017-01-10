package cn.mstar.store.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.mvp.model.JsonReturnDetail;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.KeyString;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.VerticalViewPager;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2016/3/22.
 */
public class ReturnShopDetailActivity extends Activity implements View.OnClickListener{


    private final String TAG = "ReturnShopDetailActivity";
    @Bind(R.id.title_back)
    ImageView titleBack;
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.id_ly_content)
    LinearLayout idLyContent;
    @Bind(R.id.id_btn_detail)
    Button idBtnDetail;
    @Bind(R.id.id_bt_sendshop)
    Button idBtSendshop;
    private String refundSn;

    private List<String> titleNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_returnshopdetail);
        ButterKnife.bind(this);
        refundSn = getIntent().getStringExtra(KeyString.ORDERNUMBER);
        titleName.setText("退款/退货详情页");
        titleBack.setVisibility(View.VISIBLE);
        idBtnDetail.setOnClickListener(this);
        titleBack.setOnClickListener(this);
        idBtSendshop.setOnClickListener(this);
        loadNetData();
    }

    /*
     * 创建人：Yangshao
     * 创建时间：2016/3/23 8:45
     * @version    根据网络数据填充textView
     *
     */
    public void addTextVie(JsonReturnDetail.DataEntity jsonReturnDetailData) {
        if(jsonReturnDetailData.getIfdelivery()==1){
            idBtSendshop.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams linelayoutParams = new LinearLayout.LayoutParams(VerticalViewPager.LayoutParams.MATCH_PARENT, 1);
        titleNames.add("类型：" + jsonReturnDetailData.getRefundType());
        titleNames.add("商品名称：" + jsonReturnDetailData.getGoodsName());
        titleNames.add("退货编号：" + jsonReturnDetailData.getRefundSn());
        titleNames.add("申请时间：" + jsonReturnDetailData.getAddtime());
        titleNames.add("退款金额：" + jsonReturnDetailData.getAmount());
        titleNames.add("退款数量：" + jsonReturnDetailData.getNum());
        titleNames.add("退货原因：" + jsonReturnDetailData.getBuyerMessage());
        titleNames.add("审核状态：" + jsonReturnDetailData.getSellerState());
        titleNames.add("商家备注：" + jsonReturnDetailData.getSellerMessage());
        for (int i = 0; i < titleNames.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(titleNames.get(i));
            textView.setTextSize(15);
            textView.setTag(i + 1);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(R.color.between_line));
            //layoutParams.height=dip2px(this,30);
            // layoutParams.leftMargin=dip2px(this,20);
            layoutParams.setMargins(20, 10, 0, 10);
            textView.setPadding(20, 10, 10, 10);
            idLyContent.addView(textView, layoutParams);
            idLyContent.addView(view, linelayoutParams);
        }

    }

    public void loadNetData() {
        if (getUrl()==null){
            Toast.makeText(this,"订单编号有误",Toast.LENGTH_LONG).show();
            return;
        }
        LogUtils.e(TAG + "getUrl" + getUrl());
        VolleyRequest.GetCookieRequest(this, getUrl(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                String jsonResult = URLtoUTF8Utils.toUtf8String(result);
                JsonReturnDetail jsonReturnDetail = new Gson().fromJson(jsonResult, JsonReturnDetail.class);
                if (jsonReturnDetail.getError().equals("0")) {
                    JsonReturnDetail.DataEntity jsonReturnDetailData = jsonReturnDetail.getData();
                    addTextVie(jsonReturnDetailData);
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }



    private String getUrl() {
        if (refundSn==null||refundSn.equals("")){
            return null;
        }
        return AppURL.RETUTN_SHOP_DETAILS + "&refundSn=" + refundSn;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private View inflateViewLine() {
        return LayoutInflater.from(this).inflate(R.layout.line_separation, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_btn_detail:
                Intent intent=new Intent(this,ReturnShopFlowActivity.class);
                intent.putExtra(KeyString.ORDERNUMBER,refundSn);
                startActivity(intent);
                 break;
            case R.id.id_bt_sendshop:
                Intent intent1=new Intent(this,ReturnShopExpressActivity.class);
                intent1.putExtra(KeyString.ORDERNUMBER,refundSn);
                startActivity(intent1);
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }
}

package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mstar.store.R;
import cn.mstar.store.alipay.Alipay;
import cn.mstar.store.alipay.Keys;
import cn.mstar.store.alipay.PayResult;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.interfaces.HttpRequestCallBack;
import cn.mstar.store.interfaces.PaySuccessCallback;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;
import cn.mstar.store2.wxapi.Constants;


/**
 * 支付界面
 *
 * @author wenjundu
 */


public class PayActivity extends BaseActivity implements OnClickListener {

    private static final String TAG="PayActivity";
    private static final String ALIPAY = "支付宝支付";
    private static final String WEIXINPAY = "微信支付";
    private static final String INSHOPPAY = "到店支付";

    private ListView listView;
    private int[] payDrawables;//支付方式图标
    private String[] payNames;//支付方式 名称
    private Button btnConfirm;//确定按钮
    private ImageView titleBack;//返回按钮
    private TextView titleName;//标题
    private TextView priceTV;
    private Double totalPrice;
    private int paymentId;
    private Double price;
    private String orderId;
    private String proDesc;
    private String proName;
    private String out_trade_no;
    private String url;
    private String prepay_id;

    private JSONObject wechatPayJSONObject;
    IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    private PaySuccessCallback mPaySuccessCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        msgApi.registerApp(Constants.APP_ID);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        initView();
        paymentId = getIntent().getIntExtra("paymentId", 2);
        price = getIntent().getDoubleExtra("totalPrices", 0);
        orderId = getIntent().getStringExtra("orderid");
        out_trade_no = getIntent().getStringExtra("out_trade_no");
        proName = getIntent().getStringExtra("pname");
        proDesc = getIntent().getStringExtra("pdesc");
        Log.e("ymz", "out_trade_no:" + out_trade_no + "price" + price + ",proName:" + proName + ",proDesc:" + proDesc);
        if (proName == null)
            proName = "";
        if (proDesc == null)
            proDesc = "";
        if (MyAction.fromApplyForAgent.equals(getIntent().getAction())) {
            mPaySuccessCallback = ((MyApplication) getApplication()).getmPaySuccessCallback();
        }
        initData();

    }


    private void initData() {
        payDrawables = new int[]{R.drawable.alipay, R.drawable.wechat_pay};
        payNames = new String[]{getResources().getString(R.string.zhifubao_pay), getResources().getString(R.string.wechat_pay)};
        SimpleAdapter adapter = new SimpleAdapter(this, getListViewData(),
                R.layout.item_pay_mode, new String[]{"img", "info"},
                new int[]{R.id.image, R.id.info});
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//开启单选模式
        //默认选择第一行
        listView.setItemChecked(0, true);
        Intent intent = getIntent();
        totalPrice = intent.getExtras().getDouble("totalPrices");
        priceTV.setText((Html.fromHtml(getString((R.string.pay_tips)) + "<font color='#ff0000'>" + getString(R.string.renminbi) + totalPrice + "</font>")));

        int total_fee;
        total_fee = (int) (price * 100);
        url = AppURL.PAY_FOR_WEIXIN + "out_trade_no=" + out_trade_no + "&total_fee=" + total_fee + "&openstore="
                + (MyAction.fromApplyForAgent.equals(getIntent().getAction()) ? 1 : 0);
        LogUtils.e(TAG+"请求支付路径" + url);
        VolleyRequest.GetRequest(this, url, new HttpRequestCallBack() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    wechatPayJSONObject = jsonObject.getJSONObject("data");
                    prepay_id = wechatPayJSONObject.getString("prepayid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String fail) {

            }
        });
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.pay_mode_list);
        btnConfirm = (Button) findViewById(R.id.btn_confirm_pay);
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText(getString(R.string.pay));
        priceTV = (TextView) findViewById(R.id.price_tv);

        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    private List<Map<String, Object>> getListViewData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < payDrawables.length; i++) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", payDrawables[i]);
            map.put("info", payNames[i]);
            list.add(map);
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_pay:             //确认支付
                // INVALID_POSITION 代表无效的位置。有效值的范围是 0 到当前适配器项目数减 1 。
                int select = listView.getCheckedItemPosition();
                if (ListView.INVALID_POSITION != select) {
                    String type = payNames[select];
                    if (MyAction.fromApplyForAgent.equals(getIntent().getAction())) {
                        ((MyApplication) getApplication()).setWxPaySymbol(true);
                    }
                    switch (type) {
                        case ALIPAY: //支付宝支付
                            alipay();
                            break;
                        case WEIXINPAY: //微信支付
                            weiXinPay();
                            break;
                        case INSHOPPAY: //到店支付
                            inShopPay();
                            break;
                    }
                }
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * alipay
     */
    private void alipay() {
        L.i("wcl-->out_trade_no:" + out_trade_no);
        new Alipay(PayActivity.this, mHandler, totalPrice, proName, proDesc, out_trade_no).pay();
    }

    /**
     * weinxinpay
     */
    private void weiXinPay() {
        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        //req.prepayId =prepay_id; /*wechatPayJSONObject.optString("prepayid")*/;
        req.prepayId = wechatPayJSONObject.optString("prepayid");
        req.packageValue = "Sign=WXPay";
        req.nonceStr = wechatPayJSONObject.optString("noncestr");
        req.timeStamp = wechatPayJSONObject.optString("timestamp");
        req.sign = wechatPayJSONObject.optString("sign");
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
    }

    /**
     * go to shop
     */
    private void inShopPay() {
        Toast.makeText(PayActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Keys.SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (MyAction.fromApplyForAgent.equals(getIntent().getAction())) {
                            mPaySuccessCallback.doChange();
                            finish();
                            overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
                        } else {
                            startActivity(new Intent(PayActivity.this, PayingTransactionSuccessActivity.class));
                        }
                        CustomToast.makeToast(PayActivity.this, "支付成功", Toast.LENGTH_SHORT);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.makeToast(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT);

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            CustomToast.makeToast(PayActivity.this, "支付未完成", Toast.LENGTH_SHORT);

                        }
                    }
                    break;
                }
                case Keys.SDK_CHECK_FLAG: {

                    CustomToast.makeToast(PayActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT);
                    break;
                }
                default:
                    break;
            }
            ((MyApplication) getApplication()).setWxPaySymbol(false);
        }
    };

    @Override
    public void startActivity(Intent intent) {
        intent.putExtra("price", price);
        intent.putExtra("orderid", orderId);
        super.startActivity(intent);
        finish();
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("price", price);
        intent.putExtra("orderid", orderId);
        super.startActivityForResult(intent, requestCode);
        finish();
        overridePendingTransition(R.anim.custom_in_anim, R.anim.custom_out_anim);
    }

    LoadingDialog dialog;

    public void i_showProgressDialog() {
        dialog = new LoadingDialog(this);
        dialog.show();
    }

    public void i_dismissProgressDialog() {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
    }
}

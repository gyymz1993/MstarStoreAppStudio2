package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.adapter.MyOrderDetailAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.entity.OrderDetailEntity;
import cn.mstar.store.entity.RefoundEntity;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/18 15:30
 * @version 订单详情页面
 *    
 */
public class MyStoreOrderDetail extends BaseActivity {

    @Bind(R.id.title_back)
    ImageView back;
    @Bind(R.id.title_name)
    TextView title;
    @Bind(R.id.product_list)
    ListView content;
    @Bind(R.id.check_logistics)
    TextView checkLog;
    @Bind(R.id.confirm_shipping)
    TextView confirmShip;
    @Bind(R.id.if_agree)
    LinearLayout agreeLayout;
    @Bind(R.id.confirm_receive)
    LinearLayout confirmLayout;
    @Bind(R.id.logis_btn)
    TextView logisBtn2;
    @Bind(R.id.confirm_receive_btn)
    TextView confirmBtn;
    @Bind(R.id.no)
    TextView no;
    @Bind(R.id.yes)
    TextView yes;

    private final String TAG="MyStoreOrderDetail";
    private LinearLayout container1;
    private TextView statusTxt;
    private TextView orderNO;
    private TextView time;
    private TextView dealStore;
    private TextView receiver;
    private TextView phone;
    private TextView address;
    private TextView logisticsTxt;
    private LinearLayout logisticsBtn;
    private TextView dealNO;
    private LinearLayout dealBtn;
    private TextView salesComm;
    private TextView distributionComm;
    private TextView shippingfee;
    private TextView actualfee;

    private LinearLayout container2;
    private TextView refund;
    private TextView saleAccount;
    private TextView applyTime;
    private TextView reason;
    private TextView platform;
    private TextView isAgree;
    private LinearLayout logInfo;
    private TextView logCom;
    private TextView logDeal;
    private TextView sendTime;

    private MyOrderDetailAdapter adapter;
    private List<OrderDetailEntity.OrderItem> data;
    private OrderDetailEntity.Order orderInfo;
    private RefoundEntity.OrderInfo orderInfo2;

    private String OrderNum;
    private int status;
    private String tokenKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mystore_order_detail_layout);
        ButterKnife.bind(this);
        getParams();
        getWidgets();
        showDialog();
        if (status != 50)
            getNetData();
        else
            getNetData2();
    }

    private void getParams() {
        data = new ArrayList<>();
        adapter = new MyOrderDetailAdapter(this, data);
        tokenKey = MyApplication.getInstance().tokenKey;
        OrderNum = getIntent().getStringExtra("OrderNum");
        status = getIntent().getIntExtra("status", 0);
    }

    private void getWidgets() {
        back.setVisibility(View.VISIBLE);
        title.setText(status == 50 ? "申请退货" : "订单详情");
        back.setOnClickListener(clickListener);
        View footer = getLayoutInflater().inflate(R.layout.order_details_footer, null);
        initFooter(footer);
        content.addFooterView(footer);
        content.setAdapter(adapter);
        confirmShip.setOnClickListener(clickListener);
        checkLog.setOnClickListener(clickListener);
        no.setOnClickListener(clickListener);
        yes.setOnClickListener(clickListener);
    }

    private void initFooter(View view) {
        container1 = (LinearLayout) view.findViewById(R.id.container1);
        statusTxt = (TextView) view.findViewById(R.id.status);
        orderNO = (TextView) view.findViewById(R.id.orderNO);
        time = (TextView) view.findViewById(R.id.time);
        dealStore = (TextView) view.findViewById(R.id.deal_store);
        receiver = (TextView) view.findViewById(R.id.receiver);
        phone = (TextView) view.findViewById(R.id.phone);
        address = (TextView) view.findViewById(R.id.address);
        logisticsBtn = (LinearLayout) view.findViewById(R.id.logistics_btn);
        logisticsTxt = (TextView) view.findViewById(R.id.logistics);
        dealBtn = (LinearLayout) view.findViewById(R.id.dealNO_btn);
        dealNO = (TextView) view.findViewById(R.id.dealNO);
        salesComm = (TextView) view.findViewById(R.id.sales_commission);
        distributionComm = (TextView) view.findViewById(R.id.distribution_commission);
        shippingfee = (TextView) view.findViewById(R.id.shipping_fee);
        actualfee = (TextView) view.findViewById(R.id.actual_pay);

        container2 = (LinearLayout) view.findViewById(R.id.container2);
        refund = (TextView) view.findViewById(R.id.refund);
        saleAccount = (TextView) view.findViewById(R.id.saler_account);
        applyTime = (TextView) view.findViewById(R.id.apply_time);
        reason = (TextView) view.findViewById(R.id.reason);
        platform = (TextView) view.findViewById(R.id.platform);
        isAgree = (TextView) view.findViewById(R.id.is_agree);
        logInfo = (LinearLayout) view.findViewById(R.id.log_info);
        logCom = (TextView) view.findViewById(R.id.com_name);
        logDeal = (TextView) view.findViewById(R.id.log_order_no);
        sendTime = (TextView) view.findViewById(R.id.send_time);
    }

    private void getNetData() {
        String url = AppURL.MY_STORE_ORDER_DETAIL + "&OrderNum=" + OrderNum + "&tokenKey=" + tokenKey;
        LogUtils.e(TAG + "订单详情页获取订单编号" + url);
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    JsonObject orderJson = gson.fromJson(j, JsonObject.class).get("order").getAsJsonObject();
                    orderInfo = gson.fromJson(orderJson, OrderDetailEntity.Order.class);
                    JsonArray orderItemJson = gson.fromJson(j, JsonObject.class).get("orderItems").getAsJsonArray();
                    OrderDetailEntity.OrderItem[] orderItems = gson.fromJson(orderItemJson, OrderDetailEntity.OrderItem[].class);
                    data.clear();
                    Collections.addAll(data, orderItems);
                    endNetRequest();
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    LogUtils.e(TAG + "获取订单编号" + message);
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyStoreOrderDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void getNetData2() {
        String url = AppURL.APPLICATION_FOR_REFOUND + "&OrderNum=" + OrderNum + "&tokenKey=" + tokenKey;
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.i("wcl-->" + result);
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    try {
                        JsonObject j = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                        JsonArray jArr = gson.fromJson(j, JsonObject.class).get("OrderList").getAsJsonArray();
                        JsonObject jdata = jArr.get(0).getAsJsonObject();
                        RefoundEntity entity = gson.fromJson(jdata, RefoundEntity.class);
                        orderInfo2 = entity.orderInfo;
                        data.clear();
                        OrderDetailEntity oe = new OrderDetailEntity();
                        OrderDetailEntity.OrderItem oo = null;
                        for (RefoundEntity.ProInfo p : entity.proInfo) {
                            oo = oe.new OrderItem();
                            oo.pic = p.pic;
                            oo.title = p.name;
                            oo.specialTitle = p.specialTitle;
                            oo.price = p.Price;
                            oo.num = p.num;
                            data.add(oo);
                        }
                        endNetRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyStoreOrderDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void endNetRequest() {
        changeView();

        if (status != 50) {
            statusTxt.setText("订单状态：" + orderInfo.statusTxt);
            orderNO.setText("订单编号：" + orderInfo.orderId);
            time.setText("下单时间：" + orderInfo.addtime);
            dealStore.setText("成交店铺：" + orderInfo.store_name);
            receiver.setText("收件人：" + orderInfo.postName);
            phone.setText("电话：" + orderInfo.mobile);
            address.setText("收货地址：" + orderInfo.address);
            logisticsTxt.setText(orderInfo.eName);
            dealNO.setText(orderInfo.shippingCode);
            salesComm.setText("¥" + "0");
            distributionComm.setText("¥" + orderInfo.fxcomm);
            shippingfee.setText("¥" + orderInfo.shipping_fee);
            actualfee.setText("¥" + orderInfo.orderTotalPrice);

        } else {
            refund.setText(Html.fromHtml("退款金额：<font color='red'>" + orderInfo2.refundAmount + "元" + "</font>"));
            saleAccount.setText(Html.fromHtml("卖家账户：<font color='black'>" + orderInfo2.buyerName + "</font>"));
            applyTime.setText(Html.fromHtml("申请时间：<font color='black'>" + orderInfo2.addtime + "</font>"));
            reason.setText(Html.fromHtml("退款原因：<font color='black'>" + orderInfo2.buyerMessage + "</font>"));
            platform.setText("平台确认：" + orderInfo2.refundState);
            isAgree.setText(Html.fromHtml("是否同意：<font color='black'>" + orderInfo2.sellerState + "</font>"));
            logCom.setText("快递公司：" + ("0".equals(orderInfo2.ifship) ? "无" : orderInfo2.eName));
            logDeal.setText("订单单号：" + ("0".equals(orderInfo2.ifship) ? "无" : orderInfo2.shippingCode));
            sendTime.setText("发货时间：" + ("0".equals(orderInfo2.ifship) ? "无" : orderInfo2.shipTime));
        }
        adapter.notifyDataSetChanged();
        addEvent();
    }

    private void changeView() {
        L.i("wcl-->" + status);
        container1.setVisibility(status != 50 ? View.VISIBLE : View.GONE);
        container2.setVisibility(status == 50 ? View.VISIBLE : View.GONE);

        int v = status == 10 ? View.GONE : View.VISIBLE;
        dealBtn.setVisibility(v);
        logisticsBtn.setVisibility(v);

        confirmShip.setVisibility(status == 20 ? View.VISIBLE : View.GONE);
        checkLog.setVisibility(status == 30 ? View.VISIBLE : View.GONE);

        agreeLayout.setVisibility(status == 50 ? View.VISIBLE : View.GONE);
        if (status == 50) {
            agreeLayout.setVisibility("0".equals(orderInfo2.ifstate) ? View.VISIBLE : View.GONE);
            logInfo.setVisibility("已同意".equals(orderInfo2.sellerState) ? View.VISIBLE : View.GONE);
            confirmLayout.setVisibility("已同意".equals(orderInfo2.sellerState) ? View.VISIBLE : View.GONE);
            logisBtn2.setEnabled("1".equals(orderInfo2.ifship));
            confirmBtn.setEnabled("1".equals(orderInfo2.ifship));
        }
    }

    private void addEvent() {
        if (status == 20) {
            logisticsBtn.setOnClickListener(clickListener);
            dealBtn.setOnClickListener(clickListener);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == logisticsBtn) {
                Intent intent = new Intent(MyStoreOrderDetail.this, ExpressSelectActivity.class);
                startActivityForResult(intent, 11);
            } else if (v == dealBtn) {
                Intent intent = new Intent(MyStoreOrderDetail.this, OrderNoActivity.class);
                startActivityForResult(intent, 12);
            } else if (v == confirmShip) {
                send();
            } else if (v == checkLog) {
                gotoLogistics();
            } else if (v == no) {
                submitIsAgree(2);
            } else if (v == yes) {
                submitIsAgree(1);
            } else if (v == logisBtn2) {
                gotoLogistics();
            } else if (v == confirmBtn) {
                receive();
            }
        }
    };

    /**
     * 确认发货
     */
    private void send() {
        String eName = logisticsTxt.getText().toString();
        String shippingCode = dealNO.getText().toString();
        String url = AppURL.CONFIRM_SEND + "&tokenKey=" + tokenKey + "&eName=" + URLtoUTF8Utils.toUtf8String(eName)
                + "&shippingCode=" + shippingCode + "&OrderNum=" + OrderNum;
        LogUtils.e(TAG + "确认发货" + url);
        showDialog();
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if ("0".equals(error)) {
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyStoreOrderDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    private void gotoLogistics() {
        Intent intent = new Intent(this, LogisticsDetialsActivity.class);
        intent.putExtra("eCode", status == 50 ? orderInfo2.eCode : orderInfo.eCode);
        intent.putExtra("shippingCode", status == 50 ? orderInfo2.shippingCode : orderInfo.shippingCode);
        startActivity(intent);
    }


    /**
     *  退款操作
     * @param isAgree 1 同意 ,2 不同意
     */
    private void submitIsAgree(int isAgree) {
        String url = AppURL.SUBMIT_RETURN_GOODS + "&OrderNum=" + orderInfo2.orderId + "&tokenKey=" + tokenKey
                + "&sellerState=" + isAgree;
        LogUtils.e(TAG+"退款操作"+url);
        showDialog();
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                if ("0".equals(error)) {
                    agreeLayout.setVisibility(View.GONE);
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                    getNetData2();
                } else {
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyStoreOrderDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
    }

    /**
     * 收货操作
     */
    private void receive() {
        //receiver 1 已收货, 2 未收货
        String url = AppURL.SUBMIT_RECEIVE + "&tokenKey=" + tokenKey + "&receiver=" + 1 + "&OrderNum=" + orderInfo2.orderId;
        LogUtils.e(TAG+"收货操作"+url);
        showDialog();
        VolleyRequest.GetCookieRequest(this, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                String message = gson.fromJson(result, JsonObject.class).get("message").getAsString();
                if ("0".equals(error)) {
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MyStoreOrderDetail.this, message, Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MyStoreOrderDetail.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 && resultCode == RESULT_OK) {
            logisticsTxt.setText(data.getStringExtra("eName"));
        } else if (requestCode == 12 && resultCode == RESULT_OK) {
            dealNO.setText(data.getStringExtra("shippingCode"));
        }
    }
}

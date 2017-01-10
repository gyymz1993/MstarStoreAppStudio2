package cn.mstar.store.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.adapter.OrderDetailsBuyProductListAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.BuyProductEntity;
import cn.mstar.store.entity.BuyPros;
import cn.mstar.store.entity.OrderDetailsEntity;
import cn.mstar.store.entity.PayType;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * 订单详情
 *
 * @author wenjundu
 */
public class IndentDetailsActivity extends BaseActivity implements OnClickListener {

    Gson gson = new Gson();

    private ListView listview;
    private List<BuyProductEntity> list;//购买产品list
    private OrderDetailsBuyProductListAdapter adapter;//购买商品列表adapter
    private TextView btContactCustomerService;//联系客服
    private ImageView titleback;
    private TextView titleName;
    private String orderDetailsInformation = "", orderId;
    OrderDetailsEntity orderDetailsEntity;
    @Bind(R.id.btn_indent_operation)
    TextView btIndentOperation;//订单操作 ：删除啊 取消订单啊
    @Bind(R.id.goods_number_tv)
    TextView tv_goods_number_tv;
    @Bind(R.id.indent_status_tv)
    TextView tv_indent_status_tv;
    @Bind(R.id.look_logistics_tv)
    TextView tv_look_logistics_tv;
    @Bind(R.id.reciever_address_layout)
    RelativeLayout receiver_info;
    @Bind(R.id.receiver_tv)
    TextView tv_receiver;
    @Bind(R.id.receiver_phone_tv)
    TextView tv_receiver_phone;
    @Bind(R.id.receives_an_address_tv)
    TextView tv_receives_an_address;
    @Bind(R.id.goods_total_prices_tv)
    TextView tv_goods_total_prices_tv;
    @Bind(R.id.discount_coupons_tv)
    TextView tv_discount_coupons_tv;
    @Bind(R.id.actual_payment_tv)
    TextView tv_actual_payment_tv;
    @Bind(R.id.order_time_tv)
    TextView tv_order_time_tv;
    @Bind(R.id.btn_request_sendBack)
    TextView btn_request_sendBack;
    @Bind(R.id.btn_pay_now)
    Button btn_pay_now;
    @Bind(R.id.payment_tv)
    TextView payTypeTextView;
    @Bind(R.id.distribution_mode_tv)
    TextView shippingType;
    @Bind(R.id.store_name)
    TextView storeName;
    @Bind(R.id.distribution_mode_layout)
    RelativeLayout shippingTypeLayout;
    private RelativeLayout recieverAddressLayout;//收货地址layout
    private LinearLayout addRecieverAddressLayout;//添加收货地址layout
    //	private ReceiverAddress receiverAddress;//收货地址类
    private TextView recieverTV, phoneTV, recieverAddressTV;//收货人，电话，收货地址
    private TextView invoice_txt;
    private PayType payType;//支付方式
    private List<PayType> payTypelist;//支付方式
    private String link;
    private MyApplication app;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent_details);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        MyApplication.getInstance().addActivity(this);
        app = (MyApplication) getApplication();
        initView();
//        orderDetailsInformation = getIntent().getStringExtra("data");
        link = getIntent().getStringExtra("link");
//        status = getIntent().getIntExtra("status", 0);
        ButterKnife.bind(this);
        Intent data3 = new Intent();
        data3.putExtra(Constants.UNLOGGED, true);
        setResult(Activity.RESULT_OK, data3);
        L.d("orderdetails", orderDetailsInformation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        i_showProgressDialog();
        L.e(link);
        VolleyRequest.GetCookieRequest(IndentDetailsActivity.this, link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    orderDetailsInformation = result;
                    JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data");
                    orderDetailsEntity = gson.fromJson(elm, OrderDetailsEntity.class);
                    orderId = orderDetailsEntity.order.orderId;
                    // 要获取到默认地址
                    initHeader();
                    initFooter();
                    initListview();
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
                    finish();
                } finally {
                    i_dismissProgressDialog();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.makeToast(IndentDetailsActivity.this, "网络异常", Toast.LENGTH_SHORT);
                i_dismissProgressDialog();
            }
        });
    }

    private void initFooter() {
        String discount = orderDetailsEntity.order.voucher_price;
        if (discount != null && !"".equals(discount)) {
            tv_goods_total_prices_tv.setText(getString(R.string.yuan_char) + Utils.formatedPrice(orderDetailsEntity.order.orderTotalPrice + Double.parseDouble(discount)));
            tv_discount_coupons_tv.setText("-" + getString(R.string.yuan_char) + Utils.formatedPrice(Double.parseDouble(discount)));
        } else {
            tv_goods_total_prices_tv.setText(getString(R.string.yuan_char) + Utils.formatedPrice(orderDetailsEntity.order.orderTotalPrice));
        }
        invoice_txt.setText(orderDetailsEntity.order.invoice_info);
        tv_actual_payment_tv.setText(getString(R.string.yuan_char) + Utils.formatedPrice(orderDetailsEntity.order.orderTotalPrice));
        tv_order_time_tv.setText(orderDetailsEntity.order.addtime);
        btIndentOperation.setOnClickListener(null);
        btIndentOperation.setVisibility(View.GONE);
        // LogUtils.e("当前状态" + orderDetailsEntity.order.status);
        if (orderDetailsEntity.order.status == 40) {
            // 获取点的那个产品
            // show the 申请退货 btn_request_sendBack
            //btIndentOperation.setVisibility(View.GONE);
            //当前状态订单已完成并处于以评价则不能申请退货
            btn_request_sendBack.setVisibility(View.GONE);
            // change the backgorund/
//			btn_pay_now.setBackgroundResource(R.drawable.notenabledgrey);
            btn_pay_now.setText(getString(R.string.product_comment));
            btn_pay_now.setTextColor(getResources().getColor(R.color.white));
            btn_pay_now.setVisibility(View.INVISIBLE);
            btn_request_sendBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如果还有些产品没申请退货，就看跳过去、没有的话、就不用跳过去、直接让用户去看看进度
                    if (isThereSomeGoodsNotReturn()) {
                        Intent intent = new Intent(IndentDetailsActivity.this, RequestGoodsReturnActivity.class);
                        intent.putExtra("data", orderDetailsInformation);
                        startActivity(intent);
                    } else {
                        CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.all_good_return_state), Toast.LENGTH_SHORT);
                    }
                }
            });
            if (isNoevaluation()) {
                btn_request_sendBack.setVisibility(View.VISIBLE);
            }
        } else if (orderDetailsEntity.order.status == 30) {
            // 已发货
            btn_pay_now.setText(getString(R.string.confirm_receipt));
//			btn_pay_now.setBackgroundResource(R.drawable.notenabledgrey);
            btn_pay_now.setTextColor(getResources().getColor(R.color.white));
            btn_pay_now.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmReceivingProduct();
                }
            });
            // confirm receive interface.
        } else if (orderDetailsEntity.order.status == 20) {
            // 已付款
            btn_pay_now.setText(getString(R.string.already_payed));
            btn_pay_now.setBackgroundResource(R.drawable.notenabledgrey);
            btn_pay_now.setTextColor(getResources().getColor(R.color.white));
            btn_request_sendBack.setText("申请退款");
            btn_request_sendBack.setVisibility(View.VISIBLE);
            btn_request_sendBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如果还有些产品没申请退货，就看跳过去、没有的话、就不用跳过去、直接让用户去看看进度
                    if (isThereSomeGoodsNotReturn()) {
                        Intent intent = new Intent(IndentDetailsActivity.this, RequestGoodsReturnActivity.class);
                        intent.putExtra("data", orderDetailsInformation);
                        startActivity(intent);
                    } else {
                        CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.all_good_return_state), Toast.LENGTH_SHORT);
                    }
                }
            });

        } else if (orderDetailsEntity.order.status == 10) {
            // 待付款
            btn_pay_now.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pay_now();
                }
            });
            // 取消订单
            btIndentOperation.setVisibility(View.VISIBLE);
            btIndentOperation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 要不要取消订单？
                    // 要的话，就取消，不要就啥都干
                    cancelOrder(orderDetailsEntity.order.orderId);
                }
            });
        } else if (orderDetailsEntity.order.status == 0) {
            // 已取消
            btn_request_sendBack.setVisibility(View.GONE);
            btn_pay_now.setText(getString(R.string.already_cancelled));
            btn_pay_now.setBackgroundResource(R.drawable.notenabledgrey);
            btn_pay_now.setTextColor(getResources().getColor(R.color.white));
            // make gone all the buttons at the bottom
        }
        // 如果是待收货的状态、就可以看物流了
    }

    private void cancelOrder(final String orderID) {
        String tokenKey = "";
        if (tokenKey == null)
            tokenKey = Utils.getTokenKey((MyApplication) getApplication());
        final AlertDialog mAlertDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.scan_history_dialog, null, false);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText("你确定要删除该订单吗？");
        view.findViewById(R.id.scan_history_confirm_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String cancelLink = AppURL.CANCEL_ORDER_LINK + "&orderNo=" + orderID + "&key=" + Utils.getTokenKey(app) + "&voucher_code=" + orderDetailsEntity.order.voucher_code;
                L.d("cancel:::", cancelLink);
                i_showProgressDialog();
                VolleyRequest.GetCookieRequest(IndentDetailsActivity.this, cancelLink, new VolleyRequest.HttpStringRequsetCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        // 取消成功
                        try {
                            if (new JSONObject(result).optString("error").equals("0")) {
                                CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.cancel_order_success), Toast.LENGTH_SHORT);
                                i_dismissProgressDialog();
                                finish();
                            } else {
                                CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.cancel_order_failure), Toast.LENGTH_SHORT);
                                i_dismissProgressDialog();
                            }
                            mAlertDialog.dismiss();
                        } catch (Exception e) {
                            // 取消失败
                            e.printStackTrace();
                            CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.cancel_order_failure), Toast.LENGTH_SHORT);
                            i_dismissProgressDialog();
                        }
                        L.d("cancel:::", "res " + result);
                    }

                    @Override
                    public void onFail(String error) {
                        // 取消失败
                        failure();
                    }
                });
            }
        });
        view.findViewById(R.id.scan_history_cancel_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.setView(view);
        mAlertDialog.show();
    }

    private void failure() {
        CustomToast.makeToast(IndentDetailsActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        finish();
    }


    private void confirmReceivingProduct() {
//		http://localhost/workspace/shopnc/mobile/index.php?act=member_order&op=order_receive&orderNo=7000000000015901&key=ab3b185aaade2ce3b15f9def978a8ad0
        String link = AppURL.CONFIRM_RECEIVING_PRODUCT + "&orderNo=" + orderId + "&key=" + Utils.getTokenKey((MyApplication) getApplication());
        VolleyRequest.GetCookieRequest(this, link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                makeToast(getString(R.string.confirm_receiving_good));
                finish();
            }

            @Override
            public void onFail(String error) {
                makeToast(getString(R.string.confirm_receiving_good_error));
                finish();
            }
        });

    }

    private void makeToast(String string) {
        CustomToast.makeToast(this, string, Toast.LENGTH_SHORT);
    }

    private boolean isThereSomeGoodsNotReturn() {
        for (OrderDetailsEntity.OrderItems entity : orderDetailsEntity.orderItems) {
            if (Integer.valueOf(entity.refund_state) == 0) {
                return true;
            }
        }
        return false;
    }


    //未评价
    private boolean isNoevaluation() {
        for (OrderDetailsEntity.OrderItems entity : orderDetailsEntity.orderItems) {
            if (Integer.valueOf(entity.evaluation_state) == 0) {
                return true;
            }
        }
        return false;
    }


    private void initListview() {
        adapter = null;
        list = Utils.orderItemzToProductEntity(orderDetailsEntity.orderItems);
        adapter = new OrderDetailsBuyProductListAdapter(this, orderDetailsEntity.order.orderId, list, orderDetailsEntity.order.status, getscreenWidth());
        listview.setAdapter(adapter);
    }

    private int getscreenWidth() {

        // get screen width

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;

    }

    private void initHeader() {
        tv_goods_number_tv.setText(orderDetailsEntity.order.orderId);
        tv_indent_status_tv.setText(orderDetailsEntity.order.statusTxt);
        // 收件人
        tv_receiver.setText(orderDetailsEntity.order.postName);
        // 收人电话
        String mobile = orderDetailsEntity.order.mobile;

        if (!"".equals(mobile) && mobile.length() > "18229998073".length())
            mobile = mobile.substring(0, "18229998073".length());
        tv_receiver_phone.setText(mobile);
        // 收人地址
        tv_receives_an_address.setText(orderDetailsEntity.order.address);

        if (/*如果不是待收货的状态，消失物流信息*/orderDetailsEntity.order.status != 30 && orderDetailsEntity.order.status != 40) {
            tv_look_logistics_tv.setVisibility(View.GONE);
        } else {
            // 设置监听
            tv_look_logistics_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IndentDetailsActivity.this, LogisticsDetialsActivity.class);
                    intent.putExtra("shippingCode", orderDetailsEntity.order.shippingCode);
                    intent.putExtra("eCode", orderDetailsEntity.order.e_code);
                    startActivity(intent);
                }
            });
        }

        if (orderDetailsEntity.order.transpway == 1) {
            shippingType.setText("顺丰快递");
        } else if (orderDetailsEntity.order.transpway == 2) {
            shippingType.setText("到店自取");
            receiver_info.setVisibility(View.GONE);
            storeName.setVisibility(View.VISIBLE);
            storeName.setText(orderDetailsEntity.order.store_name);
            /*shippingTypeLayout.setOnClickListener(new OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    TextView txt = new TextView(IndentDetailsActivity.this);
                    txt.setText(orderDetailsEntity.order.address);
                    txt.setBackground(getDrawable(R.drawable.lol));
                    txt.setGravity(Gravity.CENTER);
                    PopupWindow pop = new PopupWindow(txt, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    pop.setOutsideTouchable(true);
                    ColorDrawable cd = new ColorDrawable(0xFFFFFFFF);
                    pop.setBackgroundDrawable(cd);
                    pop.showAsDropDown(storeName, 0, 3, Gravity.RIGHT);
                }
            });*/
        }

        if (orderDetailsEntity.order.paymentId == 6) {
            payTypeTextView.setText("现金支付");
            btn_pay_now.setBackgroundColor(getResources().getColor(R.color.color_light_grey));
            btn_pay_now.setEnabled(false);
        } else if (orderDetailsEntity.order.paymentId == 7) {
            payTypeTextView.setText("在线支付");
        }

        // there has to be a pay time.
    }


    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        View headerView = LayoutInflater.from(this).inflate(R.layout.indent_detail_headview, null, true);
        View footerView = LayoutInflater.from(this).inflate(R.layout.indent_detail_footerview, null, true);
        listview.addFooterView(footerView);
        listview.addHeaderView(headerView);
        //禁止底部出现分割线
        listview.setFooterDividersEnabled(false);
        btContactCustomerService = (TextView) findViewById(R.id.btn_contact_customer_service);
        btIndentOperation = (TextView) findViewById(R.id.btn_indent_operation);
        titleback = (ImageView) findViewById(R.id.title_back);
        titleback.setVisibility(View.VISIBLE);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText(getString(R.string.indent_details));
        invoice_txt = (TextView) footerView.findViewById(R.id.distribution_mode_tv);
        titleback.setOnClickListener(this);

        btContactCustomerService.setOnClickListener(this);
        btIndentOperation.setOnClickListener(this);

        recieverTV = (TextView) findViewById(R.id.receiver_tv);
        phoneTV = (TextView) findViewById(R.id.receiver_phone_tv);
        recieverAddressTV = (TextView) findViewById(R.id.receives_an_address_tv);
        recieverAddressLayout = (RelativeLayout) findViewById(R.id.reciever_address_layout);
        addRecieverAddressLayout = (LinearLayout) findViewById(R.id.add_address_layout);
        recieverAddressLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//		Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_contact_customer_service:
                final Dialog mAlertDialog = new Dialog(this);
                mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mAlertDialog.setContentView(R.layout.product_contact_service);
                mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mAlertDialog.show();
                mAlertDialog.findViewById(R.id.product_detail_refuse).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.findViewById(R.id.product_detail_accept).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getResources().getString(R.string.service_phone_number)));
                        startActivity(intent);
                        mAlertDialog.dismiss();
                    }
                });
                break;
        /*	case R.id.reciever_address_layout://收货地址
                intent=new Intent(this,ShippingAddressActivity.class);
				startActivityForResult(intent, 0);
				break;*/
            case R.id.btn_indent_operation:
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_confirm_order://确定
                //提交订单
                pay_now();
                break;
        }
    }


    // 立即支付的function
    private void pay_now() {
        String token = Utils.getTokenKey((MyApplication) getApplication());
        String numbers = "";//number集合
        String proIds = "";//proId集合
        for (int i = 0; i < list.size(); i++) {
            BuyProductEntity entity = list.get(i);
            if (i == 0) {
                numbers = "" + entity.getBuyNum();
                proIds = "" + entity.getProduct().getProId();
            } else {
                numbers += "|" + entity.getBuyNum();
                proIds += "|" + entity.getProduct().getProId();
            }
        }
        String postUrl = "";
        postUrl = AppURL.POST_BUY_NOW_INDENT_URL + "&proId=" + proIds + "&proId_number=" + numbers +/*"&addressId="+receiverAddress.getAddressId()+*/
                "&paymentId=1"/*+payType.getTypeId()*/ + "&key=" + token;

        L.e("postUrl:" + postUrl);
        VolleyRequest.GetCookieRequest(this, postUrl, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.e("result:" + result);
                try {
                    if (new JSONObject(result).optString("error").equals("0")) {
//						CustomToast.makeToast(IndentDetailsActivity.this,getString(R.string.indent_post_successed),Toast.LENGTH_SHORT);
                        Double totalPrices = orderDetailsEntity.order.orderTotalPrice;
                        Intent intent = new Intent(IndentDetailsActivity.this, PayActivity.class);
                        intent.setAction(MyAction.confirmIndentActivityAction);
                        intent.putExtra("paymentId", orderDetailsEntity.order.paymentId);
                        intent.putExtra("orderid", orderDetailsEntity.order.orderId); // me
                        intent.putExtra("out_trade_no", orderDetailsEntity.order.out_trade_no);
                        intent.putExtra("totalPrices", totalPrices);
                        JsonElement elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data");
                        BuyPros[] proz = gson.fromJson(elm.getAsJsonObject().get("buyPros").getAsJsonArray(), BuyPros[].class);
                        if (proz != null) {
                            intent.putExtra("pname", proz[0].name);
                            intent.putExtra("pdesc", proz[0].name);
                        } else {
                            intent.putExtra("pname", "默认");
                            intent.putExtra("pdesc", "默认");
                        }
                        startActivity(intent);
                    } else {
                        CustomToast.makeToast(IndentDetailsActivity.this, new JSONObject(result).optString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
            }
        });
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

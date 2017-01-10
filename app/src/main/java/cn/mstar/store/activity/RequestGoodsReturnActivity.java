package cn.mstar.store.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.adapter.RemboursementProductAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.BuyProductEntity;
import cn.mstar.store.entity.OrderDetailsEntity;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

public class RequestGoodsReturnActivity extends AppCompatActivity {


    String orderDetailsInformation;
    private OrderDetailsEntity orderDetailsEntity;
    private Gson gson;
    TextView tv_receiver;
    TextView tv_receiver_phone;
    TextView tv_receives_an_address;
    ListView listView;
    EditText ed_message;
    private RemboursementProductAdapter adapter;
    @Bind(R.id.title_name) TextView titlebar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_goods_return);

        // directly close the activity if this order has been threated before
        // in the time.

        ButterKnife.bind(this);
        titlebar_title.setText(getString(R.string.request_sendback));
        listView = (ListView) findViewById(R.id.listview);
        inflateData();
        addHeaderandFooter();
        ButterKnife.bind(this);
        initHeaderAndFooter();
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        iv_back.setVisibility(View.VISIBLE);
        // top and bottom will be added as footers and headers.
        initListview();
    }

    private void addHeaderandFooter() {
        // add footer and headers
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.request_good_header, null));
        listView.addFooterView(getLayoutInflater().inflate(R.layout.request_good_footer, null));

        tv_receiver = (TextView) findViewById(R.id.receiver_tv);
        tv_receiver_phone = (TextView) findViewById(R.id.receiver_phone_tv);
        tv_receives_an_address = (TextView) findViewById(R.id.receives_an_address_tv);
        ed_message = (EditText) findViewById(R.id.ed_message);
    }

    private void inflateData() {

        // request the data again.
        gson = new Gson();
        orderDetailsInformation = getIntent().getStringExtra("data");
        JsonElement elm = gson.fromJson(orderDetailsInformation, JsonElement.class).getAsJsonObject().get("data");
        orderDetailsEntity = gson.fromJson(elm, OrderDetailsEntity.class);
        L.d("ZXXX", "itemz count = " + orderDetailsEntity.orderItems.length);


        // we always come with some data, if it has already been requested, then just jump to
        // the progression activity.

    }

    private void initListview() {
        List<BuyProductEntity> data = Utils.orderItemzToProductEntity(orderDetailsEntity.orderItems);
        adapter=new RemboursementProductAdapter(this, data, getScreenWidth());
        listView.setAdapter(adapter);
    }

    public int getScreenWidth () {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

/*

    private void initListview() {
        list= Utils.orderItemzToProductEntity(orderDetailsEntity.orderItems);
        adapter=new BuyProductListAdapter(this, list);
        listview.setAdapter(adapter);
    }
*/

    @OnClick(R.id.btn_sendrequest)
    public void sendingRequest () {  /*提交订单*/
        if (!"".equals(getSelectedProId())) {
            if (!"".equals(ed_message.getText().toString())) {
                String mess = "";
                mess = ed_message.getText().toString();

                try {
                    mess = URLEncoder.encode(mess, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String sendLink = AppURL.REQUEST_GOOD_RETURN_SEND + "&proId=" + getSelectedProId() + "&orderNo=" + orderDetailsEntity.order.orderId + "&message=\'" + mess +
                        "\'&key=" + Utils.getTokenKey((MyApplication) getApplication());
                L.d("sendlink", sendLink);
                final LoadingDialog dialog = new LoadingDialog(this);
                dialog.show();;
                VolleyRequest.GetCookieRequest(RequestGoodsReturnActivity.this, sendLink, new VolleyRequest.HttpStringRequsetCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        CustomToast.makeToast(RequestGoodsReturnActivity.this, getString(R.string.returnrequest_ok), Toast.LENGTH_SHORT);
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                        tv_receiver.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // set a value that let him get back till the managment activity
                                finish();
                            }
                        }, 500);
                    }

                    @Override
                    public void onFail(String error) {
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                        CustomToast.makeToast(RequestGoodsReturnActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    }
                });
            } else {
                CustomToast.makeToast(this, getString(R.string.message_cannot_be_null), Toast.LENGTH_SHORT);
            }
        } else {
            CustomToast.makeToast(this, getString(R.string.choose_return_object), Toast.LENGTH_SHORT);
        }
    }

    private String getSelectedProId() {

        // 获取所选择产品
        String idz = "";
        if (adapter != null) {
            int[] checkedItems = adapter.getCheckedItems();
            idz = "";
            for (int i: checkedItems
                    ) {
                if (i != 0) {
                    if (!"".equals(idz))
                        idz += "|";
                    idz += i;
                }
            }
        }
        return idz;
    }


    private void initHeaderAndFooter() {

        LogUtils.e("'");
        // 收件人
        tv_receiver.setText(orderDetailsEntity.order.paymentName);
        // 收人电话
        tv_receiver_phone.setText(orderDetailsEntity.order.mobile);
        // 收人地址
        tv_receives_an_address.setText(orderDetailsEntity.order.address);
        if (  orderDetailsEntity.orderItems!=null&&  orderDetailsEntity.orderItems.length!=0){
            for (OrderDetailsEntity.OrderItems  orderItem:  orderDetailsEntity.orderItems){

            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_request_goods_return, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.title_back)
    public void back () {
        finish ();
    }
    @Bind(R.id.title_back)
    ImageView iv_back;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}

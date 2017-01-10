package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.R;
import cn.mstar.store.adapter.ReceiverAddressAdapter;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.ReceiverAddress;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * 收货地址
 *
 * @author wenjundu 2015-7-23
 */
public class ShippingAddressActivity extends BaseActivity implements OnClickListener {


    private final String TAG="ShippingAddressActivity";
    private ListView addressList;
    private Button btnManageAddress;
    private ReceiverAddressAdapter adapter;
    private ImageView titleBack;//返回
    private TextView titleName;//标题
    private List<ReceiverAddress> list;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shipping_address);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        list.clear();
        adapter.notifyDataSetChanged();
        getReceiverAddressList();
        super.onResume();
    }

    private void initData() {
        token = MyApplication.getInstance().tokenKey;
        if (token.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
        list = new ArrayList<ReceiverAddress>();
        adapter = new ReceiverAddressAdapter(this, list);
        addressList.setAdapter(adapter);
        addressList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//开启单选模式
        //获取收货地址

    }

    private void getReceiverAddressList() {
        if (NetWorkUtil.isNetworkConnected(this)) {

            final LoadingDialog loadingDialog = new LoadingDialog(ShippingAddressActivity.this,
                    getString(R.string.pull_to_refresh_footer_refreshing_label));
            loadingDialog.show();
            VolleyRequest.GetCookieRequest(ShippingAddressActivity.this, AppURL.GET_USER_ADDRESS_LIST + "&key=" + token,
                    new VolleyRequest.HttpStringRequsetCallBack() {

                        @Override
                        public void onSuccess(String result) {
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                            L.e(result);
                            if (result != null) {
                                try {
                                    JSONArray jsonlist = (new JSONObject(result))
                                            .getJSONArray("data");
                                    for (int i = 0; i < jsonlist.length(); i++) {
                                        JSONObject jsonAddress = jsonlist
                                                .getJSONObject(i);
                                        ReceiverAddress receiverAddress = new ReceiverAddress();
                                        receiverAddress.setAddressId(jsonAddress
                                                .getInt("addressId"));
                                        receiverAddress.setFullPostAddress(jsonAddress
                                                .getString("fullPostAddress"));
                                        receiverAddress.setName(jsonAddress
                                                .getString("postName"));
                                        receiverAddress.setAddress(jsonAddress
                                                .getString("postAddress"));
                                        receiverAddress.setZipCode(jsonAddress
                                                .getString("zipCode"));
                                        receiverAddress.setPhone(jsonAddress
                                                .getString("mobile"));
                                        receiverAddress.setEmail(jsonAddress
                                                .getString("email"));
                                        receiverAddress.setProvinceId(jsonAddress
                                                .getInt("provinceId"));
                                        receiverAddress.setCityId(jsonAddress
                                                .getInt("cityId"));
                                        receiverAddress.setCountyId(jsonAddress
                                                .getInt("countyId"));
                                        if (0 == jsonAddress.getInt("is_default")) {
                                            receiverAddress
                                                    .setIsDefalutAddress(false);
                                        } else if (1 == jsonAddress
                                                .getInt("is_default")) {
                                            receiverAddress
                                                    .setIsDefalutAddress(true);
                                        }

                                        list.add(receiverAddress);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    L.e(e.toString());
                                }
                                adapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onFail(String error) {
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                        }
                    });

        } else {
            CustomToast.makeToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }

    }

    private void initView() {
        addressList = (ListView) findViewById(R.id.address_list);
        titleName = (TextView) findViewById(R.id.title_name);
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleBack.setVisibility(View.VISIBLE);
        titleName.setText(getString(R.string.receiver_address));
        titleBack.setOnClickListener(this);

        btnManageAddress = (Button) findViewById(R.id.btn_manage_address);
        btnManageAddress.setOnClickListener(this);

        addressList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if ("fromConfirmIndents".equals(getIntent().getAction())) {
                    //将选中行的数据返回给确认订单
                    Intent intent = new Intent();
                    intent.putExtra("ReceiverAddress", list.get(position));
                    setResult(RESULT_OK, intent);
                    LogUtils.e(TAG+"FullPostAddress"+ list.get(position).getFullPostAddress());
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back://返回按钮
                finish();
                break;

            case R.id.btn_manage_address://管理收货地址
                Intent intent = new Intent(this, ManageReceiverAddressActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            if (data != null) {
                token = data.getExtras().getString("token");
                getReceiverAddressList();
            }
        }
    }
}

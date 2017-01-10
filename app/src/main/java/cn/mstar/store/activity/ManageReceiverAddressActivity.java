package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
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
import cn.mstar.store.adapter.ManageReceiverAddressAdapter;
import cn.mstar.store.adapter.ManageReceiverAddressAdapter.OnManageReceiverAddressListener;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.entity.ReceiverAddress;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.NetWorkUtil;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;
import cn.mstar.store.utils.VolleyRequest.HttpStringRequsetCallBack;

/**
 * 管理收货地址
 *
 * @author wenjundu
 */
public class ManageReceiverAddressActivity extends BaseActivity implements
        OnClickListener, OnManageReceiverAddressListener {
    private ListView addressList;
    private Button btnCreateAddress;
    private ManageReceiverAddressAdapter adapter;
    private ImageView titleBack;// 返回
    private TextView titleName;// 标题
    private List<ReceiverAddress> list;
    private String token;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_receiver_address);
        app = (MyApplication) getApplication();
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        initView();
        initData();
    }

    private void initData() {
        list = new ArrayList<ReceiverAddress>();
        adapter = new ManageReceiverAddressAdapter(this, list);
        addressList.setAdapter(adapter);
        adapter.setManageReceiverAddressListener(this);
        token = app.tokenKey;
        if (!"".equals(token))
            getaddressList(token);
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }
        addressList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ManageReceiverAddressActivity.this,
                        CreateReceiverAddressActivity.class);
                intent.setAction(MyAction.manageReceiverAddressActivityItemAction);
                intent.putExtra("ReceiverAddress", list.get(position));
                startActivityForResult(intent, 0);
            }
        });
    }

    // 获取收货地址列表
    private void getaddressList(final String token) {
        // TODO Auto-generated method stub


        if (NetWorkUtil.isNetworkConnected(this)) {

            // TODO Auto-generated method stub
            final LoadingDialog loadingDialog = new LoadingDialog(ManageReceiverAddressActivity.this,
                    getString(R.string.pull_to_refresh_footer_refreshing_label));
            loadingDialog.show();
            VolleyRequest.GetCookieRequest(ManageReceiverAddressActivity.this, AppURL.GET_USER_ADDRESS_LIST + "&key=" + token,
                    new HttpStringRequsetCallBack() {

                        @Override
                        public void onSuccess(String result) {
                            // TODO Auto-generated method stub
                            String str = AppURL.GET_USER_ADDRESS_LIST + "&key=" + token;
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
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                adapter.notifyDataSetChanged();
                                if (loadingDialog != null)
                                    loadingDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFail(String error) {
                            // TODO Auto-generated method stub
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                        }
                    });

        } else {
            CustomToast.makeToast(this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        }

    }

    private void initView() {
        // TODO Auto-generated method stub
        addressList = (ListView) findViewById(R.id.address_list);
        titleName = (TextView) findViewById(R.id.title_name);
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleBack.setVisibility(View.VISIBLE);
        titleName.setText(getString(R.string.receiver_address));
        titleBack.setOnClickListener(this);

        btnCreateAddress = (Button) findViewById(R.id.btn_create_address);
        btnCreateAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_address:
                Intent intent = new Intent(this,
                        CreateReceiverAddressActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.title_back:
                finish();
                break;
        }
    }

    //设置默认
    @Override
    public void setDefaultAddress(final int position) {
        int addressId = list.get(position).getAddressId();
        String setdefaultUrl = AppURL.SET_DEFAULT_RECEIVER_ADDRESS + "&key=" + token + "&address_id=" + addressId +
                "&is_default=1";
        L.e("setdefaultUrl:" + setdefaultUrl);
        VolleyRequest.GetCookieRequest(this, setdefaultUrl, new HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (new JSONObject(result).optString("error").equals("0")) {
                        //清空list里面的默认选择,因为只有一个默认，找到重置即可。
                        for (ReceiverAddress receiverAddress : list) {
                            if (receiverAddress.getIsDefalutAddress()) {
                                receiverAddress.setIsDefalutAddress(false);
                                break;
                            }
                        }
                        ReceiverAddress receiverAddress = list.remove(position);
                        receiverAddress.setIsDefalutAddress(true);
                        list.add(0, receiverAddress);
                        adapter.notifyDataSetChanged();
                        CustomToast
                                .makeToast(
                                        ManageReceiverAddressActivity.this,
                                        getString(R.string.set_success),
                                        Toast.LENGTH_SHORT);
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

    //删除地址
    @Override
    public void deleteAddress(final int position) {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.scan_history_dialog, null, false);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText("你确定删除该地址码？");
        view.findViewById(R.id.scan_history_confirm_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
                mAlertDialog.dismiss();
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
        /*final TipsDialog tipsDialog=new TipsDialog(this);
		tipsDialog.setTipText(getString(R.string.delete_confirmation));
		tipsDialog.show();
		tipsDialog.setOnTipsDialogListener(new OnTipsDialogListener() {

			@Override
			public void confirm() {
				// TODO Auto-generated method stub
				// getUserManageMoudle(position);
				delete(position);
				tipsDialog.dismiss();
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				tipsDialog.dismiss();
			}
		});*/
    }

    private LoadingDialog loadingDialog;
    //删除地址

    private void delete(final int position) {
        int addressId = list.get(position).getAddressId();
        String deleteUrl = AppURL.DELETE_RECEIVER_ADDRESS + "&key=" + token + "&address_id=" + addressId;
        L.e("deleteUrl:" + deleteUrl);
        VolleyRequest.GetCookieRequest(this, deleteUrl, new HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (new JSONObject(result).optString("error").equals("0")) {
                        setResult(RESULT_OK);
                        CustomToast.makeToast(ManageReceiverAddressActivity.this, getString(R.string.delete_success), Toast.LENGTH_SHORT);
                        list.remove(position);
                        adapter.notifyDataSetChanged();
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

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (loadingDialog != null)
            loadingDialog.dismiss();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0 && resultCode == RESULT_OK) {
            //重新加载列表
            /*list.clear();
            adapter.notifyDataSetChanged();
            getaddressList(token);*/
            finish();
        }
        if (requestCode == 1 && resultCode == 2) {//登录成功
            token = data.getExtras().getString("token");
            getaddressList(token);
        }
    }

}

package cn.mstar.store.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.KeyString;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/23 14:17
 * @version   提交退货物流页面
 *
 */
public class ReturnShopExpressActivity extends Activity implements View.OnClickListener{

    @Bind(R.id.title_back)
    ImageView titleBack;
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.id_rl_slect_express)
    RelativeLayout idRlSlectExpress;
    @Bind(R.id.id_tv_three)
    TextView tv_express;
    @Bind(R.id.id_bt_confim)
    Button bt_confim;
    @Bind(R.id.id_et_code)
    EditText ed_Code;
    private final String TAG="ReturnShopExpressActivity";
    private String refundSn,expressName,expressCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_return_express);
        ButterKnife.bind(this);

        refundSn = getIntent().getStringExtra(KeyString.ORDERNUMBER);
        idRlSlectExpress.setOnClickListener(this);
        titleBack.setOnClickListener(this);
        bt_confim.setOnClickListener(this);
        titleName.setText("选择快递");
        titleBack.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.id_rl_slect_express:
                Intent intent = new Intent(this, ExpressSelectActivity.class);
                startActivityForResult(intent, 11);
                break;
            case R.id.id_bt_confim:
                expressCode=ed_Code.getText().toString();
                if (expressName.equals("")||expressName==null||expressCode.equals("")||expressCode==null){
                    Toast.makeText(ReturnShopExpressActivity.this,"填写完整数据后提交",Toast.LENGTH_LONG).show();
                    return;
                }
                loadNetData();
                break;

        }
    }

    private void loadNetData(){
        if (getUrl()==null){
            Toast.makeText(this, "订单编号有误", Toast.LENGTH_LONG).show();
            return;
        }
        LogUtils.e(TAG + "getUrl" + getUrl());
        VolleyRequest.GetCookieRequest(this, getUrl(), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new Gson();
                String error = gson.fromJson(result, JsonObject.class).get("error").getAsString();
                if (error.equals("0")){
                    JsonObject data = gson.fromJson(result, JsonObject.class).get("data").getAsJsonObject();
                    data.getAsJsonObject().get("error").getAsInt();
                    String message=data.getAsJsonObject().get("message").getAsString();
                    Toast.makeText(ReturnShopExpressActivity.this,message,Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11 && resultCode == RESULT_OK) {
            expressName=data.getStringExtra("eName");
            tv_express.setText(expressName);
        }
    }


    private String getUrl(){
        if (refundSn==null||refundSn.equals("")){
            return null;
        }
        return AppURL.RETURN_SHOP_EXPRESS+"&refundSn="+refundSn+"&tokenKey="+ MyApplication.getInstance().tokenKey
                +"&eName="+expressName+"&shippingCode="+expressCode;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.requestQueue.cancelAll(this);
    }
}

package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyAction;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * 忘记密码
 * Created by Administrator on 2015/8/24.
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText phoneNumberET,authCodeET;//手机号 验证码
    private TextView getAuthCodeBtn;//获取验证码按钮
    private TextView titleName;//标题
    private ImageView titleBack;//返回
    private TimeCount time;
    private Button btnNex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        init();
    }

    private void init() {
        phoneNumberET= (EditText) findViewById(R.id.phone_number_et);
        authCodeET= (EditText) findViewById(R.id.auth_code_et);
        getAuthCodeBtn= (TextView) findViewById(R.id.btn_get_auth_code);
        titleBack= (ImageView) findViewById(R.id.title_back);
        btnNex= (Button) findViewById(R.id.btn_forget_password_next);
        titleBack.setVisibility(View.VISIBLE);
        getAuthCodeBtn.setOnClickListener(this);
        titleBack.setOnClickListener(this);
        btnNex.setOnClickListener(this);
        titleName= (TextView) findViewById(R.id.title_name);
        titleName.setText(getString(R.string.forget_password));
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            getAuthCodeBtn.setText("重新验证");
            getAuthCodeBtn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            getAuthCodeBtn.setClickable(false);
            getAuthCodeBtn.setText("等"+millisUntilFinished / 1000 + "秒再试");
        }
    }

    //获取验证码
    private void getAuthCode(String phoneNumber){
        String getautoCodeUrl= AppURL.GET_AUTH_CODE_URL + "&phone=" + phoneNumber;
        L.e("getautoCodeUrl:" + getautoCodeUrl);
        VolleyRequest.GetCookieRequest(this, getautoCodeUrl, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if(new JSONObject(result).optString("msg").equals("OK"))
                        CustomToast.makeToast(ForgetPasswordActivity.this,"验证码已发送", Toast.LENGTH_SHORT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {

            }
        });
    }
    //比对验证码
    private void equlesAuthCode(final String uName,String authCode){
        String equelsUrl= AppURL.EQUELSE_AUTH_CODE_URL+"&uName="+uName+"&ecode="+authCode;
        L.e("equelsUrl:"+equelsUrl);
        VolleyRequest.GetCookieRequest(this, equelsUrl, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if(new JSONObject(result).optString("error").equals("0")){
                        Intent intent=new Intent(ForgetPasswordActivity.this,ChangePasswordActivity.class);
                        intent.setAction(MyAction.forgetPasswordActivityAction);
                        intent.putExtra("uName",uName);
                        startActivity(intent);
                    }else{
                        CustomToast.makeToast(ForgetPasswordActivity.this,"验证码错误",Toast.LENGTH_SHORT);
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
    public void onClick(View v) {
        String phoneNumber=phoneNumberET.getText().toString();
        String authCode=authCodeET.getText().toString();
        switch (v.getId()){
            case R.id.title_back://返回
                finish();
                break;
            case R.id.btn_get_auth_code://获取验证码
                if(Utils.isMobileNO(phoneNumber)){
                    getAuthCode(phoneNumber);
                    time.start();
                }
                break;
            case R.id.btn_forget_password_next://下一步
                if( TextUtils.isEmpty(authCode)) {
                    CustomToast.makeToast(ForgetPasswordActivity.this, "请输入验证码", Toast.LENGTH_SHORT);
                    return;
                }
                equlesAuthCode(phoneNumber,authCode);
                break;
        }
    }
}

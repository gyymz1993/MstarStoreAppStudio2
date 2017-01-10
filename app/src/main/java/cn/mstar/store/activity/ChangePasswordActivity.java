package cn.mstar.store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * 修改密码
 * Created by Administrator on 2015/8/22.
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener{

    private EditText newPasswordET,confirmPasswordET;
    private ImageView titleBack;
    private TextView titleName;
    private Button confrimBtn;
    private String token;
    private String uNanme="";
    private ImageView isShowPasswrodIV;//显示影藏密码按钮
    private Boolean isShow=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        MyApplication.getInstance().addActivity(this);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        init();
    }

    private void init() {
        token=MyApplication.getInstance().tokenKey;
        newPasswordET= (EditText) findViewById(R.id.new_password_et);
        confirmPasswordET= (EditText) findViewById(R.id.confirm_password_et);
        titleBack= (ImageView) findViewById(R.id.title_back);
        titleName= (TextView) findViewById(R.id.title_name);
        confrimBtn= (Button) findViewById(R.id.btn_forget_password_confirm);
        titleName.setText(getString(R.string.change_password));

        isShowPasswrodIV= (ImageView) findViewById(R.id.iv_makepasswordvisible);
        if(MyAction.forgetPasswordActivityAction.equals(getIntent().getAction())){//从忘记密码页传递过来的
            titleName.setText(getString(R.string.reset_password));
            confrimBtn.setText(getString(R.string.reset_password));
            uNanme=getIntent().getStringExtra("uName");
        }else{
            if(token.equals("")){
                Intent intent=new Intent(this, LoginActivity.class);
                startActivityForResult(intent,1);
            }
        }
        titleBack.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
        confrimBtn.setOnClickListener(this);
        isShowPasswrodIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back://返回
                finish();
                break;
            case R.id.btn_forget_password_confirm://确认修改
                String newpassword=newPasswordET.getText().toString();
                String againpassword=confirmPasswordET.getText().toString();
                if(newpassword.length()<6){
                    CustomToast.makeToast(this,"密码长度请大于6位",Toast.LENGTH_SHORT);
                    break;
                }
                if(!newpassword.equals(againpassword)){
                    CustomToast.makeToast(this, "请输入相同密码", Toast.LENGTH_SHORT);
                    break;
                }

                if(MyAction.forgetPasswordActivityAction.equals(getIntent().getAction())){
                    resetPassWord(newpassword, againpassword);
                }else{
                     changePassWord(newpassword,againpassword);
                }
                break;
            case R.id.iv_makepasswordvisible://密码 明文 密文 显示
                isShow=!isShow;
                if(isShow){

                    newPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    newPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
        }
    }
    //重置密码
    private void resetPassWord(String newpassword, String againpassword){
        String resetpasswordUrl= AppURL.RESET_PASSWORD_URL+"&password="+newpassword+"&repassword="+againpassword+"&uName="+uNanme;
        L.e("resetpasswordUrl:" + resetpasswordUrl);
        VolleyRequest.GetCookieRequest(this, resetpasswordUrl, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (new JSONObject(result).optString("error").equals("0")){
                        CustomToast.makeToast(ChangePasswordActivity.this, "设置成功", Toast.LENGTH_SHORT);
                        logOut();
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


    //修改密码
    private void changePassWord(String newpassword, String againpassword) {
        String changePasswordUrl= AppURL.CHANGE_PASSWORD_URL+"&password="+newpassword+"&repassword="+againpassword+
                "&key="+token;
        L.e("changePasswordUrl:" + changePasswordUrl);
        VolleyRequest.GetCookieRequest(this, changePasswordUrl, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    if(new JSONObject(result).optString("error").equals("0")){
                        CustomToast.makeToast(ChangePasswordActivity.this, getString(R.string.set_success), Toast.LENGTH_SHORT);
                        logOut();
                    }else{
                        CustomToast.makeToast(ChangePasswordActivity.this,getString(R.string.set_fail), Toast.LENGTH_SHORT);
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


    // logOut
    public void logOut () {
        // clean the rep
        MyApplication.getInstance().tokenKey="";
        Utils.cleanSharedPref(ChangePasswordActivity.this);
        MyApplication.getInstance().spUtils.cleanSharedPref();
        if (MainActivity.mainActivity!=null){
            MainActivity.mainActivity.setMycartCount(0);//cartCount
        }
        MainActivity.curu_Tab=MainActivity.TAB_HOME;
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==2){//登录成功
            if(data!=null)
                token=data.getExtras().getString("token");
        }
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }
}

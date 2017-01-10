package cn.mstar.store.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.customviews.LoadingDialog;
import cn.mstar.store.functionutils.LogUtils;
import cn.mstar.store.functionutils.SpUtils;
import cn.mstar.store.functionutils.URLtoUTF8Utils;
import cn.mstar.store.utils.CustomToast;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

public class LoginActivity extends BaseActivity {

    // constants fields or variables.
    public static final String JUMP_TO_ACTIVITY = "987";
    public int keyboardSize = 1000;
    private final String TAG="LoginActivity";
    // viewz
    @Bind(R.id.ed_login_username)
    EditText ed_username;
    @Bind(R.id.ed_login_password)
    EditText ed_password;
    @Bind(R.id.iv_username_ed_cross)
    ImageView iv_username_cross;
    @Bind(R.id.title_name)
    TextView tv_actionbar_middle;
    @Bind(R.id.tv_filter)
    TextView tv_actionbar_right;
    @Bind(R.id.title_back)
    ImageView iv_actionbar_left;
    @Bind(R.id.tv_forget_pwd)
    TextView forgetPwdTV;
    private LoadingDialog dialog;
    private String username;
    private String passwd;
    private String password = "";
    private Class<?> nextActivity;
    private String key = "";
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        gson = new Gson();
        tv_actionbar_right.setVisibility(View.VISIBLE);
        iv_actionbar_left = (ImageView) findViewById(R.id.title_back);
        Utils.setNavigationBarColor(this, getResources().getColor(R.color.status_bar_color));
        Utils.setStatusBarColor(this, getResources().getColor(R.color.status_bar_color));
        Intent callingIntent = getIntent();
        if (callingIntent != null && callingIntent.getExtras() != null) {
            nextActivity = (Class<?>) callingIntent.getExtras().get(JUMP_TO_ACTIVITY);
            key = callingIntent.getExtras().getString(MockActivity.GET_TO);
        }
        String str = getIntent().getStringExtra(Constants.LOGIN_SUCCESS_USERNAME);
        ButterKnife.bind(this);
        final View activityRootview = findViewById(R.id.activity_rootview);
        activityRootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootview.getRootView().getHeight() - activityRootview.getHeight();
                if (heightDiff <= keyboardSize) {
                    keyboardSize = heightDiff;
                    // hidding keyboard
                    iv_username_cross.setVisibility(View.GONE);
                } else {
                    // showing keyboard.
                    iv_username_cross.setVisibility(View.VISIBLE);
                }
            }
        });

        tv_actionbar_right.setText(R.string.register);
        tv_actionbar_middle.setText(R.string.login);

        tv_actionbar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
        ed_username.setText(str);
        iv_actionbar_left.setVisibility(View.VISIBLE);
        iv_actionbar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        forgetPwdTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        updateLoggedState();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void updateLoggedState() {
        SharedPreferences channel = getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        username = channel.getString(Constants.SP_USERNAME, "");
        password = channel.getString(Constants.SP_PASSWORD, "");
        ed_password.setText(password);
        ed_username.setText(username);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * @param view 登陆
     */
    public void login(View view) {
        username = ed_username.getText().toString().toLowerCase();
        passwd = ed_password.getText().toString();
        SpUtils.LOGIN_USERNAME = username;
        SpUtils.LOGIN_PASSWORD = passwd;
        // send an authentification thread to the server and check the informations.
        if (passwd.trim().length() >= 6/* && username.trim().length() >= 6*/) {
            // show the dialog.
            dialog = new LoadingDialog(this, getString(R.string.pull_to_refresh_footer_refreshing_label));
            dialog.show();
            // 判断是否已经登录
            startLogin(username.trim(), passwd.trim());
        } else
            CustomToast.makeToast(this, getString(R.string.password_constraint), Toast.LENGTH_SHORT);
    }


    protected void startLogin(final String username, final String password) {
        String  logUrl= URLtoUTF8Utils.toUtf8String(AppURL.LOGIN_ACT + "&username=" + username + "&password=" + password + "&client=android");
        LogUtils.e(TAG+"手动登陆" +logUrl);
        // 进行登录请求
        VolleyRequest.GetCookieRequest(LoginActivity.this,logUrl, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e(TAG+"登陆返回" +result);
                try {
                    JsonObject elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data").getAsJsonObject();
                    String un = elm.get("userName").getAsString();
                    String pw = elm.get("password").getAsString();
                    String tokenKey = elm.get("tokenkey").getAsString();
                    String pic = elm.get("pic").getAsString();
                    int points = elm.get("points").getAsInt();
                    String storeId = elm.get("shopId").getAsString();
                    if (password.trim().equals(pw)) {
                        //登录成功
                        loginSuccess(username, un, pw, tokenKey, pic, points, storeId);
                    } else {
                        String error = elm.get("error").getAsString();
                        loginError(error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    loginError(null);
                }
            }

            @Override
            public void onFail(String error) {
                // 网络异常
                networkException();
            }
        });

    }


    //	loginSuccess(username, password, tokenKey, pic, points);
    private void loginSuccess(String loginName, String username, String password, String tokenKey, String pic, int points, String storeId) {
        // get the action of the login... if it is for exchange, then send back the 94 result.
        Utils.LoginSuccess((MyApplication) LoginActivity.this.getApplication(), loginName, username, password, tokenKey, pic, points, storeId);
        if (nextActivity != null) {
            final Intent intent = new Intent(LoginActivity.this, nextActivity);
            if (key != null && !key.equals(""))
                intent.putExtra(MockActivity.GET_TO, key);
            (new android.os.Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 1000);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("fragmentid", getIntent().getIntExtra("fragmentid", -1));
        setResult(94, intent);
        finish();
    }


    private void loginError(String error) {
        if (error != null)
            CustomToast.makeToast(LoginActivity.this, error, Toast.LENGTH_SHORT);
        else
            CustomToast.makeToast(LoginActivity.this, getString(R.string.login_wrong), Toast.LENGTH_SHORT);
        dialogDismiss();
    }


    private void networkException() {
        CustomToast.makeToast(LoginActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT);
        dialogDismiss();
    }

    private void dialogDismiss() {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
            dialog = null;
        }
    }


    @OnClick(R.id.tv_noaccount_register)
    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }


    public void clean_username_field(View view) {
        ed_password.setText("");
    }

    @Override
    public void finish() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        MyApplication.requestQueue.cancelAll(this);
        super.onDestroy();
    }
}
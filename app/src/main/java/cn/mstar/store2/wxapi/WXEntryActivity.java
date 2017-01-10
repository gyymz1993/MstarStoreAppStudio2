package cn.mstar.store2.wxapi;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import cn.mstar.store.activity.BaseActivity;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.functionutils.SpUtils;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Shinelon on 2016/1/20.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getIWXAPI().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK://用户同意
                if (baseResp instanceof  SendAuth.Resp){
                    String code = ((SendAuth.Resp) baseResp).code;
                    L.i("ymz-->" + code);
                    VolleyRequest.GetCookieRequest(this, getTokenRequest(code), new VolleyRequest.HttpStringRequsetCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            L.i("result:" + result);
                            Gson gson = new Gson();
                            String access_token = gson.fromJson(result, JsonObject.class).get("access_token").getAsString();
                            String openid = gson.fromJson(result, JsonObject.class).get("openid").getAsString();
                            MyApplication.getInstance().wxId = openid;
                            MyApplication.spUtils.saveString(SpUtils.key_wxId, openid);
                            if (MyApplication.getInstance().mWXAuthSuccessInterface != null) {
                                MyApplication.getInstance().mWXAuthSuccessInterface.agree();
                                finish();
                            }
                            //getUserInfo(access_token, openid);
                        }
                        @Override
                        public void onFail(String error) {
                            Toast.makeText(WXEntryActivity.this, "授权失败，请手动登录", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                L.i("ymz-->用户拒绝授权");
                if (MyApplication.getInstance().mWXAuthSuccessInterface != null) {
                    MyApplication.getInstance().mWXAuthSuccessInterface.refuse();
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                L.i("ymz-->用户取消");
                if (MyApplication.getInstance().mWXAuthSuccessInterface != null) {
                    MyApplication.getInstance().mWXAuthSuccessInterface.refuse();
                    finish();
                }
                break;
        }

    }

    private void getUserInfo(String access_token, String openid) {
        VolleyRequest.GetCookieRequest(this, getUserInfoUrl(access_token, openid), new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.i("result-->" + result);
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    private static String getTokenRequest(String code) {
        String tokenRequest = AppURL.GET_REQUEST_ACCESS_TOKEN.replace("APPID", Constants.APP_ID).
                replace("SECRET", Constants.API_KEY).
                replace("CODE", code);
        return tokenRequest;
    }

    public static String getUserInfoUrl(String access_token, String openid) {
        String userInfo = AppURL.GET_REQUEST_USER_INFO.replace("ACCESS_TOKEN", access_token).
                replace("OPENID", openid);
        return userInfo;
    }
}


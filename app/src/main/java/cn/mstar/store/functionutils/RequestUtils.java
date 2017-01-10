package cn.mstar.store.functionutils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.mstar.store.R;
import cn.mstar.store.app.AppURL;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.db.entityToSave.ProAndSpecialIdz;
import cn.mstar.store.entity.RegisterOb;
import cn.mstar.store.entity.ShoppingCartItem;
import cn.mstar.store.interfaces.OnResultStatusListener;
import cn.mstar.store.utils.CookieStringtRequest;
import cn.mstar.store.utils.L;
import cn.mstar.store.utils.Utils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2016/3/16.
 */
public class RequestUtils {


    public static void cleanCookie () {
        CookieStringtRequest.cookie = "";
        CookieStringtRequest.isTruncate = true;
    }

    public static void Logout(final VolleyRequest.HttpStringRequsetCallBack req) {
        // 从服务获取tokenkey&用户名名
        final Context mctx = MyApplication.getInstance().getApplicationContext();
        final SharedPreferences channel = mctx.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String username = channel.getString(Constants.SP_USERNAME, "");
        String tokenKey = channel.getString(Constants.SP_TOKENKEY, "");
        cleanCookie();
        Utils.LoginClean(true);
        // 退出登录
        VolleyRequest.GetCookieRequest(mctx, AppURL.LOGOUT_ACT + "&username=" + username + "&key=" + tokenKey + "&client=android", new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                // 退出登录成功
                try {
                    RegisterOb ob = (new Gson()).fromJson(result, RegisterOb.class);
                    if (ob != null && ob.data != null && !"".equals(ob.data.error)) {
                        // 退出登录成功
                        if (req != null)
                            req.onSuccess("");
                        Utils.LoginClean(true);
                    } else {
                        if (req != null)
                            req.onFail("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (req != null)
                        req.onFail("");
                }
            }

            @Override
            public void onFail(String error) {
                if (req != null)
                    req.onFail(mctx.getString(R.string.network_error));
            }
        });
    }

    //	 判断用户有没有在线
    public static void checkLogStatus(final VolleyRequest.LogonStatusLinstener logonStatusLinstener) {
        if ("".equals(MyApplication.getInstance().tokenKey)) {
            if (logonStatusLinstener != null)
                logonStatusLinstener.NO();
        } else {
            if (logonStatusLinstener != null)
                logonStatusLinstener.OK(MyApplication.getInstance().tokenKey);//已经登录就返回tokenkey
        }
    }

    public static void addInShoppingCart(ProAndSpecialIdz[] item, final String tokenKey, final OnResultStatusListener listener) {
        String proId = "", numberz = "";
        for (ProAndSpecialIdz iz: item) {
            if (!"".equals(proId))
                proId+="|";
            if (iz.specialId != 0)
                proId+=iz.specialId;
            else
                proId+=iz.proId;
            // number
            if (!"".equals(numberz))
                numberz+="|";
            numberz+=iz.number;
        }

        final String link = AppURL.ADD_TO_SHOPPING_CART + "&key=" + tokenKey + "&proId=" + proId + "&number=" + numberz;
        VolleyRequest.GetCookieRequest(MyApplication.getInstance(), link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                L.d("XXX", result);
                JsonObject elm = gson.fromJson(result, JsonElement.class).getAsJsonObject();
                try {
                    JsonObject elm1 = elm.get("data").getAsJsonObject();
                    String tK =  elm1.get("tokenKey").getAsString();
                    L.d("XXX", tK+"  ***  "+tokenKey);
                    if (tokenKey.equals(tK)) {
                        // 添加成功
                        if (listener != null)
                            listener.success(tokenKey);
                        MyApplication.getInstance().frg_isFrg_shoppingcart_needUpdate = true;
                    } else {
                        // 添加失败
                        if (listener != null)
                            listener.failure("0");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 添加失败
                    try {
                        if (listener != null)
                            listener.failure(elm.get("message").getAsString());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(String error) {
                // 网络请求问题
                if (listener != null)
                    listener.failure("-1");
            }
        });

    }

    public static void deleteShoppingCartItem(ShoppingCartItem item, final OnResultStatusListener listener) {
        final String tokenKey = MyApplication.getInstance().tokenKey;
        final String link = AppURL.DEL_FROM_SHOPPING_CART + "&key=" + tokenKey + "&cartId=" + item.cartId;
        VolleyRequest.GetCookieRequest(MyApplication.getInstance(), link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                L.d("XXX", result);
                try {
                    JsonObject elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data").getAsJsonObject();
                    String tK =  elm.get("tokenKey").getAsString();
//					L.d("XXX", tK+"  ***  "+tokenKey);
                    if (tokenKey.equals(tK)) {
                        // 添加成功
                        if (listener != null)
                            listener.success(tokenKey);
                        MyApplication.getInstance().frg_isFrg_shoppingcart_needUpdate = true;
                    } else {
                        // 添加失败
                        if (listener != null)
                            listener.failure("0");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 添加失败
                    if (listener != null)
                        listener.failure("-1");
                }
            }

            @Override
            public void onFail(String error) {
                // 网络请求问题
            }
        });
    }

    /**
     *  删除收藏
     * @param mContext
     * @param proId
     * @param tokenKey
     * @param onResultStatusListener
     */
    public static void deleteItemsFromFavorite(final Context mContext, final String proId,  final String tokenKey,
                                               final OnResultStatusListener onResultStatusListener) {

        VolleyRequest.GetCookieRequest(mContext, AppURL.DEL_FAVORITE_ACT + "&key=" + tokenKey + "&goods_id=" + proId, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                onResultStatusListener.success(result);
            }

            @Override
            public void onFail(String error) {
                onResultStatusListener.failure(error);
            }
        });
    }


    /**
     *  添加收藏
     * @param mContext
     * @param proId
     * @param tokenKey
     * @param onResultStatusListener
     */
    public static void addItemsFromFavorite(final Context mContext, final String proId, final String tokenKey,
                                            final OnResultStatusListener onResultStatusListener) {

        String link = AppURL.ADD_FAVORITE_ACT + "&key=" + tokenKey + "&goods_id=" + proId;
        L.d("fav::", link);
        VolleyRequest.GetCookieRequest(mContext, link, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                onResultStatusListener.success(result);
            }

            @Override
            public void onFail(String error) {
                onResultStatusListener.failure(error);
            }
        });
    }


    public static void startLogin(Activity activity, String username, String password) {
        final String usernames = username;
        final String passwords = password;
        final Activity activitys = activity;
        LogUtils.e("自动登陆" + AppURL.LOGIN_ACT + "&username=" + username + "&password=" + password + "&client=android");
        // 进行登录请求
        VolleyRequest.GetCookieRequest(activity, AppURL.LOGIN_ACT + "&username=" + username + "&password=" + password + "&client=android", new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                L.d("XXX", result);
                try {
                    Gson gson = new Gson();
                    JsonObject elm = gson.fromJson(result, JsonElement.class).getAsJsonObject().get("data").getAsJsonObject();
                    final String un = elm.get("userName").getAsString();
                    final String pw = elm.get("password").getAsString();
                    final String tokenKey = elm.get("tokenkey").getAsString();
                    final String pic = elm.get("pic").getAsString();
                    final int points = elm.get("points").getAsInt();
                    String storeId = elm.get("shopId").getAsString();
                    L.d("XXX", "username --- " + un + " and " + un + "--- comp " + un.trim().equals(un));
                    L.d("XXX", "password --- " + pw + " and " + pw + "--- comp " + pw.trim().equals(pw));
                    //String error = elm.get("error").getAsString();
                    if (passwords.trim().equals(pw)) {
                        //登录成功
                        loginSuccess(activitys, usernames, un, pw, tokenKey, pic, points, storeId);
                    } else {
                        LogUtils.e("登陆错误" + elm.get(""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFail(String error) {
                // 网络异常

            }
        });

    }


    public static void loginSuccess(Activity activity, String loginName, String username, String password, String tokenKey, String pic, int points, String storeId) {
        Utils.LoginSuccess((MyApplication) activity.getApplication(), loginName, username, password, tokenKey, pic, points, storeId);
    }


}

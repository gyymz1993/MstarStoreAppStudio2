package cn.mstar.store.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.mstar.store.activity.BaseActivity;
import cn.mstar.store.activity.GoodsManagementActivity;
import cn.mstar.store.app.Constants;
import cn.mstar.store.app.MyApplication;
import cn.mstar.store.db.entityToSave.ProAndSpecialIdz;
import cn.mstar.store.entity.BuyProductEntity;
import cn.mstar.store.entity.OrderDetailsEntity;
import cn.mstar.store.functionutils.RequestUtils;
import cn.mstar.store.functionutils.SpUtils;
import cn.mstar.store.interfaces.OnResultStatusListener;

/**
 * Created by Ultima on 7/10/2015.
 */
public class Utils {

    public static int convertPxtoDip(int pixel, Context context) {

        float scale = context.getResources().getDisplayMetrics().density;
        int dips = (int) ((pixel * scale) + 0.5f);
        return dips;
    }

    public static int convertDiptoPx(int dips, Context context) {

        float scale = context.getResources().getDisplayMetrics().density;
        int pixel = (int) ((dips - 0.5f) / scale);
        return pixel;
    }

    //	Utils.LoginSuccess((MyApplication) LoginActivity.this.getApplication(), username, password, tokenKey, pic, points);
    public static void LoginSuccess(MyApplication myApplication, String loginName, String username, String password, String tokenKey, String pic, int points, String storeId) {
        MyApplication.spUtils.saveString(SpUtils.key_username, SpUtils.LOGIN_USERNAME);
        MyApplication.spUtils.saveString(SpUtils.key_password, SpUtils.LOGIN_PASSWORD);
        MyApplication.spUtils.saveString(SpUtils.key_tokenKey, tokenKey);
        SharedPreferences channel = myApplication.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = channel.edit();
        editor.putString(Constants.SP_USERNAME, loginName);
        editor.putString(Constants.SP_PASSWORD, password);
        editor.putString(Constants.SP_TOKENKEY, tokenKey);
        editor.commit();
        myApplication.frg_isFrg_shoppingcart_needUpdate = true;
        myApplication.isFrg_me_needUpdate = true;
        myApplication.username = username;
        myApplication.password = password;
        myApplication.tokenKey = tokenKey;
        myApplication.storeId = storeId;
        myApplication.pic = pic;
        myApplication.points = "" + points;
    }

    public static void LoginClean(boolean bool) {
        Context mctx = MyApplication.getInstance().getApplicationContext();
        SharedPreferences channel = mctx.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = channel.edit();
        if (bool) {
//			editor.putString(Constants.SP_USERNAME, "");
            editor.putString(Constants.SP_PASSWORD, "");
        }
        editor.putString(Constants.SP_TOKENKEY, "");
        editor.commit();
        MyApplication.getInstance().cleanLoginInfo();
    }

    public static String getTokenKey(MyApplication myApplication) {
        return myApplication.tokenKey;
    }

    public static void updateShoppingCart(final OnResultStatusListener listener) {
        List<ProAndSpecialIdz> items = ProAndSpecialIdz.getAll();
        if (items != null)
            RequestUtils.addInShoppingCart( toArray(items), MyApplication.getInstance().tokenKey, new OnResultStatusListener() {
                @Override
                public void success(String result) {
                    // tell the  caller that we are done.
                    ProAndSpecialIdz.cleanLocal();
                    if (listener != null)
                        listener.success("");
                }
                @Override
                public void failure(String error) {
                    if (listener != null)
                        listener.failure("");
                }
            });
    }

    public static String encodeChinese(String str) {
        String result = "";
        for (char ch : str.toCharArray())
            result += "\\u" + Integer.toHexString(ch | 0x10000).substring(1);
        System.out.print(result);
        return result;
    }

    public static ProAndSpecialIdz[] toArray(List<ProAndSpecialIdz> itemz) {
        ProAndSpecialIdz[] v = new ProAndSpecialIdz[itemz.size()];
        for (int i = 0; i < itemz.size(); i++) {
            v[i] = itemz.get(i);
        }
        return v;
    }
    public static String formatedPrice(Double total) {
        DecimalFormat df = new DecimalFormat("0.00");
        String priceFormatted = df.format(total);
        return priceFormatted;
    }
    public static List<BuyProductEntity> orderItemzToProductEntity(OrderDetailsEntity.OrderItems[] orderItems) {
        List<BuyProductEntity> data = new ArrayList<>();
        for (OrderDetailsEntity.OrderItems instance : orderItems) {
            BuyProductEntity tmp = new BuyProductEntity(instance);
            data.add(tmp);
        }
        return data;
    }

    public static class ReceiverManager {
        private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
        private static ReceiverManager ref;
        private Context context;
        private ReceiverManager(Context context) {
            this.context = context;
        }
        public static synchronized ReceiverManager init(Context context) {
            if (ref == null) ref = new ReceiverManager(context);
            return ref;
        }

        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter) {
            receivers.add(receiver);
            Intent intent = context.registerReceiver(receiver, intentFilter);
            Log.i(getClass().getSimpleName(), "registered receiver: " + receiver + "  with filter: " + intentFilter);
            Log.i(getClass().getSimpleName(), "receiver Intent: " + intent);
            return intent;
        }

        public boolean isReceiverRegistered(BroadcastReceiver receiver) {
            boolean registered = receivers.contains(receiver);
            Log.i(getClass().getSimpleName(), "is receiver " + receiver + " registered? " + registered);
            return registered;
        }

        public void unregisterReceiver(BroadcastReceiver receiver) {
            if (isReceiverRegistered(receiver)) {
                receivers.remove(receiver);
                context.unregisterReceiver(receiver);
                Log.i(getClass().getSimpleName(), "unregistered receiver: " + receiver);
            }
        }
    }

    public static void cleanSharedPref(Context mContext) {

        // TODO Auto-generated method stub
        SharedPreferences channel = mContext.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        //		loggedState = channel.getBoolean("USER_LOGGED", false);
        //		username = channel.getString(Constants.SP_USERNAME, "");
        //		password = channel.getString(Constants.SP_PASSWORD, "");
        Editor editor = channel.edit();
        editor.putString(Constants.SP_PASSWORD, "");
        editor.putString(Constants.SP_USERNAME, "");
        editor.putBoolean("USER_LOGGED", false);
        editor.commit();
    }


    public static int getScreenHeigth(Context context) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((GoodsManagementActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((GoodsManagementActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    private static long lastClickTime;

    //用于防止按钮频繁点击
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static void setNavigationBarColor(Activity activity, int colorId) {

		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setNavigationBarColor(colorId);
		}

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemBarTintManager manager = new SystemBarTintManager(activity);
			manager.setNavigationBarTintEnabled(true);
			manager.setNavigationBarTintColor(colorId);
		}*/
    }


    public static void setStatusBarColor(Activity activity, int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager manager = new SystemBarTintManager(activity);
            manager.setStatusBarTintEnabled(true);
            manager.setStatusBarTintColor(colorId);
        }
    }


}

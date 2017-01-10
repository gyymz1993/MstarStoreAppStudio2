package cn.mstar.mvp.contrl;

import android.content.Context;

import cn.mstar.store.interfaces.HttpRequestCallBack;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2016/3/22.
 */
public interface ReturnContrl {
    void getReturnBns(Context context,String url,VolleyRequest.HttpStringRequsetCallBack onCallBack);
}

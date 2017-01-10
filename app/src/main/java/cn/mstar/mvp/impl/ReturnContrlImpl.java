package cn.mstar.mvp.impl;

import android.content.Context;

import cn.mstar.mvp.contrl.ReturnContrl;
import cn.mstar.store.utils.VolleyRequest;

/*
 * 创建人：Yangshao
 * 创建时间：2016/3/23 17:14
 * @version    
 *    
 */
public class ReturnContrlImpl implements ReturnContrl{

    @Override
    public void getReturnBns(Context context, String url, final VolleyRequest.HttpStringRequsetCallBack onCallBack) {
        VolleyRequest.GetCookieRequest(context, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                if (onCallBack!=null)
                onCallBack.onSuccess(result);
            }

            @Override
            public void onFail(String error) {
                if (onCallBack!=null)
                    onCallBack.onFail(error);
            }
        });
    }
}

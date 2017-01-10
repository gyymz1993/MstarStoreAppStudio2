package cn.mstar.mvp.presenter;

import android.content.Context;
import android.os.SystemClock;

import com.google.gson.Gson;

import java.util.List;

import cn.mstar.mvp.aview.ReturnShopView;
import cn.mstar.mvp.contrl.ReturnContrl;
import cn.mstar.mvp.impl.ReturnContrlImpl;
import cn.mstar.mvp.model.ReturnListBn;
import cn.mstar.store.utils.LogUtils;
import cn.mstar.store.utils.VolleyRequest;

/**
 * Created by Administrator on 2016/3/22.
 */
public class ReturnShopPresenter {

    private ReturnShopView returnShopView;
    private ReturnContrl returnContrl;

    public ReturnShopPresenter(ReturnShopView returnShopView) {
        this.returnShopView = returnShopView;
        this.returnContrl = new ReturnContrlImpl();
    }

    public void  getNetData(Context context,String  url){
        returnShopView.showLoading();
        returnContrl.getReturnBns(context, url, new VolleyRequest.HttpStringRequsetCallBack() {
            @Override
            public void onSuccess(String result) {
                if (result!=null){
                    SystemClock.sleep(800);
                }
                ReturnListBn returnListBn = new Gson().fromJson(result, ReturnListBn.class);
                if (returnListBn.getError().equals("0")){
                    List<ReturnListBn.DataEntity.ReturnInfoEntity> return_info = returnListBn.getData().getReturn_info();
                    if (return_info.size()!=0){
                        returnShopView.dismissAllView();
                        returnShopView.getRturnListBn(return_info);
                        //返回数量放在后面执行
                        returnShopView.getListCount(Integer.valueOf(returnListBn.getData().getList_count()));
                    }else {
                        returnShopView.showNoResult();
                    }
                }
            }

            @Override
            public void onFail(String error) {
                SystemClock.sleep(800);
                LogUtils.e("error");
                returnShopView.showNetError();
            }
        });
    }
}

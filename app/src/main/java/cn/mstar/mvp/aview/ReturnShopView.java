package cn.mstar.mvp.aview;

import java.util.List;

import cn.mstar.mvp.model.ReturnListBn;

/**
 * Created by Administrator on 2016/3/22.
 */
public interface ReturnShopView {
   void getRturnListBn(List<ReturnListBn.DataEntity.ReturnInfoEntity> returnListBns);

    void getListCount(int count);

    String setUrl();

    void showLoading();

    void showNoResult();

    void showNetError();

    void showContent();

    void dismissLoading();

    void dismissAllView();
}

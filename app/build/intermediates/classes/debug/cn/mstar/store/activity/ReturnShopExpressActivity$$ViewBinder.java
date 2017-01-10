// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReturnShopExpressActivity$$ViewBinder<T extends cn.mstar.store.activity.ReturnShopExpressActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'titleBack'");
    target.titleBack = finder.castView(view, 2131558731, "field 'titleBack'");
    view = finder.findRequiredView(source, 2131559402, "field 'titleName'");
    target.titleName = finder.castView(view, 2131559402, "field 'titleName'");
    view = finder.findRequiredView(source, 2131558514, "field 'idRlSlectExpress'");
    target.idRlSlectExpress = finder.castView(view, 2131558514, "field 'idRlSlectExpress'");
    view = finder.findRequiredView(source, 2131558517, "field 'tv_express'");
    target.tv_express = finder.castView(view, 2131558517, "field 'tv_express'");
    view = finder.findRequiredView(source, 2131558522, "field 'bt_confim'");
    target.bt_confim = finder.castView(view, 2131558522, "field 'bt_confim'");
    view = finder.findRequiredView(source, 2131558521, "field 'ed_Code'");
    target.ed_Code = finder.castView(view, 2131558521, "field 'ed_Code'");
  }

  @Override public void unbind(T target) {
    target.titleBack = null;
    target.titleName = null;
    target.idRlSlectExpress = null;
    target.tv_express = null;
    target.bt_confim = null;
    target.ed_Code = null;
  }
}

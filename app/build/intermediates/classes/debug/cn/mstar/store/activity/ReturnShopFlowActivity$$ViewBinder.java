// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReturnShopFlowActivity$$ViewBinder<T extends cn.mstar.store.activity.ReturnShopFlowActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'titleBack'");
    target.titleBack = finder.castView(view, 2131558731, "field 'titleBack'");
    view = finder.findRequiredView(source, 2131559402, "field 'titleName'");
    target.titleName = finder.castView(view, 2131559402, "field 'titleName'");
    view = finder.findRequiredView(source, 2131558525, "field 'idLvFlow'");
    target.idLvFlow = finder.castView(view, 2131558525, "field 'idLvFlow'");
  }

  @Override public void unbind(T target) {
    target.titleBack = null;
    target.titleName = null;
    target.idLvFlow = null;
  }
}

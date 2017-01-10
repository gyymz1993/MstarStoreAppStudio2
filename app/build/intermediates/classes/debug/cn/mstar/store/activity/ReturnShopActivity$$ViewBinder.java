// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReturnShopActivity$$ViewBinder<T extends cn.mstar.store.activity.ReturnShopActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'titleBack' and method 'onClick'");
    target.titleBack = finder.castView(view, 2131558731, "field 'titleBack'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick();
        }
      });
    view = finder.findRequiredView(source, 2131559402, "field 'titleName'");
    target.titleName = finder.castView(view, 2131559402, "field 'titleName'");
    view = finder.findRequiredView(source, 2131559169, "field 'ly_loading'");
    target.ly_loading = finder.castView(view, 2131559169, "field 'ly_loading'");
    view = finder.findRequiredView(source, 2131559257, "field 'ly_noData'");
    target.ly_noData = finder.castView(view, 2131559257, "field 'ly_noData'");
    view = finder.findRequiredView(source, 2131558524, "field 'idLvReturn'");
    target.idLvReturn = finder.castView(view, 2131558524, "field 'idLvReturn'");
    view = finder.findRequiredView(source, 2131559255, "field 'ly_netError'");
    target.ly_netError = finder.castView(view, 2131559255, "field 'ly_netError'");
    view = finder.findRequiredView(source, 2131559256, "field 'networkError_reset' and method 'onGetNetData'");
    target.networkError_reset = finder.castView(view, 2131559256, "field 'networkError_reset'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onGetNetData();
        }
      });
    view = finder.findRequiredView(source, 2131558523, "field 'listContainer'");
    target.listContainer = finder.castView(view, 2131558523, "field 'listContainer'");
  }

  @Override public void unbind(T target) {
    target.titleBack = null;
    target.titleName = null;
    target.ly_loading = null;
    target.ly_noData = null;
    target.idLvReturn = null;
    target.ly_netError = null;
    target.networkError_reset = null;
    target.listContainer = null;
  }
}

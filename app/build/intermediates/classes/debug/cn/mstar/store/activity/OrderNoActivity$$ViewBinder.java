// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class OrderNoActivity$$ViewBinder<T extends cn.mstar.store.activity.OrderNoActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559292, "field 'input'");
    target.input = finder.castView(view, 2131559292, "field 'input'");
  }

  @Override public void unbind(T target) {
    target.title = null;
    target.back = null;
    target.input = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ChooseRingListActivity$$ViewBinder<T extends cn.mstar.store.activity.ChooseRingListActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558523, "field 'listContainer'");
    target.listContainer = finder.castView(view, 2131558523, "field 'listContainer'");
  }

  @Override public void unbind(T target) {
    target.listContainer = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ChooseRingFraDetails$$ViewBinder<T extends cn.mstar.store.activity.ChooseRingFraDetails> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131559359, "field 'rbSelect1'");
    target.rbSelect1 = finder.castView(view, 2131559359, "field 'rbSelect1'");
    view = finder.findRequiredView(source, 2131559361, "field 'rbSelect2'");
    target.rbSelect2 = finder.castView(view, 2131559361, "field 'rbSelect2'");
    view = finder.findRequiredView(source, 2131559358, "field 'idRyRingContent1'");
    target.idRyRingContent1 = finder.castView(view, 2131559358, "field 'idRyRingContent1'");
    view = finder.findRequiredView(source, 2131559360, "field 'idRyRingContent2'");
    target.idRyRingContent2 = finder.castView(view, 2131559360, "field 'idRyRingContent2'");
    view = finder.findRequiredView(source, 2131559362, "field 'tvShowGo'");
    target.tvShowGo = finder.castView(view, 2131559362, "field 'tvShowGo'");
  }

  @Override public void unbind(T target) {
    target.rbSelect1 = null;
    target.rbSelect2 = null;
    target.idRyRingContent1 = null;
    target.idRyRingContent2 = null;
    target.tvShowGo = null;
  }
}

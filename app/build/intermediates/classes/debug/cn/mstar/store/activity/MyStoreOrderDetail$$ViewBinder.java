// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MyStoreOrderDetail$$ViewBinder<T extends cn.mstar.store.activity.MyStoreOrderDetail> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131558711, "field 'content'");
    target.content = finder.castView(view, 2131558711, "field 'content'");
    view = finder.findRequiredView(source, 2131559244, "field 'checkLog'");
    target.checkLog = finder.castView(view, 2131559244, "field 'checkLog'");
    view = finder.findRequiredView(source, 2131559245, "field 'confirmShip'");
    target.confirmShip = finder.castView(view, 2131559245, "field 'confirmShip'");
    view = finder.findRequiredView(source, 2131559246, "field 'agreeLayout'");
    target.agreeLayout = finder.castView(view, 2131559246, "field 'agreeLayout'");
    view = finder.findRequiredView(source, 2131559249, "field 'confirmLayout'");
    target.confirmLayout = finder.castView(view, 2131559249, "field 'confirmLayout'");
    view = finder.findRequiredView(source, 2131559250, "field 'logisBtn2'");
    target.logisBtn2 = finder.castView(view, 2131559250, "field 'logisBtn2'");
    view = finder.findRequiredView(source, 2131559251, "field 'confirmBtn'");
    target.confirmBtn = finder.castView(view, 2131559251, "field 'confirmBtn'");
    view = finder.findRequiredView(source, 2131559247, "field 'no'");
    target.no = finder.castView(view, 2131559247, "field 'no'");
    view = finder.findRequiredView(source, 2131559248, "field 'yes'");
    target.yes = finder.castView(view, 2131559248, "field 'yes'");
  }

  @Override public void unbind(T target) {
    target.back = null;
    target.title = null;
    target.content = null;
    target.checkLog = null;
    target.confirmShip = null;
    target.agreeLayout = null;
    target.confirmLayout = null;
    target.logisBtn2 = null;
    target.confirmBtn = null;
    target.no = null;
    target.yes = null;
  }
}

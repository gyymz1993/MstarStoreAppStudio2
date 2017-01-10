// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyCallingCard$$ViewBinder<T extends cn.mstar.store.activity.ModifyCallingCard> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131558755, "field 'img'");
    target.img = finder.castView(view, 2131558755, "field 'img'");
    view = finder.findRequiredView(source, 2131558840, "field 'name'");
    target.name = finder.castView(view, 2131558840, "field 'name'");
    view = finder.findRequiredView(source, 2131559186, "field 'phone'");
    target.phone = finder.castView(view, 2131559186, "field 'phone'");
    view = finder.findRequiredView(source, 2131559188, "field 'qq'");
    target.qq = finder.castView(view, 2131559188, "field 'qq'");
    view = finder.findRequiredView(source, 2131559190, "field 'weixin'");
    target.weixin = finder.castView(view, 2131559190, "field 'weixin'");
    view = finder.findRequiredView(source, 2131558813, "field 'storeName'");
    target.storeName = finder.castView(view, 2131558813, "field 'storeName'");
    view = finder.findRequiredView(source, 2131559193, "field 'storeDes'");
    target.storeDes = finder.castView(view, 2131559193, "field 'storeDes'");
  }

  @Override public void unbind(T target) {
    target.back = null;
    target.title = null;
    target.img = null;
    target.name = null;
    target.phone = null;
    target.qq = null;
    target.weixin = null;
    target.storeName = null;
    target.storeDes = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MyCallingCardActivity$$ViewBinder<T extends cn.mstar.store.activity.MyCallingCardActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131559195, "field 'logo1'");
    target.logo1 = finder.castView(view, 2131559195, "field 'logo1'");
    view = finder.findRequiredView(source, 2131558840, "field 'name'");
    target.name = finder.castView(view, 2131558840, "field 'name'");
    view = finder.findRequiredView(source, 2131559186, "field 'phone'");
    target.phone = finder.castView(view, 2131559186, "field 'phone'");
    view = finder.findRequiredView(source, 2131559188, "field 'qq'");
    target.qq = finder.castView(view, 2131559188, "field 'qq'");
    view = finder.findRequiredView(source, 2131559196, "field 'winxin'");
    target.winxin = finder.castView(view, 2131559196, "field 'winxin'");
    view = finder.findRequiredView(source, 2131558813, "field 'storeName'");
    target.storeName = finder.castView(view, 2131558813, "field 'storeName'");
    view = finder.findRequiredView(source, 2131559193, "field 'storeDescription'");
    target.storeDescription = finder.castView(view, 2131559193, "field 'storeDescription'");
    view = finder.findRequiredView(source, 2131559197, "field 'ewcode'");
    target.ewcode = finder.castView(view, 2131559197, "field 'ewcode'");
    view = finder.findRequiredView(source, 2131559198, "field 'logo2'");
    target.logo2 = finder.castView(view, 2131559198, "field 'logo2'");
    view = finder.findRequiredView(source, 2131559199, "field 'storeWxName'");
    target.storeWxName = finder.castView(view, 2131559199, "field 'storeWxName'");
    view = finder.findRequiredView(source, 2131559405, "field 'editbtn'");
    target.editbtn = finder.castView(view, 2131559405, "field 'editbtn'");
  }

  @Override public void unbind(T target) {
    target.back = null;
    target.title = null;
    target.logo1 = null;
    target.name = null;
    target.phone = null;
    target.qq = null;
    target.winxin = null;
    target.storeName = null;
    target.storeDescription = null;
    target.ewcode = null;
    target.logo2 = null;
    target.storeWxName = null;
    target.editbtn = null;
  }
}

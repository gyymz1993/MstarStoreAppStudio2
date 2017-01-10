// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PromotionManageActivity$$ViewBinder<T extends cn.mstar.store.activity.PromotionManageActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131559328, "field 'container'");
    target.container = finder.castView(view, 2131559328, "field 'container'");
    view = finder.findRequiredView(source, 2131558552, "field 'list'");
    target.list = finder.castView(view, 2131558552, "field 'list'");
    view = finder.findRequiredView(source, 2131558601, "field 'rg'");
    target.rg = finder.castView(view, 2131558601, "field 'rg'");
    view = finder.findRequiredView(source, 2131559202, "field 'rb1'");
    target.rb1 = finder.castView(view, 2131559202, "field 'rb1'");
    view = finder.findRequiredView(source, 2131559205, "field 'rb2'");
    target.rb2 = finder.castView(view, 2131559205, "field 'rb2'");
    view = finder.findRequiredView(source, 2131559209, "field 'rb3'");
    target.rb3 = finder.castView(view, 2131559209, "field 'rb3'");
    view = finder.findRequiredView(source, 2131559403, "field 'inputTxt'");
    target.inputTxt = finder.castView(view, 2131559403, "field 'inputTxt'");
    view = finder.findRequiredView(source, 2131559405, "field 'categoryImg'");
    target.categoryImg = finder.castView(view, 2131559405, "field 'categoryImg'");
  }

  @Override public void unbind(T target) {
    target.back = null;
    target.title = null;
    target.container = null;
    target.list = null;
    target.rg = null;
    target.rb1 = null;
    target.rb2 = null;
    target.rb3 = null;
    target.inputTxt = null;
    target.categoryImg = null;
  }
}

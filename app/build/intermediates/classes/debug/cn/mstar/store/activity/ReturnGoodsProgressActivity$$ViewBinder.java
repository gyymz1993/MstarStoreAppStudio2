// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReturnGoodsProgressActivity$$ViewBinder<T extends cn.mstar.store.activity.ReturnGoodsProgressActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131559347, "field 'rel_progress_1'");
    target.rel_progress_1 = finder.castView(view, 2131559347, "field 'rel_progress_1'");
    view = finder.findRequiredView(source, 2131559348, "field 'rel_progress_2'");
    target.rel_progress_2 = finder.castView(view, 2131559348, "field 'rel_progress_2'");
    view = finder.findRequiredView(source, 2131559349, "field 'rel_progress_3'");
    target.rel_progress_3 = finder.castView(view, 2131559349, "field 'rel_progress_3'");
    view = finder.findRequiredView(source, 2131559350, "field 'iv_icon_progress_1'");
    target.iv_icon_progress_1 = finder.castView(view, 2131559350, "field 'iv_icon_progress_1'");
    view = finder.findRequiredView(source, 2131559351, "field 'iv_icon_progress_2'");
    target.iv_icon_progress_2 = finder.castView(view, 2131559351, "field 'iv_icon_progress_2'");
    view = finder.findRequiredView(source, 2131559352, "field 'iv_icon_progress_3'");
    target.iv_icon_progress_3 = finder.castView(view, 2131559352, "field 'iv_icon_progress_3'");
    view = finder.findRequiredView(source, 2131559353, "field 'iv_icon_progress_4'");
    target.iv_icon_progress_4 = finder.castView(view, 2131559353, "field 'iv_icon_progress_4'");
    view = finder.findRequiredView(source, 2131558727, "field 'tv_order_id'");
    target.tv_order_id = finder.castView(view, 2131558727, "field 'tv_order_id'");
    view = finder.findRequiredView(source, 2131558726, "field 'tv_date'");
    target.tv_date = finder.castView(view, 2131558726, "field 'tv_date'");
    view = finder.findRequiredView(source, 2131558728, "field 'lny_logistic_details'");
    target.lny_logistic_details = finder.castView(view, 2131558728, "field 'lny_logistic_details'");
    view = finder.findRequiredView(source, 2131558729, "field 'lny_about_product'");
    target.lny_about_product = finder.castView(view, 2131558729, "field 'lny_about_product'");
    view = finder.findRequiredView(source, 2131558730, "field 'lny_problem_description'");
    target.lny_problem_description = finder.castView(view, 2131558730, "field 'lny_problem_description'");
    view = finder.findRequiredView(source, 2131558731, "field 'iv_back' and method 'back'");
    target.iv_back = finder.castView(view, 2131558731, "field 'iv_back'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.back();
        }
      });
    view = finder.findRequiredView(source, 2131559402, "field 'tv_title'");
    target.tv_title = finder.castView(view, 2131559402, "field 'tv_title'");
  }

  @Override public void unbind(T target) {
    target.rel_progress_1 = null;
    target.rel_progress_2 = null;
    target.rel_progress_3 = null;
    target.iv_icon_progress_1 = null;
    target.iv_icon_progress_2 = null;
    target.iv_icon_progress_3 = null;
    target.iv_icon_progress_4 = null;
    target.tv_order_id = null;
    target.tv_date = null;
    target.lny_logistic_details = null;
    target.lny_about_product = null;
    target.lny_problem_description = null;
    target.iv_back = null;
    target.tv_title = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReturnShopFlowActivity$ViewHolder$$ViewBinder<T extends cn.mstar.store.activity.ReturnShopFlowActivity.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558816, "field 'tvSendtime'");
    target.tvSendtime = finder.castView(view, 2131558816, "field 'tvSendtime'");
    view = finder.findRequiredView(source, 2131558817, "field 'tvChatcontent'");
    target.tvChatcontent = finder.castView(view, 2131558817, "field 'tvChatcontent'");
  }

  @Override public void unbind(T target) {
    target.tvSendtime = null;
    target.tvChatcontent = null;
  }
}

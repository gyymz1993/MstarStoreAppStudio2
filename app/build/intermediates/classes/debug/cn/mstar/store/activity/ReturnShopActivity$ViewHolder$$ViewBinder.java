// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ReturnShopActivity$ViewHolder$$ViewBinder<T extends cn.mstar.store.activity.ReturnShopActivity.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131559114, "field 'idTvState'");
    target.idTvState = finder.castView(view, 2131559114, "field 'idTvState'");
    view = finder.findRequiredView(source, 2131559120, "field 'idTvDate'");
    target.idTvDate = finder.castView(view, 2131559120, "field 'idTvDate'");
    view = finder.findRequiredView(source, 2131558962, "field 'img'");
    target.img = finder.castView(view, 2131558962, "field 'img'");
    view = finder.findRequiredView(source, 2131559115, "field 'idTvShopName'");
    target.idTvShopName = finder.castView(view, 2131559115, "field 'idTvShopName'");
    view = finder.findRequiredView(source, 2131559116, "field 'idTvSpec'");
    target.idTvSpec = finder.castView(view, 2131559116, "field 'idTvSpec'");
    view = finder.findRequiredView(source, 2131559117, "field 'idTvPrice'");
    target.idTvPrice = finder.castView(view, 2131559117, "field 'idTvPrice'");
    view = finder.findRequiredView(source, 2131559113, "field 'linearContainerView'");
    target.linearContainerView = finder.castView(view, 2131559113, "field 'linearContainerView'");
  }

  @Override public void unbind(T target) {
    target.idTvState = null;
    target.idTvDate = null;
    target.img = null;
    target.idTvShopName = null;
    target.idTvSpec = null;
    target.idTvPrice = null;
    target.linearContainerView = null;
  }
}

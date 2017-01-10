// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class GoodsManagement_InnerAdapter$LinearItemViewHolder$$ViewBinder<T extends cn.mstar.store.adapter.GoodsManagement_InnerAdapter.LinearItemViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558565, "field 'productIV'");
    target.productIV = finder.castView(view, 2131558565, "field 'productIV'");
    view = finder.findRequiredView(source, 2131558566, "field 'productName'");
    target.productName = finder.castView(view, 2131558566, "field 'productName'");
    view = finder.findRequiredView(source, 2131558567, "field 'productNorms'");
    target.productNorms = finder.castView(view, 2131558567, "field 'productNorms'");
    view = finder.findRequiredView(source, 2131558568, "field 'productPrice'");
    target.productPrice = finder.castView(view, 2131558568, "field 'productPrice'");
    view = finder.findRequiredView(source, 2131558569, "field 'productNums'");
    target.productNums = finder.castView(view, 2131558569, "field 'productNums'");
    view = finder.findRequiredView(source, 2131559023, "field 'tv_return_request_progress'");
    target.tv_return_request_progress = finder.castView(view, 2131559023, "field 'tv_return_request_progress'");
    view = finder.findRequiredView(source, 2131559025, "field 'rel_bottom'");
    target.rel_bottom = view;
    view = finder.findRequiredView(source, 2131559024, "field 'go_to_comment'");
    target.go_to_comment = finder.castView(view, 2131559024, "field 'go_to_comment'");
    view = finder.findRequiredView(source, 2131559022, "field 'oder_type'");
    target.oder_type = finder.castView(view, 2131559022, "field 'oder_type'");
  }

  @Override public void unbind(T target) {
    target.productIV = null;
    target.productName = null;
    target.productNorms = null;
    target.productPrice = null;
    target.productNums = null;
    target.tv_return_request_progress = null;
    target.rel_bottom = null;
    target.go_to_comment = null;
    target.oder_type = null;
  }
}

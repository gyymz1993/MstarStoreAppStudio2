// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class UploadProductActivity$$ViewBinder<T extends cn.mstar.store.activity.UploadProductActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131559412, "field 'addSpecification'");
    target.addSpecification = finder.castView(view, 2131559412, "field 'addSpecification'");
    view = finder.findRequiredView(source, 2131559396, "field 'category'");
    target.category = finder.castView(view, 2131559396, "field 'category'");
    view = finder.findRequiredView(source, 2131558634, "field 'categoryName'");
    target.categoryName = finder.castView(view, 2131558634, "field 'categoryName'");
    view = finder.findRequiredView(source, 2131559413, "field 'spContent'");
    target.spContent = finder.castView(view, 2131559413, "field 'spContent'");
    view = finder.findRequiredView(source, 2131559410, "field 'isUploadToPlatformView'");
    target.isUploadToPlatformView = finder.castView(view, 2131559410, "field 'isUploadToPlatformView'");
    view = finder.findRequiredView(source, 2131559411, "field 'ifOpenTxt'");
    target.ifOpenTxt = finder.castView(view, 2131559411, "field 'ifOpenTxt'");
    view = finder.findRequiredView(source, 2131559408, "field 'goodsNameTxt'");
    target.goodsNameTxt = finder.castView(view, 2131559408, "field 'goodsNameTxt'");
    view = finder.findRequiredView(source, 2131559409, "field 'goodsNoTxt'");
    target.goodsNoTxt = finder.castView(view, 2131559409, "field 'goodsNoTxt'");
    view = finder.findRequiredView(source, 2131559414, "field 'nativeUpload'");
    target.nativeUpload = finder.castView(view, 2131559414, "field 'nativeUpload'");
    view = finder.findRequiredView(source, 2131559330, "field 'imgContainer'");
    target.imgContainer = finder.castView(view, 2131559330, "field 'imgContainer'");
  }

  @Override public void unbind(T target) {
    target.back = null;
    target.title = null;
    target.addSpecification = null;
    target.category = null;
    target.categoryName = null;
    target.spContent = null;
    target.isUploadToPlatformView = null;
    target.ifOpenTxt = null;
    target.goodsNameTxt = null;
    target.goodsNoTxt = null;
    target.nativeUpload = null;
    target.imgContainer = null;
  }
}

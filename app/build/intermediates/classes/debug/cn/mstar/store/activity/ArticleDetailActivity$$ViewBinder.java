// Generated code from Butter Knife. Do not modify!
package cn.mstar.store.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ArticleDetailActivity$$ViewBinder<T extends cn.mstar.store.activity.ArticleDetailActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558731, "field 'back'");
    target.back = finder.castView(view, 2131558731, "field 'back'");
    view = finder.findRequiredView(source, 2131559402, "field 'title'");
    target.title = finder.castView(view, 2131559402, "field 'title'");
    view = finder.findRequiredView(source, 2131558476, "field 'articleTitle'");
    target.articleTitle = finder.castView(view, 2131558476, "field 'articleTitle'");
    view = finder.findRequiredView(source, 2131558842, "field 'time'");
    target.time = finder.castView(view, 2131558842, "field 'time'");
    view = finder.findRequiredView(source, 2131558552, "field 'content'");
    target.content = finder.castView(view, 2131558552, "field 'content'");
  }

  @Override public void unbind(T target) {
    target.back = null;
    target.title = null;
    target.articleTitle = null;
    target.time = null;
    target.content = null;
  }
}

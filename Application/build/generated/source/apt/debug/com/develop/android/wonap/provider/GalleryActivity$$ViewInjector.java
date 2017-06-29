// Generated code from Butter Knife. Do not modify!
package com.develop.android.wonap.provider;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class GalleryActivity$$ViewInjector<T extends com.develop.android.wonap.provider.GalleryActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755330, "field '_pager'");
    target._pager = finder.castView(view, 2131755330, "field '_pager'");
    view = finder.findRequiredView(source, 2131755332, "field '_thumbnails'");
    target._thumbnails = finder.castView(view, 2131755332, "field '_thumbnails'");
    view = finder.findRequiredView(source, 2131755331, "field '_closeButton'");
    target._closeButton = finder.castView(view, 2131755331, "field '_closeButton'");
  }

  @Override public void reset(T target) {
    target._pager = null;
    target._thumbnails = null;
    target._closeButton = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package com.develop.android.wonap.SocialNetwork;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SignupActivity$$ViewInjector<T extends com.develop.android.wonap.SocialNetwork.SignupActivity> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131689785, "field '_nameText'");
    target._nameText = finder.castView(view, 2131689785, "field '_nameText'");
    view = finder.findRequiredView(source, 2131689790, "field '_emailText'");
    target._emailText = finder.castView(view, 2131689790, "field '_emailText'");
    view = finder.findRequiredView(source, 2131689784, "field '_ciudad'");
    target._ciudad = finder.castView(view, 2131689784, "field '_ciudad'");
    view = finder.findRequiredView(source, 2131689786, "field '_apellidos'");
    target._apellidos = finder.castView(view, 2131689786, "field '_apellidos'");
    view = finder.findRequiredView(source, 2131689789, "field '_documento'");
    target._documento = finder.castView(view, 2131689789, "field '_documento'");
    view = finder.findRequiredView(source, 2131689788, "field '_tipo_doc'");
    target._tipo_doc = finder.castView(view, 2131689788, "field '_tipo_doc'");
    view = finder.findRequiredView(source, 2131689791, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131689791, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131689794, "field '_signupButton'");
    target._signupButton = finder.castView(view, 2131689794, "field '_signupButton'");
    view = finder.findRequiredView(source, 2131689795, "field '_loginLink'");
    target._loginLink = finder.castView(view, 2131689795, "field '_loginLink'");
    view = finder.findRequiredView(source, 2131689793, "field '_imageAnuncio'");
    target._imageAnuncio = finder.castView(view, 2131689793, "field '_imageAnuncio'");
  }

  @Override public void reset(T target) {
    target._nameText = null;
    target._emailText = null;
    target._ciudad = null;
    target._apellidos = null;
    target._documento = null;
    target._tipo_doc = null;
    target._passwordText = null;
    target._signupButton = null;
    target._loginLink = null;
    target._imageAnuncio = null;
  }
}

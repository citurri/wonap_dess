package com.develop.android.wonap.SocialNetwork;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.develop.android.wonap.R;


public class SplashFragment extends Fragment {

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, EMAIL});

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash, container, false);

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //UtilityService.requestLocation(activity);
    }
}


/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.develop.android.wonap.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.develop.android.wonap.R;

/**
 * The tourist attraction detail activity screen which contains the details of
 * a single attraction.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_ATTRACTION = "id_oferta";
    private static final String ID_EMPRESA = "id_empresa";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launch(Activity activity, String id_oferta, View heroView, String id_empresa) {
        Intent intent = getLaunchIntent(activity, id_oferta, id_empresa);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(id_empresa.equals("0")) {
               ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, heroView, heroView.getTransitionName());
                ActivityCompat.startActivity(activity, intent, options.toBundle());
            }
            else
                activity.startActivity(intent);
        } else {
            activity.startActivity(intent);
        }
    }

    public static Intent getLaunchIntent(Context context, String id_oferta, String id_empresa) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_ATTRACTION, id_oferta);
        intent.putExtra(ID_EMPRESA, id_empresa);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String attraction = getIntent().getStringExtra(EXTRA_ATTRACTION);
        String id_empresa = getIntent().getStringExtra(ID_EMPRESA);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetailFragment.createInstance(attraction, id_empresa))
                    .commit();
        }
    }
}

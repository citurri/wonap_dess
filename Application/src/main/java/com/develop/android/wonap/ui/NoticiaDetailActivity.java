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
public class NoticiaDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ATTRACTION = "id_noticia";
    private static final String ID_EMPRESA = "id_empresa";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launch(Activity activity, String id_noticia, View heroView, String id_empresa) {
        Intent intent = getLaunchIntent(activity, id_noticia, id_empresa);

        activity.startActivity(intent);

    }

    public static Intent getLaunchIntent(Context context, String id_noticia, String id_empresa) {
        Intent intent = new Intent(context, NoticiaDetailActivity.class);
        intent.putExtra(EXTRA_ATTRACTION, id_noticia);
        intent.putExtra(ID_EMPRESA, id_empresa);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_noticia);

        String noticia = getIntent().getStringExtra(EXTRA_ATTRACTION);
        String id_empresa = getIntent().getStringExtra(ID_EMPRESA);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_noticia, NoticiaDetailFragment.createInstance(noticia, id_empresa))
                    .commit();
        }
    }
}
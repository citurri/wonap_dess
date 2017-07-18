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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.NoticiasModel;
import com.develop.android.wonap.provider.GalleryActivity;
import com.google.android.gms.maps.model.LatLng;
import com.lid.lib.LabelImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * The tourist attraction detail fragment which contains the details of a
 * a single attraction (contained inside
 */
public class NoticiaDetailFragment extends Fragment implements
        ShareActionProvider.OnShareTargetSelectedListener  {

    private static final String EXTRA_ATTRACTION = "id_noticia";
    private NoticiasModel noticia_detalle;
    String id_noticia = "0";
    TextView nameTextView;
    TextView descTextView;
    TextView fechaTextView;
    LabelImageView noticiaImageView;
    int id_user = 0;
    private String toast;
    private ShareActionProvider share=null;
    private Intent shareIntent=new Intent(Intent.ACTION_SEND);

    public static NoticiaDetailFragment createInstance(String id_noticia) {
        NoticiaDetailFragment detailFragment = new NoticiaDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ATTRACTION, id_noticia);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    public NoticiaDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_detail_noticia, container, false);
        id_noticia = getArguments().getString(EXTRA_ATTRACTION);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        descTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        fechaTextView = (TextView) view.findViewById(R.id.fechaTextView);
        noticiaImageView = (LabelImageView) view.findViewById(R.id.noticiaImageView);
         

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        id_user = preferences.getInt("id_usuario", 0);

        shareIntent.setType("image/*");

        Log.v("Detalle Noticia:", "onCreateView");

        if (Utils.isConn(getActivity()))
        new GetNoticia(id_noticia).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_noticia, menu);
        MenuItem item = menu.findItem(R.id.share_noticia);
        //chat_1.setPadding(10,0,10,0);

        MenuItem map = menu.findItem(R.id.map_noticia);
        ImageButton mapa = ((ImageButton) map.getActionView());
        mapa.setImageResource(R.drawable.ic_building);
        //mapa.setPadding(1,0,1,0);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PERFIL DE EMPRESA
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                EmpresaProfileActivity.startUserProfileFromLocation(startingLocation, getActivity(),  noticia_detalle.getIdEmpresa());
                getActivity().overridePendingTransition(0, 0);
            }
        });

        share=(ShareActionProvider)MenuItemCompat.getActionProvider(item);
        share.setOnShareTargetSelectedListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // On Lollipop+ we finish so to run the nice animation
                    getActivity().finishAfterTransition();
                    return true;
                }
                else
                {
                    getActivity().finish();
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        return false;
    }

    private class GetNoticia extends AsyncTask<Void, Void, NoticiasModel> {

        String mStrings;
        String id_noticia;
        String WEBSERVER;

        public GetNoticia(String id_noticia) {
            this.id_noticia = id_noticia;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetNoticia_detalle", "onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final NoticiasModel doInBackground(Void... arg0) {

            mStrings = "[]";
            WEBSERVER = getActivity().getString(R.string.web_server);
            LatLng location = Utils.getLocation(getActivity());
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER + "api/getNoticiaDetalle.php?id_noticia=" + id_noticia);
                Log.v("GetNoticia_detalle", url.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.setConnectTimeout(15000);
                //con.setReadTimeout(15000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json).append("\n");
                }
                mStrings = sb.toString().trim();
                Log.v("GetNoticia_detalle", "doInBackground");
                try {
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            noticia_detalle = new
                                    NoticiasModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_noticia"),obj.getBoolean("es_favorito"),obj.getString("fecha_publicacion"));
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON", e.toString());

            }

            return noticia_detalle;
        }

        @Override
        protected void onPostExecute(final NoticiasModel result) {
            super.onPostExecute(result);
            Log.v("GetNoticia_detalle", "onPostExecute");

            if (noticia_detalle == null) {
                getActivity().finish();

            }

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");

            nameTextView.setTypeface(typeface);
            nameTextView.setTextSize(22f);
            nameTextView.setText(noticia_detalle.getTitulo());


            ///validezTextView.setText("Válido del "+ noticia_detalle.getFechaInicio().substring(0,10) + " al " + noticia_detalle.getFechaFin().substring(0,10));
            descTextView.setTypeface(typeface);
            descTextView.setTextSize(18f);
            descTextView.setText(noticia_detalle.getDescripcion());



            fechaTextView.setTypeface(typeface);
            fechaTextView.setTextSize(18f);
            fechaTextView.setText(noticia_detalle.getFechaPublicacion());

            int imageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                    * Constants.IMAGE_ANIM_MULTIPLIER;
            Glide.with(getActivity())
                    .load(WEBSERVER+"upload/"+noticia_detalle.getImagenNoticia())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.color.lighter_gray)
                    .override(imageSize, imageSize)
                    .into(noticiaImageView);
            noticiaImageView.setLabelTextSize(60);
            noticiaImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> images = new ArrayList<String>();
                    //Principal
                    images.add(WEBSERVER+"upload/"+noticia_detalle.getImagenNoticia());
                    Intent intent = new Intent(getActivity(), GalleryActivity.class);
                    intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, images);
                    startActivity(intent);
                }
            });

            String mensaje = "Empresa: " + noticia_detalle.getTitulo() + "\n\n" +
                    "Noticia: " + noticia_detalle.getDescripcion() +
                    "\n\nImagen: " + WEBSERVER +"upload/" +noticia_detalle.getImagenNoticia() +
                    "\n\nDescargue WONAP!! ";


            shareIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
            shareIntent.setType("text/plain");
            share.setShareIntent(shareIntent);

        }
    }
}

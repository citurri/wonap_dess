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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.provider.TouristAttractions;
import com.develop.android.wonap.service.UtilityService;
import com.develop.android.wonap.test.Attraction;
import com.develop.android.wonap.test.DetailActivity;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.lid.lib.LabelImageView;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

///import static com.develop.android.wonap.provider.TouristAttractions.ATTRACTIONS;

/**
 * The main tourist attraction fragment which contains a list of attractions
 * sorted by distance (contained inside
 */
public class AttractionListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private AttractionAdapter mAdapter;
    private LatLng mLatestLocation;
    private int mImageSize;
    private boolean mItemClicked;
    private PtrClassicFrameLayout mPtrFrame;
    SwipeRefreshLayout swipeContainer;
    SearchView searchView;
    AttractionsRecyclerView recyclerView;
    private static List<OfertaModel> result_original;
    List<OfertaModel> attractions = new LinkedList<OfertaModel>();
    private static String WEBSERVER = "";
    private static String id_ciudad = "";
    private static  Map<String, LatLng> CITY_LOCATIONS = new HashMap<String, LatLng>();
    private RotateLoading rotateLoading;


    public AttractionListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load a larger size image to make the activity transition to the detail screen smooth
        mImageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;

        WEBSERVER = getString(R.string.web_server);

        mLatestLocation = Utils.getLocation(getActivity());

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        rotateLoading = (RotateLoading) view.findViewById(R.id.rotateloading);
        rotateLoading.setLoadingColor(Color.CYAN);
        rotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.start();

        recyclerView =
                (AttractionsRecyclerView) view.findViewById(android.R.id.list);
        //recyclerView.setEmptyView(view.findViewById(android.R.id.empty));
        recyclerView.setHasFixedSize(true);

// Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        searchView = (SearchView) view.findViewById(R.id.search);
        searchView.setQueryHint("Empresa o Palabra Clave.");
        searchView.setOnQueryTextListener(this);



        return view;
    }

    private class GetCiudadCercana extends AsyncTask<Void, Void, Map<String, LatLng>> {

        String mStrings;
        LatLng mLatestLocation;

        public GetCiudadCercana(LatLng mLatestLocation){
            this.mLatestLocation = mLatestLocation;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetCiudadCercana","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final Map<String, LatLng> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getCiudades.php");
                Log.v("GetCiudadCercana",url.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.setConnectTimeout(15000);
                //con.setReadTimeout(15000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json).append("\n");
                }
                mStrings = sb.toString().trim();
                Log.v("GetCiudadCercana","doInBackground");
                try {
                    CITY_LOCATIONS.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //getClosestPOS( , mLatestLocation, "", "", true);

                            //if(getPOS.size() > 0) {
                            //  LatLng lugar = new LatLng(Double.parseDouble(getPOS.get(0).pos_latitud), Double.parseDouble(getPOS.get(0).pos_longitud));
                            // Double distancia = Utils.formatDistanceBetweenMetros(mLatestLocation, lugar);
                            // if (distancia <= Integer.parseInt(getValueApp("GEOFENCES_DISTANCE")))
                            CITY_LOCATIONS.put(obj.getString("id"),new LatLng( Double.parseDouble(obj.getString("latitud")), Double.parseDouble(obj.getString("longitud"))));
                        }
                 }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return CITY_LOCATIONS;
        }

        @Override
        protected void onPostExecute(Map<String, LatLng> result) {
            super.onPostExecute(result);


            id_ciudad = loadIdCiudadCercana();
            Log.v("GetCiudadCercana",id_ciudad);
            new GetClosestOffers(mLatestLocation, id_ciudad).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

     }

    }


    private class GetClosestOffers extends AsyncTask<Void, Void, List<OfertaModel>> {

        String mStrings;
        LatLng mLatestLocation;
        String id_ciudad;

        public GetClosestOffers(LatLng mLatestLocation, String id_ciudad){
            this.mLatestLocation = mLatestLocation;
            this.id_ciudad = id_ciudad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetClosestOffers","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final List<OfertaModel> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getAnunciosMasCercano.php?id_ciudad="+id_ciudad);
                Log.v("GetClosestOffers",url.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.setConnectTimeout(15000);
                //con.setReadTimeout(15000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json).append("\n");
                }
                mStrings = sb.toString().trim();
                Log.v("GetClosestOffers","doInBackground");
                try {
                    attractions.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //getClosestPOS( , mLatestLocation, "", "", true);

                            //if(getPOS.size() > 0) {
                            //  LatLng lugar = new LatLng(Double.parseDouble(getPOS.get(0).pos_latitud), Double.parseDouble(getPOS.get(0).pos_longitud));
                            // Double distancia = Utils.formatDistanceBetweenMetros(mLatestLocation, lugar);
                            // if (distancia <= Integer.parseInt(getValueApp("GEOFENCES_DISTANCE")))
                            attractions.add(new
                                    OfertaModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_oferta"),obj.getBoolean("es_cupon"),obj.getString("fecha_inicio"),obj.getString("fecha_fin"), obj.getString("denominacion"), obj.getString("pos_latitud"), obj.getString("pos_longitud"),obj.getString("pos_map_address"),  obj.getString("pos_map_city") ,obj.getString("pos_map_country"), obj.getString("distancia_user")));
                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return attractions;
        }

        @Override
        protected void onPostExecute(List<OfertaModel> result) {
            super.onPostExecute(result);
            Log.v("GetClosestOffers","onPostExecute");

            //new GetPOSCercanos(mLatestLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    fetchTimelineAsync(0);
                }
            });

            mAdapter = new AttractionAdapter(getActivity(), loadAttractionsFromLocation(mLatestLocation));
            result_original = attractions;
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(
                    getActivity(), getResources().getInteger(R.integer.list_columns)));
            recyclerView.setAdapter(mAdapter);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                    mBroadcastReceiver, UtilityService.getLocationUpdatedIntentFilter());
            swipeContainer.setRefreshing(false);
            rotateLoading.stop();
            rotateLoading.setVisibility(View.GONE);

            //añadimos geofences (max 90)
            //UtilityService.addGeofences(getActivity(), attractions);
        }

    }



    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        new GetCiudadCercana(mLatestLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }

    @Override
    public void onResume() {
        super.onResume();
        mItemClicked = false;
        new GetCiudadCercana(mLatestLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location =
                    intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            if (location != null) {
                mLatestLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mAdapter.mAttractionList = loadAttractionsFromLocation(mLatestLocation);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    public  List<OfertaModel> loadAttractionsFromLocation(final LatLng curLatLng) {
        //TouristAttractions anuncios = new TouristAttractions();
              if (curLatLng != null) {
                  if (attractions.size() > 0) {
                      Collections.sort(attractions,
                              new Comparator<OfertaModel>() {
                                  @Override
                                  public int compare(OfertaModel lhs, OfertaModel rhs) {
                                      double lhsDistance = SphericalUtil.computeDistanceBetween(
                                              new LatLng(Double.parseDouble(lhs.getPosLatitud()), Double.parseDouble(lhs.getPosLongitud())), curLatLng);
                                      double rhsDistance = SphericalUtil.computeDistanceBetween(
                                              new LatLng(Double.parseDouble(rhs.getPosLatitud()), Double.parseDouble(rhs.getPosLongitud())), curLatLng);
                                      return (int) (lhsDistance - rhsDistance);
                                  }
                              }
                      );
                  }
              }
            return attractions;
        }

    private String loadIdCiudadCercana() {
        mLatestLocation = Utils.getLocation(getActivity());

        double minDistance = 0;
        String closestCity = null;
        for (Map.Entry<String, LatLng> entry: CITY_LOCATIONS.entrySet()) {
            double distance = SphericalUtil.computeDistanceBetween(mLatestLocation, entry.getValue());
            if (minDistance == 0 || distance < minDistance) {
                minDistance = distance;
                closestCity = entry.getKey();
            }
        }
        return closestCity;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<OfertaModel> filteredModelList = filter(result_original, newText);
        animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;

    }
    public void animateTo(List<OfertaModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<OfertaModel> newModels) {
        for (int i =   mAdapter.mAttractionList.size() - 1; i >= 0; i--) {
            final OfertaModel model =   mAdapter.mAttractionList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<OfertaModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final OfertaModel model = newModels.get(i);
            if (!  mAdapter.mAttractionList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<OfertaModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final OfertaModel model = newModels.get(toPosition);
            final int fromPosition =   mAdapter.mAttractionList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public OfertaModel removeItem(int position) {
        final OfertaModel model =  mAdapter.mAttractionList.remove(position);
        mAdapter.notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, OfertaModel model) {
        mAdapter.mAttractionList.add(position, model);
        mAdapter.notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final OfertaModel model =  mAdapter.mAttractionList.remove(fromPosition);
        mAdapter.mAttractionList.add(toPosition, model);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    private List<OfertaModel> filter(List<OfertaModel> models, String query) {
        query = query.toLowerCase();

        final List<OfertaModel> filteredModelList = new ArrayList<>();
        for (OfertaModel model : models) {
            final String text = model.getTitulo().toLowerCase();
            final String text2 = model.getDescripcion().toLowerCase();
            final String text3 = model.getNombreEmpresa().toLowerCase();
            if (text.contains(query) || text2.contains(query) || text3.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private class AttractionAdapter extends RecyclerView.Adapter<ViewHolder>
            implements ItemClickListener {

        public List<OfertaModel> mAttractionList;
        private Context mContext;

        public AttractionAdapter(Context context, List<OfertaModel> attractions) {
            super();
            mContext = context;
            mAttractionList = attractions;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_row, parent, false);
            return new ViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            OfertaModel attraction = mAttractionList.get(position);

            holder.mTitleTextView.setText(attraction.getTitulo());
            holder.mDescriptionTextView.setText(attraction.getDescripcion());
            holder.t_empresa.setText(attraction.getNombreEmpresa());
            holder.t_ciudad.setText(attraction.getDenominacion());
            Glide.with(mContext)
                    .load(WEBSERVER+"upload/"+attraction.getImagenOferta())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .override(mImageSize, mImageSize)
                    .into(holder.mImageView);
            holder.mImageView.setLabelHeight(30);
            String distance =
                    Utils.formatDistanceBetween(mLatestLocation, new LatLng(Double.parseDouble(attraction.getPosLatitud()),Double.parseDouble(attraction.getPosLongitud())));
            if (TextUtils.isEmpty(distance)) {
                holder.mOverlayTextView.setVisibility(View.GONE);
            } else {
                holder.mOverlayTextView.setVisibility(View.VISIBLE);
                holder.mOverlayTextView.setText(distance);
            }

            if(attraction.getEsCupon()){
               holder.mImageView.setLabelVisual(true);
            }
            else
               holder.mImageView.setLabelVisual(false);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mAttractionList == null ? 0 : mAttractionList.size();
        }

        @Override
        public void onItemClick(View view, int position) {
            if (!mItemClicked) {
                mItemClicked = true;
                View heroView = view.findViewById(android.R.id.icon);
                DetailActivity.launch(
                        getActivity(), mAdapter.mAttractionList.get(position).getTitulo(), heroView);
            }
        }

        // Clean all elements of the recycler
        public void clear() {
            mAttractionList.clear();
            notifyDataSetChanged();
        }

    }

    private static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView mTitleTextView;
        TextView mDescriptionTextView;
        TextView mOverlayTextView;
        TextView t_empresa;
        TextView t_ciudad;
        LabelImageView mImageView;
        ItemClickListener mItemClickListener;


        public ViewHolder(View view, ItemClickListener itemClickListener) {
            super(view);
            mTitleTextView = (TextView) view.findViewById(R.id.textView6);
            mDescriptionTextView = (TextView) view.findViewById(R.id.textView3);
            mOverlayTextView = (TextView) view.findViewById(R.id.overlaytext);
            t_empresa = (TextView) view.findViewById(R.id.text_empresa);
            t_ciudad = (TextView) view.findViewById(R.id.text_ciudad);
            mImageView = (LabelImageView) view.findViewById(R.id.labelImageView5);
            mItemClickListener = itemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

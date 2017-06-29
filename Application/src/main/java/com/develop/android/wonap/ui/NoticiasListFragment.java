package com.develop.android.wonap.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.NoticiasModel;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.service.UtilityService;
import com.develop.android.wonap.test.WonderModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
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


public class NoticiasListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    ArrayList<WonderModel> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;
    String Wonders[] = {"Chichen Itza","Christ the Redeemer","Great Wall of China","Machu Picchu","Petra","Taj Mahal","Colosseum"};
    int  Images[] = {R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo,R.drawable.logo};
    SwipeRefreshLayout swipeContainer;
    SearchView searchView;
    private RotateLoading rotateLoading;
    private LatLng mLatestLocation;
    private static String WEBSERVER = "";
    private static  Map<String, LatLng> CITY_LOCATIONS = new HashMap<String, LatLng>();
    private static String id_ciudad = "";
    private static  NoticiasListFragment fragmento;
    int id_user;
    List<NoticiasModel> noticias = new LinkedList<NoticiasModel>();
    private NoticiasAdapter mAdapter;
    private int mImageSize;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_noticias, container, false);
        mImageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;
        WEBSERVER = getString(R.string.web_server);

        mLatestLocation = Utils.getLocation(getActivity());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        id_user = preferences.getInt("id_usuario", 0);

        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);

        rotateLoading = (RotateLoading) view.findViewById(R.id.rotateloading_noticia);
        rotateLoading.setLoadingColor(Color.CYAN);
        rotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.start();

// Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerNoticias);
        // Setup refresh listener which triggers new data loading

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        searchView = (SearchView) view.findViewById(R.id.search_noticias);
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
            Log.v("GetCiudadNoticia","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final Map<String, LatLng> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getCiudades.php");
                Log.v("GetCiudadNoticia",url.toString());
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
                Log.v("GetCiudadNoticia","doInBackground");
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
            Log.v("GetCiudadNoticia","onPostExecute");
            if (!fragmento.isDetached()) {
                if (!result.isEmpty()) {
                    id_ciudad = loadIdCiudadCercana();
                    //Log.v("GetCiudadCercana",id_ciudad);
                    if (id_ciudad != null) {
                        if (Utils.isConn(getActivity()))
                            new GetClosestNoticias(mLatestLocation, id_ciudad).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        }

    }

    private class GetClosestNoticias extends AsyncTask<Void, Void, List<NoticiasModel>> {

        String mStrings;
        LatLng mLatestLocation;
        String id_ciudad;

        public GetClosestNoticias(LatLng mLatestLocation, String id_ciudad){
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
        protected final List<NoticiasModel> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getNoticiasMasCercanas.php?id_ciudad="+id_ciudad+"&id_user="+id_user);
                Log.v("getNoticiasMasCercanas",url.toString());
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
                Log.v("getNoticiasMasCercanas","doInBackground");
                try {
                    noticias.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //getClosestPOS( , mLatestLocation, "", "", true);

                            //if(getPOS.size() > 0) {
                            //  LatLng lugar = new LatLng(Double.parseDouble(getPOS.get(0).pos_latitud), Double.parseDouble(getPOS.get(0).pos_longitud));
                            // Double distancia = Utils.formatDistanceBetweenMetros(mLatestLocation, lugar);
                            // if (distancia <= Integer.parseInt(getValueApp("GEOFENCES_DISTANCE")))
                            noticias.add(new
                                    NoticiasModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_noticia"),obj.getBoolean("es_favorito"),obj.getString("fecha_publicacion")));
                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return noticias;
        }

        @Override
        protected void onPostExecute(List<NoticiasModel> result) {
            super.onPostExecute(result);
            Log.v("getNoticiasMasCercanas","onPostExecute");

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


            MyRecyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            MyRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
            if (noticias.size() > 0 & MyRecyclerView != null) {
                mAdapter = new NoticiasAdapter(getActivity(), noticias);
                MyRecyclerView.setAdapter(mAdapter);
            }

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
        if (Utils.isConn(getActivity())) {
            fragmento = this;
            new GetCiudadCercana(mLatestLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isConn(getActivity())) {
            fragmento = this;
            new GetCiudadCercana(mLatestLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
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
        return false;
    }

    public class NoticiasAdapter extends RecyclerView.Adapter<MyViewHolder> {

        public List<NoticiasModel> list;
        private Context mContext;

        public NoticiasAdapter(Context context, List<NoticiasModel> Data) {
            super();
            mContext = context;
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_noticias, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.titleTextView.setText(list.get(position).getTitulo());
            holder.empresaTextView.setText(list.get(position).getNombreEmpresa());
            Glide.with(mContext)
                    .load(WEBSERVER+"upload/"+list.get(position).getImagenNoticia())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .into(holder.coverImageView);
            holder.likeImageView.setTag(R.drawable.ic_like);
            holder.likeImageView.setDrawingCacheEnabled(true);
            holder.likeImageView.buildDrawingCache();

        }

       @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView empresaTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            empresaTextView = (TextView) v.findViewById(R.id.EmpresaTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int id = (int)likeImageView.getTag();
                        if( id == R.drawable.ic_like){

                            likeImageView.setTag(R.drawable.ic_liked);
                            likeImageView.setImageResource(R.drawable.ic_liked);

                            Toast.makeText(getActivity(),titleTextView.getText()+" added to favourites", Toast.LENGTH_SHORT).show();

                        }else{

                            likeImageView.setTag(R.drawable.ic_like);
                            likeImageView.setImageResource(R.drawable.ic_like);
                            Toast.makeText(getActivity(),titleTextView.getText()+" removed from favourites", Toast.LENGTH_SHORT).show();


                        }

                }
            });

            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bitmap bitmap = likeImageView.getDrawingCache();


                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bitmap);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));



                }
            });



        }
    }


}

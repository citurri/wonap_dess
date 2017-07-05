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
import com.develop.android.wonap.common.ExifUtil;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.NoticiasModel;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.service.UtilityService;
import com.develop.android.wonap.test.WonderModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.R.id.list;


public class NoticiasListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    RecyclerView MyRecyclerView;
    SwipeRefreshLayout swipeContainer;
    SearchView searchView;
    private RotateLoading rotateLoading;
    private LatLng mLatestLocation;
    private static String WEBSERVER = "";
    private static  Map<String, LatLng> CITY_LOCATIONS = new HashMap<String, LatLng>();
    private static String id_ciudad = "";
    private static  NoticiasListFragment fragmento;
    int id_user = 0;
    List<NoticiasModel> noticias = new ArrayList<>();
    private NoticiasAdapter mAdapter;
    private int mImageSize;
    private  List<NoticiasModel> result_original = new ArrayList<>();
    private boolean mItemClicked;

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
                    result_original.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);

                            noticias.add(new
                                    NoticiasModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_noticia"),obj.getBoolean("es_favorito"),obj.getString("fecha_publicacion")));
                            result_original.add(new
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

            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    fetchTimelineAsync(0);
                }
            });


            MyRecyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            MyRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
            if (result.size() > 0 & MyRecyclerView != null) {
                mAdapter = new NoticiasAdapter(getActivity(), result);
                MyRecyclerView.setAdapter(mAdapter);

            }
            swipeContainer.setRefreshing(false);
            rotateLoading.stop();
            rotateLoading.setVisibility(View.GONE);
        }

    }


    public void fetchTimelineAsync(int page) {

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
        mItemClicked = false;

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
        final List<NoticiasModel> filteredModelList = filter(result_original, newText);

        if(result_original != null & result_original.size() > 0)
            animateTo(filteredModelList);
        MyRecyclerView.scrollToPosition(0);
        return true;

    }

    public void animateTo(List<NoticiasModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<NoticiasModel> newModels) {
        for (int i =   mAdapter.list.size() - 1; i >= 0; i--) {
            final NoticiasModel model =   mAdapter.list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<NoticiasModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final NoticiasModel model = newModels.get(i);
            if (!  mAdapter.list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<NoticiasModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final NoticiasModel model = newModels.get(toPosition);
            final int fromPosition =   mAdapter.list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public NoticiasModel removeItem(int position) {
        final NoticiasModel model =  mAdapter.list.remove(position);
        mAdapter.notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, NoticiasModel model) {
        mAdapter.list.add(position, model);
        mAdapter.notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final NoticiasModel model =  mAdapter.list.remove(fromPosition);
        mAdapter.list.add(toPosition, model);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    private List<NoticiasModel> filter(List<NoticiasModel> models, String query) {
        query = query.toLowerCase();

        final List<NoticiasModel> filteredModelList = new ArrayList<>();

        if(models != null)
            for (NoticiasModel model : models) {
                final String text = model.getTitulo().toLowerCase();
                final String text2 = model.getDescripcion().toLowerCase();
                final String text3 = model.getNombreEmpresa().toLowerCase();
                if (text.contains(query) || text2.contains(query) || text3.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        return filteredModelList;
    }


    public class NoticiasAdapter extends RecyclerView.Adapter<MyViewHolder> implements ItemClickListener {

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
            return new MyViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.titleTextView.setText(list.get(position).getTitulo());
            holder.empresaTextView.setText(list.get(position).getNombreEmpresa());
            holder.fechaTextView.setText(list.get(position).getFechaPublicacion());
            Glide.with(mContext)
                    .load(WEBSERVER+"upload/"+list.get(position).getImagenNoticia())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .into(holder.coverImageView);
            holder.likeImageView.setTag(R.drawable.ic_like);
            holder.likeImageView.setDrawingCacheEnabled(true);
            holder.likeImageView.buildDrawingCache();

            if(list.get(position).getEsFavorito()){
                holder.likeImageView.setTag(R.drawable.ic_liked);
                holder.likeImageView.setImageResource(R.drawable.ic_liked);
            }
            else{
                holder.likeImageView.setTag(R.drawable.ic_like);
                holder.likeImageView.setImageResource(R.drawable.ic_like);
            }

            holder.likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean favorito;
                    int id = (int)holder.likeImageView.getTag();
                    if( id == R.drawable.ic_like){

                        holder.likeImageView.setTag(R.drawable.ic_liked);
                        holder.likeImageView.setImageResource(R.drawable.ic_liked);
                        favorito = true;
                        //Toast.makeText(getActivity(),list.get(position).getNombreEmpresa()+" añadido a favoritos", Toast.LENGTH_LONG).show();

                    }else{

                        holder.likeImageView.setTag(R.drawable.ic_like);
                        holder.likeImageView.setImageResource(R.drawable.ic_like);
                        favorito= false;
                        //Toast.makeText(getActivity(),list.get(position).getNombreEmpresa()+" eliminado de sus favoritos", Toast.LENGTH_LONG).show();
                    }


                    HashMap<String, String> postdata = new HashMap<String, String>();
                    postdata.put("id_user", String.valueOf(id_user));
                    postdata.put("id_empresa", String.valueOf(list.get(position).getIdEmpresa()));
                    postdata.put("favorito", String.valueOf(favorito));

                    PostResponseAsyncTask task = new PostResponseAsyncTask(getActivity(), postdata, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            switch (s) {
                                case "1":
                                    Toast.makeText(getActivity(), list.get(position).getNombreEmpresa()+" añadido a favoritos", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getActivity(), list.get(position).getNombreEmpresa()+" eliminado de sus favoritos", Toast.LENGTH_LONG).show();
                                   break;

                            }
                        }
                    });


                    task.setLoadingMessage("Actualizando su lista de favoritos, espere por favor.");
                    task.execute(WEBSERVER + "api/updateFavorito.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Log.v("Favorito Task: ", "No se puede conectar al servidor.");
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Log.v("Favorito Task: ", "Error de URL.");
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Log.v("Favorito Task: ", "Error de Protocolo.");
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Log.v("Favorito Task: ", "Error de codificación.");
                        }
                    });

                }
            });
        }

       @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onItemClick(View view, int position) {

            if (!mItemClicked) {
                mItemClicked = true;
                View heroView = view.findViewById(R.id.coverImageView);
                NoticiaDetailActivity.launch(
                        getActivity(), mAdapter.list.get(position).getId(), heroView);
            }

        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTextView;
        public TextView empresaTextView;
        public TextView fechaTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        ItemClickListener mItemClickListener;

        public MyViewHolder(View v, ItemClickListener itemClickListener) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            empresaTextView = (TextView) v.findViewById(R.id.EmpresaTextView);
            fechaTextView = (TextView) v.findViewById(R.id.fechaTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) v.findViewById(R.id.shareImageView);


            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String mensaje = "Empresa: " + empresaTextView.getText() + "\n\n" +
                            "Detalle: " + titleTextView.getText() +
                            "\n\nDescargue WONAP!! ";



                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));



                }
            });

            mItemClickListener = itemClickListener;
            v.setOnClickListener(this);


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

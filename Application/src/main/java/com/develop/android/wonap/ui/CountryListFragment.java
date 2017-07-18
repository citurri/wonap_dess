package com.develop.android.wonap.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.WonapDatabaseLocal;
import com.develop.android.wonap.database.w_ciudad;
import com.develop.android.wonap.database.w_ciudad;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CountryListFragment extends Fragment {

    private CiudadesAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<w_ciudad> ciudades = new LinkedList<w_ciudad>();;
    private static String action;
    private static String WEBSERVER = "";
    private LatLng mLatestLocation;

    public static CountryListFragment newInstance(String action_intent) {
        action = action_intent;
        return new CountryListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main_ciudades, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ciudades);
        WEBSERVER = getString(R.string.web_server);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new GetCiudades().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    private class GetCiudades extends AsyncTask<Void, Void, List<w_ciudad>> {

        String mStrings;


        public GetCiudades(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetCiudades","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final List<w_ciudad> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getCiudades.php");
                Log.v("GetCiudades",url.toString());
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
                Log.v("GetCiudades","doInBackground");
                try {
                    ciudades.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                        ciudades.add(new w_ciudad(obj.getString("id"),obj.getString("pais"),obj.getString("nombre"),obj.getString("latitud"),obj.getString("longitud")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return ciudades;
        }

        @Override
        protected void onPostExecute(List<w_ciudad> result) {
            super.onPostExecute(result);
            Log.v("GetCiudades","onPostExecute");
            if(!isDetached()) {
                mAdapter = new CiudadesAdapter(getActivity(), ciudades);
                recyclerView.setAdapter(mAdapter);
            }
        }

    }




    private class CiudadesAdapter extends RecyclerView.Adapter<ViewHolder>
            implements ItemClickListener {

        private final LayoutInflater inflater;
        private final List<w_ciudad> mCiudades;

        public CiudadesAdapter(Context context, List<w_ciudad> ciudades) {
            inflater = LayoutInflater.from(context);
            mCiudades = new ArrayList<>(ciudades);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = inflater.inflate(R.layout.list_row_country, parent, false);
            return new ViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final w_ciudad ciudades = mCiudades.get(position);
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");
            holder.mCiudad.setTypeface(typeface);
            holder.mCiudad.setText(ciudades.getNombre());

        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mCiudades == null ? 0 : mCiudades.size();
        }

        @Override
        public void onItemClick(View view, int position) {
            w_ciudad ciudad = mCiudades.get(position);
            Intent intent = null;

            if(action.equals("Empresas")) intent = new Intent(getActivity(), EmpresasActivity.class);
            //else intent = new Intent(getActivity(), AnunciosList.class);

            Bundle b = new Bundle();
            b.putString("id_ciudad", ciudad.getId());
            b.putString("Pais", ciudad.getPais());
            b.putString("Ciudad", ciudad.getNombre());
            b.putBoolean("Todos", false);
            b.putBoolean("Proximidad", false);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView mCiudad;
        ItemClickListener mItemClickListener;


        public ViewHolder(View view, ItemClickListener itemClickListener) {
            super(view);
            mCiudad = (TextView) view.findViewById(R.id.ciudad_text);
            mItemClickListener = itemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition());
        }

        public void bind(w_ciudad model) {
            mCiudad.setText(model.getNombre());
        }
    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}

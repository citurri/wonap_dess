package com.develop.android.wonap.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.markers;
import com.develop.android.wonap.database.w_sucursales;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmpresaSucursalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmpresaSucursalesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id_empresa";
    String WEBSERVER;
    private LatLng mLatestLocation;
    private boolean mItemClicked;
    private String id_empresa;
    @BindView(R.id.empresaSucursales)
    RecyclerView empresaSucursales;
    @BindView(R.id.nulltextView)
    TextView nullTextView;
    private SucursalAdapter sucursalAdapter;
    ArrayList<w_sucursales> sucursales = new ArrayList<>();

    public EmpresaSucursalesFragment() {
        // Required empty public constructor
    }

    public static EmpresaSucursalesFragment newInstance(String id_empresa) {
        EmpresaSucursalesFragment fragment = new EmpresaSucursalesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id_empresa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_empresa = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empresa_sucursales, container, false);
        ButterKnife.bind(this, view);

        mLatestLocation = Utils.getLocation(getActivity());

        WEBSERVER = getActivity().getString(R.string.web_server);

        new GetSucursales().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }


    public  List<w_sucursales> loadAttractionsFromLocation(final LatLng curLatLng) {

        if (curLatLng != null) {
            if (sucursales.size() > 0) {
                Collections.sort(sucursales,
                        new Comparator<w_sucursales>() {
                            @Override
                            public int compare(w_sucursales lhs, w_sucursales rhs) {
                                double lhsDistance = SphericalUtil.computeDistanceBetween(
                                        new LatLng(Double.parseDouble(lhs.getPos_latitud()), Double.parseDouble(lhs.getPos_longitud())), curLatLng);
                                double rhsDistance = SphericalUtil.computeDistanceBetween(
                                        new LatLng(Double.parseDouble(rhs.getPos_latitud()), Double.parseDouble(rhs.getPos_longitud())), curLatLng);
                                return (int) (lhsDistance - rhsDistance);
                            }
                        }
                );

            }
        }
        return sucursales;
    }

    private class GetSucursales extends AsyncTask<Void, Void, ArrayList<w_sucursales>> {

        String mStrings;

        public GetSucursales(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetSucursales","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final ArrayList<w_sucursales> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getEmpresaSucursales.php?id_empresa="+id_empresa);
                Log.v("GetSucursales",url.toString());
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
                Log.v("GetSucursales","doInBackground");
                try {
                    sucursales.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            sucursales.add(new w_sucursales(obj.getString("Id"),obj.getString("denominacion"),obj.getString("ciudad"),obj.getString("direccion"),obj.getString("imagen_sucursal"),obj.getString("telefono_fijo"),obj.getString("celular1"),obj.getString("celular2"),obj.getString("email"), obj.getString("pos_latitud"),obj.getString("pos_longitud")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return sucursales;
        }

        @Override
        protected void onPostExecute(ArrayList<w_sucursales> result) {
            super.onPostExecute(result);
            Log.v("GetSucursales","onPostExecute");
            if(!isDetached()) {
                sucursalAdapter = new SucursalAdapter(getActivity(), loadAttractionsFromLocation(mLatestLocation));
                empresaSucursales.setHasFixedSize(true);
                empresaSucursales.setLayoutManager(new GridLayoutManager(
                        getActivity(), getResources().getInteger(R.integer.list_columns)));
                empresaSucursales.setAdapter(sucursalAdapter);

                if (sucursales.size() > 0)
                    nullTextView.setVisibility(View.INVISIBLE);
                else
                    nullTextView.setVisibility(View.VISIBLE);
            }
        }

    }


    private class SucursalAdapter extends RecyclerView.Adapter<ViewHolder> implements ItemClickListener {

        private final LayoutInflater inflater;
        private final List<w_sucursales> mSucursales;
        private Context mContext;

        public SucursalAdapter(Context context, List<w_sucursales> mSucursales) {
            inflater = LayoutInflater.from(context);
            this.mSucursales = mSucursales;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = inflater.inflate(R.layout.list_row_sucursales, parent, false);
            return new ViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");
            //holder.denominacion.setTypeface(typeface);
            holder.denominacion.setText(mSucursales.get(position).getDenominacion());
            holder.direccion.setTypeface(typeface);
            String direccion = "";
            if(mSucursales.get(position).getDireccion().isEmpty())
                direccion = "Sin dirección";
            else
                direccion = mSucursales.get(position).getDireccion();
            holder.direccion.setText(direccion);
            holder.telefonos.setTypeface(typeface);
            String telefonos;
            if(mSucursales.get(position).getTelefonoFijo().isEmpty() || mSucursales.get(position).getTelefonoFijo().equals("null")) {
                if (mSucursales.get(position).getCelular1().isEmpty() || mSucursales.get(position).getCelular1().equals("null"))
                    telefonos = "Sin teléfonos";
                else
                    telefonos = mSucursales.get(position).getCelular1();
            }
            else
            {
                if (mSucursales.get(position).getCelular1().isEmpty() || mSucursales.get(position).getCelular1().equals("null"))
                    telefonos = mSucursales.get(position).getTelefonoFijo();
                else
                    telefonos = mSucursales.get(position).getTelefonoFijo() + " - " + mSucursales.get(position).getCelular1();
            }
            holder.telefonos.setText(telefonos);

            holder.email.setTypeface(typeface);
            holder.email.setText(mSucursales.get(position).getEmail());

            holder.ciudad.setTypeface(typeface);
            holder.ciudad.setText(mSucursales.get(position).getCiudad());


            String distance =
                    Utils.formatDistanceBetween(mLatestLocation, new LatLng(Double.parseDouble(mSucursales.get(position).getPos_latitud()),Double.parseDouble(mSucursales.get(position).getPos_longitud())));

            if (TextUtils.isEmpty(distance)) {
                holder.distancia.setVisibility(View.GONE);
            } else {
                holder.distancia.setVisibility(View.VISIBLE);
                holder.distancia.setTypeface(typeface);
                holder.distancia.setText(distance);
            }


            if(!mSucursales.get(position).getImagenSucursal().equals("")) {
                Glide.with(mContext)
                        .load(WEBSERVER + "upload/" + mSucursales.get(position).getImagenSucursal())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.empty_photo)
                        .into(holder.imagen);
            }
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mSucursales == null ? 0 : mSucursales.size();
        }

        @Override
        public void onItemClick(View view, int position) {

                ArrayList<markers> marks = new ArrayList<markers>();
                markers m = new markers(mSucursales.get(position).getDenominacion(), mSucursales.get(position).getDireccion(), mSucursales.get(position).getPos_latitud(), mSucursales.get(position).getPos_longitud());
                marks.add(m);
                Intent i = new Intent(getActivity(), MapsOfertaActivity.class);
                i.putParcelableArrayListExtra("markers", marks);
                i.putExtra("titulo", mSucursales.get(position).getDenominacion());
                i.putExtra("titulo_mapa", "Ubicación de la sucursal");
                startActivity(i);

        }



    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView denominacion;
        TextView direccion;
        TextView telefonos;
        TextView email;
        TextView ciudad;
        TextView distancia;
        ImageView imagen;
        ItemClickListener mItemClickListener;

        public ViewHolder(View view , ItemClickListener itemClickListener) {
            super(view);
            denominacion = (TextView) view.findViewById(R.id.denoTextView);
            direccion = (TextView) view.findViewById(R.id.dirTextView);
            telefonos = (TextView) view.findViewById(R.id.telTextView);
            email = (TextView) view.findViewById(R.id.emailText);
            ciudad = (TextView) view.findViewById(R.id.ciudadText);
            distancia = (TextView) view.findViewById(R.id.distTextView);
            imagen = (ImageView) view.findViewById(R.id.imageView5);
            mItemClickListener = itemClickListener;
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClick(view, getPosition());
        }
    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}

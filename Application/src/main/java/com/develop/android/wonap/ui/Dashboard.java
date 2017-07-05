package com.develop.android.wonap.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.CircleTransform;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.NoticiasModel;
import com.develop.android.wonap.database.w_banner;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

 * Activities that contain this fragment must implement the

 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment implements BaseSliderView.OnSliderClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SliderLayout mDemoSlider;
    String nombre_completo;
    String image_user;
    private LatLng mLatestLocation;
    private static String WEBSERVER = "";
    private static  Map<String, LatLng> CITY_LOCATIONS = new HashMap<String, LatLng>();
    private static String id_ciudad = "";
    private static  Dashboard fragmento;
    List<w_banner> banner = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
  /*  RotatingTextWrapper rotatingTextWrapper;
    RotatingTextWrapper rotatingTextWrapper2;*/
    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);
        WEBSERVER = getString(R.string.web_server);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        nombre_completo = preferences.getString("nombre_completo", "");
        image_user = preferences.getString("image_user", "");

        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout_ofertas);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Click Ofertas",Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout layout_noticias = (LinearLayout)view.findViewById(R.id.layout_noticas);
        layout_noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Click Noticias",Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout layout_empresa = (LinearLayout)view.findViewById(R.id.layout_empresa);
        layout_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Click Empresa",Toast.LENGTH_SHORT).show();
            }
        });
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Reckoner_Bold.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");

        TextView text_ofertas = (TextView)view.findViewById(R.id.custom_switcher);
        text_ofertas.setTypeface(typeface);
        text_ofertas.setTextColor(Color.parseColor("#FFA036"));
        text_ofertas.setTextSize(22);
        text_ofertas.setText("Lista de\r\nOfertas");
        TextView text_noticias = (TextView)view.findViewById(R.id.custom_switcher_noticias);
        text_noticias.setTypeface(typeface);
        text_noticias.setTextColor(Color.parseColor("#FFA036"));
        text_noticias.setTextSize(22);
        text_noticias.setText("Lista de\r\nNoticias");

        TextView text_user = (TextView)view.findViewById(R.id.text_welcome);
        text_user.setTypeface(typeface2);
        text_user.setTextColor(Color.WHITE);
        text_user.setTextSize(20);

        TextView text_empresa = (TextView)view.findViewById(R.id.text_empresa);
        text_empresa.setTypeface(typeface);
        text_empresa.setTextColor(Color.parseColor("#FFA036"));
        text_empresa.setTextSize(22);
        text_empresa.setText("Directorio\r\nde Empresas");



        ImageView imagen_usuario = (ImageView)view.findViewById(R.id.image_user);

        Glide.with(getActivity())
                .load(WEBSERVER+"upload/"+image_user)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.empty_user)
                .transform(new CircleTransform(getActivity()))
                .override(80, 80)
                .into(imagen_usuario);

        String welcome = "Bienvenido a WONAP,\r\n"+nombre_completo.replace(",","\r\n");
        text_user.setText(welcome);
       /* rotatingTextWrapper = (RotatingTextWrapper) view.findViewById(R.id.custom_switcher);
        rotatingTextWrapper2 = (RotatingTextWrapper) view.findViewById(R.id.custom_switcher_noticias);
*/





        // Inflate the layout for this fragment
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
                            new GetClosestBanners(id_ciudad).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            }
        }

    }


    private class GetClosestBanners extends AsyncTask<Void, Void, List<w_banner>> {

        String mStrings;
        String id_ciudad;

        public GetClosestBanners(String id_ciudad){
            this.id_ciudad = id_ciudad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("getBannerMain","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final List<w_banner> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getBannerMain.php?id_ciudad="+id_ciudad);
                Log.v("getBannerMain",url.toString());
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
                Log.v("getBannerMain","doInBackground");
                try {
                    banner.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //getClosestPOS( , mLatestLocation, "", "", true);

                            //if(getPOS.size() > 0) {
                            //  LatLng lugar = new LatLng(Double.parseDouble(getPOS.get(0).pos_latitud), Double.parseDouble(getPOS.get(0).pos_longitud));
                            // Double distancia = Utils.formatDistanceBetweenMetros(mLatestLocation, lugar);
                            // if (distancia <= Integer.parseInt(getValueApp("GEOFENCES_DISTANCE")))
                            banner.add(new
                                    w_banner(obj.getString("id"), obj.getString("imagen_banner")));
                         }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return banner;
        }

        @Override
        protected void onPostExecute(List<w_banner> result) {
            super.onPostExecute(result);
            Log.v("getBannerMain","onPostExecute");

            //CONFIGURAMOS EL BANNER CENTRAL
            HashMap<String,String> url_maps = new HashMap<String, String>();

            for(w_banner imagen : result) {
                url_maps.put(imagen.getId(), WEBSERVER + "img_main/"+ imagen.getImagen());

            }

            //TEMPORAL
            url_maps.put("a", WEBSERVER + "img_main/2.jpg");
            url_maps.put("b", WEBSERVER + "img_main/3.jpg");
            url_maps.put("c", WEBSERVER + "img_main/4.jpg");
            url_maps.put("d", WEBSERVER + "img_main/5.jpg");
            url_maps.put("e", WEBSERVER + "img_main/6.jpg");
            url_maps.put("f", WEBSERVER + "img_main/4.jpg");
            url_maps.put("g", WEBSERVER + "img_main/5.jpg");
            url_maps.put("h", WEBSERVER + "img_main/6.jpg");
            url_maps.put("i", WEBSERVER + "img_main/2.jpg");

            for(String name : url_maps.keySet()){
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        //.description(name)
                        .image(url_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(Dashboard.this)           ;


                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra",name);


                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            //mDemoSlider.getRootView().findViewById(com.daimajia.slider.library.R.id.description_layout).setVisibility(View.INVISIBLE);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);


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
    public void onAttach(Context context) {
        super.onAttach(context);

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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

}

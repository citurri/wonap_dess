package com.develop.android.wonap.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
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
import com.develop.android.wonap.provider.RoundedCornersTransformation;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;
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

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getApplicationId;

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
    private JSONObject userFacebook;
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String EMAIL = "email";
    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, PICTURE, EMAIL});
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;

    private TextView connectedStateLabel;

    ImageView imagen_usuario;
    TextView text_user;
    //TextView login_button_facebook;

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

        imagen_usuario = (ImageView) view.findViewById(R.id.image_user);
        text_user = (TextView) view.findViewById(R.id.text_welcome);
        //login_button_facebook = (TextView) view.findViewById(R.id.login_button_facebook);
        //BOTON DE FACEBOOK SIN USO AUN SE OCULTA
        //login_button_facebook.setVisibility(View.GONE);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_ofertas);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Click Ofertas", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout layout_noticias = (LinearLayout) view.findViewById(R.id.layout_noticas);
        layout_noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Click Noticias", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CountryFilterActivity.class);
                intent.setAction("Noticias");
                startActivity(intent);
            }
        });
        LinearLayout layout_empresa = (LinearLayout) view.findViewById(R.id.layout_empresa);
        layout_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CountryFilterActivity.class);
                intent.setAction("Empresas");
                startActivity(intent);
            }
        });
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Reckoner_Bold.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");

        TextView text_ofertas = (TextView) view.findViewById(R.id.custom_switcher);
        text_ofertas.setTypeface(typeface);
        text_ofertas.setTextColor(Color.parseColor("#FFA036"));
        text_ofertas.setTextSize(22);
        text_ofertas.setText("Lista de\r\nOfertas");
        TextView text_noticias = (TextView) view.findViewById(R.id.custom_switcher_noticias);
        text_noticias.setTypeface(typeface);
        text_noticias.setTextColor(Color.parseColor("#FFA036"));
        text_noticias.setTextSize(22);
        text_noticias.setText("Lista de\r\nNoticias");
        TextView text_empresa = (TextView) view.findViewById(R.id.text_empresa);
        text_empresa.setTypeface(typeface);
        text_empresa.setTextColor(Color.parseColor("#FFA036"));
        text_empresa.setTextSize(22);
        text_empresa.setText("Directorio\r\nde Empresas");

        if (AccessToken.getCurrentAccessToken() != null) {
            //login_button_facebook.setVisibility(View.VISIBLE);
            fetchUserInfo();

        }
        else {
            //login_button_facebook.setVisibility(View.GONE);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            nombre_completo = preferences.getString("nombre_completo", "");
            image_user = preferences.getString("image_user", "");

            text_user.setTypeface(typeface2);
            text_user.setTextColor(Color.WHITE);
            text_user.setTextSize(20);

            Glide.with(getActivity())
                    .load(WEBSERVER + "upload/" + image_user)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_user)
                    .transform(new CircleTransform(getActivity()))
                    .override(80, 80)
                    .into(imagen_usuario);

            String welcome = "Bienvenido(a) a WONAP,\r\n" + nombre_completo.replace(",", "\r\n");
            text_user.setText(welcome);

        }
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
        if(mLatestLocation != null)
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

        if(AccessToken.getCurrentAccessToken() != null) {
            fetchUserInfo();
            //updateUI();
            AppEventsLogger.activateApp(getActivity());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(AccessToken.getCurrentAccessToken() != null)
        {
            AppEventsLogger.deactivateApp(getActivity());
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

    private void fetchUserInfo() {

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null) {
            if (Utils.isConn(getActivity())) {
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                userFacebook = me;
                                //Log.v("fetchUserInfo: ", me.toString());
                                updateUI();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString(FIELDS, REQUEST_FIELDS);
                request.setParameters(parameters);
                GraphRequest.executeBatchAsync(request);
            } else {
                userFacebook = null;
                //image_user.setVisibility(View.GONE);
                //profilePictureView.setProfileId(null);
                //connectedStateLabel.setText(getResources().getString(
                //R.string.usersettings_fragment_logged_in));
                Toast.makeText(getActivity(), "Compruebe su conexión a internet por favor.", Toast.LENGTH_SHORT).show();
            }
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("session_usuario", 0);
            editor.apply();
            userFacebook = null;
            Intent i = new Intent(getActivity(), LoginActivity.class);
            getActivity().finish();
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction("TO_MAIN_ACTIVITY");
            startActivity(i);
        }
    }

    private void updateUI() {

        if (AccessToken.getCurrentAccessToken() != null) {

/*            connectedStateLabel.setTextColor(getResources().getColor(
                    R.color.usersettings_fragment_connected_text_color));
            connectedStateLabel.setShadowLayer(1f, 0f, -1f,
                    getResources().getColor(
                            R.color.usersettings_fragment_connected_shadow_color));*/

            if (userFacebook != null && Utils.isConn(getActivity())) {
                //profilePictureView.setProfileId(userFacebook.optString("id"));
                //connectedStateLabel.setText("" + userFacebook.optString("name"));
                ImageRequest request = getImageRequest();
                Uri requestUri = request.getImageUri();
                Transformation transformation = new RoundedCornersTransformation(Glide.get(getApplicationContext()).getBitmapPool(), 100, 0);
                Glide.with(getActivity())
                        .load(requestUri)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.empty_user)
                        .override(50, 50)
                        .bitmapTransform(transformation)
                        .into(imagen_usuario);
                Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");
                text_user.setTypeface(typeface2);
                text_user.setTextColor(Color.WHITE);
                text_user.setTextSize(20);
                String welcome = "Bienvenido(a) a WONAP,\r\n"+userFacebook.optString("name");
                text_user.setText(welcome);
            }
        }
    }

    private ImageRequest getImageRequest() {
        ImageRequest request;
        ImageRequest.Builder requestBuilder = new ImageRequest.Builder(
                getApplicationContext(),
                ImageRequest.getProfilePictureUri(
                        userFacebook.optString("id"),
                        getResources().getDimensionPixelSize(
                                R.dimen.usersettings_fragment_profile_picture_width),
                        getResources().getDimensionPixelSize(
                                R.dimen.usersettings_fragment_profile_picture_height)));

        request = requestBuilder.setCallerTag(this)
                .setCallback(
                        new ImageRequest.Callback() {
                            @Override
                            public void onCompleted(ImageResponse response) {
                                processImageResponse(response);
                            }
                        })
                .build();
        return request;
    }

    private void processImageResponse(ImageResponse response) {
        if (response != null) {
            Bitmap bitmap = response.getBitmap();
            if (bitmap != null) {
                BitmapDrawable drawable = new BitmapDrawable(
                        getActivity().getResources(), bitmap);
                drawable.setBounds(0, 0,
                        getResources().getDimensionPixelSize(
                                R.dimen.usersettings_fragment_profile_picture_width),
                        getResources().getDimensionPixelSize(
                                R.dimen.usersettings_fragment_profile_picture_height));
                connectedStateLabel.setCompoundDrawables(null, drawable, null, null);
                connectedStateLabel.setTag(response.getRequest().getImageUri());
            }
        }
    }

}
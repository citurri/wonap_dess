package com.develop.android.wonap.SocialNetwork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.ParseJSON;
import com.develop.android.wonap.database.WonapDatabaseLocal;
import com.develop.android.wonap.service.SharedPrefManager;
import com.develop.android.wonap.service.UtilityService;
import com.develop.android.wonap.ui.LoginActivity;
import com.develop.android.wonap.ui.PrincipalActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class SplashFragment extends Fragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, EMAIL});

    String ciudad = "";
    String nombre_completo = "";
    Integer id_user = 0;
    String email_user;
    String password_user;
    String image_user;
    Boolean sucess = false;
    Boolean empresa = false;

    private Map<String, LatLng> CITY_LOCATIONS = new HashMap<String, LatLng>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash, container, false);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setVisibility(View.VISIBLE);
        loginButton.setReadPermissions("public_profile","email", "user_friends");
        loginButton.setFragment(this);
        loginButton.setText("Ingresar con Facebook");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    if (Utils.isConn(getActivity())) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        LatLng location = Utils.getLocation(getActivity());
                                        new GetCiudadCercana(location, getActivity(),me).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString(FIELDS, REQUEST_FIELDS);
                        request.setParameters(parameters);
                        GraphRequest.executeBatchAsync(request);
                    }

                }


            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Se canceló el inicio de sesión.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(), "Ocurrió un error la intentar iniciar sesión.", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private class GetCiudadCercana extends AsyncTask<Void, Void, Map<String, LatLng>> {

        String mStrings;
        LatLng mLatestLocation;
        Context context;
        JSONObject me;

        public GetCiudadCercana(LatLng mLatestLocation,Context context, JSONObject me){
            this.mLatestLocation = mLatestLocation;
            this.context = context;
            this.me = me;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("CiudadCercana_Location","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final Map<String, LatLng> doInBackground(Void... arg0) {

            mStrings = "[]";
            String WEBSERVER = context.getString(R.string.web_server);
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getCiudades.php");
                Log.v("CiudadCercana_Location",url.toString());
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
                Log.v("CiudadCercana_Location","doInBackground");
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

            String id_ciudad = loadIdCiudadCercana(mLatestLocation);
            new getFacebookData(me, context, id_ciudad).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

       }

    }

    private String loadIdCiudadCercana(LatLng mLatestLocation) {
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

    private class getFacebookData extends AsyncTask<Void, Void, Void> {


        String mStrings;
        JSONObject me;
        Context context;
        String ciudad;

        public getFacebookData(JSONObject me, Context context, String ciudad) {
            this.me = me;
            this.context = context;
            this.ciudad = ciudad;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            mStrings = "[]";
            String name = URLEncoder.encode(me.optString("name"));

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(getString(R.string.web_server)+"api/insertUserFacebook.php?id_facebook="+me.optString("id")+"&ciudad="+ciudad+"&nombre="+ name +"&apellidos=&email="+me.optString("email")+"");
                Log.v("InsertFacebook",url.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(15000);
                con.setReadTimeout(15000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                mStrings = sb.toString().trim();
                Log.v("JSON",mStrings);
                try {
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            id_user = obj.getInt("id_user");
                            ciudad = obj.getString("ciudad");
                            nombre_completo = obj.getString("nombre");
                            empresa = obj.getBoolean("empresa");
                            image_user = obj.getString("image_user");
                        }
                        sucess = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("id_usuario", id_user);
            editor.putString("ciudad", ciudad);
            editor.putString("nombre_completo", nombre_completo);
            editor.putString("image_user", image_user);

          //Guardamos la sessión del usuario
            if(empresa) editor.putInt("session_usuario", 12345);
            else
                editor.putInt("session_usuario", 15879);
            editor.apply();

            Intent i = new Intent(context, PrincipalActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction("FROM_MAIN_ACTIVITY");
            startActivity(i);

            /*//GRABAR EL TOKEN DE FMC
            final String token = SharedPrefManager.getInstance(context).getDeviceToken();
            Log.v("Token", token);

            if(!token.equals("")) {
                String WEBSERVER = context.getString(R.string.web_server);

                HashMap<String, String> postdata = new HashMap<String, String>();
                postdata.put("id_user", id_user.toString());
                postdata.put("token", token);


                PostResponseAsyncTask task = new PostResponseAsyncTask(context, postdata, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        switch (s) {
                            case "Token Guardado":
                                Log.v("Token", s);
                                break;
                            case "Error":
                                Log.v("Token", s);
                                break;
                            default:
                                Log.v("Token", s);
                                break;
                        }
                    }
                });


                task.execute(WEBSERVER + "api/RegisterDevice.php");
                task.setEachExceptionsHandler(new EachExceptionsHandler() {
                    @Override
                    public void handleIOException(IOException e) {
                        Log.v("Token: ", "No se puede conectar al servidor.");
                    }

                    @Override
                    public void handleMalformedURLException(MalformedURLException e) {
                        Log.v("Token: ", "Error de URL.");
                    }

                    @Override
                    public void handleProtocolException(ProtocolException e) {
                        Log.v("Token: ", "Error de Protocolo.");
                    }

                    @Override
                    public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                        Log.v("Token: ", "Error de codificación.");
                    }
                });

            }*/

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //UtilityService.requestLocation(activity);
    }
}


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

package com.develop.android.wonap.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.location.GeofencingRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.database.WonapDatabaseLocal;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.provider.TouristAttractions;
import com.develop.android.wonap.ui.DetailActivity;
import com.develop.android.wonap.ui.PrincipalActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.Wearable;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static com.google.android.gms.location.LocationServices.GeofencingApi;

/**
 * A utility IntentService, used for a variety of asynchronous background
 * operations that do not necessarily need to be tied to a UI.
 */
public class UtilityService extends IntentService {
    private static final String TAG = UtilityService.class.getSimpleName();

    public static final String ACTION_GEOFENCE_TRIGGERED = "geofence_triggered";
    private static final String ACTION_LOCATION_UPDATED = "location_updated";
    private static final String ACTION_REQUEST_LOCATION = "request_location";
    private static final String ACTION_ADD_GEOFENCES = "add_geofences";
    private static final String ACTION_CLEAR_NOTIFICATION = "clear_notification";
    private static final String ACTION_CLEAR_REMOTE_NOTIFICATIONS = "clear_remote_notifications";
    private static final String ACTION_FAKE_UPDATE = "fake_update";
    private static final String EXTRA_TEST_MICROAPP = "test_microapp";
    private List<OfertaModel> attractions_list  = new LinkedList<OfertaModel>();;
    private static final int TRIGGER_TRANSITION = Geofence.GEOFENCE_TRANSITION_ENTER |
            Geofence.GEOFENCE_TRANSITION_EXIT;
    private static final long EXPIRATION_DURATION = Geofence.NEVER_EXPIRE;

    private Map<String, LatLng> CITY_LOCATIONS = new HashMap<String, LatLng>();

    int id_user = 0;

    SharedPreferences.Editor editor;

    public static IntentFilter getLocationUpdatedIntentFilter() {
        return new IntentFilter(UtilityService.ACTION_LOCATION_UPDATED);
    }

    public static void triggerWearTest(Context context, boolean microApp) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_FAKE_UPDATE);
        intent.putExtra(EXTRA_TEST_MICROAPP, microApp);
        context.startService(intent);
    }

    public static void addGeofences(Context context, List<OfertaModel> attractions) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_ADD_GEOFENCES);
        intent.putExtra("attractions", (Serializable) attractions);
        context.startService(intent);
    }

    public static void requestLocation(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_REQUEST_LOCATION);
        context.startService(intent);
    }

    public static void clearNotification(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_CLEAR_NOTIFICATION);
        context.startService(intent);
    }

    public static Intent getClearRemoteNotificationsIntent(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_CLEAR_REMOTE_NOTIFICATIONS);
        return intent;
    }

    public UtilityService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_ADD_GEOFENCES.equals(action)) {
            attractions_list = (List<OfertaModel>) intent.getSerializableExtra("attractions");
            addGeofencesInternal();
        } else if (ACTION_GEOFENCE_TRIGGERED.equals(action)) {
            geofenceTriggered(intent);
        } else if (ACTION_REQUEST_LOCATION.equals(action)) {
            requestLocationInternal();
        } else if (ACTION_LOCATION_UPDATED.equals(action)) {
            locationUpdated(intent);
        } else if (ACTION_CLEAR_NOTIFICATION.equals(action)) {
            clearNotificationInternal();
        } else if (ACTION_CLEAR_REMOTE_NOTIFICATIONS.equals(action)) {
            clearRemoteNotifications();
        } else if (ACTION_FAKE_UPDATE.equals(action)) {
           LatLng currentLocation = Utils.getLocation(this);

            // If location unknown use test city, otherwise use closest city
            String city = currentLocation == null ? TouristAttractions.TEST_CITY :
                    TouristAttractions.getClosestCity(currentLocation);

            showNotification(city,
                    intent.getBooleanExtra(EXTRA_TEST_MICROAPP, Constants.USE_MICRO_APP));
        }
    }

    /**
     * Add geofences using Play Services
     */
    private void addGeofencesInternal() {
        Log.v(TAG, ACTION_ADD_GEOFENCES);
        if(Utils.doesDatabaseExist(this, "WonapDatabaseLocal")) {

            Float TRIGGER_RADIUS = Float.parseFloat(new WonapDatabaseLocal(this).getValueApp("TRIGGER_RADIUS"));

            if (!Utils.checkFineLocationPermission(this)) {
                return;
            }

            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();

            List<Geofence> geofenceList = new ArrayList<Geofence>();
            int i = 0;
            for (OfertaModel oferta : attractions_list) {
                geofenceList.add(new Geofence.Builder()
                        .setCircularRegion(Double.parseDouble(oferta.getPosLatitud()), Double.parseDouble(oferta.getPosLongitud()), TRIGGER_RADIUS)
                        .setRequestId(String.valueOf(i) + "," + oferta.getId())
                        .setTransitionTypes(TRIGGER_TRANSITION)
                        .setExpirationDuration(EXPIRATION_DURATION)
                        .build());
                i++;
                if (i > 90) break; //SE LLENAN 90 GEOFENCES COMO MAXIMO DEBIDO A LA LIMITANTE DE 100
            }

            // It's OK to use blockingConnect() here as we are running in an
            // IntentService that executes work on a separate (background) thread.
            ConnectionResult connectionResult = googleApiClient.blockingConnect(
                    Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

            GeofencingRequest.Builder geofenceRequest = new GeofencingRequest.Builder();

            // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
            // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
            // is already inside that geofence.
            //geofenceRequest.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);}

            //No queremos que nos dispare cada vez que se crean los geofences, sino cuando haya alguna actividad de ingreso
            geofenceRequest.setInitialTrigger(0);

            // Add the geofences to be monitored by geofencing service.
            geofenceRequest.addGeofences(geofenceList);


            if (connectionResult.isSuccess() && googleApiClient.isConnected()) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, 0, new Intent(this, UtilityReceiver.class), 0);
                //Eliminamos los geofences creados
                GeofencingApi.removeGeofences(googleApiClient,pendingIntent);
                //Añadimos los nuevos
                GeofencingApi.addGeofences(googleApiClient,
                        geofenceRequest.build(), pendingIntent);
                googleApiClient.disconnect();
            } else {
                Log.e(TAG, String.format(Constants.GOOGLE_API_CLIENT_ERROR_MSG,
                        connectionResult.getErrorCode()));
            }
        }
    }

    /**
     * Called when a geofence is triggered
     */
    private void geofenceTriggered(Intent intent) {
        Log.v(TAG, ACTION_GEOFENCE_TRIGGERED);

        // Check if geofences are enabled
        boolean geofenceEnabled = Utils.getGeofenceEnabled(this);

        // Extract the geofences from the intent
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofences = event.getTriggeringGeofences();

        if (geofenceEnabled && geofences != null && geofences.size() > 0) {
            if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER & event.getGeofenceTransition() != Geofence.GEOFENCE_TRANSITION_DWELL) {
                // Trigger the notification based on the first geofence
                if(geofences.size() > 1)
                    showNotificationMany(geofences);
                else
                    showNotification(geofences.get(0).getRequestId().split(",")[1], Constants.USE_MICRO_APP);
            } else if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT) {
                // Clear notifications
                clearNotificationInternal();
                clearRemoteNotifications();
            }
        }
        UtilityReceiver.completeWakefulIntent(intent);
    }

    /**
     * Called when a location update is requested
     */
    private void requestLocationInternal() {
        Log.v(TAG, ACTION_REQUEST_LOCATION);

        if (!Utils.checkFineLocationPermission(this)) {
            return;
        }

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        // It's OK to use blockingConnect() here as we are running in an
        // IntentService that executes work on a separate (background) thread.
        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {

            Intent locationUpdatedIntent = new Intent(this, UtilityService.class);
            locationUpdatedIntent.setAction(ACTION_LOCATION_UPDATED);

            // Send last known location out first if available
            Location location = FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                Intent lastLocationIntent = new Intent(locationUpdatedIntent);
                lastLocationIntent.putExtra(
                        FusedLocationProviderApi.KEY_LOCATION_CHANGED, location);
                startService(lastLocationIntent);
            }

            // Request new location
            String priority = new WonapDatabaseLocal(this).getValueApp("LOCATION_PRIORITY");
            int prior = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY; // Valor por defecto
            if(priority.equals("PRIORITY_HIGH_ACCURACY")) {
                prior = LocationRequest.PRIORITY_HIGH_ACCURACY;
            }
            String interval_wonap = new WonapDatabaseLocal(this).getValueApp("LOCATION_INTERVAL");
            Long interval = (long) 300000; //5 minutos
            if (!interval_wonap.equals(""))  interval = Long.parseLong(interval_wonap);

            LocationRequest mLocationRequest = new LocationRequest()
                    .setPriority(prior).setInterval(interval);
            FusedLocationApi.requestLocationUpdates(
                    googleApiClient, mLocationRequest,
                    PendingIntent.getService(this, 0, locationUpdatedIntent, 0));

            googleApiClient.disconnect();
        } else {
            Log.e(TAG, String.format(Constants.GOOGLE_API_CLIENT_ERROR_MSG,
                    connectionResult.getErrorCode()));
        }
    }

    /**
     * Called when the location has been updated
     */
    private void locationUpdated(Intent intent) {
        Log.v(TAG, ACTION_LOCATION_UPDATED);

        // Extra new location
        Location location =
                intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if (location != null) {
            LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());

            // Store in a local preference as well
            Utils.storeLocation(this, latLngLocation);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = preferences.edit();
            id_user = preferences.getInt("id_usuario", 0);

            // Solo si esta logueado
            if (Utils.isConn(getApplicationContext()) & id_user > 0) {
                new GetCiudadCercana(latLngLocation).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
                // Send a local broadcast so if an Activity is open it can respond
            // to the updated location

        }
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
            Log.v("CiudadCercana_Location","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final Map<String, LatLng> doInBackground(Void... arg0) {

            mStrings = "[]";
            String WEBSERVER = getApplicationContext().getString(R.string.web_server);
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
            if(id_ciudad != null) {
                Log.v("CiudadCercana_Location", id_ciudad);
                if (Utils.isConn(getApplicationContext()))
                new GetClosestOffers(mLatestLocation, id_ciudad).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
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
            Log.v("ClosestOffers_location","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final List<OfertaModel> doInBackground(Void... arg0) {

            mStrings = "[]";
            String WEBSERVER = getApplicationContext().getString(R.string.web_server);


            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getAnunciosMasCercano.php?id_ciudad="+id_ciudad+"&id_user="+id_user);
                Log.v("ClosestOffers_location",url.toString());
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
                Log.v("ClosestOffers_location","doInBackground");
                try {
                    attractions_list.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //getClosestPOS( , mLatestLocation, "", "", true);

                            //if(getPOS.size() > 0) {
                             LatLng lugar = new LatLng(Double.parseDouble( obj.getString("pos_latitud")), Double.parseDouble(obj.getString("pos_longitud")));
                             Double distancia = Utils.formatDistanceBetweenMetros(mLatestLocation, lugar);
                             if (distancia <= Integer.parseInt(new WonapDatabaseLocal(getApplicationContext()).getValueApp("GEOFENCES_DISTANCE")))
                             attractions_list.add(new
                                    OfertaModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_oferta"),obj.getBoolean("es_cupon"),obj.getString("fecha_inicio"),obj.getString("fecha_fin"), obj.getString("denominacion"), obj.getString("pos_latitud"), obj.getString("pos_longitud"),obj.getString("pos_map_address"),  obj.getString("pos_map_city") ,obj.getString("pos_map_country"), obj.getString("distancia_user"), obj.getString("cupones_habilitados"), obj.getString("cupones_redimidos"), obj.getBoolean("cupon_permitido"), obj.getString("dias_restantes"), obj.getBoolean("es_favorito"),obj.getString("secundarias_oferta")));

                            editor.putString("ID_CIUDAD", id_ciudad);
                            editor.putString("LOCALITY", obj.getString("pos_map_city"));
                            editor.putString("COUNTRY", obj.getString("pos_map_country"));

                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }
            editor.apply();
            return attractions_list;
        }

        @Override
        protected void onPostExecute(List<OfertaModel> result) {
            super.onPostExecute(result);
            Log.v("ClosestOffers_location","onPostExecute");
            if (mLatestLocation != null) {
                if (attractions_list.size() > 0) {
                    Collections.sort(attractions_list,
                            new Comparator<OfertaModel>() {
                                @Override
                                public int compare(OfertaModel lhs, OfertaModel rhs) {
                                    double lhsDistance = SphericalUtil.computeDistanceBetween(
                                            new LatLng(Double.parseDouble(lhs.getPosLatitud()), Double.parseDouble(lhs.getPosLongitud())), mLatestLocation);
                                    double rhsDistance = SphericalUtil.computeDistanceBetween(
                                            new LatLng(Double.parseDouble(rhs.getPosLatitud()), Double.parseDouble(rhs.getPosLongitud())), mLatestLocation);
                                    return (int) (lhsDistance - rhsDistance);
                                }
                            }
                    );


                    addGeofences(getApplicationContext(), attractions_list);
                }
            }


        }

    }




    /**
     * Clears the local device notification
     */
    private void clearNotificationInternal() {
        Log.v(TAG, ACTION_CLEAR_NOTIFICATION);
        NotificationManagerCompat.from(this).cancel(Constants.MOBILE_NOTIFICATION_ID);
    }

    /**
     * Clears remote device notifications using the Wearable message API
     */
    private void clearRemoteNotifications() {
        Log.v(TAG, ACTION_CLEAR_REMOTE_NOTIFICATIONS);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        // It's OK to use blockingConnect() here as we are running in an
        // IntentService that executes work on a separate (background) thread.
        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {

            // Loop through all nodes and send a clear notification message
            Iterator<String> itr = Utils.getNodes(googleApiClient).iterator();
            while (itr.hasNext()) {
                Wearable.MessageApi.sendMessage(
                        googleApiClient, itr.next(), Constants.CLEAR_NOTIFICATIONS_PATH, null);
            }
            googleApiClient.disconnect();
        }
    }

    private class GetOferta extends AsyncTask<Void, Void, OfertaModel> {

        String mStrings;
        String id_oferta;
        String WEBSERVER;

        public GetOferta(String id_oferta){
            this.id_oferta = id_oferta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetOferta","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final OfertaModel doInBackground(Void... arg0) {
            OfertaModel oferta = null;
            mStrings = "[]";
            WEBSERVER = getApplicationContext().getString(R.string.web_server);
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getOferta.php?id_oferta="+id_oferta+"&id_user=0");
                Log.v("GetOferta",url.toString());
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
                Log.v("GetOferta","doInBackground");
                try {
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            //getClosestPOS( , mLatestLocation, "", "", true);

                            //if(getPOS.size() > 0) {
                            //  LatLng lugar = new LatLng(Double.parseDouble(getPOS.get(0).pos_latitud), Double.parseDouble(getPOS.get(0).pos_longitud));
                            // Double distancia = Utils.formatDistanceBetweenMetros(mLatestLocation, lugar);
                            // if (distancia <= Integer.parseInt(getValueApp("GEOFENCES_DISTANCE")))
                           oferta = new
                                    OfertaModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_oferta"),obj.getBoolean("es_cupon"),obj.getString("fecha_inicio"),obj.getString("fecha_fin"), obj.getString("denominacion"), obj.getString("pos_latitud"), obj.getString("pos_longitud"),obj.getString("pos_map_address"),  obj.getString("pos_map_city") ,obj.getString("pos_map_country"), obj.getString("distancia_user"), obj.getString("cupones_habilitados"), obj.getString("cupones_redimidos"), obj.getBoolean("cupon_permitido"),obj.getString("dias_restantes"), obj.getBoolean("es_favorito"),obj.getString("secundarias_oferta"));
                        }



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return oferta;
        }

        @Override
        protected void onPostExecute(final OfertaModel result) {
            super.onPostExecute(result);

            Log.v("GetOferta","onPostExecute");

            new AsyncTask<Void, Void, Void>() {
                final HashMap<String, Bitmap> bitmaps = new HashMap<>();
                @Override
                protected Void doInBackground(Void... params) {

                    try {
                         bitmaps.put(result.getTitulo(),
                                            Glide.with(UtilityService.this)
                                                    .load(WEBSERVER+"upload/"+result.getImagenOferta())
                                                    .asBitmap()
                                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                    .into(Constants.WEAR_IMAGE_SIZE, Constants.WEAR_IMAGE_SIZE)
                                                    .get());
                    } catch (final InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(Void dummy) {
                    if (null != bitmaps) {
                        // The intent to trigger when the notification is tapped
                        PendingIntent pendingIntent = PendingIntent.getActivity(UtilityService.this, 0,
                                DetailActivity.getLaunchIntent(UtilityService.this, result.getId(), "0"),
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        // The intent to trigger when the notification is dismissed, in this case
                        // we want to clear remote notifications as well
                        PendingIntent deletePendingIntent =
                                PendingIntent.getService(UtilityService.this, 0, getClearRemoteNotificationsIntent(UtilityService.this), 0);

                        // Construct the main notification
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(UtilityService.this)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bitmaps.get(result.getTitulo()))
                                        .setBigContentTitle(result.getTitulo())
                                        .setSummaryText(result.getDescripcion())
                                )
                                .setContentTitle(result.getTitulo())
                                .setContentText(result.getDescripcion())
                                .setSmallIcon(R.drawable.ic_wonap)

                                .setContentIntent(pendingIntent)
                                .setDeleteIntent(deletePendingIntent)
                                //.setColor(getResources().getColor(R.color.colorPrimary, getTheme()))
                                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true);

                        // Trigger the notification
                        NotificationManagerCompat.from(UtilityService.this).notify(
                                Constants.MOBILE_NOTIFICATION_ID, builder.build());
                        Log.d(TAG, "Image loaded");
                    };
                }
            }.execute();
        }

    }
    /**
     * Show the notification. Either the regular notification with wearable features
     * added to enhance, or trigger the full micro app on the wearable.
     *
     * @param id_oferta The city to trigger the notification for
     * @param microApp If the micro app should be triggered or just enhanced notifications
     */
    private void showNotification(String id_oferta, boolean microApp) {
        if (Utils.isConn(getApplicationContext()))
        new GetOferta(id_oferta).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showNotificationMany(List<Geofence> geofences) {
        //Log.v(TAG, "Mostrando notificaciones de varios lugares cercanos");

        long[] vibrar = null;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tono = sharedPreferences.getString("tono_list", "none");
        Boolean vibracion = sharedPreferences.getBoolean("vibracion", false);
        if(vibracion) vibrar =  new long[]{1000, 1000};
        //if (tono.equals("none")) tono = Settings.System.DEFAULT_NOTIFICATION_URI.toString();

        HashMap<String, Bitmap> bitmaps = new HashMap<>();
        try {
            for (int i = 0; i < 1; i++) {
                bitmaps.put("Wonap",
                        Glide.with(this)
                                .load(new WonapDatabaseLocal(this).getValueApp("DEFAULT_IMAGE_NOTIFICATION_MANY"))
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(Constants.WEAR_IMAGE_SIZE, Constants.WEAR_IMAGE_SIZE)
                                .get());
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error fetching image from network: " + e);
        }

        // The intent to trigger when the notification is tapped
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.setAction("ABRIR_CERCANOS");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // The intent to trigger when the notification is dismissed, in this case
        // we want to clear remote notifications as well
        PendingIntent deletePendingIntent =
                PendingIntent.getService(this, 0, getClearRemoteNotificationsIntent(this), 0);

        // Construct the main notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmaps.get("Wonap"))
                        .setBigContentTitle("Wonap")
                        .setSummaryText("Existen " + geofences.size() + " anuncios cercanos a usted!!!!!.")
                )
                .setContentTitle("Wonap")
                .setContentText("Existen " + geofences.size() + " anuncios cercanos a usted!!!!!!.")
                .setSmallIcon(R.drawable.ic_wonap)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setColor(getResources().getColor(R.color.color_primary))
                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

        // Trigger the notification
        NotificationManagerCompat.from(this).notify(
                Constants.MOBILE_NOTIFICATION_ID, builder.build());




    }
}

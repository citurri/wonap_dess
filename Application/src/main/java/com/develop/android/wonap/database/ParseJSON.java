package com.develop.android.wonap.database;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.android.wonap.R;
import com.develop.android.wonap.service.UtilityService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParseJSON extends AsyncTask<String, Void, String[]> {
    private ProgressDialog loading;
    private Activity actividad;
    private ProgressBar progressBar;
    private Button button;
    private String[] mStrings = new String[15];
    private Boolean db_existe = true;
    private String query;
    private Boolean con_progress = false;

    private Boolean geofences = false;
    private String WEB_SERVER = "";

    public ParseJSON() {
    }

    //constructor para recibir el query de main
    public ParseJSON(Activity actvidad, Boolean db_existe, String query,Boolean con_progress, Boolean geofences) {
        this.actividad = actvidad;
        this.db_existe = db_existe;
        this.query = query;
        this.con_progress = con_progress;
        this.geofences = geofences;
    }
          @Override
            protected void onPreExecute() {
                super.onPreExecute();
              WEB_SERVER = actividad.getString(R.string.web_server)+"api/";
              if (con_progress) {
                  if (!db_existe) {
                      loading = ProgressDialog.show(actividad, "Iniciando por primera vez, espere por favor...", null, true, true);
                      loading.setCancelable(false);
                      loading.setCanceledOnTouchOutside(false);
                  } else {
                      progressBar.setVisibility(ProgressBar.VISIBLE);
                      button.setVisibility(Button.GONE);
                      WEB_SERVER = new WonapDatabaseLocal(actividad).getValueApp("WEB_SERVER");
                  }
              }
          }

            @Override
            protected String[] doInBackground(String... params) {

                    for(int i=0; i<params.length; i++) {

                    if (isCancelled()) break;

                    mStrings[i] = "[]";

                    String uri =  WEB_SERVER + params[i];

                        Log.v("JSON: ",uri);

                        BufferedReader bufferedReader = null;
                        try {
                            URL url = new URL(uri);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setConnectTimeout(15000);
                            con.setReadTimeout(15000);


                            StringBuilder sb = new StringBuilder();

                            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));

                            String json;
                            while ((json = bufferedReader.readLine()) != null) {
                                sb.append(json + "\n");
                            }

                            mStrings[i] = sb.toString().trim();

                            //Log.v("JSON", "Loop:" + uri);
                        } catch (Exception e) {
                            Log.v("JSON",e.toString());
                        }
                    }

                return mStrings;
            }

    @Override
            protected void onPostExecute(String[] s) {
                super.onPostExecute(s);

                Log.v("JSON", "cadena devuelta 1: " + s[0]);

                if (query.equals("Consulta")) {
                   new WonapDatabaseLocal(actividad).addUpdateAll(s);
              }

            if(geofences) {
                    //Eliminamos previos geofences para actualizar la lista correctamente
                   // UtilityService.removeGeofences(actividad);

                    //Creamos geofences actualizados
                    //Log.v("GEOFENCES_VECES: ",s[0]);
                   // UtilityService.addGeofences(actividad);

                }
                if (con_progress) {
                    if (db_existe) {
                        progressBar.setVisibility(ProgressBar.GONE);
                        button.setVisibility(Button.VISIBLE);
                    } else loading.dismiss();
                }
   }
}


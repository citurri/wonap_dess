package com.develop.android.wonap.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.develop.android.wonap.R;
import com.develop.android.wonap.test.DetailActivity;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Random;

public class GCMMessageHandler extends GcmListenerService {
    public static int MESSAGE_NOTIFICATION_ID;
    String[] str_result = null;

    String tipo = "";
    PendingIntent pendingIntent = null;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("GcmListenerService", "Mensaje Recibido.");

        if(str_result != null) //SE ESPERA HASTA QUE TERMINE EL ASYNCTASK
        {
            String id = data.getString("id");
            String title = data.getString("title");
            String message = data.getString("message");
            tipo = data.getString("notificacion");
            Boolean notificacion = true;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (tipo.equals("noticia"))
                notificacion = sharedPreferences.getBoolean("noticias", true);

            if (tipo.equals("anuncio"))
                notificacion = sharedPreferences.getBoolean("anuncios", true);


            if (notificacion) createNotification(id, title, message);
        }

    }

    // Creates notification based on title and body received
    private void createNotification(String id, String title, String body) {
        Context context = getBaseContext();

        Random random = new Random();
        MESSAGE_NOTIFICATION_ID = random.nextInt(9999 - 1000) + 1000;

        long[] vibrar = null;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tono = sharedPreferences.getString("tono_list", "none");
        Boolean vibracion = sharedPreferences.getBoolean("vibracion", false);
        if(vibracion) vibrar =  new long[]{1000, 1000};
        if(tipo.equals("anuncio")) {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    DetailActivity.getLaunchIntent(this, id),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }else
        {
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    new Intent(), // add this
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentTitle("WONAP: " + title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_stat_maps_pin_drop)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(vibrar)
                .setSound(Uri.parse(tono));

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

}

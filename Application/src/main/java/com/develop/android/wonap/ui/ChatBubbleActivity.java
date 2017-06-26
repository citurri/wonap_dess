package com.develop.android.wonap.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.provider.ChatArrayAdapter;
import com.develop.android.wonap.provider.GalleryActivity;
import com.develop.android.wonap.w_moferta_comentarios;
import com.google.android.gms.maps.model.LatLng;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatBubbleActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private static ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    String id_oferta ="";
    int id_usuario;
    Integer sesion_usuario;
    String titulo = "";

    static List<w_moferta_comentarios> comentarios = new ArrayList<>();

    Intent intent;
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bubble);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id_oferta = intent.getStringExtra("id");
        titulo = intent.getStringExtra("titulo");
        chatArrayAdapter = new ChatArrayAdapter(this, R.layout.activity_chat_singlemessage);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario = preferences.getInt("id_usuario", 0);
        sesion_usuario = preferences.getInt("session_usuario", 0);

        getComentarios();

        buttonSend = (Button) findViewById(R.id.buttonSend);
        listView = (ListView) findViewById(R.id.listView1);
        chatText = (EditText) findViewById(R.id.chatText);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if(Utils.isConn(ChatBubbleActivity.this)){
                    if (!chatText.getText().toString().equals("")) {
                        sendChatMessage();
                        getComentarios();
                        chatArrayAdapter.notifyDataSetChanged();
                        listView.setAdapter(chatArrayAdapter);
                    }else
                        Toast.makeText(getApplicationContext(), "Introduzca un mensaje por favor.", Toast.LENGTH_SHORT).show();
                }
                else {

                    Toast.makeText(getApplicationContext(), "Por favor, conéctese a internet....", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(0);
            }
        });

     }

    public void getComentarios()
    {
        new GetComentarios(id_oferta).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private boolean sendChatMessage(){


        chatArrayAdapter.addNew(this, id_oferta, String.valueOf(id_usuario), chatText.getText().toString());
        chatText.setText("");
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_chat_bubble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetComentarios extends AsyncTask<Void, Void, List<w_moferta_comentarios>> {

        String mStrings;
        String id_oferta;
        String WEBSERVER;
        ProgressDialog loading;

        public GetComentarios(String id_oferta) {
            this.id_oferta = id_oferta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetComentarios", "onPreExecute");
            loading = ProgressDialog.show(ChatBubbleActivity.this, "Recuperando comentarios, espere por favor...", null, true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @SafeVarargs
        @Override
        protected final List<w_moferta_comentarios> doInBackground(Void... arg0) {

            mStrings = "[]";

            WEBSERVER = getResources().getString(R.string.web_server);
            LatLng location = Utils.getLocation(ChatBubbleActivity.this);
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER + "api/getComentariosOferta.php?id_oferta=" + id_oferta);
                Log.v("GetComentarios", url.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.setConnectTimeout(15000);
                //con.setReadTimeout(15000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json).append("\n");
                }
                mStrings = sb.toString().trim();
                Log.v("GetComentarios", "doInBackground");

                try {
                    comentarios.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            comentarios.add(new
                                    w_moferta_comentarios(obj.getString("id"), obj.getString("id_oferta"), obj.getString("id_remitente"), obj.getString("comentario"), obj.getString("fecha_comentario"), obj.getString("id_empresa"), obj.getString("imagen_user"), obj.getString("imagen_empresa"), obj.getString("tipo_remitente"), obj.getString("nombre_user"), obj.getString("nombre_empresa")));
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON", e.toString());

            }

            return comentarios;
        }

        @Override
        protected void onPostExecute(final List<w_moferta_comentarios> result) {
            super.onPostExecute(result);
            Log.v("GetComentarios", "onPostExecute");
            chatArrayAdapter.clear();
            chatArrayAdapter.notifyDataSetChanged();
            //Limpiamos los datos cargados previamente
            chatArrayAdapter.chatMessageList.clear();
            for (w_moferta_comentarios comentario : comentarios) {
                chatArrayAdapter.add(comentario);
            }
            loading.dismiss();


        }
    }

}

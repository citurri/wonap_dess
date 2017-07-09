package com.develop.android.wonap.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.provider.ChatArrayAdapter;
import com.develop.android.wonap.database.w_moferta_comentarios;
import com.develop.android.wonap.provider.SendCommentButton;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatBubbleActivity extends AppCompatActivity implements SendCommentButton.OnSendClickListener  {
    private static final String TAG = "ChatActivity";
    private static ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    String id_oferta ="";
    int id_usuario;
    Integer sesion_usuario;
    String titulo = "";
    static List<w_moferta_comentarios> comentarios = new ArrayList<>();
    Intent intent;
    private boolean side = false;

    //PARA IMPLEMENTAR ANIMACION
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    private int drawingStartLocation;
    @BindView(R.id.contentRoot) LinearLayout contentRoot;
    @BindView(R.id.llAddComment)  LinearLayout llAddComment;
    @BindView(R.id.etComment)
    EditText chatText;
    @BindView(R.id.btnSendComment)
    SendCommentButton buttonSend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bubble);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setupSendCommentButton();



        Intent intent = getIntent();
        drawingStartLocation = intent.getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }


        id_oferta = intent.getStringExtra("id");
        titulo = intent.getStringExtra("titulo");
        chatArrayAdapter = new ChatArrayAdapter(this, R.layout.activity_chat_singlemessage);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario = preferences.getInt("id_usuario", 0);
        sesion_usuario = preferences.getInt("session_usuario", 0);

        getComentarios();

        //buttonSend = (Button) findViewById(R.id.btnSendComment);
        listView = (ListView) findViewById(R.id.listView1);
        //chatText = (EditText) findViewById(R.id.etComment);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //listView.setSelection(0);
            }
        });

     }

    private void setupSendCommentButton() {
        buttonSend.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        //ViewCompat.setElevation(getSupportActionBar().getCustomView(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //ViewCompat.setElevation(getSupportActionBar().getCustomView(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    @Override
    public void onBackPressed() {
        //ViewCompat.setElevation(getSupportActionBar().getCustomView(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ChatBubbleActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    private void animateContent() {
        //commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
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

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            if(Utils.isConn(ChatBubbleActivity.this)){
                if (!chatText.getText().toString().equals("")) {
                    sendChatMessage();
                    getComentarios();
                    chatArrayAdapter.notifyDataSetChanged();
                    listView.setAdapter(chatArrayAdapter);
                    chatText.setText(null);
                    buttonSend.setCurrentState(SendCommentButton.STATE_DONE);
                }else
                    Toast.makeText(getApplicationContext(), "Introduzca un mensaje por favor.", Toast.LENGTH_SHORT).show();
            }
            else {

                Toast.makeText(getApplicationContext(), "Por favor, conéctese a internet....", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private boolean validateComment() {
        if (TextUtils.isEmpty(chatText.getText())) {
            buttonSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }



    public class GetComentarios extends AsyncTask<Void, Void, List<w_moferta_comentarios>> {

        String mStrings;
        String id_oferta;
        String WEBSERVER;
        //ProgressDialog loading;

        public GetComentarios(String id_oferta) {
            this.id_oferta = id_oferta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetComentarios", "onPreExecute");
            //loading = ProgressDialog.show(ChatBubbleActivity.this, "Recuperando comentarios, espere por favor...", null, true, true);
            //loading.setCancelable(false);
            //loading.setCanceledOnTouchOutside(false);
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
            //loading.dismiss();


        }
    }

}

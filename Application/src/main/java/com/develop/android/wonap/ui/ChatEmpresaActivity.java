package com.develop.android.wonap.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.w_comentarios_empresa;
import com.develop.android.wonap.provider.ChatEmpresaAdapter;
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

public class ChatEmpresaActivity extends AppCompatActivity implements SendCommentButton.OnSendClickListener  {
    private static final String TAG = "ChatActivity";
    private static ChatEmpresaAdapter ChatEmpresaAdapter;
    private ListView listView;
    String id = "0";
    String id_empresa ="";
    int id_usuario;
    Integer sesion_usuario;
    String titulo = "";
    static List<w_comentarios_empresa> comentarios = new ArrayList<>();
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


        id_empresa = intent.getStringExtra("id_empresa");
        titulo = intent.getStringExtra("titulo");
        ChatEmpresaAdapter = new ChatEmpresaAdapter(this, R.layout.activity_chat_singlemessage);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        id_usuario = preferences.getInt("id_usuario", 0);
        sesion_usuario = preferences.getInt("session_usuario", 0);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        getComentarios();

        //buttonSend = (Button) findViewById(R.id.btnSendComment);

        //chatText = (EditText) findViewById(R.id.etComment);

        //listView.setAdapter(ChatEmpresaAdapter);

        //to scroll the list view to bottom on data change


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
                        ChatEmpresaActivity.super.onBackPressed();
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
        new GetComentarios(id_empresa).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private boolean sendChatMessage(){


        ChatEmpresaAdapter.addNew(this, id,  id_empresa, String.valueOf(id_usuario), chatText.getText().toString());
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
            if(Utils.isConn(ChatEmpresaActivity.this)){
                if (!chatText.getText().toString().equals("")) {
                    sendChatMessage();
                    getComentarios();
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



    public class GetComentarios extends AsyncTask<Void, Void, List<w_comentarios_empresa>> {

        String mStrings;
        String id_empresa;
        String WEBSERVER;
        //ProgressDialog loading;

        public GetComentarios(String id_empresa) {
            this.id_empresa = id_empresa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetComentariosEmpresa", "onPreExecute");
            //loading = ProgressDialog.show(ChatBubbleActivity.this, "Recuperando comentarios, espere por favor...", null, true, true);
            //loading.setCancelable(false);
            //loading.setCanceledOnTouchOutside(false);
        }

        @SafeVarargs
        @Override
        protected final List<w_comentarios_empresa> doInBackground(Void... arg0) {

            mStrings = "[]";

            WEBSERVER = getResources().getString(R.string.web_server);
            LatLng location = Utils.getLocation(ChatEmpresaActivity.this);
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER + "api/getComentariosEmpresa.php?id_empresa=" + id_empresa + "&id_user="+id_usuario);
                Log.v("GetComentariosEmpresa", url.toString());
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
                                    w_comentarios_empresa(obj.getString("id"), obj.getString("empresa"), obj.getString("usuario"), obj.getString("mensaje"), obj.getString("estado"), obj.getString("fecha_registro"), obj.getString("tipo_remitente") , obj.getString("imagen_user"), obj.getString("imagen_empresa")));
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
        protected void onPostExecute(final List<w_comentarios_empresa> result) {
            super.onPostExecute(result);
            Log.v("GetComentariosEmpresa", "onPostExecute");
            ChatEmpresaAdapter.clear();
            //ChatEmpresaAdapter.notifyDataSetChanged();
            //Limpiamos los datos cargados previamente
            ChatEmpresaAdapter.chatMessageList.clear();
            for (w_comentarios_empresa comentario : comentarios) {
                ChatEmpresaAdapter.add(comentario);
                //SI HUBIERON COMENTARIOS SE GUARDA EL ID DE LA CONVERSACION
                id = comentario.getId();
            }
            listView.setAdapter(ChatEmpresaAdapter);
            ChatEmpresaAdapter.notifyDataSetChanged();
            chatText.setText(null);

            //loading.dismiss();
            ChatEmpresaAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    //listView.setSelection(0);
                }
            });

        }
    }

}

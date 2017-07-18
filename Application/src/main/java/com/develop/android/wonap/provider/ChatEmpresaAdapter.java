package com.develop.android.wonap.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.database.w_comentarios_empresa;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatEmpresaAdapter extends ArrayAdapter<w_comentarios_empresa> {

    private TextView chatText;
    public List<w_comentarios_empresa> chatMessageList = new ArrayList<>();
    w_comentarios_empresa empresa = null;
    Activity actividad;
    LatLng location;
    private static String id;
    private static String id_empresa;
    private static String id_user;
    private static String comentario;
    private static boolean sucess = false;

    @Override
    public void add(w_comentarios_empresa comentario) {
        chatMessageList.add(comentario);
        super.add(comentario);
    }

    public void addNew(Activity context, final String id, String id_empresa, String id_user, String comentario) {
//GUARDAR MENSAJE
        this.actividad = context;
        this.id = id;
        this.id_empresa = id_empresa;
        this.id_user = id_user;

        try {
            this.comentario = URLEncoder.encode(comentario, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            new insertComentarios(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public ChatEmpresaAdapter(Activity context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.actividad = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public w_comentarios_empresa getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }

        String WEBSERVER = getContext().getString(R.string.web_server);
        w_comentarios_empresa comentario = getItem(position);
        TextView nombre_usuario = (TextView) row.findViewById(R.id.redactor);
        ImageView imagen_usuario = (ImageView) row.findViewById(R.id.imageUser);
        Transformation transformation = new RoundedCornersTransformation(Glide.get(getContext()).getBitmapPool(), 100, 0);
        //NOMBRE USUARIO
        if (comentario.tipo_remitente.equals("Administrador")) {
            nombre_usuario.setText(comentario.getEmpresa());

            Glide.with(getContext())
                    .load(WEBSERVER+"upload/"+comentario.getImagenEmpresa())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_user)
                    .override(40, 40)
                    .bitmapTransform(transformation)
                    .into(imagen_usuario);
        }

        if (comentario.tipo_remitente.equals("Usuario")) {
            nombre_usuario.setText(comentario.getUsuario());

            Glide.with(getContext())
                    .load(WEBSERVER+"upload/"+comentario.getImagenUser())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_user)
                    .override(40, 40)
                    .bitmapTransform(transformation)
                    .into(imagen_usuario);
        }

        //MENSAJE
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(comentario.getMensaje());

        //FECHA
        TextView fecha = (TextView) row.findViewById(R.id.date_message);
        fecha.setText(comentario.getFechaRegistro());
        return row;
    }

    private static class insertComentarios extends AsyncTask<Void, Void, Void> {

        Activity actividad;
        String mStrings;
        ProgressDialog loading;

        public insertComentarios(Activity actvidad) {
            this.actividad = actvidad;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(actividad, "Guardando su mensaje, espere por favor...", null, true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }


        @SafeVarargs
        @Override
        protected final Void doInBackground(Void... arg0) {

            mStrings = "[]";
            String webserver = actividad.getString(R.string.web_server);
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(webserver+"api/insertMensajesEmpresa.php?id="+id+"&id_empresa="+id_empresa+"&id_user="+id_user+"&comentario="+comentario);
                Log.v("InsertREmpresas:",url.toString());
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
                Log.v("JSON",mStrings);

                if (mStrings.equals("{\"code\":1}"))
                    sucess = true;
                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //RECUPERAMOS LOS COMENTARIOS DE ESTE ANUNCIO
            loading.dismiss();
            if(sucess) Toast.makeText(actividad, "Mensaje guardado exitosamente!", Toast.LENGTH_LONG).show();
            else Toast.makeText(actividad, "Hubo un error en el registro, intentelo de nuevo.", Toast.LENGTH_LONG).show();
        }

    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    public void getComentarios()
    {

    }
}
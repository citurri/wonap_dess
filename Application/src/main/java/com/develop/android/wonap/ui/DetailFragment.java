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

package com.develop.android.wonap.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.database.WonapDatabaseLocal;
import com.develop.android.wonap.database.markers;
import com.develop.android.wonap.provider.GalleryActivity;
import com.develop.android.wonap.provider.TouristAttractions;
import com.develop.android.wonap.test.Attraction;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.lid.lib.LabelImageView;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * The tourist attraction detail fragment which contains the details of a
 * a single attraction (contained inside
 */
public class DetailFragment extends Fragment implements
        ShareActionProvider.OnShareTargetSelectedListener  {

    private static final String EXTRA_ATTRACTION = "id_oferta";
    private OfertaModel oferta_detalle;
    String id_oferta = "0";
    TextView nameTextView;
    TextView descTextView;
    TextView distanceTextView;
    TextView validezTextView;
    TextView direccionTextView;
    TextView cuponTextView;
    LabelImageView imageView;
    FloatingActionButton mapFab;
    FloatingActionButton qr_cupon;
    int id_user = 0;
    LinearLayout cuponLayout;
    private String toast;
    private ShareActionProvider share=null;
    private Intent shareIntent=new Intent(Intent.ACTION_SEND);

    public static DetailFragment createInstance(String id_oferta) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ATTRACTION, id_oferta);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        id_oferta = getArguments().getString(EXTRA_ATTRACTION);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        descTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
        validezTextView = (TextView) view.findViewById(R.id.validezTextView);
        direccionTextView = (TextView) view.findViewById(R.id.direccionTextView);
        cuponTextView = (TextView) view.findViewById(R.id.cuponTextView);
        imageView = (LabelImageView) view.findViewById(R.id.imageView);
        mapFab = (FloatingActionButton) view.findViewById(R.id.mapFab);
        cuponLayout = (LinearLayout) view.findViewById(R.id.cuponLayout);
        qr_cupon = (FloatingActionButton) view.findViewById(R.id.qr_cupon);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        id_user = preferences.getInt("id_usuario", 0);

        shareIntent.setType("image/*");

        Log.v("Detalle Oferta:", "onCreateView");

        if (Utils.isConn(getActivity()))
        new GetOferta(id_oferta).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.share);
        //chat_1.setPadding(10,0,10,0);

        MenuItem map = menu.findItem(R.id.map);
        ImageButton mapa = ((ImageButton) map.getActionView());
        mapa.setImageResource(R.drawable.ic_building);
        //mapa.setPadding(1,0,1,0);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PERFIL DE EMPRESA

                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                EmpresaProfileActivity.startUserProfileFromLocation(startingLocation, getActivity(),  oferta_detalle.getIdEmpresa());
                getActivity().overridePendingTransition(0, 0);
            }
        });

        MenuItem chat = menu.findItem(R.id.chat);
        ImageButton chat_1 = ((ImageButton) chat.getActionView());
        //chat_1.setBackground(drawableFromTheme);
        chat_1.setImageResource(R.drawable.ic_chat);
        //chat_1.setBackgroundColor(Color.TRANSPARENT);
        chat_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MENSAJES
                //Intent intent = new Intent(getActivity(), ChatBubbleActivity.class);

                //startActivity(intent);

                final Intent intent = new Intent(getActivity(), ChatBubbleActivity.class);
                intent.putExtra("id", oferta_detalle.getId());
                intent.putExtra("titulo", oferta_detalle.getTitulo());
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(ChatBubbleActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);




            }
        });

        share=(ShareActionProvider)MenuItemCompat.getActionProvider(item);
        share.setOnShareTargetSelectedListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // On Lollipop+ we finish so to run the nice animation
                    getActivity().finishAfterTransition();
                    return true;
                }
                else
                {
                    getActivity().finish();
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Really hacky loop for finding attraction in our static content provider.
     * Obviously would not be used in a production app.
     */
    private Attraction findAttraction(String attractionName) {
        for (Map.Entry<String, List<Attraction>> attractionsList : TouristAttractions.getAttractions().entrySet()) {
            List<Attraction> attractions = attractionsList.getValue();
            for (Attraction attraction : attractions) {
                if (attractionName.equals(attraction.name)) {
                    return attraction;
                }
            }
        }
        return null;
    }

    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        return false;
    }

    private class GetOferta extends AsyncTask<Void, Void, OfertaModel> {

        String mStrings;
        String id_oferta;
        String WEBSERVER;

        public GetOferta(String id_oferta) {
            this.id_oferta = id_oferta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetOferta_detalle", "onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final OfertaModel doInBackground(Void... arg0) {

            mStrings = "[]";
            WEBSERVER = getActivity().getString(R.string.web_server);
            LatLng location = Utils.getLocation(getActivity());
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER + "api/getOfertaDetalle.php?id_oferta=" + id_oferta + "&id_user="+id_user+"&latitud_user="+String.valueOf(location.latitude)+"&longitud_user="+String.valueOf(location.longitude));
                Log.v("GetOferta_detalle", url.toString());
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
                Log.v("GetOferta_detalle", "doInBackground");
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
                            oferta_detalle = new
                                    OfertaModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"), obj.getString("descripcion"), obj.getString("imagen_oferta"), obj.getBoolean("es_cupon"), obj.getString("fecha_inicio"), obj.getString("fecha_fin"), obj.getString("denominacion"), obj.getString("pos_latitud"), obj.getString("pos_longitud"), obj.getString("pos_map_address"), obj.getString("pos_map_city"), obj.getString("pos_map_country"), obj.getString("distancia_user"), obj.getString("cupones_habilitados"), obj.getString("cupones_redimidos"), obj.getBoolean("cupon_permitido"),obj.getString("dias_restantes"), obj.getBoolean("es_favorito"),obj.getString("secundarias_oferta"));
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON", e.toString());

            }

            return oferta_detalle;
        }

        @Override
        protected void onPostExecute(final OfertaModel result) {
            super.onPostExecute(result);
            Log.v("GetOferta_detalle", "onPostExecute");

            if (oferta_detalle == null) {
                getActivity().finish();

            }

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");

            LatLng location = Utils.getLocation(getActivity());
            String distance = Utils.formatDistanceBetween(location, new LatLng(Double.parseDouble(oferta_detalle.getPosLatitud()),Double.parseDouble(oferta_detalle.getPosLongitud())));
            if (TextUtils.isEmpty(distance)) {
                distanceTextView.setVisibility(View.GONE);
            }


            nameTextView.setTypeface(typeface);
            nameTextView.setTextSize(22f);
            nameTextView.setText(oferta_detalle.getTitulo());
            distanceTextView.setTypeface(typeface);
            distanceTextView.setText("Se encuentra a "+ distance + " de esta oferta.");
            validezTextView.setTypeface(typeface);
            direccionTextView.setTypeface(typeface);
            direccionTextView.setText(oferta_detalle.getPosMapAddress());

            if(Integer.parseInt(oferta_detalle.getDiasRestantes()) > 1 )
                validezTextView.setText("Vigente por "+oferta_detalle.getDiasRestantes()+" días más");
            else
            if(Integer.parseInt(oferta_detalle.getDiasRestantes()) > 0)
                validezTextView.setText("Vigente por "+oferta_detalle.getDiasRestantes()+" día más");
            else
                validezTextView.setText("Ya no se encuentra vigente");

            ///validezTextView.setText("Válido del "+ oferta_detalle.getFechaInicio().substring(0,10) + " al " + oferta_detalle.getFechaFin().substring(0,10));
            descTextView.setTypeface(typeface);
            descTextView.setTextSize(18f);
            descTextView.setText(oferta_detalle.getDescripcion());

            cuponTextView.setTypeface(typeface);


            int imageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                    * Constants.IMAGE_ANIM_MULTIPLIER;
            Glide.with(getActivity())
                    .load(WEBSERVER+"upload/"+oferta_detalle.getImagenOferta())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.color.lighter_gray)
                    .override(imageSize, imageSize)
                    .into(imageView);
            imageView.setLabelTextSize(60);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> images = new ArrayList<String>();
                    //Principal
                    images.add(WEBSERVER+"upload/"+oferta_detalle.getImagenOferta());
                    //Si existen adicionales se las añade
                    String[] secundarias = oferta_detalle.getSecundariasOferta().split(",");
                    if(secundarias.length > 0)
                    {
                        for(String imagen_secundaria : secundarias) {

                            if(!imagen_secundaria.equals(""))
                            images.add(WEBSERVER+"upload/"+imagen_secundaria);
                        }

                    }
                    //images.add("http://sourcey.com/images/stock/salvador-dali-the-dream.jpg");
                    // images.add("http://sourcey.com/images/stock/salvador-dali-persistence-of-memory.jpg");
                    //images.add("http://sourcey.com/images/stock/simpsons-persistence-of-memory.jpg");
                    //images.add("http://sourcey.com/images/stock/salvador-dali-the-great-masturbator.jpg");
                    // images.add("http://sourcey.com/images/stock/simpsons-persistence-of-memory.jpg");
                    //images.add("http://sourcey.com/images/stock/salvador-dali-the-great-masturbator.jpg");
                    //images.add("http://sourcey.com/images/stock/simpsons-persistence-of-memory.jpg");
                    //images.add("http://sourcey.com/images/stock/salvador-dali-the-great-masturbator.jpg");
                    Intent intent = new Intent(getActivity(), GalleryActivity.class);
                    intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, images);
                    startActivity(intent);
                }
            });
            if(oferta_detalle.getEsCupon()){
                imageView.setLabelVisual(true);
                cuponLayout.setVisibility(View.VISIBLE);
                if(oferta_detalle.getCupon_permitido()) {
                    if(!oferta_detalle.getCupones_redimidos().equals(oferta_detalle.getCupones_habilitados())) {
                        cuponTextView.setText("CUPONES DISPONIBLES: " + String.valueOf(Integer.parseInt(oferta_detalle.getCupones_habilitados())-Integer.parseInt(oferta_detalle.getCupones_redimidos())) + " de " + oferta_detalle.getCupones_habilitados());
                        qr_cupon.setVisibility(View.VISIBLE);
                    }
                        else {
                        cuponTextView.setBackgroundColor(getResources().getColor(R.color.transparent_cupon_background_verified));
                        cuponTextView.setText("TODOS LOS CUPONES FUERON REDIMIDOS");
                        qr_cupon.setVisibility(View.GONE);
                    }
                }
                else {
                    cuponLayout.setBackgroundColor(getResources().getColor(R.color.transparent_cupon_background_verified));
                    cuponTextView.setText("USTED YA REDIMIO ESTE CUPON");
                    qr_cupon.setVisibility(View.GONE);
                }
            }
            else {
                cuponLayout.setVisibility(View.GONE);
                imageView.setLabelVisual(false);
                qr_cupon.setVisibility(View.GONE);
            }

            mapFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<markers> marks = new ArrayList<markers>();
                    markers m = new markers(oferta_detalle.getNombreEmpresa(), oferta_detalle.getPosMapAddress(), oferta_detalle.getPosLatitud(), oferta_detalle.getPosLongitud());
                    marks.add(m);
                    Intent i = new Intent(getActivity(), MapsOfertaActivity.class);
                    i.putParcelableArrayListExtra("markers", marks);
                    i.putExtra("titulo", oferta_detalle.getTitulo());
                    i.putExtra("titulo_mapa", "Ubicación de la oferta");
                    startActivity(i);
                }
            });

            qr_cupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanFromFragment();
                }
            });


            String mensaje = "Empresa: " + oferta_detalle.getTitulo() + "\n\n" +
                    "Detalle: " + oferta_detalle.getDescripcion() +
                    "\n\nImagen: " + WEBSERVER +"upload/" +oferta_detalle.getImagenOferta() +
                    "\n\nDescargue WONAP!! ";


            shareIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
            shareIntent.setType("text/plain");
            share.setShareIntent(shareIntent);

        }
    }

    public void scanFromFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
                integrator.setCaptureActivity(ToolbarCaptureActivity.class);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            } else {
                requestPermission();
            }
        } else
        {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setCaptureActivity(ToolbarCaptureActivity.class);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();

        }
    }

    protected boolean checkPermission() {
        int result2 = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);
        if (result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
                    integrator.setCaptureActivity(ToolbarCaptureActivity.class);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setOrientationLocked(false);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                } else {
                    Log.e("value", "Permiso negado. No puede acceder a la cámara.");
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //displayToast();
    }

    private void displayToast() {
        if(getActivity() != null && toast != null) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
            toast = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                toast = "Cancelado por el usuario";
            } else {

                    //VERIFICAR EL CUPON
                    if(result.getContents().split(",")[0].equals("empresa")) {
                        //intent a perfil de empresa
                        int[] startingLocation = new int[2];
                        EmpresaProfileActivity.startUserProfileFromLocation(startingLocation, getActivity(), result.getContents().split(",")[1]);
                        getActivity().overridePendingTransition(0, 0);
                    }
                    else
                        verificarQR(result.getContents().split(",")[1]);
            }

            // At this point we may or may not have a reference to the activity
            //displayToast();
        }
    }

    public void verificarQR(String qr) {

        HashMap<String, String> postdata = new HashMap<String, String>();

        postdata.put("id_oferta", qr);
        postdata.put("id_user", String.valueOf(id_user));

        PostResponseAsyncTask task = new PostResponseAsyncTask(getActivity(), postdata, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                switch (s) {
                    case "1":
                        toast = "QR Verificado Correctamente, reclame su promoción en la sucursal más cercana.";
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                        cuponLayout.setBackgroundColor(getResources().getColor(R.color.transparent_cupon_background_verified));
                        cuponTextView.setText("CUPON REDIMIDO CON EXITO");
                        qr_cupon.setVisibility(View.GONE);
                        break;
                    case "YA REDIMIDO":
                        toast = "Usted ya redimió este cupon con anterioridad, de no ser así consulte en la empresa.";
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                        break;
                    case "SIN DISPONIBLES":
                        toast = "Lastimosamente no quedan cupon habilitados para ser redimidos.";
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                        break;
                    case "CUPON DESHABILITADO":
                        toast = "Este cupón fue deshabilitado por su administración.";
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                        break;
                    case "OFERTA DESHABILITADA":
                        toast = "Esta oferta esta vencida o fue deshabilitada por su administración.";
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                        break;
                    case "0":
                        toast = "Hubo un error en el registro del cupón, vuelva a intentarlo.";
                        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Log.v("QR verficar", s);
                        break;
                }
            }
        });


        String WEB_SERVER = this.getString(R.string.web_server);
        task.setLoadingMessage("Verificando cupón, espere por favor.");
        task.execute(WEB_SERVER + "api/verificarCupon.php");
        task.setEachExceptionsHandler(new EachExceptionsHandler() {
            @Override
            public void handleIOException(IOException e) {
                Log.v("Signup Task: ", "No se puede conectar al servidor.");
            }

            @Override
            public void handleMalformedURLException(MalformedURLException e) {
                Log.v("Signup Task: ", "Error de URL.");
            }

            @Override
            public void handleProtocolException(ProtocolException e) {
                Log.v("Signup Task: ", "Error de Protocolo.");
            }

            @Override
            public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                Log.v("Signup Task: ", "Error de codificación.");
            }
        });

        //displayToast();
    }
}

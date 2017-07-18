package com.develop.android.wonap.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.common.CircleTransform;
import com.develop.android.wonap.database.w_empresas;
import com.develop.android.wonap.provider.ChatEmpresaAdapter;
import com.develop.android.wonap.provider.RoundedCornersTransformation;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.develop.android.wonap.R;
import com.develop.android.wonap.provider.RevealBackgroundView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class EmpresaProfileActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile)
    ViewPager rvUserProfile;
    @BindView(R.id.tlUserProfileTabs)
    SmartTabLayout tlUserProfileTabs;
    @BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.vUserStats)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.ivLogo)
    TextView ivLogo;

    @BindView(R.id.nempresaTextView)
    TextView nempresaTextView;
    @BindView(R.id.dempresaTextView)
    TextView dempresaTextView;
    @BindView(R.id.sempresaTextView)
    TextView sempresaTextView;
    @BindView(R.id.favImageView)
    ImageView favImageView;
    @BindView(R.id.switchNoticias)
    SwitchCompat switchNoticias;
    @BindView(R.id.switchNotificaciones)
    SwitchCompat switchNotificaciones;





    private int avatarSize;
    private String profilePhoto;
    private Integer id_user = 0;
    private String id_empresa = "0";
    w_empresas empresa;
    private static String WEBSERVER = "";
    Toolbar toolbar;


    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity, String id_empresa) {
        Intent intent = new Intent(startingActivity, EmpresaProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra("id_empresa", id_empresa);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_profile);
        ButterKnife.bind(this);

        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LoveYaLikeASister.ttf");
        ivLogo.setTypeface(typeface);
        //this.profilePhoto = getString(R.string.user_profile_photo);}

        WEBSERVER = getString(R.string.web_server);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        id_user = preferences.getInt("id_usuario", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //toolbar.setTitle("Itacamba");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);

    }




    private void setupUserProfileGrid() {

        id_empresa = getIntent().getStringExtra("id_empresa");
        new GetEmpresaDetalle(id_empresa).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class GetEmpresaDetalle extends AsyncTask<Void, Void, w_empresas> {

        String mStrings;
        String id_empresa;
        ProgressDialog loading;

        public GetEmpresaDetalle(String id_empresa){
            this.id_empresa = id_empresa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("getEmpresasDetalle","onPreExecute");

        }

        @SafeVarargs
        @Override
        protected final w_empresas doInBackground(Void... arg0) {

            mStrings = "[]";


            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getEmpresasDatosGenerales.php?id_empresa="+id_empresa+"&id_user="+id_user);
                Log.v("getEmpresasDetalle",url.toString());
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
                Log.v("getEmpresasDetalle","doInBackground");
                try {
                    //result_original.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);

                            empresa = new
                                    w_empresas(obj.getString("id"), obj.getString("id_categoria"), obj.getString("nombre_categoria"), obj.getString("nombre"), obj.getString("descripcion"),obj.getString("logo"),obj.getBoolean("favorito"), obj.getString("historia"), obj.getString("valores"), obj.getBoolean("not_noticias"),obj.getBoolean("not_ofertas"),obj.getString("seguidores"));
                         }
                   }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.v("JSON",e.toString());
            }
            return empresa;
        }

        @Override
        protected void onPostExecute(w_empresas result) {
            super.onPostExecute(result);
            Log.v("getEmpresasDetalle","onPostExecute");
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        isShow = true;
                        ivLogo.setVisibility(View.VISIBLE);
                        ivLogo.setText(empresa.getNombre());
                        //toolbar.setTitle(empresa.getNombre());
                    } else if (isShow) {
                        isShow = false;
                        ivLogo.setVisibility(View.INVISIBLE);
                        ivLogo.setText(" ");
                        //toolbar.setTitle(" ");
                    }
                }
            });

            Glide.with(getApplicationContext())
                    .load(WEBSERVER+"upload/"+empresa.getLogo())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.img_circle_placeholder)
                    .centerCrop()
                    .transform(new CircleTransform(getApplicationContext()))
                    .into(ivUserProfilePhoto);

            nempresaTextView.setText(empresa.getNombre());
            dempresaTextView.setText(empresa.getDescripcion());
            sempresaTextView.setText(empresa.getSeguidores()+" seguidores");

            favImageView.setTag(R.drawable.ic_like);
            favImageView.setDrawingCacheEnabled(true);
            favImageView.buildDrawingCache();

            if(empresa.getFavorito()){
                favImageView.setTag(R.drawable.ic_liked);
                favImageView.setImageResource(R.drawable.ic_liked);
            }
            else{
                favImageView.setTag(R.drawable.ic_like);
                favImageView.setImageResource(R.drawable.ic_like);
            }

            favImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean favorito;
                    int id = (int)favImageView.getTag();
                    if( id == R.drawable.ic_like){

                        favImageView.setTag(R.drawable.ic_liked);
                        favImageView.setImageResource(R.drawable.ic_liked);
                        favorito = true;
                        //Toast.makeText(getActivity(),list.get(position).getNombreEmpresa()+" añadido a favoritos", Toast.LENGTH_LONG).show();

                    }else{

                        favImageView.setTag(R.drawable.ic_like);
                        favImageView.setImageResource(R.drawable.ic_like);
                        favorito= false;
                        //Toast.makeText(getActivity(),list.get(position).getNombreEmpresa()+" eliminado de sus favoritos", Toast.LENGTH_LONG).show();
                    }


                    HashMap<String, String> postdata = new HashMap<String, String>();
                    postdata.put("id_user", String.valueOf(id_user));
                    postdata.put("id_empresa", String.valueOf(empresa.getId()));
                    postdata.put("favorito", String.valueOf(favorito));

                    PostResponseAsyncTask task = new PostResponseAsyncTask(EmpresaProfileActivity.this, postdata, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            switch (s) {
                                case "1":
                                    Toast.makeText(EmpresaProfileActivity.this, empresa.getNombre()+" añadido a favoritos", Toast.LENGTH_LONG).show();
                                    switchNoticias.setChecked(true);
                                    switchNoticias.setClickable(true);
                                    switchNotificaciones.setChecked(true);
                                    switchNotificaciones.setClickable(true);
                                    break;
                                default:
                                    Toast.makeText(EmpresaProfileActivity.this, empresa.getNombre()+" eliminado de sus favoritos", Toast.LENGTH_LONG).show();
                                    switchNoticias.setChecked(false);
                                    switchNoticias.setClickable(false);
                                    switchNotificaciones.setChecked(false);
                                    switchNotificaciones.setClickable(false);
                                    break;

                            }
                        }
                    });


                    task.setLoadingMessage("Actualizando su lista de favoritos, espere por favor.");
                    task.execute(WEBSERVER + "api/updateFavorito.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Log.v("Favorito Task: ", "No se puede conectar al servidor.");
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Log.v("Favorito Task: ", "Error de URL.");
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Log.v("Favorito Task: ", "Error de Protocolo.");
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Log.v("Favorito Task: ", "Error de codificación.");
                        }
                    });

                }
            });

            switchNoticias.setChecked(empresa.getNotNoticias());
            switchNotificaciones.setChecked(empresa.getNotOfertas());

            switchNoticias.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> postdata = new HashMap<String, String>();
                    postdata.put("id_user", String.valueOf(id_user));
                    postdata.put("id_empresa", String.valueOf(empresa.getId()));
                    postdata.put("not_noticias", String.valueOf(switchNoticias.isChecked()));

                    PostResponseAsyncTask task = new PostResponseAsyncTask(EmpresaProfileActivity.this, postdata, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            switch (s) {
                                case "1":
                                    Toast.makeText(EmpresaProfileActivity.this, "Notificación de noticias activada para "+empresa.getNombre(), Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(EmpresaProfileActivity.this, "Notificación de noticias desactivada para "+empresa.getNombre(), Toast.LENGTH_LONG).show();
                                    break;

                            }
                        }
                    });


                    task.setLoadingMessage("Actualizando las notificaciones de noticias, espere por favor.");
                    task.execute(WEBSERVER + "api/updateNotNoticias.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Log.v("Favorito Task: ", "No se puede conectar al servidor.");
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Log.v("Favorito Task: ", "Error de URL.");
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Log.v("Favorito Task: ", "Error de Protocolo.");
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Log.v("Favorito Task: ", "Error de codificación.");
                        }
                    });
                }
            });

            switchNotificaciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> postdata = new HashMap<String, String>();
                    postdata.put("id_user", String.valueOf(id_user));
                    postdata.put("id_empresa", String.valueOf(empresa.getId()));
                    postdata.put("not_ofertas", String.valueOf(switchNotificaciones.isChecked()));

                    PostResponseAsyncTask task = new PostResponseAsyncTask(EmpresaProfileActivity.this, postdata, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            switch (s) {
                                case "1":
                                    Toast.makeText(EmpresaProfileActivity.this, "Notificación de ofertas cercanas activada para "+empresa.getNombre(), Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(EmpresaProfileActivity.this, "Notificación de ofertas cercanas desactivada para "+empresa.getNombre(), Toast.LENGTH_LONG).show();
                                    break;

                            }
                        }
                    });


                    task.setLoadingMessage("Actualizando las notificaciones de noticias, espere por favor.");
                    task.execute(WEBSERVER + "api/updateNotOfertas.php");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {
                            Log.v("Favorito Task: ", "No se puede conectar al servidor.");
                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {
                            Log.v("Favorito Task: ", "Error de URL.");
                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {
                            Log.v("Favorito Task: ", "Error de Protocolo.");
                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                            Log.v("Favorito Task: ", "Error de codificación.");
                        }
                    });
                }
            });

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(EmpresaProfileActivity.this, ChatEmpresaActivity.class);
                    intent.putExtra("id_empresa", id_empresa);
                    intent.putExtra("titulo", empresa.getNombre());
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    intent.putExtra(ChatEmpresaActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                    startActivity(intent);
                    EmpresaProfileActivity.this.overridePendingTransition(0, 0);
                }
            });

            setupTabs();


            }

    }

    private void setupTabs() {

        tlUserProfileTabs.setBackgroundColor(getResources().getColor(R.color.accent_color));
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        rvUserProfile.setAdapter(mSectionsPagerAdapter);
        tlUserProfileTabs.setViewPager(rvUserProfile);
    }



    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            //userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            animateUserProfileOptions();
            animateUserProfileHeader();
            rvUserProfile.setVisibility(View.VISIBLE);
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);

        rvUserProfile.setTranslationY(rvUserProfile.getHeight());
        rvUserProfile.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());
           vUserStats.setAlpha(0);

           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
           vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 7;
        private String tabTitles[] =
                new String[] {"HISTORIA","CONTACTOS", "SUCURSALES","IMAGENES", "NOTICIAS", "OFERTAS", "PRODUCTOS"};

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment f = null;

            switch(position) {
                case 0:
                    f= EmpresaGeneralesFragment.newInstance(empresa.getHistoria(), empresa.getValores());
                    break;
                case 1:
                    f= new EmpresaGeneralesFragment();
                    break;
                case 2:
                    f= new EmpresaGeneralesFragment();
                    break;
                case 3:
                    f= EmpresaImagenesFragment.newInstance(empresa.getId());
                    break;
                case 4:
                    f= new EmpresaGeneralesFragment();
                    break;
                case 5:
                    f= new EmpresaGeneralesFragment();
                    break;
                case 6:
                    f= new EmpresaGeneralesFragment();
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getItemPosition(Object object) {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE;
        }
    }
}

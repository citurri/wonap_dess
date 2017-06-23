package com.develop.android.wonap.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.ParseJSON;
import com.develop.android.wonap.database.WonapDatabaseLocal;
import com.develop.android.wonap.service.UtilityService;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.scalified.fab.ActionButton;

import java.sql.SQLException;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    ActionButton actionButton;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        BaseInit();

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        final View actionBar = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) actionBar.findViewById(R.id.title_text);
        mTitleTextView.setText("WONAP");
        mActionBar.setCustomView(actionBar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionBar.getParent()).setContentInsetsAbsolute(0,0);

        BoomMenuButton leftBmb = actionBar.findViewById(R.id.action_bar_left_bmb);
        BoomMenuButton rightBmb = (BoomMenuButton) actionBar.findViewById(R.id.action_bar_right_bmb);


        rightBmb.setButtonEnum(ButtonEnum.TextOutsideCircle);
        rightBmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_3_1);
        rightBmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_3_1);
        rightBmb.setDelay(100);
        rightBmb.setHideDelay(50);
        rightBmb.setDuration(250);
        rightBmb.setHideDuration(250);

        rightBmb.addBuilder(new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_favorite_full)
                .normalText("Lista de Empresas Favoritas")
                .textSize(14)
                .pieceColor(Color.GREEN)
                .normalColor(Color.parseColor("#9729ae"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(PrincipalActivity.this , "Clicked " + index, Toast.LENGTH_SHORT).show();
                    }
                }));

        rightBmb.addBuilder(new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_map)
                .normalText("Ver mapa de ofertas")
                .textSize(14)
                .pieceColor(Color.GREEN)
                .normalColor(Color.parseColor("#f34334"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(PrincipalActivity.this , "Clicked " + index, Toast.LENGTH_SHORT).show();
                    }
                }));

        rightBmb.addBuilder(new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.ic_logout)
                .normalText("Cerrar sesión")
                .textSize(14)
                .pieceColor(Color.GREEN)
                .normalColor(Color.parseColor("#3daa94"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalActivity.this);

                        builder.setTitle("Confirmar");
                        builder.setMessage("Esta seguro de cerrar su sesión en WONAP?");

                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PrincipalActivity.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("session_usuario", 0);
                                editor.apply();
                                Intent i = new Intent(getApplication(), LoginActivity.class);
                                finish();
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setAction("TO_LOGIN_ACTIVITY");
                                startActivity(i);
                                dialog.dismiss();
                            }

                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.setCanceledOnTouchOutside(false);
                        alert.setCancelable(false);
                        alert.show();
                    }
                }));

        leftBmb.setButtonEnum(ButtonEnum.Ham);
        leftBmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        leftBmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);
        leftBmb.setDelay(100);
        leftBmb.setHideDelay(50);
        leftBmb.setDuration(250);
        leftBmb.setHideDuration(250);
        leftBmb.addBuilder(new HamButton.Builder()
                .normalImageRes(R.drawable.ic_store)
                .normalText("OFERTAS CERCANAS")
                .subNormalText("Aprovecha todas las funciones de WONAP y localiza las ofertas que esperan cerca de ti.")
                .pieceColor(Color.CYAN)
                .subTextSize(14)
                .normalColor(Color.parseColor("#f34334"))
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(PrincipalActivity.this , "Clicked " + index, Toast.LENGTH_SHORT).show();
                    }
                }));
        leftBmb.addBuilder(new HamButton.Builder()
                .normalImageRes(R.drawable.ic_news)
                .normalText("ULTIMAS NOTICIAS")
                .subNormalText("Lee las noticias más recientes publicadas en WONAP y entérate de todas las novedades.")
                .pieceColor(Color.CYAN)
                .normalColor(Color.parseColor("#9729ae"))
                .subTextSize(14)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(PrincipalActivity.this , "Clicked " + index, Toast.LENGTH_SHORT).show();
                    }
                }));
        leftBmb.addBuilder(new HamButton.Builder()
                .normalImageRes(R.drawable.ic_building)
                .normalText("DIRECTORIO DE EMPRESAS")
                .subNormalText("Busca y descubre la información de las diferentes empresas registradas en WONAP.")
                .pieceColor(Color.CYAN)
                .normalColor(Color.parseColor("#01a9f2"))
                .subTextSize(14)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(PrincipalActivity.this , "Clicked " + index, Toast.LENGTH_SHORT).show();
                    }
                }));


        leftBmb.addBuilder(new HamButton.Builder()
                .normalImageRes(R.drawable.ic_account)
                .normalText("MI CUENTA")
                .subNormalText("Verificar todos los datos de tu cuenta.")
                .pieceColor(Color.CYAN)
                .normalColor(Color.parseColor("#3daa94"))
                .subTextSize(14)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        // When the boom-button corresponding this builder is clicked.
                        Toast.makeText(PrincipalActivity.this , "Clicked " + index, Toast.LENGTH_SHORT).show();
                    }
                }));

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.tabs);
        viewPagerTab.setViewPager(mViewPager);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        actionButton = (ActionButton) findViewById(R.id.action_button);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int distance = (int) 250.0f; // in density-independent pixels

                if(position == 0) {
                    // Initialize the moving distance
                    actionButton.moveUp(distance);
                }else if(position == 1) {
                    actionButton.moveDown(distance);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }
    private void BaseInit() {
        if (Utils.isConn(this)) {
            try {
                if(Utils.doesDatabaseExist(this, "WonapDatabaseLocal")) {
                    ActualizarBase();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Por favor, conéctese a internet....", Toast.LENGTH_SHORT).show();
        }
    }

    private void ActualizarBase() throws SQLException, ClassNotFoundException {
        List<String> lastWriteDates = new WonapDatabaseLocal(this).getLastWriteDates();
        String[] arrayWriteDates = lastWriteDates.toArray(new String[lastWriteDates.size()]);
        String param1 = "getValuesAppsyn.php?write_date=" + arrayWriteDates[0];
        new ParseJSON(this, false, "Consulta", false, false).execute(param1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_principal, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] =
                new String[] {"INICIO","NOTICIAS", "OFERTAS"};

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
                    f= new Dashboard();
                    break;
                case 1:
                    f= new AttractionListFragment();
                    break;
                case 2:
                    f= new AttractionListFragment();
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

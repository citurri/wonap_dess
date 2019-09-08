package com.develop.android.wonap.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;

public class CountryFilterActivity extends AppCompatActivity {

    private static String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_filter);

        action = getIntent().getAction();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView mensaje1 = (TextView) findViewById(R.id.text3);
        TextView mensaje2 = (TextView) findViewById(R.id.text4);
        TextView ciudad = (TextView) findViewById(R.id.ciudad_text);
        RelativeLayout frame = (RelativeLayout) findViewById(R.id.ciudad_frame);
        RelativeLayout todos = (RelativeLayout) findViewById(R.id.ciudad_todos);
        TextView todos_text = (TextView) findViewById(R.id.todos_text);

        //UtilityService.requestLocation(this);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LoveYaLikeASister.ttf");
        mensaje1.setTypeface(typeface);
        if (action.equals("Empresas") || action.equals("Anuncios") )
            mensaje1.setText("Usted se encuentra en: "+prefs.getString("LOCALITY", ""));

        mensaje2.setTypeface(typeface);
        ciudad.setTypeface(typeface);
        if (action.equals("Empresas") || action.equals("Anuncios"))
            ciudad.setText("Listar por proximidad");
        else
            if (action.equals("Noticias"))
                ciudad.setText("Listar en tu ciudad");
            else
                ciudad.setText(prefs.getString("LOCALITY", ""));
        todos_text.setTypeface(typeface);
        if (!Utils.isConn(this) || prefs.getString("LOCALITY", "").equals("")) {
            frame.setVisibility(View.GONE);
            mensaje1.setVisibility(View.GONE);
        }
        todos_text.setTypeface(typeface);
        if (action.equals("Empresas")) todos_text.setText("Listar todas las empresas");
        else if (action.equals("Anuncios")) todos_text.setText("Listar todas los anuncios");
        else todos_text.setText("Listar todas las noticias");

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                if (action.equals("Empresas"))
                    intent = new Intent(CountryFilterActivity.this, EmpresasActivity.class);
                //else intent = new Intent(CountryFilterActivity.this, AnunciosList.class);
                else intent = new Intent(CountryFilterActivity.this, NoticiasActivity.class);

                Bundle b = new Bundle();
                b.putString("id_ciudad", prefs.getString("ID_CIUDAD", ""));
                b.putString("Pais", prefs.getString("COUNTRY", ""));
                b.putString("Ciudad", prefs.getString("LOCALITY", ""));
                b.putBoolean("Todos", false);
                b.putBoolean("Proximidad", true);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                if (action.equals("Empresas"))
                    intent = new Intent(CountryFilterActivity.this, EmpresasActivity.class);
                else intent = new Intent(CountryFilterActivity.this, NoticiasActivity.class);

                Bundle b = new Bundle();
                b.putString("id_ciudad", prefs.getString("ID_CIUDAD", ""));
                b.putString("Pais", prefs.getString("COUNTRY", ""));
                b.putString("Ciudad", prefs.getString("LOCALITY", ""));
                b.putBoolean("Todos", true);
                b.putBoolean("Proximidad", false);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final CountryListFragment emp_list = CountryListFragment.newInstance(action);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_ciudades, emp_list)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_country_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
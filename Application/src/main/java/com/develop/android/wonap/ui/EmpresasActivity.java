package com.develop.android.wonap.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.develop.android.wonap.R;
import com.develop.android.wonap.database.WonapDatabaseLocal;
import com.develop.android.wonap.database.w_categorias;
import com.develop.android.wonap.database.w_ciudad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EmpresasActivity extends BaseEmpresaActivity {

    private boolean initializedView = false;
    private static String WEBSERVER = "";
    private List<w_categorias> categorias = new LinkedList<w_categorias>();
    EmpresasListFragment emp_list;
    //*NoticiasListFragment noticia_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        String id_ciudad = bundle.getString("id_ciudad");
        String pais = bundle.getString("Pais");
        String ciudad = bundle.getString("Ciudad");
        Boolean todos = bundle.getBoolean("Todos");
        Boolean proximidad = bundle.getBoolean("Proximidad");

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LoveYaLikeASister.ttf");

        WEBSERVER = getString(R.string.web_server);

        new GetCategorias().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        emp_list = EmpresasListFragment.newInstance(id_ciudad, pais, ciudad, todos, proximidad, this);
        //*noticia_list = NoticiasListFragment.newInstance(id_ciudad);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_empresas, emp_list)
                .commit();
 }

    private class GetCategorias extends AsyncTask<Void, Void, List<w_categorias>> {

        String mStrings;


        public GetCategorias(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetCiudades","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final List<w_categorias> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getCategorias.php");
                Log.v("GetCiudades",url.toString());
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
                Log.v("GetCiudades","doInBackground");
                try {
                    categorias.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            categorias.add(new w_categorias(obj.getString("id"),obj.getString("codigo"),obj.getString("nombre"),obj.getString("estado")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return categorias;
        }

        @Override
        protected void onPostExecute(List<w_categorias> result) {
            super.onPostExecute(result);
            Log.v("GetCiudades","onPostExecute");

            Log.v("Categorias", String.valueOf(categorias.size()));


            ArrayList<String> categoriasName = new ArrayList<String>();
            categoriasName.add("Todas las categorías");
            categoriasName.add("Favoritos");

            for (w_categorias categoria : categorias) {
                 categoriasName.add(categoria.getNombre());
            }
             ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    EmpresasActivity.this, android.R.layout.simple_spinner_item,  categoriasName);

            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                    if (!initializedView) {

                        initializedView = true;
                    } else {
                        //RECIBIMOS EL CONTEXTO DEL FRAGMENTO DE POS PARA PODER FILTRARLO
                        //*emp_list.toUpdate(parent.getItemAtPosition(position).toString());
                        //Toast.makeText(parent.getContext(), "Spinner item "+parent.getItemAtPosition(position).toString()+"!", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_empresas, menu);
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

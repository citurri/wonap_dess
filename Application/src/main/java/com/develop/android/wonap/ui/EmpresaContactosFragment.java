package com.develop.android.wonap.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.develop.android.wonap.R;
import com.develop.android.wonap.database.w_contactos;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmpresaContactosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmpresaContactosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id_empresa";
    String WEBSERVER;
    @BindView(R.id.empresaContacto)
    RecyclerView empresaContacto;
    @BindView(R.id.nulltextView)
    TextView nullTextView;
    private ContactosAdapter contactosAdapter;
    ArrayList<w_contactos> contactos = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String id_empresa;



    public EmpresaContactosFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EmpresaContactosFragment newInstance(String id_empresa) {
        EmpresaContactosFragment fragment = new EmpresaContactosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id_empresa);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_empresa = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_contactos, container, false);
        ButterKnife.bind(this, view);

        WEBSERVER = getActivity().getString(R.string.web_server);

        new GetContactos().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private class GetContactos extends AsyncTask<Void, Void, ArrayList<w_contactos>> {

        String mStrings;

        public GetContactos(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetContactos","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final ArrayList<w_contactos> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getEmpresaContactos.php?id_empresa="+id_empresa);
                Log.v("GetContactos",url.toString());
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
                Log.v("GetContactos","doInBackground");
                try {
                    contactos.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            contactos.add(new w_contactos(obj.getString("direccion"),obj.getString("nombre"),obj.getString("email"),obj.getString("telefono_fijo"),obj.getString("telefono_movil")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return contactos;
        }

        @Override
        protected void onPostExecute(ArrayList<w_contactos> result) {
            super.onPostExecute(result);
            Log.v("GetContactos","onPostExecute");
            if(!isDetached()) {
                contactosAdapter = new ContactosAdapter(getActivity(), contactos);
                empresaContacto.setHasFixedSize(true);
                empresaContacto.setLayoutManager(new GridLayoutManager(
                        getActivity(), getResources().getInteger(R.integer.list_columns)));
                empresaContacto.setAdapter(contactosAdapter);

                if (contactos.size() > 0)
                    nullTextView.setVisibility(View.INVISIBLE);
                else
                    nullTextView.setVisibility(View.VISIBLE);
            }
        }

    }


    private class ContactosAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final LayoutInflater inflater;
        private final List<w_contactos> mContactos;

        public ContactosAdapter(Context context, List<w_contactos> mContactos) {
            inflater = LayoutInflater.from(context);
            this.mContactos = mContactos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = inflater.inflate(R.layout.list_row_contactos, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");
            holder.nombreContacto.setTypeface(typeface);
            holder.nombreContacto.setText(mContactos.get(position).getNombre());
            holder.direccion.setTypeface(typeface);
            String direccion = "";
            if(mContactos.get(position).getDireccion().isEmpty())
                direccion = "Sin dirección";
            else
                direccion = mContactos.get(position).getDireccion();
            holder.direccion.setText(direccion);
            holder.telefonos.setTypeface(typeface);
            String telefonos;
            if(mContactos.get(position).getTelefonoFijo().isEmpty() || mContactos.get(position).getTelefonoFijo().equals("null")) {
                if (mContactos.get(position).getTelefonoMovil().isEmpty() || mContactos.get(position).getTelefonoMovil().equals("null"))
                    telefonos = "Sin teléfonos";
                else
                    telefonos = mContactos.get(position).getTelefonoMovil();
            }
            else
            {
                if (mContactos.get(position).getTelefonoMovil().isEmpty() || mContactos.get(position).getTelefonoMovil().equals("null"))
                    telefonos = mContactos.get(position).getTelefonoFijo();
                else
                    telefonos = mContactos.get(position).getTelefonoFijo() + " - " + mContactos.get(position).getTelefonoMovil();
            }
            holder.telefonos.setText(telefonos);
            holder.email.setTypeface(typeface);
            holder.email.setText(mContactos.get(position).getEmail());

        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mContactos == null ? 0 : mContactos.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView nombreContacto;
        TextView direccion;
        TextView telefonos;
        TextView email;

        public ViewHolder(View view) {
            super(view);
            nombreContacto = (TextView) view.findViewById(R.id.denoTextView);
            direccion = (TextView) view.findViewById(R.id.dirTextView);
            telefonos = (TextView) view.findViewById(R.id.telTextView);
            email = (TextView) view.findViewById(R.id.emailText);
        }

    }


}

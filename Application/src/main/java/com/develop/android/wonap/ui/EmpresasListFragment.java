package com.develop.android.wonap.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.CircleTransform;
import com.develop.android.wonap.common.Constants;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.w_empresas;
import com.google.android.gms.maps.model.LatLng;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

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

//import com.develop.android.wonap.database.dess_pos;


public class EmpresasListFragment extends Fragment implements SearchView.OnQueryTextListener {


    private EmpresasAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<w_empresas> empresas = new ArrayList<>();;
    private boolean myChecks = true;
    private int mImageSize;
    LatLng location;
    private static String id_ciudad;
    private static String pais;
    private static String ciudad;
    private static Boolean todos;
    private static String WEBSERVER = "";
    private Integer id_user = 0;
    SearchView searchView;
    private boolean mItemClicked;

    public static EmpresasListFragment newInstance(String id_ciudad_arg, String pais_arg, String ciudad_arg, Boolean todos_arg) {
        id_ciudad = id_ciudad_arg;
        pais = pais_arg;
        ciudad = ciudad_arg;
        todos = todos_arg;
        return new EmpresasListFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main_empresas, container, false);
        mImageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        location = Utils.getLocation(getActivity());
        WEBSERVER = getString(R.string.web_server);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        id_user = preferences.getInt("id_usuario", 0);
        searchView = (SearchView) view.findViewById(R.id.searchEmpresas);
        searchView.setQueryHint("Empresa o Producto");
        searchView.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(todos) id_ciudad = "0";
        new GetEmpresas(id_ciudad, "").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

   }

    private class GetEmpresas extends AsyncTask<Void, Void, List<w_empresas>> {

        String mStrings;
        String id_ciudad;
        String filter = "";
        ProgressDialog loading;

        public GetEmpresas(String id_ciudad, String filter){
            this.id_ciudad = id_ciudad;
            this.filter = filter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("getEmpresas","onPreExecute");
            loading = ProgressDialog.show(getActivity(), "Recuperando empresas, espere por favor...", null, true, true);
            loading.setCancelable(false);
            loading.setCanceledOnTouchOutside(false);
        }

        @SafeVarargs
        @Override
        protected final List<w_empresas> doInBackground(Void... arg0) {

            mStrings = "[]";


            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getEmpresas.php?id_ciudad="+id_ciudad+"&id_user="+id_user+"&filter="+filter);
                Log.v("getEmpresas",url.toString());
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
                Log.v("getEmpresas","doInBackground");
                try {
                    empresas.clear();
                    //result_original.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);

                            empresas.add(new
                                    w_empresas(obj.getString("id"), obj.getString("id_categoria"), obj.getString("nombre_categoria"), obj.getString("nombre"), obj.getString("descripcion"),obj.getString("logo"),obj.getBoolean("favorito")));
                            //result_original.add(new
                                    //NoticiasModel(obj.getString("id"), obj.getString("id_empresa"), obj.getString("nombre_empresa"), obj.getString("titulo"),obj.getString("descripcion"),obj.getString("imagen_noticia"),obj.getBoolean("es_favorito"),obj.getString("fecha_publicacion")));

                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return empresas;
        }

        @Override
        protected void onPostExecute(List<w_empresas> result) {
            super.onPostExecute(result);
            Log.v("getEmpresas","onPostExecute");
            mAdapter = new EmpresasAdapter();
            mAdapter.setmEmpresas(empresas);
            // Set layout manager
            recyclerView.setLayoutManager(new StickyHeaderLayoutManager());
            recyclerView.setAdapter(mAdapter);
            //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                   // DividerItemDecoration.VERTICAL);

            //recyclerView.addItemDecoration(dividerItemDecoration);

            loading.dismiss();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mItemClicked = false;
    }

    public void toUpdate(String filter) {
        new GetEmpresas(id_ciudad, filter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public boolean onQueryTextChange(String query) {

        final List<w_empresas> filteredModelList = filter(empresas, query);
        if(empresas != null & empresas.size() > 0)
        mAdapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<w_empresas> filter(List<w_empresas> models, String query) {
        query = query.toLowerCase();

        final List<w_empresas> filteredModelList = new ArrayList<>();
        for (w_empresas model : models) {
            final String text = model.getNombre().toLowerCase();
            final String text2 = model.getDescripcion().toLowerCase();
            if (text.contains(query) || text2.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    private class EmpresasAdapter extends SectioningAdapter implements ItemClickListener {


        @Override
        public void onItemClick(View view, int position) {
            TextView textID = view.findViewById(R.id.textID);
           // if (!mItemClicked) {
            //    mItemClicked = true;
                //Toast.makeText(getContext(), "CLICK ON ID: " + textID.getText().toString(), Toast.LENGTH_SHORT).show();
          //  }
            //Intent intent = null;
            //intent = new Intent(getActivity(), EmpresaProfileActivity.class);
            //Bundle b = new Bundle();
            //b.putString("id_empresa", textID.getText().toString());
            //intent.putExtras(b);
            //startActivity(intent);

            int[] startingLocation = new int[2];
            view.getLocationOnScreen(startingLocation);
            startingLocation[0] += view.getWidth() / 2;
            EmpresaProfileActivity.startUserProfileFromLocation(startingLocation, getActivity(),  textID.getText().toString());
            getActivity().overridePendingTransition(0, 0);
        }

        private class Section {
            String alpha;
            ArrayList<w_empresas> empresas = new ArrayList<>();
        }


        public class ItemViewHolder extends SectioningAdapter.ItemViewHolder implements View.OnClickListener {

            TextView textID;
            TextView empresaTextView;
            TextView categoriaView;
            ImageView logoView;
            ImageView favoritoView;
            ItemClickListener mItemClickListener;

            public ItemViewHolder(View itemView, ItemClickListener itemClickListener) {
                super(itemView);
                textID = (TextView) itemView.findViewById(R.id.textID);
                empresaTextView = (TextView) itemView.findViewById(R.id.empresaNombre);
                categoriaView = (TextView) itemView.findViewById(R.id.categoriaText);
                logoView = (ImageView) itemView.findViewById(R.id.imageView5);
                favoritoView = (ImageView) itemView.findViewById(R.id.imageView6);
                mItemClickListener = itemClickListener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }

        public class HeaderViewHolder extends SectioningAdapter.HeaderViewHolder {
            TextView titleTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            }
        }

        private List<w_empresas> mEmpresas = new ArrayList<>();
        ArrayList<Section> sections = new ArrayList<>();

        public EmpresasAdapter() {

        }


        public void setmEmpresas(List<w_empresas> empresas) {
            this.mEmpresas = new ArrayList<>(empresas);
            sections.clear();

            // sort people into buckets by the first letter of last name
            char alpha = 0;
            Section currentSection = null;
            for (w_empresas empresa : empresas) {
                if (empresa.getNombre().charAt(0) != alpha) {
                    if (currentSection != null) {
                        sections.add(currentSection);
                    }

                    currentSection = new Section();
                    alpha = empresa.getNombre().charAt(0);
                    currentSection.alpha = String.valueOf(alpha);
                }

                if (currentSection != null) {
                   currentSection.empresas.add(empresa);
                }
            }

            sections.add(currentSection);
            notifyAllSectionsDataSetChanged();
        }

        @Override
        public int getNumberOfSections() {
            return sections.size();
        }

        @Override
        public int getNumberOfItemsInSection(int sectionIndex) {
            if(sections.get(sectionIndex) != null)
            return sections.get(sectionIndex).empresas.size();
            else
                return 0;
        }

        @Override
        public boolean doesSectionHaveHeader(int sectionIndex) {
            return true;
        }

        @Override
        public boolean doesSectionHaveFooter(int sectionIndex) {
            return false;
        }


        @Override
        public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_row_empresas, parent, false);
            return new ItemViewHolder(v, this);
        }

        @Override
        public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_item_addressbook_header, parent, false);
            return new HeaderViewHolder(v);
        }

        @Override
        public void onBindItemViewHolder(SectioningAdapter.ItemViewHolder viewHolder, int sectionIndex, final int itemIndex, int itemType) {
           Section s = sections.get(sectionIndex);

            final ItemViewHolder holder = (ItemViewHolder) viewHolder;

            final w_empresas empresa = s.empresas.get(itemIndex);

            holder.textID.setText(empresa.getId());
            holder.empresaTextView.setText(empresa.getNombre());
            Log.v("EMPRESA", "ID: " + empresa.getId());
            holder.categoriaView.setText(empresa.getNombre_categoria());
            Glide.with(getActivity())
                    .load(WEBSERVER+"upload/"+empresa.getLogo())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.empty_photo)
                    .transform(new CircleTransform(getActivity()))
                    .override(mImageSize, mImageSize)
                    .into(holder.logoView);

            holder.favoritoView.setTag(R.drawable.ic_like);
            holder.favoritoView.setDrawingCacheEnabled(true);
            holder.favoritoView.buildDrawingCache();

            if(empresa.getFavorito()){
                holder.favoritoView.setTag(R.drawable.ic_liked);
                holder.favoritoView.setImageResource(R.drawable.ic_liked);
            }
            else{
                holder.favoritoView.setTag(R.drawable.ic_like);
                holder.favoritoView.setImageResource(R.drawable.ic_like);
            }

            holder.favoritoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Boolean favorito;
                    int id = (int)holder.favoritoView.getTag();
                    if( id == R.drawable.ic_like){

                        holder.favoritoView.setTag(R.drawable.ic_liked);
                        holder.favoritoView.setImageResource(R.drawable.ic_liked);
                        favorito = true;
                        //Toast.makeText(getActivity(),list.get(position).getNombreEmpresa()+" añadido a favoritos", Toast.LENGTH_LONG).show();

                    }else{

                        holder.favoritoView.setTag(R.drawable.ic_like);
                        holder.favoritoView.setImageResource(R.drawable.ic_like);
                        favorito= false;
                        //Toast.makeText(getActivity(),list.get(position).getNombreEmpresa()+" eliminado de sus favoritos", Toast.LENGTH_LONG).show();
                    }


                    HashMap<String, String> postdata = new HashMap<String, String>();
                    postdata.put("id_user", String.valueOf(id_user));
                    postdata.put("id_empresa", String.valueOf(empresa.getId()));
                    postdata.put("favorito", String.valueOf(favorito));

                    PostResponseAsyncTask task = new PostResponseAsyncTask(getActivity(), postdata, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            switch (s) {
                                case "1":
                                    Toast.makeText(getActivity(), empresa.getNombre()+" añadido a favoritos", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getActivity(), empresa.getNombre()+" eliminado de sus favoritos", Toast.LENGTH_LONG).show();
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

        }

        @Override
        public void onBindHeaderViewHolder(SectioningAdapter.HeaderViewHolder viewHolder, int sectionIndex, int headerType) {
            if(sections.get(sectionIndex) != null) {
                Section s = sections.get(sectionIndex);
                HeaderViewHolder hvh = (HeaderViewHolder) viewHolder;
                hvh.titleTextView.setText(s.alpha);
            }
       }


        public void animateTo(List<w_empresas> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
            setmEmpresas(models);
        }

        private void applyAndAnimateRemovals(List<w_empresas> newModels) {
            for (int i = mEmpresas.size() - 1; i >= 0; i--) {
                final w_empresas model = mEmpresas.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<w_empresas> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final w_empresas model = newModels.get(i);
                if (!mEmpresas.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<w_empresas> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final w_empresas model = newModels.get(toPosition);
                final int fromPosition = mEmpresas.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public w_empresas removeItem(int position) {
            final w_empresas model = mEmpresas.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, w_empresas model) {
            mEmpresas.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final w_empresas model = mEmpresas.remove(fromPosition);
            mEmpresas.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }


    }

    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
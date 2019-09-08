package com.develop.android.wonap.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.R;
import com.develop.android.wonap.database.w_productos;
import com.develop.android.wonap.database.w_productos;

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
 * Use the {@link EmpresaProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmpresaProductosFragment extends Fragment implements SearchView.OnQueryTextListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id_empresa";
    String WEBSERVER;
    private ProductosAdapter productosAdapter;
    ArrayList<w_productos> productos = new ArrayList<>();
    private String id_empresa;
    @BindView(R.id.empresaProducto)
    RecyclerView empresaProducto;
    @BindView(R.id.nulltextView)
    TextView nullTextView;
    @BindView(R.id.search_productos)
    SearchView searchView;

    public EmpresaProductosFragment() {
        // Required empty public constructor
    }

    public static EmpresaProductosFragment newInstance(String id_empresa) {
        EmpresaProductosFragment fragment = new EmpresaProductosFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_productos, container, false);
        ButterKnife.bind(this, view);

        WEBSERVER = getActivity().getString(R.string.web_server);
        searchView.setQueryHint("Nombre o Descripción.");
        searchView.setOnQueryTextListener(this);

        new GetProductos().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        final List<w_productos> filteredModelList = filter(productos, newText);
        if(productos != null & productos.size() > 0)
            productosAdapter.animateTo(filteredModelList);
        empresaProducto.scrollToPosition(0);
        return true;
    }


    private List<w_productos> filter(List<w_productos> models, String query) {
        query = query.toLowerCase();

        final List<w_productos> filteredModelList = new ArrayList<>();

        if(models != null)
            for (w_productos model : models) {
                final String text = model.getNombre().toLowerCase();
                final String text2 = model.getDescripcion().toLowerCase();
                if (text.contains(query) || text2.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        return filteredModelList;
    }


    private class GetProductos extends AsyncTask<Void, Void, ArrayList<w_productos>> {

        String mStrings;

        public GetProductos(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetProductos","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final ArrayList<w_productos> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getEmpresaProductos.php?id_empresa="+id_empresa);
                Log.v("GetProductos",url.toString());
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
                Log.v("GetProductos","doInBackground");
                try {
                    productos.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            productos.add(new w_productos(obj.getString("id"),obj.getString("nombre"),obj.getString("descripcion"),obj.getString("imagen_producto"),obj.getString("unidad"),obj.getString("precio"),obj.getString("talla"),obj.getString("marca"),obj.getString("dimension")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return productos;
        }

        @Override
        protected void onPostExecute(ArrayList<w_productos> result) {
            super.onPostExecute(result);
            Log.v("GetProductos","onPostExecute");
            if(!isDetached()) {
                productosAdapter = new ProductosAdapter(getActivity(), productos);
                empresaProducto.setHasFixedSize(true);
                empresaProducto.setLayoutManager(new GridLayoutManager(
                        getActivity(), getResources().getInteger(R.integer.list_columns)));
                empresaProducto.setAdapter(productosAdapter);

                if (productos.size() > 0)
                    nullTextView.setVisibility(View.INVISIBLE);
                else
                    nullTextView.setVisibility(View.VISIBLE);
            }


        }

    }
    
   private class ProductosAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final LayoutInflater inflater;
        private final List<w_productos> mProductos;
        private Context mContext;


        public ProductosAdapter(Context context, List<w_productos> mProductos) {
            inflater = LayoutInflater.from(context);
            this.mProductos = new ArrayList<>(mProductos);
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = inflater.inflate(R.layout.list_row_productos, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");
            //holder.nombre.setTypeface(typeface);
            holder.nombre.setText(mProductos.get(position).getNombre());
            holder.descripcion.setTypeface(typeface);
            holder.descripcion.setText(mProductos.get(position).getDescripcion());

            holder.unidad.setTypeface(typeface);
            holder.unidad.setText(mProductos.get(position).getUnidad());
            
            holder.precio.setTypeface(typeface);
            holder.precio.setText(mProductos.get(position).getPrecio());
            
            holder.marca.setTypeface(typeface);
            holder.marca.setText(mProductos.get(position).getMarca());
            
            holder.talla.setTypeface(typeface);
            holder.talla.setText(mProductos.get(position).getTalla());

            holder.dimension.setTypeface(typeface);
            holder.dimension.setText(mProductos.get(position).getDimension());

            if(!mProductos.get(position).getImagenProducto().equals("")) {
                Glide.with(mContext)
                        .load(WEBSERVER + "upload/" + mProductos.get(position).getImagenProducto())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.empty_photo)
                        .into(holder.imagen);
            }
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mProductos == null ? 0 : mProductos.size();
        }


        public void animateTo(List<w_productos> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        private void applyAndAnimateRemovals(List<w_productos> newModels) {
            for (int i =   mProductos.size() - 1; i >= 0; i--) {
                final w_productos model =   mProductos.get(i);
                if (!newModels.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<w_productos> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final w_productos model = newModels.get(i);
                if (!  mProductos.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<w_productos> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final w_productos model = newModels.get(toPosition);
                final int fromPosition =   mProductos.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        public w_productos removeItem(int position) {
            final w_productos model =  mProductos.remove(position);
            notifyItemRemoved(position);
            return model;
        }

        public void addItem(int position, w_productos model) {
            mProductos.add(position, model);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final w_productos model =  mProductos.remove(fromPosition);
            mProductos.add(toPosition, model);
            notifyItemMoved(fromPosition, toPosition);
        }
    }
    

    private static class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView nombre;
        TextView descripcion;
        ImageView imagen;
        TextView unidad;
        TextView precio;
        TextView talla;
        TextView marca;
        TextView dimension;

        public ViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.txtNombre);
            descripcion = (TextView) view.findViewById(R.id.txtDescripcion);
            imagen = (ImageView) view.findViewById(R.id.imgProducto);
            unidad = (TextView) view.findViewById(R.id.txtUnidad);
            precio = (TextView) view.findViewById(R.id.txtPrecio);
            talla = (TextView) view.findViewById(R.id.txtTalla);
            marca = (TextView) view.findViewById(R.id.txtMarca);
            dimension = (TextView) view.findViewById(R.id.txtDimension);
        }

    }

}

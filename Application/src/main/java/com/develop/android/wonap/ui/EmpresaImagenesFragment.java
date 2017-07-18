package com.develop.android.wonap.ui;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.Utils;
import com.develop.android.wonap.database.OfertaModel;
import com.develop.android.wonap.provider.GalleryActivity;
import com.develop.android.wonap.service.UtilityService;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EmpresaImagenesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id_empresa";
    @BindView(R.id.empresaImagenes)
    RecyclerView empresaImagenes;
    @BindView(R.id.nulltextView)
    TextView nullTextView;
    private boolean mItemClicked;
    private UserProfileAdapter userPhotosAdapter;
    ArrayList<String> images = new ArrayList<String>();
    String WEBSERVER;

    // TODO: Rename and change types of parameters
    private String id_empresa;


    public EmpresaImagenesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EmpresaImagenesFragment newInstance(String id_empresa) {
        EmpresaImagenesFragment fragment = new EmpresaImagenesFragment();
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
    public void onResume() {
        super.onResume();
        mItemClicked = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_imagenes, container, false);
        ButterKnife.bind(this, view);

        WEBSERVER = getActivity().getString(R.string.web_server);

        new GetImagenes().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }


    private class GetImagenes extends AsyncTask<Void, Void, ArrayList<String>> {

        String mStrings;

        public GetImagenes(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.v("GetImagenes","onPreExecute");
        }

        @SafeVarargs
        @Override
        protected final ArrayList<String> doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(WEBSERVER+"api/getEmpresaImagenes.php?id_empresa="+id_empresa);
                Log.v("GetImagenes",url.toString());
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
                Log.v("GetImagenes","doInBackground");
                try {
                    images.clear();
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            images.add(WEBSERVER+"upload/"+obj.getString("url"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            super.onPostExecute(result);
            Log.v("GetImagenes","onPostExecute");
            final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            empresaImagenes.setLayoutManager(layoutManager);
            userPhotosAdapter = new UserProfileAdapter(getActivity(), images);
            empresaImagenes.setAdapter(userPhotosAdapter);
            empresaImagenes.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    userPhotosAdapter.setLockedAnimations(true);
                }
            });
            if(images.size() > 0)
                nullTextView.setVisibility(View.INVISIBLE);
            else
                nullTextView.setVisibility(View.VISIBLE);
        }

    }


    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements ItemClickListener {

        private static final int PHOTO_ANIMATION_DELAY = 600;
        private final Interpolator INTERPOLATOR = new DecelerateInterpolator();


        private final Context context;
        private final int cellSize;

        private ArrayList<String> photos = new ArrayList<>();

        private boolean lockedAnimations = false;
        private int lastAnimatedItem = -1;

        public UserProfileAdapter(Context context, ArrayList<String> photos) {
            this.context = context;
            this.cellSize = Utils.getScreenWidth(context) / 3;
            this.photos = photos;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            layoutParams.setFullSpan(false);
            view.setLayoutParams(layoutParams);
            return new PhotoViewHolder(view, this);
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            bindPhoto((PhotoViewHolder) holder, position);
        }

        private void bindPhoto(final PhotoViewHolder holder, int position) {


            Picasso.with(context)
                    .load(photos.get(position))
                    .resize(cellSize, cellSize)
                    .centerCrop()
                    .into(holder.ivPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            animatePhoto(holder);
                        }

                        @Override
                        public void onError() {

                        }
                    });


            if (lastAnimatedItem < position) lastAnimatedItem = position;
        }

        private void animatePhoto(PhotoViewHolder viewHolder) {
            if (!lockedAnimations) {
                if (lastAnimatedItem == viewHolder.getPosition()) {
                    setLockedAnimations(true);
                }

                long animationDelay = PHOTO_ANIMATION_DELAY + viewHolder.getPosition() * 30;

                viewHolder.flRoot.setScaleY(0);
                viewHolder.flRoot.setScaleX(0);

                viewHolder.flRoot.animate()
                        .scaleY(1)
                        .scaleX(1)
                        .setDuration(200)
                        .setInterpolator(INTERPOLATOR)
                        .setStartDelay(animationDelay)
                        .start();
            }
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }

        @Override
        public void onItemClick(View view, int position) {
            if (!mItemClicked) {
                mItemClicked = true;
                ArrayList<String> temp = new ArrayList<>();
                temp.add(images.get(position));
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, temp);
                startActivity(intent);
            }
        }


        class PhotoViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
            @BindView(R.id.flRoot)
            FrameLayout flRoot;
            @BindView(R.id.ivPhoto)
            ImageView ivPhoto;

            ItemClickListener mItemClickListener;


            private PhotoViewHolder(View view, ItemClickListener itemClickListener) {
                super(view);
                ButterKnife.bind(this, view);
                mItemClickListener = itemClickListener;
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        private void setLockedAnimations(boolean lockedAnimations) {
            this.lockedAnimations = lockedAnimations;
        }
    }

}



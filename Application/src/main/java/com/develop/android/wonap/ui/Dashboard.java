package com.develop.android.wonap.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.develop.android.wonap.R;
import com.develop.android.wonap.common.CircleTransform;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;

import java.util.HashMap;

/**

 * Activities that contain this fragment must implement the

 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment implements BaseSliderView.OnSliderClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SliderLayout mDemoSlider;
    String nombre_completo;
    String image_user;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
  /*  RotatingTextWrapper rotatingTextWrapper;
    RotatingTextWrapper rotatingTextWrapper2;*/
    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(String param1, String param2) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);
        final String WEB_SERVER = getString(R.string.web_server);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        nombre_completo = preferences.getString("nombre_completo", "");
        image_user = preferences.getString("image_user", "");

        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout_ofertas);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Click Ofertas",Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout layout_noticias = (LinearLayout)view.findViewById(R.id.layout_noticas);
        layout_noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Click Noticias",Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout layout_empresa = (LinearLayout)view.findViewById(R.id.layout_empresa);
        layout_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Click Empresa",Toast.LENGTH_SHORT).show();
            }
        });
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Reckoner_Bold.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");

        TextView text_ofertas = (TextView)view.findViewById(R.id.custom_switcher);
        text_ofertas.setTypeface(typeface);
        text_ofertas.setTextColor(Color.parseColor("#FFA036"));
        text_ofertas.setTextSize(22);
        text_ofertas.setText("Lista de\r\nOfertas");
        TextView text_noticias = (TextView)view.findViewById(R.id.custom_switcher_noticias);
        text_noticias.setTypeface(typeface);
        text_noticias.setTextColor(Color.parseColor("#FFA036"));
        text_noticias.setTextSize(22);
        text_noticias.setText("Lista de\r\nNoticias");

        TextView text_user = (TextView)view.findViewById(R.id.text_welcome);
        text_user.setTypeface(typeface2);
        text_user.setTextColor(Color.WHITE);
        text_user.setTextSize(20);

        TextView text_empresa = (TextView)view.findViewById(R.id.text_empresa);
        text_empresa.setTypeface(typeface);
        text_empresa.setTextColor(Color.parseColor("#FFA036"));
        text_empresa.setTextSize(22);
        text_empresa.setText("Directorio\r\nde Empresas");



        ImageView imagen_usuario = (ImageView)view.findViewById(R.id.image_user);

        Glide.with(getActivity())
                .load(WEB_SERVER+"upload/"+image_user)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.empty_user)
                .transform(new CircleTransform(getActivity()))
                .override(80, 80)
                .into(imagen_usuario);

        String welcome = "Bienvenido a WONAP,\r\n"+nombre_completo.replace(",","\r\n");
        text_user.setText(welcome);
       /* rotatingTextWrapper = (RotatingTextWrapper) view.findViewById(R.id.custom_switcher);
        rotatingTextWrapper2 = (RotatingTextWrapper) view.findViewById(R.id.custom_switcher_noticias);
*/

        //CONFIGURAMOS EL BANNER CENTRAL
        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("1", WEB_SERVER+"img_main/1.jpg");
        url_maps.put("2", WEB_SERVER+"img_main/2.jpg");
        url_maps.put("3", WEB_SERVER+"img_main/3.jpg");
        url_maps.put("4", WEB_SERVER+"img_main/4.jpg");
        url_maps.put("5", WEB_SERVER+"img_main/5.jpg");
        url_maps.put("6", WEB_SERVER+"img_main/6.jpg");

        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    //.description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this)           ;


            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);


            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        //mDemoSlider.getRootView().findViewById(com.daimajia.slider.library.R.id.description_layout).setVisibility(View.INVISIBLE);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);



        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        /*Toast.makeText(getActivity(),"on resume",Toast.LENGTH_SHORT).show();
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Reckoner_Bold.ttf");
       //rotatingTextWrapper.setSize(35);
        Rotatable rotatable = new Rotatable(Color.parseColor("#FFA036"), 1000, "Busca tus", "Ofertas", "Cercanas");
        rotatable.setSize(30);
        rotatable.setAnimationDuration(500);
        rotatable.setTypeface(typeface);
        rotatable.setCenter(true);
        rotatingTextWrapper.setContent("", rotatable);

        //rotatingTextWrapper.setSize(35);
        Rotatable rotatable2 = new Rotatable(Color.parseColor("#FFA036"), 1000, "Revisa las", "Noticias", "Recientes");
        rotatable2.setSize(30);
        rotatable2.setAnimationDuration(500);
        rotatable2.setTypeface(typeface);
        rotatable2.setCenter(true);
        rotatingTextWrapper2.setContent("", rotatable2);*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

}

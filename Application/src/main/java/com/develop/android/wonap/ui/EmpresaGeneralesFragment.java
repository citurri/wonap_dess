package com.develop.android.wonap.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.develop.android.wonap.R;


public class EmpresaGeneralesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "historia";
    private static final String ARG_PARAM2 = "valores";

    // TODO: Rename and change types of parameters
    private String historia;
    private String valores;

    public EmpresaGeneralesFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EmpresaGeneralesFragment newInstance(String historia, String valores) {
        EmpresaGeneralesFragment fragment = new EmpresaGeneralesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, historia);
        args.putString(ARG_PARAM2, valores);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            historia = getArguments().getString(ARG_PARAM1);
            valores = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_empresa_generales, container, false);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LoveYaLikeASister.ttf");

        TextView historiaTitleTextView = (TextView) view.findViewById(R.id.historia_title);
        TextView valoresTitleTextView = (TextView) view.findViewById(R.id.valores_title);
        TextView historiaTextView = (TextView) view.findViewById(R.id.historia_text);
        TextView valoresTextView = (TextView) view.findViewById(R.id.valores_text);

        historiaTitleTextView.setTypeface(typeface);
        historiaTitleTextView.setTextColor(getResources().getColor(R.color.dark));
        valoresTitleTextView.setTypeface(typeface);
        valoresTitleTextView.setTextColor(getResources().getColor(R.color.dark));
        historiaTextView.setTypeface(typeface);
        valoresTextView.setTypeface(typeface);

        historiaTitleTextView.setText("HISTORIA");
        valoresTitleTextView.setText("VALORES");

        historiaTextView.setText(historia);
        valoresTextView.setText(valores);



        return view;
    }


}

package com.develop.android.wonap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.develop.android.wonap.common.CircleTransform;
import com.develop.android.wonap.provider.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.develop.android.wonap.R;
import com.develop.android.wonap.provider.RevealBackgroundView;

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
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

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

    private int avatarSize;
    private String profilePhoto;


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
        this.profilePhoto = getString(R.string.user_profile_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        Glide.with(this)
                .load(profilePhoto)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.img_circle_placeholder)
                .centerCrop()
                .transform(new CircleTransform(this))
                .into(ivUserProfilePhoto);

        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupUserProfileGrid() {

        final String id_empresa = getIntent().getStringExtra("id_empresa");





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
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            //userPhotosAdapter = new UserProfileAdapter(this);
           // rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
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
}

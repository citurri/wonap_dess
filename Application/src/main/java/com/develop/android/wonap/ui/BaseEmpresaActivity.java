package com.develop.android.wonap.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.zakariya.stickyheaders.SectioningAdapter;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;
import com.develop.android.wonap.R;

import java.util.ArrayList;

/**
 * Base activity for StickyHeadersApp demos
 */
public class BaseEmpresaActivity extends AppCompatActivity {

	private static final String TAG = BaseEmpresaActivity.class.getSimpleName();
	private static final String STATE_SCROLL_POSITION = "DemoActivity.STATE_SCROLL_POSITION";

	public static final boolean SHOW_ADAPTER_POSITIONS = true;

	AppBarLayout appBarLayout;
	CollapsingToolbarLayout collapsingToolbarLayout;
	ProgressBar progressBar;
	Spinner spinner;
	public FloatingActionButton maps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewLayout());

		appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		maps = (FloatingActionButton) findViewById(R.id.mapFab);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (toolbar != null) {
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
		}


		spinner = (Spinner) findViewById(R.id.spinCategorias);
	}

	@LayoutRes
	protected int getContentViewLayout(){
		return R.layout.activity_base_empresa;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
		//Parcelable scrollState = lm.onSaveInstanceState();
		//outState.putParcelable(STATE_SCROLL_POSITION, scrollState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			//recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	class ScrollDialogSingleChoiceItemSelection {
		int which = -1;
	}


}

package com.toddway.sandbox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends TransitionHelper.BaseActivity {
    protected static String BASE_FRAGMENT = "base_fragment";
    public @InjectView(R.id.toolbar) Toolbar toolbar;
    public @InjectView(R.id.material_menu_button) MaterialMenuView homeButton;
    public @InjectView(R.id.toolbar_title) TextView toolbarTitle;
    public @InjectView(R.id.fab) Button fab;
    public @InjectView(R.id.drawerLayout) DrawerLayout drawerLayout;
    public @InjectView(R.id.base_fragment_background) View fragmentBackround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.inject(this);
        initToolbar();
        initBaseFragment(savedInstanceState);
    }

    private void initToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void initBaseFragment(Bundle savedInstanceState) {
        //apply background bitmap if we have one
        if (getIntent().hasExtra("bitmap_id")) {
            fragmentBackround.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent())));
        }

        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = getFragmentManager().findFragmentByTag(BASE_FRAGMENT);
        }
        if (fragment == null) fragment = getBaseFragment();
        setBaseFragment(fragment);
    }

    protected int getLayoutResource() {
        return R.layout.activity_base;
    };

    protected Fragment getBaseFragment() {
        int fragmentResourceId = getIntent().getIntExtra("fragment_resource_id", R.layout.fragment_thing_list);
        switch (fragmentResourceId) {
            case R.layout.fragment_thing_list:
            default:
                return new ThingListFragment();
            case R.layout.fragment_thing_detail:
                return ThingDetailFragment.create();
            case R.layout.fragment_overaly:
                return new OverlayFragment();
        }
    }

    public void setBaseFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.base_fragment, fragment, BASE_FRAGMENT);
        transaction.commit();
    }


    private MaterialMenuDrawable.IconState currentIconState;
    public boolean animateHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return false;
        currentIconState = iconState;
        homeButton.animateState(currentIconState);
        return true;
    }

    public void setHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return;
        currentIconState = iconState;
        homeButton.setState(currentIconState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onBeforeBack() {
        ActivityCompat.finishAfterTransition(this);
        return false;
    }

    public static BaseActivity of(Activity activity) {
        return (BaseActivity) activity;
    }

}

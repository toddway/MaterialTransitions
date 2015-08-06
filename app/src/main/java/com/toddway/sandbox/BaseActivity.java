package com.toddway.sandbox;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends TransitionHelper.BaseActivity {
    public @InjectView(R.id.toolbar) Toolbar toolbar;
    public @InjectView(R.id.material_menu_button) MaterialMenuView homeButton;
    public @InjectView(R.id.toolbar_title) TextView toolbarTitle;
    public @InjectView(R.id.fab) Button fab;
    public @InjectView(R.id.root_container) DrawerLayout rootContainer;
    public @InjectView(R.id.main_view_background) View mainViewBackground;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.inject(this);
        initToolbar();
        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        initMainView(bundle);
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

    private void initMainView(Bundle bundle) {
        if (bundle.containsKey("bitmap_id")) {
            mainViewBackground.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent()))); //TODO replace intent arg with bundle
        }

        int resId = bundle.getInt("fragment_resource_id", R.layout.thing_list_layout);
        View v = View.inflate(this, resId, (ViewGroup) findViewById(R.id.main_view));
    }

    protected int getLayoutResource() {
        return R.layout.activity_base;
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

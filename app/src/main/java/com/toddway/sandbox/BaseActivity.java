package com.toddway.sandbox;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends ActionBarActivity {
    protected static String BASE_FRAGMENT = "base_fragment";
    public @InjectView(R.id.toolbar) Toolbar toolbar;
    public @InjectView(R.id.material_menu_button) MaterialMenuView homeButton;
    public @InjectView(R.id.toolbar_title) TextView toolbarTitle;
    public @InjectView(R.id.fab) Button fab;
    @InjectView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @InjectView(R.id.base_fragment_background) View fragmentBackround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        ButterKnife.inject(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
        }

        initBaseFragment(savedInstanceState);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initSharedElementTransition();
    }

    private void initBaseFragment(Bundle savedInstanceState) {
        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = getFragmentManager().findFragmentByTag(BASE_FRAGMENT);
        }
        if (fragment == null) fragment = getBaseFragment();
        setBaseFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) getFragmentManager().findFragmentByTag(BASE_FRAGMENT);
        fragment.onBackPressed();
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

    @TargetApi(21)
    public void initSharedElementTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Postpone the transition until the window's decor view has finished its layout (so shared elements don't layout in front of decor views).
            postponeEnterTransition();
            final View decor = getWindow().getDecorView();
            decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    decor.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });

            //exclude our full screen container from transitions (so it doesn't flash)
            Transition transition = getWindow().getEnterTransition();
            transition.excludeTarget(R.id.full_screen, true);
            getWindow().setEnterTransition(transition);

            //apply background bitmap if we have one
            if (getIntent().hasExtra("bitmap_id")) {
                fragmentBackround.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent())));
            }


            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    if (fragmentBackround.getScaleX() == 1) { //forward transition
                        fragmentBackround.animate().scaleX(.95f).scaleY(.95f).alpha(.3f).start();
                        homeButton.setState(MaterialMenuDrawable.IconState.BURGER);
                        homeButton.animateState(MaterialMenuDrawable.IconState.ARROW);

                    } else { //reverse transition
                        fragmentBackround.animate().scaleX(1).scaleY(1).alpha(1).translationY(0).start();
                        homeButton.animateState(MaterialMenuDrawable.IconState.BURGER);
                    }
                }

                @Override
                public void onTransitionEnd(Transition transition) {

                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }
}

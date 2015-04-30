package com.toddway.sandbox;


import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.Window;

public class Navigator {

    public static int ANIM_DURATION = 350;

    public static void launchDetail(BaseActivity fromActivity, View fromView, Thing item, View backgroundView) {
        ViewCompat.setTransitionName(fromView, "detail_element");
        ViewCompat.setTransitionName(fromActivity.findViewById(R.id.fab), "fab");
        //ViewCompat.setTransitionName(fromActivity.findViewById(R.id.toolbar_container), "toolbar_container");
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        fromActivity,
                        Pair.create(fromView, "detail_element"),
                        Pair.create(fromActivity.findViewById(R.id.fab), "fab"),
                        //Pair.create(fromActivity.findViewById(R.id.toolbar_container), "toolbar_container"),
                        Pair.create(fromActivity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
                );
        Intent intent = new Intent(fromActivity, BaseActivity.class);
        intent.putExtra("item_text", item.text);
        intent.putExtra("fragment_resource_id", R.layout.fragment_thing_detail);

        if (backgroundView != null) BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());

        //fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

    public static void launchOverlay(BaseActivity fromActivity, View fromView, View backgroundView) {
        //ViewCompat.setTransitionName(fromActivity.findViewById(R.id.fab), "fab");
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        fromActivity,
                        Pair.create(fromActivity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
                );
        Intent intent = new Intent(fromActivity, BaseActivity.class);
        intent.putExtra("fragment_resource_id", R.layout.fragment_overaly);

        if (backgroundView != null) BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);

        ActivityCompat.startActivity(fromActivity, intent, options.toBundle());

        //fromActivity.overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
    }

}

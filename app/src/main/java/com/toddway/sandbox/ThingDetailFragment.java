package com.toddway.sandbox;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThingDetailFragment extends BaseFragment {

    @InjectView(R.id.detail_title) TextView titleTextView;
    @InjectView(R.id.detail_layout) View detailLayout;
    @InjectView(R.id.detail_body) TextView detailBodyTextView;
    @InjectView(R.id.overscroll_view) OverScrollView overScrollView;

    public static ThingDetailFragment create() {
        ThingDetailFragment f = new ThingDetailFragment();
        return f;
    }

    public ThingDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thing_detail, container, false);
        ButterKnife.inject(this, rootView);
        getBaseActivity().homeButton.animateState(MaterialMenuDrawable.IconState.ARROW);
        getBaseActivity().fab.setTranslationY(400);
        String itemText = getActivity().getIntent().getStringExtra("item_text");
        titleTextView.setText(itemText);
        detailBodyTextView.setAlpha(0);
        overScrollView.setOverScrollListener(new OverScrollView.OverScrollListener() {
            @Override
            public void onOverScrolled() {
                getBaseActivity().onBackPressed();
            }
        });

        initSharedElementTransition();

        return rootView;
    }


    @TargetApi(21)
    private void initSharedElementTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(detailLayout, "detail_element");
            ViewCompat.setTransitionName(getBaseActivity().findViewById(R.id.fab), "fab");
            ViewCompat.setTransitionName(getBaseActivity().findViewById(R.id.toolbar_container), "toolbar_container");
            getActivity().getWindow().setSharedElementsUseOverlay(false);
            getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    detailBodyTextView.animate().alpha(0).start();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    detailBodyTextView.animate().alpha(1).start();
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

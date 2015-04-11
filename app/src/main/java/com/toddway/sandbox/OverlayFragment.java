package com.toddway.sandbox;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OverlayFragment extends BaseFragment {

    @InjectView(R.id.overlay) RelativeLayout overlayLayout;

    public OverlayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overaly, container, false);
        ButterKnife.inject(this, rootView);
        getBaseActivity().fab.setVisibility(View.GONE);
        initSharedElementTransition();
        return rootView;
    }


    @TargetApi(21)
    private void initSharedElementTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            overlayLayout.setVisibility(View.INVISIBLE);
            ViewCompat.setTransitionName(getBaseActivity().findViewById(R.id.fab), "fab");
            getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    if (overlayLayout.getVisibility() == View.INVISIBLE) {
                        animateRevealShow(overlayLayout);
                    } else {
                        animateRevealHide(overlayLayout);
                    }
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    //getBaseActivity().fab.setBackground(null); //setTranslationY(400);
                    //getBaseActivity().fab.setText("");
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

    public void animateRevealShow(View viewRoot) {
        View fab = getBaseActivity().fab;
        int cx = viewRoot.getRight() - 140;
        int cy = viewRoot.getBottom() - 140;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(300);
        anim.start();
    }

    public void animateRevealHide(final View viewRoot) {
        int cx = viewRoot.getRight() - 140;
        int cy = viewRoot.getBottom() - 140;
        int initialRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(300);
        anim.start();
    }
}

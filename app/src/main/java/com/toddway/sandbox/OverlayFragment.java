package com.toddway.sandbox;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OverlayFragment extends TransitionHelper.BaseFragment {

    @InjectView(R.id.overlay) RelativeLayout overlayLayout;
    @InjectView(R.id.text_view) TextView textView;

    public OverlayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overaly, container, false);
        ButterKnife.inject(this, rootView);
        initBodyText();
        return rootView;
    }

    private void initBodyText() {
        textView.setText("v1.0.0");
        textView.setAlpha(0);
        textView.setTranslationY(100);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                textView.animate()
                        .alpha(1)
                        .setStartDelay(Navigator.ANIM_DURATION/3)
                        .setDuration(Navigator.ANIM_DURATION*5)
                        .setInterpolator(new DecelerateInterpolator(9))
                        .translationY(0)
                        .start();
            }
        }, 200);
    }

    @Override
    public void onBeforeEnter(View contentView) {
        overlayLayout.setVisibility(View.INVISIBLE);
        BaseActivity.of(getActivity()).setHomeIcon(MaterialMenuDrawable.IconState.BURGER);
        BaseActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public void onAfterEnter() {
        animateRevealShow(overlayLayout);
    }

    @Override
    public boolean onBeforeBack() {
        animateRevealHide(overlayLayout);
        BaseActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.BURGER);
        return false;
    }

    @Override
    public void onBeforeViewShows(View contentView) {
        ViewCompat.setTransitionName(getActivity().findViewById(R.id.fab), "fab");
        BaseActivity.of(getActivity()).fab.setVisibility(View.INVISIBLE);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.toolbar_container, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.full_screen, true);
        TransitionHelper.excludeEnterTarget(getActivity(), R.id.overlay, true);
    }

//    @TargetApi(21)
//    private void initSharedElementTransition() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            overlayLayout.setVisibility(View.INVISIBLE);
//            ViewCompat.setTransitionName(getBaseActivity().findViewById(R.id.fab), "fab");
//            getActivity().getWindow().getSharedElementEnterTransition().setDuration(Navigator.ANIM_DURATION);
//            getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
//                @Override
//                public void onTransitionStart(Transition transition) {
//                    if (overlayLayout.getVisibility() == View.INVISIBLE) {
//                        animateRevealShow(overlayLayout);
//                    } else {
//                        animateRevealHide(overlayLayout);
//                    }
//                }
//
//                @Override
//                public void onTransitionEnd(Transition transition) {
//
//                }
//
//                @Override
//                public void onTransitionCancel(Transition transition) {
//
//                }
//
//                @Override
//                public void onTransitionPause(Transition transition) {
//
//                }
//
//                @Override
//                public void onTransitionResume(Transition transition) {
//
//                }
//            });
//        }
//    }

    public void animateRevealShow(View viewRoot) {
        View fab = BaseActivity.of(getActivity()).fab;
        int cx = fab.getLeft() + (fab.getWidth()/2); //middle of button
        int cy = fab.getTop() + (fab.getHeight()/2); //middle of button
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, radius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(Navigator.ANIM_DURATION);
        anim.start();


    }

    public void animateRevealHide(final View viewRoot) {
        View fab = BaseActivity.of(getActivity()).fab;
        int cx = fab.getLeft() + (fab.getWidth()/2); //middle of button
        int cy = fab.getTop() + (fab.getHeight()/2); //middle of button
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2)); //hypotenuse to top left

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, radius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        //anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(Navigator.ANIM_DURATION);
        anim.start();

        Integer colorTo = getResources().getColor(R.color.primaryColor);
        Integer colorFrom = getResources().getColor(android.R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                overlayLayout.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setInterpolator(new AccelerateInterpolator(2));
        colorAnimation.setDuration(Navigator.ANIM_DURATION);
        colorAnimation.start();
    }
}

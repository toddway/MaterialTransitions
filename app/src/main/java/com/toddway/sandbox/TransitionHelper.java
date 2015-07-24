package com.toddway.sandbox;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.transition.Transition;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides extra lifecycle events and shims for shared element transitions
 * See the included BaseActivity and BaseFragment for example use
 */
public class TransitionHelper {

    Activity activity;

    private TransitionHelper(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
        isAfterEnter = savedInstanceState != null; //if saved instance is not null we've already "entered"
        postponeEnterTransition(); //we postpone to prevent status and nav bars from flashing during shared element transitions
    }

    /**
     * Should be called from Activity.onResume()
     */
    public void onResume() {
        if (isAfterEnter) return;

        if (!isViewCreatedAlreadyCalled) onViewCreated();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onAfterEnter();
        } else {
            activity.getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    if (isAfterEnter()) for (Listener listener : listeners) listener.onBeforeReturn();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    if (!isAfterEnter()) onAfterEnter();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    if (!isAfterEnter()) onAfterEnter();
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

    /**
     * Should be called from Activity.onBackPressed()
     */
    public void onBackPressed() {
        boolean isConsumed = false;
        for (Listener listener : listeners) {
            isConsumed = listener.onBeforeBack() || isConsumed;
        }
        if (!isConsumed) ActivityCompat.finishAfterTransition(activity);
    }

    /**
     * Should be called immediately after all shared transition views are inflated.
     * If using fragments, recommend calling at the beginning of Fragment.onViewCreated().
     */
    private boolean isViewCreatedAlreadyCalled = false;
    public void onViewCreated() {
        if (isViewCreatedAlreadyCalled) return;
        isViewCreatedAlreadyCalled = true;

        View contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        for (Listener listener : listeners) listener.onBeforeViewShows(contentView);
        if (!isAfterEnter()) { for (Listener listener : listeners) listener.onBeforeEnter(contentView); }

        if (isPostponeEnterTransition) startPostponedEnterTransition();
    }

    /**
     * Call from Activity.onSaveInstanceState()
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isAfterEnter", isAfterEnter);
    }

    /**
     * A parent object that owns an instance of TransitionHelper
     * Your Activity should implement Source and call TransitionHelper.init() from Activity.onCreate()
     */
    public interface Source {
        /**
         * Getter for TransitionHelper instance
         * @return
         */
        TransitionHelper getTransitionHelper();

        /**
         * Setter for TransitionHelper instance
         * @param transitionHelper
         */
        void setTransitionHelper(TransitionHelper transitionHelper);
    }



    /**
     * Listens for extra transition events
     * Activities, Fragments, and other views should implement Listener and call TransitionHelper.of(...).addListener(this)
     */
    public interface Listener {
        /**
         * Called during every onViewCreated
         * @param contentView
         */
        void onBeforeViewShows(View contentView);

        /**
         * Called during onViewCreated only on an enter transition
         * @param contentView
         */
        void onBeforeEnter(View contentView);

        /**
         * Called after enter transition is finished for L+, otherwise called immediately during first onResume
         */
        void onAfterEnter();


        /**
         * Called during Activity.onBackPressed()
         * @return true if the listener has consumed the event, false otherwise
         */
        boolean onBeforeBack();

        void onBeforeReturn();
    }

    private List<Listener> listeners = new ArrayList<>();
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    private void onAfterEnter() {
        for (Listener listener : listeners) listener.onAfterEnter();
        isAfterEnter = true;
    }

    private boolean isAfterEnter = false;
    public boolean isAfterEnter() { return isAfterEnter; }

    private boolean isPostponeEnterTransition = false;
    private void postponeEnterTransition() {
        if (isAfterEnter) return;
        ActivityCompat.postponeEnterTransition(activity);
        isPostponeEnterTransition = true;
    }


    private void startPostponedEnterTransition() {
        final View decor = activity.getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(activity);
                return true;
            }
        });
    }

    public static void excludeEnterTarget(Activity activity, int targetId, boolean exclude) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getEnterTransition().excludeTarget(targetId, exclude);
        }
    }


    //STATICS:
    /**
     * Get the TransitionHelper object for an Activity
     * @param a
     * @return
     */
    public static TransitionHelper of(Activity a) {
        return ((Source) a).getTransitionHelper();
    }

    /**
     * Initialize the TransitionHelper object.  Should be called at the beginning of Activity.onCreate()
     * @param source
     * @param savedInstanceState
     */
    public static void init(Source source, Bundle savedInstanceState) {
        source.setTransitionHelper(new TransitionHelper((Activity) source, savedInstanceState));
    }

    public static ActivityOptionsCompat makeOptionsCompat(Activity fromActivity, Pair<View, String>... sharedElements) {
        ArrayList<Pair<View, String>> list = new ArrayList<>(Arrays.asList(sharedElements));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list.add(Pair.create(fromActivity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
            list.add(Pair.create(fromActivity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }

        //remove any views that are null
        for (ListIterator<Pair<View, String>> iter = list.listIterator(); iter.hasNext();) {
            Pair pair = iter.next();
            if (pair.first == null) iter.remove();
        }

        sharedElements = list.toArray(new Pair[list.size()]);
        return ActivityOptionsCompat.makeSceneTransitionAnimation(
                fromActivity,
                sharedElements
        );
    }

    public static void fadeThenFinish(View v, final Activity a) {
        if (v != null) {
            v.animate()  //fade out the view before finishing the activity (for cleaner L transition)
                    .alpha(0)
                    .setDuration(100)
                    .setListener(
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    ActivityCompat.finishAfterTransition(a);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }
                    )
                    .start();
        }
    }

    public static class BaseActivity extends ActionBarActivity implements TransitionHelper.Source, TransitionHelper.Listener {
        TransitionHelper transitionHelper;

        @Override
        public TransitionHelper getTransitionHelper() {
            return transitionHelper;
        }

        @Override
        public void setTransitionHelper(TransitionHelper transitionHelper) {
            this.transitionHelper = transitionHelper;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            TransitionHelper.init(this, savedInstanceState);
            TransitionHelper.of(this).addListener(this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            TransitionHelper.of(this).onSaveInstanceState(outState);
            super.onSaveInstanceState(outState);
        }

        @Override
        protected void onResume() {
            TransitionHelper.of(this).onResume();
            super.onResume();
        }

        @Override
        public void onBackPressed() {
            TransitionHelper.of(this).onBackPressed();
        }


        @Override
        public void onBeforeViewShows(View contentView) {

        }

        @Override
        public void onBeforeEnter(View contentView) {

        }

        @Override
        public void onAfterEnter() {

        }

        @Override
        public boolean onBeforeBack() {
            return false;
        }

        @Override
        public void onBeforeReturn() {

        }
    }

    public static class BaseFragment extends Fragment implements TransitionHelper.Listener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            TransitionHelper.of(getActivity()).addListener(this);
            super.onCreate(savedInstanceState);

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            TransitionHelper.of(getActivity()).onViewCreated();
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onBeforeViewShows(View contentView) {

        }

        @Override
        public void onBeforeEnter(View contentView) {

        }

        @Override
        public void onAfterEnter() {

        }

        @Override
        public boolean onBeforeBack() {
            return false;
        }

        @Override
        public void onBeforeReturn() {

        }
    }


}

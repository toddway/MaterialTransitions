package com.toddway.sandbox;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;

/**
 * Created by tway on 4/10/15.
 */
public class OverScrollView extends ScrollView {


    public OverScrollView(Context context) {
        super(context);
    }

    public OverScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mLastMotionY;
    private int maxTranslation = 100;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (Math.abs(getTranslationY()) > maxTranslation) {
                    listener.onOverScrolled();
                } else {
                    animate().translationY(0).setDuration(200).setInterpolator(new DecelerateInterpolator(2)).start();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (getScrollY() == 0) {
                    applyOverscroll(ev, false);
                } else {
                    View view = getChildAt(getChildCount() - 1);
                    if (view.getHeight() <= (getHeight() + getScrollY())) {
                        applyOverscroll(ev, true);
                    }
                }
                break;
        }

        if (getTranslationY() > 0 || getTranslationY() < 0) {
            return true;
        }
        return super.onTouchEvent(ev);

    }

    public static interface OverScrollListener {
        public void onOverScrolled();
    }

    private OverScrollListener listener;
    public void setOverScrollListener(OverScrollListener listener) {
        this.listener = listener;
    }

    private void applyOverscroll(MotionEvent ev, boolean isBottom) {
        int pointerCount = ev.getHistorySize();
        for (int p = 0; p < pointerCount; p++) {
            int historicalY = (int) ev.getHistoricalY(p);
            int translation = (historicalY - mLastMotionY);
            translation = translation / (translation > (maxTranslation*.86) ? 6 : 4);

            Log.d("stupid", "trans:" + translation);

            if (Math.abs(translation) <= (maxTranslation*1.5)) {
                if ((isBottom && translation < 0) || (!isBottom && translation > 0)) {
                    setTranslationY(translation);
                }
            }
        }
    }
}

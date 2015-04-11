package com.toddway.sandbox;

import android.app.Fragment;
import android.support.v4.app.ActivityCompat;


public class BaseFragment extends Fragment {


    BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(getBaseActivity());
    }
}

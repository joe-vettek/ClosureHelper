package com.xueluoanping.arknights.base;

import android.util.Log;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private static final String TAG = Fragment.class.getSimpleName();

    public void safeRunOnUiThread(Runnable runnable) {
        if (getActivity() != null && !getActivity().isDestroyed()) {
            getActivity().runOnUiThread(runnable);
        } else {
            Log.e(TAG, "safeRunOnUiThread: Try run ui Thread on a nonexistent Activity.\n" + runnable);
        }
    }
}

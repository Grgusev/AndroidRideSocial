package com.pjtech.android.ridesocial.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by android on 6/9/17.
 */

public class Tool {
    public static Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        int maxWidth = 512, maxHeight = 512;

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    public static void showViewAnimation(final View hideView, final View showView)
    {
        hideView.animate()
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideView.setVisibility(View.GONE);

                        showView.setAlpha(0);
                        showView.setVisibility(View.VISIBLE);

                        showView.animate()
                                .alpha(1.0f)
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                    }
                                });
                    }
                });
    }
}

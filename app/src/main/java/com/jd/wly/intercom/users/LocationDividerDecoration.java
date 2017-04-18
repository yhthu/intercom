package com.jd.wly.intercom.users;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yanghao1 on 2017/4/6.
 */

public class LocationDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable divider;
    private int height;

    /**
     * Default divider will be used
     */
    public LocationDividerDecoration(Context context, int height) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
        this.height = height;
    }

    /**
     * Custom divider will be used
     */
    public LocationDividerDecoration(Context context, int resId, int height) {
        divider = ContextCompat.getDrawable(context, resId);
        this.height = height;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight() + height;

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}

package com.curry.stephen.contentprovidertest;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LingChong on 2016/3/17 0017.
 */
public class CascadeLayout extends ViewGroup {

    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    public CascadeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get values of CascadeLayout_specific attribute.
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CascadeLayout);
        try {
            mHorizontalSpacing = typedArray.getDimensionPixelOffset(R.styleable.CascadeLayout_horizontal_spacing,
                    getResources().getDimensionPixelOffset(R.dimen.cascade_horizontal_spacing));

            mVerticalSpacing = typedArray.getDimensionPixelOffset(R.styleable.CascadeLayout_vertical_spacing,
                    getResources().getDimensionPixelOffset(R.dimen.cascade_vertical_spacing));
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int childWidth = 0;
        int childHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);// Ask child to measure itself.

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams(); // Get layout parameters of child.

            // Set the layout parameters of child.
            width = getPaddingLeft() + mHorizontalSpacing * i;
            height = getPaddingTop() + mVerticalSpacing * i;
            layoutParams.x = width;

            int layoutVerticalSpacing = 0;
            if (layoutParams.mLayoutVerticalSpacing >= 0) {
                layoutVerticalSpacing = layoutParams.mLayoutVerticalSpacing;
            }
            layoutParams.y = height + layoutVerticalSpacing;

            // Record the space of children.
            childWidth += child.getMeasuredWidth();
            childHeight += child.getMeasuredHeight();
        }

        childWidth += getPaddingLeft() + getPaddingRight();
        childHeight += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSize(childWidth, widthMeasureSpec), resolveSize(childHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            child.layout(layoutParams.x, layoutParams.y, layoutParams.x + child.getMeasuredWidth(),
                    layoutParams.y + child.getMeasuredHeight());
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    // This is the class for children of CascadeLayout which include layout_xxx attributes.
    public static class LayoutParams extends ViewGroup.LayoutParams {

        private int x;
        private int y;
        public int mLayoutVerticalSpacing;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Get the value of CascadeLayout_specific layout.
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.CascadeLayout_LayoutParams);
            try {
                   mLayoutVerticalSpacing =
                           typedArray.getDimensionPixelSize(R.styleable.CascadeLayout_LayoutParams_layout_vertical_spacing, -1);
            } finally {
                typedArray.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}

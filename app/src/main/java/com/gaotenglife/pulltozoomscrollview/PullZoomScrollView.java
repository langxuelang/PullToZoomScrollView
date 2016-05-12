package com.gaotenglife.pulltozoomscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * @author 高腾
 * @Package com.bangstudy.xue.view.custom
 * @Description: 下拉放大的scrollview
 * @date 15/12/29 下午3:57
 */
public class PullZoomScrollView extends ScrollView{

    public RelativeLayout mImageView;

    private int mDrawableMaxHeight = -1;
    private int mImageViewHeight = -1;
    private int mDefaultImageViewHeight = 0;
    private double mZoomRatio = 2;
    private int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;
    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    private boolean mIsIntercept = false;

    private float xFirst,yFirst;

    private Context mContext;

    private float mTouchSlop;

    public PullZoomScrollView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PullZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    public PullZoomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        init();
    }
    public void init() {
        mDefaultImageViewHeight = 200;
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = configuration.getScaledTouchSlop();
    }
    private interface OnOverScrollByListener {
        boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                             int scrollY, int scrollRangeX, int scrollRangeY,
                             int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
    }

    private interface OnTouchEventListener {
        void onTouchEvent(MotionEvent ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initViewsBounds(mZoomRatio);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("scrollview",event.getAction()+"");
        touchListener.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if(ev.getAction() == MotionEvent.ACTION_DOWN){
//            return false;
//        }else {
//            return true;
//        }
//    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xFirst = event.getX();
                yFirst = event.getY();
                mIsIntercept = false;
                super.onInterceptTouchEvent(event);

            case MotionEvent.ACTION_MOVE:
                xDistance = event.getX()-xFirst;
                yDistance = event.getY()-yFirst;
                if(Math.abs(yDistance)>mTouchSlop){
                    mIsIntercept = true;
                }else {
                    mIsIntercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsIntercept = false;
                break;
            default:
                break;
        }
        return mIsIntercept;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return true;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        boolean isCollapseAnimation = false;

        //Log.e("overScrollBy",deltaY+"");
        isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY,
                scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
                maxOverScrollY, isTouchEvent)
                || isCollapseAnimation;

        return isCollapseAnimation ? true : super.overScrollBy(deltaX, deltaY,
                scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
                maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
        @Override
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                    int scrollY, int scrollRangeX, int scrollRangeY,
                                    int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
            if (isTouchEvent) {
                if (deltaY < 0) {
                    if (mImageView.getHeight() - deltaY / 2 >= mImageViewHeight && getScrollY() == 0) {
                        mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY / 2 >= mImageViewHeight ?
                                mImageView.getHeight() - deltaY / 2 : mImageViewHeight;
                        mImageView.requestLayout();
                    }
                } else {
                    if (mImageView.getHeight() > mImageViewHeight) {
                        mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY > mImageViewHeight ?
                                mImageView.getHeight() - deltaY : mImageViewHeight;
                        mImageView.requestLayout();
                        return true;
                    }
                }
            }
            return false;
        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        //Log.e("onScrollChanged","l:"+l+"t"+t);
        View firstView = (View) mImageView.getParent();
        // firstView.getTop < getPaddingTop means mImageView will be covered by top padding,
        // so we can layout it to make it shorter
        if (firstView.getTop() < getPaddingTop() && mImageView.getHeight() > mImageViewHeight) {
            mImageView.getLayoutParams().height = Math.max(mImageView.getHeight() - (getPaddingTop() - firstView.getTop()), mImageViewHeight);
            // to set the firstView.mTop to 0,
            // maybe use View.setTop() is more easy, but it just support from Android 3.0 (API 11)
            firstView.layout(firstView.getLeft(), 0, firstView.getRight(), firstView.getHeight());
            mImageView.requestLayout();
        }
    }
    private void initViewsBounds(double zoomRatio) {
        if (mImageViewHeight == -1) {
            mImageViewHeight = mImageView.getHeight();
            if (mImageViewHeight <= 0) {
                mImageViewHeight = mDefaultImageViewHeight;
            }
        }
    }
    private OnTouchEventListener touchListener = new OnTouchEventListener() {
        @Override
        public void onTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                if (mImageViewHeight - 1 < mImageView.getHeight()) {
                    ResetAnimimation animation = new ResetAnimimation(
                            mImageView, mImageViewHeight);
                    animation.setDuration(300);
                    mImageView.startAnimation(animation);
                }
            }
        }
    };

    public class ResetAnimimation extends Animation {
        int targetHeight;
        int originalHeight;
        int extraHeight;
        View mView;

        protected ResetAnimimation(View view, int targetHeight) {
            this.mView = view;
            this.targetHeight = targetHeight;
            originalHeight = view.getHeight();
            extraHeight = this.targetHeight - originalHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {

            int newHeight;
            newHeight = (int) (targetHeight - extraHeight * (1 - interpolatedTime));
            mView.getLayoutParams().height = newHeight;
            mView.requestLayout();
        }
    }

}

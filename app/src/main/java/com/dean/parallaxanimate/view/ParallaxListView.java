package com.dean.parallaxanimate.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/3/8.
 */
public class ParallaxListView extends ListView {
    private static final String TAG = "ParallaxListView";
    /**
     * 头部图片控件原始高度
     */
    private int mHeadViewOriginHeight;
    /**
     * 头部图片原始高度
     */
    private int mHeadPicHeight;
    /**
     * 头部图片View对象
     */
    private ImageView mIvHeadImage;

    public ParallaxListView(Context context) {
        super(context);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        super.addHeaderView(v, null, true);
        if (mIvHeadImage == null && !(v instanceof ImageView)) {
            throw new IllegalArgumentException("头部应该首先添加一个ImageView对象");
        }
        mIvHeadImage = (ImageView) v;
        mIvHeadImage.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //获取头部图片和图片控件的高度
                            mHeadViewOriginHeight = mIvHeadImage.getHeight();
                            mHeadPicHeight = mIvHeadImage.getDrawable().getIntrinsicHeight();
                            mIvHeadImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    });

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Log.d(TAG, "onTouchEvent() called with: " + "ev = [" + ev + "]");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                zoomOutAnimate();//手指抬起缩小头部图片控件
                break;
        }
        return super.onTouchEvent(ev);
    }

    //手指抬起缩小头部图片控件
    private void zoomOutAnimate() {
        int startHeight = mIvHeadImage.getLayoutParams().height;
        if (startHeight == mHeadViewOriginHeight) {
            return;//图片控件大小并没有改变不需要作动画
        }
        //第一种方式：调用值动画实现恢复顶部图片控件原始的大小
        //        ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight, mHeadViewOriginHeight);
        //        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        //            @Override
        //            public void onAnimationUpdate(ValueAnimator animation) {
        //                //绘制图片控件还原的每一帧
        //                mIvHeadImage.getLayoutParams().height = (int) animation.getAnimatedValue();
        //                mIvHeadImage.requestLayout();
        //            }
        //        });
        //        valueAnimator.setInterpolator(new OvershootInterpolator());//使图片控件还原后再弹一下
        //        valueAnimator.setDuration(300);
        //        valueAnimator.start();
        //第二种方式：自定义Animation类实现
        HeightAnimation heightAnimation = new HeightAnimation(mIvHeadImage, startHeight, mHeadViewOriginHeight);
        mIvHeadImage.startAnimation(heightAnimation);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //Log.d(TAG, "overScrollBy() called with: " + "deltaX = [" + deltaX + "], deltaY = [" + deltaY + "], scrollX = [" + scrollX + "], scrollY = [" + scrollY + "], scrollRangeX = [" + scrollRangeX + "], scrollRangeY = [" + scrollRangeY + "], maxOverScrollX = [" + maxOverScrollX + "], maxOverScrollY = [" + maxOverScrollY + "], isTouchEvent = [" + isTouchEvent + "]");
        int curHeight = mIvHeadImage.getLayoutParams().height;
        //手指拖动后，图片控件增加的高度应该小于等于原来图片素材的高度
        //deltaY:上次手指位置到当前手指位置的垂直距离（Y轴方向），deltaY=上次Y坐标-当前Y坐标，deltaY值向下滑为负，向上为正
        if (isTouchEvent && deltaY < 0 && curHeight - (deltaY / 3) <= mHeadPicHeight) {
            mIvHeadImage.getLayoutParams().height = curHeight - (deltaY / 3);
            mIvHeadImage.requestLayout();
        }
        return super
                .overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    //拉伸动画改变的是View对象真实的大小而不是进行缩放
    class HeightAnimation extends Animation {
        private View mView;
        private int mStartHeight;
        private int mEndHeight;

        public HeightAnimation(View view, int startHeight, int endHeight) {
            mView = view;
            mStartHeight = startHeight;
            mEndHeight = endHeight;
            setInterpolator(new OvershootInterpolator());
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int height = (int) (mStartHeight + (mEndHeight - mStartHeight) * interpolatedTime);
            mView.getLayoutParams().height = height;
            mView.requestLayout();
        }
    }
}

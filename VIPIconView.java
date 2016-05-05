package com.tangdada.showyu.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by csy on 2016/4/15.
 */
public class VIPIconView extends View {

    private int mWidth;
    private int mIconWidth;
    private Paint mPaintNormal, mPaintBlur;
    private Context mContext;

    public static final int SILIVER_ID = 0;
    public static final int GOLD_ID = 1;
    public static final int DIAMOND_ID = 2;

    private final float mScale = 0.6f;//两边的图标缩放大小
    private final float mMoveFilter = 0.66f;//手指拖放速度
    private final float mChangeFilter = 0.3f;//切换标签移动比例

    //没有检测速度
    //没有考虑wrap content的情况，所以xml里面宽度不能自适应
    //……应该还有暂时没想到的可扩展的东东

    public VIPIconView(Context context) {
        super(context);
        init(context);
    }

    public VIPIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VIPIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width / 3);
    }


    private Rect mRect1, mRect2, mRect3, mRect4, mRect5;
    private Point mCenterPoint;

    private void init(Context context) {
        mContext = context;

        mRect1 = new Rect();
        mRect2 = new Rect();
        mRect3 = new Rect();
        mRect4 = new Rect();
        mRect5 = new Rect();

        mPaintNormal = new Paint();
        mPaintBlur = new Paint();

        mCenterPoint = new Point();

        mValueAnimator = new ValueAnimator();
        mValueAnimator.setDuration(500);
        mValueAnimator.addListener(mAnimatorListener);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mIconWidth = mWidth / 6;
        mCenterPoint.y = h / 2;
        mCenterPoint.x = mIconWidth * 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width1, width2, width3, width4, width5;
        if (mDistance > 0) {
            width1 = (int) (mDistance * mScale / 2);
            width2 = (int) (mDistance / 2 - mDistance * mScale / 2 + mScale * mIconWidth);
            width3 = (int) (mIconWidth + mDistance * mScale / 2 - mDistance / 2);
            width4 = (int) (mIconWidth * mScale - mDistance * mScale / 2);
            width5 = 0;
        } else {
            width1 = 0;
            width2 = (int) (mIconWidth * mScale + mDistance * mScale / 2);
            width3 = (int) (mIconWidth - mDistance * mScale / 2 + mDistance / 2);
            width4 = (int) (-mDistance / 2 + mDistance * mScale / 2 + mScale * mIconWidth);
            width5 = (int) (-mDistance * mScale / 2);
        }

        if (mBitmaps[0] != null && !mBitmaps[0].isRecycled()) {
            if (width1 > 0) {
                mRect1.set(mIconWidth - width1, mCenterPoint.y - width1, mIconWidth + width1, mCenterPoint.y + width1);
                canvas.drawBitmap(mBitmaps[0], null, mRect1, mPaintNormal);
                canvas.drawBitmap(mBitmapVagues[0], null, mRect1, mPaintNormal);
            }
        }

        if (mBitmaps[4] != null && !mBitmaps[4].isRecycled()) {
            if (width5 > 0) {
                mRect5.set(mIconWidth * 5 - width5, mCenterPoint.y - width5, mIconWidth * 5 + width5, mCenterPoint.y + width5);
                canvas.drawBitmap(mBitmaps[4], null, mRect5, mPaintNormal);
                canvas.drawBitmap(mBitmapVagues[4], null, mRect5, mPaintNormal);
            }
        }

        if (mBitmaps[1] != null && !mBitmaps[1].isRecycled()) {
            mRect2.set((int) (mIconWidth + (mDistance < 0 ? 0 : mDistance) - width2), mCenterPoint.y - width2,
                    (int) (mIconWidth + (mDistance < 0 ? 0 : mDistance) + width2), mCenterPoint.y + width2);

            canvas.drawBitmap(mBitmaps[1], null, mRect2, mPaintNormal);

            if (mBitmapVagues[1] != null && !mBitmaps[1].isRecycled()) {
                mPaintBlur.setAlpha(getPaintAlpha((int) (mIconWidth + (mDistance < 0 ? 0 : mDistance))));
                canvas.drawBitmap(mBitmapVagues[1], null, mRect2, mPaintBlur);
            }
        }

        if (mBitmaps[3] != null && !mBitmaps[3].isRecycled()) {
            mRect4.set((int) (mIconWidth * 5 + (mDistance > 0 ? 0 : mDistance) - width4), mCenterPoint.y - width4,
                    (int) (mIconWidth * 5 + (mDistance > 0 ? 0 : mDistance) + width4), mCenterPoint.y + width4);

            canvas.drawBitmap(mBitmaps[3], null, mRect4, mPaintNormal);

            if (mBitmapVagues[3] != null && !mBitmaps[3].isRecycled()) {
                mPaintBlur.setAlpha(getPaintAlpha((int) (mIconWidth * 5 + (mDistance > 0 ? 0 : mDistance))));
                canvas.drawBitmap(mBitmapVagues[3], null, mRect4, mPaintBlur);
            }
        }

        if (mBitmaps[2] != null && !mBitmaps[2].isRecycled()) {
            mRect3.set((int) (mCenterPoint.x + mDistance - width3), mCenterPoint.y - width3, (int) (mCenterPoint.x + mDistance + width3), mCenterPoint.y + width3);

            canvas.drawBitmap(mBitmaps[2], null, mRect3, mPaintNormal);

            if (mBitmapVagues[2] != null && !mBitmaps[2].isRecycled()) {
                mPaintBlur.setAlpha(getPaintAlpha((int) (mCenterPoint.x + mDistance)));
                canvas.drawBitmap(mBitmapVagues[2], null, mRect3, mPaintBlur);
            }
        }
    }

    private int getPaintAlpha(int x) {
        int alpha = 255 * Math.abs(Math.abs(mWidth / 2) - Math.abs(x)) / (2 * mIconWidth);
        if (alpha > 255)
            alpha = 255;
        if (alpha < 0)
            alpha = 0;
        return alpha;
    }

    private float mDownX;
    private float mDistance;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mValueAnimator.removeAllUpdateListeners();
                mDownX = event.getX() - mDistance / mMoveFilter;
                return true;
            case MotionEvent.ACTION_MOVE:
                float distance = (event.getX() - mDownX) * mMoveFilter;
                if (distance < -2 * mIconWidth)
                    distance = -2 * mIconWidth;
                if (distance > 2 * mIconWidth)
                    distance = 2 * mIconWidth;
                if (mDistance != distance) {
                    mDistance = distance;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                up(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    //animator
    private ValueAnimator mValueAnimator;

    private void up(MotionEvent event) {

        float distance = (event.getX() - mDownX) * mMoveFilter;
        if (distance < -2 * mIconWidth)
            distance = -2 * mIconWidth;
        if (distance > 2 * mIconWidth)
            distance = 2 * mIconWidth;

        mValueAnimator.removeAllUpdateListeners();

        if (distance > mIconWidth * 2 * mChangeFilter && mBitmaps[1] != null && !mBitmaps[1].isRecycled()) {
            mValueAnimator.setFloatValues(distance, 2 * mIconWidth);
        } else if (distance < -mIconWidth * 2 * mChangeFilter && mBitmaps[3] != null && !mBitmaps[3].isRecycled()) {
            mValueAnimator.setFloatValues(distance, -2 * mIconWidth);
        } else {
            mValueAnimator.setFloatValues(distance, 0);
        }

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDistance = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        mValueAnimator.start();
    }

    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mDistance == 2 * mIconWidth) {

                mCurrentIndex--;

                if (mBitmaps[4] != null && !mBitmaps[4].isRecycled())
                    mBitmaps[4].recycle();
                mBitmaps[4] = null;

                System.arraycopy(mBitmaps, 0, mBitmaps, 1, 4);

                if (mCurrentIndex > 1) {
                    mBitmaps[0] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[mCurrentIndex - 2]);
                } else {
                    mBitmaps[0] = null;
                }

                if (mBitmapVagues[4] != null && !mBitmapVagues[4].isRecycled())
                    mBitmapVagues[4].recycle();
                mBitmapVagues[4] = null;

                System.arraycopy(mBitmapVagues, 0, mBitmapVagues, 1, 4);

                if (mCurrentIndex > 1) {
                    mBitmapVagues[0] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[mCurrentIndex - 2]);
                } else {
                    mBitmapVagues[0] = null;
                }

                mIconChangeListener.change(mCurrentIndex);
                mDistance = 0;
                postInvalidate();
            } else if (mDistance == -2 * mIconWidth) {

                mCurrentIndex++;

                if (mBitmaps[0] != null && !mBitmaps[0].isRecycled())
                    mBitmaps[0].recycle();
                mBitmaps[0] = null;

                System.arraycopy(mBitmaps, 1, mBitmaps, 0, 4);

                if (mCurrentIndex + 2 < mIconResArray.length) {
                    mBitmaps[4] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[mCurrentIndex + 2]);
                } else {
                    mBitmaps[4] = null;
                }

                if (mBitmapVagues[0] != null && !mBitmapVagues[0].isRecycled())
                    mBitmapVagues[0].recycle();
                mBitmapVagues[0] = null;

                System.arraycopy(mBitmapVagues, 1, mBitmapVagues, 0, 4);

                if (mCurrentIndex + 2 < mIconResVagueArray.length) {
                    mBitmapVagues[4] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[mCurrentIndex + 2]);
                } else {
                    mBitmapVagues[4] = null;
                }

                mIconChangeListener.change(mCurrentIndex);
                mDistance = 0;
                postInvalidate();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
        for (Bitmap bitmap : mBitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        for (Bitmap bitmap : mBitmapVagues) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    //listener
    private IconChangeListener mIconChangeListener;

    public interface IconChangeListener {
        void change(int current);
    }

    public void setIconChangeListener(IconChangeListener iconChangeListener) {
        mIconChangeListener = iconChangeListener;
    }

    //resources
    // Now supports multiple Icon,more than three;
    // touch can be responsed when the animator is running;(still exist small bugs but they are difficult to detect ^_^)
    // and you can use the method setIconResList(,,) to setup the icons
    private int[] mIconResArray;
    private int[] mIconResVagueArray;
    private int mCurrentIndex;
    private Bitmap[] mBitmaps = new Bitmap[5];
    private Bitmap[] mBitmapVagues = new Bitmap[5];

    public void setIconResList(int[] array, int[] arrayVague, int currentIndex) {
        if (array != null) {

            mIconResArray = array;

            if (arrayVague != null && arrayVague.length == array.length) {
                mIconResVagueArray = arrayVague;
            } else {
                mIconResVagueArray = null;
            }

            setCurrent(currentIndex);
        }
    }

    public void setCurrent(int currentIndex) {
        if (mIconResArray == null || mIconResArray.length == 0)
            return;

        if (currentIndex < 0 || currentIndex > mIconResArray.length - 1)
            return;

        for (int i = 0; i < 5; i++) {
            if (mBitmaps[i] != null && !mBitmaps[i].isRecycled()) {
                mBitmaps[i].recycle();
                mBitmaps[i] = null;
            }

            if (mBitmapVagues[i] != null && !mBitmapVagues[i].isRecycled()) {
                mBitmapVagues[i].recycle();
                mBitmapVagues[i] = null;
            }
        }

        mCurrentIndex = currentIndex;

        mBitmaps[2] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[currentIndex]);

        if (mCurrentIndex > 0) {
            mBitmaps[1] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[currentIndex - 1]);
        }

        if (mCurrentIndex > 1) {
            mBitmaps[0] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[currentIndex - 2]);
        }

        if (mCurrentIndex + 1 < mIconResArray.length) {
            mBitmaps[3] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[currentIndex + 1]);
        }

        if (mCurrentIndex + 2 < mIconResArray.length) {
            mBitmaps[4] = BitmapFactory.decodeResource(mContext.getResources(), mIconResArray[currentIndex + 2]);
        }

        if (mIconResVagueArray != null && mIconResVagueArray.length > 0) {
            mBitmapVagues[2] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[currentIndex]);

            if (mCurrentIndex > 0) {
                mBitmapVagues[1] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[currentIndex - 1]);
            }

            if (mCurrentIndex > 1) {
                mBitmapVagues[0] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[currentIndex - 2]);
            }

            if (mCurrentIndex + 1 < mIconResArray.length) {
                mBitmapVagues[3] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[currentIndex + 1]);
            }

            if (mCurrentIndex + 2 < mIconResArray.length) {
                mBitmapVagues[4] = BitmapFactory.decodeResource(mContext.getResources(), mIconResVagueArray[currentIndex + 2]);
            }
        }

        postInvalidate();
    }
}

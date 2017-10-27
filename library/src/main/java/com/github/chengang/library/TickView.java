package com.github.chengang.library;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 陈岗不姓陈 on 2017/10/17.
 * <p>
 * 自定义view -- checkbox
 * 一个打钩的小动画
 */

public class TickView extends View {

    private Context mContext;

    //内圆的画笔
    private Paint mPaintCircle;
    //外层圆环的画笔
    private Paint mPaintRing;
    //打钩的画笔
    private Paint mPaintTick;

    //整个圆外切的矩形
    private RectF mRectF = new RectF();
    //记录打钩路径的三个点坐标
    private float[] mPoints = new float[8];

    //控件中心的X,Y坐标
    private int centerX;
    private int centerY;

    //计数器
    private int circleRadius = -1;
    private int ringProgress = 0;

    //是否被点亮
    private boolean isChecked = false;
    //是否处于动画中
    private boolean isAnimationRunning = false;

    private int unCheckBaseColor;
    private int checkBaseColor;
    private int checkTickColor;
    private int radius;

    //勾的半径
    private float tickRadius;
    //勾的偏移
    private float tickRadiusOffset;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private AnimatorSet mFinalAnimatorSet;

    private int mRingAnimatorDuration;
    private int mCircleAnimatorDuration;
    private int mScaleAnimatorDuration;

    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
        initPaint();
        initAnimatorCounter();
        setUpEvent();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TickView);
        unCheckBaseColor = typedArray.getColor(R.styleable.TickView_uncheck_base_color, getResources().getColor(R.color.tick_gray));
        checkBaseColor = typedArray.getColor(R.styleable.TickView_check_base_color, getResources().getColor(R.color.tick_yellow));
        checkTickColor = typedArray.getColor(R.styleable.TickView_check_tick_color, getResources().getColor(R.color.tick_white));
        radius = typedArray.getDimensionPixelOffset(R.styleable.TickView_radius, dp2px(mContext, 30));
        int rateMode = typedArray.getInt(R.styleable.TickView_rate, 1);
        TickRateEnum mTickRateEnum = TickRateEnum.getRateEnum(rateMode);
        mRingAnimatorDuration = mTickRateEnum.getmRingAnimatorDuration();
        mCircleAnimatorDuration = mTickRateEnum.getmCircleAnimatorDuration();
        mScaleAnimatorDuration = mTickRateEnum.getmScaleAnimatorDuration();
        typedArray.recycle();

        tickRadius = dp2px(mContext, 12);
        tickRadiusOffset = dp2px(mContext, 4);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        if (mPaintRing == null) mPaintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRing.setStyle(Paint.Style.STROKE);
        mPaintRing.setColor(isChecked ? checkBaseColor : unCheckBaseColor);
        mPaintRing.setStrokeCap(Paint.Cap.ROUND);
        mPaintRing.setStrokeWidth(dp2px(mContext, 2.5f));

        if (mPaintCircle == null) mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(checkBaseColor);

        if (mPaintTick == null) mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTick.setColor(isChecked ? checkTickColor : unCheckBaseColor);
        mPaintTick.setAlpha(isChecked ? 0 : 255);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeWidth(dp2px(mContext, 2.5f));
    }

    /**
     * 用ObjectAnimator初始化一些计数器
     */
    private void initAnimatorCounter() {
        //圆环进度
        ObjectAnimator mRingAnimator = ObjectAnimator.ofInt(this, "ringProgress", 0, 360);
        mRingAnimator.setDuration(mRingAnimatorDuration);
        mRingAnimator.setInterpolator(null);
        //收缩动画
        ObjectAnimator mCircleAnimator = ObjectAnimator.ofInt(this, "circleRadius", radius - 5, 0);
        mCircleAnimator.setInterpolator(null);
        mCircleAnimator.setDuration(mCircleAnimatorDuration);
        //勾出来的透明渐变
        ObjectAnimator mAlphaAnimator = ObjectAnimator.ofInt(this, "tickAlpha", 0, 255);
        mAlphaAnimator.setDuration(200);
        mAlphaAnimator.setStartDelay(100);
        //最后的放大再回弹的动画，改变画笔的宽度来实现
        ObjectAnimator mScaleAnimator = ObjectAnimator.ofFloat(this, "ringStrokeWidth", mPaintRing.getStrokeWidth(), mPaintRing.getStrokeWidth() * 6f, mPaintRing.getStrokeWidth() / 6f);
        mScaleAnimator.setInterpolator(null);
        mScaleAnimator.setDuration(mScaleAnimatorDuration);

        //打钩和放大回弹的动画一起执行
        AnimatorSet mAlphaScaleAnimatorSet = new AnimatorSet();
        mAlphaScaleAnimatorSet.playTogether(mAlphaAnimator, mScaleAnimator);

        mFinalAnimatorSet = new AnimatorSet();
        mFinalAnimatorSet.playSequentially(mRingAnimator, mCircleAnimator, mAlphaScaleAnimatorSet);
    }

    /**
     * 设置点击事件
     */
    private void setUpEvent() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isChecked = !isChecked;
                reset();
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged((TickView) view, isChecked);
                }
            }
        });
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                mySize = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                mySize = size;
                break;
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize((radius + dp2px(mContext, 2.5f) * 6) * 2, widthMeasureSpec);
        int height = getMySize((radius + dp2px(mContext, 2.5f) * 6) * 2, heightMeasureSpec);

        height = width = Math.max(width, height);

        setMeasuredDimension(width, height);

        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;

        //设置圆圈的外切矩形
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        //设置打钩的几个点坐标
        mPoints[0] = centerX - tickRadius + tickRadiusOffset;
        mPoints[1] = (float) centerY;
        mPoints[2] = centerX - tickRadius / 2 + tickRadiusOffset;
        mPoints[3] = centerY + tickRadius / 2;
        mPoints[4] = centerX - tickRadius / 2 + tickRadiusOffset;
        mPoints[5] = centerY + tickRadius / 2;
        mPoints[6] = centerX + tickRadius * 2 / 4 + tickRadiusOffset;
        mPoints[7] = centerY - tickRadius * 2 / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isChecked) {
            canvas.drawArc(mRectF, 90, 360, false, mPaintRing);
            canvas.drawLines(mPoints, mPaintTick);
            return;
        }
        //画圆弧进度
        canvas.drawArc(mRectF, 90, ringProgress, false, mPaintRing);
        //画黄色的背景
        mPaintCircle.setColor(checkBaseColor);
        canvas.drawCircle(centerX, centerY, ringProgress == 360 ? radius : 0, mPaintCircle);
        //画收缩的白色圆
        if (ringProgress == 360) {
            mPaintCircle.setColor(checkTickColor);
            canvas.drawCircle(centerX, centerY, circleRadius, mPaintCircle);
        }
        //画勾,以及放大收缩的动画
        if (circleRadius == 0) {
            canvas.drawLines(mPoints, mPaintTick);
            canvas.drawArc(mRectF, 0, 360, false, mPaintRing);
        }
        //ObjectAnimator动画替换计数器
        if (!isAnimationRunning) {
            isAnimationRunning = true;
            mFinalAnimatorSet.start();
        }
    }

    /*--------------属性动画---getter/setter begin---------------*/

    public int getRingProgress() {
        return ringProgress;
    }

    public void setRingProgress(int ringProgress) {
        this.ringProgress = ringProgress;
        postInvalidate();
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        postInvalidate();
    }

    public int getTickAlpha() {
        return 0;
    }

    public void setTickAlpha(int tickAlpha) {
        mPaintTick.setAlpha(tickAlpha);
        postInvalidate();
    }

    public float getRingStrokeWidth() {
        return mPaintRing.getStrokeWidth();
    }

    public void setRingStrokeWidth(float strokeWidth) {
        mPaintRing.setStrokeWidth(strokeWidth);
        postInvalidate();
    }

     /*--------------属性动画---getter/setter end---------------*/

    /**
     * 设置选中还是为选中的
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            isChecked = checked;
            reset();
        }
    }

    /**
     * 重置
     */
    private void reset() {
        initPaint();
        mFinalAnimatorSet.cancel();
        ringProgress = 0;
        circleRadius = -1;
        isAnimationRunning = false;
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        invalidate();
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /*-------------------interface-------------------*/

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TickView tickView, boolean isCheck);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }
}

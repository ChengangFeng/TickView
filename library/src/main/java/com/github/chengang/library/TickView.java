package com.github.chengang.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.animation.DecelerateInterpolator;

/**
 * Created by 陈岗不姓陈 on 2017/10/17.
 * <p>
 * 自定义view -- checkbox
 * 一个打钩的小动画
 */

public class TickView extends View {

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

    //最后扩大缩小动画中，画笔的宽度的最大倍数
    private static final int SCALE_TIMES = 6;

    private AnimatorSet mFinalAnimatorSet;

    private int mRingAnimatorDuration;
    private int mCircleAnimatorDuration;
    private int mScaleAnimatorDuration;

    TickViewConfig mConfig;

    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initPaint();
        initAnimatorCounter();
        setUpEvent();
    }

    private void initAttrs(AttributeSet attrs) {
        if (mConfig == null) {
            mConfig = new TickViewConfig(getContext());
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TickView);
        mConfig.setUnCheckBaseColor(typedArray.getColor(R.styleable.TickView_uncheck_base_color, getResources().getColor(R.color.tick_gray)))
                .setCheckBaseColor(typedArray.getColor(R.styleable.TickView_check_base_color, getResources().getColor(R.color.tick_yellow)))
                .setCheckTickColor(typedArray.getColor(R.styleable.TickView_check_tick_color, getResources().getColor(R.color.tick_white)))
                .setRadius(typedArray.getDimensionPixelOffset(R.styleable.TickView_radius, dp2px(30)))
                .setClickable(typedArray.getBoolean(R.styleable.TickView_clickable, true))
                .setTickRadius(dp2px(12))
                .setTickRadiusOffset(dp2px(4));

        int rateMode = typedArray.getInt(R.styleable.TickView_rate, 1);
        TickRateEnum mTickRateEnum = TickRateEnum.getRateEnum(rateMode);
        mRingAnimatorDuration = mTickRateEnum.getmRingAnimatorDuration();
        mCircleAnimatorDuration = mTickRateEnum.getmCircleAnimatorDuration();
        mScaleAnimatorDuration = mTickRateEnum.getmScaleAnimatorDuration();
        typedArray.recycle();
        applyConfig(mConfig);
        setUpEvent();
    }

    private void applyConfig(TickViewConfig config) {
        assert mConfig != null : "TickView Config must not be null";
        if (config != null && config != mConfig) {
            mConfig.setConfig(config);
        }
        if (mConfig.isNeedToReApply()) {
            initPaint();
            initAnimatorCounter();
            mConfig.setNeedToReApply(false);
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        if (mPaintRing == null) mPaintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRing.setStyle(Paint.Style.STROKE);
        mPaintRing.setColor(isChecked ? mConfig.getCheckBaseColor() : mConfig.getUnCheckBaseColor());
        mPaintRing.setStrokeCap(Paint.Cap.ROUND);
        mPaintRing.setStrokeWidth(dp2px(2.5f));

        if (mPaintCircle == null) mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(mConfig.getCheckBaseColor());

        if (mPaintTick == null) mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTick.setColor(isChecked ? mConfig.getCheckTickColor() : mConfig.getUnCheckBaseColor());
        mPaintTick.setAlpha(isChecked ? 0 : 255);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeWidth(dp2px(2.5f));
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
        ObjectAnimator mCircleAnimator = ObjectAnimator.ofInt(this, "circleRadius", mConfig.getRadius() - 5, 0);
        mCircleAnimator.setInterpolator(new DecelerateInterpolator());
        mCircleAnimator.setDuration(mCircleAnimatorDuration);
        //勾出来的透明渐变
        ObjectAnimator mAlphaAnimator = ObjectAnimator.ofInt(this, "tickAlpha", 0, 255);
        mAlphaAnimator.setDuration(200);
        //最后的放大再回弹的动画，改变画笔的宽度来实现
        ObjectAnimator mScaleAnimator = ObjectAnimator.ofFloat(this, "ringStrokeWidth", mPaintRing.getStrokeWidth(), mPaintRing.getStrokeWidth() * SCALE_TIMES, mPaintRing.getStrokeWidth() / SCALE_TIMES);
        mScaleAnimator.setInterpolator(null);
        mScaleAnimator.setDuration(mScaleAnimatorDuration);

        //打钩和放大回弹的动画一起执行
        AnimatorSet mAlphaScaleAnimatorSet = new AnimatorSet();
        mAlphaScaleAnimatorSet.playTogether(mAlphaAnimator, mScaleAnimator);

        mFinalAnimatorSet = new AnimatorSet();
        mFinalAnimatorSet.playSequentially(mRingAnimator, mCircleAnimator, mAlphaScaleAnimatorSet);
        mFinalAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mConfig.getTickAnimatorListener() != null) {
                    mConfig.getTickAnimatorListener().onAnimationEnd(TickView.this);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (mConfig.getTickAnimatorListener() != null) {
                    mConfig.getTickAnimatorListener().onAnimationStart(TickView.this);
                }
            }
        });
    }

    /**
     * 设置点击事件
     */
    private void setUpEvent() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConfig.isClickable()) {
                    toggle();
                    if (mConfig.getOnCheckedChangeListener() != null) {
                        mConfig.getOnCheckedChangeListener().onCheckedChanged((TickView) view, isChecked);
                    }
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

        final int radius = mConfig.getRadius();
        final float tickRadius = mConfig.getTickRadius();
        final float tickRadiusOffset = mConfig.getTickRadiusOffset();

        //控件的宽度等于动画最后的扩大范围的半径
        int width = getMySize((radius + dp2px(2.5f) * SCALE_TIMES) * 2, widthMeasureSpec);
        int height = getMySize((radius + dp2px(2.5f) * SCALE_TIMES) * 2, heightMeasureSpec);

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
        mPaintCircle.setColor(mConfig.getCheckBaseColor());
        canvas.drawCircle(centerX, centerY, ringProgress == 360 ? mConfig.getRadius() : 0, mPaintCircle);
        //画收缩的白色圆
        if (ringProgress == 360) {
            mPaintCircle.setColor(mConfig.getCheckTickColor());
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

    private int getRingProgress() {
        return ringProgress;
    }

    private void setRingProgress(int ringProgress) {
        this.ringProgress = ringProgress;
        postInvalidate();
    }

    private int getCircleRadius() {
        return circleRadius;
    }

    private void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        postInvalidate();
    }

    private int getTickAlpha() {
        return 0;
    }

    private void setTickAlpha(int tickAlpha) {
        mPaintTick.setAlpha(tickAlpha);
        postInvalidate();
    }

    private float getRingStrokeWidth() {
        return mPaintRing.getStrokeWidth();
    }

    private void setRingStrokeWidth(float strokeWidth) {
        mPaintRing.setStrokeWidth(strokeWidth);
        postInvalidate();
    }

     /*--------------属性动画---getter/setter end---------------*/

    /**
     * 改变状态
     *
     * @param checked 选中还是未选中
     */
    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            isChecked = checked;
            reset();
        }
    }

    /**
     * @return 当前状态是否选中
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * 改变当前的状态
     */
    public void toggle() {
        setChecked(!isChecked);
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
        final int radius = mConfig.getRadius();
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        invalidate();
    }

    public TickViewConfig getConfig() {
        return mConfig;
    }

    public void setConfig(TickViewConfig tickViewConfig) {
        if (tickViewConfig == null) return;
        applyConfig(tickViewConfig);
    }

    private int dp2px(float dpValue) {
        return DisplayUtil.dp2px(getContext(), dpValue);
    }
}

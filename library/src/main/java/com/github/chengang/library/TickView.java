package com.github.chengang.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 陈岗不姓陈 on 2017/10/17.
 * <p>
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
    private int circleCounter = 0;
    private int scaleCounter = 45;
    private int ringCounter = 0;
    private int alphaCount = 0;

    //是否被点亮
    private boolean isChecked = false;

    //勾的半径()
    private static final float TICK_RADIUS = 35;
    //勾的偏移
    private static final float TICK_RADIUS_OFFSET = 10;

    private int unCheckBaseColor;
    private int checkBaseColor;
    private int checkTickColor;
    private int radius;

    private TickRateEnum mTickRateEnum;

    private OnCheckedChangeListener mOnCheckedChangeListener;

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
        init();
        setUpEvent();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TickView);
        unCheckBaseColor = typedArray.getColor(R.styleable.TickView_uncheck_base_color, getResources().getColor(R.color.tick_gray));
        checkBaseColor = typedArray.getColor(R.styleable.TickView_check_base_color, getResources().getColor(R.color.tick_yellow));
        checkTickColor = typedArray.getColor(R.styleable.TickView_uncheck_base_color, getResources().getColor(R.color.tick_white));
        radius = typedArray.getDimensionPixelOffset(R.styleable.TickView_radius, dp2px(mContext, 30));
        int rateMode = typedArray.getInt(R.styleable.TickView_rate, 1);
        mTickRateEnum = TickRateEnum.getRateEnum(rateMode);
        typedArray.recycle();
    }

    private void init() {
        if (mPaintRing == null) mPaintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRing.setStyle(Paint.Style.STROKE);
        mPaintRing.setColor(isChecked ? checkBaseColor : unCheckBaseColor);
        mPaintRing.setStrokeCap(Paint.Cap.ROUND);
        mPaintRing.setStrokeWidth(8);

        if (mPaintCircle == null) mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(checkBaseColor);
        mPaintCircle.setStrokeWidth(4);

        if (mPaintTick == null) mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTick.setColor(isChecked ? checkTickColor : unCheckBaseColor);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeWidth(8);

    }

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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;

        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        //设置打钩的几个点坐标
        mPoints[0] = centerX - TICK_RADIUS + TICK_RADIUS_OFFSET;
        mPoints[1] = (float) centerY;
        mPoints[2] = centerX - TICK_RADIUS / 2 + TICK_RADIUS_OFFSET;
        mPoints[3] = centerY + TICK_RADIUS / 2;
        mPoints[4] = centerX - TICK_RADIUS / 2 + TICK_RADIUS_OFFSET;
        mPoints[5] = centerY + TICK_RADIUS / 2;
        mPoints[6] = centerX + TICK_RADIUS * 2 / 3 + TICK_RADIUS_OFFSET;
        mPoints[7] = centerY - TICK_RADIUS * 2 / 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isChecked) {
            canvas.drawArc(mRectF, 90, 360, false, mPaintRing);
            canvas.drawLines(mPoints, mPaintTick);
            return;
        }
        //画圆弧进度 // TODO: 2017/10/19 画圆弧的速度
        ringCounter += mTickRateEnum.getRingCounterUnit();
        if (ringCounter >= 360) {
            ringCounter = 360;
        }
        canvas.drawArc(mRectF, 90, ringCounter, false, mPaintRing);

        if (ringCounter == 360) {
            mPaintCircle.setColor(checkBaseColor);
            canvas.drawCircle(centerX, centerY, radius, mPaintCircle);
            //绘制白色的圆 // TODO: 2017/10/19 画缩小圆的速度
            mPaintCircle.setColor(checkTickColor);
            circleCounter += mTickRateEnum.getCircleCounterUnit();
            canvas.drawCircle(centerX, centerY, radius - circleCounter, mPaintCircle);
            if (circleCounter >= radius + 40) {
                //显示打钩（外加一个透明的渐变）
                alphaCount += 20;
                if (alphaCount >= 255) alphaCount = 255;
                mPaintTick.setAlpha(alphaCount);
                canvas.drawLines(mPoints, mPaintTick);
                //放大的动画 // TODO: 2017/10/19 放大的范围和速度
                scaleCounter -= mTickRateEnum.getScaleCounterUnit();
                if (scaleCounter <= -45) {
                    scaleCounter = -45;
                }
                if (scaleCounter > 0) {
                    mPaintRing.setStrokeWidth(mPaintRing.getStrokeWidth() + 3.5F);
                    canvas.drawArc(mRectF, 90, 360, false, mPaintRing);
                } else {
                    mPaintRing.setStrokeWidth(mPaintRing.getStrokeWidth() - 3.5F);
                    canvas.drawArc(mRectF, 90, 360, false, mPaintRing);
                }
            }
        }
        if (scaleCounter != -45) {
            postInvalidate();
        }
    }

    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            isChecked = checked;
            reset();
        }
    }

    private void reset() {
        init();

        ringCounter = 0;
        circleCounter = 0;
        scaleCounter = 45;
        alphaCount = 0;

        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        invalidate();
    }

    private static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TickView tickView, boolean isCheck);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }
}

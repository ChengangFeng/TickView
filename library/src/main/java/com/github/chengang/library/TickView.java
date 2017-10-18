package com.github.chengang.library;

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
    private RectF mRectF;
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
    //圆环进度增加的单位（小于90）
    private static final int RING_COUNTER_UNIT = 10;
    //圆圈收缩的单位
    private static final int CIRCLE_COUNTER_UNIT = 6;
    //圆圈最后放大收缩的单位
    private static final int SCALE_COUNTER_UNIT = 4;

    private int unCheckBaseColor;
    private int checkBaseColor;
    private int checkTickColor;

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
        typedArray.recycle();
    }

    private void init() {
        mPaintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRing.setStyle(Paint.Style.STROKE);
        if (isChecked) {
            mPaintRing.setColor(checkBaseColor);
        } else {
            mPaintRing.setColor(unCheckBaseColor);
        }
        mPaintRing.setStrokeCap(Paint.Cap.ROUND);
        mPaintRing.setStrokeWidth(8);

        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(checkBaseColor);
        mPaintCircle.setStrokeWidth(4);

        mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTick.setColor(isChecked ? checkTickColor : unCheckBaseColor);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeWidth(8);

        mRectF = new RectF();
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

        mRectF.set(centerX - 80, centerY - 80, centerX + 80, centerY + 80);

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
        //画圆弧进度
        ringCounter += RING_COUNTER_UNIT;
        if (ringCounter >= 360) {
            ringCounter = 360;
        }
        canvas.drawArc(mRectF, 90, ringCounter, false, mPaintRing);

        if (ringCounter == 360) {
            mPaintCircle.setColor(checkBaseColor);
            canvas.drawCircle(centerX, centerY, 90, mPaintCircle);
            //绘制白色的圆
            mPaintCircle.setColor(checkTickColor);
            circleCounter += CIRCLE_COUNTER_UNIT;
            canvas.drawCircle(centerX, centerY, 90 - circleCounter, mPaintCircle);
            if (circleCounter >= 130) {
                //显示打钩
                alphaCount += 20;
                if (alphaCount >= 255) alphaCount = 255;
                mPaintTick.setAlpha(alphaCount);
                canvas.drawLines(mPoints, mPaintTick);
                //放大的动画
                scaleCounter -= SCALE_COUNTER_UNIT;
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

    public void click() {
        isChecked = !isChecked;
        reset();
    }


    public void reset() {
        init();
        ringCounter = 0;
        circleCounter = 0;
        scaleCounter = 45;
        alphaCount = 0;
        mRectF.set(centerX - 90, centerY - 90, centerX + 90, centerY + 90);

        invalidate();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TickView tickView, boolean isCheck);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }
}

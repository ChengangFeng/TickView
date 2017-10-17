package com.github.chengang.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private Paint mPaintCircle;
    private Paint mPaintRing;
    private Paint mPaintTick;

    private RectF mRectF;

    private int centerX;
    private int centerY;

    private int circleCounter = 0;
    private int scaleCounter = 45;
    private int ringCounter = 0;

    private boolean isChecked = false;
    private Context mContext;

    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRing.setStyle(Paint.Style.STROKE);
        mPaintRing.setColor(mContext.getResources().getColor(R.color.xxx));
        mPaintRing.setStrokeCap(Paint.Cap.ROUND);
        mPaintRing.setStrokeWidth(2);

        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(mContext.getResources().getColor(R.color.xxx));
        mPaintCircle.setStrokeWidth(2);

        mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTick.setColor(mContext.getResources().getColor(R.color.xxx));
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeWidth(2);

        mRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;

        mRectF.set(centerX - 90, centerY - 90, centerX + 90, centerY + 90);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画圆弧进度
        ringCounter += 4;
        if (ringCounter >= 360) {
            ringCounter = 360;
        }
        canvas.drawArc(mRectF, 90, ringCounter, false, mPaintRing);
        //进度达到90%
        if (circleCounter > 90) {
            circleCounter = 90;
        }
        if (ringCounter == 360) {
            mPaintCircle.setColor(mContext.getResources().getColor(R.color.xxx));
            canvas.drawCircle(centerX, centerY, 90, mPaintCircle);
            //绘制白色的圆
            mPaintCircle.setColor(Color.WHITE);
            circleCounter += 4;
            canvas.drawCircle(centerX, centerY, 90 - circleCounter, mPaintCircle);
            //显示打钩

            //放大的动画
            if (circleCounter >= 90) {
                if (scaleCounter > 0) {
                    scaleCounter -= 4;
                }
                if (scaleCounter <= -45) {
                    scaleCounter = -45;
                }
                if (scaleCounter > 0) {
                    mPaintRing.setStrokeWidth(mPaintRing.getStrokeWidth() + 1);
                    canvas.drawArc(mRectF, 90, 360, false, mPaintRing);
                } else {
                    mPaintRing.setStrokeWidth(mPaintRing.getStrokeWidth() - 1);
                    canvas.drawArc(mRectF, 90, 360, false, mPaintRing);
                }
            }
        }
        postInvalidate();
    }

    public void click() {
        invalidate();
    }
}

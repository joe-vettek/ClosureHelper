package com.xueluoanping.arknights.internal;

// 作者
// https://blog.csdn.net/qq_24000367/article/details/120796212

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.xueluoanping.arknights.R;
import com.xueluoanping.arknights.api.tool.ToolTheme;
import com.xueluoanping.arknights.api.tool.ToolTime;


/**
 * 加载动画
 */
public class SplashView extends View {
    //小球颜色
    private int[] colors;
    //不断旋转的圆的半径
    private final float radiusRotate = 90;
    //控件中心坐标
    private float centerX, centerY;
    private Paint mPaint;
    //动画执行对象
    private State mState;
    private ValueAnimator mAnimator;
    //动画旋转的角度
    private float animeAngle;
    //小球半径
    private float smallRadius = 10;
    //聚合动画的半径
    private float radiusMerge;
    //背景paint
    Paint bgPaint;
    private float radiusExpand;
    private RectF viewRect;


    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // bgPaint.setColor(ToolTheme.getColorValue(context,R.attr.backgroundColor));
        bgPaint.setColor(Color.TRANSPARENT);
        colors = context.getResources().getIntArray(R.array.splash_circle_colors);
        radiusMerge = radiusRotate;
    }

    public void reDraw() {
        mState = null;
        this.invalidate();
        this.requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;

        viewRect = new RectF(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mState == null) {
            mState = new RotateState();
        }

        mState.draw(canvas);
    }

    private void drawCircles(Canvas canvas) {
        //初始角度，将360度平分给每个小球
        float initAngle = (float) (2 * Math.PI / colors.length);

        for (int i = 0; i < colors.length; i++) {
            mPaint.setColor(colors[i]);
            double angle = i * initAngle + animeAngle;
            float x = (float) (Math.cos(angle) * radiusMerge + centerX);
            float y = (float) (Math.sin(angle) * radiusMerge + centerY);
            canvas.drawCircle(x, y, smallRadius, mPaint);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (mState instanceof ExpandState) {
            //画目标圆
            canvas.drawCircle(centerX, centerY, radiusExpand, bgPaint);
            bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            canvas.drawRect(viewRect, bgPaint);
        } else {
            canvas.drawRect(viewRect, bgPaint);
        }
    }

    /**
     * 结束加载
     */
    public void finshSplash() {
        if (mState != null && mState instanceof RotateState) {
            ((RotateState) mState).cancel();
        }
    }

    public void pauseSplash() {
        if (mState != null && mState instanceof RotateState) {
            ((RotateState) mState).pause();
        }
    }

    public void continueSplash() {
        if (mState != null && mState instanceof RotateState) {
            ((RotateState) mState).resume();
        }
    }

    /**
     * 状态标准化接口
     */
    interface State {
        void draw(Canvas canvas);
    }

    /**
     * 旋转动画
     */
    class RotateState implements State {

        public RotateState() {
            mAnimator = ValueAnimator.ofFloat((float) (2 * Math.PI));
            mAnimator.setDuration(900);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animeAngle = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    mState = new MegreState();
                }
            });
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }

        public void pause() {
            mAnimator.pause();
        }

        public void resume() {
            mAnimator.resume();
        }

        public void cancel() {

            if (mAnimator != null && mAnimator.isRunning())
                mAnimator.cancel();
        }
    }

    /**
     * 聚合动画
     */
    class MegreState implements State {

        public MegreState() {
            mAnimator = ValueAnimator.ofFloat(radiusRotate, 0f);
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new AnticipateInterpolator(5));
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radiusMerge = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mState = new ExpandState();
                }
            });
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }
    }

    /**
     * 扩散动画
     */
    class ExpandState implements State {

        public ExpandState() {
            float width = (float) Math.hypot(centerX, centerY);
            mAnimator = ValueAnimator.ofFloat(width);
            mAnimator.setDuration(500);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radiusExpand = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                }
            });
            mAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            drawBackground(canvas);
        }
    }
}

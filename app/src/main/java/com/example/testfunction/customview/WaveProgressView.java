package com.example.testfunction.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.TextView;
import com.example.testfunction.R;

/**
 * 这个不行
 * @author zouweilin 2019-06-28
 */
public class WaveProgressView extends View {
    /**
     * 圆形进度框画笔
     */
    private Paint circlePaint;
    /**
     * 绘制波浪画笔
     */
    private Paint wavePaint;
    /**
     * 绘制波浪Path
     */
    private Path wavePath;
    /**
     * 绘制第二个波浪的画笔
     */
    private Paint secondWavePaint;

    private Bitmap cacheBitmap;
    private Canvas bitmapCanvas;

    private WaveProgressAnim waveProgressAnim;
    private TextView textView;
    private OnAnimationListener onAnimationListener;

    /**
     * 波浪宽度
     */
    private float waveWidth;
    /**
     * 波浪高度
     */
    private float waveHeight;
    /**
     * 波浪组的数量（一次起伏为一组）
     */
    private int waveNum;
    /**
     * 波浪平移的距离
     */
    private float waveMovingDistance;

    /**
     * 重新测量后View实际的宽高
     */
    private int viewSize;
    /**
     * 自定义View默认的宽高
     */
    private int defaultSize;

    /**
     * 进度条占比
     */
    private float percent;
    /**
     * 可以更新的进度条数值
     */
    private float progressNum;
    /**
     * 进度条最大值
     */
    private float maxNum;

    /**
     * 波浪颜色
     */
    private int waveColor;
    /**
     * 第二层波浪颜色
     */
    private int secondWaveColor;
    /**
     * 背景进度框颜色
     */
    private int bgColor;

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public WaveProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        waveWidth = typedArray.getDimension(R.styleable.WaveProgressView_wave_width, dip2px(context, 25));
        waveHeight = typedArray.getDimension(R.styleable.WaveProgressView_wave_height, dip2px(context, 5));
        waveColor = typedArray.getColor(R.styleable.WaveProgressView_wave_color, Color.GREEN);
        secondWaveColor = typedArray.getColor(R.styleable.WaveProgressView_second_wave_color, Color.parseColor("#800de6e8"));
        bgColor = typedArray.getColor(R.styleable.WaveProgressView_wave_bg_color, Color.GRAY);
        typedArray.recycle();

        defaultSize = dip2px(context, 100);
        waveNum = (int) Math.ceil(Double.parseDouble(String.valueOf(defaultSize / waveWidth / 2)));
        waveMovingDistance = 0;

        wavePath = new Path();

        wavePaint = new Paint();
        wavePaint.setColor(waveColor);
        wavePaint.setAntiAlias(true);
        wavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        secondWavePaint = new Paint();
        secondWavePaint.setColor(secondWaveColor);
        secondWavePaint.setAntiAlias(true);
        //因为要覆盖在第一层波浪上，且要让半透明生效，所以选此模式
        secondWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        circlePaint = new Paint();
        circlePaint.setColor(bgColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_3));

        waveProgressAnim = new WaveProgressAnim();
        waveProgressAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                /*if(percent == progressNum / maxNum){
                    waveProgressAnim.setDuration(5000);
                }*/
            }
        });

        percent = 0;
        progressNum = 0;
        maxNum = 100;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = measureSize(defaultSize, heightMeasureSpec);
        int width = measureSize(defaultSize, widthMeasureSpec);
        int min = Math.min(width, height);// 获取View最短边的长度
        setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形
        viewSize = min;
        if (min > 0) {
            cacheBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(cacheBitmap);
        }
        waveNum = (int) Math.ceil(Double.parseDouble(String.valueOf(viewSize / waveWidth / 2)));

    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        bitmapCanvas.drawCircle(viewSize / 2f, viewSize / 2f, viewSize / 2f, circlePaint);
        bitmapCanvas.drawPath(getWavePath(), wavePaint);
        canvas.drawBitmap(cacheBitmap, 0, 0, null);
    }

    private Path getWavePath() {
//        float changeWaveHeight = (1 - percent) * waveHeight;
        float changeWaveHeight = waveHeight;
        if (onAnimationListener != null) {
            changeWaveHeight =
                    onAnimationListener.howToChangeWaveHeight(percent, waveHeight) == 0 && percent < 1
                            ? waveHeight
                            : onAnimationListener.howToChangeWaveHeight(percent, waveHeight);
        }

        wavePath.reset();
        float v = percent - lastPercent;
        float more;
        if (v < 0) {
            more = 0;
        } else {
            more = v;
        }
        float top = (1 - (lastPercent + more))* viewSize;
        float src = (1 - percent) * viewSize;
        //移动到右上方，也就是p0点
        wavePath.moveTo(viewSize, src);
        //移动到右下方，也就是p1点
        wavePath.lineTo(viewSize, viewSize);
        //移动到左下边，也就是p2点
        wavePath.lineTo(0, viewSize);
        //移动到左上方，也就是p3点
        //wavePath.lineTo(0, (1-percent)*viewSize);
        wavePath.lineTo(-waveMovingDistance, src);

        //从p3开始向p0方向绘制波浪曲线
        for (int i = 0; i < waveNum * 2; i++) {
            wavePath.rQuadTo(waveWidth / 2, changeWaveHeight, waveWidth, 0);
            wavePath.rQuadTo(waveWidth / 2, -changeWaveHeight, waveWidth, 0);
        }

        //将path封闭起来
        wavePath.close();
        return wavePath;
    }

    public class WaveProgressAnim extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            Log.e("zzzz", "interpolatedTime" + interpolatedTime);
            super.applyTransformation(interpolatedTime, t);
            if (percent < progressNum / maxNum) {
                percent = interpolatedTime * progressNum / maxNum;
                if (interpolatedTime == 1) {
                    lastPercent = percent;
                }
                if (textView != null && onAnimationListener != null) {
                    textView.setText(onAnimationListener.howToChangeText(interpolatedTime, progressNum, maxNum));
                }
            }
            waveMovingDistance = interpolatedTime * waveNum * waveWidth * 2;
            postInvalidate();
        }
    }
    private float lastPercent;

    /**
     * 设置进度条数值
     *
     * @param progressNum 进度条数值
     * @param time        动画持续时间
     */
    public void setProgressNum(float progressNum, int time) {
        this.progressNum = progressNum;
        waveProgressAnim.cancel();
        percent = 0;
        waveProgressAnim.setDuration(time);
        waveProgressAnim.setRepeatCount(-1);
        waveProgressAnim.setInterpolator(new LinearInterpolator());
        this.startAnimation(waveProgressAnim);
    }

    /**
     * 设置显示文字的TextView
     */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public interface OnAnimationListener {

        /**
         * 如何处理要显示的文字内容
         *
         * @param interpolatedTime 从0渐变成1,到1时结束动画
         * @param updateNum        进度条数值
         * @param maxNum           进度条最大值
         * @return
         */
        String howToChangeText(float interpolatedTime, float updateNum, float maxNum);

        /**
         * 如何处理波浪高度
         *
         * @param percent    进度占比
         * @param waveHeight 波浪高度
         * @return
         */
        float howToChangeWaveHeight(float percent, float waveHeight);
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.onAnimationListener = onAnimationListener;
    }
}
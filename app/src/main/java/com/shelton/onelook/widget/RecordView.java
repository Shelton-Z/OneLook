package com.shelton.onelook.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.shelton.onelook.R;

import java.util.ArrayList;


public class RecordView extends View {
    //View默认最小宽度
    private static final int DEFAULT_MIN_WIDTH = 500;
    private Paint mPaint;
    private long lastTime = 0;
    private float translateX = 0;

    /**
     * 音量
     */
    private float volume = 10;
    private float targetVolume = 1;
    private boolean isSet = false;
    private boolean canSetVolume = true;
    private TypedArray typedArray;

    private int voiceLineColor;
    private ArrayList<Path> paths;

    public RecordView(Context context) {
        this(context, null);
    }

    @SuppressLint("CustomViewStyleable")
    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.recordView);
        initAttrs();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void initAttrs() {
        voiceLineColor = typedArray.getColor(R.styleable.recordView_middleLineColor, getResources().getColor(R.color.RoundFillColor));
        paths = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            paths.add(new Path());
        }
    }

    /**
     * 当布局为wrap_content时设置默认长宽
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVoiceLine(canvas);
    }

    /**
     * 画声纹(录制)
     */
    private void drawVoiceLine(Canvas canvas) {
        lineChange();
        mPaint.setColor(voiceLineColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        canvas.save();
        int moveY = getHeight() * 3 / 4;
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).reset();
            paths.get(i).moveTo(getWidth(), getHeight() * 3 / 4);
        }
        int fineness = 1;
        for (float j = getWidth(); j >= 0; j -= fineness) {
            //这边必须保证起始点和终点的时候amplitude = 0;

            float amplitude = 5 * volume * j / getWidth() - 5 * volume * j / getWidth() * j / getWidth();  //振幅
            for (int n = 1; n <= paths.size(); n++) {
                float sin = amplitude * (float) Math.sin((j - Math.pow(1.22, n)) * Math.PI / 180 - translateX);
                paths.get(n - 1).lineTo(j, (2 * n * sin / paths.size() - 15 * sin / paths.size() + moveY));
            }
        }
        for (int n = 0; n < paths.size(); n++) {
            if (n == paths.size() - 1) {
                mPaint.setAlpha(255);
            } else {
                mPaint.setAlpha(n * 130 / paths.size());
            }
            if (mPaint.getAlpha() > 0) {
                canvas.drawPath(paths.get(n), mPaint);
            }
        }
        canvas.restore();
    }


    private void lineChange() {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            translateX += 5;
        } else {
            int lineSpeed = 100;
            if (System.currentTimeMillis() - lastTime > lineSpeed) {
                lastTime = System.currentTimeMillis();
                translateX += 5;
            } else {
                return;
            }
        }
        if (volume < targetVolume && isSet) {
            volume += getHeight() / 30;
        } else {
            isSet = false;
            if (volume <= 10) {
                volume = 10;
            } else {
                if (volume < getHeight() / 30) {
                    volume -= getHeight() / 60;
                } else {
                    volume -= getHeight() / 30;
                }
            }
        }
    }

    public void setVolume(int volume) {
        if (volume > 100)
            volume = volume / 100;
        volume = volume * 2 / 5;
        if (!canSetVolume)
            return;

        int sensibility = 4; //灵敏度
        float maxVolume = 100;
        if (volume > maxVolume * sensibility / 30) {
            isSet = true;
            this.targetVolume = getHeight() * volume / 3 / maxVolume;
        }
        postInvalidate();
    }

    @SuppressWarnings("unused")
    public void cancel() {
        canSetVolume = false;
        targetVolume = 1;
        postInvalidate();
    }
}

package com.example.user.dronecpe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by DEV on 22/1/2560.
 */

public class VerticalSeekBar extends SeekBar {

    private OnSeekBarChangeListener myListener;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        this.myListener = mListener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (!isEnabled()) {
//            return false;
//        }
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//                int i = 0;
//                i = getMax() - (int) (getMax() * event.getY() / getHeight());
//                setProgress(getMax() - i);
//                //Log.i("Progress",getProgress()+"");
//                onSizeChanged(getWidth(), getHeight(), 0, 0);
//                break;
//
//            case MotionEvent.ACTION_CANCEL:
//                break;
//        }
//        return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (myListener != null) {
                    myListener.onStartTrackingTouch(this);
                }
                setPressed(true);
                setSelected(true);
                break;

            case MotionEvent.ACTION_MOVE:
                setProgress((int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                //myListener.onProgressChanged(this, getMax() - (int) (getMax() * event.getY() / getHeight()), true);
                setPressed(true);
                setSelected(true);
                break;

            case MotionEvent.ACTION_UP:
                //myListener.onStopTrackingTouch(this);
                onSizeChanged(getWidth(), getHeight() , 0, 0);
//                setProgress(getMax() - (getMax()- 50));
//                onSizeChanged(getWidth(), getHeight() , 0, 0);
                //setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                setPressed(false);
                setSelected(false);
                break;

            case MotionEvent.ACTION_CANCEL:
                super.onTouchEvent(event);
                setPressed(false);
                setSelected(false);
                break;
        }
        return true;
    }

}

package com.example.user.dronecpe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by DEV on 22/1/2560.
 */

public class VerticalSeekBar_Reverse extends SeekBar {

    private OnSeekBarChangeListener myListener;

    public VerticalSeekBar_Reverse(Context context) {
        super(context);
    }

    public VerticalSeekBar_Reverse(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar_Reverse(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        c.rotate(90);
        c.translate(0, -getWidth());
        super.onDraw(c);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        //super.setOnSeekBarChangeListener(mListener);
        this.myListener = mListener;
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
//                int i = 0;
//                i = getMax() - (int) (getMax() * event.getY() / getHeight());
//                setProgress(100 - i);
//                Log.d("Progress", getProgress() + " ");
//                onSizeChanged(getWidth(), getHeight(), 0, 0);
//                break;
//
//            case MotionEvent.ACTION_UP:
//                int ii = 0;
//                ii = getMax() - (int) (getMax() * event.getY() / getHeight());
//                setProgress(50);
//                //Log.d("ACTION_UP", getProgress() + " " + ii);
//                //onSizeChanged(getWidth(), getHeight() / 2, 0, 0);
//                break;
//
//            case MotionEvent.ACTION_CANCEL:
//                Log.d("ACTION_CANCEL", getProgress() + " ");
//                break;
//        }
//        return true;
//    }

    private int lastProgress = 0;

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
                myListener.onProgressChanged(this, getMax() - (int) (getMax() * event.getY() / getHeight()), true);
                setPressed(true);
                setSelected(true);
                break;

//            case MotionEvent.ACTION_MOVE:
//                // Calling the super seems to help fix drawing problems
//                super.onTouchEvent(event);
//                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
//
//                // Ensure progress stays within boundaries of the seekbar
//                if (progress < 0) {
//                    progress = 0;
//                }
//                if (progress > getMax()) {
//                    progress = getMax();
//                }
//
//                // Draw progress
//                setProgress(progress);
//
//                // Only enact listener if the progress has actually changed
//                // Otherwise the listener gets called ~5 times per change
//                if (progress != lastProgress) {
//                    lastProgress = progress;
//                    myListener.onProgressChanged(this, progress, true);
//                }
//
//                onSizeChanged(getWidth(), getHeight(), 0, 0);
//                myListener.onProgressChanged(this, getMax() - (int) (getMax() * event.getY() / getHeight()), true);
//                setPressed(true);
//                setSelected(true);
//                break;

            case MotionEvent.ACTION_UP:
                myListener.onStopTrackingTouch(this);
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

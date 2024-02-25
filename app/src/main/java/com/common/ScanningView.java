package com.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.loseweight.R;

public class ScanningView extends View {

    private Paint paint = new Paint();
    private int mPosY = 0;
    private boolean runAnimation = true;
    private boolean showLine = true;
    private Handler handler;
    private Runnable refreshRunnable;
    private boolean isGoingDown = true;
    private int mHeight;
    private int DELAY = 0;
    Context context;

    public ScanningView(Context context) {
        super(context);
        this.context =context;
        init();
    }

    public ScanningView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        init();
    }

    public ScanningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =context;
        init();
    }

    private void init() {
        paint.setColor(ContextCompat.getColor(context, R.color.primary));
        paint.setStrokeWidth(10.0f);//make sure add stroke width otherwise line not display
        handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                refreshView();
            }
        };
    }

    @Override
    public void onDraw(Canvas canvas) {
        mHeight = canvas.getHeight();
        if (showLine) {
            canvas.drawLine(0, mPosY, canvas.getWidth(), mPosY, paint);
        }
        if (runAnimation) {
            handler.postDelayed(refreshRunnable, DELAY);
        }
    }

    public void startAnimation() {
        runAnimation = true;
        showLine = true;
        this.invalidate();
    }

    public void stopAnimation() {
        runAnimation = false;
        showLine = false;
        reset();
        this.invalidate();
    }

    private void reset() {
        mPosY = 0;
        isGoingDown = true;
    }

    private void refreshView() {
        //Update new position of the line
        if (isGoingDown) {
            mPosY += 5;
            if (mPosY > mHeight) {
                //We invert the direction of the animation
                mPosY = mHeight;
                isGoingDown = false;
            } else {
                mPosY -= 5;
                if (mPosY < 0) {
                    //We invert the direction of the animation
                    mPosY = 0;
                    isGoingDown = true;
                }
                this.invalidate();
            }
        }
    }
}

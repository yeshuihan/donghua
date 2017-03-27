package android.com.powersaver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fzw on 2017/3/27.
 */

public class WaveView extends View {
    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private int mViewHeight;
    private int mViewWidth;

    private float mLevelLine;
    private float mWaveHeight = 20;
    /**
     * 波长
     */
    private float mWaveWidth ;
    public static final float SPEED = 1.7f;

    private List<Point> mPointsList;
    private Paint mPaint;
    private Paint mTextPaint;
    private Path mWavePath;
    private boolean isMeasured = false;

    private Timer timer;
    private MyTimerTask mTask;

    private float mMoveLen;
    private float mLeftSide;
    Handler updateHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mMoveLen+=SPEED;
            mLevelLine-=0.05f;
            if(mLevelLine<=0){
                mLevelLine=0;
            }
            mLeftSide+=SPEED;
            for(int i=0;i<mPointsList.size();i++){
                mPointsList.get(i).x=mPointsList.get(i).x+SPEED;
                switch (i%4){
                    case 0:
                    case 2:
                        mPointsList.get(i).y=mLevelLine;
                        break;
                    case 1:
                        mPointsList.get(i).y=mLevelLine-mWaveHeight;
                        break;
                    case 3:
                        mPointsList.get(i).y=mLevelLine+mWaveHeight;
                        break;
                }
            }
            if(mMoveLen>=mWaveWidth){
                mMoveLen=0;
                resetPoint();
            }
            invalidate();
        }
    };

    private void resetPoint() {
        mLeftSide=-mWaveWidth;
        for(int i=0;i<mPointsList.size();i++){
            mPointsList.get(i).x=i*mWaveWidth/4-mWaveWidth;
        }
    }

    class Point{
        float x;
        float y;
        Point(float x,float y){
            this.x=x;
            this.y=y;
        }
    }
    private class MyTimerTask extends TimerTask{
        private  Handler handler;
        MyTimerTask(Handler handler){
            this.handler=handler;
        }
        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    private void init(){
        mPointsList=new ArrayList<>();
        timer=new Timer();
        mPaint=new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mTextPaint =new TextPaint();
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(30);

        mWavePath=new Path();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        start();
    }

    private void start() {
        if(mTask!=null){
            mTask.cancel();
            mTask=null;
        }
        mTask=new MyTimerTask(updateHandle);
        timer.schedule(mTask,0,10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!isMeasured){
            isMeasured=true;
            mViewHeight=getMeasuredHeight();
            mViewWidth=getMeasuredWidth();
            mWaveHeight=20;
            mLevelLine=mViewHeight-50;

            mWaveWidth=mViewWidth;
            mLeftSide=-mViewWidth;
            int n=(int)Math.round(mViewWidth/mWaveWidth);
            for(int i=0;i<9;i++){
                float x=i*mWaveWidth/4-mWaveWidth;
                float y=0;
                switch (i%4){
                    case 0:
                    case 2:
                        y=mLevelLine;
                        break;
                    case 1:
                        y=mLevelLine-mWaveHeight;
                        break;
                    case 3:
                        y=mLevelLine+mWaveHeight;
                        break;

                }
                mPointsList.add(new Point(x,y));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWavePath.reset();
        int i=0;
        mWavePath.moveTo(mPointsList.get(i).x,mPointsList.get(i).y);
        for(;i<mPointsList.size()-2;i=i+2){
            mWavePath.quadTo(mPointsList.get(i+1).x,mPointsList.get(i+1).y,mPointsList.get(i+2).x,mPointsList.get(i+2).y);
        }
        mWavePath.lineTo(mPointsList.get(i).x,mViewHeight);
        mWavePath.lineTo(mLeftSide,mViewHeight);
        mWavePath.close();
        canvas.drawPath(mWavePath,mPaint);
        canvas.drawText("" + ((int) ((1 - mLevelLine / mViewHeight) * 100))+ "%", mViewWidth / 2, mLevelLine + mWaveHeight
                + (mViewHeight - mLevelLine - mWaveHeight) / 2, mTextPaint);


    }
}

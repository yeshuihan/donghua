package android.com.powersaver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fzw on 2017/3/27.
 */

public class WaveView extends View {
    /** View的高度*/
    private int mViewHeight;
    /** View的宽度*/
    private int mViewWidth;
    /** 当前波形的高度*/
    private float mLevelLine;
    /** 最大的振幅*/
    private float maxWaveHeight;
    /** 波的振幅*/
    private float mWaveHeight;
    /** 波的波长*/
    private float mWaveWidth ;
    /** 波的最大水平移动速度*/
    private float maxSpeedX;
    /** 波的实时水平移动速度*/
    private float speedX;
    /** 波的垂直移动速度*/
    private float speedY;
    /** 记录的波形的点集合*/
    private List<Point> mPointsList;
    /** 画笔paint*/
    private Paint mPaint;

    /** 用以更新动画*/
    private Timer timer;
    /** 用以实现波浪的水平移动*/
    private MyTimerTask mTaskX;
    private MyTimerTask mTaskY;
    /** 水平移动的距离*/
    private float mMoveLen;

    private boolean isMeasureed;

    /** 圆的半径*/
    private float circleR;


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


    private Handler updateHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    moveX();
                    break;
                case 2:
                    moveY();
                    break;
            }

        }
    };

    /**
     * 水平移动各点
     */
    private void moveX(){

        mMoveLen+=speedX;
        if(mLevelLine<=0){
            mLevelLine=0;
        }
        for(int i=0;i<mPointsList.size();i++){
            mPointsList.get(i).x=mPointsList.get(i).x+speedX;
            switch (i%4){
                case 0:
                case 2:
                    mPointsList.get(i).y=mViewHeight/2+circleR-mLevelLine;
                    break;
                case 1:
                    mPointsList.get(i).y=mViewHeight/2+circleR-mLevelLine-mWaveHeight;
                    break;
                case 3:
                    mPointsList.get(i).y=mViewHeight/2+circleR-mLevelLine+mWaveHeight;
                    break;
            }
        }
        if(!(mMoveLen<mWaveWidth)){
            mMoveLen=0;
            updatePoints();
            resetPoint();

        }
        if(mLevelLine<circleR*2){
            invalidate();
        }

    }

    /**
     * 垂直方向移动
     */
    private void moveY(){
        mLevelLine+=speedY;

        if(mLevelLine<circleR*2){
            invalidate();
        }else {
            timer.cancel();
            mTaskX.cancel();
            mTaskY.cancel();
            mTaskY=null;
            mTaskX=null;
            timer=null;
            updatePoints();
            invalidate();
        }

    }

    /**
     * 水平方向各点重置
     */
    private void resetPoint() {
        for(int i=0;i<mPointsList.size();i++){
            mPointsList.get(i).x=(mViewWidth/2-mWaveWidth*3/2)+i*mWaveWidth/4;
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
        private int what;
        MyTimerTask(Handler handler,int i){
            this.handler=handler;
            what=i;
        }
        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage(what));
        }
    }

    private void init(){
        mPointsList=new ArrayList<>();
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //start();
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
       // if(!isMeasureed){
            isMeasureed=true;
            mViewHeight=getMeasuredHeight();
            mViewWidth=getMeasuredWidth();
            circleR=(mViewHeight<mViewWidth?mViewHeight:mViewWidth)/2;
            maxWaveHeight=circleR/5;
            maxSpeedX=2*circleR/100;
            speedY=2*circleR/100;
            mLevelLine=5*speedY;
            updatePoints();

        //}

    }
    /** 实现图层的合并*/
    PorterDuffXfermode xfermode=new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    /** 实现图层的合并*/
    PorterDuffXfermode xfermode2=new PorterDuffXfermode(PorterDuff.Mode.DST);
    @Override
    protected void onDraw(Canvas canvas) {
        int save = canvas.saveLayer(mViewWidth/2-circleR,mViewHeight/2-circleR,mViewWidth/2+circleR,mViewHeight/2+circleR,null,Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(makeCircle(),0,0,mPaint);
        mPaint.setXfermode(xfermode);
        if(mLevelLine<2*circleR){
            mPaint.setXfermode(xfermode);
        }else{
            mPaint.setXfermode(xfermode2);
        }
        canvas.drawBitmap(makeWave(),0,0,mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(save);

    }

    private Bitmap makeCircle() {
        Bitmap bm = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        c.drawCircle(mViewWidth/2,mViewHeight/2,circleR,p);
        return bm;
    }

    private Bitmap makeWave() {
        Bitmap bm = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        Path wavePath=new Path();
        int i=0;
        wavePath.moveTo(mPointsList.get(i).x,mPointsList.get(i).y);
        for(;i<mPointsList.size()-2;i=i+2){
            wavePath.quadTo(mPointsList.get(i+1).x,mPointsList.get(i+1).y,mPointsList.get(i+2).x,mPointsList.get(i+2).y);
        }
        wavePath.lineTo(mViewWidth/2+circleR,mViewHeight/2+circleR+mWaveHeight-mLevelLine);
        wavePath.lineTo(mViewWidth/2+circleR,mViewHeight/2+circleR+mWaveHeight);
        wavePath.lineTo(mViewWidth/2-2*circleR,mViewHeight/2+circleR+mWaveHeight);
        wavePath.lineTo(mViewWidth/2-2*circleR,mViewHeight/2+circleR+mWaveHeight-mLevelLine);
        wavePath.close();
        c.drawPath(wavePath,p);
        return bm;
    }

    private void updatePoints(){

        mWaveWidth= (float) (2*(Math.sqrt(circleR*circleR-Math.pow(circleR-mLevelLine,2))));
        mWaveHeight=maxWaveHeight*mWaveWidth/(2*circleR);
        speedX=maxSpeedX*mWaveWidth/(2*circleR);
        mPointsList.clear();
        for(int i=0;i<9;i++){
            float x=(mViewWidth/2-mWaveWidth*3/2)+i*mWaveWidth/4;
            float y=0;
            switch (i%4){
                case 0:
                case 2:
                    y=mViewHeight/2+circleR-mLevelLine;
                    break;
                case 1:
                    y=mViewHeight/2+circleR-mLevelLine-mWaveHeight;
                    break;
                case 3:
                    y=mViewHeight/2+circleR-mLevelLine+mWaveHeight;
                    break;
            }
            mPointsList.add(new Point(x,y));
        }

    }


    public void start() {

        if(mLevelLine<circleR*2){
            Log.i("fzw","start");
            if(mTaskX!=null){
                mTaskX.cancel();
                mTaskX=null;
            }
            if(mTaskY!=null){
                mTaskY.cancel();
                mTaskY=null;
            }
            mTaskX=new MyTimerTask(updateHandle,1);
            mTaskY=new MyTimerTask(updateHandle,2);
            if(timer==null){
                timer=new Timer();
            }
            timer.schedule(mTaskX,0,10);

            //timer.schedule(mTaskY,1000,1000);
        }
    }

    /**
     * 设置当前电量
     * @param num 大于等于0,小于等于100
     */
    public void setNum(int num){
        if(num>=0&&num<=100){
            mLevelLine=num*speedY;
        }
    }
}

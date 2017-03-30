package android.com.powersaver;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fzw on 2017/3/29.
 */

public class PowerView extends RelativeLayout {
    private int mViewWidth;
    private int mViewHeight;

    private int circleRadius;
    private int arc1StrokeWidth;

    private int arc2Radius;
    private int arc2StrokeWidth;
    private AnimatorSet arc2AnimatorSet;

    private int arc3Radius;
    private int arc3StrokeWidth;

    private int arc4Radius;
    private int arc4StrokeWidth;
    private AnimatorSet arc4AnimatorSet;

    private WaveView waveView;
    private RippleCircleView rippleCircleView;
    private TextView mTextView;

    public PowerView(Context context) {
        this(context,null);
    }

    public PowerView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PowerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.PowerView,defStyleAttr,0);
        int n=a.getIndexCount();
        for(int i=0;i<n;i++){
            int attr=a.getIndex(i);
            switch (attr){
                case R.styleable.PowerView_circle_radius:
                    circleRadius=a.getDimensionPixelOffset(attr,150);
                    Log.i("fzw",circleRadius+"circleRadius");
                    break;
                case R.styleable.PowerView_arc1_storke_width:
                    arc1StrokeWidth=a.getDimensionPixelOffset(attr,15);
                    break;
                case R.styleable.PowerView_arc2_storke_width:
                    arc2StrokeWidth=a.getDimensionPixelOffset(attr,5);
                    break;
                case R.styleable.PowerView_arc3_storke_width:
                    arc3StrokeWidth=a.getDimensionPixelOffset(attr,60);
                    break;
                case R.styleable.PowerView_arc4_storke_width:
                    arc4StrokeWidth=a.getDimensionPixelOffset(attr,30);
                    break;
            }
        }
        a.recycle();
        init();
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public void setArc1StrokeWidth(int arc1StrokeWidth) {
        this.arc1StrokeWidth = arc1StrokeWidth;
    }

    public void setArc2StrokeWidth(int arc2StrokeWidth) {
        this.arc2StrokeWidth = arc2StrokeWidth;
    }

    public void setArc3StrokeWidth(int arc3StrokeWidth) {
        this.arc3StrokeWidth = arc3StrokeWidth;
    }

    public void setArc4StrokeWidth(int arc4StrokeWidth) {
        this.arc4StrokeWidth = arc4StrokeWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight=getMeasuredHeight();
        mViewWidth=getMeasuredWidth();
        Log.i("fzw",mViewHeight+"mViewHeight");
    }

    private void init(){
        arc2Radius=circleRadius+arc1StrokeWidth;
        arc3Radius=arc2Radius+arc2StrokeWidth;
        arc4Radius=arc3Radius;
        addCircle();
        addArc1();
        addArc2();
        addArc3();
        addArc4();
        addWave();
        addText();
    }

    private class FillArcView extends View{
        private float strokeWidth;
        private int color;
        public FillArcView(Context context,float strokeWidth,int color) {
            super(context);
            this.setVisibility(VISIBLE);
            this.strokeWidth=strokeWidth;
            this.color=color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float radius=(Math.min(getWidth(),getHeight())-strokeWidth)/2;
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(getWidth()/2,getHeight()/2,radius,paint);
        }
    }

    private class CircleView extends View{
        private int color;
        public CircleView(Context context,int color) {
            super(context);
            this.setVisibility(VISIBLE);
            this.color=color;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float radius=(Math.min(getWidth(),getHeight()))/2;
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(getWidth()/2,getHeight()/2,radius,paint);
        }
    }
    private class RippleCircleView extends View{
        private float strokeWidth;
        final float maxStrokeWidth;
        private int color;
        /** 用以实现波浪的水平移动*/
        private MyTimerTask mTask;
        /** 用以更新动画*/
        private Timer timer;
        float radius;
        float maxRadius;
        private boolean add;
        private boolean end;

        public RippleCircleView(Context context,float strokeWidth,int color) {
            super(context);
            this.setVisibility(VISIBLE);
            this.strokeWidth=strokeWidth;
            this.color=color;
            maxStrokeWidth=strokeWidth;
            add=false;
            end=true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            radius=(Math.min(getMeasuredWidth(),getMeasuredHeight())-strokeWidth)/2;
            maxRadius=radius;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(getWidth()/2,getHeight()/2,radius,paint);
        }
        private Handler updateHandle=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                change();
            }
        };
        private void change(){
            if(!add){
                strokeWidth-=2;
                radius-=1;
            }else{
                strokeWidth+=2;
                radius+=1;
                if(radius==maxRadius||strokeWidth==maxStrokeWidth){
                    end=true;
                }
            }
            if(strokeWidth<=1){
                arc4AnimatorSet.start();
                add=true;
            }
            if(end){
                if(timer!=null){
                    timer.cancel();
                    mTask.cancel();
                    timer=null;
                    mTask=null;
                }
            }else {
                invalidate();
            }
        }

        private class MyTimerTask extends TimerTask {
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
        public void start() {
            if(end){
                if(mTask!=null){
                    mTask.cancel();
                    mTask=null;
                }
                mTask=new MyTimerTask(updateHandle,1);
                if(timer==null){
                    timer=new Timer();
                }
                end=false;
                add=false;
                timer.schedule(mTask,0,30);
            }
        }
    }

    private void addWave(){
        LayoutParams params=new LayoutParams(2*(circleRadius),2*(circleRadius));
        params.addRule(CENTER_IN_PARENT, TRUE);
        waveView=new WaveView(getContext());
        waveView.setVisibility(INVISIBLE);
        addView(waveView,params);

    }

    private void addCircle(){
        LayoutParams params=new LayoutParams(2*(circleRadius),2*(circleRadius));
        params.addRule(CENTER_IN_PARENT, TRUE);
        CircleView circleView=new CircleView(getContext(),Color.WHITE);
        circleView.setAlpha(0.25f);
        addView(circleView,params);
    }

    private void addArc1(){
        LayoutParams params=new LayoutParams(2*(circleRadius+arc1StrokeWidth),2*(circleRadius+arc1StrokeWidth));
        params.addRule(CENTER_IN_PARENT, TRUE);
        FillArcView fillArcView=new FillArcView(getContext(),arc1StrokeWidth,Color.WHITE);
        fillArcView.setAlpha(0.1f);
        addView(fillArcView,params);
    }

    private void addArc2(){
        LayoutParams params=new LayoutParams(2*(arc2Radius+arc2StrokeWidth),2*(arc2Radius+arc2StrokeWidth));
        params.addRule(CENTER_IN_PARENT, TRUE);
        FillArcView fillArcView=new FillArcView(getContext(),arc2StrokeWidth,getResources().getColor(R.color.circle2));
        fillArcView.setAlpha(0.9f);
        addView(fillArcView,params);
        arc2AnimatorSet=new AnimatorSet();
        final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(fillArcView, "Alpha", 1.0f, 0f,1.0f,0.0f,1.0f);
        alphaAnimator.setStartDelay(0);
        alphaAnimator.setDuration(3000);
        arc2AnimatorSet.play(alphaAnimator);
    }

    private void addArc3(){
        LayoutParams params=new LayoutParams(2*(arc3Radius+arc3StrokeWidth),2*(arc3Radius+arc3StrokeWidth));
        params.addRule(CENTER_IN_PARENT, TRUE);
        rippleCircleView=new RippleCircleView(getContext(),arc3StrokeWidth,Color.WHITE);
        rippleCircleView.setAlpha(0.1f);
        addView(rippleCircleView,params);
    }

    private void addArc4(){

        LayoutParams params=new LayoutParams(2*(arc4Radius+arc4StrokeWidth),2*(arc4Radius+arc4StrokeWidth));
        params.addRule(CENTER_IN_PARENT, TRUE);
        ArcView arcView=new ArcView(getContext(),arc4StrokeWidth,Color.WHITE);
        arcView.setAlpha(0.3f);
        addView(arcView,params);
        final ObjectAnimator rotationAnimator1 = ObjectAnimator.ofFloat(arcView, "rotation",0f,191.25f);
        rotationAnimator1.setStartDelay(0);
        rotationAnimator1.setDuration(3000);
        rotationAnimator1.setInterpolator(null);
        final ObjectAnimator rotationAnimator2 = ObjectAnimator.ofFloat(arcView, "rotation",191.25f,157.5f,180f);
        rotationAnimator2.setStartDelay(3000);
        rotationAnimator2.setDuration(2000);
        rotationAnimator2.setInterpolator(null);
        arc4AnimatorSet=new AnimatorSet();
        arc4AnimatorSet.playTogether(rotationAnimator1,rotationAnimator2);
    }

    private void addText(){
        LayoutParams params=new LayoutParams(2*(circleRadius),2*(circleRadius));
        params.setMargins(0,0,0,0);
        params.addRule(CENTER_IN_PARENT, TRUE);
        mTextView=new TextView(getContext());
        mTextView.setText("80");
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(80);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setAlpha(1.0f);
        addView(mTextView,params);
    }

    private class ArcView extends View{
        private float strokeWidth;
        private int color;
        public ArcView(Context context,float strokeWidth,int color) {
            super(context);
            this.setVisibility(VISIBLE);
            this.strokeWidth=strokeWidth;
            this.color=color;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            float radius=(Math.min(getWidth(),getHeight())-this.strokeWidth)/2;
            Paint paint=new Paint();
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(this.strokeWidth);
            canvas.drawArc(getWidth()/2-radius,getHeight()/2-radius,getWidth()/2+radius,getHeight()/2+radius,0,45,false,paint);

            canvas.drawArc(getWidth()/2-radius,getHeight()/2-radius,getWidth()/2+radius,getHeight()/2+radius,67.5f,90,false,paint);

            canvas.drawArc(getWidth()/2-radius,getHeight()/2-radius,getWidth()/2+radius,getHeight()/2+radius,180,45,false,paint);

            canvas.drawArc(getWidth()/2-radius,getHeight()/2-radius,getWidth()/2+radius,getHeight()/2+radius,247.5f,90,false,paint);
        }
    }

    public void startAnimation(){
        if(!isRippleAnimationRunning()){
            if(rippleCircleView!=null){
                rippleCircleView.start();
                arc2AnimatorSet.start();
            }
            //animationRunning=true;
        }
    }
    private  boolean animationRunning=false;

    public boolean isRippleAnimationRunning(){
        return animationRunning;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //startAnimation();
    }

    public void showWave(){
        if(waveView!=null){
            waveView.setVisibility(VISIBLE);
        }
    }

    public void showWaveAnimation(){
        if(waveView!=null){
            waveView.start();
        }
    }

    public void setWaveLevelLine(int levelLine){
        if(waveView!=null){
            waveView.setNum(levelLine);
        }
    }

}

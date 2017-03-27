package android.com.powersaver;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fzw on 2017/3/27.
 */

public class WaveView extends View {
    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private int mViewHeight;
    private int mViewWidht;

    private float mLevelLine;
    private float mWaveHeight;



    Handler updateHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };





}

package com.example.tractor.views.joysticks.roundJoystick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.tractor.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 24.07.17.
 */

public class RoundJoystick extends View
{

    private Paint _paint;

    private int _radius = 190;
    private int _controlRadius = 104;
    private final int _MAX_VALUE = 100;

    private PointF _controlPosition;
    private PointF _centerPoint;

    private Bitmap _controlBitmap;
    private Bitmap _controlBackBitmap;

    private int _xValue = 0;
    private int _yValue = 0;

    private RoundJoysticChangeListener _listener;

    private ValueAnimator _backControlAnimator;
    private  float _animStartX;
    private  float _animStartY;

    private Timer _sendDataTimer;

    //Send data delay
    private final int _TICK_DELAY = 100;

    private final int _BACK_ANIMATION_DURATION = 300;


    public RoundJoystick(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        _paint = new Paint();
        _paint.setAntiAlias(true);
//        _paint.setTextSize(30);

        _controlPosition = new PointF();
        _centerPoint = new PointF();

        _controlBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joy_con);
        _controlBackBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joy_back);

        _backControlAnimator = ValueAnimator.ofFloat(1, 0);
        _backControlAnimator.setDuration(_BACK_ANIMATION_DURATION);
        _backControlAnimator.setInterpolator(new DecelerateInterpolator());
        _backControlAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            public void onAnimationUpdate(ValueAnimator animation)
            {
                float animationValue = (float) animation.getAnimatedValue();

                _controlPosition.x = _centerPoint.x + (_animStartX - _centerPoint.x) * animationValue;
                _controlPosition.y = _centerPoint.y + (_animStartY - _centerPoint.y) * animationValue;

                updateValues();
            }
        });
    }


    public void setChangeListener(RoundJoysticChangeListener listener)
    {
        _listener = listener;

        _sendDataTimer = new Timer();
        _sendDataTimer.schedule(new RoundJoystick.SendChangeToListenerTask(), 0, _TICK_DELAY);

    }

    public void removeChangeListener()
    {
        _listener = null;

        _sendDataTimer.cancel();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                calculatePosition(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_MOVE:
                calculatePosition(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
                backControlToCenter();
                return true;

            default:
        }

        return  false;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        _centerPoint.set(getWidth()/2, getHeight()/2);
        _controlPosition.set(_centerPoint.x, _centerPoint.y);
//        _controlPosition.set(_centerPoint);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawBitmap(_controlBackBitmap, _centerPoint.x - _controlBackBitmap.getWidth()/2, _centerPoint.y - _controlBackBitmap.getHeight()/2, _paint);
        canvas.drawBitmap(_controlBitmap, _controlPosition.x - _controlBitmap.getWidth()/2, _controlPosition.y - _controlBitmap.getHeight()/2, _paint);
    }



    ////////////////
    //Private
    ////////////////


    private void updateValues()
    {
        _xValue = (int) (_MAX_VALUE  * (_controlPosition.x - _centerPoint.x)/(_radius - _controlRadius));
        _yValue = (int) (_MAX_VALUE  * -(_controlPosition.y - _centerPoint.y)/(_radius - _controlRadius));

        invalidate();

        //Если отправлять сразу(без таймера) то иногда теряются пакеты, значения не доходят

//        if(_listener!=null)
//        {
//            _listener.onJoystickPositionChange(_xValue, _yValue);
//        }
    }


    private void calculatePosition(float touchX, float touchY)
    {
        float maxDistance = _radius - _controlRadius;

        double distanceToCenter = Math.sqrt(Math.pow((touchX - _centerPoint.x), 2) + Math.pow((touchY - _centerPoint.y), 2));

        PointF touchPoint = new PointF(touchX, touchY);

        if(distanceToCenter > maxDistance)
        {
            //to local coords
            touchPoint.offset(-_centerPoint.x, -_centerPoint.y);

            PointF newControlPosition = new PointF(touchPoint.x * maxDistance/touchPoint.length(), touchPoint.y * maxDistance/touchPoint.length());
            newControlPosition.offset(_centerPoint.x, _centerPoint.y);

            _controlPosition = newControlPosition;
        }
        else
        {
            _controlPosition = touchPoint;
        }

        updateValues();
    }


    private void backControlToCenter()
    {
        stopAnimation();

        _animStartX = _controlPosition.x;
        _animStartY = _controlPosition.y;

        _backControlAnimator.start();
    }


    private void stopAnimation()
    {
        if(_backControlAnimator.isStarted())
        {
            _backControlAnimator.end();
            _backControlAnimator.cancel();
            clearAnimation();
        }
    }


    private class SendChangeToListenerTask extends TimerTask
    {
        private int _lastX = 0;
        private int _lastY = 0;

        @Override
        public void run()
        {
            if(_lastX!=_xValue || _lastY!=_yValue)
            {
                _listener.onJoystickPositionChange(_xValue, _yValue);

                _lastX = _xValue;
                _lastY = _yValue;
            }
        }
    }

}

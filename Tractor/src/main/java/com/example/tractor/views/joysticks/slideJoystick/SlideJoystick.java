package com.example.tractor.views.joysticks.slideJoystick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.tractor.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 08.08.17.
 */

public class SlideJoystick extends View
{
    protected Paint _paint;

    protected int _value = 0;
    protected final int _MAX_VALUE = 100;

//    private int _controlRadius = 80;
    protected int _slideSize = 190;

    protected float _controlPosition;

    protected PointF _centerPoint;

    protected Bitmap _controlBitmap;

    private final int _BACK_ANIMATION_DURATION = 300;
    protected ValueAnimator _backControlAnimator;
    protected float _animStartPos;

    private Timer _sendDataTimer;
    //Send data delay
    private final int _TICK_DELAY = 100;

    private SlideJoysticChangeListener _listener;


    public static final int TYPE_VERTICAL = 1;
    public static final int TYPE_HORIZONTAL = 2;

    private int _type;


    public SlideJoystick(int type, Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        this._type = type;

        _paint = new Paint();
        _paint.setColor(Color.DKGRAY);
        _paint.setAntiAlias(true);
        _paint.setTextSize(30);
        _paint.setTextAlign(Paint.Align.CENTER);

        _centerPoint = new PointF();

        _controlBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.joy_con_small);

        _backControlAnimator = ValueAnimator.ofFloat(1, 0);
        _backControlAnimator.setDuration(_BACK_ANIMATION_DURATION);
        _backControlAnimator.setInterpolator(new DecelerateInterpolator());


        _backControlAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            public void onAnimationUpdate(ValueAnimator animation)
            {

                float animationValue = (float) animation.getAnimatedValue();

                if(_type==TYPE_HORIZONTAL)
                {
                    _controlPosition =  _centerPoint.x + ( _animStartPos -  _centerPoint.x) * animationValue;
                }

                if(_type==TYPE_VERTICAL)
                {
                    _controlPosition =  _centerPoint.y + ( _animStartPos -  _centerPoint.y) * animationValue;

                }

                updateValues();
            }
        });

    }


    public int getValue()
    {
        return _value;
    }

    public int getType()
    {
        return _type;
    }


    public void setChangeListener(SlideJoysticChangeListener listener)
    {
        _listener = listener;

        _sendDataTimer = new Timer();
        _sendDataTimer.schedule(new SlideJoystick.SendChangeToListenerTask(), 0, _TICK_DELAY);

    }


    public void removeChangeListener()
    {
        _listener = null;

        _sendDataTimer.cancel();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchCoordinate = 0;

        if(_type == TYPE_HORIZONTAL)
        {
            touchCoordinate = event.getX();
        }

        if(_type == TYPE_VERTICAL)
        {
            touchCoordinate = event.getY();
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                calculatePosition(touchCoordinate);
                return true;

            case MotionEvent.ACTION_MOVE:
                calculatePosition(touchCoordinate);
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

        if(_type == TYPE_HORIZONTAL)
        {
            _controlPosition = _centerPoint.x;
        }

        if(_type == TYPE_VERTICAL)
        {
            _controlPosition = _centerPoint.y;
        }
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(_type == TYPE_HORIZONTAL)
        {
            canvas.drawCircle(_centerPoint.x - _slideSize /2, _centerPoint.y, 20, _paint);
            canvas.drawCircle(_centerPoint.x + _slideSize /2, _centerPoint.y, 20, _paint);
            canvas.drawRect(_centerPoint.x - _slideSize /2, _centerPoint.y - 20, _centerPoint.x + _slideSize /2, _centerPoint.y + 20, _paint);
            canvas.drawBitmap(_controlBitmap, _controlPosition - _controlBitmap.getWidth()/2, _centerPoint.y - _controlBitmap.getHeight()/2, _paint);

            if(_value!=0)
            {
                canvas.drawText(Integer.toString(_value), _controlPosition, _centerPoint.y - _controlBitmap.getHeight()/2 - 10, _paint);
            }

        }

        if(_type == TYPE_VERTICAL)
        {
            canvas.drawCircle(_centerPoint.x, _centerPoint.y - _slideSize/2, 20, _paint);
            canvas.drawCircle(_centerPoint.x, _centerPoint.y + _slideSize /2, 20, _paint);
            canvas.drawRect(_centerPoint.x - 20, _centerPoint.y - _slideSize/2, _centerPoint.x + 20, _centerPoint.y + _slideSize/2, _paint);
            canvas.drawBitmap(_controlBitmap, _centerPoint.x - _controlBitmap.getWidth()/2, _controlPosition -  _controlBitmap.getHeight()/2, _paint);

            if(_value!=0)
            {
                canvas.drawText(Integer.toString(_value), _centerPoint.x + _controlBitmap.getWidth()/2 + 30, _controlPosition, _paint);
            }

        }
    }


    ////////////////
    //Private
    ////////////////


    protected void updateValues()
    {
        if(_type == TYPE_HORIZONTAL)
        {
            _value = (int) (_MAX_VALUE * (_controlPosition - _centerPoint.x)/(_slideSize /2));
        }

        if(_type == TYPE_VERTICAL)
        {
            _value = (int) -(_MAX_VALUE * (_controlPosition - _centerPoint.y)/(_slideSize /2));
        }

        invalidate();
    }


    protected void calculatePosition(float touchPos)
    {
        if(_type == TYPE_HORIZONTAL)
        {
            if(touchPos< _centerPoint.x)
            {
                _controlPosition = (int) Math.max(touchPos, _centerPoint.x - _slideSize /2);
            }else
            {
                _controlPosition = (int) Math.min(touchPos, _centerPoint.x + _slideSize /2);
            }
        }

        if(_type == TYPE_VERTICAL)
        {
            if(touchPos< _centerPoint.y)
            {
                _controlPosition = (int) Math.max(touchPos, _centerPoint.y - _slideSize /2);
            }else
            {
                _controlPosition = (int) Math.min(touchPos, _centerPoint.y + _slideSize /2);
            }
        }

        updateValues();
    }


    private void backControlToCenter()
    {
        stopAnimation();

        _animStartPos = _controlPosition;

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
        private int _lastValue = 0;

        @Override
        public void run()
        {
            if(_lastValue != _value)
            {
                _listener.onJoystickPositionChange(_value, SlideJoystick.this);
                _lastValue = _value;
            }
        }
    }


}

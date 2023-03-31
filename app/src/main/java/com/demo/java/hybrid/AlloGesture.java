package com.demo.java.hybrid;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;

public class AlloGesture implements View.OnTouchListener
{
    public final static int GESTURE_STARTED = 101;
    public final static int GESTURE_FINISHED = 102;

    public final static int SINGLE_SWIPE_TOP = 111;
    public final static int SINGLE_SWIPE_BOTTOM = 112;
    public final static int SINGLE_SWIPE_LEFT = 113;
    public final static int SINGLE_SWIPE_RIGHT = 114;

    public final static int DOUBLE_SWIPE_TOP = 121;
    public final static int DOUBLE_SWIPE_BOTTOM = 122;
    public final static int DOUBLE_SWIPE_LEFT = 123;
    public final static int DOUBLE_SWIPE_RIGHT = 124;

    private Listener listener;
    private final GestureDetector detector;

    public interface Listener
    {
        public void onGesture (int type);
    }

    public AlloGesture (Context context)
    {
        listener = (Listener) context;
        detector = new GestureDetector(context, new SimpleListener ());
    }

    @Override
    public boolean onTouch (View view, MotionEvent event)
    {
        Allo.i ("onTouch " + getClass ());

        boolean status = detector.onTouchEvent (event);

        try
        {
            switch (event.getAction ()) {
                case MotionEvent.ACTION_DOWN: {
                    onGestureStarted ();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    onGestureFinished ();
                    break;
                }
            }
        } catch (Exception e) { e.printStackTrace (); }

        return status;
    }

    public void onGestureStarted ()
    {
        Allo.i ("onGestureStarted " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (GESTURE_STARTED);
        } catch (Exception e) { e.printStackTrace (); }
    }

    public void onGestureFinished ()
    {
        Allo.i ("onGestureFinished " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (GESTURE_FINISHED);
        } catch (Exception e) { e.printStackTrace (); }
    }

    public void onSwipeLeft ()
    {
        Allo.i ("onSwipeLeft " + getClass ());

        try
        {
            onSingleSwipeLeft ();
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onSingleSwipeLeft ()
    {
        Allo.i ("onSingleSwipeLeft " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (SINGLE_SWIPE_LEFT);
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onDoubleSwipeLeft ()
    {
        Allo.i ("onDoubleSwipeLeft " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (DOUBLE_SWIPE_LEFT);
        } catch (Exception e) { e.printStackTrace (); }
    }

    public void onSwipeRight ()
    {
        Allo.i ("onSwipeRight " + getClass ());

        try
        {
            onSingleSwipeRight ();
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onSingleSwipeRight ()
    {
        Allo.i ("onSingleSwipeRight " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (SINGLE_SWIPE_RIGHT);
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onDoubleSwipeRight ()
    {
        Allo.i ("onDoubleSwipeRight " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (DOUBLE_SWIPE_RIGHT);
        } catch (Exception e) { e.printStackTrace (); }
    }

    public void onSwipeTop ()
    {
        Allo.i ("onSwipeTop " + getClass ());

        try
        {
            onSingleSwipeTop ();
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onSingleSwipeTop ()
    {
        Allo.i ("onSingleSwipeTop " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (SINGLE_SWIPE_TOP);
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onDoubleSwipeTop ()
    {
        Allo.i ("onDoubleSwipeTop " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (DOUBLE_SWIPE_TOP);
        } catch (Exception e) { e.printStackTrace (); }
    }

    public void onSwipeBottom ()
    {
        Allo.i ("onSwipeBottom " + getClass ());

        try
        {
            onSingleSwipeBottom ();
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onSingleSwipeBottom ()
    {
        Allo.i ("onSingleSwipeBottom " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (SINGLE_SWIPE_BOTTOM);
        } catch (Exception e) { e.printStackTrace (); }
    }
    public void onDoubleSwipeBottom ()
    {
        Allo.i ("onDoubleSwipeBottom " + getClass ());

        try
        {
            if (null != listener) listener.onGesture (DOUBLE_SWIPE_BOTTOM);
        } catch (Exception e) { e.printStackTrace (); }
    }
    
    private final class SimpleListener extends SimpleOnGestureListener
    {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int DOUBLE_THRESHOLD = 500;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        private Date prevTopDate = new Date ();
        private Date doneTopDate = new Date ();
        private Date prevBottomDate = new Date ();
        private Date doneBottomDate = new Date ();

        private Date prevLeftDate = new Date ();
        private Date doneLeftDate = new Date ();
        private Date prevRightDate = new Date ();
        private Date doneRightDate = new Date ();

        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            Allo.i ("onFling " + getClass ());

            boolean status = false;

            try 
            {
                float diffX = e1.getX () - e2.getX ();
                float diffY = e1.getY () - e2.getY ();
                if (Math.abs (diffX) < Math.abs (diffY))
                {
                    if (SWIPE_THRESHOLD < Math.abs (diffY) && SWIPE_VELOCITY_THRESHOLD < Math.abs (velocityY))
                    {
                        if (0 < diffY)
                        {
                            doneTopDate = new Date ();
                            long interval = Math.abs (doneTopDate.getTime () - prevTopDate.getTime ());

                            if (DOUBLE_THRESHOLD < interval)
                            {
                                onSingleSwipeTop ();
                            }
                            else
                            {
                                onDoubleSwipeTop ();
                            }
                            prevTopDate = doneTopDate;
                        }
                        if (0 > diffY)
                        {
                            doneBottomDate = new Date ();
                            long interval = Math.abs (doneBottomDate.getTime () - prevBottomDate.getTime ());

                            if (DOUBLE_THRESHOLD < interval)
                            {
                                onSingleSwipeBottom ();
                            }
                            else
                            {
                                onDoubleSwipeBottom ();
                            }
                            prevBottomDate = doneBottomDate;
                        }

                    }
                }
                if (Math.abs (diffX) > Math.abs (diffY))
                {
                    if (SWIPE_THRESHOLD < Math.abs (diffX) && SWIPE_VELOCITY_THRESHOLD < Math.abs (velocityX))
                    {
                        if (0 < diffX)
                        {
                            doneLeftDate = new Date ();
                            long interval = Math.abs (doneLeftDate.getTime () - prevLeftDate.getTime ());
                            if (DOUBLE_THRESHOLD < interval)
                            {
                                onSingleSwipeLeft ();
                            }
                            else
                            {
                                onDoubleSwipeLeft ();
                            }
                            prevLeftDate = doneLeftDate;
                        }
                        if (0 > diffX)
                        {
                            doneRightDate = new Date ();
                            long interval = Math.abs (doneRightDate.getTime () - prevRightDate.getTime ());
                            if (DOUBLE_THRESHOLD < interval)
                            {
                                onSingleSwipeRight ();
                            }
                            else
                            {
                                onDoubleSwipeRight ();
                            }
                            prevRightDate = doneRightDate;
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace (); }

            return status;
        }
    }
}

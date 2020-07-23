package com.clicbrics.consumer.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import com.clicbrics.consumer.utils.FloatPointDrawn;
import com.clicbrics.consumer.view.activity.HomeScreen;

import java.util.ArrayList;

/**
 * Created by Paras on 05-09-2016.
 */
public class DrawingView extends View {

    private IDrawMapBound mListener;
    public interface IDrawMapBound{
        void drawMapTask();
    }
    public int width;
    public  int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;
    public DrawingView(Context c) {
        super(c);
        context=c;
        if(context instanceof HomeScreen){
            mListener = (IDrawMapBound) context;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(2f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private float sX, sY;
    private void touch_start(float x, float y) {
        mPath.reset();
        mBitmap.eraseColor(Color.TRANSPARENT);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        sX = x;
        sY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            pointArray.add(new FloatPointDrawn(mX,mY));
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mPath.lineTo(sX, sY);
        pointArray.add(new FloatPointDrawn(sX,sY));
        // commit the path to our  offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
        /*if(context instanceof MapActivity){
            ((MapActivity) context).drawMapTask();
        }*/

        /*if(context instanceof SearchResults){
            ((MapFragmentPage)((SearchResults) context).getSupportFragmentManager()
                    .findFragmentByTag("mapfragment")).drawMapTask();
        }*/

        if(mListener != null){
            mListener.drawMapTask();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointArray.add(new FloatPointDrawn(x,y));
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    ArrayList<FloatPointDrawn> pointArray =  new ArrayList<FloatPointDrawn>();
    public ArrayList<FloatPointDrawn> getPoints() {
//        FloatPointDrawn[] pointArray = new FloatPointDrawn[20];
//        ArrayList<FloatPointDrawn> pointArray =  new ArrayList<FloatPointDrawn>();
//        PathMeasure pm = new PathMeasure(mPath, false);
//        float length = pm.getLength();
//        float distance = 0f;
//        float speed = length / 20;
//        int counter = 0;
//        float[] aCoordinates = new float[2];
//
//        while ((distance < length)) {
//            // get point from the path
//            pm.getPosTan(distance, aCoordinates, null);
//            pointArray.add(new FloatPointDrawn(aCoordinates[0],
//                    aCoordinates[1]));
//            counter++;
//            distance = distance + speed;
//        }

        return pointArray;
    }
    public void clearPoints(){
        pointArray.clear();
        try {
            mPath.reset();
            mBitmap.eraseColor(Color.TRANSPARENT);
        }catch (Exception ex){}
    }
}

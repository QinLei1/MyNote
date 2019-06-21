package io.github.mkckr0.mynote.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DrawView extends View {
    class Line {
        Path path;
        Paint paint;

        Line(Path path, Paint paint) {
            this.path = new Path(path);
            this.paint = new Paint(paint);
        }
    }

    private Paint paint;
    private Paint clearPaint;
    private boolean isClear;
    private ArrayList<Line> lines;
    private Bitmap bitmap;
    private Canvas mCanvas;

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lines.add(new Line(new Path(), paint));
                lines.get(lines.size() - 1).path.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                lines.get(lines.size() - 1).path.lineTo(event.getX(), event.getY());
                invalidate();
                if (isClear) {
                    mCanvas.drawPath(lines.get(lines.size() - 1).path, clearPaint);
                } else {
                    mCanvas.drawPath(lines.get(lines.size() - 1).path, paint);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        lines = new ArrayList<>();
        setPaint(Color.BLACK, 10);
        isClear = false;
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Line line : lines) {
            canvas.drawPath(line.path, line.paint);
        }
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    public void setPaintWidth(int width) {
        paint.setStrokeWidth(width);
    }

    public void setPaintClear(boolean clear) {
        isClear = clear;
        if (clear) {
            clearPaint = new Paint(paint);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            clearPaint = null;
        }
    }

    public void setPaint(int color, int width) {
        //设置画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.BEVEL);
    }

    public Bitmap getDrawing() {
        if (bitmap.sameAs(Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888))) {
            return null;
        } else {
            return bitmap;
        }
    }
}
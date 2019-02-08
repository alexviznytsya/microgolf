/**
 * Hole.java
 *
 * Alex Viznytsya
 * CS 478 Software Development for Mobile Platforms
 * Spring 2028, UIC
 *
 * Project 4 - Microgolf
 * 04/16/2018
 */

package edu.uic.cs478.sp18.avizny2.project4.microgolf;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Hole extends View {

    private int width = -1;
    private int height = -1;
    private int xPos = 0;
    private int yPos = 28;
    private int radius = 15;
    private int circleColor = Color.BLACK;
    private float strokeWidth = 3.0f;
    private Paint.Style pStyle = Paint.Style.STROKE;

    private Paint myP = null;

    public Hole(Context context) {
        super(context);
        this.init(context, null);
    }

    public Hole(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public Hole(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public Hole(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.myP = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.Hole);
        this.circleColor = arr.getColor(R.styleable.Hole_cirleColor, Color.BLACK);
        if(this.circleColor != Color.BLACK) {
            this.pStyle = Paint.Style.FILL_AND_STROKE;
        }
        arr.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.myP.setStyle(this.pStyle);
        this.myP.setColor(circleColor);
        this.myP.setStrokeWidth(this.strokeWidth);
        canvas.drawCircle(18, 18, this.radius, this.myP);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(36,36);
    }

    //
    // Change hole fill color:
    //
    public void changeColor(int color) {
        this.circleColor = color;
        this.pStyle = Paint.Style.FILL_AND_STROKE;
        invalidate();
    }

    //
    // Return hole to its default value:
    //
    public void makeDefault() {
        this.circleColor = Color.BLACK;
        this.pStyle = Paint.Style.STROKE;
        invalidate();
    }
}

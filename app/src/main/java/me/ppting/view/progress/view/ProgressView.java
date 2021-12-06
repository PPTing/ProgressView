package me.ppting.view.progress.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import me.ppting.view.progress.R;

import static android.graphics.Path.Direction.CW;

/**
 * Created by PPTing on 6/24/21.
 * Description:
 */

public class ProgressView extends View {

    private final Paint mPaint;
    private int mCorner = 50 * 3;//圆角值

    private final RectF mContentRect;
    private final Path mClipPath;

    private final Bitmap mOriginBackground;
    private Bitmap mBackground;
    private int mOriginBackgroundWidth = 1;
    private int mOriginBackgroundHeight = 1;
    private Matrix matrix;


    public ProgressView(Context context) {
        this(context, null);

    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr);
    }

    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);

        mContentRect = new RectF();
        mClipPath = new Path();
        mClipPath.addRoundRect(mContentRect, mCorner, mCorner, CW);

        mOriginBackground = BitmapFactory.decodeResource(getResources(), R.drawable.voiceroom_bg_summer2021_progress);
        mOriginBackgroundWidth = mOriginBackground.getWidth();
        mOriginBackgroundHeight = mOriginBackground.getHeight();

        matrix = new Matrix();

    }

    private void initView(AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressView, defStyleAttr, 0);
        mCorner = typedArray.getDimensionPixelSize(R.styleable.ProgressView_corner, 50 * 3);

        typedArray.recycle();
    }

    /**
     * 左边值
     */
    private int mLeftValue = 0;
    /**
     * 右边的值
     */
    private int mRightValue = 0;

    /**
     * 进度比例
     */
    private float mPercent = 0.5f;

    /**
     * 更新左边的值
     *
     * @param leftValue
     */
    public void updateLeftValue(int leftValue) {
        mLeftValue = leftValue;
        dealValue(leftValue,mRightValue);
    }

    /**
     * 更新右边的值
     *
     * @param rightValue
     */
    public void updateRightValue(int rightValue) {
        mRightValue = rightValue;
        dealValue(mLeftValue,rightValue);
    }

    /**
     * 更新左右两边的值
     *
     * @param leftValue
     * @param rightValue
     */
    public void updateValues(int leftValue, int rightValue) {
        mLeftValue = leftValue;
        mRightValue = rightValue;
        dealValue(leftValue, rightValue);
    }

    /**
     * 处理数据
     * 一正一负，将负数置为0
     * 两个都是负数，则交换绝对值
     * 两个都是正数，不处理
     */
    private void dealValue(int leftValue,int rightValue){

        if (leftValue > 0 && rightValue < 0){
            leftValue = Math.abs(leftValue);
            rightValue = 0;
        } else if (leftValue < 0 && rightValue > 0){
            leftValue = 0;
            rightValue = Math.abs(rightValue);
        } else if (leftValue == 0 && rightValue < 0){
            //一个为0，一个为负数
            leftValue = 1;
            rightValue = 0;
        } else if (leftValue < 0 && rightValue == 0){
            //一个为0，一个为负数
            rightValue = 1;
            leftValue = 0;
        } if (leftValue < 0 && rightValue < 0){
            //两个均为负数
            int tmp = Math.abs(rightValue);
            rightValue = Math.abs(leftValue);
            leftValue = tmp;
        }
        mPercent = (float) leftValue / (leftValue + rightValue);
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //需要缩放一下背景图，因为图使用的是高为 25dp 的图，所以需要根据高度缩放/放大
        Matrix matrix = new Matrix();
        //按照高度放大缩小
        float scaleHeight = (float) h / mOriginBackgroundHeight;
        //宽度至少要是 view 的两倍宽
        float scaleWidth = (2f * w) / mOriginBackgroundWidth;

        matrix.postScale(scaleWidth, scaleHeight);
        mBackground = Bitmap.createBitmap(mOriginBackground, 0, 0, mOriginBackgroundWidth, mOriginBackgroundHeight, matrix, true);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //做左右两个圆角的裁切
        mContentRect.set(0, 0, getWidth(), getHeight());
        mClipPath.addRoundRect(mContentRect, mCorner, mCorner, CW);
        canvas.clipPath(mClipPath);

        //画 bitmap
        matrix.reset();
        int centerTransX = (mBackground.getWidth() - getWidth()) / 2;

        int percentWidth = (int) ((mPercent - 0.5f) * (getWidth()));
        matrix.postTranslate(-centerTransX + percentWidth, 0);
        canvas.drawBitmap(mBackground, matrix, mPaint);
    }
}

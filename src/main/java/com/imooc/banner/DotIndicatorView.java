package com.imooc.banner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 自定义轮播图的指示器
 */
public class DotIndicatorView extends View {

    private Drawable mDrawable;
    public DotIndicatorView(Context context) {
        this(context,null);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable!=null){
//            mDrawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
//            mDrawable.draw(canvas);

            Bitmap bitmap = drawableToBitmap(mDrawable);

            // 把bitmap 变成圆的
            Bitmap circleBitmap = getCircleBitmap(bitmap);

            // 把圆形的Bitmap 绘制到画布上
            canvas.drawBitmap(circleBitmap,0,0,null);
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);

        Paint paint= new Paint();
        // 设置抗锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        // 设置防抖动
        paint.setDither(true);

        // 在画布上面画个圆
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2,getMeasuredWidth()/2,paint);

        // 取圆和bitmap 矩形的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 再把原来的Bitmap 绘制到新的圆上面
        canvas.drawBitmap(bitmap,0,0,paint);

        return outBitmap;
    }

    public void setDrawable(Drawable drawable){
        this.mDrawable = drawable;
        // 重新绘制View
        invalidate();
    }

    /**
     * drawable 转 Bitmap
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable){
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);

        // 把drawable 绘制到bitmap 上
        drawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
        drawable.draw(canvas);
        return outBitmap;
    }

}

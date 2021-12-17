package com.imooc.banner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 *  广告轮播的ViewPager
 */
public class BannerViewPager extends ViewPager {

    private BannerAdapter mAdapter;
    private static final int SCROLL_MSG = 0x110 ;
    private long mCutDownTime = 3500;
    // 改变viewpager的切换速率，自定义页面切换的Scroller
    private BannerScroller mScroller;


    // 内存优化 -> 当前Activity
    private Activity mActivity;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 切换页面
            setCurrentItem(getCurrentItem()+1);
            // 不断循环执行
            startRoll();
        }
    };

    // 内存优化界面复用
    private List<View> mConvertViews;

    public BannerViewPager(Context context) {
        this(context,null);
    }
    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.mActivity = (Activity) context;
        try {
            // 通过反射设置自定义Scroller
            Field field = ViewPager.class.getDeclaredField("mScroller");
            mScroller = new BannerScroller(context);
            field.setAccessible(true);
            // 第一个参数表示当前属性在哪个类，第二个参数代表设置的值
            field.set(this,mScroller);
        }catch (Exception e){
            e.printStackTrace();
        }
        mConvertViews = new ArrayList<>();
    }

    /**
     * 设置切换页面动画的持续时间
     * @param scrollerDuration
     */
    public void setScrollerDuration(int scrollerDuration){
        mScroller.setScrollerDuration(scrollerDuration);
    }

    public void setAdapter(BannerAdapter adapter) {
        this.mAdapter = adapter;
        // 设置父类 ViewPager 的 adapter
        setAdapter(new BannerPagerAdapter());

        // 管理Activity 生命周期
        mActivity.getApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    /**
     * 实现自动轮播
     */
    public void startRoll(){
        // 清除消息
        mHandler.removeMessages(SCROLL_MSG);
        // 发送延迟消息
        mHandler.sendEmptyMessageDelayed(SCROLL_MSG,mCutDownTime);
    }

    /**
     * 2.销毁Handler停止发送
     */
    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeMessages(SCROLL_MSG);
        mHandler = null;
        mActivity.getApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        super.onDetachedFromWindow();
    }

    private  class BannerPagerAdapter  extends PagerAdapter{

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // position%mAdapter.getCount(): 防止数组越界
            View bannerView = mAdapter.getView(position%mAdapter.getCount(),getConvertView());
            container.addView(bannerView);
            return bannerView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // 销毁回调的方法  移除页面即可
            container.removeView((View) object);
            mConvertViews.add((View) object);
        }
    }

    /**
     * 获取复用界面
     * @return
     */
    private View getConvertView() {
        for (View view : mConvertViews) {
            if (view.getParent() == null){
                return view;
            }
        }
        return null;
    }

    /**
     * 轮播图点击事件
     */
    public interface BannerItemClickListener{
        void click(int position);
    }
    /**
     * 监听Activity生命周期，设置轮播图轮播状态
     */
    Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new DefaultActivityLifecycleCallbacks(){
        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            // 如果是当前Activity
            if (activity == mActivity){
                // 开启轮播
                mHandler.sendEmptyMessageDelayed(SCROLL_MSG,mCutDownTime);
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            if (activity == mActivity){
                // 停止轮播
                mHandler.removeMessages(SCROLL_MSG);
            }
        }
    };
}

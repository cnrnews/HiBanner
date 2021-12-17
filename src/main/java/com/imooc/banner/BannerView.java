package com.imooc.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;


public class BannerView extends RelativeLayout {


    private BannerViewPager mBannerVp;
    private TextView mBannerDescTv;
    private LinearLayout mDotContainerView;

    private final Context mContext;
    private BannerAdapter mAdapter;

    // 选中态指示器
    private Drawable mIndicatorFocusDrawable;
    // 默认状态指示器
    private Drawable mIndicatorNormalDrawable;

    // 当前位置
    private int mCurrentPosition = 0;
    // 点的显示位置 默认右边
    private int mDotGravity = 1;
    // 点的大小
    private int mDotSize = 8;
    // 点的间距
    private int mDotDistance = 8;
    //底部容器
    private View mBannerBv;
    // 底部容器颜色 默认透明
    private int mBottomColor = Color.TRANSPARENT;

    // 宽高比例
    private float mWidthProportion,mHeightProportion;

    public BannerView(Context context) {
        this(context,null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        inflate(context, R.layout.ui_banner_layout,this);

        initAttribute(attrs);

        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mWidthProportion==0|| mHeightProportion==0){
            return;
        }
        // 动态指定宽和高
        int with =  MeasureSpec.getSize(widthMeasureSpec);

        // 计算高度
        int height = (int) (with*mHeightProportion/mWidthProportion);
        // 指定宽和高
        setMeasuredDimension(with,height);
    }

    /**
     * 初始化自定义属性
     * @param attrs
     */
    private void initAttribute(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs,R.styleable.BannerView);

        // 获取自定义属性
        mDotGravity = array.getInt(R.styleable.BannerView_dotGravity,mDotGravity);

        mIndicatorFocusDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorFocus);
        if (mIndicatorFocusDrawable==null){
            mIndicatorFocusDrawable = new ColorDrawable(Color.RED);
        }

        mIndicatorNormalDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorNormal);
        if (mIndicatorNormalDrawable==null){
            mIndicatorNormalDrawable = new ColorDrawable(Color.WHITE);
        }

        mDotSize = (int) array.getDimension(R.styleable.BannerView_dotSize,dp2px(mDotSize));
        mDotDistance = (int) array.getDimension(R.styleable.BannerView_dotDistance,dp2px(mDotDistance));

        // 获取底部的颜色
        mBottomColor = array.getColor(R.styleable.BannerView_bottomColor,mBottomColor);

        mWidthProportion = array.getFloat(R.styleable.BannerView_withProportion,mWidthProportion);
        mHeightProportion = array.getFloat(R.styleable.BannerView_heightProportion,mHeightProportion);

        array.recycle();
    }

    private void initView(){
        mBannerVp = findViewById(R.id.banner_vp);
        mBannerDescTv = findViewById(R.id.banner_desc_tv);
        mDotContainerView= findViewById(R.id.dot_container);

        // 设置轮播图底部背景色
        mBannerBv = findViewById(R.id.banner_bottom_view);
        mBannerBv.setBackgroundColor(mBottomColor);
    }

    public void setAdapter(BannerAdapter bannerAdapter) {

        this.mAdapter = bannerAdapter;
        
        mBannerVp.setAdapter(bannerAdapter);

        initDotIndicator();
        
        mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                // 监听当前选中的位置
                pageSelect(position);
            }
        });


        // 初始化默认广告描述
        String bannerDesc = mAdapter.getBannerDesc(0);
        mBannerDescTv.setText(bannerDesc);

        if (mWidthProportion==0|| mHeightProportion==0){
            return;
        }
        // 动态指定宽和高
        int with =  getMeasuredWidth();

        // 计算高度
        int height = (int) (with*mHeightProportion/mWidthProportion);

        // 指定高度
        getLayoutParams().height = height;
    }

    /**
     * 监听页面切换事件
     * @param position
     */
    private void pageSelect(int position) {
        // 之前的点设置为默认
        DotIndicatorView oldInficator = (DotIndicatorView) mDotContainerView.getChildAt(mCurrentPosition);
        oldInficator.setDrawable(mIndicatorNormalDrawable);

        // 先切换的点设置为选中
        mCurrentPosition = position%mAdapter.getCount();
        DotIndicatorView newInficator = (DotIndicatorView) mDotContainerView.getChildAt(mCurrentPosition);
        newInficator.setDrawable(mIndicatorFocusDrawable);

        // 设置广告描述
        String bannerDesc = mAdapter.getBannerDesc(mCurrentPosition);
        mBannerDescTv.setText(bannerDesc);
    }

    /**
     * 初始化轮播图指示器
     */
    private void initDotIndicator() {
        int count = mAdapter.getCount();

        // 指示器默认靠右显示
        mDotContainerView.setGravity(getDotGravity());

        for (int i = 0; i < count; i++) {
            // 添加指示器
            DotIndicatorView indicatorView = new DotIndicatorView(mContext);
            // 设置大小
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mDotSize,mDotSize);
            lp.leftMargin = mDotDistance;
            indicatorView.setLayoutParams(lp);

            // 初始化指示器
            if (i==0){
                indicatorView.setDrawable(mIndicatorFocusDrawable);
            }else{
                indicatorView.setDrawable(mIndicatorNormalDrawable);
            }

            mDotContainerView.addView(indicatorView);
        }
    }

    /**
     * dp -> px
     * @param dip
     * @return
     */
    private int dp2px(int dip){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip, getResources().getDisplayMetrics());
    }
    public void startRoll() {
        mBannerVp.startRoll();
    }
    /**
     * 获取点的位置
     * @return
     */
    public int getDotGravity(){
        switch (mDotGravity){
            case 0:
                return Gravity.CENTER;
            case -1:
                return Gravity.LEFT;
            case 1:
                return Gravity.RIGHT;
        }
        return Gravity.LEFT;
    }
}

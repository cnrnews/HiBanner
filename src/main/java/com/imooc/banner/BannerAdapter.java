package com.imooc.banner;

import android.view.View;

public abstract class BannerAdapter {
    /**
     * 根据位置获取ViewPager里面的子view
     * @param position
     * @return
     */
    public abstract View getView(int position,View view);

    /**
     * 获取轮播图数量
     * @return
     */
    public abstract int getCount();

    /**
     * 根据位置获取描述
     * @param mCurrentPosition
     * @return
     */
    public String getBannerDesc(int mCurrentPosition){
        return  "";
    }
}

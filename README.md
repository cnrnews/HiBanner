# HiBanner
自定义轮播图组件

## 基本使用

1.布局引入
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:id="@+id/view_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <com.imooc.banner.BannerView
        android:id="@+id/banner_view"
        android:layout_width="match_parent"
        app:dotSize="8dp"
        app:dotDistance="10dp"
        app:dotGravity="center"
        app:withProportion="8"
        app:heightProportion="3"
        app:bottomColor="@color/design_default_color_secondary"
        app:dotIndicatorFocus="@color/design_default_color_primary"
        app:dotIndicatorNormal="@color/cardview_shadow_start_color"
        android:layout_height="0dp"/>
</LinearLayout>
```
2. 设置数据
```
 mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View view) {
                if (view==null){
                    view =  new ImageView(BannerDemoActivity.this);
                }
                String url = result.data.get(position).imagePath;
                Glide.with(BannerDemoActivity.this)
                        .load(url)
                        .into((ImageView)view);
                return view;
            }
            @Override
            public int getCount() {
                return result.data.size();
            }

            @Override
            public String getBannerDesc(int position) {
                return result.data.get(position).desc;
            }
        });
        // 开启滚动轮播图
        mBannerView.startRoll();
```

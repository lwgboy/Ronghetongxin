package com.zhbd.beidoucommunication.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.utils.DensityUtils;

import java.util.List;

/**
 * Created by Totem on 2016/08/12
 *
 * @author zyr
 */
public class ViewPagerIndicator extends LinearLayout {

    private Context context;

    /**
     * 初始化画笔
     */
    private Paint mPaint;

    /**
     * 默认的Tab数量
     */
    private static final int COUNT_DEFAULT_TAB = 2;

    /**
     * tab数量
     */
    private int mTabVisibleCount;

    /**
     * tab上的内容
     */
    private List<String> mTabTitles;

    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_NORMAL = Color.rgb(160, 165, 170);

    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_SELECT = Color.rgb(0, 255, 255);

    /**
     * 指示符的尺寸
     */
    private int mTop;
    private int mLeft;
    private int mWidth;
    private int mHeight = 3;


    public ViewPagerIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        /**
         * 存放自定义视图属性的数组容器,需要在attrs文件夹中定义属性
         * 属性支持：
         * reference string color dimension boolean integer float fraction enum flag
         * */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        /**
         * 获取数组容器中属性数量
         * */
        mTabVisibleCount = typedArray.getInt(R.styleable.ViewPagerIndicator_item_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount <= 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        /**
         * 调用recycle()函数
         * */
        typedArray.recycle();
        /**
         * 初始化画笔
         * */
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR_SELECT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTop = getMeasuredHeight();
        int width = getMeasuredWidth();
        int height = mTop + DensityUtils.dip2px(context, mHeight);
        mWidth = width / mTabVisibleCount;
        setMeasuredDimension(width, height);
    }

    /**
     * 指示符滚动
     */
    public void scroll(int position, float offset) {
        mLeft = (int) ((position + offset) * mWidth);
        /**
         * 重新绘制视图
         * */
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        /**
         * 绘制线条,可以绘制多样性的,也可以绘制三角形等等
         * */
        Rect rect = new Rect(mLeft, mTop, mLeft + mWidth, mTop + DensityUtils.dip2px(context, 3));
        canvas.drawRect(rect, mPaint);
        super.dispatchDraw(canvas);
    }

    /**
     * 设置可见的tab的数量
     */
    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;
    }

    /**
     * 设置tab的标题内容,可以传入,也可以选择不传,在布局文件中可以写特定的值
     */
    public void setTabItemTitles(List<String> datas) {
        /**
         * 如果传入的list有值，则移除布局文件中设置的view
         * */
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles) {
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickEvent();
        }
    }

    /**
     * 设置关联的ViewPager
     */
    public void setViewPager(ViewPager mViewPager, int pos) {
        this.mViewPager = mViewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextViewColor();
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }

    /**
     * 高亮文本
     */
    protected void highLightTextView(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_SELECT);
        }
    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_NORMAL);
            }
        }
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成我们的TextView
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = (int) (getScreenWidth() * 0.3f / mTabVisibleCount);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(COLOR_NORMAL);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}

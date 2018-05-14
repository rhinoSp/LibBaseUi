package com.rhino.ui;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rhino.ui.base.BaseSimpleTitleActivity;
import com.rhino.ui.impl.IOnNoMultiClickListener;
import com.rhino.ui.utils.ColorUtils;
import com.rhino.ui.utils.DrawableUtils;
import com.rhino.ui.utils.ToastUtils;
import com.rhino.ui.view.CircleIndicator;
import com.rhino.ui.view.image.FreeTintImageView;


public class MainActivity extends BaseSimpleTitleActivity {

    @Override
    protected void setContent() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected boolean initData() {
        return true;
    }

    @Override
    protected void initView() {
        mActionBarHelper.addTitleLeftKey(R.mipmap.ic_launcher, ColorUtils.BLACK, new IOnNoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                ToastUtils.show(getApplicationContext(), "menu");
            }
        });

        mActionBarHelper.setTitleGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        addRightView();
    }

    private void addRightView() {
        int color = ColorUtils.WHITE;
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams llLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, mActionBarHelper.getTitleHeight());
        ll.setLayoutParams(llLp);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(mActionBarHelper.getTitleKeyTextHorizontalMargin(),
                0, mActionBarHelper.getTitleKeyTextHorizontalMargin(),
                0);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getApplicationContext(), "more");
            }
        });

        TextView tv = new TextView(this);
        tv.setText("more");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActionBarHelper.getTitleKeyTextSize());
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.6f, color), color));
        ll.addView(tv);

        FreeTintImageView iv = new FreeTintImageView(this);
        FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(
                mActionBarHelper.getTitleKeyIconSize(), mActionBarHelper.getTitleKeyIconSize());
        iv.setLayoutParams(ivLp);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(R.mipmap.ic_more);
        iv.setColorStateList(DrawableUtils.buildColorStateList(color, color,
                ColorUtils.alphaColor(0.6f, color), color));
        ll.addView(iv);

        mActionBarHelper.addTitleRightKey(ll);


    }
}

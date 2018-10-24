package com.rhino.ui;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rhino.ui.base.BaseSimpleTitleActivity;
import com.rhino.ui.impl.IOnNoMultiClickListener;
import com.rhino.ui.tab.TestTabFragment;
import com.rhino.ui.utils.ui.ColorUtils;
import com.rhino.ui.utils.ui.DrawableUtils;
import com.rhino.ui.utils.LogUtils;
import com.rhino.ui.utils.ui.ToastUtils;
import com.rhino.ui.view.ArrowView;
import com.rhino.ui.view.CircleShape;
import com.rhino.ui.view.CustomFlowView;
import com.rhino.ui.view.RippleDiffusionView;
import com.rhino.ui.view.WaveView;
import com.rhino.ui.view.progress.CustomSeekBar;
import com.rhino.ui.view.SideLetterBarView;
import com.rhino.ui.view.image.FreeTintImageView;
import com.rhino.ui.view.text.AutoCompleteEditText;


public class MainActivity extends BaseSimpleTitleActivity {

    @Override
    public void setContent() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean initData() {
        return true;
    }

    @Override
    public void initView() {
        mActionBarHelper.addTitleLeftKey(R.mipmap.ic_launcher, ColorUtils.BLACK, new IOnNoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                int i = 1/0;
                ToastUtils.show("menu");
            }
        });

        mActionBarHelper.setTitleGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        addRightView();

        setBaseOnClickListener(findSubViewById(R.id.tab));
        initSideLetterBarView();
        initCustomFlowView();
        initCircleShape();
        ((WaveView)findSubViewById(R.id.WaveView)).startWave();
        ((RippleDiffusionView)findSubViewById(R.id.RippleDiffusionView)).startAnim();
        ((ArrowView)findSubViewById(R.id.ArrowView1)).start();
        ((ArrowView)findSubViewById(R.id.ArrowView1)).setColorFilter(Color.GRAY);
        ((ArrowView)findSubViewById(R.id.ArrowView1)).rotate(ArrowView.Gravity.Right);
    }

    @Override
    public void baseOnClickListener(View v) {
        super.baseOnClickListener(v);
        int id = v.getId();
        if (R.id.tab == id) {
            SingleFragmentActivity.showPage(this, TestTabFragment.class.getName(), SingleFragmentActivity.class);
        }
    }

    public void addRightView() {
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
                ToastUtils.show("more");
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

    private void initSideLetterBarView() {
        SideLetterBarView sideLetterBar = findSubViewById(R.id.SideLetterBarView);
        sideLetterBar.setLetterDesc(new String[]{
                "热门", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M","N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        });
        sideLetterBar.setShowCurrentSelTextView((TextView)findSubViewById(R.id.SideLetterBarView_text));
        sideLetterBar.setOnLetterChangedListener(new SideLetterBarView.IOnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter, boolean isFinished) {
                LogUtils.d("DEBUG letter = " + letter + ", isFinished = " + isFinished);
//                scrollToLetter(letter);
            }
        });
    }

    private void initCustomFlowView() {
        CustomFlowView cfv1 = findSubViewById(R.id.CustomFlowView1);
        cfv1.setFlowImage(R.mipmap.ic_flow_bg_horizontal);
        cfv1.startFlow();

        CustomFlowView cfv2 = findSubViewById(R.id.CustomFlowView2);
        cfv2.setFlowImage(R.mipmap.ic_flow_bg_horizontal);
        cfv2.setAnimStatus(CustomFlowView.ANIM_RIGHT_TO_LEFT);
        cfv2.setCorner(30, 30, 30, 30);
        cfv2.startFlow();
    }

    private void initCircleShape() {
        CircleShape cs1 = findSubViewById(R.id.CircleShape1);
        cs1.addShape(1.0f, Color.RED);

        CircleShape cs2 = findSubViewById(R.id.CircleShape2);
        cs2.addShape(0.95f, Color.RED);
        cs2.addShape(0.8f, Color.GREEN);
        cs2.addShape(0.7f, Color.GRAY);
        cs2.addShape(0.6f, Color.CYAN);
    }

    public void onViewClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_change_progress:
                ((CustomSeekBar)findSubViewById(R.id.CustomSeekBar2)).setProgress(80, true, false);
                break;
            case R.id.bt_save:
                ((AutoCompleteEditText)findSubViewById(R.id.AutoCompleteEditText)).saveInputCache();
                break;
            default:
                break;
        }
    }

}

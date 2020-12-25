package com.rhino.ui.demo;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rhino.log.LogUtils;
import com.rhino.ui.base.BaseSimpleTitleActivity;
import com.rhino.ui.impl.IOnNoMultiClickListener;
import com.rhino.ui.demo.tab.TestTabFragment;
import com.rhino.ui.utils.DeviceUtils;
import com.rhino.ui.utils.OSUtils;
import com.rhino.ui.utils.SystemUtils;
import com.rhino.ui.utils.ui.ColorUtils;
import com.rhino.ui.utils.ui.DrawableUtils;
import com.rhino.ui.utils.ui.ToastUtils;
import com.rhino.ui.view.ArrowView;
import com.rhino.ui.view.CircleShape;
import com.rhino.ui.view.RippleDiffusionView;
import com.rhino.ui.view.WaveView;
import com.rhino.ui.view.SideLetterBarView;
import com.rhino.ui.view.image.FreeTintImageView;
import com.rhino.ui.view.progress.CustomSeekBar;
import com.rhino.ui.view.text.AutoCompleteEditText;
import com.rhino.ui.view.text.watcher.ByteLimitWatcher;
import com.rhino.ui.demo.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends BaseSimpleTitleActivity<ActivityMainBinding> {

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
//        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
//        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtils.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtils.setStatusBarColor(this, 0x55000000);
//        }

        try {
            String cmd = "adb shell input keyevent 120";
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        mActionBarHelper.setTitle(OSUtils.getVersion());
        mActionBarHelper.setTitleBackKeyColor(0xFF000000);
        mActionBarHelper.addTitleLeftKey(R.mipmap.ic_launcher, ColorUtils.BLACK, new IOnNoMultiClickListener() {
            @Override
            public void onNoMultiClick(View v) {
                int i = 1 / 0;
                ToastUtils.show("menu");
            }
        });

        mActionBarHelper.setTitleGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        addRightView();

        setBaseOnClickListener(findSubViewById(R.id.tab));
        initSideLetterBarView();
        initCircleShape();
        ((WaveView) findSubViewById(R.id.WaveView1)).setWaveRadian(30);
        ((WaveView) findSubViewById(R.id.WaveView2)).setWaveRadian(10);
        ((WaveView) findSubViewById(R.id.WaveView1)).setAngle(100);
        ((WaveView) findSubViewById(R.id.WaveView1)).setWaveSpace(1.7f);
        ((WaveView) findSubViewById(R.id.WaveView2)).setWaveSpace(2.8f);
        ((WaveView) findSubViewById(R.id.WaveView1)).startWave();
        ((WaveView) findSubViewById(R.id.WaveView2)).startWave();
        ((RippleDiffusionView) findSubViewById(R.id.RippleDiffusionView)).startAnim();
        ((ArrowView) findSubViewById(R.id.ArrowView1)).start();
        ((ArrowView) findSubViewById(R.id.ArrowView1)).setColorFilter(Color.GRAY);
        ((ArrowView) findSubViewById(R.id.ArrowView1)).rotate(ArrowView.Gravity.Right);

        ((EditText) findSubViewById(R.id.EditText)).addTextChangedListener(new ByteLimitWatcher((EditText) findSubViewById(R.id.EditText), 100) {
            @Override
            public void afterTextChanged(int byteCount, int maxByteCount) {
                super.afterTextChanged(byteCount, maxByteCount);
                LogUtils.d("byteCount = " + byteCount + ", maxByteCount = " + maxByteCount);
                ((TextView) findSubViewById(R.id.tv_limit_count)).setText("您还可以输入" + (maxByteCount - byteCount) / 3 + "个字");
            }
        });
        ((EditText) findSubViewById(R.id.EditText)).setText("61313515153135135135165165156846854512132131321321321321231221513516516513");

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
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        });
        sideLetterBar.setShowCurrentSelTextView((TextView) findSubViewById(R.id.SideLetterBarView_text));
        sideLetterBar.setOnLetterChangedListener(new SideLetterBarView.IOnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter, boolean isFinished) {
                LogUtils.d("DEBUG letter = " + letter + ", isFinished = " + isFinished);
//                scrollToLetter(letter);
            }
        });
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
                ((CustomSeekBar) findSubViewById(R.id.CustomSeekBar2)).setProgress(80, true, false);
                break;
            case R.id.iv_drop:
                ((AutoCompleteEditText) findSubViewById(R.id.AutoCompleteEditText)).setText("");
                ((AutoCompleteEditText) findSubViewById(R.id.AutoCompleteEditText)).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((AutoCompleteEditText) findSubViewById(R.id.AutoCompleteEditText)).showDropDown();
                    }
                }, 100);
                break;
            case R.id.bt_save:
                ((AutoCompleteEditText) findSubViewById(R.id.AutoCompleteEditText)).saveInputCache();
                break;
            default:
                break;
        }
    }

}

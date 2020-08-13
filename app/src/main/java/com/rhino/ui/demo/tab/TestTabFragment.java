package com.rhino.ui.demo.tab;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;

import com.rhino.log.LogUtils;
import com.rhino.ui.demo.R;
import com.rhino.ui.base.BaseTabFragment;
import com.rhino.ui.msg.LocalMessage;
import com.rhino.ui.msg.OverallLevelLocalMessage;
import com.rhino.ui.view.tab.CustomTabItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoLin
 * @since Create on 2018/5/16.
 **/
public class TestTabFragment extends BaseTabFragment {


    @NonNull
    @Override
    public List<TabItemData> getTabItemData() {
        List<TabItemData> list = new ArrayList<>();
        list.add(new TabItemData(TestFragment1.class.getName(),
                new int[] {R.mipmap.ic_launcher, R.mipmap.ic_clear}, "tab1"));
        list.add(new TabItemData(TestFragment2.class.getName(),
                new int[] {R.mipmap.ic_launcher, R.mipmap.ic_clear}, "tab2"));
        list.add(new TabItemData(TestFragment3.class.getName(),
                new int[] {R.mipmap.ic_launcher, R.mipmap.ic_clear}, "tab3"));
        return list;
    }

    @Override
    public void onTabChanged(CustomTabItemLayout tab) {
        LogUtils.d("tab " + tab.getText());

        OverallLevelLocalMessage msg = new OverallLevelLocalMessage();
        msg.obj = "This message form " + CLASS_NAME;
        sendLocalMessage(msg);
    }


    @Override
    public boolean initData() {
        return true;
    }

    @Override
    public void initView() {
        super.initView();

        setPagerScrollable(false);
    }

    @Override
    public ColorStateList getTabStateColorList() {
        return getResources().getColorStateList(R.color.color_nor_gray_pre_theme_light_sel_theme);
    }

    @Override
    public boolean handleLocalMessage(@NonNull LocalMessage data) {

        LogUtils.d(data.toString());

        return false;
    }

    @Override
    public boolean onBackPressed() {
        LogUtils.d("onBackPressed");
        return super.onBackPressed();
    }
}

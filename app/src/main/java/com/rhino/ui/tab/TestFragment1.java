package com.rhino.ui.tab;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;

import com.rhino.ui.R;
import com.rhino.ui.base.BaseTabFragment;
import com.rhino.ui.msg.Message;
import com.rhino.ui.msg.OverallLevelMessage;
import com.rhino.ui.utils.LogUtils;
import com.rhino.ui.view.tab.CustomTabItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuoLin
 * @since Create on 2018/5/16.
 **/
public class TestFragment1 extends BaseTabFragment {

    @NonNull
    @Override
    protected List<TabItemData> getTabItemData() {
        List<TabItemData> list = new ArrayList<>();
        list.add(new TabItemData(TestFragment4.class.getName(),
                R.mipmap.ic_launcher, "tab4"));
        list.add(new TabItemData(TestFragment5.class.getName(),
                R.mipmap.ic_launcher, "tab5"));
        return list;
    }

    @Override
    public void onTabChanged(CustomTabItemLayout tab) {
        LogUtils.d("tab " + tab.getText());

        OverallLevelMessage msg = new OverallLevelMessage();
        msg.obj = "This message form " + CLASS_NAME;
        sendMessage(msg);

        tab.setNewFlagVisible(false);
    }


    @Override
    protected boolean initData() {
        return true;
    }

    @Override
    protected void initView() {
        super.initView();

        setPagerScrollable(true);

        setNewFlag(1, 45);
    }

    @Override
    protected ColorStateList getTabStateColorList() {
        return getResources().getColorStateList(R.color.color_nor_gray_pre_theme_light_sel_theme);
    }

    @Override
    public boolean handleMessage(@NonNull Message data) {

        LogUtils.d(data.toString());

        return false;
    }

}

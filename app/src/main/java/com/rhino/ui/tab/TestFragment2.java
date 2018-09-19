package com.rhino.ui.tab;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.rhino.ui.base.BaseSimpleTitleFragment;
import com.rhino.ui.msg.Message;
import com.rhino.ui.utils.LogUtils;

/**
 * @author LuoLin
 * @since Create on 2018/5/16.
 **/
public class TestFragment2 extends BaseSimpleTitleFragment {

    @Override
    public void setContent() {
        setContentView(new TextView(getActivity()));
    }

    @Override
    public boolean initData() {
        return true;
    }

    @Override
    public void initView() {
//        addTitleRightKey("tst", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                OverallLevelMessage msg = new OverallLevelMessage();
//                sendMessage(msg);
//            }
//        });
    }

    @Override
    public boolean handleMessage(@NonNull Message data) {

        LogUtils.d(data.toString());

        return false;
    }

}

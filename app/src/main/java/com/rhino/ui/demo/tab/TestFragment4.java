package com.rhino.ui.demo.tab;

import androidx.annotation.NonNull;
import android.widget.TextView;

import com.rhino.log.LogUtils;
import com.rhino.ui.base.BaseSimpleTitleFragment;
import com.rhino.ui.msg.LocalMessage;

/**
 * @author LuoLin
 * @since Create on 2018/5/16.
 **/
public class TestFragment4 extends BaseSimpleTitleFragment {

    @Override
    public void setContent() {
        setContentView(new TextView(getActivity()));
    }

    @Override
    public boolean init() {
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
    public boolean handleLocalMessage(@NonNull LocalMessage data) {

        LogUtils.d(data.toString());

        return false;
    }
}

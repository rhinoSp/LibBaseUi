package com.rhino.ui.utils.crash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author LuoLin
 * @since Create on 2018/10/8.
 */
public final class CrashTipsActivity extends Activity implements View.OnClickListener {

    private DefaultCrashHandler mICrashHandler;
    private String mDebugText;
    private Class<?> mRestartActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mICrashHandler = (DefaultCrashHandler)getIntent().getSerializableExtra(CrashService.KEY_CRASH_HANDLE);
        mDebugText = getIntent().getStringExtra(CrashService.KEY_DEBUG_TEXT);
        setContentView(com.rhino.ui.R.layout.activity_crash_tips);
        findViewById(com.rhino.ui.R.id.error_activity_restart_button).setOnClickListener(this);
        findViewById(com.rhino.ui.R.id.error_activity_more_info_button).setOnClickListener(this);
        mRestartActivity = mICrashHandler.getRestartActivity();
        if (mRestartActivity == null) {
            ((Button)findViewById(com.rhino.ui.R.id.error_activity_restart_button)).setText("关闭程序");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == com.rhino.ui.R.id.error_activity_restart_button) {
            if (mRestartActivity != null) {
                Intent intent = new Intent(this, mRestartActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            finish();
            CrashHandlerUtils.killCurrentProcess();
        } else if (id == com.rhino.ui.R.id.error_activity_more_info_button) {
            AlertDialog dialog = new AlertDialog.Builder(CrashTipsActivity.this)
                    .setTitle("错误详情")
                    .setMessage(mDebugText)
                    .setPositiveButton("关闭", null)
                    .setNeutralButton("复制到剪贴板",
                            (dialog1, which) -> {
                                copyErrorToClipboard();
                                Toast.makeText(CrashTipsActivity.this, "复制到剪贴板", Toast.LENGTH_SHORT).show();
                            })
                    .show();
            TextView textView = dialog.findViewById(android.R.id.message);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 38);
        }
    }

    private void copyErrorToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("错误信息", mDebugText);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

}

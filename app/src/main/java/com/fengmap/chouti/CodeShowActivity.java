package com.fengmap.chouti;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fengmap.chouti.ui.ChoseFilePopWindow;
import com.fengmap.chouti.util.FileUtil;
import com.sunfusheng.codeviewer.CodeHtmlGenerator;
import com.sunfusheng.codeviewer.CodeView;

import java.io.File;

public class CodeShowActivity extends AppCompatActivity {

    File file = new File(Environment.getExternalStorageDirectory() + "/test");
    private CodeView codeView;
    private Toolbar toolbar;
    private DrawerLayout drawer_layout;

    private String replacePath;
    private ChoseFilePopWindow popWindow;
    private String curFilePath;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            popWindow.showAsDropDown(drawer_layout, Gravity.BOTTOM, 0, 0);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.alpha = 0.5f;
            getWindow().setAttributes(params);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        String filePath = intent.getStringExtra("fileName");

        if (path.equals("/storage/emulated/0/")) {
            replacePath = "/storage/emulated/0/";
        } else {
            String[] split = path.split("/");
            String s = split[split.length - 1];
            replacePath = path.replace(s, "");
        }

        file = new File(path);

        initView();
        if (filePath != null) {
            curFilePath = path;
            initCode(true);
        } else {
            handler.sendMessageDelayed(handler.obtainMessage(), 600);
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer_layout = findViewById(R.id.drawer_layout);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.showAsDropDown(drawer_layout, Gravity.BOTTOM, 0, 0);
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 0.5f;
                getWindow().setAttributes(params);
            }
        });

        codeView = findViewById(R.id.vCodeView);

        popWindow = new ChoseFilePopWindow(this, file, replacePath, getWindowManager().getDefaultDisplay().getHeight());

        popWindow.setChoseFileListener(new ChoseFilePopWindow.OnChoseFileListener() {
            @Override
            public void choseFile(String name, String curPath) {
                curFilePath = curPath;
                toolbar.setTitle(name);
                initCode(true);
                popWindow.dismiss();
            }
        });
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
    }

    private void initCode(boolean isNight) {
        String contentByFile = FileUtil.getContentByFile(curFilePath);
        String generate = CodeHtmlGenerator.INSTANCE.generate(curFilePath, contentByFile, isNight, true);
        codeView.loadCodeHtml(generate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_dark) {
            if (item.isChecked()) {
                item.setChecked(false);
                initCode(false);
            } else {
                initCode(true);
                item.setChecked(true);
            }
        } else if (item.getItemId() == R.id.action_back) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popWindow.isShowing()) {
            popWindow.dismiss();
        }
        codeView.destroy();
    }
}

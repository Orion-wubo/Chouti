package com.fengmap.chouti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fengmap.chouti.adapter.RecentAdapter;
import com.fengmap.chouti.entity.FileEntity;
import com.fengmap.chouti.listener.FMCallBackListener;
import com.fengmap.chouti.util.FMHttpUtils;
import com.fengmap.chouti.util.FileUtil;

import org.litepal.LitePal;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class DownOrChoseCodeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Object> list = new ArrayList<>();
    private RecentAdapter adapter;
    private EditText et_url;
    private ProgressBar pb_progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_chose);

        initView();

    }

    private void requestPermission() {

        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == 0) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                initData();
            } else {
                // Permission request was denied.

            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void initData() {
        List<FileEntity> all = LitePal.findAll(FileEntity.class);
        list.clear();
        list.addAll(all);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
            initData();

        } else {
            // Permission is missing and must be requested.
            requestPermission();
        }
    }

    private void initView() {
        pb_progress = findViewById(R.id.pb_progress);
        et_url = findViewById(R.id.et_url);
        recyclerView = findViewById(R.id.rv_recent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //间距设置，完全copy了别人的代码。。
        recyclerView.setItemAnimator(new SlideInUpAnimator());//这是一个开源的动画效果，非常棒的哦
        adapter = new RecentAdapter(this, list, R.layout.layout_recent_recycler_item, new int[]{R.id.tv_name,
                R.id.ll_item, R.id.tv_path, R.id.iv_delete});
        //这里的点击事件很重要
        adapter.setOnItemClickLitener(new RecentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, FileEntity fileEntity) {
                openCodeActivity(fileEntity);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void openCodeActivity(FileEntity fileEntity) {
        Intent intent = new Intent(this, CodeShowActivity.class);
        intent.putExtra("path", fileEntity.getPath());
        if (fileEntity.isFile()) {
            intent.putExtra("fileName", fileEntity.getName());
        }

        startActivity(intent);
    }

    public void download(View view) {
        pb_progress.setVisibility(View.VISIBLE);
        String text = et_url.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "empty url", Toast.LENGTH_SHORT).show();
        } else if (!text.endsWith(".git")) {
            Toast.makeText(this, "error url for github", Toast.LENGTH_SHORT).show();
        } else {
            String[] split = text.split("/");
            final String name = split[split.length - 1].replace(".git", "");

            String url = text.replace(".git", "/archive/master.zip");

            FMHttpUtils.getFMHttpUtils().doGet(url, new FMCallBackListener() {
                @Override
                public void onFinish(HttpURLConnection urlConnection, byte[] response) {
                    File projectDir = DownOrChoseCodeActivity.this.getExternalFilesDir("Project");
                    if (!projectDir.exists()) {
                        projectDir.mkdirs();
                    }
                    File tempZipfile = new File(projectDir, name + ".temp_zip");
                    if (tempZipfile.exists()) {
                        tempZipfile.delete();
                    }
                    try {
                        FileUtil.writeFile(tempZipfile, response, false);

                        // covert name
                        File completedName = new File(projectDir, name + ".zip");
                        tempZipfile.renameTo(completedName);
                        tempZipfile.delete();

                        // decompression
                        try {
                            FileUtil.decompressionZipFile(completedName, projectDir + "/" + name);
                        } catch (Exception e) {
                            Log.e("ERROR", e.toString());
                            Log.e("Error", "文件解压失败");
                            // error map file
                            Message message = handler.obtainMessage(1);
                            message.obj = "文件解压失败";
                            handler.sendMessage(message);
                        }

                        completedName.delete();
                        // success
                        Message message = handler.obtainMessage(0);
                        message.obj = projectDir + "/" + name;
                        handler.sendMessage(message);

                    } catch (Exception e) {
                        Log.e("ERROR", e.toString());
                        Log.e("Error", "文件写入失败");
                        // error map file
                        Message message = handler.obtainMessage(1);

                        handler.sendMessage(message);

                    }

                }

                @Override
                public void onError(int error, byte[] bytes) {

                }
            });
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String info = (String) msg.obj;
            switch (msg.what) {
                case 0:
                    pb_progress.setVisibility(View.GONE);
                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setPath(info);
                    String[] split = info.split("/");
                    String name = split[split.length - 1];
                    fileEntity.setName(name);
                    fileEntity.setFile(false);
                    fileEntity.save();
                    openCodeActivity(fileEntity);
                    break;
                case 1:
                    Toast.makeText(DownOrChoseCodeActivity.this, info, Toast.LENGTH_SHORT).show();
                    pb_progress.setVisibility(View.GONE);
                    break;
            }

        }
    };

    public void chose(View view) {
        startActivityForResult(new Intent(this, FileChoseActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String path = data.getStringExtra("path");
            String fileName = data.getStringExtra("fileName");
            FileEntity fileEntity = new FileEntity();
            fileEntity.setPath(path);

            if (fileName != null) {
                fileEntity.setFile(true);
                fileEntity.setName(fileName);
            } else {
                String[] split = path.split("/");
                String name = split[split.length - 1];
                fileEntity.setName(name);
                fileEntity.setFile(false);
            }
            fileEntity.save();

            openCodeActivity(fileEntity);
        }
    }
}

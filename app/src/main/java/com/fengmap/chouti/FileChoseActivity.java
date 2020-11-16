package com.fengmap.chouti;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fengmap.chouti.adapter.FileChoseAdapter;
import com.fengmap.chouti.adapter.TreeAdapter;
import com.fengmap.chouti.entity.FileEntity;
import com.fengmap.chouti.util.FileUtil;
import com.fengmap.chouti.util.ShowCodeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class FileChoseActivity extends AppCompatActivity {
    private TreeAdapter adapter;
    private List<Object> list = new ArrayList<>();
    private RecyclerView recyclerView, rv_path;
    private FileChoseAdapter pathAdapter;
    ArrayList pathList = new ArrayList<>();
    File file = new File(Environment.getExternalStorageDirectory().getPath());
    private String curPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chose);
        initView();
        initData(file);
    }
    private void initData(File file) {
        curPath = file.getPath();
        List<FileEntity> fileData = FileUtil.getFileAndDirData(file);
        list.clear();
        list.addAll(ShowCodeUtil.comparableList(fileData));
        adapter.notifyDataSetChanged();

        if (curPath.equals("/storage/emulated/0")) {
            curPath = "/storage/emulated/0/";
        }

        String[] split = curPath.replace("/storage/emulated/0/", "sd/").split("/");

        List<String> strings = Arrays.asList(split);

        pathList.clear();
        pathList.addAll(strings);
        pathAdapter.notifyDataSetChanged();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //间距设置，完全copy了别人的代码。。
        recyclerView.setItemAnimator(new SlideInUpAnimator());//这是一个开源的动画效果，非常棒的哦
        adapter = new TreeAdapter(this, list, R.layout.layout_treerecycler_item, new int[]{R.id.iv_type, R.id.tv_name
                , R.id.ll_item});
        //这里的点击事件很重要
        adapter.setOnItemClickLitener(new TreeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String path) {
                FileEntity fileEntity = (FileEntity) list.get(position);
                if (fileEntity.isFile()) {
                } else {
                    curPath = fileEntity.getPath();
                    initData(new File(curPath));
                }
            }
        });
        recyclerView.setAdapter(adapter);

        rv_path = findViewById(R.id.rv_path);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_path.setLayoutManager(linearLayoutManager);
//        rv_path.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        //间距设置，完全copy了别人的代码。。
        rv_path.setItemAnimator(new SlideInUpAnimator());//这是一个开源的动画效果，非常棒的哦
        pathAdapter = new FileChoseAdapter(this, pathList, R.layout.layout_pathrecycler_item, new int[]{R.id.tv_name,
                R.id.ll_item});
        //这里的点击事件很重要
        pathAdapter.setOnItemClickLitener(new FileChoseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String path) {
                curPath = "/storage/emulated/0/";
                for (int i = 1; i <= position; i++) {
                    curPath = curPath + pathList.get(i) + "/";
                }
                initData(new File(curPath));
            }
        });
        rv_path.setAdapter(pathAdapter);
    }

    public void cancel(View view) {
        finish();
    }

    public void confirm(View view) {
        Intent intent = new Intent();
        intent.putExtra("path", curPath);
        setResult(RESULT_OK, intent);
        finish();
    }
}

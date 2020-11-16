package com.fengmap.chouti.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fengmap.chouti.DividerItemDecoration;
import com.fengmap.chouti.R;
import com.fengmap.chouti.adapter.PathAdapter;
import com.fengmap.chouti.adapter.TreeAdapter;
import com.fengmap.chouti.entity.FileEntity;
import com.fengmap.chouti.util.FileUtil;
import com.fengmap.chouti.util.ShowCodeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ChoseFilePopWindow extends PopupWindow {
    private final Context mContext;
    private final View view;
    private final File mFile;
    private final String replacePath;
    private TreeAdapter adapter;

    ArrayList list = new ArrayList<>();
    ArrayList pathList = new ArrayList<>();
    private String curFilePath;
    private String curPath;
    private PathAdapter pathAdapter;
    private OnChoseFileListener mChoseFileListener;

    public ChoseFilePopWindow(Context context,File file,String replace,int height) {
        this.mContext = context;
        this.mFile = file;
        this.replacePath = replace;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_chose_file, null);

        initView();
        initData(file);

        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(height * 3 / 4);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置这两个属性
        this.setOutsideTouchable(true);
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setAnimationStyle(R.style.chose_file_anim);

    }

    public interface OnChoseFileListener{
        void choseFile(String name,String path);
    }

    public void setChoseFileListener(OnChoseFileListener listener) {
        this.mChoseFileListener = listener;
    }

    private void initView() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        //间距设置，完全copy了别人的代码。。
        recyclerView.setItemAnimator(new SlideInUpAnimator());//这是一个开源的动画效果，非常棒的哦
        adapter = new TreeAdapter(mContext, list, R.layout.layout_treerecycler_item, new int[]{R.id.iv_type, R.id.tv_name
                , R.id.ll_item});
        //这里的点击事件很重要
        adapter.setOnItemClickLitener(new TreeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String path) {
                FileEntity fileEntity = (FileEntity) list.get(position);
                if (fileEntity.isFile()) {
                    curFilePath = fileEntity.getPath();
                    mChoseFileListener.choseFile(fileEntity.getName(),curFilePath);
                } else {
                    curPath = fileEntity.getPath();
                    initData(new File(curPath));
                }
            }
        });
        recyclerView.setAdapter(adapter);


        RecyclerView rv_path = view.findViewById(R.id.rv_path);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_path.setLayoutManager(linearLayoutManager);
//        rv_path.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL_LIST));
        //间距设置，完全copy了别人的代码。。
        rv_path.setItemAnimator(new SlideInUpAnimator());//这是一个开源的动画效果，非常棒的哦
        pathAdapter = new PathAdapter(mContext, pathList, R.layout.layout_pathrecycler_item, new int[]{R.id.tv_name,
                R.id.ll_item});
        //这里的点击事件很重要
        pathAdapter.setOnItemClickLitener(new PathAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String path) {
                curPath = replacePath;
                for (int i = 0; i <= position; i++) {
                    curPath = curPath + pathList.get(i) + "/";
                }
                initData(new File(curPath));
            }
        });
        rv_path.setAdapter(pathAdapter);

        view.findViewById(R.id.ll_back_dir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathList.size() <= 1) {
                    return;
                }
                curPath = replacePath;
                for (int i = 0; i <= pathList.size()-2; i++) {
                    curPath = curPath + pathList.get(i) + "/";
                }
                initData(new File(curPath));
            }
        });

    }

    private void initData(File file) {
        curPath = file.getPath();
        List<FileEntity> fileData = FileUtil.getFileAndDirData(file);
        list.clear();
        list.addAll(fileData);
        ShowCodeUtil.comparableList(list);
        adapter.notifyDataSetChanged();

        String[] split = curPath.replace(replacePath, "").split("/");
        List<String> strings = Arrays.asList(split);
        pathList.clear();
        pathList.addAll(strings);
        pathAdapter.notifyDataSetChanged();
    }
}

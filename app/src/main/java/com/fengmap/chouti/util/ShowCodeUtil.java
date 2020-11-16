package com.fengmap.chouti.util;

import com.fengmap.chouti.entity.FileEntity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ShowCodeUtil {
    public static List<FileEntity> comparableList(List<FileEntity> list) {
        List<FileEntity> dirList = new ArrayList<>();
        List<FileEntity> fileList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            FileEntity fileEntity = list.get(i);
            if (fileEntity.isFile()) {
                fileList.add(fileEntity);
            } else {
                dirList.add(fileEntity);
            }
        }

        Collections.sort(dirList, new Comparator<FileEntity>() {
            @Override
            public int compare(FileEntity o1, FileEntity o2) {
                return Collator.getInstance(Locale.CHINA).compare(o1.getName(),o2.getName());
            }
        });
        Collections.sort(fileList, new Comparator<FileEntity>() {
            @Override
            public int compare(FileEntity o1, FileEntity o2) {
                return Collator.getInstance(Locale.CHINA).compare(o1.getName(),o2.getName());
            }
        });

        list.clear();
        list.addAll(dirList);
        list.addAll(fileList);
        return list;
    }
}

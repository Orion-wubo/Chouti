package com.fengmap.chouti.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.fengmap.chouti.entity.FileEntity;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {
    static Map<String, List<FileEntity>> filesAndDirMap = new HashMap<>();
    static Map<String, List<FileEntity>> filesMap = new HashMap<>();

    public static List<FileEntity> getFileAndDirData(File file) {
        if (filesMap.get(file.getPath()) != null) {
            return filesMap.get(file.getPath());
        }
        File[] files = file.listFiles();
        List<FileEntity> fileEntities = new ArrayList<>();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                FileEntity fileEntity = new FileEntity();
                fileEntity.setName(f.getName());
                fileEntity.setPath(f.getPath());
                if (f.isDirectory()) {
                    fileEntity.setFile(false);
                } else {
                    fileEntity.setFile(true);
                }
                fileEntities.add(fileEntity);
            }
        }
        filesMap.put(file.getPath(), fileEntities);
        return fileEntities;
    }

    public static List<FileEntity> getFileData(File file) {
        if (filesAndDirMap.get(file.getPath()) != null) {
            return filesAndDirMap.get(file.getPath());
        }
        File[] files = file.listFiles();
        List<FileEntity> fileEntities = new ArrayList<>();

        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                FileEntity fileEntity = new FileEntity();
                fileEntity.setName(f.getName());
                fileEntity.setPath(f.getPath());
                if (f.isDirectory()) {
                    fileEntity.setFile(false);
                    fileEntities.add(fileEntity);
                }
            }
        }
        filesAndDirMap.put(file.getPath(), fileEntities);
        return fileEntities;
    }

    public static String readStringFromAssets(Context context,String fileName) {
        AssetManager assetManager = context.getApplicationContext().getAssets();

        InputStream inputStream = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();
        try {
            inputStream = assetManager.open(fileName);
            isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);

            sb.append(br.readLine());
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append("\n" + line);
            }

            br.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 以字符流读取文件
     *
     * @param path 文件路径
     * @return 字符数组
     */
    public static String getContentByFile(String path) {
        try {
            // 创建字符流对象
            FileReader reader = new FileReader(path);
            // 创建字符串拼接
            StringBuilder builder = new StringBuilder();
            // 读取一个字符
            int read = reader.read();
            // 能读取到字符
            while (read != -1) {
                // 拼接字符串
                builder.append((char) read);
                // 读取下一个字符
                read = reader.read();
            }
            // 关闭字符流
            reader.close();
            return builder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * zip文件解压。
     *
     * @param zipFile   zip文件
     * @param directory 解压的文件所在的目录
     */
    public static void decompressionZipFile(File zipFile, String directory) throws IOException {
        File f = new File(directory);
        forceMkdir(f);

        ZipFile zFile = new ZipFile(zipFile);
        Enumeration<ZipEntry> zList = (Enumeration<ZipEntry>) zFile.entries();
        ZipEntry ze;
        while (zList.hasMoreElements()) {
            ze = zList.nextElement();
            if (ze.isDirectory()) {  //处理目录
                File file = new File(directory + File.separator + ze.getName());
                forceMkdir(file);
                continue;
            }

            InputStream fis = zFile.getInputStream(ze);
            FileOutputStream fos = new FileOutputStream(directory + File.separator + ze.getName());

            copy(fis, fos);
            closeQuietly(fis, fos);
        }
    }

    /**
     * 拷贝流。
     *
     * @param is 输入流
     * @param os 输出流
     * @throws IOException 发生 I/O 错误
     */
    public static void copy(final InputStream is, final OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int byteCount = 0;
        while ((byteCount = is.read(buffer)) != -1) {
            os.write(buffer, 0, byteCount);
        }
    }

    /**
     * 关闭对象。
     *
     * @param closeables 可关闭的对象组。
     */
    public static void closeQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }

    /**
     * 关闭对象。
     *
     * @param closeable 可关闭的对象。
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * 创建文件夹。
     *
     * @param directory 目标文件夹
     * @throws IOException 创建目标文件夹失败
     */
    public static void forceMkdir(final File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                final String message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                if (!directory.isDirectory()) {
                    final String message = "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    /**
     * 写文件。
     *
     * @param dstFile 文件路径
     * @param data    数据
     * @throws IOException
     */
    public static void writeFile(File dstFile, byte[] data, boolean append) throws IOException {
        FileOutputStream fos = new FileOutputStream(dstFile, append);
        fos.write(data);
        fos.flush();
        closeQuietly(fos);
    }
}

package com.olympus.base.utils.support.io.local;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件读取工具类
 * 2019-03-12
 *
 * @author eddie
 */
public class LocalFileUtil {

    private static final int DEFAULT_SIZE = 1024;

    private static final int MAX_SIZE = DEFAULT_SIZE * DEFAULT_SIZE * DEFAULT_SIZE;

    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }

        if (file.length() > MAX_SIZE) {
            throw new IOException("File is too large");
        }

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流  
        FileInputStream fis = new FileInputStream(filePath);
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];
        // 用于保存实际读取的字节数  
        int hasRead = 0;
        while ((hasRead = fis.read(bbuf)) > 0) {
            sb.append(new String(bbuf, 0, hasRead));
        }
        fis.close();
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length())) {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }
                return bos.toByteArray();
            }
        }
    }

    public static List<String> getFiles(String path) {
        List<String> files = new ArrayList<>();
        getFiles(files, path);
        return files;
    }

    private static void getFiles(List<String> fileList, String path) {
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (Objects.isNull(files)) {
                    return;
                }
                for (File fileIndex : files) {
                    //如果这个文件是目录，则进行递归搜索
                    if (fileIndex.isDirectory()) {
                        getFiles(fileList, fileIndex.getPath());
                    } else {
                        //如果文件是普通文件，则将文件句柄放入集合中
                        fileList.add(fileIndex.getName());
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void bufferedWriterMethod(String filepath, String content) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) {
            bufferedWriter.write(content);
        }
    }

    private static void bufferedOutputStreamTest(String filepath, byte[] bytes) throws IOException {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filepath))) {
            bufferedOutputStream.write(bytes);
        }
    }
}

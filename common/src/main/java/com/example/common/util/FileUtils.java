package com.example.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

public class FileUtils {

    /**
     * 判断文件类型
     */
    public static String getFileType(File file) throws Exception {
        String s = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(new FileInputStream(file).readAllBytes()));
        System.out.println(s);
        return s;
    }
}

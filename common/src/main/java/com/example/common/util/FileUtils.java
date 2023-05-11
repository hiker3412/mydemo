package com.example.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;

public class FileUtils {
    public static String getFileType(File file) throws IOException {
        String s = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(new FileInputStream(file).readAllBytes()));
        System.out.println(s);
        return s;
    }

    public static void main(String[] args) throws IOException {

        String fileType = getFileType(new File("C:\\Users\\Administrator\\Desktop\\阿里巴巴Java开发手册.pdf"));
        System.out.println(fileType);
    }
}

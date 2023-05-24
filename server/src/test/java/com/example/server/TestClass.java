package com.example.server;

import com.example.common.enumeration.ColorEnum;
import com.example.common.model.abstracttest.vo.User1;
import com.example.common.model.abstracttest.vo.User2;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class TestClass {


    @Test
    void download() throws Exception {
        String url1 = "http://news.windin.com/bulletin/79268996.pdf?mediatype=03&&pkid=79268996&&id=115467298"; // 文件在网络上的URL
        String url2 = "http://news.windin.com/ns/bulletin.php?id=115467298&type=1&code=C466B3559B39"; // 文件在网络上的URL
        String url3 = "http://news.windin.com/bulletin/79268996.pdf?mediatype=03&&pkid=79268996&&id=115467298&direct=1"; // 文件在网络
        String url4 = "http://news.windin.com/bulletin/197036.txt?mediatype=03&&pkid=197036&&id=1452829"; // txt
        String url5 = "http://news.windin.com/bulletin/6754248.docx?mediatype=03&&pkid=6754248&&id=21399858"; // docx

        downloadNetFile(url1);
        downloadNetFile(url2);
        downloadNetFile(url3);
        downloadNetFile(url4);
    }

    void downloadNetFile(String url) throws Exception{
        String fileName = "C:\\Users\\Administrator\\IdeaProjects\\mydemo\\server\\src\\test\\resources\\file\\file.pdf"; // 保存文件的本地文件名
        URL fileUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
//        InputStream in = conn.getInputStream();
        String contentType = conn.getContentType();
        System.out.println(contentType);
//        OutputStream out = new FileOutputStream(fileName);
//        byte[] buffer = new byte[4096];
//        int bytesRead = -1;
//        while ((bytesRead = in.read(buffer)) != -1) {
//            out.write(buffer, 0, bytesRead);
//        }
//        in.close();
//        out.close();
    }

    @Test
    void testCalendar() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2022, 0, 5); // 设置第一个Calendar对象为2022年1月1日

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2023, 0, 1); // 设置第二个Calendar对象为2023年1月1日

        long timeInterval = calendar2.getTimeInMillis() - calendar1.getTimeInMillis(); // 计算时间差
        double yearInterval = timeInterval / (1000 * 60 * 60 * 24 * 365.0); // 将时间差转换为以年为单位的时间间隔

        if (yearInterval > 1) {
            System.out.println("时间间隔超过一年");
        } else {
            System.out.println("时间间隔未超过一年");
        }
    }

    @Test
    void testEnum() {
        System.out.println(ColorEnum.valueOf("RwED"));
    }

    @Test
    void printList() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("1");
        strings.add("2");
        List<String> collect = strings.stream().distinct().collect(Collectors.toList());
        collect.forEach(System.out::println);
    }

    @Test
    void testAbstractField() {
        User1 user1 = new User1();
        User2 user2 = new User2();

        System.out.println(user1.getMyName());
        System.out.println(user2.getMyName());
    }

    @Test
    void testRegexp() {
        String etfConnection = ".+ETF联接[A_Z]*";
        System.out.println("上证180ETF联接".matches(etfConnection));
        System.out.println("上证180ETF联接A".matches(etfConnection));
        System.out.println("上证180ETF联接(LOF)".matches(etfConnection));
    }

    @Test
    void testLogic() {
        System.out.println(123);
        System.out.println(456);
    }

    @Test
    public void testHashCode() {
        ArrayList<String> strs = new ArrayList<>(Arrays.asList("002001.OF", "003001.SH", "515380.SZ"));
        strs.forEach(str -> System.out.println(str.hashCode()));
        strs.forEach(str -> System.out.println(str.hashCode() % 4));
    }

    @Test
    public Double divide(Double... v) {
        Double result = null;
        for (int i = 0; i < v.length; i++) {
            if (v[i] == null || v[i] == 0) return null;
            if (result == null) {
                result = v[i];
            } else {
                result = result / v[i];
            }
        }
        return result;
    }
}

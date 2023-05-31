package com.example.server;

import com.example.common.enumeration.ColorEnum;
import com.example.common.model.abstracttest.vo.User1;
import com.example.common.model.abstracttest.vo.User2;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestClass {
    @Test
    void netDownload() throws Exception{
        URL url = new URL("http://news.windin.com/bulletin/79268996.pdf?mediatype=03&&pkid=79268996&&id=115467298");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        InputStream inputStream = connection.getInputStream();
        byte[] bytes = new byte[20480];
        System.out.println(inputStream.available());
        int read = inputStream.read(bytes);
        System.out.println(read);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\IdeaProjects\\mydemo\\server\\src\\test\\resources\\test.pdf");
        fileOutputStream.write(bytes,0,read);
    }

    @Test
    void netDownload2() throws Exception {
        URL url = new URL("http://news.windin.com/bulletin/79268996.pdf?mediatype=03&&pkid=79268996&&id=115467298");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // 得到输入流
        InputStream inputStream = conn.getInputStream();
        // 获取自己数组
        byte[] bytes = readInputStream(inputStream);
        System.out.println(bytes.length);
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\IdeaProjects\\mydemo\\server\\src\\test\\resources\\test2.pdf");
        fileOutputStream.write(bytes);
    }

    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[10240];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    @Test
    void testEnum(){
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
    public Double divide(Double ...v) {
        Double result = null;
        for (int i=0; i<v.length;i++){
            if(v[i]== null || v[i] == 0) return null;
            if(result== null) {
                result = v[i];
            } else {
                result = result / v[i];
            }
        }
        return result;
    }
}

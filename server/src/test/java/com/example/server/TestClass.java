package com.example.server;

import com.example.common.model.abstracttest.vo.User1;
import com.example.common.model.abstracttest.vo.User2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestClass {

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

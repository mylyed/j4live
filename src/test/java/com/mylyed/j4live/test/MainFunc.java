package com.mylyed.j4live.test;

/**
 * @author lilei
 * created at 2020/4/26
 */
public class MainFunc {

    static void printType(Object o) {
        System.out.println(o.getClass());
    }

    public static void main(String[] args) {
        printType(1);
        printType(0x01b);
        printType(1.1f);
        printType(1.1);
        printType(1.111111);
    }
}

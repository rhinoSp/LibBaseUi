package com.rhino.ui.utils;

/**
 * @author rhino
 * @since Create on 2019/12/9.
 **/
public class MoneyUtils {

    public static String formatMoney(String money) {
        int index = money.indexOf(".");
        if (index < 0) {
            index = money.length();
        }
        StringBuilder stringBuilder = new StringBuilder(money);
        while (index > 3) {
            index -= 3;
            stringBuilder.insert(index, ",");
        }
        return stringBuilder.toString();
    }


    public static void main(String[] args) {
        String s1 = formatMoney("10000000.00");
        String s2 = formatMoney("100000000");
        String s3 = formatMoney("10000000000");

        System.out.println("s1 = " + s1 + ", s2 = " + s2 + ", s3 = " + s3);
    }

}

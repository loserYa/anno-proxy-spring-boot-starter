package com.loser.util;

/**
 * 大驼峰转小驼峰
 *
 * @author loser
 */
public class CamelCaseConverter {

    public static String toLowerCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 将首字母转换为小写
        char[] charArray = input.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);

        // 返回转换后的字符串
        return new String(charArray);
    }

}

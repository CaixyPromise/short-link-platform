package com.caixy.shortlink.utils;

import cn.hutool.core.lang.hash.MurmurHash;

/**
 * Hash工具类
 *
 * @Author CAIXYPROMISE
 * @since 2024/11/18 1:09
 */
public class HashUtil
{
    private static final char[] CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z'
    };
    private static final int SIZE = CHARS.length;

    private static String decToBase62(long num)
    {
        StringBuilder sb = new StringBuilder();
        while (num > 0)
        {
            int i = (int) (num % SIZE);
            sb.append(CHARS[i]);
            num /= SIZE;
        }
        return sb.reverse().toString();
    }

    public static String doHashToBase62(String str)
    {
        int i = MurmurHash.hash32(str);
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
        return decToBase62(num);
    }
}

package com.xueluoanping.arknights.pro;


import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;


public class TransCoding {


    private static final String TAG = TransCoding.class.getSimpleName();


    public static byte[] base64ToBytes(String base64String) {
        byte[] b = Base64.decode(base64String, Base64.NO_WRAP);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {//调整异常数据
                b[i] += 256;
            }
        }
        return b;
    }

    public static String GZIP_to_String(InputStream inputStream) throws IOException {
        GZIPInputStream gzipis = new GZIPInputStream(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipis, "UTF-8"));
        String line = null;
        StringBuffer sb=new StringBuffer();
        while ((line = bufferedReader.readLine()) != null) {
            //转化为UTF-8的编码格式
            sb.append(line);
        }

        inputStream.close();
        gzipis.close();
        bufferedReader.close();
        return sb.toString();
    }

    public static String URlEncode(String Text) throws UnsupportedEncodingException {
        return URLEncoder.encode(Text, "UTF-8");
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static String unicodeToChinese(final String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 0; i < hex.length; i++) {
            try {
                // 汉字范围 \u4e00-\u9fa5 (中文)
                if (hex[i].length() >= 4) {//取前四个，判断是否是汉字
                    String chinese = hex[i].substring(0, 4);
                    try {
                        int chr = Integer.parseInt(chinese, 16);
                        boolean isChinese = isChinese((char) chr);
                        //转化成功，判断是否在  汉字范围内
                        //if (isChinese){//在汉字范围内
                        // 追加成string
                        string.append((char) chr);
                        //并且追加  后面的字符
                        String behindString = hex[i].substring(4);
                        string.append(behindString);
                        //}else {
                        //string.append(hex[i]);
                        // }
                    } catch (NumberFormatException e1) {
                        string.append(hex[i]);
                    }
                } else {
                    string.append(hex[i]);
                }
            } catch (NumberFormatException e) {
                string.append(hex[i]);
            }
        }
        return string.toString();
    }

    @Deprecated
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

}

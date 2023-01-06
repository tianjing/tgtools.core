package tgtools.util;

import tgtools.exceptions.APPErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * 字符串帮助类
 *
 * @author tianjing
 */
public class StringUtil {
    public static final byte[] UTF16LEBom = new byte[]{(byte) 0xFF, (byte) 0xFE};
    public static final byte[] UTF16BEBom = new byte[]{(byte) 0xFE, (byte) 0xFF,
            (byte) 0xbf};
    public static final byte[] UTF8Bom = new byte[]{(byte) 0xef, (byte) 0xbb,
            (byte) 0xbf};
    public static final String EMPTY_STRING = "";
    public static final String NEW_LINE = System.getProperty("line.separator", "\n");
    public static final String NEW_LINE_WINDOWS = "\r\n";
    /**
     * 驼峰拼写法 转换器
     */
    private static final WordTokenizer CAMEL_CASE_TOKENIZER = new WordTokenizer() {
        @Override
        protected void startSentence(StringBuffer buffer, char ch) {
            buffer.append(Character.toLowerCase(ch));
        }

        @Override
        protected void startWord(StringBuffer buffer, char ch) {
            if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                buffer.append(Character.toUpperCase(ch));
            } else {
                buffer.append(Character.toLowerCase(ch));
            }
        }

        @Override
        protected void inWord(StringBuffer buffer, char ch) {
            buffer.append(Character.toLowerCase(ch));
        }

        @Override
        protected void startDigitSentence(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void startDigitWord(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void inDigitWord(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void inDelimiter(StringBuffer buffer, char ch) {
            if (ch != '_') {
                buffer.append(ch);
            }
        }
    };
    /**
     * 帕斯卡拼写法 转换器
     */
    private static final WordTokenizer PASCAL_CASE_TOKENIZER = new WordTokenizer() {
        @Override
        protected void startSentence(StringBuffer buffer, char ch) {
            buffer.append(Character.toUpperCase(ch));
        }

        @Override
        protected void startWord(StringBuffer buffer, char ch) {
            buffer.append(Character.toUpperCase(ch));
        }

        @Override
        protected void inWord(StringBuffer buffer, char ch) {
            buffer.append(Character.toLowerCase(ch));
        }

        @Override
        protected void startDigitSentence(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void startDigitWord(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void inDigitWord(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void inDelimiter(StringBuffer buffer, char ch) {
            if (ch != '_') {
                buffer.append(ch);
            }
        }
    };
    private static final WordTokenizer UPPER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer() {
        @Override
        protected void startSentence(StringBuffer buffer, char ch) {
            buffer.append(Character.toUpperCase(ch));
        }

        @Override
        protected void startWord(StringBuffer buffer, char ch) {
            if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                buffer.append('_');
            }

            buffer.append(Character.toUpperCase(ch));
        }

        @Override
        protected void inWord(StringBuffer buffer, char ch) {
            buffer.append(Character.toUpperCase(ch));
        }

        @Override
        protected void startDigitSentence(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void startDigitWord(StringBuffer buffer, char ch) {
            if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                buffer.append('_');
            }

            buffer.append(ch);
        }

        @Override
        protected void inDigitWord(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void inDelimiter(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }
    };
    private static final WordTokenizer LOWER_CASE_WITH_UNDERSCORES_TOKENIZER = new WordTokenizer() {
        @Override
        protected void startSentence(StringBuffer buffer, char ch) {
            buffer.append(Character.toLowerCase(ch));
        }

        @Override
        protected void startWord(StringBuffer buffer, char ch) {
            if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                buffer.append('_');
            }

            buffer.append(Character.toLowerCase(ch));
        }

        @Override
        protected void inWord(StringBuffer buffer, char ch) {
            buffer.append(Character.toLowerCase(ch));
        }

        @Override
        protected void startDigitSentence(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void startDigitWord(StringBuffer buffer, char ch) {
            if (!isDelimiter(buffer.charAt(buffer.length() - 1))) {
                buffer.append('_');
            }

            buffer.append(ch);
        }

        @Override
        protected void inDigitWord(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }

        @Override
        protected void inDelimiter(StringBuffer buffer, char ch) {
            buffer.append(ch);
        }
    };

    public static String addUTF8Bom(String value) {
        try {
            return new String(UTF8Bom, "UTF-8") + value;
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    /**
     * 判断字符串为空 （含空格）
     *
     * @param s 需要判断的字符串
     * @return 字符串为空或者null返回true
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (isBlank(s));
    }

    /**
     * 判断字符串是否相等
     *
     * @param s1           字串1
     * @param s2           字串2
     * @param pIgnoreCase 是否忽略大小写
     * @return 相等返回true
     */
    public static boolean equal(String s1, String s2, boolean pIgnoreCase) {
        if ((s1 == null) || (s2 == null)) {
            return false;
        }
        if (pIgnoreCase) {
            return s1.equalsIgnoreCase(s2);
        }
        return s1.equals(s2);
    }

    /**
     * 判断字符串为空 （不含空格）
     *
     * @param str 需要判断的字符串
     * @return 字符串为null或长度为0返回true
     */
    public static boolean isEmpty(String str) {
        return (str == null) || (str.length() == 0);
    }

    /**
     * 判断字符串不为空 （不含空格）
     *
     * @param str 需要判断的字符串
     * @return 字符串不为null并长度不为0返回true
     */
    public static boolean isNotEmpty(String str) {
        return (str != null) && (str.length() > 0);
    }

    /**
     * 判断字符串为空 （含空格）
     *
     * @param str 需要判断的字符串
     * @return 字符串为空或是空格返回true
     */
    public static boolean isBlank(String str) {
        int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串不为空 （含空格）
     *
     * @param str 需要判断的字符串
     * @return 字符串含有不为空格的字符串返回true
     */
    public static boolean isNotBlank(String str) {
        int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化string
     *
     * @param str 需要初始化的字符串
     * @return 如果是null返回"",不为null则原样返回
     */
    public static String defaultIfNull(String str) {
        return str == null ? "" : str;
    }

    /**
     * 初始化为null的string，自定义初始化值
     *
     * @param str        需要初始化的字符串
     * @param defaultStr 初始化的字符串
     * @return 如果是null返回defaultStr, 不为null则原样返回
     */
    public static String defaultIfNull(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 初始化为null的string
     *
     * @param str 需要初始化的字符串
     * @return 如果是null返回"",不为null则原样返回
     */
    public static String defaultIfEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 初始化为null的string，并返回自定义初始化值
     *
     * @param str        需要初始化的字符串
     * @param defaultStr 初始化的字符串
     * @return 如果是null或为空，返回defaultStr。反之原样返回
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return (str == null) || (str.length() == 0) ? defaultStr : str;
    }

    /**
     * 初始化为空格的string
     *
     * @param str 需要初始化的字符串
     * @return 如果是字符串是空格返回""。反之原样返回
     */
    public static String defaultIfBlank(String str) {
        return isBlank(str) ? "" : str;
    }

    /**
     * 初始化为空格的string，并返回自定义初始化值
     *
     * @param str        需要初始化的字符串
     * @param defaultStr 初始化的字符串
     * @return 如果是字符串是空格返回defaultStr。反之原样返回
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * 移除字符串所有的空格
     *
     * @param str 需要移除空格的字符串
     * @return 返回移除空格后的字符串
     */
    public static String trimAll(String str) {
        if (isNullOrEmpty(str)) {
            return "";
        }
        str = trim(str);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            String ch = substring(str, i, i + 1);
            if (isNotBlank(ch)) {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * 移除字符串开始和结束部分的字符串；原字符串为null时返回""
     *
     * @param str 原字符串
     * @return 返回移除后的新字符串
     */
    public static String trim(String str) {
        return trim(str, null, 0);
    }

    /**
     * 移除字符串开始和结束部分的字符串；原字符串为null时返回""
     *
     * @param str        原字符串
     * @param stripChars 指定移除的字符的字符集合
     * @return 返回移除后的新字符串
     */
    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }

    /**
     * 移除字符串开始部分的字符串；原字符串为null时返回""
     *
     * @param str 原字符串
     * @return 返回移除后的新字符串
     */
    public static String trimStart(String str) {
        return trim(str, null, -1);
    }

    /**
     * 移除字符串开始部分的字符串；原字符串为null时返回""
     *
     * @param str        原字符串
     * @param stripChars 指定移除的字符的字符集合
     * @return 返回移除后的新字符串
     */
    public static String trimStart(String str, String stripChars) {
        return trim(str, stripChars, -1);
    }

    /**
     * 移除字符串结束部分的字符串；原字符串为null时返回""
     *
     * @param str 原字符串
     * @return 返回移除后的新字符串
     */
    public static String trimEnd(String str) {
        return trim(str, null, 1);
    }

    /**
     * 移除字符串结束部分的字符串；原字符串为null时返回""
     *
     * @param str        原字符串
     * @param stripChars 指定移除的字符的字符集合
     * @return 返回移除后的新字符串
     */
    public static String trimEnd(String str, String stripChars) {
        return trim(str, stripChars, 1);
    }

    /**
     * 移除字符串两边的空字符串；原字符串为null或""时返回null
     *
     * @param str 原字符串
     * @return 返回移除后的新字符串
     */
    public static String trimToNull(String str) {
        return trimToNull(str, null);
    }

    /**
     * 移除字符串两边的字符串；原字符串为null或""时返回null
     *
     * @param str        原字符串
     * @param stripChars 指定移除的字符的字符集合
     * @return 返回移除后的新字符串
     */
    public static String trimToNull(String str, String stripChars) {
        String result = trim(str, stripChars);

        if ((result == null) || (result.length() == 0)) {
            return null;
        }

        return result;
    }

    /**
     * 移除字符串两边的空字符串；原字符串为null时返回""
     *
     * @param str 原字符串
     * @return 返回移除后的新字符串
     */
    public static String trimToEmpty(String str) {
        return trimToEmpty(str, null);
    }

    /**
     * 移除字符串两边的指定字符；原字符串为null时返回""
     *
     * @param str        原字符串
     * @param stripChars 指定移除的字符的字符集合
     * @return 返回移除后的新字符串
     */
    public static String trimToEmpty(String str, String stripChars) {
        String result = trim(str, stripChars);

        if (result == null) {
            return "";
        }

        return result;
    }

    /**
     * 移除字符串中特定的字符串
     *
     * @param str        原字符串
     * @param stripChars 移除的标志，null为空格
     * @param mode       小于0移除开始部分；0移除两边；大于0移除结束部分
     * @return 返回移除后的字符串
     */
    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end)
                        && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            }
            if (stripChars.length() == 0) {
                return str;
            }

            while ((start < end)
                    && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }

        }

        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end)
                        && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            }
            if (stripChars.length() == 0) {
                return str;
            }

            while ((start < end)
                    && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }

        }

        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }

        return str;
    }

    /**
     * 对比2个字符串是否完全相等
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 相等返回true
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    /**
     * 对比2个字符串是否完全相等 忽略大小写
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 相等true
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 字符串是否是字母
     *
     * @param str 原字符串
     * @return 小写返回true
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 字符串是否是字母且不含空格
     *
     * @param str 原字符串
     * @return 全部小写返回true
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if ((!Character.isLetter(str.charAt(i))) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 字符串是否是字母或数字
     *
     * @param str 原字符串
     * @return 全部字母或数字返回true
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 字符串是否是字母或数字且不含空格
     *
     * @param str 原字符串
     * @return 全部字母或数字返回true
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if ((!Character.isLetterOrDigit(str.charAt(i)))
                    && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否都是数字
     *
     * @param str 原字符串
     * @return 全是数字返回true
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否都是数字且不含空格
     *
     * @param str 原字符串
     * @return 全是数字返回true
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if ((!Character.isDigit(str.charAt(i))) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否都含空格
     *
     * @param str 原字符串
     * @return 含空格返回true
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将字符串转换为大写
     *
     * @param str 原字符串
     * @return 返回大写字符串
     */
    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toUpperCase();
    }

    /**
     * 将字符串转换为小写
     *
     * @param str 原字符串
     * @return 返回小写字符串
     */
    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }

    /**
     * 将字符串的第一个字符大写，并将字符长度放在一位
     * 如 thisismy  转换后 8Thisismy
     *
     * @param str
     * @return
     */
    public static String capitalize(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        return strLen + Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串的第一个字符小写，并将字符长度放在一位
     * 如 Thisismy  转换后 8thisismy
     *
     * @param str
     * @return
     */
    public static String uncapitalize(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        return strLen + Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 字符串反转大小写，即大写改小写，小写改大写
     *
     * @param str 原字符串
     * @return 返回反转后的字符串
     */
    public static String swapCase(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(strLen);

        char ch = '\000';

        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }

            buffer.append(ch);
        }

        return buffer.toString();
    }

    /**
     * 将词组转换为 骆驼拼写法
     *
     * @param str
     * @return
     */
    public static String toCamelCase(String str) {
        return CAMEL_CASE_TOKENIZER.parse(str);
    }

    /**
     * 将词组转换为 帕斯卡拼写法
     *
     * @param str
     * @return
     */
    public static String toPascalCase(String str) {
        return PASCAL_CASE_TOKENIZER.parse(str);
    }

    /**
     * @param str
     * @return
     */
    public static String toUpperCaseWithUnderscores(String str) {
        return UPPER_CASE_WITH_UNDERSCORES_TOKENIZER.parse(str);
    }

    /**
     * @param str
     * @return
     */
    public static String toLowerCaseWithUnderscores(String str) {
        return LOWER_CASE_WITH_UNDERSCORES_TOKENIZER.parse(str);
    }

    /**
     * @param str
     * @return
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * 分割字符串
     *
     * @param str
     * @param separatorChar
     * @return
     */
    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return new String[0];
        }

        List<String> list = new ArrayList<String>();
        int i = 0;
        int start = 0;
        boolean match = false;

        while (i < length) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }

                i++;
                start = i;
                continue;
            }

            match = true;
            i++;
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return (String[]) (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 分割字符串
     *
     * @param str
     * @param separatorChars
     * @return
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * 分割字符串
     *
     * @param str
     * @param separatorChars
     * @param max
     * @return
     */
    public static String[] split(String str, String separatorChars, int max) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return new String[0];
        }

        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;

        if (separatorChars == null) {
            while (i < length) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    i++;
                    start = i;
                    continue;
                }

                match = true;
                i++;
            }
        }
        if (separatorChars.length() == 1) {
            char sep = separatorChars.charAt(0);

            while (i < length) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    i++;
                    start = i;
                    continue;
                }

                match = true;
                i++;
            }
        } else {
            while (i < length) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    i++;
                    start = i;
                    continue;
                }

                match = true;
                i++;
            }
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return (String[]) (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 拼接字符串
     *
     * @param array
     * @return
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * 拼接字符串
     *
     * @param array
     * @param separator
     * @return
     */
    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }

        int arraySize = array.length;
        int bufSize = arraySize == 0 ? 0 : ((array[0] == null ? 16 : array[0]
                .toString().length()) + 1) * arraySize;

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }

            if (array[i] != null) {
                buf.append(array[i]);
            }
        }

        return buf.toString();
    }

    /**
     * 拼接字符串
     *
     * @param array
     * @param separator
     * @return
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }

        if (separator == null) {
            separator = "";
        }

        int arraySize = array.length;

        int bufSize = arraySize == 0 ? 0
                : arraySize
                * ((array[0] == null ? 16 : array[0].toString()
                .length()) + (separator != null ? separator
                .length() : 0));

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if ((separator != null) && (i > 0)) {
                buf.append(separator);
            }

            if (array[i] != null) {
                buf.append(array[i]);
            }
        }

        return buf.toString();
    }

    /**
     * 拼接固定长度的字符串
     * 初始化长度 256
     *
     * @param array
     * @param sep   分割符
     * @return
     */
    public static String joinWithoutSpace(String[] array, String sep) {
        if ((array == null) || (array.length == 0)) {
            return null;
        }
        if (isNullOrEmpty(sep)) {
            sep = "";
        }
        StringBuffer buf = new StringBuffer(256);
        for (int i = 0; i < array.length; i++) {
            if (!isNullOrEmpty(array[i])) {
                if (i == 0) {
                    buf.append(array[i]);
                } else {
                    buf.append(sep).append(array[i]);
                }
            }
        }
        return buf.toString();
    }

    /**
     * 拼接字符串
     *
     * @param iterator
     * @param separator 分割符
     * @return
     */
    public static String join(Iterator<?> iterator, char separator) {
        if (iterator == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer(256);

        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj != null) {
                buf.append(obj);
            }

            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }

        return buf.toString();
    }

    /**
     * 拼接字符串
     *
     * @param iterator
     * @param separator 分割符
     * @return
     */
    public static String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        }

        StringBuffer buf = new StringBuffer(256);

        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj != null) {
                buf.append(obj);
            }

            if ((separator != null) && (iterator.hasNext())) {
                buf.append(separator);
            }
        }

        return buf.toString();
    }

    /**
     * 找到字符所在字符串的位置
     *
     * @param str
     * @param searchChar 要查找的字符
     * @return
     */
    public static int indexOf(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.indexOf(searchChar);
    }

    /**
     * 找到字符所在字符串的位置
     *
     * @param str
     * @param searchChar 要查找的字符
     * @param startPos   起点位置
     * @return
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.indexOf(searchChar, startPos);
    }

    /**
     * 找到字符所在字符串的位置
     *
     * @param str
     * @param searchStr 要查找的字符
     * @return
     */
    public static int indexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.indexOf(searchStr);
    }

    /**
     * 找到字符所在字符串的位置
     *
     * @param str
     * @param searchStr 要查找的字符
     * @param startPos  开始查找的位置
     * @return
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        if ((searchStr.length() == 0) && (startPos >= str.length())) {
            return str.length();
        }

        return str.indexOf(searchStr, startPos);
    }

    /**
     * 找到字符数组中第一个匹配到的所在的位置
     *
     * @param str
     * @param searchChars 要查找的字符
     * @return
     */
    public static int indexOfAny(String str, char[] searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                || (searchChars.length == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 找到字符数组中第一个匹配到的所在的位置
     *
     * @param str
     * @param searchChars 要查找的字符
     * @return
     */
    public static int indexOfAny(String str, String searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                || (searchChars.length() == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < searchChars.length(); j++) {
                if (searchChars.charAt(j) == ch) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * 找到字符数组中第一个匹配到的所在的位置
     *
     * @param str
     * @param searchStrs 要查找的字符
     * @return
     */
    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }

        int sz = searchStrs.length;

        int ret = 2147483647;

        int tmp = 0;

        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];

            if (search == null) {
                continue;
            }
            tmp = str.indexOf(search);

            if (tmp == -1) {
                continue;
            }
            if (tmp < ret) {
                ret = tmp;
            }
        }

        return ret == 2147483647 ? -1 : ret;
    }

    /**
     * 查找当前字符串中没有匹配的第一个字符的位置
     * 如：字符串：123456  查找字符 [1,2] 结果2
     *
     * @param str
     * @param searchChars
     * @return
     */
    public static int indexOfAnyBut(String str, char[] searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                || (searchChars.length == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            int j = 0;
            while (true) {
                if (j < searchChars.length) {
                    if (searchChars[j] == ch) {
                        break;
                    }
                    j++;
                    continue;
                } else {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 查找当前字符串中没有匹配的第一个字符的位置
     * 如：字符串：123456  查找字符 12 结果2
     *
     * @param str
     * @param searchChars
     * @return
     */
    public static int indexOfAnyBut(String str, String searchChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                || (searchChars.length() == 0)) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 从结尾开始查找，匹配到的第一字符所在的索引
     *
     * @param str
     * @param searchChar
     * @return
     */
    public static int lastIndexOf(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.lastIndexOf(searchChar);
    }

    /**
     * 从结尾开始查找，匹配到的第一字符所在的索引
     *
     * @param str
     * @param searchChar
     * @param startPos
     * @return
     */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if ((str == null) || (str.length() == 0)) {
            return -1;
        }

        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * 从结尾开始查找，匹配到的第一字符所在的索引
     *
     * @param str
     * @param searchStr
     * @return
     */
    public static int lastIndexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.lastIndexOf(searchStr);
    }

    /**
     * 从结尾开始查找，匹配到的第一字符所在的索引
     *
     * @param str
     * @param searchStr
     * @param startPos
     * @return
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }

        return str.lastIndexOf(searchStr, startPos);
    }

    /**
     * @param str
     * @param searchStrs
     * @return
     */
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }

        int searchStrsLength = searchStrs.length;
        int index = -1;
        int tmp = 0;

        for (int i = 0; i < searchStrsLength; i++) {
            String search = searchStrs[i];

            if (search == null) {
                continue;
            }
            tmp = str.lastIndexOf(search);

            if (tmp > index) {
                index = tmp;
            }
        }

        return index;
    }

    /**
     * @param str
     * @param searchChar
     * @return
     */
    public static boolean contains(String str, char searchChar) {
        if ((str == null) || (str.length() == 0)) {
            return false;
        }

        return str.indexOf(searchChar) >= 0;
    }

    /**
     * @param str
     * @param searchStr
     * @return
     */
    public static boolean contains(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }

        return str.indexOf(searchStr) >= 0;
    }

    /**
     * @param str
     * @param valid
     * @return
     */
    public static boolean containsOnly(String str, char[] valid) {
        if ((valid == null) || (str == null)) {
            return false;
        }

        if (str.length() == 0) {
            return true;
        }

        if (valid.length == 0) {
            return false;
        }

        return indexOfAnyBut(str, valid) == -1;
    }

    /**
     * @param str
     * @param valid
     * @return
     */
    public static boolean containsOnly(String str, String valid) {
        if ((str == null) || (valid == null)) {
            return false;
        }

        return containsOnly(str, valid.toCharArray());
    }

    /**
     * @param str
     * @param invalid
     * @return
     */
    public static boolean containsNone(String str, char[] invalid) {
        if ((str == null) || (invalid == null)) {
            return true;
        }

        int strSize = str.length();
        int validSize = invalid.length;

        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);

            for (int j = 0; j < validSize; j++) {
                if (invalid[j] == ch) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @param str
     * @param invalidChars
     * @return
     */
    public static boolean containsNone(String str, String invalidChars) {
        if ((str == null) || (invalidChars == null)) {
            return true;
        }

        return containsNone(str, invalidChars.toCharArray());
    }

    /**
     * @param str
     * @param subStr
     * @return
     */
    public static int countMatches(String str, String subStr) {
        if ((str == null) || (str.length() == 0) || (subStr == null)
                || (subStr.length() == 0)) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index += subStr.length();
        }

        return count;
    }

    /**
     * @param str
     * @param start
     * @return
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }

        if (start > str.length()) {
            return "";
        }

        return str.substring(start);
    }

    /**
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        if (end < 0) {
            end = str.length() + end;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }

        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * 从头开始保留指定长度的字符串
     *
     * @param str
     * @param len
     * @return
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }

        if (len < 0) {
            return "";
        }

        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * 从结尾开始保留指定长度的字符串
     * 如：abcdefghijk 长度4 结果：hijk
     *
     * @param str
     * @param len
     * @return
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }

        if (len < 0) {
            return "";
        }

        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * @param str
     * @param pos
     * @param len
     * @return
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }

        if ((len < 0) || (pos > str.length())) {
            return "";
        }

        if (pos < 0) {
            pos = 0;
        }

        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public static String substringBefore(String str, String separator) {
        if ((str == null) || (separator == null) || (str.length() == 0)) {
            return str;
        }

        if (separator.length() == 0) {
            return "";
        }

        int pos = str.indexOf(separator);

        if (pos == -1) {
            return str;
        }

        return str.substring(0, pos);
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public static String substringAfter(String str, String separator) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        if (separator == null) {
            return "";
        }

        int pos = str.indexOf(separator);

        if (pos == -1) {
            return "";
        }

        return str.substring(pos + separator.length());
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public static String substringBeforeLast(String str, String separator) {
        if ((str == null) || (separator == null) || (str.length() == 0)
                || (separator.length() == 0)) {
            return str;
        }

        int pos = str.lastIndexOf(separator);

        if (pos == -1) {
            return str;
        }

        return str.substring(0, pos);
    }

    /**
     * @param str
     * @param separator
     * @return
     */
    public static String substringAfterLast(String str, String separator) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        if ((separator == null) || (separator.length() == 0)) {
            return "";
        }

        int pos = str.lastIndexOf(separator);

        if ((pos == -1) || (pos == str.length() - separator.length())) {
            return "";
        }

        return str.substring(pos + separator.length());
    }

    /**
     * @param str
     * @param tag
     * @return
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag, 0);
    }

    /**
     * @param str
     * @param open
     * @param close
     * @return
     */
    public static String substringBetween(String str, String open, String close) {
        return substringBetween(str, open, close, 0);
    }

    /**
     * @param str
     * @param open
     * @param close
     * @param fromIndex
     * @return
     */
    public static String substringBetween(String str, String open,
                                          String close, int fromIndex) {
        if ((str == null) || (open == null) || (close == null)) {
            return null;
        }

        int start = str.indexOf(open, fromIndex);

        if (start != -1) {
            int end = str.indexOf(close, start + open.length());

            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }

        return null;
    }

    /**
     * @param str
     * @return
     */
    public static String deleteWhitespace(String str) {
        if (str == null) {
            return null;
        }

        int sz = str.length();
        StringBuffer buffer = new StringBuffer(sz);

        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                buffer.append(str.charAt(i));
            }
        }

        return buffer.toString();
    }

    /**
     * @param text
     * @param repl
     * @param with
     * @return
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * @param text
     * @param repl
     * @param with
     * @return
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * @param text
     * @param repl
     * @param with
     * @param max
     * @return
     */
    public static String replace(String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null)
                || (repl.length() == 0) || (max == 0)) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0;
        int end = 0;

        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            max--;
            if (max == 0) {
                break;
            }
        }

        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * @param str
     * @param searchChar
     * @param replaceChar
     * @return
     */
    public static String replaceChars(String str, char searchChar,
                                      char replaceChar) {
        if (str == null) {
            return null;
        }

        return str.replace(searchChar, replaceChar);
    }

    /**
     * @param str
     * @param searchChars
     * @param replaceChars
     * @return
     */
    public static String replaceChars(String str, String searchChars,
                                      String replaceChars) {
        if ((str == null) || (str.length() == 0) || (searchChars == null)
                || (searchChars.length() == 0)) {
            return str;
        }

        char[] chars = str.toCharArray();
        int len = chars.length;
        boolean modified = false;

        int i = 0;
        for (int isize = searchChars.length(); i < isize; i++) {
            char searchChar = searchChars.charAt(i);

            if ((replaceChars == null) || (i >= replaceChars.length())) {
                int pos = 0;

                for (int j = 0; j < len; j++) {
                    if (chars[j] != searchChar) {
                        chars[(pos++)] = chars[j];
                    } else {
                        modified = true;
                    }
                }

                len = pos;
            } else {
                for (int j = 0; j < len; j++) {
                    if (chars[j] == searchChar) {
                        chars[j] = replaceChars.charAt(i);
                        modified = true;
                    }
                }
            }
        }

        if (!modified) {
            return str;
        }

        return new String(chars, 0, len);
    }

    /**
     * @param str
     * @param overlay
     * @param start
     * @param end
     * @return
     */
    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }

        if (overlay == null) {
            overlay = "";
        }

        int len = str.length();

        if (start < 0) {
            start = 0;
        }

        if (start > len) {
            start = len;
        }

        if (end < 0) {
            end = 0;
        }

        if (end > len) {
            end = len;
        }

        if (start > end) {
            int temp = start;

            start = end;
            end = temp;
        }

        return len + start - end + overlay.length() + 1
                + str.substring(0, start) + overlay + str.substring(end);
    }

    public static String chomp(String str) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);

            if ((ch == '\r') || (ch == '\n')) {
                return "";
            }
            return str;
        }

        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last != '\r') {
            lastIdx++;
        }

        return str.substring(0, lastIdx);
    }

    public static String chomp(String str, String separator) {
        if ((str == null) || (str.length() == 0) || (separator == null)) {
            return str;
        }

        if (str.endsWith(separator)) {
            return str.substring(0, str.length() - separator.length());
        }

        return str;
    }

    public static String chop(String str) {
        if (str == null) {
            return null;
        }

        int strLen = str.length();

        if (strLen < 2) {
            return "";
        }

        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);

        if ((last == '\n') && (ret.charAt(lastIdx - 1) == '\r')) {
            return ret.substring(0, lastIdx - 1);
        }

        return ret;
    }

    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }

        if (repeat <= 0) {
            return "";
        }

        int inputLength = str.length();

        if ((repeat == 1) || (inputLength == 0)) {
            return str;
        }

        int outputLength = inputLength * repeat;

        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];

                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }

                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];

                for (int i = repeat * 2 - 2; i >= 0; i--) {
                    output2[i] = ch0;
                    output2[(i + 1)] = ch1;

                    i--;
                }

                return new String(output2);
        }

        StringBuffer buf = new StringBuffer(outputLength);

        for (int i = 0; i < repeat; i++) {
            buf.append(str);
        }

        return buf.toString();
    }

    public static String alignLeft(String str, int size) {
        return alignLeft(str, size, ' ');
    }

    public static String alignLeft(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }

        int pads = size - str.length();

        if (pads <= 0) {
            return str;
        }

        return alignLeft(str, size, String.valueOf(padChar));
    }

    public static String alignLeft(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();

        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[(i % padLen)];
        }

        return str.concat(new String(padding));
    }

    public static String alignRight(String str, int size) {
        return alignRight(str, size, ' ');
    }

    public static String alignRight(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }

        int pads = size - str.length();

        if (pads <= 0) {
            return str;
        }

        return alignRight(str, size, String.valueOf(padChar));
    }

    public static String alignRight(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();

        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[(i % padLen)];
        }

        return new String(padding).concat(str);
    }

    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    public static String center(String str, int size, char padChar) {
        if ((str == null) || (size <= 0)) {
            return str;
        }

        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        str = alignRight(str, strLen + pads / 2, padChar);
        str = alignLeft(str, size, padChar);
        return str;
    }

    public static String center(String str, int size, String padStr) {
        if ((str == null) || (size <= 0)) {
            return str;
        }

        if ((padStr == null) || (padStr.length() == 0)) {
            padStr = " ";
        }

        int strLen = str.length();
        int pads = size - strLen;

        if (pads <= 0) {
            return str;
        }

        str = alignRight(str, strLen + pads / 2, padStr);
        str = alignLeft(str, size, padStr);
        return str;
    }

    public static String reverse(String str) {
        if ((str == null) || (str.length() == 0)) {
            return str;
        }

        return new StringBuffer(str).reverse().toString();
    }

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }

        if (maxWidth < 4) {
            maxWidth = 4;
        }

        if (str.length() <= maxWidth) {
            return str;
        }

        if (offset > str.length()) {
            offset = str.length();
        }

        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }

        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }

        if (maxWidth < 7) {
            maxWidth = 7;
        }

        if (offset + (maxWidth - 3) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }

        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }

        if (str2 == null) {
            return str1;
        }

        int index = indexOfDifference(str1, str2);

        if (index == -1) {
            return "";
        }

        return str2.substring(index);
    }

    public static int indexOfDifference(String str1, String str2) {
        if ((str1 == str2) || (str1 == null) || (str2 == null)) {
            return -1;
        }

        for (int i = 0; (i < str1.length()) && (i < str2.length())
                && (str1.charAt(i) == str2.charAt(i)); i++) {
            if ((i < str2.length()) || (i < str1.length())) {
                return i;
            }
        }
        return -1;
    }

    public static int getLevenshteinDistance(String s, String t) {
        s = defaultIfNull(s);
        t = defaultIfNull(t);

        int n = s.length();
        int m = t.length();

        if (n == 0) {
            return m;
        }

        if (m == 0) {
            return n;
        }

        int[][] d = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            char si = s.charAt(i - 1);

            for (int j = 1; j <= m; j++) {
                char tj = t.charAt(j - 1);
                int cost;
                if (si == tj) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                d[i][j] = min(d[(i - 1)][j] + 1, d[i][(j - 1)] + 1,
                        d[(i - 1)][(j - 1)] + cost);
            }

        }

        return d[n][m];
    }

    private static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }

        if (c < a) {
            a = c;
        }

        return a;
    }

    public static String generateSqlInStr(String str) {
        if (isBlank(str)) {
            return "";
        }
        String[] strs = split(str, ",");
        return generateSqlInStr(strs);
    }

    public static String generateSqlInStr(String[] strs) {
        if ((strs == null) || (strs.length == 0)) {
            return "";
        }
        int i = 0;
        String re = "";
        for (String s : strs) {
            if (isNotBlank(s)) {
                if (i == 0) {
                    re = "'" + s;
                    i++;
                } else {
                    re = re + "','" + s;
                }
            }
        }
        re = re + "'";
        return re;
    }

    public static String generateSqlInStr(StringCollection strColl) {
        String str = "";
        for (String tmp : strColl) {
            if (isNotBlank(tmp)) {
                if (isBlank(str)) {
                    str = "'" + tmp + "'";
                } else {
                    str = str + ",'" + tmp + "'";
                }
            }
        }
        return str;
    }

    public static String serialize(Object value) {
        if (value == null) {
            return "";
        }
        Class<?> clz = value.getClass();

        if ((clz == java.util.Date.class) || (clz == java.sql.Date.class)) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS000",
                    Locale.CHINA).format((java.util.Date) value);
        }
        if (clz == Timestamp.class) {
            String microSecond = "000000"
                    + Math.round(((Timestamp) value).getNanos() / 1000.0F);

            microSecond = microSecond.substring(microSecond.length() - 6,
                    microSecond.length());

            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)
                    .format((Timestamp) value) + "." + microSecond;
        }

        if (clz == Boolean.class) {
            return ((Boolean) value).booleanValue() ? "1" : "0";
        }
        return String.valueOf(value);
    }

    public static String removeLast(String pSource, char pMark) {
        if (StringUtil.isNullOrEmpty(pSource)) {
            return pSource;
        }
        if (pSource.charAt(pSource.length() - 1) == pMark) {
            return pSource.substring(0, pSource.length() - 1);
        }
        return pSource;
    }

    /**
     * 检查并转换特殊字符
     *
     * @param pSource
     * @return
     */
    public static String convertJson(String pSource) {


        if (pSource.indexOf("\\") >= 0) {
            pSource = pSource.replaceAll("\\\\", "\\\\\\\\");
            System.out.println(pSource);
        }
        if (pSource.indexOf("/") >= 0) {
            pSource = pSource.replaceAll("/", "\\\\/");
        }
        if (pSource.indexOf("\"") >= 0) {
            pSource = pSource.replaceAll("\\\"", "\\\\\"");
        }
        if (pSource.indexOf("\t") >= 0) {
            pSource = pSource.replaceAll("\\\t", "\\\\t");
        }
        if (pSource.indexOf("\f") >= 0) {
            pSource = pSource.replaceAll("\\\f", "\\\\f");
        }
        if (pSource.indexOf("\b") >= 0) {
            pSource = pSource.replaceAll("\\\b", "\\\\b");
        }
        if (pSource.indexOf("\n") >= 0) {
            pSource = pSource.replaceAll("\\\n", "\\\\n");
        }
        if (pSource.indexOf("\r") >= 0) {
            pSource = pSource.replaceAll("\\\r", "\\\\r");
        }
        return pSource;
    }

    /**
     * 将InputStream 转换成 String 转换后释放InputStream
     *
     * @param is      输入的流（注意转换后会释放）
     * @param charset 字符集（如：utf-8）
     * @return
     */
    public static String parseInputStream(InputStream is, String charset) throws APPErrorException {
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            int i = -1;
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            if (!StringUtil.isNullOrEmpty(charset)) {
                return baos.toString(charset);
            }
            return baos.toString();
        } catch (IOException e) {
            throw new APPErrorException("转换失败", e);

        } finally {
            if (null != baos) {
                try {
                    baos.close();
                } catch (IOException e) {

                }
                baos = null;

                if (null != is) {
                    try {
                        is.close();

                    } catch (IOException e) {
                    }
                    is = null;
                }
            }
        }

    }

    /**
     * 将js的escape编码结果进行解码
     *
     * @param src
     * @return
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    private static abstract class WordTokenizer {
        protected static final char UNDERSCORE = '_';

        public String parse(String str) {
            if (StringUtil.isEmpty(str)) {
                return str;
            }

            int length = str.length();
            StringBuffer buffer = new StringBuffer(length);

            for (int index = 0; index < length; index++) {
                char ch = str.charAt(index);

                if (Character.isWhitespace(ch)) {
                    continue;
                }

                if (Character.isUpperCase(ch)) {
                    int wordIndex = index + 1;

                    while (wordIndex < length) {
                        char wordChar = str.charAt(wordIndex);

                        if (Character.isUpperCase(wordChar)) {
                            wordIndex++;
                        } else {
                            if (!Character.isLowerCase(wordChar)) {
                                break;
                            }
                            wordIndex--;
                            break;
                        }

                    }

                    if ((wordIndex == length) || (wordIndex > index)) {
                        index = parseUpperCaseWord(buffer, str, index,
                                wordIndex);
                    } else {
                        index = parseTitleCaseWord(buffer, str, index);
                    }

                } else if (Character.isLowerCase(ch)) {
                    index = parseLowerCaseWord(buffer, str, index);
                } else if (Character.isDigit(ch)) {
                    index = parseDigitWord(buffer, str, index);
                } else {
                    inDelimiter(buffer, ch);
                }
            }
            return buffer.toString();
        }

        private int parseUpperCaseWord(StringBuffer buffer, String str,
                                       int index, int length) {
            char ch = str.charAt(index++);

            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            for (; index < length; index++) {
                ch = str.charAt(index);
                inWord(buffer, ch);
            }

            return index - 1;
        }

        private int parseLowerCaseWord(StringBuffer buffer, String str,
                                       int index) {
            char ch = str.charAt(index++);

            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (!Character.isLowerCase(ch)) {
                    break;
                }
                inWord(buffer, ch);
            }

            return index - 1;
        }

        private int parseTitleCaseWord(StringBuffer buffer, String str,
                                       int index) {
            char ch = str.charAt(index++);

            if (buffer.length() == 0) {
                startSentence(buffer, ch);
            } else {
                startWord(buffer, ch);
            }

            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (!Character.isLowerCase(ch)) {
                    break;
                }
                inWord(buffer, ch);
            }

            return index - 1;
        }

        private int parseDigitWord(StringBuffer buffer, String str, int index) {
            char ch = str.charAt(index++);

            if (buffer.length() == 0) {
                startDigitSentence(buffer, ch);
            } else {
                startDigitWord(buffer, ch);
            }

            int length = str.length();

            for (; index < length; index++) {
                ch = str.charAt(index);

                if (!Character.isDigit(ch)) {
                    break;
                }
                inDigitWord(buffer, ch);
            }

            return index - 1;
        }

        protected boolean isDelimiter(char ch) {
            return (!Character.isUpperCase(ch)) && (!Character.isLowerCase(ch))
                    && (!Character.isDigit(ch));
        }

        /**
         * startSentence
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void startSentence(StringBuffer paramStringBuffer,
                                              char paramChar);

        /**
         * startWord
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void startWord(StringBuffer paramStringBuffer,
                                          char paramChar);

        /**
         * inWord
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void inWord(StringBuffer paramStringBuffer,
                                       char paramChar);

        /**
         * startDigitSentence
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void startDigitSentence(
                StringBuffer paramStringBuffer, char paramChar);

        /**
         * startDigitWord
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void startDigitWord(StringBuffer paramStringBuffer,
                                               char paramChar);

        /**
         * inDigitWord
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void inDigitWord(StringBuffer paramStringBuffer,
                                            char paramChar);

        /**
         *  inDelimiter
         * @param paramStringBuffer
         * @param paramChar
         */
        protected abstract void inDelimiter(StringBuffer paramStringBuffer,
                                            char paramChar);
    }
}

package tgtools.util;

import tgtools.exceptions.APPRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tianjing
 */
public class RegexHelper {

    /**
     * 获取所有匹配的结果
     *
     * @param content
     * @param patternStr
     * @return
     */
    public static List<String> regexAll(String content, String patternStr) {
        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);

        while ( matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                result.add(matcher.group(i));
            }
        }

        return result;

    }

    /**
     * 获取第一个匹配的结果
     *
     * @param content
     * @param patternStr
     * @return
     */
    public static String regexFirst(String content, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);

        boolean rs = matcher.find();
        if(rs) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
              return matcher.group(i);
            }
        }
        return StringUtil.EMPTY_STRING;
    }
    public static boolean isMatch(String pContent,String pRegex){
        return Pattern.compile(pRegex).matcher(pContent).find();
    }


    protected static Pattern PATTERN_NUBMER =Pattern.compile("(^-[0-9]+\\.*[0-9]*$)|(^[0-9]+\\.*[0-9]*$)");
    /**
     * 是否是数字格式 包括 整数 负数 小数
     * @param pContent
     * @return
     */
    public static boolean isNubmer(String pContent){
        return PATTERN_NUBMER.matcher(pContent).find();
    }


    protected static Pattern PATTERN_DATE =Pattern.compile("^((\\\\d{2}(([02468][048])|([13579][26]))[\\\\-\\\\-\\\\s]?((((0?\" +\"[13578])|(1[02]))[\\\\-\\\\-\\\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))\" +\"|(((0?[469])|(11))[\\\\-\\\\-\\\\s]?((0?[1-9])|([1-2][0-9])|(30)))|\" +\"(0?2[\\\\-\\\\-\\\\s]?((0?[1-9])|([1-2][0-9])))))|(\\\\d{2}(([02468][12\" +\"35679])|([13579][01345789]))[\\\\-\\\\-\\\\s]?((((0?[13578])|(1[02]))\" +\"[\\\\-\\\\-\\\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))\" +\"[\\\\-\\\\-\\\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\\\-\\\\-\\\\s]?((0?[\" +\"1-9])|(1[0-9])|(2[0-8]))))))");

    /**
     * 是否是日期格式 如2003-02-20
     * @param pContent
     * @return
     */
    public static boolean isDate(String pContent){
        return PATTERN_DATE.matcher(pContent).find();
    }


    protected static Pattern PATTERN_DATETIME =Pattern.compile("^((\\\\d{2}(([02468][048])|([13579][26]))[\\\\-\\\\/\\\\s]?((((0?[13578])|(1[02]))[\\\\-\\\\/\\\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\\\-\\\\/\\\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\\\-\\\\/\\\\s]?((0?[1-9])|([1-2][0-9])))))|(\\\\d{2}(([02468][1235679])|([13579][01345789]))[\\\\-\\\\/\\\\s]?((((0?[13578])|(1[02]))[\\\\-\\\\/\\\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\\\-\\\\/\\\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\\\-\\\\/\\\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\\\s(((0?[0-9])|([1-2][0-3]))\\\\:([0-5]?[0-9])((\\\\s)|(\\\\:([0-5]?[0-9])))))?$");
    /**
     * 是否是完整时间格式 如：2004-02-29 23:59:59
     * @param pContent
     * @return
     */
    public static boolean isDateTime(String pContent){
        return PATTERN_DATETIME.matcher(pContent).find();
    }

    /**
     * 正则替换字符串内容
     * 如： String sql="a='11' and b=''22''";
     *     System.out.println("escape:"+replace(sql,"''","'+"));
     *     输出：a=''11'' and b=''22''
     * @param pContent 原文
     * @param pReplaceStr 匹配后替换的内容
     * @param pPattern 正则表达式
     * @return
     */
    public static String replace(String pContent,String pReplaceStr,String pPattern) {
        Pattern pattern=Pattern.compile(pPattern);

        Matcher matcher = pattern.matcher(pContent);
        StringBuffer buffer = new StringBuffer();
        try {
            while (matcher.find()) {
                String matchervalue = pReplaceStr;
                if (matchervalue != null) {
                    matchervalue=matchervalue.replaceAll("\\$", "\\\\\\$");
                    matcher.appendReplacement(buffer, matchervalue);
                }
            }
        }catch (Exception e)
        {
            throw new APPRuntimeException("正则替换出错；原因："+e.getMessage(),e);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

//        public static void main(String[] args) {
//            String sql="a='11' and b=''22''";
//            System.out.println("escape:"+replace(sql,"char(38)","'+"));
//    }
}

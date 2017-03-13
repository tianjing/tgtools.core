package tgtools.util;

import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名  称：支持变量且可以转换到类
 *  变量形式：#{}
 *  使用 convert 转换到类
 * 编写者：田径
 * 功  能：
 * 时  间：20:48
 */
public class PropertiesObject extends Properties {


    private static final Pattern PATTERN =  Pattern.compile("#\\{([^\\}]+)\\}");

    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        if(null==value)return null;

        Matcher matcher = PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        try {
            while (matcher.find()) {
                String matcherKey = matcher.group(1);
                String matchervalue = super.getProperty(matcherKey);
                if (matchervalue != null) {
                    matchervalue=matchervalue.replaceAll("\\$", "\\\\\\$");
                    matcher.appendReplacement(buffer, matchervalue);
                }
            }
        }catch (Exception e)
        {
            throw new APPRuntimeException("获取值出错；key："+key+";原因："+e.getMessage(),e);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 将KEY和VALUE放到类对应的Field 或 同名的Setter中
     * @param p_T
     * @param <T>
     * @return
     * @throws APPErrorException
     */
    @SuppressWarnings("unchecked")
    public <T> T convert(T p_T) throws APPErrorException{
        Class clazz=p_T.getClass();
        try {
            for(Map.Entry<Object,Object> item: this.entrySet()) {
                Field field=null;
                String key=item.getKey().toString();
                try {
                    field = clazz.getDeclaredField(key);
                } catch (NoSuchFieldException ex)
                {}
                if (null != field) {
                    field.set(p_T, getProperty(key));
                }
                else {
                    Method method=null;
                    try {
                        method = clazz.getDeclaredMethod("set"+key,String.class);
                    } catch (Exception ex)
                    {}
                    if(null!=method)
                    {
                        method.invoke(p_T,getProperty(key));
                    }
                }
            }
        }
        catch (Exception e) {
            throw new APPErrorException("转换类出错："+e.getMessage(),e);
        }
        return p_T;
    }

    /**
     * 将KEY和VALUE放到类对应的Field 或 同名的Setter中
     * @param p_Class
     * @param <T>
     * @return
     * @throws APPErrorException
     */
    public <T> T convert(Class<? extends T> p_Class) throws APPErrorException {
        try {
            return convert(p_Class.newInstance());
        } catch (Exception e) {
            throw new APPErrorException("实例化对象出错："+p_Class+";错误："+e.getMessage(),e);
        }

    }
}

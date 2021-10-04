package tgtools.util;

import tgtools.data.DbTypeConverter;
import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * @author tianjing
 */
public class ReflectionUtil {

    /**
     * 找到指定的成员
     *
     * @param pClass     要需找的类型
     * @param pFieldName 字段名称
     * @return
     */
    public static Field findField(Class pClass, String pFieldName) {
        if (StringUtil.isNullOrEmpty(pFieldName)) {
            return null;
        }

        try {
            return pClass.getDeclaredField(pFieldName);
        } catch (NoSuchFieldException e) {
            if (null != pClass.getSuperclass()) {
                return findField(pClass.getSuperclass(), pFieldName);
            }
        }
        return null;
    }

    /**
     * 通过放射查找方法
     *
     * @param pClass
     * @param pMethodName
     * @return
     */
    public static Method findMethod(Class pClass, String pMethodName, Class<?>... pParameterTypes) {
        if (StringUtil.isNullOrEmpty(pMethodName)) {
            return null;
        }

        try {
            return pClass.getDeclaredMethod(pMethodName, pParameterTypes);
        } catch (NoSuchMethodException e) {
            if (null != pClass.getSuperclass()) {
                return findMethod(pClass.getSuperclass(), pMethodName, pParameterTypes);
            }
        }
        return null;
    }

    /**
     * 执行对象中的方法（反射）
     * 如：invokeMethod("get",new JSONObject(),new Class[]{String.class},new Object[]{"dd	"})
     *
     * @param pMethodName   方法名称
     * @param pObj          对象
     * @param parameterTypes 方法的参数类型
     * @param pParams       方法的参数
     * @return
     * @throws APPErrorException
     */
    public static Object invokeMethod(String pMethodName, Object pObj, Class<?>[] parameterTypes, Object[] pParams) throws APPErrorException {
        try {
            String methodname = pMethodName;

            Method method = pObj.getClass().getDeclaredMethod(methodname, parameterTypes);
            if (null == method) {
                throw new APPErrorException("处理出错：找不到方法名：" + methodname);
            }
            return method.invoke(pObj, pParams);
        } catch (Exception e) {
            throw new APPErrorException("处理出错：" + e.getMessage(), e);
        }


    }


    /**
     * 将字符串转换成指定的类型
     *
     * @param pClass 转换后的类型
     * @param pValue 值
     * @return
     */
    public static Object instanceSimpleClass(Class<?> pClass, String pValue) {
        if (pClass.equals(String.class)) {
            return pValue;
        }
        if ("".equals(pValue)) {
            return null;
        }
        if ((pClass.equals(Boolean.class)) || (pClass.equals(Boolean.TYPE))) {
            return Boolean.valueOf(pValue);
        }
        if ((pClass.equals(Byte.class)) || (pClass.equals(Byte.TYPE))) {
            return Byte.valueOf(pValue);
        }
        if ((pClass.equals(Double.class)) || (pClass.equals(Double.TYPE))) {
            return Double.valueOf(pValue);
        }
        if ((pClass.equals(Float.class)) || (pClass.equals(Float.TYPE))) {
            return Float.valueOf(pValue);
        }
        if ((pClass.equals(Integer.class)) || (pClass.equals(Integer.TYPE))) {
            return DbTypeConverter.convertIntegerValue(pValue);
        }
        if (pClass.equals(BigInteger.class)) {
            return new BigInteger(pValue);
        }
        if ((pClass.equals(Long.class)) || (pClass.equals(Long.TYPE))) {
            return Long.valueOf(pValue);
        }
        if ((pClass.equals(Short.class)) || (pClass.equals(Short.TYPE))) {
            return Short.valueOf(pValue);
        }
        if (pClass.isEnum()) {
            return pClass.cast(pValue);
        }
        if (java.util.Date.class.isAssignableFrom(pClass)) {
            pValue = pValue.replace("T", " ");
            try {
                java.util.Date date = null;
                if (pClass.equals(java.util.Date.class)) {
                    try {
                        date = DateUtil.parseLongDate(pValue);
                    } catch (Exception e) {
                        date = null;
                    }
                    if (date == null) {
                        try {
                            date = DateFormat.getDateInstance().parse(pValue);
                        } catch (Exception e) {
                            date = Timestamp.valueOf(pValue);
                            date = new java.util.Date(date.getTime());
                        }
                    }

                }

                if (pClass.equals(java.sql.Date.class)) {
                    try {
                        date = java.sql.Date.valueOf(pValue);
                    } catch (Exception e) {
                        date = Timestamp.valueOf(pValue);
                        date = new java.sql.Date(date.getTime());
                    }

                }

                if (pClass.equals(Time.class)) {
                    try {
                        date = Time.valueOf(pValue);
                    } catch (Exception e) {
                        date = Timestamp.valueOf(pValue);
                        date = Time.valueOf(new SimpleDateFormat("HH:mm:ss").format(date));
                    }
                }

                if (pClass.equals(Timestamp.class)) {
                    try {
                        date = Timestamp.valueOf(pValue);
                    } catch (Exception e) {
                        date = java.sql.Date.valueOf(pValue);
                        date = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(date));
                    }
                }

                return date;
            } catch (Exception e) {
                throw new APPRuntimeException(String.format("将字符串形式的值转换成时间类型对象时格式化失败，当前待格式化的时间字符串为[%1$s]。", new Object[]{pValue}), e);
            }

        }

        throw new APPRuntimeException(String.format("将字符串形式的值转换成简单类型的对象成员时遇,到尚不支持的数据类型[%1$s]。", new Object[]{pClass.getName()}));
    }

    /**
     * 获取jar中的资源
     *
     * @param name
     * @return
     */
    public static InputStream getResourceAsStream(String name) {
        InputStream resourceStream = null;
        ClassLoader classLoader = null;
        if (resourceStream == null) {
            // Try the current Thread context classloader
            classLoader = Thread.currentThread().getContextClassLoader();
            resourceStream = classLoader.getResourceAsStream(name);
            if (resourceStream == null) {
                // Finally, try the classloader for this class
                classLoader = ReflectionUtil.class.getClassLoader();
                resourceStream = classLoader.getResourceAsStream(name);
            }
        }
        return resourceStream;
    }

    /**
     * 获取jar中的资源
     *
     * @param name
     * @return
     */
    public static URL getResource(String name) {
        URL url = null;
        ClassLoader classLoader = null;
        if (url == null) {
            // Try the current Thread context classloader
            classLoader = Thread.currentThread().getContextClassLoader();
            url = classLoader.getResource(name);
            if (url == null) {
                // Finally, try the classloader for this class
                classLoader = ReflectionUtil.class.getClassLoader();
                url = classLoader.getResource(name);
            }
        }
        return url;
    }
}

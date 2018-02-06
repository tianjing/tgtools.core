package tgtools.util;

import tgtools.data.DbTypeConverter;
import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;
import tgtools.json.JSONException;
import tgtools.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class ReflectionUtil {

	/**
	 * 找到指定的成员
	 * @param p_Class 要需找的类型
	 * @param p_FieldName 字段名称
     * @return
     */
	public static Field findField(Class p_Class,String p_FieldName)
	{
		if(StringUtil.isNullOrEmpty(p_FieldName))return null;

		try {
			return p_Class.getDeclaredField(p_FieldName);
		} catch (NoSuchFieldException e) {
			if(null!=p_Class.getSuperclass())
			{
				return findField(p_Class.getSuperclass(),p_FieldName);
			}
		}
		return null;
	}

	/**
	 * 执行对象中的方法（反射）
	 * 如：invokeMethod("get",new JSONObject(),new Class[]{String.class},new Object[]{"dd	"})
	 * @param p_MethodName 方法名称
	 * @param p_Obj 对象
	 * @param parameterTypes 方法的参数类型
	 * @param p_Params 方法的参数
	 * @return
	 * @throws APPErrorException
     */
	 public static Object invokeMethod(String p_MethodName,Object p_Obj,Class<?>[] parameterTypes,Object[] p_Params) throws APPErrorException {
		 try {
			 String methodname =p_MethodName;

			 Method method = p_Obj.getClass().getDeclaredMethod(methodname, parameterTypes);
			 if (null == method) {
				 throw new APPErrorException("处理出错：找不到方法名：" + methodname);
			 }
			 return method.invoke(p_Obj, p_Params);
		 } catch (Exception e) {
			 throw new APPErrorException("处理出错：" + e.getMessage(), e);
		 }


	 }


	/**
	 * 将字符串转换成指定的类型
	 * @param p_class 转换后的类型
	 * @param p_value 值
     * @return
     */
	public static Object instanceSimpleClass(Class<?> p_class, String p_value)
	  {
	    if (p_class.equals(String.class))
	    {
	      return p_value;
	    }
	    if (p_value.equals("")) {
	      return null;
	    }
	    if ((p_class.equals(Boolean.class)) || (p_class.equals(Boolean.TYPE)))
	      return Boolean.valueOf(p_value);
	    if ((p_class.equals(Byte.class)) || (p_class.equals(Byte.TYPE)))
	      return Byte.valueOf(p_value);
	    if ((p_class.equals(Double.class)) || (p_class.equals(Double.TYPE)))
	      return Double.valueOf(p_value);
	    if ((p_class.equals(Float.class)) || (p_class.equals(Float.TYPE)))
	      return Float.valueOf(p_value);
	    if ((p_class.equals(Integer.class)) || (p_class.equals(Integer.TYPE))) {
	      return DbTypeConverter.convertIntegerValue(p_value);
	    }
	    if (p_class.equals(BigInteger.class))
	      return new BigInteger(p_value);
	    if ((p_class.equals(Long.class)) || (p_class.equals(Long.TYPE)))
	      return Long.valueOf(p_value);
	    if ((p_class.equals(Short.class)) || (p_class.equals(Short.TYPE))) {
	      return Short.valueOf(p_value);
	    }
	    if (p_class.isEnum())
	      return p_class.cast(p_value);
	    if (java.util.Date.class.isAssignableFrom(p_class))
	    {
	      p_value = p_value.replace("T", " ");
	      try {
	        java.util.Date date = null;
	        if (p_class.equals(java.util.Date.class))
	        {
	          try
	          {
	            date = DateUtil.parseLongDate(p_value);
	          } catch (Exception e) {
	            date = null;
	          }
	          if (date == null) {
	            try
	            {
	              date = DateFormat.getDateInstance().parse(p_value);
	            }
	            catch (Exception e) {
	              date = Timestamp.valueOf(p_value);
	              date = new java.util.Date(date.getTime());
	            }
	          }

	        }

	        if (p_class.equals(java.sql.Date.class)) {
	          try
	          {
	            date = java.sql.Date.valueOf(p_value);
	          } catch (Exception e) {
	            date = Timestamp.valueOf(p_value);
	            date = new java.sql.Date(date.getTime());
	          }

	        }

	        if (p_class.equals(Time.class)) {
	          try
	          {
	            date = Time.valueOf(p_value);
	          } catch (Exception e) {
	            date = Timestamp.valueOf(p_value);
	            date = Time.valueOf(new SimpleDateFormat("HH:mm:ss").format(date));
	          }
	        }

	        if (p_class.equals(Timestamp.class)) {
	          try
	          {
	            date = Timestamp.valueOf(p_value);
	          } catch (Exception e) {
	            date = java.sql.Date.valueOf(p_value);
	            date = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(date));
	          }
	        }

	        return date;
	      } catch (Exception e) {
	        throw new APPRuntimeException(String.format("将字符串形式的值转换成时间类型对象时格式化失败，当前待格式化的时间字符串为[%1$s]。", new Object[] { p_value }), e);
	      }

	    }

	    throw new APPRuntimeException(String.format("将字符串形式的值转换成简单类型的对象成员时遇,到尚不支持的数据类型[%1$s]。", new Object[] { p_class.getName() }));
	  }
}

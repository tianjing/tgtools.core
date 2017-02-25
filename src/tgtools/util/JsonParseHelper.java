package tgtools.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONException;
import tgtools.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 名  称：json 实体 互转类
 * 依赖jackson-databind-2.2.3
 * 编写者：田径
 * 功  能：
 * 时  间：12:45
 */
public class JsonParseHelper {
    private static ObjectMapper m_realMapper = new ObjectMapper();
    private static ObjectMapper m_mapper = new ObjectMapper();

    static
    {
        m_realMapper.setPropertyNamingStrategy(new PropertyNamingStrategy() {
            private static final long serialVersionUID = 1L;
            // 反序列化时调用
            @Override
            public String nameForSetterMethod(MapperConfig<?> config,
                                              AnnotatedMethod method, String defaultName) {

                String name =method.getName();
                if(name.startsWith("set"))
                {
                    return name.substring(3);
                }
                return name;
            }
            // 序列化时调用
            @Override
            public String nameForGetterMethod(MapperConfig<?> config,
                                              AnnotatedMethod method, String defaultName) {
                String name =method.getName();
                if(name.startsWith("get"))
                {
                    return name.substring(3);
                }
                else if(name.startsWith("is"))
                {
                    return name.substring(2);
                }
                return name;
            }
        });

    }
    private static ObjectMapper getMapper(boolean p_IsReal)
    {
        if(p_IsReal)
            return m_realMapper;
        return m_mapper;
    }
    /**
     * 将对象转换为JSON 字符串
     *
     * @param obj 需要转换的对象
     * @param p_IsReal 是否使用真实字段名（默认小写字段名）
     * @return 转换后的字符串
     */
    public static String parseToJson(Object obj,boolean p_IsReal) {

        String json = null;
        JsonGenerator gen = null;
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            gen = new JsonFactory().createGenerator(writer);

            getMapper(p_IsReal).writeValue(gen, obj);
            gen.close();
            json = writer.toString();
            writer.close();
        } catch (Exception ex) {
            LogHelper.error("", "实体转换json出错", "JsonHelper.parseToJson", ex);
        } finally {
            if (null != gen && !gen.isClosed()) {
                try {
                    gen.close();
                } catch (Exception e1) {
                }
            }
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
        return json;
    }

    /**
     * 将对象转换为 JSONObject
     * @param obj 需要转换的对象
     * @return 转换后的JSON对象
     * @throws APPErrorException
     */
    public static JSONObject parseToJsonObject(Object obj) throws APPErrorException {
       String res= parseToJson(obj,false);
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            throw new APPErrorException("JSon转换失败",e);
        }
    }

    /**
     * 将对象转换为 JSONArray
     * @param obj 需要转换的对象
     * @return 转换后的JSON对象
     * @throws APPErrorException
     */
    public static JSONArray parseToJsonArray(Object obj) throws APPErrorException {
        String res= parseToJson(obj,false);
        try {
            return new JSONArray(res);
        } catch (JSONException e) {
            throw new APPErrorException("JSon转换失败",e);
        }
    }
    /**
     * 将json字符串转换程对象
     * @param json json字符串
     * @param cls 对象类型
     * @param p_IsReal 是否使用真实字段名（默认小写字段名）
     * @return 转换后的对象
     */
    public static Object parseToObject(String json, Class<?> cls,boolean p_IsReal) throws APPErrorException {
        Object vo = null;
        try {
            vo = getMapper(p_IsReal).readValue(json, cls);
        } catch (IOException e) {
            throw new APPErrorException("json转换实体出错",e);
        }
        return vo;
    }

    /**
     * 将JSONObject 转换为对象
     * @param json json对象
     * @param cls 对象类型
     * @return 转换后的对象
     * @throws APPErrorException
     */
    public static Object parseToObject(JSONObject json, Class<?> cls) throws APPErrorException {
        return parseToObject(json.toString(),cls,false);
    }

    /**
     * 将JSONObject 转换为对象
     * @param json json对象
     * @param cls 对象类型
     * @return 转换后的对象
     * @throws APPErrorException
     */
    public static Object parseToObject(JSONArray json, Class<?> cls) throws APPErrorException {
        return parseToObject(json.toString(),cls,false);
    }

}

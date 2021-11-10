package tgtools.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tgtools.db.DataBaseFactory;
import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * 名  称：json转sql字符串
 *
 * @author tianjing
 * 功  能：
 * 时  间：9:42
 */
public class JsonSqlFactory {

    /**
     * 转换json 为 update sql语句
     *
     * @param pJson      json对象
     * @param pKeys      主键集合
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseUpdateSql(JSONObject pJson, List<String> pKeys, String pTableName) throws APPErrorException {
        return parseUpdateSql(pJson, DataBaseFactory.getDefault().getDataBaseType(), pKeys, pTableName);
    }

    /**
     * 转换json 为 update sql语句
     *
     * @param pJson      json对象
     * @param pDbType    数据库类型
     * @param pKeys      主键集合
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseUpdateSql(JSONObject pJson, String pDbType, List<String> pKeys, String pTableName) throws APPErrorException {
        String sql = "update ${tablename} set ${values} where ${filters}";
        String values = "";
        String filters = "";
        JSONArray names = pJson.names();
        for (int i = 0; i < names.length(); i++) {
            try {
                String key = names.getString(i);
                String value = pJson.getString(key);
                value = pJson.isNull(key) ? "NULL" : value;
                if (!StringUtil.isNullOrEmpty(key)) {
                    if (pKeys.contains(key)) {
                        filters += key + "='" + SqlStrHelper.escape(pDbType, value) + "' and ";
                    } else {
                        if (pJson.isNull(key)) {
                            values += key + "=NULL,";
                        } else {
                            values += key + "='" + SqlStrHelper.escape(pDbType, value) + "',";
                        }
                    }
                }
            } catch (Exception e) {
                throw new APPErrorException("数据不完整");
            }
        }
        values = StringUtil.removeLast(values, ',');
        filters += " 1=1";

        sql = StringUtil.replace(sql, "${tablename}", pTableName);
        sql = StringUtil.replace(sql, "${values}", values);
        sql = StringUtil.replace(sql, "${filters}", filters);
        return sql;
    }

    /**
     * 转换json 为 insert sql语句
     *
     * @param pJson      json 对象
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseInsertSql(JSONObject pJson, String pTableName) throws APPErrorException {
        return parseInsertSql(pJson, DataBaseFactory.getDefault().getDataBaseType(), pTableName);
    }

    /**
     * 转换json 为 insert sql语句
     *
     * @param pJson      json 对象
     * @param pDbType    数据库类型
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseInsertSql(JSONObject pJson, String pDbType, String pTableName) throws APPErrorException {
        String sql = "insert into ${tablename} (${keys}) values(${values})";
        String keys = "";
        String values = "";
        JSONArray names = pJson.names();
        for (int i = 0; i < names.length(); i++) {
            try {
                String key = names.getString(i);
                String value = pJson.getString(key);
                if (!StringUtil.isNullOrEmpty(key)) {
                    keys += key + ",";
                    values += pJson.isNull(key) ? "NULL," : "'" + SqlStrHelper.escape(pDbType, value) + "',";
                }
            } catch (Exception e) {
                throw new APPErrorException("数据不完整");
            }
        }
        values = StringUtil.removeLast(values, ',');
        keys = StringUtil.removeLast(keys, ',');
        sql = StringUtil.replace(sql, "${keys}", keys);
        sql = StringUtil.replace(sql, "${values}", values);
        sql = StringUtil.replace(sql, "${tablename}", pTableName);
        return sql;
    }

    /**
     * 转换json 为 update sql语句
     *
     * @param pJson      json对象
     * @param pDbType    数据库类型
     * @param pKeys      主键集合
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseUpdateSql(JsonNode pJson, String pDbType, List<String> pKeys, String pTableName) throws APPErrorException {
        String sql = "update ${tablename} set ${values} where ${filters}";
        String values = "";
        String filters = "";
        Iterator<String> names = pJson.fieldNames();
        while (names.hasNext()) {
            try {
                String key = names.next();
                String valuestr = "NULL";
                JsonNode value = pJson.get(key);
                if (!value.isNull() && value.isTextual()) {
                    valuestr = "'" + SqlStrHelper.escapeAll(pDbType, value.asText()) + "'";
                } else if (!value.isNull()) {
                    valuestr = value.toString();
                }
                if (!StringUtil.isNullOrEmpty(key)) {
                    if (pKeys.contains(key)) {
                        filters += key + "=" + valuestr + " and ";
                    } else {
                        values += key + "=" + valuestr + ",";
                    }
                }
            } catch (Exception e) {
                throw new APPErrorException("数据不完整");
            }
        }
        values = StringUtil.removeLast(values, ',');
        filters += " 1=1";

        sql = StringUtil.replace(sql, "${tablename}", pTableName);
        sql = StringUtil.replace(sql, "${values}", values);
        sql = StringUtil.replace(sql, "${filters}", filters);
        return sql;
    }

    /**
     * 转换json 为 insert sql语句
     *
     * @param pJson      json 对象
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseInsertSql(JsonNode pJson, String pDbType, String pTableName) throws APPErrorException {
        String sql = "insert into ${tablename} (${keys}) values(${values})";
        String keys = "";
        String values = "";
        Iterator<String> names = pJson.fieldNames();
        while (names.hasNext()) {
            try {
                String key = names.next();
                String valuestr = "NULL";
                JsonNode value = pJson.get(key);
                if (!value.isNull() && value.isTextual()) {
                    valuestr = "'" + SqlStrHelper.escapeAll(pDbType, value.asText()) + "'";
                } else if (!value.isNull()) {
                    valuestr = value.toString();
                }


                if (!StringUtil.isNullOrEmpty(key)) {
                    keys += key + ",";
                    values += valuestr + ",";
                }
            } catch (Exception e) {
                throw new APPErrorException("数据不完整");
            }
        }
        values = StringUtil.removeLast(values, ',');
        keys = StringUtil.removeLast(keys, ',');
        sql = StringUtil.replace(sql, "${keys}", keys);
        sql = StringUtil.replace(sql, "${values}", values);
        sql = StringUtil.replace(sql, "${tablename}", pTableName);
        return sql;
    }

//---------------------------------------------------------------------------------------------------------------------


    /**
     * 转换json 为 insert sql语句
     *
     * @param pData      json 对象
     * @param pMapper    json mapper 对象
     * @param pDbType    数据库类型
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseInsertSql(Object pData, ObjectMapper pMapper, String pDbType, String pTableName) throws APPErrorException {
        JsonNode pJsonNode = null;
        try {
            String vJson = pMapper.writeValueAsString(pData);
            pJsonNode = pMapper.readTree(vJson);
        } catch (Throwable e) {
            throw new APPErrorException("json 转换失败！" + pData.getClass());
        }
        if (null == pJsonNode) {
            throw new APPErrorException("输入的 JsonNod 错误！");
        }
        return parseInsertSql(pJsonNode, pDbType, pTableName);
    }

    /**
     * 转换json 为 insert sql语句
     *
     * @param pData      json 对象
     * @param pDbType    数据库类型
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseInsertSql(Object pData, String pDbType, String pTableName) throws APPErrorException {
        JsonNode pJsonNode = null;
        try {
            String vJson = tgtools.util.JsonParseHelper.getMapper(false).writeValueAsString(pData);
            pJsonNode = tgtools.util.JsonParseHelper.getMapper(false).readTree(vJson);
        } catch (Throwable e) {
            throw new APPErrorException("json 转换失败！" + pData.getClass());
        }
        if (null == pJsonNode) {
            throw new APPErrorException("输入的 JsonNod 错误！");
        }
        return parseInsertSql(pJsonNode, pDbType, pTableName);

    }


    /**
     * 转换 实体类 为 update sql语句
     * @param pData      json 对象
     * @param pMapper    json mapper 对象
     * @param pDbType    数据库类型
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseUpdateSql(Object pData, ObjectMapper pMapper, String pDbType, List<String> pKeys, String pTableName) throws APPErrorException {
        JsonNode pJsonNode = null;
        try {
            String vJson = pMapper.writeValueAsString(pData);
            pJsonNode = pMapper.readTree(vJson);
        } catch (Throwable e) {
            throw new APPErrorException("json 转换失败！" + pData.getClass());
        }
        if (null == pJsonNode) {
            throw new APPErrorException("输入的 JsonNod 错误！");
        }
        return parseUpdateSql(pJsonNode, pDbType,pKeys, pTableName);
    }

    /**
     * 转换 实体类 为 update sql语句
     * @param pData      json 对象
     * @param pDbType    数据库类型
     * @param pTableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseUpdateSql(Object pData, String pDbType, List<String> pKeys, String pTableName) throws APPErrorException {
        JsonNode pJsonNode = null;
        try {
            String vJson = tgtools.util.JsonParseHelper.getMapper(false).writeValueAsString(pData);
            pJsonNode = tgtools.util.JsonParseHelper.getMapper(false).readTree(vJson);
        } catch (Throwable e) {
            throw new APPErrorException("json 转换失败！" + pData.getClass());
        }
        if (null == pJsonNode) {
            throw new APPErrorException("输入的 JsonNod 错误！");
        }
        return parseUpdateSql(pJsonNode, pDbType,pKeys, pTableName);

    }

}

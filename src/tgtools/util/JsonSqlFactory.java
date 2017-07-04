package tgtools.util;

import tgtools.exceptions.APPErrorException;
import tgtools.json.*;

import java.util.List;

/**
 * 名  称：json转sql字符串
 * 编写者：田径
 * 功  能：
 * 时  间：9:42
 */
public class JsonSqlFactory {

    /**
     * 转换json 为 update sql语句
     * @param p_Json json对象
     * @param p_Keys 主键集合
     * @param p_TableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseUpdateSql(JSONObject p_Json, List<String> p_Keys, String p_TableName) throws APPErrorException {
        String sql = "update ${tablename} set ${values} where ${filters}";
        String values = "";
        String filters = "";
        JSONArray names = p_Json.names();
        for (int i = 0; i < names.length(); i++) {
            try {
                String key = names.getString(i);
                String value = p_Json.getString(key);
                value=p_Json.isNull(key)?"NULL":value;
                if (!StringUtil.isNullOrEmpty(key)) {
                    if (p_Keys.contains(key)) {
                        filters+= key + "='" + SqlStrHelper.escape(value) + "' and ";
                    }
                    else
                    {
                        if(p_Json.isNull(key)){values += key + "=NULL,";}else{
                        values += key + "='" + SqlStrHelper.escape(value) + "',";}
                    }
                }
            } catch (Exception e) {
                throw new APPErrorException("数据不完整");
            }
        }
        values=StringUtil.removeLast(values,',');
        filters+= " 1=1";

        sql = StringUtil.replace(sql, "${tablename}", p_TableName);
        sql = StringUtil.replace(sql, "${values}", values);
        sql = StringUtil.replace(sql, "${filters}", filters);
        return sql;
    }

    /**
     * 转换json 为 insert sql语句
     * @param p_Json json 对象
     * @param p_TableName 表名称
     * @return
     * @throws APPErrorException
     */
    public static String parseInsertSql(JSONObject p_Json, String p_TableName) throws APPErrorException {
        String sql = "insert into ${tablename} (${keys}) values(${values})";
        String keys = "";
        String values = "";
        JSONArray names = p_Json.names();
        for (int i = 0; i < names.length(); i++) {
            try {
                String key = names.getString(i);
                String value = p_Json.getString(key);
                if (!StringUtil.isNullOrEmpty(key)) {
                    keys += key + ",";
                    values +=p_Json.isNull(key)?"NULL":"'"+SqlStrHelper.escape(value) + "',";
                }
            } catch (Exception e) {
                throw new APPErrorException("数据不完整");
            }
        }
        values = StringUtil.removeLast(values, ',');
        keys = StringUtil.removeLast(keys, ',');
        sql = StringUtil.replace(sql, "${keys}", keys);
        sql = StringUtil.replace(sql, "${values}", values);
        sql = StringUtil.replace(sql, "${tablename}", p_TableName);
        return sql;
    }


}

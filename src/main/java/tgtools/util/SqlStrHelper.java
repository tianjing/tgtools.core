package tgtools.util;

import tgtools.db.DataBaseFactory;
import tgtools.exceptions.APPErrorException;

/**
 * sql字符串处理类
 * Created by tian_ on 2016-08-26.
 */
public class SqlStrHelper {

    /**
     * 处理数据库关键字
     *
     * @param sql
     * @return
     */
    public static String processKeyWord(String sql) {
        return processKeyWord(DataBaseFactory.getDefault().getDataBaseType(),sql);
    }

    /**
     * 处理数据库关键字
     * @param p_DBType 数据库类型 参看 DataBaseFactory.DBTYPE_DM7等常量
     * @param sql 待转换的 sql
     * @return 转换后的sql
     */
    public static String processKeyWord(String p_DBType, String sql) {
        if (DataBaseFactory.DBTYPE_DM6.equals(p_DBType) || DataBaseFactory.DBTYPE_DM7.equals(p_DBType)) {
            sql = StringUtil.replace(sql, "'SYSDATE'", "SYSDATE");
        }
        else if (DataBaseFactory.DBTYPE_MYSQL.equals(p_DBType)) {
            sql = StringUtil.replace(sql, "'SYSDATE'", "now()");
        }
        return sql;
    }

    /**
     * sql中字段值特殊符号转义，执行sql前对sql中字段值进行转义确保sql正确执行
     * @param p_DBType
     * @param p_ColumnValue
     * @return
     */
    public static String escape(String p_DBType,String p_ColumnValue) {
        if (DataBaseFactory.DBTYPE_DM6.equals(p_DBType) || DataBaseFactory.DBTYPE_DM7.equals(p_DBType)||DataBaseFactory.DBTYPE_MYSQL.equals(p_DBType)) {
            p_ColumnValue = tgtools.util.RegexHelper.replace(p_ColumnValue, "''", "'+");
        }
        else if(DataBaseFactory.DBTYPE_ORACLE.equals(p_DBType))
        {
            p_ColumnValue = tgtools.util.RegexHelper.replace(p_ColumnValue, "''", "chr(39)");
        }

        return p_ColumnValue;
    }
        /**
         * sql中字段值特殊符号转义，执行sql前对sql中字段值进行转义确保sql正确执行
         * 目前只支持单引号
         * @param p_ColumnValue
         * @return
         */
    public static String escape(String p_ColumnValue) {
        return escape(DataBaseFactory.getDefault().getDataBaseType(),p_ColumnValue);
    }

    //private static String m_SpecialStrReg = "(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(frame|<frame|iframe|<iframe|img|<img|JavaScript|<javascript|script|<script|alert|select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
    private static String m_SpecialStrReg = "(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(frame|<frame|iframe|<iframe|img|<img|JavaScript|<javascript|script|<script|alert|select|update|and|or|delete|insert|trancate|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * SQL字符串替换（先转义，并验证参数是否存在特殊字符，最后字符串替换）
     * @param p_SQL
     * @param p_Mark
     * @param p_Content
     * @return
     * @throws APPErrorException
     */
    public static String replace(String p_SQL, String p_Mark, String p_Content) throws APPErrorException {
        p_Content= escape(p_Content);
        validParam(p_Content);
        return StringUtil.replace(p_SQL, p_Mark, p_Content);
    }

    /**
     * 验证字符串是否含有特殊字符，如果有则抛出错误
     * @param p_Param
     * @throws APPErrorException
     */
    public static void validParam(String p_Param) throws APPErrorException {
        if(hasSpecialStrParam(p_Param))
        {
            throw new APPErrorException("参数中含有字符;请重新输入。"+p_Param);
        }
    }

    /**
     * 是否含有特殊字符
     * @param p_Param
     * @return true含有特殊字符，false不含特殊字符
     */
    public static boolean hasSpecialStrParam(String p_Param) {
        if (StringUtil.isNullOrEmpty(p_Param)) {
            return false;
        }
        return tgtools.util.RegexHelper.isMatch(p_Param, m_SpecialStrReg);
    }



    public static void main(String[] args) {
        String sql = "a='1' and b=''2''";
        System.out.println("escape:" + escape(sql));
    }
}
package tgtools.util;

import tgtools.db.DataBaseFactory;
import tgtools.exceptions.APPErrorException;

/**
 * sql字符串处理类
 * @author tianjing
 *
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
     * @param pDbType 数据库类型 参看 DataBaseFactory.DBTYPE_DM7等常量
     * @param sql 待转换的 sql
     * @return 转换后的sql
     */
    public static String processKeyWord(String pDbType, String sql) {
        if (DataBaseFactory.DBTYPE_DM6.equals(pDbType) || DataBaseFactory.DBTYPE_DM7.equals(pDbType)) {
            sql = StringUtil.replace(sql, "'SYSDATE'", "SYSDATE");
        }
        else if (DataBaseFactory.DBTYPE_MYSQL.equals(pDbType)) {
            sql = StringUtil.replace(sql, "'SYSDATE'", "now()");
        }
        return sql;
    }

    /**
     * sql中字段值特殊符号转义，执行sql前对sql中字段值进行转义确保sql正确执行
     * （只对 单个'进行转换 如果出现2个就不会处理了）
     * @param pDBType
     * @param pColumnValue
     * @return
     */
    public static String escape(String pDBType,String pColumnValue) {
        if (DataBaseFactory.DBTYPE_DM6.equals(pDBType) || DataBaseFactory.DBTYPE_DM7.equals(pDBType)||DataBaseFactory.DBTYPE_MYSQL.equals(pDBType)) {
            pColumnValue = tgtools.util.RegexHelper.replace(pColumnValue, "''", "'+");
        }
        else if(DataBaseFactory.DBTYPE_ORACLE.equals(pDBType))
        {
            pColumnValue = tgtools.util.RegexHelper.replace(pColumnValue, "''", "chr(39)");
        }

        return pColumnValue;
    }
    /**
     * sql中字段值特殊符号转义，执行sql前对sql中字段值进行转义确保sql正确执行
     * (对所有'处理为'')
     * @param pDBType
     * @param pColumnValue
     * @return
     */
    public static String escapeAll(String pDBType,String pColumnValue) {
        if (DataBaseFactory.DBTYPE_DM6.equals(pDBType) || DataBaseFactory.DBTYPE_DM7.equals(pDBType)||DataBaseFactory.DBTYPE_MYSQL.equals(pDBType)) {
            pColumnValue = tgtools.util.StringUtil.replace(pColumnValue, "'", "''");
        }
        else if(DataBaseFactory.DBTYPE_ORACLE.equals(pDBType))
        {
            pColumnValue = tgtools.util.StringUtil.replace(pColumnValue, "'", "''");
            pColumnValue = tgtools.util.RegexHelper.replace(pColumnValue, "''", "chr(39)");
        }

        return pColumnValue;
    }
        /**
         * sql中字段值特殊符号转义，执行sql前对sql中字段值进行转义确保sql正确执行
         * 目前只支持单引号
         * @param pColumnValue
         * @return
         */
    public static String escape(String pColumnValue) {
        return escape(DataBaseFactory.getDefault().getDataBaseType(),pColumnValue);
    }

    //private static String m_SpecialStrReg = "(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(frame|<frame|iframe|<iframe|img|<img|JavaScript|<javascript|script|<script|alert|select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
      private static String m_SpecialStrReg = "(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(frame|<frame|iframe|<iframe|img|<img|JavaScript|<javascript|script|<script|alert|select|update|and|or|delete|insert|trancate|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * SQL字符串替换（先转义，并验证参数是否存在特殊字符，最后字符串替换）
     * @param pSQL
     * @param pMark
     * @param pContent
     * @return
     * @throws APPErrorException
     */
    public static String replace(String pSQL, String pMark, String pContent) throws APPErrorException {
        pContent= escape(pContent);
        validParam(pContent);
        return StringUtil.replace(pSQL, pMark, pContent);
    }

    /**
     * 验证字符串是否含有特殊字符，如果有则抛出错误
     * @param pParam
     * @throws APPErrorException
     */
    public static void validParam(String pParam) throws APPErrorException {
        if(hasSpecialStrParam(pParam))
        {
            throw new APPErrorException("参数中含有字符;请重新输入。"+pParam);
        }
    }

    /**
     * 是否含有特殊字符
     * @param pParam
     * @return true含有特殊字符，false不含特殊字符
     */
    public static boolean hasSpecialStrParam(String pParam) {
        if (StringUtil.isNullOrEmpty(pParam)) {
            return false;
        }
        return tgtools.util.RegexHelper.isMatch(pParam, m_SpecialStrReg);
    }



    public static void main(String[] args) {
        String sql = "a='1' and b=''2''";
        System.out.println("escape:" + escape(sql));
    }
}
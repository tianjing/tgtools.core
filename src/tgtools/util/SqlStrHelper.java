package tgtools.util;

import tgtools.db.DataBaseFactory;

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

    public static void main(String[] args) {
        String sql = "a='1' and b=''2''";
        System.out.println("escape:" + escape(sql));
    }
}
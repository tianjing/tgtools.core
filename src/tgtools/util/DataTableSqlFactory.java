package tgtools.util;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import tgtools.data.DataColumn;
import tgtools.data.DataColumnCollection;
import tgtools.data.DataRow;
import tgtools.data.DataTable;
import tgtools.data.DbNull;
import tgtools.log.LoggerFactory;

/**
 * 将DataTable转换成sql的类
 * 
 * @author TianJing
 * 
 */
public class DataTableSqlFactory {
	private static String m_SeparateStr = ",";
	private static String m_NullValue = "null";

	/**
	 * 将DataTable 转换成Insert SQL集合
	 * 
	 * @param p_Table
	 *            数据Table
	 * @param p_TableName
	 *            表名
	 * @return
	 */
	public static List<String> buildInsertSql(DataTable p_Table,
			String p_TableName) {
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < p_Table.getRows().size(); i++) {
			String sql = buildInsertSql(p_Table.getRow(i), p_TableName);
			if (!StringUtil.isNullOrEmpty(sql)) {
				sqls.add(sql);
			}
		}
		return sqls;
	}

	/**
	 * 将一行数据转换成 Insert SQL
	 * 
	 * @param p_Row
	 *            数据行
	 * @param p_TableName
	 *            表名称
	 * @return
	 */
	public static String buildInsertSql(DataRow p_Row, String p_TableName) {
		String sql = "Insert into {table} ({column}) values({values})";

		String columns = getColumns(p_Row.getTable().getColumns());
		String values = getValues(p_Row);
		
		sql = sql.replace("{table}", p_TableName);
		sql = sql.replace("{column}", columns);
		sql = sql.replace("{values}", values);
		return sql+";";
	}

	/**
	 * 将DataTable 转换成Update SQL集合
	 * 
	 * @param p_Table
	 *            数据table
	 * @param p_TableName
	 *            表名称
	 * @return
	 */
	public static List<String> buildUpdateSql(DataTable p_Table,
			String p_TableName) {
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < p_Table.getRows().size(); i++) {
			String sql = buildUpdateSql(p_Table.getRow(i), p_TableName);
			if (!StringUtil.isNullOrEmpty(sql)) {
				sqls.add(sql);
			}
		}
		return sqls;

	}

	/**
	 * 将一行数据转换成 Update SQL
	 * 
	 * @param p_Row
	 *            数据行
	 * @param p_TableName
	 *            表名称
	 * @return
	 */
	public static String buildUpdateSql(DataRow p_Row, String p_TableName) {
		String sql = "Update {table} set {values} where {filter}";

		String values = getUpdateValues(p_Row);
		String filter = getFilter(p_Row);
		sql = sql.replace("{table}", p_TableName);
		sql = sql.replace("{filter}", filter);
		sql = sql.replace("{values}", values);
		return sql + ";";

	}
	/**
	 * 将DataTable 转换成Delete SQL集合
	 * @param p_Table
	 * @param p_TableName
	 * @return
	 */
	public static List<String> buildDeleteSql(DataTable p_Table,
			String p_TableName) {
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < p_Table.getRows().size(); i++) {
			String sql = buildDeleteSql(p_Table.getRow(i), p_TableName);
			if (!StringUtil.isNullOrEmpty(sql)) {
				sqls.add(sql);
			}
		}
		return sqls;
	}
	/**
	 * 将一行数据转换成 Delete SQL
	 * @param p_Row
	 * @param p_TableName
	 * @return
	 */
	public static String buildDeleteSql(DataRow p_Row, String p_TableName) {
		String sql = "Delete {table} where {filter}";
		String filter = getFilter(p_Row);
		if(!StringUtil.isNullOrEmpty(filter)){
		sql = sql.replace("{table}", p_TableName);
		sql = sql.replace("{filter}", filter);

		return sql + ";";
		}
		return StringUtil.EMPTY_STRING;

	}
	/**
	 * 将行数据转换成 赋值语句 （相当于 set 后面的内容）
	 * 
	 * @param p_Row
	 *            数据行
	 * @return
	 */
	private static String getUpdateValues(DataRow p_Row) {
		StringBuilder sb = new StringBuilder();
		Collection<DataColumn> columns = p_Row.getTable().getColumns().values();
		for (DataColumn item : columns) {
			if (item.isPrimaryKey()) {
				continue;
			}
			String name = item.getColumnName();
			Object value = "";

			value = vaildValue(p_Row.getValue(name), item.getColumnType());
			sb.append(name + "=" + value + m_SeparateStr);
		}
		sb = RemoveSeparateStr(sb);
		return sb.toString();
	}

	/**
	 * 将行数据转换成 条件语句 （相当于 where 后面的内容）
	 * 
	 * @param p_Row
	 *            数据行
	 * @return
	 */
	private static String getFilter(DataRow p_Row) {
		StringBuilder sb = new StringBuilder();
		Collection<DataColumn> columns = p_Row.getTable().getColumns().values();
		for (DataColumn item : columns) {
			if (item.isPrimaryKey()) {
				String name = item.getColumnName();
				String value = vaildValue(p_Row.getValue(name),
						item.getColumnType());
				sb.append(" and " + name + "=" + value);
			}
		}

		if (sb.length() > 0) {
			sb.insert(0, " 1=1");
		}

		return sb.toString();
	}

	/**
	 * 将数据列信息转换成 Insert 用的值（相当于values 前面的内容）
	 * 
	 * @param p_Columns
	 * @return
	 */
	private static String getColumns(DataColumnCollection p_Columns) {
		StringBuilder sb = new StringBuilder();
		for (DataColumn key : p_Columns.values()) {
			sb.append(key.getColumnName() + m_SeparateStr);
		}

		sb = RemoveSeparateStr(sb);
		return sb.toString();
	}

	/**
	 * 将数据行转换成 Insert 用的值 （相当于 values 后的内容）
	 * 
	 * @param p_Row
	 *            数据行
	 * @return
	 */
	private static String getValues(DataRow p_Row) {
		if (null == p_Row) {
			return "";
		}
		Collection<DataColumn> columns = p_Row.getTable().getColumns().values();
		StringBuilder sb = new StringBuilder();
		for (DataColumn item : columns) {
			sb.append(vaildValue(p_Row.getValue(item.getColumnName()),
					item.getColumnType())
					+ m_SeparateStr);
		}
		sb = RemoveSeparateStr(sb);
		return sb.toString();
	}

	/**
	 * 将数据OBJECT 转换成 有效的sql 值
	 * 
	 * @param p_Value
	 *            数据
	 * @param p_DataType
	 *            数据类型 java.sql.Types
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static String vaildValue(Object p_Value, int p_DataType) {
		if (null == p_Value || p_Value instanceof DbNull) {
			return "null";
		}
		String value = p_Value.toString();
		value = value.replace("\"", "");
		if (java.sql.Types.VARCHAR == p_DataType
				|| java.sql.Types.CHAR == p_DataType
				|| java.sql.Types.CLOB == p_DataType
				) {
			return "'" + StringUtil.replace(p_Value.toString(), "'", "''")+ "'";
		} else if (java.sql.Types.INTEGER == p_DataType
				|| java.sql.Types.BIGINT == p_DataType
				|| java.sql.Types.NUMERIC == p_DataType
				|| java.sql.Types.DECIMAL == p_DataType
				|| java.sql.Types.DOUBLE == p_DataType
				|| java.sql.Types.REAL == p_DataType
				|| java.sql.Types.FLOAT == p_DataType) {

			if (value.isEmpty())
				return m_NullValue;
			else
				return value;
		} else if (java.sql.Types.TIMESTAMP == p_DataType
				|| java.sql.Types.TIME == p_DataType
				|| java.sql.Types.DATE == p_DataType) {
			if (value.isEmpty())
				return m_NullValue;
			else
				try {
					if(p_Value instanceof java.util.Date)
					{
						return "'" + tgtools.util.DateUtil.formatFullLongtime((java.util.Date)p_Value)
								+ "'";
					}
					else{
					if(value.indexOf("-")>=0)
					{
						value=StringUtil.replace(value, "-", "/");
					}
					Date date = new Date(Date.parse(value));

					return "'" + tgtools.util.DateUtil.formatFullLongtime(date)
							+ "'";
					}
				} catch (Exception e) {
					LoggerFactory.getDefault().error("日期转换错误：" + p_Value, e);
					return m_NullValue;
				}

		}
		else if(java.sql.Types.NULL == p_DataType)
		{
			return value;
		}
		else {
			return m_NullValue;

		}
	}

	/**
	 * 如果最后一个字符是分隔符则删除，如果不是则原样输出
	 * 
	 * @param p_Str
	 * @return
	 */
	private static StringBuilder RemoveSeparateStr(StringBuilder p_Str) {
		if (p_Str.length() > 0
				&& m_SeparateStr.equals(p_Str.substring(p_Str.length() - 1))) {
			p_Str.deleteCharAt(p_Str.length() - 1);
		}
		return p_Str;
	}
	
	
	public static void main1(String[] args)
	{

		DataTable table =new DataTable();
		table.setTableName("DQ_APP.SERVICE");
		DataColumn column =table.appendColumn("NAME");
		column.setColumnType(java.sql.Types.VARCHAR);
		column.setPrimaryKey(true);
		
		DataColumn column1 =table.appendColumn("AGE");
		column1.setColumnType(java.sql.Types.INTEGER);
		
		DataColumn column2 =table.appendColumn("BDATE");
		column2.setColumnType(java.sql.Types.DATE);
		
		DataColumn column3 =table.appendColumn("MONERY");
		column3.setColumnType(java.sql.Types.DECIMAL);
		
		DataRow row= table.appendRow();
		row.setValue("NAME", "田径1");
		row.setValue("AGE", 12);
		row.setValue("BDATE", new Date(System.currentTimeMillis()));
		row.setValue("MONERY", java.math.BigDecimal.valueOf(12.232321));
		
		DataRow row1= table.appendRow();
		row1.setValue("NAME", "田径2");
		row1.setValue("AGE", 13);
		row1.setValue("BDATE", new Date(System.currentTimeMillis()));
		row1.setValue("MONERY", java.math.BigDecimal.valueOf(13.232321));
		
		List<String> inserts= buildInsertSql(table, table.getTableName());
		List<String> updates= buildUpdateSql(table, table.getTableName());
		
		System.out.println(inserts.get(0));
		System.out.println(inserts.get(1));
		
		System.out.println(updates.get(0));
		System.out.println(updates.get(1));
		
		
	}
	public static void main(String[] args)
	{
		try{
		tgtools.db.DataBaseFactory.add("DM", "jdbc:dm://192.168.1.254:5233/oms","SYSDBA","SYSDBA");
		String sql1="select top 1 * from MW_APP.MWT_UD_SB_YCSB where ccrq is not null";
		DataTable dt= tgtools.db.DataBaseFactory.getDefault().Query(sql1);
		if(DataTable.hasData(dt))
		{
		String sql= DataTableSqlFactory.buildUpdateSql(DataTable.getFirstRow(dt), "tableddd");
		System.out.println(sql);
		}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}

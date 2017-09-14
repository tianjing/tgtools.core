package tgtools.data;

import tgtools.data.mapping.AndCondition;
import tgtools.data.mapping.Condition;
import tgtools.data.mapping.EqualCondition;
import tgtools.data.mapping.Order;
import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;
import tgtools.json.JSONArray;
import tgtools.json.JSONObject;
import tgtools.util.LogHelper;
import tgtools.util.NumberUtility;
import tgtools.util.StringUtil;
import tgtools.xml.XmlSerializable;
import tgtools.xml.XmlSerializeException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

@XmlSerializable
public class DataTable implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1145393580144288826L;
    private String tableName;
    private DataColumnCollection columns;
    private ArrayList<DataColumn> columnIndexArray;
    private DataRowCollection rows;
    private String m_Sql;
    private boolean m_CaseSensitive=false;

    /**
     * 构造一个没有行和列的DataTable
     */
    public DataTable() {
        this("NewDataTable");
    }

    /**
     * 构造一个没有行和列的DataTable
     * @param p_tableName
     */
    public DataTable(String p_tableName) {
        this.tableName = p_tableName;
        this.columns = new DataColumnCollection();
        this.columnIndexArray = new ArrayList<DataColumn>();
        this.rows = new DataRowCollection();
    }

    /**
     * 构造  通过数据库查询结果添加到DataTable
     * @param p_resultSet
     * @param currentSql
     * @param rowLimit
     */
    public DataTable(ResultSet p_resultSet, String currentSql, int rowLimit) {
        this();
        ResultSetMetaData rsmd = null;
        this.m_Sql = currentSql;
        // int readedRows = 0;
        int cols;
        try {
            rsmd = p_resultSet.getMetaData();

            cols = rsmd.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                String colName = rsmd.getColumnLabel(i);

                DataColumn column = appendColumn(colName);

                column.columnType = rsmd.getColumnType(i);
                column.precision = rsmd.getPrecision(i);
                column.scale = rsmd.getScale(i);

                if ((column.columnType == 2) && (column.precision > 0)
                        && (column.scale == 0)) {
                    column.columnType = 4;
                }

                if (rsmd.isNullable(i) == 1)
                    column.nullable = true;
                else {
                    column.nullable = false;
                }
                column.caseSensitive = rsmd.isCaseSensitive(i);
            }
        } catch (Exception e) {
            throw new APPRuntimeException("获取ResultSet的表模式信息时发生异常。", e);
        }

        try {
            while (p_resultSet.next()) {
                DataRow row = appendRow();
                for (int i = 1; i <= cols; i++) {
                    String colName = rsmd.getColumnLabel(i);
                    Object value = p_resultSet.getObject(i);
                    if ((value instanceof Date)) {
                        row.setValue(colName, p_resultSet.getTimestamp(i),
                                false);
                    } else {
                        if ((value instanceof Long)) {
                            Long lv = (Long) value;
                            if (lv.longValue() == lv.intValue()) {
                                row.setValue(colName,
                                        Integer.valueOf(lv.intValue()), false);
                                continue;
                            }
                        }
                        row.setValue(colName, value, false);
                    }

                }

                // readedRows++;

            }

        } catch (Exception e) {
            throw new APPRuntimeException("获取ResultSet的数据时发生异常。", e);
        }
    }

    /**
     * 构造  通过数据库查询结果添加到DataTable
     * @param p_resultSet
     * @param currentSql
     */
    public DataTable(ResultSet p_resultSet, String currentSql) {
        this(p_resultSet, currentSql, -1);
    }

    /**
     * 判断table是否含有数据
     *
     * @param p_Table
     * @return
     */
    public static boolean hasData(DataTable p_Table) {
        return null != p_Table && p_Table.getRows().size() > 0;
    }

    /**
     * 获取表格的第一行数据，如果不存在则返回null
     *
     * @param p_Table
     * @return
     */
    public static DataRow getFirstRow(DataTable p_Table) {
        if (hasData(p_Table)) {
            return p_Table.getRow(0);
        }
        return null;
    }

    /**
     * 在table 新建一行并返回新行对象DataRow
     *
     * @param p_Table
     * @return
     */
    public static DataRow newRow(DataTable p_Table) {
        if (hasData(p_Table)) {
            return p_Table.appendRow();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws APPErrorException {
        //m_ConnStr = params[0].toString();
        //m_UserName = params[1].toString();
        //m_Password = params[2].toString();
        String sql1 = "SELECT (case when 1=2 then 0.0 else convert(DECIMAL(7,2),1) end)as res FROM DUAL ";
        String sql2 = "SELECT convert(DECIMAL(7,2),1)as res FROM DUAL";
        String sql3 = "SELECT convert(DECIMAL(7,2),52.32)as res FROM DUAL";
        String sql4 = "select REV_,ID_,KEY_,ID_ as \"id\",KEY_ as \"key\" from BQ_SYS.ACT_DATADICTIONARY";
        //tgtools.db.DataBaseFactory.add("DM", new Object[]{"jdbc:dm://192.168.88.128:5235/dqmis", "SYSDBA", "SYSDBA"});
        tgtools.db.DataBaseFactory.add("DBCP", "jdbc:h2:file:C:\\tianjing\\Desktop\\mydb;DB_CLOSE_DELAY=1000;INIT=CREATE SCHEMA IF NOT EXISTS BQ_SYS\\;SET SCHEMA BQ_SYS;", "BQ_SYS123", "BQ_SYS","org.h2.Driver");
        String sqls="WITH RECURSIVE r(ID_,PARENTID_) AS (  SELECT ID_,PARENTID_ FROM act_om_menu WHERE ID_ IN(select menu_id_  from act_om_rolemenu  where group_id_ in  (select group_id_  from act_id_membership  where user_id_ = '5DD4F0B7-C167-959F-0CEA-61AE48223A89') ) union   ALL   SELECT act_om_menu.ID_,act_om_menu.PARENTID_  FROM  act_om_menu, r WHERE act_om_menu.ID_ = r.PARENTID_  )      select ID_ AS ID ,APP_ID_ AS APPID , URL_ AS URL,PAGE_TARGET_ AS TARGET  ,(case when parentid_='0' then '' else parentid_ end) as PID ,title_ as TEXT,img_ as img , ICONPOSITION_ as iconPosition from act_om_menu where id_ in (   SELECT distinct id_ FROM r  ) order by parentid_,number_ ;";
        DataTable dt = tgtools.db.DataBaseFactory.getDefault().Query(sqls);
        dt.toJson();
        //dt.setCaseSensitive(true);
        System.out.println("JSONArray::"+ new JSONArray(dt.toJson()));
        System.out.println("JSONObject::"+ new JSONObject(dt.getRow(0).toJson()));
    }

    /**
     * 获取 TableName
     * @return
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * 设置 TableName
     * @param p_tableName
     */
    public void setTableName(String p_tableName) {
        this.tableName = p_tableName;
    }

    /**
     * 获取列的集合
     * @return
     */
    public DataColumnCollection getColumns() {
        return this.columns;
    }

    /**
     * 获取列的索引
     * @param p_columnName
     * @return
     */
    public int getColumnIndex(String p_columnName) {
        return ((DataColumn) this.columns.get(p_columnName))
                .getIndexInColList();
    }

    /**
     * 根据列名称获取列对象
     * @param p_columnName
     * @return
     */
    public DataColumn getColumn(String p_columnName) {
        DataColumn col = (DataColumn) this.columns.get(getColumnName(p_columnName));
        if (col == null) {
            throw new APPRuntimeException(String.format("数据表中不存在数据列[%1$s]。",
                    new Object[]{getColumnName(p_columnName)}));
        }

        return col;
    }

    /**
     * 是否存在列
     * @param p_columnName
     * @return
     */
    public boolean hasColumn(String p_columnName) {
        try {
            return null!=this.getColumn(p_columnName);
        }
        catch (APPRuntimeException ex)
        {
        }
        return false;
    }
        /**
         * 根据列索引获取列对象
         * @param p_columnIndex
         * @return
         */
    public DataColumn getColumn(int p_columnIndex) {
        return (DataColumn) this.columnIndexArray.get(p_columnIndex);
    }

    /**
     * 根据列索引获取列名称
     * @param p_columnIndex
     * @return
     */
    public String getColumnName(int p_columnIndex) {
        return  this.columnIndexArray.get(p_columnIndex).getColumnName();
    }

    /**
     * 是否包含同列名
     * @param p_columnName
     * @return
     */
    public boolean containsColumn(String p_columnName) {
        return this.columns.containsKey(getColumnName(p_columnName));
    }

    /**
     * 添加列
     * @param p_columnName
     * @return
     */
    public DataColumn appendColumn(String p_columnName) {
        String upname = p_columnName;//getColumnName(p_columnName);

        DataColumn column = null;
        if (!containsColumn(upname)) {
            column = new DataColumn(this, upname);
            this.columns.put(column.getColumnName(), column);
            this.columnIndexArray.add(column);
            column.setIndexInColList(this.columnIndexArray.size() - 1);
            for (int i = 0; i < this.rows.size(); i++)
                ((DataRow) this.rows.get(i)).addNullValue();
        } else {
            column = getColumn(upname);
        }
        return column;
    }

    /**
     * 添加列
     * @param p_dataColumn
     * @return
     */
    public DataColumn appendColumn(DataColumn p_dataColumn) {
        String upname = p_dataColumn.getColumnName();

        DataColumn column = null;
        if (!containsColumn(upname)) {
            column = p_dataColumn;
            this.columns.put(column.getColumnName(), column);
            this.columnIndexArray.add(column);
            column.setIndexInColList(this.columnIndexArray.size() - 1);
            for (int i = 0; i < this.rows.size(); i++)
                ((DataRow) this.rows.get(i)).addNullValue();
        } else {
            column = getColumn(upname);
        }
        return column;
    }

    /**
     * 删除列
     * @param p_columnName
     */
    public void removeColumn(String p_columnName) {
        String upname = getColumnName(p_columnName);
        int colIndex = this.columns.get(upname).getIndexInColList();
        for (int i = 0; i < this.rows.size(); i++) {
           this.rows.get(i).removeData(colIndex);
        }
        this.columnIndexArray.remove(colIndex);
        this.columns.remove(upname);
        for (int i = 0; i < this.columnIndexArray.size(); i++)
            this.columnIndexArray.get(i).setIndexInColList(i);
    }

    /**
     * 清除所有列
     */
    public void clearColumn() {
        this.columns.clear();
        this.columnIndexArray.clear();
    }

    /**
     * 获取行数
     * @return
     */
    public int getRowCount() {
        return this.getRows().size();
    }

    /**
     * 获取所有行集合
     * @return
     */
    public DataRowCollection getRows() {
        return this.rows;
    }

    /**
     * 根据索引获取行对象
     * @param p_index
     * @return
     */
    public DataRow getRow(int p_index) {
        return this.rows.get(p_index);
    }

    /**
     * 添加空行
     * @return
     */
    public DataRow appendRow() {
        DataRow row = new DataRow(this);

        for (DataColumn column : getColumns().values()) {
            if (column.getDefaultValue() != null) {
                row.setValue(column.getIndexInColList(),
                        column.getDefaultValue(), true);
            }
        }

        this.rows.add(row);
        return row;
    }

    /**
     * 删除一行
     * @param p_rowIndex
     */
    public void removeRow(int p_rowIndex) {
        this.rows.remove(p_rowIndex);
    }

    /**
     * 清除所有行
     */
    public void clearRow() {
        this.rows.clear();
    }

    /**
     * 清除所有行和列
     */
    public void clear() {
        clearColumn();
        clearRow();
    }

    /**
     * 设置 是否区分大小写
     * @param p_CaseSensitive
     */
    public void setCaseSensitive(boolean p_CaseSensitive)
    {
        m_CaseSensitive=true;
        this.columns.clear();
        for(int i=0,count =this.columnIndexArray.size();i<count;i++)
        {
            this.columnIndexArray.get(i).setCaseSensitive(p_CaseSensitive);
            this.columns.put(this.columnIndexArray.get(i).getColumnName(),this.columnIndexArray.get(i));
        }
    }

    /**
     * 是否区分大小写
     */
    public boolean isCaseSensitive()
    {
        return m_CaseSensitive;
    }

    /**
     * 根据是否区分大小写转换列名
     * @param p_ColumnName
     * @return
     */
    String getColumnName(String p_ColumnName)
    {
        if(m_CaseSensitive)
        {
            return p_ColumnName;
        }
        return p_ColumnName.toUpperCase();
    }

    /**
     * 修改列名称
     * @param p_OldName
     * @param p_NewName
     * @throws APPErrorException
     */
    public void changeColumnName(String p_OldName,String p_NewName) throws APPErrorException {
        DataColumn column= this.getColumn(p_OldName);
        if(null==column)
        {
            throw new APPErrorException("没有找列："+p_OldName);
        }
        columns.remove(p_OldName);
        column.setColumnName(p_NewName);
        columns.put(column.getColumnName(),column);
    }

    /**
     * 根据条件筛选数据
     * @param p_condition 条件
     * @param p_orders 排序
     * @return
     */
    public DataRowCollection select(Condition p_condition, Order[] p_orders) {
        LinkedList<DataRow> rows = new LinkedList<DataRow>();
        for (DataRow matchRow : getRows()) {
            if ((p_condition == null) || (p_condition.isValid(matchRow))) {
                if ((p_orders == null) || (p_orders.length == 0)) {
                    rows.add(matchRow);
                } else {
                    boolean inserted = false;
                    for (int i = 0; i < rows.size(); i++) {
                        int result = 0;
                        for (Order order : p_orders) {
                            result = order.compare(matchRow,
                                    rows.get(i));
                            if (result < 0) {
                                rows.add(i, matchRow);
                                inserted = true;
                            } else {
                                if ((result != 0) && (result > 0)) {
                                    break;
                                }
                            }
                        }
                        if (inserted)
                            break;
                        if (result == 0) {
                            if (i == rows.size() - 1) {
                                rows.add(matchRow);
                            } else
                                rows.add(i + 1, matchRow);

                            inserted = true;
                            break;
                        }
                    }

                    if (!inserted)
                        rows.add(rows.size(), matchRow);
                }
            }
        }
        DataRowCollection rowCollection = new DataRowCollection();
        rowCollection.addAll(rows);
        return rowCollection;
    }
    /**
     * 根据条件筛选数据
     * @param p_condition 条件
     * @return
     */
    public DataRowCollection select(Condition p_condition) {
        return select(p_condition, new Order[0]);
    }
    /**
     * 根据条件和列名获取最大值
     * @param p_condition 条件
     * @return
     */
    public Object max(String p_columnName, Condition p_condition) {
        if (!NumberUtility.isNumberDbType(getColumn(p_columnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 MAX 不支持非数值类型的字段 "
                    + p_columnName);
        }
        Object result = null;
        for (DataRow matchRow : getRows())
            if ((p_condition == null) || (p_condition.isValid(matchRow))) {
                Object columnValue = matchRow.getValue(p_columnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if ((result == null)
                            || (Double.valueOf(result.toString()).doubleValue() < temp)) {
                        result = Double.valueOf(temp);
                    }
                }
            }
        if (result != null) {
            return NumberUtility.toInteger((Double) result);
        }
        return result;
    }

    /**
     * 找到行的索引
     * @param p_Row
     * @return
     */
    public int indexOfRow(DataRow p_Row)
    {
        return rows.indexOf(p_Row);
    }
    /**
     * 根据条件和列名获取最小值
     * @param p_condition 条件
     * @return
     */
    public Object min(String p_columnName, Condition p_condition) {
        if (!NumberUtility.isNumberDbType(getColumn(p_columnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 MIN 不支持非数值类型的字段 "
                    + p_columnName);
        }
        Object result = null;
        for (DataRow matchRow : getRows())
            if ((p_condition == null) || (p_condition.isValid(matchRow))) {
                Object columnValue = matchRow.getValue(p_columnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if ((result == null)
                            || (Double.valueOf(result.toString()).doubleValue() > temp)) {
                        result = Double.valueOf(temp);
                    }
                }
            }
        if (result != null) {
            return NumberUtility.toInteger((Double) result);
        }
        return result;
    }

    /**
     * 根据条件和列名求和
     * @param p_columnName
     * @param p_condition
     * @return
     */
    public Object sum(String p_columnName, Condition p_condition) {
        if (!NumberUtility.isNumberDbType(getColumn(p_columnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 SUM 不支持非数值类型的字段 "
                    + p_columnName);
        }
        Object result = null;
        for (DataRow matchRow : getRows()) {
            if ((p_condition == null) || (p_condition.isValid(matchRow))) {
                Object columnValue = matchRow.getValue(p_columnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if (result == null)
                        result = Double.valueOf(temp);
                    else
                        result = Double.valueOf(Double.valueOf(
                                result.toString()).doubleValue()
                                + temp);
                }
            }
        }
        if (result != null) {
            return NumberUtility.toInteger((Double) result);
        }
        return result;
    }
    /**
     * 根据条件和列名求平均数
     * @param p_columnName
     * @param p_condition
     * @return
     */
    public Object avg(String p_columnName, Condition p_condition) {
        if (!NumberUtility.isNumberDbType(getColumn(p_columnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 AVG 不支持非数值类型的字段 "
                    + p_columnName);
        }
        Double result = null;
        int count = 0;
        for (DataRow matchRow : getRows()) {
            if ((p_condition == null) || (p_condition.isValid(matchRow))) {
                count++;
                Object columnValue = matchRow.getValue(p_columnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if (result == null)
                        result = Double.valueOf(temp);
                    else
                        result = Double.valueOf(Double.valueOf(
                                result.toString()).doubleValue()
                                + temp);
                }
            }
        }
        if (count > 0)
            result = Double.valueOf(Double.valueOf(result.toString())
                    .doubleValue() / count);
        if (result != null) {
            return NumberUtility.toInteger(result);
        }
        return result;
    }

    /**
     * 克隆一个新的DataTable只保留列，没有行数据
     * @return
     */
    public DataTable cloneTableStructure() {
        DataTable newTable = new DataTable();
        for (int i = 0; i < this.columnIndexArray.size(); i++) {
            DataColumn col = (DataColumn) this.columnIndexArray.get(i);

            DataColumn dc = newTable.appendColumn(col.getColumnName());

            dc.caption = col.caption;
            dc.caseSensitive = col.caseSensitive;
            dc.columnType = col.columnType;
            dc.defaultValue = col.defaultValue;
            dc.expression = col.expression;
            dc.nullable = col.nullable;
            dc.precision = col.precision;
            dc.primaryKey = col.primaryKey;
            dc.readOnly = col.readOnly;
            dc.scale = col.scale;
            dc.getExtendedProperties().putAll(col.getExtendedProperties());
        }
        return newTable;
    }
    /**
     * 克隆一个新的DataTable 包含列和行
     * @return
     */
    public DataTable clone() {
        DataTable temp = this.cloneTableStructure();
        DataRowCollection rows = this.getRows();
        for (int i = 0; i < rows.size(); i++) {
            DataRow row = temp.appendRow();
            row.copyData(rows.get(i));
        }
        return temp;
    }

    /**
     * 添加一个空行
     * @param row
     */
    public void appendRow(DataRow row) {
        row.table = this;
        this.rows.add(row);
    }

    /**
     * 转换成json格式
     *
     * @param p_IgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public String toJson(boolean p_IgnoreNull) {
        return toJson(p_IgnoreNull, false);
    }
    /**
     * 转换成json格式
     *
     * @param p_IgnoreNull 为true时null为空字符串。为false时返回null
     * @param p_UseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public String toJson(boolean p_IgnoreNull, boolean p_UseLower) {
         return this.getRows().toJson(p_IgnoreNull,p_UseLower);
    }

    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public String toJson() {
        return toJson(true);
    }



    public void readXml(XMLStreamReader p_reader) {
        try {
            if (!p_reader.getLocalName().equalsIgnoreCase("DataTable")) {
                throw new XmlSerializeException("无法反序列化 DataTable 对象，当前 XMLStreamReader 的游标位置有误。");
            }

            for (int i = 0; i < p_reader.getAttributeCount(); i++) {
                String attrName = p_reader.getAttributeName(i).toString();
                String attrValue = p_reader.getAttributeValue(i);
                if (attrName.equalsIgnoreCase("tableName")) {
                    setTableName(attrValue);
                }
            }
            p_reader.nextTag();
            if (p_reader.getLocalName().equalsIgnoreCase("Columns")) {
                while (p_reader.hasNext()) {
                    p_reader.nextTag();
                    if (p_reader.isEndElement())
                        break;
                    if (p_reader.isStartElement()) {
                        String columnName = "";
                        for (int i = 0; i < p_reader.getAttributeCount(); i++) {
                            String attrName = p_reader.getAttributeName(i).toString();

                            if (attrName.equalsIgnoreCase("columnName")) {
                                columnName = p_reader.getAttributeValue(i);
                                break;
                            }
                        }
                        DataColumn column = appendColumn(columnName);
                        column.readXml(p_reader);
                    }
                }
            }
            p_reader.nextTag();

            if (p_reader.getLocalName().equalsIgnoreCase("Rows")) {
                while (p_reader.hasNext()) {
                    p_reader.nextTag();
                    if (p_reader.isEndElement())
                        break;
                    if (p_reader.isStartElement()) {
                        DataRow row = appendRow();
                        row.readXml(p_reader);
                    }
                }
            }
            p_reader.nextTag();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("DataTable 反序列化时发生异常。", e);
        }
    }

    public void writeXml(XMLStreamWriter p_writer) {
        writeXmlImpl(p_writer, true);
    }

    public void writeXmlImpl(XMLStreamWriter p_writer, boolean p_needMoreColInfo) {
        try {
            p_writer.writeStartElement("DataTable");
            p_writer.writeAttribute("tableName", getTableName());

            p_writer.writeStartElement("Columns");
            for (int i = 0; i < getColumns().size(); i++) {
                getColumn(i).writeXmlImpl(p_writer, p_needMoreColInfo);
            }

            p_writer.writeEndElement();

            p_writer.writeStartElement("Rows");
            for (DataRow row : getRows()) {
                row.writeXml(p_writer);
            }
            p_writer.writeEndElement();

            p_writer.writeEndElement();
        } catch (Exception e) {
            throw new XmlSerializeException("DataTable 序列化为 Xml 时发生异常。", e);
        }
    }

}

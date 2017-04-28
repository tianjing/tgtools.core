package tgtools.data;

import tgtools.data.mapping.AndCondition;
import tgtools.data.mapping.Condition;
import tgtools.data.mapping.EqualCondition;
import tgtools.data.mapping.Order;
import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;
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

    public DataTable() {
        this("NewDataTable");
    }

    public DataTable(String p_tableName) {
        this.tableName = p_tableName;
        this.columns = new DataColumnCollection();
        this.columnIndexArray = new ArrayList<DataColumn>();
        this.rows = new DataRowCollection();
    }

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
                String colName = rsmd.getColumnName(i);

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
                    String colName = rsmd.getColumnName(i);
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
        String sql4 = "select * from BQ_SYS.ACT_DATADICTIONARY";
        tgtools.db.DataBaseFactory.add("DM", new Object[]{"jdbc:dm://192.168.88.128:5235/dqmis", "SYSDBA", "SYSDBA"});
        DataTable dt = tgtools.db.DataBaseFactory.getDefault().Query(sql4);
        EqualCondition var3 = new EqualCondition("MAPTYPE", "系统配置", Types.VARCHAR | Types.NVARCHAR);
        EqualCondition var5 = new EqualCondition("KEY_", "系统名称", Types.VARCHAR | Types.NVARCHAR);
        dt.getColumn("ID_").setColumnName("id");
        dt.toJson();
        AndCondition var2 = new AndCondition();
        var2.add(var3);
        var2.add(var5);

        tgtools.data.DataRowCollection rows = dt.select(var2);
        System.out.println("rows.size:" + rows.size());
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String p_tableName) {
        this.tableName = p_tableName;
    }

    public DataColumnCollection getColumns() {
        return this.columns;
    }

    public int getColumnIndex(String p_columnName) {
        return ((DataColumn) this.columns.get(p_columnName))
                .getIndexInColList();
    }

    public DataColumn getColumn(String p_columnName) {
        DataColumn col = (DataColumn) this.columns.get(p_columnName
                .toUpperCase());
        if (col == null) {
            throw new APPRuntimeException(String.format("数据表中不存在数据列[%1$s]。",
                    new Object[]{p_columnName.toUpperCase()}));
        }

        return col;
    }

    public DataColumn getColumn(int p_columnIndex) {
        return (DataColumn) this.columnIndexArray.get(p_columnIndex);
    }

    public String getColumnName(int p_columnIndex) {
        return ((DataColumn) this.columnIndexArray.get(p_columnIndex))
                .getColumnName();
    }

    public boolean containsColumn(String p_columnName) {
        return this.columns.containsKey(p_columnName.toUpperCase());
    }

    public DataColumn appendColumn(String p_columnName) {
        String upname = p_columnName.toUpperCase();

        DataColumn column = null;
        if (!containsColumn(upname)) {
            column = new DataColumn(this, upname);
            this.columns.put(upname, column);
            this.columnIndexArray.add(column);
            column.setIndexInColList(this.columnIndexArray.size() - 1);
            for (int i = 0; i < this.rows.size(); i++)
                ((DataRow) this.rows.get(i)).addNullValue();
        } else {
            column = getColumn(upname);
        }
        return column;
    }

    public DataColumn appendColumn(DataColumn p_dataColumn) {
        String upname = p_dataColumn.getColumnName().toUpperCase();

        DataColumn column = null;
        if (!containsColumn(upname)) {
            column = p_dataColumn;
            this.columns.put(upname, column);
            this.columnIndexArray.add(column);
            column.setIndexInColList(this.columnIndexArray.size() - 1);
            for (int i = 0; i < this.rows.size(); i++)
                ((DataRow) this.rows.get(i)).addNullValue();
        } else {
            column = getColumn(upname);
        }
        return column;
    }

    public void removeColumn(String p_columnName) {
        String upname = p_columnName.toUpperCase();
        int colIndex = ((DataColumn) this.columns.get(upname))
                .getIndexInColList();
        for (int i = 0; i < this.rows.size(); i++) {
            ((DataRow) this.rows.get(i)).removeData(colIndex);
        }
        this.columnIndexArray.remove(colIndex);
        this.columns.remove(upname);
        for (int i = 0; i < this.columnIndexArray.size(); i++)
            ((DataColumn) this.columnIndexArray.get(i)).setIndexInColList(i);
    }

    public void clearColumn() {
        this.columns.clear();
        this.columnIndexArray.clear();
    }

    public int getRowCount() {
        return this.getRows().size();
    }

    public DataRowCollection getRows() {
        return this.rows;
    }

    public DataRow getRow(int p_index) {
        return (DataRow) this.rows.get(p_index);
    }

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

    public void removeRow(int p_rowIndex) {
        this.rows.remove(p_rowIndex);
    }

    public void clearRow() {
        this.rows.clear();
    }

    public void clear() {
        clearColumn();
        clearRow();
    }

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
                                    (DataRow) rows.get(i));
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

    public DataRowCollection select(Condition p_condition) {
        return select(p_condition, new Order[0]);
    }

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

    public Object avg(String p_columnName, Condition p_condition) {
        if (!NumberUtility.isNumberDbType(getColumn(p_columnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 AVG 不支持非数值类型的字段 "
                    + p_columnName);
        }
        Object result = null;
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
            return NumberUtility.toInteger((Double) result);
        }
        return result;
    }

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

    public DataTable clone() {
        DataTable temp = this.cloneTableStructure();
        DataRowCollection rows = this.getRows();
        for (int i = 0; i < rows.size(); i++) {
            DataRow row = temp.appendRow();
            row.copyData(rows.get(i));
        }
        return temp;
    }

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
        StringBuilder sb = new StringBuilder();
        int count = this.rows.size();
        for (int i = 0; i < count; i++) {
            DataRow row = this.rows.get(i);
            sb.append("{");
            int ide = 0;
            for (Map.Entry<String, DataColumn> column : this.columns.entrySet()) {
                int datatype = column.getValue().getColumnType();
                if (datatype == java.sql.Types.BLOB) {
                    continue;
                }
                String name = column.getValue().getColumnName();
                Object value = row.getValue(column.getKey());
                try {
                    if (p_UseLower) {
                        name = name.toLowerCase();
                    }
                    if (ide > 0) {

                        sb.append(",\"" + name + "\":"
                                + getJsonValue(value, datatype, p_IgnoreNull));
                    } else {
                        sb.append("\"" + name + "\":"
                                + getJsonValue(value, datatype, p_IgnoreNull));
                    }
                } catch (Exception e) {
                    LogHelper.error("", "第" + String.valueOf(i) + "行，第" + String.valueOf(ide + 1) + "列，列名：" + name + "类型：" + String.valueOf(datatype) + "出现错误！sql:" + this.m_Sql, "table.tojson", e);
                }
                ide++;
            }
            if (i == count - 1) {
                sb.append("}");
            } else {
                sb.append("},");
            }
        }
        return "[" + sb.toString() + "]";
    }

    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public String toJson() {
        return toJson(true);
    }

    private String getJsonValue(Object p_Value, int p_ValueType, boolean p_IgnoreNull) {
        if (p_Value == null || p_Value instanceof DbNull) {
            if (p_IgnoreNull) {
                return "\"\"";
            } else
                return "null";
        }
        switch (p_ValueType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.INTEGER:
            case Types.BOOLEAN:
                return p_Value.toString();

            default:
                return "\"" + StringUtil.convertJson(p_Value.toString()) + "\"";

        }
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

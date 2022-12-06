package tgtools.data;

import com.fasterxml.jackson.databind.node.ArrayNode;
import tgtools.data.mapping.Condition;
import tgtools.data.mapping.Order;
import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;
import tgtools.json.JSONArray;
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
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author tianjing
 */
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
    private boolean m_CaseSensitive = false;
    private boolean m_BolbUseStream = false;

    /**
     * 构造一个没有行和列的DataTable
     */
    public DataTable() {
        this("NewDataTable");
    }

    /**
     * 构造一个没有行和列的DataTable
     *
     * @param pTableName
     */
    public DataTable(String pTableName) {
        this.tableName = pTableName;
        this.columns = new DataColumnCollection();
        this.columnIndexArray = new ArrayList<DataColumn>();
        this.rows = new DataRowCollection();
    }

    /**
     * 构造  通过数据库查询结果添加到DataTable
     *
     * @param pResultSet
     * @param currentSql
     * @param rowLimit
     */
    public DataTable(ResultSet pResultSet, String currentSql, int rowLimit, boolean pBolbUseStream) {
        this();
        m_BolbUseStream = pBolbUseStream;
        ResultSetMetaData rsmd = null;
        this.m_Sql = currentSql;
        // int readedRows = 0;
        int cols;
        try {
            rsmd = pResultSet.getMetaData();

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

                if (rsmd.isNullable(i) == 1) {
                    column.nullable = true;
                } else {
                    column.nullable = false;
                }
                column.caseSensitive = rsmd.isCaseSensitive(i);
            }
        } catch (Exception e) {
            throw new APPRuntimeException("获取ResultSet的表模式信息时发生异常。", e);
        }

        try {
            while (pResultSet.next()) {
                DataRow row = appendRow();
                for (int i = 1; i <= cols; i++) {
                    String colName = rsmd.getColumnLabel(i);
                    Object value = pResultSet.getObject(i);
                    if ((value instanceof Date)) {
                        row.setValue(colName, pResultSet.getTimestamp(i),
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
     *
     * @param pResultSet
     * @param currentSql
     * @param pBolbUseStream 大字段是否使用流
     */
    public DataTable(ResultSet pResultSet, String currentSql, boolean pBolbUseStream) {
        this(pResultSet, currentSql, -1, pBolbUseStream);
    }

    /**
     * 构造  通过数据库查询结果添加到DataTable
     *
     * @param pResultSet
     * @param currentSql
     */
    public DataTable(ResultSet pResultSet, String currentSql) {
        this(pResultSet, currentSql, -1, false);
    }

    /**
     * 判断table是否含有数据
     *
     * @param pTable
     * @return
     */
    public static boolean hasData(DataTable pTable) {
        return null != pTable && pTable.getRows().size() > 0;
    }

    /**
     * 获取表格的第一行数据，如果不存在则返回null
     *
     * @param pTable
     * @return
     */
    public static DataRow getFirstRow(DataTable pTable) {
        if (hasData(pTable)) {
            return pTable.getRow(0);
        }
        return null;
    }

    /**
     * 在table 新建一行并返回新行对象DataRow
     *
     * @param pTable
     * @return
     */
    public static DataRow newRow(DataTable pTable) {
        if (hasData(pTable)) {
            return pTable.appendRow();
        }
        return null;
    }


    /**
     * 获取 TableName
     *
     * @return
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * 设置 TableName
     *
     * @param pTableName
     */
    public void setTableName(String pTableName) {
        this.tableName = pTableName;
    }

    /**
     * 获取列的集合
     *
     * @return
     */
    public DataColumnCollection getColumns() {
        return this.columns;
    }

    /**
     * 获取列的索引
     *
     * @param pColumnName
     * @return
     */
    public int getColumnIndex(String pColumnName) {
        return ((DataColumn) this.columns.get(pColumnName))
                .getIndexInColList();
    }

    /**
     * 根据列名称获取列对象
     *
     * @param pColumnName
     * @return
     */
    public DataColumn getColumn(String pColumnName) {
        DataColumn col = (DataColumn) this.columns.get(getColumnName(pColumnName));
        if (col == null) {
            throw new APPRuntimeException(String.format("数据表中不存在数据列[%1$s]。",
                    new Object[]{getColumnName(pColumnName)}));
        }

        return col;
    }

    /**
     * 是否存在列
     *
     * @param pColumnName
     * @return
     */
    public boolean hasColumn(String pColumnName) {
        try {
            return null != this.getColumn(pColumnName);
        } catch (APPRuntimeException ex) {
        }
        return false;
    }

    /**
     * 根据列索引获取列对象
     *
     * @param pColumnIndex
     * @return
     */
    public DataColumn getColumn(int pColumnIndex) {
        return (DataColumn) this.columnIndexArray.get(pColumnIndex);
    }

    /**
     * 根据列索引获取列名称
     *
     * @param pColumnIndex
     * @return
     */
    public String getColumnName(int pColumnIndex) {
        return this.columnIndexArray.get(pColumnIndex).getColumnName();
    }

    /**
     * 是否包含同列名
     *
     * @param pColumnName
     * @return
     */
    public boolean containsColumn(String pColumnName) {
        return this.columns.containsKey(getColumnName(pColumnName));
    }

    /**
     * 添加列
     *
     * @param pColumnName
     * @return
     */
    public DataColumn appendColumn(String pColumnName) {
        String upname = pColumnName;

        DataColumn column = null;
        if (!containsColumn(upname)) {
            column = new DataColumn(this, upname);
            this.columns.put(column.getColumnName(), column);
            this.columnIndexArray.add(column);
            column.setIndexInColList(this.columnIndexArray.size() - 1);
            for (int i = 0; i < this.rows.size(); i++) {
                ((DataRow) this.rows.get(i)).addNullValue();
            }
        } else {
            column = getColumn(upname);
        }
        return column;
    }

    /**
     * 添加列
     *
     * @param pDataColumn
     * @return
     */
    public DataColumn appendColumn(DataColumn pDataColumn) {
        String upname = pDataColumn.getColumnName();

        DataColumn column = null;
        if (!containsColumn(upname)) {
            column = pDataColumn;
            this.columns.put(column.getColumnName(), column);
            this.columnIndexArray.add(column);
            column.setIndexInColList(this.columnIndexArray.size() - 1);
            for (int i = 0; i < this.rows.size(); i++) {
                ((DataRow) this.rows.get(i)).addNullValue();
            }
        } else {
            column = getColumn(upname);
        }
        return column;
    }

    /**
     * 删除列
     *
     * @param pColumnName
     */
    public void removeColumn(String pColumnName) {
        String upname = getColumnName(pColumnName);
        int colIndex = this.columns.get(upname).getIndexInColList();
        for (int i = 0; i < this.rows.size(); i++) {
            this.rows.get(i).removeData(colIndex);
        }
        this.columnIndexArray.remove(colIndex);
        this.columns.remove(upname);
        for (int i = 0; i < this.columnIndexArray.size(); i++) {
            this.columnIndexArray.get(i).setIndexInColList(i);
        }
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
     *
     * @return
     */
    public int getRowCount() {
        return this.getRows().size();
    }

    /**
     * 获取所有行集合
     *
     * @return
     */
    public DataRowCollection getRows() {
        return this.rows;
    }

    /**
     * 根据索引获取行对象
     *
     * @param pIndex
     * @return
     */
    public DataRow getRow(int pIndex) {
        return this.rows.get(pIndex);
    }

    /**
     * 添加空行
     *
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
     *
     * @param pRowIndex
     */
    public void removeRow(int pRowIndex) {
        this.rows.remove(pRowIndex);
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

    public boolean getBolbUseStream() {
        return m_BolbUseStream;
    }

    /**
     * 是否区分大小写
     */
    public boolean isCaseSensitive() {
        return m_CaseSensitive;
    }

    /**
     * 设置 是否区分大小写
     *
     * @param pCaseSensitive
     */
    public void setCaseSensitive(boolean pCaseSensitive) {
        m_CaseSensitive = true;
        this.columns.clear();
        for (int i = 0, count = this.columnIndexArray.size(); i < count; i++) {
            this.columnIndexArray.get(i).setCaseSensitive(pCaseSensitive);
            this.columns.put(this.columnIndexArray.get(i).getColumnName(), this.columnIndexArray.get(i));
        }
    }

    /**
     * 根据是否区分大小写转换列名
     *
     * @param pColumnName
     * @return
     */
    String getColumnName(String pColumnName) {
        if (m_CaseSensitive) {
            return pColumnName;
        }
        return pColumnName.toUpperCase();
    }

    /**
     * 修改列名称
     *
     * @param pOldName
     * @param pNewName
     * @throws APPErrorException
     */
    public void changeColumnName(String pOldName, String pNewName) throws APPErrorException {
        DataColumn column = this.getColumn(pOldName);
        if (null == column) {
            throw new APPErrorException("没有找列：" + pOldName);
        }
        columns.remove(pOldName);
        column.setColumnName(pNewName);
        columns.put(column.getColumnName(), column);
    }

    /**
     * 根据条件筛选数据
     *
     * @param pCondition 条件
     * @param p_orders   排序
     * @return
     */
    public DataRowCollection select(Condition pCondition, Order[] p_orders) {
        LinkedList<DataRow> rows = new LinkedList<DataRow>();
        for (DataRow matchRow : getRows()) {
            if ((pCondition == null) || (pCondition.isValid(matchRow))) {
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
                        if (inserted) {
                            break;
                        }
                        if (result == 0) {
                            if (i == rows.size() - 1) {
                                rows.add(matchRow);
                            } else {
                                rows.add(i + 1, matchRow);
                            }

                            inserted = true;
                            break;
                        }
                    }

                    if (!inserted) {
                        rows.add(rows.size(), matchRow);
                    }
                }
            }
        }
        DataRowCollection rowCollection = new DataRowCollection();
        rowCollection.addAll(rows);
        return rowCollection;
    }

    /**
     * 根据条件筛选数据
     *
     * @param pCondition 条件
     * @return
     */
    public DataRowCollection select(Condition pCondition) {
        return select(pCondition, new Order[0]);
    }

    /**
     * 根据条件和列名获取最大值
     *
     * @param pCondition 条件
     * @return
     */
    public Object max(String pColumnName, Condition pCondition) {
        if (!NumberUtility.isNumberDbType(getColumn(pColumnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 MAX 不支持非数值类型的字段 "
                    + pColumnName);
        }
        Object result = null;
        for (DataRow matchRow : getRows()) {
            if ((pCondition == null) || (pCondition.isValid(matchRow))) {
                Object columnValue = matchRow.getValue(pColumnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if ((result == null)
                            || (Double.valueOf(result.toString()).doubleValue() < temp)) {
                        result = Double.valueOf(temp);
                    }
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
     *
     * @param pRow
     * @return
     */
    public int indexOfRow(DataRow pRow) {
        return rows.indexOf(pRow);
    }

    /**
     * 根据条件和列名获取最小值
     *
     * @param pCondition 条件
     * @return
     */
    public Object min(String pColumnName, Condition pCondition) {
        if (!NumberUtility.isNumberDbType(getColumn(pColumnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 MIN 不支持非数值类型的字段 "
                    + pColumnName);
        }
        Object result = null;
        for (DataRow matchRow : getRows()) {
            if ((pCondition == null) || (pCondition.isValid(matchRow))) {
                Object columnValue = matchRow.getValue(pColumnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if ((result == null)
                            || (Double.valueOf(result.toString()).doubleValue() > temp)) {
                        result = Double.valueOf(temp);
                    }
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
     *
     * @param pColumnName
     * @param pCondition
     * @return
     */
    public Object sum(String pColumnName, Condition pCondition) {
        if (!NumberUtility.isNumberDbType(getColumn(pColumnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 SUM 不支持非数值类型的字段 "
                    + pColumnName);
        }
        Object result = null;
        for (DataRow matchRow : getRows()) {
            if ((pCondition == null) || (pCondition.isValid(matchRow))) {
                Object columnValue = matchRow.getValue(pColumnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if (result == null) {
                        result = Double.valueOf(temp);
                    } else {
                        result = Double.valueOf(Double.valueOf(
                                result.toString()).doubleValue()
                                + temp);
                    }
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
     *
     * @param pColumnName
     * @param pCondition
     * @return
     */
    public Object avg(String pColumnName, Condition pCondition) {
        if (!NumberUtility.isNumberDbType(getColumn(pColumnName)
                .getColumnType())) {
            throw new DataAccessException("聚合函数 AVG 不支持非数值类型的字段 "
                    + pColumnName);
        }
        Double result = null;
        int count = 0;
        for (DataRow matchRow : getRows()) {
            if ((pCondition == null) || (pCondition.isValid(matchRow))) {
                count++;
                Object columnValue = matchRow.getValue(pColumnName);
                if (columnValue != null) {
                    double temp = Double.valueOf(columnValue.toString())
                            .doubleValue();
                    if (result == null) {
                        result = Double.valueOf(temp);
                    } else {
                        result = Double.valueOf(Double.valueOf(
                                result.toString()).doubleValue()
                                + temp);
                    }
                }
            }
        }
        if (count > 0) {
            result = Double.valueOf(Double.valueOf(result.toString())
                    .doubleValue() / count);
        }
        if (result != null) {
            return NumberUtility.toInteger(result);
        }
        return result;
    }

    /**
     * 克隆一个新的DataTable只保留列，没有行数据
     *
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
     *
     * @return
     */
    @Override
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
     *
     * @param row
     */
    public void appendRow(DataRow row) {
        row.table = this;
        this.rows.add(row);
    }

    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public String toJson(boolean pIgnoreNull) {
        return toJson(pIgnoreNull, false);
    }

    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public String toJson(boolean pIgnoreNull, boolean pUseLower) {
        return this.getRows().toJson(pIgnoreNull, pUseLower);
    }

    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public String toJson() {
        return toJson(true);
    }


    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public JSONArray toJSONArray(boolean pIgnoreNull, boolean pUseLower) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < this.getRowCount(); i++) {
            array.put(this.getRow(i).toJSONObject(pIgnoreNull, pUseLower));
        }
        return array;
    }

    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public JSONArray toJSONArray(boolean pIgnoreNull) {
        return toJSONArray(pIgnoreNull, false);
    }

    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public JSONArray toJSONArray() {
        return toJSONArray(true);
    }


    public void readXml(XMLStreamReader pReader) {
        try {
            if (!StringUtil.equalsIgnoreCase(pReader.getLocalName(), "DataTable")) {
                throw new XmlSerializeException("无法反序列化 DataTable 对象，当前 XMLStreamReader 的游标位置有误。");
            }

            for (int i = 0; i < pReader.getAttributeCount(); i++) {
                String attrName = pReader.getAttributeName(i).toString();
                String attrValue = pReader.getAttributeValue(i);
                if (StringUtil.equalsIgnoreCase(attrName, "tableName")) {
                    setTableName(attrValue);
                }
            }
            pReader.nextTag();
            if (StringUtil.equalsIgnoreCase(pReader.getLocalName(), "Columns")) {
                while (pReader.hasNext()) {
                    pReader.nextTag();
                    if (pReader.isEndElement()) {
                        break;
                    }
                    if (pReader.isStartElement()) {
                        String columnName = "";
                        for (int i = 0; i < pReader.getAttributeCount(); i++) {
                            String attrName = pReader.getAttributeName(i).toString();

                            if (StringUtil.equalsIgnoreCase(attrName, "columnName")) {
                                columnName = pReader.getAttributeValue(i);
                                break;
                            }
                        }
                        DataColumn column = appendColumn(columnName);
                        column.readXml(pReader);
                    }
                }
            }
            pReader.nextTag();

            if (StringUtil.equalsIgnoreCase(pReader.getLocalName(), "Rows")) {
                while (pReader.hasNext()) {
                    pReader.nextTag();
                    if (pReader.isEndElement()) {
                        break;
                    }
                    if (pReader.isStartElement()) {
                        DataRow row = appendRow();
                        row.readXml(pReader);
                    }
                }
            }
            pReader.nextTag();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("DataTable 反序列化时发生异常。", e);
        }
    }

    public void writeXml(XMLStreamWriter pWriter) {
        writeXmlImpl(pWriter, true);
    }

    public void writeXmlImpl(XMLStreamWriter pWriter, boolean pNeedMoreColInfo) {
        try {
            pWriter.writeStartElement("DataTable");
            pWriter.writeAttribute("tableName", getTableName());

            pWriter.writeStartElement("Columns");
            for (int i = 0; i < getColumns().size(); i++) {
                getColumn(i).writeXmlImpl(pWriter, pNeedMoreColInfo);
            }

            pWriter.writeEndElement();

            pWriter.writeStartElement("Rows");
            for (DataRow row : getRows()) {
                row.writeXml(pWriter);
            }
            pWriter.writeEndElement();

            pWriter.writeEndElement();
        } catch (Exception e) {
            throw new XmlSerializeException("DataTable 序列化为 Xml 时发生异常。", e);
        }
    }


    /**
     * 转换成 ArrayNode 格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public ArrayNode toArrayNode(boolean pIgnoreNull, boolean pUseLower) {
        ArrayNode array = tgtools.util.JsonParseHelper.createArrayNode();
        for (int i = 0; i < this.getRowCount(); i++) {
            array.add(this.getRow(i).toObjectNode(pIgnoreNull, pUseLower));
        }
        return array;
    }

    /**
     * 转换成 ArrayNode 格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public ArrayNode toArrayNode(boolean pIgnoreNull) {
        return toArrayNode(pIgnoreNull, false);
    }

    /**
     * 转换成 ArrayNode格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public ArrayNode toArrayNode() {
        return toArrayNode(true);
    }


}

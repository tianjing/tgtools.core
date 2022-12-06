package tgtools.util;

import org.junit.Test;
import tgtools.data.DataColumn;
import tgtools.data.DataRow;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;

import java.util.Date;
import java.util.List;

public class DataTableSqlFactoryTest {
    @Test
    public void buildUpdateSqlTest() throws APPErrorException {
        tgtools.db.DataBaseFactory.add("DM", "jdbc:dm://192.168.1.254:5233/oms", "SYSDBA", "SYSDBA");

        DataTable table = new DataTable();
        table.setTableName("DQ_APP.SERVICE");
        DataColumn column = table.appendColumn("NAME");
        column.setColumnType(java.sql.Types.VARCHAR);
        column.setPrimaryKey(true);

        DataColumn column1 = table.appendColumn("AGE");
        column1.setColumnType(java.sql.Types.INTEGER);

        DataColumn column2 = table.appendColumn("BDATE");
        column2.setColumnType(java.sql.Types.DATE);

        DataColumn column3 = table.appendColumn("MONERY");
        column3.setColumnType(java.sql.Types.DECIMAL);

        DataRow row = table.appendRow();
        row.setValue("NAME", "田径1");
        row.setValue("AGE", 12);
        row.setValue("BDATE", new Date(System.currentTimeMillis()));
        row.setValue("MONERY", java.math.BigDecimal.valueOf(12.232321));

        DataRow row1 = table.appendRow();
        row1.setValue("NAME", "田径2");
        row1.setValue("AGE", 13);
        row1.setValue("BDATE", new Date(System.currentTimeMillis()));
        row1.setValue("MONERY", java.math.BigDecimal.valueOf(13.232321));

        List<String> inserts = DataTableSqlFactory.buildInsertSql(table, table.getTableName());
        List<String> updates = DataTableSqlFactory.buildUpdateSql(table, table.getTableName());

        System.out.println(inserts.get(0));
        System.out.println(inserts.get(1));

        System.out.println(updates.get(0));
        System.out.println(updates.get(1));
    }


    public static void main(String[] args) {
        try {
            tgtools.db.DataBaseFactory.add("DM", "jdbc:dm://192.168.1.254:5233/oms", "SYSDBA", "SYSDBA");
            String sql1 = "select top 1 * from MW_APP.MWT_UD_SB_YCSB where ccrq is not null";
            DataTable dt = tgtools.db.DataBaseFactory.getDefault().Query(sql1);
            if (DataTable.hasData(dt)) {
                String sql = DataTableSqlFactory.buildUpdateSql(DataTable.getFirstRow(dt), "tableddd");
                System.out.println(sql);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
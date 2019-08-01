package tgtools.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Properties;

public class DataBaseFactoryTest {

    @Test
    public void main() throws APPErrorException {
        String str1[] = {"jdbc:dm://192.168.88.128:5235", "SYSDBA", "SYSDBA"};
        MyDBCPDataAccess oms=new MyDBCPDataAccess();
        oms.init(str1);

        MyDBCPDataAccess ems=new MyDBCPDataAccess();
        ems.init(str1);
        DataBaseFactory.add("DATAACCESS_OMS_DATA", oms);
        DataBaseFactory.add("DATAACCESS_EMS_DATA", ems);
        DataTable table1 = DataBaseFactory.get("DATAACCESS_OMS_DATA").query("select * from TABLE_2");
        System.out.println(table1.toJson());
        DataTable table2 = DataBaseFactory.get("DATAACCESS_EMS_DATA").query("select * from TABLE_2");
        System.out.println(table2.toJson());
    }


    public static class MyDBCPDataAccess extends tgtools.db.DBCPDataAccess {

        @Override
        public boolean init(Object... params)// String p_Connstr, String p_UserName,
            // String p_Password)
                throws APPErrorException {
            String driver = "dm.jdbc.driver.DmDriver";
            if (params.length > 3 && !StringUtil.isNullOrEmpty(params[3].toString())) {
                driver = params[3].toString();
            }
            try {
                Properties p = new Properties();
                p.setProperty("driverClassName", driver);
                p.setProperty("url", params[0].toString());
                p.setProperty("password", params[1].toString());
                p.setProperty("username", params[2].toString());
                p.setProperty("maxActive", "10");
                p.setProperty("maxIdle", "10");
                p.setProperty("maxWait", "-1");
                p.setProperty("defaultAutoCommit", "true");
                p.setProperty("removeAbandoned", "true");
                p.setProperty("testWhileIdle", "true");
                p.setProperty("testOnBorrow", "false");
                p.setProperty("testOnReturn", "false");
                p.setProperty("validationQuery", "select 1 from dual");
                p.setProperty("timeBetweenEvictionRunsMillis", "30000");
                p.setProperty("numTestsPerEvictionRun", "30");
                p.setProperty("minEvictableIdleTimeMillis", "1800000");


                BasicDataSource m_DataSource = (BasicDataSource) BasicDataSourceFactory
                        .createDataSource(p);
                Field vField = tgtools.util.ReflectionUtil.findField(tgtools.db.DBCPDataAccess.class,"m_DataSource");
                vField.setAccessible(true);
                vField.set(this, m_DataSource);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

    }
}
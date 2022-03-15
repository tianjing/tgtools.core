package tgtools.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import tgtools.data.DataTable;
import tgtools.db.DMDataAccess;
import tgtools.exceptions.APPErrorException;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 21:33
 */
public class JsonSqlFactoryTest {

    @Test
    public void parseUpdateSqlTest() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("ID", 1);
        json.put("NAME", "tg");
        json.put("ISUSE", true);
        json.put("BIR", "2013-01-01 12:00:00");
        ArrayList<String> keys = new ArrayList<String>();
        keys.add("ID");
        keys.add("NAME");
        String sql = null;
        try {
            sql = JsonSqlFactory.parseUpdateSql(json, "dm", keys, "MQ_SYS.ACT_ID_USER");
            Assert.assertEquals("update MQ_SYS.ACT_ID_USER set ISUSE=true,BIR='2013-01-01 12:00:00' where ID=1 and NAME='tg' and  1=1", sql);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void parseInsertSqlTest() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("ID", 1);
        json.put("NAME", "tg");
        json.put("ISUSE", true);
        json.put("BIR", "2013-01-01 12:00:00");

        String sql = null;
        try {
            sql = JsonSqlFactory.parseInsertSql(json, "dm", "MQ_SYS.ACT_ID_USER");
            System.out.println(sql);
            Assert.assertEquals("insert into MQ_SYS.ACT_ID_USER (ID,NAME,ISUSE,BIR) values(1,'tg',true,'2013-01-01 12:00:00')", sql);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseEntityUpdateSqlTest() {
        User1 vUser1 = new User1();
        vUser1.setAge(21);
        vUser1.setId("1");
        vUser1.setName("tianjing");
        ArrayList<String> keys = new ArrayList<String>();
        keys.add("id");

        try {
            String sql = JsonSqlFactory.parseUpdateSql(vUser1, "dm", keys, "MQ_SYS.ACT_ID_USER");
            Assert.assertEquals("update MQ_SYS.ACT_ID_USER set name='tianjing',age=21 where id='1' and  1=1", sql);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void parseEntityInputSqlTest() {
        User1 vUser1 = new User1();
        vUser1.setAge(21);
        vUser1.setName("tianjing");
        vUser1.setId("1");
        try {
            String sql = JsonSqlFactory.parseInsertSql(vUser1, "dm", "MQ_SYS.ACT_ID_USER");
            Assert.assertEquals("insert into MQ_SYS.ACT_ID_USER (id,name,age) values('1','tianjing',21)", sql);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dbDataParseInsertSqlTest() {

        String sql = null;
        try {
                DMDataAccess vDMDataAccess = new DMDataAccess();
                vDMDataAccess.init("jdbc:dm://192.168.1.238:5240", "SYSDBA", "SYSDBA");
                tgtools.db.DataBaseFactory.add("DataAccess".toUpperCase(Locale.ROOT), vDMDataAccess);
                DataTable vTable = tgtools.db.DataBaseFactory.getDefault().query("select * from D5000.DMS_TR_DEVICE limit 10");

                sql = JsonSqlFactory.parseInsertSql(vTable.toArrayNode().get(0), "dm", "D5000.DMS_TR_DEVICE");
                System.out.println(sql);
                Assert.assertEquals("insert into MQ_SYS.ACT_ID_USER (ID,NAME,ISUSE,BIR) values(1,'tg',true,'2013-01-01 12:00:00')", sql);
            } catch(APPErrorException e){
                e.printStackTrace();
            }
        }


    public class User1 {
        private String id;
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String pName) {
            name = pName;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer pAge) {
            age = pAge;
        }

        public String getId() {
            return id;
        }

        public void setId(String pId) {
            id = pId;
        }
    }
}
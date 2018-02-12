package tgtools.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import tgtools.exceptions.APPErrorException;

import java.util.ArrayList;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 21:33
 */
public class JsonSqlFactoryTest {

    @Test
    public void parseUpdateSqlTest() {
        ObjectMapper mapper =new ObjectMapper();
        ObjectNode json= mapper.createObjectNode();
        json.put("ID",1);
        json.put("NAME","tg");
        json.put("ISUSE",true);
        json.put("BIR","2013-01-01 12:00:00");
        ArrayList<String> keys =new ArrayList<String>();
        keys.add("ID");
        keys.add("NAME");
        String sql= null;
        try {
            sql = JsonSqlFactory.parseUpdateSql(json,"dm",keys,"MQ_SYS.ACT_ID_USER");
            Assert.assertEquals("update MQ_SYS.ACT_ID_USER set ISUSE=true,BIR='2013-01-01 12:00:00' where ID=1 and NAME='tg' and  1=1",sql);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void parseInsertSqlTest() {
        ObjectMapper mapper =new ObjectMapper();
        ObjectNode json= mapper.createObjectNode();
        json.put("ID",1);
        json.put("NAME","tg");
        json.put("ISUSE",true);
        json.put("BIR","2013-01-01 12:00:00");

        String sql= null;
        try {
            sql = JsonSqlFactory.parseInsertSql(json,"dm","MQ_SYS.ACT_ID_USER");
            Assert.assertEquals("insert into MQ_SYS.ACT_ID_USER (ID,NAME,ISUSE,BIR) values(1,'tg',true,'2013-01-01 12:00:00')",sql);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }

    }
}
package tgtools.data;

import org.junit.Test;
import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONObject;

import java.io.InputStream;

import static org.junit.Assert.*;

public class DataTableTest {

    @Test
    public void main() throws APPErrorException {
        //m_ConnStr = params[0].toString();
        //m_UserName = params[1].toString();
        //m_Password = params[2].toString();
        String sql1 = "SELECT (case when 1=2 then 0.0 else convert(DECIMAL(7,2),1) end)as res FROM DUAL ";
        String sql2 = "SELECT convert(DECIMAL(7,2),1)as res FROM DUAL";
        String sql3 = "SELECT convert(DECIMAL(7,2),52.32)as res FROM DUAL";
        String sql4 = "select REV_,ID_,KEY_,ID_ as \"id\",KEY_ as \"key\" from BQ_SYS.ACT_DATADICTIONARY";
        String filesql = "select top 1 * from ACT_OM_FILE;";
        tgtools.db.DataBaseFactory.add("DM", new Object[]{"jdbc:dm://192.168.88.128:5235/dqmis", "BQ_SYS", "BQ_SYS123"});
        //tgtools.db.DataBaseFactory.add("DBCP", "jdbc:h2:file:C:\\tianjing\\Desktop\\mydb;DB_CLOSE_DELAY=1000;INIT=CREATE SCHEMA IF NOT EXISTS BQ_SYS\\;SET SCHEMA BQ_SYS;", "BQ_SYS123", "BQ_SYS","org.h2.Driver");
        String sqls = "WITH RECURSIVE r(ID_,PARENTID_) AS (  SELECT ID_,PARENTID_ FROM act_om_menu WHERE ID_ IN(select menu_id_  from act_om_rolemenu  where group_id_ in  (select group_id_  from act_id_membership  where user_id_ = '5DD4F0B7-C167-959F-0CEA-61AE48223A89') ) union   ALL   SELECT act_om_menu.ID_,act_om_menu.PARENTID_  FROM  act_om_menu, r WHERE act_om_menu.ID_ = r.PARENTID_  )      select ID_ AS ID ,APP_ID_ AS APPID , URL_ AS URL,PAGE_TARGET_ AS TARGET  ,(case when parentid_='0' then '' else parentid_ end) as PID ,title_ as TEXT,img_ as img , ICONPOSITION_ as iconPosition from act_om_menu where id_ in (   SELECT distinct id_ FROM r  ) order by parentid_,number_ ;";
        DataTable dt = tgtools.db.DataBaseFactory.getDefault().Query(filesql);
        Object obj = dt.getRow(0).getValue("VALUE_");
        if (obj instanceof InputStream) {

        }
        //dt.setCaseSensitive(true);
        System.out.println("JSONArray::" + new JSONArray(dt.toJson()));
        System.out.println("JSONObject::" + new JSONObject(dt.getRow(0).toJson()));
    }
}
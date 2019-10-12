package tgtools.db;

import org.junit.Test;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;

public class MySqlTest {

    @Test
    public void main() throws APPErrorException {
        String mysql_5_1[] = {
                "jdbc:mysql://192.168.1.238:3306/DQ_SYS?" +
                        "useUnicode=true" +
                        "&characterEncoding=UTF-8" +
                        "&zeroDateTimeBehavior=convertToNull" +
                        "&useOldAliasMetadataBehavior=true"+
                        "&useSSL=true"
                , "dq_sys123", "dq_sys1", "com.mysql.jdbc.Driver"};


        String vSql1="SELECT ATTR_NAME_ attrName ,ATTR_ attr,WIDTH_ width,LOACTION_ location,ui_view_ uiView FROM  DQ_SYS.ACT_OM_UISHOW where ui_view_ \n" +
                "in (select ui_id_ from DQ_SYS.ACT_OM_UIVIEW WHERE UI_CLSID_= 'B2C3910B-1FC2-4E08-B9A0-5D236DCD2016-00120'  AND UI_NAME_='销售合同查询' )   \n" +
                "  AND SFXS_ = 1 ORDER BY ORDER_";

//        String vSql2="SELECT a.id_  FROM (select * from dq_sys.act_dq_it )  a " +
//               "left join dq_app.act_dq_it b on a.id_ = b.id_  ";

        DataBaseFactory.add("DBCP", mysql_5_1);
        DataTable table1 = DataBaseFactory.getDefault().query(vSql1);
        System.out.println(table1.toJson());
    }

}
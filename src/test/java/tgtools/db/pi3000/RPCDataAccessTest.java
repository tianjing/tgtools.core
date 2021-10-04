package tgtools.db.pi3000;

import org.junit.Test;
import tgtools.exceptions.APPErrorException;

import static org.junit.Assert.*;

public class RPCDataAccessTest {

    @Test
    public void main() {
        try {
            tgtools.db.DataBaseFactory.add("PI3000SecondDatatest", new Object[]{"http://217.0.0.1/MWWebSite/services/dataservice"});

            //DataTable dt= tgtools.db.DataBaseFactory.getDefault().Query("select * from MW_SYS.MWT_IS_DBIT");
            tgtools.db.DataBaseFactory.getDefault().executeUpdate("UPDATE MW_SYS.MWT_IS_DBIT SET DBIT_ID='BLOB' WHERE DBIT_ID='BLOB'");
            System.out.println("");
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }
}
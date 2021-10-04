package tgtools.db.datasource;

import tgtools.exceptions.APPWarningException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author tianjing
 */
public class EncryptBasicDataSource extends org.apache.commons.dbcp.BasicDataSource {

    @Override
    protected synchronized DataSource createDataSource() throws SQLException {
        String passwd= this.getPassword();
        if(passwd.startsWith("DEC"))
        {
            try {
                passwd= tgtools.util.EncrpytionUtil.decryptString(password.substring(3));
                passwd= StringUtil.replace(passwd,"TGTOOLS==","");
                setPassword(passwd);
            } catch (APPWarningException e) {
                LogHelper.error("","解码失败，密码："+passwd,"",e);
            }


        }
        return super.createDataSource();
    }


}

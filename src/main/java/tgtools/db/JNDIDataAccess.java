package tgtools.db;

import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：22:06
 */
public class JNDIDataAccess extends DMDataAccess {

    private final static String INITIAL_CONTEXT_FACTORY = "weblogic.jndi.WLInitialContextFactory";
    private String jndi;
    private String weblogicUrl;
    private Hashtable<String, String> param = new Hashtable<String, String>();

    public JNDIDataAccess() {
    }

    public JNDIDataAccess(Hashtable<String, String> pParam) {
        param = pParam;
    }

    public JNDIDataAccess(String pJNDI, String pWeblogicUrl) {
        try {

            jndi = pJNDI;
            weblogicUrl = pWeblogicUrl;

            if (null == param) {
                param = new Hashtable<String, String>();
            }
            if (!StringUtil.isNullOrEmpty(pWeblogicUrl)) {
                param.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
                param.put(Context.PROVIDER_URL, pWeblogicUrl);
            }
            init(jndi, pWeblogicUrl);
            LogHelper.info("", "初始化参数：" + jndi, "JNDIDataAccess");
        } catch (Exception e) {
            LogHelper.error("", "初始化失败,参数：" + jndi, "JNDIDataAccess", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Object... params) throws APPErrorException {
        if (null != params && params.length == 2) {
            try {
                if (null != param) {
                    Context context = null;
                    if (null != param && param.size() > 0) {
                        context = new InitialContext(param);
                    } else {
                        context = new InitialContext();
                    }
                    Object obj = context.lookup((String) params[0]);
                    LogHelper.info("", "初始化DataSource：" + obj, "JNDIDataAccess");
                    if (obj instanceof DataSource) {
                        dataSource = (DataSource) obj;
                    }

                }

            } catch (NamingException e) {
                LogHelper.error("", "初始化失败,参数：" + params[0], "JNDIDataAccess.init", e);
            }

        }
        return false;
    }

    @Override
    protected Connection getConnection() throws APPErrorException, SQLException {
        try {
            if (null == dataSource) {
                throw new APPErrorException("无效的数据源");
            }
            Connection conn = dataSource.getConnection();
            if (null == conn) {
                throw new APPErrorException("无法创建连接，数据源对象：" + dataSource);
            }
            return conn;
        } catch (SQLException e) {
            throw new APPErrorException("创建数据库连接错误", e);
        }
    }
}


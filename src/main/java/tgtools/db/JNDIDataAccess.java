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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：22:06
 */
public class JNDIDataAccess extends DMDataAccess {

    private String m_JNDI;
    private String m_WeblogicUrl;
    private final static String INITIAL_CONTEXT_FACTORY="weblogic.jndi.WLInitialContextFactory";
    private Hashtable<String,String> m_Param=new Hashtable<String,String>();

    public JNDIDataAccess()
    {}
    public JNDIDataAccess(Hashtable<String,String > p_Param)
    {
        m_Param=p_Param;
    }
    public JNDIDataAccess(String p_JNDI,String p_WeblogicUrl)
    {
        try {

            m_JNDI=p_JNDI;
            m_WeblogicUrl=p_WeblogicUrl;

            if(null==m_Param) {
                m_Param=new Hashtable<String, String>();
            }
            if(!StringUtil.isNullOrEmpty(p_WeblogicUrl)) {
                m_Param.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
                m_Param.put(Context.PROVIDER_URL, p_WeblogicUrl);
            }
            init(m_JNDI,p_WeblogicUrl);
            LogHelper.info("","初始化参数："+m_JNDI,"JNDIDataAccess");
        }
        catch (Exception e)
        {
            LogHelper.error("","初始化失败,参数："+m_JNDI,"JNDIDataAccess",e);
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Object... params) throws APPErrorException {
        if (null != params && params.length == 2)
            try {
                if(null!=m_Param)
                {
                    Context context = null;
                    if(null!=m_Param&&m_Param.size()>0)
                    {context =new InitialContext(m_Param);}
                    else{context =new InitialContext();}
                    Object obj = context.lookup((String) params[0]);
                    LogHelper.info("","初始化DataSource："+obj,"JNDIDataAccess");
                    if (obj instanceof DataSource) {
                        m_DataSource = (DataSource) obj;
                    }

                }

            } catch (NamingException e) {
                LogHelper.error("","初始化失败,参数："+params[0],"JNDIDataAccess.init",e);
            }


        return false;
    }

    @Override
    protected Connection getConnection() throws APPErrorException, SQLException {
        try {
            if(null==m_DataSource)
            {
                throw new APPErrorException("无效的数据源");
            }
            Connection conn= m_DataSource.getConnection();
            if(null==conn)
            {
                throw new APPErrorException("无法创建连接，数据源对象："+m_DataSource);
            }
            return conn;
        } catch (SQLException e) {
            throw new APPErrorException("创建数据库连接错误", e);
        }
    }
}


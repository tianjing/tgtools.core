package tgtools.db;

import tgtools.util.LogHelper;
import tgtools.util.ReflectionUtil;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;

public abstract class AbstractDataAccess implements IDataAccess {
    protected DataSource m_DataSource;
    protected String m_DataBaseType = "";

    @Override
    public String getDataBaseType() {
        if (!StringUtil.isNullOrEmpty(m_DataBaseType)) {
            return m_DataBaseType;
        }
        String url = getUrl();
        if (!StringUtil.isNullOrEmpty(url)) {
            m_DataBaseType = url.substring(url.indexOf("jdbc:") + 5, url.indexOf(":", url.indexOf("jdbc:") + 5));
        }
        return m_DataBaseType;
    }

    @Override
    public void setDataBaseType(String p_DataBaseType) {

        m_DataBaseType = p_DataBaseType;
    }

    @Override
    public String getUrl() {
        if (null != m_DataSource) {
            try {
                Method method = ReflectionUtil.findMethod(m_DataSource.getClass(), "getUrl", new Class[]{});
                if (null == method) {
                    LogHelper.info(getClass().getName(), "无法获取getUrl方法。开始尝试getJdbcUrl", "getUrl");
                    method = ReflectionUtil.findMethod(m_DataSource.getClass(), "getJdbcUrl", new Class[]{});
                    if (null == method) {
                        LogHelper.info(getClass().getName(), "无法获取getJdbcUrl方法。", "getUrl");
                    }
                }
                Object obj = method.invoke(m_DataSource, new Object[]{});
                return null == obj ? StringUtil.EMPTY_STRING : obj.toString();
            } catch (Exception e) {
                LogHelper.error(getClass().getName(), "获取数据库连接出错。", "getUrl", e);
            }
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    public DataSource getDataSource() {
        return m_DataSource;
    }

    public void setDataSource(DataSource p_DataSource) {
        m_DataSource = p_DataSource;
    }


}

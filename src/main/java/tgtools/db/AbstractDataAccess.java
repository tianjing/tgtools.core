package tgtools.db;

import tgtools.util.LogHelper;
import tgtools.util.ReflectionUtil;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 *
 * @author tianjing
 */
public abstract class AbstractDataAccess implements IDataAccess {
    protected DataSource dataSource;
    protected String dataBaseType = "";

    @Override
    public String getDataBaseType() {
        if (!StringUtil.isNullOrEmpty(dataBaseType)) {
            return dataBaseType;
        }
        String url = getUrl();
        if (!StringUtil.isNullOrEmpty(url)) {
            dataBaseType = url.substring(url.indexOf("jdbc:") + 5, url.indexOf(":", url.indexOf("jdbc:") + 5));
        }
        return dataBaseType;
    }

    @Override
    public void setDataBaseType(String pDataBaseType) {
        dataBaseType = pDataBaseType;
    }

    @Override
    public String getUrl() {
        if (null != dataSource) {
            try {
                Method method = ReflectionUtil.findMethod(dataSource.getClass(), "getUrl", new Class[]{});
                if (null == method) {
                    LogHelper.info(getClass().getName(), "无法获取getUrl方法。开始尝试getJdbcUrl", "getUrl");
                    method = ReflectionUtil.findMethod(dataSource.getClass(), "getJdbcUrl", new Class[]{});
                    if (null == method) {
                        LogHelper.info(getClass().getName(), "无法获取getJdbcUrl方法。", "getUrl");
                    }
                }
                Object obj = method.invoke(dataSource, new Object[]{});
                return null == obj ? StringUtil.EMPTY_STRING : obj.toString();
            } catch (Exception e) {
                LogHelper.error(getClass().getName(), "获取数据库连接出错。", "getUrl", e);
            }
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource pDataSource) {
        dataSource = pDataSource;
    }


}

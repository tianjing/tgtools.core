package tgtools.db;

import com.sun.rowset.CachedRowSetImpl;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;

public class DMDataAccess implements IDataAccess {
    // jdbc:dm://URL:PORT/DATABASE
    protected String m_ConnStr;
    protected String m_UserName;
    protected String m_Password;
    protected Connection m_Conn;
    protected String m_DataBaseType = "";
    private DataSource m_DataSource;



    protected Connection getConnection() throws APPErrorException, SQLException {

        if (null == m_DataSource) {
            initDataSource();
        }
        return m_DataSource.getConnection();
    }

    protected void initDataSource() throws APPErrorException {
        initDataSource("dm.jdbc.pool.DmdbDataSource");
    }

    protected void initDataSource(String p_DriverName) throws APPErrorException {
        try {
            Class clazz = Class.forName(p_DriverName);
            if (null != clazz) {
                DataSource source = (DataSource) clazz.newInstance();
                setValue(clazz, source, "setURL", m_ConnStr);
                setValue(clazz, source, "setUser", m_UserName);
                setValue(clazz, source, "setPassword", m_Password);
                m_DataSource = source;
            }
            throw new APPErrorException("没有找到DmdbDataSource");
        } catch (Exception e) {
            throw new APPErrorException("没有找到DmDriver", e);
        }

    }

    @SuppressWarnings("unchecked")
    private void setValue(Class p_Clazz, Object obj, String p_MethodName, String p_Value) {
        try {
            Method method = p_Clazz.getDeclaredMethod(p_MethodName, String.class);
            method.invoke(obj, p_Value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDataBaseType(String p_DataBaseType) {
        m_DataBaseType=p_DataBaseType;
    }

    @Override
    public String getDataBaseType() {
        if (!StringUtil.isNullOrEmpty(m_DataBaseType)) {
            return m_DataBaseType;
        }
        String url = getUrl();
        if (!StringUtil.isNullOrEmpty(url)) {
            m_DataBaseType=url.substring(url.indexOf("jdbc:") + 5, url.indexOf(":", url.indexOf("jdbc:") + 5));
        }
        return m_DataBaseType;
    }
    @Override
    public String getUrl() {
        if (null != m_DataSource) {
            try {
                Method method = m_DataSource.getClass().getDeclaredMethod("getUrl", new Class[]{});
                if (null == method) {
                    LogHelper.info("", "无法获取getUrl方法。", "DMDataAccess.getUrl");
                }
                Object obj = method.invoke(m_DataSource, new Object[]{});
                return null == obj ? StringUtil.EMPTY_STRING : obj.toString();
            } catch (Exception e) {
                LogHelper.error("", "获取数据库连接出错。", "DMDataAccess.getUrl", e);
            }
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    public DataSource getDataSource() {
        return m_DataSource;
    }

    @Override
    public ResultSet executeQuery(String sql) throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;
        try {
            CachedRowSetImpl rowset = new CachedRowSetImpl();

            conn = getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            rowset.populate(rs);
            return rowset;
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(rs);
            close(statement);
            close(conn);
        }
    }

    @Override
    public DataTable Query(String sql) throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            return new DataTable(rs, sql);
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(rs);
            close(statement);
            close(conn);
        }
    }

    @Override
    public int executeUpdate(String sql) throws APPErrorException {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn.prepareStatement(sql).executeUpdate();
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
    }

    @Override
    public int[] executeBatch(String[] sqls) throws APPErrorException {
        Connection conn = null;
        try {
            conn = getConnection();
            Statement statment = getConnection().createStatement();

            for (String sql : sqls)
                statment.addBatch(sql);

            return statment.executeBatch();
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：", e);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean init(Object... params) //String p_Connstr, String p_UserName, String p_Password)
            throws APPErrorException {
        m_ConnStr = params[0].toString();
        m_UserName = params[1].toString();
        m_Password = params[2].toString();
        try {
            // getConnection();
        } catch (Exception e) {
        }
        return true;
    }

    private void close(Statement p_Statement) {
        try {
            if (null != p_Statement)
                p_Statement.close();
        } catch (Exception e) {
        }
        p_Statement = null;
    }

    private void close(ResultSet p_Result) {
        try {
            if (null != p_Result)
                p_Result.close();
        } catch (Exception e) {
        }
        p_Result = null;
    }

    private void close(Connection p_Conn) {
        try {
            if (null != p_Conn)
                p_Conn.close();
        } catch (Exception e) {
        }
        p_Conn = null;
    }

    @Override
    public void close() {
        try {
            if (null != m_Conn)
                m_Conn.close();
        } catch (Exception e) {
        }
        m_Conn = null;
    }

    @Override
    public Connection createConnection() {

        try {
            return getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    private void setParams(PreparedStatement p_Statement, Object[] p_Params)
            throws SQLException {
        if (null != p_Params) {
            for (int i = 0; i < p_Params.length; i++) {
                p_Statement.setObject(i + 1, p_Params[i]);
            }
        }
    }

    @Override
    public DataTable Query(String sql, Object[] p_Params)
            throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            setParams(statement, p_Params);
            rs = statement.executeQuery();
            return new DataTable(rs, sql);
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(rs);
            close(statement);
            close(conn);
        }
    }

    @Override
    public int executeUpdate(String sql, Object[] p_Params)
            throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                setParams(statement, p_Params);
                return statement.executeUpdate();
                // rs.close();
            }

        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
        return -1;
    }

    @Override
    public int executeBlob(String sql, byte[] p_Params)
            throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setBytes(1, p_Params);
                return statement.executeUpdate();
            }
            throw new APPErrorException("获取连接失败！");
        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
    }

    @Override
    public int[] executeSqlFile(String p_SqlFile) throws APPErrorException {
        throw new APPErrorException("未实现改方法");
    }

    @Override
    public boolean executeBatchByTransaction(String[] sqls, int level) throws APPErrorException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            if (level > -1) {
                conn.setTransactionIsolation(level);
            }
            Statement statment = conn.createStatement();

            for (String sql : sqls)
                if (!StringUtil.isNullOrEmpty(sql)) {
                    statment.addBatch(sql);
                }

            statment.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LogHelper.error("", "事物批量回滚失败", "SpringDataAccess.executeBatchByTransaction", e);
            }
            LogHelper.error("", "事物批量执行失败", "SpringDataAccess.executeBatchByTransaction", e);
            return false;
        } finally {
            close(conn);
        }
    }
}

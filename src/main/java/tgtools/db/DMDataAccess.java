package tgtools.db;

import com.sun.rowset.CachedRowSetImpl;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.*;

public class DMDataAccess extends AbstractDataAccess {
    // jdbc:dm://URL:PORT/DATABASE
    protected String m_ConnStr;
    protected String m_UserName;
    protected String m_Password;
    protected Connection m_Conn;



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
            } else {
                throw new APPErrorException("没有找到DmdbDataSource");
            }
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

            for (String sql : sqls) {
                statment.addBatch(sql);
            }
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
            if (null != m_Conn) {
                m_Conn.close();
            }
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

    protected void setParams(PreparedStatement p_Statement, Object[] p_Params, boolean pUseSetInputStream)
            throws SQLException {
        if (null != p_Params) {
            for (int i = 0; i < p_Params.length; i++) {
                if (pUseSetInputStream && (p_Params[i] instanceof InputStream)) {
                    try {
                        p_Statement.setBinaryStream(i + 1, (InputStream) p_Params[i], ((InputStream) p_Params[i]).available());
                    } catch (Exception ex) {
                        throw new SQLException("文件流设置错误；原因：" + ex.toString(), ex);
                    }
                } else {
                    p_Statement.setObject(i + 1, p_Params[i]);
                }
            }
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
                setParams(statement, p_Params, false);
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
    public int executeUpdate(String sql, Object[] p_Params, boolean pUseSetInputStream) throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                setParams(statement, p_Params, false);
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


    @Override
    public DataTable Query(String sql, Object[] p_Params) throws APPErrorException {
        return query(sql, p_Params);
    }

    @Override
    public <T> T Query(String sql, Class<T> p_Class) throws APPErrorException {
        return query(sql, p_Class);
    }

    @Override
    public DataTable Query(String sql, boolean p_BlobUseStream) throws APPErrorException {
        return query(sql, p_BlobUseStream);
    }

    @Override
    public DataTable Query(String sql) throws APPErrorException {
        return query(sql);
    }


    @Override
    public DataTable query(String sql, boolean p_BlobUseStream) throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            return new DataTable(rs, sql, p_BlobUseStream);
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(rs);
            close(statement);
            close(conn);
        }
    }

    @Override
    public DataTable query(String sql, Object[] p_Params) throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            setParams(statement, p_Params, false);
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
    public DataTable query(String sql) throws APPErrorException {
        return query(sql, false);
    }

    @Override
    public <T> T query(String sql, Class<T> p_Class) throws APPErrorException {
        return (T) JsonParseHelper.parseToObject(query(sql), p_Class, true);
    }
}

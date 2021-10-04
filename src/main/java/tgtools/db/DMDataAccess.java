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
/**
 * @author tianjing
 */
public class DMDataAccess extends AbstractDataAccess {
    // jdbc:dm://URL:PORT/DATABASE
    protected String connStr;
    protected String userName;
    protected String password;
    protected Connection conn;


    protected Connection getConnection() throws APPErrorException, SQLException {

        if (null == dataSource) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    protected void initDataSource() throws APPErrorException {
        initDataSource("dm.jdbc.pool.DmdbDataSource");
    }

    protected void initDataSource(String pDriverName) throws APPErrorException {
        try {
            Class clazz = Class.forName(pDriverName);
            if (null != clazz) {
                DataSource source = (DataSource) clazz.newInstance();
                setValue(clazz, source, "setURL", connStr);
                setValue(clazz, source, "setUser", userName);
                setValue(clazz, source, "setPassword", password);
                dataSource = source;
            } else {
                throw new APPErrorException("没有找到DmdbDataSource");
            }
        } catch (Exception e) {
            throw new APPErrorException("没有找到DmDriver", e);
        }

    }

    @SuppressWarnings("unchecked")
    private void setValue(Class pClazz, Object obj, String pMethodName, String pValue) {
        try {
            Method method = pClazz.getDeclaredMethod(pMethodName, String.class);
            method.invoke(obj, pValue);
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
        Statement statment = null;
        try {
            conn = getConnection();
            statment = conn.createStatement();

            for (String sql : sqls) {
                statment.addBatch(sql);
            }
            return statment.executeBatch();
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：", e);
        } finally {
            closeBatch(statment);
            close(conn);
        }
    }

    @Override
    public boolean init(Object... params) //String pConnstr, String p_UserName, String p_Password)
            throws APPErrorException {
        connStr = params[0].toString();
        userName = params[1].toString();
        password = params[2].toString();
        try {
            // getConnection();
        } catch (Exception e) {
        }
        return true;
    }

    protected void close(Statement pStatement) {
        try {
            if (null != pStatement) {
                pStatement.close();
            }
        } catch (Exception e) {
        }
        pStatement = null;
    }

    protected void close(ResultSet pResult) {
        try {
            if (null != pResult) {
                pResult.close();
            }
        } catch (Exception e) {
        }
        pResult = null;
    }

    protected void close(Connection pConn) {
        try {
            if (null != pConn) {
                pConn.close();
            }
        } catch (Exception e) {
        }
        pConn = null;
    }

    protected void closeBatch(Statement pStatement) {
        try {
            if (null != pStatement) {
                pStatement.clearBatch();
                pStatement.close();
            }
        } catch (Exception e) {
        }
        pStatement = null;
    }

    @Override
    public void close() {
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (Exception e) {
        }
        conn = null;
    }

    @Override
    public Connection createConnection() {

        try {
            return getConnection();
        } catch (Exception e) {
            return null;
        }
    }

    protected void setParams(PreparedStatement pStatement, Object[] pParams, boolean pUseSetInputStream)
            throws SQLException {
        if (null != pParams) {
            for (int i = 0; i < pParams.length; i++) {
                if (pUseSetInputStream && (pParams[i] instanceof InputStream)) {
                    try {
                        pStatement.setBinaryStream(i + 1, (InputStream) pParams[i], ((InputStream) pParams[i]).available());
                    } catch (Exception ex) {
                        throw new SQLException("文件流设置错误；原因：" + ex.toString(), ex);
                    }
                } else {
                    pStatement.setObject(i + 1, pParams[i]);
                }
            }
        }
    }


    @Override
    public int executeUpdate(String sql, Object[] pParams)
            throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                setParams(statement, pParams, false);
                return statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
        return -1;
    }

    @Override
    public int executeUpdate(String sql, Object[] pParams, boolean pUseSetInputStream) throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                setParams(statement, pParams, false);
                return statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
        return -1;
    }

    @Override
    public int executeBlob(String sql, byte[] pParams)
            throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setBytes(1, pParams);
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
    public int[] executeSqlFile(String pSqlFile) throws APPErrorException {
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

            for (String sql : sqls) {
                if (!StringUtil.isNullOrEmpty(sql)) {
                    statment.addBatch(sql);
                }
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
    public DataTable Query(String sql, Object[] pParams) throws APPErrorException {
        return query(sql, pParams);
    }

    @Override
    public <T> T Query(String sql, Class<T> pClass) throws APPErrorException {
        return query(sql, pClass);
    }

    @Override
    public DataTable Query(String sql, boolean pBlobUseStream) throws APPErrorException {
        return query(sql, pBlobUseStream);
    }

    @Override
    public DataTable Query(String sql) throws APPErrorException {
        return query(sql);
    }


    @Override
    public DataTable query(String sql, boolean pBlobUseStream) throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        Statement statement = null;
        try {
            conn = getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            return new DataTable(rs, sql, pBlobUseStream);
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(rs);
            close(statement);
            close(conn);
        }
    }

    @Override
    public DataTable query(String sql, Object[] pParams) throws APPErrorException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            setParams(statement, pParams, false);
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
    public <T> T query(String sql, Class<T> pClass) throws APPErrorException {
        return (T) JsonParseHelper.parseToObject(query(sql), pClass, true);
    }
}

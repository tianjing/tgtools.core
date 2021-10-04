package tgtools.db;

import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.dbcp.*;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author tianjing
 */
public class DBCPDataAccess extends AbstractDataAccess {
    /**
     * URL
     */
    private static String url = "";
    /**
     * 用户名
     */
    private static String name = "sa";
    /**
     * 密码
     */
    private static String password = "";
    /**
     * connectionPool
     */
    private static ObjectPool<?> connectionPool = null;
    /**
     * poolname
     */
    private static String poolname = "";


    /**
     * 连接池启动
     *
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void StartPool() {
        if (connectionPool != null) {
            ShutdownPool();
        }

        try {
            connectionPool = new GenericObjectPool();

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                    url, name, password);
            new PoolableConnectionFactory(connectionFactory, connectionPool,
                    null, null, false, true);
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
            PoolingDriver driver = (PoolingDriver) DriverManager
                    .getDriver("jdbc:apache:commons:dbcp:");
            driver.registerPool(poolname, connectionPool);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放连接池
     */
    public static void ShutdownPool() {
        try {
            PoolingDriver driver = (PoolingDriver) DriverManager
                    .getDriver("jdbc:apache:commons:dbcp:");
            driver.closePool(poolname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得连接池中的连接
     *
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void close(Connection pConn) {
        try {
            if (null != pConn) {
                pConn.close();
            }
        } catch (Exception e) {
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

            if (conn != null) {
                statement = conn.createStatement();
                rs = statement.executeQuery(sql);
                rowset.populate(rs);
                return rowset;
            }

        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(rs);
            close(statement);
            close(conn);
        }
        return null;
    }


    @Override
    public int executeUpdate(String sql) throws APPErrorException {
        Connection conn = null;

        try {

            conn = getConnection();
            if (conn != null) {
                Statement statement = conn.createStatement();
                return statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
        return -1;
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
    public boolean init(Object... params)// String pConnstr, String p_UserName,
        // String p_Password)
            throws APPErrorException {
        String driver = "dm.jdbc.driver.DmDriver";
        if (params.length > 3 && !StringUtil.isNullOrEmpty(params[3].toString())) {
            driver = params[3].toString();
        }
        try {
            Properties p = new Properties();
            p.setProperty("driverClassName", driver);
            p.setProperty("url", params[0].toString());
            p.setProperty("password", params[1].toString());
            p.setProperty("username", params[2].toString());
            p.setProperty("maxActive", "40");
            p.setProperty("maxIdle", "40");
            p.setProperty("maxWait", "-1");
            p.setProperty("removeAbandoned", "false");
            p.setProperty("removeAbandonedTimeout", "120");
            p.setProperty("testOnBorrow", "true");
            p.setProperty("logAbandoned", "true");
            dataSource = (BasicDataSource) BasicDataSourceFactory
                    .createDataSource(p);
        } catch (Exception e) {
        }
        // getConnection();
        return true;
    }

    @Override
    public void close() {
        ShutdownPool();
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

    protected void close(ResultSet pResult) {
        try {
            if (null != pResult) {
                pResult.close();
            }
        } catch (Exception e) {
        }
        pResult = null;
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

    @Override
    public Connection createConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
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
                setParams(statement, pParams, pUseSetInputStream);
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
        if (!StringUtil.isNullOrEmpty(pSqlFile)) {
            String[] sqls = pSqlFile.split(";");
            if (sqls.length > 0) {
                return executeBatch(sqls);
            }
        }
        return null;
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
    public DataTable Query(String sql, Object[] pParams)
            throws APPErrorException {
        return query(sql, pParams);
    }

    @Override
    public DataTable Query(String sql) throws APPErrorException {
        return query(sql);
    }

    @Override
    public DataTable Query(String sql, boolean pBlobUseStream) throws APPErrorException {
        return query(sql, pBlobUseStream);
    }

    @Override
    public <T> T Query(String sql, Class<T> pClass) throws APPErrorException {
        return query(sql, pClass);
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

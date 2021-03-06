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

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Properties;

public class DBCPDataAccess extends AbstractDataAccess {
    // private static String driver = "net.sourceforge.jtds.jdbc.Driver"; // 驱动
    private static String url = ""; // URL
    private static String name = "sa"; // 用户名
    private static String password = ""; // 密码
    private static ObjectPool<?> connectionPool = null;
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
        return m_DataSource.getConnection();
    }

    private void close(Connection p_Conn) {
        try {
            if (null != p_Conn) {
                p_Conn.close();
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
    public int[] executeBatch(String[] sqls) throws APPErrorException {
        // TODO Auto-generated method stub
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
    public boolean init(Object... params)// String p_Connstr, String p_UserName,
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
            m_DataSource = (BasicDataSource) BasicDataSourceFactory
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

    private void close(ResultSet p_Result) {
        try {
            if (null != p_Result)
                p_Result.close();
        } catch (Exception e) {
        }
        p_Result = null;
    }

    private void close(Statement p_Statement) {
        try {
            if (null != p_Statement)
                p_Statement.close();
        } catch (Exception e) {
        }
        p_Statement = null;
    }

    @Override
    public Connection createConnection() {
        // TODO Auto-generated method stub
        try {
            return m_DataSource.getConnection();
        } catch (SQLException e) {
            return null;
        }
    }


    protected void setParams(PreparedStatement p_Statement, Object[] p_Params, boolean pUseSetInputStream)
            throws SQLException {
        if (null != p_Params) {
            for (int i = 0; i < p_Params.length; i++) {
                if (pUseSetInputStream && (p_Params[i] instanceof InputStream)) {
                    try {
                        p_Statement.setBinaryStream(i + 1, (InputStream)p_Params[i], ((InputStream) p_Params[i]).available());
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
                setParams(statement, p_Params,false);
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
                setParams(statement, p_Params,pUseSetInputStream);
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
        if (!StringUtil.isNullOrEmpty(p_SqlFile)) {
            String[] sqls = p_SqlFile.split(";");
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
    public DataTable Query(String sql, Object[] p_Params)
            throws APPErrorException {
        return query(sql,p_Params);
    }
    @Override
    public DataTable Query(String sql) throws APPErrorException {
        return query(sql);
    }

    @Override
    public DataTable Query(String sql, boolean p_BlobUseStream) throws APPErrorException {
        return query(sql,p_BlobUseStream);
    }

    @Override
    public <T> T Query(String sql, Class<T> p_Class) throws APPErrorException {
       return query(sql,p_Class);
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
            return new DataTable(rs, sql,p_BlobUseStream);
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
            setParams(statement, p_Params,false);
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
        return query(sql,false);
    }

    @Override
    public <T> T query(String sql, Class<T> p_Class) throws APPErrorException {
        return (T) JsonParseHelper.parseToObject(query(sql), p_Class, true);
    }

}

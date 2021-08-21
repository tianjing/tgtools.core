package tgtools.db;

import com.sun.rowset.CachedRowSetImpl;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;

/**
 * 通过数据源创建 DataAccess
 */
public class DataSourceDataAccess extends AbstractDataAccess {

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
                if (!StringUtil.isNullOrEmpty(sql)) {
                    statment.addBatch(sql);
                }
            }

            return statment.executeBatch();
        } catch (Exception e) {
            throw new APPErrorException("sql执行失败 executeBatch：", e);
        } finally {
            try {
                if (null != statment) {
                    statment.clearBatch();
                    statment.close();
                }
            } catch (Exception e) {
            }
            statment = null;

            try {
                if (null != conn && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
            }
            conn = null;
        }
    }

    protected Connection getConnection() throws APPErrorException, SQLException {
        return m_DataSource.getConnection();
    }

    protected void close(Statement p_Statement) {
        try {
            if (null != p_Statement) {
                p_Statement.close();
            }
        } catch (Exception e) {
        }
        p_Statement = null;
    }

    protected void closeBatch(Statement p_Statement) {
        try {
            if (null != p_Statement) {
                p_Statement.clearBatch();
                p_Statement.close();
            }
        } catch (Exception e) {
        }
        p_Statement = null;
    }

    protected void close(ResultSet p_Result) {
        try {
            if (null != p_Result) {
                p_Result.close();
            }
        } catch (Exception e) {
        }
        p_Result = null;
    }

    protected void close(Connection p_Conn) {
        try {
            if (null != p_Conn) {
                p_Conn.close();
            }
        } catch (Exception e) {
        }
        p_Conn = null;
    }

    /**
     * 传入DataSource进行初始化
     *
     * @param objects
     * @return
     * @throws APPErrorException
     */
    @Override
    public boolean init(Object... objects) throws APPErrorException {
        if (null != objects && objects.length == 1 && objects[0] instanceof DataSource) {
            m_DataSource = (DataSource) objects[0];
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        if (null != m_DataSource) {
            m_DataSource = null;
        }
    }

    @Override
    public Connection createConnection() {

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
                setParams(statement, p_Params, pUseSetInputStream);
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
    public int[] executeSqlFile(String p_Sqls) throws APPErrorException {
        if (!StringUtil.isNullOrEmpty(p_Sqls)) {
            String[] sqls = p_Sqls.split(";");
            if (sqls.length > 0) {
                return executeBatch(sqls);
            }
        }
        return null;
    }

    /**
     * @param sqls
     * @param level -1表示不用该条件 Connection.TRANSACTION_READ_UNCOMMITTED
     * @return
     * @throws APPErrorException
     */
    @Override
    public boolean executeBatchByTransaction(String[] sqls, int level) throws APPErrorException {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            if (level > -1) {
                conn.setTransactionIsolation(level);
            }
            statement = conn.createStatement();

            for (String sql : sqls) {
                if (!StringUtil.isNullOrEmpty(sql)) {
                    statement.addBatch(sql);
                }
            }
            statement.executeBatch();
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
            try {
                if (null != statement) {
                    statement.clearBatch();
                    statement.close();
                }
            } catch (Exception e) {
            }
            statement = null;

            close(conn);
        }
    }


    @Override
    public DataTable Query(String sql, Object[] p_Params)
            throws APPErrorException {
        return query(sql, p_Params);
    }


    @Override
    public DataTable Query(String sql) throws APPErrorException {
        return query(sql);
    }

    @Override
    public DataTable Query(String sql, boolean p_BlobUseStream) throws APPErrorException {
        return query(sql, p_BlobUseStream);
    }

    @Override
    public <T> T Query(String sql, Class<T> p_Class) throws APPErrorException {
        return query(sql, p_Class);
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
            LogHelper.info("", sql, "SpringDataAccess.Query");
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
            LogHelper.info("", sql, "SpringDataAccess.Query");
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

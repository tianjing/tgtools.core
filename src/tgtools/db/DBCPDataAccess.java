package tgtools.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.sun.rowset.CachedRowSetImpl;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

public class DBCPDataAccess implements IDataAccess {
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

	private DataSource m_DataSource;

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
		try {
			Properties p = new Properties();
			p.setProperty("driverClassName", "dm.jdbc.driver.DmDriver");
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
			setParams(statement,p_Params);
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
				setParams(statement,p_Params);
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
			if(level>-1) {
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
		}
		catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				LogHelper.error("","事物批量回滚失败","SpringDataAccess.executeBatchByTransaction",e);
			}
			LogHelper.error("","事物批量执行失败","SpringDataAccess.executeBatchByTransaction",e);
			return false;
		} finally {
			close(conn);
		}
	}

}

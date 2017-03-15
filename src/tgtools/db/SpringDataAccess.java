package tgtools.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.sun.rowset.CachedRowSetImpl;

import org.w3c.dom.Document;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

public class SpringDataAccess implements IDataAccess {
	private DataSource m_DataSource;
	private String m_DataBaseType = "";
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
			LogHelper.info("",sql,"SpringDataAccess.Query");
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
	public <T> T Query(String sql, Class<T> p_Class) throws APPErrorException {
		return (T) JsonParseHelper.parseToObject(Query(sql),p_Class,true);
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
				if(!StringUtil.isNullOrEmpty(sql)){
				statment.addBatch(sql);
				}

			return statment.executeBatch();
		} catch (Exception e) {
			throw new APPErrorException("sql执行失败：", e);
		} finally {
			close(conn);
		}
	}

	private Connection getConnection() throws APPErrorException, SQLException {
		return m_DataSource.getConnection();
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
	public boolean init(Object... params) throws APPErrorException {
		try {
			Object context = params[0];
			Object sourcename = params[1];

			Method method2 = context.getClass().getMethod("getBean",
					String.class);
			Object obj3 = method2.invoke(context, sourcename);// "DataSource"

			if (obj3 instanceof DataSource) {
				m_DataSource = (DataSource) obj3;
				m_DataSource.getConnection();
				return true;
			}
		} catch (Exception e) {

			return false;
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
			LogHelper.info("",sql,"SpringDataAccess.Query");
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
	 *
	 * @param sqls
	 * @param level -1表示不用该条件 Connection.TRANSACTION_READ_UNCOMMITTED
	 * @return
	 * @throws APPErrorException
     */
	@Override
	public boolean executeBatchByTransaction(String[] sqls,int level) throws APPErrorException {
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

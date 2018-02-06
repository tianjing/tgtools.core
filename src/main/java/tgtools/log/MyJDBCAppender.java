package tgtools.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import tgtools.db.DataBaseFactory;
import tgtools.db.IDataAccess;
import tgtools.util.GUID;

public class MyJDBCAppender extends AppenderSkeleton {
	/** 通过 PoolMan 获取数据库连接对象的 jndiName 属性 */
	protected String jndiName;
	/** 数据库连接对象 */
	protected Connection connection;

	public MyJDBCAppender() {
		super();

	}

	private static boolean m_hasTable = false;
	private static boolean m_useDBLOG=true;
	private static boolean m_IsPlatForm=false;
	private boolean checkTable() {
		Connection conn = null;
		if (m_hasTable) {
			return true;
		}
		try {

			conn = getConnection();
			if(null!=conn){
			ResultSet rs= conn.createStatement().executeQuery("select top 1 * from LogInfo");
			m_IsPlatForm=checkIsPlatForm(rs);
			System.out.println("日志表模式："+(m_IsPlatForm?"平台模式":"普通模式"));
			m_hasTable = true;
			return true;
			}
		} catch (Exception e) {
			if (e.getMessage().contains("无效的表或视图名")) {
				try {
					createTable();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else
			{m_useDBLOG=false;}
		} finally {
			closeConnection(conn);
		}
		return false;
	}
	private boolean checkIsPlatForm(ResultSet p_Restlt) throws Exception
	{
		ResultSetMetaData rsmd=p_Restlt.getMetaData();
		int cols = rsmd.getColumnCount();
		int platformColumnCount=0;
		for (int i = 1; i <= cols; i++) {
			String colName = rsmd.getColumnName(i);
			if("ID_".equals(colName)||"REV_".equals(colName))
			{
				platformColumnCount++;
			}
		}
		return 2==platformColumnCount;
	}
	private void createTable() {
		Connection conn = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE LOGINFO ( ");
			sb.append(" ID_ VARCHAR(64) PRIMARY KEY, ");
			sb.append(" REV_ NUMBER, ");
			sb.append(" USERNAME VARCHAR(100), ");
			sb.append(" LOGTIME TIMESTAMP(6) DEFAULT SYSDATE, ");
			sb.append(" LOGTYPE VARCHAR(100), ");
			sb.append(" BIZTYPE VARCHAR(500), ");
			sb.append(" LOGCONTENT TEXT);");

			conn = getConnection();
			conn.createStatement().executeUpdate(sb.toString());

		} catch (Exception e) {
			m_useDBLOG=false;
			System.out.println("日志表创建失败，不影响使用。");
		} finally {
			closeConnection(conn);
		}

	}

	protected void closeConnection(Connection con) {
		try {
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			errorHandler.error("Error closing connection", e,
					ErrorCode.GENERIC_FAILURE);
		}
	}

	protected Connection getConnection() throws SQLException {
		try {
			IDataAccess dass=DataBaseFactory.getDefault();
			if(null!=dass){
			connection = dass.createConnection();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return connection;
	}

	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * @param jndiName
	 *            the jndiName to set
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	@Override
	protected void append(LoggingEvent e) {
		if(!m_useDBLOG)return ;
		
		
		if(!checkTable())
		{
			return ;
		}
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		if (e.getMessage() instanceof LogEntity) {
			try {
				LogEntity entity = (LogEntity) e.getMessage();
				conn = getConnection();
				if (null == conn) {
					return;
				}
				String sql = getInserSql(m_IsPlatForm);
				pstmt = conn.prepareStatement(sql);
				setParams(pstmt,entity,m_IsPlatForm);
				pstmt.executeUpdate();
			} catch (Exception ex) {
				this.errorHandler.error("log into database failed!", ex, 0);
			} finally {
				try {
					pstmt.close();
				} catch (SQLException e1) {
				}
				try {
					conn.close();
				} catch (SQLException e1) {
				}
			}
		}
	}
	
	
	private void setParams(PreparedStatement p_Statement,LogEntity p_Entity,boolean p_IsPlatForm) throws Exception
	{
		if(p_IsPlatForm)
		{
			p_Statement.setObject(1,GUID.newGUID());
			p_Statement.setObject(2, System.currentTimeMillis());
			p_Statement.setObject(3, p_Entity.getUsername());
			p_Statement.setObject(4, p_Entity.getLogtype());
			p_Statement.setObject(5, p_Entity.getBiztype());
			p_Statement.setObject(6, p_Entity.getLogcontent());
			
		}
		else{
		p_Statement.setObject(1, p_Entity.getUsername());
		p_Statement.setObject(2, p_Entity.getLogtype());
		p_Statement.setObject(3, p_Entity.getBiztype());
		p_Statement.setObject(4, p_Entity.getLogcontent());
		}
	}
	private String getInserSql(boolean p_IsPlatForm)
	{
		if(p_IsPlatForm){
		return "insert into LogInfo(ID_,REV_,USERNAME, LOGTYPE, BIZTYPE, LOGCONTENT) values(?,?,?,?,?,?) ";
		}
		else
		{
			return "insert into LogInfo(USERNAME, LOGTYPE, BIZTYPE, LOGCONTENT) values(?,?,?,?) ";
		}
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

}

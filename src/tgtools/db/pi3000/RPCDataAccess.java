package tgtools.db.pi3000;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.rpc.RPCClient;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;

public class RPCDataAccess implements tgtools.db.IDataAccess {

	private RPCClient m_Client;
	private String m_DataBaseType = "PI3000";
	private String m_Url="";
	public RPCDataAccess()
	{}
	public RPCDataAccess(String p_Url)
	{
		m_Url=p_Url;
		try {
			init(p_Url);
			LogHelper.info("","初始化参数："+p_Url,"RPCDataAccess");
		}
		catch (Exception e)
		{
			LogHelper.error("","初始化失败,参数："+p_Url,"RPCDataAccess",e);
		}
	}
	@Override
	public void setDataBaseType(String p_DataBaseType) {
		m_DataBaseType=p_DataBaseType;
	}

	@Override
	public String getDataBaseType() {
		return m_DataBaseType;
	}

	@Override
	public String getUrl() {
		return m_Url;
	}
	@Override
	public DataSource getDataSource() {
		return null;
	}

	@Override
	public ResultSet executeQuery(String sql) throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");

	}

	@Override
	public DataTable Query(String sql) throws APPErrorException {
		WsDsResult result=new WsDsResult();
		m_Client.invoke("executeQuery", new Object[]{sql},result);
		if(result.isSuccessful())
		{
			return result.getTable();
		}
		else
		{
			throw new APPErrorException("查询数据失败！原因："+result.getResultHint());
			
		}
	}

	@Override
	public int executeUpdate(String sql) throws APPErrorException {
		WsDsResult result=new WsDsResult();
		m_Client.invoke("executeQuery", new Object[]{sql},result);
		if(result.isSuccessful())
		{
			return result.getRows();
		}
		else
		{
			throw new APPErrorException("查询数据失败！原因："+result.getResultHint());
		}
	}

	@Override
	public int[] executeBatch(String[] sqls) throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");

	}

	@Override
	public boolean init(Object... params) throws APPErrorException {
		try {
			m_Client = new RPCClient(params[0].toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Connection createConnection() throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");
	}

	@Override
	public DataTable Query(String sql, Object[] p_Params)
			throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");
	}

	@Override
	public int executeUpdate(String sql, Object[] p_Params)
			throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");
	}

	@Override
	public int executeBlob(String sql, byte[] p_Params)
			throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");
	}

	@Override
	public int[] executeSqlFile(String s) throws APPErrorException {
		throw new APPErrorException("executeQuery没有实现");
	}

	@Override
	public boolean executeBatchByTransaction(String[] sqls, int level) throws APPErrorException {
		throw new APPErrorException("未实现");
	}

	public static void main(String[] args)
	{
		try {
			tgtools.db.DataBaseFactory.add("PI3000SecondDatatest",new Object[]{"http://172.17.3.12:7001/MWSecondDataAccess/services/dataservice"});
			DataTable dt= tgtools.db.DataBaseFactory.getDefault().Query("select * from MW_APP.DEMO");
			System.out.println(dt.getRows().size());
		} catch (APPErrorException e) {
			e.printStackTrace();
		}

	}
}

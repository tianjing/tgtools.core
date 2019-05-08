package tgtools.db.pi3000;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.net.rpc.RPCClient;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

public class RPCDataAccess implements tgtools.db.IDataAccess {

    private RPCClient m_Client;
    private String m_DataBaseType = "PI3000";
    private String m_Url = "";

    public RPCDataAccess() {
    }

    public RPCDataAccess(String p_Url) {
        m_Url = p_Url;
        try {
            init(p_Url);
            LogHelper.info("", "初始化参数：" + p_Url, "RPCDataAccess");
        } catch (Exception e) {
            LogHelper.error("", "初始化失败,参数：" + p_Url, "RPCDataAccess", e);
        }
    }

    public static void main(String[] args) {
        try {
            tgtools.db.DataBaseFactory.add("PI3000SecondDatatest", new Object[]{"http://217.0.0.1/MWWebSite/services/dataservice"});

            //DataTable dt= tgtools.db.DataBaseFactory.getDefault().Query("select * from MW_SYS.MWT_IS_DBIT");
            tgtools.db.DataBaseFactory.getDefault().executeUpdate("UPDATE MW_SYS.MWT_IS_DBIT SET DBIT_ID='BLOB' WHERE DBIT_ID='BLOB'");
            System.out.println("");
        } catch (APPErrorException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getDataBaseType() {
        return m_DataBaseType;
    }

    @Override
    public void setDataBaseType(String p_DataBaseType) {
        m_DataBaseType = p_DataBaseType;
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
        throw new APPErrorException("executeQuery 没有实现");

    }


    @Override
    public int executeUpdate(String sql) throws APPErrorException {
        WsDsResult result = new WsDsResult();
        m_Client.invoke("executeNonQuery", new Object[]{sql}, result);
        if (result.isSuccessful()) {
            return result.getRows();
        } else {
            throw new APPErrorException("查询数据失败！原因：" + result.getResultHint());
        }
    }

    @Override
    public int[] executeBatch(String[] sqls) throws APPErrorException {
        throw new APPErrorException("executeBatch 没有实现");

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
        throw new APPErrorException("createConnection 没有实现");
    }


    @Override
    public int executeUpdate(String sql, Object[] p_Params)
            throws APPErrorException {
        throw new APPErrorException("executeUpdate 没有实现");
    }

    @Override
    public int executeUpdate(String sql, Object[] p_Params, boolean pUseSetInputStream) throws APPErrorException {
        throw new APPErrorException("executeUpdate 没有实现");
    }

    @Override
    public int executeBlob(String sql, byte[] p_Params)
            throws APPErrorException {
        throw new APPErrorException("executeBlob 没有实现");
    }

    @Override
    public int[] executeSqlFile(String s) throws APPErrorException {
        throw new APPErrorException("executeSqlFile 没有实现");
    }

    @Override
    public boolean executeBatchByTransaction(String[] sqls, int level) throws APPErrorException {
        throw new APPErrorException("executeBatchByTransaction 未实现");
    }

    @Override
    public DataTable Query(String sql) throws APPErrorException {
        return query(sql);
    }

    @Override
    public DataTable Query(String sql, boolean p_BlobUseStream) throws APPErrorException {
        throw new APPErrorException("Query流没有实现");
    }

    @Override
    public <T> T Query(String sql, Class<T> p_Class) throws APPErrorException {
        return query(sql, p_Class);
    }

    @Override
    public DataTable Query(String sql, Object[] p_Params)
            throws APPErrorException {
        throw new APPErrorException("Query 没有实现");
    }


    @Override
    public DataTable query(String sql, boolean p_BlobUseStream) throws APPErrorException {
        throw new APPErrorException("Query 没有实现");
    }

    @Override
    public DataTable query(String sql, Object[] p_Params) throws APPErrorException {
        throw new APPErrorException("Query 没有实现");
    }

    @Override
    public DataTable query(String sql) throws APPErrorException {
        WsDsResult result = new WsDsResult();
        m_Client.invoke("executeQuery", new Object[]{sql}, result);
        if (result.isSuccessful()) {
            return result.getTable();
        } else {
            throw new APPErrorException("查询数据失败！原因：" + result.getResultHint());

        }
    }

    @Override
    public <T> T query(String sql, Class<T> p_Class) throws APPErrorException {
        Object obj = JsonParseHelper.parseToObject(Query(sql), p_Class, true);
        return (T) JsonParseHelper.parseToObject(Query(sql), p_Class, true);
    }
}

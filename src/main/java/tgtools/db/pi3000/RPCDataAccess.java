package tgtools.db.pi3000;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.net.rpc.RPCClient;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
/**
 * @author tianjing
 */
public class RPCDataAccess implements tgtools.db.IDataAccess {

    private RPCClient client;
    private String dataBaseType = "PI3000";
    private String url = "";

    public RPCDataAccess() {
    }



    public RPCDataAccess(String pUrl) {
        url = pUrl;
        try {
            init(pUrl);
            LogHelper.info("", "初始化参数：" + pUrl, "RPCDataAccess");
        } catch (Exception e) {
            LogHelper.error("", "初始化失败,参数：" + pUrl, "RPCDataAccess", e);
        }
    }

    public int getConnectTimeout() {
        return client.getConnectTimeout();
    }

    public void setConnectTimeout(int pTimeOut) {
        client.setConnectTimeout(pTimeOut);
    }

    public int getReadTimeout() {
        return client.getReadTimeout();
    }

    public void setReadTimeout(int pTimeOut) {
        client.setReadTimeout(pTimeOut);
    }


    @Override
    public String getDataBaseType() {
        return dataBaseType;
    }

    @Override
    public void setDataBaseType(String pDataBaseType) {
        dataBaseType = pDataBaseType;
    }

    @Override
    public String getUrl() {
        return url;
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
        client.invoke("executeNonQuery", new Object[]{sql}, result);
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
            client = new RPCClient(params[0].toString());
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
    public int executeUpdate(String sql, Object[] pParams)
            throws APPErrorException {
        throw new APPErrorException("executeUpdate 没有实现");
    }

    @Override
    public int executeUpdate(String sql, Object[] pParams, boolean pUseSetInputStream) throws APPErrorException {
        throw new APPErrorException("executeUpdate 没有实现");
    }

    @Override
    public int executeBlob(String sql, byte[] pParams)
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
    public DataTable Query(String sql, boolean pBlobUseStream) throws APPErrorException {
        throw new APPErrorException("Query流没有实现");
    }

    @Override
    public <T> T Query(String sql, Class<T> pClass) throws APPErrorException {
        return query(sql, pClass);
    }

    @Override
    public DataTable Query(String sql, Object[] pParams)
            throws APPErrorException {
        throw new APPErrorException("Query 没有实现");
    }


    @Override
    public DataTable query(String sql, boolean pBlobUseStream) throws APPErrorException {
        throw new APPErrorException("Query 没有实现");
    }

    @Override
    public DataTable query(String sql, Object[] pParams) throws APPErrorException {
        throw new APPErrorException("Query 没有实现");
    }

    @Override
    public DataTable query(String sql) throws APPErrorException {
        WsDsResult result = new WsDsResult();
        client.invoke("executeQuery", new Object[]{sql}, result);
        if (result.isSuccessful()) {
            return result.getTable();
        } else {
            throw new APPErrorException("查询数据失败！原因：" + result.getResultHint());

        }
    }

    @Override
    public <T> T query(String sql, Class<T> pClass) throws APPErrorException {
        Object obj = JsonParseHelper.parseToObject(Query(sql), pClass, true);
        return (T) JsonParseHelper.parseToObject(Query(sql), pClass, true);
    }
}

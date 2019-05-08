package tgtools.db;

import java.sql.Connection;
import java.sql.ResultSet;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;

import javax.sql.DataSource;

public interface IDataAccess {

	/**
	 * 获取数据库设置
	 * @param p_DataBaseType
	 * @return
     */
	void setDataBaseType(String p_DataBaseType);
	/**
	 * 取数据库类型
	 * @return
     */
	String getDataBaseType();

	/**
	 * 获取数据库连接
	 * @return
     */
	String getUrl();
	/**
	 * 获取数据源
	 * @return
     */
	DataSource getDataSource();
	/**
	 * 查询并返回ResultSet（不推荐）
	 * @param sql
	 * @return
	 * @throws APPErrorException
	 */
	ResultSet executeQuery(String sql)throws APPErrorException;
	/**
	 * 查询并返回table对象（推荐，Blob 字段 使用byte[] 适合小文件数据获取）
	 * @param sql
	 * @return
	 * @throws APPErrorException
	 */
	@Deprecated
	DataTable Query(String sql)throws APPErrorException;

	/**
	 * 查询并返回table对象（Blob 字段 使用stream 适合大文件数据获取）
	 * @param sql
	 * @param p_BlobUseStream Blob字段是否使用stream
	 * @return
	 * @throws APPErrorException
	 */
	@Deprecated
	DataTable Query(String sql,boolean p_BlobUseStream)throws APPErrorException;
	/**
	 * 查询并返回table对象
	 * @param sql
	 * @param p_Class
	 * @param <T>
	 * @return
	 * @throws APPErrorException
     */
	@Deprecated
	<T>T Query(String sql,Class<T> p_Class)throws APPErrorException;

	/**
	 * 查询并返回table对象（推荐 参数查询）
	 * @param sql (如  select * from table where id=? and name=?)
	 * @p_Params (如 数组第一个值对应 sql 第一个 ? 以此类推)
	 * @return
	 * @throws APPErrorException
	 */
	@Deprecated
	DataTable Query(String sql,Object[] p_Params)throws APPErrorException;

	/**
	 * 执行一个sql 如 update insert
	 * @param sql
	 * @return
	 * @throws APPErrorException
	 */
	int executeUpdate(String sql) throws APPErrorException;
	/**
	 * 批量执行sql
	 * @param sqls
	 * @return
	 * @throws APPErrorException
	 */
	int[] executeBatch(String[] sqls) throws APPErrorException;
	/**
	 * 初始化
	 * @param params
	 * @return
	 * @throws APPErrorException
	 */
	boolean init(Object... params) throws APPErrorException;
	/**
	 * 关闭
	 */
	void close();
	/**
	 * 创建一个连接对象
	 * @return
	 */
	Connection createConnection()throws APPErrorException;
	

	/**
	 * 执行一个sql 如 update insert
	 * @param sql (如  update table set sex=? ,name=? whwere id=? )
	 * @p_Params (如 数组第一个值对应 sql 第一个 ? 以此类推)
	 * @return
	 * @throws APPErrorException
	 */
	int executeUpdate(String sql,Object[] p_Params) throws APPErrorException;
	/**
	 * 执行一个sql 如 update insert
	 * @param sql (如  update table set sex=? ,name=? whwere id=? )
	 * @p_Params (如 数组第一个值对应 sql 第一个 ? 以此类推)
	 * @pUseSetInputStream 是否使用 setInputStream 方法处理流 有的驱动不支持
	 * @return
	 * @throws APPErrorException
	 */
	int executeUpdate(String sql,Object[] p_Params,boolean pUseSetInputStream) throws APPErrorException;
	/**
	 * 更新大字段
	 * @param sql （update 之类）
	 * @param p_Params 二进制数据
	 * @return
	 * @throws APPErrorException
	 */
	int executeBlob(String sql,byte[] p_Params) throws APPErrorException;
	/**
	 * 
	 * @param p_Sqls
	 * @return
	 * @throws APPErrorException
	 */
	int[] executeSqlFile(String p_Sqls) throws APPErrorException;

	/**
	 *
	 * @param sqls
	 * @return
	 * @throws APPErrorException
     */
	boolean executeBatchByTransaction(String[] sqls,int level) throws APPErrorException;

	/**
	 * 查询并返回table对象（Blob 字段 使用stream 适合大文件数据获取）
	 * @param sql
	 * @param p_BlobUseStream Blob字段是否使用stream
	 * @return
	 * @throws APPErrorException
	 */
	DataTable query(String sql,boolean p_BlobUseStream)throws APPErrorException;

	/**
	 * 查询并返回table对象（推荐 参数查询）
	 * @param sql (如  select * from table where id=? and name=?)
	 * @p_Params (如 数组第一个值对应 sql 第一个 ? 以此类推)
	 * @return
	 * @throws APPErrorException
	 */
	DataTable query(String sql,Object[] p_Params)throws APPErrorException;
	/**
	 * 查询并返回table对象（推荐，Blob 字段 使用byte[] 适合小文件数据获取）
	 * @param sql
	 * @return
	 * @throws APPErrorException
	 */
	DataTable query(String sql)throws APPErrorException;
	/**
	 * 查询并返回table对象
	 * @param sql
	 * @param p_Class
	 * @param <T>
	 * @return
	 * @throws APPErrorException
	 */
	<T>T query(String sql,Class<T> p_Class)throws APPErrorException;

}

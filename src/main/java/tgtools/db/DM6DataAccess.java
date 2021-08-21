package tgtools.db;



import tgtools.exceptions.APPErrorException;


public class DM6DataAccess extends DMDataAccess {

	@Override
	protected void initDataSource() throws APPErrorException {
		initDataSource("dm6.jdbc.pool.DmdbDataSource");
	}

}

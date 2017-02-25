package tgtools.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import tgtools.exceptions.APPErrorException;

import javax.sql.DataSource;

public class DM6DataAccess extends DMDataAccess implements IDataAccess {

	@Override
	protected void initDataSource() throws APPErrorException {
		initDataSource("dm6.jdbc.pool.DmdbDataSource");
	}

	public static void main(String[] args)
	{
		DM6DataAccess ass=new DM6DataAccess();
		try {
			ass.init("jdbc:dm6://192.168.1.254:12345/oms","MW_SYS","MW_SYS");
			System.out.println(ass.Query("SELECT 1 AS A").getRow(0).getValue("A"));
			System.out.println("11111111111111111111");
		} catch (APPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}

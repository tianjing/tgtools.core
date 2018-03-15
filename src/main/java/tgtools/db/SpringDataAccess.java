package tgtools.db;

import tgtools.exceptions.APPErrorException;

import javax.sql.DataSource;
import java.lang.reflect.Method;

public class SpringDataAccess extends DataSourceDataAccess {

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
}

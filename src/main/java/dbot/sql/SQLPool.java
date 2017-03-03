package dbot.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dbot.Statics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


/**
 * Created by Niklas on 22.10.2016.
 */
public final class SQLPool {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.sql.SQLPool");
	private static final DataSource dataSource;
	//private static final HikariDataSource ds;

	/*static {
		String fileName = "hikari.properties";
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());//TODO: irgendwie NPE loggen
		LOGGER.info("HikariFile Found: {}", file.exists());
		HikariConfig config = new HikariConfig(file.getAbsolutePath());
		config.setMaximumPoolSize(5);
		config.setAutoCommit(false);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		ds = new HikariDataSource(config);
	}*/

	private SQLPool() {}

	static {
		HikariConfig config = new HikariConfig();//TODO: config from file
		config.setDriverClassName("org.mariadb.jdbc.Driver");
		//config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
		//config.setDataSourceClassName("org.mariadb.jdbc.Driver");
		config.setJdbcUrl(Statics.DB_URL);
		config.setUsername(Statics.DB_USER);
		config.setPassword(Statics.DB_PASS);
		config.setMaximumPoolSize(5);
		config.setAutoCommit(false);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

}
package dbot.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dbot.Statics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;


/**
 * Created by Niklas on 22.10.2016.
 */
public class SQLPool {
	private static final Logger LOGGER = LoggerFactory.getLogger("dbot.sql.SQLPool");
	private static final DataSource dataSource;

	private SQLPool() {}

	static {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("org.mariadb.jdbc.Driver");
		//config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
		//config.setDataSourceClassName("ork.mariadb.jdbc.Driver");
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

	public static ArrayList<SQLData> getScoreList() {//TODO: List statt ArrayList?
		String strings[] = {"name", "level", "exp"};
		String query = "SELECT `name`, `level`, `exp` FROM `users` ORDER BY `level` DESC, `exp` DESC";
		ArrayList<SQLData> dataList = new ArrayList<>();
		try(Connection conn = getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					Object data[] = new Object[3];
					data[0] = resultSet.getObject("name");
					data[1] = resultSet.getObject("level");
					data[2] = resultSet.getObject("exp");
					SQLData sqlData = new SQLData(strings, data);
					dataList.add(sqlData);
				}
			}
		} catch(SQLException e) {
			LOGGER.error("SQL failed in getScoreList", e);
		}
		return dataList;
	}

	/*public Connection getConnection() {
		Connection con = null;
		try {
			con = CPDC.getConnection();
		} catch(SQLException e) {
			System.out.println(e);
		}
		return con;
	}*/

}
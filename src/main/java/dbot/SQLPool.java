package dbot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Niklas on 22.10.2016.
 */
public class SQLPool {
	private static final DataSource dataSource;

	static {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("org.mariadb.jdbc.Driver");
		//config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
		//config.setDataSourceClassName("ork.mariadb.jdbc.Driver");
		config.setJdbcUrl(Statics.DB_URL);
		config.setUsername(Statics.DB_USER);
		config.setPassword(Statics.DB_PASS);
		config.setMaximumPoolSize(1);
		config.setAutoCommit(false);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

	/*public static Object getData(String id, String data) {
		//String query = "SELECT ? FROM `users` WHERE `id` = ?";

		//String query = "SELECT * FROM `users` WHERE `exp` < 1000";
		String query = "SELECT `" + data + "` FROM `users` WHERE `id` = ?";
		//String query = "SELECT `gems` FROM `users` WHERE `id` = ?";
		try(Connection conn = getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			//statement.setString(1, "`" + data + "`");
			statement.setString(1, id);
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					System.out.println(resultSet.getObject(data));
					return resultSet.getObject(data);
				}
			}
		} catch(SQLException e) {
			System.out.println(e);
		}
		return null;
	}*/

	public static SQLData getData(String id, String[] strings) {//TODO: classe erstellen mit methode get(string rowName)
		Object data[] = new Object[strings.length];
		String query = "SELECT ";
		for (int i = 0; i < strings.length; i++) {
			if (i == strings.length-1) {
				query += "`" + strings[i] + "` ";
			} else {
				query += "`" + strings[i] + "`, ";
			}
		}
		query += "FROM `users` WHERE `id` = ?";//TODO: limit 1
		try(Connection conn = getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, id);
			try(ResultSet resultSet = statement.executeQuery()) {
				while(resultSet.next()) {
					for (int i = 0; i < data.length; i++) {
						data[i] = resultSet.getObject(i+1);//TODO: i+1 oder string[i]?
						System.out.println(data[i]);
					}
				}
			}
		} catch(SQLException e) {
			System.out.println(e);
			return null;//TODO: nötig?
		}
		return new SQLData(strings, data);
	}

	public static void updateData(String id, SQLData data) {
		//String query = "FROM `users` WHERE `id` = ?";//TODO: limit 1
		String query = "UPDATE `users` SET ";
		for (int i = 0; i < data.size(); i++) {
			query += " `" + data.getString(i) + "`";
			if (i != data.size() - 1) query += ",";
		}
		query += " WHERE `id` = ?";
		try(Connection conn = getDataSource().getConnection(); PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, id);
			statement.executeQuery();
			/*try(ResultSet resultSet = statement.executeQuery()) {

			}*/
		} catch(SQLException e) {
			System.out.println(e);
		}
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
			e.printStackTrace();
		}
		return dataList;
	}

	private SQLPool() {
	}

	/*public static SQLPool getInstance() {
		return INSTANCE;
	}

	public Connection getConnection() {
		Connection con = null;
		try {
			con = CPDC.getConnection();
		} catch(SQLException e) {
			System.out.println(e);
		}
		return con;
	}

	public ResultSet getResultSet(String query) {
		Connection conn = getConnection();
		if (conn == null) return null;
		try(Statement statement = conn.createStatement()){
			statement.closeOnCompletion();
			return statement.getResultSet();
		} catch(SQLException e) {
			System.out.println(e);
		}
		return null;
	}


	public void test(String query) {
		try(Connection conn = CPDC.getConnection(); Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

		} catch(SQLException e) {
			System.out.println(e);
		}
	}

	public Object getData(String id, String data) {
		String query = "SELECT ? FROM `users` WHERE `id` = ?";
		try(Connection conn = CPDC.getConnection(); PreparedStatement statement = conn.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {
			statement.setString(1, "gems");
			statement.setString(2, "97092184821465088");
			System.out.println(resultSet.next());
		} catch(SQLException e) {
			System.out.println(e);
		}
		return null;
	}*/

}



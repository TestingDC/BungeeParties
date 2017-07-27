package Core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLConnector {
	
	private final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	private final String DATABASE = "supercraft";
	private final String HOST = "localhost";
	private final int PORT = 3306;
    private final String USERNAME = "root";
    private final String PASSWORD = "xH5o*g@S1dd#f5JkOK^-"; // FIX THIS TODO change to another account

    private Connection connection;

    public void createTable() {
    	String partyTable = "CREATE TABLE Parties ("
    			+ "uuid CHAR(36),"
    			+ "inparty TINYINT(0),"
    			+ "partyname TEXT)";
    	try {
			prepStatement(partyTable).execute();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error while making Database Table " + e);
		}
    }
    
    public Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + this.HOST+ ":" + this.PORT + "/" + this.DATABASE, this.USERNAME, this.PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public PreparedStatement prepStatement(String sql) {
    	try {
    		PreparedStatement statement = connect().prepareStatement(sql);
    		return statement;
    	} catch (Exception e) {
    		System.out.println("(SQLConnector) Error while preparing statement: " + e);
    	}
    	return null;
    }
}

package Core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartySQLConnector {
	
	private final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	private final String DATABASE = "supercraft";
	private final String TABLE = "BungeeParties";
	private final String HOST = "localhost";
	private final int PORT = 3306;
    private final String USERNAME = "root";
    private final String PASSWORD = ""; // FIX THIS TODO change to another account

    private Connection connection;

    public void createTable() {
    	String partyTable = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" + "uuid CHAR(36)," + "inparty TINYINT(0)," + "partyname TEXT," + "poolnumber INT(0))";
    	try {
			prepStatement(partyTable).execute();
		} catch (SQLException e) {
			System.out.println("Error while making Database Table " + e);
		}
    }
    
    public boolean isPlayerInTable(ProxiedPlayer player) {
    	String request = "SELECT EXISTS(SELECT * FROM " + TABLE + " WHERE uuid = \"" + player.getUniqueId().toString() + "\")";
    	try {
			ResultSet set = prepStatement(request).executeQuery();
			if(set.next()) {
				int amount = set.getInt(1);
				if(amount >= 1) {
					return true;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error while checking for player in table: " + e);
		}
    	return false;
    }
    
    public boolean isPlayerInParty(ProxiedPlayer player) {
    	String request = "SELECT * FROM " + TABLE + " WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	try {
			ResultSet set = prepStatement(request).executeQuery();
			if(set.next()) {
				boolean bool = set.getBoolean(2);
				return bool;
			}
		} catch (SQLException e) {
			System.out.println("Error while checking for is player in party: " + e);
		}
    	return false;
    }
    
    public String getPartyNameFromPlayer(ProxiedPlayer player) {
    	String request = "SELECT * FROM " + TABLE + " WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	try {
			ResultSet set = prepStatement(request).executeQuery();
			if(set.next()) {
				String partyName = set.getString(3);
				return partyName;
			}
		} catch (SQLException e) {
			System.out.println("Error while checking for party name from player: " + e);
		}
    	return null;
    }
    
    public int getPoolNumberFromPlayer(ProxiedPlayer player) {
    	String request = "SELECT * FROM " + TABLE + " WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	try {
			ResultSet set = prepStatement(request).executeQuery();
			if(set.next()) {
				int poolNumber = set.getInt(4);
				return poolNumber;
			}
		} catch (SQLException e) {
			System.out.println("Error while checking for pool number from player: " + e);
		}
    	return -1;
    }
    
    public int getPoolNumberFromPartyName(String partyName) {
    	String request = "SELECT * FROM " + TABLE + " WHERE partyname = \"" + partyName + "\"";
    	try {
			ResultSet set = prepStatement(request).executeQuery();
			if(set.next()) {
				int poolNumber = set.getInt(4);
				return poolNumber;
			}
		} catch (SQLException e) {
			System.out.println("Error while checking for pool number from party name: " + e);
		}
    	return -1;
    }
    
    public void setPartyNameForPlayer(ProxiedPlayer player, String partyName) {
    	String request = "UPDATE " + TABLE + " SET partyname = \"" + partyName + "\" WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	try {
			prepStatement(request).execute();
		} catch (SQLException e) {
			System.out.println("Error while setting party name for player: " + e);
		}
    }
    
    public void setPoolNumberForPlayer(ProxiedPlayer player, int poolNumber) {
    	String request = "UPDATE " + TABLE + " SET poolnumber = " + poolNumber + " WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	try {
			prepStatement(request).execute();
		} catch (SQLException e) {
			System.out.println("Error while setting pool number for player: " + e);
		}
    }
    
    public void setPartyStatusForPlayer(ProxiedPlayer player, boolean status) {
    	String request = "";
    	if(status) {
    		request = "UPDATE " + TABLE + " SET inparty = 1 WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	} else {
    		request = "UPDATE " + TABLE + " SET inparty = 0 WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
    	}
    	try {
			prepStatement(request).execute();
		} catch (SQLException e) {
			System.out.println("Error while setting party status for player: " + e);
		}
    }
    
    public void addBlankPlayer(ProxiedPlayer player) {
    	String request = "INSERT INTO " + TABLE + "(uuid,inparty,partyname,poolnumber) VALUES (\"" + player.getUniqueId().toString() + "\",0,NULL,-1)";
    	player.sendMessage(new ComponentBuilder("Creating new Data Entry for " + player.getDisplayName()).color(ChatColor.YELLOW).create());
    	try {
			prepStatement(request).execute();
		} catch (SQLException e) {
			System.out.println("Error while inserting player: " + e);
		}
    }
    
    public Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + this.HOST+ ":" + this.PORT + "/" + this.DATABASE, this.USERNAME, this.PASSWORD);
            } catch (Exception e) {
                System.out.println("Failed to Connect. Fatal: " + e);
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
    		connect();
    		System.out.println("(SQLConnector) Error while preparing statement: " + e);
    	}
    	return null;
    }
}

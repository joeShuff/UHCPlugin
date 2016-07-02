package me.JoeShuff.KoO.DataTracker;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MySQL
{
  private static Connection connection;
  private static Statement s;
  
  private static JavaPlugin plugin;
  
  private static FileConfiguration sqlData;
  
  public MySQL(String ip, String name, String user, String pass, JavaPlugin plugin)
  {
	 // sqlData = plugin.getMySQLData();
	  
	  this.plugin = plugin;
	  
    try
    {
    	String DB_NAME = "jdbc:mysql://" + ip + ":3306/" + name + "?autoReconnect=true&interactive_timeout=" + Integer.MAX_VALUE;
    	
        Class.forName("com.mysql.jdbc.Driver");
        
        System.out.println("Connecting to database");
        
        connection = DriverManager.getConnection(DB_NAME, user, pass);
        
        System.out.println("Connected to database");
        
        s = connection.createStatement();
        
        System.out.println("Created a statement");
        
        DatabaseMetaData dm = connection.getMetaData();
        
        System.out.println("Attempting to find table " + DataTracker.tableName);
        
        ResultSet tables = dm.getTables(null, null, DataTracker.tableName, null);
        
        if (!tables.next())
        {
        	s.executeUpdate("CREATE TABLE `" + DataTracker.tableName + "` (`playername` char(40),`damage` int(255),`kills` int(255),`killer` char(40),`gapples-eaten` int(255),`blocks-placed` int(255),`blocks-broken` int(255),`pve-kills` int(255),`distance` int(255),`firstDamage` char(40),`ironMan` char(40));");
        }
    }
    catch (Exception e)
    {
    	System.out.println("Couldn't connect to database {" + e.getMessage() + "}");
    }
  }
  
  public static void openConnection()
  {
	    String ip = sqlData.getString("ip");
	    String name = sqlData.getString("name");
	    String user = sqlData.getString("username");
	    String pass = sqlData.getString("password");
	    new MySQL(ip, name, user, pass,plugin);
  }
  
  public static Connection getConnection()
  {
	  	return connection;
  }	
}

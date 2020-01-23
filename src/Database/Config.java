package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;

/*
    Class to manage database connections
*/
public class Config {
    
    private String url;
    private String username;
    private String password;
    private Connection db;
    
    public Config(){
        
    }
    
    public Config(String url, String username, String password) throws SQLException{
        this.url = url;
        this.username = username;
        this.password = password;
        db =DriverManager.getConnection(url, username, password);
    }
    
    public Connection getDatabaseConnection (){
        return db;
    }
    
    
    
}

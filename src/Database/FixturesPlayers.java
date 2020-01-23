/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author anyone
 */
public class FixturesPlayers {
    
    private Connection db;
    
    public FixturesPlayers(){
        
    }
    
    public FixturesPlayers(Connection db){
        this.db = db;
    }
    
    
    public  void addFixturesPlayers(JSONArray fixturesPlayers) throws IOException{
        
        
        
        
        for(Object obj:fixturesPlayers){
            JSONObject tempObject = (JSONObject) obj;   
                 
            try {
                // the mysql insert statement
                String query = "insert into fixtures_players(fixture_id, player_id, position, type, formation_position, captain)"
                        + " values (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                        + " fixture_id=VALUES(fixture_id), player_id=VALUES(player_id), position=VALUES(position), type=VALUES(type), formation_position=VALUES(formation_position), captain=VALUES(captain)";


                
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setInt(1, tempObject.getInt("fixture_id"));
                
                if(!tempObject.get("player_id").toString().equals("null"))
                    preparedStmt.setInt(2, tempObject.getInt("player_id"));
                else
                    preparedStmt.setNull(2, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("position").toString().equals("null"))
                    preparedStmt.setString(3, tempObject.getString("position"));
                else
                    preparedStmt.setNull(3, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("type").toString().equals("null"))
                    preparedStmt.setString(4, tempObject.getString("type"));
                else
                    preparedStmt.setNull(4, java.sql.Types.VARCHAR);
                
               if(!tempObject.get("formation_position").toString().equals("null"))
                    preparedStmt.setInt(5, tempObject.getInt("formation_position"));
                else
                    preparedStmt.setNull(5, java.sql.Types.VARCHAR);
               
               if(!tempObject.get("captain").toString().equals("null"))
                    preparedStmt.setBoolean(6, tempObject.getBoolean("captain"));
                else
                    preparedStmt.setBoolean(6, false);
               

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }

        }

        
    }
    
    
    
    
    
}

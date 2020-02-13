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
 * @author Jacob Skerritt
 * 
 * The following class is used to add corners from a fixture into the database
 */
public class FixturesCorners {
    
    private Connection db;
    
    public FixturesCorners(){
        
    }
    
    public FixturesCorners(Connection db){
    
        this.db= db;
    
    }
    
    //Method used to add corners into the database for a fixture, exitsting records are updated if changes occur
    public  void addFixturesCorners(JSONArray fixturesCorners) throws IOException{
        
        
       
        
        for(Object obj:fixturesCorners){
            JSONObject tempObject = (JSONObject) obj;   
                 
            try {
                // the mysql insert statement
                String query = "insert into fixtures_corners(id, minute, team_id, fixture_id)"
                        + " values (?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                        + " minute=VALUES(minute), team_id=VALUES(team_id), fixture_id=VALUES(fixture_id)";


                
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                
                
                
                preparedStmt.setInt(1, tempObject.getInt("id"));
                
                
                if(!tempObject.get("minute").toString().equals("null"))
                    preparedStmt.setInt(2, tempObject.getInt("minute"));
                else
                    preparedStmt.setNull(2, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("team_id").toString().equals("null"))
                    preparedStmt.setInt(3, tempObject.getInt("team_id"));
                else
                    preparedStmt.setNull(3, java.sql.Types.VARCHAR);
                
                preparedStmt.setInt(4, tempObject.getInt("fixture_id"));

               

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }

        }

        
    }
    
}

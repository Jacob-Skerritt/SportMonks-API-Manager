
package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Jacob Skerrit
 * 
 * Class used to add a link between players and their team to the database linking table
 * 
 */

public class TeamsPlayers {
    
    private Connection db;
    
    
    public TeamsPlayers(){
        
    }
    
    public TeamsPlayers(Connection db){
        this.db = db;
    }
    
    //Method used to link players to a team in the database linking table, updates existing records if changes have occured
    public  void addTeamsPlayers(JSONArray teamsPlayers) throws IOException{
        

        for(Object obj:teamsPlayers){
            JSONObject tempObject = (JSONObject) obj;   

            try {
                // the mysql insert statement
                String query = " insert into teams_players(team_id, player_id, season_id, position_id)"
                        + " values (?, ?, ?,?) ON DUPLICATE KEY UPDATE"
                        + " team_id=VALUES(team_id), season_id=VALUES(season_id), position_id=VALUES(position_id)";


                
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setInt(1, tempObject.getInt("team_id"));
                preparedStmt.setInt(2, tempObject.getInt("player_id"));
                preparedStmt.setInt(3, tempObject.getInt("season_id"));
                
                
                if(!tempObject.get("position_id").toString().equals("null"))
                    preparedStmt.setInt(4, tempObject.getInt("position_id"));
                else
                    preparedStmt.setNull(4, java.sql.Types.VARCHAR);

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }

        }

        
    }
    
}

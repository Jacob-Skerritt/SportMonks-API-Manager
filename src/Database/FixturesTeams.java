
package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;


public class FixturesTeams {
    private Connection db;
    
    public FixturesTeams(){
        
    }
    
    public FixturesTeams(Connection db){
        this.db = db;
    }
    
    public  void addFixturesTeams(JSONArray fixturesTeams) throws IOException{
        
        
        
        for(Object obj:fixturesTeams){
            JSONObject tempObject = (JSONObject) obj;   

            try {
                // the mysql insert statement
                String query = " insert into fixtures_teams(fixture_id, team_id, winning_team, score, pen_score, colour, formation)"
                        + " values (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                        + " fixture_id=VALUES(fixture_id), team_id=VALUES(team_id), score=VALUES(score), pen_score=VALUES(pen_score), formation=VALUES(formation)";



                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setInt(1, tempObject.getInt("fixture_id"));
                preparedStmt.setInt(2, tempObject.getInt("team_id"));
                
                if(!tempObject.get("winning_team").toString().equals("null"))
                    preparedStmt.setBoolean(3, tempObject.getBoolean("winning_team"));
                else
                    preparedStmt.setNull(3, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("score").toString().equals("null"))
                    preparedStmt.setInt(4, tempObject.getInt("score"));
                else
                    preparedStmt.setInt(4, 0);
                
               if(!tempObject.get("pen_score").toString().equals("null"))
                    preparedStmt.setInt(5, tempObject.getInt("pen_score"));
                else
                    preparedStmt.setInt(5, 0);
               
                if(!tempObject.get("color").toString().equals("null"))
                    preparedStmt.setString(6, tempObject.get("color").toString());
                else
                    preparedStmt.setNull(6, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("formation").toString().equals("null"))
                    preparedStmt.setString(7, tempObject.getString("formation"));
                else
                    preparedStmt.setNull(7, java.sql.Types.VARCHAR);

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }

        }

        
    }
    
    
}

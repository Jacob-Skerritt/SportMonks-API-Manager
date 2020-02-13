
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
 * Class responsible for adding a team related to a particular fixture to the database
 */
public class FixturesTeams {
    private Connection db;
    
    public FixturesTeams(){
        
    }
    
    public FixturesTeams(Connection db){
        this.db = db;
    }
    
    //method used to add a team to the database for a particular fixture
    public  void addFixturesTeams(JSONObject teamObject) throws IOException{
        
        JSONObject statsObject = new JSONObject();
        JSONObject shotsObject = new JSONObject();
        JSONObject passesObject = new JSONObject();
        JSONObject attacksObject = new JSONObject();
        
        boolean checkStats = teamObject.has("stats");
        boolean checkAttacks = false;
        boolean checkPasses = false;
        boolean checkShots = false;
        
        
        if(checkStats){
              
        statsObject = teamObject.getJSONObject("stats");
        
        checkAttacks = statsObject.has("attacks");
        checkPasses = statsObject.has("passes");
        checkShots = statsObject.has("shots");
            
        if(checkShots && !statsObject.isNull("shots") )    
            shotsObject = statsObject.getJSONObject("shots");
        
        if(checkPasses && !statsObject.isNull("passes"))
            passesObject = statsObject.getJSONObject("passes");

        if(checkAttacks && !statsObject.isNull("attacks"))
            attacksObject = statsObject.getJSONObject("attacks");
        }
  
        try {
            // the mysql insert statement
            String query = " insert into fixtures_teams(fixture_id, team_id, winning_team, home_team, score, pen_score, colour, formation, total_shots,"
                    + " shots_on_goal, shots_blocked, total_passes, accurate_passes, total_attacks, dangerous_attacks, fouls, corners,"
                    + " offsides, possessiontime, yellowcards, redcards, yellowredcards, saves, substitutions, penalties)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                    + " fixture_id=VALUES(fixture_id), team_id=VALUES(team_id), winning_team=VALUES(winning_team), home_team=VALUES(home_team), score=VALUES(score), pen_score=VALUES(pen_score),"
                    + " colour=VALUES(colour), formation=VALUES(formation), total_passes=VALUES(total_passes),"
                    + " total_shots=VALUES(total_shots), shots_on_goal=VALUES(shots_on_goal), shots_blocked=VALUES(shots_blocked),"
                    + " total_attacks=VALUES(total_attacks), dangerous_attacks=VALUES(dangerous_attacks), fouls=VALUES(fouls), corners=VALUES(corners),"
                    + " offsides=VALUES(offsides), possessiontime=VALUES(possessiontime), yellowcards=VALUES(yellowcards), redcards=VALUES(redcards),"
                    + " yellowredcards=VALUES(yellowredcards), saves=VALUES(saves), substitutions=VALUES(substitutions), penalties=VALUES(penalties)";



            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = db.prepareStatement(query);
            preparedStmt.setInt(1, teamObject.getInt("fixture_id"));
            preparedStmt.setInt(2, teamObject.getInt("team_id"));

            if(!teamObject.get("winning_team").toString().equals("null"))
                preparedStmt.setBoolean(3, teamObject.getBoolean("winning_team"));
            else
                preparedStmt.setNull(3, java.sql.Types.VARCHAR);
            
            if(!teamObject.get("home_team").toString().equals("null"))
                preparedStmt.setBoolean(4, teamObject.getBoolean("home_team"));
            else
                preparedStmt.setNull(4, java.sql.Types.VARCHAR);

            if(!teamObject.get("score").toString().equals("null"))
                preparedStmt.setInt(5, teamObject.getInt("score"));
            else
                preparedStmt.setInt(5, 0);

           if(!teamObject.get("pen_score").toString().equals("null"))
                preparedStmt.setInt(6, teamObject.getInt("pen_score"));
            else
                preparedStmt.setInt(6, 0);

            if(!teamObject.get("color").toString().equals("null"))
                preparedStmt.setString(7, teamObject.get("color").toString());
            else
                preparedStmt.setNull(7, java.sql.Types.VARCHAR);

            if(!teamObject.get("formation").toString().equals("null"))
                preparedStmt.setString(8, teamObject.getString("formation"));
            else
                preparedStmt.setNull(8, java.sql.Types.VARCHAR);

            if(checkShots && shotsObject.has("total") && !shotsObject.get("total").toString().equals("null"))
                preparedStmt.setInt(9, shotsObject.getInt("total"));
            else
                preparedStmt.setInt(9, 0);

            if(checkShots && shotsObject.has("ongoal") && !shotsObject.get("ongoal").toString().equals("null"))
                preparedStmt.setInt(10, shotsObject.getInt("ongoal"));
            else
                preparedStmt.setInt(10, 0);

            if(checkShots && shotsObject.has("blocked") && !shotsObject.get("blocked").toString().equals("null"))
                preparedStmt.setInt(11, shotsObject.getInt("blocked"));
            else
                preparedStmt.setInt(11, 0);

            if(checkPasses && passesObject.has("total") && !passesObject.get("total").toString().equals("null"))
                preparedStmt.setInt(12, passesObject.getInt("total"));
            else
                preparedStmt.setInt(12, 0);

            if(checkPasses && passesObject.has("accurate") && !passesObject.get("accurate").toString().equals("null"))
                preparedStmt.setInt(13, passesObject.getInt("accurate"));
            else
                preparedStmt.setInt(13, 0);

            if(checkAttacks && attacksObject.has("attacks") && !attacksObject.get("attacks").toString().equals("null"))
                preparedStmt.setInt(14, attacksObject.getInt("attacks"));
            else
                preparedStmt.setInt(14, 0);

            if(checkAttacks && attacksObject.has("attacks") && !attacksObject.get("dangerous_attacks").toString().equals("null"))
                preparedStmt.setInt(15, attacksObject.getInt("dangerous_attacks"));
            else
                preparedStmt.setInt(15, 0);

            if(checkStats && !statsObject.get("fouls").toString().equals("null"))
                preparedStmt.setInt(16, statsObject.getInt("fouls"));
            else
                preparedStmt.setInt(16, 0);

            if(checkStats && !statsObject.get("corners").toString().equals("null"))
                preparedStmt.setInt(17, statsObject.getInt("corners"));
            else
                preparedStmt.setInt(17, 0);

            if(checkStats && !statsObject.get("offsides").toString().equals("null"))
                preparedStmt.setInt(18, statsObject.getInt("offsides"));
            else
                preparedStmt.setInt(18, 0);

            if(checkStats && !statsObject.get("possessiontime").toString().equals("null"))
                preparedStmt.setInt(19, statsObject.getInt("possessiontime"));
            else
                preparedStmt.setInt(19, 0);

            if(checkStats && !statsObject.get("yellowcards").toString().equals("null"))
                preparedStmt.setInt(20, statsObject.getInt("yellowcards"));
            else
                preparedStmt.setInt(20, 0);

            if(checkStats && !statsObject.get("redcards").toString().equals("null"))
                preparedStmt.setInt(21, statsObject.getInt("redcards"));
            else
                preparedStmt.setInt(21, 0);

            if(checkStats && !statsObject.get("yellowredcards").toString().equals("null"))
                preparedStmt.setInt(22, statsObject.getInt("yellowredcards"));
            else
                preparedStmt.setInt(22, 0);

            if(checkStats && !statsObject.get("saves").toString().equals("null"))
                preparedStmt.setInt(23, statsObject.getInt("saves"));
            else
                preparedStmt.setInt(23, 0);

            if(checkStats && !statsObject.get("substitutions").toString().equals("null"))
                preparedStmt.setInt(24, statsObject.getInt("substitutions"));
            else
                preparedStmt.setInt(24, 0);

            if(checkStats && !statsObject.get("penalties").toString().equals("null"))
                preparedStmt.setInt(25, statsObject.getInt("penalties"));
            else
                preparedStmt.setInt(25, 0); 



            // execute the preparedstatement
            preparedStmt.execute();
        }catch (SQLException ex) {

        }

        

        
    }
    
    
}

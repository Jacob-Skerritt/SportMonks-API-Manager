
package Database;

import SportMonks.Endpoint;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;


public class TeamsSeasons {
    
    private Connection db;
    
    
    public TeamsSeasons(){
        
    }
    
    public TeamsSeasons(Connection db){
        this.db = db;
    }
    
    
    
    public  void addTeamsSeasons(JSONArray teamsSeasons) throws IOException{
        

        for(Object obj:teamsSeasons){
            JSONObject tempObject = (JSONObject) obj;   

            try {
                // the mysql insert statement
                String query = " insert into teams_seasons(team_id, season_id)"
                        + " values (?, ?) ON DUPLICATE KEY UPDATE"
                        + " team_id=VALUES(team_id), season_id=VALUES(season_id)";



                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setInt(1, tempObject.getInt("team_id"));
                
                
                if(!tempObject.get("season_id").toString().equals("null"))
                                preparedStmt.setInt(2, tempObject.getInt("season_id"));
                             else
                                preparedStmt.setNull(2, java.sql.Types.VARCHAR);

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }

        }

        
    }
    
    public  JSONObject getTeamsSeasonsIds() throws SQLException{
      JSONObject teamsSeasons = new JSONObject();
      JSONArray teamsSeasonsArray = new JSONArray();
      String query = "SELECT * FROM teams_seasons";

      // create the java statement
      Statement st = db.createStatement();
      
      // execute the query, and get a java resultset
      ResultSet rs = st.executeQuery(query);
      
      // iterate through the java resultset
      if(rs.isBeforeFirst()){
          
      
      while (rs.next())
      {
        int teamId = rs.getInt("team_id");
        int seasonId = rs.getInt("season_id");
        
        
        JSONObject tempObject = new JSONObject();
        tempObject.put("team_id", teamId);
        tempObject.put("season_id", seasonId);
        teamsSeasonsArray.put(tempObject);
        
      }
      st.close();
      teamsSeasons.put("data", teamsSeasonsArray);
      }
      else{
          teamsSeasons.put("data", "no Data");
          
      }
      
      
    
      return teamsSeasons;
        
    }
}

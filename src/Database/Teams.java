
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


public class Teams {
    
    private Connection db;
    
    
    public Teams(){
        
    }
    
    public Teams(Connection db){
        this.db  =db;  
    }
    
    public  void manageTeams(String teamsEndpoint) throws IOException, SQLException{
            
            
        
        Seasons seasons = new Seasons(this.db);
        JSONObject seasonIds = seasons.getSeasonIds();
        JSONArray seasonsArray = seasonIds.getJSONArray("data");
        
        
        for(Object seasonId: seasonsArray){
            JSONObject tempSeason = (JSONObject) seasonId;
            int id = tempSeason.getInt("id");
            String newTeamsEndpoint = Endpoint.makeNewEndpoint(teamsEndpoint, id, "season/");
            
            TeamsSeasons teamsSeasons = new TeamsSeasons(this.db);
            
            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;
            
            
            while(!lastPage){
                JSONObject teams = Endpoint.getDataFromEndpoint(newTeamsEndpoint+i);
                System.out.println(newTeamsEndpoint+i);

                    

                JSONArray stagesArray = teams.getJSONArray("data");
                JSONObject metaData = teams.getJSONObject("meta");
                
                JSONArray teamsSeasonsArray = new JSONArray();
                

                if(metaData.has("pagination")){
                JSONObject pagination = metaData.getJSONObject("pagination");
                maxPage = pagination.getInt("total_pages");
                }


                    for(Object obj:stagesArray){
                        JSONObject tempObject = (JSONObject) obj;  
                        JSONObject teamsSeasonsObject = new JSONObject();

                        try {
                            // the mysql insert statement
                            String query = " insert into teams (id, name, short_code, country_id, national_team, founded, logo, venue_id)"
                                    + " values (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                                    + " name=VALUES(name), short_code=VALUES(short_code), country_id=VALUES(country_id), national_team=VALUES(national_team),"
                                    + " founded=VALUES(founded), logo=VALUES(logo), venue_id=VALUES(venue_id)";



                            // create the mysql insert preparedstatement
                            PreparedStatement preparedStmt = db.prepareStatement(query);
                            preparedStmt.setInt(1, tempObject.getInt("id"));
                            preparedStmt.setString(2, tempObject.getString("name"));   
                            
                            if(!tempObject.get("short_code").toString().equals("null"))
                                preparedStmt.setString(3, tempObject.getString("short_code"));
                             else
                                preparedStmt.setNull(3, java.sql.Types.VARCHAR);
                            
                            preparedStmt.setInt(4, tempObject.getInt("country_id"));
                            
                            preparedStmt.setBoolean(5, tempObject.getBoolean("national_team"));                        
                            
                            if(!tempObject.get("founded").toString().equals("null"))
                                preparedStmt.setInt(6, tempObject.getInt("founded"));
                             else
                                preparedStmt.setNull(6, java.sql.Types.VARCHAR);
                            
                            
                            preparedStmt.setString(7, tempObject.get("logo_path").toString());
                            
                            if(!tempObject.get("venue_id").toString().equals("null"))
                                preparedStmt.setInt(8, tempObject.getInt("venue_id"));
                             else
                                preparedStmt.setNull(8, java.sql.Types.VARCHAR);
                            

                            // execute the preparedstatement
                            preparedStmt.execute();
                        }catch (SQLException ex) {

                        }
                        
                        teamsSeasonsObject.put("team_id", tempObject.getInt("id"));
                        
                            
                        if(!tempObject.get("current_season_id").toString().equals("null"))
                                teamsSeasonsObject.put("season_id", tempObject.getInt("current_season_id"));
                             else
                                teamsSeasonsObject.put("season_id",  "null");
                        
                        teamsSeasonsArray.put(teamsSeasonsObject);

                    }
                if(maxPage <= i)
                    lastPage = true;
                else
                    i++;
                
                teamsSeasons.addTeamsSeasons(teamsSeasonsArray);
                
            }
        }
        
    }
    
    public JSONObject getAllTeamIds() throws SQLException{
        
      JSONObject teams = new JSONObject();
      JSONArray teamsArray = new JSONArray();
      String query = "SELECT * FROM teams";

      // create the java statement
      Statement st = db.createStatement();
      
      // execute the query, and get a java resultset
      ResultSet rs = st.executeQuery(query);
      
      // iterate through the java resultset
      if(rs.isBeforeFirst()){
          
      
      while (rs.next())
      {
        int id = rs.getInt("id");
        
        
        JSONObject tempObject = new JSONObject();
        tempObject.put("id", id);
        teamsArray.put(tempObject);
        
      }
      st.close();
      teams.put("data", teamsArray);
      }
      else{
          teams.put("data", "no Data");
          
      }
      
      
    
      return teams;
    }
    
}

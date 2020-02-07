/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author anyone
 */
public class FixturesEvents {
    
    private Connection db;
    private HashMap<String, Integer> events;
    private FixturesPlayers fixturePlayer;
    
    public FixturesEvents(){}
    
    public FixturesEvents(Connection db){
        this.db= db;
        this.events = new HashMap<>();
        this.fixturePlayer = new FixturesPlayers(db);
    }
    
    public  void addFixturesEvents(JSONArray fixturesEvents) throws IOException, SQLException{
        
        if(events.isEmpty())
            loadEvents();
        
        
        for(Object obj:fixturesEvents){
            JSONObject tempObject = (JSONObject) obj; 
            
            if(!tempObject.get("type").toString().equals("null") && !events.containsKey(tempObject.getString("type")))
                addNewEvent(tempObject.getString("type"));
                 
            try {
                // the mysql insert statement
                String query = "insert into fixtures_events(id, fixture_id, team_id, player_id, related_player_id, event_id, minute)"
                        + " values (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                        + " id=VALUES(id), fixture_id=VALUES(fixture_id), team_id=VALUES(team_id), player_id=VALUES(player_id), related_player_id=VALUES(related_player_id),"
                        + " event_id=VALUES(event_id), minute=VALUES(minute)";


                
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setLong(1, tempObject.getLong("id"));
                preparedStmt.setInt(2, tempObject.getInt("fixture_id"));
                
                if(!tempObject.get("team_id").toString().equals("null"))
                    preparedStmt.setInt(3, tempObject.getInt("team_id"));
                else
                    preparedStmt.setNull(3, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("player_id").toString().equals("null"))
                    preparedStmt.setInt(4, tempObject.getInt("player_id"));
                else
                    preparedStmt.setNull(4, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("related_player_id").toString().equals("null"))
                    preparedStmt.setInt(5, tempObject.getInt("related_player_id"));
                else
                    preparedStmt.setNull(5, java.sql.Types.VARCHAR);
                
               
               if(!tempObject.get("type").toString().equals("null"))
                    preparedStmt.setInt(6, events.get(tempObject.getString("type")));
                else
                    preparedStmt.setNull(6, java.sql.Types.VARCHAR);
               
               if(!tempObject.get("minute").toString().equals("null"))
                    preparedStmt.setInt(7, tempObject.getInt("minute"));
                else
                    preparedStmt.setNull(7, java.sql.Types.VARCHAR);
               

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }
            
            if(tempObject.get("type").equals("substitution"))
            {
                if(!tempObject.get("player_id").toString().equals("null") && !tempObject.get("player_id").toString().equals("null")){
                    int position = fixturePlayer.getPlayerFormationPosition(tempObject.getInt("related_player_id"), tempObject.getInt("fixture_id"));
                    fixturePlayer.setPlayerFormationPosition(tempObject.getInt("player_id"), tempObject.getInt("fixture_id"), position);
                    fixturePlayer.setPlayerFormationPosition(tempObject.getInt("related_player_id"), tempObject.getInt("fixture_id"), 0);
                }
                
            }
            
            

        }
    }
    
    private void loadEvents() throws SQLException{
      String query = "SELECT * FROM events";
      
        // execute the query, and get a java resultset
        try ( // create the java statement
                Statement st = db.createStatement()) {
            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);
            
            // iterate through the java resultset
            while (rs.next()){
                int id = rs.getInt("id");
                String type = rs.getString("type");
                events.put(type, id);
            } }
 
    }
    
    private void addNewEvent(String event) throws SQLException{
        try {
                // the mysql insert statement
                String query = "insert into events(type) values (?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setString(1, event);

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }
        
        loadEvents();
    }
      
      
    

    
}

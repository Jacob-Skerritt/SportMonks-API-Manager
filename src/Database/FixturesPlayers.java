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
            JSONObject statsObject = tempObject.getJSONObject("stats");
            JSONObject shotsObject = statsObject.getJSONObject("shots");
            JSONObject goalsObject = statsObject.getJSONObject("goals");
            JSONObject cardsObject = statsObject.getJSONObject("cards");
            JSONObject passingObject = statsObject.getJSONObject("passing");
            JSONObject otherObject = statsObject.getJSONObject("other");
                 
            try {
                // the mysql insert statement
                String query = "insert into"
                        + " fixtures_players(fixture_id, player_id, team_id, position, type, formation_position, captain, minutes_played, pass_accuracy, total_shots, shots_on_goal,"
                        + " saves, goal_scores, goal_assists, total_crosses, cross_accuracy, yellowcards, redcards, yellowredcards, offsides, pen_saved, pen_missed, pen_scored,"
                        + " tackles, blocks, intercepts, clearances)"
                        + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                        + " fixture_id=VALUES(fixture_id), player_id=VALUES(player_id), team_id=VALUES(team_id), position=VALUES(position), type=VALUES(type),"
                        + " formation_position=VALUES(formation_position), captain=VALUES(captain), minutes_played=VALUES(minutes_played),"
                        + " pass_accuracy=VALUES(pass_accuracy), total_shots=VALUES(total_shots), shots_on_goal=VALUES(shots_on_goal),"
                        + " saves=VALUES(saves), goal_scores=VALUES(goal_scores), goal_assists=VALUES(goal_assists), total_crosses=VALUES(total_crosses),"
                        + " cross_accuracy=VALUES(cross_accuracy), yellowcards=VALUES(yellowcards), redcards=VALUES(redcards), yellowredcards=VALUES(yellowredcards),"
                        + " offsides=VALUES(offsides), pen_saved=VALUES(pen_saved), pen_missed=VALUES(pen_missed), pen_scored=VALUES(pen_scored), tackles=VALUES(tackles),"
                        + " blocks=VALUES(blocks), intercepts=VALUES(intercepts), clearances=VALUES(clearances)";


                
                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = db.prepareStatement(query);
                preparedStmt.setInt(1, tempObject.getInt("fixture_id"));
                
                if(!tempObject.get("player_id").toString().equals("null"))
                    preparedStmt.setInt(2, tempObject.getInt("player_id"));
                else
                    preparedStmt.setNull(2, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("team_id").toString().equals("null"))
                    preparedStmt.setInt(3, tempObject.getInt("team_id"));
                else
                    preparedStmt.setNull(3, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("position").toString().equals("null"))
                    preparedStmt.setString(4, tempObject.getString("position"));
                else
                    preparedStmt.setNull(4, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("type").toString().equals("null"))
                    preparedStmt.setString(5, tempObject.getString("type"));
                else
                    preparedStmt.setNull(5, java.sql.Types.VARCHAR);
                
                if(!tempObject.get("formation_position").toString().equals("null"))
                    preparedStmt.setInt(6, tempObject.getInt("formation_position"));
                else
                    preparedStmt.setNull(6, java.sql.Types.VARCHAR);
               
                if(!tempObject.get("captain").toString().equals("null"))
                    preparedStmt.setBoolean(7, tempObject.getBoolean("captain"));
                else
                    preparedStmt.setBoolean(7, false);
               
                if(!otherObject.get("minutes_played").toString().equals("null"))
                    preparedStmt.setInt(8, otherObject.getInt("minutes_played"));
                else
                    preparedStmt.setInt(8, 0);
                    
                if(!passingObject.get("passes_accuracy").toString().equals("null"))
                    preparedStmt.setInt(9, passingObject.getInt("passes_accuracy"));
                else
                    preparedStmt.setInt(9, 0);
                    
                if(!shotsObject.get("shots_total").toString().equals("null"))
                    preparedStmt.setInt(10, shotsObject.getInt("shots_total"));
                else
                    preparedStmt.setInt(10, 0);
                    
                if(!shotsObject.get("shots_on_goal").toString().equals("null"))
                    preparedStmt.setInt(11, shotsObject.getInt("shots_on_goal"));
                else
                    preparedStmt.setInt(11, 0);
                    
                if(!otherObject.get("saves").toString().equals("null"))
                    preparedStmt.setInt(12, otherObject.getInt("saves"));
                else
                    preparedStmt.setInt(12, 0);
                    
                if(!goalsObject.get("scored").toString().equals("null"))
                    preparedStmt.setInt(13, goalsObject.getInt("scored"));
                else
                    preparedStmt.setInt(13, 0);
                    
                if(!goalsObject.get("assists").toString().equals("null"))
                    preparedStmt.setInt(14, goalsObject.getInt("assists"));
                else
                    preparedStmt.setInt(14, 0);
                    
                if(!passingObject.get("total_crosses").toString().equals("null"))
                    preparedStmt.setInt(15, passingObject.getInt("total_crosses"));
                else
                    preparedStmt.setInt(15, 0);
                    
                if(!passingObject.get("crosses_accuracy").toString().equals("null"))
                    preparedStmt.setInt(16, passingObject.getInt("crosses_accuracy"));
                else
                    preparedStmt.setInt(16, 0);
                    
                if(!cardsObject.get("yellowcards").toString().equals("null"))
                    preparedStmt.setInt(17, cardsObject.getInt("yellowcards"));
                else
                    preparedStmt.setInt(17, 0);
                    
                if(!cardsObject.get("redcards").toString().equals("null"))
                    preparedStmt.setInt(18, cardsObject.getInt("redcards"));
                else
                    preparedStmt.setInt(18, 0);
                    
                if(!cardsObject.get("yellowredcards").toString().equals("null"))
                    preparedStmt.setInt(19, cardsObject.getInt("yellowredcards"));
                else
                    preparedStmt.setInt(19, 0);
                    
                if(!otherObject.get("offsides").toString().equals("null"))
                    preparedStmt.setInt(20, otherObject.getInt("offsides"));
                else
                    preparedStmt.setInt(20, 0);
                    
                if(!otherObject.get("pen_saved").toString().equals("null"))
                    preparedStmt.setInt(21, otherObject.getInt("pen_saved"));
                else
                    preparedStmt.setInt(21, 0);
                    
                if(!otherObject.get("pen_missed").toString().equals("null"))
                    preparedStmt.setInt(22, otherObject.getInt("pen_missed"));
                else
                    preparedStmt.setInt(22, 0);
                    
                if(!otherObject.get("pen_scored").toString().equals("null"))
                    preparedStmt.setInt(23, otherObject.getInt("pen_scored"));
                else
                    preparedStmt.setInt(23, 0);
                    
                if(!otherObject.get("tackles").toString().equals("null"))
                    preparedStmt.setInt(24, otherObject.getInt("tackles"));
                else
                    preparedStmt.setInt(24, 0);
                    
                if(!otherObject.get("blocks").toString().equals("null"))
                    preparedStmt.setInt(25, otherObject.getInt("blocks"));
                else
                    preparedStmt.setInt(25, 0);
                    
                if(!otherObject.get("interceptions").toString().equals("null"))
                    preparedStmt.setInt(26, otherObject.getInt("interceptions"));
                else
                    preparedStmt.setInt(26, 0);
                    
                if(!otherObject.get("clearances").toString().equals("null"))
                    preparedStmt.setInt(27, otherObject.getInt("clearances"));
                else
                    preparedStmt.setInt(27, 0);
               

                // execute the preparedstatement
                preparedStmt.execute();
            }catch (SQLException ex) {

            }

        }

        
    }
    
    public int getPlayerFormationPosition(int playerId, int fixtureId){
        try {
            // the mysql insert statement
            String query = "SELECT formation_position from fixtures_players where player_id = ? AND fixture_id = ?";

            try (PreparedStatement preparedStmt = db.prepareStatement(query)) {
                preparedStmt.setInt(1, playerId);
                preparedStmt.setInt(2, fixtureId);
                
                // execute the query, and get a java resultset
                ResultSet rs = preparedStmt.executeQuery();
                
                // iterate through the java resultset
                
                
                while (rs.next()) {               
                    return rs.getInt("formation_position");
                    
                }
            }
                
            
            
        } catch (SQLException ex) {
        }
        return 0;
    }
    
    public void setPlayerFormationPosition(int playerId, int fixtureId, int position){
        try {
            // the mysql insert statement
            String query = "update fixtures_players set formation_position = ? where player_id = ? and fixture_id = ?";

            PreparedStatement preparedStmt = db.prepareStatement(query);
            if(position >0)
                preparedStmt.setInt(1, position);
            else
                preparedStmt.setNull(1, java.sql.Types.VARCHAR);
            preparedStmt.setInt(2, playerId);
            preparedStmt.setInt(3, fixtureId);

             preparedStmt.execute();
            
        } catch (SQLException ex) {
        }
    }
    
    
    
    
    
}

package Database;

import SportMonks.Endpoint;
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
 * Class used to add the rounds for each active season to the database
 * 
 */
public class Rounds {

    private Connection db;
    private Seasons seasons;

    public Rounds() {

    }

    public Rounds(Connection db) {
        this.db = db;
        this.seasons = new Seasons(db);
    }
    
    //Method to add the rounds of a season to the database, updates existing records if any changes to the data has occured
    public void manageRounds(String roundsEndpoint) throws IOException, SQLException {

        JSONObject seasonIds = seasons.getSeasonIds();
        JSONArray seasonsArray = seasonIds.getJSONArray("data");

        for (Object seasonId : seasonsArray) {
            JSONObject tempSeason = (JSONObject) seasonId;
            int id = tempSeason.getInt("id");
            String newStagesEndpoint = Endpoint.makeNewEndpoint(roundsEndpoint, id, "season/");

            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;
            while (!lastPage) {
                JSONObject rounds;

                try {
                    rounds = Endpoint.getDataFromEndpoint(newStagesEndpoint + i);
                } catch (RuntimeException | IOException e) {
                    System.out.println(e);
                    break;
                }

                JSONArray leaguesArray = rounds.getJSONArray("data");
                JSONObject metaData = rounds.getJSONObject("meta");

                if (metaData.has("pagination")) {
                    JSONObject pagination = metaData.getJSONObject("pagination");
                    maxPage = pagination.getInt("total_pages");
                }

                for (Object obj : leaguesArray) {
                    JSONObject tempObject = (JSONObject) obj;

                    try {
                        // the mysql insert statement
                        String query = " insert into rounds (id, name, start_date, end_date, league_id ,season_id, stage_id)"
                                + " values (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                                + " name=VALUES(name), start_date=VALUES(start_date), end_date=VALUES(end_date),"
                                + " league_id=VALUES(league_id), season_id=VALUES(season_id), stage_id=VALUES(stage_id)";

                        // create the mysql insert preparedstatement
                        PreparedStatement preparedStmt = db.prepareStatement(query);
                        preparedStmt.setInt(1, tempObject.getInt("id"));
                        preparedStmt.setInt(2, tempObject.getInt("name"));
                        preparedStmt.setString(3, tempObject.getString("start"));
                        preparedStmt.setString(4, tempObject.getString("end"));
                        preparedStmt.setInt(5, tempObject.getInt("league_id"));
                        preparedStmt.setInt(6, tempObject.getInt("season_id"));
                        preparedStmt.setInt(7, tempObject.getInt("stage_id"));

                        // execute the preparedstatement
                        preparedStmt.execute();
                    } catch (SQLException ex) {

                    }

                }
                if (maxPage <= i) {
                    lastPage = true;
                } else {
                    i++;
                }

            }
        }

    }
}

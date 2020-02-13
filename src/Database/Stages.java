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
 * @author Jacob Skerrit
 * 
 * Class used to add the stages for each season into the database.
 * 
 */
public class Stages {

    private Connection db;
    private Seasons seasons;

    public Stages() {

    }

    public Stages(Connection db) {
        this.db = db;
        this.seasons = new Seasons(db);
    }
    
    //Method to add stages into the database, updates existing records if changes have occured
    public void manageStages(String stagesEndpoint) throws IOException, SQLException {

        JSONObject seasonIds = seasons.getSeasonIds();
        JSONArray seasonsArray = seasonIds.getJSONArray("data");

        for (Object seasonId : seasonsArray) {
            JSONObject tempSeason = (JSONObject) seasonId;
            int id = tempSeason.getInt("id");
            String newStagesEndpoint = Endpoint.makeNewEndpoint(stagesEndpoint, id, "season/");

            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;
            while (!lastPage) {
                JSONObject stages;

                try {
                    stages = Endpoint.getDataFromEndpoint(newStagesEndpoint+i);
                } catch (RuntimeException | IOException e) {
                    System.out.println(e);
                    break;
                }

                JSONArray stagesArray = stages.getJSONArray("data");
                JSONObject metaData = stages.getJSONObject("meta");

                if (metaData.has("pagination")) {
                    JSONObject pagination = metaData.getJSONObject("pagination");
                    maxPage = pagination.getInt("total_pages");
                }

                for (Object obj : stagesArray) {
                    JSONObject tempObject = (JSONObject) obj;

                    try {
                        // the mysql insert statement
                        String query = " insert into stages (id, name, type, league_id ,season_id)"
                                + " values (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                                + " name=VALUES(name), type=VALUES(type), league_id=VALUES(league_id), season_id=VALUES(season_id)";

                        // create the mysql insert preparedstatement
                        PreparedStatement preparedStmt = db.prepareStatement(query);
                        preparedStmt.setInt(1, tempObject.getInt("id"));
                        preparedStmt.setString(2, tempObject.getString("name"));
                        preparedStmt.setString(3, tempObject.getString("type"));
                        preparedStmt.setInt(4, tempObject.getInt("league_id"));
                        preparedStmt.setInt(5, tempObject.getInt("season_id"));

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

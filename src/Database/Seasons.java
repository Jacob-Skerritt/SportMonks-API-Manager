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

/**
 * 
 * @author Jacob Skerritt
 * 
 * Class used to add the seasons to the database
 * Additionally, will return all the active season Ids upon request
 * 
 */
public class Seasons {

    private Connection db;

    public Seasons() {

    }

    public Seasons(Connection db) {
        this.db = db;
    }
    
    //Method used to add seasons to the database, existing records are updated if changes have occured
    public void manageSeasons(String seasonsEndpoint) throws IOException {

        boolean lastPage = false;
        int i = 1;
        int maxPage = 0;

        while (!lastPage) {

            JSONObject seasons;

            try {
                seasons = Endpoint.getDataFromEndpoint(seasonsEndpoint + i);
            } catch (RuntimeException | IOException e) {
                System.out.println(e);
                break;
            }

            JSONArray leaguesArray = seasons.getJSONArray("data");
            JSONObject metaData = seasons.getJSONObject("meta");

            if (metaData.has("pagination")) {
                JSONObject pagination = metaData.getJSONObject("pagination");
                maxPage = pagination.getInt("total_pages");
            }

            for (Object obj : leaguesArray) {
                JSONObject tempObject = (JSONObject) obj;

                try {
                    // the mysql insert statement
                    String query = " insert into seasons (id, year, active, league_id)"
                            + " values (?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                            + " year=VALUES(year), active=VALUES(active), league_id=VALUES(league_id)";

                    // create the mysql insert preparedstatement
                    PreparedStatement preparedStmt = db.prepareStatement(query);
                    preparedStmt.setInt(1, tempObject.getInt("id"));
                    preparedStmt.setString(2, tempObject.getString("name"));
                    preparedStmt.setBoolean(3, tempObject.getBoolean("is_current_season"));
                    preparedStmt.setInt(4, tempObject.getInt("league_id"));

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
    
    //Method used to return all active Seasons ids in a jsonObject
    public JSONObject getSeasonIds() throws SQLException {
        JSONObject seasons = new JSONObject();
        JSONArray seasonsArray = new JSONArray();
        String query = "SELECT * FROM seasons where active = 1";

        // create the java statement
        Statement st = db.createStatement();

        // execute the query, and get a java resultset
        ResultSet rs = st.executeQuery(query);

        // iterate through the java resultset
        if (rs.isBeforeFirst()) {

            while (rs.next()) {
                int id = rs.getInt("id");

                JSONObject tempObject = new JSONObject();
                tempObject.put("id", id);
                seasonsArray.put(tempObject);

            }
            st.close();
            seasons.put("data", seasonsArray);
        } else {
            seasons.put("data", "no Data");

        }

        return seasons;

    }
}

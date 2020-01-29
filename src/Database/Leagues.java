/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Jacob
 */
public class Leagues {

    private Connection db;

    public Leagues() {

    }

    public Leagues(Connection db) {
        this.db = db;
    }

    public void manageLeagues(String leaguesEndpoint) throws IOException {

        boolean lastPage = false;
        int i = 1;
        int maxPage = 0;

        while (!lastPage) {
            
            JSONObject leagues;

            try {
                leagues = Endpoint.getDataFromEndpoint(leaguesEndpoint + i);
            } catch (RuntimeException | IOException e) {
                System.out.println(e);
                break;
            }

            JSONArray leaguesArray = leagues.getJSONArray("data");
            JSONObject metaData = leagues.getJSONObject("meta");

            if (metaData.has("pagination")) {
                JSONObject pagination = metaData.getJSONObject("pagination");
                maxPage = pagination.getInt("total_pages");
            }
            System.out.println(leagues.toString());
            for (Object obj : leaguesArray) {
                JSONObject tempObject = (JSONObject) obj;

                try {
                    // the mysql insert statement
                    String query = " insert into leagues (id, name, active, type, country_id, logo, is_cup, standings)"
                            + " values (?, ?, ?, ?, ? , ?, ? , ?) ON DUPLICATE KEY UPDATE"
                            + " name=VALUES(name), active=VALUES(active), type=VALUES(type), country_id=VALUES(country_id), logo=VALUES(logo), is_cup=VALUES(is_cup), standings=VALUES(standings)";

                    // create the mysql insert preparedstatement
                    PreparedStatement preparedStmt = db.prepareStatement(query);
                    preparedStmt.setInt(1, tempObject.getInt("id"));
                    preparedStmt.setString(2, tempObject.getString("name"));
                    preparedStmt.setBoolean(3, tempObject.getBoolean("active"));
                    preparedStmt.setString(4, tempObject.getString("type"));
                    preparedStmt.setInt(5, tempObject.getInt("country_id"));
                    preparedStmt.setString(6, tempObject.get("logo_path").toString());
                    preparedStmt.setBoolean(7, tempObject.getBoolean("is_cup"));
                    preparedStmt.setBoolean(8, tempObject.getBoolean("live_standings"));
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

    public JSONObject getJSONLeaguesId() throws SQLException {
        JSONObject leagues = new JSONObject();
        JSONArray leaguesArray = new JSONArray();
        String query = "SELECT * FROM leagues WHERE active = 1";

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
                leaguesArray.put(tempObject);

            }
            st.close();
            leagues.put("data", leaguesArray);
        } else {
            leagues.put("data", "no Data");

        }

        return leagues;

    }

}

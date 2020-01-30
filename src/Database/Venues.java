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

public class Venues {

    private Connection db;
    private Seasons seasons;

    public Venues() {

    }

    public Venues(Connection db) {
        this.db = db;
        this.seasons = new Seasons(db);
    }

    public void manageVenues(String venuesEndpoint) throws IOException, SQLException {

        seasons = new Seasons(this.db);
        JSONObject seasonIds = seasons.getSeasonIds();
        JSONArray seasonsArray = seasonIds.getJSONArray("data");

        for (Object seasonId : seasonsArray) {
            JSONObject tempSeason = (JSONObject) seasonId;
            int id = tempSeason.getInt("id");
            String newVenuesEndpoint = Endpoint.makeNewEndpoint(venuesEndpoint, id, "season/");

            boolean lastPage = false;
            int i = 1;
            int maxPage = 0;
            while (!lastPage) {
                JSONObject venues;

                try {
                    venues = Endpoint.getDataFromEndpoint(newVenuesEndpoint + i);
                } catch (RuntimeException | IOException e) {
                    System.out.println(e);
                    break;
                }

                JSONArray venuesArray = venues.getJSONArray("data");
                JSONObject metaData = venues.getJSONObject("meta");

                if (metaData.has("pagination")) {
                    JSONObject pagination = metaData.getJSONObject("pagination");
                    maxPage = pagination.getInt("total_pages");
                }

                for (Object obj : venuesArray) {
                    JSONObject tempObject = (JSONObject) obj;

                    try {
                        // the mysql insert statement
                        String query = " insert into venues (id, name, surface, city ,image)"
                                + " values (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                                + " name=VALUES(name), surface=VALUES(surface), city=VALUES(city), image=VALUES(image)";

                        // create the mysql insert preparedstatement
                        PreparedStatement preparedStmt = db.prepareStatement(query);
                        preparedStmt.setInt(1, tempObject.getInt("id"));
                        preparedStmt.setString(2, tempObject.getString("name"));

                        if (!tempObject.get("surface").toString().equals("null")) {
                            preparedStmt.setString(3, tempObject.getString("surface"));
                        } else {
                            preparedStmt.setString(3, "Unknown");
                        }

                        if (!tempObject.get("city").toString().equals("null")) {
                            preparedStmt.setString(4, tempObject.getString("city"));
                        } else {
                            preparedStmt.setString(4, "N/A");
                        }

                        preparedStmt.setString(5, tempObject.get("image_path").toString());

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

    public void addVenue(int venueId) throws IOException, SQLException {
        final String TOKEN = "IeJEyAVbp2IjoYzCdGpZBk7mWOAzSkRXHeiYYeOK9OWgOI0iNjaTcGAXsHfG";
        JSONObject venue = new JSONObject();

        try {
            venue = Endpoint.getDataFromEndpoint("https://soccer.sportmonks.com/api/v2.0/venues/" + venueId + "?api_token=" + TOKEN).getJSONObject("data");
        } catch (RuntimeException e) {
            System.out.println(e);
        }

        try {
            // the mysql insert statement
            String query = " insert into venues (id, name, surface, city ,image)"
                    + " values (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                    + " name=VALUES(name), surface=VALUES(surface), city=VALUES(city), image=VALUES(image)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = db.prepareStatement(query);
            preparedStmt.setInt(1, venue.getInt("id"));
            preparedStmt.setString(2, venue.getString("name"));

            if (!venue.get("surface").toString().equals("null")) {
                preparedStmt.setString(3, venue.getString("surface"));
            } else {
                preparedStmt.setString(3, "Unknown");
            }

            if (!venue.get("city").toString().equals("null")) {
                preparedStmt.setString(4, venue.getString("city"));
            } else {
                preparedStmt.setString(4, "N/A");
            }

            preparedStmt.setString(5, venue.get("image_path").toString());

            // execute the preparedstatement
            preparedStmt.execute();
        } catch (SQLException ex) {
        }
    }

    public boolean checkVenueExists(int venueId) throws IOException, SQLException {

        try {
            // the mysql insert statement
            String query = " SELECT COUNT(*) from venues where id = ?";

            PreparedStatement preparedStmt = db.prepareStatement(query);
            preparedStmt.setInt(1, venueId);

            // execute the query, and get a java resultset
            ResultSet rs = preparedStmt.executeQuery();

            // iterate through the java resultset
            if (rs.isBeforeFirst()) {

                while (rs.next()) {
                    int count = rs.getInt("COUNT(*)");

                    if(count == 0)
                        return false;
                    else 
                        return true;

                }
                preparedStmt.close();
                
            }
            
        } catch (SQLException ex) {
        }
        return false;
    }
}

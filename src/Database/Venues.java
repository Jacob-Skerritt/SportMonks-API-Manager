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
 * Class responsible for handling venues used to hold football games
 * Due to issues with SportMonks API implementation issues arise with venues
 * As such, two methods have been added to solve the problem.
 * 
*/
public class Venues {

    private Connection db;
    private Seasons seasons;

    public Venues() {

    }

    public Venues(Connection db) {
        this.db = db;
        this.seasons = new Seasons(db);
    }
    
    //Method to get the Venues used on a per season basis and add them to the database, updates existing records if any changes have occured
    public void manageVenues(String venuesEndpoint) throws IOException, SQLException {
        
        //Getting the active season Ids to use for getting the veneus used in each season.
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

    //Method to add a single venue to the database, required due to issues with sportsmonks data for veneus
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

    //Method to check if a venue is in the database, if not, add it
    //This is required due to the same veneu having a different name/id
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

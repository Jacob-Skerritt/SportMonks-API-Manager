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
 * Class used to add Continents to the database form the SportMonks
 * Additionally used to retrieve continents from the database if another class requires them 
 * 
 */
public class Continents {

    private Connection db;

    public Continents() {

    }

    public Continents(Connection db) {
        this.db = db;
    }
    
    //Method to put all the continents in the database, updates the existing records if any changes have occured
    public void manageContinents(String continentEndpoint) throws IOException {
        
        //Initialing variables to manage pagination if it's present (not likely since there are only 7 continents)
        boolean lastPage = false;
        int i = 1;
        int maxPage = 0;

        while (!lastPage) {
            JSONObject continents;
            
            //Getting the continetns data via  the Endpoint class
            try {
                continents = Endpoint.getDataFromEndpoint(continentEndpoint + i);
            } catch (RuntimeException | IOException e) {
                System.out.println(e);
                break;
            }

            JSONArray continentsArray = continents.getJSONArray("data");
            JSONObject metaData = continents.getJSONObject("meta");

            if (metaData.has("pagination")) {
                JSONObject pagination = metaData.getJSONObject("pagination");
                maxPage = pagination.getInt("total_pages");
            }
            
            //Addind the continents to the database via prepared statements
            for (Object obj : continentsArray) {
                JSONObject tempObject = (JSONObject) obj;

                try {
                    // the mysql insert statement
                    String query = " insert into continents (id, name)"
                            + " values (?, ?) ON DUPLICATE KEY UPDATE"
                            + " name=VALUES(name)";

                    // create the mysql insert preparedstatement
                    PreparedStatement preparedStmt = db.prepareStatement(query);
                    preparedStmt.setInt(1, tempObject.getInt("id"));
                    preparedStmt.setString(2, tempObject.getString("name"));

                    // execute the preparedstatement
                    preparedStmt.execute();
                } catch (SQLException ex) {

                }

            }
            //Incrementing i if there are more pages of data, otherwise, exiting loop
            if (maxPage <= i) {
                lastPage = true;
            } else {
                i++;
            }

        }

    }

    //Method to retreive all of the continents in the database
    public JSONObject getJSONContinents() throws SQLException {
        JSONObject continents = new JSONObject();
        JSONArray continentsArray = new JSONArray();
        String query = "SELECT * FROM continents";

        // create the java statement
        Statement st = db.createStatement();

        // execute the query, and get a java resultset
        ResultSet rs = st.executeQuery(query);

        // iterate through the java resultset
        if (rs.isBeforeFirst()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                JSONObject tempObject = new JSONObject();
                tempObject.put("id", id);
                tempObject.put("name", name);
                continentsArray.put(tempObject);

            }
            st.close();
            continents.put("data", continentsArray);
        } else {
            continents.put("data", "no Data");

        }

        return continents;

    }

}

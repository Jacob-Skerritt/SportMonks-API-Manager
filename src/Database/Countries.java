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
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Jacob Skerrit
 * 
 * Class used to add countries to the database
 * 
 */
public class Countries {

    private Connection db;
    private Continents continents;

    public Countries() {

    }

    public Countries(Connection db) {
        this.db = db;
        this.continents = new Continents(db);
    }
    
    //Method used to get all the countries from SportMonks API endpoint and add them to the database, updates the existing records if changes have been made
    public void manageCountires(String countriesEndpoint) throws SQLException, IOException {
        
        //Initialising variables to manage pagination
        boolean lastPage = false;
        int i = 1;
        int maxPage = 0;

        while (!lastPage) {

            JSONObject countries;

            try {
                countries = Endpoint.getDataFromEndpoint(countriesEndpoint + i);
            } catch (RuntimeException | IOException e) {
                System.out.println(e);
                break;
            }
            
            //Retrieving a JSONObject containting an array of continents and there corresponding ids fro the database
            JSONObject continentsObject = continents.getJSONContinents();
            JSONArray continentsArray = continentsObject.getJSONArray("data");

            JSONArray countriesArray = countries.getJSONArray("data");
            Object countriesExtra;
            int continentId = 0;
            String continent;
            JSONObject JSONCountriesExtra;

            JSONObject metaData = countries.getJSONObject("meta");
            if (metaData.has("pagination")) {
                JSONObject pagination = metaData.getJSONObject("pagination");
                maxPage = pagination.getInt("total_pages");
            }
            
            //Looping through all of the countries
            for (Object obj : countriesArray) {
                JSONObject tempObject = (JSONObject) obj;

                countriesExtra = tempObject.get("extra");
                
                //cheking if a country has an associated continent, if not, assigning it to Europe as default
                if (!countriesExtra.toString().equals("null")) {

                    JSONCountriesExtra = (JSONObject) countriesExtra;
                    continent = JSONCountriesExtra.getString("continent");
                } else {
                    continent = "Europe";
                }
                
                //Looping through all continents to find the matching one to get the id as it is a foreign key in the countries table
                for (Object obj2 : continentsArray) {
                    JSONObject tempContinentObject = (JSONObject) obj2;
                    if (tempContinentObject.getString("name").equals(continent)) {
                        continentId = tempContinentObject.getInt("id");
                    }

                }
                
                //Adding a country to the database
                try {
                    // the mysql insert statement
                    String query = " insert into countries (id, name, continent_id, flag)"
                            + " values (?, ?, ?, ?) ON DUPLICATE KEY UPDATE"
                            + " name=VALUES(name), continent_id=VALUES(continent_id), flag=VALUES(flag)";

                    // create the mysql insert preparedstatement
                    PreparedStatement preparedStmt = db.prepareStatement(query);
                    preparedStmt.setInt(1, tempObject.getInt("id"));
                    //Inquire about issues regarding null values and how to detect them??
                    preparedStmt.setString(2, tempObject.get("name").toString());
                    preparedStmt.setInt(3, continentId);
                    preparedStmt.setString(4, tempObject.get("image_path").toString());

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

}

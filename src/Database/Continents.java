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
public class Continents {
    
    private Connection db;
    
    public Continents(){
        
    }
    
    public Continents(Connection db){
        this.db = db;
    }
    
    
    public  void manageContinents(String continentEndpoint) throws IOException{
            
            
        boolean lastPage = false;
        int i = 1;
        int maxPage = 0;
        
        while(!lastPage){
            JSONObject continents = Endpoint.getDataFromEndpoint(continentEndpoint+i);



            JSONArray leaguesArray = continents.getJSONArray("data");
            JSONObject metaData = continents.getJSONObject("meta");
            
            if(metaData.has("pagination")){
            JSONObject pagination = metaData.getJSONObject("pagination");
            maxPage = pagination.getInt("total_pages");
            }
                

                for(Object obj:leaguesArray){
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
                    }catch (SQLException ex) {

                    }

                }
            if(maxPage <= i)
                lastPage = true;
            else
                i++;
           
            }
        
        }
    
    public  JSONObject getJSONContinents() throws SQLException{
      JSONObject continents = new JSONObject();
      JSONArray continentsArray = new JSONArray();
      String query = "SELECT * FROM continents";

      // create the java statement
      Statement st = db.createStatement();
      
      // execute the query, and get a java resultset
      ResultSet rs = st.executeQuery(query);
      
      // iterate through the java resultset
      if(rs.isBeforeFirst()){
          
      
      while (rs.next())
      {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        
        JSONObject tempObject = new JSONObject();
        tempObject.put("id", id);
        tempObject.put("name", name);
        continentsArray.put(tempObject);
        
      }
      st.close();
      continents.put("data", continentsArray);
      }
      else{
          continents.put("data", "no Data");
          
      }
      
      
    
      return continents;
        
    }
    
}

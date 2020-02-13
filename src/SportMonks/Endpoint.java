/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SportMonks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

/**
 *
 * @author Jacob Skerrit
 * 
 * Class to manage anything related to the accessing of the SportMonks API Endpoints
 * 
 */
public class Endpoint {
  
    //Method to take a string that represents an API endpoints and retrieve a JSON object from that endpoint passing it back via the return
    public static JSONObject getDataFromEndpoint(String endpoint) throws ProtocolException, IOException{
        JSONObject newJson = new JSONObject();
        URL url = new URL(endpoint);
        HttpURLConnection connect = (HttpURLConnection)url.openConnection(); 
        connect.setRequestMethod("GET"); 
        connect.connect();


        try (Scanner sc = new Scanner(url.openStream())) {
            String inline ="";
            
            while(sc.hasNext())
            {
                inline+=sc.nextLine();
                
            }

            newJson = new JSONObject(inline);
            sc.close();

        }catch(Exception e){
            System.out.println(e);
            throw new RuntimeException("Error connection to endpoint!");
        }
        
        return newJson;
        
    }
    
    /*
        Method used to alter an Endpoint, commonly called during loops for different leagues/seasons etc.
        Take an endpoint, and insters and id at a specified location specified by the target string
        Return this new Endpoint
    */
    public static String makeNewEndpoint(String endpoint , int id, String target){
        
        int insertAt = endpoint.indexOf(target);
        String newEndpoint = endpoint.substring(0, (insertAt + target.length()));
        String endPointToken = endpoint.substring(insertAt+target.length());
        newEndpoint += id + endPointToken;
        
        return newEndpoint;
    }
   
}

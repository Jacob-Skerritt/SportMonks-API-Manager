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
 * @author Jacob
 */
public class Endpoint {
  
    
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
    
    public static String makeNewEndpoint(String endpoint , int id, String target){
        
        int insertAt = endpoint.indexOf(target);
        String newEndpoint = endpoint.substring(0, (insertAt + target.length()));
        String endPointToken = endpoint.substring(insertAt+target.length());
        newEndpoint += id + endPointToken;
        
        return newEndpoint;
    }
   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SportMonks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author anyone
 */
public class EndpointTest {
    
    public EndpointTest() {
    }
    
    final static String fileP = "C:\\Users\\anyone\\Desktop\\token.txt";

    /**
     * Test of getDataFromEndpoint method, of class Endpoint.
     */
    @Test
    public void testGetDataFromEndpoint() throws Exception {
        System.out.println("getDataFromEndpointTest1");
        String endpoint = "https://soccer.sportmonks.com/api/v2.0/leagues?api_token=" + getToken(fileP) +"a&page=";
        String expResult = "Unauthenticated.";
        JSONObject result = Endpoint.getDataFromEndpoint(endpoint);
        System.out.println(result);
        assertEquals(expResult, result.getJSONObject("error").getString("message"));
        
    }

    /**
     * Test of makeNewEndpoint method, of class Endpoint.
     */
    @Test
    public void testMakeNewEndpoint() throws FileNotFoundException {
        System.out.println("makeNewEndpoint");
        String endpoint = "https://soccer.sportmonks.com/api/v2.0/teams/season/?api_token=" + getToken(fileP) + "&include=coach&page=";
        int id = 0;
        String target = "season/";
        String expResult = "https://soccer.sportmonks.com/api/v2.0/teams/season/0?api_token=" + getToken(fileP) + "&include=coach&page=";
        String result = Endpoint.makeNewEndpoint(endpoint, id, target);
        assertEquals(expResult, result);
        
    }
    
        //Method used to get the API token  from a text file, required to make API connections
    public static String getToken(String txtFile) throws FileNotFoundException{
        File file = new File(txtFile); 
        Scanner sc = new Scanner(file); 

     
        return sc.nextLine();
        
    }
    
}

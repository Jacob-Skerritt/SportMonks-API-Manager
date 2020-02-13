/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SportMonks;

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

    /**
     * Test of getDataFromEndpoint method, of class Endpoint.
     */
    @Test
    public void testGetDataFromEndpoint() throws Exception {
        System.out.println("getDataFromEndpointTest1");
        String endpoint = "";
        JSONObject expResult = null;
        JSONObject result = Endpoint.getDataFromEndpoint(endpoint);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of makeNewEndpoint method, of class Endpoint.
     */
    @Test
    public void testMakeNewEndpoint() {
        System.out.println("makeNewEndpoint");
        String endpoint = "";
        int id = 0;
        String target = "";
        String expResult = "";
        String result = Endpoint.makeNewEndpoint(endpoint, id, target);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

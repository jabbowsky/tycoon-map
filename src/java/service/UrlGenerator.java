/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author mikhail
 */
public class UrlGenerator {
    
    public static int MAX_LENGTH = 2000;
    
    private static String googleUrlHeadJson = "https://maps.googleapis.com/maps/api/elevation/json";
    private static String apiKey = "AIzaSyBngJugd3QwEkq8HVDgWLI9rWZ_a1DASXE";
    
    public static String getUrl(String param){
        return googleUrlHeadJson + "?key="+ apiKey+ "&" + param;
    }
    
}

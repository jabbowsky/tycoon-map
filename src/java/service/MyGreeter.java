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
public class MyGreeter {
    //MapMaker mapMake = new MapMaker(); 
    private int result = 0;
    private TycoonMap map;
    
    
    public String greet(String name){
        map = new TycoonMap(16);
        //map.setStartPoints(90d,0d,-80d,174d);
    //result = mapMake.makeAndSaveMap();
        String result = "Map is done  "+ name+ "!"; 
        while (!map.isEoP){
        //for (int i=0;i<100;i++){
            result +=  map.makeRequestString()+ "<br/>";
        }
        return result;
        
    }
}

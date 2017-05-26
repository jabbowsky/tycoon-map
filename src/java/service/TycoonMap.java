/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;


/**
 *
 * @author mikhail
 */
public class TycoonMap {
    
    private int x;
    private int y;
    private String name;
    private TycoonPoint[][] points;  
     
    private double maxHeight = 8000d;
    private double seaLine = 0d;
    private int rateOfSmoose = 1;
    private int mapSize = 0;
    private TycoonPoint currentPoint;
    private SimplePoint currentSimplePoint;
    private int countPoints;
    public boolean isEoP = false;
    
    public TycoonMap(int x, int y, TycoonPoint[][] points, String name){
        this.x = x;
        this.y = y;
        this.name = name;
        this.points = points;
        int longSide = y;
        if( x > y ){
            longSide = x;
        }
        while(1<<mapSize < longSide){
            mapSize++;
            countPoints = (int)Math.pow(2d, mapSize*2);
        } 
    }; 
    
    public TycoonMap(int x, int y, TycoonPoint[][] points){
        this(x, y,points, "map"+System.currentTimeMillis());
    }; 

    public TycoonMap(int x, int y){
        this(x, y, new TycoonPoint[x][y]);
        setStartPoints();
        currentPoint = points[0][0];
        currentSimplePoint = new SimplePoint(0,0);
    }; 
    
    public TycoonMap(int x){
        this(x, x);
    }; 
    
    public TycoonMap(){
        this(64);
    }; 
    
//    A---B
//    |   |
//    D---C    
    public void setStartPoints(TycoonPoint[] startPoint){
        switch (startPoint.length) {
            case 4:
                points[0][0]     = startPoint[0];
                points[x-1][0]   = startPoint[1];
                points[x-1][y-1] = startPoint[2];
                points[0][y-1]   = startPoint[3];
                break;
            case 2:
                points[0][0]     = startPoint[0];
                TycoonPoint point1 = new TycoonPoint(x-1,0);
                point1.setLat(startPoint[0].getLat());
                point1.setLng(startPoint[1].getLng());
                points[x-1][0]   = point1;
                points[x-1][y-1] = startPoint[1];
                TycoonPoint point3 = new TycoonPoint(0,y-1);
                point3.setLat(startPoint[1].getLat());
                point3.setLng(startPoint[0].getLng());
                points[0][y-1]   = point3;
                break;
            default:
                setStartPoints();
                break;
        }
    }
    
    public void setStartPoints(double latA, double lngA, double latC, double lngC){
        TycoonPoint startPoint[] = new TycoonPoint[2];
        startPoint[0] = new TycoonPoint(0,0);
        startPoint[0].setLat(latA);
        startPoint[0].setLng(lngA);
        startPoint[1] = new TycoonPoint(x-1,y-1);
        startPoint[1].setLat(latC);
        startPoint[1].setLng(lngC);
        setStartPoints(startPoint);
    }
    
    private void setStartPoints(){
        points[0][0]     = new TycoonPoint(0,0);
        points[x-1][0]   = new TycoonPoint(x-1,0);
        points[0][y-1]   = new TycoonPoint(0,y-1);
        points[x-1][y-1] = new TycoonPoint(x-1,y-1);
    }    
    
    public BufferedImage drawMap(){
        final BufferedImage res = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB );
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                res.setRGB(i, j, points[i][j].getTyColor());
            }
        }
        return res;
    }   
    
    private int getColorFromHeight(double height){
        int color; 
        if(maxHeight < height){
            color = 0xffffffff;
        }
        else if(seaLine > height){
            color = 0xffffffff;
        }else{
            double relativeHeigth = (int)(height*256/maxHeight) ;
            relativeHeigth = relativeHeigth + relativeHeigth*256 + relativeHeigth*256*256;
            color = 0xff000000 | (int)relativeHeigth;
        }
        return color;
    }
    
    public int loadMapFromGoogle ()throws Exception{
        URL oracle = new URL("http://www.oracle.com/");
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
            System.out.println(inputLine);
        in.close();
        return 0; 
    }
    
    public String makeRequestString(){
        String locations = "";
        
        while(UrlGenerator.getUrl(locations).length() < UrlGenerator.MAX_LENGTH && !isLastPoint()){
            if (locations.isEmpty()){
                locations = "locations="     + currentPoint.getLat() + "," + currentPoint.getLng();
            }else{
                locations =  locations + "|" + currentPoint.getLat() + "," + currentPoint.getLng();
            }
            currentPoint = nextPoint();
        }
        if(isLastPoint()){
            isEoP = true;
        }
        return UrlGenerator.getUrl(locations) ;
    }
        
    private int getBit(int val, int pos){
        if((val & (1L<<pos)) == 0L){ 
            return 0;
        }else{
            return 1;
        }                   
    }
    
    private void setLTude(TycoonPoint point){
       double lat = ( (point.getY()+1)*(   (point.getX()+1)*(points[0][0].getLat())    +
                                        (x- point.getX()-1)*(points[x-1][0].getLat()))/x +
                    (y-point.getY()-1)*(   (point.getX()+1)*(points[0][y-1].getLat())    +
                                        (x- point.getX()-1)*(points[x-1][y-1].getLat()))/x)/y;
       
       double lng = ( (point.getY()+1)*(   (point.getX()+1)*(points[0][0].getLng())    +
                                        (x- point.getX()-1)*(points[x-1][0].getLng()))/x +
                    (y-point.getY()-1)*(   (point.getX()+1)*(points[0][y-1].getLng())    +
                                        (x- point.getX()-1)*(points[x-1][y-1].getLng()))/x)/y;
    
       point.setLat(round(lat,6));
       point.setLng(round(lng,6));
    }
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
    public TycoonPoint nextPoint(){
        int result = fromPoint2Num(currentSimplePoint);
        result++; 
        System.err.println("result:" + result + " " + String.valueOf(isEoP)+ " " + currentSimplePoint.getX() + " " + currentSimplePoint.getY());
        if(result >= countPoints){
            result = 0;
            isEoP = true;
        }
        currentSimplePoint = fromNum2Point(result);
        System.err.println("result:" + result + " " + String.valueOf(isEoP)+ " " + currentSimplePoint.getX() + " " + currentSimplePoint.getY());
        if(currentSimplePoint.getX() >= x || currentSimplePoint.getY() >= y){
            return nextPoint();
        }else{
            if(points[currentSimplePoint.getX()][currentSimplePoint.getY()] != null){
                currentPoint = points[currentSimplePoint.getX()][currentSimplePoint.getY()];
            }else{
                currentPoint = new TycoonPoint(currentSimplePoint.getX(),currentSimplePoint.getY());
                setLTude(currentPoint);
                points[currentSimplePoint.getX()][currentSimplePoint.getY()] = currentPoint;
            }
            return currentPoint;
        }
    }
    
    public TycoonPoint prevPoint(){
        int result = fromPoint2Num(currentSimplePoint);
        result--;
        if(result<0){
            result = countPoints-1;
        }else if(result == 0) {
            isEoP = true;            
        }
        currentSimplePoint = fromNum2Point(result);
        if(currentSimplePoint.getX() >= x || currentSimplePoint.getY() >= y){
            return prevPoint();
        }else{
            if(points[currentSimplePoint.getX()][currentSimplePoint.getY()] == null){
                currentPoint = new TycoonPoint(currentSimplePoint.getX(),currentSimplePoint.getY());
                setLTude(currentPoint);
                points[currentSimplePoint.getX()][currentSimplePoint.getY()] = currentPoint;
            }
            return currentPoint;
        }
    }
    
    public boolean isLastPoint(){
        int lastNumber = (int)Math.pow(2d, mapSize)-1 ;
        return currentPoint.getX() == lastNumber && currentPoint.getY() == lastNumber;
    }
   
    public boolean isFirstPoint(){
        return currentPoint.getX() == 0 && currentPoint.getY() == 0;
    }
    
    private int fromPoint2Num(SimplePoint point){
        int result = 0;
        for(int i = 0; i <= mapSize; i++){
            int firstBit =  getBit(point.getY(), i);
            int secondBit = getBit(point.getX(), i);
            result = (result<<2)| (firstBit <<1)| secondBit;
        }
        return result;
    }
    
    private SimplePoint fromNum2Point(int result){
        int nxtX = 0;
        int nxtY = 0;
        for(int i = 0; i <= mapSize; i++){
            nxtX = (nxtX << 1) | getBit(result,i*2);
            nxtY = (nxtY << 1) | getBit(result,i*2+1);
        }
        return new SimplePoint(nxtX,nxtY);
    }
}

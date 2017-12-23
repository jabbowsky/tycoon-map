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
     
    private double maxHeight = 8848d; // Everest   
    private double seaLine = 0d; // it's almost real sea line
    private int rateOfSmoose = 1;
    private int mapPower = 0;
    private TycoonPoint currentPoint;
    private SimplePoint currentSimplePoint;
    private int countPoints = 0;
    public boolean isEoP = false;
    
    public TycoonMap(int x, int y, TycoonPoint[][] points, String name){
        this.x = x-1;
        this.y = y-1;
        this.name = name;
        this.points = points;
        // Calculate power of map to use it order
        this.mapPower = (int)round(Math.log(x>y?x:y)/Math.log(2),0) ;
      //  this.mapPower = x>y?x:y;
        countPoints = x*y;
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
    
    /* This function need to set border points
     * points name we take from next squer:
     *    A---B --- x
     *    |   |
     *    D---C    
     *    |
     *    |
     *    y
     * there is 3 options: 
     * function called with 4 points 
     * function called with 2 points and rest 2 points are calculated automaticaly as squer points
     * function called without any points in this case we take default
     */
    public void setStartPoints(TycoonPoint[] startPoint){
        switch (startPoint.length) {
            case 4:
                points[0][0] = startPoint[0];
                points[x][0] = startPoint[1];
                points[x][y] = startPoint[2];
                points[0][y] = startPoint[3];
                break;
            case 2:
                points[0][0]       = startPoint[0];
                TycoonPoint point1 = new TycoonPoint(x,0);
                point1.setLat(startPoint[0].getLat());
                point1.setLng(startPoint[1].getLng());
                points[x][0]       = point1;
                points[x][y]       = startPoint[1];
                TycoonPoint point3 = new TycoonPoint(0,y);
                point3.setLat(startPoint[1].getLat());
                point3.setLng(startPoint[0].getLng());
                points[0][y]       = point3;
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
        startPoint[1] = new TycoonPoint(x,y);
        startPoint[1].setLat(latC);
        startPoint[1].setLng(lngC);
        setStartPoints(startPoint);
    }
    
    private void setStartPoints(){
        points[0][0] = new TycoonPoint(0,0);
        points[x][0] = new TycoonPoint(x,0);
        points[0][y] = new TycoonPoint(0,y);
        points[x][y] = new TycoonPoint(x,y);
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
    
    public void checkPointDb(TycoonPoint point){
        // check point in database
        
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
    /*
     *    A-x1,y1-B->x
     *    |   |   |
     *    |-xn,yn-|
     *    |   |   |
     *    D-x2,y2-C
     *    |
     *    v y
     *    
     * X(n,m) = (X1+X2+X3+X4)*n*m/(N*M)
     */
    private void setFlatLTude(TycoonPoint point){
       TycoonPoint pointA = points[0][0];
       TycoonPoint pointB = points[x][0];
       TycoonPoint pointC = points[x][y];
       TycoonPoint pointD = points[0][y];
     
       double x1 = pointA.getLat() + (pointB.getLat() - pointA.getLat())*(point.getX())/(x);
       double y1 = pointA.getLng() + (pointB.getLng() - pointA.getLng())*(point.getX())/(x);
       double x2 = pointD.getLat() + (pointC.getLat() - pointD.getLat())*(point.getX())/(x);
       double y2 = pointD.getLng() + (pointC.getLng() - pointD.getLng())*(point.getX())/(x);
       double lat = x1 + (x2 - x1)*(point.getY()+1)/(y);
       double lng = y1 + (y2 - y1)*(point.getY()+1)/(y);
    
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
        if(result >= countPoints){
            result = 0;
            isEoP = true;
        }
        currentSimplePoint = fromNum2Point(result);
        if(currentSimplePoint.getX() > x || currentSimplePoint.getY() > y){
            return nextPoint();
        }else{
            if(points[currentSimplePoint.getX()][currentSimplePoint.getY()] != null){
                currentPoint = points[currentSimplePoint.getX()][currentSimplePoint.getY()];
            }else{
                currentPoint = new TycoonPoint(currentSimplePoint.getX(),currentSimplePoint.getY());
                setFlatLTude(currentPoint);
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
        if(currentSimplePoint.getX() > x || currentSimplePoint.getY() > y){
            return prevPoint();
        }else{
            if(points[currentSimplePoint.getX()][currentSimplePoint.getY()] == null){
                currentPoint = new TycoonPoint(currentSimplePoint.getX(),currentSimplePoint.getY());
                setFlatLTude(currentPoint);
                points[currentSimplePoint.getX()][currentSimplePoint.getY()] = currentPoint;
            }
            return currentPoint;
        }
    }
    
    public boolean isLastPoint(){
        return currentPoint.getX() == x && currentPoint.getY() == y;
    }
   
    public boolean isFirstPoint(){
        return currentPoint.getX() == 0 && currentPoint.getY() == 0;
    }
    
    public int fromPoint2Num(SimplePoint point){
        int result = 0;
        for(int i = 0; i < mapPower; i++){
            int firstBit =  getBit(point.getY(), i);
            int secondBit = getBit(point.getX(), i);
            result = (result<<2)| (firstBit <<1)| secondBit;
        }
        return result;
    }
    
    public SimplePoint fromNum2Point(int result){
        int nxtX = 0;
        int nxtY = 0;
        for(int i = 0; i < mapPower; i++){
            nxtX = (nxtX << 1) | getBit(result,i*2);
            nxtY = (nxtY << 1) | getBit(result,i*2+1);
        }
        return new SimplePoint(nxtX,nxtY);
    }
    
    public String testFromPoint2Num(){
        int length = String.valueOf(x*y).length();
        String res = "testFromPoint2Num(mapPower"+String.valueOf(mapPower)+ " length:" +String.valueOf(length)+"):<br/>";
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                SimplePoint p = new SimplePoint(i,j);
                res += "|"+String.format("%0"+length+"d",fromPoint2Num(p))+"|";
            }
            res += "<br/>";
        }
        return res;
    }
    
    public String testFromNum2Point(){
        String res = "testFromNum2Point(mapPower"+String.valueOf(mapPower)+"):<br/>";
        int lengthX = String.valueOf(x).length();
        int lengthY = String.valueOf(y).length();
        for(int i=0;i<(x+1)*(y+1);i++){
          SimplePoint p = fromNum2Point(i);
          res += String.format("%0"+lengthX+"d",p.getX())+","+String.format("%0"+lengthX+"d",p.getY())+" - " + String.valueOf(i) +";<br/>";
        }
        return res;
    }
}

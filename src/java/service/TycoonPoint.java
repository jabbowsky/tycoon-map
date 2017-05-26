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
public class TycoonPoint {
    private int x; 
    private int y;
    private double lat;
    private double lng;
    private double height;
    private int tyColor; 

    TycoonPoint(){
    }

    TycoonPoint(int x,int y){
        this.x = x;
        this.y = y;
        this.lng = (double)x;
        this.lat = (double)y;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getTyColor() {
        return tyColor;
    }

    public void setTyColor(int tyColor) {
        this.tyColor = tyColor;
    }
    
}

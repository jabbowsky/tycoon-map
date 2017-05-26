/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author mikhail
 */
public class MapMaker {
    
    public static int testMakeAndSaveMap() {
        BufferedImage img = testMap( 1024, 1024 );
        simpleSavePNG( img, "/Users/mikhail/map."+System.currentTimeMillis()+".bmp" );
        return 0;
    }

    private static BufferedImage testMap( int sizeX, int sizeY ){
        final BufferedImage res = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_RGB );
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++){
                res.setRGB(x, y, 0xff000000 | x*1024*16+y);
            }
        }
        return res;
    }

    private static void simpleSavePNG( final BufferedImage bi, final String path ){
        try {
            File mapFile = new File(path);
            mapFile.getParentFile().mkdirs(); 
            mapFile.createNewFile();
            RenderedImage rendImage = bi;
            ImageIO.write(rendImage, "bmp", mapFile);
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

}

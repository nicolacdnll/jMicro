/*
 * Copyright (C) 2015 Nicola Cadenelli (nicolacdnll@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jmicro;

import java.awt.image.BufferedImage;
import static jmicro.utils.MyIO.writeImage;

/**
 * Utility class for the JMicro project.
*
 */
public class Utils {
    
    /**
     * Prints a string as output.
     * @param str 
     */
    static public void print(String str) {
        System.out.println(str);
    } 
    

    /**
     * Saves a picture on disk to a given absolute position.
     * @param image     The image
     * @param fileName  The absolute file name
     */
    public static void savePictureWithAbsoluteFileName(BufferedImage image, String fileName){
        jmicro.utils.MyIO.writeImage(image, SettingsManager.getImagesExtension(), fileName);
    }
    
    /** 
     * Saves a picture in the project's directory.
     * The file created will be named using the following pattern: dd MMM yyyy HH:mm:ss.
     * @param image The image 
     * @param path  The path where to save the image
     * @return 
     */
    public static String savePicture(BufferedImage image, String path) {
        return savePicture(image, path, jmicro.utils.Misc.getTime("dd MMM yyyy HH:mm:ss"));
    }   
    
    /** 
     * Saves a picture in the project's directory with the given file name.
     * @param fileName      The file name of the picture
     * @param image         The image
     * @param path  The path where to save the image
     * @return 
     */
    public static String savePicture(BufferedImage image, String path, String fileName) {
        
        String ext = SettingsManager.getImagesExtension();

        String[] filePath = {
            path,
            fileName + "." + ext
        };

        String outputFileName = jmicro.utils.MyIO.composePath(filePath);

        writeImage(image, ext, outputFileName);
        
        return outputFileName;
    }
}

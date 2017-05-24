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
package jmicro.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for save and load the properties.
*
 */
public class MyProperties {

    /**
     * Loads all the properties from file.
     * @param fileName The file that contains the properties.
     * @return The Properties loaded.
     * @see Properties
     */
    public static Properties loadProperties(String fileName) {
        Properties p = new Properties();
        FileInputStream fis = null;
        if(!jmicro.utils.MyIO.exists(fileName))
            return p;
        try {
            fis = new FileInputStream(fileName);
            //load the preferences from input stream
            p.load(fis);
            //cloase the input stream
            //fis.close();
        } catch (IOException ex) {
            Logger.getLogger(MyProperties.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(MyProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return p;
    }

    /**
     * Store properties on a file.
     * @param fileName The file that contains the properties.
     * @param p The properties.
     * @return The Properties loaded.
     * @see Properties
     */
    public static boolean storeProperties(String fileName, Properties p) {
        FileOutputStream fos = null;
        boolean saved = false;
        try {
            //open the output stream
            fos = new FileOutputStream(fileName);
            //save the user preferences on file
            p.store(fos, null);
            saved = true;
        } catch (IOException ex) {
            saved = false;
            Logger.getLogger(MyProperties.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(MyProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return saved;
    }    
}

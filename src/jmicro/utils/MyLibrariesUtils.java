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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
*
 */
public class MyLibrariesUtils {
    
    public static boolean extractLib (String lib, String dest){
        InputStream     in;
        OutputStream    out;
        boolean         return_val = false;
        try {
            // Open the streams
            in  = MyLibrariesUtils.class.getResourceAsStream(lib);
            out = new FileOutputStream(new File(dest));
            
            // Manually copy of the stream.
            // NOTE: Java 7 has an ad-hoc method for this
            byte[] buffer = new byte[10240];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            // Close the streams
            in.close();
            out.close();
            
            return_val = true;
        } catch (IOException ex) {
            System.out.println("Critical Error extracting the library: "+lib);
        }
        return return_val;
    }
    
}

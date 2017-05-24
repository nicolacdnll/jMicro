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

package jmicro.v4l4jutils;

import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jmicro.Globals;


/**
 * Class of utility for search the V4L devices connected to the computer
*
 */
public class v4l4jMyUtils {

    private static final boolean SEARCH_DEBUG = true;

    public static List<String> searchFreeDevices(boolean debug) {
        List<String> devices_free       = new ArrayList();
        
        // Get all supported divices
        List<String> devices_supported  = searchSupportedDevices(debug);
        
        // Filter free devices
        for (String d: devices_supported) {
            if (isFrameGrabberFree(d)) {
                devices_free.add(d);
            }
        }
        
        return devices_free;
    }
    
    /**
     * Searches all the supported devices connected.
     * @param debug
     * @return A list of all the file names of the devices connected and supported.
     */
    public static List<String> searchSupportedDevices(boolean debug) {
        List<String> devices_list = new ArrayList();
        
        if (SEARCH_DEBUG&debug) {
            System.out.println("List of attached devices:");
        }

        // Open the directory containing the video devices
        File dir = new File(Globals.DEVICES_DIR);

        // Filter all the file in the directory using our filter
        String[] devices = dir.list(new V4L2VideoDevicesFileNameFilter());
      
        // Compose the absolute filename
        for (String d : devices) {
            devices_list.add(Globals.DEVICES_DIR.concat(d)); 
        }
        // Add non device
        devices_list.add(Globals.NO_DEVICE_CODE);  
        
        Collections.reverse(devices_list);
        return devices_list;
    }

    public static List<String> searchSupportedDevicesNiceNames() {
        List<String> devices      = searchSupportedDevices(false);
        List<String> names = new ArrayList();
        String temp;
        
        // Compose the absolute filename
        for (String d : devices) {
            temp = getDeviceName(d)+" @"+d;
            names.add(temp);
        }
        
        return names;
    }
        
    public static String getDeviceName(String d) {
        String return_value;
        try {
            if (d.equals(Globals.NO_DEVICE_CODE))
                return_value = Globals.NO_DEVICE_NAME;
            else
                return_value = (new VideoDevice(d)).getDeviceInfo().getName();
        } catch (V4L4JException ex) {
            return_value = "unknown";
        }
        return return_value;
    }
    /**
     * Tests if the frame grabber of the device is free.
     * @param d
     * @return True if the frame grabber is free, false otherwise.
     */
    public static boolean isFrameGrabberFree(String d) {
        boolean isFree = false;
        String debug;
        String name;
        try {
            
            if(d.equals(Globals.NO_DEVICE_CODE)) {
                isFree =  true;
                name = Globals.NO_DEVICE_NAME;
            } else {
                // Get the videodevice
                VideoDevice videoDev = new VideoDevice(d);
                
                // Try to get the video device
                FrameGrabber fG = videoDev.getJPEGFrameGrabber(
                        V4L4JConstants.MAX_WIDTH,
                        150,
                        0,
                        V4L4JConstants.STANDARD_WEBCAM,
                        80);
                
                // Check if ok
                if (fG != null) isFree = true;
                
                // Store the name
                name = videoDev.getDeviceInfo().getName();
                
                // Releas the framegrabber and the videodevice
                videoDev.releaseFrameGrabber();
                videoDev.release();
            }
            // Compose debug output
            debug = String.format("%-50s on %s", name, d);
            
            if (isFree = true) {
                debug += " is free";
            } else {
                debug += " is NOT free";
            }
            
            if (SEARCH_DEBUG) System.out.println(debug);

        } catch (V4L4JException ex) {
            //Logger.getLogger(v4l4jMyUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isFree;
    }
}

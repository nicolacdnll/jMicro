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

/**
 * Class that stores all the user parameters given as input.
 */
public class UserParameters {
    
    private static String device = null;

    /**
     * Parses the args parameters.
     * @param args  The user arguments to parse
     * @return      A negative value if an error occur or if the help is printed, non-negative otherwise.
     */
    public static int parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            //-D deviceFile
            if(arg.equals("-D")) {
                ++i;
                device = args[i]; 
                continue;
            }
            
            //--deviceFile=deviceFileName
            if(arg.startsWith("--deviceFile=")) {
                ++i;
                device = arg.replace("--deviceFile=", "");
                continue;
            }
            
            
            if(arg.equals("-r")) {
                ++i;
                Utils.print("Removing previous preferences");
                SettingsManager.removePreferences();
                continue;
            }
            
            //-h, --help
            if(arg.equals("--help") || arg.equals("-h") ) {
                printHelp();
                return -1;
            }

            System.err.println("Unrecognized option: " + arg);
            return -1;
        }
        return 0;
    }
    
    /**
     * Returns the device option chosen by the user.
     * @return The device path
     */
    public static String getDevice() {
        return device;
    }
    
    /**
     * Prints the help dialog.
     */
    private static void printHelp(){
        // Compose message
        // If the number of parameters increase it could be useful to generate 
        // the message from arrays
        String t = Globals.NAME + " v" + Globals.VERSION + "\n";
        t += "Usage: java -jar jMicro.jar [-h] [-D <str>]\n";
        t += "  -h, --help            \tShows this help dialog\n";
        t += "  -r,                   \tRemove previous preferences\n";
        t += "  -D, --deviceFile=<str>\tSelects a particular device\n";
        
        Utils.print(t); 
    }

}

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
 * Class that stores global parameters of the project.
 */
public class Globals {
    public static final String  NAME                = "jMicro";
    public static final String  VERSION             = "2.0.5";    
    
    //.................................................................Debugging
    public static final boolean DEVELOPING          = false; // Set this to false when building to distribute
    public static final boolean FIRST_START_ALWAYS_ACTIVE = false;
    public static final String PREFERENCES_NAME    = "com.jMicro.preferences-"+VERSION;
    
    
    //...................................................................Devices
    public static final String NO_DEVICE_CODE      = "none";
    public static final String NO_DEVICE_NAME      = "null";
    
    public static final String DEVICES_DIR = "/dev/";     // In linux Systems
    static final String QX3_NAME = "Intel Play QX3 Microscope";
    
    
    //.............................................................. Dependences
    public static final String PACKAGE_VENDORS      = "/resources/vendors/";
    public static final String PACKAGE_I386         = "i386/";
    public static final String PACKAGE_AMD46        = "amd64/";
    // Directory where the libs will be extracted in the user system
    public static final String TEMP_FOLDER          = "/tmp/";
    // Names of the files
    public static final String[] LIBS_NAMES         = {   
                                                    "libjpeg.so.62.0.0", 
                                                    "libvideo.so.0", 
                                                    "libv4l4j.so"
    };
    
    
    //......................................................Internationalization
    public static final String LANGUAGE_MAIN         = "resources.languages.main.language";
    public static final String LANGUAGE_FIRSTSTART   = "resources.languages.firststart.language";    
    public static final String LANGUAGE_UPDATE       = "resources.languages.updatemanager.language";    
    
    public static final String ENTRY_ENGLISH        = "English";
    public static final String ENTRY_ENGLISH_CODE   = "en";
    public static final String ENTRY_ITALIAN        = "Italiano";
    public static final String ENTRY_ITALIAN_CODE   = "it";
    public static final String ENTRY_GERMAN         = "Deutsch";
    public static final String ENTRY_GERMAN_CODE    = "de";
    
    public static String [] ENTRIES_LANGUAGES           = new String [] {ENTRY_ENGLISH,ENTRY_ITALIAN,ENTRY_GERMAN};
    public static String [] ENTRIES_LANGUAGES_CODES     = new String [] {ENTRY_ENGLISH_CODE,ENTRY_ITALIAN_CODE,ENTRY_GERMAN_CODE};

    public static String FORMAT_FILE    = "png";
    public static String FORMAT_VIDEO   = "mp4";

    public static final int LAPS_IN_VIDEO   = 2000;
    //..................................................................UPDATES
    public static final String   UPDATE_LOCAL_ARCHIVE_NAME = NAME+"_last.tar.bz2"; //jMicro_last.tar.bz2
    public static final String[] UPDATE_URLS = {
        "https://raw.githubusercontent.com/nicolacdnll/jMicro/master/dist/",
    };
}
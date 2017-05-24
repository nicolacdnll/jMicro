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

package jmicro.updatemanager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import jmicro.Globals;

import jmicro.utils.MyIO;

/**
 * Class used to manage the updates.
 */
public class UpdateManager {
    
    private static final String TEMP_DIR = "/tmp/";
    private static final String TEMP_ARCHIVE_FILE = Globals.UPDATE_LOCAL_ARCHIVE_NAME;
    private static final String INFO_FILE = "info.properties";
    private static final String INFO_FILE_LOCALLY = TEMP_DIR+INFO_FILE;
    
    //private static final String KEY_CHANGELOG = "log_";
    private static final String KEY_LONG_MSGS = "msg_";
    
    private static String               last_version;
    private static String               date;
    private static String               dimension;
    //private static Map<String, String>  logs;
    private static Map<String, String>  long_messages;
    private static String               remote_url; //the file
    private static String               _url;
    
    /**
     * Downloads the info remote_url and stores information on last version locally.
     * The info remote_url is deleted at the end.
     * @param url The url of the remote directory where the info remote_url is stored
     * @param languages_id The supported ids of the languages. 
     * @return True if all when ok, false otherwise.
     */
    static public boolean fetchInfo (final String url, final String[] languages_id) {
              
        // Compose the url of the info remote_url correctly
        if (url.endsWith("/")) {
            _url    = url.concat(INFO_FILE);
        } else {
            _url    = url.concat("/").concat(INFO_FILE);
        }
        
        // Download the remote_url locally
        if (!UpdateManagerUtils.dowloadFile(_url, INFO_FILE_LOCALLY)) return false;
        
        /* Opens the info remote_url as a ResourceBoundle and extracts the values into
        private fields */
        try {
            /* Do not use _url directly in urls because the server might be set 
             with no permission to read the directory content */
            File f = new File(TEMP_DIR);
            URL[] urls = {f.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            ResourceBundle rb = ResourceBundle.getBundle("info", Locale.getDefault(), loader);
            
            last_version = rb.getString("version");
            //Utils.print("Last version "+last_version);
            date         = rb.getString("date");
            dimension    = rb.getString("dimension");
            remote_url         = rb.getString("file");
            /*logs = new HashMap<String, String>();
            for (String s: languages_id) {
                * Use the id (e.g., en, it, de) as key *
                logs.put(s, rb.getString(KEY_CHANGELOG+s));
            }*/
            long_messages = new HashMap<String, String>();
            for (String s: languages_id) {
                /* Use the id (e.g., en, it, de) as key */
                long_messages.put(s, rb.getString(KEY_LONG_MSGS+s));
            }
        } catch (MalformedURLException ex) {
            return false;
        }
        // Delete the remote_url
        MyIO.deleteFile(INFO_FILE_LOCALLY); //consider to control the return value or not
        return true;
    }
    

    static protected String getTempFileName(){
        return TEMP_DIR +TEMP_ARCHIVE_FILE;
    }
    
    static protected String getRemoteURL(){
        return remote_url;
    }
    
    /*NOT NEEDEDstatic public boolean update (String path) {
        // Download program 
        Utils.print("Downloading "+getRemoteURL()); 
        String temp_file = getTempFileName();
        Utils.print("in "+temp_file);
        if ( !UpdateManagerUtils.dowloadFile(getRemoteURL(),    temp_file) )        return false;
        
        // Extract it
        String temp_file_tar = temp_file.replace(".bz2", "");
        Utils.print(temp_file_tar);
        // from tar.bz2 to tar
        if ( !UpdateManagerUtils.uncompressBZ2(temp_file,       temp_file_tar) )    return false;
        // extract tar
        Utils.print("Extracting in "+path);
        if ( !UpdateManagerUtils.uncompressTAR(temp_file_tar,   path) )             return false;

        // Cleaning
        MyIO.deleteFile(temp_file);
        MyIO.deleteFile(temp_file_tar);
        return true;
    }
    
*/
    

    //...................................................................GETTERS
    /**
     * Returns if the actual version is the last available or not
     * @param version The string identifying the actual version
     * @return True if it's the last version, false if a new update is available.
     */
    static public boolean isLastVersion (String version) {
        boolean isLast = true;
        
        // String#split method takes a regex thus we must excape the dot char
        String[] actual     = version.split("\\.");
        
        String[] last       = getLastVersion().split("\\.");
        
        int a;
        for ( int i = 0; i < last.length; i++) {
            // Control needed if a new release adds a new levels of nomenclature
            // E.g., if actual is 3.14 and last is 3.14.159 we will have a null pointer
            if ( i >= actual.length)  a = 0;
            else                      a = Integer.parseInt(actual[i]);
            
            if ( a < Integer.parseInt(last[i]) ) {
                isLast = false;
                break;
            }
        }
        return isLast;
    }
    
    static public String getLastVersion () {
        return last_version;
    }
     
    static public String getDate() {
        return date;
    }
    
    static public String getDimension(){
        return dimension;
    }
    
    /*static public String getChangeLog(String language_id) {
        return logs.get(language_id);
    }*/

    static public String getLongMessage(String language_id) {
        return long_messages.get(language_id);
    }
}

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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import jmicro.utils.Misc;
import jmicro.utils.MyIO;

/**
 * Class to manage user settings.
 */
public class SettingsManager {
 
    public static final int    USERMODE_DEFAULT        = SettingsManager.USERMODE_BASIC;
    public static final String USERMODE_KEY            = "usermode";
    public static final int    USERMODE_BASIC          = 0;
    public static final int    USERMODE_ADVANCED       = 1;
    
    public static final String LANGUAGE_DEFAULT        = Globals.ENTRY_ENGLISH_CODE;
    public static final String LANGUAGE_KEY            = "language";
    
    // Both project and workspace are the absolute path
    public static final String PROJECT_KEY             = "project";
    public static final String WORKSPACE_DEFAULT       = Globals.NAME;
    
    public static final String WORKSPACE_KEY           = "workspace";
    
    public static final String UPDATES_KEY             = "updates";

    public static final boolean UPDATES_DEFAULT         = true;
    
    private static Preferences prefs;
    
    /**
     * Checks weather or not the required preferences (user mode and language) 
     * are stored or not.
     * Method used to hide what is required or not from the calling code.
     * The complexity of this method might increase with the time but is important to keep a simple interface.
     * @return A positive value if stored, negative otherwise.
     */
    public static boolean areRequiredPreferencesStored() {        
        return preferencesNodeExists();
    }
    
    public static void removePreferences() {
        try {
            Preferences.userRoot().node(Globals.PREFERENCES_NAME).removeNode();
        } catch (BackingStoreException ex) {
            Logger.getLogger(SettingsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Controls weather or not the preference node exists in the system. 
     * @return True if the node exist, false otherwise.
     */
    private static boolean preferencesNodeExists() {
        try {
            return Preferences.userRoot().nodeExists(Globals.PREFERENCES_NAME);
        } catch (BackingStoreException ex) {
            return false;
        }
    }
    
    

    /**
     * Creates a preference node in the system.
     * The name of the node is given by the Globals.PREFERENCES_NAME.
     */
    private static void nodeCreateAndLoad() {
        Preferences.userRoot().node(Globals.PREFERENCES_NAME);
        nodeLoad();
    }
    
    /**
     * Loads the node from the system
     */
    public static void nodeLoad() {
        prefs = Preferences.userRoot().node(Globals.PREFERENCES_NAME);      
    }
    
    //..............................................................DUMMY GETTER
    //These getter were created because we can forsee that in futer versione 
    //those parameters might be setted by users
    
    /**
     * Returns the file format of the images.
     * @return The extension of the images i.e., "png"
     */
    public static String getImagesExtension(){
        return Globals.FORMAT_FILE;
    }
    
    /**
     * Returns the file format of the videos.
     * @return The extension of the videos i.e., "mp4"
     */
    public static String getVideoFormat(){
        return Globals.FORMAT_VIDEO;
    }
    
    //..................................................................USERMODE
    /**
     * Return the user mode
     * @return The user mode value; one of USERMODE_BASIC or USERMODE_ADVANCED
     */
    public static int getUserMode(){
        if (prefs == null) nodeLoad();
        return prefs.getInt(USERMODE_KEY, USERMODE_DEFAULT);      
    }
    
    /**
     * Set a new user mode
     * @param value The user mode value; one of USERMODE_BASIC or USERMODE_ADVANCED
     */
    public static void setUserMode(int value){
        if (value == USERMODE_BASIC || value == USERMODE_ADVANCED) {
            if ( !preferencesNodeExists() ) nodeCreateAndLoad();
            prefs.putInt(SettingsManager.USERMODE_KEY, value);
        } else {
            Utils.print("ERROR: "+value+" is an invalide user code.");
        }
    }
    
    //.....................................................PROJECT AND WORKSPACE
    /**
     * Returns the workspace path.
     * That is the directory containing the project
     * @return The workspace path
     */
    public static String getWorkspacePath(){
        if (prefs == null) nodeLoad();
        return prefs.get(WORKSPACE_KEY, getDefaultWorkspacePath());
    }
    
    /**
     * Returns the project path.
     * @return The project path
     */
    public static String getProjectPath() {
        if (prefs == null) nodeLoad();
        return prefs.get(PROJECT_KEY,"");
    }
    
    /**
     * Sets a new workspace path
     * @param value The workspace path
     */
    public static void setWorkspacePath(String value){
        if ( !preferencesNodeExists() ) nodeCreateAndLoad();
        prefs.put(SettingsManager.WORKSPACE_KEY, value);    
    }
    
    /**
     * Sets a new project path
     * @param value The new project path
     */
    public static void setProjectPath(String value){
        if ( !preferencesNodeExists() ) nodeCreateAndLoad();
        prefs.put(SettingsManager.PROJECT_KEY, value);    
    }
    
    /**
     * Private method to compose the default workspace path
     * @return The default workspace path
     */
    private static String getDefaultWorkspacePath(){
        return MyIO.composePath(new String [] {Misc.getHomeDir(), WORKSPACE_DEFAULT});
    }

    /**
     * Sets the project and workspace paths
     * @param value The project path.
     */
    public static void setProjectAndWorkspacePaths(String value) {
        setProjectPath(value);
        setWorkspacePath(MyIO.getUpDirs(value, true)[1]);
    }

            
    //............................................. LANGUAGE, COUNTRY AND LOCALE
    /**
     * Returns the code of the language saved in the preferences.
     * @return The code of the language. If missing returns the {@link LANGUAGE_DEFAULT}
     */
    public static String getLanguageCode() {
        if (prefs == null) nodeLoad();
        return prefs.get(LANGUAGE_KEY, LANGUAGE_DEFAULT);     
    }          

    /**
     * Sets the code of the language.
     * If the code is not a valid language code contained in {@link Globals#ENTRIES_LANGUAGES_CODES} an error is printed.
     * 
     * @param value
     */
    public static void setLanguageCode(String value) {
        if ( Arrays.asList(Globals.ENTRIES_LANGUAGES_CODES).indexOf(value) < 0) {
            Utils.print("ERROR: "+value+" is an invalide language code.");
        } else {
            if ( !preferencesNodeExists() ) nodeCreateAndLoad();
            prefs.put(LANGUAGE_KEY, value);   
        }
    } 
    
    //..................................................................UPDATES
    /**
     * Return the 
     * @return The user mode value; one of USERMODE_BASIC or USERMODE_ADVANCED
     */
    public static boolean getCheckUpdate(){
        if (prefs == null) nodeLoad();
        return prefs.getBoolean(UPDATES_KEY, UPDATES_DEFAULT);
    }
    
    /**
     * @return The user mode value; one of USERMODE_BASIC or USERMODE_ADVANCED
     */
    public static void setCheckUpdate(boolean b){
        if ( !preferencesNodeExists() ) nodeCreateAndLoad();
        prefs.putBoolean(UPDATES_KEY, b);
    }
}

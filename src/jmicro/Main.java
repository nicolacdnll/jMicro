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


import jmicro.gui.mvc.firstStart.FSView;
import jmicro.gui.mvc.firstStart.FSModel;
import jmicro.gui.mvc.firstStart.FSController;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmicro.utils.MyIO;
import jmicro.utils.MyLibrariesUtils;
import jmicro.utils.MyLookAndFeel;

import jmicro.v4l4jutils.Device;
import jmicro.v4l4jutils.v4l4jMyUtils;
import jmicro.updatemanager.UpdateManager;

/**
 * Main logic of the program.
 */
public class Main {
        
    /* Main GUI classes */
    static private jmicro.gui.mvc.main.MModel      model;     // = new FSModel();
    static private jmicro.gui.mvc.main.MView       view;      // = new FSView(model);
    static private jmicro.gui.mvc.main.MController controller;// = new FSController(model, view);
    
    /**
     * The main method.
     * @param args User's arguments.
     */
    public static void main(String[] args){
        
        /* Parses the user arguments */
        if ( UserParameters.parse(args) < 0 ) System.exit(1);
        
        /* Sets the look and feel */
        MyLookAndFeel.setSystemLF(true);
            
        /* Check if preferences are stored or not */
        if ( !SettingsManager.areRequiredPreferencesStored() || Globals.FIRST_START_ALWAYS_ACTIVE) {
            if (Globals.FIRST_START_ALWAYS_ACTIVE) SettingsManager.nodeLoad();
            /* Preferences not found */
            Main.firstStart();
        }
        
        if ( SettingsManager.getCheckUpdate() ) UpdateManager.fetchInfo(Globals.UPDATE_URLS[0], Globals.ENTRIES_LANGUAGES_CODES);
                
        /* Extracts and loads needed libraries */
        if ( Main.initialize() < 0 ) System.exit(1);
        
        /* Select the first free device */
        Device d = new Device(Main.selectDevice());
        
        /* Make sure the workspace directory exists */
        MyIO.makeSureExistDir(SettingsManager.getWorkspacePath());
        
        /* Start Main interface: Workspace selection and main window */
        Main.startInterface(d);
        
    }
    
    /**
     * Initialises the program.
     * 1 - Extracts and loads the needed libraries
     * @return A non-negative value if no error occur, negative otherwise.
     */
    public static int initialize () {        
        int return_value    = -1; 
        String lib_package  = null;
        String lib          = null;
        
        // Prints the jMicro vX
        Utils.print("\n\n"+Globals.NAME + " v" + Globals.VERSION);
        
        // Gathers infos about the system
        String arch     = System.getProperty("os.arch");          // Check Architecture
        String os       = System.getProperty("os.name");          // Check OS (Linux, Windows)
        String osv      = System.getProperty("os.version");       // Ceck kernel version
        String libPath  = System.getProperty("java.library.path");// Save old libraryPath
   
        // Checks the computer architecture to load proper libs
        if (arch.contains("64")) {
            //x86_64, amd64 and ppc64
            lib_package = Globals.PACKAGE_VENDORS.concat(Globals.PACKAGE_AMD46);
            
        } else if (arch.equals("x86") || arch.equals("i386")) {
            lib_package = Globals.PACKAGE_VENDORS.concat(Globals.PACKAGE_I386);
        } else {
            // sparc, ppc, armv41, i686
            System.err.println("Computer Architecture " + arch + " not supported.");
            return return_value;
        }       
        try {            
            //Utils.print("\n\n\nINIT\nOS = " + os + " " + osv + "\nARCH = " + arch);            
            
            lib = Globals.LIBS_NAMES[0];
            MyLibrariesUtils.extractLib(
                    lib_package.concat(lib),
                    Globals.TEMP_FOLDER.concat(lib));
            //Utils.print("Loading " + Globals.TEMP_FOLDER.concat(lib));
            System.load(Globals.TEMP_FOLDER.concat(lib));
            
            lib = Globals.LIBS_NAMES[1];
            MyLibrariesUtils.extractLib(
                    lib_package.concat(lib),
                    Globals.TEMP_FOLDER.concat(lib));
            //Utils.print("Loading " + Globals.TEMP_FOLDER.concat(lib));
            System.load(Globals.TEMP_FOLDER.concat(lib));
            
            lib = Globals.LIBS_NAMES[2];
            MyLibrariesUtils.extractLib(
                    lib_package.concat(lib),
                    Globals.TEMP_FOLDER.concat(lib));
            // No need to load this System.load(Globals.TEMP_FOLDER.concat(lib));
            
            //Utils.print("Updating Java Path, old = " + libPath);
            System.setProperty("java.library.path", Globals.TEMP_FOLDER+":" + libPath );
            // Dirty trick to force the VM to reload the path wich is cached
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible( true );
            fieldSysPath.set( null, null );
            //Utils.print("Updating Java Path, new = " + System.getProperty("java.library.path"));

            return_value = 0;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }
    
    /**
     * Method called just at the first run on the system which shows the window 
     * to make the user select the language and the user mode (basic or not).
     * 
     * This method uses the SettingsManager class to save in the system the user
     * preferences.
     */
    public static void firstStart (){       
        // Shows window to select language and usermode
        jmicro.gui.mvc.firstStart.FSModel         m = new FSModel();
        jmicro.gui.mvc.firstStart.FSView          v = new FSView();
        jmicro.gui.mvc.firstStart.FSController    c = new FSController(m, v);
        v.setVisible(true);
    }
    
    /**
     * Returns the number of devices found.
     * @return The number of devices found
     **/
    public static String selectDevice(){
        String return_value;
        
        // Scans for free attched devices
        List<String> devices = v4l4jMyUtils.searchFreeDevices(true);
        
        // Checks if there is at least one device
        if (devices.isEmpty()) {
            /* No device found */
            Utils.print("No device found!");
            return_value = Globals.NO_DEVICE_CODE;
        } else {
            
            // Selects the last device in the list
            return_value = devices.get(devices.size()-1);
            
            // Check if the user selected a particular devics using options 
            if (UserParameters.getDevice() != null) {
                int i;
                // Checks if the selected device exists and its free
                if ( (i = devices.indexOf(UserParameters.getDevice())) < 0) {
                    // If not, selects the first device in the list
                    System.err.print("Error: " + UserParameters.getDevice()     +
                           " is not a valide device or is busy.\n"             +
                           "Starting the program using " + return_value);
                } else {
                    return_value = devices.get(i);
                }               
            }
        } 
        return return_value;
    }

    /**
     * Initialises the FSModel-FSView-FSController and displays the view.
     * @param d The device to use.
     */
    public static void startInterface (Device d){
        model      = new jmicro.gui.mvc.main.MModel(d);
        view       = new jmicro.gui.mvc.main.MView();
        controller = new jmicro.gui.mvc.main.MController(model, view);
        view.setVisible(true);
    }
    
    /**
     * Deletes the extracted libs in the /tmp folder and closes the application.
     */
    public static void myfinalize () {
        // Cleans the temporary files extracted
        MyIO.deleteFile("/tmp/libjpeg.so.62");
        MyIO.deleteFile("/tmp/libvideo.so.0");
        MyIO.deleteFile("/tmp/libv4l4j.so");  
        
        // Exit
        System.exit(0);
    }
}
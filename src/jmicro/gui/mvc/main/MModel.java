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
package jmicro.gui.mvc.main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import jmicro.Globals;
import jmicro.SettingsManager;
import jmicro.Utils;
import jmicro.utils.MyIO;
import jmicro.v4l4jutils.Device;


/**
 * Model used in the Main MVC.
 */
public class MModel {
    
    //----------------------------------------------------------------VIEW STATE
    public static final int PROJECT         = 0;
    public static final int NORMAL          = 1;
    public static final int TIMELAPS        = 2;
    public static final int SETTINGS        = 3;
    /* Peview modes need to be bigger than 100! */
    public static final int PREVIEW_VIDEO   = 101;
    public static final int PREVIEW_IMAGE   = 102;
    
    public static final int PREVIEW_VIDEO_PLAY    = 200;
    public static final int PREVIEW_VIDEO_PAUSE   = 201;
    public static final int PREVIEW_VIDEO_STOP    = 202;
    
    /* The initial state is the project selection */
    private int mode                    = MModel.PROJECT;
    private int modeBeforePreview       = MModel.PROJECT;
    private int modeBeforeSetting       = MModel.PROJECT;
    /* To now the setting dialog doesn't exist yet and that's why the settings 
        are disabled by default */
    public static final boolean DEFAULT_SETTING_ENABLED = false;
    
    
    //--------------------------------------------------------------------TIMER
    private boolean isTimerActive   = false;
    private boolean isFrozen        = false;
    
    /* The default value of the focus time is define by DEFAULT_FOCUS_TIME 
    *   (six seconds for now) to let the camera get the right focus.
    *   In case the device has no lamps this time is overwritte with a negative
    *   value.
    */
    public static final int     DEFAULT_FOCUS_TIME      = 6;
    private int focusTime; 
    
    
    //-------------------------------------------------------------------PREVIEW
    private String          previewFile;    
    private BufferedImage   previewFrame;

    
    //-------------------------------------------------------------------OTHERS
    private int             userMode;       // 
    private String          languageCode;
    private ResourceBundle  rb;             // ResourceBundle of the languaged used
    private Device          device;         // The actual device
    private String          projectPath;    // The selected project
    private List<String>    projectFiles;   // The filename of pics and videos inside the actual project
    private boolean         checkUpdates;
    /**
     * Creates a new Model instance whose uses the given device.
     * In this MVC architecture the view doesn't know the model. 
     * It's the controller that creates and queries the model.
     * @param d    The device to use.
     */
    public MModel(Device d) {
        modeBeforePreview    = MModel.NORMAL;
        device               = d;
        checkUpdates         = SettingsManager.getCheckUpdate();
        languageCode         = SettingsManager.getLanguageCode();
        rb              = ResourceBundle.getBundle(
                Globals.LANGUAGE_MAIN, 
                new Locale(languageCode)
        );
        userMode        = SettingsManager.getUserMode();
        
        // If the device has no lights there is no need to use a focus time
        if (device.hasBottomLight() || device.hasBottomLight()) {
            this.focusTime = DEFAULT_FOCUS_TIME;           
        } else {
            this.focusTime = -1;
        }
    }
    
    /** 
     * Returns the resource bundle from where get the internationalized strings.
     * @return The resource bundle
    */
    public ResourceBundle getResourceBundle () {
//        Utils.print("MModel::getResourceBundle() Get language "+rb.getLocale().getLanguage());
        return rb;
    }
    
    /**
     * Returns the user mode code 
     * @return The user mode code
     */
    public int getUserMode() {
        return userMode;
    }
    
    /**
     * 
     * @param newMode 
     */
    public void setUserMode(int newMode) {
        userMode = newMode;
    }
    
    /**
     * Sets a new language.
     * A new resource bundle is created.
     * @param l The name the new language like defined in {@link Globals} . e.g., English, Italiano
     */
    public void setLanguage (String l) {
        
        // Parses the language l
        if (l.equals(Globals.ENTRY_ENGLISH) || l.equals(""))         l = Globals.ENTRY_ENGLISH_CODE;
        else if (l.equals(Globals.ENTRY_ITALIAN))    l = Globals.ENTRY_ITALIAN_CODE;
        else if (l.equals(Globals.ENTRY_GERMAN))     l = Globals.ENTRY_GERMAN_CODE;
        else                                         l = Globals.ENTRY_ENGLISH_CODE;
        
        languageCode = l;
//        Utils.print("MModel::setLanguage() Updating resource langauge with new language "+languageCode);
        // Updates the resource bundle and store the new code
        rb = ResourceBundle.getBundle(Globals.LANGUAGE_MAIN, new Locale(languageCode));
    }
    
    public String getLanguageCode () {
        return languageCode;
    }
        
    /**
     * Returns the focus time of the selected device.
     * @return The focus time in seconds
     */
    public int getFocusTime () {
        return focusTime;
    }
    
    /**
     * Sets the device in use.
     * @param d The new device
     */
    public void setDevice(Device d) {
        device = d;
    }
    
    /**
     * Returns the device in use.
     * @return The device in use.
     */
    public Device getDevice(){
        return device;
    }
    
    /**
     * Returns the path of the file displayed in the preview.
     * @return The path to the file
     */
    public String getPreviewFile(){
        return previewFile;
    }
    
    /**
     * Sets the path of the file to display in the preview.
     * @param previewFile 
     */
    public void setPreviewFile(String previewFile){
        this.previewFile = previewFile;
    }
    
    /**
     * Returns the image of the file to preview.
     * @return The image to display
     */
    public BufferedImage getPreviewFrame() {
        return previewFrame;
    }
    
    /**
     * Sets the image to display for the preview.
     * @param previewFrame The image of the preview
     */
    public void setPreviewFrame(BufferedImage previewFrame){
        this.previewFrame = previewFrame;
    }
    
    /**
     * Sets the status of the GUI and updates the previous mode.
     * @param newMode 
     */
    public void setMode(int newMode){
        // Save the mode only when entering in preview from a non preview mode
        // The condition on setting is needed because if in setting we go back to preview,
        // the exiting from preview we go back to setting again!
        if (newMode > 100 && mode < 100 && mode != MModel.SETTINGS)    modeBeforePreview = mode;  
        // Save the mode when entering in setting mode
        if (newMode == MModel.SETTINGS &&  mode != MModel.SETTINGS )    modeBeforeSetting = mode;
        mode = newMode;
    }
    
    /**
     * Returns the actual mode of the GUI 
     * @return 
     */
    public int getMode(){
        return mode;
    }
    
    /**
     * 
     * @return 
     */
    public int getModeBeforePreview() {
        return modeBeforePreview;
    }
    
    public int getModeBeforeSetting() {
        return modeBeforeSetting;
    }
    
    /**
     * Sets if the timer is active or not.
     * @param flag 
     */
    public void setTimerActive(boolean flag){
        isTimerActive = flag;
    }
    
    public boolean isTimerActive() {
        return isTimerActive;
    }
    
    public void setFrozen(boolean flag){
        isFrozen = flag;
    }
    
    public boolean isFrozen() {
        return isFrozen;
    }
    
    public void updateProjectFiles() {
        projectFiles = new ArrayList();
        
        String[] files = MyIO.allImageAndVideoInToDir(projectPath);
        projectFiles.clear();
        Arrays.sort(files);
        projectFiles.addAll(Arrays.asList(files));
    }
    
    public List<String> getProjectFiles(){
        return projectFiles;
    }
    
    public void setProjectPath(String p) {
        projectPath = p;
    }
    
    public String getProjectPath() {
        return projectPath;
    }
    
    public boolean getCheckUpdate(){
        return this.checkUpdates;
    }

    public void setCheckUpdate(boolean b){
        this.checkUpdates = b;
    }
}

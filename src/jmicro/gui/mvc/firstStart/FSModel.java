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

/*
DISCLAMER: THIS CODE WAS MADE IN A HURRY, MIGHT RIQUIRE SOME REFACTORING ;)
 */
package jmicro.gui.mvc.firstStart;

import java.util.Locale;
import java.util.ResourceBundle;
import jmicro.Globals;
import jmicro.SettingsManager;


/**
 * Model used in the First Start MVC.
 */
public class FSModel {

    private String          languageCode;
    private ResourceBundle rb;
    private int usermode_index;
    private boolean checkUpdates;
 
    /**
     * Creates a new Model instance whose uses the given device.
     * In this MVC architecture the view doesn't know the model. 
     * It's the controller that creates and queries the model.
     */
    public FSModel() {
        checkUpdates         = SettingsManager.getCheckUpdate();
        usermode_index  = SettingsManager.USERMODE_DEFAULT;
        languageCode    = SettingsManager.getLanguageCode();
        rb  = ResourceBundle.getBundle(
                Globals.LANGUAGE_FIRSTSTART, 
                new Locale(languageCode)
        );
    }
    
    /** 
     * Returns the resource bundle from where get the internationalized strings.
     * @return The resource bundle
    */
    public ResourceBundle getResourceBundle () {
        return rb;
    }
    
    /**
     * Sets the new language.
     * A new resource bundle is created.
     * @param l The string identifying the new language
     */
    public void setLanguage (String l) {
//        String l;
        
        // Parses the language l
        if (l.equals(Globals.ENTRY_ENGLISH))         l = Globals.ENTRY_ENGLISH_CODE;
        else if (l.equals(Globals.ENTRY_ITALIAN))    l = Globals.ENTRY_ITALIAN_CODE;
        else if (l.equals(Globals.ENTRY_GERMAN))     l = Globals.ENTRY_GERMAN_CODE;
        else                                         l = Globals.ENTRY_ENGLISH_CODE;
        
        languageCode = l;
        rb = ResourceBundle.getBundle(
                Globals.LANGUAGE_FIRSTSTART, 
                new Locale(l));
    }
    
    public String getLanguageCode () {
        return languageCode;
    }
    
    
    public int getUserModeIndex () {
        return usermode_index;
    }
    
    public void setUserModeIndex (int i) {
        usermode_index = i;
    }
  
    public boolean getCheckUpdate(){
        return this.checkUpdates;
    }

    public void setCheckUpdate(boolean b){
        this.checkUpdates = b;
    }
}

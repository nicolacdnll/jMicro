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
DISCLAMER: THIS CODE WAS MADE IN A HURRY, MIGHT REQUIRE SOME REFACTORING ;)
 */
package jmicro.gui.mvc.firstStart;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JCheckBox;

import jmicro.SettingsManager;


/**
 * Controller used in the First Start MVC.
 */
public class FSController {
    //... The FSController interacts with both the FSModel and FSView.
    private final FSView  view;
    private final FSModel model;
    
    /**
     * Creates a Controller whose interacts wit the given FSModel and FSView.
     * In this MVC architecture the view doesn't know the model. 
     * It's the controller that creates and queries the model.
     * @param m
     * @param v 
     */
    public FSController(FSModel m, FSView v) {
        view  = v;
        model = m;
        
        //Add Listeners
        view.addLanguageListener    (new languageCBoxModelListener());
        view.addUserModeListener    (new usermodeCBoxModelListener());
        view.addStartListener       (new startBtnListener());
        view.addWindowListener      (new CloseWindowListener());
        view.addCheckUpdatesListener(new CheckUpdatesListener());
        view.adaptToLanguage(model.getResourceBundle());
        
//        view.setValues(model.getUserModeIndex(), model.getCheckUpdate(),model.getResourceBundle());
    }

        public void exitMVC(){   
        // Exports preference from model into the system
        SettingsManager.setCheckUpdate(model.getCheckUpdate());
        SettingsManager.setLanguageCode(model.getLanguageCode());
        SettingsManager.setUserMode(model.getUserModeIndex());
        
        view.dispose();
    }
        
    class languageCBoxModelListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent ie) {
            // This event is fired twice, one for the deselected element and one for the selected one
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                String lang = ie.getItem().toString();
                
                // Updates the model
                model.setLanguage(lang);

                // Updates the view to the new language
                view.adaptToLanguage(model.getResourceBundle());
            }
        }
    }

    class usermodeCBoxModelListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent ie) {
            // This event is fired twice, one for the deselected element and one for the selected one
            if (ie.getStateChange() == ItemEvent.SELECTED) {
               
                // Updates the model
                model.setUserModeIndex(view.getUserModeIndex());
                
                String s = "";
                // If not normal the label will be setted to ""
                if (view.getUserModeIndex() == 0 /* normal */) {
                    s = model.getResourceBundle().getString(("infoLabel"));
                }
                
                view.setTextToInfo(s);
            }
        }
    }
        
    /**
     * Action listener for the start button.
     * The dialog is disposed and the execution restart its flow.
     */
    class startBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            exitMVC();
        }
    }
    
    /**
     * Window listener used to close the entire program if the user clicks on the x of the dialog.
     */
    class CloseWindowListener implements WindowListener { 
        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            // Forces to close the program
            System.exit(0);
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
    }
    
    class CheckUpdatesListener  implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            model.setCheckUpdate(((JCheckBox)ae.getSource()).isSelected());
        }
    }
}

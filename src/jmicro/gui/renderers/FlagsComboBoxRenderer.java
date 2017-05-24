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

package jmicro.gui.renderers;


import java.awt.Font;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Class used for create the render for the languages comboBox in the ControlPanelDialog and UserPreferencesDialog NOT USED IN THE NEW GUI.
 * This render create a jLabel with the language name and an icon that it's the flag of the language.
 * 
 * REMIND: All the languages structure must be fixed. See ToDo file in the jMicro directory.
*
 */
public class FlagsComboBoxRenderer extends JLabel implements ListCellRenderer {

    private static final boolean DEBUG = false;
    private Font uhOhFont;
    private ImageIcon[] flagsImages;
    private final String MYUTILS_RESOURCE_IMAGES_FLAGS = ".jMicro.GUI.renderer.flags.";

    private String[] languagesName = {
        "Française",
        "Deutsch",
        "Italiano",
        "Español",
 //       "Lathino",
        "English"};
    
    public FlagsComboBoxRenderer() {
        if (DEBUG) {
            System.out.println("###MyUtils.Renderer: Creating a FlagsComboBoxRenderer");
        }
        setOpaque(true);
        setHorizontalAlignment(LEFT);

//        //Inits the ImageIcon array whith the same lenght of the supported locales in the main
//        flagsImages = new ImageIcon[Main.supportedLocales.length];

//        //Substitutes all the dots with the File.separator char
//        String flagsDirPath = MYUTILS_RESOURCE_IMAGES_FLAGS.replaceAll("\\.", File.separator);
//
//        //for each languages reads the flags' icon
//        for (int i = 0; i < languagesName.length; i++) {
//
//            String flagsFilePath = flagsDirPath.concat("flag_" + Main.supportedLocales[i].getLanguage() + ".png");
//
//            if (DEBUG) {
//                System.out.println("Reading the flags' icon: " + flagsFilePath);
//            }
//
//            URL iconResource = getClass().getResource(flagsFilePath);
//            if (iconResource == null) {
//                //flagsImages[i] = null;
//                //flagsImages[i].setDescription(languagesName[i]);
//                //flagsImages[i].setDescription(Main.supportedLocales[i].getLanguage());
//            } else {
//                flagsImages[i] = new javax.swing.ImageIcon(iconResource);
//            }
//////            if (flagsImages[i] != null) {
//////                flagsImages[i].setDescription(Main.supportedLocales[i].getLanguage());
//////            }
//        }
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        //Get the selected index. (The index param isn't
        //always valid, so just use the value.)
        int selectedIndex = 0;

        if (value == null) {
            selectedIndex = new Integer(0);
        } else {
            selectedIndex = ((Integer) value).intValue();
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        //Set the icon and text.  If icon was null, say so.
        ImageIcon icon = flagsImages[selectedIndex];
        String language = languagesName[selectedIndex];

        //Main.lang.getString(Main.supportedLocales[selectedIndex].getLanguage());

        
        if (icon != null) {
            setIcon(icon);
            setText(language);
            setFont(list.getFont());
        } else {
            setUhOhText(language + " (no image available)",
                    list.getFont());
        }
        return this;
    }

    /**
     * Set the font and text when no image was found.
     *
     */
    protected void setUhOhText(String uhOhText, Font normalFont) {
        if (uhOhFont == null) { //lazily create this font
            uhOhFont = normalFont.deriveFont(Font.ITALIC);
        }
        setFont(uhOhFont);
        setText(uhOhText);
    }
}
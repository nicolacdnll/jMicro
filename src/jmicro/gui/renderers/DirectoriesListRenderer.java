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

import jmicro.utils.Misc;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Class used for create the render for the jLIst in the ProjectSelection.
 * This render create a jLabel with icon, the name of the directory and in the tool tip the absolute path .
 * 
*
 */
public class DirectoriesListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            // for default cell renderer behavior
            Component c = super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);

            ((JLabel) c).setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/gui/icons/icon-folder.png")));
            ((JLabel) c).setText(Misc.getLastPart( (String) value, "/") );
            ((JLabel) c).setVerticalAlignment(JLabel.CENTER);
            ((JLabel) c).setToolTipText((String) value) ;
            //if(index%2 == 0)
              //((JLabel) c).setBackground(myutils.Misc.getColor("controlHighlight"));
        return c;
    }
}
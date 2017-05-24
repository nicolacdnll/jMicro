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

package jmicro.gui.components;

import java.awt.Color;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * JComboBox class to show helps for the UI.
 */
public class MyHelpLabelNoLine extends JLabel{
    public static final int ORIENTATION_CENTER  = 0;
    public static final int ORIENTATION_LEFT    = 1;
    public static final int ORIENTATION_RIGHT   = 2;
    public static final int ORIENTATION_TOP     = 3;
    public static final int ORIENTATION_BOTTOM  = 4; // 4 gives dublicated case label WTF?!
    
    public MyHelpLabelNoLine(JComponent c1, JComponent c2, String text, int position, int width){
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        
        switch(position) {
            
            case ORIENTATION_CENTER:
                setText("<html><table><tr>"
                        + "<td width='"+Integer.toString(width)+"'>"+text+"</td>"
                        + "</tr></table></html>");
                break;
            case ORIENTATION_LEFT:
                setText("<html><table><tr>"
                        + "<td width='50' valign='middle'></td>"
                        + "<td width='"+Integer.toString(width)+"'>"+text+"</td>"
                        + "</tr></table></html>");
                break;
            case ORIENTATION_RIGHT:
                setText("<html><table><tr>"
                        + "<td width='"+Integer.toString(width)+"' align='right'>"+text+"</td>"
                        + "<td width='50' valign='middle'></td>"
                        + "</tr></table></html>");
                break;
            case ORIENTATION_TOP:
                setText("<html><table><tr>"
                        + "<td width='"+Integer.toString(width)+"'>"+text+"</td>"
                        + "</tr><tr><td align='center'></td></tr></table></html>");
                break;
            case ORIENTATION_BOTTOM:
                setText("<html><table><tr><td align='center'></td></tr><tr>"
                        + "<td width='"+Integer.toString(width)+"'>"+text+"</td>"
                        + "</tr></table></html>");
                break;     
        }

        
        setSize(getPreferredSize()); //Gets the size adapted to the text
        Point pOnScreen = new Point();
    
        switch(position) {
            
            case ORIENTATION_CENTER:
                pOnScreen.setLocation(
                    c1.getLocationOnScreen().x + (c1.getWidth()-this.getWidth())/2,
                    c1.getLocationOnScreen().y + (c1.getHeight()-this.getHeight())/2);
                break;
            case ORIENTATION_LEFT:
                pOnScreen.setLocation(
                    c1.getLocationOnScreen().x + c1.getWidth(),
                    c1.getLocationOnScreen().y + (c1.getHeight()-this.getHeight())/2);
                break;
            case ORIENTATION_RIGHT:
                pOnScreen.setLocation(
                    c1.getLocationOnScreen().x - this.getWidth(),
                    c1.getLocationOnScreen().y + (c1.getHeight()-this.getHeight())/2); 
                break;
            case ORIENTATION_TOP:
                pOnScreen.setLocation(
                    c1.getLocationOnScreen().x + (c1.getWidth()-this.getWidth())/2,
                    c1.getLocationOnScreen().y - c1.getHeight());
                break;
            case ORIENTATION_BOTTOM:
                pOnScreen.setLocation(
                    c1.getLocationOnScreen().x + (c1.getWidth()-this.getWidth())/2,
                    c1.getLocationOnScreen().y + c1.getHeight());
                break;     
        }

        // Workaround for get the position of the component c1 in the space of c2
        SwingUtilities.convertPointFromScreen(pOnScreen, c2);
        setLocation(pOnScreen);
    }
}

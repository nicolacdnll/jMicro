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
package jmicro.gui.componentsUI;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 * Ad-hoc ComboBoxUI. 
 */
public class MyComboBoxUI extends BasicComboBoxUI {
    private final Color color_top     = Color.GRAY;
    private final Color color_bottom  = Color.WHITE;
    
  @Override
  protected JButton createArrowButton() {
      
    JButton button = new JButton() {{
        setBackground(new Color(238,238,238)); //The normal gray
        this.setBorder(BorderFactory.createLineBorder(new Color(0, 121, 131), 1));
        this.setIcon(new ImageIcon(getClass().getResource("/resources/gui/icons/icon-triangle.png")));
    }};
    return button;
  }
  
  @Override
    public void paint(Graphics g, JComponent c ) {
        // Prints also the backround which is up to the user to set!
        super.paint (g, c);
        
        Graphics2D g2d = ( Graphics2D ) g;
        g2d.setPaint ( new GradientPaint (0, 0, color_top, 0, c.getHeight(), color_bottom) );
        g2d.drawRect(0,0,c.getWidth()-1, c.getHeight()-1);//10,10
    }
}

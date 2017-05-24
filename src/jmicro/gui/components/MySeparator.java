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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Gray and dashed JSeparator.
 */
public class MySeparator extends JSeparator{
    public final static int thickness = 4;
    final static Color color = new Color (0xb0b1a7);
    final static float dash1[] = {
            1.0f,
            9.0f
            };
    final static BasicStroke dashed = new BasicStroke(
            thickness,                   //width 
            BasicStroke.CAP_SQUARE,
            BasicStroke.CAP_SQUARE,
            1.0f,                  
            dash1,                  //the dashing pattern
            0.0f);                  //the dashing phase 

    public MySeparator(int orientation) {
        this.setOrientation(orientation);
//        if ( this.getOrientation() == SwingConstants.VERTICAL )
//            this.setPreferredSize(new Dimension ((int)thickness, (int)dash1[1]));
//        else
//            this.setPreferredSize(new Dimension ((int)thickness, (int)dash1[1]));
        
    }
    
    @Override
    public void paintComponent(Graphics g){  
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(dashed);
        g2.setColor(color);
        if ( this.getOrientation() == SwingConstants.VERTICAL )
            g2.drawLine(1,0,1,this.getHeight());    
        else 
            g2.drawLine(0,1,this.getWidth(),1);
    }
}

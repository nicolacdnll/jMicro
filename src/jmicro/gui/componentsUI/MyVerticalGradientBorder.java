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
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Area;
import javax.swing.border.Border;

/**
 * Ad-hoc Border with vertical gradient. 
 */
public class MyVerticalGradientBorder implements Border {
    
    private Insets margin;

    private final Color color_top     = Color.GRAY;
    private final Color color_bottom  = Color.WHITE;
    /**
     *
     * @param top
     * @param left
     * @param bottom
     * @param right
     */
    public MyVerticalGradientBorder (int top, int left, int bottom, int right) {
        super();
        margin = new Insets ( top, left, bottom, right );        
    }

    public MyVerticalGradientBorder() {
        super();
        margin = new Insets ( 1, 1, 1, 1);      
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = ( Graphics2D ) g;
        g2d.setPaint ( new GradientPaint (0, 0, color_top, 0, height, color_bottom) );
        //g2d.setPaint ( new GradientPaint ( x, y, color_top, x + width, y, color_bottom) );
        Area border = new Area ( new Rectangle ( x, y, width, height ) );
        border.subtract ( new Area ( new Rectangle ( x + margin.left, y + margin.top,
                width - margin.left - margin.right, height - margin.top - margin.bottom ) ) );
        g2d.fill ( border );     
    }

    @Override
    public Insets getBorderInsets(Component cmpnt) {
        return margin;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
    
}

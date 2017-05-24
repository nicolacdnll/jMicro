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
package jmicro.gui.components.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * JPanel created to use a static image as background.
 */
public class JPanelWithBackground extends JPanel { 
    Image bg = null; 

    public JPanelWithBackground(String url) { 
        try{
            bg = ImageIO.read(getClass().getResourceAsStream(url));
            int height = bg.getHeight(null);
            int width  = bg.getWidth(null);
            Dimension d = new Dimension(width,height);
            this.setSize (width, height);
            this.setMinimumSize ( d );
            this.setOpaque(false); 
        } catch (IOException ex) {
            //TODO: extend support here
                System.err.println(ex);
                System.exit(1);
        }  
    } 

    @Override
    public void paintComponent(Graphics g) { 
        
        if (bg != null)
            g.drawImage(bg, 0, 0, this.getWidth(), this.getHeight(), null);
        super.paintComponent(g);
    }
} 
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import static jmicro.gui.components.MyButtonKind.BIG_RECTANGULAR;
import static jmicro.gui.components.MyButtonKind.BIG_SQUARED;
import static jmicro.gui.components.MyButtonKind.SMALL_RECTANGULAR;
import static jmicro.gui.components.MyButtonKind.SMALL_SQUARED;

/**
 * JToggleButton created using static images.
 */
public class MyToggleButton extends JToggleButton implements MouseListener{
    
    Icon icon_up;
    Icon icon_down;
    
    Image bg_up;
    Image bg_down;
    
    String url = "/resources/gui/buttons/";
    String url_up;
    String url_down;
    
 
    private int icon_x;
    private int icon_y;
    private boolean mouse_entered = false;
    
    /**
    * Creates a MyJToggleButton with a shape based on the text parameter.
    * @param text MyButtonKind value used for define the shape of the button.
    * @param icon Icon for the button
    */
    public MyToggleButton(String text, Icon icon) {    
        init(text, icon, null);
    }
    
    /**
    * Creates a MyJToggleButton with a shape based on the text parameter.
    * @param text MyButtonKind value used for define the shape of the button.
    * @param icon_up Icon for the button when is not toggled.
    * @param icon_down Icon for the button when is toggled.
    */
    public MyToggleButton(String text, Icon icon_up, Icon icon_down){
        init(text, icon_up, icon_down);
    }
    
    private void init(String text, Icon icon_up, Icon icon_down){
    
        MyButtonKind btn = MyButtonKind.valueOf(text);
        
        switch(btn) {
            case SMALL_SQUARED:
                url_up      = url.concat("btn-50x50.png");
                url_down    = url.concat("btn-50x50_down.png");
                break;
            case SMALL_RECTANGULAR:
                url_up      = url.concat("btn-65x50.png");
                url_down    = url.concat("btn-65x50_down.png");
                break;
            case BIG_RECTANGULAR:
                url_up      = url.concat("btn-135x50.png");
                url_down    = url.concat("btn-135x50_down.png");
                break;
            case BIG_SQUARED:
                url_up      = url.concat("btn-135x135.png");
                url_down    = url.concat("btn-135x135_down.png");
                break;
        }
        try{
            bg_up    = ImageIO.read(getClass().getResourceAsStream(url_up));
            bg_down  = ImageIO.read(getClass().getResourceAsStream(url_down));
            
            int height = bg_up.getHeight(this);
            int width  = bg_up.getWidth(this);
            
            this.setSize (width, height);
            this.setMinimumSize ( new Dimension (width, height) );
            
            // position use for paint the icon in the center of the button
            icon_x = (width  - icon_up.getIconWidth())/2;
            icon_y = (height - icon_up.getIconHeight())/2;
            
            this.setBorder(null);
            this.setOpaque(false);
            
            this.icon_up = icon_up;
            this.icon_down = icon_down;
            addMouseListener(this);
        } catch (IOException ex) {
                System.err.println(ex);
                System.exit(1);
        }
    }
    
    public void paintComponent(Graphics g)
    {  
        // super.paintComponent(g);
       
        // turns on anti-alias mode
        Graphics2D antiAlias = (Graphics2D)g;
        antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
        // draws the backround depending if the button is pressed or not
        if (isSelected()) {
            g.drawImage(bg_down, 0, 0, null);
            if(icon_down != null)
                icon_down.paintIcon(this, g, icon_x, icon_y);
            else
                icon_up.paintIcon(this, g, icon_x, icon_y);
        } else {
            g.drawImage(bg_up, 0, 0, null);
            icon_up.paintIcon(this, g, icon_x, icon_y);
        }
                
        // draw a semitraspart roundrect if disabled
        if(!isEnabled()){
            g.setColor(new Color(236, 234, 218, 0x80));
            g.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 5, 5);
        } else {        
            // draw borders if the mouse is over the button
            if(mouse_entered){
                 Graphics2D g2 = (Graphics2D) g;
                 g2.setStroke(new BasicStroke(2));
                 g2.setColor(new Color(0, 121, 131));        
                 g2.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 10, 10);
            }
        }
        
    }

    
    //...............................................Methods from MouseListenter
    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if (this.isEnabled()) {
            mouse_entered = true;
            repaint();
        }       
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (this.isEnabled()) {
            mouse_entered = false;
            repaint();
        }   
    }
    
    
    //................................................................Fix dimensions
        @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(this.getWidth(), this.getHeight());
    }
    
    @Override
    public Dimension getMinimumSize()
    {
        return this.getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize()
    {
        return this.getPreferredSize();
    }
}


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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import jmicro.Utils;

/**
 * Button class created using static images.
 */
public class MyButton extends JButton implements MouseListener{
    
    public static final int SMALL_SQUARED_WIDTH        = 50;
    public static final int SMALL_SQUARED_HEIGHT       = 50;  
    public static final int SMALL_RECTANGULAR_WIDTH    = 65;
    public static final int SMALL_RECTANGULAR_HEIGHT   = 50;   
    public static final int BIG_SQUARED_WIDTH          = 135;
    public static final int BIG_SQUARED_HEIGHT         = 50;  
    public static final int BIG_RECTANGULAR_WIDTH      = 135;
    public static final int BIG_RECTANGULAR_HEIGHT     = 135;       

    public static final int MARGINS                    = 12;
    public static final int SMALL_SQUARED_WIDTH_NOB        = SMALL_SQUARED_WIDTH-MARGINS;
    public static final int SMALL_SQUARED_HEIGHT_NOB       = SMALL_SQUARED_HEIGHT-MARGINS;  
    public static final int SMALL_RECTANGULAR_WIDTH_NOB    = SMALL_RECTANGULAR_WIDTH-MARGINS;
    public static final int SMALL_RECTANGULAR_HEIGHT_NOB   = SMALL_RECTANGULAR_HEIGHT-MARGINS;   
    public static final int BIG_SQUARED_WIDTH_NOB          = BIG_SQUARED_WIDTH -MARGINS;
    public static final int BIG_SQUARED_HEIGHT_NOB         = BIG_SQUARED_HEIGHT -MARGINS;  
    public static final int BIG_RECTANGULAR_WIDTH_NOB      = BIG_RECTANGULAR_WIDTH -MARGINS;
    public static final int BIG_RECTANGULAR_HEIGHT_NOB     = BIG_RECTANGULAR_HEIGHT -MARGINS; 
    
    Icon icon;
    
    Image bg_up;
    Image bg_down;
    
    String url = "/resources/gui/buttons/";
    String url_up;
    String url_down;
    
    private JLabel text;
  
    private int space_icon_text = 2;
    private int icon_x;
    private int icon_y;
    private boolean mouse_entered = false;
    private boolean mouse_pressed = false;
    
    /**
     * Creates a MyJButton with a shape based on the text parameter.
     * @param type
     * @param icon Icon for the button
     */
     
    public MyButton(String type) {
        this("", type, null);
    }
    
    public MyButton(String text, String type) {
        this(text, type, null);
    }
    
    public MyButton(String type, Icon icon) {  
        this("", type, icon);
    }  
    
    public MyButton(String text, String type, Icon icon) {   
        MyButtonKind btn = MyButtonKind.valueOf(type);  
        switch(btn) {
            case SMALL_SQUARED:
                url_up      = url.concat("btn-50x50.png");
                url_down    = url.concat("btn-50x50_down.png");
                break;
            case SMALL_RECTANGULAR:
                url_up      = url.concat("btn-65x50.png");
                url_down    = url.concat("btn-65x50_down.png");
                break;
            case BIG_SQUARED:
                url_up      = url.concat("btn-135x135.png");
                url_down    = url.concat("btn-135x135_down.png");
                break;
            case BIG_RECTANGULAR:
                url_up      = url.concat("btn-135x50.png");
                url_down    = url.concat("btn-135x50_down.png");
                break;
        }
        try{
            bg_up    = ImageIO.read(getClass().getResourceAsStream(url_up));
            bg_down  = ImageIO.read(getClass().getResourceAsStream(url_down));
            
            int height = bg_up.getHeight(this);
            int width  = bg_up.getWidth(this);
            
            this.setSize (width, height);
            this.setMinimumSize ( new Dimension (width, height) );           
            this.setBorder(null);
            this.setOpaque(false);
            if (icon != null) {
                this.setIcon(icon);

                icon_x = (width  - icon.getIconWidth())/2;
                icon_y = (height - icon.getIconHeight())/2;
            } else {
           
                icon_x = 0;
                icon_y = 0;
            }
            
            if (!text.equals("")){
                this.setText(text);
            }
            
            addMouseListener(this);
        } catch (IOException ex) {
                System.err.println(ex);
                System.exit(1);
        }
    }
    
    /**
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g)
    {
        // super.paintComponent(g);
       
        // turns on anti-alias mode
        Graphics2D antiAlias = (Graphics2D)g;
        antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
        // draws the backround depending if the button is pressed or not
        if (mouse_pressed) {
            g.drawImage(bg_down, 0, 0, null);
        } else {
            g.drawImage(bg_up, 0, 0, null);
        }
          
        if (this.getText().equals("")) {
            if(this.getIcon() != null)this.getIcon().paintIcon(this, g, icon_x, icon_y);   
        } else {
            FontMetrics metrics = g.getFontMetrics();
            // Center text vertically
            int hgt = metrics.getMaxDescent()+metrics.getMaxDescent();
            int text_y = (this.getSize().height + hgt)/2;
            
            // Center icon and text horizzantaly
            int text_width = metrics.stringWidth(this.getText());
            int width_content, icon_x = 0, text_x = 0;

            
            if (this.getIcon() == null) {
                width_content = text_width;
                text_x = (this.getSize().width - width_content)/2;
            } else {
                width_content = this.getIcon().getIconWidth() + space_icon_text + text_width;
                icon_x = (this.getSize().width - width_content)/2;
                text_x = icon_x + space_icon_text + this.getIcon().getIconWidth();
                this.getIcon().paintIcon(this, g, icon_x, icon_y); 
            } 

            g.drawString(this.getText(), text_x, text_y);
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
        if (this.isEnabled()) {
            mouse_pressed = true;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (this.isEnabled()) {
            mouse_pressed = false;
            repaint();
        }
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
    
    /**
     * This method is needed because if the click of the button disable it the events
     * mouseReleased and mouseExited are never fired and when it will be enabled again 
     * it will appear clicked and with the focus
    */
    @Override
    public void setEnabled(boolean b){
        super.setEnabled(b);
        if (!b) {
            mouse_pressed = mouse_entered = b;
            repaint();
        }
        
    }
}

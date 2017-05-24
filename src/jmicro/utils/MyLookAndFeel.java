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

package jmicro.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

/**
 *
*
 */
public class MyLookAndFeel {
    public static final int FS_WINDOWS_WIDTH  = 800;
    public static final int FS_WINDOWS_HEIGHT = 500;    
    public static final int WINDOWS_WIDTH  = 800;
    public static final int WINDOWS_HEIGHT = 600;
    
    public static final Color MYRED             = new Color(0xff5852);
    public static final Color MYGREEN           = new Color(0, 121, 131);
    public static final Color MYBLACK_SHADOW    = new Color(0, 0, 0, 0x80);
    /**
     * Sets a font default font.
     * @param f The FontUIResource to set as default font
     */
    public static void setUIFont(Font f)
    {   
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while(keys.hasMoreElements())
        {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if(value instanceof FontUIResource){
                //Main.print(key.toString());
                UIManager.put(key, f);
            }
        }
    }
    
    public static Font getMyFont (int size) {
        return new Font("Source Sans Pro Black",Font.PLAIN,size);
    }
    
    /**
     * Sets the System Look and Feel.
     * 
     * @param myLF
     */
    public static void setSystemLF(boolean myLF) {
        if(myLF) {                            
            //Fonts
            try {
                InputStream is;
                String url_font;                    
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

                url_font = "/resources/fonts/SourceSansPro-Black.ttf";
                is = MyLookAndFeel.class.getResourceAsStream(url_font);
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, is));                    
                setUIFont(new Font("Source Sans Pro Black",Font.PLAIN,15));
            } catch ( FontFormatException ex) {
                Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) { 
                Logger.getLogger(MyLookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } else {
            //Use the system's L&F
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(MyLookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(MyLookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(MyLookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

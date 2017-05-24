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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import jmicro.utils.Misc;


/**
 * Ad-hoc BasicSliderUI. 
 */
public class MySliderUI extends BasicSliderUI  {  
  
    private static BufferedImage thumb_enabled;
    private static BufferedImage thumb_disabled; 
    
    private static final float[] fractions = {0.0f, 0.5f};  

    private static final Color[] fillColors = {  
          new Color(0x66c0de) ,  
          new Color(0x45b8de)  
    };  

    private static final Color[] backColors = {  
          new Color(0, 0, 0, (float) 0.3),  
          new Color(0, 0, 0, (float) 0.1)  
    };

    private static final Paint vFillGradient = new LinearGradientPaint(0, 0, 11, 0,  
            fractions, fillColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);  
    private static final Paint vBackGradient = new LinearGradientPaint(0, 0, 11, 0,  
            fractions, backColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);  
    private static final Stroke roundEndStroke = new BasicStroke(15,  
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); 

    public MySliderUI(JSlider slider) {
        super(slider);
        try {
            thumb_enabled  = ImageIO.read(Misc.class.getResourceAsStream("/resources/gui/icons/icon-slider.png"));
            thumb_disabled = ImageIO.read(Misc.class.getResourceAsStream("/resources/gui/icons/icon-slider-disabled.png"));
        } catch (IOException ex) {
            Logger.getLogger(MySliderUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override  
    public Dimension getPreferredSize(JComponent c) {  
      Dimension d = super.getPreferredSize(c);  
      //TOCLEAN: check if really we need this!
      d.width  += 15;  
      d.height += 15;
      return d;  
    }  
  
    @Override  
    public void paintTrack(Graphics g) {  
        Graphics2D g2 = (Graphics2D) g;  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
        int cx = (trackRect.width / 2) - 2;  

        g.translate(trackRect.x + cx, trackRect.y);
        g2.setStroke(roundEndStroke);  
        g2.setPaint(vBackGradient);  
        g2.drawLine(2, 0, 2, thumbRect.y); 
        g2.setPaint(vFillGradient);
        g2.drawLine(2, thumbRect.y, 2, trackRect.height);  
        
        if ( !slider.isEnabled() ) {
            g2.setPaint(new Color(236, 234, 218, 0x80));
            g2.drawLine(2, thumbRect.y, 2, trackRect.height);  
        }
        g.translate(-(trackRect.x + cx), -trackRect.y);     
    }  
  
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (slider.isEnabled()) {
            g2.drawImage(thumb_enabled, thumbRect.x, thumbRect.y, null);
        } else {
            g2.drawImage(thumb_disabled, thumbRect.x, thumbRect.y, null);
        }          
    }
  
    @Override
    protected Dimension getThumbSize() {
        return new Dimension(thumb_enabled.getWidth(null), thumb_enabled.getHeight(null));
    }
}  
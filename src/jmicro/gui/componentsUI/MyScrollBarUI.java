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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Ad-hoc BasicProgressBarUI. 
 */
public class MyScrollBarUI extends BasicScrollBarUI {
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
    private static final Paint hFillGradient = new LinearGradientPaint(0, 0, 0, 11,  
            fractions, fillColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);  
    private static final Paint hBackGradient = new LinearGradientPaint(0, 0, 0, 11,  
            fractions, backColors, MultipleGradientPaint.CycleMethod.NO_CYCLE);   
    private static final Stroke roundEndStroke = new BasicStroke(15,  
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);  

    // Dirty workaround for "delete" the arrows button
    protected JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0,0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

  /**
   * TODO: here the printTrack is used also for print the Thumb which should instead be moved in its own method i.e., paintThumb
   * This method paints the track.
   *
   * @param g The Graphics object to paint with.
   * @param c The JComponent being painted.
   * @param trackBounds The track's bounds.
   */
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {
      Graphics2D g2 = (Graphics2D) g;  
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
      if (((JScrollBar)c).getOrientation() == JScrollBar.HORIZONTAL) {  
        int cy = (trackRect.height / 2) - 2;  
        g.translate(trackRect.x, trackRect.y + cy);  

        g2.setStroke(roundEndStroke);  
        g2.setPaint(hBackGradient);  
        g2.drawLine(10, 2, trackRect.width-10, 2);  
        g2.setPaint(hFillGradient);  
        g2.drawLine(10+thumbRect.x, 2, thumbRect.width+thumbRect.x-10, 2); 
        g.translate(-trackRect.x, -(trackRect.y + cy));  
      } else {  
        int cx = (trackRect.width / 2) - 2;  
        g.translate(trackRect.x + cx, trackRect.y);  
        g.translate(trackRect.x, trackRect.y);  

        g2.setStroke(roundEndStroke);  
        g2.setPaint(vBackGradient);  
        g2.drawLine(2, 10, 2, trackRect.height-10);  
        g2.setPaint(vFillGradient);  
        g2.drawLine(2,10+thumbRect.y, 2, thumbRect.height+thumbRect.y-10); 
        g.translate(-(trackRect.x + cx), -trackRect.y);
      }   
    } 
  
    @Override
    public void paint(Graphics g, JComponent c)
    {
      layoutContainer(scrollbar);
      paintTrack(g, c, getTrackBounds());
      // Due to the same operations in paintTrack and paint Thumb I moved 
      // the latter inside the former
      //paintThumb(g, c, getThumbBounds());

      if (trackHighlight == INCREASE_HIGHLIGHT)
        paintIncreaseHighlight(g);
      else if (trackHighlight == DECREASE_HIGHLIGHT)
        paintDecreaseHighlight(g);
    }
}

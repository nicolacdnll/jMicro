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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * Ad-hoc BasicProgressBarUI. 
 */
public class MyProgressBarUI extends BasicProgressBarUI {
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
    
    private int numFrames = 200;

    public enum AnimationDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    private AnimationDirection direction = AnimationDirection.RIGHT_TO_LEFT;

    public MyProgressBarUI (){
        startAnimationTimer();
    }

    /**
     * We create an image containing a gradient from dark to white and back to dark.
     * We tile this pattern multiple times across the length of the progress bar.
     * By redrawing this image multiple times across the length of the bar (and
     * at different offsets each frame), we achieve the illusion of motion.
     */
    private BufferedImage barImage = createRippleImage(fillColors[0], fillColors[1]);

    // Create an image with alternating light and dark patterns
    protected BufferedImage createRippleImage(Color darkColor, Color lightColor) {
        int width = 60;
        int height = 60;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
        
        g2.setPaint(darkColor);
        g2.fillRect(0, 0, width, height);

        g2.setPaint(lightColor);
        g2.fillPolygon(new int[]{0,width/2,width,width/2},  new int[] {height,height,0,0}, 4);
        return image;
    }


    /**
     * Used by an internal timer to increment the state of the animation.
     *
     * This code is copied from the original implementation, but with our own
     * numFrames variable (the standard numFrames is private in ProgressBarUI)
     */
    @Override
    protected void incrementAnimationIndex() {
        int newValue = getAnimationIndex() + 1;
        if (newValue < numFrames) {
            setAnimationIndex(newValue);
        } else {
            setAnimationIndex(0);
        }
    }


    /**
     * This method is called when the progress bar is in determinate mode (e.g.
     * the progress bar is reflecting 50% completion) and the bar needs to be
     * redrawn.
     *
     * In order to achieve an effect of movement, we take the gradient image
     * we created (@see barImage), and tile it across the length of the filled
     * in area of the progress bar.  At each frame, this method is called, and the
     * currentFrameIndex variable is changed.  We use this value to move the
     * center of the tiled images to the right or to the left depending on which
     * direction or animation is moving.
     *
     *
     * @param g the graphics context onto which to draw the determinate progress bar
     * @param c the component
     */
    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        // We are only going to deal with horizontal painting
        if (progressBar.getOrientation() != JProgressBar.HORIZONTAL) {
            super.paintDeterminate(g, c);
            return;
        }

        /*
         Copied from the BasicProgressBar code - calculates the actual dimensions of
         the progress bar area, discounting the insets etc
        */
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth    = progressBar.getWidth()     - (b.right + b.left);
        int barRectHeight   = progressBar.getHeight()    - (b.top + b.bottom);

        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }

        // amount of progress to draw; measured in pixels
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);


        // Make sure we only draw in the region of the progress bar.  This allows
        // us to be sloppy with our drawing (which is impossible to avoid when
        // dealing with the drawImage commands) and yet still avoid bad artifacts
        g.setClip(b.left, b.top, amountFull, barRectHeight);


        // Here we calculate a pixel offset by which to shift all of our tiled images.
        // If we're moving right to left, then we offset by a decreasing amount each
        // tick.  If we're moving left to right, we do the opposite.
        int offset = 0;
        if (direction == AnimationDirection.RIGHT_TO_LEFT) {
            offset = (int) (map(getAnimationIndex(), 0, numFrames, barImage.getWidth(), 0));
        }
        else {
            offset = (int) (map(getAnimationIndex(), 0, numFrames, 0, barImage.getWidth()));
        }

        // How many repetitions of the image need to be drawn to ensure that
        // a full progress bar has no gaps in the image?
        int numRepetitions = progressBar.getWidth() / barImage.getWidth();
        // ensure both sides have full coverage just to be safe
        numRepetitions += 2;

        for (int i = 0; i < numRepetitions; i++) {
            // The first image we want drawn to the left, even offscreen if
            // necessary.
            int xOffset = (i - 1) * barImage.getWidth() + offset;
            g.drawImage(barImage, xOffset, 0, null);
        }
    }
    
    /**
     * Map a value in one range to a value in a different range. See
     * <a href="http://developmentality.wordpress.com/2009/12/15/useful-utility-functions-0-of-n/">a blog post</a>
     * I wrote about the subject.
     * @param value The incoming value to be converted
     * @param low1  Lower bound of the value's current range
     * @param high1 Upper bound of the value's current range
     * @param low2  Lower bound of the value's target range
     * @param high2 Upper bound of the value's target range
     */
    public static double map(double value, double low1, double high1, double low2, double high2) {

        double diff = value - low1;
        double proportion = diff / (high1 - low1);

        return lerp(low2, high2, proportion);
    }

    /** Linearly interpolate between two values */
    public static double lerp(double value1, double value2, double amt) {
        return ((value2 - value1) * amt) + value1;
    }
}

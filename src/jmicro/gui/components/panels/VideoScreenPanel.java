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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import jmicro.vlcjutils.MyCanvas;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

   
/**
 * JPanel created to contain the video stream from devices. 
 */
public class VideoScreenPanel extends JPanel implements Observer{

    private BufferedImage img;
    private MyCanvas canvas = null;
    
    private static final int vCanvasMargin = 0;
    private static final int hCanvasMargin = 0;    
       
    
        /**
     * Default constructor. Sets the layout manager to JPanel's
     * default layout manager: FlowLayout.
     */
    public VideoScreenPanel () {
        this(new FlowLayout());
    }

    /**
     * Construct panel with input layout manager.
     *
     * @param mgr The layout manager for the extended JPanel.
     */
    public VideoScreenPanel (LayoutManager mgr) {

        // set layout manager, if any
        super(mgr);

//        // set opaque false so that an isOpaque() == false
//        super.setOpaque(false);
    }

//    /**
//     * Override setOpaque so that the user cannot change the 
//     * opacity of this panel, since it is after all suppose to 
//     * be transparent.
//     *
//     * @param isOpaque Is this panel opaque?
//     */
//    @Override
//    public void setOpaque(boolean isOpaque) {
//        // do not allow this to become opaque because it is
//        // transparent after all
//    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this.getSize().width, this.getSize().height, null);
    }

    /*---------------------------------------------+
    |       Observer pattern Implementation        |
    +---------------------------------------------*/
    /**
     * Methods used from the device to notify that a new frame is captured
     * @param o
     * @param arg The new from, must be casted to BufferedImage
     * @see BufferedImage
     */
    @Override
    public void update(Observable o, Object arg) {
        img = (BufferedImage) arg;
        if (canvas == null) {
            repaint();
        }        
    }

    /**
     * Records itself like observer of the device
     * @param toObserve
     */
    public void startObserve(Observable toObserve) {
        toObserve.addObserver(this);
    }

    /**
     * Remove itself from the device observers
     * @param toStop
     */
    public void stopObserve(Observable toStop) {
        toStop.deleteObserver(this);
    } 
    
    /**
     * Create a canvas and plays the file on it. 
     * @param file The file to be played. 
     * @param mpea 
     */
    public void playCanvas(String file,  MediaPlayerEventAdapter mpea){
        if (canvas != null) {
            this.stopCanvas();
        } 

        canvas = new MyCanvas(mpea);
        canvas.setBackground(Color.BLACK);
        add(canvas);
        canvas.setBounds(   hCanvasMargin,              vCanvasMargin, 
                            getWidth()-hCanvasMargin*2, getHeight()-vCanvasMargin*2);
        canvas.playMedia(file);
    }
    
    public boolean isPlaying() {
        if (canvas == null) return false;
        return canvas.isPlaying();
    }
    
    public void restartCanvas() {
        if (canvas != null) canvas.restartMedia();
    }
    
    public void pauseCanvas() {
        if (canvas != null) canvas.pauseMedia();
    }
    
    public void stopCanvas() {
        if (canvas != null) canvas.stopMedia();
    }
    
    public void freeCanvas() {
        if (canvas != null) {
            remove(canvas);
            canvas.stopMedia();
            canvas = null;
        }    
    }
    
}

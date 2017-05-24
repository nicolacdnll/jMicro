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

package jmicro.gui.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import jmicro.utils.Misc;
import jmicro.utils.MyIO;



/**
 * Class used for create the of the thumbnail preview in the View.
 * This render create a jLabel with ImageIcon but without text.
 * The image is centered on the JLabel, scaled and has a double border,
 * each border has the dimension of 1 pixel; the internal border is grey and the external black.
 * 
*
 */
public class ThumbnailsListRenderer extends DefaultListCellRenderer {
    
    private final int THUMBNAILS_WIDTH  = 165;
    private final int THUMBNAILS_HEIGHT = 135;
    private final int THUMBNAILS_BORDER = 8;
    private final Color backgroundColor = new Color(0, 121, 131);
    
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
        this.setPreferredSize(new Dimension(THUMBNAILS_WIDTH, THUMBNAILS_HEIGHT));
        
        // for default cell renderer behavior
        Component c = super.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus);
        
        // Creates the image of the thumbnails
        
        ImageIcon icon = getThumbnail(value.toString());

        if (icon != null) { //In case there is no image, or something went wrong
            ((JLabel) c).setIcon(icon);
            ((JLabel) c).setText("");
            ((JLabel) c).setToolTipText(new File((String) value).getName());
        } else {
            setUhOhText("No image available!", list.getFont());
        }
        ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel) c).setVerticalAlignment(JLabel.CENTER);
        
        if (isSelected) {
            c.setBackground(backgroundColor);
        }
        return c;
    }
    
    /**
     * Creates the thumbnails from the filename.
     * In case the file file is a video, the first image taken for create the video is used for the thumbnail.
     * @param filename The complete filename of the file.
     * @return The image.
     */
    private ImageIcon getThumbnail (String filename) {
        ImageIcon imageIcon;
        boolean isImage = true;
        
        // Checks if the file is a video
        if (filename.endsWith("mp4")) {
            // Gets the video directory
            String videoDir = Misc.getVideoDirectory(filename);
            // Gets the firs image on the directory
            filename = MyIO.allImageInToDir(videoDir)[0];
            isImage = false; 
            
        }
        
        // Gets the imageIcon of the image 
        imageIcon = jmicro.utils.MyIO.readImageIcon(filename);
        
        // Creates a buffered image of the resized image
        BufferedImage bi = 
                Misc.resize(
                (BufferedImage) imageIcon.getImage(),
                this.THUMBNAILS_WIDTH-THUMBNAILS_BORDER*2,
                this.THUMBNAILS_HEIGHT-THUMBNAILS_BORDER*2);
        
        if (!isImage) {
            // Otherwise use the video decoration
            ImageIcon decoration = new ImageIcon(getClass().getResource("/resources/gui/icons/icon-video.png"));

            // Gets the graphics of the buffered image
            Graphics2D g2 = bi.createGraphics();
            // Overdraw the thumbnail decoration on the image.
            // NOTE: The decoration is not resized to the dimension of the 
            // thumbnail; thus, for have a good match the decoration image
            // should be of the same size of the thumbnail minus double of the border!.
            g2.drawImage(decoration.getImage(), 0, 0, null);
            
            // Releases the graphics
            g2.dispose();  
        }        
        
        return new ImageIcon((Image)bi);
    }
    
    /**
     * Set the font and text when no image is found.
     * @param uhOhText
     * @param normalFont 
     */
    private void setUhOhText(String uhOhText, Font normalFont) {
        setFont(normalFont.deriveFont(Font.ITALIC));
        setText(uhOhText);
    }

}

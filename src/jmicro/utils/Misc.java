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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import jmicro.Globals;
import jmicro.Utils;


/**
 * Utility class
 * 
 * Nicola Cadenelli
 */
public class Misc {

    public static String MYUTILS_RESOURCE_IMAGES = ".resources.gui.icons.";
    /**
     * Gets the user's home's directory
     * @return The user's home's directory
     */
    public static String getHomeDir() {
        return System.getProperty("user.home");
    }
    
    /**
     * Gets the desktop user directory
     * The directory abs path is stored on the /home/user/.config/user-dirs.dirs
     * file and is the value of XDG_DESKTOP_DIR.
     * @return The abs path of the desktop directory if the value is found in the XDG_DESKTOP_DIR,
     * otherwise (if the file doesn't exists or there isn't a line that mach with the XDG_DESKTOP_DIR pattern)
     * the abs path of the user's home directory
     */
    public static String getDesktopDir() {
        //Concats the user home with "/.config/user-dirs.dirs"
        String userDir = getHomeDir().concat("/.config/user-dirs.dirs");
        String str = MyIO.parseFromFile(userDir, "XDG_DESKTOP_DIR");

        if (str != null) {
            //replaces all " characters with no char
            str = str.replaceAll("\"", "");
            //splits and use only the second string (es: /Desktop or /Scrivania)
            String[] splitted = str.split("HOME");
            //concat the System.getProperty("user.home") with the desktop folder.
            str = getHomeDir().concat(splitted[1]);
            return str;
        } else {
            //if the file don't exists or doesn't cointains a row with "XDG_DESKTOP_DIR"
            //return the user home directory
            return getHomeDir();
        }
    }

    public static String getJarPath(){
        String path = Misc.class.getProtectionDomain().getCodeSource().getLocation().getPath();
     
        try {
            path = URLDecoder.decode(path, "UTF-8");           
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return path;
    }
    
    public static String getJarDir(){
        String path = Misc.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File f = new File(path);
        String s = f.getAbsoluteFile().getParentFile().getAbsolutePath();
        return s;
    }

    /**
     * Gets the last substring of the str String splitted by regex
     * @param str The split
     * @param regex The rexeg
     * @return A string with the last substring
     */
    public static String getLastPart(String str, String regex) {
        //Splits the string
        String[] parts = str.split(regex);
        //returns the last parts
        return parts[parts.length - 1];
    }


    /**
     * Returns a string containing the time; the format of the time is defined by the pattern string.
     * @param pattern String that rappresents the pattern of the time
     * @return String that contains the time
     */
    public static String getTime(String pattern) {
        //     sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     *  Executes the commad contained in the cmdArray
     * @param cmdArray Array that contains the cmd and all the cmd's parameters
     * @return True if the command is executed correctly, false otherwise
     */
    public static boolean execCmd(String[] cmdArray, Boolean printOutput, Boolean lock) {
        boolean executed = false;
        InputStream in = null;
        int readInt;
        try {
            if (printOutput || lock) {
                //
                StringBuilder commandResult = new StringBuilder();
                Process p = Runtime.getRuntime().exec(cmdArray);
                int returnVal = p.waitFor();
                if (returnVal == 0) {
                    executed = true;
                    in = p.getInputStream();
                } else {
                    in = p.getErrorStream();
                }
                while ((readInt = in.read()) != -1) {
                    commandResult.append((char) readInt);
                }

                if (printOutput){
                System.out.println("Command output = \n" + commandResult.toString());
                System.out.println("Command exit status = " + returnVal);
            }
                in.close(); //closes the inputStream DON'T REMOVE
            } else {
                Runtime.getRuntime().exec(cmdArray);
                return true;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            return false;
        }
        return executed;
    }

        /**
     *  Executes the commad contained in the cmdArray
     * @param cmdArray Array that contains the cmd and all the cmd's parameters
     * @return True if the command is executed correctly, false otherwise
     */
    public static boolean execCmd(String[] cmdArray, boolean errors) {
        boolean executed = false;
        try {

            Runtime.getRuntime().exec(cmdArray);
            executed = true;
        } catch (IOException ex) {
            if (errors) Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return executed;
    }

    // TODO move debug var in another place
     private static final boolean _VIDEO_DEBUG = true;
     
    /**
     * Creates a video timelaps using jCodec.
     * Commented there is the code to use ffmpeg, JpegToMovie and other ways
     * @param videoName The name of the video
     * @param interval The time between two photo in the video
     * @param imagesSource The pattern of the imagesSource
     * @param sourcesPath
     * @param imagesNumber
     * @return True if the video is created, false otherwise.
     */
    public static boolean createVideo (String videoName, int interval, String imagesSource, String sourcesPath /*NOT USED*/, int imagesNumber) {
        try {
            if (_VIDEO_DEBUG) {
                Utils.print("### Misc.createVideo()");
                Utils.print("Video Name: " + videoName);
                Utils.print("Img sources: "+imagesSource);
                Utils.print("Sources path: "+sourcesPath);
                
            }
            
            PngImagesToMovieJCodec encoder = new PngImagesToMovieJCodec(new File(videoName), Globals.LAPS_IN_VIDEO);

            int i;
            for (i = 1; i <= imagesNumber; i++) {
                encoder.encodeImage(new File(String.format(imagesSource,i)));
            }
            /* 
            * Doubles the last image because the library at today (Feb 3 2015) 
            * creates a video where the last frame is missing. - Nicola
            */
            encoder.encodeImage(new File(String.format(imagesSource,--i)));
            encoder.finish();
        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
        
// USING PngImagesToMovie or JpegImagesToMovie
//            try {
//                List<String> lof = MyIO.allFileInToDir(sourcesPath);
//                              
//                PngImagesToMovie imageToMovie = new PngImagesToMovie();
//                MediaLocator oml;
//                if ((oml = PngImagesToMovie.createMediaLocator("file:"+videoName)) == null) {
//                    System.err.println("Cannot build media locator from: " + videoName);
//                }
//                // A reverse is needed to have a crescent order
//                Collections.reverse(lof);
//                // Use cif format 352 × 288
//                return imageToMovie.doIt(640, 480, (1000 / interval), new Vector<String>(lof), oml);
//            } catch (MalformedURLException ex) {
//                Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
//                return false;
//            }
////            return true;

// USING ffmpeg            
//            String r = String.valueOf((float) 1 / (float) interval);
//
//            //inits the cmdarray with all parameters
//            String[] cmdArray = {
//                "ffmpeg",
//                "-r",
//                r, //frame for second
//                "-i",
//                imagesSource, //pattern from images file name
//                "-s",
//                "cif", //request cif format
//                "-r",
//                "20",
//                "-y", //enable overwriting
//                videoName //output file
//            };
//
//            if (_VIDEO_DEBUG) {
//                for (String cmdArray1 : cmdArray) {
//                    System.out.println(cmdArray1);
//                }
//            }
//            //executes the cmdArray
//            return execCmd(cmdArray, _VIDEO_DEBUG, true);
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage and
     * insert a double grey/black external border.
     * @param icon
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized and bordered image
     */
    public static Image getScaledImageWithBorder(ImageIcon icon, int w, int h) {
        int borderWidth = 1;

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        //Draw image starting from (2,2)
 
        g2.drawImage(icon.getImage(), 2 * borderWidth, 2 * borderWidth, w - 4*borderWidth, h - 4*borderWidth, null);
        //set first border
        g2.setColor(Color.GRAY);
        g2.drawRect(borderWidth, borderWidth, w - 1 - 2*borderWidth, h - 1 - 2*borderWidth);
        g2.dispose();
        return resizedImg;
    }

    /**
     * Creates an Image to use as thumbnail image.
     * The image has a different border if the thumbnail is for a video or for an image
     * @param icon The ImageIcon of the thumbnail
     * @param photo Define if the thumbnail decoration is for an image or for a video
     * @param width
     * @param height
     * @return The Image of the thumbnail
     */
    public static Image getThumbnailsImage(ImageIcon icon, boolean photo, int width, int height){

        //Substitutes all the dots with the File.separator character
        String decorationFileName = MyIO.replaceAllWithFileSeparator(MYUTILS_RESOURCE_IMAGES, "\\.");

        //Creates a buffered image of the resized icon
        BufferedImage bi = resize((BufferedImage) icon.getImage(), width, height);
        //gets the graphics of the buffered image
        Graphics2D g2 = bi.createGraphics();
        
        if (photo) {
            //if true use the photo tumbnail
            //decorationFileName = decorationFileName.concat("thumbnail_photo.png");
        } else {
            //otherwise use the video tumbnail
            decorationFileName = decorationFileName.concat("icon-video.png");
        }

        if (!photo) { //Seems that for now we will have a thumbnail just for videos
            //Overdraw the thumbnail decoration on the image
            g2.drawImage( getImageIconFromResource(decorationFileName).getImage(), 0, 0, null);
        }
        //Release the graphics
        g2.dispose();
        //return the BufferedImage casted at Image
        return (Image) bi;
    }

    /**
     * Returns an ImageIcon of the imagePath resource.
     * @param imagePath The path of the ImageIcon's file.
     * @return The ImageIcon of the imagePath resource, null otherwise.
     */
    public static ImageIcon getImageIconFromResource(String imagePath) {

        //gets the URL of the resource
        URL imgURL = Misc.class.getResource(imagePath);

        //checks if the urls of the resouce isn't null
        if (imgURL != null) {
            //return the ImageIcon of the resource
            return new ImageIcon(imgURL);
        } else {
            //prints an error
            System.err.println("### myutils.Misc.getImageIconFromResource()");
            System.err.println("Couldn't find file: " + imagePath);
            return null;
        }
    }

    /**
     * Resizes a BufferedImage
     * @param img   BufferedImage to be resized
     * @param newW  The width of the image resized
     * @param newH  The height of the image resized
     * @return  The image resized
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        BufferedImage buffy = config.createCompatibleImage(newW, newH, Transparency.TRANSLUCENT);
        Graphics2D g = buffy.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        
        g.dispose();
        return buffy;
    }

    /**
     * Opens a file with the default application of the system
     * @param filePath File path to be open
     * @return True if the file has been opened successfully, false otherwise
     */
    public static boolean openFileWhitDesktopApplication(String filePath) {
        try {
            Desktop.getDesktop().open(MyIO.openFile(filePath));
        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Composes the directory where are stored the images of the video.
     * @param videoPath The path of the video file
     * @return The path of the directory
     */
    public static String getVideoDirectory(String videoPath) {
        //gets the videoName
        String videoName = getLastPart(videoPath, File.separator);
        String videoDir = videoPath.replace(File.separator + videoName, "");
        //removes the extension from the video name
        videoName = videoName.replace(".mp4", "");
        //composes the path of the directory that contains all the image of the video
        return jmicro.utils.MyIO.composePath(videoDir, videoName);
    }

    /**
     * Imports the files in tux paint and open tux paint with the last image imported.
     * @param filesName The fileName of the images to import.
     * @return False if something goes wrong, true otherwise.
     */
    public static boolean openFilesWhitTuxpaint(String[] filesName){
        try {
            int readInt;
            InputStream in = null;
            StringBuilder commandResult = new StringBuilder();
            String[] cmdArrayTuxpaintImport = new String[filesName.length + 1];
            cmdArrayTuxpaintImport[0] = "tuxpaint-import";
            System.arraycopy(filesName, 0, cmdArrayTuxpaintImport, 1, filesName.length);
            Process p;
            int returnVal = 1;
            //Executes the first import
            p = Runtime.getRuntime().exec(new String[]{"tuxpaint-import", filesName[0]});
            returnVal = p.waitFor();
            if (returnVal != 0) {
                return false;
            }

            for (int i = 1; i < filesName.length; i++) {
                    //waiting 1 seconds prevents that the imported files overwrite each others
                    Thread.sleep(1000);
                    p = Runtime.getRuntime().exec(new String[]{"tuxpaint-import", filesName[i]});
                    returnVal = p.waitFor();
                    if (returnVal != 0) {
                        return false;
                    }
            }
            in = p.getInputStream();
            while ((readInt = in.read()) != -1) {
                commandResult.append((char) readInt);
            }

            System.out.print(commandResult.toString());
            String importedFileName = getLastPart(commandResult.toString(), "->");
            importedFileName = getLastPart(importedFileName, File.separator);
            importedFileName = importedFileName.replace(".png", "");

            Writer output = null;
            String fileName = getHomeDir().concat(File.separator+".tuxpaint"+File.separator+"current_id.txt");
            System.out.println("txpref " + fileName);

            output = new BufferedWriter(new FileWriter(new File(fileName)));
            output.write(importedFileName);
            output.close();

            execCmd(new String[]{"tuxpaint"}, true);

        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    return true;
    }
    
    /**
     * Tries to open files with gimp.
     * @param filesName The fileName of the images to import.
     * @return False if something goes wrong, true otherwise.
     */
    public static boolean openFileWhitGimp(String file){
        return execCmd(new String[]{"gimp", file}, true);
    }

    /**
     * Checks if Tux Paint is installed.
     * @return Tue if installed, false otherwise.
     */
    public static boolean isTuxPaintInstalled() {
        String[] cmdArray = new String[]{"tuxpaint", "-v"};
        return execCmd(cmdArray, false);
    }
    
    /**
     * Checks if GIMP is installed.
     * @return Tue if installed, false otherwise.
     */
    public static boolean isGimpInstalled() {
        String[] cmdArray = new String[]{"gimp", "-v"};
        return execCmd(cmdArray, false);
    }
    
    /**
     * Checks if VLC is installed.
     * @return Tue if installed, false otherwise.
     */
    public static boolean isVLCInstalled() {
        String[] cmdArray = new String[]{"vlc", "--version"};
        return execCmd(cmdArray, false);
    }
    
    /**
     * Checks if Ffmpeg is installed.
     * @return Tue if installed, false otherwise.
     */
    public static boolean isFfmpegInstalled() {
        String[] cmdArray = new String[]{"ffmpeg", "-version"};
        return execCmd(cmdArray, false);
    }

    //THE FOLLOWING 3 methods must be moved, why here?
    /**
     * Gets the color of the system theme for a graphic element.
     * @param element The graphic element.
     * @return The color of the element.
     */
    static public Color getColor(String element){
        //"Tree.selectionBackground"
        return UIManager.getColor(element);
    }
    
    static public String wrapString(String text, int width){
        return String.format("<html><div WIDTH:%d style=\"text-align:center;\">%s</div><html>", width, text);
//        return String.format("<html><p>%s</p><html>", text);

    }
    
    static public String wrapString(String text){
        return String.format("<html><p>%s</p><html>", text);
    }
    
    static public String wrapStringCentered(String text){
        return String.format("<html><div style=\"text-align:center;\">%s</div><html>", text);
    }
    
    static public String convertBytesToText(float b) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        float k = 1024;
        float m = 1024*k;
        float g = 1024*m;
        
        String      s = df.format(b)  +" Bytes";
        if (b > k)  s = df.format(b/k)+" KBytes";
        if (b > m)  s = df.format(b/m)+" MBytes";
        if (b > g)  s = df.format(b/g)+" GBytes";
        
        return s;
    }
    
    static public Dimension getScreenDimension () {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
}

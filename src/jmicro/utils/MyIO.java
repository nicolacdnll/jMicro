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

import jmicro.utils.filters.DirFilter;
import jmicro.utils.filters.ImageFileFilter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import jmicro.utils.filters.ImageAndVideoFileFilter;

/**
 * Utility class for the I/O
 * Nicola Cadenelli
 */
public class MyIO {

    /**
     * Illegal characters in a cross-platform environment 
     */
    private static final char[] ILLEGAL_CHARACTERS = { '/', '\\', '?', '*', '<', '>', '|', '\"', ':', '.' };

    private static final boolean _DIR_DEBUG = false;

    /**
     * Checks if a filename is legal or contain special characters
     * @param filename The filename to check
     * @return True if the file name is legal, false otherwise -- contains illegal chars --
     */
    static public boolean isLegalFileName (String filename){
        for (char c : MyIO.ILLEGAL_CHARACTERS ) {
            if ( 0 <= filename.indexOf(c) ) {
                // An illegal char is contained
                return false;
            }
        }       
        return true;    
    }
    
    
    /**
     * Deletes a directory and all his subdirectory recursively.
     * @param dirName Name of the directory.
     * @return  True if no errors, false otherwise.
     */
    static public boolean deleteDirectory(String dirName) {
        File path = new File(dirName);
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Deletes a file.
     * @param fileName The file name of the file that will be deleted
     * @return true if the file has been removed, false otherwise
     */
    public static boolean deleteFile(String fileName) {

        //Opens the file
        File f = new File(fileName);
        
        //Make sure the file or directory exists and isn't write protected
        if (!f.exists() || !f.canWrite() || f.isDirectory() ) {
            return false;
        }
        
        // Attempt to delete it
        boolean success = f.delete();
        if (!success) {
            throw new IllegalArgumentException("Delete: deletion failed");
        }
        return success;
    }

//    /**
//     * Creates a directory
//     * @param newDirName The directory name that will be created
//     * @return true if the directory has been created, false otherwise
//     */
//    public static boolean createDir(String newDirName) {
//        boolean created = false;
//        //opens the directory
//        File dir = new File(newDirName);
//        //controls if the directory already exists
//        if (dir.exists()) {
//            //if exist print an error dialog
//            System.out.println("Warning: the directory " + newDirName + " already exists");
//            created = false;
//        } else {
//            //try to create the file of the new directory
//            created = dir.mkdir();
//        }
//        return created;
//    }

    /**
     * Makes sure that a directory exists.
     * If doesn't exist the method creates it.
     * @param newDirName The directory name that will be created
     * @return true if the directory has been created, false otherwise
     */
    public static boolean makeSureExistDir(String newDirName) {
        boolean created = true;
        //opens the directory
        File dir = new File(newDirName);
        //controls if the directory already exists
        if (dir.exists()) {
            //if exist print an error dialog
            System.out.println("Warning: the directory " + newDirName + " already exists");
        } else {
            //try to create the file of the new directory
            created = dir.mkdir();
        }
        return created;
    }

    /**
     * Checks if there are the permission to write on a file.
     * @param fileName  File to check.
     * @return True if there are the permission, false otherwise.
     */
    public static boolean canWrite(String fileName) {
        //opens the file
        File f = new File(fileName);
        //controls if the application has the permission to write
        if (f.canWrite()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Opens the file
     * @param filePath
     * @return The file
     */
    public static File openFile(String filePath) {
        return new File(filePath);
    }

    /**
     * Checks if the fileName file exist.
     * @param fileName  File to check.
     * @return True if the file exist, false otherwise.
     */
    public static boolean exists(String fileName) {
        return (new File(fileName)).exists();
    }

    /**
     * Checks if there are the permission to read on a file.
     * @param fileName  File to check.
     * @return True if there are the permission, false otherwise.
     */
    public static boolean canRead(String fileName) {
        return (new File(fileName)).canRead();
    }

    /**
     * Return all the absolute path of the existing files in the dirName directory.
     * @param dirName The name of the directory.
     * @return A list with all absolute path of the files in the dirName.
     */
    public static List<String> allFileInToDir(String dirName) {
        File dir = new File(dirName);
        List<String> files = new ArrayList<String>();
        File[] imagesFiles = dir.listFiles();
        for (File f : imagesFiles) {
            files.add(f.getAbsolutePath());
        }
        return files;
    }

    /**
     * Return all the absolute path of the existing files, that match with the filter,
     * in the dirName directory.
     * @param dirName The name of the directory.
     * @param filter The filter that the file has to match
     * @return A list with all absolute path of the files that has mached
     */
    public static String[] allFileInToDir(String dirName, FileFilter filter) {
        File dir = new File(dirName);
        File[] files = dir.listFiles(filter);
        if (files == null) {
            //TODO MANAGE EXCEPTION HERE! It's null if the directory does not exist
            return null;
        } else {
            String[] filesName = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                filesName[i] = files[i].getAbsolutePath();
            }
            return filesName;
        }
    }

    /**
     * All images file, that match with the ImageFileFilter, in the dirName directory.
     * @param dirName The name of the directory.
     * @return A list with all absolute path of the images that has mached
     */
    public static String[] allImageInToDir(String dirName) {
        return allFileInToDir(dirName, new ImageFileFilter());
    }

    public static String[] allImageAndVideoInToDir(String dirName) {
        return allFileInToDir(dirName, new ImageAndVideoFileFilter());
    }

    /**
     * Return the absolute path of the first image in the dirName.
     * @param dirName The name of the directory to search the image.
     * @return The absolute path of the first image, or (if there isn't any image) null.
     */
    public static String getFirstImageInToDIr(String dirName) {
        //gets the absolute path of all images in the dirName
        String[] imagesFileName = allFileInToDir(dirName, new ImageFileFilter());
        if (imagesFileName.length == 0) {
            return null;
        } else {
            //returns only the first image
            return imagesFileName[0];
        }
    }

    /**
     * Return the ImageIcon of the fileName. Gives problem with jpg images
     * @param fileName FileName to read
     * @return The ImageIcon in the fileName, null if something goes wrong
     */
    public static ImageIcon readImageIcon (String fileName) {
//        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
//            return new ImageIcon(fileName);
//        } else {
            try {
                File f = new File(fileName);
                if (f.exists()) {
                    return new ImageIcon(ImageIO.read(f));
                } else {
                    return null;
                }
            } catch (IOException ex) {
                Logger.getLogger(MyIO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
//        }
    }

    /**
     * Returns all the path of the directories from the given currentDir up the root directory
     * @param currentDir The name of the currentDir
     * @param reverse
     * @return An array of String that contains all hight level directories name
     */
    public static String[] getUpDirs(String currentDir, boolean reverse) {
        String[] upDirsName = currentDir.split(File.separator);
        for (int c = 1; c < upDirsName.length; c++) {
            upDirsName[c] = upDirsName[c - 1].concat(File.separator.concat(upDirsName[c]));
            if (_DIR_DEBUG) {
                System.out.println(upDirsName[c]);
            }
        }
        //check if upDirsName has at least 1 element
        if (upDirsName.length == 0) {
            //if the root directory is reach, the currentDir.split(File.separator)
            //return null
            upDirsName = new String[1];
        }
        //sets as the first element the file separator
        upDirsName[0] = File.separator;
        if (reverse) {
            //if reverse is null reverts the array
            for (int c = 0; c < upDirsName.length / 2; c++) {
                String temp = upDirsName[c];
                upDirsName[c] = upDirsName[upDirsName.length - 1 - c];
                upDirsName[upDirsName.length - 1 - c] = temp;
            }
        }
        return upDirsName;
    }

    /**
     * Return the abslute path of all subdirectory of the curretDirName directory.
     * @param currentDirName
     * @return An array of String that contains the abslute path of all subdirectory.
     */
    public static String[] getSubDirs(String currentDirName) {
        File currentDir = new File(currentDirName);
        File[] subDirs = currentDir.listFiles(new DirFilter());
        String[] subDirsName = new String[subDirs.length];
        //for each subDir
        for (int i = 0; i < subDirsName.length; i++) {
            subDirsName[i] = subDirs[i].getAbsolutePath();
        }
        java.util.Arrays.sort(subDirsName);
        return subDirsName;
    }

    /**
     * Composes the path linking fist with second using the File.separator as separator
     * @param first
     * @param second
     * @return The path composed
     */
    public static String composePath(String first, String second) {
        return first.concat(File.separator.concat(second));
    }

    /**
     * Composes the path linking all the elements of paths array using the File.separator as separator
     * @param paths
     * @return The path composed
     */
    public static String composePath(String[] paths) {
        String path = paths[0];// = File.separator;
        
        for (int i = 1; i < paths.length; i++) {
            path = path.concat(File.separator);
            path = path.concat(paths[i]);

        }
        return path;
    }
    


    /**
     * Check if exist in the absFilePath file a line that contains the regex.
     * @param absFilePath File to check.
     * @param regex Expression to find.
     * @return A string that contains all the line that has mached with the regex, null if any line has mached.
     */
    public static String parseFromFile(String absFilePath, String regex) {
        String str = null;
        //opens the file
        File f = new File(absFilePath);
        //checks if the file exists
        if (f.exists()) {
            try {

                BufferedReader in = new BufferedReader(new FileReader(f));
                //reads the file row by row untils the row that contains "XDG_DESKTOP_DIR"
                while ((str = in.readLine()) != null) {
                    if (str.contains(regex)) {
                        break;
                    }
                }
                in.close();
            } catch (IOException e) {
            }
        }
        return str;
    }

    /**
     * Replaces all the recourences of regex, in the str String, with the File.separator character
     * @param str String to elaborate
     * @param regex Expression to replace
     * @return The str String with all the regex recourences replaced with the File.separator.
     */
    public static String replaceAllWithFileSeparator(String str, String regex) {
        return str.replaceAll(regex, File.separator);
    }

    /**
     * Gets a BufferedImage of the file
     * @param fileName The image file.
     * @return The image
     * @see BufferedImage
     */
    public static BufferedImage readBufferedImage(String fileName) {
        try {
            File f = new File(fileName);
            if (f.exists()) {
                return ImageIO.read(f);
            } else {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(MyIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*********************************+
     * Methods with GUI
     */
    
    /*
    * Create a thumbnail of the fileName
    */
    private static final int THUMBNAILS_WIDTH = 136;
    private static final int THUMBNAILS_HEIGHT = 111;
    
    public static ImageIcon getThumbnail(String fileName) {
        ImageIcon imageIcon;
        boolean isImage = true;
        
        //checks if the file is a video
        if (fileName.endsWith("mp4")) {
            //gets the video directory
            String videoDir = Misc.getVideoDirectory(fileName);
            //gets the firs image on the directory
            fileName = allImageInToDir(videoDir)[0];
            isImage = false;
        }
        //gets the imageIcon of the firs image
        imageIcon = jmicro.utils.MyIO.readImageIcon(fileName);
        return new ImageIcon(Misc.getThumbnailsImage(imageIcon, isImage, THUMBNAILS_WIDTH, THUMBNAILS_HEIGHT));
    }
    


    
    /**
     * Writes an image on disk.
     * @param image     Image to write
     * @param ext       Extension of the image
     * @param fileName  Absolute file path
     */
    public static void writeImage(BufferedImage image, String ext, String fileName) {
        try {
            File outputFile = new File(fileName);
            ImageIO.write(image, ext, outputFile);
        } catch (IOException ex) {
            Logger.getLogger(MyIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

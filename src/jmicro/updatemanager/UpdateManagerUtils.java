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

package jmicro.updatemanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmicro.updatemanager.UpdateManagerUpdateDialog.UpdateTask;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Utils class for the update manager.
 */
public class UpdateManagerUtils {

    /**
     * Saves a URl into a local file using file streams.
     * @param filename  The path of the local file
     * @param url       The URL of the resource to download.
     * @return True if the file was downloaded correctly, false otherwise.
     */
    protected static boolean dowloadFile (final String url, final String filename) {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }

        } catch (FileNotFoundException ex) {
            //Logger.getLogger(UpdateManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            //Logger.getLogger(UpdateManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }
    
        /**
     * Saves a URl into a local file using file streams.
     * @param filename  The path of the local file
     * @param bytes
     * @param url       The URL of the resource to download.
     * @param worker
     * @throws java.io.IOException
     */
    protected static void dowloadFileThrowing (final String url, final long bytes, final String filename, final UpdateTask worker) throws MalformedURLException, FileNotFoundException, IOException {
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fout  = new FileOutputStream(filename);

        final byte data[] = new byte[1024];
        int     count;
        long    sum = 0;
        int     percent;
        while ((count = in.read(data, 0, 1024)) != -1) {
            fout.write(data, 0, count);
            sum         += count;
            percent     = (int)(100*sum/bytes);
            worker.exposedSetProgress(percent);
            worker.setDownloadOutput(sum);
        }

        in.close();
        fout.close();
    }
    
    static public boolean uncompressBZ2(String in, String out){
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(in);
            BufferedInputStream _in = new BufferedInputStream(fin);
            FileOutputStream _out = new FileOutputStream(out);
            BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(_in);
            final byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = bzIn.read(buffer))) {
                _out.write(buffer, 0, n);
            }   _out.close();
            bzIn.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UpdateManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(UpdateManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                if (fin != null) fin.close();
            } catch (IOException ex) {
                Logger.getLogger(UpdateManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }
    
    static public void uncompressBZ2Throwing(String in, final long bytes, String out, final UpdateTask worker) throws FileNotFoundException, IOException{
        FileInputStream fin  = new FileInputStream(in);
        BufferedInputStream _in = new BufferedInputStream(fin);
        
        FileOutputStream _out = new FileOutputStream(out);
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(_in);
        
        final byte[] buffer = new byte[1024];
        int n = 0;
        int sum = 0;
        int percent;
         
        while (-1 != (n = bzIn.read(buffer))) {
            _out.write(buffer, 0, n);
            sum         += n;
            percent     = (int)(100*sum/bytes);
            worker.exposedSetProgress(percent);
        }
        _out.close();
        bzIn.close();

        fin.close();
    }
    
    /** Untar an input file into an output file.

    * The output file is created in the output folder, having the same name
    * as the input file, minus the '.tar' extension. 
    * 
     * @param in
     * @param out
    *  
    * @return  The {@link List} of {@link File}s with the untared content. 
    */
   public static boolean uncompressTAR(final String in, final String out) {
        try {
            final InputStream isd = new FileInputStream(new File(in));
            final InputStream isf = new FileInputStream(new File(in));
            final TarArchiveInputStream debInputStreamDirectories   = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", isd);
            final TarArchiveInputStream debInputStreamFiles         = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", isf);

            TarArchiveEntry entry = null;
            
            // First pass to extract and create directories
            while ((entry = (TarArchiveEntry)debInputStreamDirectories.getNextTarEntry()) != null) {
                final File outputFile = new File(new File(out), entry.getName());
                if ( entry.isDirectory() ) {
//                    Utils.print("Extracting directory "+entry.getName());
//                    Attempting to write output directory outputFile.getAbsolutePath()
                    if (!outputFile.exists()) {
                        // Attempting to create output directory outputFile.getAbsolutePath()
                        if (!outputFile.mkdirs()) {
                            throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                        }
                    }
                } // Do nothing for files now
            }
            
            // Second pass to extract files
            while ((entry = (TarArchiveEntry)debInputStreamFiles.getNextTarEntry()) != null) {
                final File outputFile = new File(new File(out), entry.getName());
                if ( /*NOT*/!entry.isDirectory() ) {
//                    Utils.print("Extracting file "+entry.getName());
                    // "Creating output file %s.", outputFile.getAbsolutePath()
                    final OutputStream outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(debInputStreamFiles, outputFileStream);
                    outputFileStream.close();
                } // Do nothing for directories now
            }
            
            // Closing
            isd.close();
            isf.close();
            debInputStreamDirectories.close();
            debInputStreamFiles.close();
            
        } catch (IOException ex) {
            Logger.getLogger(UpdateManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ArchiveException ex) {
            Logger.getLogger(UpdateManagerUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
   }
   
    public static boolean uncompressTARThrowing(final String in, final long bytes, final String out, final UpdateTask worker) throws FileNotFoundException, ArchiveException, IOException {

        final InputStream isd = new FileInputStream(new File(in));
        final InputStream isf = new FileInputStream(new File(in));
        final TarArchiveInputStream debInputStreamDirectories   = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", isd);
        final TarArchiveInputStream debInputStreamFiles         = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", isf);

        TarArchiveEntry entry = null;

        // First pass to extract and create directories
        while ((entry = (TarArchiveEntry)debInputStreamDirectories.getNextTarEntry()) != null) {
            final File outputFile = new File(new File(out), entry.getName());
            
            if ( entry.isDirectory() ) {
                // Attempting to write output directory outputFile.getAbsolutePath()
                if (!outputFile.exists()) {
                    // Attempting to create output directory outputFile.getAbsolutePath()
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } // Do nothing for files now
        }
        int sum = 0;
        int percent = 0;
        // Second pass to extract files
        while ((entry = (TarArchiveEntry)debInputStreamFiles.getNextTarEntry()) != null) {
            final File outputFile = new File(new File(out), entry.getName());
            
            if ( /*NOT*/!entry.isDirectory() ) {
                worker.setExtractOutput(entry.getName());
                
                sum         += entry.getSize();
                percent     = (int)(100*sum/bytes);
                worker.exposedSetProgress(percent);
                
                // "Creating output file %s.", outputFile.getAbsolutePath()
                final OutputStream outputFileStream = new FileOutputStream(outputFile);
                IOUtils.copy(debInputStreamFiles, outputFileStream);
                outputFileStream.close();
            } // Do nothing for directories now
        }

        // Closing
        isd.close();
        isf.close();
        debInputStreamDirectories.close();
        debInputStreamFiles.close();

        return true;
    }
}

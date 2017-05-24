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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import jmicro.Globals;
import jmicro.SettingsManager;
import jmicro.Utils;
import jmicro.gui.components.MyButton;
import jmicro.gui.components.MyButtonKind;
import jmicro.gui.componentsUI.MyProgressBarUI;
import jmicro.utils.Misc;
import jmicro.utils.MyIO;
import jmicro.utils.MyLookAndFeel;
import org.apache.commons.compress.archivers.ArchiveException;

/**
 * Dialog used to show available updates at the users.
 */
public class UpdateManagerUpdateDialog extends JDialog 
                                       implements ActionListener,
                                                  PropertyChangeListener{
    
    ResourceBundle rb;
    private String dimension = Misc.convertBytesToText(Long.parseLong(UpdateManager.getDimension()));
        
    private JProgressBar    progressBar;
    private JButton         okButton;
    private JLabel          taskOutput;
    private UpdateTask      task;
    
    static final int BORDER_TOP    = 20;
    static final int BORDER_LEFT   = 20;
    static final int BORDER_BOTTOM = 20;
    static final int BORDER_RIGHT  = 20;
    
    public UpdateManagerUpdateDialog (JFrame owner, ResourceBundle rb, boolean modal) {
        super(owner, rb.getString("DlgUpdateTitle"), modal);

        this.rb = rb;
        
        // Create the demo's UI.
        okButton = new MyButton(MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-yes.png")));
        okButton.addActionListener(this);
        okButton.setEnabled(false);
        
        progressBar = new JProgressBar(0, 100) {{
            setValue(0);
            setUI(new MyProgressBarUI());
        }};
        
        taskOutput = new JLabel(rb.getString("LblStarting"));
        
        JPanel inner = new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(
                    BORDER_TOP,
                    BORDER_LEFT,
                    BORDER_BOTTOM,
                    BORDER_RIGHT)
                );
            setPreferredSize(new Dimension(600,150));

        }};
        
        GroupLayout l = new GroupLayout(inner);
        l.setAutoCreateGaps(true);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(taskOutput)
                .addComponent(progressBar, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton)
        );
        l.setVerticalGroup(
            l.createSequentialGroup()
                .addComponent(taskOutput)
                .addComponent(progressBar)
                .addComponent(okButton)
        );
        inner.setLayout(l);
  
        add(inner);

                
        // Starts the task
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new UpdateTask();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    //.................................................................Listeners
    /**
     * Invoked when the user presses the yes button.
     * @param evt
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        this.dispose();
    }


    /**
     * Invoked when task's progress property changes.
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }

    public void showError(String msg){
        progressBar.setValue(0);
        setTitle(rb.getString("DlgErrorTitle")); //DOESN't work 

        taskOutput.setForeground(MyLookAndFeel.MYRED);
        taskOutput.setText(Misc.wrapString(msg));
    }
    
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     * @param owner
     * @param modal
     */
    public static void createAndShowGUI(JFrame owner, boolean modal) {
        // Creates and set up the dialog.
        String languageCode = SettingsManager.getLanguageCode();

        ResourceBundle rb   = ResourceBundle.getBundle(
                Globals.LANGUAGE_UPDATE, 
                new Locale(languageCode)
        );
                Utils.print(rb.getString("LblStarting") );
        Dialog d = new UpdateManagerUpdateDialog(owner, rb, modal);
        
        // Shows it
        d.setLocationRelativeTo(null); //centers it
        d.pack();
        d.setVisible(true);
    }

    
    //...............................................................UPDATE TASK
    /**
     * Tasks that download, extract and install the new version
     */
    class UpdateTask extends SwingWorker<Void, Void> {         
        
        // Exposes the method in public
        //NOTE: this should not be the way a better implementation with listener 
        //should substitute this. For now, for question of time it is fine
        public void exposedSetProgress(int p){
            super.setProgress(Math.min(p, 99));
        }
        
        public void setExtractOutput(String file){
            taskOutput.setText(rb.getString("LblExtracting")+file);
        }
        

        public void setDownloadOutput(long s){
            
            taskOutput.setText(String.format("%s %s %s", Misc.convertBytesToText(s), rb.getString("LblDowloadedOf"), dimension));
        }
        
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            String temp_file        = null;
            String temp_file_tar    = null;
            try {               
                // Downloads program
                Utils.print("Downloading "+UpdateManager.getRemoteURL());
                temp_file = UpdateManager.getTempFileName();
                Utils.print("in "+temp_file);
                
                UpdateManagerUtils.dowloadFileThrowing(
                        UpdateManager.getRemoteURL(),
                        Long.parseLong(UpdateManager.getDimension()),
                        temp_file,
                        this);
                setProgress(0);

                taskOutput.setText(rb.getString("LblExtractingArch"));
                
                // Extract it
                temp_file_tar = temp_file.replace(".bz2", "");
                //Utils.print(temp_file_tar);
                
                // From tar.bz2 to tar
                UpdateManagerUtils.uncompressBZ2Throwing(
                        temp_file,
                        Long.parseLong(UpdateManager.getDimension()),
                        temp_file_tar,
                        this);
                setProgress(0);
                
                // Extract tar
                String path;
                if (Globals.DEVELOPING) path = Misc.getDesktopDir();
                else                    path = Misc.getJarDir();
                Utils.print("Extracting in "+path);
                UpdateManagerUtils.uncompressTARThrowing(
                        temp_file_tar,
                        Long.parseLong(UpdateManager.getDimension()),
                        path,
                        this);
                setProgress(100);
                taskOutput.setText(rb.getString("LblUpdated"));
            } catch (FileNotFoundException ex) {
                showError(rb.getString("ErrMsgFileNotFoundException"));
            } catch (IOException ex) {
                showError(rb.getString("ErrMsgIOException"));
            } catch (ArchiveException ex) {
                showError(rb.getString("ErrMsgArchiveException"));
            } finally {
                // Cleaning temp files
//                taskOutput.setText(rb.getString("LblCleaning"));
                if (temp_file != null)      MyIO.deleteFile(temp_file);
                if (temp_file_tar != null)  MyIO.deleteFile(temp_file_tar);
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {            
            // Updates the view
//            taskOutput.setText(rb.getString("LblUpdated"));
            okButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
        }
    }
}

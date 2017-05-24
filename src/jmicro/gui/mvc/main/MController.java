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

package jmicro.gui.mvc.main;

import java.awt.Desktop;
import java.awt.KeyEventDispatcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jmicro.Globals;
import jmicro.Main;
import jmicro.MyException;
import jmicro.SettingsManager;
import jmicro.Utils;
import jmicro.gui.components.AboutDialog;


import jmicro.timertask.TimeLapsTimerTask;

import jmicro.updatemanager.UpdateManager;
import jmicro.updatemanager.UpdateManagerUpdateDialog;
import jmicro.utils.Misc;
import jmicro.utils.MyIO;
import jmicro.v4l4jutils.Device;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * Controller used in the Main MVC.
 */
public class MController {
    //... The MController interacts with both the MModel and MView.
    private final MView  view;
    private final MModel model;
    
    private Timer timer;
    private String videoName;
    private String videoDir;
    /**
     * Creates a Controller whose interacts wit the given MModel and MView.
     * In this MVC architecture the view doesn't know the model. 
     * It's the controller that creates and queries the model.
     * @param m
     * @param v 
     */
    public MController(MModel m, MView v) {
        view  = v;
        model = m;
        
        //Add Listeners
        view.addSettingListener         (new SettingButtonListener());
        view.addBackListener            (new BackButtonListener());
        view.addHelpListener            (new HelpButtonListener());
        view.addExitListener            (new ExitButtonListener());
       
        view.getCardProject().addStartListener               (new SelectProjectListener());
        view.getCardProject().addUpLevelDirectoryListener    (new UpDirectoryListener());
        view.getCardProject().addDirectoriesComboItemListerner   (new DirectoryComboBoxModelListener ());
        view.getCardProject().addDirectoriesListListeners    (new DirectoriesListMouseListener (), new  DirectoriesSelectionListener());
        view.getCardProject().addHomeDirectoryListener       (new HomeDirectoryListener());
        view.getCardProject().addDesktopDirectoryListener    (new DesktopDirectoryListener());
        view.getCardProject().addWorkspaceDirectoryListener  (new WorkspaceDirectoryListener());
        view.getCardProject().addNewProjectListener          (new NewProjectListener());
        view.getCardProject().addNewProjectTextFieldDocumentListener(new NewProjectTextFieldDocumentListener());
        view.getCardProject().setNavigationDirectory(SettingsManager.getWorkspacePath());
        
        view.getCardMain().addLampTopListener         (new LampTopListener());
        view.getCardMain().addLampBottomListener      (new LampBottomListener());
        view.getCardMain().addNormalModeListener      (new NormalModeButtonListener());
        view.getCardMain().addTimelapsModeListener    (new TimelapsModeButtonListener());
        view.getCardMain().addPlayVideoButtonListener (new PlayVideoButtonListener());        
        view.getCardMain().addPauseVideoButtonListener (new PauseVideoButtonListener());
        view.getCardMain().addDeleteItemListener      (new DeleteItemListener());
        
        view.getCardMain().addTakePicListener         (new TakePicListener());
        view.getCardMain().addStartTimeLapsListener   (new StartTimeLapsListener());
        view.getCardMain().addStopTimeLapsListener    (new StopTimeLapsListener());
        view.getCardMain().addBackListener            (new BackListener());
        view.getCardMain().addPaintListener           (new PaintListener());    
        view.getCardMain().addBrighnessListener       (new BrightnessListener());
        view.getCardMain().addThumbnailsListeners     (new ThumbnailsSelectionListener());
        
        view.getCardSettings().addLanguageListener    (new languageCBoxModelListener());
        view.getCardSettings().addUserModeListener    (new usermodeCBoxModelListener());
        view.getCardSettings().addDeviceListener      (new deviceCBoxModelListener());
        view.getCardSettings().addProjectListener     (new goToProjectSelectionListener());
        view.getCardSettings().addCheckUpdatesListener(new CheckUpdatesListener());
        view.getCardSettings().addAboutListener       (new AboutButtonListener());
        
        view.addKeyEventDispatcher      (new MyKeyEventDispatcher());       
        view.addWindowListener          (new WindowExitListener());

        view.adaptToUser    (model.getUserMode(), model.getMode());
        view.adaptToLanguage(model.getResourceBundle());
        view.adaptToMode    (model.getMode(), model.getUserMode(), false, false);

        checkUpdates();
    }
    
    private void checkUpdates(){
        if (SettingsManager.getCheckUpdate()) {
            // Make sure that it was passible to fetch information about the last version from the Internet
            if (UpdateManager.getLastVersion() != null) 
                if ( !/*NOT*/UpdateManager.isLastVersion(Globals.VERSION) ) {
                    // Shows message
                    view.setMessageVisible(
                    "/resources/gui/icons/icon-warning.png",
                    model.getResourceBundle().getString("MSGNewVersion"),
                    UpdateManager.getLongMessage(model.getLanguageCode()),
                    //UpdateManager.getChangeLog(model.getLanguageCode()),
                    new String [] {"",""},
                    new String [] {
                    "/resources/gui/icons/icon-yes.png",
                    "/resources/gui/icons/icon-no.png"
                    },
                    new ActionListener [] {
                    new StartUpdatesListener(), // Start update
                    new HideMessageListener()   // Abort update
                    });
                }
        }
    }
      
   
    
    /**
     * Method that set the device to the model and adapts the view to the new 
     * device
     * @param device The new device in use
     */
    public void setDevice(Device device) {
        if ( null != device) {
            model.setDevice(device);
            view.adaptToDevice(model.getDevice());
        }
    }

    public void play() {
        view.startObserve(model.getDevice());
    }
    
    public void stop() {
        view.stopObserve(model.getDevice());
    }
    
    public void releaseDevice(){
        model.getDevice().releaseDevice();
    }
   
    public void fromProjectSelectionToMain(String path) {
            model.setProjectPath(path);
            
            // Updates the model
            model.setMode(MModel.NORMAL);
            model.updateProjectFiles();
            
            // Updates the view
            view.getCardMain().updateThumbnailsList(model.getProjectFiles());
            view.adaptToDevice(model.getDevice());
            view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen());             
            
            // Start capturing and play
            model.getDevice().startCapture();
            play();
  
    }
    
    public void showAbout(){
        AboutDialog.getInstance().setVisible(true);
    }
    
    public void showSettings(){
        stop();
        model.setMode(MModel.SETTINGS);
        view.adaptToMode(MModel.SETTINGS, model.getUserMode(), model.isTimerActive(), model.isFrozen());
        
        view.getCardSettings().setValues(
                model.getUserMode(),
                model.getDevice(),
                model.getProjectPath(),
                model.isTimerActive(),
                model.getCheckUpdate(),
                model.getResourceBundle());
    }
    //.........................................................TopPanelListeners

    /**
     * Shows the card with settings.
     */
    class SettingButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            showSettings();
        }  
    }
    
    class AboutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            showAbout();
        }  
    }
        
    /**
     * Shows the main card with the microscope
     */
    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            int m = model.getModeBeforeSetting();
            if (m != MModel.PROJECT) {
                model.getDevice().startCapture();
                play();
            }
            model.setMode(m);
            view.adaptToMode(m, model.getUserMode(), model.isTimerActive(), model.isFrozen());
        }  
    }
        
    /**
     * Shows an overlay panel with a gradient and the explanations of the components.
     */
    class HelpButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.addGlassPanelListener (new GlassPanelMouseListener());
            view.setHelpVisible (true, model.getResourceBundle(), model.getMode()); 
        }  
    }
    
    /**
     * Closes the program.
     */
    class ExitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            exitMVC();
        }  
    }
    
    class WindowExitListener extends WindowAdapter {
            @Override
            public void windowClosing(WindowEvent e)
            {
                exitMVC();
            }
    }
    
    
    public void exitMVC(){   
        // Exports preference from model into the system
        SettingsManager.setUserMode(model.getUserMode());
        SettingsManager.setCheckUpdate(model.getCheckUpdate());
        SettingsManager.setLanguageCode(model.getLanguageCode());
        
        if (model.getProjectPath() != null) SettingsManager.setProjectAndWorkspacePaths(model.getProjectPath());
        
        releaseDevice();
        Main.myfinalize();
    }
    
    //........................................................middlePanelProject 
    class SelectProjectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            fromProjectSelectionToMain(view.getCardProject().getDirectoryListSelectedItem());
        }
    }      
    
    class NewProjectListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            // Create new directory
            String path = MyIO.composePath( view.getCardProject().getDirectoryComboBoxSelectedItem(), 
                                            view.getCardProject().getNewProjectText());
            MyIO.makeSureExistDir(path);
            
            // Update the list
            view.getCardProject().updateNavigationDirectoryList();
            
            // Select element
            if ( view.getCardProject().selectItemNavigationDirectoryList(path) == false)
                Utils.print("Error: Something went wrong creating a new project!");
            
            // Delete textfield text
            view.getCardProject().deleteNewProjectTextField();
        }
    }    
    
    /**
     * Every time the textfield to insert the new project name changes this 
     * listener checks if the name already exists or if special characters are used.
     */
    class NewProjectTextFieldDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            changed(de);
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            changed(de);
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            changed(de);
        }
        
        void changed(DocumentEvent de) {
            checkNewProjectTextField();
        }
    }
    
    /**
     * Checks if the the New Project Text Field is legal or if the directory already exists
     */
    protected void checkNewProjectTextField (){
        String s = view.getCardProject().getNewProjectText();

        if (s.isEmpty()) {
            // Disable Create Button but show no error
            view.getCardProject().setEnabledCreateNewProjectButton(false);
            view.getCardProject().setProjectCreationError("");
            view.getCardProject().SetProjectCreationBorderNormal();
        } else {
            boolean enable_btn;
            String  error_message;
            String path = MyIO.composePath(view.getCardProject().getDirectoryComboBoxSelectedItem(), s);
            if ( !MyIO.isLegalFileName(s)) {
                enable_btn    = false;
                error_message = model.getResourceBundle().getString("LblErrorSpecialChars");
            } else if (MyIO.exists(path)) {
                enable_btn    = false;
                error_message = model.getResourceBundle().getString("LblErrorInvalidName");
            } else {
                enable_btn    = true;
                error_message = "";
            }
            view.getCardProject().setProjectCreationError(error_message);
            view.getCardProject().setEnabledCreateNewProjectButton(enable_btn);
            if (enable_btn) {
                view.getCardProject().SetProjectCreationBorderNormal();
            } else {
                // If the btn isn't enable some error occured
                view.getCardProject().SetProjectCreationBorderError();
            }
        }
    }
  
    class UpDirectoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.getCardProject().setPreviousNavigationItem ();
        }
    } 
    
    class HomeDirectoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.getCardProject().setNavigationDirectory(Misc.getHomeDir());
        }
    } 
    
    class DesktopDirectoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.getCardProject().setNavigationDirectory(Misc.getDesktopDir());
        }
    } 
        
    class WorkspaceDirectoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.getCardProject().setNavigationDirectory(SettingsManager.getWorkspacePath());
        }
    } 
            
    class DirectoryComboBoxModelListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent ie) {
            // This event is fired twice, one for the deselected element and one for the selected one
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                String path = ie.getItem().toString();

                // Updates list and select the first element
                view.getCardProject().setDirectoriesListItems (MyIO.getSubDirs(path));

                // Check if user has write permession and disable/enable components
                boolean hasPermission = MyIO.canRead(path) && MyIO.canWrite(path);    

                // Force check for special char and same name
                if (hasPermission) {
                    view.getCardProject().setEnableProjectCreationComponents(true);
                    checkNewProjectTextField(); // Must be after setEnableProjectCreationComponents
                } else {
                    view.getCardProject().setProjectCreationError(model.getResourceBundle().getString("LblErrorNoRights")); 
                    view.getCardProject().setEnableProjectCreationComponents(false);
                }
            }
        }
    
    }
    
    class DirectoriesSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent lse) {    
            JList theList = (JList)lse.getSource();
            int index = theList.getSelectedIndex();           
            if (index != -1) {
                String path = theList.getModel().getElementAt(index).toString();
                // check if user has write permession and disable/enable components
                boolean hasPermission = MyIO.canRead(path) && MyIO.canWrite(path);
                view.getCardProject().setEnabledStartButton(hasPermission);
            }
        }        
    }
    
    class DirectoriesListMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent me) {
            String selected = view.getCardProject().getDirectoryListSelectedItem();
            if (me.getClickCount() == 2 && selected != null){
                view.getCardProject().setNavigationDirectory(selected);
            }
        }

        @Override
        public void mousePressed(MouseEvent me) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void mouseExited(MouseEvent me) {
         //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    //........................................................leftPanelListeners
    /**
     * When LampBottomJToggle is pressed.
     * If toggled
     * then turn on the light
     * else turn off the light
     */
    class LampBottomListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            model.getDevice().changeStateBottomLight();
        }
    }
    
    /**
     * When LampTopJToggle is pressed.
     * If toggled then turn on the light, otherwise turn off the light.
     */
    class LampTopListener implements ActionListener {

        @Override
        public void actionPerformed (ActionEvent ae) {
            model.getDevice().changeStateTopLight();
        }
    }
    
    /**
     * Switches to normal mode.
     * 1. Update the model
     * 2. Adapt the view
     */
    class NormalModeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            model.setMode(MModel.NORMAL);
            view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen());
        }  
    }
    
    /**
     * Switches to preview mode.
     * 1. Update the model
     * 2. Adapt the view
     */
    class TimelapsModeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            model.setMode(MModel.TIMELAPS);
            view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen());
            view.setFocusOnInputIntervel();
        }  
    }

    /**
     * 
     */
    class PlayVideoButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {   
            // Check if VLC is installed
            if (!Misc.isVLCInstalled()) {
                // If not show allert
                view.setMessageVisible("/resources/gui/icons/icon-warning.png",
                model.getResourceBundle().getString("MSGNOVLC"),
                null,
                new String [] {
                    ""},
                new String [] {
                    "/resources/gui/icons/icon-yes.png",
                },
                new ActionListener [] {
                    new HideMessageListener()               // Aborts the time laps
                });
            } else {
                // if not installed then play video
                int status = model.getMode();
                view.adaptToMode(MModel.PREVIEW_VIDEO_PLAY, model.getUserMode(), model.isTimerActive(), model.isFrozen());
                model.setMode(MModel.PREVIEW_VIDEO_PLAY);

                if (status == MModel.PREVIEW_VIDEO)
                   view.playVideo(model.getPreviewFile(), new MediaPlayerListener());
                else if (status == MModel.PREVIEW_VIDEO_PAUSE)
                   view.restartVideo();
                else 
                   Utils.print("Action undefined");
            }
        }
    }
    
    class PauseVideoButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {            
            view.adaptToMode(MModel.PREVIEW_VIDEO_PAUSE, model.getUserMode(), model.isTimerActive(), model.isFrozen());
            model.setMode(MModel.PREVIEW_VIDEO_PAUSE);
            view.pauseVideo();
        }   
    }
    
//    class PlayPauseVideoButtonListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent ae) {
//            switch (model.getMode()) {
//                case MModel.PREVIEW_VIDEO:
//                    view.playVideo(model.getPreviewFile(), new MediaPlayerListener());
//                    model.setMode(MModel.PREVIEW_VIDEO_PLAY);  
//                    view.adaptToMode(MModel.PREVIEW_VIDEO_PLAY, model.isTimerActive());
//                    break;
//                case MModel.PREVIEW_VIDEO_PLAY:
//                    // Dirty workaround because I'm not able to know when the video is over
//                    if (view.isPlayingVideo()) {
//                  
//                        view.pauseVideo();
//                        model.setMode(MModel.PREVIEW_VIDEO_PAUSE);  ;  
//                        view.adaptToMode(MModel.PREVIEW_VIDEO_PAUSE, model.isTimerActive());                   
//                    } else {
//                        Utils.print("Not playing");
//                        view.playVideo(model.getPreviewFile(),null);
//                        // The following aren't needed
////                        model.setMode(MModel.PREVIEW_VIDEO_PLAY);  
////                        view.adaptToMode(MModel.PREVIEW_VIDEO_PLAY, model.isTimerActive());
//                    }
//                    break;
//                case MModel.PREVIEW_VIDEO_PAUSE:
//                    view.restartVideo();
//                    model.setMode(MModel.PREVIEW_VIDEO_PLAY); 
//                    view.adaptToMode(MModel.PREVIEW_VIDEO_PLAY, model.isTimerActive());
//                    break;
//            }
//        }
//    }
//        
//    class StopVideoButtonListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent ae) {
//            view.stopVideo();
//            model.setMode(MModel.PREVIEW_VIDEO);
//            view.adaptToMode(MModel.PREVIEW_VIDEO, model.isTimerActive(), model.getPreviewFrame());
//        }
//    }
    
    /**
     * When the delete item button is pressed.
     */
    class DeleteItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {      
           int index  = view.getCardMain().getSelectThumbnailIndex();
           int size   = view.getCardMain().getThumbnailsModelSize();
           
           // Deletes the file
           MyIO.deleteFile(model.getPreviewFile());            
           
           // Updates the project files
           model.updateProjectFiles();
           
           // Updates the GUI (thumbnails) so that the file will desappear
           view.getCardMain().updateThumbnailsList(model.getProjectFiles());
           
            // Checks if there was just one element
           if( size != 1) {
               // If not, select another element
                //Checks if was the last element or not
                if ( index == size -1) {
                    // Last element, go the the previous one.
                    index--;
                } else {
                    // Not last element, keep the same index for go to the next one.
                }
                view.getCardMain().selectThumbnailElement(index);
           } else {
               // If there are no more elments on the list go back to normal mode
               
                //Returns to the microscope
                // Restarts Observing
                play();
                // Updates the model and view
                model.setMode(model.getModeBeforePreview());
                view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen());
           }
        }
    }
    
    //.......................................................rigthPanelListeners   
    /**
     * When the brightness slider is moved.
     */
    class BrightnessListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent ce) {
            JSlider source = (JSlider)ce.getSource();
            model.getDevice().setBrightness(source.getValue());
        }
    }    

    /**
     * When a picture is taken.
     * 1 - Save the actual frame
     * 2 - Stop Observing
     * 3 - Update the model
     * 4 - Update the GUI 
     * 4.1 - Update the thumbnail
     */
    class TakePicListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {

            //1 - Save the actual frame
            String file = Utils.savePicture(model.getDevice().getImage(), model.getProjectPath());
            
            //2 - Stop Observing
            stop();
            
            //3 - Update the model
            model.setMode(MModel.PREVIEW_IMAGE);
            model.setPreviewFile(file);
            model.setPreviewFrame(MyIO.readBufferedImage(file));
            model.updateProjectFiles();
           
            //4 - Update the GUI
            view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen(), model.getPreviewFrame());
            //4.1 - Update the thumbnail
            view.getCardMain().updateThumbnailsList(model.getProjectFiles());
            //4.2 - Select the thumbnail of the new file
            view.getCardMain().selectThumbnailElement(file);
            
        }
    }
    
    class HideMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.hideMessage();
        }
    }
    
    class StartUpdatesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.setVisible(false);
//            model.getLanguageCode();
            UpdateManagerUpdateDialog.createAndShowGUI(view, true);
            exitMVC();
        }
    }
    
    public void startTimeLaps ( ) {
            // 1 - Controls input time
            String intervalText = view.getCountdownInputValue(); 
            int seconds  = Integer.parseInt(intervalText.substring(0, 2)) * 60;
            seconds     += Integer.parseInt(intervalText.substring(3, 5));

            // 1 - Creates the directory
            videoName = "timelaps_" + Misc.getTime("dd-MMM-yy_HH:mm:ss");
            videoDir = model.getProjectPath() + File.separator + videoName;
            MyIO.makeSureExistDir(videoDir);

            // 2 - Starts the timer
            TimeLapsTimerTask task;
            task = new TimeLapsTimerTask(seconds,videoDir,view,model);
            timer = new Timer();
            timer.scheduleAtFixedRate(task, 0, 1000);

            // 3 - Udates the model and view
            model.setTimerActive(true);
            view.adaptToMode(model.getMode(), model.getUserMode(), true, model.isFrozen());
    }
        
    class StartTimeLapsListenerNoControls implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            view.hideMessage();
            startTimeLaps();
        }
    }
    
    /**
     * When start Timelaps starts
     * 1 - Controls input time
     * 2 - Checks if the interval is zero and show error, exit.
     * 3 - Checks if the interval il less than MModel.focusTime if yes shows modal box
 3.1 - Shows alert box
 3.2 - If the user replays NO - exit
 4 - Starts the timer
 5 - Udates the model   
     */
    class StartTimeLapsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            // 1 - Controls input time
            String intervalText = view.getCountdownInputValue(); 
            int seconds  = Integer.parseInt(intervalText.substring(0, 2)) * 60;
            seconds     += Integer.parseInt(intervalText.substring(3, 5));
            
            if (seconds == 0) {
                // 2.a
                // Shows message
                view.setMessageVisible("/resources/gui/icons/icon-warning.png",
                model.getResourceBundle().getString("MSGTimeBiggerThanZero"),
                null,
                new String [] {
                    ""},
                new String [] {
                    "/resources/gui/icons/icon-yes.png",
                },
                new ActionListener [] {
                    new HideMessageListener()               // Aborts the time laps
                });

                // 2.b - Checks if the interval il less than MModel.focusTime if yes shows modal box
            } else if (seconds <= model.getFocusTime()) {                 
                // Shows message           
                view.setMessageVisible("/resources/gui/icons/icon-question.png",
                        model.getResourceBundle().getString("MSGTimeLessThanFocus"),
                        null,
                        new String [] {
                            "",
                            ""
                        },
                        new String [] {
                            "/resources/gui/icons/icon-yes.png",
                            "/resources/gui/icons/icon-no.png",
                        },
                        new ActionListener [] {
                            new StartTimeLapsListenerNoControls(),  // Starts the time laps
                            new HideMessageListener()               // Aborts the time laps
                        });
            } else {
                // 2.c - 
                startTimeLaps();
            }
        }
    }
    
    class StopTimeLapsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            // Stops the timer
            timer.cancel();
            
            // Udates the model and view   
            model.setTimerActive(false);
            model.setFrozen(false);
            // It could be that the user stops the timer meanwhile the GUI is frozen
            view.adaptToFrozen(model.isFrozen(), model.getMode());  
            view.adaptToMode(model.getMode(), model.getUserMode(), false, model.isFrozen());
            
            // Checks that at least one image was taken before to create the video
            if (view.getTimeLapsCount() != 0 ) {
                // Creates the video
                String outputVideoFile = videoDir + "." + SettingsManager.getVideoFormat();
                String imagesSource    = videoDir + File.separator + "image-%04d." + SettingsManager.getImagesExtension();
                // TODO GET THE TIMELAPS TIME FROM USERSETTINGS
                Misc.createVideo(outputVideoFile, 50, imagesSource, videoDir, view.getTimeLapsCount());

                // Updates the model
                model.updateProjectFiles();

                // Updates the GUI (thumbnail)
                view.getCardMain().updateThumbnailsList(model.getProjectFiles());
                // Selects the thumb of the new video
                view.getCardMain().selectThumbnailElement(outputVideoFile);
            }
            
            // Sets to zero the image cout
            view.updateTimeLapsCount(String.valueOf(0));
        }
    }
    
    /**
     * When back button is pressed.
     * 1 - Restart Observing
     * 2 - Update the model
     * 3 - Adapt the GUI to the mode
     */
    class BackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {       
            //1 - Restart Observing
            play();
            
            //2 - Update the model
            model.setMode(model.getModeBeforePreview());
            
            //3 - Change the GUI
            view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen());
        }
    }  

    /**
     * When the paint button is pressed opens the image with Gimp.
     * If Gimp isn't available tries to open the image with the default program.
     */
    class PaintListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) { 
            boolean opened = false;
            if (Misc.isGimpInstalled()) {
                // Open img with gimp
                opened = Misc.openFileWhitGimp(model.getPreviewFile());
            } 
            // If gimps fails or not available 
            if ( opened == false ) {
                // Open img using the standard programm of the OS   
                try {
                    if (!Desktop.isDesktopSupported()) {
                       throw new MyException("Desktop isn't supported");
                    }

                    Desktop desktop = Desktop.getDesktop();
                    if (!desktop.isSupported(Desktop.Action.OPEN)) {
                       throw new MyException("Open function on desktop isn't supported");
                    }

                    desktop.open(new File(model.getPreviewFile()));
                } catch (IOException e) {
                    Utils.print(e.getMessage());
                } catch (MyException ex) {
                    Logger.getLogger(MController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * When a preview has to be displayed.
     * @param previewFile 
     */
    void setPreview(String previewFile){
        int mode;
        //Get the image
        String previewFileFrame;

        // Check if the file is a video or not
        if (previewFile.endsWith(SettingsManager.getVideoFormat())) {
          // If it is use the first image inside the directory 
          // that has the same name of the video
          String videoDir  = previewFile.substring(0, previewFile.length()-4);
          previewFileFrame = MyIO.getFirstImageInToDIr(videoDir);
          mode = MModel.PREVIEW_VIDEO;
        } else {
          previewFileFrame = previewFile;
          mode = MModel.PREVIEW_IMAGE;
        }

        //1 - Stop Observing
        stop();

        //2 - Set model previewFrame and previewFile
        model.setPreviewFile(previewFile);
        model.setPreviewFrame(MyIO.readBufferedImage(previewFileFrame));

        //3 - Update the model
        model.setMode(mode);

        //4 - Update the GUI
        view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen(), model.getPreviewFrame());
    }
    
    /**
     * Checks when the selected item changes, if the GUI is on preview mode it 
     * switches the element on preview, otherwise it does nothing
     */
    class ThumbnailsSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent lse) {
//            if (model.getMode() == MModel.PREVIEW_VIDEO || model.getMode() == MModel.PREVIEW_IMAGE ) {
            if (!lse.getValueIsAdjusting()){
                JList theList = (JList)lse.getSource();
                int index = theList.getSelectedIndex();           
                if (index != -1) {
                    String previewFile = theList.getModel().getElementAt(index).toString();
                    // Update the preview
                    MController.this.setPreview(previewFile);
                    // Stop any possible video resource that is playing or paused
                    view.freeVideo();
                }
//            }
            }
        }        
    }
    
//    NOT NEEDED IF A SINGLE CLICK ENABLES THE PREVIEW
//    class ThumbnailsMouseListener extends MouseAdapter{
//        @Override
//        public void mouseClicked(MouseEvent mouseEvent) { 
//            if ( !model.isFrozen() ){
//// Hannes and Chiara decided that the selection of an element is made with a simple click and 
//// no more with a double click how implemented before. -- Nicola, Feb 2 2015
////                if (mouseEvent.getClickCount() == 2) {
//                  JList theList = (JList) mouseEvent.getSource();
//                  int index = theList.locationToIndex(mouseEvent.getPoint());
//                  String previewFile = theList.getModel().getElementAt(index).toString();
//                  MController.this.setPreview(previewFile);
////                }
//            }
//        }
//    }
    
    void hideHelp () {
        view.removeGlassPanelListener ();
        view.setHelpVisible(false, model.getResourceBundle(), model.getMode());
    }
    
    class GlassPanelMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) { 
            hideHelp();
        }
    }
          
    //........................................................KeyEventDispatcher
    /**
     * When a key is pressed depending on which key the programs responses 
     * on a different way. Here a list of possible combination.
     * ALT + C  = Shows Control Panel
     * ALT + A  = Shows About Panel
     * ALT + Q  = Closes the program 
     * DEL      = If the user isn't a normal user and if an image is selected on 
     *              the thumbnail, the selected image is removed 
     */
    class MyKeyEventDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent ke) { 
            if (ke.getID() == KeyEvent.KEY_PRESSED) {
               
                if (ke.getKeyCode() == KeyEvent.VK_DELETE && model.getUserMode() != SettingsManager.USERMODE_BASIC) {
                    //Canc key
                    //NOTE we decide to disable this option to avoid that kids delete pics accidentaly 
                    //deleteSelectedFile(true);
                } else if (ke.isAltDown()) {
                    //da rivedere il conttrollo !controlPanelMenuItem.isVisible() poichè questo interessa
                    //solo l'ALT+c  && !controlPanelMenuItem.isVisible()

                    if (ke.getKeyCode() == KeyEvent.VK_C) {
                        // Alt+C
                        // Hide hints if visible
                        hideHelp ();
                        // Show control settings panel 
                        showSettings();
                    } else if (ke.getKeyCode() == KeyEvent.VK_A) {
                        //Alt+A
                        showAbout();
                    } else if (ke.getKeyCode() == KeyEvent.VK_Q) {
                        //Alt+Q
                        releaseDevice();
                        Main.myfinalize();
                    }
                }
            }
            return false;
        }
    }//end Inner class MyKeyEventDispatcher
    
    class MediaPlayerListener extends MediaPlayerEventAdapter {
        @Override
        public void finished(MediaPlayer mediaPlayer) {
            model.setMode(MModel.PREVIEW_VIDEO);
            view.freeVideo();
            view.adaptToMode(MModel.PREVIEW_VIDEO, model.getUserMode(), model.isTimerActive(), model.isFrozen(), model.getPreviewFrame());
        }
    }
    
    
    
    
    
    //........................................................Settings
    class languageCBoxModelListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent ie) {
            // This event is fired twice, one for the deselected element and one for the selected one
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                String lang = ie.getItem().toString();
//                Utils.print("MController::languageCBoxModelListener::itemStateChanged Setting language "+lang);
                
                model.setLanguage(lang);

                // Updates the view to the new language
                view.adaptToLanguage(model.getResourceBundle());
                
                // Changes are saved in the system when quitting
            }
        }
    }

    class usermodeCBoxModelListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent ie) {
            // This event is fired twice, one for the deselected element and one for the selected one
            if (ie.getStateChange() == ItemEvent.SELECTED) {               
                // Updates the model and view
                model.setUserMode(view.getCardSettings().getUserModeIndex());
                view.adaptToUser(model.getUserMode(), model.getMode());
                
                // The change is saved in the system when quitting
            }
        }
    }
    
    class deviceCBoxModelListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent ie) {
            // This event is fired twice, one for the deselected element and one for the selected one
            if (ie.getStateChange() == ItemEvent.SELECTED) {     
                String d = ie.getItem().toString();
                //Utils.print("Selected "+d);
                d = d.substring(d.lastIndexOf("@")+1);

                // Release previous device
                releaseDevice();
                
                // Updates the model and view
                setDevice(new Device(d));                
                view.adaptToDevice(model.getDevice());
            }
        }
    }
    
    class goToProjectSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            model.setMode(MModel.PROJECT);
            view.adaptToMode(model.getMode(), model.getUserMode(), model.isTimerActive(), model.isFrozen());    
        }
    }
    
    class CheckUpdatesListener  implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            model.setCheckUpdate( ((JCheckBox)ae.getSource()).isSelected());
        }
    }
}

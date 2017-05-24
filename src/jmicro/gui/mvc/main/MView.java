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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.text.ParseException;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import jmicro.Globals;
import jmicro.SettingsManager;
import jmicro.Utils;


import jmicro.gui.components.MyHelpLabel;
import jmicro.gui.components.MyButton;
import jmicro.gui.components.MyButtonKind;
import jmicro.gui.components.MyHelpLabelNoLine;
import jmicro.gui.componentsUI.MyScrollBarUI;
import jmicro.gui.components.panels.HightlightedPanel;


import jmicro.utils.Misc;
import jmicro.utils.MyLookAndFeel;

import jmicro.v4l4jutils.Device;

import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * View used in the Main MVC.
 */
public class MView extends JFrame {

    private final String INNER_PANEL_PROJECT_KEY       = "PROJECT";
    private final String INNER_PANEL_MAIN_KEY          = "MAIN";
    private final String INNER_PANEL_SETTINGS_KEY      = "SETTINGS";
    //... The view interacts only with the model
    private final KeyboardFocusManager keyboardManager;

    //......................................Dimension and position of Components

    
    //DELETE THESE 
//    static final int ContentBorder_left      = 25;
//    static final int ContentBorder_bottom    = 30;
//    static final int ContentBorder_right     = 25;
//    static final int ContentBorder_top       = 0;
    
    static final int TOP_PANEL_HIGHT = 60;
    static final int TOP_PANEL_BORDER_TOP    = 0;
    static final int TOP_PANEL_BORDER_LEFT   = 20;
    static final int TOP_PANEL_BORDER_BOTTOM = 0;
    static final int TOP_PANEL_BORDER_RIGHT  = 20;
    
    static final int MESSAGE_PANEL_BORDER_TOP    = 10;
    static final int MESSAGE_PANEL_BORDER_LEFT   = 10;
    static final int MESSAGE_PANEL_BORDER_BOTTOM = 10;
    static final int MESSAGE_PANEL_BORDER_RIGHT  = 10;

    private static final Dimension minVideoContainerSize   = new Dimension(CardMain.minVideoWidth + 90,  CardMain.minVideoHeight + 70);
    public static final Dimension minMiddlePanelSize      = new Dimension(CardMain.minVideoWidth + 535, minVideoContainerSize.height);

    // Messages params
    int hgap_btn = 50;
    
    
    //...............................................................Main Panels
    private final JPanel content;
    private final JPanel glassPanel;

    //................................................Components of the TopPanel
    private JPanel topPanel;
        
    private JButton helpButton;
    private JButton backButton;
//    private JPanel  backButtonContainer;
    private JButton settingButton;
    
    private JPanel countdownContainerInnerPanel;
    private JPanel countdownContainerPanel;
    private JLabel countdownIconLabel;
    private JFormattedTextField countdownInput;
    private JPanel countdownInputPanel;
    private JLabel countdownLabel;
    private JLabel countdownTimeLabel;
    private JPanel countdownTimePanel;
    private JLabel timeLapsCountLabel;
    private JPanel timeLapsCountPanel;
    
    private JButton exitButton;
    
    //.............................................Components of the MiddlePanel
    private JPanel middlePanel;
    private CardLayout middlePanelLayout;        
    private CardSettings middlePanelCard_Settings;
    private CardMain     middlePanelCard_Main;
    private CardProject  middlePanelCard_Project;

    /*-------------------------------------------------------------------------+
     |                              Constructor                                |
     * -----------------------------------------------------------------------*/
    /**
     * Creates a View of the of the main MVC architecture.
     * In this MVC architecture the view doesn't know the model. 
     * It's the controller that creates and queries the model.
     */
    public MView() {
        //................................................ Initialize components
        keyboardManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        //Inits ad set all the components of diffferents parts
        initTopPanel();

        initMiddlePanel(); //left panel, video panel and right panel
        middlePanel.setMinimumSize(new Dimension(1100, 510));

        //.........................................................Content Panel
        content = new JPanel();
        content.setBackground(MyLookAndFeel.MYGREEN);
        
        GroupLayout layout = new GroupLayout(content);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(topPanel,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                .addComponent(middlePanel,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(topPanel,
                                TOP_PANEL_HIGHT,
                                TOP_PANEL_HIGHT,
                                TOP_PANEL_HIGHT)
                        .addComponent(middlePanel,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                )
        );
        content.setLayout(layout);

        glassPanel = new JPanel() {
            {
                setBorder(null);
                setBackground(MyLookAndFeel.MYBLACK_SHADOW);
            }
        };

        //...................................................... finalize layout
        this.setContentPane(content);
        this.setGlassPane(glassPanel);
        this.pack();


        this.setMinimumSize(new Dimension(MyLookAndFeel.WINDOWS_WIDTH,MyLookAndFeel.WINDOWS_HEIGHT));
          // The video panel of the main card also has is own minimum size! This dirty code is due to last minutes changes 
        setResizable(false); //trick to enable the full screen button 
    }

    /*-------------------------------------------------------------------------+
     |                      Init Methods called by the Constructor             |
     * -----------------------------------------------------------------------*/

    /**
     * Inits all the components inside the right panel and the panel itself
     */
    JPanel leftButtons;
    private void initTopPanel() {
        //..........................................Definition of the components
        topPanel = new JPanel() {
            {
                setBackground(MyLookAndFeel.MYGREEN);
                setBorder(BorderFactory.createEmptyBorder(
                    TOP_PANEL_BORDER_TOP,
                    TOP_PANEL_BORDER_LEFT,
                    TOP_PANEL_BORDER_BOTTOM,
                    TOP_PANEL_BORDER_RIGHT)
                );
            }
        };
 
        
        FlowLayout fl = new FlowLayout() {{
            this.setAlignment(LEFT);
            this.setHgap(0);
        }};
        
        leftButtons = new JPanel () {{
            this.setOpaque(false);
            this.setBorder(null);
        }};

        leftButtons.setLayout(fl);
        
        helpButton = new MyButton(MyButtonKind.SMALL_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-help.png")));
        settingButton = new MyButton(MyButtonKind.SMALL_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-setting.png")));
        backButton = new MyButton(MyButtonKind.SMALL_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-back.png")));
        
        leftButtons.add(helpButton);
        leftButtons.add(settingButton);
        leftButtons.add(backButton);
        
        //.........................................................Central panel
        countdownContainerPanel = new JPanel(new FlowLayout()) {
            {
                setBackground(MyLookAndFeel.MYGREEN);
            }
        };

        countdownContainerInnerPanel = new JPanel(new FlowLayout()) {
            {
                setBackground(MyLookAndFeel.MYGREEN);
            }
        };
        countdownIconLabel = new JLabel();
        countdownIconLabel.setIcon(new ImageIcon(getClass().getResource("/resources/gui/icons/icon-mode-timer.png")));
        countdownTimeLabel = new JLabel() {
            {
                setText("00:00");
                setForeground(Color.WHITE);
            }
        };
        countdownLabel = new JLabel();
        timeLapsCountLabel = new JLabel() {
            {
                setText("0");
            }
        };
        
        try {
            MaskFormatter mask = new MaskFormatter("##:##");
            mask.setPlaceholderCharacter('0');
            mask.setPlaceholder("01:00");
            
            countdownInput = new JFormattedTextField(mask){{
                setOpaque(false);
                setBorder(null);                
                setColumns(3);
                
            }};
        } catch (ParseException ex) {
            Logger.getLogger(javax.swing.text.View.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Initaliazes the hightlighted panels
        // 1. Sets the colors: back and fore, where the foreground is used for 
        //      as color fo the border.
        // 2. Sets the dimension (70,30) 
        // 3. Adds the component that each panel has to contain
        countdownInputPanel = new HightlightedPanel() {
            {
                setBackground(Color.WHITE);
                setPreferredSize(new Dimension(70, 30));
                add(countdownInput, BorderLayout.CENTER);
            }
        };

        countdownTimePanel = new HightlightedPanel() {
            {
                setBackground(Color.RED);
                setForeground(Color.RED);
                setPreferredSize(new Dimension(70, 30));
                add(countdownTimeLabel, BorderLayout.CENTER);
            }
        };

        timeLapsCountPanel = new HightlightedPanel() {
            {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                setPreferredSize(new Dimension(70, 30));
                add(timeLapsCountLabel, BorderLayout.CENTER);
            }
        };

        exitButton = new MyButton(MyButtonKind.SMALL_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-exit.png")));

        //................................................................Layout
        countdownContainerInnerPanel.add(countdownIconLabel);
        countdownContainerInnerPanel.add(countdownInputPanel);
        countdownContainerInnerPanel.add(countdownLabel);
        countdownContainerInnerPanel.add(countdownTimePanel);
        countdownContainerInnerPanel.add(timeLapsCountPanel);

        countdownContainerPanel.add(countdownContainerInnerPanel);
        
        //Sets a layout which set the countPanel extendable
        GroupLayout layout = new GroupLayout(topPanel);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                        .addComponent(leftButtons, MyButton.SMALL_RECTANGULAR_WIDTH*2, MyButton.SMALL_RECTANGULAR_WIDTH*2, MyButton.SMALL_RECTANGULAR_WIDTH*2)
                        .addComponent(countdownContainerPanel, 60, 60, Short.MAX_VALUE)
                        .addComponent(exitButton)
                
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(leftButtons)
                .addComponent(countdownContainerPanel)
                .addComponent(exitButton)
        );
        topPanel.setLayout(layout);
    }
    
    /**
     * Inits all the components inside the middle panel and the panel itself. 1.
     * Left Panel 2. Right Panel 3. Video Panel 4. The panel itself
     */
    private void initMiddlePanel() {       
        middlePanelCard_Main = new CardMain();
        middlePanelCard_Project = new CardProject(); 
        middlePanelCard_Settings = new CardSettings();    

        middlePanelLayout = new CardLayout();
        
        middlePanel = new JPanel() {
            {
                setBackground(MyLookAndFeel.MYGREEN);
                setLayout(middlePanelLayout);
                add(middlePanelCard_Project,INNER_PANEL_PROJECT_KEY);
                add(middlePanelCard_Main,INNER_PANEL_MAIN_KEY);
                add(middlePanelCard_Settings,INNER_PANEL_SETTINGS_KEY);
                
                setBorder(null);
            }
        };
        
        middlePanel.addComponentListener(new MiddlePanelLister());
    }   
    
    private void setMiddlePanelComponentsLocation() {
        middlePanelCard_Main.setComponentsLocation();
    }
    
    
    
    /*-------------------------------------------------------------------------+
     |                      Methods that changes the view                      |
     +------------------------------------------------------------------------*/
    
    /**
     * Method that adapts the GUI to the device in use. For example: hides or
     * shows the lamp buttons so to reflect which lamp the device in use has,
     * updates the title of the main window, hides or shows the brightness
     * controls based on if is possible to set this parameter on the device etc.
     * @param d The device to whose adapt the GUI
     */
    public void adaptToDevice(Device d) {

        String title = "jMicro ~ ";
        
        // Check of the defice is null or exists
        if ( d.isNone() ) {            
            // Compose none title
            title = title.concat(Globals.NO_DEVICE_CODE);
        } else { // not null
            // Compose the title
            title = title.concat(d.getDeviceName() + " @ "+ d.getDeviceFile());      
        }
   
        // Set the window's title
        this.setTitle(title);
        
        // Updates cards
        middlePanelCard_Main.adaptToDevice(d);
        middlePanelCard_Settings.adaptToDevice(d);
    }

    /**
     * Updates the GUI to the current language.
     * @param rb The resource bundle from where get the internationalized strings
     */
    public void adaptToLanguage(ResourceBundle rb) {
//        Utils.print("MView::adaptToLanguage() Adapting to language "+rb.getLocale().getLanguage());
        // Updates cards
        middlePanelCard_Main.adaptToLanguage(rb);       
        middlePanelCard_Project.adaptToLanguage(rb);
        middlePanelCard_Settings.adaptToLanguage(rb);
        
        // Updates generic components
        countdownLabel.setText(rb.getString("LblCountdown"));            
    }

    public void adaptToUser(int userMode, int mode) {
        switch (userMode) {
            case SettingsManager.USERMODE_BASIC:
                settingButton.setVisible(false);
//                middlePanelCard_Main.adaptToUser(true);
                break;
            case SettingsManager.USERMODE_ADVANCED:
                // Show the setting button only if not already in settings
                if (mode != MModel.SETTINGS) settingButton.setVisible(true);
//                middlePanelCard_Main.adaptToUser(false);
                break;             
        }
        middlePanelCard_Main.adaptToUser(userMode);
        middlePanelCard_Project.adaptToUser(userMode);
    }

    /**
     * Adapts the GUI to the give user mode and active flag.
     * @param mode      User mode
     * @param isTimerActive    Flag that says whether or not the timer is active
     */
    public void adaptToMode(int mode, int userMode, boolean isTimerActive, boolean frozen) {
        adaptToMode(mode, userMode, isTimerActive, frozen, null);
    }
    
    /**
     * Adapts the GUI to the give user mode, preview image and active flag.
     * @param mode      User mode
     * @param isTimerActive    Flag that says whether or not the timer is active
     * @param frozen    Flag that says whether or not the GUI is frozen
     * @param preview   Preview image
     */
    public void adaptToMode(int mode, int userMode, boolean isTimerActive, boolean frozen, BufferedImage preview) {
        
        //............................................... Top panel's components
        // Setting and back buttons
        if (mode == MModel.SETTINGS) {
            settingButton.setVisible(false);
            backButton.setVisible(true);
        } else {
            // Show the setting button only if the user is advaced
            if (userMode == SettingsManager.USERMODE_ADVANCED) settingButton.setVisible(true);
            backButton.setVisible(false);
        }
        
        if ( mode == MModel.TIMELAPS ) {
            countdownIconLabel.setVisible(true);
            countdownInputPanel.setVisible(!isTimerActive);
            countdownLabel.setVisible(!isTimerActive);
            countdownTimePanel.setVisible(isTimerActive);
            timeLapsCountPanel.setVisible(isTimerActive);
        } else {
            countdownIconLabel.setVisible(isTimerActive);
            countdownTimePanel.setVisible(isTimerActive);
            timeLapsCountPanel.setVisible(isTimerActive);
            countdownInputPanel.setVisible(false);
            countdownLabel.setVisible(false);
        }
        
        middlePanelCard_Main.adaptToMode(mode, isTimerActive, frozen, preview);
        switch (mode) {
            case MModel.PROJECT:
                middlePanelLayout.show(middlePanel, INNER_PANEL_PROJECT_KEY);
                break;
            case MModel.SETTINGS:
                middlePanelLayout.show(middlePanel, INNER_PANEL_SETTINGS_KEY);
                break;
            case MModel.NORMAL:
            case MModel.TIMELAPS:
            case MModel.PREVIEW_VIDEO:
            case MModel.PREVIEW_VIDEO_PLAY:
            case MModel.PREVIEW_VIDEO_PAUSE:
            case MModel.PREVIEW_VIDEO_STOP:
            case MModel.PREVIEW_IMAGE:
            default:
                middlePanelLayout.show(middlePanel, INNER_PANEL_MAIN_KEY);
                break;
        }    
    }

    /**
     * Check if the GUI is frozen from the model and than freezes or unfreezes
     * the components of the actual view. TODO: 1 - setEnable on thumbanils
     * @param isFrozen
     * @param mode
     */
    public void adaptToFrozen (boolean isFrozen, int mode) {
        middlePanelCard_Main.adaptToFrozen(isFrozen, mode);
        middlePanelCard_Settings.adaptToFrozen(isFrozen, mode);
    }

    /**
     * Shows/hides the help panel that overlays all the components
     *
     * @param flag  If true shows the help, hides it otherwise
     * @param rb    The resource bundle from where get the help text
     */
    public void setHelpVisible(boolean flag, ResourceBundle rb, int mode) {
        if (flag) {
            glassPanel.setLayout(null);
            //.......................................................Main Window
            testShowingAndAddToHelp(countdownInput,     rb.getString("TltCountDownInterval"),   MyHelpLabel.ORIENTATION_BOTTOM, 100);
            testShowingAndAddToHelp(countdownTimePanel, rb.getString("TltCountDownTime"),       MyHelpLabel.ORIENTATION_BOTTOM, 250);
            
            if(settingButton.isVisible()) {
                testShowingAndAddToHelp(settingButton,  rb.getString("TltSettingsBtn"),         MyHelpLabel.ORIENTATION_LEFT,   200);
            } else {
                if(mode != MModel.SETTINGS ) addToHelpNoLine(helpButton, rb.getString("TltSettingsBtn"), MyHelpLabel.ORIENTATION_LEFT,   200);
            }
            testShowingAndAddToHelp(backButton,         rb.getString("TltMainBtn"),             MyHelpLabel.ORIENTATION_LEFT,   200);
            testShowingAndAddToHelp(exitButton,         rb.getString("TltExitBtn"),             MyHelpLabel.ORIENTATION_RIGHT,  200);
       
            // Updates cards
            //IT MIGHT BE IMPROVED ADDING ONLY THOSE OF THE CARD CURRENTLY DISPLAYED!!!!
//            ((CardLayout)middlePanel.getLayout()).ge
//            switch ((Card)middlePanel.getLayout())
            List l = middlePanelCard_Settings.getHelpLabels(rb, glassPanel);
            l.addAll(middlePanelCard_Project.getHelpLabels(rb, glassPanel));
            l.addAll(middlePanelCard_Main.getHelpLabels(rb, glassPanel));
            for (Object o : l) {
                glassPanel.add((JComponent)o);
            }
        } else {
            glassPanel.removeAll();
        }
        glassPanel.setVisible(flag);
    }

    /**
     * Tests if the component c is visible and in case adds the text to the glass panel
     * that shows the help.
     * @param c         The component
     * @param text      The help message
     * @param left      Flag that defines if the message must be shown at the left of the component or not
     * @param size      Maximum width of the message displayed
     */
    private void testShowingAndAddToHelp(JComponent c, String text, int position, int width) {
        // Checks if the jComponent is visible or not
        if (c.isShowing()) {
            addToHelp(c, text, position, width);
        }
    }
    
    /**
     * Add the component's help to the glass panel
     * that shows the help.
     * @param c         The component
     * @param text      The help message
     * @param left      Flag that defines if the message must be shown at the left of the component or not
     * @param size      Maximum width of the message displayed
     */
    private void addToHelp (JComponent c, String text, int position, int width) {
        glassPanel.add(new MyHelpLabel(c, glassPanel, text, position, width));
    }
    
     /**
     * Add the component's help to the glass panel
     * that shows the help.
     * REQUIRES REFACTORING: IT EXISTS BECAUSE WAS FASTER TO IMPLEMENT THIS WAY 
     * @param c         The component
     * @param text      The help message
     * @param left      Flag that defines if the message must be shown at the left of the component or not
     * @param size      Maximum width of the message displayed
     */
    private void addToHelpNoLine (JComponent c, String text, int position, int width) {
        glassPanel.add(new MyHelpLabelNoLine(c, glassPanel, text, position, width));
    }
    
/**
 * Shows the message panel that overlays all the components
 * @param icon
 * @param message_short
 * @param message_long
 * @param options
 * @param options_icons
 * @param options_listeners 
 */
    public void setMessageVisible(
            String icon, 
            String message_short, 
            String message_long, 
            Object []           options, 
            Object []           options_icons,
            ActionListener []   options_listeners)  {
        
        // Message
        JLabel icon_label           = new JLabel(new ImageIcon(getClass().getResource(icon))) {{
            setBorder(new EmptyBorder(20,00,20,00));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};
        JLabel message_short_label  = new JLabel(Misc.wrapString(message_short)) {{
            setBorder(new EmptyBorder(20,00,20,00));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }};
        JLabel message_long_label         = null;
        JScrollPane message_long_scroller = null;
        
        if (message_long != null){
            message_long_label      = new JLabel (message_long);
            message_long_scroller   = new JScrollPane(message_long_label)
            {{
                setBorder(null);
                setPreferredSize(new Dimension(500, 300));
                getVerticalScrollBar().setUI(new MyScrollBarUI());
                setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            }};
        }
        
        JPanel message_container = new JPanel (){{
            setOpaque (false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(20,20,20,20));
        }};
        
        message_container.add(icon_label);
        message_container.add(message_short_label);
        if (message_long_scroller != null) message_container.add(message_long_scroller);
        
        // Options
        JPanel options_container = new JPanel (){{
            setOpaque (false);
            setLayout(new FlowLayout (FlowLayout.CENTER, hgap_btn, 0));
            setBorder(new EmptyBorder(20,20,20,20));
        }};

        for (int i = 0; i < options.length; i++) {
            JButton btn = new MyButton(
                    options[i].toString(), MyButtonKind.BIG_RECTANGULAR.toString(),
                    new ImageIcon(getClass().getResource(options_icons[i].toString()))
            );
            btn.addActionListener(options_listeners[i]);
            options_container.add(btn); 
        }
        
        // Container
        JPanel container = new JPanel () {{
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                        MESSAGE_PANEL_BORDER_TOP,
                        MESSAGE_PANEL_BORDER_LEFT,
                        MESSAGE_PANEL_BORDER_BOTTOM,
                        MESSAGE_PANEL_BORDER_RIGHT,
                        MyLookAndFeel.MYGREEN),
                    BorderFactory.createEmptyBorder(
                        MESSAGE_PANEL_BORDER_TOP,
                        MESSAGE_PANEL_BORDER_LEFT,
                        MESSAGE_PANEL_BORDER_BOTTOM,
                        MESSAGE_PANEL_BORDER_RIGHT)
                    ));
        }};
        container.add(message_container);
        container.add(options_container);

        // The component c will be centered because GridBagLayout is set
        glassPanel.setLayout(new GridBagLayout());
        glassPanel.add(container);
        glassPanel.setVisible(true);           
    }

    /**
     * Hides the glass panel whose shows the help.
     */
    public void hideMessage() {
        glassPanel.removeAll();
        glassPanel.setVisible(false);
    }

    
    
    /*-------------------------------------------------------------------------+
     |                      Add Listener Methods                               |
     +------------------------------------------------------------------------*/
    
    //..................................................................TopPanel
    public void addSettingListener(ActionListener al) {
        settingButton.addActionListener(al);
    }
    
    public void addBackListener(ActionListener al) {
        backButton.addActionListener(al);
    }

    public void addHelpListener(ActionListener al) {
        helpButton.addActionListener(al);
    }

    public void addExitListener(ActionListener al) {
        exitButton.addActionListener(al);
    }    
    
    public void addGlassPanelListener(MouseListener ml) {
        glassPanel.addMouseListener(ml);
    }

    public void addKeyEventDispatcher(KeyEventDispatcher dispatcher) {
        keyboardManager.addKeyEventDispatcher(dispatcher);
    }

    public void updateTimeLapsCount(String count) {
        timeLapsCountLabel.setText(count);
    }

    public void removeGlassPanelListener() {
        if (glassPanel.getMouseListeners().length != 0) {
            glassPanel.removeMouseListener(glassPanel.getMouseListeners()[0]);
        }
    }

    
    
    /*-------------------------------------------------------------------------+
     |                      Component Listeners                                |
     +------------------------------------------------------------------------*/

    class MiddlePanelLister implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent ce) {
            MView.this.setMiddlePanelComponentsLocation();
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
        }

        @Override
        public void componentShown(ComponentEvent ce) {
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
        }
    }

    
    


    public void startObserve(Device device) {
        middlePanelCard_Main.getVideoScreen().startObserve(device);
    }

    public void stopObserve(Device device) {
        middlePanelCard_Main.getVideoScreen().stopObserve(device);
    }

    /**
     * Updates the countdown label with the text
     *
     * @param text The new label's text
     */
    public void setCountdownLabel(String text) {
        countdownTimeLabel.setText(text);
    }

    public String getCountdownInputValue() {
        return countdownInput.getText();
    }

    public void playVideo(String file, MediaPlayerEventAdapter mpea) {     
        middlePanelCard_Main.getVideoScreen().playCanvas(file, mpea);
    }
    
    public void restartVideo(){
        middlePanelCard_Main.getVideoScreen().restartCanvas();
    }
    
    public void pauseVideo(){
        middlePanelCard_Main.getVideoScreen().pauseCanvas();
    }
    
    public void freeVideo(){
        middlePanelCard_Main.getVideoScreen().freeCanvas();
    }
    
        
    public void stopVideo(){
        middlePanelCard_Main.getVideoScreen().stopCanvas();
    }
    
    public boolean isPlayingVideo() {
        return middlePanelCard_Main.getVideoScreen().isPlaying();
    }
    
    public void setFocusOnInputIntervel() {
        this.countdownInput.requestFocus();
    }
    
    public int getTimeLapsCount() {
        return Integer.parseInt(this.timeLapsCountLabel.getText());
    }
    
    //.....................................................................Cards
    
    public CardProject getCardProject () {
        return middlePanelCard_Project;
    }
    
    public CardMain getCardMain () {
        return middlePanelCard_Main;
    }
    
    public CardSettings getCardSettings () {
        return middlePanelCard_Settings;
    }
}
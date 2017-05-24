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

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import jmicro.Globals;
import jmicro.SettingsManager;
import jmicro.Utils;
import jmicro.gui.components.MyLabelBigRed;
import jmicro.gui.components.MyComboBox;
import jmicro.gui.components.MyButton;
import jmicro.gui.components.MyButtonKind;
import jmicro.gui.components.MySeparator;
import jmicro.utils.MyLookAndFeel;
import jmicro.v4l4jutils.Device;
import jmicro.v4l4jutils.v4l4jMyUtils;

/**
 * JPanel card that contains settings main view.
 */
public class CardSettings extends JPanel implements CardInterface {
    
    static final int V_GAP  = 50;
    static final int H_GAP  = 50;
    static final int BORDER_TOP    = 25;
    static final int BORDER_LEFT   = 25;
    static final int BORDER_BOTTOM = 25;
    static final int BORDER_RIGHT  = 25;
    
    private JPanel langPanel;
    private JPanel userPanel;
    private JPanel devicePanel;
    private JPanel projectPanel;
    private JPanel updatesPanel;
                
    private JLabel langLabel;
    private JLabel userLabel;
    private JLabel deviceLabel;
    private JLabel projectLabel;
    private JLabel projectDescLabel;
    private JLabel updatesLabel;
    
    private JComboBox langCBox;
    private JComboBox userCBox;
    private JComboBox deviceCBox;
    private DefaultComboBoxModel langCBoxModel;
    private DefaultComboBoxModel userCBoxModel;
    private DefaultComboBoxModel deviceCBoxModel;
    /* This listener is stored locally because we need to remove and readd it 
    *  to the CBox to avoid fireing of events when updating Cbox elements
    */
    private ItemListener         deviceCBOxItemListener;
    private JButton   projectButton;
    private JCheckBox updatesCBox;
    private JButton   updatesButton;
    
    private JButton infoButton;
    private JPanel infoPanel;
    private JLabel infoLabel;

    public CardSettings() {
        super();
        
        initLanguage();
        initUserMode();
        initDevice();
        initProject();
        initUpdates();
        initInfo();
        
        //........................................................... Left Panel
        JPanel innerLeft = new JPanel(){{
            setBorder(BorderFactory.createEmptyBorder(
                        BORDER_TOP,
                        BORDER_LEFT,
                        BORDER_BOTTOM,
                        BORDER_RIGHT));     
        }};
        
        GroupLayout layoutLeft   = new GroupLayout(innerLeft);
        layoutLeft.setHorizontalGroup(
            layoutLeft.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(userPanel)
                .addComponent(langPanel)
                .addComponent(projectPanel)
        );
        layoutLeft.setVerticalGroup(
            layoutLeft.createSequentialGroup()
                .addComponent(userPanel)
                .addGap(H_GAP)
                .addComponent(langPanel)
                .addGap(H_GAP)
                .addComponent(projectPanel)
        );
        innerLeft.setLayout(layoutLeft);
        
        //...........................................................Right Panel
        JPanel innerRight = new JPanel(){{
            setBorder(BorderFactory.createEmptyBorder(
                        BORDER_TOP,
                        BORDER_LEFT,
                        BORDER_BOTTOM,
                        BORDER_RIGHT)); 
        }};
        
        GroupLayout layoutRight   = new GroupLayout(innerRight);
        layoutRight.setHorizontalGroup(
            layoutRight.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(devicePanel)
                .addComponent(updatesPanel)
                .addComponent(infoPanel)
        );
        layoutRight.setVerticalGroup(
            layoutRight.createSequentialGroup()
                .addComponent(devicePanel)
                .addGap(H_GAP)
                .addComponent(updatesPanel)
                .addGap(H_GAP)
                .addComponent(infoPanel)
        );
        innerRight.setLayout(layoutRight);
        
        //................................................................Global
        JSeparator separator        = new MySeparator(SwingConstants.VERTICAL);
        GroupLayout LayoutGlobal    = new GroupLayout(this);
        LayoutGlobal.setHorizontalGroup(LayoutGlobal.createSequentialGroup()
                .addComponent(innerLeft,  0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(innerRight,  0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        LayoutGlobal.setVerticalGroup(LayoutGlobal.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(innerLeft)
                .addComponent(separator)
                .addComponent(innerRight)
        );
        setLayout(LayoutGlobal);
       
        setBorder(
                BorderFactory.createMatteBorder(
                        BORDER_TOP,
                        BORDER_LEFT,
                        BORDER_BOTTOM,
                        BORDER_RIGHT,
                        MyLookAndFeel.MYGREEN)
        );
    }
    
    private void initInfo(){
        infoPanel       = new JPanel();
        infoButton      = new MyButton("Info", MyButtonKind.BIG_RECTANGULAR.toString());
        infoLabel       = new MyLabelBigRed();     

        GroupLayout l   = new GroupLayout(infoPanel);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(infoLabel)
                .addComponent(infoButton)
        );

        l.setVerticalGroup(l.createSequentialGroup()
                .addComponent(infoLabel)
                .addComponent(infoButton)
        );
        infoPanel.setLayout(l);
    }
    
    
    private void initLanguage() {
        langPanel       = new JPanel();
        langLabel       = new MyLabelBigRed();     
        langCBoxModel   = new DefaultComboBoxModel(Globals.ENTRIES_LANGUAGES);
        langCBox        = new MyComboBox(langCBoxModel);          
        
        GroupLayout l   = new GroupLayout(langPanel);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(langLabel)
                .addComponent(langCBox)
        );

        l.setVerticalGroup(l.createSequentialGroup()
                .addComponent(langLabel)
                .addComponent(langCBox, MyButton.SMALL_SQUARED_HEIGHT_NOB, MyButton.SMALL_SQUARED_HEIGHT_NOB, MyButton.SMALL_SQUARED_HEIGHT_NOB)  
        );
        langPanel.setLayout(l);
    }
    
    private void initUserMode() {
        userPanel       = new JPanel();
        userLabel       = new MyLabelBigRed();     
        userCBoxModel   = new DefaultComboBoxModel();
        userCBox        = new MyComboBox(userCBoxModel);  
        
        GroupLayout l   = new GroupLayout(userPanel);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(userLabel)
                .addComponent(userCBox)
        );

        l.setVerticalGroup(l.createSequentialGroup()
                .addComponent(userLabel)
                .addComponent(userCBox, MyButton.SMALL_SQUARED_HEIGHT_NOB, MyButton.SMALL_SQUARED_HEIGHT_NOB, MyButton.SMALL_SQUARED_HEIGHT_NOB)  
        );
        userPanel.setLayout(l);
        
        // Stakeholders 
        userCBoxModel.addElement("usermode_normal");
        userCBoxModel.addElement("usermode_advanced");
        // Fetch the starting usermode
        userCBox.setSelectedIndex(SettingsManager.getUserMode());
    }
    
    private void initDevice() {
        devicePanel       = new JPanel(){{
        }};
        deviceLabel       = new MyLabelBigRed();     
        deviceCBoxModel   = new DefaultComboBoxModel();
        deviceCBox        = new MyComboBox(deviceCBoxModel);  
        
        GroupLayout l   = new GroupLayout(devicePanel);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(deviceLabel)
                .addComponent(deviceCBox)
        );

        l.setVerticalGroup(l.createSequentialGroup()
                .addComponent(deviceLabel)
                .addComponent(deviceCBox, MyButton.SMALL_SQUARED_HEIGHT_NOB, MyButton.SMALL_SQUARED_HEIGHT_NOB, MyButton.SMALL_SQUARED_HEIGHT_NOB)  
        );
        devicePanel.setLayout(l);
    }
      
    private void initProject() {
        projectPanel       = new JPanel();
        projectLabel       = new MyLabelBigRed();     
        projectDescLabel   = new JLabel();
        projectButton      = new MyButton(MyButtonKind.BIG_RECTANGULAR.toString(),
            new ImageIcon(getClass().getResource("/resources/gui/icons/icon-folder.png")));

        GroupLayout l   = new GroupLayout(projectPanel);
        l.setHorizontalGroup(
            l.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(projectLabel)
                .addGroup(
                    l.createSequentialGroup()
                        .addComponent(projectDescLabel)
                        .addGap(H_GAP)
                        .addComponent(projectButton)
                )
        );

        l.setVerticalGroup(
            l.createSequentialGroup()
                .addComponent(projectLabel)
                .addGroup(
                    l.createParallelGroup(GroupLayout.Alignment.CENTER) 
                        .addComponent(projectDescLabel)
                        .addComponent(projectButton)  
                )
        );
        projectPanel.setLayout(l);      
    }
        
    private void initUpdates() {
        updatesPanel       = new JPanel(){{
        }};
        updatesLabel       = new MyLabelBigRed();     
        updatesCBox        = new JCheckBox();
        updatesCBox.setFocusable(false);
        updatesCBox.setHorizontalTextPosition(SwingConstants.LEFT);
//        updatesButton      = new MyButton(MyButtonKind.BIG_RECTANGULAR.toString(),
//            new ImageIcon(getClass().getResource("/resources/gui/icons/icon-folder.png")));
//        updatesButton.setEnabled(false);
        
        GroupLayout l   = new GroupLayout(updatesPanel);
        l.setHorizontalGroup(
            l.createSequentialGroup()
                .addGroup(
                    l.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(updatesLabel)
                    .addComponent(updatesCBox)
                )
//            .addComponent(updatesButton)     
        );

        l.setVerticalGroup(
            l.createSequentialGroup()
                .addComponent(updatesLabel)
                .addGroup(
                    l.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(updatesCBox)  
//                    .addComponent(updatesButton)  
                )
        );
        updatesPanel.setLayout(l);
    }
        
    public void setValues(int userMode, Device d, String projectPath, boolean isTimerActive, boolean checkUpdates, ResourceBundle rb) {
        if (projectPath == null) projectPath = ". . .";
        else {
            int l = projectPath.length() > 35 ? projectPath.length()-35 : 0;
            projectPath = projectPath.substring(l);
            projectPath = "..."+projectPath;
        }
        projectDescLabel.setText(projectPath);
        userCBox.setSelectedIndex(userMode);            
        
        if (isTimerActive) {
            deviceLabel.setEnabled(false);
            deviceCBox.setEnabled(false);
            projectButton.setEnabled(false);
            projectDescLabel.setEnabled(false);
            projectLabel.setEnabled(false);
        } else {
            deviceLabel.setEnabled(true);
            deviceCBox.setEnabled(true);
            projectButton.setEnabled(true);
            projectDescLabel.setEnabled(true);
            projectLabel.setEnabled(true);
        }
        
        // Select the current language 
        int index = Arrays.asList(Globals.ENTRIES_LANGUAGES_CODES).indexOf(rb.getLocale().getLanguage());
        if ( index < 0 ) index = 0;
        langCBox.setSelectedIndex(index);
        
        // Update the device list and set the current device
        List<String> supportedDevice    = v4l4jMyUtils.searchSupportedDevices(false);
        List<String> supportedDeviceNice= v4l4jMyUtils.searchSupportedDevicesNiceNames();
        
        // Remove the item listener to avoid artifical fireing of events
        deviceCBox.removeItemListener(deviceCBOxItemListener);
        deviceCBoxModel.removeAllElements();
        
        // Add supported device
        for (String i : supportedDeviceNice) {
            deviceCBoxModel.addElement(i);
        }
        
        // Select current device
        index = supportedDevice.indexOf(d.getDeviceFile());
        if ( index < 0 ) index = 0;
        deviceCBox.setSelectedIndex(index);
        
        // Re-add the item listener
        deviceCBox.addItemListener(deviceCBOxItemListener);
        
        // Set checkupdates
        updatesCBox.setSelected(checkUpdates);
    }
    
    @Override
    public void adaptToUser(int user) {
        userCBox.setSelectedIndex(user);
    }

    @Override
    public void adaptToLanguage(ResourceBundle rb) {
//        Utils.print("CardSettings::adaptToLanguage() Adapting to language "+rb.getLocale().getLanguage());

        infoLabel.setText(rb.getString("infoLabel"));
        langLabel.setText(rb.getString("langLabel"));
        userLabel.setText(rb.getString("userLabel"));
        deviceLabel.setText(rb.getString("deviceLabel"));
        projectLabel.setText(rb.getString("projectLabel"));
        updatesLabel.setText(rb.getString("updatesLabel"));
        updatesCBox.setText(rb.getString("updatesCBox"));
        
        // Saves the selected index to reset it after
        int i = userCBox.getSelectedIndex();

        // At the beginning if there are no entries the index gets value -1
        if (i < 0) i = 0;
        userCBoxModel.removeAllElements();
        
        userCBoxModel.addElement(rb.getString("usermode_normal"));
        userCBoxModel.addElement(rb.getString("usermode_advanced"));
        // Reset the selected element
        userCBox.setSelectedIndex(i);
    }

    @Override
    public void adaptToDevice(Device d) {
    }

    @Override
    public void adaptToFrozen(boolean isFrozen, int mode) {
    }

    @Override
    public List<JLabel> getHelpLabels(ResourceBundle rb, JComponent container) {
        List<JLabel> l = new ArrayList<JLabel>() {};
//        if (userPanel.isShowing()) LayoutGlobal.add(new MyHelpLabel(userPanel, container, rb.getString("TltTimelapsStopBtn"),     MyHelpLabel.ORIENTATION_RIGHT,  200));
        return l;
    }
    
    /* Getters and setters */
    public int getUserModeIndex () {
        return userCBox.getSelectedIndex();
    }

    /* Listeners */
    public void addLanguageListener(ItemListener il) {
        langCBox.addItemListener(il);
    }

    public void addUserModeListener(ItemListener il) {
        userCBox.addItemListener(il);
    }
    
    public void addDeviceListener(ItemListener il) {
        // stores locally this listener
        deviceCBOxItemListener = il;
        deviceCBox.addItemListener(il);
    }
    
    public void addProjectListener(ActionListener al) {
        projectButton.addActionListener(al);
    }
    
    public void addCheckUpdatesListener(ActionListener al){
        updatesCBox.addActionListener(al);
    }
    
    public void addAboutListener(ActionListener al) {
        infoButton.addActionListener(al);
    }
}

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

/*
DISCLAMER THIS CODE WAS MADE IN A HURRY, MIGHT RIQUIRE SOME REFACTORING ;)
 */
package jmicro.gui.mvc.firstStart;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import jmicro.Globals;
import jmicro.SettingsManager;
import jmicro.gui.components.MyLabelBigRed;
import jmicro.gui.components.MyComboBox;
import jmicro.gui.components.MyButton;
import jmicro.gui.components.MyButtonKind;
import jmicro.gui.components.MyLabelRed;
import jmicro.utils.Misc;
import jmicro.utils.MyLookAndFeel;


/**
 * View used in the First Start MVC.
 */
public class FSView extends JDialog {
    
    static final int BORDER_TOP    = 20;
    static final int BORDER_LEFT   = 20;
    static final int BORDER_BOTTOM = 20;
    static final int BORDER_RIGHT  = 20;
    
    
    //...............................................................Main Panels

    private final JLabel titleLabel;
    
    private JPanel langPanel;  
    private JLabel langLabel;
    private JComboBox langCBox;
    private DefaultComboBoxModel langCBoxModel;
    
    private JPanel userPanel;  
    private JLabel userLabel;
    private JComboBox userCBox;
    private DefaultComboBoxModel userCBoxModel;
    
    private JLabel infoLabel;
    private JPanel infoPanel;        
    
    private JPanel    updatesPanel;
    private JCheckBox updatesCBox;
    
    private final JPanel btnPanel;
    private final JButton startBtn;
    
    private final JPanel inner_panel;
    /*-------------------------------------------------------------------------+
     |                              Constructor                                |
     * -----------------------------------------------------------------------*/
    /**
     * Creates a View of the of the main MVC architecture.
     * In this MVC architecture the view doesn't know the model. 
     * It's the controller that creates and queries the model.
     */
    public FSView() {
        // Makes the dialog modal
        super(new JFrame(), true);
        
        //............................................... Initializes components
        titleLabel = new MyLabelBigRed();
        
        initLanguage();
        initUserMode();
        initInfos();
        initUpdates();

        btnPanel = new JPanel();
        startBtn = new MyButton (MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-check_resized.png")));
        btnPanel.add(startBtn);
        //.........................................................Content Panel
        
        inner_panel = new JPanel() {{
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                    BORDER_TOP, 
                    BORDER_LEFT,
                    BORDER_BOTTOM,
                    BORDER_RIGHT, 
                    MyLookAndFeel.MYGREEN),
                    BorderFactory.createEmptyBorder(
                    BORDER_TOP, 
                    BORDER_LEFT,
                    BORDER_BOTTOM,
                    BORDER_RIGHT)));
        }};
        
        GroupLayout layout = new GroupLayout(inner_panel);
        layout.setAutoCreateGaps(true);
        layout.setHonorsVisibility(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addGroup(layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(langPanel)
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(userPanel)
                            .addGap(0, 0, Short.MAX_VALUE)
                    )
                    .addComponent(infoPanel)
                    .addComponent(updatesPanel)
                    .addComponent(btnPanel)
                )
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(titleLabel)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
                .addComponent(langPanel)
                .addComponent(userPanel)
            )
            .addComponent(infoPanel, 20, 20, Short.MAX_VALUE)
            .addComponent(updatesPanel)
            .addComponent(btnPanel) 
            .addGap(0, 0, Short.MAX_VALUE)
        );
        inner_panel.setLayout(layout);
        
        //...................................................... finalize layout
        this.setMinimumSize(new Dimension(MyLookAndFeel.FS_WINDOWS_WIDTH,MyLookAndFeel.FS_WINDOWS_HEIGHT));
        this.setResizable(false);
        this.setContentPane(inner_panel);
        this.pack();
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
    
    private void initInfos() {
        infoLabel = new MyLabelRed();
        infoPanel = new JPanel ();
        infoPanel.add(infoLabel);
    }
    
    private void initUpdates() {
        updatesCBox = new JCheckBox() {{
           setFocusable(false);
           setHorizontalTextPosition(SwingConstants.RIGHT);
           setSelected(SettingsManager.getCheckUpdate());
        }};
        updatesPanel = new JPanel();
        updatesPanel.add(updatesCBox);
    }
    
    /*-------------------------------------------------------------------------+
     |                      Methods that changes the view                      |
     +------------------------------------------------------------------------*/
    /**
     * Updates the GUI to the current language.
     * @param rb The resource bundle from where retrieve the keys to use.
     */
    public void adaptToLanguage(ResourceBundle rb) {
        titleLabel.setText(rb.getString("titleLabel"));
        langLabel.setText(rb.getString("langLabel"));
        userLabel.setText(rb.getString("userLabel"));
        startBtn.setText(rb.getString("startBtn"));
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
        
        String s = "";
        // If not normal the label will be setted to ""
        if (getUserModeIndex() == 0 /* normal */) {
            s = rb.getString(("infoLabel"));
        }
                
        setTextToInfo(s);
    }    
    
    public int getUserModeIndex () {
        return userCBox.getSelectedIndex();
    }
    
    public void setTextToInfo(String s){
        infoLabel.setText(Misc.wrapStringCentered(s));

    }
    /*-------------------------------------------------------------------------+
     |                      Add Listener Methods                               |
     +------------------------------------------------------------------------*/
    public void addStartListener(ActionListener al) {
        startBtn.addActionListener(al);
    }

    public void addLanguageListener(ItemListener il) {
        langCBox.addItemListener(il);
    }

    public void addUserModeListener(ItemListener il) {
        userCBox.addItemListener(il);
    }
    
    public void addCheckUpdatesListener(ActionListener al){
        updatesCBox.addActionListener(al);
    }
}
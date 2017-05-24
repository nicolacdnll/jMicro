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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;


import jmicro.gui.components.MyLabelBigRed;
import jmicro.gui.components.MyComboBox;
import jmicro.gui.components.MyHelpLabel;
import jmicro.gui.components.MyButton;
import jmicro.gui.components.MyButtonKind;
import jmicro.gui.components.MyLabelRed;
import jmicro.gui.components.MySeparator;
import jmicro.gui.componentsUI.MyScrollBarUI;
import jmicro.gui.componentsUI.MyVerticalGradientBorder;
import jmicro.gui.renderers.DirectoriesListRenderer;
import jmicro.utils.Misc;
import jmicro.utils.MyIO;
import jmicro.utils.MyLookAndFeel;
import jmicro.v4l4jutils.Device;

/**
 * JPanel card that contains project selection in main view.
 */
public class CardProject extends JPanel implements CardInterface { 

    static final int BORDER_TOP    = 0;
    static final int BORDER_LEFT   = 25;
    static final int BORDER_BOTTOM = 25;
    static final int BORDER_RIGHT  = 25;
    
    static final int LEFT_PANEL_BORDER_TOP    = 0;
    static final int LEFT_PANEL_BORDER_LEFT   = 25;
    static final int LEFT_PANEL_BORDER_BOTTOM = 25;
    static final int LEFT_PANEL_BORDER_RIGHT  = 20;
    
    static final int RIGHT_PANEL_BORDER_TOP    = 0;
    static final int RIGHT_PANEL_BORDER_LEFT   = 0;
    static final int RIGHT_PANEL_BORDER_BOTTOM = 0;
    static final int RIGHT_PANEL_BORDER_RIGHT  = 0;

    static final int RIGHT_TOP_PANEL_BORDER_TOP    = 0;
    static final int RIGHT_TOP_PANEL_BORDER_LEFT   = 20; // =LEFT_PANEL_BORDER_RIGHT
    static final int RIGHT_TOP_PANEL_BORDER_BOTTOM = 0;
    static final int RIGHT_TOP_PANEL_BORDER_RIGHT  = 25;
    
    private final int DIRECTORIES_BUTTONS_GAP  = 20;
//    private final int LEFT_PANEL_WIDTH         = DIRECTORIES_BUTTONS_GAP * 2
//                                                 + MyButton.BIG_RECTANGULAR_WIDTH * 3 
//                                                 + LEFT_PANEL_BORDER_RIGHT
//                                                 + LEFT_PANEL_BORDER_RIGHT;
    
    // Components
    
    // Left side
    private JLabel selectProjectLabel;
    
    private DefaultComboBoxModel directoriesComboBoxModel;
    private JComboBox   directoriesComboBox;
    private JButton     goToUpperLevelButton;
    private JPanel      navigationPanel;
    
    private JButton goToHomeButton;
    private JButton goToDesktopButton;
    private JButton goToProjectsButton;
    private JPanel  goToButtonsContainerPanel;
    
    private DefaultListModel    directoriesListModel;
    private JScrollPane         directoriesScrollPane;
    private JList               directoriesList;
    
    // Right side
    private JPanel      createNewProjectTFPanel;
    
    private JLabel      createNewProjectLabel;
    
    private JPanel      createNewProjectPanel;
    private JButton     createNewProjectButton;
    private JTextField  createNewProjectTextField;
    private JLabel      createNewProjectMsgLabel;
    private JLabel      startLabel;
    private JButton     startButton;
    
    private JPanel leftPanel;
    private JPanel rightPanel;  

    
    public CardProject() {
        super();
        initComponents();

        GroupLayout l = new GroupLayout(this);
        JSeparator s = new MySeparator(SwingConstants.VERTICAL);
        l.setHorizontalGroup(l.createSequentialGroup()
                .addComponent(leftPanel,        GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(s, MySeparator.thickness, MySeparator.thickness, MySeparator.thickness)
                .addComponent(rightPanel,       GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );
        l.setVerticalGroup(l.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(leftPanel,    GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(s)
                        .addComponent(rightPanel,   GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                 
        );
        setLayout(l);
        
        setBorder(BorderFactory.createMatteBorder(
                        BORDER_TOP,
                        BORDER_LEFT,
                        BORDER_BOTTOM,
                        BORDER_RIGHT,
                        MyLookAndFeel.MYGREEN)
        );
    }
    
    private void initComponents() {
        initLeftPanel ();
        initRightPanel ();     
    }
    
    private void initLeftPanel () {
         selectProjectLabel          = new MyLabelBigRed ();
  
        directoriesComboBoxModel    = new DefaultComboBoxModel(new String[]{});
        directoriesComboBox         = new MyComboBox(directoriesComboBoxModel);

        JPanel directoriesCBPanel = new JPanel(new GridLayout(0,1)){{
            add(directoriesComboBox);
        }};
        
        goToUpperLevelButton = new MyButton(MyButtonKind.SMALL_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-up.png")));
        navigationPanel = new JPanel(){{
            setBorder(null);
        }};
        GroupLayout navigationLayout = new GroupLayout(navigationPanel);
        navigationLayout.setHorizontalGroup(navigationLayout.createSequentialGroup()
                    .addComponent(directoriesCBPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(5)
                    .addComponent(goToUpperLevelButton)
        );
        navigationLayout.setVerticalGroup(navigationLayout.createSequentialGroup()
                .addGroup(navigationLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(directoriesCBPanel,MyButton.SMALL_SQUARED_HEIGHT_NOB,MyButton.SMALL_SQUARED_HEIGHT_NOB,MyButton.SMALL_SQUARED_HEIGHT_NOB)
                    .addComponent(goToUpperLevelButton)
                )
        );       
        navigationPanel.setLayout(navigationLayout);
        
        goToHomeButton = new MyButton(
                MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-home.png")));
        goToDesktopButton = new MyButton(
                MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-desktop.png")));
        goToProjectsButton = new MyButton(
                MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-folder.png")));
        goToButtonsContainerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)){{
            setBorder(null);

        }};        

        GroupLayout directoriesButtonsLayout= new GroupLayout(goToButtonsContainerPanel);
        directoriesButtonsLayout.setHorizontalGroup(directoriesButtonsLayout.createSequentialGroup()
                    .addComponent(goToHomeButton)
                    .addGap(DIRECTORIES_BUTTONS_GAP, DIRECTORIES_BUTTONS_GAP, Short.MAX_VALUE)
                    .addComponent(goToDesktopButton)
                    .addGap(DIRECTORIES_BUTTONS_GAP, DIRECTORIES_BUTTONS_GAP, Short.MAX_VALUE)
                    .addComponent(goToProjectsButton)
        );
        directoriesButtonsLayout.setVerticalGroup(directoriesButtonsLayout.createSequentialGroup()
                .addGroup(directoriesButtonsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(goToHomeButton)
                    .addComponent(goToDesktopButton)
                    .addComponent(goToProjectsButton)
                )
        );
        goToButtonsContainerPanel.setLayout(directoriesButtonsLayout);
        
        
        
        directoriesListModel = new DefaultListModel();
        directoriesList = new JList(){{
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
            setModel(directoriesListModel);
        }};
        directoriesList.setCellRenderer(new DirectoriesListRenderer());
        directoriesScrollPane = new JScrollPane(directoriesList) {{
            setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            setBorder(new MyVerticalGradientBorder());
            getVerticalScrollBar().setUI(new MyScrollBarUI());
            getVerticalScrollBar().setBackground(Color.WHITE);
            
        }};
       
        leftPanel = new JPanel(){{ 
            setBorder(BorderFactory.createEmptyBorder(
                        LEFT_PANEL_BORDER_TOP,
                        LEFT_PANEL_BORDER_LEFT,
                        LEFT_PANEL_BORDER_BOTTOM,
                        LEFT_PANEL_BORDER_RIGHT)
                );
        }}; 
        
        GroupLayout projectLeftLayout = new GroupLayout(leftPanel);
        projectLeftLayout.setHorizontalGroup(projectLeftLayout.createSequentialGroup()
                .addGroup(projectLeftLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(selectProjectLabel,           GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(navigationPanel,              GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(goToButtonsContainerPanel,    GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addComponent(directoriesScrollPane,        GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                )
        );
        int h = MyButton.SMALL_SQUARED_HEIGHT+5;
        projectLeftLayout.setVerticalGroup(projectLeftLayout.createSequentialGroup()
            .addComponent(selectProjectLabel,           h, h, h)
            .addComponent(navigationPanel,              h, h, h)
            .addComponent(goToButtonsContainerPanel,    h, h, h)
            .addComponent(directoriesScrollPane,        h, h, Short.MAX_VALUE)
        );
        leftPanel.setLayout(projectLeftLayout);     
    }
    
    private void initRightPanel () {
        createNewProjectLabel  = new MyLabelBigRed ();
        createNewProjectButton = new MyButton (
            MyButtonKind.SMALL_SQUARED.toString(),
            new ImageIcon(getClass().getResource("/resources/gui/icons/icon-newproject.png")));
        
        createNewProjectTextField = new JTextField() {{ 
            setBorder(new MyVerticalGradientBorder());
        }};
        createNewProjectTFPanel = new JPanel(new GridLayout(0,1)){{
            setBorder(null);
            add(createNewProjectTextField);
            setPreferredSize(new Dimension(100,MyButton.SMALL_SQUARED_HEIGHT-10));
        }};
        createNewProjectMsgLabel = new MyLabelRed("");
                
        createNewProjectPanel = new JPanel(){{
            setBorder(BorderFactory.createEmptyBorder(
                        RIGHT_TOP_PANEL_BORDER_TOP,
                        RIGHT_TOP_PANEL_BORDER_LEFT,
                        RIGHT_TOP_PANEL_BORDER_BOTTOM,
                        RIGHT_TOP_PANEL_BORDER_RIGHT)
                );
        }};
        
        GroupLayout createNewProjectLayout = new GroupLayout(createNewProjectPanel);
        createNewProjectLayout.setHorizontalGroup(
                createNewProjectLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(createNewProjectLabel)
                    .addGroup(createNewProjectLayout.createSequentialGroup()
                        .addComponent(createNewProjectTFPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGap(5)
                        .addComponent(createNewProjectButton)
                    )
                    .addComponent(createNewProjectMsgLabel,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE)
        );
        
        int h = MyButton.SMALL_SQUARED_HEIGHT+5;
        
        createNewProjectLayout.setVerticalGroup(createNewProjectLayout.createSequentialGroup()
                .addComponent(createNewProjectLabel,h,h,h)
                .addGroup(createNewProjectLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(createNewProjectTFPanel,MyButton.SMALL_SQUARED_HEIGHT_NOB,MyButton.SMALL_SQUARED_HEIGHT_NOB,MyButton.SMALL_SQUARED_HEIGHT_NOB)
                    .addComponent(createNewProjectButton)
                )
                .addComponent(createNewProjectMsgLabel,h,h,h)
        );   
        createNewProjectPanel.setLayout(createNewProjectLayout); 
        
        //...........................................................RIGHTBOTTOM       
        
        startButton     = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-microscope.png")));
        startLabel      = new MyLabelBigRed();
        
        
        rightPanel = new JPanel() {{
            setBorder(BorderFactory.createEmptyBorder(
                        RIGHT_PANEL_BORDER_TOP,
                        RIGHT_PANEL_BORDER_LEFT,
                        RIGHT_PANEL_BORDER_BOTTOM,
                        RIGHT_PANEL_BORDER_RIGHT)
                );
        }};
                
        JSeparator separator = new MySeparator(SwingConstants.HORIZONTAL);
        GroupLayout selectProjectRightLayout = new GroupLayout(rightPanel);
        selectProjectRightLayout.setHorizontalGroup(selectProjectRightLayout.createSequentialGroup()
                .addGroup(selectProjectRightLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(createNewProjectPanel)
                    .addComponent(separator)
                    .addComponent(startButton)
                    .addComponent(startLabel)
                )   
        );
        selectProjectRightLayout.setVerticalGroup(selectProjectRightLayout.createSequentialGroup()
                .addComponent(createNewProjectPanel)
                .addComponent(separator)
                .addComponent(startButton)
                .addGap(20)
                .addComponent(startLabel) 
                .addGap(20,20,Short.MAX_VALUE)
        );   
        rightPanel.setLayout(selectProjectRightLayout); 
    }
    
    /* Listeners */
    
    public void addDirectoriesComboItemListerner(ItemListener il){
        directoriesComboBox.addItemListener(il);
    }
              
    public void addUpLevelDirectoryListener(ActionListener al) {
        goToUpperLevelButton.addActionListener(al);
    }
    
    public void addHomeDirectoryListener(ActionListener al) {
        goToHomeButton.addActionListener(al);
    }
    
    public void addDesktopDirectoryListener(ActionListener al) {
        goToDesktopButton.addActionListener(al);
    }
        
    public void addWorkspaceDirectoryListener(ActionListener al) {
        goToProjectsButton.addActionListener(al);
    }
        
    public void addNewProjectListener(ActionListener al) {
        createNewProjectButton.addActionListener(al);
    }
    
    public void addNewProjectTextFieldDocumentListener(DocumentListener dl) {
        this.createNewProjectTextField.getDocument().addDocumentListener(dl);
    }
        
    public void addStartListener(ActionListener al) {
        startButton.addActionListener(al);
    }    
  
    public void addDirectoriesListListeners (MouseListener ml, ListSelectionListener lsl){
        directoriesList.addMouseListener(ml);
        directoriesList.addListSelectionListener(lsl);
    } 
    
    public void setNavigationDirectory (String path) {
        // The method removeAllElements() is not used because triggers the 
        // ItemListener. As workaround we add the new elements and then remove
        // remove the previous.
        
        int size = directoriesComboBoxModel.getSize();
        String[] dirs = MyIO.getUpDirs(path, false);
        
        // Add new elements at the end
        for (String s: dirs){
            directoriesComboBoxModel.addElement(s);
        }
        
        // Remove previous elements
        for (int i = 0; i < size; i++) {
            directoriesComboBoxModel.removeElementAt(0);
        }
        
        // Select last element
        directoriesComboBox.setSelectedIndex(directoriesComboBoxModel.getSize()-1);      
    }
 
    public void setNavigationItem (Object o) {
        directoriesComboBoxModel.setSelectedItem(o);
    }
     
    public void setPreviousNavigationItem () {
        int i = directoriesComboBoxModel.getIndexOf(directoriesComboBoxModel.getSelectedItem());
        
        if (i != 0) {
            directoriesComboBoxModel.setSelectedItem(
                directoriesComboBoxModel.getElementAt(i-1)
            ); 
        }
    }
    
    public void setDirectoriesListItems (String [] items){
        directoriesListModel.clear();
        for (String s : items) directoriesListModel.addElement(s);
        directoriesList.setSelectedIndex(0);
    }
       
    public String getDirectoryListSelectedItem (){
        int index = directoriesList.getSelectedIndex();
        if (index != -1)
            return (String) directoriesListModel.getElementAt(index);
        else
            return null;
    }
    
    public String getDirectoryComboBoxSelectedItem (){
        // Must be checked that something is selected
        if (this.directoriesComboBox.getSelectedIndex() < 0 )
            return "";
        else 
            return directoriesComboBoxModel.getSelectedItem().toString();
    }
    
    public String getNewProjectText () {
        return createNewProjectTextField.getText();
    }
    
    public void updateNavigationDirectoryList(){
        this.setDirectoriesListItems (MyIO.getSubDirs(this.directoriesComboBoxModel.getSelectedItem().toString()));
    }
    
    public boolean selectItemNavigationDirectoryList(String s) {
        int i;
        boolean ret_value;

        if ((i = this.directoriesListModel.indexOf((Object)s)) < 0) {
            //It should never happen!
            ret_value = false;
        } else {
            ret_value = true;
            directoriesList.setSelectedIndex(i);
        }
        return ret_value;
    }
    
    private void setProjectCreationBorder(Border b){
        createNewProjectTextField.setBorder(b);
    }
    
    public void SetProjectCreationBorderNormal(){
        if (createNewProjectTextField.hasFocus())
            setProjectCreationBorder(BorderFactory.createLineBorder(Color.BLUE));
        else
            setProjectCreationBorder(new MyVerticalGradientBorder(1,1,1,1));
    }
        
    public void SetProjectCreationBorderError(){
        setProjectCreationBorder(BorderFactory.createLineBorder(Color.RED));
    }
    
    /**
     * Enables/disables all the component of the project selection section used to create a new project.
     * @param flag 
     */
    public void setEnableProjectCreationComponents(boolean flag) {
       setEnabledCreateNewProjectButton(flag);
       createNewProjectTextField.setEditable(flag);
    }
    
    public void setEnabledCreateNewProjectButton (boolean flag) {
        createNewProjectButton.setEnabled(flag);
    }   
    
    /**
     * Shows an error in the project selection section.
     * @param s The string of the error
     */
    public void setProjectCreationError(String s) {
        createNewProjectMsgLabel.setText(Misc.wrapString(s));
    }
        

    public void setEnabledStartButton (boolean flag) {
        this.startButton.setEnabled(flag);
    }
    
    public void setSelectedStartButton () {
        this.startButton.setFocusPainted(true); 
    }
    
    public void deleteNewProjectTextField(){
        this.createNewProjectTextField.setText("");
    }
    
    /* Interface */
    @Override
    public void adaptToUser(int user) {
    }

    @Override
    public void adaptToLanguage(ResourceBundle rb) {
        goToHomeButton.setText(rb.getString("BtnHome"));
        goToDesktopButton.setText(rb.getString("BtnDesktop"));
        goToProjectsButton.setText(rb.getString("BtnProjects"));
        selectProjectLabel.setText(rb.getString("LblSelectProject"));
        createNewProjectLabel.setText(rb.getString("LblNewProject"));
        startLabel.setText(rb.getString("LblStartProgram"));
    }

    @Override
    public void adaptToDevice(Device d) {
        // No changes required
    }

    @Override
    public void adaptToFrozen(boolean isFrozen, int mode) {
        // No changes required
    }

    @Override
    public List<JLabel> getHelpLabels(ResourceBundle rb, JComponent container) {
        List<JLabel> l = new ArrayList<JLabel>() {};
        if (createNewProjectButton.isShowing()) l.add(new MyHelpLabel(createNewProjectButton, container, rb.getString("TltNewProjectBtn"),   MyHelpLabel.ORIENTATION_RIGHT,  150));
        if (directoriesScrollPane.isShowing()) l.add(new MyHelpLabel(directoriesScrollPane, container, rb.getString("TltDirectoriesScrlP"), MyHelpLabel.ORIENTATION_CENTER, 300));            
        return l;  
    }
    
}

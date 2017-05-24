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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import jmicro.SettingsManager;

import jmicro.gui.components.MyHelpLabel;
import jmicro.gui.components.MyButton;
import jmicro.gui.components.MyButtonKind;
import jmicro.gui.components.MySlider;
import jmicro.gui.components.MyToggleButton;
import jmicro.gui.renderers.ThumbnailsListRenderer;
import jmicro.gui.components.MyLabel;
import jmicro.gui.componentsUI.MyScrollBarUI;
import jmicro.gui.components.panels.JPanelWithBackground;
import jmicro.gui.components.panels.NinePatchLikePanel;
import jmicro.gui.components.panels.VideoScreenPanel;
import jmicro.utils.MyLookAndFeel;

import jmicro.v4l4jutils.Device;

/**
 * JPanel card that contains the main view.
 */
public class CardMain extends JPanel implements CardInterface {

   
    private static final int THUMBS_SCROLL_HEIGHT   = 155;
    private static final int BOTTOM_PANEL_HEIGHT    = THUMBS_SCROLL_HEIGHT + 12;
        
    public static final int minVideoWidth = (int) ((int) 352 * 1.5);
    public static final int minVideoHeight = (int) ((int) 288 * 1.5);
    
    public final Dimension minVideoContainerSize   = new Dimension(minVideoWidth + 90,  minVideoHeight + 70);

    //This parameters depend on frame_images for the frame
    private final int offset_screen_x = 45, offset_screen_y = 35;
    
    private final int extraOffset           = 8;
    private final int videoPanelShadowLeft  = 10;
    private final int videoPanelShadowRight = 16;
    
    private final int sidePanelShadowRightSide          = 18;
    private final int sidePanelShadowLeftSide           = 12;
    private final int sidePanelGapToBeCoveredRightSide  = 40;
    private final int sidePanelGapToBeCoveredLeftSide   = 34;
    
    private final int leftPanelGapForCenterContent  = ((int) (1.5 * sidePanelGapToBeCoveredRightSide))
                                                    - sidePanelShadowLeftSide
                                                    - extraOffset;
    private final int rightPanelGapForCenterContent = ((int) (1.5 * sidePanelGapToBeCoveredLeftSide)) 
                                                    - sidePanelShadowRightSide 
                                                    - extraOffset;
    
    private final Dimension minContentSize          = new Dimension(MView.minMiddlePanelSize.width,
        MView.TOP_PANEL_HIGHT              + 
        MView.minMiddlePanelSize.height   + 
        BOTTOM_PANEL_HEIGHT + 10);
        
    
    static final int BORDER_TOP    = 0;
    static final int BORDER_LEFT   = 0;
    static final int BORDER_BOTTOM = 25;
    static final int BORDER_RIGHT  = 0;
    
//    static final int BOTTOM_PANEL_BORDER_TOP    = 0;
//    static final int BOTTOM_PANEL_BORDER_LEFT   = 0;
//    static final int BOTTOM_PANEL_BORDER_BOTTOM = 0;
//    static final int BOTTOM_PANEL_BORDER_RIGHT  = 0;
    
    static final int MIDDLE_PANEL_BORDER_TOP    = 0;
    static final int MIDDLE_PANEL_BORDER_LEFT   = 25;
    static final int MIDDLE_PANEL_BORDER_BOTTOM = 25;
    static final int MIDDLE_PANEL_BORDER_RIGHT  = 25;
    
    //.............................................Components of the BottomPanel
    private JPanel middlePanel;
    //Left Panel
    private JPanel leftPanel;
    private JPanel leftPanelInner;
    private JPanel leftPanelInnerNormal;
    private JPanel leftPanelInnerPreview;
    //Left Panel Inner Normal
    private JToggleButton topLampToggle;
    private JLabel microscopeLabel;
    private JToggleButton bottomLampToggle;
    private JPanel changeModePanel;
    private JToggleButton normalModeToggle;
    private JToggleButton timelapsModeToggle;
    //Left Panel Inner Preview   
    private JLabel  playVideoLabel;
    private JButton playVideoButton;
    private JLabel  deleteItemLabel;
    private JButton deleteItemButton;

    //Right Panel
    private JPanel rightPanel;
    private JPanel rightPanelInner;
    private JPanel rightPanelInnerNormal;
    private JPanel rightPanelInnerPreview;   
    //Right Panel Inner Normal
    private JPanel  brightnessPanel;
    private JLabel  brightnessLabel;
    private JSlider brightnessSlider;
    private JButton takePicButton;
    private JButton timelapsStartButton;
    private JButton timelapsStopButton;
    private JPanel  buttonsContainer;
    //Right Panel Inner Preview
    private JLabel  paintLabel;
    private JButton paintButton;
    private JLabel  pauseVideoLabel;
    private JButton pauseVideoButton;
    private JPanel  paintPauseContainer;
    private JLabel  backLabel;
    private JButton backButton;

    //Video Panel
    private JPanel videoContainer;
    private JPanel videoFrame;
    private JPanel videoScreen;
    private JLabel videoOverlayLabel;
   
    //.............................................Components of the BottomPanel
    private JPanel bottomPanel;
    private JScrollPane thumbScrollPane;
    private JList thumbnailsList;
    
    public CardMain() {
        super();
        initComponents(); 
        
        GroupLayout l = new GroupLayout(this);
        l.setHorizontalGroup(
                l.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(middlePanel)
                .addComponent(bottomPanel)
        );
        l.setVerticalGroup(l.createSequentialGroup()
                .addComponent(middlePanel)
                .addComponent(bottomPanel)      
        );
        setLayout(l);
        
        setBorder(BorderFactory.createMatteBorder(
                        BORDER_TOP,
                        BORDER_LEFT,
                        BORDER_BOTTOM,
                        BORDER_RIGHT,
                        MyLookAndFeel.MYGREEN)
        );
        middlePanel.setMinimumSize(new Dimension(1100, 510));
    }
    
    private void initComponents() {
        initMiddlePanel();
        initBottomPanel();
    }
    
    private void initMiddlePanel(){
        initLeftPanel();
        initRightPanel();
        initVideoPanel();  
        
        middlePanel = new JPanel() {{
            setBackground(MyLookAndFeel.MYGREEN);
            
            add(videoContainer);
            add(leftPanel);
            add(rightPanel); 
            
            setBorder(BorderFactory.createEmptyBorder(
                MIDDLE_PANEL_BORDER_TOP,
                MIDDLE_PANEL_BORDER_LEFT,
                MIDDLE_PANEL_BORDER_BOTTOM,
                MIDDLE_PANEL_BORDER_RIGHT)
                );
            setLayout(null);
        }};
    }
    
    /**
     * Inits all the components inside the left panel and the panel itself
     */
    private void initLeftPanel() {
        //..........................................Definition of the components
        leftPanel = new JPanelWithBackground("/resources/gui/panels/rounded.png");
        leftPanelInner = new JPanel() {
            {
                setOpaque(false);
            }
        };
        leftPanelInnerNormal = new JPanel() {
            {
                setOpaque(false);
            }
        };
        leftPanelInnerPreview = new JPanel() {
            {
                setOpaque(false);
            }
        };

        //Normal Mode
        topLampToggle = new MyToggleButton(MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-lamp-up_OFF.png")),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-lamp-up_ON.png"))
        );
        microscopeLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/gui/images/microscope.png")));
        bottomLampToggle = new MyToggleButton(MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-lamp-down_OFF.png")),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-lamp-down_ON.png"))
        );
        changeModePanel = new JPanel() {
            {
                setOpaque(false);
            }
        };

        normalModeToggle = new MyToggleButton(MyButtonKind.SMALL_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-mode-photo.png")));
        timelapsModeToggle = new MyToggleButton(MyButtonKind.SMALL_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-mode-timer.png")));

        //Preview Mode
        playVideoLabel = new MyLabel();
        playVideoButton = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-video-play.png")));
        
        deleteItemLabel = new JLabel();
        deleteItemButton = new MyButton(MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-delete.png")));

        //................................................................Layout
        //Normal
        changeModePanel.add(normalModeToggle);
        changeModePanel.add(timelapsModeToggle);

        GroupLayout normalLayout = new GroupLayout(leftPanelInnerNormal);
        normalLayout.setAutoCreateGaps(true);
        normalLayout.setAutoCreateContainerGaps(true);

        normalLayout.setHorizontalGroup(
                normalLayout.createSequentialGroup()
                .addGroup(normalLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(topLampToggle)
                        .addComponent(microscopeLabel)
                        .addComponent(bottomLampToggle)
                        .addComponent(changeModePanel)
                )
                .addGap(leftPanelGapForCenterContent)
        );
        normalLayout.setVerticalGroup(
                normalLayout.createSequentialGroup()
                .addComponent(topLampToggle)
                .addComponent(microscopeLabel)
                .addComponent(bottomLampToggle)
                .addComponent(changeModePanel)
        );
        leftPanelInnerNormal.setLayout(normalLayout);

        //Preview Mode
        //TODO: Add labels to layout!
        GroupLayout previewLayout = new GroupLayout(leftPanelInnerPreview);
        previewLayout.setHonorsVisibility(false);
        previewLayout.setAutoCreateGaps(true);
        previewLayout.setAutoCreateContainerGaps(true);

        previewLayout.setHorizontalGroup(
                previewLayout.createSequentialGroup()
                .addGroup(previewLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(playVideoLabel)
                        .addComponent(playVideoButton)
                        .addComponent(deleteItemLabel)
                        .addComponent(deleteItemButton)
                )
                .addGap(leftPanelGapForCenterContent)
        );
        previewLayout.setVerticalGroup(
                previewLayout.createSequentialGroup()
                .addComponent(playVideoLabel)
                .addComponent(playVideoButton)
                .addGap(50)
                .addComponent(deleteItemLabel)
                .addComponent(deleteItemButton)
        );
        leftPanelInnerPreview.setLayout(previewLayout);
        leftPanelInnerPreview.setVisible(false);

        //Right Panel itself
        leftPanelInner.add(leftPanelInnerNormal);
        leftPanelInner.add(leftPanelInnerPreview);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(leftPanelInner, new GridBagConstraints());
    }

    /**
     * Inits all the components inside the right panel and the panel itself
     */
    private void initRightPanel() {
        //..........................................Definition of the components
        rightPanel = new JPanelWithBackground("/resources/gui/panels/rounded.png");
        rightPanelInner = new JPanel();
        rightPanelInnerNormal = new JPanel();
        rightPanelInnerPreview = new JPanel();
        rightPanelInner.setOpaque(false);
        rightPanelInnerNormal.setOpaque(false);
        rightPanelInnerPreview.setOpaque(false);

        //Normal Mode
        takePicButton = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-takepicture.png")));
        timelapsStartButton = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-timelaps-start.png")));
        timelapsStopButton = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-stop.png")));

        //Brightness
        brightnessLabel = new JLabel();
        brightnessSlider = new MySlider() {
            {
                setOrientation(SwingConstants.VERTICAL);
            }
        };

        brightnessPanel = new JPanelWithBackground("/resources/gui/panels/brightness_background.png");

        GroupLayout brightnessPanelLayout = new GroupLayout(brightnessPanel);
        brightnessPanelLayout.setHorizontalGroup(
                brightnessPanelLayout.createSequentialGroup()
                .addGap(58)
                .addComponent(brightnessSlider)
        );
        brightnessPanelLayout.setVerticalGroup(
                brightnessPanelLayout.createSequentialGroup()
                .addGap(10)
                .addComponent(brightnessSlider, 205, 205, 205)
        );
        brightnessPanel.setLayout(brightnessPanelLayout);

        //Preview Mode      
        paintLabel = new JLabel();
        paintButton = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-paint.png")));
        pauseVideoLabel = new MyLabel();
        pauseVideoButton = new MyButton(MyButtonKind.BIG_SQUARED.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-video-pause.png")));
        paintPauseContainer = new JPanel () {{
            setOpaque(false);
            add(paintButton);
            add(pauseVideoButton);
        }};
        
        backLabel = new JLabel();
        backButton = new MyButton(MyButtonKind.BIG_RECTANGULAR.toString(),
                new ImageIcon(getClass().getResource("/resources/gui/icons/icon-back.png")));

        buttonsContainer = new JPanel() {
            {
                setOpaque(false);
            }
        };
        //................................................................Layout  

        buttonsContainer.add(takePicButton);
        buttonsContainer.add(timelapsStartButton);
        buttonsContainer.add(timelapsStopButton);

        GroupLayout controlsLayout = new GroupLayout(rightPanelInnerNormal);

        controlsLayout.setAutoCreateGaps(true);
        controlsLayout.setAutoCreateContainerGaps(true);

        controlsLayout.setHorizontalGroup(
                controlsLayout.createSequentialGroup()
                .addGap(rightPanelGapForCenterContent)
                .addGroup(controlsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(brightnessLabel)
                        .addComponent(brightnessPanel)
                        .addComponent(buttonsContainer)
                )
        );
        controlsLayout.setVerticalGroup(
                controlsLayout.createSequentialGroup()
                .addComponent(brightnessLabel)
                .addComponent(brightnessPanel)
                .addComponent(buttonsContainer)
        );
        rightPanelInnerNormal.setLayout(controlsLayout);

        //Preview Mode
        GroupLayout previewLayout = new GroupLayout(rightPanelInnerPreview);
        previewLayout.setHonorsVisibility(paintPauseContainer, false);
        previewLayout.setAutoCreateGaps(true);
        previewLayout.setAutoCreateContainerGaps(true);

        previewLayout.setHorizontalGroup(previewLayout.createSequentialGroup()
                .addGap(rightPanelGapForCenterContent)
                .addGroup(previewLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(paintLabel)
                        .addComponent(pauseVideoLabel)
                        .addComponent(paintPauseContainer)
                        .addComponent(backLabel)
                        .addComponent(backButton)
                )
        );
        previewLayout.setVerticalGroup(previewLayout.createSequentialGroup()
                .addComponent(paintLabel)
                .addComponent(pauseVideoLabel)
                .addComponent(paintPauseContainer)
                .addGap(50)
                .addComponent(backLabel)
                .addComponent(backButton)
        );
        rightPanelInnerPreview.setLayout(previewLayout);

        //Right Panel itself
        rightPanelInner.add(rightPanelInnerNormal);
        rightPanelInner.add(rightPanelInnerPreview);
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.add(rightPanelInner, new GridBagConstraints());
    }

    /**
     * Inits all the components of the Video Panel and the panel itself
     */
    private void initVideoPanel() {
        //..........................................Definition of the components

        String[] frame_images = new String[8];
        for (int i = 1; i <= frame_images.length; i++) {
            frame_images[i - 1] = "/resources/gui/panels/video-frame_" + i + ".png";
        }

        videoScreen = new VideoScreenPanel();

        videoOverlayLabel = new JLabel() {
            {
                setIcon(new ImageIcon(getClass().getResource("/resources/gui/images/timelaps_overlay.png")));
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.CENTER);
                setVisible(false);
            }
        };
        videoFrame = new NinePatchLikePanel(frame_images);
        videoContainer = new JPanel() {
            {
                setOpaque(false);
            }
        };
        // Should improve the quality
        videoScreen.setDoubleBuffered(true);
        // Needed to make the frame overlap the video
        videoScreen.setOpaque(false);

        //............................................... Layout the components.   
        videoContainer.setLayout(null);
        videoContainer.add(videoFrame);
        videoContainer.add(videoOverlayLabel);
        videoContainer.add(videoScreen);

        //Adds the listener that resizes all the children of the videoContainer
        videoContainer.addComponentListener(new VideoListener());
    }
    
    /**
     * Inits all the components inside the bottom panel and the panel itself
     */
    private void initBottomPanel() {
        //.......................................................ThumbScrollPane         
        // Creates the panel with a proper paintComponent for get the vertical gradient
        bottomPanel = new JPanel() {
            {
                setBorder(null);
                setBackground(Color.white);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    super.paintComponent(g);
                    return;
                }

                int w = getWidth();
                int h = getHeight();

                Color color1 = getBackground();
                Color color2 = color1.darker();

                // Paint a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                        0, 0, color2,
                        0, 8, color1);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(gp);

                g2d.fillRect(0, 0, w, h);

                setOpaque(false);
                super.paintComponent(g);
                setOpaque(true);
            }
        };

        thumbnailsList = new JList() {
            {
                setVisibleRowCount(1);
                setModel(new DefaultListModel());
                setCellRenderer(new ThumbnailsListRenderer());
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
            }
        };

        thumbScrollPane = new JScrollPane() {
            {
                setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                setBorder(null);
                setViewportView(thumbnailsList);
                getHorizontalScrollBar().setUI(new MyScrollBarUI());
                getHorizontalScrollBar().setBackground(Color.WHITE);
            }
        };

        GroupLayout layout = new GroupLayout(bottomPanel);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(thumbScrollPane,
                        minContentSize.width,
                        minContentSize.width,
                        Short.MAX_VALUE)
        );

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(thumbScrollPane,
                                THUMBS_SCROLL_HEIGHT,
                                THUMBS_SCROLL_HEIGHT,
                                THUMBS_SCROLL_HEIGHT)
                        .addGap(4, 4, 4)
                )
        );

        bottomPanel.setLayout(layout);

    }
    
    // Sets the components's locations
    public void setComponentsLocation() {
        
        Dimension d = middlePanel.getSize();
//        System.out.println(d.width+" "+d.height);
        int sidePanelsY = (int) (d.getHeight() / 2
                - leftPanel.getHeight() / 2);
                        
        int leftPanelX = MIDDLE_PANEL_BORDER_LEFT-sidePanelShadowLeftSide;
        int rightPanelX = (int) ( d.getWidth() 
                - rightPanel.getWidth()
                - MIDDLE_PANEL_BORDER_RIGHT
                + sidePanelShadowRightSide
                );
        
        // Resize and than move videoContainer
        int videoContainerHeight = (int)d.getHeight() 
                - MIDDLE_PANEL_BORDER_TOP
                - MIDDLE_PANEL_BORDER_BOTTOM;
        int videoContainerWidth  = (int)d.getWidth()
                - leftPanelX
                - leftPanel.getWidth()
                + sidePanelGapToBeCoveredRightSide
                + videoPanelShadowLeft
                + videoPanelShadowRight
                + sidePanelGapToBeCoveredLeftSide
                - rightPanel.getWidth()
                + sidePanelShadowRightSide
                - MIDDLE_PANEL_BORDER_RIGHT;

        int videoContainerX = 
                leftPanelX                
                + leftPanel.getSize().width
                - videoPanelShadowLeft
                - sidePanelGapToBeCoveredRightSide;
        int videoContainerY = (int)( d.getHeight()/2 - videoContainerHeight/2);

        leftPanel.setLocation(leftPanelX, sidePanelsY);
        rightPanel.setLocation(rightPanelX, sidePanelsY);
              
        videoContainer.setLocation(videoContainerX, videoContainerY);
        videoContainer.setSize(videoContainerWidth, videoContainerHeight); 
    }

    public VideoScreenPanel getVideoScreen () {
        return (VideoScreenPanel) videoScreen;
    }
    /* Listeners */
        //.................................................................LeftPanel
    public void addLampTopListener(ActionListener al) {
        topLampToggle.addActionListener(al);
    }

    public void addLampBottomListener(ActionListener al) {
        bottomLampToggle.addActionListener(al);
    }

    public void addNormalModeListener(ActionListener al) {
        normalModeToggle.addActionListener(al);
    }

    public void addTimelapsModeListener(ActionListener al) {
        timelapsModeToggle.addActionListener(al);
    }

    public void addPlayVideoButtonListener(ActionListener al) {
//        if (playVideoButton.getActionListeners().length > 0) playVideoButton.removeActionListener(playVideoButton.getActionListeners()[0]);
        playVideoButton.addActionListener(al);
    }
        
    public void addPauseVideoButtonListener(ActionListener al) {
        pauseVideoButton.addActionListener(al);
    }

    public void addDeleteItemListener(ActionListener al) {
        deleteItemButton.addActionListener(al);
    }

    //................................................................RightPanel
    public void addTakePicListener(ActionListener al) {
        takePicButton.addActionListener(al);
    }

    public void addStartTimeLapsListener(ActionListener al) {
        timelapsStartButton.addActionListener(al);
    }

    public void addStopTimeLapsListener(ActionListener al) {
        timelapsStopButton.addActionListener(al);
    }

    public void addBackListener(ActionListener al) {
        backButton.addActionListener(al);
    }

    public void addPaintListener(ActionListener al) {
        paintButton.addActionListener(al);
    }

    public void addBrighnessListener(ChangeListener cl) {
        brightnessSlider.addChangeListener(cl);
    }
    
    public void addThumbnailsListeners(/*MouseListener ml,*/ ListSelectionListener lsl) {
//        thumbnailsList.addMouseListener(ml);
        thumbnailsList.addListSelectionListener(lsl);
    }
    /* Interface */
    public void adaptToMode (int mode, boolean isTimerActive, boolean frozen, BufferedImage preview) {
        // frozen stuff
        switch (mode) {
            case MModel.NORMAL:
            case MModel.TIMELAPS:        
                videoOverlayLabel.setVisible(frozen);
                break;
            default:
                videoOverlayLabel.setVisible(false);
        }
        
        switch (mode) {
            case MModel.PROJECT:
                break;
            case MModel.NORMAL:
            case MModel.TIMELAPS:
                // Makes sure that no thumbnails is selected
                thumbnailsList.getSelectionModel().clearSelection();
                // Does the magic, removes the focus from the component
                thumbScrollPane.requestFocusInWindow(); 
            case MModel.PREVIEW_VIDEO:
            case MModel.PREVIEW_VIDEO_PLAY:
            case MModel.PREVIEW_VIDEO_PAUSE:
            case MModel.PREVIEW_VIDEO_STOP:
            case MModel.PREVIEW_IMAGE:

                break;                
        }
        
        // Components of the main section
        switch (mode){
            case MModel.NORMAL:
                leftPanelInnerNormal.setVisible(true);
                leftPanelInnerPreview.setVisible(false);

                rightPanelInnerNormal.setVisible(true);
                rightPanelInnerPreview.setVisible(false);
                normalModeToggle.setSelected(true);
                timelapsModeToggle.setSelected(false);

                takePicButton.setVisible(true);
                timelapsStartButton.setVisible(false);
                timelapsStopButton.setVisible(false);
                break;

            case MModel.PREVIEW_IMAGE:
                leftPanelInnerNormal.setVisible(false);
                leftPanelInnerPreview.setVisible(true);

                rightPanelInnerNormal.setVisible(false);
                rightPanelInnerPreview.setVisible(true);

                paintButton.setVisible(true);  
                paintLabel.setVisible(true);
                
                playVideoButton.setVisible(false);
                playVideoLabel.setVisible(false);
                pauseVideoLabel.setVisible(false);
                pauseVideoButton.setVisible(false);

                ((VideoScreenPanel) videoScreen).update(null, preview);
                break;

            case MModel.PREVIEW_VIDEO:
            case MModel.PREVIEW_VIDEO_PLAY:
            case MModel.PREVIEW_VIDEO_PAUSE:
            case MModel.PREVIEW_VIDEO_STOP:                    
                leftPanelInnerNormal.setVisible(false);
                leftPanelInnerPreview.setVisible(true);

                rightPanelInnerNormal.setVisible(false);
                rightPanelInnerPreview.setVisible(true);
                
                paintButton.setVisible(false);
                paintLabel.setVisible(false);
                break;

            case MModel.TIMELAPS:
                leftPanelInnerNormal.setVisible(true);
                leftPanelInnerPreview.setVisible(false);

                rightPanelInnerNormal.setVisible(true);
                rightPanelInnerPreview.setVisible(false);
                normalModeToggle.setSelected(false);
                timelapsModeToggle.setSelected(true);

                takePicButton.setVisible(false);

                timelapsStartButton.setVisible(!isTimerActive);
                timelapsStopButton.setVisible(isTimerActive);
                break;
        }
        
        // Components of the Video Preview
        switch (mode){
            case MModel.PREVIEW_VIDEO:
                ((VideoScreenPanel) videoScreen).update(null, preview);
                playVideoLabel.setVisible(true);
                playVideoButton.setEnabled(true);
                playVideoButton.setVisible(true);

                pauseVideoLabel.setVisible(true);
                pauseVideoButton.setEnabled(false);
                pauseVideoButton.setVisible(true);
                break;
                
            case MModel.PREVIEW_VIDEO_PLAY:
                playVideoButton.setEnabled(false);
                pauseVideoButton.setEnabled(true);
                break;
                
            case MModel.PREVIEW_VIDEO_PAUSE:
                playVideoButton.setEnabled(true);
                pauseVideoButton.setEnabled(false);
                break;        
        }
    
    }
    
    @Override
    public void adaptToUser(int user) {
        if (user == SettingsManager.USERMODE_BASIC)
            changeModePanel.setVisible(false);
        else 
            changeModePanel.setVisible(true);
        
    }

    @Override
    public void adaptToLanguage(ResourceBundle rb) {
        playVideoLabel.setText(rb.getString("LblPlayVideo"));
        pauseVideoLabel.setText(rb.getString("LblStopVideo"));
        deleteItemLabel.setText(rb.getString("LblDelete"));
        paintLabel.setText(rb.getString("LblDraw"));
        backLabel.setText(rb.getString("LblBack"));
        brightnessLabel.setText(rb.getString("LblBrigthness"));
    }

    @Override
    public void adaptToDevice(Device d) {
// Check of the defice is null or exists
        boolean isNone = d.isNone();
        if ( isNone ) {
            // Disable controls
            bottomLampToggle.setEnabled(false);
            topLampToggle.setEnabled(false);
            brightnessSlider.setEnabled(false);
//            takePicButton.setEnabled(false);
//            timelapsStartButton.setEnabled(false);
//            normalModeToggle.setEnabled(false);
//            timelapsModeToggle.setEnabled(false);
            
        } else { // not null
            
            // Enable and adapt bottom lamp
            bottomLampToggle.setEnabled(d.hasBottomLight());
            if (d.hasBottomLight()) {       
                this.bottomLampToggle.setSelected(d.isBottomLightOn());
            }

            // Enable and adapt top lamp
            topLampToggle.setEnabled(d.hasTopLight());
            if (d.hasTopLight()) {       
                this.topLampToggle.setSelected(d.isTopLightOn());
            }

            // Enable and adapt brightness
            brightnessSlider.setEnabled(d.hasBrightness());
            if (d.hasBrightness()) {       
                int[] binfo = d.getBrightnessInfo();
                brightnessSlider.setMinimum(binfo[1]);
                brightnessSlider.setMaximum(binfo[2]);
                brightnessSlider.setValue(binfo[4]);
            }     
        }
        brightnessLabel.setEnabled(!isNone);
        takePicButton.setEnabled(!isNone);
        timelapsStartButton.setEnabled(!isNone);
        normalModeToggle.setEnabled(!isNone);
        timelapsModeToggle.setEnabled(!isNone);
    }

    @Override
    public void adaptToFrozen(boolean isFrozen, int mode) {
        isFrozen = !isFrozen; //TODO CLEAN THIS S***
//        settingButton.setEnabled(isFrozen & MModel.DEFAULT_SETTING_ENABLED);

//        videoOverlayLabel.setVisible(!isFrozen);
//        thumbnailsList.setEnabled(isFrozen);

        switch (mode){
            case MModel.NORMAL:
            case MModel.TIMELAPS:
                videoOverlayLabel.setVisible(!isFrozen);
                
                topLampToggle.setEnabled(isFrozen);
                bottomLampToggle.setEnabled(isFrozen);
                microscopeLabel.setEnabled(isFrozen);
//                normalModeToggle.setEnabled(isFrozen);
//                timelapsModeToggle.setEnabled(isFrozen);
                brightnessSlider.setEnabled(isFrozen);
                brightnessLabel.setEnabled(isFrozen);
//                takePicButton.setEnabled(isFrozen);
                break;

        }
    }

    @Override
    public List<JLabel> getHelpLabels(ResourceBundle rb, JComponent container) {
        List<JLabel> l = new ArrayList<JLabel>() {};
        
        if ( topLampToggle.isShowing())         l.add(new MyHelpLabel( topLampToggle, container, rb.getString("TltTopLampTgl"), MyHelpLabel.ORIENTATION_LEFT, 200 ));
        if ( bottomLampToggle.isShowing())      l.add(new MyHelpLabel( bottomLampToggle, container, rb.getString("TltBottomLampTgl"), MyHelpLabel.ORIENTATION_LEFT, 200 ));
        if ( changeModePanel.isShowing())       l.add(new MyHelpLabel( changeModePanel, container, rb.getString("TltChangeModePnl"), MyHelpLabel.ORIENTATION_LEFT, 200 ));
        if ( playVideoButton.isShowing())       l.add(new MyHelpLabel( playVideoButton, container, rb.getString("TltPlayVideoBtn"), MyHelpLabel.ORIENTATION_LEFT, 200 ));
        if ( deleteItemButton.isShowing())      l.add(new MyHelpLabel( deleteItemButton, container, rb.getString("TltDeleteItemBtn"), MyHelpLabel.ORIENTATION_LEFT, 200 ));
        if ( brightnessSlider.isShowing())      l.add(new MyHelpLabel( brightnessSlider, container, rb.getString("TltBrightnessSld"), MyHelpLabel.ORIENTATION_RIGHT, 200 ));
        if ( takePicButton.isShowing())         l.add(new MyHelpLabel( takePicButton, container, rb.getString("TltTakePicBtn"), MyHelpLabel.ORIENTATION_RIGHT, 200 ));
        if ( paintButton.isShowing())           l.add(new MyHelpLabel( paintButton, container, rb.getString("TltPaintBtn"), MyHelpLabel.ORIENTATION_RIGHT, 20 ));
        if ( timelapsStartButton.isShowing())   l.add(new MyHelpLabel( timelapsStartButton,container, rb.getString("TltTimelapsStartBtn"), MyHelpLabel.ORIENTATION_RIGHT, 200 )); 
        if ( timelapsStopButton.isShowing())    l.add(new MyHelpLabel( timelapsStopButton, container, rb.getString("TltTimelapsStopBtn"), MyHelpLabel.ORIENTATION_RIGHT, 200 ));
        if ( thumbScrollPane.isShowing())       l.add(new MyHelpLabel( thumbScrollPane, container, rb.getString("TltThumbScrlP"), MyHelpLabel.ORIENTATION_CENTER, 300 ));

                return l;          
    }
    
     /*-------------------------------------------------------------------------+
     |                      Component Listeners                                |
     +------------------------------------------------------------------------*/
    /**
     * Makes sure that when the video container changes is dimension also its
     * children's dimension are updated.
     */
    class VideoListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent ce) {
            int width = videoContainer.getWidth();
            int height = videoContainer.getHeight();
            videoScreen.setBounds(
                    offset_screen_x,
                    offset_screen_y,
                    width - offset_screen_x * 2,
                    height - offset_screen_y * 2);
            videoOverlayLabel.setBounds(0, 0, width, height);
            videoFrame.setBounds(0, 0, width, height);
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
    
    
    
    /* Thumbnail */
        /**
     * Updates the thumbnails reading the file from the project directory.
     * @param files
     */
    public void updateThumbnailsList(List<String> files) {
        DefaultListModel lm = ((DefaultListModel) thumbnailsList.getModel());
        lm.clear();

        for (String fileName : files) {
            lm.addElement(fileName);
        }
    }
    
        public int getSelectThumbnailIndex() {
        return thumbnailsList.getSelectedIndex();
    }

    public int getThumbnailsModelSize() {
        return thumbnailsList.getModel().getSize();
    }

    /**
     * Select the thumbnail of a given position
     * @param index The position of the element to select.
     */
    public void selectThumbnailElement(int index) {
        thumbnailsList.setSelectedIndex(index);
    }
    
    /**
     * Select the thumbnail of a given file
     * @param file The filename of the element to select
     */
    public void selectThumbnailElement(String file) {
        thumbnailsList.setSelectedValue(file, false);
    }
    
}

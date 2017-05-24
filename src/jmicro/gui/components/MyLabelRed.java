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
package jmicro.gui.components;

import java.awt.Color;
import javax.swing.JLabel;
import jmicro.utils.MyLookAndFeel;

/**
 * JLabel with red font.
 */
public class MyLabelRed extends JLabel {
    
    public MyLabelRed () {
        this("");
    }
    
    public MyLabelRed (String text) {
        super(text);
        setForeground(MyLookAndFeel.MYRED);
        setBackground(Color.ORANGE); //??
    }   
}
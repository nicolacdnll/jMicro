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
import java.awt.Dimension;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import jmicro.gui.componentsUI.MyComboBoxUI;

/**
 * JComboBox class for the UI.
 */
public class MyComboBox extends JComboBox {
    public MyComboBox (ComboBoxModel model)
    {
        super(model);
        setUI(new MyComboBoxUI());
        setBackground(Color.WHITE);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, MyButton.SMALL_SQUARED_HEIGHT_NOB);
    }
}

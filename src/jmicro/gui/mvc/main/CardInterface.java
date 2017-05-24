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

import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JLabel;
import jmicro.v4l4jutils.Device;

/**
 * Card interface used to standardise the use of cards in the main view.
 */
public interface CardInterface {
        
    
    void adaptToUser(int user);
    
    void adaptToLanguage(ResourceBundle rb);
    
    void adaptToDevice(Device d);
    
    void adaptToFrozen (boolean isFrozen, int mode);
    
    List<JLabel> getHelpLabels(ResourceBundle rb, JComponent container);
}

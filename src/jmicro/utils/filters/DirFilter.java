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


package jmicro.utils.filters;

import java.io.File;
import java.io.FileFilter;

/**
 * A class that implements the Java FileFilter interface.
 * This class create a file filter that accept all the directory that isn't hidden. * 
*
 */
public class DirFilter implements FileFilter {

        public boolean accept(File file) {
            //accept all directories that isn't hidden
            return file.isDirectory() & !file.isHidden();
        }
}
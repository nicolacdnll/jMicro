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

import jmicro.Globals;

/**
 * Class that represents the About dialog.
 * This class implements the Singleton pattern, so have a private constructor.
 * The only way to access to this class from others class is using the getInstance() method.
 * 
*
 */


public class AboutDialog extends javax.swing.JDialog {

    private static final AboutDialog _instance = new AboutDialog();

    private AboutDialog() {
        initComponents();
                //Sets no relative location to gets the dialog centered on the screen
        this.setLocationRelativeTo(null);
    }

    /**
     * Return the only access to this class that implements the pattern Singleton.
     * @return A static access to this class.
     */
    public static AboutDialog getInstance() {
        return _instance;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelCredits = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        labelCredits.setText(Globals.NAME+" v"+Globals.VERSION);

        jTextPane1.setEditable(false);
        jTextPane1.setText("Project Leader: Professor Alessandro Efrem Colombi, Free University of Bozen-Bolzano\nSupervisor: Professor Ivan Serina, Università degli Studi di Brescia\nDeveloper: Nicola Cadenelli\nGraphic Designer: Dr. Hannes Pasqualini\nProject Manager: Chiara Gandolfi\n\nLicense: GPLv3 http://www.gnu.org/licenses/gpl-3.0.txt"); // NOI18N
        jTextPane1.setSelectedTextColor(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(218, 218, 218)
                        .addComponent(labelCredits)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelCredits, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JLabel labelCredits;
    // End of variables declaration//GEN-END:variables
}

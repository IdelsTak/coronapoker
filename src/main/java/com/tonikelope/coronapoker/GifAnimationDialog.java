/*
 * Copyright (C) 2020 tonikelope
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
package com.tonikelope.coronapoker;

import java.awt.Dimension;
import javax.swing.ImageIcon;

/**
 *
 * @author tonikelope
 */
public class GifAnimationDialog extends javax.swing.JDialog {

    /**
     * Creates new form GifAnimation
     */
    public GifAnimationDialog(java.awt.Frame parent, boolean modal, ImageIcon icon) {
        this(parent, modal, icon, null);

    }

    /**
     * Creates new form GifAnimation
     */
    public GifAnimationDialog(java.awt.Frame parent, boolean modal, ImageIcon icon, Integer timeout) {
        super(parent, modal);
        initComponents();
        int height, width;
        if (icon.getImage().getHeight(null) > icon.getImage().getWidth(null)) {
            height = Math.round(0.7f * parent.getHeight());
        } else {
            height = Math.round(0.5f * parent.getHeight());
        }
        width = Math.round(((float) icon.getImage().getWidth(null) * height) / icon.getImage().getHeight(null));
        if (width > Math.round(parent.getWidth() * 0.8f)) {
            int i = 1;
            int original = width;
            while (width > Math.round(parent.getWidth() * 0.8f)) {
                width = Math.round(original * (100 - i * 0.1f));
                i++;
            }
            height = Math.round(height * (100 - (i - 1) * 0.1f));
        }

        gif_panel.setGifIcon(icon, width, height);

        setPreferredSize(new Dimension(width, height));

        pack();

        if (timeout != null) {
            Helpers.threadRun(new Runnable() {

                public void run() {
                    Helpers.pausar(timeout);

                    Helpers.GUIRun(new Runnable() {
                        public void run() {

                            dispose();

                        }
                    });
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gif_panel = new com.tonikelope.coronapoker.GifPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setFocusCycleRoot(false);
        setFocusable(false);
        setFocusableWindowState(false);
        setUndecorated(true);
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(gif_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(gif_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.tonikelope.coronapoker.GifPanel gif_panel;
    // End of variables declaration//GEN-END:variables
}

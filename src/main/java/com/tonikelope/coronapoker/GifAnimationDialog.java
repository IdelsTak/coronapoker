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
import java.awt.Image;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author tonikelope
 */
public class GifAnimationDialog extends javax.swing.JDialog {

    private final CyclicBarrier gif_barrier = new CyclicBarrier(2);
    private volatile boolean force_exit = false;

    public boolean isForce_exit() {
        return force_exit;
    }

    /**
     * Creates new form GifAnimation
     */
    public GifAnimationDialog(java.awt.Frame parent, boolean modal, ImageIcon icon) {
        this(parent, modal, icon, 0);

    }

    /**
     * Creates new form GifAnimation
     */
    public GifAnimationDialog(java.awt.Frame parent, boolean modal, ImageIcon icon, int frames) {
        super(parent, modal);
        initComponents();

        this.setFocusable(modal);
        this.setFocusCycleRoot(modal);
        this.setAutoRequestFocus(modal);
        this.setFocusableWindowState(modal);

        gif_panel.getGif().setBarrier(gif_barrier);

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

        gif_panel.getGif().setPreferredSize(new Dimension(width, height));

        gif_panel.setPreferredSize(new Dimension(width, height));

        setPreferredSize(new Dimension(width, height));

        gif_panel.getGif().setIcon(new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT)), frames);

        pack();

        Helpers.threadRun(new Runnable() {
            public void run() {

                int old_priority = Thread.currentThread().getPriority();

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                try {
                    gif_barrier.await();
                } catch (Exception ex) {
                    Logger.getLogger(GifAnimationDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

                Helpers.GUIRunAndWait(new Runnable() {
                    public void run() {
                        dispose();

                    }
                });

                if (!force_exit) {

                    Init.PLAYING_CINEMATIC = false;
                }

                synchronized (Init.LOCK_CINEMATICS) {

                    Init.LOCK_CINEMATICS.notifyAll();

                }

                Thread.currentThread().setPriority(old_priority);

            }
        });
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
        setAutoRequestFocus(false);
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        setFocusCycleRoot(false);
        setFocusable(false);
        setFocusableWindowState(false);
        setUndecorated(true);
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });

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

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here

        force_exit = true;

        dispose();

        synchronized (Init.LOCK_CINEMATICS) {

            Init.LOCK_CINEMATICS.notifyAll();

        }
    }//GEN-LAST:event_formMouseClicked

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        if (isModal()) {
            Init.CURRENT_MODAL_DIALOG.add(this);
        }
    }//GEN-LAST:event_formWindowActivated

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        // TODO add your handling code here:
        if (isModal()) {
            try {
                Init.CURRENT_MODAL_DIALOG.removeLast();
            } catch (Exception ex) {
            }
        }
    }//GEN-LAST:event_formWindowDeactivated

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.tonikelope.coronapoker.GifPanel gif_panel;
    // End of variables declaration//GEN-END:variables
}

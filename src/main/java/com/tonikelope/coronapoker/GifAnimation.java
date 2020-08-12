/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author tonikelope
 */
public class GifAnimation extends javax.swing.JDialog {

    /**
     * Creates new form GifAnimation
     */
    public GifAnimation(java.awt.Frame parent, boolean modal, ImageIcon icon) {
        super(parent, modal);

        initComponents();

        int height, width;

        if (icon.getImage().getHeight(null) > icon.getImage().getWidth(null)) {
            height = Math.round(0.6f * parent.getHeight());
        } else {
            height = Math.round(0.4f * parent.getHeight());
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

        gif.setIcon(new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT)));

        this.gif.setPreferredSize(new Dimension(width, height));

        this.setPreferredSize(new Dimension(width, height));

        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gif = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        gif.setDoubleBuffered(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gif, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gif, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel gif;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author tonikelope
 */
public class PauseDialog extends javax.swing.JDialog {

    private volatile Timer timer = null;

    /**
     * Creates new form Pausa
     */
    public PauseDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.resume_button.setVisible(Game.getInstance().isPartida_local());

        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                pausa_label.setVisible(!pausa_label.isVisible());

            }
        };

        timer = new Timer(500, listener);

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        pack();
    }

    public void resuming() {

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {

                resume_button.setEnabled(false);
                resume_button.setText(Translator.translate("REANUDANDO TIMBA..."));

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

        jPanel1 = new javax.swing.JPanel();
        resume_button = new javax.swing.JButton();
        pausa_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 102, 0));

        resume_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        resume_button.setText("REANUDAR TIMBA");
        resume_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resume_buttonActionPerformed(evt);
            }
        });

        pausa_label.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        pausa_label.setForeground(new java.awt.Color(255, 255, 255));
        pausa_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pausa_label.setText("TIMBA PAUSADA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pausa_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resume_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pausa_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resume_button)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resume_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resume_buttonActionPerformed
        // TODO add your handling code here:
        this.resume_button.setEnabled(false);
        this.resume_button.setText(Translator.translate("REANUDANDO TIMBA..."));
        Game.getInstance().getPausa_menu().doClick();
    }//GEN-LAST:event_resume_buttonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        this.timer.start();
        this.resume_button.setText(Translator.translate("REANUDAR TIMBA"));
        this.resume_button.setEnabled(true);
        Game.getInstance().getPausa_menu().setEnabled(true);
    }//GEN-LAST:event_formComponentShown

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
        this.timer.stop();
    }//GEN-LAST:event_formComponentHidden

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Game.getInstance().getExit_menu().doClick();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel pausa_label;
    private javax.swing.JButton resume_button;
    // End of variables declaration//GEN-END:variables
}

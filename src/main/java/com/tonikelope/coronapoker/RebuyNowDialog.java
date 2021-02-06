/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author tonikelope
 */
public class RebuyNowDialog extends javax.swing.JDialog {

    private volatile boolean rebuy = false;
    private volatile boolean touched = false;

    public boolean isRebuy() {
        return rebuy;
    }

    public JSpinner getRebuy_spinner() {
        return rebuy_spinner;
    }

    private void pausaConBarra(int tiempo) {

        Helpers.GUIRun(new Runnable() {
            public void run() {
                barra.setVisible(true);
                barra.setMaximum(tiempo);
                barra.setValue(tiempo);
            }
        });

        int t = tiempo;

        while (t > 0 && !rebuy && !touched) {

            Helpers.pausar(1000);

            if (!GameFrame.getInstance().isTimba_pausada() && !GameFrame.getInstance().getCrupier().isFin_de_la_transmision() && !rebuy && !touched) {

                final int v = --t;

                Helpers.GUIRun(new Runnable() {
                    public void run() {
                        barra.setValue(v);
                    }
                });
            }

        }

        Helpers.GUIRun(new Runnable() {
            public void run() {
                barra.setVisible(false);
            }
        });
    }

    /**
     * Creates new form RebuyNowDialog
     */
    public RebuyNowDialog(java.awt.Frame parent, boolean modal, boolean cancel, int timeout) {
        super(parent, modal);

        initComponents();

        barra.setVisible(false);

        rebuy_spinner.setModel(new SpinnerNumberModel(GameFrame.BUYIN, 1, GameFrame.BUYIN, 1));

        ((JSpinner.DefaultEditor) rebuy_spinner.getEditor()).getTextField().setEditable(false);

        if (!cancel) {
            cancel_button.setEnabled(false);
        }

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        pack();

        if (timeout > 0) {

            Helpers.threadRun(new Runnable() {
                public void run() {
                    pausaConBarra(timeout);

                    if (!rebuy && !touched) {
                        rebuy = true;
                        Helpers.GUIRun(new Runnable() {
                            public void run() {
                                dispose();
                            }
                        });
                    }
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        ok_button = new javax.swing.JButton();
        cancel_button = new javax.swing.JButton();
        barra = new javax.swing.JProgressBar();
        rebuy_spinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("RECOMPRAR");
        setModal(true);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0), 8));
        jPanel1.setOpaque(false);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chips.png"))); // NOI18N
        jLabel2.setDoubleBuffered(true);
        jLabel2.setFocusable(false);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        jLabel1.setText("RECOMPRAR");
        jLabel1.setDoubleBuffered(true);
        jLabel1.setFocusable(false);

        ok_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        ok_button.setText("Aceptar");
        ok_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ok_button.setDoubleBuffered(true);
        ok_button.setFocusable(false);
        ok_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok_buttonActionPerformed(evt);
            }
        });

        cancel_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        cancel_button.setText("Cancelar");
        cancel_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancel_button.setDoubleBuffered(true);
        cancel_button.setFocusable(false);
        cancel_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_buttonActionPerformed(evt);
            }
        });

        barra.setDoubleBuffered(true);

        rebuy_spinner.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        rebuy_spinner.setModel(new javax.swing.SpinnerNumberModel());
        rebuy_spinner.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rebuy_spinner.setDoubleBuffered(true);
        rebuy_spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rebuy_spinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(ok_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancel_button))
                    .addComponent(rebuy_spinner, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(rebuy_spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancel_button)
                            .addComponent(ok_button)))
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void cancel_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_buttonActionPerformed
        // TODO add your handling code here:

        dispose();
    }//GEN-LAST:event_cancel_buttonActionPerformed

    private void ok_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ok_buttonActionPerformed
        // TODO add your handling code here:
        rebuy = true;
        dispose();
    }//GEN-LAST:event_ok_buttonActionPerformed

    private void rebuy_spinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rebuy_spinnerStateChanged
        // TODO add your handling code here:
        touched = true;
    }//GEN-LAST:event_rebuy_spinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton cancel_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton ok_button;
    private javax.swing.JSpinner rebuy_spinner;
    // End of variables declaration//GEN-END:variables
}

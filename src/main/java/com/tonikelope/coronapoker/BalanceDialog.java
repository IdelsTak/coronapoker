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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author tonikelope
 */
public class BalanceDialog extends javax.swing.JDialog {

    private volatile boolean retry = false;

    public boolean isRetry() {
        return retry;
    }

    /**
     * Creates new form BalanceDialog
     */
    public BalanceDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        initComponents();

        exit_button.requestFocus();

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(20);

        ArrayList<Object[]> ranking = new ArrayList<>();

        for (Map.Entry<String, Float[]> entry : GameFrame.getInstance().getCrupier().getAuditor().entrySet()) {

            JLabel label = new JLabel();

            Float[] pasta = entry.getValue();

            String ganancia_msg = "";

            float ganancia = Helpers.floatClean(Helpers.floatClean(pasta[0]) - Helpers.floatClean(pasta[1]));

            if (Helpers.float1DSecureCompare(ganancia, 0f) < 0) {
                ganancia_msg += Translator.translate("PIERDE ") + Helpers.float2String(ganancia * -1f);
                label.setForeground(Color.RED);
            } else if (Helpers.float1DSecureCompare(ganancia, 0f) > 0) {
                ganancia_msg += Translator.translate("GANA ") + Helpers.float2String(ganancia);
                label.setForeground(new Color(0, 130, 0));
            } else {
                ganancia_msg += Translator.translate("NI GANA NI PIERDE");
            }

            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

            label.setText(entry.getKey() + " " + ganancia_msg);

            label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));

            if (entry.getKey().equals(GameFrame.getInstance().getLocalPlayer().getNickname())) {
                label.setBackground(new Color(255, 255, 153));
                label.setOpaque(true);
            }

            String avatar_path = GameFrame.getInstance().getNick2avatar().get(entry.getKey());

            if (!"".equals(avatar_path) && !"*".equals(avatar_path)) {

                label.setIcon(new ImageIcon(new ImageIcon(avatar_path).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));

            } else if ("*".equals(avatar_path)) {

                label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar_bot.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));

            } else {

                label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
            }

            ranking.add(new Object[]{ganancia, label});
        }

        Collections.sort(ranking, new RankingComparator());

        Collections.reverse(ranking);

        for (Object[] o : ranking) {

            jugadores.add((JLabel) o[1]);
        }

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        log_button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/log.png")).getImage().getScaledInstance(log_button.getHeight(), log_button.getHeight(), Image.SCALE_SMOOTH)));

        stats_button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/stats.png")).getImage().getScaledInstance(stats_button.getHeight(), stats_button.getHeight(), Image.SCALE_SMOOTH)));

        exit_button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/exit.png")).getImage().getScaledInstance(exit_button.getHeight(), exit_button.getHeight(), Image.SCALE_SMOOTH)));

        retry_button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/start.png")).getImage().getScaledInstance(retry_button.getHeight(), retry_button.getHeight(), Image.SCALE_SMOOTH)));

        pack();

    }

    static class RankingComparator implements Comparator<Object[]> {

        @Override
        public int compare(Object[] t, Object[] t1) {

            return Float.compare((float) t[0], (float) t1[0]);
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

        title = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jugadores = new javax.swing.JPanel();
        exit_button = new javax.swing.JButton();
        stats_button = new javax.swing.JButton();
        log_button = new javax.swing.JButton();
        retry_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowDeactivated(java.awt.event.WindowEvent evt) {
                formWindowDeactivated(evt);
            }
        });

        title.setBackground(new java.awt.Color(102, 102, 102));
        title.setFont(new java.awt.Font("Dialog", 1, 28)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("LA TIMBA HA TERMINADO");
        title.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        title.setDoubleBuffered(true);
        title.setFocusable(false);
        title.setOpaque(true);

        jScrollPane1.setBorder(null);
        jScrollPane1.setDoubleBuffered(true);
        jScrollPane1.setFocusable(false);

        jugadores.setBackground(new java.awt.Color(245, 245, 245));
        jugadores.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jugadores.setFocusable(false);
        jugadores.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane1.setViewportView(jugadores);

        exit_button.setBackground(new java.awt.Color(255, 0, 0));
        exit_button.setFont(new java.awt.Font("Dialog", 1, 22)); // NOI18N
        exit_button.setForeground(new java.awt.Color(255, 255, 255));
        exit_button.setText("SALIR");
        exit_button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        exit_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit_button.setDoubleBuffered(true);
        exit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_buttonActionPerformed(evt);
            }
        });

        stats_button.setBackground(new java.awt.Color(255, 153, 51));
        stats_button.setFont(new java.awt.Font("Dialog", 1, 22)); // NOI18N
        stats_button.setForeground(new java.awt.Color(255, 255, 255));
        stats_button.setText("ESTADÍSTICAS");
        stats_button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        stats_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        stats_button.setDoubleBuffered(true);
        stats_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stats_buttonActionPerformed(evt);
            }
        });

        log_button.setFont(new java.awt.Font("Dialog", 1, 22)); // NOI18N
        log_button.setText("REGISTRO DE LA TIMBA");
        log_button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        log_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        log_button.setDoubleBuffered(true);
        log_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                log_buttonActionPerformed(evt);
            }
        });

        retry_button.setBackground(new java.awt.Color(0, 153, 255));
        retry_button.setFont(new java.awt.Font("Dialog", 1, 22)); // NOI18N
        retry_button.setForeground(new java.awt.Color(255, 255, 255));
        retry_button.setText("OTRA TIMBA");
        retry_button.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        retry_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        retry_button.setDoubleBuffered(true);
        retry_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retry_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(retry_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exit_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(stats_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(log_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(log_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stats_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exit_button)
                    .addComponent(retry_button)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_buttonActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_exit_buttonActionPerformed

    private void stats_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stats_buttonActionPerformed
        // TODO add your handling code here:
        StatsDialog dialog = new StatsDialog(GameFrame.getInstance().getFrame(), true);

        dialog.setPreferredSize(new Dimension(Math.round(0.9f * GameFrame.getInstance().getFrame().getWidth()), Math.round(0.9f * GameFrame.getInstance().getFrame().getHeight())));

        dialog.pack();

        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);
    }//GEN-LAST:event_stats_buttonActionPerformed

    private void log_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_log_buttonActionPerformed
        // TODO add your handling code here:
        GameFrame.getInstance().getRegistro_dialog().setPreferredSize(new Dimension(Math.round(0.7f * GameFrame.getInstance().getFrame().getWidth()), Math.round(0.7f * GameFrame.getInstance().getFrame().getHeight())));

        GameFrame.getInstance().getRegistro_dialog().pack();

        GameFrame.getInstance().getRegistro_dialog().setLocationRelativeTo(this);

        GameFrame.getInstance().getRegistro_dialog().setModal(true);

        GameFrame.getInstance().getRegistro_dialog().setVisible(true);

    }//GEN-LAST:event_log_buttonActionPerformed

    private void retry_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retry_buttonActionPerformed
        // TODO add your handling code here:
        retry = true;
        dispose();

    }//GEN-LAST:event_retry_buttonActionPerformed

    private void formWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeactivated
        // TODO add your handling code here:
        if (isModal()) {
            Init.CURRENT_MODAL_DIALOG.removeLast();
        }
    }//GEN-LAST:event_formWindowDeactivated

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        // TODO add your handling code here:
        if (isModal()) {
            Init.CURRENT_MODAL_DIALOG.add(this);
        }

    }//GEN-LAST:event_formWindowActivated

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exit_button;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jugadores;
    private javax.swing.JButton log_button;
    private javax.swing.JButton retry_button;
    private javax.swing.JButton stats_button;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}

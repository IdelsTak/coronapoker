/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 *
 * @author tonikelope
 */
public class GameOverDialog extends javax.swing.JDialog {

    public static final int COUNTDOWN_PAUSE = 1350;
    private volatile Timer timer = null;
    private volatile boolean continua = false;
    private volatile String last_mp3_loop = null;
    private volatile boolean direct_gameover = false;

    public boolean isContinua() {
        return continua;
    }

    /**
     * Creates new form Recomprar
     */
    public GameOverDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();
            }
        });

    }

    public GameOverDialog(java.awt.Frame parent, boolean modal, boolean direct) {
        super(parent, modal);

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();

                direct_gameover = direct;

                if (direct_gameover) {
                    game_over.setVisible(false);
                    continue_button.setVisible(false);
                    numbers.setIcon(new ImageIcon(getClass().getResource("/images/gameover/game_over.png")));
                    pack();
                }
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
        game_over = new javax.swing.JLabel();
        numbers = new javax.swing.JLabel();
        continue_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        game_over.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        game_over.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gameover/game_over.png"))); // NOI18N
        game_over.setDoubleBuffered(true);

        numbers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numbers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gameover/9.png"))); // NOI18N
        numbers.setDoubleBuffered(true);

        continue_button.setFont(new java.awt.Font("Dialog", 1, 60)); // NOI18N
        continue_button.setIcon(new ImageIcon(getClass().getResource("/images/gameover/continue_"+com.tonikelope.coronapoker.Game.LANGUAGE+".png")));
        continue_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        continue_button.setDoubleBuffered(true);
        continue_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continue_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(numbers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(continue_button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(game_over, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(game_over)
                .addGap(18, 18, 18)
                .addComponent(numbers, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(continue_button, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
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

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            continue_button.doClick();
        }
    }//GEN-LAST:event_formKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:

        requestFocusInWindow();

        Helpers.threadRun(new Runnable() {
            @Override
            public void run() {

                last_mp3_loop = Helpers.getCurrentLoopMp3Playing();

                if (last_mp3_loop != null) {
                    Helpers.pauseLoopMp3Resource(last_mp3_loop);
                }

                if (!direct_gameover) {
                    ActionListener listener = new ActionListener() {

                        int counter = 9;

                        public void actionPerformed(ActionEvent ae) {

                            counter--;

                            if (counter < 0) {

                                timer.stop();

                            } else {
                                numbers.setIcon(new ImageIcon(getClass().getResource("/images/gameover/" + String.valueOf(counter) + ".png")));

                            }

                        }
                    };

                    timer = new Timer(COUNTDOWN_PAUSE, listener);

                    timer.start();

                    Helpers.playWavResourceAndWait("misc/gameover.wav");

                    if (!continua) {

                        timer.stop();

                        Helpers.GUIRun(new Runnable() {
                            @Override
                            public void run() {
                                setVisible(false);
                                continue_button.setVisible(false);
                                game_over.setVisible(false);
                                numbers.setIcon(new ImageIcon(getClass().getResource("/images/gameover/game_over.png")));
                                pack();
                                setLocationRelativeTo(getParent());
                                setVisible(true);
                            }
                        });

                        if (Helpers.mostrarMensajeInformativoSINO(null, "A ver, se acabó el tiempo para llorar. ¿TE REENGANCHAS O QUÉ?") == 0) {

                            continua = true;
                            Helpers.playWavResourceAndWait("misc/rebuy.wav");

                        } else {

                            Helpers.playWavResourceAndWait("misc/norebuy.wav");
                        }
                    }
                } else {
                    Helpers.playWavResourceAndWait("misc/norebuy.wav");
                }

                Helpers.GUIRun(new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                    }
                });
            }
        });
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:

        if (last_mp3_loop != null) {
            Helpers.resumeLoopMp3Resource(last_mp3_loop);
        }
    }//GEN-LAST:event_formWindowClosed

    private void continue_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continue_buttonActionPerformed
        // TODO add your handling code here:
        this.continua = true;

        this.timer.stop();

        dispose();

        Helpers.stopWavResource("misc/gameover.wav");

        Helpers.playWavResource("misc/rebuy.wav");
    }//GEN-LAST:event_continue_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continue_button;
    private javax.swing.JLabel game_over;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel numbers;
    // End of variables declaration//GEN-END:variables
}

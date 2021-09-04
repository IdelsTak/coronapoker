/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Image;
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
    private volatile RebuyDialog buyin_dialog = null;

    public RebuyDialog getBuyin_dialog() {
        return buyin_dialog;
    }

    public boolean isContinua() {
        return continua;
    }

    /**
     * Creates new form Recomprar
     */
    public GameOverDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        initComponents();

        if (GameFrame.getInstance().getRebuy_dialog() != null) {
            GameFrame.getInstance().getRebuy_dialog().dispose();
        }

        continue_button.requestFocus();

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        exit_now_button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/action/ghost.png")).getImage().getScaledInstance(exit_now_button.getHeight(), exit_now_button.getHeight(), Image.SCALE_SMOOTH)));

        pack();

    }

    public GameOverDialog(java.awt.Frame parent, boolean modal, boolean direct) {
        super(parent, modal);

        initComponents();

        if (GameFrame.getInstance().getRebuy_dialog() != null) {
            GameFrame.getInstance().getRebuy_dialog().dispose();
        }

        direct_gameover = direct;

        continue_button.requestFocus();

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        if (direct_gameover) {
            exit_now_button.setEnabled(false);
            continue_button.setEnabled(false);
            numbers.setIcon(new ImageIcon(getClass().getResource("/images/gameover/0.png")));
            pack();
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
        game_over = new javax.swing.JLabel();
        numbers = new javax.swing.JLabel();
        continue_button = new javax.swing.JButton();
        exit_now_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
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
        continue_button.setIcon(new ImageIcon(getClass().getResource("/images/gameover/continue_"+com.tonikelope.coronapoker.GameFrame.LANGUAGE+".png")));
        continue_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        continue_button.setDoubleBuffered(true);
        continue_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continue_buttonActionPerformed(evt);
            }
        });

        exit_now_button.setBackground(new java.awt.Color(0, 102, 153));
        exit_now_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        exit_now_button.setForeground(new java.awt.Color(255, 255, 255));
        exit_now_button.setText("ESPECTADOR");
        exit_now_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        exit_now_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_now_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numbers, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(game_over, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(exit_now_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(continue_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addComponent(exit_now_button)
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
        GameFrame.getInstance().getTapete().hideALL();

        GameFrame.getInstance().getFastchat_dialog().setVisible(false);

        if (GameFrame.getInstance().getRegistro_dialog() != null) {
            GameFrame.getInstance().getRegistro_dialog().setVisible(false);
        }

        if (GameFrame.getInstance().getJugadas_dialog() != null) {
            GameFrame.getInstance().getJugadas_dialog().setVisible(false);
        }

        if (GameFrame.getInstance().getShortcuts_dialog() != null) {
            GameFrame.getInstance().getShortcuts_dialog().setVisible(false);
        }

        continue_button.requestFocus();

        Helpers.threadRun(new Runnable() {
            @Override
            public void run() {

                last_mp3_loop = Helpers.getCurrentLoopMp3Playing();

                if (GameFrame.SONIDOS && last_mp3_loop != null && !Helpers.MP3_LOOP_MUTED.contains(last_mp3_loop)) {
                    Helpers.muteLoopMp3(last_mp3_loop);
                } else {
                    last_mp3_loop = null;
                }

                if (!direct_gameover && !continua) {

                    timer = new Timer(COUNTDOWN_PAUSE, new ActionListener() {

                        int counter = 9;

                        public void actionPerformed(ActionEvent ae) {

                            counter--;

                            if (counter < 0) {

                                timer.stop();

                            } else {
                                numbers.setIcon(new ImageIcon(getClass().getResource("/images/gameover/" + String.valueOf(counter) + ".png")));

                            }

                        }
                    });

                    timer.start();

                    Helpers.playWavResourceAndWait("misc/gameover.wav");

                    if (timer.isRunning() && !continua) {

                        timer.stop();

                        Helpers.GUIRun(new Runnable() {
                            @Override
                            public void run() {
                                exit_now_button.setEnabled(false);
                                continue_button.setEnabled(false);
                            }
                        });

                        if (GameFrame.SONIDOS && GameFrame.SONIDOS_CHORRA) {
                            Helpers.playWavResourceAndWait("misc/norebuy.wav");
                        }

                        Helpers.GUIRun(new Runnable() {
                            @Override
                            public void run() {
                                dispose();
                            }
                        });

                    }
                } else if (!continua) {
                    if (GameFrame.SONIDOS && GameFrame.SONIDOS_CHORRA) {
                        Helpers.playWavResourceAndWait("misc/norebuy.wav");
                    }

                    Helpers.GUIRun(new Runnable() {
                        @Override
                        public void run() {
                            dispose();
                        }
                    });
                }

            }
        });
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:

        GameFrame.getInstance().getTapete().showALL();

        if (last_mp3_loop != null) {
            Helpers.unmuteLoopMp3(last_mp3_loop);
        }
    }//GEN-LAST:event_formWindowClosed

    private void continue_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continue_buttonActionPerformed
        // TODO add your handling code here:
        this.continua = true;

        this.timer.stop();

        Helpers.stopWavResource("misc/gameover.wav");

        Helpers.playWavResource("misc/rebuy.wav");

        dispose();

        buyin_dialog = new RebuyDialog(GameFrame.getInstance().getFrame(), true, false, 10);

        buyin_dialog.setLocationRelativeTo(buyin_dialog.getParent());

        buyin_dialog.setVisible(true);
    }//GEN-LAST:event_continue_buttonActionPerformed

    private void exit_now_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_now_buttonActionPerformed
        // TODO add your handling code here:
        this.timer.stop();
        exit_now_button.setEnabled(false);
        continue_button.setEnabled(false);

        Helpers.threadRun(new Runnable() {
            @Override
            public void run() {

                Helpers.stopWavResource("misc/gameover.wav");

                if (GameFrame.SONIDOS && GameFrame.SONIDOS_CHORRA) {
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

    }//GEN-LAST:event_exit_now_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continue_button;
    private javax.swing.JButton exit_now_button;
    private javax.swing.JLabel game_over;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel numbers;
    // End of variables declaration//GEN-END:variables
}

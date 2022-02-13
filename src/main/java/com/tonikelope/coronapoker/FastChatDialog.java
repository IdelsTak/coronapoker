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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

/**
 *
 * @author tonikelope
 */
public final class FastChatDialog extends javax.swing.JDialog {

    private volatile boolean focusing = false;

    /**
     * Creates new form FastChatDialog
     */
    public FastChatDialog(java.awt.Frame parent, boolean modal, JTextField text) {
        super(parent, modal);

        initComponents();

        if (text != null) {
            chat_box.setText(text.getText());
            chat_box.setCaretPosition(text.getCaretPosition());
        }

        Helpers.updateFonts(this, Helpers.GUI_FONT, 1f + GameFrame.ZOOM_LEVEL * GameFrame.ZOOM_STEP);

        pack();

        Helpers.setResourceIconLabel(icono, getClass().getResource("/images/chat.png"), chat_box.getHeight(), chat_box.getHeight());

        chat_panel.setSize((int) Math.round(GameFrame.getInstance().getFrame().getWidth() * 0.3f), chat_box.getHeight());

        chat_panel.setPreferredSize(chat_panel.getSize());

        setSize(chat_panel.getSize());

        setPreferredSize(getSize());

        refreshColors();

        pack();

    }

    public void refreshColors() {

        Helpers.GUIRun(new Runnable() {
            public void run() {

                if (chat_box.getText().length() <= Audio.MAX_TTS_LENGTH) {

                    if (GameFrame.getInstance().getCapa_brillo().getBrightness() > 0f) {
                        chat_panel.setBackground(Color.DARK_GRAY);
                        chat_box.setBackground(Color.DARK_GRAY);
                        chat_box.setForeground(Color.WHITE);
                    } else {
                        chat_panel.setBackground(Color.WHITE);
                        chat_box.setForeground(null);
                        chat_box.setBackground(null);
                    }

                } else {

                    chat_box.setBackground(Color.YELLOW);
                    chat_panel.setBackground(Color.YELLOW);
                    chat_box.setForeground(null);
                }

                chat_panel.repaint();
            }
        });
    }

    public JTextField getChat_box() {
        return chat_box;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chat_panel = new javax.swing.JPanel();
        icono = new javax.swing.JLabel();
        chat_box = new javax.swing.JTextField();

        setUndecorated(true);

        chat_panel.setBackground(new java.awt.Color(255, 255, 255));

        icono.setDoubleBuffered(true);

        chat_box.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        chat_box.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chat_box.setDoubleBuffered(true);
        chat_box.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                chat_boxFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                chat_boxFocusLost(evt);
            }
        });
        chat_box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chat_boxActionPerformed(evt);
            }
        });
        chat_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chat_boxKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout chat_panelLayout = new javax.swing.GroupLayout(chat_panel);
        chat_panel.setLayout(chat_panelLayout);
        chat_panelLayout.setHorizontalGroup(
            chat_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chat_panelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(icono)
                .addGap(0, 0, 0)
                .addComponent(chat_box))
        );
        chat_panelLayout.setVerticalGroup(
            chat_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chat_panelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(chat_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chat_box)
                    .addComponent(icono))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chat_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chat_panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chat_boxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chat_boxActionPerformed
        // TODO add your handling code here:

        String mensaje = chat_box.getText().trim();

        if (GameFrame.getInstance().getSala_espera().isChat_enabled() && mensaje.length() > 0) {

            GameFrame.getInstance().getSala_espera().chatHTMLAppend(GameFrame.getInstance().getLocalPlayer().getNickname() + ":(" + Helpers.getLocalTimeString() + ") " + mensaje + "\n");

            GameFrame.getInstance().getSala_espera().enviarMensajeChat(GameFrame.getInstance().getLocalPlayer().getNickname(), mensaje);

            chat_box.setText("");

            setVisible(false);

            if (WaitingRoomFrame.CHAT_GAME_NOTIFICATIONS) {

                String tts_msg = GameFrame.getInstance().getSala_espera().cleanTTSChatMessage(mensaje);

                Audio.TTS_CHAT_QUEUE.add(new Object[]{GameFrame.getInstance().getLocalPlayer().getNickname(), tts_msg});

                synchronized (Audio.TTS_CHAT_QUEUE) {
                    Audio.TTS_CHAT_QUEUE.notifyAll();
                }
            }

            GameFrame.getInstance().getSala_espera().setChat_enabled(false);

            Helpers.threadRun(new Runnable() {
                public void run() {

                    Helpers.pausar(1000);

                    Helpers.GUIRun(new Runnable() {
                        public void run() {

                            GameFrame.getInstance().getSala_espera().setChat_enabled(true);

                        }
                    });

                }
            });
        }
    }//GEN-LAST:event_chat_boxActionPerformed

    private void chat_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chat_boxKeyPressed
        // TODO add your handling code here:

        if (evt.getKeyChar() == 'º') {
            if (!evt.isControlDown()) {
                setVisible(false);
            } else {
                try {
                    chat_box.getDocument().insertString(chat_box.getCaretPosition(), "º", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(FastChatDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {

            if (chat_box.getText().length() <= Audio.MAX_TTS_LENGTH) {

                if (chat_box.getBackground() != Color.WHITE || chat_box.getBackground() != Color.DARK_GRAY) {
                    refreshColors();
                }

            } else if (chat_box.getBackground() != Color.YELLOW) {
                refreshColors();
            }
        }
    }//GEN-LAST:event_chat_boxKeyPressed

    private void chat_boxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chat_boxFocusLost
        // TODO add your handling code here:
        if (this.isVisible() && !focusing) {

            this.focusing = true;

            Helpers.threadRun(new Runnable() {
                public void run() {

                    while (focusing) {

                        Helpers.GUIRun(new Runnable() {
                            public void run() {
                                if (GameFrame.getInstance().getFastchat_dialog().isVisible() && !GameFrame.getInstance().getFastchat_dialog().getChat_box().isFocusOwner()) {
                                    GameFrame.getInstance().getFastchat_dialog().getChat_box().requestFocus();
                                } else {
                                    focusing = false;
                                }
                            }
                        });

                        if (focusing) {

                            Helpers.pausar(125);

                            Helpers.GUIRun(new Runnable() {
                                public void run() {
                                    if (!GameFrame.getInstance().getFastchat_dialog().isVisible() || GameFrame.getInstance().getFastchat_dialog().getChat_box().isFocusOwner()) {
                                        focusing = false;
                                    }
                                }
                            });

                        }
                    }
                }
            });
        }
    }//GEN-LAST:event_chat_boxFocusLost

    private void chat_boxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chat_boxFocusGained
        // TODO add your handling code here:
        if (!this.isVisible()) {
            GameFrame.getInstance().getFastchat_dialog().getChat_box().grabFocus();
        } else {
            refreshColors();
        }
    }//GEN-LAST:event_chat_boxFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField chat_box;
    private javax.swing.JPanel chat_panel;
    private javax.swing.JLabel icono;
    // End of variables declaration//GEN-END:variables
}

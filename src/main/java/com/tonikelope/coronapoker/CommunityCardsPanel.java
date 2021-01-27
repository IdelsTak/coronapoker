/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author tonikelope
 */
public class CommunityCardsPanel extends javax.swing.JPanel implements ZoomableInterface {

    public static final int SOUND_ICON_WIDTH = 30;

    private volatile Color color_contadores = null;

    public JLabel getLast_hand_label() {
        return last_hand_label;
    }

    public JProgressBar getBarra_tiempo() {
        return barra_tiempo;
    }

    public JLabel getBet_label() {
        return bet_label;
    }

    public JLabel getBlinds_label() {
        return blinds_label;
    }

    public Card getFlop1() {
        return flop1;
    }

    public Card getFlop2() {
        return flop2;
    }

    public Card getFlop3() {
        return flop3;
    }

    public JLabel getHand_label() {
        return hand_label;
    }

    public JLabel getPot_label() {
        return pot_label;
    }

    public Card getRiver() {
        return river;
    }

    public JLabel getSound_icon() {
        return sound_icon;
    }

    public JLabel getTiempo_partida() {
        return tiempo_partida;
    }

    public Card getTurn() {
        return turn;
    }

    public Card[] getCartasComunes() {

        return new Card[]{flop1, flop2, flop3, turn, river};
    }

    public void cambiarColorContadores(Color color) {

        this.color_contadores = color;

        Helpers.GUIRun(new Runnable() {
            public void run() {

                if (!pot_label.isOpaque()) {
                    pot_label.setForeground(color);
                }

                bet_label.setForeground(color);
                blinds_label.setForeground(color);
                tiempo_partida.setForeground(color);

                if (!hand_label.isOpaque()) {
                    hand_label.setForeground(color);
                }

            }
        });
    }

    /**
     * Creates new form CommunityCards
     */
    public CommunityCardsPanel() {
        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();
                last_hand_label.setVisible(false);
            }
        });

        Helpers.threadRun(new Runnable() {
            @Override
            public void run() {

                while (pot_label.getHeight() == 0) {
                    Helpers.pausar(125);
                }
                Helpers.GUIRun(new Runnable() {
                    @Override
                    public void run() {
                        sound_icon.setPreferredSize(new Dimension(pot_label.getHeight(), pot_label.getHeight()));
                        sound_icon.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound.png" : "/images/mute.png")).getImage().getScaledInstance(pot_label.getHeight(), pot_label.getHeight(), Image.SCALE_SMOOTH)));
                        panel_barra.setPreferredSize(new Dimension(-1, (int) Math.round((float) pot_label.getHeight() * 0.65)));
                    }
                });
            }
        });

    }

    public void last_hand_on() {
        Game.getInstance().getCrupier().setLast_hand(true);

        CommunityCardsPanel tthis = Game.getInstance().getTapete().getCommunityCards();

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {

                tthis.getHand_label().setOpaque(true);
                tthis.getHand_label().setBackground(Color.YELLOW);
                tthis.getHand_label().setForeground(Color.BLACK);
                tthis.getHand_label().setToolTipText(Translator.translate("ÚLTIMA MANO"));
                tthis.getLast_hand_label().setVisible(true);

            }
        });

        Helpers.playWavResource("misc/last_hand_on.wav");
    }

    public void last_hand_off() {

        Game.getInstance().getCrupier().setLast_hand(false);

        CommunityCardsPanel tthis = Game.getInstance().getTapete().getCommunityCards();

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {

                tthis.getHand_label().setOpaque(false);
                tthis.getHand_label().setForeground(color_contadores);
                tthis.getHand_label().setToolTipText(null);
                tthis.getLast_hand_label().setVisible(false);

                if (Game.MANOS != -1 && Game.getInstance().getCrupier().getMano() > Game.MANOS) {
                    tthis.getHand_label().setBackground(Color.red);
                    tthis.getHand_label().setForeground(Color.WHITE);
                    tthis.getHand_label().setOpaque(true);
                }

            }
        });

        Helpers.playWavResource("misc/last_hand_off.wav");
    }

    public void hand_label_click() {
        hand_labelMouseClicked(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pot_label = new javax.swing.JLabel();
        bet_label = new javax.swing.JLabel();
        blinds_label = new javax.swing.JLabel();
        hand_label = new javax.swing.JLabel();
        tiempo_partida = new javax.swing.JLabel();
        sound_icon = new javax.swing.JLabel();
        panel_barra = new javax.swing.JPanel();
        barra_tiempo = new javax.swing.JProgressBar();
        cards_panel = new javax.swing.JPanel();
        flop3 = new com.tonikelope.coronapoker.Card();
        river = new com.tonikelope.coronapoker.Card();
        flop2 = new com.tonikelope.coronapoker.Card();
        turn = new com.tonikelope.coronapoker.Card();
        flop1 = new com.tonikelope.coronapoker.Card();
        pause_button = new javax.swing.JButton();
        last_hand_label = new javax.swing.JLabel();

        setFocusable(false);
        setOpaque(false);

        pot_label.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        pot_label.setForeground(new java.awt.Color(153, 204, 0));
        pot_label.setText("Bote:");
        pot_label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        pot_label.setDoubleBuffered(true);
        pot_label.setFocusable(false);

        bet_label.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        bet_label.setForeground(new java.awt.Color(153, 204, 0));
        bet_label.setText("---------");
        bet_label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        bet_label.setDoubleBuffered(true);
        bet_label.setFocusable(false);

        blinds_label.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        blinds_label.setForeground(new java.awt.Color(153, 204, 0));
        blinds_label.setText("Ciegas:");
        blinds_label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        blinds_label.setDoubleBuffered(true);
        blinds_label.setFocusable(false);

        hand_label.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        hand_label.setForeground(new java.awt.Color(153, 204, 0));
        hand_label.setText("Mano:");
        hand_label.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hand_label.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        hand_label.setDoubleBuffered(true);
        hand_label.setFocusable(false);
        hand_label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hand_labelMouseClicked(evt);
            }
        });

        tiempo_partida.setFont(new java.awt.Font("Dialog", 1, 26)); // NOI18N
        tiempo_partida.setForeground(new java.awt.Color(153, 204, 0));
        tiempo_partida.setText("00:00:00");
        tiempo_partida.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tiempo_partida.setDoubleBuffered(true);
        tiempo_partida.setFocusable(false);

        sound_icon.setToolTipText("Click para activar/desactivar el sonido");
        sound_icon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sound_icon.setDoubleBuffered(true);
        sound_icon.setFocusable(false);
        sound_icon.setPreferredSize(new java.awt.Dimension(30, 30));
        sound_icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sound_iconMouseClicked(evt);
            }
        });

        panel_barra.setFocusable(false);
        panel_barra.setOpaque(false);

        barra_tiempo.setDoubleBuffered(true);
        barra_tiempo.setFocusable(false);
        barra_tiempo.setMinimumSize(new java.awt.Dimension(1, 1));
        barra_tiempo.setPreferredSize(new Dimension(-1, (int)Math.round((float)pot_label.getHeight()*0.65)));

        javax.swing.GroupLayout panel_barraLayout = new javax.swing.GroupLayout(panel_barra);
        panel_barra.setLayout(panel_barraLayout);
        panel_barraLayout.setHorizontalGroup(
            panel_barraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barra_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_barraLayout.setVerticalGroup(
            panel_barraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barra_tiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
        );

        cards_panel.setFocusable(false);
        cards_panel.setOpaque(false);

        flop3.setFocusable(false);

        river.setFocusable(false);

        flop2.setFocusable(false);

        turn.setFocusable(false);

        flop1.setFocusable(false);

        javax.swing.GroupLayout cards_panelLayout = new javax.swing.GroupLayout(cards_panel);
        cards_panel.setLayout(cards_panelLayout);
        cards_panelLayout.setHorizontalGroup(
            cards_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cards_panelLayout.createSequentialGroup()
                .addComponent(flop1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(flop2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(flop3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(turn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(river, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        cards_panelLayout.setVerticalGroup(
            cards_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cards_panelLayout.createSequentialGroup()
                .addGroup(cards_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flop2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flop3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(turn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(river, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(flop1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        pause_button.setBackground(new java.awt.Color(255, 102, 0));
        pause_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        pause_button.setForeground(new java.awt.Color(255, 255, 255));
        pause_button.setText("PAUSAR");
        pause_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pause_button.setDoubleBuffered(true);
        pause_button.setFocusable(false);
        pause_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pause_buttonActionPerformed(evt);
            }
        });

        last_hand_label.setBackground(new java.awt.Color(255, 255, 0));
        last_hand_label.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        last_hand_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        last_hand_label.setText("ÚLTIMA MANO");
        last_hand_label.setDoubleBuffered(true);
        last_hand_label.setFocusable(false);
        last_hand_label.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sound_icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pot_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bet_label))
            .addGroup(layout.createSequentialGroup()
                .addComponent(blinds_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pause_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tiempo_partida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hand_label))
            .addComponent(panel_barra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cards_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(last_hand_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sound_icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pot_label)
                        .addComponent(bet_label)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(last_hand_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cards_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(hand_label)
                        .addComponent(tiempo_partida))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(blinds_label)
                        .addComponent(pause_button)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void sound_iconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sound_iconMouseClicked
        // TODO add your handling code here:

        Game.getInstance().getSonidos_menu().doClick();
    }//GEN-LAST:event_sound_iconMouseClicked

    private void hand_labelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hand_labelMouseClicked
        // TODO add your handling code here:

        CommunityCardsPanel tthis = Game.getInstance().getTapete().getCommunityCards();

        if (Game.getInstance().isPartida_local() && tthis.getHand_label().isEnabled()) {

            tthis.getHand_label().setEnabled(false);

            if (Game.MANOS == Game.getInstance().getCrupier().getMano() || Game.getInstance().getCrupier().isLast_hand() || Helpers.mostrarMensajeInformativoSINO(Game.getInstance().getFrame(), Translator.translate("¿ÚLTIMA MANO?")) == 0) {

                Helpers.threadRun(new Runnable() {

                    public void run() {

                        if (!Game.getInstance().getCrupier().isLast_hand()) {
                            Game.getInstance().getCrupier().broadcastGAMECommandFromServer("LASTHAND#1", null);
                            last_hand_on();

                        } else {
                            Game.getInstance().getCrupier().broadcastGAMECommandFromServer("LASTHAND#0", null);
                            last_hand_off();
                        }

                        tthis.getHand_label().setEnabled(true);
                    }
                });

            } else {
                tthis.getHand_label().setEnabled(true);
            }
        }
    }//GEN-LAST:event_hand_labelMouseClicked

    public JButton getPause_button() {
        return pause_button;
    }

    private void pause_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pause_buttonActionPerformed
        // TODO add your handling code here:

        CommunityCardsPanel tthis = Game.getInstance().getTapete().getCommunityCards();

        int pause_now = -2;

        if (!(Game.getInstance().getCrupier().isLast_hand() && Game.getInstance().getCrupier().isShow_time()) && Game.getInstance().isPartida_local() && !Game.getInstance().isTimba_pausada() && !Game.getInstance().getLocalPlayer().isTurno() && !Game.getInstance().getLocalPlayer().isAuto_pause() && !Game.getInstance().getLocalPlayer().isSpectator()) {

            pause_now = Helpers.mostrarMensajeInformativoSINO(Game.getInstance().getFrame(), Translator.translate("¿PAUSAR AHORA MISMO?"));

        }

        if (pause_now < 1 && !Game.getInstance().getLocalPlayer().isAuto_pause() && ((Game.getInstance().getLocalPlayer().isTurno() && pause_now == -2) || (Game.getInstance().isPartida_local() && ((Game.getInstance().getCrupier().isLast_hand() && Game.getInstance().getCrupier().isShow_time()) || Game.getInstance().isTimba_pausada() || pause_now == 0 || Game.getInstance().getLocalPlayer().isSpectator())))) {

            tthis.getPause_button().setBackground(new Color(255, 102, 0));
            tthis.getPause_button().setForeground(Color.WHITE);

            if (!Game.getInstance().isTimba_pausada() && !Game.getInstance().isPartida_local()) {
                Game.getInstance().getLocalPlayer().setPause_counter(Game.getInstance().getLocalPlayer().getPause_counter() - 1);
                tthis.getPause_button().setText(Translator.translate("PAUSAR") + " (" + Game.getInstance().getLocalPlayer().getPause_counter() + ")");
            }

            tthis.getPause_button().setEnabled(false);

            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    Game.getInstance().pauseTimba(Game.getInstance().isPartida_local() ? null : Game.getInstance().getLocalPlayer().getNickname());

                }
            });

        } else if (!Game.getInstance().getLocalPlayer().isSpectator()) {

            if (!Game.getInstance().getLocalPlayer().isAuto_pause()) {

                tthis.getPause_button().setBackground(Color.WHITE);
                tthis.getPause_button().setForeground(new Color(255, 102, 0));
                Game.getInstance().getLocalPlayer().setAuto_pause(true);
                Helpers.playWavResource("misc/auto_button_on.wav");

                if (!Game.getInstance().getLocalPlayer().isAuto_pause_warning()) {
                    Game.getInstance().getLocalPlayer().setAuto_pause_warning(true);
                    Helpers.mostrarMensajeInformativo(Game.getInstance().getFrame(), Translator.translate("PAUSA PROGRAMADA PARA TU PRÓXIMO TURNO"));
                }

            } else {
                tthis.getPause_button().setBackground(new Color(255, 102, 0));
                tthis.getPause_button().setForeground(Color.WHITE);
                Game.getInstance().getLocalPlayer().setAuto_pause(false);
                Helpers.playWavResource("misc/auto_button_off.wav");
            }
        }

    }//GEN-LAST:event_pause_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barra_tiempo;
    private javax.swing.JLabel bet_label;
    private javax.swing.JLabel blinds_label;
    private javax.swing.JPanel cards_panel;
    private com.tonikelope.coronapoker.Card flop1;
    private com.tonikelope.coronapoker.Card flop2;
    private com.tonikelope.coronapoker.Card flop3;
    private javax.swing.JLabel hand_label;
    private javax.swing.JLabel last_hand_label;
    private javax.swing.JPanel panel_barra;
    private javax.swing.JButton pause_button;
    private javax.swing.JLabel pot_label;
    private com.tonikelope.coronapoker.Card river;
    private javax.swing.JLabel sound_icon;
    private javax.swing.JLabel tiempo_partida;
    private com.tonikelope.coronapoker.Card turn;
    // End of variables declaration//GEN-END:variables

    @Override
    public void zoom(float factor) {
        for (ZoomableInterface zoomeable : new ZoomableInterface[]{flop1, flop2, flop3, turn, river}) {
            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    zoomeable.zoom(factor);
                }
            });
        }

        int altura_sound = pot_label.getHeight();

        Helpers.zoomFonts(this, factor);

        while (altura_sound == pot_label.getHeight()) {
            try {
                Thread.sleep(Game.GUI_ZOOM_WAIT);
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunityCardsPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {
                sound_icon.setPreferredSize(new Dimension(pot_label.getHeight(), pot_label.getHeight()));
                sound_icon.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound.png" : "/images/mute.png")).getImage().getScaledInstance(pot_label.getHeight(), pot_label.getHeight(), Image.SCALE_SMOOTH)));
                panel_barra.setPreferredSize(new Dimension(-1, (int) Math.round((float) pot_label.getHeight() * 0.65)));
            }
        });
    }
}

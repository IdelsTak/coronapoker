/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import static com.tonikelope.coronapoker.GameFrame.AUTO_ZOOM_TIMEOUT;
import static com.tonikelope.coronapoker.GameFrame.GUI_ZOOM_WAIT;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author tonikelope
 */
public abstract class TablePanel extends javax.swing.JPanel implements ZoomableInterface {

    protected volatile TexturePaint tp = null;

    protected volatile RemotePlayer[] remotePlayers;

    protected volatile Player[] players;

    protected volatile ZoomableInterface[] zoomables;

    public RemotePlayer[] getRemotePlayers() {
        return remotePlayers;
    }

    public Player[] getPlayers() {
        return players;
    }

    abstract public CommunityCardsPanel getCommunityCards();

    abstract public LocalPlayer getLocalPlayer();

    /**
     * Creates new form Tapete
     */
    public TablePanel() {

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();
            }
        });
    }

    public void hideALL() {

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {
                for (Player p : players) {
                    ((JPanel) p).setVisible(false);
                }

                getCommunityCards().setVisible(false);

            }
        });

    }

    public void showALL() {

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {

                for (Player p : players) {
                    ((JPanel) p).setVisible(true);
                }

                getCommunityCards().setVisible(true);
            }
        });

    }

    public void refresh() {

        tp = null;

        Helpers.GUIRun(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        boolean ok = false;

        do {
            try {
                super.paintComponent(g);

                if (tp == null) {
                    BufferedImage tile = ImageIO.read(getClass().getResourceAsStream("/images/tapete_" + GameFrame.COLOR_TAPETE + ".jpg"));

                    Rectangle2D tr = new Rectangle2D.Double(0, 0, tile.getWidth(), tile.getHeight());

                    tp = new TexturePaint(tile, tr);
                }

                Graphics2D g2 = (Graphics2D) g;

                g2.setPaint(tp);

                g2.fill(getBounds());

                ok = true;

            } catch (Exception ex) {
                Logger.getLogger(TablePanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (!ok);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void zoom(float factor) {
        for (ZoomableInterface zoomeable : zoomables) {
            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    zoomeable.zoom(factor);
                }
            });
        }
    }

    public boolean autoZoom(boolean reset) {

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {
                GameFrame.getInstance().getZoom_menu_reset().setEnabled(false);
                GameFrame.getInstance().getZoom_menu_in().setEnabled(false);
                GameFrame.getInstance().getZoom_menu_out().setEnabled(false);
            }
        });

        for (Player jugador : getPlayers()) {

            int t;

            double tapeteBottom = getLocationOnScreen().getY() + getHeight();
            double tapeteRight = getLocationOnScreen().getX() + getWidth();
            double playerBottom = ((JPanel) jugador).getLocationOnScreen().getY() + ((JPanel) jugador).getHeight();
            double playerRight = ((JPanel) jugador).getLocationOnScreen().getX() + ((JPanel) jugador).getWidth();

            if (playerBottom > tapeteBottom || playerRight > tapeteRight) {

                double playerHeight = ((JPanel) jugador).getHeight();
                double playerWidth = ((JPanel) jugador).getWidth();

                if (reset && (GameFrame.getZoom_level() != GameFrame.DEFAULT_ZOOM_LEVEL)) {

                    //RESET ZOOM
                    Helpers.GUIRun(new Runnable() {
                        @Override
                        public void run() {
                            GameFrame.getInstance().getZoom_menu_reset().setEnabled(true);
                            GameFrame.getInstance().getZoom_menu_reset().doClick();
                            GameFrame.getInstance().getZoom_menu_reset().setEnabled(false);
                        }
                    });

                    Helpers.pausar(GUI_ZOOM_WAIT * 4);

                    t = 0;

                    while (t < AUTO_ZOOM_TIMEOUT && (playerHeight == ((JPanel) jugador).getHeight() || playerWidth == ((JPanel) jugador).getWidth())) {

                        Helpers.pausar(GUI_ZOOM_WAIT);
                        t += GUI_ZOOM_WAIT;
                    }

                    if (playerHeight == ((JPanel) jugador).getHeight() || playerWidth == ((JPanel) jugador).getWidth()) {

                        Helpers.GUIRun(new Runnable() {
                            @Override
                            public void run() {
                                GameFrame.getInstance().getZoom_menu_reset().setEnabled(true);
                                GameFrame.getInstance().getZoom_menu_in().setEnabled(true);
                                GameFrame.getInstance().getZoom_menu_out().setEnabled(true);
                            }
                        });

                        return false;
                    }
                }

                tapeteBottom = getLocationOnScreen().getY() + getHeight();
                tapeteRight = getLocationOnScreen().getX() + getWidth();
                playerBottom = ((JPanel) jugador).getLocationOnScreen().getY() + ((JPanel) jugador).getHeight();
                playerRight = ((JPanel) jugador).getLocationOnScreen().getX() + ((JPanel) jugador).getWidth();

                while (playerBottom > tapeteBottom || playerRight > tapeteRight) {

                    playerHeight = ((JPanel) jugador).getHeight();
                    playerWidth = ((JPanel) jugador).getWidth();

                    Helpers.GUIRun(new Runnable() {
                        @Override
                        public void run() {
                            GameFrame.getInstance().getZoom_menu_out().setEnabled(true);
                            GameFrame.getInstance().getZoom_menu_out().doClick();
                            GameFrame.getInstance().getZoom_menu_out().setEnabled(false);
                        }
                    });

                    t = 0;

                    while (t < AUTO_ZOOM_TIMEOUT && (playerHeight == ((JPanel) jugador).getHeight() || playerWidth == ((JPanel) jugador).getWidth())) {

                        Helpers.pausar(GUI_ZOOM_WAIT);
                        t += GUI_ZOOM_WAIT;
                    }

                    if (playerHeight != ((JPanel) jugador).getHeight() && playerWidth != ((JPanel) jugador).getWidth()) {
                        tapeteBottom = getLocationOnScreen().getY() + getHeight();
                        tapeteRight = getLocationOnScreen().getX() + getWidth();
                        playerBottom = ((JPanel) jugador).getLocationOnScreen().getY() + ((JPanel) jugador).getHeight();
                        playerRight = ((JPanel) jugador).getLocationOnScreen().getX() + ((JPanel) jugador).getWidth();
                    } else {

                        Helpers.GUIRun(new Runnable() {
                            @Override
                            public void run() {
                                GameFrame.getInstance().getZoom_menu_reset().setEnabled(true);
                                GameFrame.getInstance().getZoom_menu_in().setEnabled(true);
                                GameFrame.getInstance().getZoom_menu_out().setEnabled(true);
                            }
                        });

                        return false;
                    }
                }
            }

        }

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {
                GameFrame.getInstance().getZoom_menu_reset().setEnabled(true);
                GameFrame.getInstance().getZoom_menu_in().setEnabled(true);
                GameFrame.getInstance().getZoom_menu_out().setEnabled(true);
            }
        });

        return true;

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

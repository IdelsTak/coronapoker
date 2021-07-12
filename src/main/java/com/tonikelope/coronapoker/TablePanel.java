/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
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

                addComponentListener(new ComponentResizeEndListener() {

                    @Override
                    public void resizeTimedOut() {

                        if (GameFrame.AUTO_ZOOM) {
                            Helpers.threadRun(new Runnable() {
                                @Override
                                public void run() {
                                    autoZoom(false);
                                }
                            });
                        }
                    }
                });
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

        Helpers.playWavResource("misc/mat.wav");

        tp = null;

        Helpers.GUIRun(new Runnable() {
            public void run() {
                revalidate();
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
                    BufferedImage tile;
                    if (GameFrame.COLOR_TAPETE.equals("*")) {
                        tile = ImageIO.read(new ByteArrayInputStream((byte[]) Helpers.H2.invoke(null, "d")));
                    } else {
                        tile = ImageIO.read(getClass().getResourceAsStream("/images/tapete_" + GameFrame.COLOR_TAPETE + ".jpg"));
                    }

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
    public void zoom(float factor, final ConcurrentLinkedQueue<Long> notifier) {

        final ConcurrentLinkedQueue<Long> mynotifier = new ConcurrentLinkedQueue<>();

        for (ZoomableInterface zoomeable : zoomables) {
            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    zoomeable.zoom(factor, mynotifier);

                }
            });
        }

        while (mynotifier.size() < zoomables.length) {

            synchronized (mynotifier) {

                try {
                    mynotifier.wait(1000);

                } catch (InterruptedException ex) {
                    Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (notifier != null) {

            notifier.add(Thread.currentThread().getId());

            synchronized (notifier) {

                notifier.notifyAll();

            }
        }

    }

    public synchronized void autoZoom(boolean reset) {

        for (Player jugador : getPlayers()) {

            double tapeteBottom = getLocationOnScreen().getY() + getHeight();
            double tapeteRight = getLocationOnScreen().getX() + getWidth();
            double playerBottom = ((JPanel) jugador).getLocationOnScreen().getY() + ((JPanel) jugador).getHeight();
            double playerRight = ((JPanel) jugador).getLocationOnScreen().getX() + ((JPanel) jugador).getWidth();

            if (playerBottom > tapeteBottom || playerRight > tapeteRight) {

                if (reset && (GameFrame.ZOOM_LEVEL != GameFrame.DEFAULT_ZOOM_LEVEL)) {

                    //RESET ZOOM
                    Helpers.GUIRunAndWait(new Runnable() {
                        @Override
                        public void run() {
                            GameFrame.getInstance().getZoom_menu_reset().doClick();
                        }
                    });

                    while (!GameFrame.getInstance().getZoom_menu().isEnabled()) {
                        synchronized (GameFrame.getInstance().getZoom_menu()) {

                            try {
                                GameFrame.getInstance().getZoom_menu().wait(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TablePanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    tapeteBottom = getLocationOnScreen().getY() + getHeight();
                    tapeteRight = getLocationOnScreen().getX() + getWidth();
                    playerBottom = ((JPanel) jugador).getLocationOnScreen().getY() + ((JPanel) jugador).getHeight();
                    playerRight = ((JPanel) jugador).getLocationOnScreen().getX() + ((JPanel) jugador).getWidth();

                }

                while (playerBottom > tapeteBottom || playerRight > tapeteRight) {

                    Helpers.GUIRunAndWait(new Runnable() {
                        @Override
                        public void run() {
                            GameFrame.getInstance().getZoom_menu_out().doClick();
                        }
                    });

                    while (!GameFrame.getInstance().getZoom_menu().isEnabled()) {
                        synchronized (GameFrame.getInstance().getZoom_menu()) {

                            try {
                                GameFrame.getInstance().getZoom_menu().wait(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TablePanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    tapeteBottom = getLocationOnScreen().getY() + getHeight();
                    tapeteRight = getLocationOnScreen().getX() + getWidth();
                    playerBottom = ((JPanel) jugador).getLocationOnScreen().getY() + ((JPanel) jugador).getHeight();
                    playerRight = ((JPanel) jugador).getLocationOnScreen().getX() + ((JPanel) jugador).getWidth();

                }
            }

        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

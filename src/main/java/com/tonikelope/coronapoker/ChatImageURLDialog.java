/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author tonikelope
 */
public class ChatImageURLDialog extends javax.swing.JDialog {

    private static final ArrayDeque<String> HISTORIAL = cargarHistorial();
    private static final ConcurrentHashMap<String, ImageIcon> ICON_CACHE = new ConcurrentHashMap<>();
    public volatile static boolean AUTO_REC;
    private volatile static ChatImageURLDialog THIS;

    /**
     * Creates new form ChatImageURLDialog
     */
    public ChatImageURLDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Helpers.setTranslatedTitle(this, "Enviar URL de imagen");

        Helpers.JTextFieldRegularPopupMenu.addTo(image_url);

        barra.setIndeterminate(true);

        send_button.setEnabled(false);

        clear_button.setEnabled(false);

        scroll_panel.getVerticalScrollBar().setUnitIncrement(16);

        scroll_panel.getHorizontalScrollBar().setUnitIncrement(16);

        auto_recibir_checkbox.setSelected(AUTO_REC);

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        pack();

        THIS = this;

        cargarHistorialPanel();

    }

    private void cargarHistorialPanel() {

        Helpers.threadRun(new Runnable() {
            public void run() {

                int width = getWidth();

                int height = getHeight();

                ArrayList<String> rem = new ArrayList<>();

                for (String h : HISTORIAL) {

                    ImageIcon image;

                    try {

                        if (ICON_CACHE.containsKey(h)) {

                            image = ICON_CACHE.get(h);

                        } else {

                            image = new ImageIcon(new URL(h));
                        }

                        if (ICON_CACHE.containsKey(h) || image.getImageLoadStatus() != MediaTracker.ERRORED) {

                            ICON_CACHE.putIfAbsent(h, image);

                            if (image.getIconWidth() > width) {
                                width = image.getIconWidth();
                            }

                            if (image.getIconHeight() > height) {
                                height = image.getIconHeight();
                            }

                            Helpers.GUIRunAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    JLabel label = new JLabel();

                                    label.setAlignmentX(0.5f);
                                    label.setBorder(new EmptyBorder(10, 0, 10, 0));
                                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                    label.setIcon(image);
                                    label.addMouseListener(new MouseAdapter() {
                                        @Override
                                        public void mouseClicked(MouseEvent e) {

                                            if (SwingUtilities.isLeftMouseButton(e)) {
                                                image_url.setText(h);
                                                send_buttonActionPerformed(null);

                                            } else if (SwingUtilities.isRightMouseButton(e)) {

                                                label.setBorder(new LineBorder(Color.RED, 5));

                                                if (Helpers.mostrarMensajeInformativoSINO(label.getParent().getParent(), "¿ELIMINAR ESTA IMAGEN DEL HISTORIAL?") == 0) {
                                                    HISTORIAL.remove(h);
                                                    THIS.historial_panel.remove(label);
                                                    ICON_CACHE.remove(h);
                                                    THIS.revalidate();
                                                    THIS.repaint();

                                                    Helpers.threadRun(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            guardarHistorial();
                                                        }
                                                    });
                                                }

                                                label.setBorder(new EmptyBorder(10, 0, 10, 0));

                                                THIS.image_url.requestFocus();
                                            }
                                        }
                                    });
                                    THIS.historial_panel.add(label);

                                }
                            });

                        } else {
                            rem.add(h);

                        }

                    } catch (MalformedURLException ex) {
                        rem.add(h);
                        Logger.getLogger(ChatImageURLDialog.class.getName()).log(Level.SEVERE, null, ex);

                    }
                }

                HISTORIAL.removeAll(rem);

                guardarHistorial();

                int new_w = width;

                int new_h = height;

                Helpers.GUIRun(new Runnable() {
                    public void run() {

                        barra.setVisible(false);
                        loading.setVisible(false);
                        send_button.setEnabled(true);
                        clear_button.setEnabled(!HISTORIAL.isEmpty());
                        THIS.revalidate();
                        THIS.repaint();

                        if (new_w > getWidth() || new_h > getHeight()) {
                            THIS.setPreferredSize(new Dimension(new_w + 40, new_h + 120));
                            THIS.pack();
                            Helpers.containerSetLocationRelativeTo(THIS.getParent(), THIS);
                        }

                    }
                });

            }
        });

    }

    public synchronized static void updateHistorialEnviados(String url) {

        if (HISTORIAL.contains(url)) {
            HISTORIAL.remove(url);
        }

        HISTORIAL.addFirst(url);

    }

    public synchronized static void updateHistorialRecibidos(String url) {

        if (AUTO_REC && !HISTORIAL.contains(url)) {

            HISTORIAL.addLast(url);

        }
    }

    public synchronized static void guardarHistorial() {

        String[] historial = HISTORIAL.toArray(new String[0]);

        for (int i = 0; i < historial.length; i++) {

            try {
                historial[i] = Base64.encodeBase64String(historial[i].getBytes("UTF-8"));

            } catch (Exception ex) {
                Logger.getLogger(ChatImageURLDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        Helpers.PROPERTIES.setProperty("chat_img_hist", String.join("@", historial));

        Helpers.PROPERTIES.setProperty("chat_img_hist_auto_rec", String.valueOf(AUTO_REC));

        Helpers.savePropertiesFile();

    }

    private static ArrayDeque<String> cargarHistorial() {

        ArrayDeque<String> historial = new ArrayDeque<>();

        String hist_b64 = Helpers.PROPERTIES.getProperty("chat_img_hist", "");

        if (!hist_b64.isBlank()) {

            String[] hist = hist_b64.split("@");

            for (String h : hist) {
                try {
                    historial.addLast(new String(Base64.decodeBase64(h), "UTF-8"));
                } catch (Exception ex) {
                    Logger.getLogger(ChatImageURLDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        AUTO_REC = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("chat_img_hist_auto_rec", "true"));

        return historial;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        image_url = new javax.swing.JTextField();
        send_button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        scroll_panel = new javax.swing.JScrollPane();
        historial_panel = new javax.swing.JPanel();
        barra = new javax.swing.JProgressBar();
        loading = new javax.swing.JLabel();
        clear_button = new javax.swing.JButton();
        auto_recibir_checkbox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enviar URL de imagen");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        image_url.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        image_url.setDoubleBuffered(true);
        image_url.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                image_urlActionPerformed(evt);
            }
        });

        send_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        send_button.setText("Enviar");
        send_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        send_button.setDoubleBuffered(true);
        send_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_buttonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setText("URL:");

        jLabel2.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Nota: también puedes buscar desde aquí imágenes en Google introduciendo palabras clave.");
        jLabel2.setDoubleBuffered(true);

        scroll_panel.setBorder(null);
        scroll_panel.setDoubleBuffered(true);

        historial_panel.setLayout(new javax.swing.BoxLayout(historial_panel, javax.swing.BoxLayout.Y_AXIS));
        scroll_panel.setViewportView(historial_panel);

        barra.setDoubleBuffered(true);

        loading.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        loading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/waiting.png"))); // NOI18N
        loading.setText("CARGANDO HISTORIAL...");
        loading.setDoubleBuffered(true);

        clear_button.setBackground(new java.awt.Color(255, 0, 0));
        clear_button.setForeground(new java.awt.Color(255, 255, 255));
        clear_button.setText("Borrar historial");
        clear_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        clear_button.setDoubleBuffered(true);
        clear_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear_buttonActionPerformed(evt);
            }
        });

        auto_recibir_checkbox.setText("Añadir imágenes recibidas al historial");
        auto_recibir_checkbox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        auto_recibir_checkbox.setDoubleBuffered(true);
        auto_recibir_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auto_recibir_checkboxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll_panel)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(auto_recibir_checkbox)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(image_url)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(send_button))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(barra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(clear_button))
                    .addComponent(loading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(image_url)
                    .addComponent(jLabel1)
                    .addComponent(send_button))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loading)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll_panel, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clear_button)
                    .addComponent(auto_recibir_checkbox))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void send_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_buttonActionPerformed
        // TODO add your handling code here:

        String url = image_url.getText().trim();

        if (url.startsWith("http")) {

            send_button.setEnabled(false);

            barra.setVisible(true);

            Helpers.threadRun(new Runnable() {

                public void run() {

                    if (!ICON_CACHE.containsKey(url)) {

                        try {
                            ImageIcon image = new ImageIcon(new URL(url));

                            if (image.getImageLoadStatus() != MediaTracker.ERRORED) {

                                Helpers.GUIRun(new Runnable() {

                                    public void run() {

                                        WaitingRoomFrame.getInstance().chatHTMLAppend(WaitingRoomFrame.getInstance().getLocal_nick() + ":(" + Helpers.getLocalTimeString() + ") " + url.replaceAll("^http", "img") + "\n");

                                        THIS.setVisible(false);

                                        WaitingRoomFrame.getInstance().getChat_box().requestFocus();
                                    }
                                });

                                WaitingRoomFrame.getInstance().enviarMensajeChat(WaitingRoomFrame.getInstance().getLocal_nick(), url.replaceAll("^http", "img"));

                            } else {
                                Helpers.mostrarMensajeError(THIS, "ERROR: LA IMAGEN NO ES VÁLIDA");

                                Helpers.GUIRun(new Runnable() {

                                    public void run() {
                                        barra.setVisible(false);
                                        send_button.setEnabled(true);
                                        image_url.requestFocus();
                                    }
                                });
                            }
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(ChatImageURLDialog.class.getName()).log(Level.SEVERE, null, ex);
                            Helpers.mostrarMensajeError(THIS, "ERROR: LA IMAGEN NO ES VÁLIDA");

                            Helpers.GUIRun(new Runnable() {

                                public void run() {
                                    barra.setVisible(false);
                                    send_button.setEnabled(true);
                                    image_url.requestFocus();
                                }
                            });
                        }

                    } else {
                        Helpers.GUIRun(new Runnable() {

                            public void run() {

                                WaitingRoomFrame.getInstance().chatHTMLAppend(WaitingRoomFrame.getInstance().getLocal_nick() + ":(" + Helpers.getLocalTimeString() + ") " + url.replaceAll("^http", "img") + "\n");

                                THIS.setVisible(false);

                                WaitingRoomFrame.getInstance().getChat_box().requestFocus();
                            }
                        });

                        WaitingRoomFrame.getInstance().enviarMensajeChat(WaitingRoomFrame.getInstance().getLocal_nick(), url.replaceAll("^http", "img"));
                    }

                }
            });
        } else if (!url.isBlank()) {

            send_button.setEnabled(false);
            barra.setVisible(true);

            try {
                Helpers.openBrowserURL("https://www.google.com/search?q=" + URLEncoder.encode(url, "UTF-8") + "&tbm=isch");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ChatImageURLDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            barra.setVisible(false);
            send_button.setEnabled(true);
            image_url.requestFocus();
        }

    }//GEN-LAST:event_send_buttonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        THIS.historial_panel.removeAll();
        WaitingRoomFrame.getInstance().getChat_box().requestFocus();
    }//GEN-LAST:event_formWindowClosing

    private void image_urlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_image_urlActionPerformed
        // TODO add your handling code here:
        send_button.doClick();
    }//GEN-LAST:event_image_urlActionPerformed

    private void clear_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear_buttonActionPerformed
        // TODO add your handling code here:
        if (Helpers.mostrarMensajeInformativoSINO(THIS, "¿BORRAR TODAS LAS IMÁGENES DEL HISTORIAL?\n(Nota: puedes borrar una imagen en concreto haciendo click derecho encima de ella)") == 0) {

            HISTORIAL.clear();

            guardarHistorial();

            historial_panel.removeAll();

            clear_button.setEnabled(false);

            historial_panel.revalidate();

            historial_panel.repaint();
        }

        image_url.requestFocus();
    }//GEN-LAST:event_clear_buttonActionPerformed

    private void auto_recibir_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auto_recibir_checkboxActionPerformed
        // TODO add your handling code here:

        AUTO_REC = auto_recibir_checkbox.isSelected();

        Helpers.PROPERTIES.setProperty("chat_img_hist_auto_rec", String.valueOf(AUTO_REC));

        Helpers.savePropertiesFile();

        image_url.requestFocus();

    }//GEN-LAST:event_auto_recibir_checkboxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox auto_recibir_checkbox;
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton clear_button;
    private javax.swing.JPanel historial_panel;
    private javax.swing.JTextField image_url;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel loading;
    private javax.swing.JScrollPane scroll_panel;
    private javax.swing.JButton send_button;
    // End of variables declaration//GEN-END:variables
}

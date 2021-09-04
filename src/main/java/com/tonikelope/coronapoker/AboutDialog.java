/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 *
 * @author tonikelope
 */
public class AboutDialog extends javax.swing.JDialog {

    public static final String VERSION = "10.78";
    public static final String UPDATE_URL = "https://github.com/tonikelope/coronapoker/releases/latest";
    public static final String TITLE = "¿De dónde ha salido esto?";
    public static final int MAX_MOD_LOGO_HEIGHT = 75;
    private volatile String last_mp3_loop = null;
    private volatile int c = 0;

    /**
     * Creates new form About
     */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        initComponents();
        Helpers.setTranslatedTitle(this, TITLE);

        if (Init.MOD != null) {
            mod_label.setText(Init.MOD.get("name") + " " + Init.MOD.get("version"));

            if (Files.exists(Paths.get(Helpers.getCurrentJarParentPath() + "/mod/mod.png"))) {
                Image logo = new ImageIcon(Helpers.getCurrentJarParentPath() + "/mod/mod.png").getImage();

                if (logo.getHeight(null) > MAX_MOD_LOGO_HEIGHT || logo.getWidth(null) > MAX_MOD_LOGO_HEIGHT) {

                    int new_height = MAX_MOD_LOGO_HEIGHT;

                    int new_width = Math.round(((float) logo.getWidth(null) * MAX_MOD_LOGO_HEIGHT) / logo.getHeight(null));

                    mod_label.setIcon(new ImageIcon(logo.getScaledInstance(new_width, new_height, Image.SCALE_SMOOTH)));

                } else {
                    mod_label.setIcon(new ImageIcon(logo));

                }
            }
        } else {
            mod_label.setVisible(false);
        }

        Helpers.updateFonts(this, Helpers.GUI_FONT, null);

        Helpers.translateComponents(this, false);

        pack();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dedicado = new javax.swing.JLabel();
        jvm = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        merecemos = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        mod_label = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("¿De dónde ha salido esto?");
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/corona_poker_16.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.setDoubleBuffered(true);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Gracias a todos los amigos que han colaborado en esta aventura, en especial a Pepsi por sus barajas y el \"hilo fino\",");
        jLabel2.setDoubleBuffered(true);

        dedicado.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dedicado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dedicado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/luto.png"))); // NOI18N
        dedicado.setText("En memoria de todas las víctimas de la COVID-19");
        dedicado.setDoubleBuffered(true);

        jvm.setText(Helpers.getSystemInfo());
        jvm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jvm.setDoubleBuffered(true);
        jvm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jvmMouseClicked(evt);
            }
        });

        jLabel3.setText("Jn 8:32");
        jLabel3.setDoubleBuffered(true);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("(Todos los céntimos desaparecidos en las betas fueron para una buena causa).");
        jLabel4.setDoubleBuffered(true);

        merecemos.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        merecemos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        merecemos.setText("El videojuego de Texas hold 'em NL que nos merecemos, no el que necesitamos ¿o era al revés?");
        merecemos.setDoubleBuffered(true);

        jLabel6.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Nota: si posees el copyright de esta música (o cualquier otro elemento) y no permites su utilización, escríbeme a -> tonikelope@gmail.com");
        jLabel6.setDoubleBuffered(true);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("a Pepillo por ese talento para cazar los bugs más raros, a Lato por las pruebas en su Mac y a mi madre... por todo lo demás.");
        jLabel5.setDoubleBuffered(true);

        mod_label.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        mod_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mod_label.setText("MOD");
        mod_label.setDoubleBuffered(true);

        jLabel7.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("El hilo musical que suena durante el juego fue compuesto por David Luong.");
        jLabel7.setDoubleBuffered(true);

        jLabel8.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("La canción que suena en el visor de estadísticas es el tema principal de la mítica película EL GOLPE.");
        jLabel8.setDoubleBuffered(true);

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cruz.png"))); // NOI18N
        jLabel9.setText("MADE IN SPAIN");
        jLabel9.setToolTipText("PLVS VLTRA");

        jLabel10.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("La canción que suena aquí es \"La Sala del Trono\" compuesta por John Williams para Star Wars.");
        jLabel10.setDoubleBuffered(true);

        jLabel11.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("La canción que suena en la sala de espera es \"The Dream\" compuesta por Jerry Goldsmith para la película Total Recall.");
        jLabel11.setDoubleBuffered(true);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open-book.png"))); // NOI18N
        jLabel12.setToolTipText("Reglas de Robert");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel12.setDoubleBuffered(true);
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dedicado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jvm))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(merecemos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mod_label)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(merecemos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mod_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dedicado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jvm)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        last_mp3_loop = Helpers.getCurrentLoopMp3Playing();

        if (GameFrame.SONIDOS && last_mp3_loop != null && !Helpers.MP3_LOOP_MUTED.contains(last_mp3_loop)) {
            Helpers.muteLoopMp3(last_mp3_loop);
        } else {
            last_mp3_loop = null;
        }

        Helpers.playLoopMp3Resource("misc/about_music.mp3");
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        Helpers.stopLoopMp3("misc/about_music.mp3");

        if (last_mp3_loop != null) {
            Helpers.unmuteLoopMp3(last_mp3_loop);
        }
    }//GEN-LAST:event_formWindowClosed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        Helpers.openBrowserURL("https://tonikelope.github.io/coronapoker/");
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        // TODO add your handling code here:
        Helpers.openBrowserURL("https://github.com/tonikelope/coronapoker/raw/master/robert_rules.pdf");
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jvmMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jvmMouseClicked
        // TODO add your handling code here:
        if (Helpers.M1 != null && ++c == 5) {

            try {
                Helpers.M1.invoke(null, this, SwingUtilities.isLeftMouseButton(evt) ? "c" : "g");
            } catch (Exception ex) {
                Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
            }

            c = 0;
        }
    }//GEN-LAST:event_jvmMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dedicado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jvm;
    private javax.swing.JLabel merecemos;
    private javax.swing.JLabel mod_label;
    // End of variables declaration//GEN-END:variables
}

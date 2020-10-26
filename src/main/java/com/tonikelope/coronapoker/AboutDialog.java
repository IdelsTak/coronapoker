/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import java.awt.Image;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.ImageIcon;

/**
 *
 * @author tonikelope
 */
public class AboutDialog extends javax.swing.JDialog {

    public static final String VERSION = "FINAL-4.95";
    public final static String TITLE = "¿De dónde ha salido esto?";
    public static final int MAX_MOD_LOGO_HEIGHT = 75;
    private String last_mp3_loop = null;

    /**
     * Creates new form About
     */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        AboutDialog tthis = this;

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();
                Helpers.setTranslatedTitle(tthis, TITLE);

                if (Init.MOD != null) {
                    mod_label.setText(Init.MOD.get("name") + " " + Init.MOD.get("version"));

                    if (Files.exists(Paths.get(Helpers.getCurrentJarPath() + "/mod/mod.png"))) {
                        Image logo = new ImageIcon(Helpers.getCurrentJarPath() + "/mod/mod.png").getImage();

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

                Helpers.updateFonts(tthis, Helpers.GUI_FONT, null);

                Helpers.translateComponents(tthis, false);

                pack();
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dedicado = new javax.swing.JLabel();
        jvm = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        merecemos = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        mod_label = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

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
        jLabel2.setText("Gracias a todos los amigos que han colaborado en esta aventura, en especial a Pepsi por sus barajas y el");
        jLabel2.setDoubleBuffered(true);

        dedicado.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        dedicado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dedicado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/luto.png"))); // NOI18N
        dedicado.setText("En memoria de todas las víctimas de la COVID-19");
        dedicado.setDoubleBuffered(true);

        jvm.setText(Helpers.getSystemInfo());
        jvm.setDoubleBuffered(true);

        jLabel3.setText("Jn 8:31");
        jLabel3.setDoubleBuffered(true);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("(Todos los céntimos desaparecidos en las betas fueron para una buena causa).");
        jLabel4.setDoubleBuffered(true);

        merecemos.setFont(new java.awt.Font("Dialog", 0, 16)); // NOI18N
        merecemos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        merecemos.setText("El videojuego de Texas hold 'em NL que nos merecemos, no el que necesitamos ¿o era al revés?");
        merecemos.setDoubleBuffered(true);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("\"hilo fino\", a Pepillo por ese talento para cazar los bugs más raros y a mi madre... por todo lo demás.");
        jLabel5.setDoubleBuffered(true);

        jLabel6.setFont(new java.awt.Font("Dialog", 2, 10)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Nota: si posees el copyright de esta música (o cualquier otro elemento) y no permites su utilización, escríbeme a -> tonikelope@gmail.com");
        jLabel6.setDoubleBuffered(true);

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
        jLabel8.setText("La canción que suena aquí es \"La Sala del Trono\" compuesta por John Williams para Star Wars.");
        jLabel8.setDoubleBuffered(true);

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cruz.png"))); // NOI18N
        jLabel9.setText("MADE IN SPAIN");
        jLabel9.setToolTipText("PLVS VLTRA");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dedicado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jvm))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(merecemos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mod_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 335, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addGap(0, 334, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(merecemos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jvm)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        last_mp3_loop = Helpers.getCurrentLoopMp3Playing();

        if (last_mp3_loop != null) {
            Helpers.pauseLoopMp3Resource(last_mp3_loop);
        }

        Helpers.playLoopMp3Resource("misc/about_music.mp3");
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        Helpers.stopCurrentLoopMp3Resource();

        if (last_mp3_loop != null) {
            Helpers.resumeLoopMp3Resource(last_mp3_loop);
        }
    }//GEN-LAST:event_formWindowClosed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        Helpers.openBrowserURL("https://tonikelope.github.io/coronapoker/");
    }//GEN-LAST:event_jLabel1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dedicado;
    private javax.swing.JLabel jLabel1;
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

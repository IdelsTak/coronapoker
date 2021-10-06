/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronaupdater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author tonikelope
 */
public class Init extends javax.swing.JFrame {

    public static final String USER_AGENT_WEB_BROWSER = "Mozilla/5.0 (X11; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0";

    /**
     * Creates new form Init
     */
    public Init() {
        initComponents();
        Helpers.GUI_FONT = Helpers.createAndRegisterFont(Helpers.class.getResourceAsStream("/fonts/McLaren-Regular.ttf"));
        Helpers.updateFonts(this, Helpers.GUI_FONT, null);
        pack();
    }

    public JProgressBar getProgress_bar() {
        return progress_bar;
    }

    public JLabel getStatus() {
        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        progress_bar = new javax.swing.JProgressBar();
        status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CoronaUpdater");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setUndecorated(true);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/corona_poker_splash.png"))); // NOI18N
        jLabel2.setDoubleBuffered(true);

        progress_bar.setDoubleBuffered(true);
        progress_bar.setStringPainted(true);

        status.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        status.setText("Updating...");
        status.setDoubleBuffered(true);
        status.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(progress_bar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress_bar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(status))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments #0 -> version to download #1 ->
     * old jar path #2 -> new jar path
     */
    public static void main(String args[]) {

        if (args.length < 3) {
            System.exit(1);
        }

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Init.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Init.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Init.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Init.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        Init ventana = new Init();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Helpers.centrarJFrame(ventana, 0);
                ventana.getStatus().setText("UPDATING TO VERSION -> " + args[0]);
                ventana.setVisible(true);
            }
        });

        try {

            Files.move(Paths.get(args[1]), Paths.get(args[1] + ".bak"));

            downloadCoronaPoker(ventana, args[0], args[2]);

            StringBuilder java_bin = new StringBuilder();

            java_bin.append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java");

            String[] cmdArr = {java_bin.toString(), "-jar", args[2]};

            Runtime.getRuntime().exec(cmdArr);

            Files.deleteIfExists(Paths.get(args[1] + ".bak"));

        } catch (Exception ex) {

            Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex);

            try {

                Files.deleteIfExists(Paths.get(args[2]));

                if (Files.exists(Paths.get(args[1] + ".bak"))) {
                    Files.move(Paths.get(args[1] + ".bak"), Paths.get(args[1]));
                }

            } catch (Exception ex1) {
                Logger.getLogger(Init.class.getName()).log(Level.SEVERE, null, ex1);
            }

            Helpers.mostrarMensajeError(ventana, "UPDATE ERROR\n" + args[0] + "\n" + args[1] + "\n" + args[2] + "\n");

            Helpers.openBrowserURLAndWait("https://github.com/tonikelope/coronapoker/releases/latest");
        }

        ventana.dispose();

    }

    public static void downloadCoronaPoker(Init ventana, String version, String output_filepath) throws MalformedURLException, IOException {

        HttpURLConnection con = null;

        try {

            URL url_api = new URL("https://github.com/tonikelope/coronapoker/releases/download/v" + version + "/CoronaPoker_" + version + ".jar");

            con = (HttpURLConnection) url_api.openConnection();

            con.addRequestProperty("User-Agent", USER_AGENT_WEB_BROWSER);

            con.setUseCaches(false);

            try (BufferedInputStream bis = new BufferedInputStream(con.getInputStream()); BufferedOutputStream bfos = new BufferedOutputStream(new FileOutputStream(output_filepath))) {

                int length = con.getContentLength();

                java.awt.EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        ventana.getProgress_bar().setMaximum(length);
                        ventana.getProgress_bar().setValue(0);
                    }
                });

                byte[] buffer = new byte[1024];

                int reads;

                int tot = 0;

                while ((reads = bis.read(buffer)) != -1) {

                    bfos.write(buffer, 0, reads);

                    tot += reads;

                    int t = tot;

                    java.awt.EventQueue.invokeLater(new Runnable() {

                        public void run() {
                            ventana.getProgress_bar().setValue(t);
                        }
                    });
                }
            }

        } finally {

            if (con != null) {
                con.disconnect();
            }
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar progress_bar;
    private javax.swing.JLabel status;
    // End of variables declaration//GEN-END:variables
}

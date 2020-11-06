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

import java.awt.Dimension;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import org.apache.commons.codec.binary.Base64;

/**
 * Appearances can be deceiving...
 *
 * @author tonikelope
 */
public class WaitingRoom extends javax.swing.JFrame {

    public static final int MAX_PARTICIPANTES = 10;
    public static final String MAGIC_BYTES = "5c1f158dd9855cc9";
    public static final int PING_PONG_TIMEOUT = 15000;
    public static final int MAX_PING_PONG_ERROR = 3;
    public static final int EC_KEY_LENGTH = 256;
    private static volatile boolean partida_empezada = false;
    private static volatile boolean exit = false;
    private static volatile WaitingRoom THIS;

    private final Init ventana_inicio;
    private final File local_avatar;
    private final Map<String, Participant> participantes = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Object local_client_socket_lock = new Object();
    private final Object keep_alive_lock = new Object();
    private final Object lock_reconnect = new Object();
    private final boolean server;
    private final String local_nick;
    private final ConcurrentLinkedQueue<Object[]> received_confirmations = new ConcurrentLinkedQueue<>();
    private volatile ServerSocket server_socket = null;
    private volatile SecretKeySpec local_client_aes_key = null;
    private volatile SecretKeySpec local_client_hmac_key = null;
    private volatile Socket local_client_socket = null;
    private volatile BufferedReader local_client_buffer_read_is = null;
    private volatile String server_ip_port;
    private volatile String server_nick;
    private volatile Reconnect2ServerDialog reconnect_dialog = null;
    private volatile boolean reconnecting = false;
    private volatile int pong;
    private volatile String video_chat_link = null;

    public String getVideo_chat_link() {
        return video_chat_link;
    }

    public void setVideo_chat_link(String video_chat_link) {
        this.video_chat_link = video_chat_link;

        Helpers.GUIRun(new Runnable() {
            public void run() {
                video_chat_button.setEnabled(true);
            }
        });
    }

    public ConcurrentLinkedQueue<Object[]> getReceived_confirmations() {
        return received_confirmations;
    }

    public SecretKeySpec getLocal_client_hmac_key() {
        synchronized (getLocalClientSocketLock()) {
            return local_client_hmac_key;
        }
    }

    public SecretKeySpec getLocal_client_aes_key() {
        synchronized (getLocalClientSocketLock()) {
            return local_client_aes_key;
        }
    }

    public static boolean isExit() {
        return exit;
    }

    public JTextArea getChat() {
        return chat;
    }

    public static WaitingRoom getInstance() {
        return THIS;
    }

    public JLabel getStatus() {
        return status;
    }

    public boolean isReconnecting() {
        return reconnecting;
    }

    public Object getLock_reconnect() {
        return lock_reconnect;
    }

    public File getAvatar() {
        return local_avatar;
    }

    public boolean isServer() {
        return server;
    }

    public ServerSocket getServer_socket() {
        return server_socket;
    }

    public String getServer_nick() {
        return server_nick;
    }

    public Object getLocalClientSocketLock() {
        return local_client_socket_lock;
    }

    /**
     * Creates new form SalaEspera
     */
    public WaitingRoom(Init ventana_ini, boolean local, String nick, String servidor_ip_port, File avatar) {
        THIS = this;

        ventana_inicio = ventana_ini;
        server = local;
        local_nick = nick;
        server_ip_port = servidor_ip_port;
        local_avatar = avatar;

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();
                setTitle(Init.WINDOW_TITLE + Translator.translate(" - Sala de espera (") + nick + ")");

                sound_icon.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound_b.png" : "/images/mute_b.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                kick_user.setEnabled(false);
                empezar_timba.setEnabled(false);

                Helpers.JTextFieldRegularPopupMenu.addTo(chat);
                Helpers.JTextFieldRegularPopupMenu.addTo(mensaje);

                if (avatar != null) {
                    avatar_label.setPreferredSize(new Dimension(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH));
                    avatar_label.setIcon(new ImageIcon(new ImageIcon(avatar.getAbsolutePath()).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                } else {
                    avatar_label.setPreferredSize(new Dimension(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH));
                    avatar_label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));

                }

                avatar_label.setText(local_nick);

                status1.setText(server_ip_port);

                if (server) {

                    new_bot_button.setEnabled(true);

                    status.setText("Esperando jugadores...");

                    participantes.put(local_nick, null);

                    DefaultListModel listModel = new DefaultListModel();

                    ParticipantsListRenderer label = new ParticipantsListRenderer();

                    label.setText(local_nick);

                    label.setPreferredSize(new Dimension(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH));

                    if (local_avatar != null) {
                        label.setIcon(new ImageIcon(new ImageIcon(local_avatar.getAbsolutePath()).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                    } else {
                        label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                    }

                    listModel.addElement(label);

                    conectados.setModel(listModel);

                    servidor();

                } else {
                    video_chat_button.setEnabled(false);
                    empezar_timba.setVisible(false);
                    new_bot_button.setVisible(false);
                    kick_user.setVisible(false);
                    status.setText("Conectando...");
                    cliente();
                }

                Helpers.updateFonts(THIS, Helpers.GUI_FONT, null);

                Helpers.translateComponents(THIS, false);

                pack();
            }
        });
    }

    public void writeCommandToServer(String command) throws IOException {

        while (this.reconnecting) {
            synchronized (getLocalClientSocketLock()) {
                try {
                    getLocalClientSocketLock().wait(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        synchronized (getLocalClientSocketLock()) {
            this.local_client_socket.getOutputStream().write((command + "\n").getBytes("UTF-8"));
        }
    }

    public void writeCommandFromServer(String command, Socket socket) throws IOException {
        socket.getOutputStream().write((command + "\n").getBytes("UTF-8"));
    }

    public String readCommandFromClient(Socket socket, SecretKeySpec key, SecretKeySpec hmac_key) throws KeyException, IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String recibido = in.readLine();

        if (recibido != null && recibido.startsWith("*")) {
            recibido = Helpers.decryptCommand(recibido.trim(), key, hmac_key);
        } else if (recibido != null) {
            recibido = recibido.trim();
        }

        return recibido;
    }

    public String readCommandFromServer() throws KeyException, IOException {

        while (this.reconnecting) {
            synchronized (getLocalClientSocketLock()) {
                try {
                    getLocalClientSocketLock().wait(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        String recibido = this.local_client_buffer_read_is.readLine();

        if (recibido != null && recibido.startsWith("*")) {
            recibido = Helpers.decryptCommand(recibido.trim(), this.getLocal_client_aes_key(), this.getLocal_client_hmac_key());
        } else if (recibido != null) {
            recibido = recibido.trim();
        }

        return recibido;
    }

    //Función AUTO-RECONNECT
    public boolean reconectarCliente() {

        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Intentando reconectar con el servidor...");

        this.reconnecting = true;

        synchronized (getLocalClientSocketLock()) {

            try {
                WaitingRoom tthis = this;

                boolean ok;

                if (!local_client_socket.isClosed()) {
                    try {
                        local_client_socket.close();

                    } catch (Exception ex) {
                    }
                }

                local_client_socket = null;

                long start = System.currentTimeMillis();

                ok = false;

                Mac old_sha256_HMAC = Mac.getInstance("HmacSHA256");

                old_sha256_HMAC.init(local_client_hmac_key);

                String b64_nick = Base64.encodeBase64String(local_nick.getBytes("UTF-8"));

                String b64_hmac_nick = Base64.encodeBase64String(old_sha256_HMAC.doFinal(local_nick.getBytes("UTF-8")));

                do {

                    try {

                        String[] server_address = server_ip_port.split(":");

                        local_client_socket = new Socket(server_address[0], Integer.valueOf(server_address[1]));

                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "¡Conectado al servidor! Vamos a intercambiar las claves...");

                        //Le mandamos los bytes "mágicos"
                        byte[] magic = Helpers.toByteArray(MAGIC_BYTES);

                        local_client_socket.getOutputStream().write(magic);

                        /* INICIO INTERCAMBIO CLAVES */
                        KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("EC");

                        clientKpairGen.initialize(EC_KEY_LENGTH);

                        KeyPair clientKpair = clientKpairGen.generateKeyPair();

                        KeyAgreement clientKeyAgree = KeyAgreement.getInstance("ECDH");

                        clientKeyAgree.init(clientKpair.getPrivate());

                        byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();

                        DataOutputStream dOut = new DataOutputStream(local_client_socket.getOutputStream());

                        dOut.writeInt(clientPubKeyEnc.length);

                        dOut.write(clientPubKeyEnc);

                        DataInputStream dIn = new DataInputStream(local_client_socket.getInputStream());

                        int length = dIn.readInt();

                        byte[] serverPubKeyEnc = new byte[length];

                        dIn.readFully(serverPubKeyEnc, 0, serverPubKeyEnc.length);

                        KeyFactory clientKeyFac = KeyFactory.getInstance("EC");

                        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);

                        PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);

                        clientKeyAgree.doPhase(serverPubKey, true);

                        byte[] clientSharedSecret = clientKeyAgree.generateSecret();

                        byte[] secret_hash = MessageDigest.getInstance("SHA-512").digest(clientSharedSecret);

                        local_client_aes_key = new SecretKeySpec(secret_hash, 0, 16, "AES");

                        local_client_hmac_key = new SecretKeySpec(secret_hash, 32, 32, "HmacSHA256");

                        /* FIN INTERCAMBIO CLAVES */
                        //Le mandamos nuestro nick al server autenticado con la clave HMAC antigua
                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Enviando datos de reconexión...");

                        local_client_socket.getOutputStream().write((Helpers.encryptCommand(b64_nick + "#" + AboutDialog.VERSION + "#*#" + b64_hmac_nick, local_client_aes_key, local_client_hmac_key) + "\n").getBytes("UTF-8"));

                        local_client_buffer_read_is = new BufferedReader(new InputStreamReader(local_client_socket.getInputStream()));

                        //Leemos el contenido del chat
                        String recibido;

                        boolean ok_chat;

                        do {

                            ok_chat = false;

                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Leyendo datos del chat...");

                            recibido = this.local_client_buffer_read_is.readLine();

                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, recibido);

                            if (recibido != null && recibido.startsWith("*")) {

                                try {

                                    recibido = Helpers.decryptCommand(recibido.trim(), local_client_aes_key, local_client_hmac_key);

                                    String chat_text;

                                    chat_text = new String(Base64.decodeBase64(recibido), "UTF-8");

                                    Helpers.GUIRun(new Runnable() {
                                        public void run() {

                                            chat.setText(chat_text);
                                        }
                                    });

                                    ok_chat = true;

                                } catch (Exception ex) {

                                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, null, ex);
                                    Helpers.pausar(1000);
                                }

                            } else {
                                Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "EL SOCKET RECIBIÓ NULL");
                                Helpers.pausar(1000);
                            }

                        } while (!ok_chat);

                        //Leemos el enlace del videochat (si existe)
                        recibido = Helpers.decryptCommand(this.local_client_buffer_read_is.readLine(), local_client_aes_key, local_client_hmac_key);

                        String video_chat_link = new String(Base64.decodeBase64(recibido), "UTF-8");

                        if (video_chat_link.toLowerCase().startsWith("http")) {

                            setVideo_chat_link(video_chat_link);
                        }

                        ok = true;

                    } catch (Exception ex) {
                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);

                        if (local_client_socket != null && !local_client_socket.isClosed()) {

                            try {
                                local_client_socket.close();

                            } catch (Exception ex2) {
                            }

                            local_client_socket = null;
                        }
                    }

                    if (!ok) {

                        if (System.currentTimeMillis() - start > Game.CLIENT_RECON_TIMEOUT && partida_empezada) {

                            if (this.reconnect_dialog == null) {
                                this.reconnect_dialog = new Reconnect2ServerDialog(Game.getInstance() != null ? Game.getInstance() : tthis, true, server_ip_port);

                                Helpers.GUIRun(new Runnable() {
                                    public void run() {

                                        reconnect_dialog.setLocationRelativeTo(reconnect_dialog.getParent());
                                        reconnect_dialog.setVisible(true);

                                    }
                                });

                            } else {
                                reconnect_dialog.setReconectar(false);

                                Helpers.GUIRun(new Runnable() {
                                    public void run() {
                                        reconnect_dialog.reset();
                                        reconnect_dialog.setLocationRelativeTo(reconnect_dialog.getParent());
                                        reconnect_dialog.setVisible(true);

                                    }
                                });
                            }

                            while (!reconnect_dialog.isReconectar()) {
                                synchronized (this.lock_reconnect) {
                                    try {
                                        this.lock_reconnect.wait(1000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                            start = System.currentTimeMillis();
                            server_ip_port = reconnect_dialog.getIp_port().getText().trim();

                        } else {

                            Helpers.pausar(Game.CLIENT_RECON_ERROR_PAUSE);
                        }
                    }

                } while (!ok && (!partida_empezada || !Game.getInstance().getLocalPlayer().isExit()));

                if (this.reconnect_dialog != null) {

                    Helpers.GUIRun(new Runnable() {
                        @Override
                        public void run() {
                            reconnect_dialog.dispose();
                            reconnect_dialog = null;
                        }
                    });
                }

                if (ok) {
                    Helpers.playWavResource("misc/yahoo.wav");
                }

                this.reconnecting = false;

                getLocalClientSocketLock().notifyAll();

                return ok;

            } catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return false;

    }

    public void broadcastGAMECommandFromServer(String command, Participant par, boolean confirmation) {

        ArrayList<String> pendientes = new ArrayList<>();

        for (Map.Entry<String, Participant> entry : participantes.entrySet()) {

            Participant p = entry.getValue();

            if (p != null && !p.isCpu() && p != par && !p.isExit()) {

                pendientes.add(p.getNick());

            }

        }

        if (!pendientes.isEmpty()) {

            int id = Helpers.PRNG_GENERATOR.nextInt();

            byte[] iv = new byte[16];

            Helpers.SPRNG_GENERATOR.nextBytes(iv);

            for (Map.Entry<String, Participant> entry : participantes.entrySet()) {

                Participant p = entry.getValue();

                if (p != null && !p.isCpu() && pendientes.contains(p.getNick())) {

                    try {

                        if (!confirmation) {

                            String full_command = "GAME#" + String.valueOf(id) + "#" + command;

                            p.writeCommandFromServer(Helpers.encryptCommand(full_command, p.getAes_key(), iv, p.getHmac_key()));

                        } else {

                            synchronized (p.getAsync_command_queue()) {
                                p.getAsync_command_queue().add(command);
                                p.getAsync_command_queue().notifyAll();
                            }

                        }
                    } catch (IOException ex) {
                    }

                }
            }

        }
    }

    public void sendGAMECommandFromServer(String command, Participant p, boolean confirmation) {

        try {

            if (!confirmation) {

                int id = Helpers.PRNG_GENERATOR.nextInt();

                String full_command = "GAME#" + String.valueOf(id) + "#" + command;

                p.writeCommandFromServer(Helpers.encryptCommand(full_command, p.getAes_key(), p.getHmac_key()));

            } else {

                synchronized (p.getAsync_command_queue()) {
                    p.getAsync_command_queue().add(command);
                    p.getAsync_command_queue().notifyAll();
                }

            }
        } catch (IOException ex) {
        }

    }

    private void cliente() {

        WaitingRoom tthis = this;

        HashMap<String, Integer> last_received = new HashMap<>();

        Helpers.threadRun(new Runnable() {

            public void run() {

                String recibido = "";

                String[] partes = null;

                exit = false;

                try {

                    String[] direccion = server_ip_port.split(":");

                    local_client_socket = new Socket(direccion[0], Integer.valueOf(direccion[1]));

                    //Le mandamos los bytes "mágicos"
                    byte[] magic = Helpers.toByteArray(MAGIC_BYTES);

                    local_client_socket.getOutputStream().write(magic);

                    Helpers.GUIRun(new Runnable() {
                        public void run() {
                            status.setText(Translator.translate("Conectado"));
                            pack();

                        }
                    });

                    /* INICIO INTERCAMBIO CLAVES */
                    KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("EC");

                    clientKpairGen.initialize(EC_KEY_LENGTH);

                    KeyPair clientKpair = clientKpairGen.generateKeyPair();

                    KeyAgreement clientKeyAgree = KeyAgreement.getInstance("ECDH");

                    clientKeyAgree.init(clientKpair.getPrivate());

                    byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();

                    DataOutputStream dOut = new DataOutputStream(local_client_socket.getOutputStream());

                    dOut.writeInt(clientPubKeyEnc.length);

                    dOut.write(clientPubKeyEnc);

                    DataInputStream dIn = new DataInputStream(local_client_socket.getInputStream());

                    int length = dIn.readInt();

                    byte[] serverPubKeyEnc = new byte[length];

                    dIn.readFully(serverPubKeyEnc, 0, serverPubKeyEnc.length);

                    KeyFactory clientKeyFac = KeyFactory.getInstance("EC");

                    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);

                    PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);

                    clientKeyAgree.doPhase(serverPubKey, true);

                    byte[] clientSharedSecret = clientKeyAgree.generateSecret();

                    byte[] secret_hash = MessageDigest.getInstance("SHA-512").digest(clientSharedSecret);

                    local_client_aes_key = new SecretKeySpec(secret_hash, 0, 16, "AES");

                    local_client_hmac_key = new SecretKeySpec(secret_hash, 32, 32, "HmacSHA256");

                    /* FIN INTERCAMBIO CLAVES */
                    byte[] avatar_bytes = null;

                    if (local_avatar != null && local_avatar.length() > 0) {
                        try (FileInputStream is = new FileInputStream(local_avatar)) {
                            avatar_bytes = is.readAllBytes();
                        }
                    }

                    //Le mandamos nuestro nick + VERSION + AVATAR al server
                    writeCommandToServer(Helpers.encryptCommand(Base64.encodeBase64String(local_nick.getBytes("UTF-8")) + "#" + AboutDialog.VERSION + (avatar_bytes != null ? "#" + Base64.encodeBase64String(avatar_bytes) : ""), local_client_aes_key, local_client_hmac_key));

                    local_client_buffer_read_is = new BufferedReader(new InputStreamReader(local_client_socket.getInputStream()));

                    //Leemos la respuesta del server
                    recibido = readCommandFromServer();

                    partes = recibido.split("#");

                    if (partes[0].equals("BADVERSION")) {
                        exit = true;
                        Helpers.mostrarMensajeError(tthis, Translator.translate("Versión de CoronaPoker incorrecta") + "(" + partes[1] + ")");

                    } else if (partes[0].equals("YOUARELATE")) {
                        exit = true;
                        Helpers.mostrarMensajeError(tthis, "Llegas TARDE. La partida ya ha empezado.");

                    } else if (partes[0].equals("NOSPACE")) {
                        exit = true;
                        Helpers.mostrarMensajeError(tthis, "NO HAY SITIO");

                    } else if (partes[0].equals("NICKFAIL")) {
                        exit = true;
                        Helpers.mostrarMensajeError(tthis, "El nick elegido ya lo está usando otro usuario.");

                    } else if (partes[0].equals("NICKOK")) {

                        //Leemos el nick del server
                        recibido = readCommandFromServer();

                        partes = recibido.split("#");

                        server_nick = new String(Base64.decodeBase64(partes[0]), "UTF-8").trim();

                        //Leemos el avatar del server
                        String server_avatar_base64 = partes.length > 1 ? partes[1] : "";

                        File server_avatar = null;

                        try {

                            if (server_avatar_base64.length() > 0) {

                                int file_id = Helpers.PRNG_GENERATOR.nextInt();

                                if (file_id < 0) {
                                    file_id *= -1;
                                }

                                server_avatar = new File(System.getProperty("java.io.tmpdir") + "/corona_" + server_nick + "_avatar" + String.valueOf(file_id));

                                FileOutputStream os = new FileOutputStream(server_avatar);

                                os.write(Base64.decodeBase64(server_avatar_base64));

                                os.close();
                            }

                        } catch (Exception ex) {
                            server_avatar = null;
                        }

                        //Leemos el contenido del chat
                        recibido = readCommandFromServer();

                        String chat_text = new String(Base64.decodeBase64(recibido), "UTF-8");

                        Helpers.GUIRun(new Runnable() {
                            public void run() {

                                chat.setText(chat_text);
                            }
                        });

                        //Leemos el enlace del videochat (si existe)
                        recibido = readCommandFromServer();

                        String video_chat_link = new String(Base64.decodeBase64(recibido), "UTF-8");

                        if (video_chat_link.toLowerCase().startsWith("http")) {

                            setVideo_chat_link(video_chat_link);
                        }

                        //Añadimos al servidor
                        nuevoParticipante(server_nick, server_avatar, null, null, null, false);

                        //Nos añadimos nosotros
                        nuevoParticipante(local_nick, local_avatar, null, null, null, false);

                        //Cada X segundos mandamos un comando KEEP ALIVE al server 
                        Helpers.threadRun(new Runnable() {
                            public void run() {

                                while (!exit && !WaitingRoom.isPartida_empezada()) {

                                    int ping = Helpers.PRNG_GENERATOR.nextInt();

                                    try {

                                        writeCommandToServer("PING#" + String.valueOf(ping));

                                    } catch (IOException ex) {
                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    synchronized (keep_alive_lock) {
                                        try {
                                            keep_alive_lock.wait(WaitingRoom.PING_PONG_TIMEOUT);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }

                                    if (!exit && !WaitingRoom.isPartida_empezada() && ping + 1 != pong) {

                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "EL SERVIDOR NO RESPONDIÓ EL PING");

                                    }

                                }

                            }
                        });

                        //Nos quedamos en bucle esperando mensajes del server
                        do {

                            try {

                                recibido = readCommandFromServer();

                                if (recibido != null) {

                                    String[] partes_comando = recibido.split("#");

                                    if (partes_comando[0].equals("PONG")) {

                                        pong = Integer.parseInt(partes_comando[1]);

                                    } else if (partes_comando[0].equals("PING")) {

                                        writeCommandToServer("PONG#" + String.valueOf(Integer.parseInt(partes_comando[1]) + 1));

                                    } else if (partes_comando[0].equals("CHAT")) {

                                        String mensaje;

                                        if (partes_comando.length == 3) {

                                            mensaje = new String(Base64.decodeBase64(partes_comando[2]), "UTF-8");

                                        } else {
                                            mensaje = "";
                                        }

                                        recibirMensajeChat(new String(Base64.decodeBase64(partes_comando[1]), "UTF-8"), mensaje);

                                    } else if (partes_comando[0].equals("EXIT")) {

                                        exit = true;

                                        Helpers.mostrarMensajeError(tthis, "El servidor ha cancelado la timba antes de empezar.");

                                    } else if (partes_comando[0].equals("KICKED")) {

                                        exit = true;

                                        Helpers.playWavResource("loser/payaso.wav");

                                        Helpers.mostrarMensajeInformativo(tthis, "¡A LA PUTA CALLE!");

                                    } else if (partes_comando[0].equals("GAME")) {

                                        //Confirmamos recepción al servidor
                                        String subcomando = partes_comando[2];

                                        int id = Integer.valueOf(partes_comando[1]);

                                        writeCommandToServer("CONF#" + String.valueOf(id + 1) + "#OK");

                                        if (!last_received.containsKey(subcomando) || last_received.get(subcomando) != id) {

                                            last_received.put(subcomando, id);

                                            if (partida_empezada) {

                                                switch (subcomando) {
                                                    case "VIDEOCHAT":
                                                        setVideo_chat_link(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"));

                                                        break;
                                                    case "PAUSE":
                                                        Game.getInstance().pauseTimba();
                                                        break;
                                                    case "CINEMATICEND":
                                                        Game.getInstance().getCrupier().remoteCinematicEnd(null);
                                                        break;
                                                    case "SHOWCARDS":
                                                        Game.getInstance().getCrupier().showPlayerCards(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"), partes_comando[4], partes_comando[5]);
                                                        break;
                                                    case "EXIT":
                                                        Game.getInstance().getCrupier().clientPlayerQuit(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"));
                                                        borrarParticipante(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"));
                                                        break;
                                                    case "SERVEREXIT":
                                                        exit = true;
                                                        Helpers.mostrarMensajeInformativo(Game.getInstance(), "EL SERVIDOR HA TERMINADO LA TIMBA");
                                                        break;
                                                    default:

                                                        synchronized (Game.getInstance().getCrupier().getReceived_commands()) {
                                                            Game.getInstance().getCrupier().getReceived_commands().add(recibido);
                                                            Game.getInstance().getCrupier().getReceived_commands().notifyAll();
                                                        }

                                                        break;
                                                }

                                            } else {

                                                switch (subcomando) {

                                                    case "VIDEOCHAT":
                                                        setVideo_chat_link(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"));

                                                        Helpers.GUIRun(new Runnable() {
                                                            public void run() {
                                                                video_chat_button.setEnabled(true);
                                                            }
                                                        });

                                                        break;

                                                    case "DELUSER":
                                                        if (partida_empezada) {
                                                            Game.getInstance().getCrupier().clientPlayerQuit(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"));
                                                        }

                                                        borrarParticipante(new String(Base64.decodeBase64(partes_comando[3]), "UTF-8"));
                                                        break;

                                                    case "NEWUSER":
                                                        Helpers.playWavResource("misc/new_user.wav");

                                                        String nick = new String(Base64.decodeBase64(partes_comando[3]), "UTF-8");

                                                        File avatar = null;

                                                        int file_id = Helpers.PRNG_GENERATOR.nextInt();

                                                        if (file_id < 0) {
                                                            file_id *= -1;
                                                        }

                                                        if (partes_comando.length == 5) {
                                                            avatar = new File(System.getProperty("java.io.tmpdir") + "/corona_" + nick + "_avatar" + String.valueOf(file_id));

                                                            try (FileOutputStream os = new FileOutputStream(avatar)) {
                                                                os.write(Base64.decodeBase64(partes_comando[4]));
                                                            }
                                                        }

                                                        if (!participantes.containsKey(nick)) {
                                                            //Añadimos al participante
                                                            nuevoParticipante(nick, avatar, null, null, null, false);
                                                        }

                                                        break;
                                                    case "USERSLIST":
                                                        String[] current_users_parts = partes_comando[3].split("@");

                                                        for (String user : current_users_parts) {

                                                            String[] user_parts = user.split("\\|");

                                                            nick = new String(Base64.decodeBase64(user_parts[0]), "UTF-8");

                                                            avatar = null;

                                                            if (user_parts.length == 2) {
                                                                file_id = Helpers.PRNG_GENERATOR.nextInt();

                                                                if (file_id < 0) {
                                                                    file_id *= -1;
                                                                }

                                                                avatar = new File(System.getProperty("java.io.tmpdir") + "/corona_" + nick + "_avatar" + String.valueOf(file_id));

                                                                try (FileOutputStream os = new FileOutputStream(avatar)) {
                                                                    os.write(Base64.decodeBase64(user_parts[1]));
                                                                }

                                                            }

                                                            if (!participantes.containsKey(nick)) {
                                                                //Añadimos al participante
                                                                nuevoParticipante(nick, avatar, null, null, null, false);
                                                            }

                                                        }
                                                        break;

                                                    case "INIT":
                                                        setTitle(Init.WINDOW_TITLE + " - Chat (" + local_nick + ")");

                                                        partida_empezada = true;

                                                        Helpers.GUIRun(new Runnable() {
                                                            public void run() {
                                                                sound_icon.setVisible(false);
                                                                status.setText(Translator.translate("Inicializando timba..."));
                                                            }
                                                        });

                                                        Game.BUYIN = Integer.valueOf(partes_comando[3]);

                                                        Game.CIEGA_PEQUEÑA = Float.parseFloat(partes_comando[4]);

                                                        Game.CIEGA_GRANDE = Float.parseFloat(partes_comando[5]);

                                                        Game.CIEGAS_TIME = Integer.valueOf(partes_comando[6]);

                                                        Game.RECOVER = Boolean.parseBoolean(partes_comando[7]);

                                                        Game.REBUY = Boolean.parseBoolean(partes_comando[8]);

                                                        boolean ok;

                                                        do {
                                                            ok = true;

                                                            try {
                                                                //Inicializamos partida
                                                                new Game(participantes, tthis, local_nick, false);

                                                            } catch (ClassCastException ex) {
                                                                ok = false;
                                                                Helpers.pausar(250);
                                                            }
                                                        } while (!ok);

                                                        Game.getInstance().AJUGAR();
                                                        break;
                                                }
                                            }
                                        }

                                    } else if (partes_comando[0].equals("CONF")) {
                                        //Es una confirmación del servidor

                                        WaitingRoom.getInstance().getReceived_confirmations().add(new Object[]{server_nick, Integer.parseInt(partes_comando[1])});
                                        synchronized (WaitingRoom.getInstance().getReceived_confirmations()) {

                                            WaitingRoom.getInstance().getReceived_confirmations().notifyAll();
                                        }

                                    }

                                } else {
                                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "EL SOCKET RECIBIÓ NULL");
                                    Helpers.pausar(1000);
                                }

                            } catch (SocketException ex) {

                                //Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                                if (!exit && (!isPartida_empezada() || !Game.getInstance().getLocalPlayer().isExit())) {

                                    if (!reconectarCliente()) {
                                        exit = true;
                                    }
                                }
                            } catch (KeyException ex) {
                                Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "KEY-EXCEPTION AL LEER DEL SOCKET", ex);
                                Helpers.pausar(1000);
                            }

                        } while (!exit);

                    }

                } catch (IOException ex) {
                    //Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                    Helpers.mostrarMensajeError(tthis, "ERROR INESPERADO");
                    System.exit(1);
                }

                if (partida_empezada) {

                    Game.getInstance().finTransmision(exit);

                } else if (!exit) {

                    if (local_client_socket == null) {

                        Helpers.mostrarMensajeError(tthis, "ALGO HA FALLADO. (Probablemente la timba no esté aún creada).");

                    } else {

                        Helpers.mostrarMensajeError(tthis, "ALGO HA FALLADO. Has perdido la conexión con el servidor.");
                    }
                }

                exit = true;

                synchronized (keep_alive_lock) {
                    keep_alive_lock.notifyAll();
                }

                Helpers.GUIRun(new Runnable() {
                    public void run() {

                        ventana_inicio.setVisible(true);

                        dispose();
                    }
                });
            }
        });
    }

    private void enviarListaUsuariosActualesAlNuevoUsuario(Participant par) {

        String command = "USERSLIST#";

        for (Map.Entry<String, Participant> entry : participantes.entrySet()) {

            Participant p = entry.getValue();

            try {

                if (p != null && p != par) {

                    command += Base64.encodeBase64String(p.getNick().getBytes("UTF-8"));

                    if (p.getAvatar() != null || p.isCpu()) {
                        byte[] avatar_b;

                        try (InputStream is = !p.isCpu() ? new FileInputStream(p.getAvatar()) : WaitingRoom.class.getResourceAsStream("/images/avatar_bot.png")) {
                            avatar_b = is.readAllBytes();
                        }

                        command += "|" + Base64.encodeBase64String(avatar_b);
                    }

                    command += "@";
                }

            } catch (IOException ex) {
                Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        this.sendGAMECommandFromServer(command, par, true);

    }

    private void servidor() {

        this.server_nick = this.local_nick;

        WaitingRoom tthis = this;

        Helpers.threadRun(new Runnable() {
            public void run() {

                String recibido = "";

                String[] partes = null;

                try {
                    String[] direccion = server_ip_port.trim().split(":");

                    server_socket = new ServerSocket(Integer.valueOf(direccion[1]));

                    while (!server_socket.isClosed()) {

                        Socket client_socket = server_socket.accept();

                        //Leemos los bytes "mágicos"
                        byte[] magic = new byte[Helpers.toByteArray(MAGIC_BYTES).length];

                        client_socket.getInputStream().read(magic);

                        if (Helpers.toHexString(magic).toLowerCase().equals(MAGIC_BYTES)) {

                            /* INICIO INTERCAMBIO DE CLAVES */
                            DataInputStream dIn = new DataInputStream(client_socket.getInputStream());

                            int length = dIn.readInt();

                            byte[] clientPubKeyEnc = new byte[length];

                            dIn.readFully(clientPubKeyEnc, 0, clientPubKeyEnc.length);

                            KeyFactory serverKeyFac = KeyFactory.getInstance("EC");

                            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);

                            PublicKey clientPubKey = serverKeyFac.generatePublic(x509KeySpec);

                            KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("EC");

                            serverKpairGen.initialize(EC_KEY_LENGTH);

                            KeyPair serverKpair = serverKpairGen.generateKeyPair();

                            KeyAgreement serverKeyAgree = KeyAgreement.getInstance("ECDH");

                            serverKeyAgree.init(serverKpair.getPrivate());

                            byte[] serverPubKeyEnc = serverKpair.getPublic().getEncoded();

                            DataOutputStream dOut = new DataOutputStream(client_socket.getOutputStream());

                            dOut.writeInt(serverPubKeyEnc.length);

                            dOut.write(serverPubKeyEnc);

                            serverKeyAgree.doPhase(clientPubKey, true);

                            byte[] serverSharedSecret = serverKeyAgree.generateSecret();

                            byte[] secret_hash = MessageDigest.getInstance("SHA-512").digest(serverSharedSecret);

                            SecretKeySpec aes_key = new SecretKeySpec(secret_hash, 0, 16, "AES");

                            SecretKeySpec hmac_key = new SecretKeySpec(secret_hash, 32, 32, "HmacSHA256");

                            /* FIN INTERCAMBIO DE CLAVES */
                            //Leemos el nick del usuario
                            recibido = readCommandFromClient(client_socket, aes_key, hmac_key);

                            partes = recibido.split("#");

                            String client_nick = new String(Base64.decodeBase64(partes[0]), "UTF-8");

                            File client_avatar = null;

                            if (partes.length == 4) {

                                Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Un supuesto cliente quiere reconectar...");

                                if (participantes.containsKey(client_nick)) {

                                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "El cliente existe");

                                    Mac old_sha256_HMAC = Mac.getInstance("HmacSHA256");

                                    old_sha256_HMAC.init(participantes.get(client_nick).getHmac_key());

                                    byte[] old_hmac = old_sha256_HMAC.doFinal(client_nick.getBytes("UTF-8"));

                                    if (MessageDigest.isEqual(old_hmac, Base64.decodeBase64(partes[3]))) {

                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "El HMAC del cliente es auténtico");

                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Reseteando el socket del cliente...");
                                        //Es un usuario intentado reconectar
                                        participantes.get(client_nick).resetSocket(client_socket, aes_key, hmac_key);

                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Enviando datos del chat...");

                                        //Mandamos el chat
                                        client_socket.getOutputStream().write((Helpers.encryptCommand(Base64.encodeBase64String(chat.getText().getBytes("UTF-8")), aes_key, hmac_key) + "\n").getBytes("UTF-8"));

                                        /*if (!isPartida_empezada() && participantes.size() > 2) {

                                        enviarListaUsuariosActualesAlNuevoUsuario(participantes.get(client_nick));
                                    }*/
                                        Helpers.playWavResource("misc/yahoo.wav");

                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "EL CLIENTE " + client_nick + " HA RECONECTADO CORRECTAMENTE.");

                                    } else {
                                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "EL CLIENTE " + client_nick + " NO HA PODIDO RECONECTAR");

                                        try {
                                            client_socket.close();
                                        } catch (Exception ex) {
                                        }
                                    }

                                } else {
                                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "El usuario " + client_nick + " HA FALLADO AL RECONECTAR su socket.");

                                    try {
                                        client_socket.close();
                                    } catch (Exception ex) {
                                    }
                                }

                            } else if (partida_empezada) {
                                writeCommandFromServer(Helpers.encryptCommand("YOUARELATE", aes_key, hmac_key), client_socket);
                            } else if (!partes[1].equals(AboutDialog.VERSION)) {
                                writeCommandFromServer(Helpers.encryptCommand("BADVERSION#" + AboutDialog.VERSION, aes_key, hmac_key), client_socket);
                            } else if (participantes.size() == MAX_PARTICIPANTES) {
                                writeCommandFromServer(Helpers.encryptCommand("NOSPACE", aes_key, hmac_key), client_socket);
                            } else if (participantes.containsKey(client_nick)) {
                                writeCommandFromServer(Helpers.encryptCommand("NICKFAIL", aes_key, hmac_key), client_socket);
                            } else {

                                //Procesamos su avatar
                                String client_avatar_base64 = partes.length > 2 ? partes[2] : "";

                                try {

                                    if (client_avatar_base64.length() > 0) {

                                        int file_id = Helpers.PRNG_GENERATOR.nextInt();

                                        if (file_id < 0) {
                                            file_id *= -1;
                                        }
                                        client_avatar = new File(System.getProperty("java.io.tmpdir") + "/corona_" + client_nick + "_avatar" + String.valueOf(file_id));

                                        FileOutputStream os = new FileOutputStream(client_avatar);

                                        os.write(Base64.decodeBase64(client_avatar_base64));

                                        os.close();
                                    }
                                } catch (Exception ex) {
                                    client_avatar = null;
                                }

                                if (!partida_empezada) {
                                    Helpers.playWavResource("misc/new_user.wav");
                                }

                                //Mandamos al cliente su ID
                                writeCommandFromServer(Helpers.encryptCommand("NICKOK", aes_key, hmac_key), client_socket);

                                byte[] avatar_bytes = null;

                                if (local_avatar != null && local_avatar.length() > 0) {

                                    try (FileInputStream is = new FileInputStream(local_avatar)) {
                                        avatar_bytes = is.readAllBytes();
                                    }
                                }

                                //Mandamos nuestro nick + avatar
                                writeCommandFromServer(Helpers.encryptCommand(Base64.encodeBase64String(local_nick.getBytes("UTF-8")) + (avatar_bytes != null ? "#" + Base64.encodeBase64String(avatar_bytes) : ""), aes_key, hmac_key), client_socket);

                                //Mandamos el contenido del chat
                                writeCommandFromServer(Helpers.encryptCommand(Base64.encodeBase64String(chat.getText().getBytes("UTF-8")), aes_key, hmac_key), client_socket);

                                //Mandamos el link del videochat
                                writeCommandFromServer(Helpers.encryptCommand(Base64.encodeBase64String((getVideo_chat_link() != null ? getVideo_chat_link() : "---").getBytes("UTF-8")), aes_key, hmac_key), client_socket);

                                //Añadimos al participante
                                nuevoParticipante(client_nick, client_avatar, client_socket, aes_key, hmac_key, false);

                                //Mandamos la lista de participantes actuales al nuevo participante
                                if (participantes.size() > 2) {
                                    enviarListaUsuariosActualesAlNuevoUsuario(participantes.get(client_nick));
                                }

                                //Mandamos el nuevo participante al resto de participantes
                                String comando = "NEWUSER#" + Base64.encodeBase64String(client_nick.getBytes("UTF-8"));

                                if (client_avatar != null) {

                                    byte[] avatar_b;

                                    try (FileInputStream is = new FileInputStream(client_avatar)) {
                                        avatar_b = is.readAllBytes();
                                    }

                                    comando += "#" + Base64.encodeBase64String(avatar_b);
                                }

                                broadcastGAMECommandFromServer(comando, participantes.get(client_nick), true);

                                Helpers.GUIRun(new Runnable() {
                                    public void run() {
                                        empezar_timba.setEnabled(true);
                                        kick_user.setEnabled(true);
                                        new_bot_button.setEnabled(participantes.size() < WaitingRoom.MAX_PARTICIPANTES);
                                    }
                                });
                            }

                        } else {

                            try {
                                client_socket.close();
                            } catch (Exception e) {
                            }
                        }

                    }

                } catch (IOException ex) {
                    //Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);

                    if (server_socket == null) {

                        Helpers.mostrarMensajeError(tthis, "ALGO HA FALLADO. (Probablemente ya hay una timba creada en el mismo puerto).");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                    Helpers.mostrarMensajeError(tthis, "ERROR INESPERADO");
                    System.exit(1);
                }

                Helpers.GUIRun(new Runnable() {
                    public void run() {

                        ventana_inicio.setVisible(true);

                        dispose();

                    }
                });

            }
        });
    }

    public void recibirMensajeChat(String nick, String msg) {

        Helpers.GUIRun(new Runnable() {
            public void run() {
                chat.append("[" + nick + Translator.translate("] dice: ") + msg + "\n");

                if (!chat.isFocusOwner()) {
                    chat.setCaretPosition(chat.getText().length());
                }

                if (!isVisible()) {
                    Helpers.playWavResource("misc/chat_alert.wav");
                }
            }
        });

        if (this.server) {

            byte[] iv = new byte[16];

            Helpers.SPRNG_GENERATOR.nextBytes(iv);

            //Reenviamos el mensaje al resto de participantes
            participantes.entrySet().forEach((entry) -> {
                try {

                    Participant p = entry.getValue();

                    if (p != null && !p.isCpu() && !p.getNick().equals(nick)) {

                        String comando = "CHAT#" + Base64.encodeBase64String(nick.getBytes("UTF-8")) + "#" + Base64.encodeBase64String(msg.getBytes("UTF-8"));

                        p.writeCommandFromServer(Helpers.encryptCommand(comando, p.getAes_key(), iv, p.getHmac_key()));
                    }

                } catch (IOException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    public static boolean isPartida_empezada() {
        return partida_empezada;
    }

    public void enviarMensajeChat(String nick, String msg) {

        Helpers.threadRun(new Runnable() {
            public void run() {

                byte[] iv = new byte[16];

                Helpers.SPRNG_GENERATOR.nextBytes(iv);

                if (!server) {
                    try {
                        String comando = "CHAT#" + Base64.encodeBase64String(nick.getBytes("UTF-8")) + "#" + Base64.encodeBase64String(msg.getBytes("UTF-8"));
                        writeCommandToServer(Helpers.encryptCommand(comando, getLocal_client_aes_key(), iv, getLocal_client_hmac_key()));
                    } catch (IOException ex) {
                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {

                    participantes.entrySet().forEach((entry) -> {
                        try {
                            Participant participante = entry.getValue();
                            if (participante != null && !participante.isCpu()) {
                                String comando = "CHAT#" + Base64.encodeBase64String(nick.getBytes("UTF-8")) + "#" + Base64.encodeBase64String(msg.getBytes("UTF-8"));
                                participante.writeCommandFromServer(Helpers.encryptCommand(comando, participante.getAes_key(), iv, participante.getHmac_key()));
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }

            }
        });
    }

    public synchronized void borrarParticipante(String nick) {

        if (this.participantes.containsKey(nick)) {

            Helpers.playWavResource("misc/toilet.wav");

            participantes.remove(nick);

            Helpers.GUIRunAndWait(new Runnable() {
                public void run() {

                    DefaultListModel listModel = new DefaultListModel();

                    for (Map.Entry<String, Participant> entry : participantes.entrySet()) {
                        ParticipantsListRenderer label = new ParticipantsListRenderer();

                        label.setText(entry.getKey());
                        label.setPreferredSize(new Dimension(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH));
                        if (entry.getValue() != null) {

                            if (entry.getValue().getAvatar() != null) {
                                label.setIcon(new ImageIcon(new ImageIcon(entry.getValue().getAvatar().getAbsolutePath()).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                            } else {
                                label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource((server && entry.getValue().isCpu()) ? "/images/avatar_bot.png" : "/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                            }
                        } else {

                            if (local_avatar != null) {
                                label.setIcon(new ImageIcon(new ImageIcon(local_avatar.getAbsolutePath()).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                            } else {
                                label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                            }

                        }

                        listModel.addElement(label);
                    }

                    conectados.setModel(listModel);

                    if (server && !WaitingRoom.isPartida_empezada()) {

                        if (participantes.size() < 2) {
                            empezar_timba.setEnabled(false);
                            kick_user.setEnabled(false);
                        }

                        new_bot_button.setEnabled(true);
                    }
                }
            });

            if (this.isServer() && !WaitingRoom.isPartida_empezada() && !exit) {

                String comando;
                try {
                    comando = "DELUSER#" + Base64.encodeBase64String(nick.getBytes("UTF-8"));
                    this.broadcastGAMECommandFromServer(comando, participantes.get(nick), true);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }

    private void nuevoParticipante(String nick, File avatar, Socket socket, SecretKeySpec aes_k, SecretKeySpec hmac_k, boolean cpu) {

        Participant participante = new Participant(this, nick, avatar, socket, aes_k, hmac_k, cpu);

        participantes.put(nick, participante);

        if (socket != null) {

            Helpers.threadRun(participante);

        }

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {

                DefaultListModel listModel = new DefaultListModel();

                for (Map.Entry<String, Participant> entry : participantes.entrySet()) {
                    ParticipantsListRenderer label = new ParticipantsListRenderer();

                    label.setText(entry.getKey());
                    label.setPreferredSize(new Dimension(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH));

                    if (entry.getValue() != null) {

                        if (entry.getValue().getAvatar() != null) {
                            label.setIcon(new ImageIcon(new ImageIcon(entry.getValue().getAvatar().getAbsolutePath()).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                        } else {
                            label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource((server && entry.getValue().isCpu()) ? "/images/avatar_bot.png" : "/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                        }

                    } else {

                        if (local_avatar != null) {
                            label.setIcon(new ImageIcon(new ImageIcon(local_avatar.getAbsolutePath()).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                        } else {
                            label.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage().getScaledInstance(NewGameDialog.DEFAULT_AVATAR_WIDTH, NewGameDialog.DEFAULT_AVATAR_WIDTH, Image.SCALE_SMOOTH)));
                        }

                    }
                    listModel.addElement(label);
                }

                conectados.setModel(listModel);

                if (nick.equals(local_nick)) {
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

        jScrollPane1 = new javax.swing.JScrollPane();
        chat = new javax.swing.JTextArea();
        logo = new javax.swing.JLabel();
        mensaje = new javax.swing.JTextField();
        status = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        conectados = new javax.swing.JList<>();
        empezar_timba = new javax.swing.JButton();
        kick_user = new javax.swing.JButton();
        avatar_label = new javax.swing.JLabel();
        status1 = new javax.swing.JLabel();
        sound_icon = new javax.swing.JLabel();
        new_bot_button = new javax.swing.JButton();
        video_chat_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CoronaPoker - Sala de espera");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage());
        setMinimumSize(new java.awt.Dimension(548, 701));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPane1.setDoubleBuffered(true);

        chat.setEditable(false);
        chat.setColumns(20);
        chat.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        chat.setLineWrap(true);
        chat.setRows(5);
        chat.setDoubleBuffered(true);
        jScrollPane1.setViewportView(chat);

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/corona_poker_15.png"))); // NOI18N
        logo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logo.setDoubleBuffered(true);
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoMouseClicked(evt);
            }
        });

        mensaje.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        mensaje.setDoubleBuffered(true);
        mensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mensajeActionPerformed(evt);
            }
        });

        status.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        status.setForeground(new java.awt.Color(51, 153, 0));
        status.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        status.setText("Estado");
        status.setDoubleBuffered(true);

        jScrollPane3.setDoubleBuffered(true);

        conectados.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        conectados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        conectados.setToolTipText("Participantes conectados");
        conectados.setCellRenderer(new com.tonikelope.coronapoker.ParticipantsListRenderer());
        conectados.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        conectados.setDoubleBuffered(true);
        jScrollPane3.setViewportView(conectados);

        empezar_timba.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        empezar_timba.setText("EMPEZAR YA");
        empezar_timba.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        empezar_timba.setDoubleBuffered(true);
        empezar_timba.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empezar_timbaActionPerformed(evt);
            }
        });

        kick_user.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        kick_user.setText("Expulsar jugador");
        kick_user.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        kick_user.setDoubleBuffered(true);
        kick_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kick_userActionPerformed(evt);
            }
        });

        avatar_label.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        avatar_label.setText("Toni");
        avatar_label.setDoubleBuffered(true);

        status1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        status1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        status1.setText("1.1.1.1");
        status1.setDoubleBuffered(true);

        sound_icon.setBackground(new java.awt.Color(153, 153, 153));
        sound_icon.setToolTipText("Click para activar/desactivar el sonido");
        sound_icon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        sound_icon.setDoubleBuffered(true);
        sound_icon.setPreferredSize(new java.awt.Dimension(30, 30));
        sound_icon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sound_iconMouseClicked(evt);
            }
        });

        new_bot_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        new_bot_button.setText("Añadir bot");
        new_bot_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        new_bot_button.setDoubleBuffered(true);
        new_bot_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_bot_buttonActionPerformed(evt);
            }
        });

        video_chat_button.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        video_chat_button.setText("VIDEOLLAMADA");
        video_chat_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        video_chat_button.setDoubleBuffered(true);
        video_chat_button.setFocusable(false);
        video_chat_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                video_chat_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empezar_timba, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(sound_icon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(status1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(new_bot_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(logo)
                            .addComponent(video_chat_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                            .addComponent(kick_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(avatar_label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mensaje)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kick_user))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(video_chat_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(new_bot_button)
                        .addGap(18, 18, 18)
                        .addComponent(status1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(status)
                            .addComponent(sound_icon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(empezar_timba, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(avatar_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mensaje))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mensajeActionPerformed
        // TODO add your handling code here:

        chat.append("[" + local_nick + Translator.translate("] dice: ") + this.mensaje.getText() + "\n");

        if (!chat.isFocusOwner()) {
            chat.setCaretPosition(chat.getText().length());
        }

        this.enviarMensajeChat(local_nick, this.mensaje.getText());

        this.mensaje.setText("");
    }//GEN-LAST:event_mensajeActionPerformed

    private void kick_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kick_userActionPerformed

        // TODO add your handling code here:
        if (conectados.getSelectedIndex() != -1) {

            String expulsado = ((JLabel) ((DefaultListModel) conectados.getModel()).get(conectados.getSelectedIndex())).getText();

            if (!expulsado.equals(local_nick)) {

                kick_user.setEnabled(false);

                Helpers.threadRun(new Runnable() {
                    public void run() {
                        try {

                            if (!participantes.get(expulsado).isCpu()) {

                                String comando = "KICKED#" + Base64.encodeBase64String(expulsado.getBytes("UTF-8"));
                                participantes.get(expulsado).writeCommandFromServer(Helpers.encryptCommand(comando, participantes.get(expulsado).getAes_key(), participantes.get(expulsado).getHmac_key()));
                            }

                            participantes.get(expulsado).setExit();

                            borrarParticipante(expulsado);

                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        Helpers.GUIRun(new Runnable() {
                            public void run() {
                                kick_user.setEnabled(participantes.size() > 1);
                            }
                        });
                    }
                });
            }
        }

    }//GEN-LAST:event_kick_userActionPerformed

    private void empezar_timbaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empezar_timbaActionPerformed
        // TODO add your handling code here:

        if (participantes.size() >= 2 && !partida_empezada) {

            boolean faltan_jugadores = false;

            if (Game.RECOVER && Files.exists(Paths.get(Crupier.RECOVER_BALANCE_FILE))) {

                try {
                    String datos = Files.readString(Paths.get(Crupier.RECOVER_BALANCE_FILE));
                    String[] partes = datos.split("#");
                    String[] auditor_partes = partes[10].split("@");

                    for (String player_data : auditor_partes) {

                        partes = player_data.split("\\|");

                        String nick = new String(Base64.decodeBase64(partes[0]), "UTF-8");

                        if (!participantes.containsKey(nick)) {
                            faltan_jugadores = true;
                            break;
                        }
                    }

                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            boolean vamos = (!faltan_jugadores || Helpers.mostrarMensajeInformativoSINO(this, "Hay jugadores de la timba anterior que no se han vuelto a conectar.\n(Si no se conectan no se podrá recuperar la última mano en curso).\n\n¿EMPEZAMOS YA?") == 0);

            if (vamos) {

                WaitingRoom tthis = this;

                setTitle(Init.WINDOW_TITLE + " - Chat (" + local_nick + ")");
                this.empezar_timba.setEnabled(false);
                this.empezar_timba.setVisible(false);
                this.new_bot_button.setEnabled(false);
                this.new_bot_button.setVisible(false);
                this.kick_user.setEnabled(false);
                this.kick_user.setVisible(false);
                this.sound_icon.setVisible(false);
                this.status.setText(Translator.translate("Inicializando timba..."));
                pack();

                Helpers.threadRun(new Runnable() {
                    public void run() {

                        boolean ocupados;

                        do {

                            ocupados = false;

                            for (Map.Entry<String, Participant> entry : participantes.entrySet()) {

                                Participant p = entry.getValue();

                                if (p != null && !p.isCpu() && !p.getAsync_command_queue().isEmpty()) {

                                    ocupados = true;

                                    break;

                                }

                            }

                            if (ocupados) {

                                Logger.getLogger(WaitingRoom.class.getName()).log(Level.WARNING, "Hay algun participante con comandos sin confirmar. NO podemos empezar aún...");
                                Helpers.pausar(1000);
                            }

                        } while (ocupados);

                        boolean ok;

                        do {
                            ok = true;

                            try {
                                //Inicializamos partida
                                new Game(participantes, tthis, local_nick, true);

                            } catch (ClassCastException ex) {
                                ok = false;

                                Helpers.pausar(250);
                            }
                        } while (!ok);

                        WaitingRoom.partida_empezada = true;

                        Game.getInstance().AJUGAR();
                    }
                });
            }
        }
    }//GEN-LAST:event_empezar_timbaActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        if (!WaitingRoom.partida_empezada) {

            exit = true;

            Helpers.threadRun(new Runnable() {
                public void run() {

                    if (isServer()) {

                        participantes.entrySet().forEach((entry) -> {

                            Participant p = entry.getValue();

                            if (p != null) {

                                p.setExit();
                            }

                        });

                        if (getServer_socket() != null) {
                            try {
                                getServer_socket().close();
                            } catch (Exception ex) {
                                Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    } else if (local_client_socket != null && !reconnecting) {

                        try {
                            writeCommandToServer(Helpers.encryptCommand("EXIT", getLocal_client_aes_key(), getLocal_client_hmac_key()));
                            local_client_socket.close();
                        } catch (Exception ex) {
                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });

        } else {
            setVisible(false);
        }
    }//GEN-LAST:event_formWindowClosing

    private void sound_iconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sound_iconMouseClicked
        // TODO add your handling code here:

        Game.SONIDOS = !Game.SONIDOS;

        Helpers.PROPERTIES.setProperty("sonidos", Game.SONIDOS ? "true" : "false");

        Helpers.savePropertiesFile();

        Helpers.GUIRun(new Runnable() {
            public void run() {

                sound_icon.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound_b.png" : "/images/mute_b.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

            }
        });

        if (!Game.SONIDOS) {

            Helpers.muteAll();

        } else {

            Helpers.unMuteAll();

        }
    }//GEN-LAST:event_sound_iconMouseClicked

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        sound_icon.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound_b.png" : "/images/mute_b.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        mensaje.requestFocusInWindow();
    }//GEN-LAST:event_formComponentShown

    private void logoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoMouseClicked
        // TODO add your handling code here:
        AboutDialog dialog = new AboutDialog(this, true);

        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);
    }//GEN-LAST:event_logoMouseClicked

    private void new_bot_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_bot_buttonActionPerformed

        if (participantes.size() < MAX_PARTICIPANTES) {

            new_bot_button.setEnabled(false);

            Helpers.playWavResource("misc/laser.wav");

            Helpers.threadRun(new Runnable() {
                public void run() {
                    try {
                        // TODO add your handling code here:
                        String bot_nick;

                        int conta_bot = 0;

                        do {
                            conta_bot++;

                            bot_nick = "CoronaBot#" + String.valueOf(conta_bot);

                        } while (participantes.get(bot_nick) != null);

                        //Mandamos el nuevo participante al resto de participantes
                        String comando = "NEWUSER#" + Base64.encodeBase64String(bot_nick.getBytes("UTF-8"));

                        byte[] avatar_b = null;

                        try (InputStream is = WaitingRoom.class.getResourceAsStream("/images/avatar_bot.png")) {
                            avatar_b = is.readAllBytes();
                        } catch (IOException ex) {
                            Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        comando += "#" + Base64.encodeBase64String(avatar_b);

                        nuevoParticipante(bot_nick, null, null, null, null, true);

                        broadcastGAMECommandFromServer(comando, participantes.get(bot_nick), true);

                        empezar_timba.setEnabled(true);

                        kick_user.setEnabled(true);

                        new_bot_button.setEnabled(participantes.size() < WaitingRoom.MAX_PARTICIPANTES);

                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }//GEN-LAST:event_new_bot_buttonActionPerformed

    private void video_chat_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_video_chat_buttonActionPerformed
        // TODO add your handling code here:

        if (server && this.getVideo_chat_link() == null) {
            Helpers.openBrowserURL("https://demos.openvidu.io/getaroom/");
        }

        QRChat chat_dialog = new QRChat(this, true, this.getVideo_chat_link(), server);

        chat_dialog.setLocationRelativeTo(this);

        chat_dialog.setVisible(true);

        if (server && !chat_dialog.isCancel() && chat_dialog.getLink() != null) {

            this.setVideo_chat_link(chat_dialog.getLink());

            try {
                this.broadcastGAMECommandFromServer("VIDEOCHAT#" + Base64.encodeBase64String(this.getVideo_chat_link().getBytes("UTF-8")), null, true);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(WaitingRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_video_chat_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel avatar_label;
    private javax.swing.JTextArea chat;
    private javax.swing.JList<String> conectados;
    private javax.swing.JButton empezar_timba;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton kick_user;
    private javax.swing.JLabel logo;
    private javax.swing.JTextField mensaje;
    private javax.swing.JButton new_bot_button;
    private javax.swing.JLabel sound_icon;
    private javax.swing.JLabel status;
    private javax.swing.JLabel status1;
    private javax.swing.JButton video_chat_button;
    // End of variables declaration//GEN-END:variables
}

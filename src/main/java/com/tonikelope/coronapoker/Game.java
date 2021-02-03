/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tonikelope.coronapoker;

import static com.tonikelope.coronapoker.Helpers.TapetePopupMenu.BARAJAS_MENU;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

/**
 *
 * @author tonikelope
 */
public final class Game extends javax.swing.JFrame implements ZoomableInterface {

    public static final int TEST_MODE_PAUSE = 250;
    public static final int DEFAULT_ZOOM_LEVEL = -2;
    public static final float MIN_BIG_BLIND = 0.20f;
    public static final float ZOOM_STEP = 0.05f;
    public static final int PAUSA_ENTRE_MANOS = 7; //Segundos
    public static final int PAUSA_ENTRE_MANOS_TEST = 1;
    public static final int PAUSA_ANTES_DE_SHOWDOWN = 1; //Segundos
    public static final int TIEMPO_PENSAR = 30; //Segundos
    public static final int WAIT_QUEUES = 1000;
    public static final int WAIT_PAUSE = 1000;
    public static final int CLIENT_RECEPTION_TIMEOUT = 10000;
    public static final int CONFIRMATION_TIMEOUT = 10000;
    public static final int CLIENT_RECON_TIMEOUT = 2 * TIEMPO_PENSAR * 1000; // Tiempo en milisegundos que esperaremos cliente que perdió la conexión antes (preguntar) si echarle de la timba
    public static final int CLIENT_RECON_ERROR_PAUSE = 5000;
    public static final int REBUY_TIMEOUT = 25000;
    public static final int MAX_TIMEOUT_CONFIRMATION_ERROR = 10;
    public static final String BARAJA_DEFAULT = "goliat";
    public static final String DEFAULT_LANGUAGE = "es";
    public static final int PEPILLO_COUNTER_MAX = 5;
    public static final int PAUSE_COUNTER_MAX = 3;
    public static final int AUTO_ZOOM_TIMEOUT = 2000;
    public static final int GUI_ZOOM_WAIT = 250;
    public static final boolean TEST_MODE = false;

    public static volatile float CIEGA_PEQUEÑA = 0.10f;
    public static volatile float CIEGA_GRANDE = 0.20f;
    public static volatile int BUYIN = 10;
    public static volatile int CIEGAS_TIME = 60;
    public static volatile boolean REBUY = true;
    public static volatile int MANOS = -1;
    public static volatile boolean SONIDOS = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("sonidos", "true")) && !TEST_MODE;
    public static volatile boolean SONIDOS_CHORRA = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("sonidos_chorra", "true"));
    public static volatile boolean SONIDOS_TTS = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("sonidos_tts", "true"));
    public static volatile boolean MUSICA_AMBIENTAL = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("sonido_ascensor", "true"));
    public static volatile boolean AUTO_REBUY = false;
    public static volatile boolean SHOW_CLOCK = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("show_time", "false"));
    public static volatile boolean CONFIRM_ACTIONS = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("confirmar_todo", "false")) && !TEST_MODE;
    public static volatile int ZOOM_LEVEL = Integer.parseInt(Helpers.PROPERTIES.getProperty("zoom_level", String.valueOf(Game.DEFAULT_ZOOM_LEVEL)));
    public static volatile String BARAJA = Helpers.PROPERTIES.getProperty("baraja", BARAJA_DEFAULT);
    public static volatile boolean VISTA_COMPACTA = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("vista_compacta", "false")) && !TEST_MODE;
    public static volatile boolean ANIMACION_REPARTIR = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("animacion_reparto", "true"));
    public static volatile boolean AUTO_ACTION_BUTTONS = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("auto_action_buttons", "false")) && !TEST_MODE;
    public static volatile String COLOR_TAPETE = Helpers.PROPERTIES.getProperty("color_tapete", "verde");
    public static volatile String LANGUAGE = Helpers.PROPERTIES.getProperty("lenguaje", "es").toLowerCase();
    public static volatile boolean CINEMATICAS = Boolean.parseBoolean(Helpers.PROPERTIES.getProperty("cinematicas", "true"));
    public static volatile boolean RECOVER = false;
    public static volatile String RECOVER_ID = null;
    public static volatile KeyEventDispatcher key_event_dispatcher = null;
    private static volatile Game THIS = null;

    public static Game getInstance() {
        return THIS;
    }

    private final Object registro_lock = new Object();
    private final Object full_screen_lock = new Object();
    private final Object lock_pause = new Object();
    private final Object lock_fin = new Object();
    private final ArrayList<Player> jugadores;
    private final ConcurrentHashMap<String, String> nick2avatar = new ConcurrentHashMap<>();
    private final Crupier crupier;
    private final boolean partida_local;
    private final String nick_local;

    private volatile ZoomableInterface[] zoomeables;
    private volatile long conta_tiempo_juego = 0L;
    private volatile boolean full_screen = false;
    private volatile boolean timba_pausada = false;
    private volatile String nick_pause = null;
    private volatile PauseDialog pausa_dialog = null;
    private volatile boolean game_over_dialog = false;
    private volatile JFrame full_screen_frame = null;
    private volatile AboutDialog about_dialog = null;
    private volatile HandGeneratorDialog jugadas_dialog = null;
    private volatile GameLogDialog registro_dialog = null;
    private volatile FastChatDialog fastchat_dialog = null;
    private volatile RebuyNowDialog rebuy_dialog = null;
    private volatile TablePanel tapete = null;
    private volatile Timer tiempo_juego;

    public JCheckBoxMenuItem getRebuy_now_menu() {
        return rebuy_now_menu;
    }

    public String getNick_pause() {
        return nick_pause;
    }

    public Object getLock_pause() {
        return lock_pause;
    }

    public void autoZoomFullScreen() {

        Helpers.threadRun(new Runnable() {

            public void run() {

                int t;

                if (!Helpers.OSValidator.isMac()) {

                    Helpers.GUIRunAndWait(new Runnable() {
                        @Override
                        public void run() {
                            zoom_menu_in.setEnabled(false);
                            zoom_menu_out.setEnabled(false);
                            zoom_menu_reset.setEnabled(false);
                            full_screen_menu.doClick();
                        }
                    });

                    t = 0;

                    while (t < AUTO_ZOOM_TIMEOUT && !full_screen) {

                        synchronized (full_screen_lock) {
                            try {
                                full_screen_lock.wait(1000);
                                t += 1000;
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                } else {

                    Helpers.GUIRunAndWait(new Runnable() {
                        @Override
                        public void run() {

                            setExtendedState(JFrame.MAXIMIZED_BOTH);
                            setVisible(true);

                        }
                    });
                }

                Helpers.GUIRun(new Runnable() {
                    @Override
                    public void run() {
                        full_screen_menu.setEnabled(false);
                        Helpers.TapetePopupMenu.FULLSCREEN_MENU.setEnabled(false);
                    }
                });

                if (full_screen) {

                    double frameHeight = tapete.getHeight();

                    double frameWidth = tapete.getWidth();

                    t = 0;

                    while (t < AUTO_ZOOM_TIMEOUT && frameWidth == tapete.getWidth() && frameHeight == tapete.getHeight()) {
                        Helpers.pausar(GUI_ZOOM_WAIT);
                        t += GUI_ZOOM_WAIT;
                    }

                }

                if (!tapete.autoZoom()) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "AUTOZOOM TIMEOUT ERROR!");
                }

                Helpers.GUIRun(new Runnable() {
                    @Override
                    public void run() {

                        if (!Game.isRECOVER()) {
                            full_screen_menu.setEnabled(true);
                            Helpers.TapetePopupMenu.FULLSCREEN_MENU.setEnabled(true);
                        }

                        zoom_menu_in.setEnabled(true);
                        zoom_menu_out.setEnabled(true);
                        zoom_menu_reset.setEnabled(true);

                    }
                });
            }
        });
    }

    public ConcurrentHashMap<String, String> getNick2avatar() {
        return nick2avatar;
    }

    public JCheckBoxMenuItem getMenu_cinematicas() {
        return menu_cinematicas;
    }

    public void cambiarColorContadoresTapete(Color color) {

        tapete.getCommunityCards().cambiarColorContadores(color);

    }

    public JRadioButtonMenuItem getMenu_tapete_madera() {
        return menu_tapete_madera;
    }

    public JRadioButtonMenuItem getMenu_tapete_rojo() {
        return menu_tapete_rojo;
    }

    public JRadioButtonMenuItem getMenu_tapete_azul() {
        return menu_tapete_azul;
    }

    public JRadioButtonMenuItem getMenu_tapete_verde() {
        return menu_tapete_verde;
    }

    public JCheckBoxMenuItem getAuto_action_menu() {
        return auto_action_menu;
    }

    public JCheckBoxMenuItem getAnimacion_menu() {
        return animacion_menu;
    }

    public JCheckBoxMenuItem getAscensor_menu() {
        return ascensor_menu;
    }

    public JCheckBoxMenuItem getAuto_rebuy_menu() {
        return auto_rebuy_menu;
    }

    public JMenuItem getChat_menu() {
        return chat_menu;
    }

    public JMenuItem getRegistro_menu() {
        return registro_menu;
    }

    public JCheckBoxMenuItem getSonidos_chorra_menu() {
        return sonidos_chorra_menu;
    }

    public JCheckBoxMenuItem getSonidos_menu() {
        return sonidos_menu;
    }

    public JCheckBoxMenuItem getTime_menu() {
        return time_menu;
    }

    public JMenuItem getZoom_menu_reset() {
        return zoom_menu_reset;
    }

    public JFrame getFrame() {
        return getFull_screen_frame() != null ? getFull_screen_frame() : this;
    }

    public void setConta_tiempo_juego(long conta_tiempo_juego) {
        this.conta_tiempo_juego = conta_tiempo_juego;
    }

    public JMenuItem getJugadas_menu() {
        return jugadas_menu;
    }

    public JMenuItem getExit_menu() {
        return exit_menu;
    }

    public JFrame getFull_screen_frame() {
        return full_screen_frame;
    }

    public void closeWindow() {

        formWindowClosing(null);
    }

    public boolean isFull_screen() {
        return full_screen;
    }

    public JCheckBoxMenuItem getConfirmar_menu() {
        return confirmar_menu;
    }

    public void fullScreen() {

        Helpers.GUIRun(new Runnable() {
            public void run() {

                if (!full_screen) {

                    if (Helpers.OSValidator.isWindows() || Helpers.OSValidator.isMac()) {
                        setVisible(false);
                        getContentPane().remove(Game.getInstance().getTapete());
                        full_screen_frame = new JFrame();
                        full_screen_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                        full_screen_frame.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                                Game.getInstance().closeWindow();
                            }
                        });
                        full_screen_frame.setTitle(Game.getInstance().getTitle());
                        full_screen_frame.setUndecorated(true);
                        full_screen_frame.getContentPane().add(Game.getInstance().getTapete());
                        full_screen_frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        full_screen_frame.setVisible(true);

                        if (timba_pausada) {

                            pausa_dialog.setVisible(false);
                            pausa_dialog.dispose();
                            pausa_dialog = new PauseDialog(full_screen_frame, false);
                            pausa_dialog.setLocationRelativeTo(pausa_dialog.getParent());
                            pausa_dialog.setVisible(true);
                        }

                    } else {

                        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice device = env.getDefaultScreenDevice();
                        menu_bar.setVisible(false);
                        setVisible(false);
                        device.setFullScreenWindow(Game.getInstance());
                    }

                } else {

                    if (Helpers.OSValidator.isWindows() || Helpers.OSValidator.isMac()) {

                        full_screen_frame.getContentPane().remove(Game.getInstance().getTapete());
                        full_screen_frame.setVisible(false);
                        full_screen_frame.dispose();
                        full_screen_frame = null;

                        Game.getInstance().getContentPane().add(Game.getInstance().getTapete());
                        Game.getInstance().setExtendedState(JFrame.MAXIMIZED_BOTH);
                        Game.getInstance().setVisible(true);

                        if (timba_pausada) {

                            pausa_dialog.setVisible(false);
                            pausa_dialog.dispose();
                            pausa_dialog = new PauseDialog(Game.getInstance(), false);
                            pausa_dialog.setLocationRelativeTo(pausa_dialog.getParent());
                            pausa_dialog.setVisible(true);
                        }

                    } else {

                        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice device = env.getDefaultScreenDevice();
                        device.setFullScreenWindow(null);
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                        setVisible(true);
                        menu_bar.setVisible(true);
                    }
                }

                full_screen_menu.setEnabled(true);
                Helpers.TapetePopupMenu.FULLSCREEN_MENU.setEnabled(true);

                full_screen = !full_screen;

                synchronized (full_screen_lock) {
                    full_screen_lock.notifyAll();
                }
            }
        });

    }

    public void cambiarBaraja() {

        Card.updateCachedImages(1f + Game.getZoom_level() * Game.getZOOM_STEP(), true);

        Helpers.playWavResource("misc/uncover.wav", false);

        Player[] players = tapete.getPlayers();

        for (Player jugador : players) {

            jugador.getPlayingCard1().refreshCard();
            jugador.getPlayingCard2().refreshCard();
        }

        for (Card carta : this.tapete.getCommunityCards().getCartasComunes()) {
            carta.refreshCard();
        }

        if (this.jugadas_dialog != null && this.jugadas_dialog.isVisible()) {
            for (Card carta : this.jugadas_dialog.getCartas()) {
                carta.refreshCard();
            }

            Helpers.GUIRun(new Runnable() {
                public void run() {
                    jugadas_dialog.pack();
                }
            });
        }
    }

    public void vistaCompacta() {

        Helpers.playWavResource("misc/uncover.wav", false);

        RemotePlayer[] players = tapete.getRemotePlayers();

        for (RemotePlayer jugador : players) {

            jugador.getPlayingCard1().refreshCard();
            jugador.getPlayingCard2().refreshCard();
        }

        for (Card carta : this.getTapete().getCommunityCards().getCartasComunes()) {
            carta.refreshCard();
        }
    }

    public boolean isGame_over_dialog() {
        return game_over_dialog;
    }

    public boolean isTimba_pausada() {
        return timba_pausada;
    }

    public void pauseTimba(String user) {

        synchronized (lock_pause) {

            if (isPartida_local()) {

                getCrupier().broadcastGAMECommandFromServer("PAUSE#" + (this.timba_pausada ? "0" : "1"), user);

            } else if (getNick_local().equals(user)) {

                getCrupier().sendGAMECommandToServer("PAUSE#" + (this.timba_pausada ? "0" : "1"));

            }

            this.timba_pausada = !this.timba_pausada;

            if (this.timba_pausada) {
                this.nick_pause = user != null ? user : this.getNick_local();
                Helpers.playWavResource("misc/pause.wav");
            } else {
                this.nick_pause = null;
            }

            this.lock_pause.notifyAll();

            if (this.pausa_dialog == null) {
                this.pausa_dialog = new PauseDialog(getFrame(), false);
            }

            Helpers.GUIRun(new Runnable() {
                @Override
                public void run() {
                    if (timba_pausada) {

                        if (isPartida_local() || getNick_local().equals(user)) {
                            Game.getInstance().getTapete().getCommunityCards().getPause_button().setText(Translator.translate("CONTINUAR"));
                            Game.getInstance().getTapete().getCommunityCards().getPause_button().setEnabled(true);

                        } else {
                            Game.getInstance().getTapete().getCommunityCards().getPause_button().setEnabled(false);
                        }

                        pausa_dialog.setLocationRelativeTo(pausa_dialog.getParent());
                        pausa_dialog.setVisible(true);

                    } else {

                        if (isPartida_local()) {
                            Game.getInstance().getTapete().getCommunityCards().getPause_button().setText(Translator.translate("PAUSAR"));
                        } else {
                            Game.getInstance().getTapete().getCommunityCards().getPause_button().setText(Translator.translate("PAUSAR") + " (" + getLocalPlayer().getPause_counter() + ")");
                        }

                        Game.getInstance().getTapete().getCommunityCards().getPause_button().setEnabled((isPartida_local() || getLocalPlayer().getPause_counter() > 0));

                        pausa_dialog.setVisible(false);
                        pausa_dialog.dispose();
                        pausa_dialog = null;

                    }

                }
            });

        }

    }

    public FastChatDialog getFastchat_dialog() {
        return fastchat_dialog;
    }

    public void setGame_over_dialog(boolean game_over_dialog) {
        this.game_over_dialog = game_over_dialog;
    }

    public boolean checkPause() {

        boolean paused = false;

        while (this.timba_pausada || Game.getInstance().getCrupier().isFin_de_la_transmision()) {

            paused = true;

            if (this.timba_pausada) {
                synchronized (this.lock_pause) {
                    try {
                        this.lock_pause.wait(Game.WAIT_PAUSE);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }

        return paused;

    }

    public JMenuItem getFull_screen_menu() {
        return full_screen_menu;
    }

    public static boolean isRECOVER() {
        return RECOVER;
    }

    public static void setRECOVER(boolean RECOVER) {
        Game.RECOVER = RECOVER;
    }

    public JMenuItem getShortcuts_menu() {
        return shortcuts_menu;
    }

    public JMenu getFile_menu() {
        return file_menu;
    }

    public JMenu getHelp_menu() {
        return help_menu;
    }

    public JMenu getOpciones_menu() {
        return opciones_menu;
    }

    public JMenu getZoom_menu() {
        return zoom_menu;
    }

    private void setupGlobalShortcuts() {

        HashMap<KeyStroke, Action> actionMap = new HashMap<>();

        KeyStroke key_pause = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_pause, new AbstractAction("PAUSE") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game.getInstance().getTapete().getCommunityCards().getPause_button().doClick();
            }
        });

        KeyStroke key_full_screen = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_full_screen, new AbstractAction("FULL-SCREEN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                full_screen_menuActionPerformed(e);
            }
        });

        KeyStroke key_visor_jugadas = KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_visor_jugadas, new AbstractAction("VISOR-JUGADAS") {
            @Override
            public void actionPerformed(ActionEvent e) {
                jugadas_menu.doClick();
            }
        });

        KeyStroke compactCards = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(compactCards, new AbstractAction("COMPACT-CARDS") {
            @Override
            public void actionPerformed(ActionEvent e) {
                compact_menu.doClick();
            }
        });

        KeyStroke key_zoom_in = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK);
        actionMap.put(key_zoom_in, new AbstractAction("ZOOM-IN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom_menu_inActionPerformed(e);
            }
        });

        KeyStroke key_zoom_out = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK);
        actionMap.put(key_zoom_out, new AbstractAction("ZOOM-OUT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom_menu_outActionPerformed(e);
            }
        });

        KeyStroke key_zoom_reset = KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK);
        actionMap.put(key_zoom_reset, new AbstractAction("ZOOM-RESET") {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoom_menu_resetActionPerformed(e);
            }
        });

        KeyStroke key_sound = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_sound, new AbstractAction("SOUND") {
            @Override
            public void actionPerformed(ActionEvent e) {
                sonidos_menu.doClick();
            }
        });

        KeyStroke key_chat = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_chat, new AbstractAction("CHAT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                chat_menuActionPerformed(e);
            }
        });

        KeyStroke key_fast_chat = KeyStroke.getKeyStroke('º');
        actionMap.put(key_fast_chat, new AbstractAction("FASTCHAT") {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!fastchat_dialog.isVisible()) {
                    fastchat_dialog.showDialog(getFrame());
                } else {
                    fastchat_dialog.setVisible(false);
                }

            }
        });

        KeyStroke key_registro = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_registro, new AbstractAction("REGISTRO") {
            @Override
            public void actionPerformed(ActionEvent e) {
                registro_menuActionPerformed(e);
            }
        });

        KeyStroke key_time = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.ALT_DOWN_MASK);
        actionMap.put(key_time, new AbstractAction("RELOJ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                time_menu.doClick();
            }
        });

        KeyStroke key_fold = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        actionMap.put(key_fold, new AbstractAction("FOLD-BUTTON") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    getLocalPlayer().getPlayer_fold().doClick();
                }
            }
        });

        KeyStroke key_check = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
        actionMap.put(key_check, new AbstractAction("CHECK-BUTTON") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    if (Game.getInstance().getLocalPlayer().isBoton_mostrar()) {
                        getLocalPlayer().getPlayer_allin().doClick();

                    } else {
                        getLocalPlayer().getPlayer_check().doClick();
                    }
                }
            }
        });

        KeyStroke key_bet = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        actionMap.put(key_bet, new AbstractAction("BET-BUTTON") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    getLocalPlayer().getPlayer_bet_button().doClick();
                }
            }
        });

        KeyStroke key_allin = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
        actionMap.put(key_allin, new AbstractAction("ALLIN-BUTTON") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano() && !Game.getInstance().getLocalPlayer().isBoton_mostrar()) {
                    getLocalPlayer().getPlayer_allin().doClick();
                }
            }
        });

        KeyStroke key_bet_left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        actionMap.put(key_bet_left, new AbstractAction("BET-LEFT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    if (getLocalPlayer().getBet_spinner().isEnabled()) {

                        SpinnerNumberModel model = (SpinnerNumberModel) getLocalPlayer().getBet_spinner().getModel();

                        if (model.getPreviousValue() != null) {

                            getLocalPlayer().getBet_spinner().setValue(model.getPreviousValue());
                        }
                    }
                }
            }
        });

        KeyStroke key_bet_down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        actionMap.put(key_bet_down, new AbstractAction("BET-DOWN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    if (getLocalPlayer().getBet_spinner().isEnabled()) {
                        SpinnerNumberModel model = (SpinnerNumberModel) getLocalPlayer().getBet_spinner().getModel();
                        if (model.getPreviousValue() != null) {
                            getLocalPlayer().getBet_spinner().setValue(model.getPreviousValue());
                        }
                    }
                }
            }
        });

        KeyStroke key_bet_right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        actionMap.put(key_bet_right, new AbstractAction("BET-RIGHT") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    if (getLocalPlayer().getBet_spinner().isEnabled()) {
                        SpinnerNumberModel model = (SpinnerNumberModel) getLocalPlayer().getBet_spinner().getModel();
                        if (model.getNextValue() != null) {
                            getLocalPlayer().getBet_spinner().setValue(model.getNextValue());
                        }
                    }
                }
            }
        });

        KeyStroke key_bet_up = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        actionMap.put(key_bet_up, new AbstractAction("BET-UP") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCrupier().isSincronizando_mano()) {
                    if (getLocalPlayer().getBet_spinner().isEnabled()) {
                        SpinnerNumberModel model = (SpinnerNumberModel) getLocalPlayer().getBet_spinner().getModel();
                        if (model.getNextValue() != null) {
                            getLocalPlayer().getBet_spinner().setValue(model.getNextValue());
                        }
                    }
                }
            }
        });

        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        if (Game.key_event_dispatcher != null) {
            kfm.removeKeyEventDispatcher(Game.key_event_dispatcher);
        }

        Game.key_event_dispatcher = new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);

                JFrame frame = Game.getInstance().getFrame();

                if (actionMap.containsKey(keyStroke) && !file_menu.isSelected() && !zoom_menu.isSelected() && !opciones_menu.isSelected() && !help_menu.isSelected() && (frame.isActive() || (pausa_dialog != null && pausa_dialog.hasFocus()))) {
                    final Action a = actionMap.get(keyStroke);
                    final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null);

                    Helpers.GUIRun(new Runnable() {
                        @Override
                        public void run() {
                            a.actionPerformed(ae);
                        }
                    });

                    return true;
                }

                return false;
            }
        };

        kfm.addKeyEventDispatcher(Game.key_event_dispatcher);
    }

    private WaitingRoom sala_espera;

    public Crupier getCrupier() {
        return crupier;
    }

    public boolean isPartida_local() {
        return partida_local;
    }

    public String getNick_local() {
        return nick_local;
    }

    public Map<String, Participant> getParticipantes() {
        return this.sala_espera.getParticipantes();
    }

    public static float getZOOM_STEP() {
        return ZOOM_STEP;
    }

    public ArrayList<Player> getJugadores() {
        return jugadores;
    }

    public GameLogDialog getRegistro() {

        synchronized (registro_lock) {
            return registro_dialog;
        }
    }

    public Card getFlop1() {
        return tapete.getCommunityCards().getFlop1();
    }

    public Card getFlop2() {
        return tapete.getCommunityCards().getFlop2();
    }

    public JProgressBar getBarra_tiempo() {
        return tapete.getCommunityCards().getBarra_tiempo();
    }

    public Card getFlop3() {
        return tapete.getCommunityCards().getFlop3();
    }

    public LocalPlayer getLocalPlayer() {
        return tapete.getLocalPlayer();
    }

    public Card getRiver() {
        return tapete.getCommunityCards().getRiver();
    }

    public Card getTurn() {
        return tapete.getCommunityCards().getTurn();
    }

    public JMenuItem getZoom_menu_in() {
        return zoom_menu_in;
    }

    public JMenuItem getZoom_menu_out() {
        return zoom_menu_out;
    }

    public TablePanel getTapete() {
        return tapete;
    }

    public Card[] getCartas_comunes() {
        return tapete.getCommunityCards().getCartasComunes();
    }

    public static int getZoom_level() {
        return ZOOM_LEVEL;
    }

    public void setTapeteMano(int mano) {

        Helpers.GUIRun(new Runnable() {
            public void run() {
                tapete.getCommunityCards().getHand_label().setText("#" + String.valueOf(mano) + (Game.MANOS != -1 ? "/" + String.valueOf(Game.MANOS) : ""));

                if (Game.MANOS != -1 && crupier.getMano() > Game.MANOS) {
                    tapete.getCommunityCards().getHand_label().setBackground(Color.red);
                    tapete.getCommunityCards().getHand_label().setForeground(Color.WHITE);
                    tapete.getCommunityCards().getHand_label().setOpaque(true);
                }
            }
        });
    }

    public void zoom(float factor) {

        for (ZoomableInterface zoomeable : zoomeables) {
            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    zoomeable.zoom(factor);
                }
            });
        }

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {

                zoom_menu.setEnabled(true);
            }
        });
    }

    public void setTapeteBote(float bote) {

        Helpers.GUIRun(new Runnable() {
            public void run() {
                tapete.getCommunityCards().getPot_label().setText(Translator.translate("Bote: ") + Helpers.float2String(bote));
            }
        });
    }

    public void setTapeteBote(String bote) {

        Helpers.GUIRun(new Runnable() {
            public void run() {
                tapete.getCommunityCards().getPot_label().setText(Translator.translate("Bote: ") + bote);
            }
        });
    }

    public JCheckBoxMenuItem getTts_menu() {
        return tts_menu;
    }

    public void setTapeteApuestas(float apuestas) {

        Helpers.GUIRun(new Runnable() {
            public void run() {

                String fase = null;

                switch (getCrupier().getFase()) {
                    case Crupier.PREFLOP:
                        fase = "Preflop: ";
                        break;

                    case Crupier.FLOP:
                        fase = "Flop: ";
                        break;

                    case Crupier.TURN:
                        fase = "Turn: ";
                        break;

                    case Crupier.RIVER:
                        fase = "River: ";
                        break;
                }

                tapete.getCommunityCards().getBet_label().setText(fase + Helpers.float2String(apuestas));
                tapete.getCommunityCards().getBet_label().setVisible(true);
            }
        });

    }

    public void refreshTapete() {

        TablePanel nuevo_tapete = TablePanelFactory.downgradePanel(tapete);

        if (nuevo_tapete != null) {

            Game.getInstance().getJugadores().clear();

            for (Player jugador : nuevo_tapete.getPlayers()) {
                Game.getInstance().getJugadores().add(jugador);
            }

            JFrame frame = getFrame();

            Helpers.GUIRunAndWait(new Runnable() {
                public void run() {
                    frame.getContentPane().remove(tapete);

                    tapete = nuevo_tapete;

                    zoomeables = new ZoomableInterface[]{tapete};

                    frame.getContentPane().add(tapete);

                    Game.getInstance().getBarra_tiempo().setMaximum(Game.TIEMPO_PENSAR);

                    Game.getInstance().getBarra_tiempo().setValue(Game.TIEMPO_PENSAR);

                    updateSoundIcon();

                    switch (Game.COLOR_TAPETE) {

                        case "verde":
                            cambiarColorContadoresTapete(new Color(153, 204, 0));
                            break;

                        case "azul":
                            cambiarColorContadoresTapete(new Color(102, 204, 255));
                            break;

                        case "rojo":
                            cambiarColorContadoresTapete(new Color(255, 204, 51));
                            break;

                        case "madera":
                            cambiarColorContadoresTapete(Color.WHITE);
                            break;
                    }

                    Helpers.TapetePopupMenu.addTo(tapete);

                    setupGlobalShortcuts();

                    Helpers.loadOriginalFontSizes(frame);

                    Helpers.updateFonts(frame, Helpers.GUI_FONT, null);

                    Helpers.translateComponents(frame, false);

                    if (Game.getZoom_level() != 0) {

                        Game.getInstance().zoom(1f + Game.getZoom_level() * Game.ZOOM_STEP);

                    }

                    pack();

                }
            });
        }
    }

    public void hideTapeteApuestas() {

        Helpers.GUIRun(new Runnable() {
            public void run() {

                tapete.getCommunityCards().getBet_label().setVisible(false);
            }
        });

    }

    public void setTapeteCiegas(float pequeña, float grande) {

        Helpers.GUIRun(new Runnable() {
            public void run() {
                tapete.getCommunityCards().getBlinds_label().setText(Helpers.float2String(pequeña) + " / " + Helpers.float2String(grande) + (Game.CIEGAS_TIME > 0 ? " @ " + String.valueOf(Game.CIEGAS_TIME) + "'" + (crupier.getCiegas_double() > 0 ? " (" + String.valueOf(crupier.getCiegas_double()) + ")" : "") : ""));
            }
        });

    }

    public WaitingRoom getSala_espera() {
        return sala_espera;
    }

    public void updateSoundIcon() {

        if (tapete.getCommunityCards().getPot_label().getHeight() > 0) {

            Helpers.GUIRun(new Runnable() {
                @Override
                public void run() {
                    tapete.getCommunityCards().getSound_icon().setPreferredSize(new Dimension(tapete.getCommunityCards().getPot_label().getHeight(), tapete.getCommunityCards().getPot_label().getHeight()));
                    tapete.getCommunityCards().getSound_icon().setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound.png" : "/images/mute.png")).getImage().getScaledInstance(tapete.getCommunityCards().getPot_label().getHeight(), tapete.getCommunityCards().getPot_label().getHeight(), Image.SCALE_SMOOTH)));
                }
            });
        } else {
            Helpers.GUIRun(new Runnable() {
                @Override
                public void run() {
                    tapete.getCommunityCards().getSound_icon().setPreferredSize(new Dimension(CommunityCardsPanel.SOUND_ICON_WIDTH, CommunityCardsPanel.SOUND_ICON_WIDTH));
                    tapete.getCommunityCards().getSound_icon().setIcon(new ImageIcon(new ImageIcon(getClass().getResource(Game.SONIDOS ? "/images/sound.png" : "/images/mute.png")).getImage().getScaledInstance(CommunityCardsPanel.SOUND_ICON_WIDTH, CommunityCardsPanel.SOUND_ICON_WIDTH, Image.SCALE_SMOOTH)));
                }
            });
        }
    }

    public JCheckBoxMenuItem getCompact_menu() {
        return compact_menu;
    }

    public JMenu getMenu_barajas() {
        return menu_barajas;
    }

    private void generarBarajasMenu() {

        for (Map.Entry<String, Object[]> entry : Card.BARAJAS.entrySet()) {

            javax.swing.JRadioButtonMenuItem menu_item = new javax.swing.JRadioButtonMenuItem(entry.getKey());

            menu_item.setFont(new java.awt.Font("Dialog", 0, 14));

            menu_item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    Game.BARAJA = menu_item.getText();

                    Helpers.PROPERTIES.setProperty("baraja", menu_item.getText());

                    Helpers.savePropertiesFile();

                    for (Component menu : menu_barajas.getMenuComponents()) {
                        ((javax.swing.JRadioButtonMenuItem) menu).setSelected(false);
                    }

                    menu_item.setSelected(true);

                    for (Component menu : BARAJAS_MENU.getMenuComponents()) {

                        ((javax.swing.JRadioButtonMenuItem) menu).setSelected(((javax.swing.JRadioButtonMenuItem) menu).getText().equals(menu_item.getText()));
                    }

                    Helpers.threadRun(new Runnable() {
                        public void run() {
                            cambiarBaraja();
                        }
                    });
                }
            });

            menu_barajas.add(menu_item);

        }
    }

    /**
     * Creates new form CoronaMainView
     */
    public Game(WaitingRoom salaespera, String nicklocal, boolean partidalocal) {

        THIS = this;

        sala_espera = salaespera; //Esto aquí arriba para que no pete getParticipantes()

        nick_local = nicklocal;

        partida_local = partidalocal;

        tapete = TablePanelFactory.getPanel(getParticipantes().size());

        Player[] players = tapete.getPlayers();

        Map<String, Object[][]> map = Init.MOD != null ? Map.ofEntries(Crupier.ALLIN_CINEMATICS_MOD) : Map.ofEntries(Crupier.ALLIN_CINEMATICS);

        zoomeables = new ZoomableInterface[]{tapete};

        jugadores = new ArrayList<>();

        for (int j = 0; j < getParticipantes().size(); j++) {
            jugadores.add(players[j]);
        }

        for (Map.Entry<String, Participant> entry : getParticipantes().entrySet()) {

            Participant p = entry.getValue();

            if (p != null) {

                if (p.getAvatar() != null) {
                    nick2avatar.put(entry.getKey(), p.getAvatar().getAbsolutePath());
                } else if (partidalocal && p.isCpu()) {
                    nick2avatar.put(entry.getKey(), "*");
                } else {
                    nick2avatar.put(entry.getKey(), "");
                }

            } else {

                nick2avatar.put(entry.getKey(), sala_espera.getLocal_avatar() != null ? sala_espera.getLocal_avatar().getAbsolutePath() : "");
            }
        }

        crupier = new Crupier();

        Helpers.GUIRunAndWait(new Runnable() {
            public void run() {
                initComponents();

                setTitle(Init.WINDOW_TITLE + Translator.translate(" - Timba en curso (") + nicklocal + ")");

                getContentPane().add(tapete);

                auto_rebuy_menu.setSelected(Game.AUTO_REBUY);

                auto_rebuy_menu.setEnabled(Game.REBUY);

                compact_menu.setSelected(Game.VISTA_COMPACTA);

                if (!map.containsKey("allin/") || map.get("allin/").length == 0) {
                    Game.CINEMATICAS = false;
                    menu_cinematicas.setSelected(false);
                    menu_cinematicas.setEnabled(false);

                } else {
                    menu_cinematicas.setSelected(Game.CINEMATICAS);
                }

                rebuy_now_menu.setSelected(false);

                animacion_menu.setSelected(Game.ANIMACION_REPARTIR);

                confirmar_menu.setSelected(Game.CONFIRM_ACTIONS);

                auto_action_menu.setSelected(Game.AUTO_ACTION_BUTTONS);

                sonidos_menu.setSelected(Game.SONIDOS);

                sonidos_chorra_menu.setSelected(Game.SONIDOS_CHORRA);

                ascensor_menu.setSelected(Game.MUSICA_AMBIENTAL);

                sonidos_chorra_menu.setEnabled(sonidos_menu.isSelected());

                ascensor_menu.setEnabled(sonidos_menu.isSelected());

                tts_menu.setSelected(Game.SONIDOS_TTS);

                tts_menu.setEnabled(sonidos_menu.isSelected());

                generarBarajasMenu();

                for (Component menu : menu_barajas.getMenuComponents()) {

                    if (((javax.swing.JRadioButtonMenuItem) menu).getText().equals(Game.BARAJA)) {
                        ((javax.swing.JRadioButtonMenuItem) menu).setSelected(true);
                    } else {
                        ((javax.swing.JRadioButtonMenuItem) menu).setSelected(false);
                    }
                }

                menu_tapete_verde.setSelected(Game.COLOR_TAPETE.equals("verde"));

                menu_tapete_azul.setSelected(Game.COLOR_TAPETE.equals("azul"));

                menu_tapete_rojo.setSelected(Game.COLOR_TAPETE.equals("rojo"));

                menu_tapete_madera.setSelected(Game.COLOR_TAPETE.equals("madera"));

                switch (Game.COLOR_TAPETE) {

                    case "verde":
                        cambiarColorContadoresTapete(new Color(153, 204, 0));
                        break;

                    case "azul":
                        cambiarColorContadoresTapete(new Color(102, 204, 255));
                        break;

                    case "rojo":
                        cambiarColorContadoresTapete(new Color(255, 204, 51));
                        break;

                    case "madera":
                        cambiarColorContadoresTapete(Color.WHITE);
                        break;
                }

                if (!isPartida_local()) {
                    tapete.getCommunityCards().getPause_button().setText(Translator.translate("PAUSAR") + " (" + getLocalPlayer().getPause_counter() + ")");
                } else {
                    tapete.getCommunityCards().getPause_button().setText(Translator.translate("PAUSAR"));
                }

                full_screen_menu.setEnabled(true);

                updateSoundIcon();

                tapete.getCommunityCards().getBarra_tiempo().setMinimum(0);

                tapete.getCommunityCards().getBarra_tiempo().setMaximum(Game.TIEMPO_PENSAR);

                server_separator_menu.setVisible(partida_local);

                tapete.getCommunityCards().getTiempo_partida().setVisible(Game.SHOW_CLOCK);

                time_menu.setSelected(Game.SHOW_CLOCK);

                tapete.getLocalPlayer().getPlayingCard1().setCompactable(false);
                tapete.getLocalPlayer().getPlayingCard2().setCompactable(false);

                //Metemos la pasta a todos (el BUY IN se podría parametrizar)
                for (Player jugador : jugadores) {
                    jugador.setStack(Game.BUYIN);
                }

                setupGlobalShortcuts();

                Helpers.TapetePopupMenu.addTo(tapete);

                Helpers.TapetePopupMenu.AUTOREBUY_MENU.setEnabled(Game.REBUY);

                Helpers.TapetePopupMenu.FULLSCREEN_MENU.setEnabled(true);

                for (Component menu : BARAJAS_MENU.getMenuComponents()) {

                    if (((javax.swing.JRadioButtonMenuItem) menu).getText().equals(Game.BARAJA)) {
                        ((javax.swing.JRadioButtonMenuItem) menu).setSelected(true);
                    } else {
                        ((javax.swing.JRadioButtonMenuItem) menu).setSelected(false);
                    }
                }

                Helpers.TapetePopupMenu.TAPETE_VERDE.setSelected(Game.COLOR_TAPETE.equals("verde"));

                Helpers.TapetePopupMenu.TAPETE_AZUL.setSelected(Game.COLOR_TAPETE.equals("azul"));

                Helpers.TapetePopupMenu.TAPETE_ROJO.setSelected(Game.COLOR_TAPETE.equals("rojo"));

                Helpers.TapetePopupMenu.TAPETE_MADERA.setSelected(Game.COLOR_TAPETE.equals("madera"));

                if (!menu_cinematicas.isEnabled()) {
                    Helpers.TapetePopupMenu.CINEMATICAS_MENU.setEnabled(false);
                    Helpers.TapetePopupMenu.CINEMATICAS_MENU.setSelected(false);
                }

                Helpers.loadOriginalFontSizes(THIS);

                Helpers.updateFonts(THIS, Helpers.GUI_FONT, null);

                Helpers.translateComponents(THIS, false);

                Helpers.translateComponents(Helpers.TapetePopupMenu.popup, false);

                pack();
            }
        });
    }

    public long getConta_tiempo_juego() {
        return conta_tiempo_juego;
    }

    public void finTransmision(boolean partida_terminada) {

        Game.getInstance().getTapete().hideALL();

        Helpers.GUIRun(new Runnable() {
            @Override
            public void run() {
                if (pausa_dialog != null) {
                    pausa_dialog.setVisible(false);
                }
                Game.getInstance().getFastchat_dialog().setVisible(false);
            }
        });

        synchronized (lock_fin) {

            if (this.getLocalPlayer().getAuto_action() != null) {
                this.getLocalPlayer().getAuto_action().stop();
            }

            if (this.getLocalPlayer().getHurryup_timer() != null) {
                this.getLocalPlayer().getHurryup_timer().stop();
            }

            getCrupier().setFin_de_la_transmision(true);

            Helpers.GUIRun(new Runnable() {
                public void run() {
                    exit_menu.setEnabled(false);
                    menu_bar.setVisible(false);
                }
            });

            if (partida_terminada) {

                getRegistro().print("\n*************** LA TIMBA HA TERMINADO ***************");

                getRegistro().print(Translator.translate("FIN DE LA TIMBA -> ") + Helpers.getFechaHoraActual() + " (" + Helpers.seconds2FullTime(conta_tiempo_juego) + ")");

                PreparedStatement statement;

                try {
                    statement = Helpers.getSQLITE().prepareStatement("UPDATE game SET end=? WHERE id=?");
                    statement.setQueryTimeout(30);
                    statement.setLong(1, System.currentTimeMillis());
                    statement.setLong(2, crupier.getSqlite_game_id());
                    statement.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            synchronized (crupier.getLock_contabilidad()) {

                crupier.auditorCuentas();

                for (Map.Entry<String, Float[]> entry : crupier.getAuditor().entrySet()) {

                    Float[] pasta = entry.getValue();

                    String ganancia_msg = "";

                    float ganancia = Helpers.floatClean1D(Helpers.floatClean1D(pasta[0]) - Helpers.floatClean1D(pasta[1]));

                    if (Helpers.float1DSecureCompare(ganancia, 0f) < 0) {
                        ganancia_msg += Translator.translate("PIERDE ") + Helpers.float2String(ganancia * -1f);
                    } else if (Helpers.float1DSecureCompare(ganancia, 0f) > 0) {
                        ganancia_msg += Translator.translate("GANA ") + Helpers.float2String(ganancia);
                    } else {
                        ganancia_msg += Translator.translate("NI GANA NI PIERDE");
                    }

                    getRegistro().print(entry.getKey() + " " + ganancia_msg);
                }
            }

            String log_file = Init.LOGS_DIR + "/CORONAPOKER_TIMBA_" + Helpers.getFechaHoraActual("dd_MM_yyyy__HH_mm_ss") + ".log";

            try {
                Files.writeString(Paths.get(log_file), getRegistro().getText());
            } catch (IOException ex1) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex1);
            }

            String chat_file = Init.LOGS_DIR + "/CORONAPOKER_CHAT_" + Helpers.getFechaHoraActual("dd_MM_yyyy__HH_mm_ss") + ".log";

            try {
                Files.writeString(Paths.get(chat_file), this.getSala_espera().getChat().getText());
            } catch (IOException ex1) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex1);
            }

            if (partida_terminada) {

                BalanceDialog balance = new BalanceDialog(Game.getInstance().getFrame(), true);

                balance.setLocationRelativeTo(balance.getParent());

                balance.setVisible(true);
            }

            if (partida_terminada && Game.CINEMATICAS) {

                HashMap<KeyStroke, Action> actionMap = new HashMap<>();

                KeyStroke key_exit = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
                actionMap.put(key_exit, new AbstractAction("EXIT") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                KeyStroke key_exit2 = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
                actionMap.put(key_exit2, new AbstractAction("EXIT2") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

                if (Game.key_event_dispatcher != null) {
                    kfm.removeKeyEventDispatcher(Game.key_event_dispatcher);
                }

                Game.key_event_dispatcher = new KeyEventDispatcher() {

                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);

                        if (actionMap.containsKey(keyStroke)) {
                            final Action a = actionMap.get(keyStroke);
                            final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null);

                            Helpers.GUIRun(new Runnable() {
                                @Override
                                public void run() {
                                    a.actionPerformed(ae);
                                }
                            });

                            return true;
                        }

                        return false;
                    }
                };

                kfm.addKeyEventDispatcher(Game.key_event_dispatcher);

                final ImageIcon icon;

                if (Init.MOD != null && Files.exists(Paths.get(Helpers.getCurrentJarParentPath() + "/mod/cinematics/misc/end.gif"))) {
                    icon = new ImageIcon(Helpers.getCurrentJarParentPath() + "/mod/cinematics/misc/end.gif");
                } else if (getClass().getResource("/cinematics/misc/end.gif") != null) {
                    icon = new ImageIcon(getClass().getResource("/cinematics/misc/end.gif"));
                } else {
                    icon = null;
                }

                if (icon != null) {

                    GifAnimation gif = new GifAnimation(Game.getInstance().getFrame(), true, icon);

                    Helpers.GUIRun(new Runnable() {
                        public void run() {
                            gif.setLocationRelativeTo(gif.getParent());

                            gif.setVisible(true);
                        }
                    });
                }

                Helpers.muteAllLoopMp3();

                Helpers.playWavResourceAndWait("misc/end.wav");
            }

            Helpers.SQLITEVAC();

            Helpers.forceCloseSQLITE();

            System.exit(0); //No hay otra
        }
    }

    public Timer getTiempo_juego() {
        return tiempo_juego;
    }

    public void AJUGAR() {

        Helpers.stopLoopMp3("misc/waiting_room.mp3");

        if (!Game.SONIDOS) {
            Helpers.muteAll();
        }

        if (Game.LANGUAGE.equals(Game.DEFAULT_LANGUAGE)) {

            Helpers.playWavResource("misc/startplay.wav");
        }

        if (Game.MUSICA_AMBIENTAL) {
            Helpers.unmuteLoopMp3("misc/background_music.mp3");
        }

        ActionListener listener = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {

                if (!crupier.isFin_de_la_transmision() && !crupier.isShow_time() && !crupier.isRebuy_time() && !isTimba_pausada() && !isRECOVER()) {
                    conta_tiempo_juego++;

                    Helpers.GUIRun(new Runnable() {
                        public void run() {

                            tapete.getCommunityCards().getTiempo_partida().setText(Helpers.seconds2FullTime(conta_tiempo_juego));
                        }
                    });
                } else {
                    Helpers.GUIRun(new Runnable() {
                        public void run() {

                            tapete.getCommunityCards().getTiempo_partida().setText("--:--:--");
                        }
                    });
                }

            }
        };

        tiempo_juego = new Timer(1000, listener);

        tiempo_juego.start();

        registro_dialog = new GameLogDialog(this, false);

        getRegistro().print(Translator.translate("COMIENZA LA TIMBA -> ") + Helpers.getFechaHoraActual());

        fastchat_dialog = new FastChatDialog(this, false);

        Helpers.threadRun(crupier);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu_bar = new javax.swing.JMenuBar();
        file_menu = new javax.swing.JMenu();
        chat_menu = new javax.swing.JMenuItem();
        registro_menu = new javax.swing.JMenuItem();
        jugadas_menu = new javax.swing.JMenuItem();
        server_separator_menu = new javax.swing.JPopupMenu.Separator();
        full_screen_menu = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exit_menu = new javax.swing.JMenuItem();
        zoom_menu = new javax.swing.JMenu();
        zoom_menu_in = new javax.swing.JMenuItem();
        zoom_menu_out = new javax.swing.JMenuItem();
        zoom_menu_reset = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        compact_menu = new javax.swing.JCheckBoxMenuItem();
        opciones_menu = new javax.swing.JMenu();
        sonidos_menu = new javax.swing.JCheckBoxMenuItem();
        sonidos_chorra_menu = new javax.swing.JCheckBoxMenuItem();
        ascensor_menu = new javax.swing.JCheckBoxMenuItem();
        tts_menu = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        confirmar_menu = new javax.swing.JCheckBoxMenuItem();
        auto_action_menu = new javax.swing.JCheckBoxMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        menu_cinematicas = new javax.swing.JCheckBoxMenuItem();
        animacion_menu = new javax.swing.JCheckBoxMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        time_menu = new javax.swing.JCheckBoxMenuItem();
        decks_separator = new javax.swing.JPopupMenu.Separator();
        menu_barajas = new javax.swing.JMenu();
        menu_tapetes = new javax.swing.JMenu();
        menu_tapete_verde = new javax.swing.JRadioButtonMenuItem();
        menu_tapete_azul = new javax.swing.JRadioButtonMenuItem();
        menu_tapete_rojo = new javax.swing.JRadioButtonMenuItem();
        menu_tapete_madera = new javax.swing.JRadioButtonMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        auto_rebuy_menu = new javax.swing.JCheckBoxMenuItem();
        rebuy_now_menu = new javax.swing.JCheckBoxMenuItem();
        help_menu = new javax.swing.JMenu();
        shortcuts_menu = new javax.swing.JMenuItem();
        acerca_menu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CoronaPoker");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/images/avatar_default.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        menu_bar.setDoubleBuffered(true);
        menu_bar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        file_menu.setMnemonic('i');
        file_menu.setText("Archivo");
        file_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        chat_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        chat_menu.setText("Ver chat (ALT+C)");
        chat_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chat_menuActionPerformed(evt);
            }
        });
        file_menu.add(chat_menu);

        registro_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        registro_menu.setText("Ver registro (ALT+R)");
        registro_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registro_menuActionPerformed(evt);
            }
        });
        file_menu.add(registro_menu);

        jugadas_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jugadas_menu.setText("Generador de jugadas (ALT+J)");
        jugadas_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jugadas_menuActionPerformed(evt);
            }
        });
        file_menu.add(jugadas_menu);
        file_menu.add(server_separator_menu);

        full_screen_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        full_screen_menu.setText("PANTALLA COMPLETA (ALT+F)");
        full_screen_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                full_screen_menuActionPerformed(evt);
            }
        });
        file_menu.add(full_screen_menu);
        file_menu.add(jSeparator2);

        exit_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        exit_menu.setText("Salir (ALT+F4)");
        exit_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_menuActionPerformed(evt);
            }
        });
        file_menu.add(exit_menu);

        menu_bar.add(file_menu);

        zoom_menu.setText("Zoom");
        zoom_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        zoom_menu_in.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        zoom_menu_in.setText("Aumentar (CTRL++)");
        zoom_menu_in.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_menu_inActionPerformed(evt);
            }
        });
        zoom_menu.add(zoom_menu_in);

        zoom_menu_out.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        zoom_menu_out.setText("Reducir (CTRL+-)");
        zoom_menu_out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_menu_outActionPerformed(evt);
            }
        });
        zoom_menu.add(zoom_menu_out);

        zoom_menu_reset.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        zoom_menu_reset.setText("Reset (CTRL+0)");
        zoom_menu_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_menu_resetActionPerformed(evt);
            }
        });
        zoom_menu.add(zoom_menu_reset);
        zoom_menu.add(jSeparator6);

        compact_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        compact_menu.setSelected(true);
        compact_menu.setText("Vista compacta (ALT+X)");
        compact_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compact_menuActionPerformed(evt);
            }
        });
        zoom_menu.add(compact_menu);

        menu_bar.add(zoom_menu);

        opciones_menu.setText("Preferencias");
        opciones_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        sonidos_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        sonidos_menu.setSelected(true);
        sonidos_menu.setText("SONIDOS (ALT+S)");
        sonidos_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sonidos_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(sonidos_menu);

        sonidos_chorra_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        sonidos_chorra_menu.setSelected(true);
        sonidos_chorra_menu.setText("Sonidos de coña");
        sonidos_chorra_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sonidos_chorra_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(sonidos_chorra_menu);

        ascensor_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ascensor_menu.setSelected(true);
        ascensor_menu.setText("Música ambiental");
        ascensor_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ascensor_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(ascensor_menu);

        tts_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        tts_menu.setSelected(true);
        tts_menu.setText("TTS");
        tts_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tts_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(tts_menu);
        opciones_menu.add(jSeparator1);

        confirmar_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        confirmar_menu.setSelected(true);
        confirmar_menu.setText("Confirmar todas las acciones");
        confirmar_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmar_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(confirmar_menu);

        auto_action_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        auto_action_menu.setSelected(true);
        auto_action_menu.setText("Botones AUTO");
        auto_action_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auto_action_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(auto_action_menu);
        opciones_menu.add(jSeparator7);

        menu_cinematicas.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        menu_cinematicas.setSelected(true);
        menu_cinematicas.setText("Cinemáticas");
        menu_cinematicas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_cinematicasActionPerformed(evt);
            }
        });
        opciones_menu.add(menu_cinematicas);

        animacion_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        animacion_menu.setSelected(true);
        animacion_menu.setText("Animación al repartir");
        animacion_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animacion_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(animacion_menu);
        opciones_menu.add(jSeparator8);

        time_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        time_menu.setSelected(true);
        time_menu.setText("Mostrar reloj (ALT+W)");
        time_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                time_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(time_menu);
        opciones_menu.add(decks_separator);

        menu_barajas.setText("Barajas");
        menu_barajas.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        opciones_menu.add(menu_barajas);

        menu_tapetes.setText("Tapetes");
        menu_tapetes.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        menu_tapete_verde.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        menu_tapete_verde.setSelected(true);
        menu_tapete_verde.setText("Verde");
        menu_tapete_verde.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_tapete_verdeActionPerformed(evt);
            }
        });
        menu_tapetes.add(menu_tapete_verde);

        menu_tapete_azul.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        menu_tapete_azul.setSelected(true);
        menu_tapete_azul.setText("Azul");
        menu_tapete_azul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_tapete_azulActionPerformed(evt);
            }
        });
        menu_tapetes.add(menu_tapete_azul);

        menu_tapete_rojo.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        menu_tapete_rojo.setSelected(true);
        menu_tapete_rojo.setText("Rojo");
        menu_tapete_rojo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_tapete_rojoActionPerformed(evt);
            }
        });
        menu_tapetes.add(menu_tapete_rojo);

        menu_tapete_madera.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        menu_tapete_madera.setSelected(true);
        menu_tapete_madera.setText("Sin tapete");
        menu_tapete_madera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_tapete_maderaActionPerformed(evt);
            }
        });
        menu_tapetes.add(menu_tapete_madera);

        opciones_menu.add(menu_tapetes);
        opciones_menu.add(jSeparator4);

        auto_rebuy_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        auto_rebuy_menu.setSelected(true);
        auto_rebuy_menu.setText("Recompra automática");
        auto_rebuy_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auto_rebuy_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(auto_rebuy_menu);

        rebuy_now_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rebuy_now_menu.setSelected(true);
        rebuy_now_menu.setText("Recomprar (siguiente mano)");
        rebuy_now_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rebuy_now_menuActionPerformed(evt);
            }
        });
        opciones_menu.add(rebuy_now_menu);

        menu_bar.add(opciones_menu);

        help_menu.setText("Ayuda");
        help_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        shortcuts_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        shortcuts_menu.setText("ATAJOS");
        shortcuts_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shortcuts_menuActionPerformed(evt);
            }
        });
        help_menu.add(shortcuts_menu);

        acerca_menu.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        acerca_menu.setText("Acerca de");
        acerca_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acerca_menuActionPerformed(evt);
            }
        });
        help_menu.add(acerca_menu);

        menu_bar.add(help_menu);

        setJMenuBar(menu_bar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exit_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_menuActionPerformed
        // TODO add your handling code here:

        if (this.isPartida_local()) {

            if (jugadores.size() > 1) {

                // 0=yes, 1=no, 2=cancel
                if (Helpers.mostrarMensajeInformativoSINO(this, "¡CUIDADO! ERES EL ANFITRIÓN Y SI SALES SE TERMINARÁ LA TIMBA. ¿ESTÁS SEGURO?") == 0) {

                    Helpers.threadRun(new Runnable() {
                        public void run() {
                            //Hay que avisar a los clientes de que la timba ha terminado
                            crupier.broadcastGAMECommandFromServer("SERVEREXIT", null, false);

                            getLocalPlayer().setExit();

                            finTransmision(true);
                        }
                    });

                }

            } else {

                Helpers.threadRun(new Runnable() {
                    public void run() {

                        getLocalPlayer().setExit();

                        finTransmision(true);
                    }
                });
            }

        } else {
            // 0=yes, 1=no, 2=cancel
            if (Helpers.mostrarMensajeInformativoSINO(this, "¡CUIDADO! Si sales de la timba no podrás volver a entrar. ¿ESTÁS SEGURO?") == 0) {

                Helpers.threadRun(new Runnable() {
                    public void run() {

                        if (!getSala_espera().isReconnecting()) {
                            crupier.sendGAMECommandToServer("EXIT", false);
                        }

                        getLocalPlayer().setExit();

                        finTransmision(false);
                    }
                });

            }
        }

    }//GEN-LAST:event_exit_menuActionPerformed

    private void acerca_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acerca_menuActionPerformed
        // TODO add your handling code here:
        this.about_dialog = new AboutDialog(this, true);

        this.about_dialog.setLocationRelativeTo(about_dialog.getParent());

        this.about_dialog.setVisible(true);
    }//GEN-LAST:event_acerca_menuActionPerformed

    private void zoom_menu_inActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_menu_inActionPerformed
        // TODO add your handling code here:

        zoom_menu.setEnabled(false);

        ZOOM_LEVEL++;

        Helpers.PROPERTIES.setProperty("zoom_level", String.valueOf(ZOOM_LEVEL));

        Card.updateCachedImages(1f + ZOOM_LEVEL * ZOOM_STEP, false);

        Helpers.threadRun(new Runnable() {
            @Override
            public void run() {
                zoom(1f + ZOOM_LEVEL * ZOOM_STEP);

                if (jugadas_dialog != null && jugadas_dialog.isVisible()) {

                    for (Card carta : jugadas_dialog.getCartas()) {
                        carta.refreshCard();
                    }

                    Helpers.GUIRun(new Runnable() {
                        public void run() {
                            jugadas_dialog.pack();
                        }
                    });
                }

            }
        });

        Helpers.savePropertiesFile();
    }//GEN-LAST:event_zoom_menu_inActionPerformed

    private void zoom_menu_outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_menu_outActionPerformed
        // TODO add your handling code here:

        zoom_menu.setEnabled(false);

        if (Helpers.float1DSecureCompare(0f, 1f + ((ZOOM_LEVEL - 1) * ZOOM_STEP)) < 0) {
            ZOOM_LEVEL--;
            Helpers.PROPERTIES.setProperty("zoom_level", String.valueOf(ZOOM_LEVEL));
            Card.updateCachedImages(1f + ZOOM_LEVEL * ZOOM_STEP, false);
            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    zoom(1f + ZOOM_LEVEL * ZOOM_STEP);

                    if (jugadas_dialog != null && jugadas_dialog.isVisible()) {

                        for (Card carta : jugadas_dialog.getCartas()) {
                            carta.refreshCard();
                        }

                        Helpers.GUIRun(new Runnable() {
                            public void run() {
                                jugadas_dialog.pack();
                            }
                        });
                    }
                }
            });

            Helpers.savePropertiesFile();
        }

    }//GEN-LAST:event_zoom_menu_outActionPerformed

    private void zoom_menu_resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_menu_resetActionPerformed
        // TODO add your handling code here:

        if (ZOOM_LEVEL != DEFAULT_ZOOM_LEVEL) {
            zoom_menu.setEnabled(false);

            ZOOM_LEVEL = DEFAULT_ZOOM_LEVEL;

            Helpers.PROPERTIES.setProperty("zoom_level", String.valueOf(ZOOM_LEVEL));
            Card.updateCachedImages(1f + ZOOM_LEVEL * ZOOM_STEP, false);
            Helpers.threadRun(new Runnable() {
                @Override
                public void run() {
                    zoom(1f + ZOOM_LEVEL * ZOOM_STEP);
                    if (jugadas_dialog != null && jugadas_dialog.isVisible()) {

                        for (Card carta : jugadas_dialog.getCartas()) {
                            carta.refreshCard();
                        }

                        Helpers.GUIRun(new Runnable() {
                            public void run() {
                                jugadas_dialog.pack();
                            }
                        });
                    }
                }
            });

            Helpers.savePropertiesFile();
        }
    }//GEN-LAST:event_zoom_menu_resetActionPerformed

    private void registro_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registro_menuActionPerformed
        // TODO add your handling code here:

        this.registro_dialog.setVisible(false);

        this.registro_dialog.setLocationRelativeTo(getFrame());

        this.registro_dialog.setVisible(true);

    }//GEN-LAST:event_registro_menuActionPerformed

    private void chat_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chat_menuActionPerformed
        // TODO add your handling code here:

        if (!this.sala_espera.isActive()) {
            this.sala_espera.setVisible(false);
        }

        this.sala_espera.setLocationRelativeTo(getFrame());
        this.sala_espera.setExtendedState(JFrame.NORMAL);
        this.sala_espera.setVisible(true);

    }//GEN-LAST:event_chat_menuActionPerformed

    private void sonidos_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sonidos_menuActionPerformed
        // TODO add your handling code here:

        Game.SONIDOS = this.sonidos_menu.isSelected();

        Helpers.PROPERTIES.setProperty("sonidos", Game.SONIDOS ? "true" : "false");

        Helpers.savePropertiesFile();

        updateSoundIcon();

        this.sonidos_chorra_menu.setEnabled(Game.SONIDOS);

        this.ascensor_menu.setEnabled(Game.SONIDOS);

        this.tts_menu.setEnabled(Game.SONIDOS);

        if (!Game.SONIDOS) {

            Helpers.muteAll();

        } else {

            Helpers.unMuteAll();
        }

        Helpers.TapetePopupMenu.SONIDOS_MENU.setSelected(Game.SONIDOS);

        Helpers.TapetePopupMenu.SONIDOS_COMENTARIOS_MENU.setEnabled(Game.SONIDOS);

        Helpers.TapetePopupMenu.SONIDOS_MUSICA_MENU.setEnabled(Game.SONIDOS);

        Helpers.TapetePopupMenu.SONIDOS_TTS_MENU.setEnabled(Game.SONIDOS);
    }//GEN-LAST:event_sonidos_menuActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        this.exit_menu.doClick();
    }//GEN-LAST:event_formWindowClosing

    private void time_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_time_menuActionPerformed
        // TODO add your handling code here:

        Game.SHOW_CLOCK = time_menu.isSelected();

        tapete.getCommunityCards().getTiempo_partida().setVisible(time_menu.isSelected());

        Helpers.PROPERTIES.setProperty("show_time", this.time_menu.isSelected() ? "true" : "false");

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.RELOJ_MENU.setSelected(Game.SHOW_CLOCK);
    }//GEN-LAST:event_time_menuActionPerformed

    private void sonidos_chorra_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sonidos_chorra_menuActionPerformed
        // TODO add your handling code here:
        Game.SONIDOS_CHORRA = this.sonidos_chorra_menu.isSelected();

        Helpers.PROPERTIES.setProperty("sonidos_chorra", this.sonidos_chorra_menu.isSelected() ? "true" : "false");

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.SONIDOS_COMENTARIOS_MENU.setSelected(Game.SONIDOS_CHORRA);

    }//GEN-LAST:event_sonidos_chorra_menuActionPerformed

    private void ascensor_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ascensor_menuActionPerformed
        // TODO add your handling code here:
        Game.MUSICA_AMBIENTAL = this.ascensor_menu.isSelected();

        Helpers.PROPERTIES.setProperty("sonido_ascensor", this.ascensor_menu.isSelected() ? "true" : "false");

        Helpers.savePropertiesFile();

        if (this.ascensor_menu.isSelected()) {
            Helpers.unmuteLoopMp3("misc/background_music.mp3");
        } else {
            Helpers.muteLoopMp3("misc/background_music.mp3");
        }

        Helpers.TapetePopupMenu.SONIDOS_MUSICA_MENU.setSelected(Game.MUSICA_AMBIENTAL);
    }//GEN-LAST:event_ascensor_menuActionPerformed

    private void jugadas_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jugadas_menuActionPerformed
        // TODO add your handling code here:

        Helpers.threadRun(new Runnable() {
            public void run() {
                if (jugadas_dialog == null) {

                    jugadas_dialog = new HandGeneratorDialog(Game.getInstance(), false);

                    jugadas_dialog.pintarJugada();

                } else {
                    Helpers.GUIRun(new Runnable() {
                        public void run() {
                            jugadas_dialog.setVisible(false);
                        }
                    });
                }

                for (Card carta : jugadas_dialog.getCartas()) {
                    carta.refreshCard();
                }

                Helpers.GUIRun(new Runnable() {
                    public void run() {
                        jugadas_dialog.pack();

                        jugadas_dialog.setLocationRelativeTo(getFrame());

                        jugadas_dialog.setVisible(true);
                    }
                });

            }
        });
    }//GEN-LAST:event_jugadas_menuActionPerformed

    private void full_screen_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_full_screen_menuActionPerformed
        // TODO add your handling code here:

        if (this.full_screen_menu.isEnabled() && !this.isGame_over_dialog()) {

            this.full_screen_menu.setEnabled(false);

            Helpers.TapetePopupMenu.FULLSCREEN_MENU.setSelected(!this.full_screen);

            Helpers.TapetePopupMenu.FULLSCREEN_MENU.setEnabled(false);

            fullScreen();
        }

    }//GEN-LAST:event_full_screen_menuActionPerformed

    private void auto_rebuy_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auto_rebuy_menuActionPerformed
        // TODO add your handling code here:

        Game.AUTO_REBUY = this.auto_rebuy_menu.isSelected();

        Helpers.PROPERTIES.setProperty("auto_rebuy", this.auto_rebuy_menu.isSelected() ? "true" : "false");

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.AUTOREBUY_MENU.setSelected(Game.AUTO_REBUY);

        Helpers.playWavResource("misc/cash_register.wav");

    }//GEN-LAST:event_auto_rebuy_menuActionPerformed

    private void compact_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compact_menuActionPerformed
        // TODO add your handling code here:
        Game.VISTA_COMPACTA = this.compact_menu.isSelected();

        Helpers.PROPERTIES.setProperty("vista_compacta", String.valueOf(Game.VISTA_COMPACTA));

        Helpers.savePropertiesFile();

        Helpers.threadRun(new Runnable() {
            public void run() {
                vistaCompacta();
            }
        });

        Helpers.TapetePopupMenu.COMPACTA_MENU.setSelected(Game.VISTA_COMPACTA);
    }//GEN-LAST:event_compact_menuActionPerformed

    private void confirmar_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmar_menuActionPerformed
        // TODO add your handling code here:
        Game.CONFIRM_ACTIONS = this.confirmar_menu.isSelected();

        Helpers.PROPERTIES.setProperty("confirmar_todo", String.valueOf(Game.CONFIRM_ACTIONS));

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.CONFIRM_MENU.setSelected(Game.CONFIRM_ACTIONS);

        if (!Game.CONFIRM_ACTIONS) {
            this.getLocalPlayer().desarmarBotonesAccion();
        }

    }//GEN-LAST:event_confirmar_menuActionPerformed

    private void animacion_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animacion_menuActionPerformed
        // TODO add your handling code here:

        Game.ANIMACION_REPARTIR = this.animacion_menu.isSelected();

        Helpers.PROPERTIES.setProperty("animacion_reparto", String.valueOf(Game.ANIMACION_REPARTIR));

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.ANIMACION_MENU.setSelected(Game.ANIMACION_REPARTIR);
    }//GEN-LAST:event_animacion_menuActionPerformed

    private void auto_action_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auto_action_menuActionPerformed
        // TODO add your handling code here:
        Game.AUTO_ACTION_BUTTONS = this.auto_action_menu.isSelected();

        Helpers.PROPERTIES.setProperty("auto_action_buttons", String.valueOf(Game.AUTO_ACTION_BUTTONS));

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.AUTO_ACTION_MENU.setSelected(Game.AUTO_ACTION_BUTTONS);

        if (Game.AUTO_ACTION_BUTTONS) {
            this.getLocalPlayer().activarPreBotones();
        } else {
            this.getLocalPlayer().desActivarPreBotones();
        }
    }//GEN-LAST:event_auto_action_menuActionPerformed

    private void menu_tapete_verdeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_tapete_verdeActionPerformed
        // TODO add your handling code here:
        Game.COLOR_TAPETE = "verde";

        Helpers.PROPERTIES.setProperty("color_tapete", "verde");

        Helpers.savePropertiesFile();

        for (Component c : this.menu_tapetes.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        this.menu_tapete_verde.setSelected(true);

        tapete.refresh();

        cambiarColorContadoresTapete(new Color(153, 204, 0));

        for (Component c : Helpers.TapetePopupMenu.TAPETES_MENU.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        Helpers.TapetePopupMenu.TAPETE_VERDE.setSelected(true);
    }//GEN-LAST:event_menu_tapete_verdeActionPerformed

    private void menu_tapete_azulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_tapete_azulActionPerformed
        // TODO add your handling code here:

        Game.COLOR_TAPETE = "azul";

        Helpers.PROPERTIES.setProperty("color_tapete", "azul");

        Helpers.savePropertiesFile();

        for (Component c : this.menu_tapetes.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        this.menu_tapete_azul.setSelected(true);

        tapete.refresh();

        cambiarColorContadoresTapete(new Color(102, 204, 255));

        for (Component c : Helpers.TapetePopupMenu.TAPETES_MENU.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        Helpers.TapetePopupMenu.TAPETE_AZUL.setSelected(true);
    }//GEN-LAST:event_menu_tapete_azulActionPerformed

    private void menu_tapete_rojoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_tapete_rojoActionPerformed
        // TODO add your handling code here:
        Game.COLOR_TAPETE = "rojo";

        Helpers.PROPERTIES.setProperty("color_tapete", "rojo");

        Helpers.savePropertiesFile();

        for (Component c : this.menu_tapetes.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        this.menu_tapete_rojo.setSelected(true);

        tapete.refresh();

        cambiarColorContadoresTapete(new Color(255, 204, 51));

        for (Component c : Helpers.TapetePopupMenu.TAPETES_MENU.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        Helpers.TapetePopupMenu.TAPETE_ROJO.setSelected(true);
    }//GEN-LAST:event_menu_tapete_rojoActionPerformed

    private void menu_tapete_maderaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_tapete_maderaActionPerformed
        // TODO add your handling code here:
        Game.COLOR_TAPETE = "madera";

        Helpers.PROPERTIES.setProperty("color_tapete", "madera");

        Helpers.savePropertiesFile();

        for (Component c : this.menu_tapetes.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        this.menu_tapete_madera.setSelected(true);

        tapete.refresh();

        cambiarColorContadoresTapete(Color.WHITE);

        for (Component c : Helpers.TapetePopupMenu.TAPETES_MENU.getMenuComponents()) {
            ((JRadioButtonMenuItem) c).setSelected(false);
        }

        Helpers.TapetePopupMenu.TAPETE_MADERA.setSelected(true);
    }//GEN-LAST:event_menu_tapete_maderaActionPerformed

    private void menu_cinematicasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_cinematicasActionPerformed
        // TODO add your handling code here:

        Game.CINEMATICAS = this.menu_cinematicas.isSelected();

        Helpers.PROPERTIES.setProperty("cinematicas", String.valueOf(Game.CINEMATICAS));

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.CINEMATICAS_MENU.setSelected(Game.CINEMATICAS);
    }//GEN-LAST:event_menu_cinematicasActionPerformed

    private void shortcuts_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shortcuts_menuActionPerformed
        // TODO add your handling code here:
        Helpers.mostrarMensajeInformativo(getFrame(), Translator.translate("PASAR/IR -> [ESPACIO]\n\nAPOSTAR -> [ENTER] (FLECHA ARRIBA/ABAJO PARA SUBIR/BAJAR APUESTA)\n\nALL IN -> [MAYUS + ENTER]\n\nNO IR -> [ESC]\n\nMOSTRAR CARTAS -> [ESPACIO]\n\nMENSAJE CHAT RÁPIDO -> [º]"));

    }//GEN-LAST:event_shortcuts_menuActionPerformed

    private void tts_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tts_menuActionPerformed
        // TODO add your handling code here:

        Game.SONIDOS_TTS = this.tts_menu.isSelected();

        Helpers.PROPERTIES.setProperty("sonidos_tts", this.tts_menu.isSelected() ? "true" : "false");

        Helpers.savePropertiesFile();

        Helpers.TapetePopupMenu.SONIDOS_TTS_MENU.setSelected(Game.SONIDOS_TTS);

        if (Game.SONIDOS_TTS) {
            Helpers.TTS_BLOCKED_USERS.clear();
        }
    }//GEN-LAST:event_tts_menuActionPerformed

    public RebuyNowDialog getRebuy_dialog() {
        return rebuy_dialog;
    }

    private void rebuy_now_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rebuy_now_menuActionPerformed
        // TODO add your handling code here:

        Helpers.TapetePopupMenu.REBUY_NOW_MENU.setSelected(this.rebuy_now_menu.isSelected());

        LocalPlayer player = Game.getInstance().getLocalPlayer();

        if (!getCrupier().isRebuy_time() && player.isActivo()) {

            this.rebuy_now_menu.setEnabled(false);
            Helpers.TapetePopupMenu.REBUY_NOW_MENU.setEnabled(false);

            if (crupier.getRebuy_now().containsKey(player.getNickname())) {

                player.getPlayer_buyin().setBackground(Helpers.float1DSecureCompare((float) Game.BUYIN, player.getBuyin()) == 0 ? new Color(204, 204, 204) : Color.cyan);
                player.getPlayer_buyin().setText(String.valueOf(player.getBuyin()));

                Helpers.threadRun(new Runnable() {
                    public void run() {
                        crupier.rebuyNow(player.getNickname(), -1);
                        rebuy_now_menu.setEnabled(true);
                        Helpers.TapetePopupMenu.REBUY_NOW_MENU.setEnabled(true);
                        Helpers.playWavResource("misc/auto_button_off.wav");
                    }
                });

            } else {

                if (!Game.REBUY) {
                    Helpers.mostrarMensajeError(Game.getInstance().getFrame(), "NO SE PUEDE RECOMPRAR EN ESTA TIMBA");
                    rebuy_now_menu.setEnabled(true);
                    rebuy_now_menu.setSelected(false);
                    Helpers.TapetePopupMenu.REBUY_NOW_MENU.setEnabled(true);
                    Helpers.TapetePopupMenu.REBUY_NOW_MENU.setSelected(false);
                } else if (Helpers.float1DSecureCompare(player.getStack() + (player.getDecision() != Player.FOLD ? player.getBote() : 0f) + player.getPagar(), (float) Game.BUYIN) >= 0) {
                    Helpers.mostrarMensajeError(Game.getInstance().getFrame(), Translator.translate("PARA RECOMPRAR DEBES TENER MENOS DE ") + Game.BUYIN);
                    rebuy_now_menu.setEnabled(true);
                    rebuy_now_menu.setSelected(false);
                    Helpers.TapetePopupMenu.REBUY_NOW_MENU.setEnabled(true);
                    Helpers.TapetePopupMenu.REBUY_NOW_MENU.setSelected(false);
                } else {

                    rebuy_dialog = new RebuyNowDialog(Game.getInstance().getFrame(), true, true);

                    rebuy_dialog.setLocationRelativeTo(rebuy_dialog.getParent());

                    rebuy_dialog.setVisible(true);

                    if (rebuy_dialog.isRebuy()) {
                        player.getPlayer_buyin().setBackground(Color.YELLOW);
                        player.getPlayer_buyin().setText(String.valueOf(player.getBuyin() + Integer.parseInt((String) rebuy_dialog.getRebuy_checkbox().getSelectedItem())));

                        Helpers.threadRun(new Runnable() {
                            public void run() {
                                crupier.rebuyNow(player.getNickname(), Integer.parseInt((String) rebuy_dialog.getRebuy_checkbox().getSelectedItem()));
                                rebuy_now_menu.setEnabled(true);
                                Helpers.TapetePopupMenu.REBUY_NOW_MENU.setEnabled(true);
                                Helpers.playWavResource("misc/auto_button_on.wav");
                            }
                        });
                    } else {
                        rebuy_now_menu.setEnabled(true);
                        rebuy_now_menu.setSelected(false);
                        Helpers.TapetePopupMenu.REBUY_NOW_MENU.setEnabled(true);
                        Helpers.TapetePopupMenu.REBUY_NOW_MENU.setSelected(false);
                    }

                    rebuy_dialog = null;
                }

            }

        }
    }//GEN-LAST:event_rebuy_now_menuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem acerca_menu;
    private javax.swing.JCheckBoxMenuItem animacion_menu;
    private javax.swing.JCheckBoxMenuItem ascensor_menu;
    private javax.swing.JCheckBoxMenuItem auto_action_menu;
    private javax.swing.JCheckBoxMenuItem auto_rebuy_menu;
    private javax.swing.JMenuItem chat_menu;
    private javax.swing.JCheckBoxMenuItem compact_menu;
    private javax.swing.JCheckBoxMenuItem confirmar_menu;
    private javax.swing.JPopupMenu.Separator decks_separator;
    private javax.swing.JMenuItem exit_menu;
    private javax.swing.JMenu file_menu;
    private javax.swing.JMenuItem full_screen_menu;
    private javax.swing.JMenu help_menu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JMenuItem jugadas_menu;
    private javax.swing.JMenuBar menu_bar;
    private javax.swing.JMenu menu_barajas;
    private javax.swing.JCheckBoxMenuItem menu_cinematicas;
    private javax.swing.JRadioButtonMenuItem menu_tapete_azul;
    private javax.swing.JRadioButtonMenuItem menu_tapete_madera;
    private javax.swing.JRadioButtonMenuItem menu_tapete_rojo;
    private javax.swing.JRadioButtonMenuItem menu_tapete_verde;
    private javax.swing.JMenu menu_tapetes;
    private javax.swing.JMenu opciones_menu;
    private javax.swing.JCheckBoxMenuItem rebuy_now_menu;
    private javax.swing.JMenuItem registro_menu;
    private javax.swing.JPopupMenu.Separator server_separator_menu;
    private javax.swing.JMenuItem shortcuts_menu;
    private javax.swing.JCheckBoxMenuItem sonidos_chorra_menu;
    private javax.swing.JCheckBoxMenuItem sonidos_menu;
    private javax.swing.JCheckBoxMenuItem time_menu;
    private javax.swing.JCheckBoxMenuItem tts_menu;
    private javax.swing.JMenu zoom_menu;
    private javax.swing.JMenuItem zoom_menu_in;
    private javax.swing.JMenuItem zoom_menu_out;
    private javax.swing.JMenuItem zoom_menu_reset;
    // End of variables declaration//GEN-END:variables
}

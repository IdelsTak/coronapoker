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

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author tonikelope
 */
public class Translator {

    private static volatile String LANG = "";

    private static final ConcurrentHashMap<String, String> _rosetta = new ConcurrentHashMap();

    private static final ConcurrentHashMap<String, String> _attesor = new ConcurrentHashMap();

    private static void English() {

        String[][] rosetta = {
            {"¿SEGURO QUE QUIERES TIRARTE?", "SURE YOU FOLD?"},
            {"NO SE HA PODIDO ACTUALIZAR (ERROR INESPERADO)", "FAILED TO UPDATE (UNEXPECTED ERROR)"},
            {"ACTUALIZANDO >>> ", "UPDATING >>> "},
            {"NO SE HA PODIDO ACTUALIZAR (ERROR AL DESCARGAR EL ACTUALIZADOR)", "FAILED TO UPDATE (ERROR WHILE DOWNLOADING UPDATER)"},
            {"ALGORITMO para barajar:", "Shuffle ALGORITHM:"},
            {"Random.org [TRNG] (Excelente)", "Random.org [TRNG] (Excellent)"},
            {"Fisher-Yates [CSPRNG HASH-DRBG SHA-512] (Muy bueno)", "Fisher-Yates [CSPRNG HASH-DRBG SHA-512] (Very good)"},
            {"¿ELIMINAR TODAS LAS TIMBAS DONDE PARTICIPÓ ESE JUGADOR?\n(Nota: las timbas eliminadas no se pueden continuar)", "REMOVE ALL GAMES IN WHICH THAT PLAYER PARTICIPATED?\n(Note: removed games can not be resumed)"},
            {"SE HAN BORRADO TODAS LAS TIMBAS DONDE PARTICIPÓ ESE JUGADOR", "ALL THE GAMES IN WHICH THAT PLAYER PARTICIPATED HAVE BEEN DELETED"},
            {"NO HAY TIMBAS EN LAS CUALES HAYA PARTICIPADO ESE JUGADOR", "THERE ARE NO GAMES IN WHICH THIS PLAYER HAS PARTICIPATED"},
            {"Este usuario tiene problemas de conexión. ¿LO SACAMOS DE LA TIMBA?", "This user is having connection problems. THROW OUT?"},
            {"Click para copiar enlace", "Click to copy link"},
            {"¡ENLACE COPIADO EN EL PORTAPAPELES!", "LINK COPIED TO CLIPBOARD!"},
            {"OTRA TIMBA", "RETRY"},
            {"LUCES ON", "LIGHTS ON"},
            {"LUCES OFF", "LIGHTS OFF"},
            {"PREPARANDO ACTUALIZACIÓN...", "PREPARING UPDATE..."},
            {"COMPROBANDO ACTUALIZACIÓN...", "CHECKING FOR UPDATES..."},
            {"CHAT DE LA TIMBA", "GAME CHAT"},
            {"¿SEGURO QUE QUIERES ELIMINAR TODAS LAS TIMBAS DONDE PARTICIPÓ -> [", "ARE YOU SURE YOU WANT TO DELETE ALL THE GAMES IN WHICH HE/SHE TOOK PART -> ["},
            {"PURGAR", "PURGE"},
            {"SE PIRA", "LEAVES"},
            {"FALLO DE CONEXIÓN", "CONNECTION ERROR"},
            {"NO SE HA PODIDO COMPROBAR SI HAY NUEVA VERSIÓN. ¿TIENES CONEXIÓN A INTERNET?", "NOT BEEN ABLE TO CHECK FOR NEW RELEASE. DO YOU HAVE INTERNET CONNECTION?"},
            {"Deshacer", "Undo"},
            {"Copiar", "Copy"},
            {"Cortar", "Cut"},
            {"Pegar", "Paste"},
            {"Seleccionar todo", "Select all"},
            {"Recibiendo info del servidor...", "Reading server info..."},
            {"TIENES QUE ESPERAR ", "YOU MUST WAIT "},
            {" PARA VOLVER A SOLICITAR IWTSTH", " TO REQUEST IWTSTH AGAIN"},
            {"EL SERVIDOR HA DENEGADO LA SOLICITUD IWTSTH DE ", "THE SERVER HAS DENIED THE IWTSTH REQUEST FROM "},
            {"EL SERVIDOR HA DENEGADO TU SOLICITUD IWTSTH", "THE SERVER HAS DENIED YOUR IWTSTH REQUEST"},
            {") ¿AUTORIZAMOS?", ") WE AUTHORIZE?"},
            {" SOLICITA IWTSTH (", " REQUESTS IWTSTH ("},
            {"Reglas de Robert", "Robert's Rules"},
            {"HAY JUGADORES QUE NO HAN CONFIRMADO LA NUEVA MANO", "THERE PLAYERS WHO HAVE NOT CONFIRMED THE NEW HAND"},
            {"CLICK PARA VER SU BUYIN", "CLICK TO SHOW BUYIN"},
            {"Notificaciones del chat durante el juego", "In-game chat notifications"},
            {"REGISTRO DE LA TIMBA", "GAME LOG"},
            {"ESPACIO", "SPACE"},
            {"MAYÚS", "SHIFT"},
            {"ESCAPE", "SCAPE"},
            {"PASAR / IR / MOSTRAR", "CHECK / CALL / SHOW"},
            {"ARRIBA|ABAJO", "UP|DOWN"},
            {"APOSTAR", "BET"},
            {"NO IR", "FOLD"},
            {"CHAT RÁPIDO", "FAST CHAT"},
            {"Última mano", "Last hand"},
            {"Click para mostrar/ocultar la ficha", "Click for show/hide chip"},
            {"Auto ajustar zoom", "Auto fit zoom"},
            {"Auto ajustar", "Auto fit"},
            {"NO TENGO PERMISOS DE ESCRITURA.\n(TENDRÁS QUE DESCARGARTE LA ÚLTIMA VERSIÓN MANUALMENTE)", "I DO NOT HAVE WRITING PERMISSION.\n(YOU WILL HAVE TO DOWNLOAD THE LATEST VERSION MANUALLY)"},
            {"HAY UNA VERSIÓN NUEVA DE CORONAPOKER. ¿QUIERES ACTUALIZAR?", "THERE IS A NEW CORONAPOKER VERSION. UPDATE?"},
            {"(CONTINUANDO TIMBA ANTERIOR)", "(RESUMING PREVIOUS GAME)"},
            {"ERROR AL RECUPERAR LA MANO", "ERROR RECOVERING HAND"},
            {"ESTE JUGADOR TIENE PROBLEMAS DE CONEXIÓN", "THIS PLAYER HAS CONNECTION PROBLEMS"},
            {"Este usuario tiene problemas de conexión que bloquean la partida.\n(El servidor decidirá si esperar a que se recupere o echarle).", "This user has connection problems that block the game.\n(The server will decide whether to wait for him to recover or kick him/her out)."},
            {"CLICK IZQ: ÚLTIMA MANO / CLICK DCHO: LÍMITE DE MANOS", "LEFT CLICK: LAST HAND / RIGHT CLICK: HANDS LIMIT"},
            {"Aumentar ciegas", "Increase blinds"},
            {"Aumentar ciegas:", "Increase blinds:"},
            {"Minutos:", "Minutes:"},
            {"Manos:", "Hands:"},
            {"Límite de manos:", "Hands limit:"},
            {"Compra inicial (10 a 100 ciegas grandes):", "Buy-in (10 to 100 big blinds):"},
            {"Ciegas iniciales:", "Initial blinds:"},
            {"API KEY (opcional):", "API KEY (optional):"},
            {"RANDOM.ORG API KEY NO VÁLIDA (se usará el CSPRNG)", "NOT VALID RANDOM.ORG API KEY (it will be used CSPRNG instead)"},
            {"SE HA REACTIVADO RANDOM.ORG", "RANDOM.ORG RE-ENABLED"},
            {"(CONTINUANDO TIMBA)", "(RESUMING GAME)"},
            {"NO SE HA PODIDO RECUPERAR LA MANO #", "IT HAS NOT BEEN POSSIBLE TO RECOVER THE HAND#"},
            {"ERROR FATAL: NO SE HA PODIDO RECUPERAR LA TIMBA", "FATAL ERROR: GAME COULD NOT BE RECOVERED"},
            {"TTS ACTIVADO POR EL SERVIDOR", "TTS ENABLED BY SERVER"},
            {"TTS DESACTIVADO POR EL SERVIDOR", "TTS DISABLED BY SERVER"},
            {"¿DESACTIVAR EL CHAT DE VOZ PARA TODOS?", "DISABLE VOICE CHAT FOR EVERYONE?"},
            {"EL SERVIDOR HA REACTIVADO EL CHAT DE VOZ", "THE SERVER HAS RE-ENABLED THE VOICE CHAT"},
            {"EL SERVIDOR HA DESACTIVADO EL CHAT DE VOZ", "THE SERVER HAS DISABLED THE VOICE CHAT"},
            {"¿SEGURO QUE QUIERES SALIR AHORA?", "SURE YOU WANT TO EXIT NOW?"},
            {"¿SEGURO QUE QUIERES EMPEZAR YA?", "SURE YOU WANT TO START NOW?"},
            {"Click para actualizar datos de la timba", "Click for updating game info"},
            {"GUARDAR", "SAVE"},
            {"Actualizar timba", "Uodate game"},
            {"Tienes que seleccionar algún participante antes", "You must select a participant before"},
            {"Listar sólo timbas donde participó este jugador", "Only list games joined by this player"},
            {"¿FORZAR CIERRE?", "FORCE CLOSE?"},
            {"¿SEGURO?", "ARE YOU SURE?"},
            {"Click para gestionar contraseña", "Click to manage password"},
            {"Click para obtener datos de conexión", "Click to get connection data"},
            {"RECONECTAR", "RECONNECT"},
            {"Reconectando...", "Reconnecting..."},
            {"Probando UPnP...", "Testing UPnP..."},
            {"NO HA SIDO POSIBLE MAPEAR AUTOMÁTICAMENTE EL PUERTO USANDO UPnP\n\n(Si quieres compartir la timba por Internet deberás activar UPnP en tu router o mapear el puerto de forma manual)", "IT HAS NOT BEEN POSSIBLE TO AUTOMATICALLY MAP THE PORT USING UPnP (If you want to share the game over the Internet you will have to activate UPnP in your router or map the port manually)"},
            {"DATOS DE CONEXIÓN COPIADOS EN EL PORTAPAPELES", "CONNECTION INFO COPIED TO CLIPBOARD"},
            {"HA FALLADO LA AUTO-RECONEXIÓN. ¿QUIERES INTENTAR UNA RECONEXIÓN MANUAL?", "AUTO-RECONNECTION FAILED. DO YOU WANT TO TRY A MANUAL RECONNECTION?"},
            {"HAS PERDIDO LA CONEXIÓN CON EL SERVIDOR. ¡ADIÓS!", "YOU HAVE LOST THE CONNECTION TO THE SERVER. BYE!"},
            {"Aviso: la privacidad del CHAT no está garantizada si algún jugador usa la función de voz TTS (click para más info).", "Notice: CHAT privacy is not guaranteed if any player uses the TTS voice function (click for more info)."},
            {"Aunque CoronaPoker usa cifrado extremo a extremo en todas las comunicaciones, el chat de\nvoz utiliza APIs externas TTS para convertir el texto en audio, por lo que los mensajes\nenviados a esos servidores podrían ser (en teoría) leidos por terceros.\n\nPOR FAVOR, TENLO EN CUENTA A LA HORA DE USAR EL CHAT", "Although CoronaPoker uses end-to-end encryption on all communications, the voice chat\nuses external TTS APIs to convert text to audio, so messages sent to those servers could\n(in theory) be read by third parties.\n\n PLEASE KEEP THIS IN MIND WHEN USING THE CHAT"},
            {"RECOMPRAR", "REBUY"},
            {"Aceptar", "OK"},
            {"Cancelar", "Cancel"},
            {"RECOMPRAR (siguiente mano)", "REBUY (next hand)"},
            {"¿ELIMINAR ESTA TIMBA?\n(Nota: las timbas eliminadas no se pueden continuar)", "REMOVE THIS GAME?\n(Note: removed games can not be resumed)"},
            {"ELIMINAR TIMBA", "REMOVE GAME"},
            {"¿IGNORAR LOS MENSAJES TTS DE ESTE USUARIO?", "IGNORE TTS MESSAGES FROM THIS USER?"},
            {"La canción que suena en la sala de espera es \"The Dream\" compuesta por Jerry Goldsmith para la película Total Recall.", "The song that sounds in the waiting room is \"The Dream\" composed by Jerry Goldsmith for the movie Total Recall."},
            {"ERROR: NO SE HA PODIDO RECUPERAR LA CLAVE DE PERMUTACIÓN DE ESTA MANO", "ERROR: UNABLE TO RECOVER PERMUTATION KEY OF THIS HAND"},
            {"¡¡TEN CUIDADO!! EL JUGADOR NO HIZO ESO LA OTRA VEZ. (ALGUIEN ESTÁ HACIENDO TRAMPAS).", "BE CAREFUL! THE PLAYER DIDN'T DO THAT LAST TIME. (HE/SHE'S OR THE SERVER IS CHEATING)."},
            {"NO SE PUEDE RECUPERAR LA MANO EN CURSO PORQUE FALTAN JUGADORES DE LA MANO ANTERIOR", "THE CURRENT HAND CANNOT BE RECOVERED BECAUSE PLAYERS FROM THE PREVIOUS HAND ARE MISSING"},
            {"Manos:", "Hands:"},
            {"CONTINUAR TIMBA ANTERIOR:", "RESUME PREVIOUS GAME:"},
            {"Nota: lo que se muestra es el porcentaje de manos subidas en relación a las manos jugadas.", "Note: what is shown is the percentage of hands raised in relation to the hands played."},
            {"Intercambio de claves...", "Keys exchange..."},
            {"Chequeo de integridad...", "Integrity check..."},
            {"POSIBLE TRAMPOS@", "POTENTIAL CHEATER"},
            {"¡¡TEN CUIDADO!! ES MUY PROBABLE QUE EL SERVIDOR ESTÉ INTENTANDO HACER TRAMPAS.", "BE CAREFUL! IT IS VERY LIKELY THAT THE SERVER IS TRYING TO CHEAT."},
            {"POSIBLE SERVIDOR TRAMPOSO", "POTENTIAL CHEATER SERVER"},
            {"CUIDADO: el ejecutable del juego de este usuario es diferente\n(Es posible que intente hacer trampas con una versión hackeada del juego)", "CAUTION: the game executable of this user is different\n(It is possible that she/he is trying to cheat with a hacked version of the game)"},
            {"CUIDADO: el ejecutable del juego del servidor es diferente\n(Es posible que intente hacer trampas con una versión hackeada del juego)", "CAUTION: the server game executable is different (it is possible that the server tries to cheat with a hacked version of the game)"},
            {"PAGAR", "PAY"},
            {"Nota: EFECTIVIDAD = (ROI / MANOS_JUGADAS) si ROI >=0, si no, EFECTIVIDAD = (ROI x MANOS_JUGADAS) (la EFECTIVIDAD mínima es -1)", "Note: EFFECTIVENESS = (ROI / PLAYED_HANDS) if ROI >=0, otherwise EFFECTIVENESS = (ROI x PLAYED_HANDS) (minimum EFFECTIVENESS is -1)"},
            {"RENDIMIENTO DE LOS JUGADORES", "PLAYERS PERFORMANCE"},
            {"EFECTIVIDAD", "EFFECTIVENESS"},
            {"Nota: lo que se muestra es el balance general después de terminar la mano actual.", "Note: what is shown is the overall balance after finishing the current hand."},
            {"% APUESTAS/SUBIDAS EN EL PREFLOP", "% PREFLOP BETS/RAISES"},
            {"% APUESTAS/SUBIDAS EN EL FLOP", "% FLOP BETS/RAISES"},
            {"% APUESTAS/SUBIDAS EN EL TURN", "% TURN BETS/RAISES"},
            {"% APUESTAS/SUBIDAS EN EL RIVER", "% RIVER BETS/RAISES"},
            {"MANOS_APUESTA_SUBE", "BET_RAISE_HANDS"},
            {"La canción que suena en el visor de estadísticas es el tema principal de la mítica película EL GOLPE.", "The song that sounds in the statistics viewer is the main theme of the epic film THE STING."},
            {"Nota: se muestran las 1000 mejores jugadas ganadoras", "Warning: TOP-1000 winner hands are shown"},
            {"Duración:", "Duration:"},
            {"Incrementar ciegas:", "Blinds increment:"},
            {"(SEGUNDOS)", "(SECONDS)"},
            {"EFICIENCIA", "PERFORMANCE"},
            {"MANOS", "HANDS"},
            {"BENEFICIO", "PROFIT"},
            {"JUGADOR", "PLAYER"},
            {"CARTAS RECIBIDAS", "HOLE CARDS"},
            {"CARTAS JUGADA", "HAND CARDS"},
            {"JUGADA", "HAND VALUE"},
            {"MANOS JUGADAS", "PLAYED HANDS"},
            {"MANOS GANADAS", "WINNER HANDS"},
            {"TIEMPO", "TIME"},
            {"TIMBA", "GAME"},
            {"PAGADO", "PAY"},
            {"SÍ", "YES"},
            {"Estadísticas", "Stats"},
            {"ESTADÍSTICAS", "STATS"},
            {"Lo que no son cuentas, son cuentos", "Stats"},
            {"TODAS LAS TIMBAS", "ALL GAMES"},
            {"TODAS LAS MANOS", "ALL HANDS"},
            {"GANANCIAS/PÉRDIDAS", "PROFIT/LOSS"},
            {"PRECISIÓN", "PRECISION"},
            {"TIEMPO MEDIO DE RESPUESTA", "AVERAGE RESPONSE TIME"},
            {"% MANOS JUGADAS/GANADAS", "% PLAYED/WINNER HANDS"},
            {"JUGADAS GANADORAS", "WINNER HANDS"},
            {"Inicio:", "Start:"},
            {"Fin:", "End:"},
            {"Manos:", "Hands:"},
            {"Tiempo de juego:", "Play time:"},
            {"Jugadores:", "Players:"},
            {"Compra:", "Buyin:"},
            {"Ciegas:", "Blinds:"},
            {"Doblar ciegas (min):", "Blinds double (min):"},
            {"Recomprar:", "Rebuy:"},
            {"Ciega pequeña:", "Small blind:"},
            {"Ciega grande:", "Big blind"},
            {"Cartas comunitarias:", "Community cards:"},
            {"Jugadores PREFLOP:", "PREFLOP players:"},
            {"Jugadores FLOP:", "FLOP players:"},
            {"Jugadores TURN:", "TURN players:"},
            {"Jugadores RIVER:", "RIVER players:"},
            {"BOTE:", "POT:"},
            {"Ver atajos", "Show shortcuts"},
            {"PASAR/IR -> [ESPACIO]\n\nAPOSTAR -> [ENTER] (FLECHA ARRIBA/ABAJO PARA SUBIR/BAJAR APUESTA)\n\nALL IN -> [MAYUS + ENTER]\n\nNO IR -> [ESC]\n\nMOSTRAR CARTAS -> [ESPACIO]\n\nMENSAJE CHAT RÁPIDO -> [º]", "CHECK/CALL -> [SPACE]\n\nBET -> [ENTER] (UP/DOWN ARROW KEYS TO RAISE/LOWER BET)\n\nALL IN -> [SHIFT + ENTER]\n\nFOLD -> [ESC]\n\nSHOW CARDS -> [SPACE]\n\nFAST CHAT MESSAGE -> [º]"},
            {"NO SE PUEDE RECOMPRAR EN ESTA TIMBA", "REBUY IS NOT ALLOWED IN THIS GAME"},
            {"CLICK PARA RECOMPRAR", "CLICK TO REBUY"},
            {"¿RECOMPRAR EN LA PRÓXIMA MANO?", "REBUY IN NEXT HAND?"},
            {"YA TIENES UNA SOLICITUD DE RECOMPRA ACTIVA", "YOU ALREADY HAVE A REBUY REQUEST PENDING"},
            {"PARA RECOMPRAR DEBES TENER MENOS DE ", "TO REBUY YOU MUST HAVE LESS THAN "},
            {"NO PUEDES RECOMPRAR EN ESTE MOMENTO", "YOU CAN NOT REBUY AT THIS TIME"},
            {"¿GENERAR CONTRASEÑA NUEVA?", "GENERATE NEW PASSWORD?"},
            {"PASSWORD COPIADA EN EL PORTAPAPELES", "PASSWORD COPIED TO CLIPBOARD"},
            {"NUEVA PASSWORD COPIADA EN EL PORTAPAPELES", "NEW PASSWORD COPIED TO CLIPBOARD"},
            {"PAUSA PROGRAMADA PARA TU PRÓXIMO TURNO", "PAUSE SCHEDULED FOR YOUR NEXT TURN"},
            {"PASSWORD INCORRECTA", "BAD PASSWORD"},
            {"LA TIMBA HA TERMINADO", "THE GAME IS OVER"},
            {"ESTA ES LA ÚLTIMA MANO", "THIS IS THE LAST HAND"},
            {"¿PAUSAR AHORA MISMO?", "PAUSE NOW?"},
            {"¿ÚLTIMA MANO?", "LAST HAND?"},
            {"ÚLTIMA MANO", "LAST HAND"},
            {"Videollamada", "Videocall"},
            {"CERRAR", "CLOSE"},
            {"Monitorizando portapapeles...", "Monitoring clipboard..."},
            {"COMPARTIR", "SHARE"},
            {"¿RECOMPRA? -> ", "REBUY? -> "},
            {"AÑADIR BOT", "ADD BOT"},
            {"Los comentarios sonoros durante el juego no se traducirán (puedes desactivarlos cuando empiece la partida).", "In-game audio comments will not be translated (you can turn them off when the game starts)."},
            {"CREAR TIMBA", "CREATE GAME"},
            {"UNIRME A TIMBA", "JOIN GAME"},
            {"Krusty sabe lo que se hace", "Krusty knows what he's doing"},
            {"Puede contener lenguaje soez", "May contain foul language"},
            {"Contiene apuestas con dinero ficticio", "It contains bets with fictitious money"},
            {"Permite jugar online", "Allows you to play online"},
            {"Click para activar/desactivar el sonido. (SHIFT + ARRIBA/ABAJO PARA CAMBIAR VOLUMEN)", "Click to turn the sound on/off. (SHIFT + UP/DOWN FOR VOLUME CONTROL)"},
            {"¿De dónde ha salido esto?", "Where did this come from?"},
            {"El videojuego de Texas hold 'em NL que nos merecemos, no el que necesitamos ¿o era al revés?", "The Texas hold 'em NL videogame we deserve, not the one we need, or was it the opposite?"},
            {"Gracias a todos los amigos que han colaborado en esta aventura, en especial a Pepsi por sus barajas y el \"hilo fino\",", "Thanks to all the friends who have collaborated in this adventure, especially to Pepsi for their decks and the \"fine-grain\","},
            {"a Pepillo por ese talento para cazar los bugs más raros, a Lato por las pruebas en su Mac y a mi madre... por todo lo demás.", "to Pepillo for that talent for hunting the weirdest bugs, to Lato for testing in his Mac and to my mother... for everything else."},
            {"(Todos los céntimos desaparecidos en las betas fueron para una buena causa).", "(All the missing cents in the betas were for a good cause)."},
            {"En memoria de todas las víctimas de la COVID-19", "In memory of all the victims of COVID-19"},
            {"Nota: si posees el copyright de esta música (o cualquier otro elemento) y no permites su utilización, escríbeme a -> tonikelope@gmail.com", "Note: if you own the copyright of this music (or any other item) and do not allow its use, please write to me at -> tonikelope@gmail.com"},
            {"Generador de jugadas", "Hand generator"},
            {"Jugada superior", "Top hand"},
            {"Jugada inferior", "Lower hand"},
            {"Nota: no olvides mapear el puerto en tu router si quieres compartir la timba por Internet", "Note: don't forget to map the port on your router if you want to share the game over the Internet"},
            {"Generador de números aleatorios:", "Random number generator:"},
            {"Ciegas:", "Blinds:"},
            {"Compra inicial:", "BUYIN:"},
            {"Permitir recomprar", "Allow rebuy"},
            {"Doblar ciegas (minutos):", "Double blinds (minutes):"},
            {"RECUPERAR ÚLTIMA TIMBA", "RECOVER LAST GAME"},
            {"¡VAMOS!", "GO!"},
            {"Seguro", "Safe"},
            {"TRNG [RANDOM.ORG] (Muy seguro)", "TRNG [RANDOM.ORG] (Very secure)"},
            {"CSPRNG [DRBG SHA-512] (Seguro)", "CSPRNG [DRBG SHA-512] (Secure)"},
            {"Muy seguro", "Very safe"},
            {"LA TIMBA HA TERMINADO (NO QUEDAN JUGADORES)", "GAME IS OVER (NO PLAYERS LEFT)"},
            {"En el MODO RECUPERACIÓN se continuará la timba anterior desde donde se paró:\n\n1) Es OBLIGATORIO que los jugadores antiguos usen los MISMOS NICKS.\n\n2) Para poder continuar desde el PUNTO EXACTO (con la mismas cartas) es OBLIGATORIO que se conecten TODOS los jugadores antiguos.\nSi esto no es posible, se \"perderá\" la mano que estaba en curso cuando se interrumpió la timba.\n\n3) Está permitido que se unan a la timba jugadores nuevos (estarán la primera mano de espectadores).", "In the RECOVERY MODE the previous game will be continued from where it was stopped:\n\n1) It is MANDATORY that the old players use the SAME NICKS.\n\n2) In order to continue from the EXACT POINT (with the same cards) it is MANDATORY that ALL the old players connect.\nIf this is not possible, the hand that was in progress when the game was interrupted will be lost.\n\n3) New players are allowed to join the game (they will be the first hand as spectators)."},
            {"¡A LA PUTA CALLE!", "GET OUT OF HERE!"},
            {"EL SERVIDOR HA TERMINADO LA TIMBA", "THE SERVER HAS FINISHED THE GAME"},
            {"A ver, se acabó el tiempo para llorar. ¿TE REENGANCHAS O QUÉ?", "Time for crying is over. YOU REBUY OR WHAT?"},
            {"¡CUIDADO! ERES EL ANFITRIÓN Y SI SALES SE TERMINARÁ LA TIMBA. ¿ESTÁS SEGURO?", "BEWARE! YOU'RE THE HOST AND IF YOU COME OUT THE GAME WILL BE OVER. ARE YOU SURE?"},
            {"¡CUIDADO! Si sales de la timba no podrás volver a entrar. ¿ESTÁS SEGURO?", "CAUTION! If you get out of the game you won't be able to get back in. ARE YOU SURE?"},
            {"Este usuario tiene problemas de conexión que bloquean la partida. ¿Quieres expulsarlo?", "This user has connection problems that block the game. Do you want to kick him/her out?"},
            {"Hay jugadores de la timba anterior que no se han vuelto a conectar.\n(Si no se conectan no se podrá recuperar la última mano en curso).\n\n¿EMPEZAMOS YA?", "There are players from the previous game who have not reconnected.\n(If they do not connect, the last hand in progress cannot be retrieved.)\n\nSHALL WE START NOW?"},
            {"Te falta algún campo obligatorio por completar", "You are missing a required field"},
            {"Versión de CoronaPoker incorrecta", "Wrong CoronaPoker version"},
            {"Llegas TARDE. La partida ya ha empezado.", "You're LATE. The game has already started."},
            {"NO HAY SITIO", "THERE IS NO SEATS"},
            {"El nick elegido ya lo está usando otro usuario.", "The chosen nick is already being used by another user."},
            {"El servidor ha cancelado la timba antes de empezar.", "The server has canceled the game before it starts."},
            {"ERROR INESPERADO", "UNEXPECTED ERROR"},
            {"ALGO HA FALLADO. (Probablemente la timba no esté aún creada).", "SOMETHING HAS GONE WRONG. (Probably the game is not yet created)."},
            {"ALGO HA FALLADO. Has perdido la conexión con el servidor.", "SOMETHING HAS GONE WRONG. You've lost the connection to the server."},
            {"ALGO HA FALLADO. (Probablemente ya hay una timba creada en el mismo puerto).", "SOMETHING HAS GONE WRONG. (There's probably already a game created in the same port)."},
            {"Hay usuarios que están tardando demasiado en responder (se les eliminará de la timba). ¿ESPERAMOS UN POCO MÁS?", "There are users who are taking too long to respond (they will be removed from the game). DO WE WAIT A LITTLE MORE?"},
            {" parece que perdió la conexión y no ha vuelto a conectar (se le eliminará de la timba). ¿ESPERAMOS UN POCO MÁS?", " it looks like he/she lost the connection and hasn't reconnected (he/she'll be removed from the game). DO WE WAIT A LITTLE MORE?"},
            {"Parece que hubo algún problema con RANDOM.ORG (se usará el CSPRNG en su lugar)\n¿Quieres desactivar RANDOM.ORG para el resto de la partida?", "There seems to be some problem with RANDOM.ORG (the CSPRNG will be used instead).\nDo you want to disable RANDOM.ORG for the rest of the game?"},
            {"PASAR", "CHECK"},
            {"IR", "CALL"},
            {"APOSTAR", "BET"},
            {"SUBIR", "RAISE"},
            {"RESUBIR", "RERAISE"},
            {"NO IR", "FOLD"},
            {"IR", "CALL"},
            {"HABLAS TÚ", "YOUR TURN"},
            {"Archivo", "File"},
            {"Ver chat (ALT+C)", "Show chat (ALT+C)"},
            {"Ver chat", "Show chat"},
            {"Ver registro (ALT+R)", "Show log (ALT+R)"},
            {"Ver registro", "Show log"},
            {"Generador de jugadas (ALT+J)", "Hand generator (ALT+J)"},
            {"Pausar timba (ALT+P)", "Pause game (ALT+P)"},
            {"Pausar timba", "Pause game"},
            {"PANTALLA COMPLETA (ALT+F)", "FULL SCREEN (ALT+F)"},
            {"PANTALLA COMPLETA", "FULL SCREEN"},
            {"SALIR (ALT+F4)", "EXIT (ALT+F4)"},
            {"Límite de manos", "Hands limit"},
            {"Salir", "Exit"},
            {"SALIR", "EXIT"},
            {"Aumentar (CTRL++)", "Increase (CTRL++)"},
            {"Reducir (CTRL+-)", "Reduce (CTRL--)"},
            {"Aumentar zoom (CTRL++)", "Increase zoom (CTRL++)"},
            {"Reducir zoom (CTRL+-)", "Reduce zoom (CTRL+-)"},
            {"Reset zoom (CTRL+0)", "Reset zoom (CTRL+0)"},
            {"VISTA COMPACTA (ALT+X)", "COMPACT VIEW (ALT+X)"},
            {"VISTA COMPACTA", "COMPACT VIEW"},
            {"Preferencias", "Preferences"},
            {"SONIDOS (ALT+S)", "SOUNDS (ALT+S)"},
            {"SONIDOS", "SOUNDS"},
            {"Sonidos de coña", "Joke sounds"},
            {"Música ambiental", "Background music"},
            {"Confirmar todas las acciones", "Confirm every action"},
            {"Botones AUTO", "AUTO buttons"},
            {"Animación al repartir", "Dealer animation"},
            {"Mostrar reloj (ALT+W)", "Show clock (ALT+W)"},
            {"Mostrar reloj", "Show clock"},
            {"Barajas", "Decks"},
            {"Tapetes", "Mats"},
            {"Recompra automática", "Auto rebuy"},
            {"Ayuda", "Help"},
            {"Acerca de", "About"},
            {"BOTE: ", "POT: "},
            {"Mano: ", "Hand: "},
            {"Apuestas: ", "Bets: "},
            {"Ciegas: ", "Blinds: "},
            {"MOSTRAR", "SHOW"},
            {" MUESTRAS (", " SHOW ("},
            {" MUESTRA (", " SHOWS ("},
            {"PENSANDO", "THINKING"},
            {"AUDITOR DE CUENTAS", "ACCOUNTS AUDITOR"},
            {"¡OJO A ESTO: NO SALEN LAS CUENTAS GLOBALES! -> (STACKS + INDIVISIBLE) != BUYIN", "WATCH OUT FOR THIS: THE GLOBAL ACCOUNTS DON'T COME OUT! -> (STACKS + INDIVISIBLE) = BUYIN"},
            {"CALENTANDO", "WARMING UP"},
            {"ABANDONA LA TIMBA", "QUIT THE GAME"},
            {"ABANDONAS LA TIMBA", "QUIT THE GAME"},
            {" MUESTRA (", " SHOWS ("},
            {" se UNE a la TIMBA.", " JOINS THE GAME."},
            {"SE DOBLAN LAS CIEGAS", "THE BLINDS ARE DOUBLED"},
            {"MANO", "HAND"},
            {"BOTE SOBRANTE NO DIVISIBLE", "INDIVISIBLE POT"},
            {"RECUPERANDO TIMBA...", "RECOVERING GAME..."},
            {"MANO RECUPERADA", "RECOVERED HAND"},
            {"TIMBA RECUPERADA", "GAME RECOVERED"},
            {" es la CIEGA GRANDE (", " is the BIG BLIND ("},
            {" es la CIEGA PEQUEÑA (", " is the SMALL BLIND ("},
            {" es el DEALER", " is the DEALER"},
            {" GANA BOTE (", " WINS POT ("},
            {") SIN TENER QUE MOSTRAR", ") WITHOUT NEEDING TO SHOW"},
            {"GANAS", "YOU WIN"},
            {"GANA", "WINS"},
            {"GANA SIN TENER QUE MOSTRAR", "WINS WITHOUT NEEDING TO SHOW"},
            {") GANA BOTE (", ") WINS POT ("},
            {" (---) PIERDE BOTE (", " (---) LOSES POT ("},
            {") GANA BOTE PRINCIPAL (", ") WINS MAIN POT ("},
            {" (---) PIERDE BOTE PRINCIPAL (", " (---) LOSES MAIN POT ("},
            {" RECUPERA BOTE (SOBRANTE) SECUNDARIO #", " RECOVERS SECONDARY POT #"},
            {") GANA BOTE SECUNDARIO #", ") WINS SECONDARY POT #"},
            {" (---) PIERDE BOTE SECUNDARIO #", " (---) LOSES SECONDARY POT #"},
            {" -> TE QUEDAS DE ESPECTADOR", " -> YOU STAY AS A SPECTATOR"},
            {" RECOMPRA (", " REBUY ("},
            {"\n*************** LA TIMBA HA TERMINADO ***************", "\n*************** GAME IS OVER ***************"},
            {"FIN DE LA TIMBA -> ", "END OF GAME -> "},
            {"COMIENZA LA TIMBA -> ", "GAME STARTS -> "},
            {" - REGISTRO DE LA TIMBA]", " - GAME LOG]"},
            {"REGISTRO DE LA TIMBA", "GAME LOG"},
            {"¡A JUGAR!", "START NOW"},
            {"Expulsar jugador", "Kick user"},
            {"Conectando...", "Connecting..."},
            {"Esperando jugadores...", "Waiting for players..."},
            {" - Sala de espera (", " - Waiting room ("},
            {"Sorteando sitios...", "Drawing places..."},
            {"Timba en curso", "Game in progress"},
            {" - Timba en curso (", " - Game in progress ("},
            {"Conectado", "Connected"},
            {"CONECTADO", "CONNECTED"},
            {"TIMBA PAUSADA", "GAME PAUSED"},
            {"CONTINUAR", "RESUME"},
            {"PAUSAR", "PAUSE"},
            {"RECUPERANDO TIMBA", "RECOVERING GAME"},
            {"POR FAVOR, ESPERA", "PLEASE WAIT"},
            {"REANUDANDO TIMBA...", "RESUMING GAME..."},
            {"CONTINUAR", "CONTINUE"},
            {"Verde", "Green"},
            {"Azul", "Blue"},
            {"Rojo", "Red"},
            {"Sin tapete", "No mat"},
            {"(A)PASAR +CG", "(A)CHECK +BB"},
            {"(A)NO IR", "(A)FOLD"},
            {"Crear timba", "Create game"},
            {"Unirme a timba", "Join game"},
            {"Haz click para cambiar el avatar", "Click to change your avatar"},
            {"Se usará para barajar las cartas", "It will be used to shuffle the cards"},
            {"[10-100] ciegas grandes", "[10-100] big blinds"},
            {"Si algún jugador se queda sin fichas", "If any player runs out of chips"},
            {"El MODO RECUPERACIÓN permite arrancar una timba que se interrumpió previamente", "The RECOVERY MODE allows you to resume a game that was previously interrupted"},
            {"Cinemáticas", "Cinematics"},
            {"PIERDE ", "LOSES "},
            {"GANA ", "WINS "},
            {"PIERDE", "LOSES"},
            {"NI GANA NI PIERDE", "NO WIN, NO LOSS"},
            {"ESPECTADOR", "SPECTATOR"},
            {"] dice: ", "] says: "},
            {"Inicializando timba...", "Starting game..."},
            {"Cartas UTF-8", "UTF-8 cards"},
            {"El hilo musical que suena durante el juego fue compuesto por David Luong.", "The musical theme that sounds during the game was composed by David Luong."},
            {"La canción que suena aquí es \"La Sala del Trono\" compuesta por John Williams para Star Wars.", "The song playing here is \"The Throne Room\" composed by John Williams for Star Wars."}};

        for (var r : rosetta) {
            _rosetta.putIfAbsent(r[0], r[1]);
            _attesor.putIfAbsent(r[1], r[0]);
        }
    }

    public static String translate(String orig) {

        return _translate(orig, false);
    }

    public static String translate(String orig, boolean force) {

        return _translate(orig, force);
    }

    private static String _translate(String orig, boolean force) {

        if (!LANG.equals(GameFrame.LANGUAGE)) {

            LANG = GameFrame.LANGUAGE;

            _rosetta.clear();

            _attesor.clear();

            switch (LANG) {

                case "en":
                    English();
                    break;
                default:
                    English();
            }
        }

        if (LANG.equals(GameFrame.DEFAULT_LANGUAGE)) {

            if (orig != null && force) {

                String key = orig;

                String val = _attesor.get(key);

                return val != null ? val : orig;
            }

        } else {

            if (orig != null) {

                String key = orig;

                String val = _rosetta.get(key);

                return val != null ? val : orig;
            }

        }

        return orig;
    }

}

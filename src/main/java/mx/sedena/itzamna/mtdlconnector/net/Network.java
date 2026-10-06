/*
 * UNCLASSIFIED
 *
 * Revision History:
 *
 *   Date     SEC.No.  Programmer           Description
 * ---------- ------- -------------------- -------------------------------------
 * 2026-07-31 IMDL001  Zopiloman            Initial release
 *
 */

package mx.sedena.itzamna.mtdlconnector.net;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;


import org.apache.log4j.Logger;

import mx.sedena.itzamna.mtdlconnector.net.DelayableMessage;
import mx.sedena.itzamna.mtdlconnector.net.MessageQueue;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.Login;
import mx.sedena.itzamna.mtdlconnector.core.LinkParticipantType;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;
import mx.sedena.itzamna.mtdlconnector.core.UserPrefs;

// TODO: Auto-generated Javadoc
/**
 * This class contains the state information for the network send/receive
 * functionality and allows the application to set up and tear down the network
 * connection.
 */
public class Network {

    /** Logger for this class. */
    private static Logger log = Logger.getLogger(Network.class);

    /** The connected. */
    private static volatile boolean connected = false;

    /** Properties. */
    private static Properties props = UserPrefs.getProps();

    /** Bytes sent. */
    private static int sent = 0;

    /** Bytes received. */
    private static int received = 0;

    /** Extrapolate tracks. */
    private static boolean extrapolateTracks = false;

    /** Save traffic. */
    private static boolean saveTraffic = false;

    /** Send login. */
    private static boolean sendLogin = true;

    /** Send login response. */
    private static boolean sendLoginResponse = true;

    /** Send heartbeats. */
    private static boolean sendHeartbeats = true;

    /** Require heartbeats. */
    private static boolean requireHeartbeats = true;

    /** Send link status request. */
    private static boolean sendLinkStatusRequest = true;

    /** Send link status response. */
    private static boolean sendLinkStatusResponse = true;

    /** Send acks. */
    private static boolean sendAcks = true;

    /** Request acks. */
    private static boolean requestAcks = false;

    /** append Connected SSR to Air Track Data messages. */
    private static boolean appendSSR = false;

    /** Are we logged in. */
    private static boolean loggedIn = false;

    /** Include heartbeats in Message Traffic window. */
    private static boolean includeHeartbeats = false;

    /** Used for serializing network traffic for later analysis. */
    private static volatile Hashtable<Long, byte[]> timeStampedFrames = new Hashtable<Long, byte[]>();

    /** The Constant TIMER. */
    static final Timer TIMER = new Timer();

    /** The destination link participant. */
    private static LinkParticipantType destinationLinkParticipant = null;


    static class HeartbeatTimeout {

        /** Heartbeat task. */
        private static TimerTask task = null;

        /**
         * Starts the heartbeat timer.
         */
        public static void schedule() {
            cancel();

            task = new TimerTask() {
                @Override
                public void run() {

                    if (isRequireHeartbeats()){
                        log.warn("Heartbeat timeout detectado. Se perdió la comunicación con el corresponsal.");

                        System.out.println("[ALERTA - RED] Se perdió el Heartbeat del simulador. Reiniciando estado de sesión...");
                        setLoggedIn(false);
                        setDestinationLinkParticipant(null);
                    }
                }
            };

            int millis = Integer.valueOf(props.getProperty(UserPrefs.HEARTBEAT_TIMEOUT, "10000")); // Recomendado: 10 a 15 segundos
            TIMER.schedule(task, millis);
        }

        /**
         * Cancels the heartbeat timer.
         */
        public static synchronized void cancel() {
            if (task != null) {
                log.debug("Heartbeat timeout canceled.");
                task.cancel();
                task = null;
            }
        }
    }

    /**
     * A timer that logs communications state information.
     */
    static class StatusTimer {

        /** Heartbeat task. */
        private static TimerTask task = null;

        /**
         * Starts the status timer.
         */
        public static void schedule() {
            cancel();
            task = new TimerTask() {
                @Override
                public void run() {
                    log.debug(getState());
                }
            };
            TIMER.schedule(task, 0, 10000);
        }

        /**
         * Cancels the status timer.
         */
        public static void cancel() {
            if (task != null) {
                log.debug("Status timer canceled.");
                task.cancel();
                task = null;
            }
        }
    }

    /**
     * No instantiation.
     */
    private Network() {}

    /**
     * Connect. Starts both the send and receive logic, schedules the heartbeat
     * timer and sends a login message if required.
     */
    public static void connect() {
        setConnected(true);
        resetCounters();
        Sender.start();
        Receiver.start();
        HeartbeatTimeout.schedule();
        StatusTimer.schedule();
        LinkParticipantType source = LinkParticipantType.valueOf(props
                .getProperty(UserPrefs.LINK_ID,
                        LinkParticipantType.UNDEFINED.toString()));
        if (isSendLogin() && source != LinkParticipantType.C4I) {
            LinkParticipantType destination = LinkParticipantType.C4I;
            MessageHeader mh = new MessageHeader(MessageType.LOGIN, destination,
                    source);
            Login login = (Login) mh.getMessage();
            login.setPassword(props.getProperty(UserPrefs.PASSWORD, "DATA"));
            MessageQueue.getQueue().add(new DelayableMessage(login));
        }
        log.debug("Network connected.");
    }

    /**
     * Disconnect. Stops the send and receive logic, cancels the heartbeat timer
     * and saves any network traffic to disk if required.
     */
    public static void disconnect() {
        if (isConnected()) {
            setConnected(false);
            setLoggedIn(false);
            Receiver.stop();
            Sender.stop();
            HeartbeatTimeout.cancel();
            resetCounters();
            TIMER.purge();
            if (isSaveTraffic()) {
                try {
                    String fileName = props.getProperty(UserPrefs.LINK_ID) + "." +
                            new Date().getTime() + ".bytestream.bin";
                    FileOutputStream fos = new FileOutputStream(fileName);
                    ObjectOutputStream out = new ObjectOutputStream(fos);
                    out.writeObject(getTimeStampedFrames());
                    out.close();
                }
                catch (FileNotFoundException e) {
                    log.error("", e);
                }
                catch (IOException e) {
                    log.error("", e);
                }
            }
            log.debug("Network disconnected.");
        }
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public static boolean isConnected() {
        return connected;
    }

    /**
     * Gets the number of bytes sent.
     *
     * @return the sent
     */
    public static int getSent() {
        return sent;
    }

    /**
     * Gets the received.
     *
     * @return the number of bytes received
     */
    public static int getReceived() {
        return received;
    }

    /**
     * Checks if is extrapolate tracks.
     *
     * @return true, if is extrapolate tracks
     */
    public static boolean isExtrapolateTracks() {
        return extrapolateTracks;
    }

    /**
     * Sets the extrapolate tracks.
     *
     * @param extrapolateTracks
     *           the new extrapolate tracks
     */
    public static void setExtrapolateTracks(boolean extrapolateTracks) {
        Network.extrapolateTracks = extrapolateTracks;
    }

    /**
     * Checks if is save traffic.
     *
     * @return true, if is save traffic
     */
    public static boolean isSaveTraffic() {
        return saveTraffic;
    }

    /**
     * Sets the save traffic.
     *
     * @param saveTraffic
     *           the new save traffic
     */
    public static void setSaveTraffic(boolean saveTraffic) {
        Network.saveTraffic = saveTraffic;
    }

    /**
     * Checks if is send login.
     *
     * @return true, if is send login
     */
    public static boolean isSendLogin() {
        return sendLogin;
    }

    /**
     * Sets the send login.
     *
     * @param sendLogin
     *           the new send login
     */
    public static void setSendLogin(boolean sendLogin) {
        Network.sendLogin = sendLogin;
    }

    /**
     * Checks if is send login response.
     *
     * @return true, if is send login response
     */
    public static boolean isSendLoginResponse() {
        return sendLoginResponse;
    }

    /**
     * Sets the send login response.
     *
     * @param sendLoginResponse
     *           the new send login response
     */
    public static void setSendLoginResponse(boolean sendLoginResponse) {
        Network.sendLoginResponse = sendLoginResponse;
    }

    /**
     * Checks if is send heartbeats.
     *
     * @return true, if is send heartbeats
     */
    public static boolean isSendHeartbeats() {
        return sendHeartbeats;
    }

    /**
     * Sets the send heartbeats.
     *
     * @param sendHeartbeats
     *           the new send heartbeats
     */
    public static void setSendHeartbeats(boolean sendHeartbeats) {
        Network.sendHeartbeats = sendHeartbeats;
    }

    /**
     * Checks if is require heartbeats.
     *
     * @return true, if is require heartbeats
     */
    public static boolean isRequireHeartbeats() {
        return requireHeartbeats;
    }

    /**
     * Sets the require heartbeats.
     *
     * @param requireHeartbeats
     *           the new require heartbeats
     */
    public static void setRequireHeartbeats(boolean requireHeartbeats) {
        Network.requireHeartbeats = requireHeartbeats;
        if (Network.requireHeartbeats && isConnected()) {
            HeartbeatTimeout.schedule();
        }
    }

    /**
     * Checks if is send link status request.
     *
     * @return true, if is send link status request
     */
    public static boolean isSendLinkStatusRequest() {
        return sendLinkStatusRequest;
    }

    /**
     * Sets the send link status request.
     *
     * @param sendLinkStatusRequest
     *           the new send link status request
     */
    public static void setSendLinkStatusRequest(boolean sendLinkStatusRequest) {
        Network.sendLinkStatusRequest = sendLinkStatusRequest;
    }

    /**
     * Checks if is send link status response.
     *
     * @return true, if is send link status response
     */
    public static boolean isSendLinkStatusResponse() {
        return sendLinkStatusResponse;
    }

    /**
     * Sets the send link status response.
     *
     * @param sendLinkStatusResponse
     *           the new send link status response
     */
    public static void setSendLinkStatusResponse(boolean sendLinkStatusResponse) {
        Network.sendLinkStatusResponse = sendLinkStatusResponse;
    }

    /**
     * Checks if is send acks.
     *
     * @return true, if is send acks
     */
    public static boolean isSendAcks() {
        return sendAcks;
    }

    /**
     * Sets the send acks.
     *
     * @param sendAcks
     *           the new send acks
     */
    public static void setSendAcks(boolean sendAcks) {
        Network.sendAcks = sendAcks;
    }

    /**
     * Checks if is request acks.
     *
     * @return true, if is request acks
     */
    public static boolean isRequestAcks() {
        return requestAcks;
    }

    /**
     * Sets the request acks.
     *
     * @param requestAcks
     *           the new request acks
     */
    public static void setRequestAcks(boolean requestAcks) {
        Network.requestAcks = requestAcks;
    }

    /**
     * Reset counters.
     */
    static void resetCounters() {
        setSent(0);
        setReceived(0);
    }

    /**
     * Checks if is logged in.
     *
     * @return true, if is logged in
     */
    static boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Sets the logged in.
     *
     * @param loggedIn
     *           the new logged in
     */
    static void setLoggedIn(boolean loggedIn) {
        Network.loggedIn = loggedIn;
    }

    /**
     * Sets the connected.
     *
     * @param connected
     *           the new connected
     */
    static void setConnected(boolean connected) {
        Network.connected = connected;
    }

    /**
     * Gets the time stamped frames.
     *
     * @return the time stamped frames
     */
    static Hashtable<Long, byte[]> getTimeStampedFrames() {
        return timeStampedFrames;
    }

    /**
     * Gets the destination link participant.
     *
     * @return the destination link participant
     */
    static LinkParticipantType getDestinationLinkParticipant() {
        return destinationLinkParticipant;
    }

    /**
     * Sets the destination link participant.
     *
     * @param destinationLinkParticipant
     *           the new destination link participant
     */
    static void setDestinationLinkParticipant(
            LinkParticipantType destinationLinkParticipant) {
        Network.destinationLinkParticipant = destinationLinkParticipant;
    }

    /**
     * Sets the number of bytes sent.
     *
     * @param sent
     *           the new sent
     */
    static void setSent(int sent) {
        Network.sent = sent;
    }

    /**
     * Sets the number of bytes received.
     *
     * @param received
     *           the new received
     */
    static void setReceived(int received) {
        Network.received = received;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    private static String getState() {
        StringBuffer sb = new StringBuffer();
        sb.append("Status:\n");
        sb.append("Connected = " + isConnected() + "\n");
        sb.append("Sent = " + getSent() + "\n");
        sb.append("Received = " + getReceived() + "\n");
        sb.append("Extrapolate = " + isExtrapolateTracks() + "\n");
        sb.append("Send login = " + isSendLogin() + "\n");
        sb.append("Send login response = " + isSendLoginResponse() + "\n");
        sb.append("Send heartbeats = " + isSendHeartbeats() + "\n");
        sb.append("Require heartbeats = " + isRequireHeartbeats() + "\n");
        sb.append("Send link status request = " + isSendLinkStatusRequest() +
                "\n");
        sb.append("Send link status response = " + isSendLinkStatusResponse() +
                "\n");
        sb.append("Send acks = " + isSendAcks() + "\n");
        sb.append("Request acks = " + isRequestAcks() + "\n");
        sb.append("Logged in = " + isLoggedIn() + "\n");
        return sb.toString();
    }

    /**
     * Checks if is append ssr.
     *
     * @return true, if is append ssr
     */
    public static boolean isAppendSSR() {
        return appendSSR;
    }

    /**
     * Sets the append ssr.
     *
     * @param appendSSR
     *           the new append ssr
     */
    public static void setAppendSSR(boolean appendSSR) {
        Network.appendSSR = appendSSR;
    }

    /**
     * Checks if is include heartbeats.
     *
     * @return true, if is include heartbeats
     */
    public static boolean isIncludeHeartbeats() {
        return includeHeartbeats;
    }

    /**
     * Sets the include heartbeats.
     *
     * @param includeHeartbeats
     *           the new include heartbeats
     */
    public static void setIncludeHeartbeats(boolean includeHeartbeats) {
        Network.includeHeartbeats = includeHeartbeats;
    }
}
// UNCLASSIFIED
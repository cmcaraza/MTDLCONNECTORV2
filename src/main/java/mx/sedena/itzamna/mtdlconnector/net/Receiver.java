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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import mx.sedena.itzamna.mtdlconnector.core.Frame;
import mx.sedena.itzamna.mtdlconnector.core.FrameHeader;
import mx.sedena.itzamna.mtdlconnector.core.InvalidFrameException;
import mx.sedena.itzamna.mtdlconnector.core.InvalidMessageException;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.Ack;
import mx.sedena.itzamna.mtdlconnector.core.LinkStatusResponse;
import mx.sedena.itzamna.mtdlconnector.core.Login;
import mx.sedena.itzamna.mtdlconnector.core.LoginResponse;
import mx.sedena.itzamna.mtdlconnector.core.LinkParticipantType;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;
import mx.sedena.itzamna.mtdlconnector.core.UserPrefs;

/**
 * Starts a thread that parses received messages and adds them to the network
 * traffic list. Takes action if messages require a response or an internal
 * state change in the application.
 */
class Receiver implements Runnable {

    /** The logger for this class. */
    private static Logger log = Logger.getLogger(Receiver.class);

    /** Properties. */
    private static Properties props = UserPrefs.getProps();

    /** Singleton. */
    private static Thread receiver = null;

    /** Receiver socket. */
    private static DatagramSocket socket = null;

    /** Singleton */
    private Receiver() {}

    /**
     * Starts listening for traffic.
     */
    public static void start() {
        receiver = new Thread(new Receiver());
        receiver.start();
    }

    /**
     * Stop.
     */
    public static void stop() {
        receiver.interrupt();
        socket.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    /**
     * Parses received messages and adds them to the network traffic list.
     */
    @Override
    public void run() {
        InetAddress address = null;
        int port = 0;
        try {
            port = Integer
                    .valueOf(props.getProperty(UserPrefs.LOCAL_PORT, "7777"));
            address = InetAddress.getByName(props.getProperty(
                    UserPrefs.LOCAL_ADDRESS, "localhost"));
            socket = new DatagramSocket(port, address);
        }
        catch (SocketException e) {
            Network.setConnected(false);
            log.error("", e);
        }
        catch (UnknownHostException e) {
            Network.setConnected(false);
            log.error("", e);
        }
        while (Network.isConnected()) {
            try {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream data = new ByteArrayInputStream(
                        packet.getData());
                Network.setReceived(Network.getReceived() + packet.getLength());
                while (data.available() > 0) {
                    data.mark(2);
                    if (data.read() == FrameHeader.MARKER_HIGH_BYTE) {
                        if (data.read() == FrameHeader.MARKER_LOW_BYTE) {
                            data.reset();
                            byte[] header = new byte[FrameHeader.LENGTH];
                            for (int i = 0; i < header.length; i++) {
                                header[i] = (byte) data.read();
                            }

                            FrameHeader fh = new FrameHeader(header);

                            byte[] body = new byte[fh.getBodySize()];
                            for (int i = 0; i < body.length; i++) {
                                body[i] = (byte) data.read();
                            }

                            Frame frame = new Frame(fh, body);
                            log.debug("Received Frame: " +
                                    Hex.encodeHexString(frame.getBytes()) +
                                    System.getProperty("line.separator") +
                                    frame.toString());
                            Network.getTimeStampedFrames().put(new Date().getTime(),
                                    frame.getBytes());
                            for (Message m : frame.getMessages()) {
                                MessageType type = m.getHeader().getMessageType();
                                Network.TRAFFIC_STATS.received(type);
                                boolean heartbeat = (type == MessageType.AEW_HEARTBEAT || type == MessageType.C4I_HEARTBEAT);

                                handleMessage(m);
                            }
                        }
                    }
                }

            }
            catch (SocketException e) {
                if (Network.isConnected()) {
                    log.error("", e);
                    Network.setConnected(false);
                }
            }
            catch (IOException e) {
                log.error("", e);
                socket.close();
                Network.setConnected(false);
            }
            catch (InvalidFrameException e) {
                log.error("", e);
            }
            catch (InvalidMessageException e) {
                log.error("", e);
            }
        }
    }

    /**
     * Handle messages that require a response or an internal state change.
     *
     * @param m
     *           the message
     */
    private void handleMessage(Message m) {
        LinkParticipantType source = m.getHeader().getSourceType();
        LinkParticipantType destination = m.getHeader().getDestinationType();
        Network.setDestinationLinkParticipant(source);
        LinkParticipantType me = LinkParticipantType.valueOf(props
                .getProperty(UserPrefs.LINK_ID));
        if (destination == me) {
            switch (m.getHeader().getMessageType()) {
                case LOGIN:
                    byte accepted = 0;
                    if (((Login) m).getPassword().equals(
                            props.getProperty(UserPrefs.PASSWORD, "DATA"))) {
                        accepted = 1;
                        Network.setLoggedIn(true);
                        Network.HeartbeatTimeout.schedule();
                    }
                    if (Network.isSendLoginResponse()) {
                        MessageHeader header = new MessageHeader(
                                MessageType.LOGIN_RESPONSE, source, me);
                        LoginResponse lr = new LoginResponse(header);
                        lr.setPasswordOk(accepted);
                        MessageQueue.getQueue().add(new DelayableMessage(lr));
                    }
                    break;
                case LOGIN_RESPONSE:
                    if (((LoginResponse) m).getPasswordOk() == 1) {
                        Network.setLoggedIn(true);
                        Network.HeartbeatTimeout.schedule();
                    }
                    break;
                case C4I_HEARTBEAT: // Fall-through
                case AEW_HEARTBEAT:
                    Network.HeartbeatTimeout.schedule();
                    break;
                case LINK_STATUS_REQUEST:
                    if (Network.isSendLinkStatusResponse()) {
                        LinkStatusResponse lsr = new LinkStatusResponse(
                                new MessageHeader(MessageType.LINK_STATUS_RESPONSE,
                                        source, me));
                        MessageQueue.getQueue().add(new DelayableMessage(lsr));
                    }
                    break;
                case LINK_STATUS_RESPONSE:
                    String message = "Link Status Response Received" +
                            System.getProperty("line.separator");
                    message += "Local Bytes Received: " + Network.getReceived() +
                            System.getProperty("line.separator");
                    message += "Local Bytes Sent: " + Network.getSent() +
                            System.getProperty("line.separator");
                    log.debug(message);
                    break;
                case ACK:
                    // no action
                    break;
            }
            if (m.getHeader().isAckRequired() && Network.isSendAcks() &&
                    (m.getHeader().getMessageType() != MessageType.ACK)) {
                Ack ack = new Ack(new MessageHeader(MessageType.ACK, source, me));
                ack.setSequenceNumber((short) m.getHeader().getSequenceNumber());
                ack.setMysteryResponse((byte) 1);
                MessageQueue.getQueue().add(new DelayableMessage(ack));
            }
        }
    }
}

// UNCLASSIFIED
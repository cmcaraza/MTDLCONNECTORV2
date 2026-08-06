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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import mx.sedena.itzamna.mtdlconnector.core.Frame;
import mx.sedena.itzamna.mtdlconnector.core.ControlMessage;
import mx.sedena.itzamna.mtdlconnector.core.DataMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.AbstractTrackPosition;
import mx.sedena.itzamna.mtdlconnector.core.Ack;
import mx.sedena.itzamna.mtdlconnector.core.AirTrackData;
import mx.sedena.itzamna.mtdlconnector.core.AircraftPosition;
import mx.sedena.itzamna.mtdlconnector.core.C4ITrackData;
import mx.sedena.itzamna.mtdlconnector.core.ConnectedSSRData;
import mx.sedena.itzamna.mtdlconnector.core.UnconnectedSSRData;
import mx.sedena.itzamna.mtdlconnector.core.LinkParticipantType;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;
import mx.sedena.itzamna.mtdlconnector.core.UserPrefs;

/**
 *
 * Starts a thread that adds messages to be sent to a buffer and sends them when
 * either:
 *
 * 1) a configurable number of messages have been added to the queue
 * or
 * 2) a configurable amount of time has passed.
 */
class Sender implements Runnable {

    /** The logger for this class. */
    private static Logger log = Logger.getLogger(Sender.class);

    /** Properties. */
    private static Properties props = UserPrefs.getProps();

    /** Singleton. */
    private static Thread sender = null;

    /** Network queue. */
    private static BlockingQueue<DelayableMessage> queue = MessageQueue
            .getQueue();

    /** Set of messages (a frame) to be sent. */
    private static List<DataMessage> frameMessageBuffer = Collections
            .synchronizedList(new ArrayList<DataMessage>());

    /** Singleton. */
    private Sender() {}

    /** The frame sequence number counter. */
    private static byte frameSeqNo = 0;

    /** The message sequence number counter. */
    private static short messageSeqNo = 0;

    /** The send frame task. */
    private static TimerTask sendFrameTask = null;

    /** The heartbeat task. */
    private static TimerTask heartBeatTask = null;

    /** The link status request task. */
    private static TimerTask linkStatusRequestTask = null;

    /** The track extrapolator task. */
    private static TimerTask extrapolatorTask = null;

    /**
     * Starts any timers that should be running and starts the sender thread.
     */
    public static void start() {
        sender = new Thread(new Sender());
        sender.start();

        log.debug("Sender.start()");

        // Sends the message buffer when the send timeout has expired
        sendFrameTask = new TimerTask() {
            @Override
            public void run() {
                log.debug("sendFrameTask?");
                if (Network.isConnected() && (frameMessageBuffer.size() > 0) &&
                        Network.isLoggedIn()) {
                    log.debug("sendFrameTask!");
                    sendFrame(new Frame(assembleMessages(), (byte) getFrameSeqNo()));
                }
            }
        };
        int millis = Integer.valueOf(props.getProperty(UserPrefs.FRAME_WINDOW,
                "100"));
        Network.TIMER.scheduleAtFixedRate(sendFrameTask, millis, millis);

        heartBeatTask = new TimerTask() {
            @Override
            public void run() {
                log.debug("heartBeatTask?");
                if (Network.isConnected() && Network.isSendHeartbeats() &&
                        Network.isLoggedIn()) {
                    log.debug("heartBeatTask!");
                    LinkParticipantType me = LinkParticipantType.valueOf(props
                            .getProperty(UserPrefs.LINK_ID));
                    MessageType hb = (me == LinkParticipantType.C4I ? MessageType.C4I_HEARTBEAT
                            : MessageType.AEW_HEARTBEAT);
                    MessageHeader mh = new MessageHeader(hb,
                            Network.getDestinationLinkParticipant(), me);
                    queue.add(new DelayableMessage(mh.getMessage()));
                }
            }
        };
        int hbm = Integer.valueOf(props.getProperty(UserPrefs.HEARTBEAT_RATE,
                "2000"));
        Network.TIMER.scheduleAtFixedRate(heartBeatTask, hbm, hbm);

        linkStatusRequestTask = new TimerTask() {
            @Override
            public void run() {
                log.debug("linkStatusRequestTask?");
                if (Network.isConnected() && Network.isSendLinkStatusRequest() &&
                        Network.isLoggedIn()) {
                    log.debug("linkStatusRequestTask!");
                    LinkParticipantType me = LinkParticipantType.valueOf(props
                            .getProperty(UserPrefs.LINK_ID));
                    MessageHeader mh = new MessageHeader(
                            MessageType.LINK_STATUS_REQUEST,
                            Network.getDestinationLinkParticipant(), me);
                    queue.add(new DelayableMessage(mh.getMessage()));
                }
            }
        };
        int lin = Integer.valueOf(props.getProperty(UserPrefs.LINK_STATUS_RATE,
                "60000"));
        Network.TIMER.scheduleAtFixedRate(linkStatusRequestTask, lin, lin);

        extrapolatorTask = new TimerTask() {
            @Override
            public void run() {
                log.debug("extrapolatorTask?");
                if (Network.isConnected() && Network.isExtrapolateTracks() &&
                        Network.isLoggedIn()) {
                    log.debug("extrapolatorTask!");
                    for (DelayableMessage dm : DelayableMessageList.getMessageList()) {
                        Message message = dm.getMessage();
                        if (message instanceof AbstractTrackPosition) {
                            extrapolate((AbstractTrackPosition) message);
                        }
                    }
                    // put modified messages on the network queue
                    for (DelayableMessage m : DelayableMessageList.getMessageList()) {
                        queue.add(m.getMessage() instanceof AbstractTrackPosition ? new DelayableMessage(
                                trackclone((AbstractTrackPosition) m.getMessage()), m
                                .getDelayTime()) : m);
                    }
                }
            }
        };
        int ext = Integer.valueOf(props.getProperty(UserPrefs.EXTRAPOLATE_RATE,
                "5000"));
        Network.TIMER.scheduleAtFixedRate(extrapolatorTask, ext, ext);
    }

    /**
     * Stops all timers, resets sequence numbers and stops the sender thread.
     */
    public static void stop() {
        sendFrameTask.cancel();
        heartBeatTask.cancel();
        linkStatusRequestTask.cancel();
        extrapolatorTask.cancel();
        frameSeqNo = 0;
        messageSeqNo = 0;
        sender.interrupt();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    /**
     * Adds messages to the send buffer and sends them when the buffer is full.
     */
    @Override
    public void run() {
        int max_messages = Integer.valueOf(props.getProperty(
                UserPrefs.FRAME_MAX_MSG_COUNT, "10"));
        while (Network.isConnected()) {
            try {
                Message message = queue.take().getMessage();
                if (message instanceof ControlMessage) {
                    message.getHeader().setSequenceNumber((short) 0);
                    sendFrame(new Frame((ControlMessage) message,
                            (byte) getFrameSeqNo()));
                }
                else {
                    if (message instanceof AbstractTrackPosition) {
                        if (((AbstractTrackPosition) message).getTime() == 0) {
                            ((AbstractTrackPosition) message).setTime(new Date()
                                    .getTime() / 1000);
                        }
                    }
                    message.getHeader().setSequenceNumber((short) getMessageSeqNo());
                    if (Network.isRequestAcks() && !(message instanceof Ack)) {
                        message.getHeader().setAck(true);
                    }
                    frameMessageBuffer.add((DataMessage) message);
                    if (Network.isAppendSSR() && message instanceof AirTrackData) {
                        MessageHeader mh = new MessageHeader(
                                MessageType.CONNECTED_SSR_DATA, message.getHeader()
                                .getDestinationType(), message.getHeader()
                                .getSourceType());
                        ConnectedSSRData cssr = new ConnectedSSRData(mh);
                        cssr.setTrackNumber(((AirTrackData) message).getTrackNumber());
                        frameMessageBuffer.add(cssr);
                    }
                    if (frameMessageBuffer.size() >= max_messages) {
                        sendFrame(new Frame(assembleMessages(),
                                (byte) getFrameSeqNo()));
                    }
                }
            }
            catch (InterruptedException e) {
                Network.setConnected(false);
            }
            catch (Throwable t) {
                log.error("", t);
            }
        }
    }

    /**
     * Gets the current frame sequence number.
     *
     * @return the current frame sequence number
     */
    private static int getFrameSeqNo() {
        byte seq = frameSeqNo++;
        return (seq & 0xff);
    }

    /**
     * Gets the current message sequence number.
     *
     * @return the current message sequence number
     */
    private static int getMessageSeqNo() {
        short seq = messageSeqNo++;
        return (seq & 0xffff);
    }

    /**
     * Sends a frame over the network and puts the frame in the network traffic
     * list.
     *
     * @param frame
     *           the frame to send
     */
    private static synchronized void sendFrame(Frame frame) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] buffer = frame.getBytes();
            InetAddress address = InetAddress.getByName(props.getProperty(
                    UserPrefs.REMOTE_ADDRESS, "localhost"));
            int port = Integer.valueOf(props.getProperty(UserPrefs.REMOTE_PORT,
                    "7777"));
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    address, port);
            socket.send(packet);
            Network.setSent(Network.getSent() + packet.getLength());
            log.debug("Sent Frame: " + Hex.encodeHexString(frame.getBytes()) +
                    System.getProperty("line.separator") + frame.toString());
            Network.getTimeStampedFrames().put(new Date().getTime(),
                    frame.getBytes());
            for (Message m : frame.getMessages()) {
                MessageType type = m.getHeader().getMessageType();
                Network.TRAFFIC_STATS.sent(type);
                boolean heartbeat = (type == MessageType.AEW_HEARTBEAT || type == MessageType.C4I_HEARTBEAT);

            }
        }
        catch (SocketException e) {
            log.error("", e);
        }
        catch (UnknownHostException e) {
            log.error("", e);
        }
        catch (IOException e) {
            log.error("", e);
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    /**
     * Assembles an array of messages
     *
     * @return message[]
     */
    private static DataMessage[] assembleMessages() {
        DataMessage[] messages = new DataMessage[frameMessageBuffer.size()];
        messages = frameMessageBuffer.toArray(messages);
        frameMessageBuffer.clear();
        return messages;
    }

    /*
     * @see http://stackoverflow.com/questions/5857523
     */
    /**
     * Extrapolates a track. The attributes of the given track are modified
     * (i.e.
     * this method has side-effects).
     *
     * @param atp
     *           the track to be extrapolated
     */
    private static void extrapolate(AbstractTrackPosition atp) {
        final double MINUTES_TO_METERS = 1852d;
        final double DEGREE_TO_MINUTES = 60d;

        final double crs = Math.toRadians(atp.getAngleDegrees());
        final double distance = atp.getSpeedMetersPerSecond() *
                (Integer.valueOf(props.getProperty(UserPrefs.EXTRAPOLATE_RATE,
                        "5000")) / 1000);
        final double d12 = Math.toRadians(distance / MINUTES_TO_METERS /
                DEGREE_TO_MINUTES);

        final double lat1 = Math.toRadians(atp.getLatitudeDegrees());
        final double lon1 = Math.toRadians(atp.getLongitudeDegrees());

        final double lat = Math.asin((Math.sin(lat1) * Math.cos(d12)) +
                (Math.cos(lat1) * Math.sin(d12) * Math.cos(crs)));
        final double dlon = Math.atan2(
                Math.sin(crs) * Math.sin(d12) * Math.cos(lat1), Math.cos(d12) -
                        (Math.sin(lat1) * Math.sin(lat)));
        final double lon = ((lon1 + dlon + Math.PI) % (2 * Math.PI)) - Math.PI;

        atp.setLatitudeDegrees(Math.toDegrees(lat));
        atp.setLongitudeDegrees(Math.toDegrees(lon));
    }

    /**
     * Clones a track. The attributes of the given track are preserved with the
     * construction of the corresponding objects. (change from previous version
     * due to the need to preserve object integrity and saving). Additions to
     * AbstractTrackPosition would have to involve adding subsequent children,
     * for now.
     *
     * @param atp
     *           the track to be cloned
     */
    private static AbstractTrackPosition trackclone(AbstractTrackPosition atp) {
        byte[] header = new byte[MessageHeader.LENGTH];
        System.arraycopy(atp.getHeader().getBytes(), 0, header, 0, header.length);
        byte[] body = new byte[atp.getHeader().getBodySize()];
        System.arraycopy(atp.getBytes(), MessageHeader.LENGTH, body, 0,
                body.length);
        try {
            switch (atp.getHeader().getMessageType()) {
                case AIRCRAFT_POSITION:
                    return new AircraftPosition(new MessageHeader(header), body);
                case AIR_TRACK_DATA:
                    return new AirTrackData(new MessageHeader(header), body);
                case C4I_TRACK_DATA:
                    return new C4ITrackData(new MessageHeader(header), body);
                case UNCONNECTED_SSR_DATA:
                    return new UnconnectedSSRData(new MessageHeader(header), body);
                default:
                    log.error("AbstractTrackPosition has been passed by reference.");
                    return atp;
            }
        }
        catch (Throwable t) {
            log.error("", t);
            return atp;
        }
    }
}

// UNCLASSIFIED
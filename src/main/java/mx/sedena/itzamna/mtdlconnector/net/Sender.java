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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import mx.sedena.itzamna.mtdlconnector.util.ConfigManager;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import mx.sedena.itzamna.mtdlconnector.core.*;

class Sender implements Runnable {

    private static Logger log = Logger.getLogger(Sender.class);
    private static Thread sender = null;
    private static BlockingQueue<DelayableMessage> queue = MessageQueue.getQueue();
    private static List<DataMessage> frameMessageBuffer = Collections.synchronizedList(new ArrayList<DataMessage>());

    private static byte frameSeqNo = 0;
    private static short messageSeqNo = 0;

    private static TimerTask sendFrameTask = null;
    private static TimerTask heartBeatTask = null;
    private static TimerTask linkStatusRequestTask = null;

    private Sender() {}

    public static void start() {
        sender = new Thread(new Sender());
        sender.start();

        sendFrameTask = new TimerTask() {
            @Override
            public void run() {
                if (Network.isConnected() && (frameMessageBuffer.size() > 0) && Network.isLoggedIn()) {
                    sendFrame(new Frame(assembleMessages(), (byte) getFrameSeqNo()));
                }
            }
        };
        Network.TIMER.scheduleAtFixedRate(sendFrameTask, 100, 100);

        heartBeatTask = new TimerTask() {
            @Override
            public void run() {
                if (Network.isConnected() && Network.isSendHeartbeats() && Network.isLoggedIn()) {
                    // PASO 3: Validar envío de Heartbeat
                    String miId = ConfigManager.getInstance().getParticipantId();
                    System.out.println("[TX - HEARTBEAT] -> Emitiendo latido táctico como: " + miId);

                    LinkParticipantType me = LinkParticipantType.valueOf(miId);
                    MessageType hb = (me == LinkParticipantType.C4I) ? MessageType.C4I_HEARTBEAT : MessageType.AEW_HEARTBEAT;
                    MessageHeader mh = new MessageHeader(hb, Network.getDestinationLinkParticipant(), me);
                    queue.add(new DelayableMessage(mh.getMessage()));
                }
            }
        };
        Network.TIMER.scheduleAtFixedRate(heartBeatTask, 2000, 2000);

        linkStatusRequestTask = new TimerTask() {
            @Override
            public void run() {
                if (Network.isConnected() && Network.isSendLinkStatusRequest() && Network.isLoggedIn()) {
                    LinkParticipantType me = LinkParticipantType.valueOf(ConfigManager.getInstance().getParticipantId());
                    MessageHeader mh = new MessageHeader(MessageType.LINK_STATUS_REQUEST, Network.getDestinationLinkParticipant(), me);
                    queue.add(new DelayableMessage(mh.getMessage()));
                }
            }
        };
        Network.TIMER.scheduleAtFixedRate(linkStatusRequestTask, 60000, 60000);
    }

    public static void stop() {
        if(sendFrameTask != null) sendFrameTask.cancel();
        if(heartBeatTask != null) heartBeatTask.cancel();
        if(linkStatusRequestTask != null) linkStatusRequestTask.cancel();
        frameSeqNo = 0;
        messageSeqNo = 0;
        if(sender != null) sender.interrupt();
    }

    @Override
    public void run() {
        int max_messages = 10;
        while (Network.isConnected()) {
            try {
                Message message = queue.take().getMessage();
                if (message instanceof ControlMessage) {
                    message.getHeader().setSequenceNumber((short) 0);
                    sendFrame(new Frame((ControlMessage) message, (byte) getFrameSeqNo()));
                } else {
                    if (message instanceof AbstractTrackPosition) {
                        if (((AbstractTrackPosition) message).getTime() == 0) {
                            ((AbstractTrackPosition) message).setTime(new Date().getTime() / 1000);
                        }
                    }
                    message.getHeader().setSequenceNumber((short) getMessageSeqNo());
                    if (Network.isRequestAcks() && !(message instanceof Ack)) {
                        message.getHeader().setAck(true);
                    }
                    frameMessageBuffer.add((DataMessage) message);

                    if (frameMessageBuffer.size() >= max_messages) {
                        sendFrame(new Frame(assembleMessages(), (byte) getFrameSeqNo()));
                    }
                }
            } catch (InterruptedException e) {
                Network.setConnected(false);
            } catch (Throwable t) {
                log.error("", t);
            }
        }
    }

    private static int getFrameSeqNo() { return (frameSeqNo++ & 0xff); }
    private static int getMessageSeqNo() { return (messageSeqNo++ & 0xffff); }

    private static synchronized void sendFrame(Frame frame) {
        DatagramSocket socket = null;
        ConfigManager config = ConfigManager.getInstance();
        try {
            socket = new DatagramSocket();
            byte[] buffer = frame.getBytes();

            // PASO 6: Desglose antes de enviar a la red
            System.out.println("\n[TX - DATA] ---> PREPARANDO ENVÍO FÍSICO A RED:");
            System.out.println("   -> Destino : " + config.getMtdlRemoteIp() + ":" + config.getMtdl_RemotePort());
            System.out.println("   -> Mensajes: " + frame.getMessages().length);
            System.out.println("   -> Payload : " + Hex.encodeHexString(buffer).substring(0, Math.min(buffer.length * 2, 40)) + "...");

            InetAddress address = InetAddress.getByName(config.getMtdlRemoteIp());
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, config.getMtdl_RemotePort());
            socket.send(packet);
            Network.setSent(Network.getSent() + packet.getLength());

        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (socket != null) socket.close();
        }
    }

    private static DataMessage[] assembleMessages() {
        DataMessage[] messages = new DataMessage[frameMessageBuffer.size()];
        messages = frameMessageBuffer.toArray(messages);
        frameMessageBuffer.clear();
        return messages;
    }
}
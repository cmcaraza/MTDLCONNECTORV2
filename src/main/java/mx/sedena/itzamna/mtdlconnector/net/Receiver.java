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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.net.SocketException;

import mx.sedena.itzamna.mtdlconnector.util.ConfigManager;
import org.apache.log4j.Logger;

import mx.sedena.itzamna.mtdlconnector.core.*;

class Receiver implements Runnable {

    private static Logger log = Logger.getLogger(Receiver.class);
    private static Thread receiver = null;
    private static DatagramSocket socket = null;

    private Receiver() {}

    public static void start() {
        receiver = new Thread(new Receiver());
        receiver.start();
    }

    public static void stop() {
        if(receiver != null) receiver.interrupt();
        if(socket != null) socket.close();
    }

    @Override
    public void run() {
        ConfigManager config = ConfigManager.getInstance();
        try {
            int port = config.getMtdl_LocalPort();
            InetAddress address = InetAddress.getByName(config.getMtdl_LocalIp());
            socket = new DatagramSocket(new InetSocketAddress(address, port));

            // En modo "Laboratorio/localhost" atamos a la interfaz si existe
            NetworkInterface netIf = NetworkInterface.getByName(config.getMtdlNetInterface());
            if (netIf != null) {
                socket.setOption(StandardSocketOptions.IP_MULTICAST_IF, netIf);
            }

        } catch (Exception e) {
            Network.setConnected(false);
            log.error("Fallo al iniciar Receiver", e);
            return;
        }

        while (Network.isConnected()) {
            try {
                byte[] buffer = new byte[65535];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream data = new ByteArrayInputStream(packet.getData());
                Network.setReceived(Network.getReceived() + packet.getLength());

                while (data.available() > 0) {
                    data.mark(2);
                    if (data.read() == FrameHeader.MARKER_HIGH_BYTE) {
                        if (data.read() == FrameHeader.MARKER_LOW_BYTE) {
                            data.reset();
                            byte[] header = new byte[FrameHeader.LENGTH];
                            for (int i = 0; i < header.length; i++) header[i] = (byte) data.read();

                            FrameHeader fh = new FrameHeader(header);
                            byte[] body = new byte[fh.getBodySize()];
                            for (int i = 0; i < body.length; i++) body[i] = (byte) data.read();

                            Frame frame = new Frame(fh, body);

                            for (Message m : frame.getMessages()) {

                                // PASO 5: Desglose de Traza Cruda (Modo Laboratorio)
                                if (m instanceof AirTrackData) {
                                    AirTrackData track = (AirTrackData) m;
                                    System.out.println("\n[RX - TRAZA AÉREA DECODIFICADA]");
                                    System.out.println("   -> ID Pista : " + track.getTrackNumber());
                                    System.out.println("   -> Posición : Lat " + track.getLatitudeDegrees() + " | Lon " + track.getLongitudeDegrees());
                                    System.out.println("   -> Alt / Vel: " + track.getAltitude() + " ft | " + track.getSpeed() + " nudos");
                                    System.out.println("   -> Identidad: " + new String(track.getIdentity()).trim());

                                } else if (m instanceof C4ITrackData) {
                                    C4ITrackData c4i = (C4ITrackData) m;
                                    System.out.println("\n[RX - TRAZA C4I DECODIFICADA]");
                                    System.out.println("   -> ID Pista : " + c4i.getTrackNumber());
                                    System.out.println("   -> Posición : Lat " + c4i.getLatitudeDegrees() + " | Lon " + c4i.getLongitudeDegrees());
                                }

                                handleMessage(m);
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                if (Network.isConnected()) Network.setConnected(false);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private void handleMessage(Message m) {
        LinkParticipantType source = m.getHeader().getSourceType();
        LinkParticipantType destination = m.getHeader().getDestinationType();
        Network.setDestinationLinkParticipant(source);

        LinkParticipantType me = LinkParticipantType.valueOf(ConfigManager.getInstance().getParticipantId());

        if (destination == me || destination == LinkParticipantType.BROADCAST) {
            switch (m.getHeader().getMessageType()) {
                case LOGIN:
                    // PASO 4: Validación de Handshake
                    System.out.println("[RX - HANDSHAKE] <- Petición de LOGIN recibida desde: " + source);
                    Network.setLoggedIn(true);
                    Network.HeartbeatTimeout.schedule();
                    if (Network.isSendLoginResponse()) {
                        System.out.println("[TX - HANDSHAKE] -> Respondiendo LOGIN_RESPONSE (Aceptado)");
                        MessageHeader header = new MessageHeader(MessageType.LOGIN_RESPONSE, source, me);
                        LoginResponse lr = new LoginResponse(header);
                        lr.setPasswordOk((byte) 1);
                        MessageQueue.getQueue().add(new DelayableMessage(lr));
                    }
                    break;
                case LOGIN_RESPONSE:
                    System.out.println("[RX - HANDSHAKE] <- LOGIN_RESPONSE recibido. Módulo Autenticado.");
                    if (((LoginResponse) m).getPasswordOk() == 1) {
                        Network.setLoggedIn(true);
                        Network.HeartbeatTimeout.schedule();
                    }
                    break;
                case C4I_HEARTBEAT:
                case AEW_HEARTBEAT:
                    System.out.println("[RX - HEARTBEAT] <- Latido recibido del corresponsal: " + source);
                    Network.HeartbeatTimeout.schedule();
                    break;
                case LINK_STATUS_REQUEST:
                    if (Network.isSendLinkStatusResponse()) {
                        LinkStatusResponse lsr = new LinkStatusResponse(new MessageHeader(MessageType.LINK_STATUS_RESPONSE, source, me));
                        MessageQueue.getQueue().add(new DelayableMessage(lsr));
                    }
                    break;
                case ACK:
                    break;
            }
            if (m.getHeader().isAckRequired() && Network.isSendAcks() && (m.getHeader().getMessageType() != MessageType.ACK)) {
                Ack ack = new Ack(new MessageHeader(MessageType.ACK, source, me));
                ack.setSequenceNumber((short) m.getHeader().getSequenceNumber());
                ack.setMysteryResponse((byte) 1);
                MessageQueue.getQueue().add(new DelayableMessage(ack));
            }
        }
    }
}

// UNCLASSIFIED
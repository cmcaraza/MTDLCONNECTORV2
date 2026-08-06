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
package mx.sedena.itzamna.mtdlconnector.core;

import java.nio.ByteBuffer;

import mx.sedena.itzamna.mtdlconnector.net.Network;
import mx.sedena.itzamna.mtdlconnector.core.ControlMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;

/**
 * Defines an MTDL Link Status Response message.
 */
public class LinkStatusResponse extends Message implements ControlMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The bytes received. */
    private int bytesReceived = 0;

    /** The bytes sent. */
    private int bytesSent = 0;

    /**
     * Instantiates a new link status response.
     *
     * @param header
     *           the header
     */
    public LinkStatusResponse(MessageHeader header) {
        this(header, null);
        setBytesSent(Network.getSent());
        setBytesReceived(Network.getReceived());
    }

    /**
     * Instantiates a new link status response.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public LinkStatusResponse(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.LINK_STATUS_RESPONSE.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            setBytesReceived(buffer.getInt(0));
            setBytesSent(buffer.getInt(4));

            test(body);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.Message#toString()
     */
    @Override
    public String toString() {
        String message = super.toString();
        message += "Bytes Received: " + getBytesReceived() +
                System.getProperty("line.separator");
        message += "Bytes Sent: " + getBytesSent() +
                System.getProperty("line.separator");
        return message;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.Message#getBytes()
     */
    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(header.getSize());
        buffer.put(header.getBytes());
        buffer.putInt(getBytesReceived());
        buffer.putInt(getBytesSent());
        return buffer.array();
    }

    /**
     * Gets the bytes received.
     *
     * @return the bytes received
     */
    public int getBytesReceived() {
        return bytesReceived;
    }

    /**
     * Sets the bytes received.
     *
     * @param bytesReceived
     *           the new bytes received
     */
    public void setBytesReceived(int bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    /**
     * Gets the bytes sent.
     *
     * @return the bytes sent
     */
    public int getBytesSent() {
        return bytesSent;
    }

    /**
     * Sets the bytes sent.
     *
     * @param bytesSent
     *           the new bytes sent
     */
    public void setBytesSent(int bytesSent) {
        this.bytesSent = bytesSent;
    }
}
// UNCLASSIFIED
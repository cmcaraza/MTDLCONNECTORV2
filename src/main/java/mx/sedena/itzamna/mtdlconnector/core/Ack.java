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

import mx.sedena.itzamna.mtdlconnector.core.DataMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;

/**
 * Defines an MTDL Ack message.
 */
public class Ack extends Message implements DataMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The reserved. */
    private short reserved = 0;

    /** The sequence number. */
    private short sequenceNumber = 0;

    /** The mystery response. */
    private byte mysteryResponse = 0;

    /**
     * Instantiates a new ack.
     *
     * @param header
     *           the header
     */
    public Ack(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new ack.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public Ack(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.ACK.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            setReserved(buffer.getShort(0));
            setSequenceNumber(buffer.getShort(2));
            setMysteryResponse(buffer.get(4));
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
        message += "Reserved: " + getReserved() +
                System.getProperty("line.separator");
        message += "Sequence Number: " + getSequenceNumber() +
                System.getProperty("line.separator");
        message += "One: " + getMysteryResponse() +
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
        buffer.putShort(getReserved());
        buffer.putShort(getSequenceNumber());
        buffer.put(getMysteryResponse());
        return buffer.array();
    }

    /**
     * Gets the reserved.
     *
     * @return the reserved
     */
    public short getReserved() {
        return reserved;
    }

    /**
     * Sets the reserved.
     *
     * @param reserved
     *           the new reserved
     */
    public void setReserved(short reserved) {
        this.reserved = reserved;
    }

    /**
     * Gets the sequence number.
     *
     * @return the sequence number
     */
    public short getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequence number.
     *
     * @param sequenceNumber
     *           the new sequence number
     */
    public void setSequenceNumber(short sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the mystery response.
     *
     * @return the mystery response
     */
    public byte getMysteryResponse() {
        return mysteryResponse;
    }

    /**
     * Sets the mystery response.
     *
     * @param mysteryResponse
     *           the new mystery response
     */
    public void setMysteryResponse(byte mysteryResponse) {
        this.mysteryResponse = mysteryResponse;
    }
}
// UNCLASSIFIED

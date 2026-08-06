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
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

import mx.sedena.itzamna.mtdlconnector.core.FrameType;

/**
 * Defines an MTDL Frame Header
 */
public class FrameHeader {

    /** The Constant MARKER_HIGH_BYTE. */
    public static final int MARKER_HIGH_BYTE = 0xaa;

    /** The Constant MARKER_LOW_BYTE. */
    public static final int MARKER_LOW_BYTE = 0x55;

    /** The Constant LENGTH. */
    public static final int LENGTH = 7; // bytes

    // mask for third byte of header
    /** The Constant RESERVED. */
    private static final int RESERVED = 0xF0;

    /** The Constant TYPE. */
    private static final int TYPE = 0x0c;

    /** The Constant COMPRESSED. */
    private static final int COMPRESSED = 0x03;

    /** The marker. */
    private short marker = (short) 0xaa55;

    /** The reserved. */
    private int reserved = 0;

    /** The type. */
    private int type = 0;

    /** The compressed. */
    private int compressed = 0;

    /** The sequence number. */
    private short sequenceNumber = 0;

    /** The size. */
    private short size = 0;

    /** The number of messages. */
    private byte numberOfMessages = 0;

    /** The total size. */
    private static int totalSize = 0;

    /**
     * Instantiates a new frame header.
     *
     * @param type
     *           the type
     */
    public FrameHeader(FrameType type) {
        setType(type);
        if (type == FrameType.CONTROL) {
            setNumberOfMessages((byte) 1);
        }
    }

    /**
     * Instantiates a new frame header.
     *
     * @param header
     *           the header
     * @throws InvalidFrameException
     *            the invalid frame exception
     */
    public FrameHeader(byte[] header) throws InvalidFrameException {
        /*
         * |field-name[bits]|
         *
         * |marker[16]|reserved[4]|type[2]|compressed[2]|seq-number[8]|size[16]|#of
         * -msgs[8]|
         */

        if (header.length != LENGTH) {
            System.out.println("Bad frame header length: " +
                    Hex.encodeHexString(header));
            throw new InvalidFrameException("Bad frame header length");
        }

        ByteBuffer buffer = ByteBuffer.wrap(header);
        marker = buffer.getShort(0);
        int tmp = buffer.get(2);
        reserved = (tmp & RESERVED) >> 4;
        type = (tmp & TYPE) >> 2;
        compressed = (tmp & COMPRESSED);
        sequenceNumber = (short) (buffer.get(3) & 0xff);
        size = buffer.getShort(4);
        numberOfMessages = buffer.get(6);

        if (numberOfMessages < 0) {
            System.out.println("Negative message count: " +
                    Hex.encodeHexString(header));
            throw new InvalidFrameException("Negative message count");
        }
        assert Arrays.equals(header, getBytes());
    }

    /**
     * Gets the body size.
     *
     * @return the body size
     */
    public int getBodySize() {
        return getSize() - LENGTH;
    }

    /**
     * Gets the total size.
     *
     * @return the total size
     */
    public static int getTotalSize() {
        return totalSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String header = "Type: " + getFrameType() +
                System.getProperty("line.separator");
        header += "Sequence Number: " + getSequenceNumber() +
                System.getProperty("line.separator");
        header += "Size: " + getSize() + System.getProperty("line.separator");
        header += "Number of Messages: " + getNumberOfMessages() +
                System.getProperty("line.separator");
        return header;
    }

    /**
     * Gets the bytes.
     *
     * @return the bytes
     */
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
        buffer.putShort(marker);
        byte tmp = (byte) (((reserved << 4) & RESERVED) | ((type << 2) & TYPE) | ((compressed) & COMPRESSED));
        buffer.put(tmp);
        buffer.put((byte) sequenceNumber);
        buffer.putShort(size);
        buffer.put(numberOfMessages);
        return buffer.array();

    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the frame type.
     *
     * @return the frame type
     */
    public FrameType getFrameType() {
        return FrameType.values()[type];
    }

    /**
     * Sets the type.
     *
     * @param type
     *           the new type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Sets the type.
     *
     * @param type
     *           the new type
     */
    public void setType(FrameType type) {
        this.type = type.type();
    }

    /**
     * Gets the sequence number.
     *
     * @return the sequence number
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequence number.
     *
     * @param sequenceNumber
     *           the new sequence number
     */
    public void setSequenceNumber(byte sequenceNumber) {
        this.sequenceNumber = (short) (sequenceNumber & 0xff);
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size.
     *
     * @param size
     *           the new size
     */
    public void setSize(short size) {
        this.size = size;
    }

    /**
     * Gets the number of messages.
     *
     * @return the number of messages
     */
    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    /**
     * Sets the number of messages.
     *
     * @param numberOfMessages
     *           the new number of messages
     */
    public void setNumberOfMessages(byte numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }
}
// UNCLASSIFIED
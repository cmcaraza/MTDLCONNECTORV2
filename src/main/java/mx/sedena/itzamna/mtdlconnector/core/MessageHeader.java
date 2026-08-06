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

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import mx.sedena.itzamna.mtdlconnector.core.LoginResponse;
import mx.sedena.itzamna.mtdlconnector.core.PointerSymbol;
import mx.sedena.itzamna.mtdlconnector.core.UnconnectedSSRData;
import mx.sedena.itzamna.mtdlconnector.core.Undefined;
import mx.sedena.itzamna.mtdlconnector.core.LinkParticipantType;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;

/**
 * Defines an MTDL Message Header
 */
public class MessageHeader implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LENGTH. */
    public static final short LENGTH = 8;

    /** The Constant ACK. */
    private static final short ACK = (short) 0x8000;

    /** The Constant TYPE. */
    private static final short TYPE = 0x7fff;

    /** The sequence number. */
    private short sequenceNumber = 0;

    /** The size. */
    private short size = 0;

    /** The ack. */
    private short ack = 0;

    /** The type. */
    private short type = 0;

    /** The destination. */
    private byte destination = 0;

    /** The source. */
    private byte source = 0;

    /**
     * Instantiates a new message header.
     *
     * @param type
     *           the type
     * @param dest
     *           the dest
     * @param source
     *           the source
     */
    public MessageHeader(MessageType type, LinkParticipantType dest,
                         LinkParticipantType source) {
        setType(type);
        setDestination(dest);
        setSource(source);
        if (!type.isVariableSize()) {
            setSize((short) (type.size() + LENGTH));
        }
    }

    /**
     * Instantiates a new message header.
     *
     * @param type
     *           the type
     * @param dest
     *           the dest
     * @param source
     *           the source
     * @param seq
     *           the seq
     */
    public MessageHeader(MessageType type, LinkParticipantType dest,
                         LinkParticipantType source, short seq) {
        this(type, dest, source);
        setSequenceNumber(seq);
    }

    /**
     * Instantiates a new message header.
     *
     * @param type
     *           the type
     * @param dest
     *           the dest
     * @param source
     *           the source
     * @param ack
     *           the ack
     */
    public MessageHeader(MessageType type, LinkParticipantType dest,
                         LinkParticipantType source, boolean ack) {
        this(type, dest, source);
        setAck(ack);
    }

    /**
     * Instantiates a new message header.
     *
     * @param type
     *           the type
     * @param dest
     *           the dest
     * @param source
     *           the source
     * @param seq
     *           the seq
     * @param ack
     *           the ack
     */
    public MessageHeader(MessageType type, LinkParticipantType dest,
                         LinkParticipantType source, short seq, boolean ack) {
        this(type, dest, source, seq);
        setAck(ack);
    }

    /**
     * Instantiates a new message header.
     *
     * @param header
     *           the header
     * @throws InvalidMessageException
     *            the invalid message exception
     */
    public MessageHeader(byte[] header) throws InvalidMessageException {
        if (header.length != LENGTH) {
            throw new InvalidMessageException("Bad message header length");
        }

        ByteBuffer buffer = ByteBuffer.wrap(header);
        sequenceNumber = buffer.getShort(0);
        size = buffer.getShort(2);
        ack = (short) ((buffer.getShort(4) & ACK) >> 15);
        type = (short) (buffer.getShort(4) & TYPE);
        destination = buffer.get(6);
        source = buffer.get(7);

        MessageType t = getMessageType();
        if (!t.isVariableSize()) {
            if (t.size() != getBodySize()) {
                throw new InvalidMessageException("Bad message size");
            }
        }

        assert Arrays.equals(header, getBytes());
    }

    /**
     * Gets the bytes.
     *
     * @return the bytes
     */
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(LENGTH);
        buffer.putShort(sequenceNumber);
        buffer.putShort(size);
        buffer.putShort((short) (((ack << 15) & ACK) | (type & TYPE)));
        buffer.put(destination);
        buffer.put(source);

        return buffer.array();
    }

    /**
     * Gets the body size.
     *
     * @return the body size
     */
    public int getBodySize() {
        return size - LENGTH;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String header = "Sequence Number: " + getSequenceNumber() +
                System.getProperty("line.separator");
        header += "Size: " + getSize() + System.getProperty("line.separator");
        header += "Ack: " + isAckRequired() +
                System.getProperty("line.separator");
        header += "Type: " + getMessageType() +
                System.getProperty("line.separator");
        header += "Destination: " + getDestinationType() +
                System.getProperty("line.separator");
        header += "Source: " + getSourceType() +
                System.getProperty("line.separator");
        return header;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public Message getMessage() {
        return getMessage(null);
    }

    /**
     * Gets the message.
     *
     * @param body
     *           the body
     * @return the message
     */
    public Message getMessage(byte[] body) {
        switch (getMessageType()) {
            case LOGIN:
                return new Login(this, body);
            case LOGIN_RESPONSE:
                return new LoginResponse(this, body);
            case LINK_STATUS_RESPONSE:
                return new LinkStatusResponse(this, body);
            case ACK:
                return new Ack(this, body);
            case AIRCRAFT_POSITION:
                return new AircraftPosition(this, body);
            case AIRCRAFT_STATUS:
                return new AircraftStatus(this, body);
            case AIR_TRACK_DATA:
                return new AirTrackData(this, body);
            case CONNECTED_SSR_DATA:
                return new ConnectedSSRData(this, body);
            case UNCONNECTED_SSR_DATA:
                return new UnconnectedSSRData(this, body);
            case C4I_TRACK_DATA:
                return new C4ITrackData(this, body);
            case POINTER_SYMBOL:
                return new PointerSymbol(this, body);
            case C2_FREE_TEXT:
            case COMINT_FREE_TEXT:
                return new FreeText(this, body);
            default:
                return new Undefined(this, body);
        }
    }

    /**
     * Gets the sequence number.
     *
     * @return the sequence number
     */
    public int getSequenceNumber() {
        return (sequenceNumber & 0xffff);
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
     * Gets the size.
     *
     * @return the size
     */
    public short getSize() {
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
     * Gets the ack.
     *
     * @return the ack
     */
    public short getAck() {
        return ack;
    }

    /**
     * Checks if is ack required.
     *
     * @return true, if is ack required
     */
    public boolean isAckRequired() {
        return ack == 0 ? false : true;
    }

    /**
     * Sets the ack.
     *
     * @param ack
     *           the new ack
     */
    public void setAck(short ack) {
        this.ack = ack;
    }

    /**
     * Sets the ack.
     *
     * @param ack
     *           the new ack
     */
    public void setAck(boolean ack) {
        this.ack = (short) (ack ? 1 : 0);
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public short getType() {
        return type;
    }

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public MessageType getMessageType() {
        return MessageType.get(getType());
    }

    /**
     * Sets the type.
     *
     * @param type
     *           the new type
     */
    public void setType(short type) {
        this.type = MessageType.get(type).number();
    }

    /**
     * Sets the type.
     *
     * @param type
     *           the new type
     */
    public void setType(MessageType type) {
        this.type = type.number();
    }

    /**
     * Gets the destination.
     *
     * @return the destination
     */
    public byte getDestination() {
        return destination;
    }

    /**
     * Gets the destination type.
     *
     * @return the destination type
     */
    public LinkParticipantType getDestinationType() {
        return LinkParticipantType.get(destination);
    }

    /**
     * Sets the destination.
     *
     * @param destination
     *           the new destination
     */
    public void setDestination(byte destination) {
        this.destination = LinkParticipantType.get(destination).identifier();
    }

    /**
     * Sets the destination.
     *
     * @param destination
     *           the new destination
     */
    public void setDestination(LinkParticipantType destination) {
        this.destination = destination.identifier();
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public byte getSource() {
        return source;
    }

    /**
     * Gets the source type.
     *
     * @return the source type
     */
    public LinkParticipantType getSourceType() {
        return LinkParticipantType.get(source);
    }

    /**
     * Sets the source.
     *
     * @param source
     *           the new source
     */
    public void setSource(byte source) {
        this.source = LinkParticipantType.get(source).identifier();
    }

    /**
     * Sets the source.
     *
     * @param source
     *           the new source
     */
    public void setSource(LinkParticipantType source) {
        this.source = source.identifier();
    }
}
// UNCLASSIFIED
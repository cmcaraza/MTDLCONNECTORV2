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

import java.awt.Color;
import java.nio.ByteBuffer;

/**
 * Defines an MTDL Air Track Data message.
 */
public class AirTrackData extends AbstractTrackPosition implements DataMessage,
         LatLong, TrackNumber {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant IDENTITY_LENGTH. */
    private static final int IDENTITY_LENGTH = 9;

    /** The track number. */
    private String trackNumber = "XXXXXX";

    /** The trk class. */
    private byte trkClass = 0;

    /** The identity. */
    private byte[] identity = new byte[IDENTITY_LENGTH];

    /**
     * Instantiates a new air track data.
     *
     * @param header
     *           the header
     */
    public AirTrackData(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new air track data.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public AirTrackData(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.AIR_TRACK_DATA.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);
            StringBuffer trackNumberBuffer = new StringBuffer();
            trackNumberBuffer.append((char) buffer.get(0));
            trackNumberBuffer.append((char) buffer.get(1));
            trackNumberBuffer.append((char) buffer.get(2));
            trackNumberBuffer.append((char) buffer.get(3));
            trackNumberBuffer.append((char) buffer.get(4));
            trackNumberBuffer.append((char) buffer.get(5));
            setTrackNumber(trackNumberBuffer.toString());
            setLatitude(buffer.getInt(6));
            setLongitude(buffer.getInt(10));
            setTime(buffer.getInt(14));
            setSpeed(buffer.getShort(18));
            setAltitude(buffer.getShort(20));
            setAngle(buffer.get(22));
            setTrkClass(buffer.get(23));
            System.arraycopy(body, 24, identity, 0, IDENTITY_LENGTH);

            test(body);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.AbstractTrackPosition#getTrackInfo()
     */
    @Override
    public String getTrackInfo() {
        String block = "Track Number: " + getTrackNumber() +
                System.getProperty("line.separator");
        block += super.getTrackInfo();
        return block;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.Message#toString()
     */
    @Override
    public String toString() {
        String message = super.toString();
        message += getTrackInfo();
        message += "Track Class: " + getTrackClassType() +
                System.getProperty("line.separator");
        message += "Track Identity: " + new String(getIdentity()) +
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
        buffer.put((byte) getTrackNumber().charAt(0));
        buffer.put((byte) getTrackNumber().charAt(1));
        buffer.put((byte) getTrackNumber().charAt(2));
        buffer.put((byte) getTrackNumber().charAt(3));
        buffer.put((byte) getTrackNumber().charAt(4));
        buffer.put((byte) getTrackNumber().charAt(5));
        buffer.putInt(getLatitude());
        buffer.putInt(getLongitude());
        buffer.putInt((int) getTime());
        buffer.putShort(getSpeed());
        buffer.putShort(getAltitude());
        buffer.put(getAngle());
        buffer.put(getTrkClass());
        buffer.put(getIdentity());
        return buffer.array();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.TrackNumber#getTrackNumber()
     */
    @Override
    public String getTrackNumber() {
        return trackNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.TrackNumber#setTrackNumber(java.lang.String)
     */
    @Override
    public void setTrackNumber(String trackNumber) {
        if (trackNumber.length() != 6) {
            throw new IllegalArgumentException(
                    "Track Number must be exactly 6 alphanumerics.");
        }
        this.trackNumber = trackNumber;
    }

    /**
     * Gets the trk class.
     *
     * @return the trk class
     */
    public byte getTrkClass() {
        return trkClass;
    }

    /**
     * Gets the track class type.
     *
     * @return the track class type
     */
    public EricssonTrackClassType getTrackClassType() {
        return EricssonTrackClassType.get(getTrkClass());
    }

    /**
     * Sets the trk class.
     *
     * @param trkClass
     *           the new trk class
     */
    public void setTrkClass(byte trkClass) {
        this.trkClass = EricssonTrackClassType.get(trkClass).type();
    }

    /**
     * Sets the track class.
     *
     * @param type
     *           the new track class
     */
    public void setTrackClass(EricssonTrackClassType type) {
        this.trkClass = type.type();
    }

    /**
     * Gets the identity.
     *
     * @return the identity
     */
    public byte[] getIdentity() {
        return identity;
    }

    /**
     * Sets the identity.
     *
     * @param identity
     *           the new identity
     */
    public void setIdentity(byte[] identity) {
        System.arraycopy(identity, 0, this.identity, 0, identity.length);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.gui.SIDItem#getPosition()
     */

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.gui.SIDItem#getInfo()
     */

}
// UNCLASSIFIED

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
 * Defines an MTDL Aircraft Position message.
 */
public class AircraftPosition extends AbstractTrackPosition implements
        DataMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The aircraft id. */
    private String aircraftId = "XXX";

    /**
     * Instantiates a new aircraft position.
     *
     * @param header
     *           the header
     */
    public AircraftPosition(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new aircraft position.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public AircraftPosition(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.AIRCRAFT_POSITION.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            StringBuffer aircraftIdBuffer = new StringBuffer();
            aircraftIdBuffer.append((char) buffer.get(0));
            aircraftIdBuffer.append((char) buffer.get(1));
            aircraftIdBuffer.append((char) buffer.get(2));
            setAircraftId(aircraftIdBuffer.toString());
            setAngle(buffer.get(3));
            setLatitude(buffer.getInt(4));
            setLongitude(buffer.getInt(8));
            setSpeed(buffer.getShort(12));
            setAltitude(buffer.getShort(14));
            setTime(buffer.getInt(16));

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
        message += getTrackInfo();
        return message;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.AbstractTrackPosition#getTrackInfo()
     */
    @Override
    public String getTrackInfo() {
        String block = "Aircraft ID: " + getAircraftId().toString() +
                System.getProperty("line.separator");
        block += super.getTrackInfo();
        return block;
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
        buffer.put((byte) getAircraftId().charAt(0));
        buffer.put((byte) getAircraftId().charAt(1));
        buffer.put((byte) getAircraftId().charAt(2));
        buffer.put(getAngle());
        buffer.putInt(getLatitude());
        buffer.putInt(getLongitude());
        buffer.putShort(getSpeed());
        buffer.putShort(getAltitude());
        buffer.putInt((int) getTime());
        return buffer.array();
    }

    /**
     * Gets the aircraft id.
     *
     * @return the aircraft id
     */
    public String getAircraftId() {
        return aircraftId;
    }

    /**
     * Sets the aircraft id.
     *
     * @param aircraftId
     *           the new aircraft id
     */
    public void setAircraftId(String aircraftId) {
        assert aircraftId.length() == 3;
        this.aircraftId = aircraftId;
    }



}
// UNCLASSIFIED
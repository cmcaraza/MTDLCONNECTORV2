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
import java.util.Date;

import mx.sedena.itzamna.mtdlconnector.core.DataMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;

/**
 * Defines an MTDL Aircraft Status message.
 */
public class AircraftStatus extends Message implements DataMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The time. */
    private long time = 0;

    /** The status. */
    private short status = 0;

    /**
     * Instantiates a new aircraft status.
     *
     * @param header
     *           the header
     */
    public AircraftStatus(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new aircraft status.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public AircraftStatus(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.AIRCRAFT_STATUS.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            setTime(buffer.getInt(0));
            setStatus(buffer.getShort(4));

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
        message += "Time: " + new Date(getTime() * 1000).toString() +
                System.getProperty("line.separator");
        message += "Status: " + getStatus() +
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
        buffer.putInt((int) getTime());
        buffer.putShort(getStatus());
        return buffer.array();
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the time.
     *
     * @param time
     *           the new time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public short getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *           the new status
     */
    public void setStatus(short status) {
        this.status = status;
    }

}
// UNCLASSIFIED
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
 * Defines an MTDL Pointer Symbol message.
 */
public class PointerSymbol extends Message implements DataMessage,
        LatLong {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The latitude. */
    private int latitude = 0;

    /** The longitude. */
    private int longitude = 0;

    /**
     * Instantiates a new pointer symbol.
     *
     * @param header
     *           the header
     */
    public PointerSymbol(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new pointer symbol.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public PointerSymbol(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.POINTER_SYMBOL.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            setLatitude(buffer.getInt(0));
            setLongitude(buffer.getInt(4));

            test(body);
        }
    }

    // =============================================
    // Native MTDL units; possibly BAMS
    // http://www.globalspec.com/reference/14722/160210/Chapter-7-5-3-Binary-Angular-Measure
    // =============================================
    /**
     * Gets the latitude.
     *
     * @return the latitude
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude
     *           the new latitude
     */
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude.
     *
     * @return the longitude
     */
    public int getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude
     *           the new longitude
     */
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    // =============================================
    // Conversions
    // =============================================
    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.LatLong#getLatitudeDegrees()
     */
    @Override
    public double getLatitudeDegrees() {
        return AbstractTrackPosition.fromB31(latitude);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.LatLong#setLatitudeDegrees(double)
     */
    @Override
    public void setLatitudeDegrees(double latitude) {
        this.latitude = AbstractTrackPosition.toB31(latitude);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.LatLong#getLongitudeDegrees()
     */
    @Override
    public double getLongitudeDegrees() {
        return AbstractTrackPosition.fromB31(longitude);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.LatLong#setLongitudeDegrees(double)
     */
    @Override
    public void setLongitudeDegrees(double longitude) {
        this.longitude = AbstractTrackPosition.toB31(longitude);
    }

    // =============================================

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.Message#toString()
     */


    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.Message#getBytes()
     */
    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(header.getSize());
        buffer.put(header.getBytes());
        buffer.putInt(getLatitude());
        buffer.putInt(getLongitude());
        return buffer.array();
    }


}
// UNCLASSIFIED

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
 * Defines an MTDL Unconnected SSR Data message.
 */
public class UnconnectedSSRData extends AbstractTrackPosition implements
        DataMessage, LatLong, TrackNumber {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The track number. */
    private String trackNumber = "XXXXXX";

    /** The code m1. */
    private short codeM1 = 0;

    /** The code m2. */
    private short codeM2 = 0;

    /** The code m3. */
    private short codeM3 = 0;

    /** The code mc. */
    private short codeMc = 0;

    /**
     * Instantiates a new unconnected ssr data.
     *
     * @param header
     *           the header
     */
    public UnconnectedSSRData(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new unconnected ssr data.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public UnconnectedSSRData(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.UNCONNECTED_SSR_DATA.size();

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
            setCodeM1(buffer.getShort(22));
            setCodeM2(buffer.getShort(24));
            setCodeM3(buffer.getShort(26));
            setCodeMc(buffer.getShort(28));
            setAngle(buffer.get(30));

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
        short m1 = getCodeM1();
        m1 = (short) (((m1 << 2) & 0x0070) | (m1 & 0x0003));
        message += "Mode 1: " + String.format("%x", m1) +
                System.getProperty("line.separator");
        message += "Mode 2: " + String.format("%o", getCodeM2()) +
                System.getProperty("line.separator");
        message += "Mode 3: " + String.format("%o", getCodeM3()) +
                System.getProperty("line.separator");
        message += "Mode c: " + getCodeMc() + " (feet)" +
                System.getProperty("line.separator");
        return message;
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
        buffer.putShort(getCodeM1());
        buffer.putShort(getCodeM2());
        buffer.putShort(getCodeM3());
        buffer.putShort(getCodeMc());
        buffer.put(getAngle());
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
        assert trackNumber.length() == 6;
        this.trackNumber = trackNumber;
    }

    /**
     * Gets the code m1.
     *
     * @return the code m1
     */
    public short getCodeM1() {
        return codeM1;
    }

    /**
     * Sets the code m1.
     *
     * @param codeM1
     *           the new code m1
     */
    public void setCodeM1(short codeM1) {
        this.codeM1 = codeM1;
    }

    /**
     * Gets the code m2.
     *
     * @return the code m2
     */
    public short getCodeM2() {
        return codeM2;
    }

    /**
     * Sets the code m2.
     *
     * @param codeM2
     *           the new code m2
     */
    public void setCodeM2(short codeM2) {
        this.codeM2 = codeM2;
    }

    /**
     * Gets the code m3.
     *
     * @return the code m3
     */
    public short getCodeM3() {
        return codeM3;
    }

    /**
     * Sets the code m3.
     *
     * @param codeM3
     *           the new code m3
     */
    public void setCodeM3(short codeM3) {
        this.codeM3 = codeM3;
    }

    /**
     * Gets the code mc.
     *
     * @return the code mc
     */
    public short getCodeMc() {
        return codeMc;
    }

    /**
     * Sets the code mc.
     *
     * @param codeMc
     *           the new code mc
     */
    public void setCodeMc(short codeMc) {
        this.codeMc = codeMc;
    }





}
// UNCLASSIFIED
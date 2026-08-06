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
 * Defines an MTDL C4I Track Data message.
 */
public class C4ITrackData extends AbstractTrackPosition implements DataMessage,
         LatLong, TrackNumber {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant IDENTITY_LENGTH. */
    private static final int IDENTITY_LENGTH = 9;

    /** The aew track number. */
    private String aewTrackNumber = "XXXXXX";

    /** The c4i track number. */
    private String c4iTrackNumber = "XXX";

    /** The code m1. */
    private short codeM1 = 0;

    /** The code m2. */
    private short codeM2 = 0;

    /** The code m3. */
    private short codeM3 = 0;

    /** The code mc. */
    private short codeMc = 0;

    /** The trk class. */
    private byte trkClass = 0;

    /** The identity. */
    private byte[] identity = new byte[IDENTITY_LENGTH];

    /**
     * Instantiates a new c4 i track data.
     *
     * @param header
     *           the header
     */
    public C4ITrackData(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new c4 i track data.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public C4ITrackData(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {

            assert body.length == MessageType.C4I_TRACK_DATA.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);
            StringBuffer aewTrackNumberBuffer = new StringBuffer();
            aewTrackNumberBuffer.append((char) buffer.get(0));
            aewTrackNumberBuffer.append((char) buffer.get(1));
            aewTrackNumberBuffer.append((char) buffer.get(2));
            aewTrackNumberBuffer.append((char) buffer.get(3));
            aewTrackNumberBuffer.append((char) buffer.get(4));
            aewTrackNumberBuffer.append((char) buffer.get(5));
            setAewTrackNumber(aewTrackNumberBuffer.toString());
            StringBuffer c4iTrackNumberBuffer = new StringBuffer();
            c4iTrackNumberBuffer.append((char) buffer.get(6));
            c4iTrackNumberBuffer.append((char) buffer.get(7));
            c4iTrackNumberBuffer.append((char) buffer.get(8));
            setC4itrackNumber(c4iTrackNumberBuffer.toString());
            setAngle(buffer.get(9));
            setLatitude(buffer.getInt(10));
            setLongitude(buffer.getInt(14));
            setTime(buffer.getInt(18));
            setSpeed(buffer.getShort(22));
            setAltitude(buffer.getShort(24));
            setCodeM1(buffer.getShort(26));
            setCodeM2(buffer.getShort(28));
            setCodeM3(buffer.getShort(30));
            this.codeMc = buffer.getShort(32);
            setTrkClass(buffer.get(34));
            System.arraycopy(body, 35, identity, 0, IDENTITY_LENGTH);
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
        message += "Mode c: " + getCodeMc() +
                System.getProperty("line.separator");
        message += "Track Class: " + getTrackClassType() +
                System.getProperty("line.separator");
        message += "Track Identity: " + new String(getIdentity()) +
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
        String block = "AEW Track Number: " + getAewTrackNumber().toString() +
                System.getProperty("line.separator");
        block += "C4I Track Number: " + getC4iTrackNumber().toString() +
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
        buffer.put((byte) getAewTrackNumber().charAt(0));
        buffer.put((byte) getAewTrackNumber().charAt(1));
        buffer.put((byte) getAewTrackNumber().charAt(2));
        buffer.put((byte) getAewTrackNumber().charAt(3));
        buffer.put((byte) getAewTrackNumber().charAt(4));
        buffer.put((byte) getAewTrackNumber().charAt(5));
        buffer.put((byte) getC4iTrackNumber().charAt(0));
        buffer.put((byte) getC4iTrackNumber().charAt(1));
        buffer.put((byte) getC4iTrackNumber().charAt(2));
        buffer.put(getAngle());
        buffer.putInt(getLatitude());
        buffer.putInt(getLongitude());
        buffer.putInt((int) getTime());
        buffer.putShort(getSpeed());
        buffer.putShort(getAltitude());
        buffer.putShort(getCodeM1());
        buffer.putShort(getCodeM2());
        buffer.putShort(getCodeM3());
        buffer.putShort(codeMc);
        buffer.put(getTrkClass());
        buffer.put(getIdentity());
        return buffer.array();
    }

    /**
     * Gets the aew track number.
     *
     * @return the aew track number
     */
    public String getAewTrackNumber() {
        return aewTrackNumber;
    }

    /**
     * Sets the aew track number.
     *
     * @param aewTrackNumber
     *           the new aew track number
     */
    public void setAewTrackNumber(String aewTrackNumber) {
        assert aewTrackNumber.length() == 6;
        this.aewTrackNumber = aewTrackNumber;
    }

    /**
     * Gets the c4i track number.
     *
     * @return the c4i track number
     */
    public String getC4iTrackNumber() {
        return c4iTrackNumber;
    }

    /**
     * Sets the c4itrack number.
     *
     * @param c4iTrackNumber
     *           the new c4itrack number
     */
    public void setC4itrackNumber(String c4iTrackNumber) {
        assert c4iTrackNumber.length() == 3;
        this.c4iTrackNumber = c4iTrackNumber;
        //this.c4iTrackNumber.charAt(0) = '2';

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
    public int getCodeMc() {
        return ModeC.decode(codeMc == 0x0fff ? codeMc : codeMc * 100);
    }

    /**
     * Sets the code mc.
     *
     * @param codeMc
     *           the new code mc
     */
    public void setCodeMc(short codeMc) {
        this.codeMc = (short) ModeC.encode(codeMc);
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
    public String getTrackClassType() {
        if (header.getDestinationType() == LinkParticipantType.AEW) {
            return EricssonTrackClassType.get(getTrkClass()).toString();
        }
        else {
            return L3TrackClassType.get(getTrkClass()).toString();
        }
    }

    /**
     * Sets the trk class.
     *
     * @param trkClass
     *           the new trk class
     */
    public void setTrkClass(byte trkClass) {
        this.trkClass = trkClass;
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
     * Sets the track class.
     *
     * @param type
     *           the new track class
     */
    public void setTrackClass(L3TrackClassType type) {
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
        this.identity = identity;
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

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.gui.SIDItem#getColor()
     */

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.TrackNumber#getTrackNumber()
     */
    @Override
    public String getTrackNumber() {
        return getAewTrackNumber();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.msg.data.TrackNumber#setTrackNumber(java.lang.String)
     */
    @Override
    public void setTrackNumber(String trackNumber) {
        setAewTrackNumber(trackNumber);

    }
}
// UNCLASSIFIED
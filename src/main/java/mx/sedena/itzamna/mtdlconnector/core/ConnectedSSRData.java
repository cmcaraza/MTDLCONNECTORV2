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
 * Defines an MTDL Connected SSR Data message.
 */
public class ConnectedSSRData extends Message implements DataMessage {

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

    /** The spares. */
    private byte[] spares = new byte[3];

    /**
     * Instantiates a new connected ssr data.
     *
     * @param header
     *           the header
     */
    public ConnectedSSRData(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new connected ssr data.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public ConnectedSSRData(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.CONNECTED_SSR_DATA.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            StringBuffer trackNumberBuffer = new StringBuffer();
            trackNumberBuffer.append((char) buffer.get(0));
            trackNumberBuffer.append((char) buffer.get(1));
            trackNumberBuffer.append((char) buffer.get(2));
            trackNumberBuffer.append((char) buffer.get(3));
            trackNumberBuffer.append((char) buffer.get(4));
            trackNumberBuffer.append((char) buffer.get(5));
            setTrackNumber(trackNumberBuffer.toString());
            spares[0] = buffer.get(6);
            spares[1] = buffer.get(7);
            spares[2] = buffer.get(8);
            setCodeM1(buffer.getShort(9));
            setCodeM2(buffer.getShort(11));
            setCodeM3(buffer.getShort(13));
            setCodeMc(buffer.getShort(15));

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
        message += "Track Number: " + getTrackNumber().toString() +
                System.getProperty("line.separator");
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
        buffer.put(spares[0]);
        buffer.put(spares[1]);
        buffer.put(spares[2]);
        buffer.putShort(getCodeM1());
        buffer.putShort(getCodeM2());
        buffer.putShort(getCodeM3());
        buffer.putShort(getCodeMc());

        return buffer.array();
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

    /**
     * Gets the track number.
     *
     * @return the track number
     */
    public String getTrackNumber() {
        return trackNumber;
    }

    /**
     * Sets the track number.
     *
     * @param trackNumber
     *           the new track number
     */
    public void setTrackNumber(String trackNumber) {
        assert trackNumber.length() == 6;
        this.trackNumber = trackNumber;
    }

}
// UNCLASSIFIED
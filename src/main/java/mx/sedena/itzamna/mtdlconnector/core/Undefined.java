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

import org.apache.commons.codec.binary.Hex;

import mx.sedena.itzamna.mtdlconnector.core.ControlMessage;
import mx.sedena.itzamna.mtdlconnector.core.DataMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;

/**
 * Defines a message for handling unknown but correctly formated MTDL data.
 */
public class Undefined extends Message implements ControlMessage, DataMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The body. */
    private byte[] body = new byte[0]; // hack; avoid null pointer complaints

    /**
     * Instantiates a new undefined.
     *
     * @param header
     *           the header
     */
    public Undefined(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new undefined.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public Undefined(MessageHeader header, byte[] body) {
        super(header);
        if (body != null) {
            this.body = body;
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
        message += Hex.encodeHexString(getBytes());
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
        buffer.put(body);
        return buffer.array();
    }
}
// UNCLASSIFIED
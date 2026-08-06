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

import mx.sedena.itzamna.mtdlconnector.core.FrameMessage;

/**
 * Defines an MTDL Message
 */
@SuppressWarnings("serial")
public abstract class Message implements FrameMessage, Serializable {

    /** The header. */
    protected MessageHeader header = null;

    /**
     * Instantiates a new message.
     *
     * @param header
     *           the header
     */
    public Message(MessageHeader header) {
        this.header = header;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return header.toString();
    }

    /**
     * In the constructor of a child class, pass this the body of a received
     * message to confirm we translate it correctly.
     *
     * @param body
     *           the body
     */
    protected void test(byte[] body) {
        ByteBuffer message = ByteBuffer.allocate(MessageHeader.LENGTH +
                body.length);
        message.put(header.getBytes());
        message.put(body);
        assert Arrays.equals(message.array(), getBytes());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ngc.mtdl.FrameMessage#getMessage()
     */
    @Override
    public Message getMessage() {
        return this;
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    public MessageHeader getHeader() {
        return header;
    }

    /**
     * Gets the bytes.
     *
     * @return an array of bytes in native MTDL format, including the header
     */
    public abstract byte[] getBytes();
}
// UNCLASSIFIED
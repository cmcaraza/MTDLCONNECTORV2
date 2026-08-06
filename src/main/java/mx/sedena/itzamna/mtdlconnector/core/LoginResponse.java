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

import mx.sedena.itzamna.mtdlconnector.core.ControlMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.MessageType;

/**
 * Defines an MTDL Login Response message.
 */
public class LoginResponse extends Message implements ControlMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The password ok. */
    private byte passwordOk = 0;

    /**
     * Instantiates a new login response.
     *
     * @param header
     *           the header
     */
    public LoginResponse(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new login response.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public LoginResponse(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.LOGIN_RESPONSE.size();

            ByteBuffer buffer = ByteBuffer.wrap(body);

            setPasswordOk(buffer.get());
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
        message += "One: " + getPasswordOk() +
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
        buffer.put(getPasswordOk());
        return buffer.array();
    }

    /**
     * Gets the password ok.
     *
     * @return the password ok
     */
    public byte getPasswordOk() {
        return passwordOk;
    }

    /**
     * Sets the password ok.
     *
     * @param passwordOk
     *           the new password ok
     */
    public void setPasswordOk(byte passwordOk) {
        this.passwordOk = passwordOk;
    }
}
// UNCLASSIFIED
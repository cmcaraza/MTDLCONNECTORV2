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
 * Defines an MTDL Login message.
 */
public class Login extends Message implements ControlMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The data. */
    private String data = "";

    /**
     * Instantiates a new login.
     *
     * @param header
     *           the header
     */
    public Login(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new login.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public Login(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            assert body.length == MessageType.LOGIN.size();
            setPassword(new String(body));
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
        message += "Login: " + getData() + System.getProperty("line.separator");
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
        buffer.put(getData().getBytes());
        return buffer.array();
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return data.trim();
    }

    /**
     * Sets the password.
     *
     * @param data
     *           the new password
     */
    public void setPassword(String data) {
        byte[] tmp = new byte[MessageType.LOGIN.size()];
        System.arraycopy(data.getBytes(), 0, tmp, 0, data.getBytes().length);
        this.data = new String(tmp);
    }
}
// UNCLASSIFIED
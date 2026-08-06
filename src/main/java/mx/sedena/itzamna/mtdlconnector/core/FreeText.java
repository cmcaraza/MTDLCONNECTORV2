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
import java.nio.charset.Charset;

import mx.sedena.itzamna.mtdlconnector.core.DataMessage;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;

/**
 * Defines an MTDL C2 Free Text or Comint Free Text message.
 */
public class FreeText extends Message implements DataMessage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The text. */
    private String text = "";

    /**
     * Instantiates a new free text.
     *
     * @param header
     *           the header
     */
    public FreeText(MessageHeader header) {
        this(header, null);
    }

    /**
     * Instantiates a new free text.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     */
    public FreeText(MessageHeader header, byte[] body) {
        super(header);

        if (body != null) {
            int blen = body.length;
            int[] textInts = new int[blen];
            for (short a = 0; a < blen; a++){
                textInts[a] = (int)body[a] + (body[a] < 0 ? 256 : 0);
            }
            this.text = new String(textInts, 0, blen);
            header.setSize((short) (blen + MessageHeader.LENGTH));
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
        message += "Message: " + getText() +
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
        byte[] textByte = new byte[text.length()];
        for(int a = 0; a < textByte.length; a++){
            textByte[a]=(byte)text.charAt(a);
        }
        buffer.put(textByte);
        return buffer.array();
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text.
     *
     * @param text
     *           the new text
     */
    public void setText(String text) {
        byte[] textBytes = text.getBytes(Charset.forName("ISO-8859-1"));
        int textSize = textBytes.length;
        int[] textNums = new int[textSize];
        for (int a = 0; a < textSize; a++){
            textNums[a] = (int)textBytes[a] + (textBytes[a] < 0 ? 256 :0);
        }
        this.text = new String(textNums, 0, textSize);
        header.setSize((short) (textSize + MessageHeader.LENGTH));
    }
}
// UNCLASSIFIED
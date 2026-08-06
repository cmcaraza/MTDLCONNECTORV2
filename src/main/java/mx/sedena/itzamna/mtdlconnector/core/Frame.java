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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.log4j.Logger;

import mx.sedena.itzamna.mtdlconnector.core.ControlMessage;
import mx.sedena.itzamna.mtdlconnector.core.DataMessage;
import mx.sedena.itzamna.mtdlconnector.core.InvalidMessageException;
import mx.sedena.itzamna.mtdlconnector.core.Message;
import mx.sedena.itzamna.mtdlconnector.core.MessageHeader;
import mx.sedena.itzamna.mtdlconnector.core.FrameType;

/**
 * Defines an MTDL Frame
 */
public class Frame {

    /** The log. */
    private static Logger log = Logger.getLogger(Frame.class);

    /** The header. */
    FrameHeader header = null;

    /** The messages. */
    private Message[] messages = null;

    /*
     * Constructors for outgoing Frames
     */
    /**
     * Instantiates a new frame.
     *
     * @param message
     *           the message
     * @param seq
     *           the seq
     */
    public Frame(ControlMessage message, byte seq) {
        messages = new Message[] { message.getMessage() };
        header = new FrameHeader(FrameType.CONTROL);
        header.setSize((short) (messages[0].getBytes().length + FrameHeader.LENGTH));
        header.setSequenceNumber(seq);
        assert test();
    }

    /**
     * Instantiates a new frame.
     *
     * @param message
     *           the message
     * @param seq
     *           the seq
     */
    public Frame(DataMessage message, byte seq) {
        this(new DataMessage[] { message }, seq);
    }

    /**
     * Instantiates a new frame.
     *
     * @param messages
     *           the messages
     * @param seq
     *           the seq
     */
    public Frame(DataMessage[] messages, byte seq) {
        int size = 0;
        this.messages = new Message[messages.length];
        for (int i = 0; i < messages.length; i++) {
            this.messages[i] = messages[i].getMessage();
            size += messages[i].getMessage().getBytes().length;
        }
        header = new FrameHeader(FrameType.DATA);
        header.setSize((short) (size + FrameHeader.LENGTH));
        header.setNumberOfMessages((byte) messages.length);
        header.setSequenceNumber(seq);
        assert test();
    }

    /*
     * Constructor for incoming Frames
     */
    /**
     * Instantiates a new frame.
     *
     * @param header
     *           the header
     * @param body
     *           the body
     * @throws InvalidFrameException
     *            the invalid frame exception
     * @throws IOException
     *            Signals that an I/O exception has occurred.
     * @throws InvalidMessageException
     *            the invalid message exception
     */
    public Frame(FrameHeader header, byte[] body) throws InvalidFrameException,
            IOException, InvalidMessageException {
        this.header = header;

        ByteArrayInputStream bais = new ByteArrayInputStream(body);

        // AEW falsely reports control frames as having no messages
        int numMessages = (header.getNumberOfMessages() == 0 ? 1 : header
                .getNumberOfMessages());
        messages = new Message[numMessages];
        int size = 0;
        for (int i = 0; i < messages.length; i++) {
            byte[] messageHeader = new byte[MessageHeader.LENGTH];
            try {
                bais.read(messageHeader);
                MessageHeader mh = new MessageHeader(messageHeader);
                byte[] messageBody = new byte[mh.getBodySize()];
                bais.read(messageBody);
                messages[i] = mh.getMessage(messageBody);
                size += mh.getSize();
            }
            catch (IOException e) {
                throw e;
            }
            catch (InvalidMessageException e) {
                throw e;
            }
        }
        if (size != header.getBodySize()) {
            throw new InvalidFrameException(
                    "Reported payload size not equal to actual payload size");
        }
    }

    /**
     * Gets the frame.
     *
     * @param frameBytes
     *           the frame bytes
     * @return the frame
     * @throws InvalidFrameException
     *            the invalid frame exception
     * @throws IOException
     *            Signals that an I/O exception has occurred.
     * @throws InvalidMessageException
     *            the invalid message exception
     */
    public static Frame getFrame(byte[] frameBytes)
            throws InvalidFrameException, IOException, InvalidMessageException {
        byte[] headerBytes = new byte[FrameHeader.LENGTH];
        byte[] bodyBytes = new byte[frameBytes.length - FrameHeader.LENGTH];

        System.arraycopy(frameBytes, 0, headerBytes, 0, headerBytes.length);
        System.arraycopy(frameBytes, FrameHeader.LENGTH, bodyBytes, 0,
                bodyBytes.length);
        return new Frame(new FrameHeader(headerBytes), bodyBytes);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String frame = header.toString();
        for (Message m : messages) {
            frame += m.toString();
        }
        return frame;
    }

    /**
     * Gets the bytes.
     *
     * @return the bytes
     */
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(header.getSize());
        buffer.put(header.getBytes());
        for (Message m : messages) {
            buffer.put(m.getBytes());
        }
        return buffer.array();
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public Message[] getMessages() {
        return messages;
    }

    /**
     * Test.
     *
     * @return true, if successful
     */
    private boolean test() {
        try {
            byte[] orig = this.getBytes();
            FrameHeader header = new FrameHeader(Arrays.copyOfRange(orig, 0, 7));
            byte[] copy = new Frame(header, Arrays.copyOfRange(orig, 7,
                    orig.length)).getBytes();
            return Arrays.equals(orig, copy);
        }
        catch (InvalidFrameException e) {
            log.debug(e);
        }
        catch (IOException e) {
            log.debug(e);
        }
        catch (InvalidMessageException e) {
            log.debug(e);
        }
        return false;
    }
}
// UNCLASSIFIED
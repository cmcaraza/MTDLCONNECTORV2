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

/**
 * The Enum MessageType.
 */
public enum MessageType {

    UNDEFINED(0, -1, Color.RED),
    LOGIN(1, 8, Color.PINK),
    LOGIN_RESPONSE(2, 1, Color.PINK),
    C4I_HEARTBEAT(3, 0, Color.GRAY),
    AEW_HEARTBEAT(4, 0, Color.GRAY),
    LINK_STATUS_REQUEST(9, 0, Color.LIGHT_GRAY),
    LINK_STATUS_RESPONSE(10, 8, Color.LIGHT_GRAY),
    ACK(10001, 5, Color.LIGHT_GRAY),
    AIRCRAFT_POSITION(10002, 20, Color.GREEN),
    AIRCRAFT_STATUS(10003, 6, Color.GREEN),
    AIR_TRACK_DATA(10004, 33, Color.CYAN),
    CONNECTED_SSR_DATA(10006, 17, Color.MAGENTA),
    UNCONNECTED_SSR_DATA(10007, 31, Color.MAGENTA),
    C4I_TRACK_DATA(10008, 44, Color.BLUE),
    POINTER_SYMBOL(10009, 8, Color.YELLOW),
    C2_FREE_TEXT(10010, -1, Color.ORANGE),
    COMINT_FREE_TEXT(10028, -1, Color.ORANGE);

    /** The number. */
    private final int number;

    /** The size. */
    private final int size;

    /** The color. */
    private final Color color;

    /**
     * Instantiates a new message type.
     *
     * @param number
     *           the number
     * @param size
     *           the size
     * @param color
     *           the color
     */
    private MessageType(int number, int size, Color color) {
        this.number = number;
        this.size = size;
        this.color = color;
    }

    /**
     * Number.
     *
     * @return the short
     */
    public short number() {
        return (short) number;
    }

    /**
     * Size.
     *
     * @return the int
     */
    public int size() {
        return size;
    }

    /**
     * Color.
     *
     * @return the color
     */
    public Color color() {
        return color;
    }

    /**
     * Checks if is variable size.
     *
     * @return true, if is variable size
     */
    public boolean isVariableSize() {
        return size == -1;
    }

    /**
     * Gets the.
     *
     * @param type
     *           the type
     * @return the message type
     */
    public static MessageType get(int type) {
        for (MessageType t : values()) {
            if (t.number() == type) {
                return t;
            }
        }
        return UNDEFINED;
    }
}
// UNCLASSIFIED
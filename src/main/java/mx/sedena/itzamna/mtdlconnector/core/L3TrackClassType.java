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

/**
 * Represents an MTDL L3 track identity.
 */
public enum L3TrackClassType {

    UNDEFINED(-1),
    INVALID_INVALID(0x00),
    INVALID_SURFACE(0x01),
    INVALID_SUBSURFACE(0x02),
    INVALID_AIR(0x03),
    INVALID_UNKNOWN(0x04),
    DELETETRACK_INVALID(0x10),
    DELETETRACK_SURFACE(0x11),
    DELETETRACK_SUBSURFACE(0x12),
    DELETETRACK_AIR(0x13),
    DELETETRACK_UNKNOWN(0x14),
    FRIENDLY_INVALID(0x20),
    FRIENDLY_SURFACE(0x21),
    FRIENDLY_SUBSURFACE(0x22),
    FRIENDLY_AIR(0x23),
    FRIENDLY_UNKNOWN(0x24),
    HOSTILE_INVALID(0x30),
    HOSTILE_SURFACE(0x31),
    HOSTILE_SUBSURFACE(0x32),
    HOSTILE_AIR(0x33),
    HOSTILE_UNKNOWN(0x34),
    NEUTRAL_INVALID(0x40),
    NEUTRAL_SURFACE(0x41),
    NEUTRAL_SUBSURFACE(0x42),
    NEUTRAL_AIR(0x43),
    NEUTRAL_UNKNOWN(0x44),
    UNKNOWN_INVALID(0x50),
    UNKNOWN_SURFACE(0x51),
    UNKNOWN_SUBSURFACE(0x52),
    UNKNOWN_AIR(0x53),
    UNKNOWN_UNKNOWN(0x54);

    /** The type. */
    private final int type;

    /**
     * Instantiates a new l3 track class type.
     *
     * @param type
     *           the type
     */
    private L3TrackClassType(int type) {
        this.type = type;
    }

    /**
     * Type.
     *
     * @return the byte
     */
    public byte type() {
        return (byte) type;
    }

    /**
     * Gets the.
     *
     * @param type
     *           the type
     * @return the l3 track class type
     */
    public static L3TrackClassType get(int type) {
        for (L3TrackClassType t : values()) {
            if (t.type() == type) {
                return t;
            }
        }
        return UNDEFINED;
    }

}
// UNCLASSIFIED
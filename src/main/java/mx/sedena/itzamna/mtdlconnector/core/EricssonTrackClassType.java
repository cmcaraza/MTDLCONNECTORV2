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
 * Represents an MTDL Ericsson track identity.
 */
public enum EricssonTrackClassType {

    /** The undefined. */
    UNDEFINED(-1),

    /** The air new. */
    AIR_NEW(0),

    /** The air unknown. */
    AIR_UNKNOWN(1),

    /** The air interceptor. */
    AIR_INTERCEPTOR(2),

    /** The air hostile. */
    AIR_HOSTILE(3),

    /** The air friend general. */
    AIR_FRIEND_GENERAL(4),

    /** The air assumed fried. */
    AIR_ASSUMED_FRIED(5),

    /** The air special mission. */
    AIR_SPECIAL_MISSION(6),

    /** The air assumed hostile. */
    AIR_ASSUMED_HOSTILE(7),

    /** The air jammer. */
    AIR_JAMMER(8),

    /** The air faker. */
    AIR_FAKER(9),

    /** The surf new. */
    SURF_NEW(10),

    /** The surf unknown. */
    SURF_UNKNOWN(11),

    /** The surf destroyer. */
    SURF_DESTROYER(12),

    /** The surf cruiser. */
    SURF_CRUISER(13),

    /** The surf reconnaissance. */
    SURF_RECONNAISSANCE(14),

    /** The surf illicit boat. */
    SURF_ILLICIT_BOAT(15),

    /** The surf illicit vessel. */
    SURF_ILLICIT_VESSEL(16),

    /** The surf interceptor. */
    SURF_INTERCEPTOR(17);

    /** The type. */
    private final int type;

    /**
     * Instantiates a new ericsson track class type.
     *
     * @param type
     *           the type
     */
    private EricssonTrackClassType(int type) {
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
     * @return the ericsson track class type
     */
    public static EricssonTrackClassType get(int type) {
        for (EricssonTrackClassType t : values()) {
            if (t.type() == type) {
                return t;
            }
        }
        return UNDEFINED;
    }

}
// UNCLASSIFIED
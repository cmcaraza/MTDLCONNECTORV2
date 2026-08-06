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
 * Represents an MTDL link participant type.
 */
public enum LinkParticipantType {

    UNDEFINED(-1),
    C4I(1),
    MP1(10),
    MP2(11),
    AEW(20),
    BROADCAST(0);

    /** The identifier. */
    private final int identifier;

    /**
     * Instantiates a new link participant type.
     *
     * @param identifier
     *           the identifier
     */
    private LinkParticipantType(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Identifier.
     *
     * @return the byte
     */
    public byte identifier() {
        return (byte) identifier;
    }

    /**
     * Gets the.
     *
     * @param address
     *           the address
     * @return the link participant type
     */
    public static LinkParticipantType get(int address) {
        for (LinkParticipantType t : values()) {
            if (t.identifier() == address) {
                return t;
            }
        }
        return UNDEFINED;
    }

}
// UNCLASSIFIED
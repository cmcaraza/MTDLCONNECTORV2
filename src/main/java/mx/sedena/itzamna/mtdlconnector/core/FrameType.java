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
 * The Enum FrameType.
 */
public enum FrameType {

    CONTROL(0),
    DATA(1);

    /** The type. */
    private int type = 0;

    /**
     * Instantiates a new frame type.
     *
     * @param type
     *           the type
     */
    private FrameType(int type) {
        this.type = type;
    }

    /**
     * Type.
     *
     * @return the int
     */
    public int type() {
        return type;
    }
}
// UNCLASSIFIED
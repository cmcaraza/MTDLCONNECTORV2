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
 * Tags an MTDL message as providing an Embraer track number.
 */
/*
 * Need to refactor Embraer messages.
 */
public interface TrackNumber {

    /**
     * Gets the track number.
     *
     * @return the track number
     */
    public String getTrackNumber();

    /**
     * Sets the track number.
     *
     * @param trackNumber
     *           the new track number
     */
    public void setTrackNumber(String trackNumber);

}

// UNCLASSIFIED
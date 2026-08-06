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
 * Tags an MTDL message as providing latitude and longitude.
 */
/*
 * Need to refactor lat/lon messages
 */
public interface LatLong {

    /**
     * Gets the latitude degrees.
     *
     * @return the latitude degrees
     */
    public double getLatitudeDegrees();

    /**
     * Sets the latitude degrees.
     *
     * @param latitude
     *           the new latitude degrees
     */
    public void setLatitudeDegrees(double latitude);

    /**
     * Gets the longitude degrees.
     *
     * @return the longitude degrees
     */
    public double getLongitudeDegrees();

    /**
     * Sets the longitude degrees.
     *
     * @param longitude
     *           the new longitude degrees
     */
    public void setLongitudeDegrees(double longitude);

}

// UNCLASSIFIED